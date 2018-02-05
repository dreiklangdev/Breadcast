package io.dreiklang.breadcast.base.exec;

import android.content.Context;
import android.content.Intent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds executives mapped with their classes and ensures type safety.
 *
 * @author Nhu Huy Le, mail@huy-le.de
 */

public class ExecutiveManager {

    private final Map<Class, TypedExecutive> executives = new ConcurrentHashMap<>();

    /**
     * Sets or overrides the execution run on all instances according to class type and listening method.
     * @param clazz         class object as the primary key.
     * @param method        method as secondary key.
     * @param execution     execution to run on {@link #exec(Class, String, Context, Intent)} call.
     * @param <T>           generic class type to keep both class and execution in fit.
     */
    public <T> void defineExecutive(Class<T> clazz, String method, TypedMultiExecution<T> execution) {
        TypedExecutive<T> executive = executives.get(clazz);
        if (executive == null) {
            executive = new TypedExecutive<T>();
            executives.put(clazz, executive);
        }

        executive.put(method, execution);
    }

    /**
     * Adds an instance to execute with, previously defined with {@link #defineExecutive(Class, String, TypedMultiExecution)}.
     * @param object    object of type T defined in {@link #defineExecutive(Class, String, TypedMultiExecution)}
     * @return          if there is a defined class to map the object execution with.
     */
    public boolean addInstance(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isAnonymousClass()) {
            clazz = clazz.getSuperclass();
        }

        TypedExecutive executive = executives.get(clazz);
        if (executive == null) {
            return false;
        }

        return executive.addInstance(object);
    }

    /**
     * Removes an instance to execute with, previously defined with {@link #defineExecutive(Class, String, TypedMultiExecution)}.
     * @param object    object of type T defined in {@link #defineExecutive(Class, String, TypedMultiExecution)}
     * @return          if there is a defined class to map the object execution with.
     */
    public boolean removeInstance(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isAnonymousClass()) {
            clazz = clazz.getSuperclass();
        }

        TypedExecutive executive = executives.get(clazz);
        if (executive == null) {
            return false;
        }

        return executive.removeInstance(object);
    }

    /**
     * Runs the execution specified by class and method name on all instances of the respective class type.
     * @param clazz     class type to execute on.
     * @param method    method specifier.
     * @param context   context of #onReceive.
     * @param intent    intent parameter from {@link android.content.BroadcastReceiver#onReceive(Context, Intent)}
     * @param <T>
     */
    public <T> void exec(Class<T> clazz, String method, Context context, Intent intent) {
        executives.get(clazz).exec(method, context, intent);
    }

}
