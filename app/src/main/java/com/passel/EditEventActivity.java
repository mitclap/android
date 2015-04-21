package com.passel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.passel.data.Event;
import com.passel.data.PasselApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

// TODO: Change this so that it actually edits an event
public class EditEventActivity extends Activity {
    private EditText fromDateEtxt;
    private EditText toDateEtxt;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        // Opens Calendar app
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 2);
        long time = cal.getTime().getTime();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        builder.appendPath(Long.toString(time));
        Intent intent = new Intent(Intent.ACTION_VIEW, builder.build());
        startActivity(intent);

        if (getIntent().getBooleanExtra("edit", false)) {
            int index = getIntent().getIntExtra("index", 0);
            Event event = ((PasselApplication) getApplication()).getEvents().get(index);
            ((EditText) findViewById(R.id.name)).setText(event.getName());
            ((EditText) findViewById(R.id.description_input)).setText(event.getDescription());
            ((TextView) findViewById(R.id.start_time_data)).setText(event.getStart().toString());
            ((TextView) findViewById(R.id.end_time_data)).setText(event.getEnd().toString());
            ((EditText) findViewById(R.id.location_input)).setText(event.getLocation().toString());

            //guestNameList.addAll(event.getGuests());
            //adapter.notifyDataSetChanged();

            setTitle("Edit Event");
            //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }



    }

    private boolean editEvent(){
        // TODO fix this
        ((PasselApplication) getApplication()).updateEvent(null); // TODO Ew null

        return true;
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
                if (editEvent()){
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
