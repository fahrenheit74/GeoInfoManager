package com.eleksheep.gim;

public class LocationInfo {

	private double lat = 0.0;
	private double lng = 0.0;
	private double elv = 0.0;
	
	void SetLat(double dlat){
		lat = dlat;
	}
	void SetLng(double dlng){
		lng = dlng;
	}
	void SetElv(double delv){
		elv = delv;
	}
	
	double GetLat(){
		return lat;
	}
	double GetLng(){
		return lng;
	}
	double GetElv(){
		return elv;
	}
}
