package com.passel.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.passel.R;
import com.passel.util.Optional;
import com.passel.util.Some;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * In the data layer because it handles data persistence
 * Created by aneesh on 4/19/15.
 */
public class PasselApplication extends Application {

    // TODO: double check that Android has the new volatile semantics
    // which were introduced in JDK 1.5
    private volatile Optional<List<Event>> events;
    private final JsonMapper mapper = new JsonMapper();

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
        dummyEvents.add(new NewEvent(0, "Study Session",
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

    public JsonMapper getJsonMapper() {
        return this.mapper;
    }

    public List<Event> getEvents() {
        if (!events.isPresent()) {
            synchronized (this) {
                if (!events.isPresent()) {
                    String eventsKey = "PASSEL_EVENTS";

                    Context context = getBaseContext();
                    SharedPreferences sharedPref = context.getSharedPreferences(
                            getString(R.string.preference_file_key),
                            Context.MODE_PRIVATE);

                    String savedValue = sharedPref.getString(eventsKey, "");

                    List<Event> eventList;
                    if (!savedValue.equals("")) {
                        try {
                            eventList = mapper.deserialize(savedValue,
                                    new TypeReference<List<Event>>() {
                                    });
                        } catch (JsonProcessingException e) {
                            Log.e("PASSEL_APPLICATION",
                                    "Unable to load events", e);
                            // TODO some kind of UI error?
                            eventList = new ArrayList<>();
                        }
                    } else {
                        eventList = new ArrayList<>();
                    }
                    events = new Some<>(eventList);
                }
            }
        }
        return events.get();
    }

    // TODO: use type safety to ensure that events are loaded

    /**
     * Precondition: events are loaded
     *
     * @param index
     * @param event
     */
    public void updateEvent(final Event event) {
        events.get().set(event.getLocalId(), event);
        syncEvents();
    }

    /**
     * Precondition: events are loaded
     */
    public void createEvent(final String name,
                            final Date start,
                            final Date end,
                            final String description,
                            final List<String> guests,
                            final Location location) {
        // TODO: write the new event to sql,
        // then make a NewEvent object in memory
        // and add to the list
        int index = events.get().size();
        Event newEvent = new NewEvent(index, name, start, end, description, guests, location);
        events.get().add(newEvent);
        syncEvents();
    }

    private void syncEvents() {
        String eventsKey = "PASSEL_EVENTS";

        Context context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        try {
            editor.putString(eventsKey, mapper.serialize(events)).commit();
            Log.d("PASSEL_APPLICATION", "Synced events to disk");
        } catch (JsonProcessingException e) {
            Log.e("PASSEL_APPLICATION", "Could not serialize events for sync");
        }
    }
}