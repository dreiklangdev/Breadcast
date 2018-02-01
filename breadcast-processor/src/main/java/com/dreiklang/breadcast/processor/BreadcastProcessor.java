package com.dreiklang.breadcast.processor;

import com.dreiklang.breadcast.annotation.Receive;
import com.dreiklang.breadcast.annotation.ThreadModus;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
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
import java.util.Set;

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
            JavaFile.builder("com.dreiklang.breadcast", breadcastSpec).build().writeTo(filer);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "breadcast:\t processing duration: " + Duration.between(startTime, Instant.now()));
        return true;
    }

    private TypeSpec generateBreadcast() {
        ClassName breadcastName = ClassName.get("com.dreiklang.breadcast", "Breadcast");
        ClassName execName      = ClassName.get("com.dreiklang.breadcast.base.exec", "TypedExecution");
        ClassName contextName   = ClassName.get("android.content", "Context");
        ClassName intentName    = ClassName.get("android.content", "Intent");

        TypeSpec.Builder breadcastBuilder = TypeSpec
                .classBuilder(breadcastName.simpleName())
                .addModifiers(PUBLIC)
                .superclass(ClassName.get("com.dreiklang.breadcast.base", "BaseBreadcast"))
                .addField(FieldSpec
                        .builder(breadcastName, "instance", PRIVATE, STATIC)
                        .build())
                .addMethod(MethodSpec
                        .constructorBuilder()
                        .addModifiers(PRIVATE)
                        .addParameter(contextName, "context")
                        .addStatement("super(context)")
                        .build())
                .addMethod(MethodSpec
                        .methodBuilder("register")
                        .addModifiers(PUBLIC, STATIC)
                        .addParameter(Object.class, "object")
                        .beginControlFlow("if(instance == null)")
                            .addStatement("throw new $T($S)", IllegalStateException.class, "breadcast not yet initialized.")
                        .endControlFlow()
                        .addStatement("instance.addReceiver(object)")
                        .build())
                .addMethod(MethodSpec
                        .methodBuilder("unregister")
                        .addModifiers(PUBLIC, STATIC)
                        .addParameter(Object.class, "object")
                        .beginControlFlow("if(instance == null)")
                        .addStatement("throw new $T($S)", IllegalStateException.class, "breadcast not yet initialized.")
                        .endControlFlow()
                            .addStatement("instance.removeReceiver(object)")
                        .build())
                .addMethod(MethodSpec
                        .methodBuilder("init")
                        .addModifiers(PUBLIC, STATIC)
                        .addParameter(contextName, "context")
                        .beginControlFlow("if(instance != null)")
                        .addStatement("throw new $T($S)", IllegalStateException.class, "breadcast already initialized.")
                        .endControlFlow()
                        .addStatement("instance = new $T(context)", breadcastName)
                        .build());

        MethodSpec.Builder initExecutionSpec = MethodSpec
                .methodBuilder("initExecutions")
                .addAnnotation(Override.class)
                .addModifiers(PROTECTED);

        receives.forEach(receive -> {
            TypeSpec execution = TypeSpec
                    .anonymousClassBuilder("")
                    .addJavadoc("item")
                    .addSuperinterface(ParameterizedTypeName.get(execName, receive.type))
                    .addMethod(MethodSpec
                            .methodBuilder("exec")
                            .addModifiers(PUBLIC)
                            .addParameter(intentName, "intent")
                            .addParameter(receive.type, "object")
                            .addStatement("object.$L($L)", receive.methodName, receive.intentParam)
                            .build())
                    .build();
            initExecutionSpec
                    .addStatement("putTypedExecution($T.class, $S, $T.$L, $L)", receive.type, receive.action, ThreadModus.class, receive.threadModus, execution);
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
                messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t annotated method must not be public.", annotated);
                return false;
            }

            if (!(method.getReturnType() instanceof NoType)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t annotated method must not be void.", annotated);
                return false;
            }

            List<? extends VariableElement> params = method.getParameters();

            if (params.size() > 1) {
                messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t annotated method must have either parameter android.content.Intent or none.", annotated);
                return false;
            }

            if (params.size() == 1) {
                if (!params.get(0).asType().toString().equals("android.content.Intent")) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "breadcast:\t annotated method must have either parameter android.content.Intent or none.", annotated);
                    return false;
                }
            }

            Receive annotation    = annotated.getAnnotation(Receive.class);
            ReceiveHolder receive = new ReceiveHolder();
            receive.type          = ClassName.get((TypeElement) method.getEnclosingElement());
            receive.methodName    = method.getSimpleName().toString();
            receive.action        = annotation.action();
            receive.threadModus   = annotation.threadMode();
            receive.intentParam   = params.size() == 1
                    ?"intent"
                    : "";

            receives.add(receive);
        }

        return true;
    }

    private class ReceiveHolder {
        TypeName type;
        String methodName;
        String action;
        String intentParam;
        ThreadModus threadModus;
    }

}
