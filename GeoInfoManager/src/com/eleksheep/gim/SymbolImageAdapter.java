package com.eleksheep.gim;

import com.eleksheep.gim.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class SymbolImageAdapter extends BaseAdapter  {
	private Context mContext;

	@Override
	public int getCount() {
		return mThumbIds.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public SymbolImageAdapter(Context c) {
        mContext = c;
    }
	public int getSymbolDrawId(int position){
		return mThumbIds[position];
	}
	
	@Override
	// create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(36, 36));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

	// references to our images
    private Integer[] mThumbIds = {
            
            R.drawable.mapsym_1, R.drawable.mapsym_2,R.drawable.mapsym_3, R.drawable.mapsym_4,R.drawable.mapsym_5,
            R.drawable.mapsym_6, R.drawable.mapsym_7,R.drawable.mapsym_8, R.drawable.mapsym_9,R.drawable.mapsym_10,
            R.drawable.mapsym_11, R.drawable.mapsym_12,R.drawable.mapsym_13, R.drawable.mapsym_14,R.drawable.mapsym_15,
            R.drawable.mapsym_16, R.drawable.mapsym_17,R.drawable.mapsym_18, R.drawable.mapsym_19,R.drawable.mapsym_20,
            R.drawable.mapsym_21, R.drawable.mapsym_22,R.drawable.mapsym_23, R.drawable.mapsym_24,R.drawable.mapsym_25,
            R.drawable.mapsym_26, R.drawable.mapsym_27,R.drawable.mapsym_28, R.drawable.mapsym_29,R.drawable.mapsym_30,
            R.drawable.mapsym_31, R.drawable.mapsym_32,R.drawable.mapsym_33, R.drawable.mapsym_34,R.drawable.mapsym_35,
            R.drawable.mapsym_36, R.drawable.mapsym_37,R.drawable.mapsym_38, R.drawable.mapsym_39,R.drawable.mapsym_40,
            R.drawable.mapsym_41, R.drawable.mapsym_42,R.drawable.mapsym_43, R.drawable.mapsym_44,R.drawable.mapsym_45,
            R.drawable.mapsym_46, R.drawable.mapsym_47,R.drawable.mapsym_48, R.drawable.mapsym_49,R.drawable.mapsym_50
            
    };

}
