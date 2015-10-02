package com.eleksheep.gim;

import java.text.DecimalFormat;

import android.text.format.Time;

public class SunPosCalculator {
	private static String[] MonthStr = { "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec" };
	 private static final int DAY_MILLIS = 24 * 60 * 60 * 1000;
//	 private static final double DARK = Double.NaN;
//   public static final double ASTRONOMICAL_TWILIGHT = -18;
	 public static final double NAUTICAL_TWILIGHT = -12;
	 public static final double CIVIL_TWILIGHT = -6;
	 @SuppressWarnings("unused")
	 private double twilight = CIVIL_TWILIGHT;
	 @SuppressWarnings("unused")
	 private boolean updated = false;
	 //Time at which the Sun is at its highest during a day, in milliseconds since January 1st, 1970.
	 @SuppressWarnings("unused")
	 private long noonTime;
	 
	 private double latitude, longitude = 0.0;
	 private double elevation= 0.0;
	 private double azimuth = 0.0;
	 private long time = System.currentTimeMillis();
	 DecimalFormat leadingzeros = new DecimalFormat("00.0");
	 
// Convert radian angle to degrees
// These functionsa are duplicates to MapMath Class
	
	public double degToRad(double v) {
		return v * Math.PI / 180.0;
	}
	public double radToDeg(double v){
		return v * 180.0 / Math.PI;
	}
	
