package passel.w21789.com.passel;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class NewEventActivity extends ActionBarActivity{
    private TextView fromTimeEtxt;
    private TextView toTimeEtxt;
    private ImageButton fromDateButton;
    private ImageButton toDateButton;

    private Calendar calendar;

    private DatePickerDialog dpDialog;
    private TimePickerDialog tpDialog;

    private EditText guestInput;
    private Button addGuestButton;
    private ListView guestList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> guestNameList=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                guestNameList);
        fromDateButton = (ImageButton) findViewById(R.id.start_date_button);
        toDateButton = (ImageButton) findViewById(R.id.end_date_button);
        fromTimeEtxt = (TextView) findViewById(R.id.start_time_data);
        toTimeEtxt = (TextView) findViewById(R.id.end_time_data);
        guestInput = (EditText) findViewById(R.id.guest_input);
        addGuestButton = (Button) findViewById(R.id.add_guest_button);
        guestList = (ListView) findViewById(R.id.guest_list_view);
        guestList.setAdapter(adapter);

        // Setting Date
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

        // Setting Time
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
                if (!guestInputText.equals("") && !guestInputText.equals(" ")){
                    guestNameList.add(guestInputText);
                }
                guestInput.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(guestInput.getWindowToken(),0);
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
            default:
                return super.onOptionsItemSelected(item);
        }
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
                setDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year, TextView.BufferType.EDITABLE);
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
}
