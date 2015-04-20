package com.passel;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PeerLocationDataStreamingService extends Service {

    Timer timer;

    public PeerLocationDataStreamingService() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        final List<Location> peerLocationsAneesh = decodeGPX(R.raw.waypoints_aneesh1);
        final List<Location> peerLocationsCarlos = decodeGPX(R.raw.waypoints_carlos1);
        final List<Location> peerLocationsPriscilla = decodeGPX(R.raw.waypoints_priscilla1);

        int delay = 1000; // delay for 1 sec.
        int period = 1000; // repeat every 1 sec.
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int locationIndex = 0;
            public void run() {
                if (locationIndex < peerLocationsAneesh.size()){
                sendMessage(peerLocationsAneesh.get(locationIndex), "Aneesh", Integer.toString((peerLocationsAneesh.size()-locationIndex)/10));
                locationIndex++;
                }
            }
        }, delay, period);

        timer.scheduleAtFixedRate(new TimerTask() {
            int locationIndex = 0;
            public void run() {
                if (locationIndex < peerLocationsCarlos.size()){
                    sendMessage(peerLocationsCarlos.get(locationIndex), "Carlos", Integer.toString((peerLocationsCarlos.size()-locationIndex)/10));
                    locationIndex++;
                }
            }
        }, delay, period);

        timer.scheduleAtFixedRate(new TimerTask() {
            int locationIndex = 0;
            public void run() {
                if (locationIndex < peerLocationsPriscilla.size()){
                    sendMessage(peerLocationsPriscilla.get(locationIndex), "Priscilla", Integer.toString((peerLocationsPriscilla.size()-locationIndex)/10));
                    locationIndex++;
                }
            }
        }, delay, period);

        return START_NOT_STICKY;
    }

    private void sendMessage(Location location, String name, String eta) {
        Log.d("sender", "Broadcasting message");

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Intent intent = new Intent("send-peer-location-data");
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("name", name);
        intent.putExtra("eta", eta);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private List<Location> decodeGPX(int resourceID){
        List<Location> list = new ArrayList<Location>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            Log.d("debug: ", "parsing gpx!");
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream inputStream = getResources().openRawResource(resourceID);
            Document document = documentBuilder.parse(inputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("wpt");

            for(int i = 0; i < nodelist_trkpt.getLength(); i++){

                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                Double newLatitude_double = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                Double newLongitude_double = Double.parseDouble(newLongitude);

                String newLocationName = newLatitude + ":" + newLongitude;
                Location newLocation = new Location(newLocationName);
                newLocation.setLatitude(newLatitude_double);
                newLocation.setLongitude(newLongitude_double);

                list.add(newLocation);

            }
            Log.d("debug: ", "done parsing gpx");
            Log.d("debug: ", "list size: " + Integer.toString(list.size()));
            inputStream.close();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
