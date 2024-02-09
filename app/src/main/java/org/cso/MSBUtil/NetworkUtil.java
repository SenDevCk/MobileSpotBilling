package org.cso.MSBUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;



import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class NetworkUtil {

	public static boolean isOnline(Context context, String Type) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = null;
		if (Type != null) {
			if (Type.compareToIgnoreCase("mobile") == 0) {
				netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			} else if (Type.compareToIgnoreCase("wifi") == 0) {
				netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			}
		} else {
			netInfo = cm.getActiveNetworkInfo();
		}

		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static boolean isConnectedToServer(String URL, int timeout) {
		System.out.println("Checking Server");
		try {
			URL url = new URL(URL);
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setConnectTimeout(timeout);
			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			System.err.println("Error creating HTTP connection");
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static void setMobileDataEnabled(Context context, boolean enabled) {
		try {
			final ConnectivityManager conman = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			Class conmanClass;

			conmanClass = Class.forName(conman.getClass().getName());

			final Field connectivityManagerField = conmanClass
					.getDeclaredField("mService");
			connectivityManagerField.setAccessible(true);
			final Object connectivityManager = connectivityManagerField
					.get(conman);
			final Class connectivityManagerClass = Class
					.forName(connectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = connectivityManagerClass
					.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static BluetoothSocket createBluetoothSocket(BluetoothDevice device,UUID uuid) throws IOException {
	    if(Build.VERSION.SDK_INT >= 10){
	        try {
	            final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
	          return (BluetoothSocket) m.invoke(device, uuid);
	       } catch (Exception e) {
	          e.printStackTrace();
	    }
	  }
	    
	  return  device.createRfcommSocketToServiceRecord(uuid);
	}
}