package com.eleksheep.gim;

import static android.provider.BaseColumns._ID;
import static com.eleksheep.gim.DBaseConstants.ADDCITY;
import static com.eleksheep.gim.DBaseConstants.ADDCOUNTRY;
import static com.eleksheep.gim.DBaseConstants.ADDPOSTALCODE;
import static com.eleksheep.gim.DBaseConstants.ADDSTATE;
import static com.eleksheep.gim.DBaseConstants.ADDSTREET;
import static com.eleksheep.gim.DBaseConstants.DESCR;
import static com.eleksheep.gim.DBaseConstants.IMAGENAME;
import static com.eleksheep.gim.DBaseConstants.LAT;
import static com.eleksheep.gim.DBaseConstants.LATDMS;
import static com.eleksheep.gim.DBaseConstants.LNG;
import static com.eleksheep.gim.DBaseConstants.LNGDMS;
import static com.eleksheep.gim.DBaseConstants.PTNAME;
import static com.eleksheep.gim.DBaseConstants.SYMBOL;
import static com.eleksheep.gim.DBaseConstants.TABLE_NAME;
import static com.eleksheep.gim.DBaseConstants.TIME;
import static com.eleksheep.gim.DBaseConstants.TIMESTR;
import static com.eleksheep.gim.DBaseConstants.XCOORD;
import static com.eleksheep.gim.DBaseConstants.YCOORD;

import java.util.ArrayList;
import java.util.List;

