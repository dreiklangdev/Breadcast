package com.dreiklang.breadcast.base;

import android.content.Context;
import android.content.Intent;

import com.dreiklang.breadcast.annotation.ThreadModus;
import com.dreiklang.breadcast.base.exception.NoAnnotatedMethodException;
import com.dreiklang.breadcast.base.exec.Execution;
import com.dreiklang.breadcast.base.exec.ExecutiveManager;
import com.dreiklang.breadcast.base.exec.TypedExecution;
import com.dreiklang.breadcast.base.exec.TypedMultiExecution;
import com.dreiklang.breadcast.base.thread.ThreadUtil;

import java.util.Collection;

/**
 * Created by Huy on 29/01/2018.
 */

public abstract class BaseBreadcast {

    private final ExecutiveManager manager = new ExecutiveManager();

    private final Breadcaster caster;

    private boolean NamExceptionOpt = false;

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

    protected <T> void putTypedExecution(final Class<T> clazz, String action, final ThreadModus modus, final TypedExecution<T> execution) {
        manager.createExecutive(clazz, new TypedMultiExecution<T>() {
            @Override
            public void exec(Intent intent, Collection<T> instances) {
                for (T instance: instances) {
                    execution.exec(intent, instance);
                }
            }
        });

        caster.action(action, new Execution() {
            @Override
            public void exec(final Context context, final Intent intent) {
                ThreadUtil.runOnThread(modus, new Runnable() {
                    @Override
                    public void run() {
                        manager.exec(clazz, intent);
                    }
                });
            }
        });
    }

    protected abstract void initExecutions();

}
