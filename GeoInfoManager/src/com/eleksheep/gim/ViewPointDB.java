package com.eleksheep.gim;

import static android.provider.BaseColumns._ID;

import com.eleksheep.gim.R;

//import static com.eleksheep.geocoord.DBaseConstants.ALT;
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
import static com.eleksheep.gim.DBaseConstants.TIMESTR;
import static com.eleksheep.gim.DBaseConstants.XCOORD;
import static com.eleksheep.gim.DBaseConstants.YCOORD;
//import static com.eleksheep.geocoord2.DBaseConstants.DISTANCE;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ViewPointDB  extends ListActivity {
	   private static final int SORTORDER_REQUEST_CODE=1338; 
	   public static final String KEY_DBSORTDESCRIPT = "KEY_DBSORTDESCRIPT";
	   private static String[] FROM = { _ID, TIMESTR, LATDMS, LNGDMS, PTNAME,ADDSTREET,ADDCITY,ADDSTATE,ADDPOSTALCODE,SYMBOL};
	   private static String[] FROM3 = { PTNAME,_ID, TIMESTR, LATDMS, LNGDMS,ADDSTREET,ADDCITY,ADDSTATE,ADDPOSTALCODE,SYMBOL};
	//   private static String[] FROM2 = { _ID, PTNAME,LAT,LNG,XCOORD,YCOORD,ADDSTREET,ADDCITY,ADDSTATE,ADDPOSTALCODE,ADDCOUNTRY,SYMBOL,IMAGENAME };
	   private static String[] FROM2 = { PTNAME, _ID, LAT, LNG,XCOORD,YCOORD,ADDSTREET,ADDCITY,ADDSTATE,ADDPOSTALCODE,ADDCOUNTRY,SYMBOL,IMAGENAME,DESCR/*,DISTANCE*/};
	 //  private static String ORDER_BY_DESC = TITLE + " DESC";  //DESC is keyword for descending order. 
	 //  private static String ORDER_BY_DESC = _ID + " DESC";  //DESC is keyword for descending order. 
	   private static String ORDER_BY_ASC = _ID + " ASC";  //ASC by PTNAME 
	   private PointDBData events;
	   private int m_position = 0;
	   
	   private static final int DELETEREC_REQUEST_CODE=1436; 
	   private static final String KEYDELETEREC = "KEY_DELETE_RECORD";
	   private static final String KEYLAT = "KEY_LAT";
	   private static final String KEYLNG = "KEY_LNG";
	   private static final String KEYXCOORD = "KEY_XCOORD";
	   private static final String KEYYCOORD = "KEY_YCOORD";
	   private static final String KEYDESCR = "KEY_DESCR";
	   private static final String KEYADDSTREET = "KEY_ADD_STREET";
	   private static final String KEYADDCITY = "KEY_ADD_CITY";
	   private static final String KEYADDSTATE = "KEY_ADD_STATE";
	   private static final String KEYADDPOSTAL = "KEY_ADD_POSTAL";
	   private static final String KEYADDCOUNTRY = "KEY_ADD_COUNTRY";
	   @SuppressWarnings("unused")
	   private static final String KEYACC = "KEY_ACC";
	   @SuppressWarnings("unused")
	   private static final String KEYSTA = "KEY_STATUS";
	   @SuppressWarnings("unused")
	   private static final String KEYLPR = "KEY_LOCPROVIDER";
	   private static final String KEYPTNAME = "KEY_PTNAME";
	   private static final String KEYSYMBOL = "KEY_SYMBOL";
	   private static final String KEYIMAGENAME = "KEY_IMAGE_NAME";
	   
	   private static final String KEYSORTORDER = "KEY_SORTORDER";
	   private double m_lat = -1.0;
	   private double m_lng = -190.0;
	   
	   @Override
		public void onResume(){
			super.onResume();
			setContentView(R.layout.point_dbview); 
			//final Bundle bun = getIntent().getExtras();
			//m_lng = bun.getDouble(KEYLNG);
			//m_lat = bun.getDouble(KEYLAT);
			
			events = new PointDBData(this); 
			//sortbyDistance();

		      try {
		         Cursor cursor = getEvents(); 
		         showEvents(cursor); 
		      } finally {
		         events.close(); 
		      }	
		}
	   
	   public void sortbyDistance(){
		   SQLiteDatabase db = events.getReadableDatabase();
		   Cursor cursor = db.query(TABLE_NAME, FROM2, null, null, null,null, ORDER_BY_ASC);
		   
		   cursor.moveToFirst();
		   float[] distresults2 = new float[2];
		   for(int i = 0; i< cursor.getCount(); i++){
			   double dlat = cursor.getDouble(cursor.getColumnIndex(LAT));
			   double dlng = cursor.getDouble(cursor.getColumnIndex(LNG));

   		       Location.distanceBetween(dlat, dlng, m_lat, m_lng, distresults2);
   		       double distance = distresults2[0];
   		       cursor.moveToNext();
		   }
	   }
	   
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.point_dbview); 

	      SharedPreferences mPrefs;
	        mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
	        
	      ORDER_BY_ASC = mPrefs.getString(TestProj1.KEY_DBSORTDESCRIPT, "_ID ASC");
	   }

	   private Cursor getEvents() {
	      // Perform a managed query. The Activity will handle closing
	      // and re-querying the cursor when needed.
	      SQLiteDatabase db = events.getReadableDatabase();
	      Cursor cursor = db.query(TABLE_NAME, FROM3, null, null, null,null, ORDER_BY_ASC);
	      if(cursor.getCount() < 1)
	    	  Toast.makeText(this, "No Data Available", Toast.LENGTH_LONG).show();
	      startManagingCursor(cursor);
	      return cursor;
	   }
	   private static int[] TO = { R.id.pdb_rowid, R.id.pdb_time,R.id.pdb_latvalue, R.id.pdb_lngvalue, R.id.pdb_ptname,R.id.pdb_add_street,R.id.pdb_add_city,R.id.pdb_add_state,
		   R.id.pdb_add_postal_code,R.id.pdb_symbol};
	   private void showEvents(Cursor cursor) {
		  SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.pointdb_item, cursor, FROM, TO);
		  setListAdapter(adapter);
	   }
	   
	   protected void onListItemClick(ListView l, View v, int position, long id) { 
		   SQLiteDatabase db = events.getReadableDatabase();
		   Cursor cursor = db.query(TABLE_NAME, FROM2, null, null, null,null, ORDER_BY_ASC);
		   cursor.moveToPosition(position);
		   m_position = position;
		   Intent idbopt = new Intent(this,DBOptions.class);
		   Bundle bun = new Bundle();
		   bun.putString(KEYPTNAME,cursor.getString(cursor.getColumnIndex(PTNAME)) );
		   bun.putDouble(KEYLAT,cursor.getDouble(cursor.getColumnIndex(LAT)) );
		   bun.putDouble(KEYLNG,cursor.getDouble(cursor.getColumnIndex(LNG)) );
		   bun.putDouble(KEYXCOORD, cursor.getDouble(cursor.getColumnIndex(XCOORD)) );
		   bun.putDouble(KEYYCOORD, cursor.getDouble(cursor.getColumnIndex(YCOORD)) );
		   bun.putInt(KEYSYMBOL, cursor.getInt(cursor.getColumnIndex(SYMBOL)));
		   bun.putString(KEYIMAGENAME,Environment.getExternalStorageDirectory().getAbsolutePath()+cursor.getString(cursor.getColumnIndex(IMAGENAME)));
		   bun.putString(KEYDESCR, cursor.getString(cursor.getColumnIndex(DESCR)));
		   bun.putString(KEYADDSTREET, cursor.getString(cursor.getColumnIndex(ADDSTREET)));
		   bun.putString(KEYADDCITY, cursor.getString(cursor.getColumnIndex(ADDCITY)));
		   bun.putString(KEYADDSTATE, cursor.getString(cursor.getColumnIndex(ADDSTATE)));
		   bun.putString(KEYADDPOSTAL, cursor.getString(cursor.getColumnIndex(ADDPOSTALCODE)));
		   bun.putString(KEYADDCOUNTRY, cursor.getString(cursor.getColumnIndex(ADDCOUNTRY)));
		   idbopt.putExtras(bun);
		   startActivityForResult(idbopt, DELETEREC_REQUEST_CODE);
	   }
	   @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
	        super.onActivityResult(requestCode, resultCode, intent);
	        
	        if(requestCode==SORTORDER_REQUEST_CODE){
	        	if(resultCode == RESULT_OK){
	        		Bundle bun = intent.getExtras();
	        		ORDER_BY_ASC = bun.getString(KEYSORTORDER);
	        		
	        		SharedPreferences mPrefs;
	                mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
	              //  if(!mPrefs.contains(TestProj1.KEY_DBSORTDESCRIPT)){
	                	Editor mPrefsEditor = mPrefs.edit();
	                	mPrefsEditor.putString(TestProj1.KEY_DBSORTDESCRIPT, ORDER_BY_ASC);
	                    mPrefsEditor.commit();
	              //  }
	        	}
	        }
	        if(requestCode==DELETEREC_REQUEST_CODE){
	        	if(resultCode==RESULT_OK)
	        	{
	        		Bundle bun = intent.getExtras();
	        		if(bun.getBoolean(KEYDELETEREC)){
			        	SQLiteDatabase db = events.getReadableDatabase();
			        	Cursor cursor = db.query(TABLE_NAME, FROM2, null, null, null,null, ORDER_BY_ASC);		   
	        			cursor.moveToPosition(m_position);
	        			db.delete(TABLE_NAME,  "_id=" + cursor.getInt(cursor.getColumnIndex(_ID)), null);
	        			Toast.makeText(ViewPointDB.this, "Point Deleted", Toast.LENGTH_SHORT).show();
	        		}	
	        	}
	        }
	   }
	   
	   @Override
		public boolean onCreateOptionsMenu(Menu menu){
			super.onCreateOptionsMenu(menu);
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_dbview, menu);
			return true;
		}
	   
	   @Override
		public boolean onOptionsItemSelected(MenuItem item){
			switch (item.getItemId()){
			case R.id.menu_sort_options:
			case R.id.menu_sort_options_shortcut:
				Intent isortopt = new Intent(this,SortOptions.class);
				Bundle bun = new Bundle();
        		bun.putString(KEYSORTORDER, ORDER_BY_ASC);
        		isortopt.putExtras(bun);
        		startActivityForResult(isortopt, SORTORDER_REQUEST_CODE);
				return true;
			default:
				return false;
			}
	   }
}
