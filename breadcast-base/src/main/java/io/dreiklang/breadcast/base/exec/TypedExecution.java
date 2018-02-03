package io.dreiklang.breadcast.base.exec;

import android.content.Context;
import android.content.Intent;

/**
 * Callback functional interface on a typed object on receiving a broadcast in Breadcast.
 *  @author Nhu Huy Le, mail@huy-le.de
 */

@FunctionalInterface
public interface TypedExecution<T> {

    /**
     * Runs on receiving respective broadcasts.
     * @param context   Context parameter from {@link android.content.BroadcastReceiver#onReceive(Context, Intent)}
     * @param intent    Intent parameter from {@link android.content.BroadcastReceiver#onReceive(Context, Intent)}
     * @param instance  instance of T to exec on
     */
    void exec(Context context, Intent intent, T instance);
}
