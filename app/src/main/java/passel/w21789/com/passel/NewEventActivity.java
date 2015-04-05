package passel.w21789.com.passel;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class NewEventActivity extends ActionBarActivity {
    private EditText fromDateEtxt;
    private EditText fromTimeEtxt;
    private EditText toDateEtxt;
    private EditText toTimeEtxt;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    private CalendarView calendarView;

    private PopupWindow calendarPopUp;
    private LinearLayout layout;
    private ViewGroup.LayoutParams params;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        // Opens Calendar app
        /*
        Calendar cal = Calendar.getInstance();
        long time = cal.getTime().getTime();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        builder.appendPath(Long.toString(time));
        Intent intent = new Intent(Intent.ACTION_VIEW, builder.build());
        startActivity(intent);
        */

        fromDateEtxt = (EditText) findViewById(R.id.start_date_data);
        toDateEtxt = (EditText) findViewById(R.id.end_date_data);

        fromDateEtxt.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                setDate(R.id.start_date_data);
            }
        });
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
                //if (createEvent()){
                //    finish();
                //}
                return true;
            case R.id.start_date_data:
                System.out.println("is it going here?");
                setDate(id);
                return true;
            case R.id.end_date_data:
                setDate(id);
                return true;
            case R.id.start_time_data:
                setTime(id);
                return true;
            case R.id.end_time_data:
                setTime(id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDate(int id){

        final EditText setDate = (EditText) findViewById(id);

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
            @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                setDate.setText(dayOfMonth + '-' + (monthOfYear + 1) + '-' +year);
            }
        }, mYear, mMonth, mDay);

        fromDatePickerDialog.show();
    }

    private void setTime(int id){

    }

    public void setUp() throws IOException, GeneralSecurityException{
        //HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    }
}
