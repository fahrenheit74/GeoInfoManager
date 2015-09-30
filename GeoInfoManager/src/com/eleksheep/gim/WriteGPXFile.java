package com.eleksheep.gim;

//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.eleksheep.gim.R;

import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;

import static com.eleksheep.gim.DBaseConstants.ADDCITY;
import static com.eleksheep.gim.DBaseConstants.ADDCOUNTRY;
import static com.eleksheep.gim.DBaseConstants.ADDPOSTALCODE;
import static com.eleksheep.gim.DBaseConstants.ADDSTATE;
import static com.eleksheep.gim.DBaseConstants.ADDSTREET;
import static com.eleksheep.gim.DBaseConstants.DESCR;
import static com.eleksheep.gim.DBaseConstants.IMAGENAME;
import static com.eleksheep.gim.DBaseConstants.LAT;
import static com.eleksheep.gim.DBaseConstants.LNG;
import static com.eleksheep.gim.DBaseConstants.PTNAME;
import static com.eleksheep.gim.DBaseConstants.SYMBOL;
import static com.eleksheep.gim.DBaseConstants.TIMESTR;

public class WriteGPXFile {
	
	private String m_fname = "";
	private static List<Waypoint> waypt = new ArrayList<Waypoint>();
	private Cursor m_cursor;
	double m_minlat = 90.0;
	double m_minlon = 180.0;
	double m_maxlat = -90.0;
	double m_maxlon = -180.0;
	
	void setFileName(String fname){
		m_fname = fname;
	}
	
	void GPXWriteHeader(OutputStreamWriter out) throws IOException{
		//Write GPX Header
		String szGPXLine = "<?xml version=\"1.0\"?>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "<gpx \n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "version=\"1.0\"\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "creator=\"GeoCoord - http://www.topografix.com\"\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "xmlns=\"http://www.topografix.com/GPX/1/0\"\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\"\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\">\n";
		WriteAndFlush(szGPXLine,out);
	}
	void GPXWriteMetaData(OutputStreamWriter out) throws IOException{
		String szGPXLine = "<metadata>";
		WriteAndFlush(szGPXLine,out);
		//<bounds maxlat="41.3944166" maxlon="-81.5070707" minlat="41.1581667" minlon="-81.6304834"/>
		szGPXLine = "<bounds maxlat=\"" + m_maxlat + "\" maxlon=\"" + m_maxlon + "\" minlat=\"" + m_minlat + "\" minlon=\"" + m_minlon + "\"/>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "</metadata>";
		WriteAndFlush(szGPXLine,out);
	}
	void WriteAndFlush(String str, OutputStreamWriter out) throws IOException{
		out.write(str);
		//out.flush();
	}
	
	void GPXWriteTail(OutputStreamWriter out) throws IOException{
		String szGPXLine = "</gpx>";
		out.write(szGPXLine);
		//out.flush();
	}
	
	void GPXWriteWpt(OutputStreamWriter out, Waypoint wpt) throws IOException{
		String szGPXLine;
		//<wpt lat="42.434980" lon="-71.109942">
		szGPXLine = "<wpt lat=\"" + wpt.getLat() + "\" lon=\"" + wpt.getLng() + "\">\n";
		WriteAndFlush(szGPXLine,out);
		//<ele>73.761600</ele>
		szGPXLine = "  <ele>" + wpt.getElv() + "</ele>\n";
		WriteAndFlush(szGPXLine,out);
		//<time>2001-11-07T23:53:41Z</time>
		szGPXLine = "  <time>" + wpt.getTimeString() + "</time>\n";
		WriteAndFlush(szGPXLine,out);
		//<name>PANTHRCAVE</name>
		szGPXLine = "  <name>" + wpt.getName() + "</name>\n";
		WriteAndFlush(szGPXLine,out);
		//<desc><![CDATA[Panther Cave]]></desc>
		szGPXLine = "  <desc>" + wpt.getDesc() + "</desc>\n";
		WriteAndFlush(szGPXLine,out);
		//<sym>Tunnel</sym>
		szGPXLine = "  <sym>" + wpt.getSymbo() + "</sym>\n";
		WriteAndFlush(szGPXLine,out);
		// <type><![CDATA[Tunnel]]></type>
		szGPXLine = "  <type>" + "" + "</type>\n";
		WriteAndFlush(szGPXLine,out);
		//Extensions 
		szGPXLine = "<extensions>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "  <gpxx:WaypointExtension>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "    <gpxx:Address>\n"; 
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "      <gpxx:StreetAddress>" + wpt.getStreetAdd()  + "</gpxx:StreetAddress>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "      <gpxx:City>" + wpt.getCityAdd() + "</gpxx:City>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "      <gpxx:State>" + wpt.getStateAdd() + "</gpxx:State>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "      <gpxx:PostalCode>" + wpt.getPostalCodeAdd() + "</gpxx:PostalCode>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "      <gpxx:Country>" + wpt.getCountryAdd() + "</gpxx:Country>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "    </gpxx:Address>\n"; 
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "    <gpxx:ImagePath>" + wpt.getImagePath() + "</gpxx:ImagePath>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "  </gpxx:WaypointExtension>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine = "</extensions>\n";
		WriteAndFlush(szGPXLine,out);
		szGPXLine ="</wpt>\n";
		WriteAndFlush(szGPXLine,out);
		//out.flush();
	}
	
