package passel.w21789.com.passel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import passel.w21789.com.passel.api.APIClient;
import passel.w21789.com.passel.data.Event;
import passel.w21789.com.passel.data.Location;


public class SplashScreenActivity extends Activity {
    ArrayList<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        addSignUpButtonListener();
        addLogInButtonListener();

        // TODO: switch to joda-time maybe
        Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        cal.setTimeInMillis(0);
        cal.set(2015, Calendar.APRIL, 21, 20, 0, 0);
        Date start = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        Date end = cal.getTime();

        eventList.add(new Event("Study Session",

                start, end,
                "Study for 21W.789",
                Arrays.asList("Aneesh", "Carlos"),
                new Location(42.35965, -71.09206)));

//        eventList.add(new PasselEvent("Grab Coffee at Flour with Aneesh", "10:00 PM", studyGuests, studyLocation));
//
//        studyLocation = new ArrayList<>();
//        studyLocation.addAll(Arrays.asList(42.35902, -71.09517));
//
//        eventList.add(new PasselEvent("Team Passel Group Meeting", "5:00 PM", studyGuests, studyLocation));
//
//        studyLocation = new ArrayList<>();
//        studyLocation.addAll(Arrays.asList(42.36634, -71.01661));
//
//        eventList.add(new PasselEvent("Meet at Airport", "9:00 PM", studyGuests, studyLocation));
//
//        studyLocation = new ArrayList<>();
//        studyLocation.addAll(Arrays.asList(42.35745, -71.09412));
//
//        eventList.add(new PasselEvent("DT Practice", "10:00 PM", studyGuests, studyLocation));
//
        setEvents(eventList);

        new APIClient().signup("testUsername", "testPubKey");
    }

    private void addSignUpButtonListener(){
        Button signUpButton = (Button)findViewById(R.id.splash_sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Splash", "Here!");
                Intent myIntent = new Intent(SplashScreenActivity.this, SignUpActivity.class);
//        myIntent.putExtra("key", value); //Optional parameters
                SplashScreenActivity.this.startActivity(myIntent);
            }
        });
    }

    private void addLogInButtonListener(){
        Button logInButton = (Button)findViewById(R.id.splash_log_in_button);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Splash", "Here!");
                Intent myIntent = new Intent(SplashScreenActivity.this, LogInActivity.class);
//        myIntent.putExtra("key", value); //Optional parameters
                SplashScreenActivity.this.startActivity(myIntent);
            }
        });
    }

    public void setEvents(ArrayList<Event> events) {
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
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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
}
