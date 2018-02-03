package io.dreiklang.breadcast.base.exec;

import android.content.Context;
import android.content.Intent;

/**
 * Callback functional interface on receiving a broadcast in Breadcast.
 * @author Nhu Huy Le, mail@huy-le.de
 */

@FunctionalInterface
public interface Execution {

    /**
     * Runs on receiving respective broadcasts.
     * @param context   Context parameter from {@link android.content.BroadcastReceiver#onReceive(Context, Intent)}
     * @param intent    Intent parameter from {@link android.content.BroadcastReceiver#onReceive(Context, Intent)}
     */
    void exec(Context context, Intent intent);
}
