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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.passel.api.APIClient;
import com.passel.api.APIError;
import com.passel.api.APIResponse;
import com.passel.data.Event;
import com.passel.data.Location;
import com.passel.util.Result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewEventActivity extends ActionBarActivity{
    private TextView fromTimeEtxt;
    private TextView toTimeEtxt;
    private ImageButton fromDateButton;
    private ImageButton toDateButton;

    private Calendar calendar;
    private Location location;
    private DatePickerDialog dpDialog;
    private TimePickerDialog tpDialog;

    private EditText guestInput;
    private Button addGuestButton;
    private ListView guestList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> guestNameList=new ArrayList<String>();

    private boolean isEditing = false;

    List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        addDateOnClickListeners();
        addTimeOnClickListeners();
        addGuestListeners();
        addMapPickerListener();

        if (getIntent().getBooleanExtra("edit", false)) {
            isEditing = true;
            int index = getIntent().getIntExtra("index", 0);
            Event event = ((PasselApplication) getApplication()).getEvents().get(index);
            ((EditText) findViewById(R.id.name)).setText(event.getName());
            ((EditText) findViewById(R.id.description_input)).setText(event.getDescription());
            ((TextView) findViewById(R.id.start_time_data)).setText(event.getStart().toString());
            ((TextView) findViewById(R.id.end_time_data)).setText(event.getEnd().toString());
            ((EditText) findViewById(R.id.location_input)).setText(event.getLocation().toString());

            guestNameList.addAll(event.getGuests());
            adapter.notifyDataSetChanged();

            setTitle("Edit Event");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }


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

    private void addDateOnClickListeners(){
        fromDateButton = (ImageButton) findViewById(R.id.start_date_button);
        toDateButton = (ImageButton) findViewById(R.id.end_date_button);

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

    }

    private void addTimeOnClickListeners(){
        fromTimeEtxt = (TextView) findViewById(R.id.start_time_data);
        toTimeEtxt = (TextView) findViewById(R.id.end_time_data);

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
    }

    private void addGuestListeners(){

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                guestNameList);

        guestInput = (EditText) findViewById(R.id.guest_input);
        addGuestButton = (Button) findViewById(R.id.add_guest_button);
        guestList = (ListView) findViewById(R.id.guest_list_view);
        guestList.setAdapter(adapter);

        //Listening to see if a user pressed enter if they are entering a Guest
        guestInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    addGuestButton.callOnClick();
                }
                return false;
            }
        });

        addGuestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("add guest");
                String guestInputText = String.valueOf(guestInput.getText());
                System.out.println("S" + guestInputText + "E");
                if (!guestInputText.matches("\n")) {
                    guestNameList.add(guestInputText);
                }
                guestInput.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(guestInput.getWindowToken(), 0);
            }
        });
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
                setDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year, TextView.BufferType.EDITABLE);
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
                    location = new Location(res.getDouble("lat"), res.getDouble("lng"));
                    Log.d("FIRST", "result:"+locationName);

                    EditText mapPickerButton = (EditText)findViewById(R.id.location_input);
                    mapPickerButton.setText(locationName);
                }
                break;
        }
    }

    private boolean createEvent(){
        try {
            // TOOD: change check mark to spinning animation for the duration
            // of this method, then do something at the end to validate to the
            // user that the event was sucessfully saved
            eventList = ((PasselApplication) getApplication()).getEvents();

            // TOOD: just fix validation in general
            EditText eventNameField = (EditText) findViewById(R.id.name);
            final String eventName = eventNameField.getText().toString();
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

            if(null == location) {
                throw new UnsupportedOperationException("Please enter a location");
            }

            DateFormat datetimeParser = new SimpleDateFormat("MM/dd/yyyyhh:mm a");

            Event newEvent = new Event(eventName,
                    datetimeParser.parse(startDate + startTime),
                    datetimeParser.parse(startDate + startTime),
                    description,
                    guestNameList,
                    location);
            if (isEditing){
                ((PasselApplication) getApplication()).updateEvent(
                        getIntent().getIntExtra("index", 0),
                        newEvent);
            } else {
                ((PasselApplication) getApplication()).addEvent(newEvent);
            }

            Calendar startDateTime = Calendar.getInstance();
            int startHour = Integer.parseInt(startTime.split(":|\\s")[0]);
            if (startTime.split(":|\\s")[0] == "PM"){
                startHour +=12;
            }

            startDateTime.set(Integer.parseInt(startDate.split("/")[2]),
                    Integer.parseInt(startDate.split("/")[0]),
                    Integer.parseInt(startDate.split("/")[1]),
                    startHour,
                    Integer.parseInt(startTime.split(":|\\s")[1]));

            Calendar endDateTime = Calendar.getInstance();
            int endHour = Integer.parseInt(startTime.split(":|\\s")[0]);
            if (endTime.split(":|\\s")[0] == "PM"){
                endHour +=12;
            }

            endDateTime.set(Integer.parseInt(endDate.split("/")[2]),
                    Integer.parseInt(endDate.split("/")[0]),
                    Integer.parseInt(endDate.split("/")[1]),
                    endHour,
                    Integer.parseInt(endTime.split(":|\\s")[1]));


            Result<APIResponse, APIError> result = new APIClient().addEvent(
                            eventName,
                            startDateTime.getTime(),
                            endDateTime.getTime(),
                            description);

            if (result.isOk()){
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "Unable to communicate to server",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
