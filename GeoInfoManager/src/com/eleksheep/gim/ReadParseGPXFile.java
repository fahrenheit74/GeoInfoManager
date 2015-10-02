package com.eleksheep.gim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


//import android.text.format.DateFormat;

public class ReadParseGPXFile extends DefaultHandler  {

	@SuppressWarnings("unused")
	private List<Trackpoint> track = new ArrayList<Trackpoint>();
	private static List<Waypoint> waypt = new ArrayList<Waypoint>();
	private StringBuffer buf = new StringBuffer();
	private String m_fname = "";
	private double lat = 0.0;
	private double lon = 0.0;
	private double ele = 0.0;
	private String name = "";
	private String desc = "";
	private String imagepath = "";
	private String timestr = "";
	private String streetadd = "";
	private String cityadd = "";
	private String stateadd = "";
	private String postalcodeadd = "";
	private String countryadd = "";
	private String sym =  "";
	private String type = ""; 
	private boolean iswaypoint = false; 
	private boolean iswaypointadd = false;
	private boolean isaddressposted = false;
	private boolean isextension = false;
	private boolean iswaypointextension = false;
	private Waypoint wpt;
	
//	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//	private Date time;

	@Override
    public void startDocument() throws SAXException {
		 // Not used
    }

    @Override
    public void endDocument() throws SAXException {
         // Not used
    }
 
    @Override
    public void startElement(String namespaceURI, String localName,
    		String qName, Attributes atts) throws SAXException 
    {
		buf.setLength(0);
		if (localName.equals("wpt")) {
			lat = Double.parseDouble(atts.getValue("lat"));
			lon = Double.parseDouble(atts.getValue("lon"));
			wpt = new Waypoint();
			wpt.init();
			iswaypoint = true;
		}
		if(localName.equals("Address")&&(iswaypointextension == true)){
			iswaypointadd = true;
		}
		if(localName.equals("extensions")){
			isextension = true;
		}
		if(localName.equals("WaypointExtension")&&(isextension == true)){
			iswaypointextension = true;
		}
    }
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
    	throws SAXException 
    {
    	if (localName.equals("wpt")) {
    		if(iswaypoint){
    			wpt.set(lat, lon, ele, name, sym, desc, type,timestr,imagepath);
    			if(!isaddressposted)
    				wpt.setaddress("", "", "", "", "");
    			waypt.add(wpt);
    			iswaypoint = false;
    			isaddressposted = false;
    		}
        } 
    	else if(localName.equals("Address")){
    		if(iswaypointadd){
    			wpt.setaddress(streetadd, cityadd, stateadd, postalcodeadd, countryadd);
    			iswaypointadd = false;
    			isaddressposted = true;
    		}
    	}
    	else if(localName.equals("StreetAddress")&&(iswaypointadd)){
    		streetadd = buf.toString();
    	}
    	else if(localName.equals("City")&&(iswaypointadd)){
    		cityadd = buf.toString();
    	}
    	else if(localName.equals("State")&&(iswaypointadd)){
    		stateadd = buf.toString();
    	}
    	else if(localName.equals("PostalCode")&&(iswaypointadd)){
    		postalcodeadd = buf.toString();
    	}
    	else if(localName.equals("Country")&&(iswaypointadd)){
    		countryadd = buf.toString();
    	}
    	//<time>2008-06-05T14:56:59Z</time>
    	else if(localName.equals("time")){
    		timestr = buf.toString();
    	}
    	else if (localName.equals("ele")) {
            ele = Double.parseDouble(buf.toString());
        }
    	else if (localName.equals("name")) {
            name = buf.toString();
        }
    	else if (localName.equals("desc")) {
            desc = buf.toString();
        }
    	else if (localName.equals("sym")) {
            sym = buf.toString();
        }
    	else if (localName.equals("type")) {
            type = buf.toString();
        }
    	else if(localName.equals("ImagePath")&&(iswaypointextension))
    		imagepath = buf.toString();
    	else if(localName.equals("WaypointExtension"))
    		iswaypointextension = false;
    	else if(localName.equals("extensions"))
    		isextension = false;
    }
    
    @Override
    public void characters(char ch[], int start, int length)
    throws SAXException {
           buf.append(ch, start, length);
    }

	void readWayPoint(InputStream in) throws IOException {
        try {
        	/* Get a SAXParser from the SAXPArserFactory. */
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            ReadParseGPXFile reader = new ReadParseGPXFile();
            parser.parse(in, reader);
           // return reader.getTrack();
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        }
    }

    void readWayPoint(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            readWayPoint(in);
        } finally {
            in.close();
        }
    }
    
  //  private Waypoint[] getTrack() {
  //      return waypt.toArray(new Waypoint[waypt.size()]);
  //  }

    void ReadGPXFile(List<Waypoint> mwaypt){
    	//String fname = "/sdcard/gpx_files/" + "fells_loop.gpx";
    	//File myFile = new File("/sdcard/gpx_files/" + "fells_loop.gpx");
    	File myFile = new File(m_fname);
    	try {
    		waypt.clear();
			readWayPoint(myFile);
			for(int j=0; j<waypt.size(); j++){
				mwaypt.add(waypt.get(j));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    void setFileName(String fname){
    	m_fname = fname;
    }
/*	void ReadGPXFile(){
		BufferedReader bufreader = null;
        try {
        	File myFile = new File("/sdcard/gpx_files/" + "fells_loop.gpx");
			FileReader fileReader = new FileReader(myFile);
			
			bufreader = new BufferedReader(fileReader);
			
			String line = null;
			while ((line = bufreader.readLine()) != null){
				double db1 = Double.valueOf(line.trim()).doubleValue();
				double db2 = db1 * 2;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            if (bufreader != null) {
                try {
                    bufreader.close();
                } catch (IOException e) {
                    // swallow
                }
            }
        }
	}*/
		
}
