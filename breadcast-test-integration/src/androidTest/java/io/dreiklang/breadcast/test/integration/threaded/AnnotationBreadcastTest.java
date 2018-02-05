package io.dreiklang.breadcast.test.integration.threaded;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import io.dreiklang.breadcast.Breadcast;
import io.dreiklang.breadcast.test.integration.TestReceiver;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AnnotationBreadcastTest {

    @BeforeClass
    public static void beforeClass() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Breadcast.init(appContext);
    }

    @Test
    public void testCast() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        TestReceiver receiver = new TestReceiver();
        Breadcast.register(receiver);

        appContext.sendBroadcast(new Intent("1"));
        appContext.sendBroadcast(new Intent("2"));
        Thread.sleep(1000);

        assertTrue("hasRun1", receiver.isRun1());
        assertTrue("hasRun2", receiver.isRun2());
        assertTrue("hasRun3", receiver.isRun3());
        assertTrue("notRun", receiver.isNoRun());
    }

    private final CountDownLatch latch = new CountDownLatch(4);

    @Test(timeout = 5000)
    public void testThreaded() throws Exception {
        boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
        assertFalse("isNotMainThread", isMainThread);

        final Context appContext = InstrumentationRegistry.getTargetContext();
        TestThreadedReceiver receiver = new TestThreadedReceiver(latch);
        Breadcast.register(receiver);

        // receives in main thread
        appContext.sendBroadcast(new Intent("thread"));
        latch.await();
    }

}
