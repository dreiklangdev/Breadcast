package io.dreiklang.breadcast.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dreiklang.breadcast.base.exec.Execution;

/**
 * Standalone broadcast receiver object.
 * - Registered breadcasters must not be modified (unregister to add more actions).
 * - Changes to this breadcaster object are not thread-safe.
 * - Remember to {@link #release()} resources if not needed anymore to avoid memory leaks.
 */
public class Breadcaster extends BroadcastReceiver {

    private Map<String, List<Execution>> executions = new HashMap<>();

    private Context context;

    private boolean isRegistered = false;

    /**
     * Initializes Breadcaster with a context. Breadcaster does not know about lifecycles.
     * @param context
     */
    public Breadcaster(Context context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        exec(context, intent);
    }

    /**
     * Must be called after adding actions to start listening to broadcasts.
     * Equals to {@link Context#registerReceiver(BroadcastReceiver, IntentFilter)}
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

    /**
     * Breadcaster stops to listen. Equals to {@link Context#unregisterReceiver(BroadcastReceiver)}
     * @return
     */
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
     * Removes all action callbacks. Breadcaster must be unregistered.
     */
    public Breadcaster clear() {
        if (isRegistered) {
            throw new IllegalStateException("breadcaster is registered - unregister to clear actions");
        }
        executions.clear();
        return this;
    }

    /**
     * Releases resources (e.g. context). After that, this Breadcaster object is not useable anymore.
     */
    public void release() {
        unregister();
        context = null;
        executions = null;
    }

    /**
     * Defines an execution to run on a broadcast action.
     * @param action    broadcasted action to listen to.
     * @param execution interface to run on action.
     * @return
     */
    public Breadcaster action(String action, Execution execution) {
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

    /**
     * @return If breadcaster object is currently registered.
     */
    public boolean isRegistered() {
        return isRegistered;
    }

    /**
     * @return Underlying context.
     */
    public Context getContext() {
        if (context == null) {
            throw new IllegalStateException("breadcaster already released");
        }
        return context;
    }

    /**
     * Manually run all callbacks/executions listening to the intent action on the context initialized with.
     * @param intent    intent with action the executions are mapped to.
     *                  if mapping exists and executions ran.
     */
    public boolean exec(Intent intent) {
        return exec(context, intent);
    }

    private boolean exec(Context context, Intent intent) {
        if (intent == null) {
            return false;
        }

        List<Execution> list = executions.get(intent.getAction());
        if (list == null) {
            return false;
        }

        for (Execution execution : list) {
            execution.exec(context, intent);
        }

        return true;
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        for (String action : executions.keySet()) {
            intentFilter.addAction(action);
        }
        return intentFilter;
    }

}
