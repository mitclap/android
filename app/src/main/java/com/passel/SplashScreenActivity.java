package com.passel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        addSignUpButtonListener();
        addLogInButtonListener();
    }

    private void addSignUpButtonListener(){
        Button signUpButton = (Button)findViewById(R.id.splash_sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Splash", "Here!");
                Intent myIntent = new Intent(SplashScreenActivity.this, SignUpActivity.class);
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
                SplashScreenActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
