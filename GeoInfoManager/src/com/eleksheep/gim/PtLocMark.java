package com.eleksheep.gim;


public class PtLocMark {
	String m_name = "";
	int m_marker;
	double m_latitude = 0.0;
	double m_longitude = 0.0;
	String m_timestr = "";
	String m_addline1 = "";
	String m_addline2 = "";
	String m_addline3 = "";
	String m_addStreet = "";
	String m_addCity = "";
	String m_addState = "";
	String m_addCountry = "";
	String m_addPostalCode = "";
	String m_imagepath = "";
	
	void setMarker(int marker){
		m_marker = marker;
	}
	void setTimeString(String timestr){
		m_timestr = timestr;
	}
	void setName(String name){
		m_name = name;
	}
	void setLatitude(double latitude){
		m_latitude = latitude;
	}
	void setLongitude(double longitude){
		m_longitude = longitude;
	}
	void setAddStreet(String addStreet){
		m_addStreet = addStreet;
	}
	void setAddCity(String addCity){
		m_addCity = addCity;
	}
	void setAddState(String addState){
		m_addState = addState;
	}
	void setAddCountry(String addCountry){
		m_addCountry = addCountry;
	}
	void setAddPostalCode(String addPostalCode){
		m_addPostalCode = addPostalCode;
	}
	void setImagePath(String imgPath){
		m_imagepath = imgPath;
	}
	int getMarker(){
		return m_marker;
	}
	
}
