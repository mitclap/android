package com.passel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.passel.data.Event;
import com.passel.data.Location;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class MapEventActivity extends ActionBarActivity {
    HashMap<String, Marker> peopleMarkerHashMap = new HashMap<>();

    Intent BackgroundGPSIntent;
    Intent PeerLocationIntent;

    Marker eventMarker;
    Marker selfMarker;

    MapView map;

    protected static void showOnMap(Context context, int index) {
        // TODO: take an event instead of an id
        Intent intent = new Intent(context, MapEventActivity.class);
        intent.putExtra("index", index);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_event);

        map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        Log.d("MapEventActivity", Integer.toString(getIntent().getIntExtra("index", -1)));
        Event event = ((PasselApplication) getApplication())
                .getEvents().get(getIntent().getIntExtra("index", -1));
        Location location = event.getLocation();
        IMapController mapController = map.getController();
        mapController.setZoom(18);
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//        GeoPoint startPoint = new GeoPoint(42.35965, -71.09206);
        mapController.setCenter(startPoint);

        eventMarker = new Marker(map);
        eventMarker.setPosition(startPoint);
        eventMarker.setTitle(event.getName());
        eventMarker.setSubDescription(new SimpleDateFormat("h:mm a").format(event.getStart()));
        eventMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(eventMarker);

        selfMarker = new Marker(map);
        selfMarker.setTitle("You");
        selfMarker.setPosition(new GeoPoint(startPoint));
        selfMarker.setIcon(getResources().getDrawable(R.drawable.ic_map_marker));
        map.getOverlays().add(selfMarker);

        addPersonMarker(42.33, -71.07, "Lisandro", "ETA: 5 min.");
        addPersonMarker(42.7, -71.11, "Carlos", "ETA: 9 min.");

        //Local messages passed go through here
        LocalBroadcastManager.getInstance(this).registerReceiver(locationMessageReceiver,
                new IntentFilter("send-location-data"));

        LocalBroadcastManager.getInstance(this).registerReceiver(peerLocationMessageReceiver,
                new IntentFilter("send-peer-location-data"));

        BackgroundGPSIntent = new Intent(getBaseContext(), BackgroundGPSService.class);
        startService(BackgroundGPSIntent);

        PeerLocationIntent = new Intent(getBaseContext(), PeerLocationDataStreamingService.class);
        startService(PeerLocationIntent);
    }

    private void addPersonMarker(double lat, double lon, String name, String eta) {
        Marker personMarker;
        if (peopleMarkerHashMap.containsKey(name)) {
            personMarker = peopleMarkerHashMap.get(name);

        } else {
            personMarker = new Marker(map);
            peopleMarkerHashMap.put(name, personMarker);
        }
        personMarker.setPosition(new GeoPoint(lat, lon));
        personMarker.setTitle(name);
        personMarker.setSubDescription(eta);
        personMarker.setIcon(getResources().getDrawable(R.drawable.ic_map_marker_red));
        personMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(personMarker);
        map.invalidate();
    }

    private BroadcastReceiver locationMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);
            selfMarker.setPosition(new GeoPoint(lat, lng));
            map.invalidate();

            Log.d("receiver", "Got message");
        }
    };

    private BroadcastReceiver peerLocationMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);
            String name = intent.getStringExtra("name");
            String eta = "ETA: " + intent.getStringExtra("eta") + " min.";
            addPersonMarker(lat, lng, name, eta);

            Log.d("receiver", "Got message");
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_edit:
//                openSearch();
                Intent settingsIntent = new Intent(MapEventActivity.this, NewEventActivity.class);
                settingsIntent.putExtra("index", getIntent().getIntExtra("index", -1));
                settingsIntent.putExtra("edit", true);
                MapEventActivity.this.startActivity(settingsIntent);
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

    @Override
    protected void onDestroy() {
        Log.d("MapEventActivity: ", "Destroyed");
        stopService(BackgroundGPSIntent);
        stopService(PeerLocationIntent);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(peerLocationMessageReceiver);
        super.onDestroy();
    }
}
