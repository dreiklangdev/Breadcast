package io.dreiklang.breadcast.test.integration.receiver;

import io.dreiklang.breadcast.annotation.Receive;

/**
 * @author Nhu Huy Le (mail@huy-le.de), on 05/02/2018
 */

public class TestReceiver {

    private boolean run1 = false;
    private boolean run2 = false;
    private boolean run3 = false;
    private boolean noRun = true;

    @Receive(action = "1")
    public void onAction1() {
        run1 = true;
    }

    @Receive(action = "2")
    public void onAction2() {
        run2 = true;
    }

    @Receive(action = "1")
    public void onAction3() {
        run3 = true;
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

    public boolean isNoRun() {
        return noRun;
    }

}
