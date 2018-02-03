package io.dreiklang.breadcast.base.exec;

import android.content.Context;
import android.content.Intent;

import java.util.Collection;

/**
 * Callback functional interface on a list of typed objects on receiving a broadcast in Breadcast.
 * @author Nhu Huy Le, mail@huy-le.de
 */

@FunctionalInterface
public interface TypedMultiExecution<T> {

    /**
     * Runs on receiving respective broadcasts.
     * @param context   Context parameter from {@link android.content.BroadcastReceiver#onReceive(Context, Intent)}
     * @param intent    Intent parameter from {@link android.content.BroadcastReceiver#onReceive(Context, Intent)}
     * @param instances List of instances of T to exec on
     */
    void exec(Context context, Intent intent, Collection<T> instances);
}
