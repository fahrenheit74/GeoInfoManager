package com.eleksheep.gim;

	import java.text.DecimalFormat;
	import com.jhlabs.map.MapMath;
	import java.util.ArrayList;
	import java.util.List;
	import android.content.Context;
	import android.content.Intent;
	import android.content.SharedPreferences;
	import android.graphics.drawable.Drawable;
	import android.location.Criteria;
	import android.location.Location;
	import android.location.LocationManager;
	import android.os.Bundle;
	import android.view.KeyEvent;
	import android.view.Menu;
	import android.view.MenuItem;
	import android.view.View;
	import android.view.View.OnClickListener;
	import android.widget.Button;
    import android.widget.TextView;
	import android.widget.Toast;

	import com.eleksheep.gim.R;
	import com.eleksheep.gim.LineOverlay;
	import com.google.android.maps.GeoPoint;
	import com.google.android.maps.MapActivity;
	import com.google.android.maps.MapController;
	import com.google.android.maps.MapView;
	import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

	public class TapeMeasure extends MapActivity { 
		//private static String[] FROM = { _ID, TIMESTR, LATDMS, LNGDMS, PTNAME,XCOORD,YCOORD };
		private static final int MENU_SET_SATELLITE = 1;
		private static final int MENU_ENABLE_COMPASS = 2;
		private static final int MENU_STORE_POINT = 3;
	    private static final int MENU_BACK_TO_LAST_LOCATION = 4;
	    private static final int MENU_SATINFO = 5;
	    private static final int MENU_CALCAREA = 6;
	    private static final String KEYREP = "KEY_REP";
		private LocationManager locationManager;
		private MapView mapView;
		private MyLocationOverlay myLocationOverlay;
		private MapController mapController;
		private LineOverlay lol;
		private String best;
		private LocationManager locmgr;
		private Location location = null;
		@SuppressWarnings("unused")
		private double m_lat;
		@SuppressWarnings("unused")
		private double m_lng;
		@SuppressWarnings("unused")
		private float m_GPSacc;
		@SuppressWarnings("unused")
		private String m_GPSStatus;
		@SuppressWarnings("unused")
		private String m_locprovider;
		private ArrayList<Location>	m_locArrayLst;
		private boolean bstarpositionset;
		private double m_perimeter;
		private Drawable pointMarker;
		private Drawable pointMarker2;
		private Drawable pointMarker3;
		private TextView tapeDistTextVw;
		private TextView tapeAreaTextVw;
		private Button tapeReport;
		private boolean isMeters;
		private Button ptStopbtn;
		private boolean m_stoprec= false;
		private boolean m_calcArea = false;

		private static final String KEY_UNITS = "KEY_UNITS";
		
		@Override
		public void onResume(){
			super.onResume();
			initMapOverlay();
		}
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.setContentView(R.layout.tapeview);
	        
	        final Button ptMarkbtn = (Button)findViewById(R.id.tape_pt_btn);
	        ptStopbtn = (Button)findViewById(R.id.tape_stop_btn);
	        tapeReport = (Button)findViewById(R.id.tape_report_btn);
	        tapeDistTextVw = (TextView) findViewById(R.id.tapedisttext);
	        tapeAreaTextVw = (TextView) findViewById(R.id.tapeareatext);
	        locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        
	        tapeDistTextVw.setVisibility(View.GONE);
	        tapeAreaTextVw.setVisibility(View.GONE);
	        ptStopbtn.setVisibility(View.GONE);
	        tapeReport.setVisibility(View.GONE);
	        ptMarkbtn.setText("Start Pt");
	        
	        SharedPreferences mPrefs;
	        mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
	        isMeters = mPrefs.getBoolean(KEY_UNITS, false);
	        
	        this.m_locArrayLst = new ArrayList<Location>();
	        
	        this.pointMarker = getResources().getDrawable(R.drawable.redflag2);
	        this.pointMarker.setBounds(0, 0, this.pointMarker.getIntrinsicWidth(), this.pointMarker.getIntrinsicHeight());
	        this.pointMarker2 = getResources().getDrawable(R.drawable.greenflag2);
	        this.pointMarker2.setBounds(0, 0, this.pointMarker2.getIntrinsicWidth(), this.pointMarker2.getIntrinsicHeight());
	        this.pointMarker3 = getResources().getDrawable(R.drawable.checkflag);
	        this.pointMarker3.setBounds(0, 0, this.pointMarker3.getIntrinsicWidth(), this.pointMarker3.getIntrinsicHeight());
	        
	        this.mapView = (MapView) findViewById(R.id.tape_view);
	        mapView.setBuiltInZoomControls(true);
	        bstarpositionset = false;
	        m_perimeter = 0.0;
	        
	     // create a Thread that will periodically send messages to our Handler 
	        new Thread(new RefreshRunner()).start();
	        
	        ptMarkbtn.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v){
	        		if(m_stoprec==false){
	        			setMarkPt();
	        			ptMarkbtn.setText("Next Pt");
	        		}
	        		else{
	        			clearMarkPts();
	        			ptMarkbtn.setText("Start Pt");
	        		}
	        	}
	        });
	        ptStopbtn.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v){
	        		setMarkPt();
	        		setStop();
	        		m_stoprec = true;
	        		ptMarkbtn.setText("Clear");
	        	}
	        });
	        tapeReport.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v){
	        		CreateReport();
	        	}
	        });
	    }
		
		public void setStop(){
			lol.setStop(true);
			tapeReport.setVisibility(View.VISIBLE);
			if(m_calcArea)
	    		calcArea(m_locArrayLst);
	    	else
	    		tapeAreaTextVw.setText("");
		}
		public void CreateReport(){
			String rep = DumpReport(m_locArrayLst);
			Bundle bun = new Bundle();
			Intent ireport=new Intent(this, Report.class);
			bun.putString(KEYREP, rep);
			ireport.putExtras(bun);
        	startActivity(ireport);
		}
		
		public void clearMarkPts(){
			m_locArrayLst.clear();
			bstarpositionset = false;
			lol.setDisplayLines(false);
			lol.setStop(false);
			m_stoprec = false;
			m_perimeter = 0.0;
			tapeDistTextVw.setVisibility(View.GONE);
			tapeAreaTextVw.setVisibility(View.GONE);
		 	ptStopbtn.setVisibility(View.GONE);
		 	tapeReport.setVisibility(View.GONE);
		}
		
		public void setMarkPt(){
			location = locmgr.getLastKnownLocation(best);
			m_locArrayLst.add(location);
			if(lol !=null){
		    	lol.setStartLocation(location.getLatitude(),location.getLongitude());
		    	lol.setLocArrayList(m_locArrayLst);
		    	lol.setUnits(isMeters);
		    	calcLength(m_locArrayLst);
		    	if(m_calcArea)
		    		calcArea(m_locArrayLst);
		    	else
		    		tapeAreaTextVw.setText("");
		    	bstarpositionset = true;
		    	lol.setDisplayLines(true);
		    	lol.setClosePoly(m_calcArea);
		    	
		    	if(m_perimeter>0){
	   		 		tapeDistTextVw.setVisibility(View.VISIBLE);
	   		 		tapeAreaTextVw.setVisibility(View.VISIBLE);
	   		 		ptStopbtn.setVisibility(View.VISIBLE);
		    	}
			}
		}
	
		@Override
		public void onStart() {
	        super.onStart();
	        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        this.mapController = this.mapView.getController();
	        // animate to, and get buoy data for lastKnownPoint on startup (or fake/prime point if no last known)
	        GeoPoint lastKnownPoint = getLastKnownPoint();
	        this.mapController.animateTo(lastKnownPoint);
	    }
		
		 private void initMapOverlay() {
			 List<Overlay> ol = mapView.getOverlays();
			 lol = new LineOverlay();
			 ol.add(lol);
		     myLocationOverlay = new MyLocationOverlay(this, mapView);
		     ol.add(myLocationOverlay);
		     myLocationOverlay.enableMyLocation();
		     myLocationOverlay.runOnFirstFix(new Runnable() {
		     public void run() {
	                mapController.animateTo(myLocationOverlay.getMyLocation());
		          }
	        });
		} 
		 
		 @Override
	     public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	 if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
	    		 setMarkPt();
	    	  	 return true;
	    	 } 
	        	 return super.onKeyDown(keyCode, event);
	     }
		 
		private void calcLength(ArrayList<Location> locArrayLst){
			double prevlat = 0.0;
	    	double prevlng = 0.0;
	    	double perimeter = 0.0;
	    	m_perimeter = 0.0;
	    	if(locArrayLst.isEmpty())
	    		return;
	    	else{
	    		float[] distresults = new float[2];
				for (int j=0; j<locArrayLst.size(); j++) {
		        	Location loc = locArrayLst.get(j);
		        	double lat = loc.getLatitude();
		        	double lng = loc.getLongitude();
		        	//double dacc = loc.getAccuracy();
		        	if(j > 0)
		        	{
		        		Location.distanceBetween(lat, lng, prevlat, prevlng, distresults);
		        		@SuppressWarnings("unused")
						double degbearings= MapMath.bearing(prevlat, prevlng, lat, lng);
		        		perimeter = distresults[0];
		        		m_perimeter += perimeter;
		        	}
		        	prevlat = lat;
		        	prevlng = lng;
				}
			//	if(!m_stoprec){
			//		@SuppressWarnings("unused")
			//		int locsize = locArrayLst.size();
			//		if(locsize < 2)
			//			return;
			//		Location loc = locArrayLst.get(locArrayLst.size()-1);
			//		Location.distanceBetween(location.getLatitude(), location.getLongitude(), loc.getLatitude(), loc.getLongitude(), distresults);
			//		m_perimeter += distresults[0];
			//	}
				if(m_calcArea){
					Location loc = locArrayLst.get(0);
					Location.distanceBetween(location.getLatitude(), location.getLongitude(), loc.getLatitude(), loc.getLongitude(), distresults);
					perimeter = distresults[0];
					m_perimeter += perimeter;
				}
				
			  	DecimalFormat onePlaces = new DecimalFormat("0.0");
		    	DecimalFormat fourPlaces = new DecimalFormat("0.0000");
		    	
		    	String szLengthorPerimeterLabel = "Len: ";
		    	if(m_calcArea)
		    		szLengthorPerimeterLabel = "Per: ";
		    	
		    	String szUnits = " ft";
		    	double unitconversion = 1.0;
		    	String perimeter_string = onePlaces.format(m_perimeter*unitconversion) +" ft";
		   		  if(isMeters){
		   			if(m_perimeter > 1000){  
		   				szUnits = " km";
		   				unitconversion = 0.001;
		   				perimeter_string = szLengthorPerimeterLabel + fourPlaces.format(m_perimeter*unitconversion)+ szUnits;
		   			}
		   			else{
		   				unitconversion = 1.0;
		   				szUnits = " m";
		   				perimeter_string = szLengthorPerimeterLabel + onePlaces.format(m_perimeter*unitconversion)+ szUnits;
		   			}
		   		  }
		   		  else{
		   			if(m_perimeter > 1609.000){ 
		   				szUnits = " mi";
		   				unitconversion = 0.000621371192;
		   				perimeter_string = szLengthorPerimeterLabel + fourPlaces.format(m_perimeter*unitconversion)+ szUnits;
		   			}
		   			else{
		   				unitconversion = 3.2808399;
		   				szUnits = " ft";
		   				perimeter_string = szLengthorPerimeterLabel + onePlaces.format(m_perimeter*unitconversion)+ szUnits;
		   			}
		   		  }
		    	
		    	if(m_perimeter>0){
	   		 		tapeDistTextVw.setText(perimeter_string);
		    	}
				
	    	}
		}
		
		private void calcArea(ArrayList<Location> locArrayLst){
			double area = CalcAreaTriangle(locArrayLst);  //in Sq. Meters
	    	double areaunitconversion = 1.0;
	    	String areaunits = "sqft";
	    	int decimalplaces = 2;
	    	if(isMeters){
	    		if(area > 10000){  //
	    			areaunitconversion = 0.0001;
	    			areaunits = " ha";
	    			decimalplaces = 4;
	    		}
	    		else{
	    			areaunitconversion = 1.0;
	    			areaunits = " sq m";
	    		}
	    	}
	    	else{
	    		if(area > 4046.85642){  //
	    			areaunitconversion = 0.000247105381;
	    			areaunits = " acres";
	    			decimalplaces = 4;
	    		}
	    		else{
	    			areaunitconversion = 10.7639104;
	    			areaunits = " sqft";
	    		}
	    		
	    	}
		    DecimalFormat twoPlaces = new DecimalFormat("0.00");
		    DecimalFormat fourPlaces = new DecimalFormat("0.0000");
		    @SuppressWarnings("unused")
			String AreaString = "0.0" + areaunits;
		    
	    	if(decimalplaces == 4)
	    		AreaString = "Area: "  + fourPlaces.format(area*areaunitconversion) + areaunits;
	    	else
	    		AreaString = "Area: "  + twoPlaces.format(area*areaunitconversion) + areaunits;
	    	tapeAreaTextVw.setText(AreaString);
		}
		
		private GeoPoint CalcAreaCenterPoint(ArrayList<Location>locArrayLst){
			Double avglat = 0.0;
			Double avglng = 0.0;

    		Location loc;
			for (int j=0; j<locArrayLst.size()-1; j++) {
				loc = locArrayLst.get(j);
	        	avglat += loc.getLatitude()*1E6;
	        	avglng += loc.getLongitude()*1E6;
			}
			int locsize = locArrayLst.size();
			avglat = avglat/(locsize-1);
			avglng = avglng/(locsize-1);
			GeoPoint CenterPt = new GeoPoint(avglat.intValue(),avglng.intValue());
			return CenterPt;
		}
		private double CalcAreaTriangle(ArrayList<Location>locArrayLst){
			double area = 0.0;
			double lat = 0.0;
			double lng = 0.0;
			double prevlat = 0.0;
			double prevlng = 0.0;
			double side_a = 0.0;
			double side_b = 0.0;
			double side_c = 0.0;
			
			if(locArrayLst.isEmpty()){
	    		area = 0.0;
	    	}
	    	else{
	    		ArrayList<Location> locArrayLst2 = new ArrayList<Location>();
	    		for(int i=0; i<locArrayLst.size(); i++) {
	    			locArrayLst2.add(locArrayLst.get(i));
	    		}
	    		Location firstloc = locArrayLst2.get(0);
	    		locArrayLst2.add(firstloc);
	    		
	    		GeoPoint cpt = CalcAreaCenterPoint(locArrayLst2);
	    		
	    		for (int j=0; j<locArrayLst2.size(); j++) {
					float[] distresults = new float[2];
		        	Location loc = locArrayLst2.get(j);
		        	lat = loc.getLatitude();
		        	lng = loc.getLongitude();
		        	boolean isRight = true;
		        	if(j > 0)
		        	{
		        		//Calculate triangle sides
		        		Location.distanceBetween(cpt.getLatitudeE6()/1E6, cpt.getLongitudeE6()/1E6, prevlat, prevlng, distresults);
		        		side_a = distresults[0];
		        		Location.distanceBetween(prevlat, prevlng, lat, lng, distresults);
		        		side_b = distresults[0];
		        		Location.distanceBetween(lat, lng, cpt.getLatitudeE6()/1E6, cpt.getLongitudeE6()/1E6, distresults);
		        		side_c = distresults[0];
		        		
		        		//DecimalFormat fourPlaces = new DecimalFormat("0.0000");		        		
		        		double angline_a = MapMath.bearing(cpt.getLatitudeE6()/1E6, cpt.getLongitudeE6()/1E6, prevlat, prevlng);
		        		double angline_b = MapMath.bearing(prevlat, prevlng,lat, lng);
		        		//double angline_c = MapMath.bearing(lat, lng, cpt.getLatitudeE6()/1E6, cpt.getLongitudeE6()/1E6);
		        		
		        		if(angline_a + 180 > 360){
		        			double al360_adj = (angline_a+180)-360;
		        			if((angline_b<=al360_adj)||(angline_b>angline_a))
		        				isRight = true;
		        			else
		        				isRight = false;
		        		}
		        		else {
		        			if( ((angline_b - angline_a) >0)&&((angline_b-angline_a)<180) )
		        				isRight = true;
		        			else
		        				isRight = false;
		        		}
		        		double tmparea = 0.0;
		        		if(isRight){
		        			
		        			tmparea = MapMath.areatriangle(side_a, side_b, side_c);
		        			area += tmparea;
		        		}
		        		else{
		        			tmparea = MapMath.areatriangle(side_a, side_b, side_c);
		        			area -= tmparea;
		        		}
		        	}
		        	prevlat = lat;
		        	prevlng = lng;
	    		}
	    	}
			return Math.abs(area);
		}
		
		private double CalcAreaTriangle(ArrayList<Location>locArrayLst,StringBuilder report_area){
			double area = 0.0;
			double lat = 0.0;
			double lng = 0.0;
			double prevlat = 0.0;
			double prevlng = 0.0;
			double side_a = 0.0;
			double side_b = 0.0;
			double side_c = 0.0;
			
			if(locArrayLst.isEmpty()){
	    		area = 0.0;
	    	}
	    	else{
	    		GeoPoint cpt = CalcAreaCenterPoint(locArrayLst);
	    		report_area.append("Center Point: \n");
	    		report_area.append("Lat: " + Location.convert(cpt.getLatitudeE6()/1E6, Location.FORMAT_SECONDS));
	    		report_area.append(" Long: " + Location.convert(cpt.getLongitudeE6()/1E6, Location.FORMAT_SECONDS)+"\n");
	    		
	    		for (int j=0; j<locArrayLst.size(); j++) {
					float[] distresults = new float[2];
		        	Location loc = locArrayLst.get(j);
		        	lat = loc.getLatitude();
		        	lng = loc.getLongitude();
		        	boolean isRight = true;
		        	if(j > 0)
		        	{
		        		//Calculate triangle sides
		        		Location.distanceBetween(cpt.getLatitudeE6()/1E6, cpt.getLongitudeE6()/1E6, prevlat, prevlng, distresults);
		        		side_a = distresults[0];
		        		Location.distanceBetween(prevlat, prevlng, lat, lng, distresults);
		        		side_b = distresults[0];
		        		Location.distanceBetween(lat, lng, cpt.getLatitudeE6()/1E6, cpt.getLongitudeE6()/1E6, distresults);
		        		side_c = distresults[0];
		        		
		        		DecimalFormat fourPlaces = new DecimalFormat("0.0000");		        		
		        		double angline_a = MapMath.bearing(cpt.getLatitudeE6()/1E6, cpt.getLongitudeE6()/1E6, prevlat, prevlng);
		        		double angline_b = MapMath.bearing(prevlat, prevlng,lat, lng);
		        		double angline_c = MapMath.bearing(lat, lng, cpt.getLatitudeE6()/1E6, cpt.getLongitudeE6()/1E6);
		        		report_area.append("\nTriangle " + Integer.toString(j) + "\n");
		        		
		        		report_area.append("Side_a: " + fourPlaces.format(side_a)+ "\n");
		        		report_area.append("Bearing A-B: " + fourPlaces.format(angline_a)+ "\n");
		        		report_area.append("Side_b: " + fourPlaces.format(side_b)+ "\n");
		        		report_area.append("Bearing B-C:" + fourPlaces.format(angline_b)+ "\n");
		        		report_area.append("Side_c: " + fourPlaces.format(side_c)+ "\n");
		        		report_area.append("Bearing C-A:" + fourPlaces.format(angline_c)+ "\n");
		        		
		        		if(angline_a + 180 > 360){
		        			double al360_adj = (angline_a+180)-360;
		        			if((angline_b<=al360_adj)||(angline_b>angline_a))
		        				isRight = true;
		        			else
		        				isRight = false;
		        		}
		        		else {
		        			if( ((angline_b - angline_a) >0)&&((angline_b-angline_a)<180) )
		        				isRight = true;
		        			else
		        				isRight = false;
		        		}
		        		double tmparea = 0.0;
		        		if(isRight){
		        			
		        			tmparea = MapMath.areatriangle(side_a, side_b, side_c);
		        			report_area.append("Area:" + "+" + fourPlaces.format(tmparea)+ "\n");
		        			area += tmparea;
		        		}
		        		else{
		        			tmparea = MapMath.areatriangle(side_a, side_b, side_c);
		        			report_area.append("Area:" + "-" + fourPlaces.format(tmparea)+ "\n");
		        			area -= tmparea;
		        		}
		        	}
		        	prevlat = lat;
		        	prevlng = lng;
	    		}
	    	}
			return Math.abs(area);
		}
		private String DumpReport(ArrayList<Location> locArrayLst){
			double perimeter = 0.0;
			double prevlat = 0.0;
	    	double prevlng = 0.0;
	    	double lat = 0.0;
        	double lng = 0.0;
	    	double dacc = 0.0;
	    	StringBuilder report = new StringBuilder();
	    	DecimalFormat onePlaces = new DecimalFormat("0.0");
	    	DecimalFormat twoPlaces = new DecimalFormat("0.00");
	    	DecimalFormat fourPlaces = new DecimalFormat("0.0000");
	    	DecimalFormat sixPlaces = new DecimalFormat("0.000000");
	    	double unitconversion = 0.0;
	    	String szUnits = "m";
	    	
	    	if(locArrayLst.isEmpty()){
	    		m_perimeter = 0.0;
	    		report.append("No Measurements");
	    	}
	    		
	    	else{
	    		if(m_calcArea) { //close polygon by adding first boint to end.
		    		Location firstloc = locArrayLst.get(0);
		    		locArrayLst.add(firstloc);
		    	}
	    		
				for (int j=0; j<locArrayLst.size(); j++) {
					float[] distresults = new float[2];
		        	Location loc = locArrayLst.get(j);
		        	lat = loc.getLatitude();
		        	lng = loc.getLongitude();
		        	dacc = loc.getAccuracy();
		        	if(j > 0)
		        	{
		        		Location.distanceBetween(lat, lng, prevlat, prevlng, distresults);
						double degbearings= MapMath.bearing(prevlat, prevlng, lat, lng);
		        		perimeter += distresults[0];
		        		m_perimeter = perimeter;
		        		
		        		//Report 
			        	report.append("Point: ");
			        	report.append(Integer.toString(j)+"\n");
			        	report.append("Lat: " + Location.convert(prevlat, Location.FORMAT_SECONDS));
			        	report.append(" Long: " + Location.convert(prevlng, Location.FORMAT_SECONDS)+"\n");
			        	report.append("Point ");
			        	report.append(Integer.toString(j+1)+"\n");
			        	report.append("Lat: " + Location.convert(lat, Location.FORMAT_SECONDS));
			        	report.append(" Long: " + Location.convert(lng, Location.FORMAT_SECONDS)+"\n");
			        	report.append("Line Segment: ");
			        	report.append(Integer.toString(j));
			        	report.append("(Pt "+Integer.toString(j) +" Pt "+ Integer.toString(j+1)+")"+"\n");
			        	if(isMeters){
			        		if(m_perimeter > 1000){ 
			        			szUnits = " km";
				   				unitconversion = 0.001;
			        			report.append("Length: " + fourPlaces.format(distresults[0]*unitconversion) + szUnits+"\n");
			        		}
			        		else{
			        			unitconversion = 1.0;
				   				szUnits = " m";
				   				report.append("Length: " + twoPlaces.format(distresults[0]*unitconversion) + szUnits+"\n");
			        		}
			        	}
			        	else{
			        		if(m_perimeter > 1000){ 
			        			szUnits = " mi";
				   				unitconversion = 0.000621371192;
				   				report.append("Length: " + fourPlaces.format(distresults[0]*unitconversion) + szUnits+"\n");
			        		}
			        		else{
			        			unitconversion = 3.2808399;
				   				szUnits = " ft";
				   				report.append("Length: " + fourPlaces.format(distresults[0]*unitconversion) + szUnits+"\n");
			        		}
			        			
			        	}
			        	report.append("Bearing: " + fourPlaces.format(degbearings)+" deg"+ "\n");
			        	if(isMeters)
			        		report.append("GPS Accuracy: " + twoPlaces.format(dacc)+ "m"+"\n");
			        	else
			        		report.append("GPS Accuracy: " + twoPlaces.format(dacc*3.2808399)+ "ft"+"\n");
			        	report.append("\n"); //Blank Line
		        	}
		        	prevlat = lat;
		        	prevlng = lng;
				}
				if(isMeters){
					if(m_perimeter > 1000){  
		   				szUnits = " km";
		   				unitconversion = 0.001;
		   				report.append("Total Length: " + fourPlaces.format(m_perimeter*unitconversion) + szUnits + "\n");
		   			}
		   			else{
		   				unitconversion = 1.0;
		   				szUnits = " m";
		   				report.append("Total Length: " + twoPlaces.format(m_perimeter*unitconversion) + szUnits + "\n");
		   			}
				}
				else{
					if(m_perimeter > 1000){  
		   				szUnits = " mi";
		   				unitconversion = 0.000621371192;
		   				report.append("Total Length: " + fourPlaces.format(m_perimeter*unitconversion) + szUnits + "\n");
		   			}
		   			else{
		   				unitconversion = 3.2808399;
		   				szUnits = " ft";
		   				report.append("Total Length: " + fourPlaces.format(m_perimeter*unitconversion) + szUnits + "\n");
		   			}
				}
	    	}
	    	if(m_calcArea){
		    	String areaunits = "sq m";
		    	StringBuilder report_area = new StringBuilder();
		    	report_area.append("\nArea Report\n");
		    	double area = CalcAreaTriangle(locArrayLst,report_area);  //in Sq. Meters
		    	double areaunitconversion = 1.0;
		    	int decimalplaces = 2;
		    	if(isMeters){
		    		if(area > 10000){  //
		    			areaunitconversion = 0.0001;
		    			areaunits = " ha";
		    			decimalplaces = 4;
		    		}
		    		else{
		    			areaunitconversion = 1.0;
		    			areaunits = " sq m";
		    		}
		    	}
		    	else{
		    		if(area > 4046.85642){  //
		    			areaunitconversion = 0.000247105381;
		    			areaunits = " acres";
		    			decimalplaces = 4;
		    		}
		    		else{
		    			areaunitconversion = 10.7639104;
		    			areaunits = " sq ft";
		    		}
		    		
		    	}
		    	if(decimalplaces == 4)
		    		report.append("Area: "  + sixPlaces.format(area*areaunitconversion) + areaunits + "\n");
		    	else
		    		report.append("Area: "  + twoPlaces.format(area*areaunitconversion) + areaunits + "\n");
		    	
		    	report.append(report_area);
	    	}
	    	
	    	return report.toString();
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
		        int menuitemSatOnOfficon = android.R.drawable.button_onoff_indicator_off;
		        if(this.mapView.isSatellite())
		        	menuitemSatOnOfficon = android.R.drawable.button_onoff_indicator_on;
		        else
		        	menuitemSatOnOfficon = android.R.drawable.button_onoff_indicator_off;
		        int menuitemCompassOnOfficon = android.R.drawable.button_onoff_indicator_off;
		        if(myLocationOverlay.isCompassEnabled())
		        	menuitemCompassOnOfficon = android.R.drawable.button_onoff_indicator_on;
		        else
		        	menuitemCompassOnOfficon = android.R.drawable.button_onoff_indicator_off;
		        
		        menu.add(1, TapeMeasure.MENU_SET_SATELLITE, 0, "Satellite").setIcon(menuitemSatOnOfficon); 
		        menu.add(2, TapeMeasure.MENU_ENABLE_COMPASS, 0, "Compass").setIcon(menuitemCompassOnOfficon); 
		        menu.add(3, TapeMeasure.MENU_BACK_TO_LAST_LOCATION, 0, "My Location").setIcon(android.R.drawable.ic_menu_mylocation);
		        menu.add(4, TapeMeasure.MENU_SATINFO, 0, "Satellite Info").setIcon(R.drawable.satellite_48bw);
		        menu.add(5, TapeMeasure.MENU_CALCAREA, 0, "Calculate Area").setIcon(android.R.drawable.button_onoff_indicator_off);
		        return true;
		    }
		 
		 @Override
		 public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
	        switch (item.getItemId()) {
	            case TapeMeasure.MENU_SET_SATELLITE:
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
	            case TapeMeasure.MENU_ENABLE_COMPASS:
	            	if(myLocationOverlay.isCompassEnabled()){
	            		myLocationOverlay.disableCompass();
	            		item.setIcon(android.R.drawable.button_onoff_indicator_off);
	            	}
	            	else{
	            		myLocationOverlay.enableCompass();
	            		item.setIcon(android.R.drawable.button_onoff_indicator_on);
	            	}
	            	break;
	            case TapeMeasure.MENU_BACK_TO_LAST_LOCATION:
	                this.mapController.animateTo(getLastKnownPoint());
	                break;
	            case TapeMeasure.MENU_SATINFO:
	            	Intent isatinfo=new Intent(this, SatActivity.class);
	            	startActivity(isatinfo);
	            	break;
	            case TapeMeasure.MENU_CALCAREA:
	            	if(m_calcArea){
	            		item.setIcon(android.R.drawable.button_onoff_indicator_off);
	            		m_calcArea = false;
	            	}
	            	else{
	            		item.setIcon(android.R.drawable.button_onoff_indicator_on);
	            		m_calcArea = true;
	            	}
	            		
	            	break;
	        }
	        return super.onMenuItemSelected(featureId, item);
		  }
		
		@Override
		protected boolean isRouteDisplayed() {
			return false;
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
	                       Thread.sleep(1000); // 4x sec
	                       if(locmgr.getProvider(best)!=null){
		                       	 location = locmgr.getLastKnownLocation(best);
		                       	 if(location !=null){
		                       		dumplocation(location);
		                       		
		                       		if(lol != null){
		                       			lol.setLocation(location);
		                       			lol.setStartBMP(pointMarker);
		                       			lol.setEPBMP(pointMarker2);
		                       			lol.setStopBMP(pointMarker3);
		                       		}
		                       		if(!bstarpositionset){
		                       			if(lol != null)
		                       				lol.setStartLocation(location.getLatitude(),location.getLongitude());
		                       		}
		                       		if(m_locArrayLst != null){
		                       		//	calcLength(locArrayLst);
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
	 /*  	Double loclat = location.getLatitude()*1E6;
            Double loclng = location.getLongitude()*1E6;
            Double maxlat = -600.00*1E6;
            Double maxlng = -600.00*1E6;
            Double minlat = 600.00*1E6;
            Double minlng = 600.00*1E6;
            Location loc;
            if(!m_locArrayLst.isEmpty()){
				for (int j=0; j<m_locArrayLst.size(); j++) {
					loc = m_locArrayLst.get(j);
					if(maxlat < loc.getLatitude()*1E6)
						maxlat = loc.getLatitude()*1E6;
					if(maxlng < loc.getLongitude()*1E6)
						maxlng = loc.getLongitude()*1E6;
					if(minlat > loc.getLatitude()*1E6)
						minlat = loc.getLatitude()*1E6;
					if(minlng > loc.getLongitude()*1E6)
						minlng = loc.getLongitude()*1E6;
				}
				if(maxlat < location.getLatitude()*1E6)
					maxlat = location.getLatitude()*1E6;
				if(maxlng < location.getLongitude()*1E6)
					maxlng = location.getLongitude()*1E6;
				if(minlat > location.getLatitude()*1E6)
					minlat = location.getLatitude()*1E6;
				if(minlng > location.getLongitude()*1E6)
					minlng = location.getLongitude()*1E6;
            }
	    	if(m_centerPt==null){
	    		m_centerPt = new GeoPoint(loclat.intValue(),loclng.intValue());
	    	}
	    	else{
	    		if(!m_locArrayLst.isEmpty()){
		    		ArrayList<Location> locArrayLst2 = new ArrayList<Location>();
		    		for(int i=0; i<m_locArrayLst.size(); i++) {
		    			locArrayLst2.add(m_locArrayLst.get(i));
		    		}
		    		locArrayLst2.add(location);
		    		locArrayLst2.add(location);
		    		m_centerPt = CalcAreaCenterPoint(locArrayLst2);
	    		}
	    		else
	    			m_centerPt = new GeoPoint(loclat.intValue(),loclng.intValue());
	    	}
	    		
	    	if(!m_locArrayLst.isEmpty()){
	    		if(minlat != 600.00*1E6 || minlng != 600.00*1E6 || maxlat != -600.00*1E6 || maxlng != -600.00*1E6){
	    			int latSpanE6 = Math.abs(maxlat.intValue()-minlat.intValue())+3;
		            int lonSpanE6 = Math.abs(maxlng.intValue()-minlng.intValue())+3;
		            if(latSpanE6 != 0 ||lonSpanE6 != 0){
		            	int currentlatSpanE6 = mapView.getLatitudeSpan();
		            	int currentlonSpanE6 = mapView.getLatitudeSpan();
		            	
		            	if(latSpanE6 > currentlatSpanE6 || lonSpanE6 > currentlonSpanE6){
		            		int currentZoomLevel= mapView.getZoomLevel();
		            		this.mapController.setZoom(currentZoomLevel-1);
		            	}
		            	if(latSpanE6 < currentlatSpanE6 || lonSpanE6 < currentlonSpanE6){
		            		int currentZoomLevel= mapView.getZoomLevel();
		            		this.mapController.setZoom(currentZoomLevel +1);
		            	}
		            	isZoomLevelAutoSet = true;
		            }
		            this.mapController.setCenter(m_centerPt);
		    	}
	    	}
	    	else{
	    		if(isZoomLevelAutoSet == false)
	    			m_zoomLevel = this.mapController.setZoom(18);
	    	}*/
	    	
	    }
	}


