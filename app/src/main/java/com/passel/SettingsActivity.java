package com.passel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import com.passel.R;


public class SettingsActivity extends ActionBarActivity {
//    private TextView emailAddress;
//
//    private DatePickerDialog fromDatePickerDialog;
//    private DatePickerDialog toDatePickerDialog;
//
//    private SimpleDateFormat dateFormatter;
//
//    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        addChangePasswordButtonListener();
    }

    private void addChangePasswordButtonListener(){
        Button changeButton = (Button) findViewById(R.id.btnChangePassword);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
//        myIntent.putExtra("key", value); //Optional parameters
                SettingsActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_new_event, menu);
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
//                if (createEvent()){
//                    finish();
//                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
