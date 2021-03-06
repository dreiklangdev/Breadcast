package io.dreiklang.breadcast.base;

import android.content.Context;
import android.content.Intent;

import java.util.Collection;

import io.dreiklang.breadcast.annotation.ThreadModus;
import io.dreiklang.breadcast.base.exception.NoAnnotatedMethodException;
import io.dreiklang.breadcast.base.exec.Execution;
import io.dreiklang.breadcast.base.exec.ExecutiveManager;
import io.dreiklang.breadcast.base.exec.TypedExecution;
import io.dreiklang.breadcast.base.exec.TypedMultiExecution;
import io.dreiklang.breadcast.base.thread.ThreadUtil;

/**
 * Base class of the custom generated Breadcast.
 * @author Nhu Huy Le, mail@huy-le.de
 */
// TODO cant resolve jdoc dependencies
// see https://stackoverflow.com/questions/10895032/javadoc-with-gradle-dont-get-the-libraries-while-running-javadoc-task

public abstract class BaseBreadcast {

    private final ExecutiveManager manager = new ExecutiveManager();

    private final Breadcaster caster;

    private boolean NamExceptionOpt = true;

    protected BaseBreadcast(Context context) {
        caster = new Breadcaster(context);
        initExecutions();
        caster.register();
    }

    /**
     * Manually run all callbacks/executions listening to the intent action.
     * @param intent    intent with action the executions are mapped to.
     * @return          if mapping exists and executions ran.
     */
    public boolean exec(Intent intent) {
        return caster.exec(intent);
    }

    /**
     * Registers an object with annotated methods of {@link io.dreiklang.breadcast.annotation.Receive}. Throws an exception if no annotated method is found. */
    public void register(Object object) {
        if (!manager.addInstance(object)) {
            if (NamExceptionOpt) {
                throw new NoAnnotatedMethodException();
            }
        }
    }

    /**
     * Unregisters an object with annotated methods of {@link io.dreiklang.breadcast.annotation.Receive}. Throws an exception if no annotated method is found. */
    public void unregister(Object object) {
        if (!manager.removeInstance(object)) {
            if (NamExceptionOpt) {
                throw new NoAnnotatedMethodException();
            }
        }
    }

    protected <T> void map(final Class<T> clazz, final String action, final String method, final boolean isStatic, final ThreadModus modus, final TypedExecution<T> execution) {
        mapMethod(clazz, method, isStatic, execution);
        mapAction(clazz, method, modus, action);
    }

    protected <T> void map(final Class<T> clazz, final String[] actions, final String method, final boolean isStatic, final ThreadModus modus, final TypedExecution<T> execution) {
        mapMethod(clazz, method, isStatic, execution);
        for (String action : actions) {
            mapAction(clazz, method, modus, action);
        }
    }

    private <T> void mapMethod(Class<T> clazz, String method, final boolean isStatic, final TypedExecution<T> execution) {
        manager.defineExecutive(clazz, method, new TypedMultiExecution<T>() {
            @Override
            public void exec(Context context, Intent intent, Collection<T> instances) {
                if (isStatic) {
                    execution.exec(context, intent, null);
                }
                else if (instances != null) {
                    for (T instance: instances) {
                        execution.exec(context, intent, instance);
                    }
                }
            }
        });
    }

    private <T> void mapAction(final Class<T> clazz, final String method, final ThreadModus modus, String action) {
        caster.action(action, new Execution() {
            @Override
            public void exec(final Context context, final Intent intent) {
                ThreadUtil.runOnThread(modus, new Runnable() {
                    @Override
                    public void run() {
                        manager.exec(clazz, method, context, intent);
                    }
                });
            }
        });
    }

    protected abstract void initExecutions();

}
