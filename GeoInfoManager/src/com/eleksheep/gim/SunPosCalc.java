package com.eleksheep.gim;

import java.text.DecimalFormat;

import com.eleksheep.gim.R;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.ImageView;
import android.widget.TextView;

public class SunPosCalc extends Activity {
    /** Called when the activity is first created. */
	
	private static final String KEYLAT = "KEY_LAT";
	private static final String KEYLNG = "KEY_LNG";
	private double dlat = 0.0;
	private double dlng = 0.0;
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sunposition);
        
        final Bundle bun = getIntent().getExtras();
        dlat = bun.getDouble(KEYLAT);
        dlng = bun.getDouble(KEYLNG);
        
        final TextView txtlatitude = (TextView)findViewById(R.id.spc_latitude_text);
        final TextView txtlongitude = (TextView)findViewById(R.id.spc_longitude_text);
        final ImageView moonphaseview =(ImageView)findViewById(R.id.moon_phase_view);
        final TextView txtmoonphrase1 =(TextView)findViewById(R.id.moon_phrase1);
        final TextView txtmoonphrase2 =(TextView)findViewById(R.id.moon_phrase2);
        final TextView sunrise_TextView =(TextView)findViewById(R.id.sunrise_text);
        final TextView sunset_TextView =(TextView)findViewById(R.id.sunset_text);
        final TextView sunelevation = (TextView)findViewById(R.id.sun_elevation_text);
        final TextView sunazimuth = (TextView)findViewById(R.id.sun_azimuth_text);

        final SunPosCalculator spc = new SunPosCalculator();

		Time time = new Time();
	    time.setToNow();
	    spc.setLat(dlat);
	    spc.setLng(dlng);
	    double moonage = spc.getMoonAge(time);
	    if(moonage == 30)
	    	moonage = 1;
	    //Calculate Moon Phase.
	    String moonphrase = "";
	    String moonphrase2 = "";
	    switch ((int)moonage){
    	    case 0:
    	    case 29:
    	    	moonphrase = mMoonPhrases[0];
    	    	break;
    	    case 1:
    	    case 2:
    	    case 3:
    	    case 4:
    	    case 5:
    	    	moonphrase = mMoonPhrases[1];
    	    	break;
    	    case 6:
    	    	moonphrase = mMoonPhrases[2];  //First Quarter
    	    	break;
    	    case 7:
    	    case 8:
    	    case 9:
    	    case 10:
    	    case 11:
    	    case 12:
    	    case 13:
    	    case 14:
    	    	moonphrase = mMoonPhrases[3];  //Waxing Gibbous
    	    	break;
    	    case 15:
    	    	moonphrase = mMoonPhrases[4];  //Moon Full
    	    	break;
    	    case 16:
    	    case 17:
    	    case 18:
    	    case 19:
    	    case 20:
    	    case 21:
    	    	moonphrase = mMoonPhrases[5];  //Waxing Gibbous
    	    	break;
    	    case 22:
    	    	moonphrase = mMoonPhrases[6];  //Third Quarter
    	    	break;
    	    case 23:
    	    case 24:
    	    case 25:
    	    case 26:
    	    case 27:
    	    case 28:
    	    	moonphrase = mMoonPhrases[7];  //Waxing Gibbous
    	    	break;
	    };
	    moonphrase2 = String.format("%s%d", "Day - ",(int)moonage);
	    txtmoonphrase1.setText(moonphrase);
	    txtmoonphrase2.setText(moonphrase2);
	    moonphaseview.setImageResource(mMoonPhasePhotos[(int)moonage]);
	    DecimalFormat twoPlaces = new DecimalFormat("0.00");

	    double az = spc.getAz(time);
	    double elv = spc.getElv(time);   
	    spc.setPosCalculator(dlat, dlng);
	    sunrise_TextView.setText(spc.getSunRiseUTC(time));
	    spc.setPosCalculator(dlat, dlng);
	    sunset_TextView.setText(spc.getSunSetUTC(time));
	    
	    txtlatitude.setText("Latitude: " + Location.convert(dlat, Location.FORMAT_SECONDS));
	    txtlongitude.setText("Longitude: " + Location.convert(dlng, Location.FORMAT_SECONDS));
	    
	    sunelevation.setText("Sun Elevation: " + twoPlaces.format(elv));
	    sunazimuth.setText("Sun Azimuth: " + twoPlaces.format(az));
	}
    
    private Integer[] mMoonPhasePhotos = {
    		R.drawable.moon0, R.drawable.moon1,R.drawable.moon2,R.drawable.moon3,R.drawable.moon4,R.drawable.moon5,R.drawable.moon6,
    		R.drawable.moon7, R.drawable.moon8,R.drawable.moon9,R.drawable.moon10,R.drawable.moon11,R.drawable.moon12,R.drawable.moon13,
    		R.drawable.moon14, R.drawable.moon15,R.drawable.moon16,R.drawable.moon17,R.drawable.moon18,R.drawable.moon19,R.drawable.moon20,
    		R.drawable.moon21, R.drawable.moon22,R.drawable.moon23,R.drawable.moon24,R.drawable.moon25,R.drawable.moon26,R.drawable.moon27,
    		R.drawable.moon28,R.drawable.moon29
    };
    
    private String[] mMoonPhrases = {
    		"New Moon", "Waxing Crescent", "First Quarter", "Waxing Gibbous" , "Full Moon", "Wanning Gibbous", "Third Quarter", "Wanning Crescent"
    };
}