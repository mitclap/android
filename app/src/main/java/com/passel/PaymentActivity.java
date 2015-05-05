package com.passel;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Adapted from https://github.com/mgottein/app-switch-demo/
 *
 * Payment Activity: The activity to be launched after an event has finished and it is necesssary
 * to charge Venmo-enabled attendees who arrived late.
 *
 * TODO: Is there a user-class that contains all user info
 */
public class PaymentActivity extends Activity {

    private AutoCompleteTextView mRecipient;
    private RecipientAdapter mAdapter;
    private EditText mAmount;
    private EditText mNote;
    private Button mCharge;
    private ListView mList;

    private static final String APP_NAME = "Passel";
    private static final String APP_SECRET = "vGgLeNF7ERp7ZGphvY38ZKdT4jrxWes4";
    private static final String APP_ID = "1873";
    private static final int VENMO_REQUEST_CODE = 1;

    private static final String PAY = "pay";
    private static final String CHARGE = "charge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mRecipient = (AutoCompleteTextView) findViewById(R.id.recipient);
        mAmount = (EditText) findViewById(R.id.amount);
        mNote = (EditText) findViewById(R.id.note);
        mCharge = (Button) findViewById(R.id.charge);

        mAdapter = new RecipientAdapter(this);
        mRecipient.setAdapter(mAdapter);

        //TODO: Populate list with EXTRA_* info sent from intent
        mList = (ListView) findViewById(R.id.chargees);
        final String[] CANNED_DATA = new String[] {"Ben Bitdiddle", "38 minutes late"}; //TODO: Remove
        final String CHARGE_RATE = "0.03";

        ChargeeListAdapter adapter = new ChargeeListAdapter(this, CANNED_DATA, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTransactionWithInput(CHARGE);
            }
        });
        mList.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.menu_payment, menu);
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

    private void doTransactionWithInput(String txn) {
        String recipient = mRecipient.getText().toString();
        String amount = mAmount.getText().toString();
        String note = mNote.getText().toString();
        doTransaction(recipient, amount, note, txn);
    }

    private void doTransaction(String recipient, String amount, String note, String txn) {
        try {
            Intent venmoIntent = VenmoLibrary.openVenmoPayment(APP_ID, APP_NAME, recipient, amount, note, txn);
            startActivityForResult(venmoIntent, VENMO_REQUEST_CODE); //1 is the requestCode we are using for Venmo. Feel free to change this to another number.
        }
        catch (android.content.ActivityNotFoundException e) //Venmo native app not install on device, so let's instead open a mobile web version of Venmo in a WebView
        {
            Intent venmoIntent = new Intent(PaymentActivity.this, VenmoWebViewActivity.class);
            String venmo_uri = VenmoLibrary.openVenmoPaymentInWebView(APP_ID, APP_NAME, recipient, amount, note, txn);
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
