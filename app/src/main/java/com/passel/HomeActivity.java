package com.passel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.passel.data.Event;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends ActionBarActivity {

    private List<String> eventNames;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final List<Event> events = ((PasselApplication) getApplication()).getEvents();
        eventNames = new ArrayList<>(events.size());
        for (Event currentEvent: events) {
            eventNames.add(currentEvent.getName());
        }

        final ListView eventListView = (ListView) findViewById(R.id.eventListView);
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Event> eventList = ((PasselApplication) getApplication()).getEvents();
                Intent intent = createEventIntent(eventList.get(position));
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });

        eventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                List<Event> eventList = ((PasselApplication) getApplication()).getEvents();
                Intent intent = createEventIntent(eventList.get(position));
                intent.putExtra("index", position);
                intent.putExtra("edit", true);
                startActivity(intent);
                return true;
            }
        });

        final SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        eventListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView,
                                                  int[] reverseSortedPositions) {
                                // TODO actually remove event
                                // TODO: Add undo functionality and handle
                                // AOOBException that sometimes pops up
                                for (int position : reverseSortedPositions) {
                                    adapter.remove(adapter.getItem(position));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        eventListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that
        // during ListView scrolling, we don't look for swipes.
        eventListView.setOnScrollListener(touchListener.makeScrollListener());

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                eventNames);
//        adapter.notifyDataSetChanged(); TODO see if needed
        eventListView.setAdapter(adapter);

        final FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_content_add))
                .withButtonColor(getResources().getColor(R.color.dark_primary_color))
                .withMargins(0, 0, 8, 8)
                .create(); // Also adds the button to the screen
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomeActivity.this, NewEventActivity.class);
                HomeActivity.this.startActivityForResult(myIntent, 2);
            }
        });

    }

    Intent createEventIntent(Event event){
        Intent intent = new Intent(this, MapEventActivity.class);
        intent.putExtra("event_name", event.getName());
        intent.putExtra("event_time", event.getStart().toString());
        intent.putStringArrayListExtra("event_guests", new ArrayList<>(event.getGuests()));
        intent.putExtra("event_lat", event.getLocation().getLatitude());
        intent.putExtra("event_lng", event.getLocation().getLongitude());
        return intent;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
