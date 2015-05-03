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

    private final int REQUEST_LOCATION_UPDATE_TIMER = 5*1000;
    private final int REQUEST_LOCATION_UPDATE_MINDISTANCE_METER = 0;
    LocationManager locationManager;
    LocationManager locationManagerGPS;
    LocationListener locationListener;
    android.location.Location oldLocation;

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

        android.location.Location lastLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        android.location.Location lastLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        android.location.Location bestLastLocation;

        if (isBetterLocation(lastLocationNetwork, lastLocationGPS)) {
            bestLastLocation = lastLocationNetwork;
        } else {
            bestLastLocation = lastLocationGPS;
        }

        if (isBetterLocation(bestLastLocation, oldLocation)) {
            oldLocation = bestLastLocation;
            sendMessage(oldLocation);
        } else {
            sendMessage(oldLocation);
        }

        return START_NOT_STICKY;
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(android.location.Location location, android.location.Location currentBestLocation) {
        final int FIVE_SECONDS = 1000 * 5;

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > FIVE_SECONDS;
        boolean isSignificantlyOlder = timeDelta < -FIVE_SECONDS;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
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
