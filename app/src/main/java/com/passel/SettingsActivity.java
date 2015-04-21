package com.passel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        // TODO: Add settings menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
