//Manages the adventure database

package com.maciekchudek.geoadventure;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class AdventureManager {

	public DatabaseInterface dbi;
	public static Context parent;

	public AdventureManager(Context parent) {
		dbi = new DatabaseInterface(parent);
		AdventureManager.parent = parent;
	}

	public void deleteAdventure(int id) {
		dbi.deleteAdventure(id);
	}

	public void getAdventure(int id) { // returns interface to adventure...
		
	}

	private void importAdventure(int id, Reader adventureCSV) throws Exception {

		CSVReader c = new CSVReader(adventureCSV);
		List<String[]> rows = c.readAll();
		c.close();
		if (rows.size() < 1)
			throw new Exception("No adventure data.");
		String[] cols = rows.get(0);
		rows.remove(0);
		if (false == dbi.addAdventureData(id, cols, rows))
			throw new Exception("Failed to import adventure.csv, perhaps the file wasn't correctly specified? Making this message more informative would be very useful and require a lot of work.");
	}

	private long importMetaData(Reader metaCSV) throws Exception {
		// return value -1 = sql error; -2 = adventure already exists
		CSVReader c = new CSVReader(metaCSV);
		List<String[]> rows = c.readAll();
		c.close();

		HashMap<String, String> meta = new HashMap<String, String>();
		for (String[] row : rows) {
			if (row.length != 2)
				continue; // skip incorrectly formatted rows
			meta.put(row[0], row[1]);
		}

		if (!(meta.containsKey("title") && meta.containsKey("author") && meta
				.containsKey("version")))
			throw new Exception("Required columns missing."); 
		// don't proceed if we don't have the essential columns

		// TODO: refactor the inconsistency between calling it "name" in the
		// metadata and "title" in the CSV
		
		long result = dbi.addAdventureMeta(meta.get("title"),
				meta.get("version"), meta.get("author"));
		if (result == -1)
			throw new Exception("Data import error");
		else
			return result;
	}

	void DeleteRecursive(String fileOrDirectoryLocation) {		
		DeleteRecursive(new File(fileOrDirectoryLocation));
	}

	void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.exists()) {
			if (fileOrDirectory.isDirectory())
				for (File child : fileOrDirectory.listFiles())
					DeleteRecursive(child);

			fileOrDirectory.delete();
		}
	}

	private boolean validateFilenames(List<String> names) {
		// checks to ensure that the list contains meta.csv, adventure.csv and
		// that everything else is in the resource directory
		boolean meta = false;
		boolean adventure = false;
		boolean other = true;

		for (String s : names) {
			if (s.equals("meta.csv"))
				meta = true;
			else if (s.equals("adventure.csv"))
				adventure = true;
			else if (!s.startsWith("assets/")
					|| (s.length() - s.replace("/", "").length() > 1))
				other = false;
		}

		return meta && adventure && other;

	}


	
	
	
	public void importData(final Uri data){
		try {	
		final ContentResolver cr = parent.getContentResolver();
		InputStream is =  cr.openInputStream(data);	
		
		if (is == null)	throw new Exception("No data...");

		List<String> names = ZipManager.unzipNames(is);
		if (!validateFilenames(names)) throw new Exception("Unable to open .GeoAdventure file. Data is not formatted correctly.");
		is.close();

		// extract and save the files
		int id = dbi.getNextID();
		final String dir = parent.getFilesDir().toString() + "/adventures_"
				+ Integer.toString(id) + "/";
		// Constants.showMessage(parent, "dir", dir);
		DeleteRecursive(dir); // clean out the directory, just in case
		deleteAdventure(id); // remove anything with this id, just in case
		try {
			
			//get the csv files
			is = cr.openInputStream(data); //reset input stream
			Reader[] CSVs = ZipManager.getCSVReaders(is);
			is.close();
			
			// asynchronously unzip just the assets
		    Runnable unzipperRunnable = new Runnable() {
		      @Override
		      public void run() {
		    	  try {
			    	  InputStream is2 = cr.openInputStream(data); // reset the input stream
			    	  ZipManager.unzipAssetFiles(is2, dir);
			    	  is2.close();
		    	  } catch (Exception e) {			
		    		  e.printStackTrace();
		    		  Message m = new Message();
		    		  Bundle b = new Bundle();
		    		  b.putString("error", e.toString());
		    		  m.setData(b);		    		  
		    		  handler.handleMessage(m);
		    	  } 
		        };
		      };
		      
		      new Thread(unzipperRunnable).start(); 

		      //now import the csv files
		      long result = importMetaData(CSVs[0]);
		      if (result < -1) {
		    	  throw new Exception("An adventure with that author, title and version already exists on this device.");
		      }
		      if (result != id) throw new Exception("Filesystem doesn't correspond to database ID.");
		      importAdventure(id, CSVs[1]);
		} catch (Exception e) {
			deleteAdventure(id);
			DeleteRecursive(dir);
			throw e;
		}	
		
		AdventureMetaData a = dbi.getSingleAdventureMetaData(id);
		Constants.showMessage(parent, "A new adventure was imported", a.name + " by "+a.author);
		
		
		} catch (Exception e) {
			Constants.showMessage(parent, "There was an error importing the file.", e.getMessage());
			System.out.println(e);
		}			
		
		return;
	}
	
	public void importUri(Uri uri) {
		
		if(uri.getScheme().startsWith("http")){ //we need to download the file			
			try{
				new Downloader(this, uri);
			} catch (Exception e){
				Constants.showMessage(parent, "There was an error downloading the file.", e.getMessage());
			}			
			return;
		}		
		importData(uri);
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(android.os.Message msg) {
			 Constants.showMessage(parent, "Error unzipping image files", msg.getData().getString("error"));
			return false;
		}
	});
	
	public void sleep(){
		dbi.sleep();
	}

	public void wake(){
		dbi.wake();
	}

	
}
