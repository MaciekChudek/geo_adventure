/**
* Scans for messages
*  
*/


package com.maciekchudek.geoadventure;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.view.View;

public class ScannerCanvas {
	
	private Timer timer;
	private int radius1;
	private int radius2;
	private Bitmap bmp;
	private Canvas c;
	private Paint paint;
	private Random rand;
	public boolean scanning = false;
	float nearestSingularity = Float.MAX_VALUE;
	Message nearestMessage = null;
	LocationChecker loc;
	GameActivity parent;
	View scannerFrame;
	
	public ScannerCanvas(GameActivity parentActivity){
		
		parent = parentActivity; 
		loc = new LocationChecker(parentActivity);	
		scannerFrame = parent.scannerFrame;
		
		bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
		rand = new Random();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		

		
	}
	

	//display modes
	
	@SuppressWarnings("deprecation")
	void scanningMode(){
		c = new Canvas(bmp);
		c.drawColor(Color.BLACK);
		scannerFrame.setBackgroundDrawable(new BitmapDrawable(bmp));
	}
	
	/*public void pictureMode(BitmapDrawable pic){
		scannerFrame.setBackgroundDrawable(pic);
	}*/
	
	//utility functions
	
	public float checkDistance(Message m) {
		Location here = m.location;
		if (here == null || loc.curLoc == null)
			return Float.MAX_VALUE;		
		return loc.curLoc.distanceTo(here);
	}
	

	public String r() {
		return String.format(Locale.CANADA, "%2d",
				java.lang.Math.round(10+rand.nextDouble() * 89));
	}
	
	//message triggers
	
	
	public Boolean triggersLocationlessMessages(List<Message> viables) {
		Boolean foundMessage = false;
		for (Message m : viables) {
			if (m.location == null) {
				parent.dbi.trigger(m.id);
				foundMessage = true;
			}
		}
		return foundMessage;
	}
	
	public Boolean triggerMessages() {
		Boolean foundMessage = false;
		float dist;
		nearestSingularity = Float.MAX_VALUE;
		nearestMessage = null;
		List<Message> viables = parent.dbi.getViableMessages();
		if (triggersLocationlessMessages(viables))
			return true;
		if (loc.curLoc == null)
			return false;
		for (Message m : viables) {
			if (m.location != null) {
				dist = checkDistance(m);
				if (dist < m.proximity) {
					parent.dbi.trigger(m.id);
					foundMessage = true;
				} else if (!foundMessage && dist < nearestSingularity) {
					nearestSingularity = dist;
					nearestMessage = m;
				}
			}
		}
		return foundMessage;
	}
	
	//start & stop scanning

	
	public void stopScanning(){
		scanning = false;		
		stopTimer();
		loc.stopUpdating();
	}
	
	public void stopScanningAndUpdate(boolean messagesUpdated) {
		stopScanning();
		String result;

		if (messagesUpdated || triggerMessages()) {
			while (triggerMessages()) {
			}
			result = "New message received. Downloading....\n\n Please check your messages.";
		} else {
			if (loc.curLoc == null) {
				result = "Scanner not functioning correctly. Enable GPS or move around outside a bit so we can get a lock on your position.";
			} else {
				result = "No messages detected within scanning range.";
				if (nearestSingularity < Float.MAX_VALUE) {
					result = result
							+ "Approximate distance to nearest check-point: "
							+ Math.round(nearestSingularity*10)/10 + "m";
				}
				
				SimpleDateFormat timingFormat = new SimpleDateFormat("hh:mm:ss", Locale.CANADA);
				
				
				String nearest = "";
				if(nearestMessage != null){
					nearest = "Target: " + nearestMessage.location.getLongitude() + ", " + nearestMessage.location.getLatitude()
							+"; "+ loc.curLoc.bearingTo(nearestMessage.location)+" deg. east of north.\n";
							
				}
				
				String extra = "\n\n\nGPS info:\n\n"
						+ "Current: " + loc.curLoc.getLongitude() + ", "+ loc.curLoc.getLatitude()
						+ "\n"
						+ nearest
						+ "accuracy: "
						+ loc.curLoc.getAccuracy()
						+ "\n"
						+ "last GPS update: "
						+ loc.lastUpdate()
						+ "\n"+ "\n" + "GPS read time: " + timingFormat.format(new Date(loc.curLoc.getTime()))
						+ "\n" + "Current time: " + timingFormat.format(new Date(System.currentTimeMillis()))
						+ "";
				result = result+extra;
			}
		}
		parent.stopScanning(result);
	}

	public void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	public void startScanning() {
		scanningMode();
		scanning = true;	
		loc.updateLocation();
		radius1 = 0;
		radius2 = -45;
		stopTimer();
		TimerTask t = new TimerTask() {
			@Override
			public void run() {
				parent.runOnUiThread(new Runnable() {
					public void run() {
						drawScaner();
					}
				});
			}
		};
		timer = new Timer();
		timer.schedule(t, 0, 50);
	}

	//drawing
	public void drawScaner() {
		if (scanning) {
			c.drawColor(Color.BLACK);
			c.drawCircle(50, 50, radius1, paint);
			c.drawCircle(50, 50, radius2, paint);
			parent.scannerFrame.invalidate();
			String scanMessage = "\u2123:" + r() + "\t\t\u2230:" + r() + "\t\t\u2135:" + r() + "\n";
			//String scanMessage = "Scanning temporal displacement coordinates... \n\u2136:" + r() + "\t\t\u2237:" + r() + "\t\t\u2135:" + r();
			if(loc.curLoc != null){
				//TODO: since this happens many times a second, it should not use string pasting sprintf() like function should be much faster.
				 scanMessage = scanMessage + "Scan accuracy: "+ loc.curLoc.getAccuracy() + "m\n" + "Last GPS update" + ":" + loc.lastUpdate();
			}
			
			parent.showMessage(scanMessage);
			radius1 = radius1 + 1;
			radius2 = radius2 + 1;
			if (radius1 > 90) {
				radius1 = 0;
				if (triggerMessages()) {
					stopScanningAndUpdate(true);
					return;
				}
			}

			if (radius2 > 90)
				radius2 = 0;
		}
	}


}
