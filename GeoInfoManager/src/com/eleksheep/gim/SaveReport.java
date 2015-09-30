package com.eleksheep.gim;

import java.io.File;
import java.io.FileWriter;

import com.eleksheep.gim.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class SaveReport extends Activity {
	public static final String KEY_REP_DEFSTRING = "KEY_REPDEFSTRING";
	private static final String KEYREP = "KEY_REP";
	 @Override
		public void onResume(){
			super.onResume();
			final SharedPreferences mPrefs;
			final EditText repfilename = (EditText)findViewById(R.id.saverpt_fname);
		    mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
		    String reportFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/gim/reports";
		    File reportFileFolder = new File(reportFilePath);
			reportFileFolder.mkdirs();
			String reportFileName = reportFilePath + "/newreport.txt";
		    String tmpFilePath = mPrefs.getString(KEY_REP_DEFSTRING, "");
		    if(tmpFilePath.length()>0)
		    	repfilename.setText(mPrefs.getString(KEY_REP_DEFSTRING, ""));
		    else
		    	repfilename.setText(reportFileName);
		}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.savereport);
		final Bundle bun = getIntent().getExtras();
		
		final EditText repfilename = (EditText)findViewById(R.id.saverpt_fname);
		final ImageButton saverptBtn = (ImageButton) findViewById(R.id.saverpt_savebtn);
		final ImageButton repbrowseBtn = (ImageButton) findViewById(R.id.saverpt_browse);
		
		repbrowseBtn.setVisibility(View.GONE);
		
		saverptBtn.setOnClickListener(new OnClickListener(){
        	@Override
            public void onClick(View v) {
        		
        		String repfname = repfilename.getText().toString();
        		WriteReport(repfname,bun.getString(KEYREP));
        		//readgpx.setFileName(repfname);
        		//String[] strarr = repfname.split("/");
            }
        }); 
	}
	
	void WriteReport(String fname,String szRep){
		
		FileWriter fWriter;
        try{
             fWriter = new FileWriter(fname);
             fWriter.write(szRep);
             fWriter.flush();
             fWriter.close();
         }catch(Exception e){
                  e.printStackTrace();
         }
	}
	
}
