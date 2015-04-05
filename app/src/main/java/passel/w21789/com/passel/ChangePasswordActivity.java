package passel.w21789.com.passel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


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
                Intent myIntent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
//        myIntent.putExtra("key", value); //Optional parameters
                ChangePasswordActivity.this.startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
