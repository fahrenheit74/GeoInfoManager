package com.eleksheep.gim;

import static android.provider.BaseColumns._ID;
//import static com.eleksheep.geocoord.DBaseConstants.ALT;
//import static com.eleksheep.geocoord2.DBaseConstants.DESCR;
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
//import static com.eleksheep.geocoord2.DBaseConstants.DISTANCE;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PointDBData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "geocoord.db";
	private static final int DATABASE_VERSION = 20;
	 /** Create a helper object for the Events database */
	   public PointDBData(Context ctx) { 
	      super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	   }

	   @Override
	   public void onCreate(SQLiteDatabase db) { 
	      db.execSQL("CREATE TABLE " + TABLE_NAME + " (" 
	    		+ _ID  + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	    		+ PTNAME + " TEXT NOT NULL,"
	            + TIME   + " INTEGER," + TIMESTR   + " TEXT NOT NULL,"
	            + LAT + " REAL," + LNG + " REAL,"
	            + XCOORD + " REAL," + YCOORD + " REAL,"
	            + LATDMS + " TEXT NOT NULL," + LNGDMS + " TEXT NOT NULL,"
	            + ADDSTREET + " TEXT NOT NULL," + ADDCITY + " TEXT NOT NULL," 
	            + ADDSTATE + " TEXT NOT NULL,"  + ADDCOUNTRY + " TEXT NOT NULL," 
	            + ADDPOSTALCODE + " TEXT NOT NULL," + DESCR + " TEXT NOT NULL,"
	            + IMAGENAME + " TEXT NOT NULL," + SYMBOL + " INTEGER);");
	   }
	   
	   @Override
	   public void onUpgrade(SQLiteDatabase db, int oldVersion, 
	         int newVersion) {
	      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	      onCreate(db);
	   }
}

