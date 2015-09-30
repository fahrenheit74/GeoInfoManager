package com.eleksheep.gim;

//import static com.eleksheep.geocoord.DBaseConstants.NUMBER;
//import static com.eleksheep.geocoord2.DBaseConstants.DESCR;
import static com.eleksheep.gim.DBaseConstants.ADDCITY;
import static com.eleksheep.gim.DBaseConstants.ADDCOUNTRY;
import static com.eleksheep.gim.DBaseConstants.ADDPOSTALCODE;
import static com.eleksheep.gim.DBaseConstants.ADDSTATE;
import static com.eleksheep.gim.DBaseConstants.ADDSTREET;
import static com.eleksheep.gim.DBaseConstants.DESCR;
import static com.eleksheep.gim.DBaseConstants.IMAGENAME;
import static com.eleksheep.gim.DBaseConstants.LAT;
import static com.eleksheep.gim.DBaseConstants.LATDMS;
import static com.eleksheep.gim.DBaseConstants.LNG;
import static com.eleksheep.gim.DBaseConstants.LNGDMS;
import static com.eleksheep.gim.DBaseConstants.PTNAME;
import static com.eleksheep.gim.DBaseConstants.SYMBOL;
import static com.eleksheep.gim.DBaseConstants.TABLE_NAME;
import static com.eleksheep.gim.DBaseConstants.TIME;
import static com.eleksheep.gim.DBaseConstants.TIMESTR;
import static com.eleksheep.gim.DBaseConstants.XCOORD;
import static com.eleksheep.gim.DBaseConstants.YCOORD;

