package com.eleksheep.gim;

import java.text.DecimalFormat;

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
import com.jhlabs.map.MapMath;

public class StakeOverlay extends Overlay{
	
	Location m_location = null;
	double m_splatitude = -180.00; 
	double m_splongitude = -600.00;
	boolean m_ismeters =false;
	private Drawable m_stopbmp=null;
	
	public void setLocation(Location location) {
	    this.m_location = location;
	  }
	public void setUnits(boolean isMeters){
		m_ismeters = isMeters;
	}
	public void setDestination(double latitude, double longitude){
		this.m_splatitude = latitude;
		this.m_splongitude = longitude;
	}
	public void setStopBMP(Drawable stopbmp){
		this.m_stopbmp = stopbmp;
	}
	
	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
	    return false;
	  }
	
	public String distanceString(boolean ismeters, double distance){
		double unitconversion = 0.0;
		DecimalFormat onePlaces = new DecimalFormat("0.0");
	    DecimalFormat fourPlaces = new DecimalFormat("0.0000");
  		String szUnits = " ft";
  		String dist_string = onePlaces.format(distance*unitconversion)+" ft";
  		if(ismeters){
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
  		return dist_string;
	}
	public int pixdistance(Point pt1, Point pt2){
		int distance = 0;
		int distx = Math.abs(pt1.x-pt2.x);
		int disty = Math.abs(pt1.y-pt2.y);
		distance = (int)Math.sqrt((distx*distx)+(disty*disty));
		return distance;
	}
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	    Projection projection = mapView.getProjection();
	 
	    if(this.m_location == null)
	    	return;
	    if(m_splatitude == -180.00 && m_splongitude == -600.00)
	    	return;
	    
	    Paint txtpaint = new Paint();
        txtpaint.setARGB(255, 255, 0, 0);
        txtpaint.setAntiAlias(true);
        txtpaint.setFakeBoldText(true);
        txtpaint.setTextSize(12);
        txtpaint.setTextAlign(Align.CENTER);
        
        Paint bluetxtpaint = new Paint();
        bluetxtpaint.setARGB(255, 0, 0, 255);
        bluetxtpaint.setAntiAlias(true);
        bluetxtpaint.setFakeBoldText(true);
        bluetxtpaint.setTextSize(12);
        bluetxtpaint.setTextAlign(Align.CENTER);
        
        Paint redlinePaint = new Paint();
        redlinePaint.setARGB(255, 255, 0, 0);  //Red line
        redlinePaint.setStrokeWidth(3);
        redlinePaint.setDither(true);
        redlinePaint.setStyle(Style.STROKE);
        redlinePaint.setAntiAlias(true);
        redlinePaint.setStrokeJoin(Paint.Join.ROUND);
        redlinePaint.setStrokeCap(Paint.Cap.ROUND);	   
        
        Paint bluelinePaint = new Paint();
	   	bluelinePaint.setARGB(255, 0, 0, 255);
	   	bluelinePaint.setStrokeWidth(3);
	   	bluelinePaint.setDither(true);
	   	bluelinePaint.setStyle(Style.STROKE);
	   	bluelinePaint.setAntiAlias(true);
	   	bluelinePaint.setStrokeJoin(Paint.Join.ROUND);
	   	bluelinePaint.setStrokeCap(Paint.Cap.ROUND);	
	   	
	   	DashPathEffect dashPath = new DashPathEffect(new float[]{5, 5}, 1);
	   	bluelinePaint.setPathEffect(dashPath);
	   	
	   	//double degbearings= MapMath.bearing(m_location.getLatitude(), m_location.getLongitude(), m_splatitude, m_splongitude);
	   	//String strbearing = "Bearing: " + fourPlaces.format(degbearings)+"°";
	   	
	   	GeoPoint dest_geoPoint = new GeoPoint((int)(m_splatitude*1E6),(int)(m_splongitude*1E6));
	   	Point destPt = new Point();
	    projection.toPixels(dest_geoPoint, destPt);
	    
	    if(m_stopbmp !=null){
	    	m_stopbmp.setBounds(destPt.x-(int)(m_stopbmp.getIntrinsicWidth()/2), destPt.y-m_stopbmp.getIntrinsicHeight(), 
	    			destPt.x + (int)(m_stopbmp.getIntrinsicWidth()/2), destPt.y);
        	m_stopbmp.draw(canvas);
	    }
	    
	    Double lat = m_location.getLatitude()*1E6;
	    Double lng = m_location.getLongitude()*1E6;
	    GeoPoint startGeoPoint = new  GeoPoint(lat.intValue(),lng.intValue());
	    Point startPt = new Point();
	    projection.toPixels(startGeoPoint, startPt);
	    
		float[] distresults = new float[2];
	    Location.distanceBetween(startGeoPoint.getLatitudeE6()/1E6, startGeoPoint.getLongitudeE6()/1E6, 
	    		dest_geoPoint.getLatitudeE6()/1E6, dest_geoPoint.getLongitudeE6()/1E6, distresults);
	    double distance = distresults[0];
	    
	    Path pth1 = new Path();
	   	pth1.moveTo(startPt.x,startPt.y);
	   	pth1.lineTo(destPt.x, destPt.y);
	   	canvas.drawPath(pth1, redlinePaint);
	   	
	   	if(pixdistance(startPt,destPt)>20)
	   		canvas.drawTextOnPath(distanceString(m_ismeters, distance), pth1, 0, 20, txtpaint);
	   	
	   	Location.distanceBetween(startGeoPoint.getLatitudeE6()/1E6, startGeoPoint.getLongitudeE6()/1E6, 
	   			startGeoPoint.getLatitudeE6()/1E6, dest_geoPoint.getLongitudeE6()/1E6, distresults);
	   	distance = distresults[0];
	   	
	   	Path pth2 = new Path();
	   	pth2.moveTo(startPt.x, startPt.y);
	   	pth2.lineTo(destPt.x, startPt.y);
	   	canvas.drawPath(pth2, bluelinePaint);
	   	Point pt2 = new Point();
	   	pt2.x = destPt.x;
	   	pt2.y = startPt.y;
	   	
	   	if(pixdistance(startPt,pt2)>20)
	   		canvas.drawTextOnPath(distanceString(m_ismeters, distance), pth2, 0, 20, bluetxtpaint);
	   	
		Location.distanceBetween(dest_geoPoint.getLatitudeE6()/1E6, dest_geoPoint.getLongitudeE6()/1E6, 
	   			startGeoPoint.getLatitudeE6()/1E6, dest_geoPoint.getLongitudeE6()/1E6, distresults);
	   	distance = distresults[0];
	   	
	   	Path pth3 = new Path();
	   	pth3.moveTo(destPt.x, destPt.y);
	   	pth3.lineTo(destPt.x, startPt.y);
	   	canvas.drawPath(pth3, bluelinePaint);
	   	pt2.x = destPt.x;
	   	pt2.y = startPt.y;
	   	if(pixdistance(destPt,pt2)>20)
	   		canvas.drawTextOnPath(distanceString(m_ismeters, distance), pth3, 0, 20, bluetxtpaint);
	   	
	   	Location.distanceBetween(startGeoPoint.getLatitudeE6()/1E6, startGeoPoint.getLongitudeE6()/1E6,
	   			dest_geoPoint.getLatitudeE6()/1E6, startGeoPoint.getLongitudeE6()/1E6, distresults);
	   	distance = distresults[0];
	   	
	   	Path pth4 = new Path();
	   	pth4.moveTo(startPt.x, startPt.y);
	   	pth4.lineTo(startPt.x, destPt.y);
	   	canvas.drawPath(pth4, bluelinePaint);
	   	pt2.x = startPt.x;
	   	pt2.y = destPt.y;
	   	if(pixdistance(startPt,pt2)>20)
	   		canvas.drawTextOnPath(distanceString(m_ismeters, distance), pth4, 0, 20, bluetxtpaint);
	   	
	   	Location.distanceBetween(dest_geoPoint.getLatitudeE6()/1E6, dest_geoPoint.getLongitudeE6()/1E6, 
	   			dest_geoPoint.getLatitudeE6()/1E6, startGeoPoint.getLongitudeE6()/1E6, distresults);
	   	distance = distresults[0];
	   	
	   	Path pth5 = new Path();
	   	pth5.moveTo(destPt.x, destPt.y);
	   	pth5.lineTo(startPt.x, destPt.y);
	   	canvas.drawPath(pth5, bluelinePaint);
	   	pt2.x = startPt.x;
	   	pt2.y = destPt.y;
	   	if(pixdistance(destPt,pt2)>20)
	   		canvas.drawTextOnPath(distanceString(m_ismeters, distance), pth5, 0, 20, bluetxtpaint);
	}


}
