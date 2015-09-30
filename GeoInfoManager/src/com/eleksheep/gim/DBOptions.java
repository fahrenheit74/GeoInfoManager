package com.eleksheep.gim;

import java.text.DecimalFormat;

import com.eleksheep.gim.R;
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class DBOptions extends Activity {

	private double dlat = 0.0;
	private double dlng = 0.0;
	private int symbol = 0;
	private boolean delete_rec = false;
	private String imgname = "";
	private String ptname = "";
	private String addstreet = "";
	private String addcity = "";
	private String addstate = "";
	private String addpostalcode = "";
	private String addcountry = "";
	private String description = "";
	
	private static final String KEYDELETEREC = "KEY_DELETE_RECORD";
	private static final String KEYPTNAME = "KEY_PTNAME";
	private static final String KEYLAT = "KEY_LAT";
	private static final String KEYLNG = "KEY_LNG";
	private static final String KEYSYMBOL = "KEY_SYMBOL";
	private static final String KEYIMAGENAME = "KEY_IMAGE_NAME";
	public static final String KEY_UNITS = "KEY_UNITS";
	public static final String KEY_PROJ_STRING = "KEY_PROJSTRING";
	
   private static final String KEYDESCR = "KEY_DESCR";
   private static final String KEYADDSTREET = "KEY_ADD_STREET";
   private static final String KEYADDCITY = "KEY_ADD_CITY";
   private static final String KEYADDSTATE = "KEY_ADD_STATE";
   private static final String KEYADDPOSTAL = "KEY_ADD_POSTAL";
   private static final String KEYADDCOUNTRY = "KEY_ADD_COUNTRY";
	
	Projection projection;
	private final String defprojstr = "+proj=utm, +zone=10, +ellps=GRS80,+ellps=GRS80,+towgs84=0,0,0,0,0,0,0,+units=m,+no_defs";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_options);

		final ImageButton editDBRecordbtn = (ImageButton) findViewById(R.id.dbopt_editdb);
		final ImageButton deleteDBRecordbtn = (ImageButton) findViewById(R.id.dbopt_deletedb);
		//final ImageButton clearDBbtn = (ImageButton) findViewById(R.id.dbopt_cleardb);
		final ImageButton sendMessagebtn = (ImageButton)findViewById(R.id.dbopt_sendmessage);
		final ImageButton sendEmailbtn = (ImageButton)findViewById(R.id.dbopt_sendemail);
		final ImageButton googlenavbtn = (ImageButton) findViewById(R.id.dbopt_googlenav);
		final ImageButton googlestreetviewbtn = (ImageButton) findViewById(R.id.dbopt_googlestreetview);
		final ImageButton navdashbtn = (ImageButton) findViewById(R.id.dbopt_navdash);
		
		final Bundle bun = getIntent().getExtras();
		dlng = bun.getDouble(KEYLNG);
		dlat = bun.getDouble(KEYLAT);
		ptname = bun.getString(KEYPTNAME);
		imgname = bun.getString(KEYIMAGENAME);
		symbol = bun.getInt(KEYSYMBOL);
		description = bun.getString(KEYDESCR);
		addstreet = bun.getString(KEYADDSTREET);
		addcity = bun.getString(KEYADDCITY);
		addstate = bun.getString(KEYADDSTATE);
		addcountry = bun.getString(KEYADDCOUNTRY);
		addpostalcode = bun.getString(KEYADDPOSTAL);

		editDBRecordbtn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){
	    		AlertDialog ad= new AlertDialog.Builder(DBOptions.this).create();
	             ad.setTitle("Question");
	             ad.setMessage("Do you want to View and Edit this Point?\n" + ptname );
	             ad.setButton("OK",new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int which) {
	                	Toast.makeText(DBOptions.this, "Edit Point", Toast.LENGTH_SHORT).show();
	                	Intent i = new Intent(DBOptions.this,PointEditor.class);
	     	    		Bundle bun = new Bundle();
	     	    		bun.putInt(KEYSYMBOL, symbol);
	     	    		bun.putDouble(KEYLAT, dlat);
	     	    		bun.putDouble(KEYLNG, dlng);
	     	    		bun.putString(KEYPTNAME, ptname);
	     	    		bun.putString(KEYIMAGENAME, imgname);
	     	    		bun.putString(KEYDESCR, description);
	     	    		bun.putString(KEYADDSTREET, addstreet);
	     	    		bun.putString(KEYADDCITY, addcity);
	     	    		bun.putString(KEYADDSTATE, addstate);
	     	    		bun.putString(KEYADDCOUNTRY, addcountry);
	     	    		bun.putString(KEYADDPOSTAL, addpostalcode);
	     	    		i.putExtras(bun);
	     	    		startActivity(i);
	     	    		finish();
	                     return;
	                   } }); 
	             ad.setButton2("Cancel", new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int which) {
	                	 Toast.makeText(DBOptions.this, "Point Not Edited. ", Toast.LENGTH_SHORT).show();
	                	finish();
	                   return;
	                 }}); 
	             ad.show();
	    	//	Bundle bun = new Bundle();
	    	//	bun.putDouble(KEYLNG,dlng);
	    	//	bun.putDouble(KEYLAT,dlat);
	    	//	bun.putInt(KEYSYMBOL,symbol);
	    	//	Intent istakeout = new Intent(DBOptions.this,StakeOutView.class);
	    	//	istakeout.putExtras(bun);
	    	//	startActivity(istakeout);
	    	}
	    });
		
		deleteDBRecordbtn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){
	    		AlertDialog ad= new AlertDialog.Builder(DBOptions.this).create();
	            ad.setTitle("Question");
	            ad.setMessage("Do you want to delete this Point?\n" + ptname );
	            ad.setButton("OK",new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int which) {
	                	 Intent intent = new Intent();
	     	             Bundle bun = new Bundle();
	     	             delete_rec = true;
	     	             bun.putBoolean(KEYDELETEREC, delete_rec);
	     				 intent.putExtras(bun);
	     				 setResult(RESULT_OK,intent);
	                	// Toast.makeText(DBOptions.this, "Point Deleted", Toast.LENGTH_SHORT).show();
	                	 finish();
	                    return;
	                   } }); 
	             ad.setButton2("Cancel", new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int which) {
	                	 Toast.makeText(DBOptions.this, "Point not Deleted", Toast.LENGTH_SHORT).show();
	                	 finish();
	                   return;
	                 }}); 
	             ad.show();
	    	}
	    });
		
		sendMessagebtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				SharedPreferences mPrefs;
					mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
					boolean isMeters = mPrefs.getBoolean(KEY_UNITS, false);
					String szProjString = mPrefs.getString(TestProj1.KEY_PROJ_STRING, defprojstr);
			        String delim2 = ",";
			        String[] tokens2 = szProjString.split(delim2);
			        projection = ProjectionFactory.fromPROJ4Specification(tokens2);
					
			        Point2D.Double src = new Point2D.Double();
			        Point2D.Double dst = new Point2D.Double();
			        src.x = dlng;
			        src.y = dlat;
			      	String szprojname = projection.getName().toString();
			      	String dx = ""; String dy = "";
			      	if(szprojname.length()>0){
			      		projection.transform(src,dst);
			      		if(isMeters == false)
			      			MapMath.MeterstoFeet(true, dst);
			      		DecimalFormat twoPlaces = new DecimalFormat("0.00");
			      		dx = twoPlaces.format(dst.x);
				   		dy = twoPlaces.format(dst.y);
			      	}
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
	    		String url = "Location: " + ptname + "\n";
	    		url = url + Location.convert(dlat, Location.FORMAT_SECONDS) + ",";
	    		url = url + Location.convert(dlng, Location.FORMAT_SECONDS) + "\n";
		    	url = url + dx + "," + dy + "\n";
	    		url = url + "http://maps.google.com/maps?q=" + dlat + "," + dlng + "\n"; 
	    		sendIntent.putExtra("sms_body", url); 
	            sendIntent.setType("vnd.android-dir/mms-sms");
	            startActivity(sendIntent);
	    		finish();
			}
		});
		sendEmailbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				SharedPreferences mPrefs;
				mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
				boolean isMeters = mPrefs.getBoolean(KEY_UNITS, false);
				String szProjString = mPrefs.getString(TestProj1.KEY_PROJ_STRING, defprojstr);
		        String delim2 = ",";
		        String[] tokens2 = szProjString.split(delim2);
		        projection = ProjectionFactory.fromPROJ4Specification(tokens2);
				
		        Point2D.Double src = new Point2D.Double();
		        Point2D.Double dst = new Point2D.Double();
		        src.x = dlng;
		        src.y = dlat;
		      	String szprojname = projection.getName().toString();
		      	String dx = ""; String dy = "";
		      	if(szprojname.length()>0){
		      		projection.transform(src,dst);
		      		if(isMeters == false)
		      			MapMath.MeterstoFeet(true, dst);
		      		DecimalFormat twoPlaces = new DecimalFormat("0.00");
		      		dx = twoPlaces.format(dst.x);
			   		dy = twoPlaces.format(dst.y);
		      	}
				
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.setType("text/html");
				String szLocation = "";
				if(ptname.length() > 0)
					szLocation = ptname;
				else
				szLocation = "Location";
				String url = "Location: " + ptname + "\n";
				url = url + Location.convert(dlat, Location.FORMAT_SECONDS) + ",";
	    		url = url + Location.convert(dlng, Location.FORMAT_SECONDS) + "\n";
	    		url = url + dx + "," + dy + "\n";
	    		//url = url + "http://maps.google.com/maps?q=38.39,-83.45";
	    		url = url + "<a href=\"http://maps.google.com/maps?q=" + dlat + "," + dlng + "\">Google map link"; 
	    		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,url);
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, szLocation);
				
				emailIntent.setType("image/jpg");
				emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + imgname));
				
				startActivity(Intent.createChooser(emailIntent, "Email:"));
	    		finish();
			}
		});
		
		googlenavbtn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){

	    		String url = "google.navigation:q="+dlat+","+dlng;
	 		    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));            
	 		    startActivity(i);
	    		finish();
	    	}
	    });
		
		googlestreetviewbtn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){

	    		String url = "google.streetview:cbll="+dlat+","+dlng+"&cbp=1,180,,0,1.0";
	 		    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));            
	 		    startActivity(i);
	    		finish();
	    	}
	    });
		
		navdashbtn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){
	    		Intent i = new Intent(DBOptions.this,NavigateGPS.class);
	    		Bundle bun = new Bundle();
	    		bun.putInt(KEYSYMBOL, symbol);
	    		bun.putDouble(KEYLAT, dlat);
	    		bun.putDouble(KEYLNG, dlng);
	    		bun.putString(KEYPTNAME, ptname);
	    		i.putExtras(bun);
	    		startActivity(i);
	    		finish();
	    	}
	    });
		
	}
}
