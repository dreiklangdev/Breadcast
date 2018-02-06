package io.dreiklang.breadcast.test.integration.receiver;

import android.content.Context;
import android.content.Intent;

import io.dreiklang.breadcast.annotation.Receive;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Nhu Huy Le (mail@huy-le.de), on 05/02/2018
 */

public class TestReceiver {

    private boolean run1 = false;
    private static boolean run2 = false;
    private boolean run3 = false;
    private boolean run4 = false;
    private boolean noRun = true;

    @Receive(action = "1")
    public void onAction1(Context context, Intent intent) {
        run1 = true;
        assertNotNull(context);
        assertNotNull(intent);
    }

    @Receive(action = "2")
    public static void onAction2(Context context, Intent intent) {
        System.out.println("2");
        assertNotNull(context);
        assertNotNull(intent);
        run2 = true;
    }

    @Receive(action = "1")
    public void onAction3(Intent intent) {
        System.out.println("1");
        assertEquals("1", intent.getAction());
        run3 = true;
    }

    @Receive(action = {"4", "3"})
    public void onMultiAction(Intent intent) {
        String action = intent.getAction();
        System.out.println("action: " + intent.getAction());
        if (action.equals("3")) {
            run3 = true;
        }
        else if (action.equals("4")) {
            run4 = true;
        }
    }

    @Receive(action = "0")
    public void onNoAction() {
        noRun = false;
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

    public boolean isRun4() {
        return run4;
    }

    public boolean isNoRun() {
        return noRun;
    }

}
