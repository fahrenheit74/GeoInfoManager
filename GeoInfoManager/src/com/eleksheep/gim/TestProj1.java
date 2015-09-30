
package com.eleksheep.gim;

import java.text.DecimalFormat;
import java.util.Iterator;

import com.eleksheep.gim.R;
import com.google.android.maps.GeoPoint;
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D;
import com.jhlabs.map.ZoneBndy;
import com.jhlabs.map.proj.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
//import android.widget.AdapterView.OnItemSelectedListener;

public class TestProj1 extends Activity implements LocationListener  {
	
	public static final String PREF_XML = "PREFXML";
	public static final String KEY_COORD_LIST = "KEY_COORDLIST";
	public static final String KEY_COORD_SYSTEM = "KEY_SYSTEM";
	public static final String KEY_UNITS = "KEY_UNITS";
	public static final String KEY_SYMBOL = "KEY_SYMBOL";
	public static final String KEY_PROJ_STRING = "KEY_PROJSTRING";
	public static final String KEY_GPX_DEFSTRING = "KEY_GPXDEFSTRING";
	
	private static final String KEYLAT = "KEY_LAT";
	private static final String KEYLNG = "KEY_LNG";
	private static final String KEYXCOORD = "KEY_XCOORD";
	private static final String KEYYCOORD = "KEY_YCOORD";
	private static final String KEYACC = "KEY_ACC";
	private static final String KEYSTA = "KEY_STATUS";
	private static final String KEYLPR = "KEY_LOCPROVIDER";
	public static final String KEY_DBSORTDESCRIPT = "KEY_DBSORTDESCRIPT";
	
	public static final int COORD_LIST_DEFAULT = 0;
	public static final int COORD_SYSTEM_DEFAULT = 0;
	
	private double m_lat;
	private double m_lng;
	private double m_xcoord =0.0;
	private double m_ycoord = 0.0;
	private float m_GPSacc;
	private String m_GPSStatus;
	private String m_locprovider;
	
    /** Called when the activity is first created. */
	private Spinner s_coordsystem;
	private Spinner s_coordlist;
	private EditText LatEdit;
	private EditText LngEdit;
	private EditText XEdit;
	private EditText YEdit;
	private RadioButton radio_feet;
    private RadioButton radio_meters;
    Projection projection;
    private String best;
    private LocationManager locmgr;
    private Location loc = null;
    private EditText GPSacc;
	private EditText GPSstatus;
	private EditText LocProvider;
	private final String defprojstr = "+proj=utm, +zone=10, +ellps=GRS80,+ellps=GRS80,+towgs84=0,0,0,0,0,0,0,+units=m,+no_defs";
    
    @SuppressWarnings("unchecked")
	private ArrayAdapter coordlist_adapter;
    
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences mPrefs;
        mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
        
        s_coordlist = (Spinner) findViewById(R.id.coordspinner);
        s_coordsystem = (Spinner) findViewById(R.id.coordsysspinner);
        
        final ArrayAdapter coordsystem_adapter = ArrayAdapter.createFromResource(this, R.array.coordsystems, android.R.layout.simple_spinner_item);
        coordsystem_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_coordsystem.setAdapter(coordsystem_adapter);
        
		//String defprojstr = "+proj=utm, +zone=10, +ellps=GRS80,+ellps=GRS80,+towgs84=0,0,0,0,0,0,0,+units=m,+no_defs";
        
        if(!mPrefs.contains(KEY_COORD_SYSTEM)){
           	Editor mPrefsEditor = mPrefs.edit();
        	mPrefsEditor.putInt(TestProj1.KEY_COORD_SYSTEM,0);
        	mPrefsEditor.putInt(TestProj1.KEY_COORD_LIST,0);
        	mPrefsEditor.putBoolean(TestProj1.KEY_UNITS, false);
        	mPrefsEditor.putInt(TestProj1.KEY_SYMBOL, R.drawable.mapsym_1);
        	mPrefsEditor.putString(TestProj1.KEY_PROJ_STRING,defprojstr);
        	mPrefsEditor.putString(TestProj1.KEY_GPX_DEFSTRING, "");
        	mPrefsEditor.putString(TestProj1.KEY_DBSORTDESCRIPT, "_ID ASC");
            mPrefsEditor.commit();
        }
        //Set projection from store preference. 
        String szProjString = mPrefs.getString(TestProj1.KEY_PROJ_STRING, defprojstr);
        String delim2 = ",";
        String[] tokens2 = szProjString.split(delim2);
        projection = ProjectionFactory.fromPROJ4Specification(tokens2);
        	
