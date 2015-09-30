package com.eleksheep.gim;


import com.eleksheep.gim.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class main extends Activity {
    private static final int MENU_SET_HINTS = 1;
    private static final int MENU_SET_ABOUT = 2;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        
        final ImageButton CoordSystemBtn = (ImageButton) findViewById(R.id.coordsystem_button);
    	final ImageButton GPXBtn = (ImageButton) findViewById(R.id.gpx_file_button);
    	final ImageButton LocBtn = (ImageButton) findViewById(R.id.location_button);
    	final ImageButton DBBtn = (ImageButton) findViewById(R.id.database_button);
    	final ImageButton TapeBtn = (ImageButton) findViewById(R.id.gps_tapemeasure_button);
    	
    	GPXBtn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v) {
	    		Intent readgpxfile = new Intent(main.this,ReadGPXFile.class);
				startActivity(readgpxfile);
	    	}
    	});
    	CoordSystemBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		Intent geocoord_intent=new Intent(main.this, TestProj1.class);
	    		startActivity(geocoord_intent);
        	}
        });
    	
    	LocBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		Intent location_intent=new Intent(main.this, MapViewActivity.class);
	    		startActivity(location_intent);
        	}
        });
    	DBBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		Intent iviewdb = new Intent(main.this,ViewPointDB.class);
	    		startActivity(iviewdb);
        	}
        });
    	TapeBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		Intent itape = new Intent(main.this,TapeMeasure.class);
	    		startActivity(itape);
        	}
        });
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, main.MENU_SET_ABOUT, 0, "About").setIcon(android.R.drawable.ic_menu_info_details);
        menu.add(0, main.MENU_SET_HINTS, 0, "Helpful Hints").setIcon(android.R.drawable.ic_menu_help);

        return true;
    }
    
    @Override
	 public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
       switch (item.getItemId()) {
           case main.MENU_SET_ABOUT:
        	   Intent iabout = new Intent(this,About.class);
	   		   startActivity(iabout);
              break;
           case main.MENU_SET_HINTS:
        	   	Intent ihints = new Intent(this,Hints.class);
   				startActivity(ihints);
              break;
       }
       return super.onMenuItemSelected(featureId, item);
	  }
    
}
