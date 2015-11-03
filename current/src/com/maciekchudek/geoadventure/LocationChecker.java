/**
* Checks the GPS coordinates
*  
*/

package com.maciekchudek.geoadventure;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

public class LocationChecker extends Activity implements LocationListener {


	  private LocationManager locationManager;
	  public String provider;
	  public Location curLoc;
	  
	
	public LocationChecker(Context c) {
		super();
		// Get the location manager
	    locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
	    		
	    		
	    // Define the criteria how to select the location provider -> use
	    // default
	    //Criteria criteria = new Criteria();
	    //criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    //criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	    //provider = locationManager.getBestProvider(criteria, false);
	    
	    provider = LocationManager.GPS_PROVIDER;
	    curLoc = locationManager.getLastKnownLocation(provider);
	}

	public String lastUpdate(){
		//curLoc =  locationManager.getLastKnownLocation(provider);
		long t =  TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()- curLoc.getTime());
		//long t =  TimeUnit.NANOSECONDS.toSeconds(SystemClock.elapsedRealtimeNanos()- curLoc.getElapsedRealtimeNanos());
		if(t > 300) return ">5 min ago";
		
		//SimpleDateFormat timingFormat = new SimpleDateFormat("hh:mm:ss", Locale.CANADA);
		
		//return  "now: " +timingFormat.format(new Date(curLoc.getTime())) + "; last: "+ timingFormat.format(new Date(System.currentTimeMillis())); 
		return "approx. "+ t+" sec ago";
	}

	public void setGPS(){
		provider = LocationManager.GPS_PROVIDER;
	}
	
	public void setNETWORK(){
		provider = LocationManager.NETWORK_PROVIDER;
	}
	
	 public void updateLocation() {
		 curLoc = locationManager.getLastKnownLocation(provider);
		 //locationManager.removeUpdates(this);
		 locationManager.requestLocationUpdates(provider, 1000, 0, this);
	 }
	 
	 public void stopUpdating(){
		 locationManager.removeUpdates(this);
	 }
	 
	 public Boolean checkLocation(Location dest, float minDist) {
		 float dist = curLoc.distanceTo(dest);		 
		 if (dist < minDist){
			 return true;
		 }else{
			 if(curLoc.getAccuracy()*2 < (dist - minDist)){
				 return true;
			 }else{
				 return false;
			 }
		 }
	 }
	
	@Override
	public void onLocationChanged(Location location) {
		curLoc = location;	
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
}
