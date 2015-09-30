package com.eleksheep.gim;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class PointOverlayItem extends OverlayItem {
	
	public final GeoPoint point;
    public final PointData PtData;

	public PointOverlayItem(final GeoPoint pt, final PointData PtData) {
		super(pt, PtData.title, PtData.dateString);
		
		this.PtData = PtData;
		this.point = pt;
	}
}
