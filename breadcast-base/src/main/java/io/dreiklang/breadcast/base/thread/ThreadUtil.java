package io.dreiklang.breadcast.base.thread;

import android.os.Handler;
import android.os.Looper;

import io.dreiklang.breadcast.annotation.ThreadModus;

/**
 * Util class for threading in Breadcast.
 * @author Nhu Huy Le, mail@huy-le.de
 */

public final class ThreadUtil {

    private ThreadUtil() {
    }

    /**
     * Runs on thread defined by {@link ThreadModus}
     * @param modus     ThreadModus
     * @param runnable  functional interface to run
     */
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

    /**
     * Runs on the android UI thread aka main thread. See {@link ThreadModus#MAIN}
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    /**
     * Runs on a background thread. See {@link ThreadModus#BACKGROUND}
     * @param runnable
     */
    public static void runOnBackgroundThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runAsync(runnable);
        }
        else {
            runnable.run();
        }
    }

    /**
     * Runs asynchronous. See {@link ThreadModus#ASYNC}
     * @param runnable
     */
    // TODO replace by threadpool/executor?
    public static void runAsync(Runnable runnable) {
        new Thread(runnable).start();
    }

}
