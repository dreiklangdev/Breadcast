package io.dreiklang.breadcast.test.integration.receiver;

import android.os.Looper;

import java.util.concurrent.CountDownLatch;

import io.dreiklang.breadcast.annotation.Receive;
import io.dreiklang.breadcast.annotation.ThreadModus;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Nhu Huy Le (mail@huyle.de), on 05/02/2018
 */

public class TestThreadedReceiver {

    private final CountDownLatch latch;

    public TestThreadedReceiver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Receive(action = "thread", threadMode = ThreadModus.ASYNC)
    public void onAsync() {
        System.out.println("onAsync");
        boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
        assertFalse(isMainThread);
        latch.countDown();
    }

    @Receive(action = "thread", threadMode = ThreadModus.BACKGROUND)
    public void onBackground() {
        System.out.println("onBackground");
        boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
        assertFalse(isMainThread);
        latch.countDown();
    }

    @Receive(action = "thread", threadMode = ThreadModus.MAIN)
    public void onMain() {
        System.out.println("onMain");
        boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
        assertTrue(isMainThread);
        latch.countDown();
    }

    @Receive(action = "thread", threadMode = ThreadModus.POSTING)
    public void onPosting() {
        System.out.println("onPosting");
        boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
        assertTrue(isMainThread);
        latch.countDown();
    }

}
