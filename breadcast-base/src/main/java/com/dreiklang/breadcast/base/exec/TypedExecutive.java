package com.dreiklang.breadcast.base.exec;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * responsible for executions in instances of one distinct class.
 * Created by Huy on 30/01/2018.
 */

public class TypedExecutive<T> {

    final List<T> instances = Collections.synchronizedList(new ArrayList<T>());

    final List<TypedMultiExecution<T>> list = new ArrayList<>();

    public void exec(Intent intent) {
        for (TypedMultiExecution<T> execution : list) {
            execution.exec(intent, instances);
        }
    }

    public void add(TypedMultiExecution<T> execution) {
        list.add(execution);
    }

}
