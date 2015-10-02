package com.eleksheep.gim;

import com.eleksheep.gim.R;

import android.app.Activity;
import android.content.Context;
//import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
//import android.widget.ImageButton;
import android.widget.ImageView;
//import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SymbolGridView extends Activity {
    /** Called when the activity is first created. */
	
	private GridView gridview;
	private static final String KEY_SYMBOLS = "KEY_SYMBOLS";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symbolgridview);
        
        final SharedPreferences mPrefs;
        mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
        final ImageView symcurrent = (ImageView)findViewById(R.id.symcurrent);
        
        symcurrent.setImageResource(mPrefs.getInt(KEY_SYMBOLS, R.drawable.mapsym_1));
        
        gridview = (GridView) findViewById(R.id.symbolgridview);
        gridview.setAdapter(new SymbolImageAdapter(this));
        
        gridview.setOnItemClickListener(new OnItemClickListener() 
        {
            @SuppressWarnings("unchecked")
			public void onItemClick(AdapterView parent, 
            View v, int position, long id) 
            {                
                Editor mPrefsEditor = mPrefs.edit();
                SymbolImageAdapter symImgAdp = new SymbolImageAdapter(null);
        		mPrefsEditor.putInt(KEY_SYMBOLS, symImgAdp.getSymbolDrawId(position));
        		mPrefsEditor.commit();
        		symcurrent.setImageResource(mPrefs.getInt(KEY_SYMBOLS, R.drawable.mapsym_1));
            }
        });        
    }
}

