package com.dreiklang.breadcast.base.exec;

import android.content.Context;
import android.content.Intent;

/**
 * callback on receiving a broadcast.
 * Created by Huy on 29/01/2018.
 */

@FunctionalInterface
public interface Execution {
    void exec(Context context, Intent intent);
}
