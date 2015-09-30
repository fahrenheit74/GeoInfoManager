package com.eleksheep.gim;

import static android.provider.BaseColumns._ID;
//import static com.eleksheep.geocoord2.DBaseConstants.LATDMS;
//import static com.eleksheep.geocoord2.DBaseConstants.LNGDMS;
import static com.eleksheep.gim.DBaseConstants.ADDCITY;
import static com.eleksheep.gim.DBaseConstants.ADDCOUNTRY;
import static com.eleksheep.gim.DBaseConstants.ADDPOSTALCODE;
import static com.eleksheep.gim.DBaseConstants.ADDSTATE;
import static com.eleksheep.gim.DBaseConstants.ADDSTREET;
import static com.eleksheep.gim.DBaseConstants.IMAGENAME;
import static com.eleksheep.gim.DBaseConstants.LAT;
import static com.eleksheep.gim.DBaseConstants.LNG;
import static com.eleksheep.gim.DBaseConstants.PTNAME;
import static com.eleksheep.gim.DBaseConstants.SYMBOL;
import static com.eleksheep.gim.DBaseConstants.TABLE_NAME;
import static com.eleksheep.gim.DBaseConstants.TIMESTR;
import static com.eleksheep.gim.DBaseConstants.XCOORD;
import static com.eleksheep.gim.DBaseConstants.YCOORD;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eleksheep.gim.R;
import com.eleksheep.gim.LineOverlay;
import com.eleksheep.gim.PtLocMark;
import com.eleksheep.gim.PtOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;


public class MapViewActivity extends MapActivity { 
	//private static String[] FROM = { _ID, TIMESTR, LATDMS, LNGDMS, PTNAME,XCOORD,YCOORD };
	private static String[] FROM2 = { _ID, PTNAME,TIMESTR,LAT,LNG,XCOORD,YCOORD,SYMBOL,ADDSTREET,ADDCITY,ADDSTATE,ADDCOUNTRY,ADDPOSTALCODE,IMAGENAME };
	private static String ORDER_BY_DESC = _ID + " DESC";  //DESC is keyword for descending order. 
	private static final int MENU_SET_SATELLITE = 1;
    private static final int MENU_SET_MAP = 2;
    private static final int MENU_STORE_POINT = 3;
    private static final int MENU_BACK_TO_LAST_LOCATION = 4;
    private static final int MENU_SATINFO = 5;
	private LocationManager locationManager;
	private MapView mapView;
	private MyLocationOverlay myLocationOverlay;
	private MapController mapController;
	private ArrayList<PtLocMark>ptLocMarkList;
	private PointDBData points;
	private PtOverlay ptol;
	private LineOverlay lol;
	private String best;
	private LocationManager locmgr;
	private Location location = null;
	private double m_lat;
	private double m_lng;
	private float m_GPSacc;
	private String m_GPSStatus;
	private String m_locprovider;
	private boolean bstarpositionset;
	private Drawable pointMarker;
	private Drawable pointMarker2;
	
	private static final String KEYLAT = "KEY_LAT";
	private static final String KEYLNG = "KEY_LNG";
	private static final String KEYXCOORD = "KEY_XCOORD";
	private static final String KEYYCOORD = "KEY_YCOORD";
	private static final String KEYACC = "KEY_ACC";
	private static final String KEYSTA = "KEY_STATUS";
	private static final String KEYLPR = "KEY_LOCPROVIDER";
	
	@Override
	public void onResume(){
		super.onResume();
		initMapOverlay();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mapview);
        
        locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    //    SharedPreferences mPrefs;
     //   mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
        
        this.ptLocMarkList = new ArrayList<PtLocMark>();
 
        this.pointMarker = getResources().getDrawable(R.drawable.redflag2);
        this.pointMarker.setBounds(0, 0, this.pointMarker.getIntrinsicWidth(), this.pointMarker.getIntrinsicHeight());
        this.pointMarker2 = getResources().getDrawable(R.drawable.greenflag2);
        this.pointMarker2.setBounds(0, 0, this.pointMarker2.getIntrinsicWidth(), this.pointMarker2.getIntrinsicHeight());
        
        this.mapView = (MapView) findViewById(R.id.map_view);
        mapView.setBuiltInZoomControls(true);
        bstarpositionset = false;

