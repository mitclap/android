package com.passel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.passel.data.Event;
import com.passel.util.Optional;
import com.passel.util.Some;

import java.util.List;

/**
 * Created by aneesh on 4/19/15.
 */
public class PasselApplication extends Application {

    // TODO: double check that Android has the new volatile semantics
    // which were introduced in JDK 1.5
    private volatile Optional<List<Event>> events;

    public PasselApplication() {
        super();
        events = Optional.empty();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // This is called before anything else (except content providers)
        // are created
        // so don't load anything here
        // TODO: load data lazily (on request, not in this method)
        // from file into memory as cache
    }

    public List<Event> getEvents() {
        if (!events.isPresent()) {
            synchronized (this) {
                if (!events.isPresent()) {
                    String eventsKey = "PASSEL_EVENTS";
                    Gson gson = new GsonBuilder().create();

                    Context context = getBaseContext();
                    SharedPreferences sharedPref = context.getSharedPreferences(
                            getString(R.string.preference_file_key),
                            Context.MODE_PRIVATE);

                    String savedValue = sharedPref.getString(eventsKey, "");

                    if (!savedValue.equals("")) {
                        List<Event> eventsList = gson.fromJson(savedValue,
                                new TypeToken<List<Event>>() {}.getType());
                        events = new Some<>(eventsList);
                    }
                }
            }
        }
        return events.get();
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
