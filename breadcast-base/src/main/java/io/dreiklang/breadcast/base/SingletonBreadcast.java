package io.dreiklang.breadcast.base;

import android.content.Context;

/**
 * Breadcast implementing the static singleton pattern.
 * @author Nhu Huy Le (mail@huy-le.de), on 06/02/2018
 */

public abstract class SingletonBreadcast extends BaseBreadcast {

    private static SingletonBreadcast instance;

    protected SingletonBreadcast(Context context) {
        super(context);
        if(instance != null) {
            throw new IllegalStateException("Breadcast already initialized.");
        }
        instance = this;
    }

    public static BaseBreadcast instance() {
        if(instance == null) {
            throw new IllegalStateException("Breadcast not yet initialized.");
        }
        return instance;
    }

}
