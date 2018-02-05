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

    protected void addReceiver(Object object) {
        if (!manager.addInstance(object)) {
            if (NamExceptionOpt) {
                throw new NoAnnotatedMethodException();
            }
        }
    }

    protected void removeReceiver(Object object) {
        if (!manager.removeInstance(object)) {
            if (NamExceptionOpt) {
                throw new NoAnnotatedMethodException();
            }
        }
    }

    protected <T> void putTypedExecution(final Class<T> clazz, final String action, final String method, final ThreadModus modus, final TypedExecution<T> execution) {
        manager.defineExecutive(clazz, method, new TypedMultiExecution<T>() {
            @Override
            public void exec(Context context, Intent intent, Collection<T> instances) {
                for (T instance: instances) {
                    execution.exec(context, intent, instance);
                }
            }
        });

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