        s_coordsystem.setSelection(mPrefs.getInt(TestProj1.KEY_COORD_SYSTEM, 0));
        locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
     //  final Button CalcBtn = (Button)findViewById(R.id.calculate_btn);
        LatEdit = (EditText) findViewById(R.id.latitude_text);
        LngEdit = (EditText) findViewById(R.id.longitude_text);
        XEdit = (EditText) findViewById(R.id.x_text);
        YEdit = (EditText) findViewById(R.id.y_text);
        radio_feet = (RadioButton) findViewById(R.id.radio_feet);
        radio_meters = (RadioButton) findViewById(R.id.radio_meters);
        GPSacc= (EditText) findViewById(R.id.gpsacc_text);
        GPSstatus = (EditText) findViewById(R.id.gpsstatus_text);
        LocProvider=(EditText) findViewById(R.id.locprovider_text);
        
        boolean bismeters = mPrefs.getBoolean(KEY_UNITS, false);
        
        if(bismeters){
        	radio_meters.setChecked(true);
        }
        else{
        	radio_feet.setChecked(true);
        }
        
        LatEdit.setText("0.0");
        LngEdit.setText("0.0");
        
        setCoordListSpinner();
        
        s_coordlist.setOnItemSelectedListener(
        	new  AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
	
		          	String coordname = s_coordlist.getSelectedItem().toString();
					Toast.makeText(TestProj1.this, coordname, Toast.LENGTH_SHORT).show();
					
					setCoordSys();
					if(getLat()!= 0.00 && getLng()!= 0.00)
	            		calcposition();
					
					SharedPreferences mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
			        Editor mPrefsEditor = mPrefs.edit();
			        mPrefsEditor.putInt(TestProj1.KEY_COORD_LIST,s_coordlist.getSelectedItemPosition());
			        mPrefsEditor.commit();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
				}           
        	});
        
        s_coordsystem.setOnItemSelectedListener(
            	new  AdapterView.OnItemSelectedListener() {

    				@Override
    				public void onItemSelected(AdapterView<?> arg0, View arg1,
    						int position, long arg3) {
    					String coordname = s_coordsystem.getSelectedItem().toString();
    					Toast.makeText(TestProj1.this, coordname, Toast.LENGTH_SHORT).show();
    					setCoordListSpinner();
    					
    					SharedPreferences mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
   		          	 	Editor mPrefsEditor = mPrefs.edit();
   		          	 	mPrefsEditor.putInt(KEY_COORD_SYSTEM, s_coordsystem.getSelectedItemPosition());
   		          	 	mPrefsEditor.commit();
    				}

    				@Override
    				public void onNothingSelected(AdapterView<?> arg0) {
    					// TODO Auto-generated method stub
    				}           
            	});
      
        OnClickListener radio_listener = new OnClickListener() {
            public void onClick(View v) {
            	if(getLat()!= 0.00 && getLng()!= 0.00)
            		calcposition();
            	
             SharedPreferences mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
          	 Editor mPrefsEditor = mPrefs.edit();
          	  
          	  if(radio_feet.isChecked())
          		  mPrefsEditor.putBoolean(KEY_UNITS, false);
          	  else
          		  mPrefsEditor.putBoolean(KEY_UNITS, true);
          	  mPrefsEditor.commit();
            }
        };
        radio_feet.setOnClickListener(radio_listener);
        radio_meters.setOnClickListener(radio_listener);
    
    final Listener gpslistener=new GpsStatus.Listener()
    {		
		boolean isGpsFixed = false;
		
		public void showGpsSatInfo(){
			GpsStatus xGpsStatus = locmgr.getGpsStatus(null) ; 
			Iterable<GpsSatellite> iSatellites = xGpsStatus.getSatellites(); 
    		Iterator<GpsSatellite> it  = iSatellites.iterator();
    		int satsused = 0;
    		int sats = 0;
    		String str = "";
    		while ( it.hasNext() )
            {
                    GpsSatellite oSat = it.next();
                    //String str = "***Satellites: "+ oSat.getPrn() + "," + oSat.getAzimuth() + "," + oSat.getElevation() ;
                    //String strfix = "";
                    
                    if(oSat.usedInFix()){
                    	//str =+ oSat.getPrn() + ",";
                    	satsused++;
                    }
                    sats++;
            }
    		if(satsused > 0){
    			GPSstatus.setText(str+sats + "," + satsused);
    			m_GPSStatus = str+sats + "," + satsused;
    		}
    		else{
    			GPSstatus.setText(str+sats);
    			m_GPSStatus = str+sats;
    		}
		}

		@Override
		public void onGpsStatusChanged(int event) {
			 switch( event )
                {
                    case GpsStatus.GPS_EVENT_STARTED:
                            //log("**********GPS STARTED:");
                            break ;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    		//log("**********Satellite Status");
                    		if(isGpsFixed)
                    			showGpsSatInfo();
                    		break;
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                           	//log("**********GPS FIRST FIX"); 
                           	isGpsFixed = true;
                           	showGpsSatInfo();
                            break ;
                    case GpsStatus.GPS_EVENT_STOPPED:
                            //log("**********GPS STOPPED");
                            isGpsFixed = false;
                            break ;
                }
		}
    };
    	locmgr.addGpsStatusListener(gpslistener);
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	best = locmgr.getBestProvider(criteria, true);
    	GeoPoint lastKnownPoint = null;
    	// last KNOWN may be null, if none set after power up, or in emulator and not setup
        Location lastKnownLocation = this.locmgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	
    	 if (lastKnownLocation != null) {
	            lastKnownPoint = LocationHelper.getGeoPoint(lastKnownLocation);   
	        	
	         	setCoordSys();
	        	dumplocation(lastKnownLocation );
	        } 
    	 else {
	            lastKnownPoint = LocationHelper.OLD_FAITHFUL_YS;

	            Toast.makeText(this,"GPS Currently Disabled Please enable GPS.",Toast.LENGTH_LONG).show();
	            Intent itogglegps = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
				startActivity(itogglegps);
				finish();
	        }
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_db, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.menu_about:
		case R.id.menu_about_shortcut:
			Intent iabout = new Intent(this,About.class);
			startActivity(iabout);
			return true;
		case R.id.menu_store_point:
		case R.id.menu_store_point_shortcut:
			Intent istorept = new Intent(this,StorePoint.class);
			Bundle bun = new Bundle();
			bun.putDouble(KEYLAT,m_lat );
			bun.putDouble(KEYLNG, m_lng);
			bun.putDouble(KEYXCOORD, m_xcoord);
			bun.putDouble(KEYYCOORD, m_ycoord);
			bun.putFloat(KEYACC, m_GPSacc);
			bun.putString(KEYSTA, m_GPSStatus);
			bun.putString(KEYLPR, m_locprovider);
			istorept.putExtras(bun);
			startActivity(istorept);
			return true;
		case R.id.menu_view_pointdb:
		case R.id.menu_view_pointdb_shortcut:
			Intent iviewdb = new Intent(this,ViewPointDB.class);
			//Bundle bun2 = new Bundle();
			//bun2.putDouble(KEYLAT, m_lat);
			//bun2.putDouble(KEYLNG, m_lng);
			//iviewdb.putExtras(bun2);
			startActivity(iviewdb);
			return true;
		case R.id.menu_view_map:
		case R.id.menu_view_map_shortcut:
			Intent imapvw = new Intent(this,MapViewActivity.class);
			startActivity(imapvw);
			return true;
		case R.id.menu_import_gpx:
		case R.id.menu_import_gpx_shortcut:
			Intent readgpxfile = new Intent(this,ReadGPXFile.class);
			startActivity(readgpxfile);
			//events = new PointDBData(this); 
			return true;
		case R.id.menu_satinfo:
		case R.id.menu_satinfo_shortcut:
			Intent isatinfo=new Intent(this, SatActivity.class);
        	startActivity(isatinfo);
        	return true;
		case R.id.menu_togglegps:
		case R.id.menu_togglegps_shortcut:
			Intent itogglegps = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
			startActivity(itogglegps);
			return true;
		case R.id.menu_more:
		case R.id.menu_more_shortcut:
			Intent sunpos = new Intent(this,SunPosCalc.class);
			Bundle bun3 = new Bundle();
			bun3.putDouble(KEYLAT,m_lat );
			bun3.putDouble(KEYLNG, m_lng);
			sunpos.putExtras(bun3);
			startActivity(sunpos);
			return true;
		default:
			return false;
		}
	}
	
      private void setCoordListSpinner(){
    	  int coordsyspos = s_coordsystem.getSelectedItemPosition();
    	  switch(coordsyspos)
    	  {
	    	  case 0:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.stateplanenad83, android.R.layout.simple_spinner_item);
	              coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 1:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.utmzones_n, android.R.layout.simple_spinner_item);
	              coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 2:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.utmzones_s, android.R.layout.simple_spinner_item);
	              coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 3:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.europe_epsg,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 4:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.nz_australia_epsg,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 5:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.japan_korea_taiwan,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 6:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.malaysia,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 7:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.india,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 8:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.middle_east,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 9:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.africa,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 10:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.caribbean_islands,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 11:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.south_america,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 12:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.north_america,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
	    	  case 13:
	    		  coordlist_adapter = ArrayAdapter.createFromResource(this, R.array.china,android.R.layout.simple_spinner_item);
	    		  coordlist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	              s_coordlist.setAdapter(coordlist_adapter);
	              break;
    	  }
    	  
    	  SharedPreferences mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
          Editor mPrefsEditor = mPrefs.edit();
        	
        	if(!mPrefs.contains(KEY_COORD_LIST)){
        		mPrefsEditor.putInt(TestProj1.KEY_COORD_LIST,0);
        		mPrefsEditor.commit();
        	}
        	
        	if(mPrefs.getInt(KEY_COORD_SYSTEM, 0)== s_coordsystem.getSelectedItemPosition()){
        		s_coordlist.setSelection(mPrefs.getInt(TestProj1.KEY_COORD_LIST, 0));
        	}
        	else{
        		//TODO add code to a suggested items searching through boundaries.
        		int zone = 0;
        		boolean bsuggestionmade = false;
        		if(coordsyspos==0){
        			zone = ZoneBndy.findsuggestedzone(getLat(),getLng());
        			bsuggestionmade = true;
        		}
        		if(coordsyspos==3){
        			zone = ZoneBndy.findsuggestedzoneEU(getLat(), getLng());
        			bsuggestionmade = true;
        		}
        		if(coordsyspos==11){
        			zone = ZoneBndy.findsuggestedzoneSA(getLat(), getLng());
        			bsuggestionmade = true;
        		}
        		if(coordsyspos==12){
        			zone = ZoneBndy.findsuggestedzoneNA(getLat(), getLng());
        			bsuggestionmade = true;
        		}
        		if(coordsyspos==13){
        			zone = ZoneBndy.findsuggestedzoneCH(getLat(), getLng());
        			bsuggestionmade = true;
        		}
        		s_coordlist.setSelection(zone);
        		if(bsuggestionmade){
        			String coordname = "Suggested Coordinate Zone";
        			Toast.makeText(TestProj1.this, coordname, Toast.LENGTH_SHORT).show();
        		}
        	}		
      }
     
      private void setCoordSys(){
    	  String coordname = s_coordlist.getSelectedItem().toString();
	   	  
	   	  String delim = ":";
	   	  String[] tokens = coordname.split(delim);
	   	  
	   	String StrInit;
	   	String szizone;
	   	int izone = 10;
	   	
	   	double lng = getLng();
	   	if(s_coordlist.getSelectedItemPosition()== 0){
			if(lng != 0.0)
				izone = MapMath.getUtmZone(lng);
			else
				izone = 10;
			}
		else
			izone = s_coordlist.getSelectedItemPosition();
	   		   	  
	   	int coordsyspos = s_coordsystem.getSelectedItemPosition();
	   	SharedPreferences mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
	   	Editor mPrefsEditor = mPrefs.edit();
	   	switch(coordsyspos){
	   	case 0://NAD83 SP
	   		StrInit = "+init=" + tokens[1];
	   		projection = ProjectionFactory.fromPROJ4Specification( new String[] {  StrInit } );
	   		mPrefsEditor.putString(TestProj1.KEY_PROJ_STRING, StrInit);
	   		mPrefsEditor.commit();
	   		break;
	   	case 3://Europe EPSG
	    case 4://EPSG New Zealand Australia
	   	case 5://Japan Korea Hong Kong
	   	case 6://Malaysia and Indonesia
	   	case 7://India Pakastan and Banglodesh
	   	case 8://Middle East
	   	case 9://Africa
	   	case 10://Caribbean Islands and Central America
	   	case 11://South America
	   	case 12://North America
	   	case 13://China
	   		switch(coordsyspos){
	   		case 3: //Europe
	   			StrInit = "+init=" + "eu" + tokens[1];
	   			break;
	   		case 4: //EPSG New Zealand Australia
	   			StrInit = "+init=" + "au" + tokens[1];
	   			break;
	   		case 11: //South America
	   			StrInit = "+init=" + "sa" + tokens[1];
	   			break;
	   		case 12: //North America
	   			StrInit = "+init=" + "na" + tokens[1];
	   			break;
	   		case 13: //China
	   			StrInit = "+init=" + "ch" + tokens[1];
	   			break;
	   		default:
	   			StrInit = "+init=" + "99" + tokens[1];
	   		}
	   		projection = ProjectionFactory.fromPROJ4Specification( new String[] {  StrInit } );
	   		mPrefsEditor.putString(TestProj1.KEY_PROJ_STRING, StrInit);
	   		mPrefsEditor.commit();
	   		break;
	   	case 1://UTM 83 N
  				szizone = "+zone=" + izone;
  				StrInit = "+proj=utm," + szizone + ",+ellps=GRS80,+ellps=GRS80,+towgs84=0,0,0,0,0,0,0,+units=m,+no_defs";
  				String delim2 = ",";
  				String[] tokens2 = StrInit.split(delim2);
  				projection = ProjectionFactory.fromPROJ4Specification(tokens2);
  				mPrefsEditor.putString(TestProj1.KEY_PROJ_STRING, StrInit);
  				mPrefsEditor.commit();
	   		break;
	  	case 2://UTM 83 S
				szizone = "+zone=" + izone;
		   		projection = ProjectionFactory.fromPROJ4Specification(
		   	        new String[] {
		   	            "+proj=utm",
		   	            "+south",
		   	            szizone,
		   	            "+ellps=GRS80",
		   	            "+towgs84=0,0,0,0,0,0,0",
		   	            "+units=m",
		   	            "+no_defs"
		   	        }
		   	    );
	   		break;
	   	}
	//  	String proj4desc = projection.getPROJ4Description();
      }
      
      private double getLat(){
    	  double lat = 0.0;
    	  Location location = getLocation();
    	  if (location == null){
    		  String str = LatEdit.getText().toString();
    		  lat = Double.parseDouble(str);
    	  }
    	  else
    		  lat = location.getLatitude();
	   	  return lat;
      }
      private double getLng(){
    	  double lng = 0.0;
    	  Location location = getLocation();
		  if (location == null){
	  		  String str = LngEdit.getText().toString();
	  		  lng = Double.parseDouble(str);
	  	  }
	  	  else
	  		  lng = location.getLongitude();
	   	  return lng;
      }
      
	  private void calcposition(){
		 double lat = 0.0;
		 double lng = 0.0;

	   	lat = getLat();
	   	lng = getLng();
	   	  
	   	Point2D.Double src = new Point2D.Double();
        Point2D.Double dst = new Point2D.Double();
        src.x = lng;
      	src.y = lat;
      	
      	String szprojname = projection.getName().toString();
      	if(szprojname.length()>0){
      			
      		projection.transform(src,dst);
      	
	      	DecimalFormat fourPlaces = new DecimalFormat("0.0000");
	      	MapMath.MeterstoFeet(radio_feet.isChecked(), dst);
	      	
	      	m_xcoord = dst.x;
	      	m_ycoord = dst.y;
	   		XEdit.setText(fourPlaces.format(dst.x));
	   		YEdit.setText(fourPlaces.format(dst.y));
      	}
	  }
	  
	  private void dumplocation(Location location){
	    	if (location == null)
	            return;
	         else{
	         	String locstr;
	         	DecimalFormat twoPlaces = new DecimalFormat("0.00");
	         	locstr = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
	      		LatEdit.setText(locstr);
	      		locstr = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
	      		LngEdit.setText(locstr);
	      		
	      		//StorePoint spt = new StorePoint();
	      		m_lat = location.getLatitude();
	      		m_lng = location.getLongitude();

	      		GPSacc.setText(twoPlaces.format(location.getAccuracy())+"m");
	      		m_GPSacc = location.getAccuracy();
	     		String locprovider = location.getProvider();
	     		if(locprovider != null){
	     			LocProvider.setText(locprovider);
	     			m_locprovider = locprovider;
	     		}
	      		
	      		setLocation(location);
	        	 
	     		if(getLat()!= 0.00 && getLng()!= 0.00)
            		calcposition();
	         }
	    }
	  
	  private void setLocation(Location location){
		  loc = location;
	  }
		
	private Location getLocation(){
		return loc;
	}
	@Override
	    protected void onResume(){
	    	super.onResume();
	    	locmgr.requestLocationUpdates(best,500,1,this);
	    }

	@Override
	public void onLocationChanged(Location location) {
		setLocation(location);
		dumplocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}