package org.cso.MSBUtil;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPSLocation {

	Location location;
	
	public String getLocation(Context context)
	{
		double Lat;
		double Long;

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); 

		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //getting network status
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled)
        	return "Disabled";
        else		
        	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		Lat = location.getLatitude();
		Long = location.getLongitude();
		
		return String.valueOf(Lat) + "¥" + String.valueOf(Long);
	}
}