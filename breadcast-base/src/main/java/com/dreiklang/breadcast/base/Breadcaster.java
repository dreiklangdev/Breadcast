package com.dreiklang.breadcast.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.dreiklang.breadcast.base.exec.Execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * registered breadcasts must not be modified (unregister to add actions)
 * changes to breadcaster object are not thread-safe
 */
public class Breadcaster extends BroadcastReceiver {

    private Map<String, List<Execution>> executions = new HashMap<>();

    private Context context;

    private boolean isRegistered = false;

    public Breadcaster(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        exec(intent);
    }

    /**
     * must be called after adding actions.
     * @return
     */
    public Breadcaster register() {
        if (context == null) {
            throw new IllegalStateException("breadcaster already released");
        }
        context.registerReceiver(this, getIntentFilter());
        isRegistered = true;
        return this;
    }

    public Breadcaster unregister() {
        if (context == null) {
            throw new IllegalStateException("breadcaster already released");
        }
        if (isRegistered) {
            context.unregisterReceiver(this);
        }
        isRegistered = false;
        return this;
    }

    /**
     * removes all action callbacks. Breadcaster must be unregistered.
     */
    public Breadcaster clear() {
        if (isRegistered) {
            throw new IllegalStateException("breadcaster is registered - unregister to clear actions");
        }
        executions.clear();
        return this;
    }

    public void release() {
        unregister();
        context = null;
        executions = null;
    }

    public <T> Breadcaster action(String action, Execution execution) {
        if (isRegistered) {
            throw new IllegalStateException("breadcaster is registered - unregister to add action");
        }
        List<Execution> list = executions.get(action);
        if (list == null) {
            list = new ArrayList<>();
            executions.put(action, list);
        }

        list.add(execution);
        return this;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    private void exec(Intent intent) {
        if (intent == null) {
            return;
        }

        List<Execution> list = executions.get(intent.getAction());
        if (list != null) {
            for (Execution execution : list) {
                execution.exec(context, intent);
            }
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        for (String action : executions.keySet()) {
            intentFilter.addAction(action);
        }
        return intentFilter;
    }

}
