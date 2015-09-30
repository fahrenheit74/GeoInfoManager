package com.eleksheep.gim;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class LineOverlay extends Overlay{
//	private final int m_radius = 5;
	Location m_location;
	double m_splatitude; 
	double m_splongitude;
	double m_accuracy;
	private Drawable m_spbmp;
	private Drawable m_epbmp;
	private Drawable m_stopbmp;
	private ArrayList<Location>	m_locArrayLst;
	private boolean m_ismeters = false;
	private boolean m_display_lines = false;
	private boolean m_stoprec = false;
	private boolean m_closepoly =false;
	private boolean m_drawpolyclose = false;
	Point startPoint; 
	GeoPoint startGeoPoint;
	
	private ArrayList<Drawable> m_symList;
	
	public Location getLocation() {
	    return m_location;
	  }
	public void setLocation(Location location) {
	    this.m_location = location;
	  }
	public void setStop(boolean stoprec){
		m_stoprec = stoprec;
	}
	public void setClosePoly(boolean closepoly){
		m_closepoly = closepoly;
	}
	
	public void setLocArrayList(ArrayList<Location> locArrayLst){
		this.m_locArrayLst = new ArrayList<Location>();
		for(int j=0; j< locArrayLst.size(); j++)
		{
			m_locArrayLst.add(locArrayLst.get(j));
		}
	}
	public void setStartLocation(double latitude, double longitude){
		this.m_splatitude = latitude;
		this.m_splongitude = longitude;
	}
	public void setUnits(boolean isMeters){
		m_ismeters = isMeters;
	}
	public void setDisplayLines(boolean display_lines){
		m_display_lines = display_lines;
	}
	
	public void setStartBMP(Drawable spbmp){
		this.m_spbmp = spbmp;
	}
	public void setEPBMP(Drawable epbmp){
		this.m_epbmp = epbmp;
	}
	public void setStopBMP(Drawable stopbmp){
		this.m_stopbmp = stopbmp;
	}
	
	public void setSymlist(ArrayList<Drawable> symlist){
		this.m_symList = new ArrayList<Drawable>();
		for(int i=0; i<symlist.size(); i++){
			m_symList.add(symlist.get(i));
		}
	}
	
	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
	    return false;
	  }
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	    Projection projection = mapView.getProjection();
	    DecimalFormat onePlaces = new DecimalFormat("0.0");
	    DecimalFormat fourPlaces = new DecimalFormat("0.0000");
	    if(!m_display_lines)
	    	return;
	       
      if(this.m_location != null){
    	  Double latitude = this.m_location.getLatitude()*1E6;
	      Double longitude = this.m_location.getLongitude()*1E6;
	      GeoPoint geoPoint = new GeoPoint(latitude.intValue(),longitude.intValue());
	      
	      float[] distresults = new float[2];
	      Location.distanceBetween(geoPoint.getLatitudeE6()/1E6, geoPoint.getLongitudeE6()/1E6, m_splatitude, m_splongitude, distresults);
	      
	      // Convert the location to screen pixels     
	      Point point = new Point();
	      projection.toPixels(geoPoint, point);
	      	      
	      geoPoint = new GeoPoint((int)(m_splatitude*1E6),(int)(m_splongitude*1E6));
	      Point sPt = new Point();
	      projection.toPixels(geoPoint, sPt);
	      
	      // Setup the paint
	      Paint paint = new Paint();
	      paint.setARGB(250, 0, 0, 255);
	      paint.setAntiAlias(true);
	      paint.setFakeBoldText(true);
	      
	      Paint txtpaint = new Paint();
	      txtpaint.setARGB(255, 255, 0, 0);
	      txtpaint.setAntiAlias(true);
	      txtpaint.setFakeBoldText(true);
	      txtpaint.setTextSize(12);
	      txtpaint.setTextAlign(Align.CENTER);
	      
	      Paint txtbluepaint = new Paint();
	      txtbluepaint.setARGB(255, 0, 0, 255);
	      txtbluepaint.setAntiAlias(true);
	      txtbluepaint.setFakeBoldText(true);
	      txtbluepaint.setTextSize(12);
	      txtbluepaint.setTextAlign(Align.CENTER);

	      Paint backPaint = new Paint();
	      backPaint.setARGB(175, 50, 50, 50);
	      backPaint.setAntiAlias(true);
	      
	      Paint linePaint = new Paint();
	      linePaint.setARGB(255, 255, 0, 0);
	   	  linePaint.setStrokeWidth(3);
	   	  linePaint.setDither(true);
	   	  linePaint.setStyle(Style.STROKE);
	   	  linePaint.setAntiAlias(true);
	   	  linePaint.setStrokeJoin(Paint.Join.ROUND);
	   	  linePaint.setStrokeCap(Paint.Cap.ROUND);	   	 

	   	  Paint closelinePaint = new Paint();
	   	  closelinePaint.setARGB(255, 0, 0, 255);
	   	  closelinePaint.setStrokeWidth(3);
	   	  closelinePaint.setDither(true);
	   	  closelinePaint.setStyle(Style.STROKE);
	   	  closelinePaint.setAntiAlias(true);
	   	  closelinePaint.setStrokeJoin(Paint.Join.ROUND);
	   	  closelinePaint.setStrokeCap(Paint.Cap.ROUND);	  
	   	  
	   	if(m_locArrayLst != null){
	   		GeoPoint firstGeoPt;
    		Point firstPt; 
    		
    		if(m_locArrayLst.isEmpty())
    			return;
    		
    		Location loc = m_locArrayLst.get(0);
    		Double lat = loc.getLatitude()*1E6;
			Double lng = loc.getLongitude()*1E6;
    		firstGeoPt = new  GeoPoint(lat.intValue(),lng.intValue());
    		startGeoPoint = new  GeoPoint(lat.intValue(),lng.intValue());
    		firstPt = new Point();
			projection.toPixels(firstGeoPt,firstPt);
			startPoint = new Point();
			projection.toPixels(startGeoPoint,startPoint);
    		GeoPoint GeoPt;
    		int ht = m_spbmp.getIntrinsicHeight();
  	      	int wd = m_spbmp.getIntrinsicWidth();
  	      	int ht2 = m_epbmp.getIntrinsicHeight();
	      	int wd2= m_epbmp.getIntrinsicWidth();
    		m_spbmp.setBounds(firstPt.x-(int)(wd/2), firstPt.y-ht,
	  	      		firstPt.x + (int)(wd/2), firstPt.y);
	  	    m_spbmp.draw(canvas);
    		
	    	if(m_locArrayLst.size() > 1){
	    		if(m_stoprec && m_closepoly && m_drawpolyclose){
	    			Location locofFirstPt = m_locArrayLst.get(0);
	    			m_locArrayLst.add(locofFirstPt);
	    		}
	    	
	    		m_drawpolyclose=true; 
	    		for(int i=1; i<m_locArrayLst.size(); i++){
	    			loc = m_locArrayLst.get(i);
	    			lat = loc.getLatitude()*1E6;
	    			lng = loc.getLongitude()*1E6;
	    			GeoPt = new  GeoPoint(lat.intValue(),lng.intValue());
	    			Point pPt = new Point();
	    			projection.toPixels(GeoPt,pPt);
	    			Path pth2 = new Path();
	    		   	pth2.moveTo(firstPt.x,firstPt.y);
	    		   	pth2.lineTo(pPt.x, pPt.y);
	    		   	canvas.drawPath(pth2, linePaint);
	    		   	float[] distresults2 = new float[2];
	    		    Location.distanceBetween(GeoPt.getLatitudeE6()/1E6, GeoPt.getLongitudeE6()/1E6, 
	    		    		firstGeoPt.getLatitudeE6()/1E6, firstGeoPt.getLongitudeE6()/1E6, distresults2);
	    		    
	    		    double distance = distresults2[0];
	    		    double unitconversion = 0.0;
	          		String szUnits = " ft";
	          		String dist_string = onePlaces.format(distance*unitconversion)+" ft";
	          		if(m_ismeters){
	          			if(distance > 1000){  
	          				szUnits = " km";
	          				unitconversion = 0.001;
	          				dist_string = fourPlaces.format(distance*unitconversion)+ szUnits;
	          			}
	          			else{
	          				unitconversion = 1.0;
	          				szUnits = " m";
	          				dist_string = onePlaces.format(distance*unitconversion)+ szUnits;
	          			}
	          		}
	          		else{
	          			if(distance > 1609.000){ 
	          				szUnits = " mi";
	          				unitconversion = 0.000621371192;
	          				dist_string = fourPlaces.format(distance*unitconversion)+ szUnits;
	          			}
	          			else{
	          				unitconversion = 3.2808399;
	          				szUnits = " ft";
	          				dist_string = onePlaces.format(distance*unitconversion)+ szUnits;
	          			}
	          		}
	    	 		
	    	 		canvas.drawTextOnPath(dist_string, pth2, 0, 20, txtpaint);
	    			firstPt.x = pPt.x;
		            firstPt.y = pPt.y;
		            firstGeoPt = GeoPt;
		            if((m_stoprec)&&(i == m_locArrayLst.size()-1)){
		            	m_stopbmp.setBounds(pPt.x-(int)(wd2/2), pPt.y-ht2, pPt.x + (int)(wd2/2), pPt.y);
		            	m_stopbmp.draw(canvas);
		            }
		            else{
		            	m_epbmp.setBounds(pPt.x-(int)(wd2/2), pPt.y-ht2, pPt.x + (int)(wd2/2), pPt.y);
		            	m_epbmp.draw(canvas);
		            }
	    		}
	    	}
	    }
	   	if(m_stoprec == false){
		    // Draw the marker    
		   	Path pth = new Path();
		   	DashPathEffect dashPath = new DashPathEffect(new float[]{5, 5}, 1);
		   	linePaint.setPathEffect(dashPath);
		   	pth.moveTo(sPt.x,sPt.y);
		   	pth.lineTo(point.x, point.y);
		   	canvas.drawPath(pth, linePaint);
		   	
		   	Path pth2 = new Path();
		   	closelinePaint.setPathEffect(dashPath);
		   	pth2.moveTo(point.x, point.y);
		   	float[] distresults3 = new float[2];
		   	   distresults3[0] = 0;
		   	   distresults3[1] = 0;
		   	if(startPoint !=null){
		   		pth2.lineTo(startPoint.x, startPoint.y);
		   		if(m_closepoly && m_drawpolyclose)
		   			canvas.drawPath(pth2, closelinePaint);
			    Location.distanceBetween(startGeoPoint.getLatitudeE6()/1E6, startGeoPoint.getLongitudeE6()/1E6, m_location.getLatitude(), m_location.getLongitude(), distresults3);
		   	}
	     
	 		 double distance = distresults[0];
	 		 double distance_tostart = distresults3[0];
			 double unitconversion = 1.0;
	   		  String szUnits = " ft";
	   		  String dist_string = onePlaces.format(distance*unitconversion) +" ft";
	   		  String dist_tostart_string = onePlaces.format(distance_tostart*unitconversion) +" ft";
	   		  if(m_ismeters){
	   			if(distance > 1000){  
	   				szUnits = " km";
	   				unitconversion = 0.001;
	   				dist_string = fourPlaces.format(distance*unitconversion)+ szUnits;
	   				dist_tostart_string = fourPlaces.format(distance_tostart*unitconversion)+ szUnits;
	   			}
	   			else{
	   				unitconversion = 1.0;
	   				szUnits = " m";
	   				dist_string = onePlaces.format(distance*unitconversion)+ szUnits;
	   				dist_tostart_string = onePlaces.format(distance_tostart*unitconversion)+ szUnits;
	   			}
	   		  }
	   		  else{
	   			if(distance > 1609.000){ 
	   				szUnits = " mi";
	   				unitconversion = 0.000621371192;
	   				dist_string = fourPlaces.format(distance*unitconversion)+ szUnits;
	   				dist_tostart_string = fourPlaces.format(distance_tostart*unitconversion)+ szUnits;
	   			}
	   			else{
	   				unitconversion = 3.2808399;
	   				szUnits = " ft";
	   				dist_string = onePlaces.format(distance*unitconversion)+ szUnits;
	   				dist_tostart_string = onePlaces.format(distance_tostart*unitconversion)+ szUnits;
	   			}
	   		  }
	   		  if(distance > 10)
	   			  canvas.drawTextOnPath(dist_string, pth, 0, 20, txtpaint);
	   		  if(distance_tostart > 10){
	   			  if(m_closepoly && m_drawpolyclose)
	   				canvas.drawTextOnPath(dist_tostart_string, pth2, 0, 20, txtbluepaint);
	   		  }
		   }
	    }
	  }
}
