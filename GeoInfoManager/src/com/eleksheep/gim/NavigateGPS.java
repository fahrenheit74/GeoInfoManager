package com.eleksheep.gim;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import com.eleksheep.gim.R;
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigateGPS extends Activity implements LocationListener  { 
	private static final String KEYLAT = "KEY_LAT";
	private static final String KEYLNG = "KEY_LNG";
	private static final String KEYXCOORD = "KEY_XCOORD";
	private static final String KEYYCOORD = "KEY_YCOORD";
	//private static final String KEYACC = "KEY_ACC";
	//private static final String KEYSTA = "KEY_STATUS";
	//private static final String KEYLPR = "KEY_LOCPROVIDER";
	private static final String KEYPTNAME = "KEY_PTNAME";
	public static final String KEY_UNITS = "KEY_UNITS";
	private static final String KEYSYMBOL = "KEY_SYMBOL";
	public static final String KEY_PROJ_STRING = "KEY_PROJSTRING";
	private TextView PointNameText;
	private TextView LatText;
	private TextView LngText;
	private TextView CoordXText;
	private TextView CoordYText;
	private TextView LatTextCP;
	private TextView LngTextCP;
	private TextView CoordXTextCP;
	private TextView CoordYTextCP;
	private TextView bearingText;
	private TextView headingText;
	private TextView speedText;
	private TextView vmgText;
	private TextView distText;
	private TextView etaText;
	private TextView ettaText;
	private double pointlat = 0.0;
	private double pointlng = 0.0;
	Projection projection;
	private final String defprojstr = "+proj=utm, +zone=10, +ellps=GRS80,+ellps=GRS80,+towgs84=0,0,0,0,0,0,0,+units=m,+no_defs";
	
	private String best = "";
	private LocationManager locmgr = null;
	private Location loc = null;
	private double distance = 0.0;
	private String szUnits = " ft";  //ft or m
	boolean isMeters = false;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.navigate);
	    
	    SharedPreferences mPrefs;
        mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
        
        isMeters = mPrefs.getBoolean(KEY_UNITS, false);
        
        locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        PointNameText = (TextView) findViewById(R.id.nav_pointname);
        LatText = (TextView) findViewById(R.id.nav_latvalue);
        LngText = (TextView) findViewById(R.id.nav_lngvalue);
        CoordXText = (TextView)findViewById(R.id.nav_coordx);
        CoordYText = (TextView)findViewById(R.id.nav_coordy);
        bearingText = (TextView) findViewById(R.id.nav_bearing);
        headingText = (TextView) findViewById(R.id.nav_heading);
        speedText = (TextView)	findViewById(R.id.nav_speed);
        vmgText = (TextView) findViewById(R.id.nav_vmg);
        LatTextCP = (TextView) findViewById(R.id.nav_latvaluecp);
        LngTextCP = (TextView) findViewById(R.id.nav_lngvaluecp);
        CoordXTextCP = (TextView) findViewById(R.id.nav_coordxcp);
        CoordYTextCP = (TextView) findViewById(R.id.nav_coordycp);
        distText = (TextView) findViewById(R.id.nav_distance);
        
        final ImageView navSymbol = (ImageView)findViewById(R.id.nav_symbol);
        etaText = (TextView) findViewById(R.id.nav_eta);
        ettaText = (TextView) findViewById(R.id.nav_etta);
        locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        DecimalFormat twoPlaces = new DecimalFormat("0.00");
        final Bundle bun = getIntent().getExtras();
        String szTmp = bun.getString(KEYPTNAME);
        
        if(szTmp.length() >12)
        	PointNameText.setText(szTmp.substring(0, 12));
        else
        	PointNameText.setText(szTmp);
        
        LatText.setText(Location.convert(bun.getDouble(KEYLAT), Location.FORMAT_SECONDS));
        pointlat = bun.getDouble(KEYLAT);
        LngText.setText(Location.convert(bun.getDouble(KEYLNG), Location.FORMAT_SECONDS));
        pointlng = bun.getDouble(KEYLNG);
        
        navSymbol.setImageResource(bun.getInt(KEYSYMBOL));
        
        String szProjString = mPrefs.getString(TestProj1.KEY_PROJ_STRING, defprojstr);
        String delim2 = ",";
        String[] tokens2 = szProjString.split(delim2);
        projection = ProjectionFactory.fromPROJ4Specification(tokens2);
        
        Point2D.Double src = new Point2D.Double();
        Point2D.Double dst = new Point2D.Double();
        src.x = pointlng;
        src.y = pointlat;
      	String szprojname = projection.getName().toString();
      	if(szprojname.length()>0){
      		projection.transform(src,dst);
      		if(isMeters == false)
      			MapMath.MeterstoFeet(true, dst);
	
      		CoordXText.setText(twoPlaces.format(dst.x));
	   		CoordYText.setText(twoPlaces.format(dst.y));
      	}
	
	final Listener gpslistener=new GpsStatus.Listener()
    {		
		boolean isGpsFixed = false;
		
		public void showGpsSatInfo(){
			GpsStatus xGpsStatus = locmgr.getGpsStatus(null) ; 
			Iterable<GpsSatellite> iSatellites = xGpsStatus.getSatellites(); 
    		Iterator<GpsSatellite> it  = iSatellites.iterator();
    		int satsused = 0;
    		int sats = 0;
    		while ( it.hasNext() )
            {
                    GpsSatellite oSat = (GpsSatellite) it.next();
                    //String str = "***Satellites: "+ oSat.getPrn() + "," + oSat.getAzimuth() + "," + oSat.getElevation() ;
                    //String strfix = "";
                    
                    if(oSat.usedInFix()){
                    	//str =+ oSat.getPrn() + ",";
                    	satsused++;
                    }
                    sats++;
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
    	Location location = locmgr.getLastKnownLocation(best);
    	loc = locmgr.getLastKnownLocation(best);
    	//setCoordSys();
    	dumplocation(location);
    }

	private void dumplocation(Location location){
    	if (location == null)
            return;
         else{
            String locstr;
         	DecimalFormat twoPlaces = new DecimalFormat("0.00");
         	locstr = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
      		LatTextCP.setText(locstr);
      		locstr = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
      		LngTextCP.setText(locstr);
      		float[] distresults = new float[2];
      		Location.distanceBetween(location.getLatitude(), location.getLongitude(), pointlat, pointlng, distresults);
      		distance = distresults[0];
      		
      		Point2D.Double src = new Point2D.Double();
            Point2D.Double dst = new Point2D.Double();
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            
            src.x = lng;
          	src.y = lat;
          	String szprojname = projection.getName().toString();
          	if(szprojname.length()>0){
          		projection.transform(src,dst);
          		DecimalFormat fourPlaces = new DecimalFormat("0.0000");
          		if(isMeters == false)
          			MapMath.MeterstoFeet(true, dst);
    	
          		CoordXTextCP.setText(fourPlaces.format(dst.x));
    	   		CoordYTextCP.setText(fourPlaces.format(dst.y));
          	}
      		
      		double unitconversion = 1.0;
      		String szUnits = " ft";
      		if(isMeters){
      			if(distance > 1000/2){  //If more than 1/2 km
      				szUnits = " km";
      				unitconversion = 0.001;
      			}
      			else{
      				unitconversion = 1.0;
      				szUnits = " m";
      			}
      		}
      		else{
      			if(distance > 1609.000/4){  //If more than 1/2 mi
      				szUnits = " mi";
      				unitconversion = 0.000621371192;
      			}
      			else{
      				unitconversion = 3.2808399;
      				szUnits = " ft";
      			}
      		}
      		
            distText.setText(twoPlaces.format(distresults[0]*unitconversion)+ szUnits);

            Location dest = loc;
            dest.setLatitude(pointlat);
            dest.setLongitude(pointlng);
            double bearingTo = location.bearingTo(dest);
            if(bearingTo < 0)
            	bearingTo = 360 + bearingTo;
            headingText.setText(twoPlaces.format(bearingTo));
            double bearing = location.getBearing();
            bearingText.setText(twoPlaces.format(bearing));
            double speed = location.getSpeed();
            speedText.setText(twoPlaces.format(speed));
            
            long secondstilarrival = 0;
            if(bearing != 0.0 && speed != 0.0){
            	double course_offset = bearingTo-bearing;
            	double vmg = Math.cos(MapMath.degToRad(course_offset));
            	vmg = vmg*speed;
            	vmgText.setText(twoPlaces.format(vmg));
            	if(vmg > 0){
            		secondstilarrival = (long)((distance / vmg)*1000);
            		long etamillis = System.currentTimeMillis()+ secondstilarrival;
            		Date date = new Date(etamillis);
            		int hr = date.getHours();
            		int min = date.getMinutes();
            		double secs = date.getSeconds();
            		DecimalFormat leadingzeros = new DecimalFormat("00.0");
            		String seconds = leadingzeros.format(secs);
            		String etatime = String.format("%d:%d:%s", hr,min,seconds);
            		etaText.setText(etatime);
            		
                  	hr = (int)(secondstilarrival / (1000*60*60));
                  	min = (int)((secondstilarrival % (1000*60*60)) / (1000*60));
                  	secs = ((secondstilarrival % (1000*60*60)) % (1000*60)) / 1000.00;
                  	String ettatime = "-:-:-";
                  	if(hr >0)
                  		ettatime = String.format("%d:%d:%s", hr,min,seconds);
                  	else{
                  		if(min > 0)
                  			ettatime = String.format("%d:%s", min,seconds);
                  		else {
                  			ettatime = String.format("%ss", seconds);
                  		}
                  	}
                  	ettaText.setText(ettatime);
            	}
            	else{
                	String ettatime = "-:-:-";
                	ettaText.setText(ettatime);
                	etaText.setText(ettatime);
                }
            }
            else{
            	String ettatime = "-:-:-";
            	ettaText.setText(ettatime);
            	etaText.setText(ettatime);
            }
            
      		//GPSacc.setText(twoPlaces.format(location.getAccuracy())+"m");
      		//m_GPSacc = location.getAccuracy();
     		String locprovider = location.getProvider();
     		if(locprovider != null){
     		//	LocProvider.setText(locprovider);
     		//	m_locprovider = locprovider;
     		}
         }
    }
	private void setLocation(Location location){
		//  loc = location;
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
