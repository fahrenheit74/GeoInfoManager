package com.eleksheep.gim;

import com.eleksheep.gim.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

public class PointEditor extends Activity {
	private static final String KEYIMAGENAME = "KEY_IMAGE_NAME";
	private static final String KEYPTNAME = "KEY_PTNAME";
	private static final String KEYLAT = "KEY_LAT";
	private static final String KEYLNG = "KEY_LNG";
	private static final String KEYSYMBOL = "KEY_SYMBOL";
    private static final String KEYDESCR = "KEY_DESCR";
    private static final String KEYADDSTREET = "KEY_ADD_STREET";
    private static final String KEYADDCITY = "KEY_ADD_CITY";
    private static final String KEYADDSTATE = "KEY_ADD_STATE";
    private static final String KEYADDPOSTAL = "KEY_ADD_POSTAL";
    private static final String KEYADDCOUNTRY = "KEY_ADD_COUNTRY";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pteditor);
		final ImageView ptimage = (ImageView)findViewById(R.id.pted_image);
		final TextView ptnametxtvw = (TextView)findViewById(R.id.pted_ptname);
		final ImageView ptsymimg = (ImageView)findViewById(R.id.pted_symbol);
		
		final Bundle bun = getIntent().getExtras();
		String imageFilePath = bun.getString(KEYIMAGENAME);
		double dlng = bun.getDouble(KEYLNG);
		double dlat = bun.getDouble(KEYLAT);
	//	String ptname = bun.getString(KEYPTNAME);
	//	String imgname = bun.getString(KEYIMAGENAME);
	//	int symbol = bun.getInt(KEYSYMBOL);
	//	String description = bun.getString(KEYDESCR);
	//	String addstreet = bun.getString(KEYADDSTREET);
	//	String addcity = bun.getString(KEYADDCITY);
	//	String addstate = bun.getString(KEYADDSTATE);
	//	String addcountry = bun.getString(KEYADDCOUNTRY);
	//	String addpostalcode = bun.getString(KEYADDPOSTAL);
		
		ptnametxtvw.setText(bun.getString(KEYPTNAME));
		ptsymimg.setImageResource(bun.getInt(KEYSYMBOL));
		
		if(imageFilePath.length()>1){
			
			 BitmapFactory.Options resample = new BitmapFactory.Options();
			 resample.inSampleSize = 4;

			Bitmap bmp = BitmapFactory.decodeFile(imageFilePath,resample);
			ptimage.setImageBitmap(bmp);
		}
	}
}
