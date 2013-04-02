package com.tanktankfighttank.game_engine;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;

public class LocationGetter {
	
	private LocationManager manager;
	private LocationListener listener;
	private Location currentBest;
	
	private static final int TWO_MINTUES = 1000 * 60 * 2;
	
	// Constructor for use in MainMenu to get the user's location
	public LocationGetter(Context context){
		manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		// If GPS is enable and working
		listener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if(isBetterLocation(location, currentBest)){
					currentBest = location;
				}
			}
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			public void onProviderEnabled(String provider) {}
			public void onProviderDisabled(String provider) {}
		};
	}
	
	// Constructor for use in GameField to center the map (unnecessary?)
	public LocationGetter(Context context, MapController mapController){
		manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		// If GPS is enable and working
		if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			currentBest = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(currentBest != null){
				// Center the map on the device's last known location
				mapController.setCenter(new GeoPoint(
						(int) (currentBest.getLatitude() * 1E6 + .5 * GameManager.MAX_Y_GRID * GameManager.scale),
						(int) (currentBest.getLongitude() * 1E6 + .5 * GameManager.MAX_X_GRID * GameManager.scale)));
				
			}
			else
			{
				//Set the location to Churchill Hall
				currentBest = new Location(LocationManager.GPS_PROVIDER);
				currentBest.setLatitude(46.894351);
				currentBest.setLongitude(-96.797461);
				mapController.setCenter(new GeoPoint(
						(int) (currentBest.getLatitude() * 1E6 + .5 * GameManager.MAX_Y_GRID * GameManager.scale),
						(int) (currentBest.getLongitude() * 1E6 + .5 * GameManager.MAX_X_GRID * GameManager.scale)));
			}
		}
		else
		{
			//Set the location to Churchill Hall
			currentBest = new Location(LocationManager.GPS_PROVIDER);
			currentBest.setLatitude(46.894351);
			currentBest.setLongitude(-96.797461);
			mapController.setCenter(new GeoPoint(
					(int) (currentBest.getLatitude() * 1E6 + .5 * GameManager.MAX_Y_GRID * GameManager.scale),
					(int) (currentBest.getLongitude() * 1E6 + .5 * GameManager.MAX_X_GRID * GameManager.scale)));
		}
		
		// Define the listener
		listener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if(isBetterLocation(location, currentBest)){
					currentBest = location;
				}
			}
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			public void onProviderEnabled(String provider) {}
			public void onProviderDisabled(String provider) {}
		};
	}
	
	public void startListening(){
		if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, listener);
		}
	}
	
	public void stopListening(){
		manager.removeUpdates(listener);
	}
	
	public Location getCurrentBestLocation(){
		return currentBest;
	}
	
	public boolean isGPSEnabled(){
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	/** Code obtained from http://developer.android.com/guide/topics/location/obtaining-user-location.html
	 * 
	 * Determines whether one Location reading is better the current Location fix
	 * @param location The new Location that you want to evaluate
	 * @param currentBestLocation
	 * @return boolean true if location better, false otherwise
	 */
	public boolean isBetterLocation(Location location, Location currentBestLocation){
		if(currentBestLocation == null){
			// A new location is always better than no location
			return true;
		}
		
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINTUES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINTUES;
		boolean isNewer = timeDelta > 0;
		
		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if(isSignificantlyNewer){
			return true;
		// If the new location is more than two minutes older, it must be worse
		} else if(isSignificantlyOlder){
			return false;
		}
		
		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
		
		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());
		
		// Determine location quality using a combination of timeliness and accuracy
		if(isMoreAccurate){
			return true;
		} else if(isNewer && !isLessAccurate){
			return true;
		} else if(isNewer && !isSignificantlyLessAccurate && isFromSameProvider){
			return true;
		}
		return false;
	}
	
	private boolean isSameProvider(String provider1, String provider2){
		if(provider1 == null){
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
}
