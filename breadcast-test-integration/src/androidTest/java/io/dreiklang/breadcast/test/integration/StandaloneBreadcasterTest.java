package io.dreiklang.breadcast.test.integration;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.dreiklang.breadcast.base.Breadcaster;
import io.dreiklang.breadcast.base.exec.Execution;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class StandaloneBreadcasterTest {

    @Test
    public void testCast() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        final AtomicBoolean hasRun1 = new AtomicBoolean(false);
        final AtomicBoolean hasRun2 = new AtomicBoolean(false);
        final AtomicBoolean hasRun3 = new AtomicBoolean(false);
        final AtomicBoolean notRun = new AtomicBoolean(true);
        final AtomicInteger times = new AtomicInteger(0);

        Breadcaster caster = new Breadcaster(appContext)
                .action("1", new Execution() {
                    @Override
                    public void exec(Context context, Intent intent) {
                        hasRun1.set(true);
                        times.incrementAndGet();
                    }
                })
                .action("2", new Execution() {
                    @Override
                    public void exec(Context context, Intent intent) {
                        hasRun2.set(true);
                    }
                })
                .action("1", new Execution() {
                    @Override
                    public void exec(Context context, Intent intent) {
                        hasRun3.set(true);
                        times.incrementAndGet();
                    }
                })
                .register();

        assertTrue("isRegistered", caster.isRegistered());

        appContext.sendBroadcast(new Intent("1"));
        appContext.sendBroadcast(new Intent("2"));
        Thread.sleep(1000);
        assertTrue("hasRun1", hasRun1.get());
        assertTrue("hasRun2", hasRun2.get());
        assertTrue("hasRun3", hasRun3.get());
        assertTrue("notRun", notRun.get());
        assertEquals(times.get(), 2);
        caster.unregister();
        caster.release();
    }

}
