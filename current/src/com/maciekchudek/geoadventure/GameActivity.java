package com.maciekchudek.geoadventure;

/**
* Sets up the main application view
* 
* 
* LOAD EITHER THE MAIN MENU OR A GAME...
*  
*/

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.maciekchudek.geoadventure.R;

public class GameActivity extends Activity {

	Button navButtonMessages;
	Button navButtonScanner;
	Button navButtonAttachedImage;
	ListView messagesListView;
	TextView messageDetailsTextView;
	View scannerFrame;
	ImageView picturesImageView;
	AdventureDataInterface dbi;
	public ScannerCanvas scanner;
	MessageAdapter messageAdapter;
	Message currentMessage = null;
	
	int id;
	
	// INITIALISATION AND DESTRUCTION

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.findAllViewsById();

		// connect buttons to listeners
		navButtonMessages.setOnClickListener(navMessagesListener);
		navButtonScanner.setOnClickListener(navScannerListener);
		navButtonAttachedImage.setOnClickListener(navAttachImageListener);
		messagesListView.setOnItemClickListener(messageSelectedListener);
		

		// misc
		getActionBar().setDisplayHomeAsUpEnabled(true);
		messageDetailsTextView.setMovementMethod(new ScrollingMovementMethod());		
		scanner = new ScannerCanvas(this);
		
		//ADD A UTF8 FONT		
		Typeface font = Typeface.createFromAsset(getAssets(), "DejaVuSans.ttf");
		messageDetailsTextView.setTypeface(font);
		
				
		id = getIntent().getIntExtra(Constants.INTENT_EXTRA_ADVENTURE_ID, -1);			
		if (id < 0) {	showMessage("No adventure selected... \n\n Please choose from the main menu."); }
		