import java.io.File;
import java.text.DecimalFormat;
import com.eleksheep.gim.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class StorePoint extends Activity {
	
	private static final int EDITADD_REQUEST_CODE=1336; 
	private static final int EDITNOTE_REQUEST_CODE=1337; 
	private static final String KEYLAT = "KEY_LAT";
	private static final String KEYLNG = "KEY_LNG";
	private static final String KEYXCOORD = "KEY_XCOORD";
	private static final String KEYYCOORD = "KEY_YCOORD";
	private static final String KEYACC = "KEY_ACC";
	private static final String KEYSTA = "KEY_STATUS";
	private static final String KEYLPR = "KEY_LOCPROVIDER";
	@SuppressWarnings("unused")
	private static final String KEY_UNITS = "KEY_UNITS";
	private static final String KEY_SYMBOLS = "KEY_SYMBOLS";
//	private static final String KEYRETURNACTIVITYCODE = "KEY_RETURN_ACTIVITY_CODE";
	private static final String KEYRETURNCOUNTRYADD = "KEY_RETURN_COUNTRY_ADD";
	private static final String KEYRETURNSTATEADD = "KEY_RETURN_STATE_ADD";
	private static final String KEYRETURNCITYADD = "KEY_RETURN_CITY_ADD";
	private static final String KEYRETURNSTREETADD = "KEY_RETURN_STREET_ADD";
	private static final String KEYRETURNPOSTCODEADD = "KEY_RETURN_POSTCODE_ADD";
	private static final String KEYRETURNDESCRIPTION = "KEY_RETURN_DESCRIPTION";
	
	private static final String KEYDESCRIPTION = "KEY_DESCRIPTION";
	
	private TextView LatEdit;
	private TextView LngEdit;
	private EditText PointNameEdit;
	private TextView GPSaccText;
	private TextView GPSStatusText;
	private TextView GPSLocProviderText;
	private PointDBData events;
	private double dlat = 0.0;
	private double dlng = 0.0;
	private double xcoord = 0.0;
	private double ycoord = 0.0;
	
	private String addStreet = "";
	private String addCity = "";
	private String addState = "";
	private String addCountry = "";
	private String addPostalCode = "";
	
	private String description = "";
	String imageFilePath = "";
	String timename = "";
	String imageFileName = "";
	
	@Override
	public void onResume(){
		super.onResume();
		final SharedPreferences mPrefs;
        mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
        final ImageButton symBtn = (ImageButton) findViewById(R.id.spt_sym_btn);
        symBtn.setImageResource(mPrefs.getInt(KEY_SYMBOLS, R.drawable.mapsym_1));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_point);
        events = new PointDBData(this); 
        
        final SharedPreferences mPrefs;
        mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
        
        final ImageButton StorePointDB_btn = (ImageButton)findViewById(R.id.storeptdb_btn);
        final ImageButton symBtn = (ImageButton) findViewById(R.id.spt_sym_btn);
        final ImageButton GetGeoCodeBtn = (ImageButton)findViewById(R.id.getgeocode_btn);
        final ImageButton GetNoteBtn = (ImageButton)findViewById(R.id.getnote_btn);
        final ImageButton GetCameraBtn = (ImageButton)findViewById(R.id.getcamera_btn);
        
        symBtn.setImageResource(mPrefs.getInt(KEY_SYMBOLS, R.drawable.mapsym_1));

        final Bundle bun = getIntent().getExtras();
        LatEdit = (TextView) findViewById(R.id.spt_latitude_text);
        LngEdit = (TextView) findViewById(R.id.spt_longitude_text);
        GPSaccText = (TextView) findViewById(R.id.spt_gpsacc_text);
        GPSStatusText = (TextView) findViewById(R.id.spt_gpsstatus_text);
        GPSLocProviderText = (TextView) findViewById(R.id.spt_locprovider_text);
        PointNameEdit = (EditText) findViewById(R.id.spt_pointname_text);
        String locstr;
        locstr = Location.convert(bun.getDouble(KEYLAT), Location.FORMAT_SECONDS);
        dlat = bun.getDouble(KEYLAT);
        LatEdit.setText(locstr);
        locstr = Location.convert(bun.getDouble(KEYLNG), Location.FORMAT_SECONDS);
        dlng = bun.getDouble(KEYLNG);
        LngEdit.setText(locstr);
        xcoord = bun.getDouble(KEYXCOORD);
        ycoord = bun.getDouble(KEYYCOORD);
        DecimalFormat twoPlaces = new DecimalFormat("0.00");
        GPSaccText.setText(twoPlaces.format(bun.getFloat(KEYACC))+"m");
        GPSStatusText.setText(bun.getString(KEYSTA));
        GPSLocProviderText.setText(bun.getString(KEYLPR));
        
        symBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent symgridview = new Intent(StorePoint.this,SymbolGridView.class);
        		StorePoint.this.startActivity(symgridview);
        	}
        });
          
        StorePointDB_btn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
 
        	  SQLiteDatabase db = events.getWritableDatabase();
      	      ContentValues values = new ContentValues();
      	      
      	      values.put(TIME, System.currentTimeMillis());
      	      values.put(TIMESTR, GetUTCTimeString());
      	      values.put(LAT, dlat);
      	      values.put(LNG, dlng);
      	      values.put(XCOORD, xcoord);
      	      values.put(YCOORD, ycoord);
      	      values.put(LATDMS, Location.convert(dlat, Location.FORMAT_SECONDS));
      	      values.put(LNGDMS, Location.convert(dlng, Location.FORMAT_SECONDS));
      	      values.put(PTNAME,PointNameEdit.getText().toString());
      	      
      	      //Get Addresses 	
      	      values.put(ADDSTREET, addStreet);
      	      values.put(ADDCITY, addCity);
      	      values.put(ADDSTATE, addState);
      	      values.put(ADDCOUNTRY, addCountry);
      	      values.put(ADDPOSTALCODE, addPostalCode);
      	      values.put(DESCR, description);
      	      imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/gim/photos" + timename;
      	      
      	      //Check image exists. 
      	      if(imageFilePath.length()> 1){
      	     	java.io.File file = new java.io.File(imageFilePath);
      	     	if (!file.exists()) {
      	    		imageFilePath = "";
      	    		imageFileName = "";
      	    	}
      	      }
      	      else{
      	    		imageFilePath = "";
      	    		imageFileName = "";
      	      }
      	      values.put(IMAGENAME, imageFileName);
      	      values.put(SYMBOL, mPrefs.getInt(KEY_SYMBOLS, R.drawable.mapsym_1));
      	      
      	      long rowid = db.insertOrThrow(TABLE_NAME, null, values);
      	      if(rowid > -1)
      	    	Toast.makeText(StorePoint.this, "Point Stored", Toast.LENGTH_SHORT).show();
      	      finish();
        	}
        });

        GetGeoCodeBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent addresseditor = new Intent(StorePoint.this,AddressEditor.class);
        		Bundle bun = new Bundle();
    			bun.putDouble(KEYLAT,dlat );
    			bun.putDouble(KEYLNG, dlng);
    			bun.putString(KEYRETURNSTREETADD, addStreet);
    			bun.putString(KEYRETURNCITYADD, addCity);
    			bun.putString(KEYRETURNSTATEADD, addState);
    			bun.putString(KEYRETURNPOSTCODEADD, addPostalCode);
    			bun.putString(KEYRETURNCOUNTRYADD, addCountry);
    			addresseditor.putExtras(bun);
    			startActivityForResult(addresseditor, EDITADD_REQUEST_CODE);
        	}
        });
        GetNoteBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        
        		Intent noteeditor = new Intent(StorePoint.this,NoteEditor.class);
        		Bundle bun = new Bundle();
        		bun.putString(KEYDESCRIPTION, description);
        		noteeditor.putExtras(bun);
        		startActivityForResult(noteeditor, EDITNOTE_REQUEST_CODE);
        	}
        });
        GetCameraBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Time time = new Time();
        	    time.setToNow();
        	    String date = Integer.toString(time.year) + Integer.toString(time.month+1) + Integer.toString(time.monthDay);
        	    String hms = Integer.toString(time.hour)+ Integer.toString(time.minute)+ Integer.toString(time.second) + "Z";
        	    timename = "/geoEx_" + date + "T" + hms + ".jpg";
        		imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/gim/photos";
        		File imageFileFolder = new File(imageFilePath);
        		imageFileFolder.mkdirs();
        		imageFileName = "/gim/photos" + timename;
    			imageFilePath = imageFilePath + timename;
        		File imageFile = new File(imageFilePath);
                Uri imageFileUri = Uri.fromFile(imageFile);
        		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        		cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
        		startActivity(cameraIntent);
        	}
        });
    }
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
      
        Bundle addbun = intent.getExtras();
        if(addbun != null){
        
	        if(requestCode == EDITADD_REQUEST_CODE){
	        	if(resultCode == RESULT_OK){
	        		addState = addbun.getString(KEYRETURNSTATEADD);
	        		addCity = addbun.getString(KEYRETURNCITYADD);
	        		addCountry = addbun.getString(KEYRETURNCOUNTRYADD);
	        		addPostalCode = addbun.getString(KEYRETURNPOSTCODEADD);
	        		addStreet = addbun.getString(KEYRETURNSTREETADD);
	        	}
	        }
	        if(requestCode == EDITNOTE_REQUEST_CODE){
	        	if(resultCode == RESULT_OK)
	        		description = addbun.getString(KEYRETURNDESCRIPTION);
	        }
		}
    }
	
	protected String GetUTCTimeString(){
		String utcTimeStr = "";
		
		Time time = new Time();
	    time.setToNow();
	    long timems = System.currentTimeMillis();
	    String tz = Time.getCurrentTimezone();
	    time.timezone = Time.TIMEZONE_UTC;
	    time.set(timems);
	    utcTimeStr = time.format3339(false);
	      
	    time.timezone = tz;
	    time.set(timems);
		return utcTimeStr;
	}
	
}