        new Thread(new RefreshRunner()).start();
    }
	private Cursor getPoints() {
	      // Perform a managed query. The Activity will handle closing
	      // and re-querying the cursor when needed.
	      SQLiteDatabase db = points.getReadableDatabase();
	      Cursor cursor = db.query(TABLE_NAME, FROM2, null, null, null,null, ORDER_BY_DESC);
	      return cursor;
	   }
	
	@Override
	public void onStart() {
        super.onStart();
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.mapController = this.mapView.getController();
        GeoPoint lastKnownPoint = getLastKnownPoint();

        this.mapController.animateTo(lastKnownPoint);
    }
	
	 private void initMapOverlay() {
		 rungetPoints();

		 List<Overlay> ol = mapView.getOverlays();
		 ptol = new PtOverlay(MapViewActivity.this);
		 ptol.setPtLocMarkers(ptLocMarkList);
		 ol.add(ptol);
	     myLocationOverlay = new MyLocationOverlay(this, mapView);
	     ol.add(myLocationOverlay);
	     myLocationOverlay.enableMyLocation();
	     myLocationOverlay.disableCompass();
	     myLocationOverlay.runOnFirstFix(new Runnable() {
	       public void run() {
                mapController.animateTo(myLocationOverlay.getMyLocation());
          }
        });
	} 
	 private void rungetPoints(){
		 points = new PointDBData(this); 
		 try {
	         Cursor cursor = getPoints(); 
	         getPointData(cursor);
	      } finally {
	         points.close(); 
	      } 
	 }
	 
	 @Override
     public boolean onKeyDown(int keyCode, KeyEvent event) {
    	 if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

    	 } 
        	 return super.onKeyDown(keyCode, event);
     }

	 private GeoPoint getLastKnownPoint() {
	        GeoPoint lastKnownPoint = null;

	        // last KNOWN may be null, if none set after power up, or in emulator and not setup
	        Location lastKnownLocation = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

	        // get lastKnown GeoPoint (either from lastKnownLocation, or prime the pump manually)
	        if (lastKnownLocation != null) {
	            lastKnownPoint = LocationHelper.getGeoPoint(lastKnownLocation);   
	            if(mapView.getZoomLevel()<4)
	            	this.mapController.setZoom(16);
	        } else {
	            lastKnownPoint = LocationHelper.OLD_FAITHFUL_YS;
	            this.mapController.setZoom(16);

	            Toast.makeText(this,
	            	"GeoCoord Explorer can not determine your location at start. \nLast know location is unknown - defaulting to Old Faithful Yellowstone NP. Please enable GPS and use menu->My Location",
	                Toast.LENGTH_LONG).show();
	        }
	        return lastKnownPoint;
	    }
	 @Override
	    public boolean onCreateOptionsMenu(final Menu menu) {
	        super.onCreateOptionsMenu(menu);
	        int menuitemOnOfficon = android.R.drawable.button_onoff_indicator_off;
	        if(this.mapView.isSatellite())
	        	menuitemOnOfficon = android.R.drawable.button_onoff_indicator_on;
	        else
	        	menuitemOnOfficon = android.R.drawable.button_onoff_indicator_off;
	        menu.add(0, MapViewActivity.MENU_SET_MAP, 0, "Streetview").setIcon(R.drawable.streetview_48bw);
	        menu.add(1, MapViewActivity.MENU_SET_SATELLITE, 0, "Satellite").setIcon(menuitemOnOfficon);
	        menu.add(2, MapViewActivity.MENU_STORE_POINT, 0, "Store Point").setIcon(android.R.drawable.ic_menu_myplaces);  
	        menu.add(3, MapViewActivity.MENU_BACK_TO_LAST_LOCATION, 0, "My Location").setIcon(android.R.drawable.ic_menu_mylocation);
	        menu.add(4, MapViewActivity.MENU_SATINFO, 0, "Satellite Info").setIcon(R.drawable.satellite_48bw);
	        return true;
	    }
	 
	 @Override
	 public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        switch (item.getItemId()) {
            case MapViewActivity.MENU_SET_MAP:
            	String url = "google.streetview:cbll="+m_lat+","+m_lng+"&cbp=1,180,,0,1.0";
	 		    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));            
	 		    startActivity(i);
                break;
            case MapViewActivity.MENU_SET_SATELLITE:
            	this.mapView.setStreetView(false);
            	if(this.mapView.isSatellite()){
            		this.mapView.setSatellite(false);
            		item.setIcon(android.R.drawable.button_onoff_indicator_off);
            	}
            	else{
            		this.mapView.setSatellite(true);
            		item.setIcon(android.R.drawable.button_onoff_indicator_on);
            	}
                break;
            case MapViewActivity.MENU_STORE_POINT:
            	Intent istorept = new Intent(this,StorePoint.class);
    			Bundle bun = new Bundle();
    			bun.putDouble(KEYLAT,m_lat);
    			bun.putDouble(KEYLNG, m_lng);
    			bun.putDouble(KEYXCOORD, 0.0);
    			bun.putDouble(KEYYCOORD, 0.0);
    			bun.putFloat(KEYACC, m_GPSacc);
    			bun.putString(KEYSTA, m_GPSStatus);
    			bun.putString(KEYLPR, m_locprovider);
    			istorept.putExtras(bun);
    			startActivity(istorept);
                break;
            case MapViewActivity.MENU_BACK_TO_LAST_LOCATION:
                this.mapController.animateTo(getLastKnownPoint());
                break;
            case MapViewActivity.MENU_SATINFO:
            	Intent isatinfo=new Intent(this, SatActivity.class);
            	startActivity(isatinfo);
            	break;
        }
        return super.onMenuItemSelected(featureId, item);
	  }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private void getPointData(Cursor cursor) 
	{
	 	ArrayList<PointData> ptDataList = new ArrayList<PointData>();

	 	int numRows = cursor.getCount();
	    cursor.moveToFirst();
	    PointData ptdata = new PointData();
	    for(int i=0; i<numRows; ++i){
    	  ptdata.dateString = cursor.getString(cursor.getColumnIndex(TIMESTR));
    	  ptdata.title = cursor.getString(cursor.getColumnIndex(PTNAME));
    	  cursor.moveToNext();
    	  ptDataList.add(ptdata);
	     }

	    getPointOverlayItems(ptDataList,cursor);
  }
 
  //private ArrayList<PointOverlayItem> getPointOverlayItems(ArrayList<PointData> PointDataList, Cursor cursor) {
  private ArrayList<PtLocMark> getPointOverlayItems(ArrayList<PointData> PointDataList, Cursor cursor) {
    //    ArrayList<PointOverlayItem> PointOverylayItemList = new ArrayList<PointOverlayItem>();

        cursor.moveToFirst();
       // int cnt = 0;
        for(int j= 0; j<PointDataList.size(); j++)
        {
      	  	PtLocMark ptlocmrk = new PtLocMark();
      	  	ptlocmrk.setName(cursor.getString(cursor.getColumnIndex(PTNAME)));
      	  	ptlocmrk.setLatitude(cursor.getDouble(cursor.getColumnIndex(LAT)));
      	    ptlocmrk.setLongitude(cursor.getDouble(cursor.getColumnIndex(LNG)));
      	    ptlocmrk.setMarker(cursor.getInt(cursor.getColumnIndex(SYMBOL)));
      	    ptlocmrk.setAddStreet(cursor.getString(cursor.getColumnIndex(ADDSTREET)));
      	    ptlocmrk.setAddCity(cursor.getString(cursor.getColumnIndex(ADDCITY)));
      	    ptlocmrk.setAddState(cursor.getString(cursor.getColumnIndex(ADDSTATE)));
      	    ptlocmrk.setAddCountry(cursor.getString(cursor.getColumnIndex(ADDCOUNTRY)));
      	    ptlocmrk.setAddPostalCode(cursor.getString(cursor.getColumnIndex(ADDPOSTALCODE)));
      	    ptlocmrk.setTimeString(cursor.getString(cursor.getColumnIndex(TIMESTR)));
      	    ptlocmrk.setImagePath(cursor.getString(cursor.getColumnIndex(IMAGENAME)));
      	    ptLocMarkList.add(ptlocmrk);
      	  
        	cursor.moveToNext();
        }
        //return PointOverylayItemList;
        return ptLocMarkList;
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
	                       		if(lol != null){
	                       			lol.setLocation(location);
	                       			lol.setStartBMP(pointMarker);
	                       			lol.setEPBMP(pointMarker2);
	                       		}
	                       		if(!bstarpositionset){
	                       			if(lol != null)
	                       				lol.setStartLocation(location.getLatitude(),location.getLongitude());
	                       		}
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
        	m_lat = location.getLatitude();
        	m_lng = location.getLongitude();
      		m_GPSacc = location.getAccuracy();
     		String locprovider = location.getProvider();
     		if(locprovider != null){
     			m_locprovider = locprovider;
     		}
         }
    }
}
