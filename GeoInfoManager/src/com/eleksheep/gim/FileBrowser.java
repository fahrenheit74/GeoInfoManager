package com.eleksheep.gim;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.eleksheep.gim.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
//import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FileBrowser  extends ListActivity {
	private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }
	private boolean isfiletypefound = false;
	public static final String KEY_GPX_DEFSTRING = "KEY_GPXDEFSTRING";

    private final DISPLAYMODE displayMode = DISPLAYMODE.ABSOLUTE;
    private List<String> directoryEntries = new ArrayList<String>();
    private File currentDirectory = new File("/"); 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        // setContentView() gets called within the next line,
        // so we do not need it here.
        browseToRoot(); 
    }
    protected void onListItemClick(ListView l, View v, int position, long id) { 
 	   int selectionRowID = (int)id;
        String selectedFileString = this.directoryEntries.get(selectionRowID);
        String filetypestring = ".gpx";
        int start = selectedFileString.length()-4;
        int indxofstring = selectedFileString.indexOf(filetypestring, start);
        if(indxofstring >0)
     	   isfiletypefound = true;
        else
     	   isfiletypefound = false;
        if (selectedFileString.equals(".")) {
             // Refresh
             this.browseTo(this.currentDirectory);
        } 
        else if(selectedFileString.equals("..")){
             this.upOneLevel();
        } 
        else {
             File clickedFile = null;
             switch(this.displayMode){
                  case RELATIVE:
                       clickedFile = new File(this.currentDirectory.getAbsolutePath()
                                                          + this.directoryEntries.get(selectionRowID));
                       break;
                  case ABSOLUTE:
                       clickedFile = new File(this.directoryEntries.get(selectionRowID));
                       break;
             }
             if(clickedFile != null)
                  this.browseTo(clickedFile);
        }
     }
    
    /**
     * This function browses to the
     * root-directory of the file-system.
     */
    private void browseToRoot() {
    	String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/gim/gpx_files";
    	File imageFileFolder = new File(imageFilePath);
		imageFileFolder.mkdirs();
         browseTo(new File(imageFilePath));
   }
    
    /**
     * This function browses up one level
     * according to the field: currentDirectory
     */
    private void upOneLevel(){
         if(this.currentDirectory.getParent() != null)
              this.browseTo(this.currentDirectory.getParentFile());
    } 
    
    private void browseTo(final File aDirectory){
        if (aDirectory.isDirectory()){
             this.currentDirectory = aDirectory;
             fill(aDirectory.listFiles());
        }else{
		/*	OnClickListener okButtonListener = new OnClickListener(){
                  // @Override
                  public void onClick(DialogInterface arg0, int arg1) {
                            // Lets start an intent to View the file, that was clicked...
                         //   openFile(aDirectory);
                  }
             };
             @SuppressWarnings("unused")
			OnClickListener cancelButtonListener = new OnClickListener(){
                  // @Override
                  public void onClick(DialogInterface arg0, int arg1) {
                       // Do nothing
                  }
             };*/
             if(isfiletypefound){
	             AlertDialog ad= new AlertDialog.Builder(this).create();
	             ad.setTitle("Question");
	             ad.setMessage("Do you want to open this file?\n" + aDirectory.getName() );
	             ad.setButton("OK",new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int which) {
	                	 final SharedPreferences mPrefs;
	                	 mPrefs = getSharedPreferences(TestProj1.PREF_XML,Context.MODE_PRIVATE);
	                	 Editor mPrefsEditor = mPrefs.edit();
	                	 mPrefsEditor.putString(TestProj1.KEY_GPX_DEFSTRING, aDirectory.getPath());
	         	   		 mPrefsEditor.commit();
	                	 finish();
	                     return;
	                   } }); 
	             ad.setButton2("Cancel", new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int which) {
	                   return;
	                 }}); 
	             ad.show();
             }
        }
   }
   private void fill(File[] files) {
        this.directoryEntries.clear();
        
        // Add the "." == "current directory"
        // and the ".." == 'Up one level'
        this.directoryEntries.add(getString(R.string.current_dir));      
        if(this.currentDirectory.getParent() != null)
             this.directoryEntries.add(getString(R.string.up_one_level));
        
        switch(this.displayMode){
             case ABSOLUTE:
                  for (File file : files){
                	  if(file.isFile()){
                		  //check for GPX Files
                		  String szfname = file.getPath();
                		  if(szfname.contains(".gpx"))
                			  this.directoryEntries.add(file.getPath());
                	  }
                	  else{
                		  this.directoryEntries.add(file.getPath());
                	  }
                  }
                  break;
             case RELATIVE: // On relative Mode, we have to add the current-path to the beginning
                  int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
                  for (File file : files){
                       this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLenght));
                  }
                  break;
        }
        	ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,R.layout.file_row, this.directoryEntries);
        	this.setListAdapter(directoryList); 
    }
}
