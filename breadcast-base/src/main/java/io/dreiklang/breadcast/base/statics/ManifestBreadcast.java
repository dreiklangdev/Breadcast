package io.dreiklang.breadcast.base.statics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Nhu Huy Le (mail@huy-le.de), on 06/02/2018
 */

public class ManifestBreadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SingletonBreadcast.instance().exec(intent);
    }

}
