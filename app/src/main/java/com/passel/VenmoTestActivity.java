package com.passel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Adapted from https://github.com/mgottein/app-switch-demo/
 */
public class VenmoTestActivity extends Activity {

    private AutoCompleteTextView mRecipient;
    private RecipientAdapter mAdapter;
    private EditText mAmount;
    private EditText mNote;
    private Button mPay;
    private Button mCharge;
    private ArrayList<String> mRecipientList;


    private static final String APP_NAME = "App Switch Demo";
    private static final String APP_SECRET = "vGgLeNF7ERp7ZGphvY38ZKdT4jrxWes4";
    private static final String APP_ID = "1873";
    private static final int VENMO_REQUEST_CODE = 1;

    private static final String PAY = "pay";
    private static final String CHARGE = "charge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venmo_test);
        mRecipient = (AutoCompleteTextView) findViewById(R.id.recipient);
        mRecipientList = new ArrayList<>();

        mAmount = (EditText) findViewById(R.id.amount);
        mNote = (EditText) findViewById(R.id.note);
        mPay = (Button) findViewById(R.id.pay);
        mCharge = (Button) findViewById(R.id.charge);

        mAdapter = new RecipientAdapter(this);
        mRecipient.setAdapter(mAdapter);
        addRecipientsListener();

        mPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTransactionWithInput(PAY);
            }
        });

        mCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTransactionWithInput(CHARGE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_venmo_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addRecipientsListener() {

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                mRecipientList);

        //final EditText recipientInput = (AutoCompleteTextView) findViewById(R.id.recipient);
        final ListView recipientList = (ListView) findViewById(R.id.recipient_list_view);
        recipientList.setAdapter(adapter);

        //Listening to see if a user pressed enter if they are entering a Guest
        mRecipient.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String guestInputText = String.valueOf(mRecipient.getText());
                    if (!guestInputText.matches("\n")) {
                        mRecipientList.add(guestInputText);
                    }
                    mRecipient.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mRecipient.getWindowToken(), 0);
                }
                return false;
            }
        });
    }
    private void doTransactionWithInput(String txn) {
        ArrayList<String> recipients = mRecipientList;
        String amount = mAmount.getText().toString();
        String note = mNote.getText().toString();
        doTransaction(recipients, amount, note, txn);
    }

    private void doTransaction(ArrayList<String> recipients, String amount, String note, String txn) {
        try {
            Intent venmoIntent = VenmoLibrary.openVenmoPaymentWithMultipleRecipients(APP_ID, APP_NAME, recipients, amount, note, txn);
            startActivityForResult(venmoIntent, VENMO_REQUEST_CODE); //1 is the requestCode we are using for Venmo. Feel free to change this to another number.
        }
        catch (android.content.ActivityNotFoundException e) //Venmo native app not install on device, so let's instead open a mobile web version of Venmo in a WebView
        {
            Intent venmoIntent = new Intent(VenmoTestActivity.this, VenmoWebViewActivity.class);
            String venmo_uri = VenmoLibrary.openVenmoPaymentInWebViewWithMultipleRecipients(APP_ID, APP_NAME, recipients, amount, note, txn);
            venmoIntent.putExtra("url", venmo_uri);
            startActivityForResult(venmoIntent, VENMO_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case VENMO_REQUEST_CODE: { //1 is the requestCode we picked for Venmo earlier when we called startActivityForResult
                if(resultCode == RESULT_OK) {
                    String signedrequest = data.getStringExtra("signedrequest");
                    if(signedrequest != null) {
                        VenmoLibrary.VenmoResponse response = (new VenmoLibrary()).validateVenmoPaymentResponse(signedrequest, APP_SECRET);
                        if(response.getSuccess().equals("1")) {
                            //Payment successful.  Use data from response object to display a success message
                            String note = response.getNote();
                            String amount = response.getAmount();
                            Toast.makeText(this, "Made payment!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        String errorMessage = data.getStringExtra("error_message");
                        Toast.makeText(this, "Couldn't make payment: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                else if(resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Payment was cancelled!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
