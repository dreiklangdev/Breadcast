package io.dreiklang.breadcast.annotation;

/**
 * Defines the thread the receiving method should run in.
 *
 * @author Nhu Huy Le, mail@huy-le.de
 */

public enum ThreadModus {

    /**
     * Runs in the thread the broadcast is sent with android.Context#sendBroadcast(Intent, String, Handler).
     * If no handler-thread is defined, the receiving method will be called in the UI Thread aka Main Thread.
     */
    POSTING,

    /**
     * Runs in the UI thread of the Android framework.
     */
    MAIN,

    /**
     * Ensures that the receiving method will be called in the background, either as the posting thread or
     * by starting a separate thread on its own.
     */
    BACKGROUND,

    /**
     * Starts a separate thread the method is called in.
     */
    ASYNC,

}
