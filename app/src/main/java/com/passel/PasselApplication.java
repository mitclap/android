package com.passel;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

import com.passel.data.Event;
import com.passel.util.None;
import com.passel.util.Optional;

import java.util.List;

/**
 * Created by aneesh on 4/19/15.
 */
public class PasselApplication extends Application {

    private Optional<List<Event>> events;

    public PasselApplication() {
        super();
        events = Optional.empty();
    }

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

}
