package io.dreiklang.breadcast.test.integration.receiver;

import android.content.Context;
import android.content.Intent;

import io.dreiklang.breadcast.annotation.Receive;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Nhu Huy Le (mail@huy-le.de), on 05/02/2018
 */

public class TestReceiver {

    private boolean run1 = false;
    private boolean run2 = false;
    private boolean run3 = false;
    private boolean runMultiA = false;
    private boolean runMultiB = false;
    private static boolean runStatic = false;
    private boolean noRun = true;

    @Receive(action = "1")
    public void onAction1(Context context, Intent intent) {
        System.out.println("action1");
        assertFalse(run1);
        run1 = true;
        assertNotNull(context);
        assertNotNull(intent);
    }

    @Receive(action = "2")
    public void onAction2() {
        System.out.println("action2");
        assertFalse(run2);
        run2 = true;
    }

    @Receive(action = "1")
    public void onAction3(Intent intent) {
        System.out.println("action3");
        assertEquals("1", intent.getAction());
        assertFalse(run3);
        run3 = true;
    }

    @Receive(action = {"4", "3"})
    public void onMultiAction(Intent intent) {
        String action = intent.getAction();
        System.out.println("multiAction: " + intent.getAction());
        if (action.equals("3")) {
            assertFalse(runMultiA);
            runMultiA = true;
        }
        else if (action.equals("4")) {
            assertFalse(runMultiB);
            runMultiB = true;
        }
    }

    @Receive(action = "static")
    public static void onStaticAction(Context context, Intent intent) {
        System.out.println("staticAction");
        assertNotNull(context);
        assertNotNull(intent);
        assertFalse(runStatic);
        runStatic = true;
    }

    @Receive(action = "0")
    public void onNoAction() {
        System.out.println("noAction");
        noRun = false;
        throw new IllegalStateException("should never run");
    }

    public boolean isRun1() {
        return run1;
    }

    public boolean isRun2() {
        return run2;
    }

    public boolean isRun3() {
        return run3;
    }

    public boolean isRunMultiA() {
        return runMultiA;
    }

    public boolean isRunMultiB() {
        return runMultiB;
    }

    public static boolean isRunStatic() {
        return runStatic;
    }

    public boolean isNoRun() {
        return noRun;
    }

}
