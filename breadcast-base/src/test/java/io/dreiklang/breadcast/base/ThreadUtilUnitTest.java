package io.dreiklang.breadcast.base;

import org.junit.Test;

import io.dreiklang.breadcast.base.thread.ThreadUtil;

import static junit.framework.Assert.assertTrue;

public class ThreadUtilUnitTest {

    @Test
    public void testAsync() throws Exception {
        final long mainThread = Thread.currentThread().getId();
        ThreadUtil.runAsync(new Runnable() {
            @Override
            public void run() {
                final long asyncThread = Thread.currentThread().getId();
                assertTrue("is async thread", mainThread != asyncThread);
            }
        });
        Thread.sleep(1000);
    }

}
