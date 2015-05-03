package com.passel;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.passel.api.APIClient;
import com.passel.api.APIError;
import com.passel.api.APIResponse;
import com.passel.data.Event;
import com.passel.data.JsonMapper;
import com.passel.data.Location;
import com.passel.data.PasselApplication;
import com.passel.util.Result;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewEventActivity extends ActionBarActivity {
    private Location location;

    private ArrayList<String> attendeeNameList = new ArrayList<>();

    List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        findViewById(R.id.start_date_data).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setDate(R.id.start_date_data);
            }
        });

        findViewById(R.id.end_date_data).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(R.id.end_date_data);
            }
        });
        findViewById(R.id.start_time_data).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(R.id.start_time_data);
            }
        });

        findViewById(R.id.end_time_data).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(R.id.end_time_data);
            }
        });

        findViewById(R.id.location_input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(NewEventActivity.this, LocationPickerActivity.class);
                NewEventActivity.this.startActivityForResult(myIntent, 1);
            }
        });

        addGuestListeners();
        setDefaultDate();
        setDefaultTime();
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
                if (createEvent()) {
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

    private void addGuestListeners() {

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                attendeeNameList);

        final EditText attendeeInput = (EditText) findViewById(R.id.attendee_input);
        final ListView attendeeList = (ListView) findViewById(R.id.attendee_list_view);
        attendeeList.setAdapter(adapter);

        //Listening to see if a user pressed enter if they are entering a Guest
        attendeeInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String attendeeInputText = String.valueOf(attendeeInput.getText());
                    if (!attendeeInputText.matches("\n")) {
                        attendeeNameList.add(attendeeInputText);
                    }
                    attendeeInput.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(attendeeInput.getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    private void setDate(int id) {

        final TextView setDate = (TextView) findViewById(id);

        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                System.out.println("Date Picker Set");
                setDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
            }
        }, mYear, mMonth, mDay);

        dpDialog.show();
    }

    private void setTime(int id) {

        final TextView setTime = (TextView) findViewById(id);
        String setTimeText = setTime.getText().toString();
        int mHour = Integer.parseInt(setTimeText.substring(0, 2));
        int mMinute = Integer.parseInt(setTimeText.substring(3, 5));
        String textMeridiem = setTimeText.substring(6);

        if (textMeridiem.equals("PM")) {
            System.out.println("In if loop");
            mHour += 12;
        } else if (textMeridiem.equals("AM") && mHour == 12) {
            mHour = 0;
        }

        TimePickerDialog tpDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int modHour = hourOfDay % 12;
                String sHour = String.valueOf(modHour);
                if (modHour == 0) {
                    sHour = "12";
                }
                String sMinute = String.valueOf(minute);
                String meridiem = "AM";
                if (modHour < 10 && modHour > 0) {
                    sHour = "0" + sHour;
                }
                if (hourOfDay > 11) {
                    meridiem = "PM";
                }

                if (minute < 10) {
                    sMinute = "0" + minute;
                }
                setTime.setText(sHour + ":" + sMinute + " " + meridiem);
            }
        }, mHour, mMinute, false);

        tpDialog.show();
    }

    private void setDefaultDate(){
        TextView fromDateView = (TextView) findViewById(R.id.start_date_data);
        TextView toDateView = (TextView) findViewById(R.id.end_date_data);

        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        fromDateView.setText((mMonth + 1) + "/" + mDay + "/" + mYear);
        toDateView.setText((mMonth + 1) + "/" + mDay + "/" + mYear);
    }

    private void setDefaultTime(){
        TextView fromTimeEtxt = (TextView) findViewById(R.id.start_time_data);
        TextView toTimeEtxt = (TextView) findViewById(R.id.end_time_data);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String timeText = "";

        if (hour==23){
            hour = 0;
            timeText += "12:";
        } else {
            hour += 1;

        }

        int modHour = hour % 12;
        String sHour = String.valueOf(modHour);
        if (modHour == 0){
            sHour = "12";
        }
        String meridiem = "AM";
        if (modHour < 10 && modHour >0){
            sHour = "0" + sHour;
        }
        if (hour > 11){
            meridiem = "PM";
        }

        fromTimeEtxt.setText(sHour + ":" + "00" + " " + meridiem);
        toTimeEtxt.setText(sHour + ":" + "30" + " " + meridiem);

    }

    /* Called when the map activity's finished */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String locationName = res.getString("loc_name");
                    location = new Location(res.getDouble("lat"), res.getDouble("lng"));
                    // Make sure the edit text isn't blank after getting a location
                    EditText mapPickerButton = (EditText) findViewById(R.id.location_input);
                    mapPickerButton.setText(locationName);
                }
                break;
        }
    }

    private boolean createEvent() {
        // TOOD: change check mark to spinning animation for the duration
        // of this method, then do something at the end to validate to the
        // user that the event was sucessfully saved
        eventList = ((PasselApplication) getApplication()).getEvents();

        // TOOD: just fix validation in general
        EditText eventNameField = (EditText) findViewById(R.id.name);
        final String eventName = eventNameField.getText().toString();

        if ((!eventName.matches("^[a-zA-Z0-9][a-zA-Z0-9 ]*[a-zA-Z0-9]$") || (eventName.length() > 30))) {
            showToast("Please enter a valid event name:  only lowercase/uppercase letters and numbers, max of 30 chars.");
            return false;
        }

        TextView startDateField = (TextView) findViewById(R.id.start_date_data);
        String startDate = startDateField.getText().toString();

        TextView endDateField = (TextView) findViewById(R.id.end_date_data);
        String endDate = endDateField.getText().toString();

        TextView startTimeField = (TextView) findViewById(R.id.start_time_data);
        String startTime = startTimeField.getText().toString();

        if (startTime == "") {
            showToast("Please enter a start time");
            return false;
        }

        TextView endTimeField = (TextView) findViewById(R.id.end_time_data);
        String endTime = endTimeField.getText().toString();
        EditText descriptionField = (EditText) findViewById(R.id.description_input);
        String description = descriptionField.getText().toString();

        if (description.length() > 1000) {
            showToast("Please enter a valid description: max of 1000 chars.");
            return false;
        }

        if (null == location) {
            showToast("Please enter a location");
            return false;
        }

        DateFormat datetimeParser = new SimpleDateFormat("MM/dd/yyyyhh:mm a");

        try {
            ((PasselApplication) getApplication()).createEvent(eventName,
                    datetimeParser.parse(startDate + startTime),
                    datetimeParser.parse(startDate + startTime),
                    description,
                    attendeeNameList,
                    location);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar startDateTime = Calendar.getInstance();
        int startHour = Integer.parseInt(startTime.split(":|\\s")[0]);
        if (startTime.split(":|\\s")[0] == "PM") {
            startHour += 12;
        }

        startDateTime.set(Integer.parseInt(startDate.split("/")[2]),
                Integer.parseInt(startDate.split("/")[0]),
                Integer.parseInt(startDate.split("/")[1]),
                startHour,
                Integer.parseInt(startTime.split(":|\\s")[1]));

        Calendar endDateTime = Calendar.getInstance();
        int endHour = Integer.parseInt(startTime.split(":|\\s")[0]);
        if (endTime.split(":|\\s")[0] == "PM") {
            endHour += 12;
        }

        endDateTime.set(Integer.parseInt(endDate.split("/")[2]),
                Integer.parseInt(endDate.split("/")[0]),
                Integer.parseInt(endDate.split("/")[1]),
                endHour,
                Integer.parseInt(endTime.split(":|\\s")[1]));

        JsonMapper mapper = ((PasselApplication) getApplication()).getJsonMapper();
        Result<APIResponse, APIError> result = new APIClient(mapper).addEvent(
                eventName,
                startDateTime.getTime(),
                endDateTime.getTime(),
                description);

        if (result.isOk()) {
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "Unable to communicate to server",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
        return;
    }
}
