package com.eleksheep.gim;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleksheep.gim.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PtOverlay extends Overlay{

	private ArrayList<Drawable> m_symList;
	private ArrayList<Location>	m_locArrayLst;
	private ArrayList<PtLocMark>m_ptlocmarkerLst;
	private Context m_context;
	
	public PtOverlay(final Context context){
		m_context = context;
	}
	
	public void setSymlist(ArrayList<Drawable> symArrayList){
		this.m_symList = new ArrayList<Drawable>();
		for(int i=0; i<symArrayList.size(); i++){
			m_symList.add(symArrayList.get(i));
		}
	}
	public void setLocArrayList(ArrayList<Location> locArrayLst){
		this.m_locArrayLst = new ArrayList<Location>();
		for(int j=0; j< locArrayLst.size(); j++)
		{
			m_locArrayLst.add(locArrayLst.get(j));
		}
	}
	public void setPtLocMarkers(ArrayList<PtLocMark> ptlocmarker){
		this.m_ptlocmarkerLst = new ArrayList<PtLocMark>();
		for(int j=0; j< ptlocmarker.size(); j++)
		{
			m_ptlocmarkerLst.add(ptlocmarker.get(j));
		}
	}
	
	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		Projection projection = mapView.getProjection();
		if(m_ptlocmarkerLst != null){
			if(m_ptlocmarkerLst.isEmpty())
    			return false;
			if(m_ptlocmarkerLst.size() >= 0){
				for(int i=0; i<m_ptlocmarkerLst.size(); i++){
	    			PtLocMark ptlocmark = m_ptlocmarkerLst.get(i);
	    			Double lat = ptlocmark.m_latitude*1E6;
	    			Double lng = ptlocmark.m_longitude*1E6;
	    			GeoPoint geoPt= new  GeoPoint(lat.intValue(),lng.intValue());
	    			
	    			Point pt1 = new Point();
	    			Point pt2 = new Point();
	    			projection.toPixels(geoPt, pt1);
	    			projection.toPixels(point, pt2);
	    			Drawable marker = this.m_context.getResources().getDrawable(ptlocmark.getMarker());
	    			int ht = marker.getIntrinsicHeight();
	      	      	int wd = marker.getIntrinsicWidth();
	    			
	    			if((Math.abs(pt1.x - pt2.x))<wd/2 && (Math.abs(pt1.y - pt2.y))<ht/2)
	    			{
	    				LayoutInflater inflater = LayoutInflater.from(this.m_context);
	    				View bView = inflater.inflate(R.layout.pointinfo, null);
	    				TextView title = (TextView) bView.findViewById(R.id.point_name);
	    				ImageView ptinfoSymbol = (ImageView)bView.findViewById(R.id.ptinfo_symbol);
	    				TextView datetime = (TextView)bView.findViewById(R.id.point_date);
	    				String szDateTime = "Date: " + ptlocmark.m_timestr;
	    				datetime.setText(szDateTime);
	    		        title.setText(ptlocmark.m_name);
	    		        //TextView date = (TextView) bView.findViewById(R.id.point_date);
	    		        //date.setText(ptdata.dateString);
	    		        TextView ptlat = (TextView) bView.findViewById(R.id.point_lat);
	    		        ptlat.setText(Location.convert((ptlocmark.m_latitude), Location.FORMAT_SECONDS));
	    		        TextView ptlng = (TextView) bView.findViewById(R.id.point_lng);
	    		        ptlng.setText(Location.convert((ptlocmark.m_longitude), Location.FORMAT_SECONDS));
	    		        ptinfoSymbol.setImageResource(ptlocmark.getMarker());
	    		        TextView addStreet = (TextView) bView.findViewById(R.id.ptinfo_addstreet);
	    		        TextView addStreet_lab = (TextView) bView.findViewById(R.id.ptinfo_addstreet_lab);
	    		        TextView addCity = (TextView) bView.findViewById(R.id.ptinfo_addcity);
	    		        TextView addCity_lab = (TextView) bView.findViewById(R.id.ptinfo_addcity_lab);
	    		        TextView addState = (TextView) bView.findViewById(R.id.ptinfo_addstate);
	    		        TextView addState_lab = (TextView) bView.findViewById(R.id.ptinfo_addstate_lab);
	    		        TextView addCountry = (TextView) bView.findViewById(R.id.ptinfo_addcountry);
	    		        TextView addCountry_lab = (TextView) bView.findViewById(R.id.ptinfo_addcountry_lab);
	    		        TextView addPostalCode = (TextView) bView.findViewById(R.id.ptinfo_addpostalcode);
	    		        TextView addPostalCode_lab = (TextView) bView.findViewById(R.id.ptinfo_addpostalcode_lab);
	    		        ImageView ptImage = (ImageView)bView.findViewById(R.id.ptinfo_image);
	    		        
	    		        addStreet.setVisibility(View.GONE);
	    		        addStreet_lab.setVisibility(View.GONE);
	    		        addCity.setVisibility(View.GONE);
	    		        addCity_lab.setVisibility(View.GONE);
	    		        addState.setVisibility(View.GONE);
	    		        addState_lab.setVisibility(View.GONE);
	    		        addPostalCode.setVisibility(View.GONE);
	    		        addPostalCode_lab.setVisibility(View.GONE);
	    		        addCountry.setVisibility(View.GONE);
	    		        addCountry_lab.setVisibility(View.GONE);
	    		
	    		        if(ptlocmark.m_addStreet.length() > 0){
	    		        	addStreet.setVisibility(View.VISIBLE);
	    		        	addStreet_lab.setVisibility(View.VISIBLE);
	    		        	addStreet.setText(ptlocmark.m_addStreet);
	    		        }
	    		        if(ptlocmark.m_addCity.length() > 0){
	    		        	addCity.setVisibility(View.VISIBLE);
	    		        	addCity_lab.setVisibility(View.VISIBLE);
	    		        	addCity.setText(ptlocmark.m_addCity);
	    		        }
	    		        if(ptlocmark.m_addState.length() > 0){
	    		        	addState.setVisibility(View.VISIBLE);
	    		        	addState_lab.setVisibility(View.VISIBLE);
	    		        	addState.setText(ptlocmark.m_addState);
	    		        }
	    		        if(ptlocmark.m_addPostalCode.length() > 0){
	    		        	addPostalCode.setVisibility(View.VISIBLE);
	    		        	addPostalCode_lab.setVisibility(View.VISIBLE);
	    		        	addPostalCode.setText(ptlocmark.m_addPostalCode);
	    		        }
	    		        if(ptlocmark.m_addCountry.length() > 0){
	    		        	addCountry.setVisibility(View.VISIBLE);
	    		        	addCountry_lab.setVisibility(View.VISIBLE);
	    		        	addCountry.setText(ptlocmark.m_addCountry);
	    		        }
	    		        if(ptlocmark.m_imagepath.length() > 0){
	    		        	BitmapFactory.Options resample = new BitmapFactory.Options();
	    					resample.inSampleSize = 16;

	    		        	Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+ptlocmark.m_imagepath,resample);
	    		        	ptImage.setImageBitmap(bmp);
	    		        }
	    				new AlertDialog.Builder(this.m_context).setView(bView).setPositiveButton("Ok",
    		                new DialogInterface.OnClickListener() {
    							@Override
    							public void onClick(DialogInterface di, int which) {
    								di.dismiss();}
    		                }).show();
	    				return true;
	    			}
				}
			}
		}
	    return false;
	  }

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	    Projection projection = mapView.getProjection();
	       
	   	if(m_ptlocmarkerLst != null){

    		if(m_ptlocmarkerLst.isEmpty())
    			return;

	    	if(m_ptlocmarkerLst.size() >= 0){
	    	
	    		for(int i=0; i<m_ptlocmarkerLst.size(); i++){
	    			PtLocMark ptlocmark = m_ptlocmarkerLst.get(i);
	    			Double lat = ptlocmark.m_latitude*1E6;
	    			Double lng = ptlocmark.m_longitude*1E6;
	    			GeoPoint geoPt= new  GeoPoint(lat.intValue(),lng.intValue());
	    			Drawable marker = this.m_context.getResources().getDrawable(ptlocmark.getMarker());
	    			Point pPt = new Point();
	    			projection.toPixels(geoPt,pPt);

	    			int ht = marker.getIntrinsicHeight();
	      	      	int wd = marker.getIntrinsicWidth();
	      	      	marker.setBounds(pPt.x-(int)(wd/2), pPt.y-(int)(ht/2), pPt.x + (int)(wd/2), pPt.y + (int)(ht/2));
	      	      	marker.draw(canvas);
	    		}
	    	}
	    }
	    super.draw(canvas, mapView, shadow);
	  }
}
