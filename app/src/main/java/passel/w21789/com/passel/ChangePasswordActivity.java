package passel.w21789.com.passel;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ChangePasswordActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        return true;
    }

    private boolean checkPasswordAndConfirmPassword(String password,String confirmPassword)
    {
        boolean confirmed = false;
        if (confirmPassword != null && password != null && confirmPassword.length() > 0 && password.length() > 0)
        {
            if (password.equals(confirmPassword))
            {
                confirmed = true;
            }
        }
        return confirmed;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
//            case R.id.action_settings:
////                openSearch();
//                Intent settingsIntent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
//                ChangePasswordActivity.this.startActivity(settingsIntent);
//                return true;
            case R.id.change_password_check:
                // Go back to the settings page
                //TODO: Insert password changing logic here
                //TODO: Make it display that little text at the bottom if the pw was changed
                //Take inputs from the password fields
                String password = ((EditText)findViewById(R.id.passwordNew)).getText().toString();
                String passwordConfirm = ((EditText)findViewById(R.id.passwordConfirm)).getText().toString();
                boolean confirmed = checkPasswordAndConfirmPassword(password, passwordConfirm);
                if (confirmed) {
                    finish();
                    return true;
                } else {
                    Context context = getApplicationContext();
                    String text = "Passwords do not match";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
