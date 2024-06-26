package org.cso.MSBUtil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;

public class AppUtil {


	public static Uri getFileUri(Context context, File photoFile){
		Uri uri= FileProvider.getUriForFile(context,
				"org.cso.MobileSpotBilling.fileprovider", photoFile);
		return uri;
	}
	 public static String GetIMEI(Context context)
     {
     	return getImei(context);
		 /*TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService( context.TELEPHONY_SERVICE);

		 try {
			 return telephonyManager.getDeviceId();
		 } catch (SecurityException e) {
			 e.printStackTrace();
			 return null;
		 }*/
	 }

	public static String getImei(Context context){
		String  imei = null;
		try {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
				imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);


			} else {
				TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) ==
						PackageManager.PERMISSION_GRANTED) {
					imei = telephonyManager.getDeviceId();
				}

			}
		}catch (Exception e){
			e.printStackTrace();
			imei = null;
		}
		return imei;
	}
	  public static String GetVersion(Context context)
     {
		  String verString=null;
		  PackageManager manager = context.getApplicationContext().getPackageManager();
		  PackageInfo info;
		try {
			info = manager.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
			verString=info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return verString;
     }

     public static String unUploadedFilePath(Context context)
	 {
		 String AppDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()
				 + "/SBDocs";
		 String photoUnuploadDir = AppDir + "/.PhotosUnuploaded";

		 File file = new File(photoUnuploadDir);
		 if (!file.exists()) {
			 file.mkdirs();
		 }

		 String[] unUploadedFolder = new String[1];

		 return file.getAbsolutePath();
	 }
	public static String unUploadedFilePath2(Context context)
	{
		File binderfile=context.getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/SBDocs/.PhotosUnuploaded");
		Log.e("binderfile",binderfile.getAbsolutePath());
		if (!binderfile.exists()) {
			binderfile.mkdirs();
		}
		return binderfile.getAbsolutePath();
	}

}