	public void setDBCursor(Cursor cursor){
		m_cursor = cursor;
	}
	
	private String findSymName(int sym){
		String symName = "";

		switch(sym)
		{
		case R.drawable.redflag2:
			symName = "REDFLAG";
			break;
		case R.drawable.map_pin1:
			symName = "MAP_PIN1";
			break;
		case R.drawable.map_pin2:
			symName = "MAP_PIN2";
			break;
		case R.drawable.mapsym_1:
			symName = "MAP_SYM1";
			break;
		case R.drawable.mapsym_2:
			symName = "MAP_SYM2";
			break;
		case R.drawable.mapsym_3:
			symName = "MAP_SYM3";
			break;
		case R.drawable.mapsym_4:
			symName = "MAP_SYM4";
			break;
		case R.drawable.mapsym_5:
			symName = "MAP_SYM5";
			break;
		case R.drawable.mapsym_6:
			symName = "MAP_SYM6";
			break;
		case R.drawable.mapsym_7:
			symName = "MAP_SYM7";
			break;
		case R.drawable.mapsym_8:
			symName = "MAP_SYM8";
			break;
		case R.drawable.mapsym_9:
			symName = "MAP_SYM9";
			break;
		case R.drawable.mapsym_10:
			symName = "MAP_SYM10";
			break;
		case R.drawable.mapsym_11:
			symName = "MAP_SYM11";
			break;
		case R.drawable.mapsym_12:
			symName = "MAP_SYM12";
			break;
		case R.drawable.mapsym_13:
			symName = "MAP_SYM13";
			break;
		case R.drawable.mapsym_14:
			symName = "MAP_SYM14";
			break;
		case R.drawable.mapsym_15:
			symName = "MAP_SYM15";
			break;
		case R.drawable.mapsym_16:
			symName = "MAP_SYM16";
			break;
		case R.drawable.mapsym_17:
			symName = "MAP_SYM17";
			break;
		case R.drawable.mapsym_18:
			symName = "MAP_SYM18";
			break;
		case R.drawable.mapsym_19:
			symName = "MAP_SYM19";
			break;
		case R.drawable.mapsym_20:
			symName = "MAP_SYM20";
			break;
		case R.drawable.mapsym_21:
			symName = "MAP_SYM21";
			break;
		case R.drawable.mapsym_22:
			symName = "MAP_SYM22";
			break;
		case R.drawable.mapsym_23:
			symName = "MAP_SYM23";
			break;
		case R.drawable.mapsym_24:
			symName = "MAP_SYM24";
			break;
		case R.drawable.mapsym_25:
			symName = "MAP_SYM25";
			break;
		case R.drawable.mapsym_26:
			symName = "MAP_SYM26";
			break;
		case R.drawable.mapsym_27:
			symName = "MAP_SYM27";
			break;
		case R.drawable.mapsym_28:
			symName = "MAP_SYM28";
			break;
		case R.drawable.mapsym_29:
			symName = "MAP_SYM29";
			break;
		case R.drawable.mapsym_30:
			symName = "MAP_SYM30";
			break;
		case R.drawable.mapsym_31:
			symName = "MAP_SYM31";
			break;
		case R.drawable.mapsym_32:
			symName = "MAP_SYM32";
			break;
		case R.drawable.mapsym_33:
			symName = "MAP_SYM33";
			break;
		case R.drawable.mapsym_34:
			symName = "MAP_SYM34";
			break;
		case R.drawable.mapsym_35:
			symName = "MAP_SYM35";
			break;
		case R.drawable.mapsym_36:
			symName = "MAP_SYM36";
			break;
		case R.drawable.mapsym_37:
			symName = "MAP_SYM37";
			break;
		case R.drawable.mapsym_38:
			symName = "MAP_SYM38";
			break;
		case R.drawable.mapsym_39:
			symName = "MAP_SYM39";
			break;
		case R.drawable.mapsym_40:
			symName = "MAP_SYM40";
			break;
		case R.drawable.mapsym_41:
			symName = "MAP_SYM41";
			break;
		case R.drawable.mapsym_42:
			symName = "MAP_SYM42";
			break;
		case R.drawable.mapsym_43:
			symName = "MAP_SYM43";
			break;
		case R.drawable.mapsym_44:
			symName = "MAP_SYM44";
			break;
		case R.drawable.mapsym_45:
			symName = "MAP_SYM45";
			break;
		case R.drawable.mapsym_46:
			symName = "MAP_SYM46";
			break;
		case R.drawable.mapsym_47:
			symName = "MAP_SYM47";
			break;
		case R.drawable.mapsym_48:
			symName = "MAP_SYM48";
			break;
		case R.drawable.mapsym_49:
			symName = "MAP_SYM49";
			break;
		case R.drawable.mapsym_50:
			symName = "MAP_SYM50";
			break;
		}
		return symName;
	}
	
