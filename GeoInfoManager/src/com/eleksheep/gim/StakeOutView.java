package com.eleksheep.gim;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eleksheep.gim.R;
import com.eleksheep.gim.TapeMeasure.RefreshRunner;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class StakeOutView extends MapActivity {
	private static final int MENU_SET_SATELLITE = 1;
	private static final int MENU_ENABLE_COMPASS = 2;
	private static final int MENU_STORE_POINT = 3;
    private static final int MENU_BACK_TO_LAST_LOCATION = 4;
    private static final int MENU_SATINFO = 5;
    
	private static final String KEY_UNITS = "KEY_UNITS";
	private static final String KEYLAT = "KEY_LAT";
	private static final String KEYLNG = "KEY_LNG";
	private static final String KEYSYMBOL = "KEY_SYMBOL";
	private static final String KEYXCOORD = "KEY_XCOORD";
	private static final String KEYYCOORD = "KEY_YCOORD";
	private static final String KEYACC = "KEY_ACC";
	private static final String KEYSTA = "KEY_STATUS";
	private static final String KEYLPR = "KEY_LOCPROVIDER";
	
	private Drawable pointMarker3;
	private double dlat = 0.0;
	private double dlng = 0.0;
	private int symbol = 0;
	private boolean isMeters;
	
	private float m_GPSacc;
	private String m_GPSStatus;
	private String m_locprovider;
	
	private LocationManager locationManager;
	private MapView mapView;
	private MyLocationOverlay myLocationOverlay;
	private MapController mapController;
	private LocationProvider locationProvider;
	private String best;
	private LocationManager locmgr;
	private Location location = null;
	
	private StakeOverlay stkol;

	@Override
	public void onResume(){
		super.onResume();
		initMapOverlay();
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.stakeoutview);
        
        final Bundle bun = getIntent().getExtras();
        dlng = bun.getDouble(KEYLNG);
		dlat = bun.getDouble(KEYLAT);
		symbol = bun.getInt(KEYSYMBOL);
		
		this.pointMarker3 = getResources().getDrawable(R.drawable.checkflag);
        this.pointMarker3.setBounds(0, 0, this.pointMarker3.getIntrinsicWidth(), this.pointMarker3.getIntrinsicHeight());
        
        SharedPreferences mPrefs;
        mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
        isMeters = mPrefs.getBoolean(KEY_UNITS, false);
        mapView = (MapView) findViewById(R.id.stakeout_view);
        mapView.setBuiltInZoomControls(true);
  
        locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
     // create a Thread that will periodically send messages to our Handler 
        new Thread(new RefreshRunner()).start();
        
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
	    			m_GPSStatus = str+sats + "," + satsused;
	    		}
	    		else{
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

	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	} 
	@Override
	public void onStart() {
        super.onStart();
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapController = mapView.getController();
        mapController.setZoom(16);
        this.locationProvider = this.locationManager.getProvider(LocationManager.GPS_PROVIDER);
        // animate to, and get buoy data for lastKnownPoint on startup (or fake/prime point if no last known)
        GeoPoint lastKnownPoint = getLastKnownPoint();
        mapController.animateTo(lastKnownPoint);
        Toast.makeText(this,"On Start Completed",Toast.LENGTH_LONG).show();
    }
	
	private void initMapOverlay() {
		 List<Overlay> ol = mapView.getOverlays();
	     myLocationOverlay = new MyLocationOverlay(this, mapView);
	     ol.add(myLocationOverlay);
	     stkol = new StakeOverlay();
		 ol.add(stkol);
		 stkol.setDestination(dlat, dlng);
		 stkol.setUnits(isMeters);
		 stkol.setStopBMP(pointMarker3);
	     myLocationOverlay.enableMyLocation();
	     myLocationOverlay.runOnFirstFix(new Runnable() {
	       public void run() {
                mapController.animateTo(myLocationOverlay.getMyLocation());
          }
        });
	} 
	
	@Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        int menuitemSatOnOfficon = android.R.drawable.button_onoff_indicator_off;
        if(mapView.isSatellite())
        	menuitemSatOnOfficon = android.R.drawable.button_onoff_indicator_on;
        else
        	menuitemSatOnOfficon = android.R.drawable.button_onoff_indicator_off;
        int menuitemCompassOnOfficon = android.R.drawable.button_onoff_indicator_off;
        if(myLocationOverlay.isCompassEnabled())
        	menuitemCompassOnOfficon = android.R.drawable.button_onoff_indicator_on;
        else
        	menuitemCompassOnOfficon = android.R.drawable.button_onoff_indicator_off;
        
        menu.add(1, StakeOutView.MENU_SET_SATELLITE, 0, "Satellite").setIcon(menuitemSatOnOfficon); 
        menu.add(2, StakeOutView.MENU_ENABLE_COMPASS, 0, "Compass").setIcon(menuitemCompassOnOfficon); 
        menu.add(3, StakeOutView.MENU_STORE_POINT, 0, "Store Point").setIcon(android.R.drawable.ic_menu_myplaces); 
        menu.add(4, StakeOutView.MENU_BACK_TO_LAST_LOCATION, 0, "My Location").setIcon(android.R.drawable.ic_menu_mylocation);
        menu.add(5, StakeOutView.MENU_SATINFO, 0, "Satellite Info").setIcon(R.drawable.satellite_48bw);
        return true;
    }
 
 @Override
 public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
    switch (item.getItemId()) {
        case StakeOutView.MENU_SET_SATELLITE:
        	if(mapView.isSatellite()){
        		mapView.setSatellite(false);
        		item.setIcon(android.R.drawable.button_onoff_indicator_off);
        	}
        	else{
        		mapView.setSatellite(true);
        		item.setIcon(android.R.drawable.button_onoff_indicator_on);
        	}
            break;
        case StakeOutView.MENU_ENABLE_COMPASS:
        	if(myLocationOverlay.isCompassEnabled()){
        		myLocationOverlay.disableCompass();
        		item.setIcon(android.R.drawable.button_onoff_indicator_off);
        	}
        	else{
        		myLocationOverlay.enableCompass();
        		item.setIcon(android.R.drawable.button_onoff_indicator_on);
        	}
        	break;
        case StakeOutView.MENU_STORE_POINT:
        	Intent istorept = new Intent(this,StorePoint.class);
			Bundle bun = new Bundle();
			bun.putDouble(KEYLAT,location.getLatitude());
			bun.putDouble(KEYLNG,location.getLongitude());
			bun.putDouble(KEYXCOORD, 0.0);
			bun.putDouble(KEYYCOORD, 0.0);
			bun.putFloat(KEYACC, m_GPSacc);
			bun.putString(KEYSTA, m_GPSStatus);
			bun.putString(KEYLPR, m_locprovider);
			istorept.putExtras(bun);
			startActivity(istorept);
        	break;
        case StakeOutView.MENU_BACK_TO_LAST_LOCATION:
            mapController.animateTo(getLastKnownPoint());
            break;
        case StakeOutView.MENU_SATINFO:
        	Intent isatinfo=new Intent(this, SatActivity.class);
        	startActivity(isatinfo);
        	break;
    }
    return super.onMenuItemSelected(featureId, item);
  }
	
	private GeoPoint getLastKnownPoint() {
        GeoPoint lastKnownPoint = null;

        // last KNOWN may be null, if none set after power up, or in emulator and not setup
        Location lastKnownLocation = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // get lastKnown GeoPoint (either from lastKnownLocation, or prime the pump manually)
        if (lastKnownLocation != null) {
            lastKnownPoint = LocationHelper.getGeoPoint(lastKnownLocation);   
            if(mapView.getZoomLevel()<4)
            	mapController.setZoom(16);
        } 
        else {
            lastKnownPoint = LocationHelper.OLD_FAITHFUL_YS;
            mapController.setZoom(16);

            Toast.makeText(this,
            	"GeoCoord Explorer can not determine your location at start. \nLast know location is unknown - defaulting to Old Faithful Yellowstone NP. Please enable GPS and use menu->My Location",
                Toast.LENGTH_LONG).show();
        }
        return lastKnownPoint;
    }
	
	  class RefreshRunner implements Runnable {
	        // @Override
	        public void run() {
	             while (! Thread.currentThread().isInterrupted()) {
	                  // Send Message to the Handler which will call the invalidate() method of the BoucneView
	           	  Criteria criteria = new Criteria();
	              criteria.setAccuracy(Criteria.ACCURACY_FINE);
	              best = locmgr.getBestProvider(criteria, true);
	              location = locmgr.getLastKnownLocation(best);
	              
	                  try {
	                       Thread.sleep(250); // 4x sec
	                       if(locmgr.getProvider(best)!=null){
		                       	 location = locmgr.getLastKnownLocation(best);
		                       	 if(location !=null){
		                       		 dumplocation(location);
		                       		 stkol.setLocation(location);
			       		        	 Double loclat = location.getLatitude()*1E6;
			       		             Double loclng = location.getLongitude()*1E6;
			       		             Double destlat = dlat*1E6;
			       		             Double destlng = dlng*1E6;
			       		             int centerlat = (int) ((loclat + destlat)/2);
			       		             int centerlng = (int) ((loclng + destlng)/2);
			       		             GeoPoint CenterGeoPoint = new  GeoPoint(centerlat,centerlng);
			       		             int latSpanE6 = Math.abs(loclat.intValue()-destlat.intValue());
			       		             int lonSpanE6 = Math.abs(loclng.intValue()-destlng.intValue());
			       		             //mapController.zoomToSpan(latSpanE6, lonSpanE6);
			       		             //mapController.setCenter(CenterGeoPoint);
		                       	 }
	                       }
	                  } catch (InterruptedException e) {
	                       Thread.currentThread().interrupt();
	                       if(locmgr.getProvider(best)!=null){
	                      	 location = locmgr.getLastKnownLocation(best);
	                      	 if(location !=null)
	                      		 dumplocation(location);
	                       }
	                  }
	             }
	        }
	   }
		private void dumplocation(Location location){
	    	if (location == null)
	            return;
	         else{
	       		m_GPSacc = location.getAccuracy();
	      		String locprovider = location.getProvider();
	      		if(locprovider != null){
	      			m_locprovider = locprovider;
		         }
	         }
	    }
}