import com.eleksheep.gim.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class ReadGPXFile  extends Activity  {

	private PointDBData points;
	private static String[] FROM2 = { _ID,PTNAME,TIMESTR,LAT,LNG,XCOORD,YCOORD,SYMBOL,ADDSTREET,ADDCITY,ADDSTATE,ADDPOSTALCODE,ADDCOUNTRY,DESCR,IMAGENAME };
	private static String ORDER_BY_DESC = _ID + " DESC";  //DESC is keyword for descending order.
	private static final String KEY_SYMBOLS = "KEY_SYMBOLS";
	public static final String KEY_GPX_DEFSTRING = "KEY_GPXDEFSTRING";
	ReadParseGPXFile readgpx;
	WriteGPXFile writegpx;
	private int typeBar = 0;
	private String m_fname = "";
	ListView lview;
	ImageButton button1, button2;
	ProgressDialog progDialog;
	ProgressThread progThread;
	int delay = 40;                  // Milliseconds of delay in the update loop
    int maxBarValue = 200;           // Maximum value of horizontal progress bar
    
    private int symbolindex = R.drawable.mapsym_1;
    @Override
	public void onResume(){
		super.onResume();
		final SharedPreferences mPrefs;
		final EditText gpxfilename = (EditText)findViewById(R.id.rgpx_fname);
        mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/gim/gpx_files/new.gpx";
        String tmpFilePath = mPrefs.getString(KEY_GPX_DEFSTRING, "");
        if(tmpFilePath.length()>0)
        	gpxfilename.setText(mPrefs.getString(KEY_GPX_DEFSTRING, ""));
        else
        	gpxfilename.setText(imageFilePath);
        final ImageButton symBtn = (ImageButton) findViewById(R.id.rgpx_sym_btn);
        symbolindex =mPrefs.getInt(KEY_SYMBOLS, R.drawable.mapsym_1);
        symBtn.setImageResource(symbolindex);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.readgpxfile);
		
		readgpx = new ReadParseGPXFile();
		points = new PointDBData(this);
		// Process button to start spinner progress dialog with anonymous inner class
		final EditText gpxfilename = (EditText)findViewById(R.id.rgpx_fname);
		final ImageButton symBtn = (ImageButton) findViewById(R.id.rgpx_sym_btn);
		final ImageButton browseBtn = (ImageButton) findViewById(R.id.rgpx_browse);
        button1 = (ImageButton) findViewById(R.id.rgpx_button01);
        
        button1.setOnClickListener(new OnClickListener(){
        	@Override
            public void onClick(View v) {
        		String gpxfname = gpxfilename.getText().toString();
        		readgpx.setFileName(gpxfname);
        		String[] strarr = gpxfname.split("/");
        		if(gpxfname.length()>0){
	        		m_fname = strarr[strarr.length-1];
	                typeBar = 0;
	                showDialog(typeBar);
        		}
            }
        }); 
        
        symBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent symgridview = new Intent(ReadGPXFile.this,SymbolGridView.class);
        		ReadGPXFile.this.startActivity(symgridview);
        	}
        });
        
        browseBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        	Intent ifilebrowser = new Intent(ReadGPXFile.this,FileBrowser.class);
			startActivity(ifilebrowser);
        	}
        });
        
        // Process button to start horizontal progress bar dialog with anonymous inner class
        button2 = (ImageButton) findViewById(R.id.rgpx_button02);
        button2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
		//		Intent ifilebrowser = new Intent(ReadGPXFile.this,FileBrowser.class);
		//		startActivity(ifilebrowser);
				writegpx = new WriteGPXFile();
				String gpxfname = gpxfilename.getText().toString();
				writegpx.setFileName(gpxfname);
				SQLiteDatabase db = points.getReadableDatabase();
			    Cursor cursor = db.query(TABLE_NAME, FROM2, null, null, null,null, ORDER_BY_DESC);
				writegpx.setDBCursor(cursor);
				String[] strarr = gpxfname.split("/");
        		if(gpxfname.length()>0){
	        		m_fname = strarr[strarr.length-1];
	                typeBar = 1;
	                showDialog(typeBar);
        		}
				//writegpx.GPXFileWriter();
			}
        }); 
	}
	
	// Method to create a progress bar dialog of either spinner or horizontal type
    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case 0: 
            progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setMessage("Reading " + m_fname);
            progDialog.setTitle("Loading GPX");
            progThread = new ProgressThread(handler);
            progThread.start();
            return progDialog;
        case 1: 
        	progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setMessage("Saving " + m_fname);
            progDialog.setTitle("Saving GPX");
            progThread = new ProgressThread(handler);
            progThread.start();
            return progDialog;
        	
        case 2:                      // Horizontal
            progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progDialog.setMax(maxBarValue);
            progDialog.setMessage("Dollars in checking account:");
            progThread = new ProgressThread(handler);
            progThread.start();
            return progDialog;
        default:
            return null;
        }
    }
  //  @Override 
  //  protected void onStop(){
  //  	progDialog.cancel();
  //  }
	
	private void readGPXFile(){
    	List<Waypoint> mapwaypt = new ArrayList<Waypoint>();
        readgpx.ReadGPXFile(mapwaypt);
        points = new PointDBData(this);
        SQLiteDatabase db = points.getWritableDatabase();
        for(int j = 0; j<mapwaypt.size(); j++){
        	Waypoint wpt = new Waypoint();
        	wpt = mapwaypt.get(j);
        //	String[] MonthStr = { "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec" };        	 
    	    ContentValues values = new ContentValues();
    	    Time time = new Time();
    	    time.setToNow();
    	//    String date = Integer.toString(time.monthDay) + " " + MonthStr[time.month] + " " + Integer.toString(time.year);
    	//    String hms = Integer.toString(time.hour) + ":" + Integer.toString(time.minute) + ":" + Integer.toString(time.second);
    	    values.put(TIME, System.currentTimeMillis());
    	    values.put(TIMESTR, wpt.getTimeString());
    	    values.put(LAT, wpt.getLat());
    	    values.put(LNG, wpt.getLng());
    	    values.put(XCOORD, 0.0);
    	    values.put(YCOORD, 0.0);
    	    values.put(LATDMS, Location.convert(wpt.getLat(), Location.FORMAT_SECONDS));
    	    values.put(LNGDMS, Location.convert(wpt.getLng(), Location.FORMAT_SECONDS));
    	    values.put(PTNAME,wpt.getName());
    	 
    	    String SymbolName = wpt.getSymbo();
    	    if(SymbolName.length() > 4){
    	    String symName = SymbolName.substring(3);
	    	    if(SymbolName.contains("gc_")&& getSymbolInt(symName)!=-1)
	    	    	values.put(SYMBOL, getSymbolInt(symName));
	    	    else
	    	    	values.put(SYMBOL, symbolindex);
    	    }
    	    else 
    	    	values.put(SYMBOL, symbolindex);
    	    
    	    values.put(ADDSTREET, wpt.getStreetAdd());
    	    values.put(ADDCITY, wpt.getCityAdd());
    	    values.put(ADDSTATE, wpt.getStateAdd());
    	    values.put(ADDPOSTALCODE, wpt.getPostalCodeAdd());
    	    values.put(ADDCOUNTRY, wpt.getCountryAdd());
    	    values.put(DESCR,wpt.getDesc());
    	    values.put(IMAGENAME, wpt.getImagePath());

    	    long rowid = db.insertOrThrow(TABLE_NAME, null, values);
        }
	    points.close();
	}
	
	private int getSymbolInt(String symName){
				
		if(symName.matches("REDFLAG"))
			return R.drawable.redflag2;
		else if(symName.matches("MAP_PIN1"))
			return R.drawable.map_pin1;
		else if(symName.matches("MAP_PIN2"))
			return R.drawable.map_pin2;
		else if(symName.matches("MAP_SYM1"))
			return R.drawable.mapsym_1;
		else if(symName.matches("MAP_SYM2"))
			return R.drawable.mapsym_2;
		else if(symName.matches("MAP_SYM3"))
			return R.drawable.mapsym_3;
		else if(symName.matches("MAP_SYM4"))
			return R.drawable.mapsym_4;
		else if(symName.matches("MAP_SYM5"))
			return R.drawable.mapsym_5;
		else if(symName.matches("MAP_SYM6"))
			return R.drawable.mapsym_6;
		else if(symName.matches("MAP_SYM7"))
			return R.drawable.mapsym_7;
		else if(symName.matches("MAP_SYM8"))
			return R.drawable.mapsym_8;
		else if(symName.matches("MAP_SYM9"))
			return R.drawable.mapsym_9;
		else if(symName.matches("MAP_SYM10"))
			return R.drawable.mapsym_10;
		else if(symName.matches("MAP_SYM11"))
			return R.drawable.mapsym_11;
		else if(symName.matches("MAP_SYM12"))
			return R.drawable.mapsym_12;
		else if(symName.matches("MAP_SYM13"))
			return R.drawable.mapsym_13;
		else if(symName.matches("MAP_SYM14"))
			return R.drawable.mapsym_14;
		else if(symName.matches("MAP_SYM15"))
			return R.drawable.mapsym_15;
		else if(symName.matches("MAP_SYM16"))
			return R.drawable.mapsym_16;
		else if(symName.matches("MAP_SYM17"))
			return R.drawable.mapsym_17;
		else if(symName.matches("MAP_SYM18"))
			return R.drawable.mapsym_18;
		else if(symName.matches("MAP_SYM19"))
			return R.drawable.mapsym_19;
		else if(symName.matches("MAP_SYM20"))
			return R.drawable.mapsym_20;
		else if(symName.matches("MAP_SYM21"))
			return R.drawable.mapsym_21;
		else if(symName.matches("MAP_SYM22"))
			return R.drawable.mapsym_22;
		else if(symName.matches("MAP_SYM23"))
			return R.drawable.mapsym_23;
		else if(symName.matches("MAP_SYM24"))
			return R.drawable.mapsym_24;
		else if(symName.matches("MAP_SYM25"))
			return R.drawable.mapsym_25;
		else if(symName.matches("MAP_SYM26"))
			return R.drawable.mapsym_26;
		else if(symName.matches("MAP_SYM27"))
			return R.drawable.mapsym_27;
		else if(symName.matches("MAP_SYM28"))
			return R.drawable.mapsym_28;
		else if(symName.matches("MAP_SYM29"))
			return R.drawable.mapsym_29;
		else if(symName.matches("MAP_SYM30"))
			return R.drawable.mapsym_30;
		else if(symName.matches("MAP_SYM30"))
			return R.drawable.mapsym_30;
		else if(symName.matches("MAP_SYM31"))
			return R.drawable.mapsym_31;
		else if(symName.matches("MAP_SYM32"))
			return R.drawable.mapsym_32;
		else if(symName.matches("MAP_SYM33"))
			return R.drawable.mapsym_33;
		else if(symName.matches("MAP_SYM34"))
			return R.drawable.mapsym_34;
		else if(symName.matches("MAP_SYM35"))
			return R.drawable.mapsym_35;
		else if(symName.matches("MAP_SYM36"))
			return R.drawable.mapsym_36;
		else if(symName.matches("MAP_SYM37"))
			return R.drawable.mapsym_37;
		else if(symName.matches("MAP_SYM38"))
			return R.drawable.mapsym_38;
		else if(symName.matches("MAP_SYM39"))
			return R.drawable.mapsym_39;
		else if(symName.matches("MAP_SYM40"))
			return R.drawable.mapsym_40;
		else if(symName.matches("MAP_SYM40"))
			return R.drawable.mapsym_40;
		else if(symName.matches("MAP_SYM41"))
			return R.drawable.mapsym_41;
		else if(symName.matches("MAP_SYM42"))
			return R.drawable.mapsym_42;
		else if(symName.matches("MAP_SYM43"))
			return R.drawable.mapsym_43;
		else if(symName.matches("MAP_SYM44"))
			return R.drawable.mapsym_44;
		else if(symName.matches("MAP_SYM45"))
			return R.drawable.mapsym_45;
		else if(symName.matches("MAP_SYM46"))
			return R.drawable.mapsym_46;
		else if(symName.matches("MAP_SYM47"))
			return R.drawable.mapsym_47;
		else if(symName.matches("MAP_SYM48"))
			return R.drawable.mapsym_48;
		else if(symName.matches("MAP_SYM49"))
			return R.drawable.mapsym_49;
		else if(symName.matches("MAP_SYM50"))
			return R.drawable.mapsym_50;
		else
			return -1;
	}

