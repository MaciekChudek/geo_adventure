package com.maciekchudek.geoadventure;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;

public class Constants {

	protected static final String INTENT_EXTRA_ADVENTURE_ID = "com.maciekchudek.geoadventure.adventure_id";
	public static int default_proximity = 100;
	
	public static AdventureMetaData no_installed_adventures_message = new AdventureMetaData(-1, "No adventures added yet","Please download and install a GeoAdventure file."); 
	
	
	public static List<AdventureMetaData> loadMessages = Arrays.asList(
			new AdventureMetaData(1, "How do I find adventures?", "Geo-adventures are made by cool people like yourself. They package them up as .GeoAdventure.zip files and publish them on the internet for you to download to your device. A good place to start looking for .GeoAdventure.zip files is GEOADVENTURE_WEBSITE_URL_ONCE_I_HAVE_ONE. If you can't find any in your area, then you ought to make one yourself."),
			new AdventureMetaData(2, "How do I install adventures?", "When you click on a .GeoAdventure.zip file, either online or saved on your device, this program ought to recognise it and install the adventure automatically."),
			new AdventureMetaData(3, "Where do I get support?", "GeoAdventure is an ameturish spare time production. You can try contacting me, but I probably wont respond. It's open source though, so why not hack in to the code and try to improve the game for others?")				
			);
	
	public enum MenuGroups{
		PLAY ("Play adventure"),
		DELETE ("Delete adventure"),
		LOAD ("How to install a new adventure");
		
		public String text;
		
		private MenuGroups (String text){
			this.text = text;
		}
	}

	
	public static void showMessage(Context c, String message) {
		showMessage(c, message, "");
	}
	
	
	public static void showMessage(Context c, String title, String message) {
		new AlertDialog.Builder(c).setTitle(title).setMessage(message).show();
	}
	
	

	

}