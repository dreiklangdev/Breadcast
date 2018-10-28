package io.dreiklang.breadcast.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import io.dreiklang.breadcast.annotation.Receive;
import io.dreiklang.breadcast.annotation.ThreadModus;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BreadcastProcessor extends AbstractProcessor {

    private Types typeUtils;

    private Elements elementUtils;

    private Filer filer;

    private Messager messager;

    private List<ReceiveHolder> receives = new ArrayList<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supported = new HashSet<>();
        supported.add(Receive.class.getCanonicalName());
        return supported;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "breadcast:\t init ...");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            // TODO generate empty breadcast class nevertheless
            return true;
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "breadcast:\t processing annotation(s): " + annotations);
        Instant startTime = Instant.now();
        if (roundEnv.getElementsAnnotatedWith(Receive.class).isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t no elements annotated with: " + annotations);
            return true;
        }

        if (!extractReceives(roundEnv)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t extracting items failed.");
            return true;
        }

        TypeSpec breadcastSpec = generateBreadcast();
        try {
            JavaFile.builder("io.dreiklang.breadcast", breadcastSpec).build().writeTo(filer);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "breadcast:\t processing duration: " + Duration.between(startTime, Instant.now()));
        return true;
    }

    private TypeSpec generateBreadcast() {
        ClassName breadcastName   = ClassName.get("io.dreiklang.breadcast", "Breadcast");
        ClassName singletonBCName = ClassName.get("io.dreiklang.breadcast.base.statics", "SingletonBreadcast");
        ClassName execName        = ClassName.get("io.dreiklang.breadcast.base.exec", "TypedExecution");
        ClassName contextName     = ClassName.get("android.content", "Context");
        ClassName intentName      = ClassName.get("android.content", "Intent");

        TypeSpec.Builder breadcastBuilder = TypeSpec
                .classBuilder(breadcastName.simpleName())
                .addJavadoc("Breadcast listens to broadcasted actions and executes the respective methods annotated" +
                        "with {@link io.dreiklang.breadcast.annotation.Receive} of objects registered by {@link #register(Object)}.")
                .addModifiers(PUBLIC)
                .superclass(singletonBCName)
                .addMethod(MethodSpec
                        .constructorBuilder()
                        .addModifiers(PRIVATE)
                        .addParameter(contextName, "context")
                        .addStatement("super(context)")
                        .build())
                .addMethod(MethodSpec
                        .methodBuilder("init")
                        .addJavadoc("Installs Breadcast with a context, preferably the application context. (Baking the bread)")
                        .addModifiers(PUBLIC, STATIC)
                        .addParameter(contextName, "context")
                        .addStatement("new $T(context)", breadcastName)
                        .build());

        MethodSpec.Builder initExecutionSpec = MethodSpec
                .methodBuilder("initExecutions")
                .addAnnotation(Override.class)
                .addModifiers(PROTECTED);

        receives.forEach(receive -> {
            String params = Stream.of(receive.contextParam, receive.intentParam)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
            String actions = Stream.of(receive.actions)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\", \"","\"", "\""));
            TypeSpec execution = TypeSpec
                    .anonymousClassBuilder("")
                    .addJavadoc("item")
                    .addSuperinterface(ParameterizedTypeName.get(execName, receive.type))
                    .addMethod(MethodSpec
                            .methodBuilder("exec")
                            .addModifiers(PUBLIC)
                            .addParameter(contextName, "context")
                            .addParameter(intentName, "intent")
                            .addParameter(receive.type, "object")
                            .addStatement("object.$L($L)", receive.methodName, params)
                            .build())
                    .build();

            switch (receive.actions.length) {
                case 0:
                    break;

                case 1:
                    initExecutionSpec
                            .addStatement("map($T.class, $S, $S, $L, $T.$L, $L)",
                                    receive.type, receive.actions[0], receive.methodName, receive.isStatic, ThreadModus.class, receive.threadModus, execution);
                    break;

                default: // mutliple actions
                    initExecutionSpec
                            .addStatement("map($T.class, new String[] {$L}, $S, $L, $T.$L, $L)",
                                    receive.type, actions, receive.methodName, receive.isStatic, ThreadModus.class, receive.threadModus, execution);
                    break;
            }
        });

        return breadcastBuilder
                .addMethod(initExecutionSpec.build())
                .build();
    }

    private boolean extractReceives(RoundEnvironment roundEnv) {
        for (Element annotated : roundEnv.getElementsAnnotatedWith(Receive.class)) {

            if (annotated.getKind() != ElementKind.METHOD) {
                messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t annotated element must be a method.", annotated);
                return false;
            }

            ExecutableElement method = (ExecutableElement) annotated;

            if (method.getModifiers().contains(ABSTRACT)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t annotated method must not be abstract.", annotated);
                return false;
            }

            if (!method.getModifiers().contains(PUBLIC)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t annotated method must be public.", annotated);
                return false;
            }

            if (!(method.getReturnType() instanceof NoType)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t annotated method must be void.", annotated);
                return false;
            }

            List<? extends VariableElement> params = method.getParameters();
            Receive annotation    = annotated.getAnnotation(Receive.class);
            ReceiveHolder receive = new ReceiveHolder();

            if (method.getModifiers().contains(STATIC)) {
                receive.isStatic = true;
            }

            for (VariableElement param : params) {
                switch (param.asType().toString()) {
                    case "android.content.Context":
                        receive.contextParam = "context";
                        break;

                    case "android.content.Intent":
                        receive.intentParam = "intent";
                        break;

                    default:
                        messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t annotated method must have either parameter(s) android.content.Context and/or android.content.Intent or none.", annotated);
                        return false;
                }
            }

            receive.type        = ClassName.get((TypeElement) method.getEnclosingElement());
            receive.methodName  = method.getSimpleName().toString();
            receive.actions     = annotation.action();
            receive.threadModus = annotation.threadMode();

            receives.add(receive);
        }

        return true;
    }

    private class ReceiveHolder {
        TypeName type;
        String methodName;
        String[] actions;
        String contextParam;
        String intentParam;
        ThreadModus threadModus;
        boolean isStatic = false;
    }

}
