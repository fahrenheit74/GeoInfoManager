package com.eleksheep.gim;

import com.eleksheep.gim.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Report extends Activity {
	private static final String KEYREP = "KEY_REP";
	private static final int MENU_SAVE_REPORT = 101;
	private String szRep;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
		final TextView reptextvw = (TextView)findViewById(R.id.rpt_output);
		
		final Bundle bun = getIntent().getExtras();
		szRep = bun.getString(KEYREP);
		reptextvw.setText(szRep);
	}
	 @Override
	    public boolean onCreateOptionsMenu(final Menu menu) {
	        super.onCreateOptionsMenu(menu);
	        menu.add(1, Report.MENU_SAVE_REPORT, 0, "Save Report").setIcon(android.R.drawable.ic_menu_save); 
	        return true;
	    }
	 @Override
	 public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        switch (item.getItemId()) {
            case Report.MENU_SAVE_REPORT:
            	Intent isaverep = new Intent(this,SaveReport.class);
            	Bundle bun = new Bundle();
            	bun.putString(KEYREP, szRep);
            	isaverep.putExtras(bun);
    			startActivity(isaverep);
    			break;
        }
        return true;
	 }
}
