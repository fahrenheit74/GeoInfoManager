package com.eleksheep.gim;

public class CurrentSat {

	private int prn = 0;
	private float azi = 0;  //0-360
	private float elv = 0;	//0-90
	private float snr = 0;  //0-60?
	private boolean bused = false;
	
	//-----Sets--------------------------------------------
	
	public void SetPrn(int mprn){
		prn = mprn;
	}
	public void SetAzi(float mazi){
		azi = mazi;
	}
	public void SetElv(float melv){
		elv = melv;
	}
	public void SetSNR(float msnr){
		snr = msnr;
	}
	public void SetUsed(boolean mbused){
		bused = mbused;
	}
	
	//----Gets----------------------------------------------
	
	public int GetPrn(){
		return prn;
	}
	public float GetAzi(){
		return azi;
	}
	public float GetElv(){
		return elv;
	}
	public float GetSNR(){
		return snr;
	}
	public boolean GetUsed(){
		return bused;
	}
}
