package com.passel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.passel.data.Event;
import com.passel.data.Location;
import com.passel.util.Optional;
import com.passel.util.Some;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

        // TODO remove the demo event here
        // TODO: switch to joda-time maybe
        Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        cal.setTimeInMillis(0);
        cal.set(2015, Calendar.APRIL, 21, 20, 0, 0);
        Date start = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        Date end = cal.getTime();


        List<Event> dummyEvents = new ArrayList<>();
        dummyEvents.add(new Event("Study Session",
                start, end,
                "Study for 21W.789",
                Arrays.asList("Aneesh", "Carlos"),
                new Location(42.35965, -71.09206)));
        events = new Some(dummyEvents);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        // TODO
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // TODO
        super.onTrimMemory(level);
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
                                new TypeToken<ArrayList<Event>>() {}.getType());
                        events = new Some<>(eventsList);
                    }
                }
            }
        }
        return events.get();
    }

    // TODO: use type safety to ensure that events are loaded
    /**
     * Precondition: events are loaded
     * @param index
     * @param event
     */
    public void updateEvent(int index, Event event) {
        events.get().set(index, event);
        syncEvents();
    }

    /**
     * Precondition: events are loaded
     * @param event
     */
    public void addEvent(Event event) {
        events.get().add(event);
        syncEvents();
    }

    private void syncEvents() {
        String eventsKey = "PASSEL_EVENTS";
        Gson gson = new GsonBuilder().create();

        Context context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (events == null) {
            editor.putString(eventsKey, "").commit();
        } else {
            editor.putString(eventsKey, gson.toJson(events)).commit();
        }
    }
}
