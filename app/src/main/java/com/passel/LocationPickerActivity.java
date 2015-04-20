package com.passel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.passel.R;

public class LocationPickerActivity extends Activity {

	private WebView locationPickerView;
	private EditText searchText;
	private Button searchButton;

	private Double latitude = 42.359711;
	private Double longitude = -71.094627;
	private Integer zoom = 16;
	private String locationName;

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_picker);

		if (savedInstanceState!=null) {
//			latitude = savedInstanceState.getDouble("latitude");
//			longitude = savedInstanceState.getDouble("longitude");
			zoom = savedInstanceState.getInt("zoom");
			locationName = savedInstanceState.getString("locationName");
		}

		// LOCATION PICKER WEBVIEW SETUP
		locationPickerView = (WebView) findViewById(R.id.locationPickerView);
		locationPickerView.setScrollContainer(false);
		locationPickerView.getSettings().setDomStorageEnabled(true);
		locationPickerView.getSettings().setJavaScriptEnabled(true);
		locationPickerView.addJavascriptInterface(new LocationPickerJSInterface(), "AndroidFunction");
		
		locationPickerView.loadUrl("file:///android_asset/locationPickerPage/index.html");

		locationPickerView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				if (progress == 100) {
					locationPickerView.loadUrl("javascript:activityInitialize(" + Double.toString(latitude) + "," + Double.toString(longitude) + "," + zoom + ")");
				}
			}
		});
		// ^^^
		
		// EVENT HANDLER FOR PERFORMING SEARCH IN WEBVIEW
		searchText = (EditText) findViewById(R.id.searchText);
		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View view) {
				locationPickerView.loadUrl("javascript:if (typeof activityPerformSearch == 'function') {activityPerformSearch(\"" + searchText.getText().toString() + "\")}");
			}
		});
		// ^^^

		// EVENT HANDLER FOR ZOOM IN WEBVIEW
		Button zoomIncreaseButton = (Button) findViewById(R.id.zoomIncreaseButton);
		zoomIncreaseButton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View view) {
				locationPickerView.loadUrl("javascript:activityPerformZoom(1)");
			}
		});
		
		Button zoomDecreaseButton = (Button) findViewById(R.id.zoomDecreaseButton);
		zoomDecreaseButton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View view) {
				locationPickerView.loadUrl("javascript:activityPerformZoom(-1)");
			}
		});
		// ^^^
		
		// EVENT HANDLER FOR SAMPLE QUERY BUTTON
		Button sampleQueryButton = (Button) findViewById(R.id.queryButton);
		sampleQueryButton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				AlertDialog alertDialog = new AlertDialog.Builder(LocationPickerActivity.this).create();
				alertDialog.setTitle("Data");
				alertDialog.setMessage("lat=" + latitude + ", lng=" + longitude + ", zoom=" + zoom + "\nloc=" + locationName);
//				alertDialog.show();
                finishWithResult();
			}
		});
	}

	public class LocationPickerJSInterface {
		@JavascriptInterface 
		public void getValues(String latitude, String longitude, String zoom, String locationName){
			LocationPickerActivity.this.latitude = Double.parseDouble(latitude);
			LocationPickerActivity.this.longitude = Double.parseDouble(longitude);
			LocationPickerActivity.this.zoom = Integer.parseInt(zoom);
			LocationPickerActivity.this.locationName = locationName;
		}

		// to ease debugging
		public void showToast(String toast){
			Toast.makeText(LocationPickerActivity.this, toast, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putDouble("latitude", latitude);
		outState.putDouble("longitude", longitude);
		outState.putInt("zoom", zoom);
		outState.putString("locationName", locationName);
	}

    private void finishWithResult()
    {
        Bundle conData = new Bundle();
        conData.putDouble("lat", latitude);
        conData.putDouble("lng", longitude);
        conData.putString("loc_name", locationName);
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);
        finish();
    }

}
