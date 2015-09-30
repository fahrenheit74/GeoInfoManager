package com.eleksheep.gim;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eleksheep.gim.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class PointItemizedOverlay extends ItemizedOverlay<OverlayItem>{
	
	private final List<PointOverlayItem> items;
    private final Context context;

	public PointItemizedOverlay(final List<PointOverlayItem> items, final Drawable defaultMarker, final Context context) {
		super(boundCenterBottom(defaultMarker));
		this.items = items;
        this.context = context;
		populate();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected OverlayItem createItem(int i) {
		return this.items.get(i);
	}
	@Override
    protected boolean onTap(final int i) {
		final PointData ptdata = this.items.get(i).PtData;
		final GeoPoint gpt = this.items.get(i).point;
		
		LayoutInflater inflater = LayoutInflater.from(this.context);
        View bView = inflater.inflate(R.layout.pointinfo, null);
        TextView title = (TextView) bView.findViewById(R.id.point_name);
        title.setText(ptdata.title);
        TextView date = (TextView) bView.findViewById(R.id.point_date);
        date.setText(ptdata.dateString);
        TextView ptlat = (TextView) bView.findViewById(R.id.point_lat);
        ptlat.setText(Location.convert((gpt.getLatitudeE6()/1E6), Location.FORMAT_SECONDS));
        TextView ptlng = (TextView) bView.findViewById(R.id.point_lng);
        ptlng.setText(Location.convert((gpt.getLongitudeE6()/1E6), Location.FORMAT_SECONDS));
        
        new AlertDialog.Builder(this.context).setView(bView).setPositiveButton("More Detail",
                new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						di.dismiss();}
				
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface di, int what) {
                    di.dismiss();
                }
            }).show();

		return true;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		 return this.items.size();
	}
	
	@Override
    public void draw(Canvas canvas, MapView mapView, boolean b) {
        super.draw(canvas, mapView, false);    
    }

}
