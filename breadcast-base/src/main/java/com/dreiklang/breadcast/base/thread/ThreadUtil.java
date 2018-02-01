package com.dreiklang.breadcast.base.thread;

import android.os.Handler;
import android.os.Looper;

import com.dreiklang.breadcast.annotation.ThreadModus;

/**
 * Created by Huy on 30/01/2018.
 */

public final class ThreadUtil {

    private ThreadUtil() {
    }

    public static void runOnThread(ThreadModus modus, Runnable runnable) {
        switch (modus) {

            case POSTING:
                runnable.run();
                break;

            case MAIN:
                runOnUiThread(runnable);
                break;

            case ASYNC:
                runAsync(runnable);
                break;

            case BACKGROUND:
                runOnBackgroundThread(runnable);
                break;
        }
    }

    public static void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static void runOnBackgroundThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runAsync(runnable);
        }
        else {
            runnable.run();
        }
    }

    // TODO replace by threadpool/executor?
    public static void runAsync(Runnable runnable) {
        new Thread(runnable).start();
    }

}
