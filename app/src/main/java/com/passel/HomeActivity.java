package com.passel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import com.passel.R;

import com.passel.data.Event;

public class HomeActivity extends ActionBarActivity {

    HashMap<Integer, String> eventMap = new HashMap();

    ArrayList<String> eventNameList=new ArrayList<String>();
    ArrayList<Event> eventList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                eventNameList);
        final ListView eventListView = (ListView) findViewById(R.id.eventListView);
        eventListView.setAdapter(adapter);
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter.getItem(position);
                Log.d("Item Clicked: ", item);

                Event event = eventList.get(position);
                Intent intent = createEventIntent(event);
                intent.putExtra("index", position);

                startActivity(intent);
            }
        });

        eventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter.getItem(position);
                Log.d("Item Clicked: ", item);

                Event event = eventList.get(position);
                Intent intent = createEventIntent(event);
                intent.putExtra("index", position);
                intent.putExtra("edit", true);

                startActivity(intent);

                return true;
            }
        });

        for (Event currentEvent: getPasselEvents()){
            addEvent(currentEvent);
        }

//        ArrayList<String> studyGuests = new ArrayList<>();
//        studyGuests.addAll(Arrays.asList("Aneesh", "Carlos"));
//
//        ArrayList<Double> studyLocation = new ArrayList<>();
//        studyLocation.addAll(Arrays.asList(42.35965, -71.09206));
//
//        addEvent(new PasselEvent("Study Sesh", "8:00PM", studyGuests, studyLocation));
//        addEvent(new PasselEvent("Team Passel Meeting", "5:00 PM"));




        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        eventListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    adapter.remove(adapter.getItem(position));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        //TODO: Add undo functionality and handle AOOBException that sometimes pops up
        eventListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        eventListView.setOnScrollListener(touchListener.makeScrollListener());
        FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_content_add))
                .withButtonColor(R.color.dark_primary_color)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 8, 8)
                .create();
        fabButton.setFloatingActionButtonColor(getResources().getColor(R.color.dark_primary_color));
        addFABButtonListener(fabButton);
    }

    private void addFABButtonListener(FloatingActionButton fab) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomeActivity.this, NewEventActivity.class);
                //        myIntent.putExtra("key", value); //Optional parameters
                HomeActivity.this.startActivityForResult(myIntent, 2);
            }
        });
    }

//    private void addMapButtonListener(){
//        Button mapButton = (Button) findViewById(R.id.mapButton);
//        mapButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(HomeActivity.this, MapEventActivity.class);
////        myIntent.putExtra("key", value); //Optional parameters
//                HomeActivity.this.startActivity(myIntent);
//            }
//        });
//    }

    //Adds a new row to the ListView with contents of message
    public void addItems(String message) {
        eventNameList.add(message);
        adapter.notifyDataSetChanged();
    }

    public void addEvent(Event event) {
        eventList.add(event);
        addItems(event.getName());
        setPasselEvents(eventList);
    }

    Intent createEventIntent(Event event){
        Intent intent = new Intent(HomeActivity.this, MapEventActivity.class);
        intent.putExtra("event_name", event.getName());
        intent.putExtra("event_time", event.getStart().toString());
        intent.putStringArrayListExtra("event_guests", new ArrayList<>(event.getGuests()));
        Log.d("HomeActivity: ", event.getLocation().toString());
        intent.putExtra("event_lat", event.getLocation().getLatitude());
        intent.putExtra("event_lng", event.getLocation().getLongitude());
        return intent;

    }

    public void setPasselEvents(ArrayList<Event> events) {
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

    public ArrayList<Event> getPasselEvents() {
        ArrayList<Event> parsedEvents = new ArrayList<>();
        String eventsKey = "PASSEL_EVENTS";
        Gson gson = new GsonBuilder().create();

        Context context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String savedValue = sharedPref.getString(eventsKey, "");
        if (savedValue.equals("")) {
            parsedEvents = null;
        } else {
            parsedEvents = gson.fromJson(savedValue, new TypeToken<ArrayList<Event>>() {}.getType());
        }

        Log.d("HomeActivity: ", "Parsing successful!");
        Log.d("HomeActivity: ", savedValue);

        return parsedEvents;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
//                openSearch();
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                HomeActivity.this.startActivity(settingsIntent);
                return true;
           /* case R.id.action_create:
                Intent myIntent = new Intent(HomeActivity.this, NewEventActivity.class);
//        myIntent.putExtra("key", value); //Optional parameters
                HomeActivity.this.startActivity(myIntent);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshEvents(){
//        eventList = new ArrayList<>();
//        eventNameList = new ArrayList<>();
//
//        adapter.notifyDataSetChanged();
//
//        for (PasselEvent currentEvent: getPasselEvents()){
//            Log.d("HomeActivity: ",currentEvent.getEventName());
//            addEvent(currentEvent);
//        }

        recreate();
    }

    /* Called when the map activity's finished */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    Log.d("I'm here!", "ok");
                    refreshEvents();
                }
                break;
        }
    }
}
