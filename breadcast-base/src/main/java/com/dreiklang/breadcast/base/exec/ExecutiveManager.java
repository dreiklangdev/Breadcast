package com.dreiklang.breadcast.base.exec;

import android.content.Intent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Huy on 30/01/2018.
 */

public class ExecutiveManager {

    private final Map<Class, TypedExecutive> executives = new ConcurrentHashMap<>();

    public <T> void createExecutive(Class<T> clazz, TypedMultiExecution<T> execution) {
        executives.put(clazz, new TypedExecutive<T>());
        TypedExecutive<T> executive = executives.get(clazz);
        executive.add(execution);
    }

    public boolean addInstance(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isAnonymousClass()) {
            clazz = clazz.getSuperclass();
        }

        TypedExecutive executive = executives.get(clazz);
        if (executive == null) {
            return false;
        }

        return executive.instances.add(object);
    }

    public boolean removeInstance(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isAnonymousClass()) {
            clazz = clazz.getSuperclass();
        }

        TypedExecutive executive = executives.get(clazz);
        if (executive == null) {
            return false;
        }

        return executive.instances.remove(object);
    }

    public <T> void exec(Class<T> clazz, Intent intent) {
        executives.get(clazz).exec(intent);
    }

}
