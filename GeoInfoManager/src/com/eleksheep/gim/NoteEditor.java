package com.eleksheep.gim;

import com.eleksheep.gim.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.view.KeyEvent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEditor extends Activity {
	//private static final String KEYRETURNACTIVITYCODE = "KEY_RETURN_ACTIVITY_CODE";
	private static final String KEYRETURNDESCRIPTION = "KEY_RETURN_DESCRIPTION";
	private static final String KEYDESCRIPTION = "KEY_DESCRIPTION";
	private EditText descriptionText;
	private String description;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_editor);
		
		final Button returnbtn = (Button)findViewById(R.id.noted_returnbtn);
		descriptionText = (EditText)findViewById(R.id.noted_body);
		
		final Bundle bun = getIntent().getExtras();
		description = bun.getString(KEYDESCRIPTION);
		descriptionText.setText(description);
		
		returnbtn.setOnClickListener(new View.OnClickListener(){
	        public void onClick(View view){
	            //create a new intent...
	            Intent intent = new Intent();
	            //add "returnKey" as a key and assign it the value
	            //in the textbox...
	            Bundle bun = new Bundle();
	            bun.putString(KEYRETURNDESCRIPTION, descriptionText.getText().toString());
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
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
   	 if (keyCode == KeyEvent.KEYCODE_BACK) {
   		 Intent intent = new Intent();
   		 Bundle bun = new Bundle();
         bun.putString(KEYRETURNDESCRIPTION, "");
   		 intent.putExtras(bun);
   		 setResult(RESULT_OK,intent);
   	  	 finish();
   	 } 
       	 return super.onKeyDown(keyCode, event);
    }
}

