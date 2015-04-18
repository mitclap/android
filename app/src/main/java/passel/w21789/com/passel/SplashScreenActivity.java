package passel.w21789.com.passel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import passel.w21789.com.passel.messaging.APIClient;
import passel.w21789.com.passel.messaging.SignupMessage;


public class SplashScreenActivity extends Activity {
    ArrayList<PasselEvent> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        addSignUpButtonListener();
        addLogInButtonListener();

        setPasselEvents(eventList);

        ArrayList<String> studyGuests = new ArrayList<>();
        studyGuests.addAll(Arrays.asList("Aneesh", "Carlos"));

        ArrayList<Double> studyLocation = new ArrayList<>();
        studyLocation.addAll(Arrays.asList(42.35965, -71.09206));

        eventList.add(new PasselEvent("Study Session", "8:00PM", studyGuests, studyLocation));

        studyLocation = new ArrayList<>();
        studyLocation.addAll(Arrays.asList(42.36100, -71.09649));

        eventList.add(new PasselEvent("Grab Coffee at Flour with Aneesh", "10:00 PM", studyGuests, studyLocation));

        studyLocation = new ArrayList<>();
        studyLocation.addAll(Arrays.asList(42.35902, -71.09517));

        eventList.add(new PasselEvent("Team Passel Group Meeting", "5:00 PM", studyGuests, studyLocation));

        studyLocation = new ArrayList<>();
        studyLocation.addAll(Arrays.asList(42.36634, -71.01661));

        eventList.add(new PasselEvent("Meet at Airport", "9:00 PM", studyGuests, studyLocation));

        studyLocation = new ArrayList<>();
        studyLocation.addAll(Arrays.asList(42.35745, -71.09412));

        eventList.add(new PasselEvent("DT Practice", "10:00 PM", studyGuests, studyLocation));

        setPasselEvents(eventList);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String message = APIClient.getObjectMapper().writeValueAsString(new SignupMessage("testUsername", "testPubKey"));
                    Log.e("DEBUGGG", "Made a json :)");
                    Log.e("DEBUGGG", message);
                    HttpResponse response = APIClient.post(Constants.API_BASE_URL + "/accounts", message);
                    Log.e("DEBUGGG", "Request was successful :)");
                    Log.e("DEBUGGG", response.getEntity().toString());
                } catch (JsonProcessingException e) {
                    Log.e("DEBUGGG", "Couldn't serialize the json :(");
                } catch (IOException e) {
                    Log.e("DEBUGGG", "Error when trying to make the POST request :(", e);
                } catch (RuntimeException e) {
                    Log.e("DEBUGGG", "I just failed completely :(", e);
                }
                return null;
            }
        }.execute();
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

    public void setPasselEvents(ArrayList<PasselEvent> events) {
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

    public ArrayList<PasselEvent> getPasselEvents() {
        ArrayList<PasselEvent> parsedEvents = new ArrayList<>();
        String eventsKey = "PASSEL_EVENTS";
        Gson gson = new GsonBuilder().create();

        Context context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String savedValue = sharedPref.getString(eventsKey, "");
        if (savedValue.equals("")) {
            parsedEvents = null;
        } else {
            parsedEvents = gson.fromJson(savedValue, new TypeToken<ArrayList<PasselEvent>>() {}.getType());
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
