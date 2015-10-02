package com.eleksheep.gim;

import java.util.ArrayList;
import java.util.Iterator;

import com.eleksheep.gim.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

public class SatActivity extends Activity implements LocationListener {
     protected static final int GUIUPDATEIDENTIFIER = 0x101;
     private LocationManager locmgr;
	 private Location location = null;
	 private static final int MENU_TOGGLEGPS = 1;
	 
	 ArrayList<CurrentSat> cSats = new ArrayList<CurrentSat>();
	 private String best;

     Thread myRefreshThread = null;

     /* Our 'ball' is located within this View */
     SatView satview = null;

     Handler myGUIUpdateHandler = new Handler() {

          // @Override
          public void handleMessage(Message msg) {
               switch (msg.what) {
                    case SatActivity.GUIUPDATEIDENTIFIER:
                         /* Repaint the BounceView
                          * (where the ball is in) */
                         satview.invalidate();
                         break;
               }
               super.handleMessage(msg);
          }
     };
     

     /** Called when the activity is first created. */
     @Override
     public void onCreate(Bundle icicle) {
          super.onCreate(icicle);
          // Set fullscreen
          this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
          locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

          // Create a
          this.satview = new SatView(this);
          this.setContentView(this.satview);
          
          CurrentSat cSat = new CurrentSat();
          cSat.SetPrn(3);
          cSat.SetAzi(180);
          cSat.SetElv(30);
          cSat.SetSNR(50);
          cSat.SetUsed(false);
          satview.cSats.add(cSat);
          
          cSat = new CurrentSat();
          cSat.SetPrn(4);
          cSat.SetAzi(270);
          cSat.SetElv(45);
          cSat.SetSNR(50);
          cSat.SetUsed(true);
          satview.cSats.add(cSat);
          
          cSat = new CurrentSat();
          cSat.SetPrn(5);
          cSat.SetAzi(90);
          cSat.SetElv(60);
          cSat.SetSNR(26);
          cSat.SetUsed(true);
          satview.cSats.add(cSat);
          
          cSat = new CurrentSat();
          cSat.SetPrn(6);
          cSat.SetAzi(360);
          cSat.SetElv(15);
          cSat.SetSNR(50);
          cSat.SetUsed(true);
          satview.cSats.add(cSat);
          
          cSat.SetPrn(7);
          cSat.SetAzi(150);
          cSat.SetElv(30);
          cSat.SetSNR(50);
          cSat.SetUsed(false);
          satview.cSats.add(cSat);
          
          cSat = new CurrentSat();
          cSat.SetPrn(8);
          cSat.SetAzi(245);
          cSat.SetElv(45);
          cSat.SetSNR(30);
          cSat.SetUsed(true);
          satview.cSats.add(cSat);
          
          cSat = new CurrentSat();
          cSat.SetPrn(9);
          cSat.SetAzi(120);
          cSat.SetElv(60);
          cSat.SetSNR(50);
          cSat.SetUsed(true);
          satview.cSats.add(cSat);
          
          cSat = new CurrentSat();
          cSat.SetPrn(10);
          cSat.SetAzi(330);
          cSat.SetElv(20);
          cSat.SetSNR(50);
          cSat.SetUsed(true);
          satview.cSats.add(cSat);
          
          cSat.SetPrn(11);
          cSat.SetAzi(36);
          cSat.SetElv(90);
          cSat.SetSNR(50);
          cSat.SetUsed(false);
          satview.cSats.add(cSat);
          
          cSat = new CurrentSat();
          cSat.SetPrn(12);
          cSat.SetAzi(15);
          cSat.SetElv(30);
          cSat.SetSNR(30);
          cSat.SetUsed(true);
          satview.cSats.add(cSat);
          
          cSat = new CurrentSat();
          cSat.SetPrn(13);
          cSat.SetAzi(300);
          cSat.SetElv(40);
          cSat.SetSNR(50);
          cSat.SetUsed(true);
          satview.cSats.add(cSat);
          
          cSat = new CurrentSat();
          cSat.SetPrn(14);
          cSat.SetAzi(30);
          cSat.SetElv(60);
          cSat.SetSNR(50);
          cSat.SetUsed(true);
          satview.cSats.add(cSat);
          
          final Listener gpslistener=new GpsStatus.Listener()
          {		
  			boolean isGpsFixed = false;
  			
  			public void showGpsSatInfo(){
  				GpsStatus xGpsStatus = locmgr.getGpsStatus(null) ; 
  				Iterable<GpsSatellite> iSatellites = xGpsStatus.getSatellites(); 
          		Iterator<GpsSatellite> it  = iSatellites.iterator();
          		satview.cSats.clear();
          		
          		int satcnt = 0;
          		int satfixcnt = 0;
          		while ( it.hasNext() )
                  {
                      GpsSatellite oSat = (GpsSatellite) it.next();
                      CurrentSat cSat = new CurrentSat();
                      cSat.SetPrn(oSat.getPrn());
                      cSat.SetAzi(oSat.getAzimuth());
                      cSat.SetElv(oSat.getElevation());
                      cSat.SetSNR(oSat.getSnr());
                      cSat.SetUsed(oSat.usedInFix());
                      if(oSat.usedInFix())
                    	  satfixcnt++;
                      satcnt++;
                      satview.cSats.add(cSat);
                  }
          		satview.setSatFix(satfixcnt);
          		satview.setSatTotal(satcnt);
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
                        	  		Toast.makeText(SatActivity.this, "FIRST FIX", Toast.LENGTH_SHORT).show();
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
          location = locmgr.getLastKnownLocation(best);
          
          satview.setProvider(location.getProvider());
          satview.setSatError(location.getAccuracy());
          
          /* create a Thread that will
           * periodically send messages
           * to our Handler */
          new Thread(new RefreshRunner()).start();
     }
     @Override
	    public boolean onCreateOptionsMenu(final Menu menu) {
	        super.onCreateOptionsMenu(menu);
	        menu.add(0, SatActivity.MENU_TOGGLEGPS, 0, "Toggle GPS").setIcon(R.drawable.togglegps);
	        return true;
	    }
     @Override
	 public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        switch (item.getItemId()) {
        	case SatActivity.MENU_TOGGLEGPS:
        		Intent itogglegps = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
    			startActivity(itogglegps);
    			break;
        }
        return super.onMenuItemSelected(featureId, item);
     }

     class RefreshRunner implements Runnable {
          // @Override
          public void run() {
               while (! Thread.currentThread().isInterrupted()) {
                    // Send Message to the Handler which will call the invalidate() method of the BoucneView
                    Message message = new Message();
                    message.what = SatActivity.GUIUPDATEIDENTIFIER;
                    SatActivity.this.myGUIUpdateHandler.sendMessage(message);

                    try {
                         Thread.sleep(10); // a 10th of a second
                        	 
                    } catch (InterruptedException e) {
                         Thread.currentThread().interrupt();
                    }
               }
          }
     }
     
     public void onPause(){
      	super.onPause();
      	locmgr.removeUpdates(this);
      }
      @Override
      protected void onResume(){
      	super.onResume();
      	locmgr.requestLocationUpdates(best,100,1,this);
      }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
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