//Handler on the main (UI) thread that will receive messages from the 
// second thread and update the progress.

	final Handler handler = new Handler() {
	    public void handleMessage(Message msg) {
	        // Get the current value of the variable total from the message data
	        // and update the progress bar.
	        int total = msg.getData().getInt("total");
	        progDialog.setProgress(total);
	        if (total <= 0){
	            dismissDialog(typeBar);
	            progThread.setState(ProgressThread.DONE);
	        }
	    }
	};
	
	// Inner class that performs progress calculations on a second thread.  Implement
    // the thread by subclassing Thread and overriding its run() method.  Also provide
    // a setState(state) method to stop the thread gracefully.
    
    private class ProgressThread extends Thread {	
        
        // Class constants defining state of the thread
        final static int DONE = 0;
        final static int RUNNING = 1;
        
        Handler mHandler;
        @SuppressWarnings("unused")
		int mState;
        int total;
    
        // Constructor with an argument that specifies Handler on main thread
        // to which messages will be sent by this thread.
        
        ProgressThread(Handler h) {
            mHandler = h;
        }
        
        // Override the run() method that will be invoked automatically when 
        // the Thread starts.  Do the work required to update the progress bar on this
        // thread but send a message to the Handler on the main UI thread to actually
        // change the visual representation of the progress. In this example we count
        // the index total down to zero, so the horizontal progress bar will start full and
        // count down.
        
        @Override
        public void run() {
            mState = RUNNING;   
            total = maxBarValue;
            //while (mState == RUNNING) {
                // The method Thread.sleep throws an InterruptedException if Thread.interrupt() 
                // were to be issued while thread is sleeping; the exception must be caught.
            /*    try {
                    // Control speed of update (but precision of delay not guaranteed)
                    Thread.sleep(delay);
                    Thread.
                } catch (InterruptedException e) {
                    Log.e("ERROR", "Thread was Interrupted");
                }
                
                // Send message (with current value of  total as data) to Handler on UI thread
                // so that it can update the progress bar.
                */
            switch(typeBar) {
            case 0: 
                //total--;    // Count down  
            	readGPXFile();
            	total = -1;
            	Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("total", total);
                msg.setData(b);
                mHandler.sendMessage(msg);
                return;
            case 1: // Write
            	writegpx.GPXFileWriter();
            	total = -1;
            	Message msg2 = mHandler.obtainMessage();
                Bundle bb = new Bundle();
                bb.putInt("total", total);
                msg2.setData(bb);
                mHandler.sendMessage(msg2);
                return;
            }
        }
       
        // Set current state of thread (use state=ProgressThread.DONE to stop thread)
        public void setState(int state) {
            mState = state;
        }
    }
}



