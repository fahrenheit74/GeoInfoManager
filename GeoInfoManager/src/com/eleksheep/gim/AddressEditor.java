package com.eleksheep.gim;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.eleksheep.gim.R;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;

public class AddressEditor extends Activity {
	private static final String KEYLAT = "KEY_LAT";
	private static final String KEYLNG = "KEY_LNG";
	private static final String KEYRETURNACTIVITYCODE = "KEY_RETURN_ACTIVITY_CODE";
	private static final String KEYRETURNCOUNTRYADD = "KEY_RETURN_COUNTRY_ADD";
	private static final String KEYRETURNSTATEADD = "KEY_RETURN_STATE_ADD";
	private static final String KEYRETURNCITYADD = "KEY_RETURN_CITY_ADD";
	private static final String KEYRETURNSTREETADD = "KEY_RETURN_STREET_ADD";
	private static final String KEYRETURNPOSTCODEADD = "KEY_RETURN_POSTCODE_ADD";
	private double dlat = 0.0;
	private double dlng = 0.0;
	private EditText addStreetText;
	private EditText addCityText;
	private EditText addStateText;
	private EditText addCountryText;
	private EditText addPostCodeText;
	
	
	private String StreetAdd = "3006 Moyer";
	private String CityAdd = "Maysville";
	private String CountryAdd = "United States";
	private String PostalAdd = "41056";
	private String StateAdd = "KY";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_edit);
		
		final Bundle bun = getIntent().getExtras();
		dlat = bun.getDouble(KEYLAT);
		dlng = bun.getDouble(KEYLNG);
		
		StreetAdd = bun.getString(KEYRETURNSTREETADD);
		CityAdd = bun.getString(KEYRETURNCITYADD);
		StateAdd = bun.getString(KEYRETURNSTATEADD);
		PostalAdd = bun.getString(KEYRETURNPOSTCODEADD);
		CountryAdd = bun.getString(KEYRETURNCOUNTRYADD);
		
		final ImageButton addSearchBtn = (ImageButton)findViewById(R.id.aded_addsearch_btn);
		final Button returnbtn = (Button)findViewById(R.id.aded_returnbtn);
		addStreetText = (EditText)findViewById(R.id.aded_street_text);
		addCityText = (EditText)findViewById(R.id.aded_city_text);
		addStateText = (EditText)findViewById(R.id.aded_state_text);
		addCountryText = (EditText)findViewById(R.id.aded_country_text);
		addPostCodeText = (EditText)findViewById(R.id.aded_postcode_text);
		
		addStreetText.setText(StreetAdd);
		addCityText.setText(CityAdd);
		addStateText.setText(StateAdd);
		addCountryText.setText(CountryAdd);
		addPostCodeText.setText(PostalAdd);
		
		final Geocoder geocode = new Geocoder(this, Locale.getDefault());
		
		addSearchBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		List<Address> addresses = null;
        		try {
					addresses = geocode.getFromLocation(dlat, dlng, 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(addresses.size() > 0){
        			Address address = addresses.get(0);
        			addPostCodeText.setText(address.getPostalCode()); 
        			addStreetText.setText(address.getThoroughfare());   
        			addCityText.setText(address.getSubAdminArea());     
        			addStateText.setText(address.getAdminArea());	
        			addCountryText.setText(address.getCountryName());
				}
				else
					Toast.makeText(AddressEditor.this, "No Address Found", Toast.LENGTH_LONG).show();
        	}
        });
		
		returnbtn.setOnClickListener(new View.OnClickListener(){
        public void onClick(View view){
            //create a new intent...
            Intent intent = new Intent();
            //add "returnKey" as a key and assign it the value
            //in the textbox...
            Bundle bun = new Bundle();
            bun.putInt(KEYRETURNACTIVITYCODE, 0);
            bun.putString(KEYRETURNSTREETADD,addStreetText.getText().toString());
            bun.putString(KEYRETURNCITYADD,addCityText.getText().toString());
            bun.putString(KEYRETURNPOSTCODEADD,addPostCodeText.getText().toString());
            bun.putString(KEYRETURNCOUNTRYADD,addCountryText.getText().toString());
            bun.putString(KEYRETURNSTATEADD,addStateText.getText().toString());
			intent.putExtras(bun);

            //get ready to send the result back to the caller (MainActivity)
            //and put our intent into it (RESULT_OK will tell the caller that
            //we have successfully accomplished our task..
            setResult(RESULT_OK,intent);
            //close this Activity...
            finish();
        }
    });
	}
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
   	 if (keyCode == KeyEvent.KEYCODE_BACK) {
   		 Intent intent = new Intent();
   		 Bundle bun = new Bundle();
   		 bun.putInt(KEYRETURNACTIVITYCODE, 0);
   		 bun.putString(KEYRETURNSTREETADD,"");
   		 bun.putString(KEYRETURNCITYADD,"");
   		 bun.putString(KEYRETURNPOSTCODEADD,"");
   		 bun.putString(KEYRETURNCOUNTRYADD,"");
   		 bun.putString(KEYRETURNSTATEADD,"");
   		 intent.putExtras(bun);
   		 setResult(RESULT_OK,intent);
   	  	 finish();
   	 } 
       	 return super.onKeyDown(keyCode, event);
    }
	
	public String getStateAdd(){
		return StateAdd;
	}
	public String getCountryAdd(){
		return CountryAdd;
	}
	public String getCityAdd(){
		return CityAdd;
	}
	public String getPostalCodeAdd(){
		return PostalAdd;
	}
	public String getStreetAdd(){
		return StreetAdd;
	}
}
