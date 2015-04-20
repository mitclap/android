package com.passel;

import android.content.Intent;
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

import com.passel.data.Event;

import java.util.ArrayList;

public class HomeActivity extends ActionBarActivity {

    ArrayList<String> eventNameList = new ArrayList<>();
    ArrayList<Event> eventList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                eventNameList);
        final ListView eventListView = (ListView) findViewById(R.id.eventListView);
        eventListView.setAdapter(adapter);
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = createEventIntent(eventList.get(position));
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });

        eventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = createEventIntent(eventList.get(position));
                intent.putExtra("index", position);
                intent.putExtra("edit", true);
                startActivity(intent);
                return true;
            }
        });



        for (Event currentEvent:
                ((PasselApplication) getApplication()).getEvents()) {
            eventList.add(currentEvent);
            eventNameList.add(currentEvent.getName());
            adapter.notifyDataSetChanged();
        }

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
                .withButtonColor(getResources().getColor(R.color.dark_primary_color))
                .withMargins(0, 0, 8, 8)
                .create();
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
