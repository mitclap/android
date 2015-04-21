package com.passel;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.passel.data.Location;

public class BackgroundGPSService extends Service {

    private final int REQUEST_LOCATION_UPDATE_TIMER = 30*1000;
    private final int REQUEST_LOCATION_UPDATE_MINDISTANCE_METER = 0;
    LocationManager locationManager;
    LocationManager locationManagerGPS;
    LocationListener locationListener;

    private void sendMessage(android.location.Location location) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("send-location-data");
        intent.putExtra("location", new Location(location));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                sendMessage(location);  // send to the main Activity so user can see
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, //LocationProvider.NETWORK_PROVIDER, // GPS_PROVIDER
                REQUEST_LOCATION_UPDATE_TIMER, // 5*60*1000
                REQUEST_LOCATION_UPDATE_MINDISTANCE_METER, // 500
                locationListener);

        locationManagerGPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerGPS.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,//LocationManager.NETWORK_PROVIDER, // GPS_PROVIDER
                REQUEST_LOCATION_UPDATE_TIMER, // 5*60*1000
                REQUEST_LOCATION_UPDATE_MINDISTANCE_METER, // 500
                locationListener);

        android.location.Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLocation == null){
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (lastLocation != null){
            sendMessage(lastLocation);
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onDestroy() {
//        timer.cancel();
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }
}
