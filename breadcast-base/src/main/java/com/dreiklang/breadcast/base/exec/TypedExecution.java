package com.dreiklang.breadcast.base.exec;

import android.content.Intent;

/**
 * Created by Huy on 29/01/2018.
 */

@FunctionalInterface
public interface TypedExecution<T> {
    void exec(Intent intent, T instance);
}
