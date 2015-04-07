package passel.w21789.com.passel;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.CalendarView;

import android.database.Cursor;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.ResourceProxy;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class NewEventActivity extends ActionBarActivity {
    private TextView fromTimeEtxt;
    private TextView toTimeEtxt;
    private ImageButton fromDateButton;
    private ImageButton toDateButton;

    private Calendar calendar;

    private DatePickerDialog dpDialog;
    private TimePickerDialog tpDialog;

    private double latitude = 0;
    private double longitude = 0;

    ArrayList<PasselEvent> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        fromDateButton = (ImageButton) findViewById(R.id.start_date_button);
        toDateButton = (ImageButton) findViewById(R.id.end_date_button);
        fromTimeEtxt = (TextView) findViewById(R.id.start_time_data);
        toTimeEtxt = (TextView) findViewById(R.id.end_time_data);

        // Setting Date
        fromDateButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                setDate(R.id.start_date_data);
            }
        });

        toDateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(R.id.end_date_data);
            }
        });

        // Setting Time
        fromTimeEtxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(R.id.start_time_data);
            }
        });

        toTimeEtxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(R.id.end_time_data);
            }
        });

        addMapPickerListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch (id) {
            case R.id.new_event_cancel:
                finish();
                return true;
            case R.id.new_event_check:
                if (createEvent()){
                    Intent intent = new Intent();
                    Bundle conData = new Bundle();
                    intent.putExtras(conData);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDate(int id){

        final EditText setDate = (EditText) findViewById(id);

        calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        dpDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                System.out.println("Date Picker Set");
                setDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year, TextView.BufferType.EDITABLE);
            }
        }, mYear, mMonth, mDay);

        dpDialog.show();
    }

    private void setTime(int id){

        final TextView setTime = (TextView) findViewById(id);
        String setTimeText = setTime.getText().toString();
        int mHour =  Integer.parseInt(setTimeText.substring(0,2));
        int mMinute = Integer.parseInt(setTimeText.substring(3,5));
        String textMeridiem = setTimeText.substring(6);

        if (textMeridiem.equals("PM")){
            System.out.println("In if loop");
            mHour += 12;
        } else if (textMeridiem.equals("AM") && mHour== 12){
            mHour = 0;
        }

        tpDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){
            public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                int modHour = hourOfDay % 12;
                String sHour = String.valueOf(modHour);
                if (modHour == 0){
                    sHour = "12";
                }
                String sMinute = String.valueOf(minute);
                String meridiem = "AM";
                if (modHour < 10 && modHour >0){
                    sHour = "0" + sHour;
                }
                if (hourOfDay > 11){
                    meridiem = "PM";
                }

                if (minute < 10){
                    sMinute = "0" + minute;
                }
                setTime.setText(sHour + ":" + sMinute + " " + meridiem);
            }
        }, mHour, mMinute, false);

        tpDialog.show();
    }

    private void addMapPickerListener(){
        EditText mapPickerButton = (EditText)findViewById(R.id.location_input);
        mapPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("NewEventActivity", "Here!");
                Intent myIntent = new Intent(NewEventActivity.this, LocationPickerActivity.class);
//        myIntent.putExtra("key", value); //Optional parameters
                NewEventActivity.this.startActivityForResult(myIntent, 1);
            }
        });
    }

    /* Called when the map activity's finished */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String locationName = res.getString("loc_name");
                    latitude = res.getDouble("lat");
                    longitude = res.getDouble("lng");
                    Log.d("FIRST", "result:"+locationName);

                    EditText mapPickerButton = (EditText)findViewById(R.id.location_input);
                    mapPickerButton.setText(locationName);
                }
                break;
        }
    }

    private boolean createEvent(){
        try {
            eventList = getPasselEvents();

            EditText eventNameField = (EditText) findViewById(R.id.eventName);
            String eventName = eventNameField.getText().toString();
            if(eventName == ""){
                throw new UnsupportedOperationException("Please enter an event name");
            }

            EditText startDateField = (EditText) findViewById(R.id.start_date_data);
            String startDate = startDateField.getText().toString();

            EditText endDateField = (EditText) findViewById(R.id.end_date_data);
            String endDate = endDateField.getText().toString();

            TextView startTimeField = (TextView) findViewById(R.id.start_time_data);
            String startTime = startTimeField.getText().toString();

            if(startTime == ""){
                throw new UnsupportedOperationException("Please enter a start time");
            }

            TextView endTimeField = (TextView) findViewById(R.id.end_time_data);
            String endTime = endTimeField.getText().toString();

            EditText descriptionField = (EditText) findViewById(R.id.description_input);
            String description = descriptionField.getText().toString();

            EditText usersField = (EditText) findViewById(R.id.guests);
            String guests = usersField.getText().toString();

            ArrayList<String> guestsList = new ArrayList<>();
            guestsList.addAll(Arrays.asList(guests.split("\\s+")));

            if(latitude == 0){
                throw new UnsupportedOperationException("Please enter a location");
            }

            ArrayList<Double> location = new ArrayList<>();
            location.addAll(Arrays.asList(latitude, longitude));

            eventList.add(new PasselEvent(eventName, startTime, guestsList, location));

            setPasselEvents(eventList);

            return true;
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
            return false;
        }
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
}
