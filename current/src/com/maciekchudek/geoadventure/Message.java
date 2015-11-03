/**
* A view for the messages in the adventure
*  
*/

package com.maciekchudek.geoadventure;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationProvider;

public class Message {
	public int id;
	public int meta_id ;
	public String title;
	public String message;
	public int required;
	public int prohibited;
	public int triggered;
	public Location location;
	//public String location;
	public int read;	
	public int proximity;
	//private LocationChecker loc;
	
	
	public String image_path;
	private Bitmap image = null;
	private Boolean has_image = null;

	public Message(GameActivity parent, Cursor cursor) {
		this.id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.ID.s));
		this.meta_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.METAID.s));
		this.title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.TITLE.s));
		this.message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.MESSAGE.s));
		this.required = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.REQUIRED.s));
		this.prohibited = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.PROHIBITED.s));
		this.triggered = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.TRIGGERED.s));
		this.location = extractLocationFromString(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.LOCATION.s)), parent.scanner.loc);
		this.read = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.READ.s));		
		try{
			this.proximity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.PROXIMITY.s));
		} catch (Exception e){
			this.proximity = Constants.default_proximity;
		}
		if (this.proximity <= 0) this.proximity = 100;
		
		
		int i = cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsAdventure.IMAGE.s);
		if(cursor.isNull(i)){
			has_image = false;
		} else {
			String image_name = cursor.getString(i);
			if(image_name == "")has_image = false;
			else{
				image_path = parent.getFilesDir().toString() + "/adventures_" + Integer.toString(meta_id) + "/assets/" + image_name;
			}
		}						
	}

	
	private static Location extractLocationFromString(String l, LocationChecker loc) {		
		if (l != null && l.matches("-*[0-9]*\\.[0-9]*,[ ]*-*[0-9]*.[0-9]*")){ 				
			String[] locString = l.split(",");
			Location destination = new Location(loc.provider);
			destination.setLatitude(Double.parseDouble(locString[0]));
			destination.setLongitude(Double.parseDouble(locString[1]));
			return destination;
		} else {return null;}
	}
	
	
	private void setImage(){	
			image = BitmapFactory.decodeFile(image_path);			
			has_image = null != image;
	}
	
	public Boolean hasImage(){
		if(null == has_image){
			setImage();
		}
		return has_image;
	}
	
	public Bitmap getImage(){
		if(null == has_image){
			setImage();
		}
		return image;
	}
	
	public String toString() {
		if (read == 1) {
			return title;
		} else {
			return "*NEW*: " + title;
		}
	}
}
