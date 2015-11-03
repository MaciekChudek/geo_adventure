package com.maciekchudek.geoadventure;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

public class Downloader {

	AdventureManager am;
	Context parent;
	Uri data;
	
	protected ProgressDialog progressDialog;
	protected long downloadId;
	protected DownloadManager manager;

	
	public Downloader(AdventureManager am, Uri data) throws Exception {
		
		this.am = am;
		this.parent = am.parent;
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			throw new Exception("Unable to download file. Try saving it on your device manually.");
		}
		String url = data.toString();
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		request.setDescription(url);
		//request.setTitle("Downloading GeoAdventure");
		// in order for this if to run, you must use the android 3.2 to compile your app
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    request.allowScanningByMediaScanner();
		    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, data.getLastPathSegment() + ".GeoAdventure.zip");

		// get download service and enqueue file
		manager = (DownloadManager) parent.getSystemService(Context.DOWNLOAD_SERVICE);
		downloadId = manager.enqueue(request);
		
		
		parent.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		
		final ProgressDialog progressDialog = new ProgressDialog(parent);		
		progressDialog.setTitle("Downloading GeoAdventure");
		progressDialog.setMessage(data.getLastPathSegment());
		progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(0);
		progressDialog.setMax(100);
		progressDialog.show();
		
		progressDialog.setCancelable(false);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int progress = 0;
					while(progress < 100){
						progress = getProgress();
						progressDialog.setProgress(progress);
						Thread.sleep(100);
					}
				} catch (Exception e) {

				}
				progressDialog.dismiss();
			}
		}).start();
		
		
	}
	
	private int getProgress(){
		DownloadManager.Query q = new DownloadManager.Query();
		q.setFilterById(downloadId);
		Cursor cursor = manager.query(q);
		cursor.moveToFirst();
		int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
		int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
		cursor.close();
		return (bytes_downloaded * 100 / bytes_total);
	}
	
	BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	am.importUri( manager.getUriForDownloadedFile(downloadId) );
	    }
	};
	
}
