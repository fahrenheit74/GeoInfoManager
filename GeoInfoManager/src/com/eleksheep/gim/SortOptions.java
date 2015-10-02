package com.eleksheep.gim;

import com.eleksheep.gim.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class SortOptions extends Activity {
	
	String SortOrder = "_ID ASC";
	private static final String KEYSORTORDER = "KEY_SORTORDER";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sort_options);
		
		final RadioButton radio_ascdist = (RadioButton) findViewById(R.id.srtopt_ascdist);
		final RadioButton radio_dscdist = (RadioButton) findViewById(R.id.srtopt_dscdist);
		final RadioButton radio_ascname = (RadioButton) findViewById(R.id.srtopt_ascname);
		final RadioButton radio_dscname = (RadioButton) findViewById(R.id.srtopt_dscname);
		final RadioButton radio_ascid = (RadioButton) findViewById(R.id.srtopt_ascid);
		final RadioButton radio_dscid = (RadioButton) findViewById(R.id.srtopt_dscid);
		final Button returnbtn = (Button)findViewById(R.id.srtopt_returnbtn);
		radio_dscid.setOnClickListener(radio_listener);
		radio_ascid.setOnClickListener(radio_listener);
		radio_dscname.setOnClickListener(radio_listener);
		radio_ascname.setOnClickListener(radio_listener);
		radio_dscdist.setOnClickListener(radio_listener);
		radio_ascdist.setOnClickListener(radio_listener);
		
		final Bundle bun = getIntent().getExtras();
		SortOrder  = bun.getString(KEYSORTORDER);
		
		if(SortOrder.contentEquals("_ID ASC"))
			radio_ascid.setChecked(true);
		if(SortOrder.contentEquals("_ID DESC"))
			radio_dscid.setChecked(true);
		if(SortOrder.contentEquals("PTNAME ASC"))
			radio_ascname.setChecked(true);
		if(SortOrder.contentEquals("PTNAME DESC"))
			radio_dscname.setChecked(true);
		
		
		returnbtn.setOnClickListener(new View.OnClickListener(){
	        public void onClick(View view){
	            //create a new intent...
	            Intent intent = new Intent();
	            //add "returnKey" as a key and assign it the value
	            //in the textbox...
	            Bundle bun = new Bundle();
	            bun.putString(KEYSORTORDER, SortOrder);
				intent.putExtras(bun);

	            //get ready to send the result back to the caller (MainActivity)
	            //and put our intent into it (RESULT_OK will tell the caller that
	            //we have successfully accomplished our task..
	            setResult(RESULT_OK,intent);
	            //close this Activity...
	            finish();
	        }
	    });
	}
		OnClickListener radio_listener = new OnClickListener() {
	    public void onClick(View v) {

	        RadioButton rb = (RadioButton) v;


	        String szSortOrder = rb.getText().toString();
	        if(szSortOrder.contentEquals("Ascend by Point Id"))
	        	SortOrder = "_ID ASC";
	        if(szSortOrder.contentEquals("Descend by Point Id"))
	        	SortOrder = "_ID DESC";
	        if(szSortOrder.contentEquals("Ascend by Point Name"))
	        	SortOrder = "PTNAME ASC";
	        if(szSortOrder.contentEquals("Descend by Point Name"))
	        	SortOrder = "PTNAME DESC";
	    }
	};
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
   	 if (keyCode == KeyEvent.KEYCODE_BACK) {
   		 Intent intent = new Intent();
   		 Bundle bun = new Bundle();
   		 bun.putString(KEYSORTORDER, SortOrder);
   		 intent.putExtras(bun);
   		 setResult(RESULT_OK,intent);
   	  	 finish();
   	 } 
       	 return super.onKeyDown(keyCode, event);
    }
		
}
