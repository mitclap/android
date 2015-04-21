package com.passel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.passel.api.APIClient;
import com.passel.api.APIError;
import com.passel.api.APIResponse;
import com.passel.data.JsonMapper;
import com.passel.data.PasselApplication;
import com.passel.util.Result;

public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpClick();
            }
        });
        findViewById(R.id.sign_up_username).requestFocus();
    }

    private void onSignUpClick() {
        String usernameInput = ((EditText) findViewById(R.id.sign_up_username)).getText().toString();

        String passwordInput = ((EditText) findViewById(R.id.sign_up_password)).getText().toString();
        String confirmPasswordInput = ((EditText) findViewById(R.id.sign_up_confirm_password)).getText().toString();

        if (!passwordInput.equals(confirmPasswordInput)) {
            showToast("Passwords do not match");
            return;
        }

        if ((!usernameInput.matches("^[a-zA-Z0-9]+$") || (usernameInput.length() > 30))) {
            showToast("Invalid username: only lowercase/uppercase letters and numbers, max of 30 chars.");
            return;
        }

        JsonMapper mapper = ((PasselApplication) getApplication()).getJsonMapper();
        Result<APIResponse, APIError> result = new APIClient(mapper).signup(usernameInput, passwordInput);

        if (result.isOk()) {
            if (result.unwrap().getCode() == 409) {
                showToast("Username already exists");
                return;
            }
            SignUpActivity.this.startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
        } else {
            showToast("Unable to communicate to server");
            return;
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_settings == item.getItemId()) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
