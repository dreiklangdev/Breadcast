package io.dreiklang.breadcast.base.exec;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Responsible for executions in instances of distinct class and distinct broadcast action.
 * @author Nhu Huy Le, mail@huy-le.de
 */

public class TypedExecutive<T> {

    private final List<T> instances = Collections.synchronizedList(new ArrayList<T>());

    private final Map<String, List<TypedMultiExecution<T>>> executionsMap = new ConcurrentHashMap<>();

    /**
     * Run the callback of a distinct action defined by {@link #put(String, TypedMultiExecution)}.
     * @param action    broadcast action to run on
     * @param context   context under which broadcast was sent with {@link Context#sendBroadcast(Intent)}
     * @param intent    intent with which broadcast was sent with {@link Context#sendBroadcast(Intent)}
     */
    public void exec(String action, Context context, Intent intent) {
        List<TypedMultiExecution<T>> executions = executionsMap.get(action);
        if (executions == null) {
            throw new IllegalArgumentException("no executions found under action: " + action);
        }
        for (TypedMultiExecution<T> execution : executions) {
            execution.exec(context, intent, instances);
        }
    }

    /**
     * Defines an execution mapped by the action string.
     * @param action    broadcast action, on which the execution will run
     * @param execution typed callback to run on action
     */
    public void put(String action, TypedMultiExecution<T> execution) {
        List<TypedMultiExecution<T>> executions = executionsMap.get(action);
        if (executions == null) {
            executions = Collections.synchronizedList(new ArrayList<TypedMultiExecution<T>>());
            executionsMap.put(action, executions);
        }
        executions.add(execution);
    }

    /**
     * Adds an instance on which the execution will run.
     * @param object    instance of T to add.
     * @return          result of {@link List#add(Object)}
     */
    public boolean addInstance(T object) {
        return instances.add(object);
    }

    /**
     * Removes an instance on which the execution will run.
     * @param object    instance of T to remove.
     * @return          result of {@link List#remove(Object)}
     */
    public boolean removeInstance(T object) {
        return instances.remove(object);
    }

}