	public void GPXFileWriter(){
		//Open output stream
		int numRows = m_cursor.getCount();
	    m_cursor.moveToFirst();
	    
		for(int i=0; i<numRows; ++i){
			Waypoint wpt = new Waypoint();
			wpt.init();
			double lat = m_cursor.getDouble(m_cursor.getColumnIndex(LAT));
			double lng = m_cursor.getDouble(m_cursor.getColumnIndex(LNG));
			
			if(lat >= m_maxlat)
				m_maxlat = lat;
			if(lat <= m_minlat)
				m_minlat = lat;
			if(lng >= m_maxlon )
				m_maxlon = lng;
			if(lng <= m_minlon)
				m_minlon = lng;
			
			double ele = 0.0;
			String name = m_cursor.getString(m_cursor.getColumnIndex(PTNAME));
			String symbol = "gc_" + findSymName(m_cursor.getInt(m_cursor.getColumnIndex(SYMBOL)));
			String type = "";
			String streetadd = m_cursor.getString(m_cursor.getColumnIndex(ADDSTREET));
			String cityadd = m_cursor.getString(m_cursor.getColumnIndex(ADDCITY));
			String stateadd = m_cursor.getString(m_cursor.getColumnIndex(ADDSTATE));
			String postalcodeadd = m_cursor.getString(m_cursor.getColumnIndex(ADDPOSTALCODE));
			String countryadd = m_cursor.getString(m_cursor.getColumnIndex(ADDCOUNTRY));
			String timestr = m_cursor.getString(m_cursor.getColumnIndex(TIMESTR));
			String desc = m_cursor.getString(m_cursor.getColumnIndex(DESCR));
			String imagepath = m_cursor.getString(m_cursor.getColumnIndex(IMAGENAME));
			
			wpt.set(lat, lng, ele, name, symbol,desc,type,timestr,imagepath);
			wpt.setaddress(streetadd, cityadd, stateadd, postalcodeadd, countryadd);
			waypt.add(wpt);
			m_cursor.moveToNext();
		}
		if(waypt.size() > 0){
			OutputStream openFileOutput;
			try {
				openFileOutput = new FileOutputStream(m_fname);
				OutputStreamWriter out = new OutputStreamWriter(openFileOutput);
				GPXWriteHeader(out);
				for(int j = 0; j<waypt.size(); j++){
					GPXWriteWpt(out,waypt.get(j));
				}
				GPXWriteTail(out);
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