		dbi = new AdventureDataInterface(this, id);
		
		
		

		
	}
	private void findAllViewsById() {
		navButtonMessages = (Button) findViewById(R.id.nav_button_messages);
		navButtonScanner = (Button) findViewById(R.id.nav_button_scanner);
		navButtonAttachedImage = (Button) findViewById(R.id.nav_button_attached_image);
		messagesListView = (ListView) findViewById(R.id.message_list);
		messageDetailsTextView = (TextView) findViewById(R.id.message_details_text_view);
		scannerFrame = (View) findViewById(R.id.scanner_frame);
		picturesImageView = (ImageView)findViewById(R.id.pictures_image_view);
	}
	@Override
	protected void onPause() {
		scanner.stopScanning();
		dbi.sleep();
		super.onStop();
		
	}
	
	@Override
	protected void onResume() {
		super.onPause();
		dbi.wake();
		showMessageList();
     }	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}

	

	
	
	
	// NAVIGATION
	
	@Override
	public void onBackPressed() {
		if (scanner.scanning) {
			stopScanning();
		} else if (messagesListView.getVisibility() == View.VISIBLE){
			super.onBackPressed();
		} else {
			showMessageList();
		}
	}
	public void showMessageList() {
		scannerFrame.setVisibility(View.GONE);
		messageDetailsTextView.setVisibility(View.GONE);
		navButtonAttachedImage.setVisibility(View.GONE);
		picturesImageView.setVisibility(View.GONE);
		messagesListView.setVisibility(View.VISIBLE);
		scanner.stopTimer();
		messageAdapter = new MessageAdapter(this, R.layout.message_list_headers, dbi.getTriggeredMessages());
		messagesListView.setAdapter(messageAdapter);
	}
	public void readMessage(Message m) {
		currentMessage = m;
		m.read = 1;
		dbi.read(m.id);
		scannerFrame.setVisibility(View.GONE);
		messagesListView.setVisibility(View.GONE);
		messageDetailsTextView.setVisibility(View.VISIBLE);
		picturesImageView.setVisibility(View.GONE);
		
		if(m.hasImage()){
			navButtonAttachedImage.setVisibility(View.VISIBLE);
		}
		showMessage(TextUtils.concat(Html.fromHtml("<b><h1>" + m.title + "</h1></b><br />"),android.text.Spannable.Factory.getInstance().newSpannable(m.message)));
		
	}
	
	public void showMessage(CharSequence text){
		messageDetailsTextView.scrollTo(0, 0);
		messageDetailsTextView.setText(text);
	}

	//Scanning
	public void startScanning() {
		navButtonScanner.setText(R.string.navButtonScannerRunning);
		picturesImageView.setVisibility(View.GONE);
		navButtonMessages.setVisibility(View.GONE);
		messagesListView.setVisibility(View.GONE);
		navButtonAttachedImage.setVisibility(View.GONE);
		messageDetailsTextView.setVisibility(View.VISIBLE);
		scannerFrame.setVisibility(View.VISIBLE);
		scanner.startScanning();
	}
	public void stopScanning(){
		scanner.stopScanningAndUpdate(false);
	}
	public void stopScanning(String message) {
		navButtonScanner.setText(R.string.navButtonScanner);
		navButtonMessages.setVisibility(View.VISIBLE);
		scannerFrame.setVisibility(View.GONE);
		picturesImageView.setVisibility(View.GONE);
		showMessage(message);
	}
	public void showPicture(){
		navButtonScanner.setText(R.string.navButtonScanner);
		messagesListView.setVisibility(View.GONE);
		messageDetailsTextView.setVisibility(View.GONE);
		navButtonAttachedImage.setVisibility(View.GONE);
		scannerFrame.setVisibility(View.GONE);
		picturesImageView.setVisibility(View.VISIBLE);
		//picturesImageView.setImageResource(currentMessage.getImage(this));
		picturesImageView.setImageBitmap(currentMessage.getImage());
		//scanner.pictureMode(currentMessage.getImage(this));
		
		
	}

	// LISTENERS
	private OnItemClickListener messageSelectedListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			readMessage((Message) parent.getItemAtPosition(position));
		}
	};
	private OnClickListener navMessagesListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			showMessageList();
		}
	};
	private OnClickListener navScannerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (scanner.scanning) {
				stopScanning();
			} else {
				startScanning();
			}
		}
	};
	private OnClickListener navAttachImageListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showPicture();			
		}
	};
	
	//Debug menu
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);			
			return true;
		case R.id.debugCode:
			getDebugCode();
			return true;
		case R.id.locationProvider:
			selectLocationProvider();
			return true;
		default: 
			return super.onOptionsItemSelected(item);
		}
}
	
	
	private void selectLocationProvider() {
		new AlertDialog.Builder(this)
		.setTitle("Select Location Provider")
		.setMessage(
				"Select your method of determining your location:")
		.setPositiveButton("GPS",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						scanner.loc.setGPS(); 
					}
				})
		.setNegativeButton("NETWORK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						scanner.loc.setNETWORK();
					}
				}).show();
	}

private void useDebugCode(String code) {
	if(code.startsWith("3103")){
		try{
			int toTrigger = Integer.parseInt(code.substring(4));
			dbi.trigger(toTrigger);
		} catch (NumberFormatException e){}
	}
	if(code.equals("6858")){
		//dbi.resetDatabase();
	}		
	showMessageList();
	//if start is correct, trigger thingy
	//if reset code, reset
	//if destruct code, destruct
}



private void getDebugCode() {
	if(scanner.scanning){
		stopScanning();
	}
	final EditText debugCodeEditText = new EditText(this);
	debugCodeEditText.setHint("");
	new AlertDialog.Builder(this)
			.setTitle("Debug Code")
			.setMessage(
					"Enter debug code. Careful. This can cause the app to self destruct!")
			.setView(debugCodeEditText)
			.setPositiveButton("Submit",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							useDebugCode(debugCodeEditText.getText()
									.toString());
						}
					})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
						}
					}).show();
}

	
}
