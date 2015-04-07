package passel.w21789.com.passel;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.Gravity;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeActivity extends ActionBarActivity {

    HashMap<Integer, String> eventMap = new HashMap();

    ArrayList<String> eventList=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                eventList);
        ListView eventListView = (ListView) findViewById(R.id.eventListView);
        eventListView.setAdapter(adapter);
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter.getItem(position);

                Intent intent = new Intent(HomeActivity.this, MapEventActivity.class);
                Log.d("Item Clicked: ", item);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });

        addItems("Team Passel Group Meeting");
        addItems("Dinner With Friends");
        addItems("Birthday Party!");
        addItems("Hang out");
        addItems("Interview With Company");
        addItems("DT Practice");
        addItems("Help Set Up For Event");
        addItems("Baseball Practice");
        addItems("Volunteer for something");
        addItems("Meet at Airport");
        addItems("Brunch With Friend");
        addItems("Weekly Study Group");
        addItems("Chipotle Fridays with Joe");
        addItems("Grab Coffee at Flour with ");


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
                .withMargins(0, 0, 16, 16)
                .create();
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
        eventList.add(message);
        adapter.notifyDataSetChanged();
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
            case R.id.action_create:
                Intent myIntent = new Intent(HomeActivity.this, NewEventActivity.class);
//        myIntent.putExtra("key", value); //Optional parameters
                HomeActivity.this.startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
