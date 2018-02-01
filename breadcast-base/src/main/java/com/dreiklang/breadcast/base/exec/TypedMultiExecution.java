package com.dreiklang.breadcast.base.exec;

import android.content.Intent;

import java.util.Collection;

/**
 * Created by Huy on 29/01/2018.
 */
@FunctionalInterface
public interface TypedMultiExecution<T> {
    void exec(Intent intent, Collection<T> instances);
}
