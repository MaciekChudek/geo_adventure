/**
* Sets up the main application view
* 
* 
* LOAD EITHER THE MAIN MENU OR A GAME...
*  
*/

 
//TODO: MAKE GAME PRETTIER


/*
 * TODO: FIX UNIQUE ID COLUMNS FOR MULTIPLE ADVENTURES
 * TODO: FIX LOCATION-LOGIC IN MESSAGES, MAKE WHOLE LOCATION PROVIDER THING SIMPLER
 * TODO: DOUBLE CHECK THAT WHOLE NETWORK PROVIDER THING
 * TODO: FIX WHAT HAPPENS WHEN SCREEN ROTATES/BLANKS WHILE SCANNER IS RUNNING
 * TODO: ADD RESUME OF RUNNING ADVENTURE, INSTEAD OF ALWAYS GOING TO THE MENU
 * TODO: SOMETIMES ADVENTURE LIST DOESN'T UPDATE AFTER IMPORT?
 */


package com.maciekchudek.geoadventure;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	public static final String EXTRA_ADVENTURE_LIST = "com.maciekchudek.geoadventure.adventure_list";
	public static final String EXTRA_ADVENTURE = "com.maciekchudek.geoadventure.adventure";	
		
	AdventureManager adventureManager;	
	//GameActivity gameActivity;
	ViewGroup background;
	MainMenu mainMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		//set up the main menu
		background = new LinearLayout(this);
		setContentView(background);
		adventureManager = new AdventureManager(this);		
		
		//adventureManager.dbi.resetDatabase();
		
		tryToImportAdventuer();	
		mainMenu = new MainMenu(this, adventureManager.dbi, background);		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		adventureManager.sleep();
     }

	
	@Override
	protected void onResume() {
		super.onResume();
		adventureManager.wake();
		tryToImportAdventuer();
		mainMenu.populateMenus();
     }	
	
	
	@Override  
	protected void onNewIntent(Intent intent) {       
	    super.onNewIntent(intent);  
	    setIntent(intent);	    
	}
	
	
	
	private void tryToImportAdventuer(){
			//check if we've been called to import a file
		Uri data = getIntent().getData();
		if (data != null) {
			
			//Constants.showMessage(this, data.getLastPathSegment() , data.toString());
			 getIntent().setData(null);
			 adventureManager.importUri(data);			
		}
	}
}
	

