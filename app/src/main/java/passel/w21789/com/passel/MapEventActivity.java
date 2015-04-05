package passel.w21789.com.passel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;


public class MapEventActivity extends ActionBarActivity {
    HashMap<String, Marker> peopleMarkerHashMap = new HashMap<>();

    Marker startMarker;
    Marker eventMarker;
    Marker selfMarker;

    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_event);

        map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(14);
        GeoPoint startPoint = new GeoPoint(42.3736, -71.1106);
        mapController.setCenter(startPoint);

        startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setTitle("Aneesh Aggarwal");
        startMarker.setSubDescription("ETA: 12 min.");
        startMarker.setIcon(getResources().getDrawable(R.drawable.marker_via));
//        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_action_room));

        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);

        eventMarker = new Marker(map);
        eventMarker.setPosition(startPoint);
        eventMarker.setTitle("Team Passel Group Meeting");
        eventMarker.setSubDescription("8:56 PM");
        eventMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(eventMarker);

        selfMarker = new Marker(map);
        selfMarker.setTitle("You");
        selfMarker.setPosition(new GeoPoint(startPoint));
        selfMarker.setIcon(getResources().getDrawable(R.drawable.marker_node));
        map.getOverlays().add(selfMarker);

        moveMarker();

        addPersonMarker(42.33,-71.07,"Lisandro", "ETA: 5 min.");
        addPersonMarker(42.7,-71.11,"Carlos", "ETA: 9 min.");

        //Local messages passed go through here
        LocalBroadcastManager.getInstance(this).registerReceiver(locationMessageReceiver,
                new IntentFilter("send-location-data"));

        Intent intentName = new Intent(getBaseContext(), BackgroundGPSService.class);
        startService(intentName);
    }

    private void  moveMarker(){
        double x = 42.3598;
        double y = -71.0921;
        startMarker.setPosition(new GeoPoint(x, y));
        map.invalidate();
    }

    private void addPersonMarker(double lat, double lon, String name, String eta){
        Marker personMarker;
        if (peopleMarkerHashMap.containsKey(name)) {
            personMarker = peopleMarkerHashMap.get(name);

        }
        else {
            personMarker = new Marker(map);
            peopleMarkerHashMap.put(name,personMarker);
        }
        personMarker.setPosition(new GeoPoint(lat, lon));
        personMarker.setTitle(name);
        personMarker.setSubDescription(eta);
        personMarker.setIcon(getResources().getDrawable(R.drawable.marker_via));
        map.getOverlays().add(personMarker);
        map.invalidate();
    }

    private BroadcastReceiver locationMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng",0);
            selfMarker.setPosition(new GeoPoint(lat, lng));
            map.invalidate();

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationMessageReceiver);
        super.onDestroy();
    }
}