	public void setLat(double lat){
		latitude = lat;
	}
	public void setLng(double lng){
		longitude = lng*-1;
	}

///Return day of year from time
	public static int getDayOfYear(Time time){
		return time.yearDay;
	}
///Return day of week
	public static String getDayofWeek(Time time){
		final String[] dayofweek = { "Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
		return dayofweek[time.weekDay];
	}
///Return Julian Day
	public double getJD(Time time){
		boolean ignoreDst = true;
		long ms = time.toMillis(ignoreDst );
		double jd = Time.getJulianDay(ms, time.gmtoff); 
		
		int hour = time.hour;
		int min = time.minute;
		int sec = time.second;
		double hr = hour + (min/60.00) + (sec/3600.00)- (time.gmtoff/3600);
		double decday = (hr/24.0000) -0.5;

		jd = jd + decday;
		return jd;
	}
	
///Return Date from Julian Day
	public String getDateFromJD(int jd, boolean includeyear){
		double z = Math.floor(jd+0.5);
		double f = (jd + 0.5) -z;
		double A = 0.0;
		if(z < 2299161)
			A = z;
		else{
			double alpha = Math.floor((z-1867216.25)/36524.25);
			A = z + 1 + alpha - Math.floor(alpha/4);
		}
		double B = A + 1524;
		double C = Math.floor((B - 122.1)/365.25);
		double D = Math.floor(365.25 * C);
		double E = Math.floor((B - D)/30.6001);

		int day = (int)(B - D - Math.floor(30.6001 * E) + f);
		int month = (int)((E < 14) ? E - 1 : E - 13);
		int year = (int)((month > 2) ? C - 4716 : C - 4715);
		String szdate;
		if(includeyear)
			szdate = Integer.toString(day) + "-" + MonthStr[month-1] + "-" + Integer.toString(year);
		else
			szdate = Integer.toString(day) + "-" + MonthStr[month-1];
		return szdate;
	}
	
///Return Julian Century
	public double  getJulianCent(double jd)
	{
		return (jd - 2451545.0)/36525.0;
	}

	///Return Julian date from J Century
	public double getJulianDatefromCent(double jcent){
		int JD = (int)(jcent * 36525.0 + 2451545.0);
		return JD;
	}
	
	///Calculates the Geometric Mean Anomaly of the Sun.
    //@param  t number of Julian centuries since J2000.
    //@return Geometric Mean Anomaly of the Sun in degrees.

    private double sunGeometricMeanAnomaly(final double jcent) {
        return 357.52911 + jcent * (35999.05029 - 0.0001537*jcent);
    }
	
	///Calculates the equation of center for the sun. This value is a correction to add to the geometric mean longitude in order to get the "true" longitude of the sun.
	//@param  t number of Julian centuries since J2000.
	//@return Equation of center in degrees.

	private double sunEquationOfCenter(final double jcent) {	
		final double m = degToRad(sunGeometricMeanAnomaly(jcent));
	    return Math.sin(1*m) * (1.914602 - jcent*(0.004817 + 0.000014*jcent)) +
	    Math.sin(2*m) * (0.019993 - jcent*(0.000101)) + Math.sin(3*m) * (0.000289);
	}
///Calculates the true longitude of the sun. This the geometric mean longitude plus a correction factor ("equation of center" for the sun).
	//@param  t number of Julian centuries since J2000.
	//@return Sun's true longitude in degrees.
	private double sunTrueLongitude(final double jcent) {
		return sunGeometricMeanLongitude(jcent) + sunEquationOfCenter(jcent); 
	}
	
/// Calculates the apparent longitude of the sun.
	//@param  t number of Julian centuries since J2000.
	//@return Sun's apparent longitude in degrees.
	private double sunApparentLongitude(final double jcent) {

	    final double omega = degToRad(125.04 - 1934.136 * jcent);
	         return sunTrueLongitude(jcent) - 0.00569 - 0.00478 * Math.sin(omega);
	 }
    
/// Calculate the Geo-Mean Longitude from julian Cent
	public double sunGeometricMeanLongitude(double jcent)	
	{
		double L0 = 280.46646 + jcent * (36000.76983 + 0.0003032 * jcent);
		L0 -= 360 * Math.floor(L0 / 360);
		return L0;		// in degrees
	}

///Calculates the eccentricity of earth's orbit. 
	//This is the ratio {@code (a-b)/a} where <var>a</var> is the semi-major axis length
	// and <var>b</var> is the semi-minor axis length. Value is 0 for a
	//circular orbit.
	//@param  t number of Julian centuries since J2000.
	// @return The unitless eccentricity.
	private double eccentricityEarthOrbit(final double jcent) {
	    return 0.016708634 - jcent*(0.000042037 + 0.0000001267*jcent);
	}

/// Calculates the distance to the sun in Astronomical Units (AU).
//	private double sunRadiusVector(final double jcent) {
//         final double v = degToRad(sunTrueAnomaly(jcent));
//         final double e = eccentricityEarthOrbit(jcent);
//	         return (1.000001018 * (1 - e*e)) / (1 + e*Math.cos(v));
//	}
	
///Calculates the mean obliquity of the ecliptic.
	private double meanObliquityOfEcliptic(final double jcent) {
		 final double seconds = 21.448 - jcent*(46.8150 + jcent*(0.00059 - jcent*(0.001813)));
		 return 23.0 + (26.0 + (seconds/60.0))/60.0;
	}
	
///Calculates the corrected obliquity of the ecliptic.
	private double obliquityCorrected(final double jcent) {
		    final double e0 = meanObliquityOfEcliptic(jcent);
		    final double omega = Math.toRadians(125.04 - 1934.136*jcent);
		    return e0 + 0.00256 * Math.cos(omega);
		 }
///Calculates the right ascension of the sun. Similar to the angular system used to define latitude and longitude on Earth's surface, right ascension
///is roughly analogous to longitude, and defines an angular offset from the meridian of the vernal equinox.
	//return Sun's right ascension in degrees.
	 @SuppressWarnings("unused")
	 private  double sunRightAscension(final double jcent) {
         final double e = degToRad(obliquityCorrected(jcent));
         final double b = degToRad(sunApparentLongitude(jcent));
         final double y = Math.sin(b) * Math.cos(e);
         final double x = Math.cos(b);
         final double alpha = Math.atan2(y, x);
         return radToDeg(alpha);
     }
///Calculates the declination of the sun. Declination is analogous to latitude
	 //return Sun's declination in degrees.
	 private double sunDeclination(final double jcent) {
      	final double e = degToRad(obliquityCorrected(jcent));
        final double b = degToRad(sunApparentLongitude(jcent));
        final double sintheta = Math.sin(e) * Math.sin(b);
        final double theta = Math.asin(sintheta);
        return radToDeg(theta);
     }
///Calculates the Universal Coordinated Time (UTC) of solar noon for the given day at the given location on earth.
	 private double solarNoonTime(final double lon, final double eqTime) {
         return 720.0 + (lon * 4.0) - eqTime;
     }
	 
	 private double equationOfTime(final double jcent) {
         double eps = degToRad(obliquityCorrected(jcent));
         double L0  = degToRad(sunGeometricMeanLongitude(jcent));
         double m   = degToRad(sunGeometricMeanAnomaly(jcent));
         double e   = eccentricityEarthOrbit(jcent);
         double y   = Math.tan(eps/2);
         y *= y;
         double sin2l0 = Math.sin(2 * L0);
         double cos2l0 = Math.cos(2 * L0);
         double sin4l0 = Math.sin(4 * L0);
         double sin1m  = Math.sin(m);
         double sin2m  = Math.sin(2 * m);
         double etime = y*sin2l0 - 2*e*sin1m + 4*e*y*sin1m*cos2l0 - 0.5*y*y*sin4l0 - 1.25*e*e*sin2m;
         return radToDeg(etime) * 4.0;
     }
	 
	 private double refractionCorrection(final double zenith) {
         final double exoatmElevation = 90 - zenith;
         if (exoatmElevation > 85) {
             return 0;
         }

         final double refractionCorrection; // In minute of degrees
         final double te = Math.tan(degToRad(exoatmElevation));

         if (exoatmElevation > 5.0) {

             refractionCorrection = 58.1/te - 0.07/(te*te*te) + 0.000086/(te*te*te*te*te);

         } else {

             if (exoatmElevation > -0.575) {
                 refractionCorrection =  1735.0 + exoatmElevation * (-518.2 + exoatmElevation *
                                        ( 103.4 + exoatmElevation * (-12.79 + exoatmElevation * 0.711)));
             } 
             else {
                 refractionCorrection = -20.774 / te;
             }
         }
         return refractionCorrection / 3600;

     }
	 
	 /// Constructs a sun relative position calculator.

	     public SunPosCalculator() { }
	     
	     public SunPosCalculator(final double twilight) throws IllegalArgumentException {
	    	         setTwilight(twilight);
	    	     }
		private void setTwilight(double twilight) throws IllegalArgumentException  {
	        if (twilight<-90 || twilight>-90) {
	        	throw new IllegalArgumentException(String.valueOf(twilight));
	        }
	        this.twilight = twilight;	
	        this.updated = false;
		}
		public double getTwilight(double twilight){
			return twilight;
		}
		
		public void setPosCalculator(double lat, double lng){
			longitude = lng*-1;
			latitude = lat;
		}
		
		public double getAz(Time tm){
			calc(tm);
			return azimuth;
		}
		public double getElv(Time tm){
			calc(tm);
			return elevation;
		}
		public double getMoonAge(Time tm){
			double jDay = getJD(tm);
			return MoonAge(jDay);
		}
		
		private double MoonAge(Double jDay)
		{ 
		    double ip = (jDay + 4.867) / 29.53059;
		    ip = ip - Math.floor(ip); 
		    //After several trials I've seen to add the following lines, 
		    //which gave the result was not bad 
		    double moonage = 0.0;
		    if(ip < 0.5)
		    	moonage = ip * 29.53059 + 29.53059 / 2;
		    else
		    	moonage = ip * 29.53059 - 29.53059 / 2;
		    // Moon's age in days
		    moonage = Math.round(moonage);
		    return moonage;
		}
		
		private void calc(Time time){
			double jDay = getJD(time);
			final double tm      = (jDay-2451545)/36525;
			
			double solarDec = sunDeclination(tm);
			double eqTime   = equationOfTime(tm);
			noonTime   = Math.round(solarNoonTime(longitude, eqTime) * (60*1000)) + (this.time/DAY_MILLIS)*DAY_MILLIS;		
			double trueSolarTime = ((jDay + 0.5) - Math.floor(jDay + 0.5)) * 1440;
			trueSolarTime += (eqTime - 4.0*longitude); // Correction in minutes.
			trueSolarTime -= 1440 * Math.floor(trueSolarTime / 1440);
			
			double longitude_rad = degToRad(longitude);
			double latitude_rad  = degToRad(latitude );
			solarDec  = degToRad(solarDec );
			
			double csz = Math.sin(latitude_rad) *
				Math.sin(solarDec) +
				Math.cos(latitude_rad) *
				Math.cos(solarDec) *
				Math.cos(degToRad(trueSolarTime/4 - 180));
			
		     if (csz > +1) csz = +1;
		     if (csz < -1) csz = -1;
		     final double zenith  = Math.acos(csz);
		     final double azDenom = Math.cos(latitude_rad) * Math.sin(zenith);
		
		     //Calculate Azimuth
		     if (Math.abs(azDenom) > 0.001) {

	             double azRad = ((Math.sin(latitude_rad)*Math.cos(zenith)) - Math.sin(solarDec)) / azDenom;

	             if (azRad > +1) azRad = +1;

	             if (azRad < -1) azRad = -1;

	             azimuth = 180 - radToDeg(Math.acos(azRad));

	             if (trueSolarTime > 720) { // 720 minutes == 12 hours
	                 azimuth = -azimuth;
	             }
		      } 
			  else {
		             azimuth = (latitude>0) ? 180 : 0;
		      }
		  
		     azimuth -= 360 * Math.floor(azimuth / 360);
		   //Elevation
			double refractCorrection = refractionCorrection(zenith);
			double solarZen = radToDeg(zenith) - refractCorrection;
			elevation = 90 - solarZen;
		}
		double calcHourAngleSunriseSunset(double lat, double solarDec, boolean sunriseorsunset)
		{
			double latRad = degToRad(lat);
			double sdRad  = degToRad(solarDec);

			//contant value of 90.833 converted to radians

//			double HAarg = (cos(D2R*90.833)/(cos(latRad)*cos(sdRad))-tan(latRad) * tan(sdRad));

			double HA = (Math.acos(Math.cos(degToRad(90.833))/(Math.cos(latRad)*Math.cos(sdRad))-Math.tan(latRad) * Math.tan(sdRad)));

			if(sunriseorsunset)
				HA = HA * -1;

			return HA;		// in radians
		}
		
		
		double calcSolNoonUTC(double jCentTime, double jd, double lng)
		{
			// First pass uses approximate solar noon to calculate eqtime
			double tnoon = getJulianCent(jd + lng/360.0);
			double eqTime = equationOfTime(tnoon);
			double solNoonUTC = 720 + (lng * 4) - eqTime; // min

			double newtime = getJulianCent(jd -0.5 + solNoonUTC/1440.0); 

			eqTime = equationOfTime(newtime);
			solNoonUTC = 720 + (lng * 4) - eqTime; // min

			return solNoonUTC;
		}
		
		String getSunRiseUTC(Time tm)
		{
			//double latitude = 38.0;
			//double longitude = 83.0;
			double jd = getJD(tm);
			double jCentTime = getJulianCent(jd);
			double noonmin = calcSolNoonUTC(jCentTime,jd, longitude);
			double noontime = getJulianCent(jd+noonmin/1440.0);

			double eqTime = equationOfTime(noontime);
			double solarDec = sunDeclination(noontime);
			double hourAngle = calcHourAngleSunriseSunset(latitude, solarDec, true);  //Sunset

			double delta = longitude - radToDeg(hourAngle);
			double timeDiff = 4 * delta;
			double timeUTC = 720 + timeDiff - eqTime;

			// first pass used to include fractional day in gamma calc
			double newt = getJulianCent(jd + timeUTC/1440.0); 
			eqTime = equationOfTime(newt);
			solarDec = sunDeclination(newt);
			hourAngle = calcHourAngleSunriseSunset(latitude, solarDec, false);

			delta = longitude - radToDeg(hourAngle);
			timeDiff = 4 * delta;
			timeUTC = 720 + timeDiff - eqTime; // in minutes	*/
			
			double hh = timeUTC /60.000;
			double mm = (hh - Math.floor(hh)) * 60;
			hh = Math.floor(hh);
			hh = hh + tm.gmtoff/3600;
			double ss = (mm - Math.floor(mm))* 60;
			mm = Math.floor(mm);

			int hr = (int)hh;
			int min = (int)mm;
			String seconds = leadingzeros.format(ss);
			
			return String.format("%s%d:%d:%s", "Sunrise: ",hr,min,seconds);
		}
		String getSunSetUTC(Time tm)
		{	
			double jd = getJD(tm);
			//double latitude = 38.0;
			//double longitude = 83.0;
			double jCentTime = getJulianCent(jd);
			double noonmin = calcSolNoonUTC(jCentTime,jd, longitude);
			double noontime = getJulianCent(jd+noonmin/1440.0);

			double eqTime = equationOfTime(noontime);
			double solarDec = sunDeclination(noontime);
			double hourAngle = calcHourAngleSunriseSunset(latitude, solarDec, true);  //Sunset

			double delta = longitude - radToDeg(hourAngle);
			double timeDiff = 4 * delta;
			double timeUTC = 720 + timeDiff - eqTime;

			// first pass used to include fractional day in gamma calc
			double newt = getJulianCent(jd + timeUTC/1440.0); 
			eqTime = equationOfTime(newt);
			solarDec = sunDeclination(newt);
			hourAngle = calcHourAngleSunriseSunset(latitude, solarDec, true);

			delta = longitude - radToDeg(hourAngle);
			timeDiff = 4 * delta;
			timeUTC = 720 + timeDiff - eqTime; // in minutes	
			//String tmzonebias = Time.getCurrentTimezone();
			
			double hh = timeUTC /60.000;
			double mm = (hh - Math.floor(hh)) * 60;
			hh = Math.floor(hh);
			hh = hh + tm.gmtoff/3600;
			
			double ss = (mm - Math.floor(mm))* 60;
			mm = Math.floor(mm);
			
			int hr = (int)hh;
			int min = (int)mm;
			String seconds = leadingzeros.format(ss);
			
			return String.format("%s%d:%d:%s", "Sunset: ",hr,min,seconds);
		}
		
	
}




	





