package com.eleksheep.gim;

import android.location.Location;
import com.google.android.maps.GeoPoint;

public class LocationHelper {
	public static final String CLASSTAG = LocationHelper.class.getSimpleName();

	public static final GeoPoint GOLDEN_GATE = new GeoPoint((int) (37.49 * 1E6),
	        (int) (-122.49 * 1E6));
	//Old Faithful Geyser 
	public static final GeoPoint OLD_FAITHFUL_YS = new GeoPoint((int)(44.460363889 *1E6),(int)(-110.828219444*1E6));

public static GeoPoint getGeoPoint(final Location loc) {
    int lat = (int) (loc.getLatitude() * 1E6);
    int lon = (int) (loc.getLongitude() * 1E6);
    return new GeoPoint(lat, lon);
}
}

