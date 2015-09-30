package com.eleksheep.gim;

public class Waypoint {
	private double m_lat=0.0;
	private double m_lng=0.0;
	private double m_elv=0.0;
	private String m_timestr="";
	private String m_name="";
	private String m_sym="";
	private String m_desc="";
	private String m_type="";
	
	private String m_streetadd = "";
	private String m_cityadd = "";
	private String m_stateadd = "";
	private String m_postalcodeadd = "";
	private String m_countryadd = "";
	private String m_imagepath = "";
	
	void set(double lat, double lng, double elv, 
		String name, String symbol, String desc, String type, String timestr,String imagepath){
		m_lat = lat;
		m_lng = lng;
		m_elv = elv;
		m_sym = symbol;
		m_name = name;
		m_desc = desc;
		m_type = type;
		m_timestr = timestr;
		m_imagepath = imagepath;
	}
	void setaddress(String streetadd, String cityadd, String stateadd, String postalcodeadd, String countryadd){
		m_streetadd = streetadd;
		m_cityadd = cityadd;
		m_stateadd = stateadd;
		m_postalcodeadd = postalcodeadd;
		m_countryadd = countryadd;
	}
	void init(){
		m_lat = 0.0;
		m_lng = 0.0;
		m_elv = 0.0;
		
		m_timestr = "";
		m_name = "";
		m_sym = "";
		m_desc = "";
		m_type = "";
		
		m_streetadd = "";
		m_cityadd = "";
		m_stateadd = "";
		m_postalcodeadd = "";
		m_countryadd = "";
	}
	
	String getName(){
		return m_name;
	}
	String getDesc(){
		return m_desc;
	}
	double getLat(){
		return m_lat;
	}
	double getLng(){
		return m_lng;
	}
	double getElv(){
		return m_elv;
	}
	String getType(){
		return m_type;
	}
	String getSymbo(){
		return m_sym;
	}
	String getStreetAdd(){
		return m_streetadd;
	}
	String getCityAdd(){
		return m_cityadd;
	}
	String getStateAdd(){
		return m_stateadd;
	}
	String getPostalCodeAdd(){
		return m_postalcodeadd;
	}
	String getCountryAdd(){
		return m_countryadd;
	}
	String getTimeString(){
		return m_timestr;
	}
	String getImagePath(){
		return m_imagepath;
	}
	
}
