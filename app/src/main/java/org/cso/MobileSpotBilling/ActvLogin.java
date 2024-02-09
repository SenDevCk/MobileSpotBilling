package org.cso.MobileSpotBilling;

import org.cso.MSBAsync.AsyncGetUserInfo;
import org.cso.MSBAsync.AsyncValidateDevice;
import org.cso.MSBUtil.GPSTracker;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.MailTo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ActvLogin extends Activity implements OnClickListener,
		TaskCallback {
	/** Called when the activity is first created. */
	UtilDB util;
	EditText username, password;
	TextView txtVersion;
	AlertDialog alertDialog;
	String credentialParam[] = new String[2];
	private static final int PERMISSION_ACCESS_FINE_LOCATION = 13;
	private static final int PERMISSION_CAMERA = 14;
	private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 15;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dg_login);

		//startActivity(new Intent(this, ActvSetupInfo.class));
		
		username = (EditText) findViewById(R.id.edtTxtUserName);
		password = (EditText) findViewById(R.id.edtTxtPassword);
		txtVersion = (TextView) findViewById(R.id.TxtVersion);
		UtilAppCommon.strAppVersion = txtVersion.getText().toString();
		if(!UtilAppCommon.ValidDevice || !UtilAppCommon.ValidVersion)
		{
			if(!UtilAppCommon.ValidDevice){
				final AlertDialog.Builder alert=new AlertDialog.Builder(this);
				alert.setCancelable(false);
				alert.setTitle("Invalid Device");
				alert.setMessage("This the not an authorised device to run this application. Device Id : "+UtilAppCommon.IMEI_Number);
				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						finish();
					}
				});
				alertDialog = alert.create();
				alertDialog.show();
			}
			else if(!UtilAppCommon.ValidVersion){
				final AlertDialog.Builder alert=new AlertDialog.Builder(this);
				alert.setCancelable(false);
				alert.setTitle("Invalid Version");
				alert.setMessage("This the not a valid version to run this application. Version Installed : "+UtilAppCommon.strAppVersion);
				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
					}
				});
				alertDialog = alert.create();
				alertDialog.show();
			}
		}





		Button loginBtn = (Button) findViewById(R.id.btnLogin);
		loginBtn.setOnClickListener(this);
		Button cancelbtn = (Button) findViewById(R.id.btnCancel);
		cancelbtn.setOnClickListener(this);

		if (ContextCompat.checkSelfPermission(ActvLogin.this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(ActvLogin.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
		}

		if (ContextCompat.checkSelfPermission(ActvLogin.this, Manifest.permission.CAMERA)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(ActvLogin.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
		}

		if (ContextCompat.checkSelfPermission(ActvLogin.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(ActvLogin.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
		}

		/*TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(this.TELEPHONY_SERVICE);*/
		UtilAppCommon.IMEI_Number = getImei();
        //UtilAppCommon.IMEI_Number = "911542201041919";

		String limei=UtilAppCommon.IMEI_Number;
		//username.setText(limei);
		
		showSettingsAlert();
		
		Button btnTestPrinter = (Button) findViewById(R.id.btnPrinterTest);
        btnTestPrinter.setOnClickListener(this);



        /*UtilDB utildb = new UtilDB(getBaseContext());
        utildb.insertIntoUserInfo("meterreader_id|METER_READER_NAME|password|IMEINo");
        startActivity(new Intent(this, ActvivityMain.class));*/

	}

	public String getImei(){
		String  imei = null;
		try {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
				imei = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


			} else {
				TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
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
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_ACCESS_FINE_LOCATION: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission granted!
					// you may now do the action that requires this permission


				} else {
					// permission denied
					ActivityCompat.requestPermissions(ActvLogin.this,
							new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
							PERMISSION_ACCESS_FINE_LOCATION);
				}
				return;
			}

			case PERMISSION_CAMERA: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission granted!
					// you may now do the action that requires this permission


				} else {
					// permission denied
					ActivityCompat.requestPermissions(ActvLogin.this,
							new String[]{Manifest.permission.CAMERA},
							PERMISSION_CAMERA);
				}
				return;
			}

			case PERMISSION_WRITE_EXTERNAL_STORAGE: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission granted!
					// you may now do the action that requires this permission


				} else {
					// permission denied
					ActivityCompat.requestPermissions(ActvLogin.this,
							new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							PERMISSION_WRITE_EXTERNAL_STORAGE);
				}
				return;
			}

		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.home:
			finish();	   	 
	    	 Intent intent = new Intent(this, ActvivityMain.class);  
	    	 startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));  
	         startActivity(intent);
			break;
		}
		return true;
	}

	public void onClick(View view) {
		int id = view.getId();
		switch (id) {

		case R.id.btnLogin:			
			String uname = username.getText().toString().trim().toUpperCase();
			String pass = password.getText().toString();
			credentialParam[0] = uname; 	//UtilAppCommon.IMEI_Number;
			credentialParam[1] = pass;
			/*TelephonyManager telephonyManager = (TelephonyManager) this
					.getSystemService(this.TELEPHONY_SERVICE);
			*/
			System.out.println(" UtilAppCommon.IMEI_Number"	+ UtilAppCommon.IMEI_Number);
			//credentialParam[2] = UtilAppCommon.IMEI_Number;
			if (uname.length() <= 0 || pass.length() <= 0) {
				Toast.makeText(getApplicationContext(),
						"Please enter value for User name and password!",
						Toast.LENGTH_LONG).show();
				break;
			}

			if (pass.length() < 4) {
				Toast.makeText(getApplicationContext(),
						"Password must be between 4 and 8 digits!",
						Toast.LENGTH_LONG).show();
				break;
			}

			else {

				/*if (NetworkUtil.isOnline(getApplicationContext(),null) && !UtilAppCommon.strRedirectTo.equalsIgnoreCase("Setup")) 
				{
					String validateParam[] = new String[2];
					validateParam[0] = UtilAppCommon.IMEI_Number;	//"353835062918882";
					validateParam[1] = txtVersion.getText().toString();
					Log.e("Parameters ==>> ", validateParam[0] + "  ==  " + validateParam[1]);
					AsyncValidateDevice asyncValidate = new AsyncValidateDevice(this);
					asyncValidate.execute(validateParam);
					
					if(!UtilAppCommon.ValidDevice)
					{
						//done();
						return;
					}
				}*/
				
				UtilDB util = new UtilDB(getBaseContext());
				//util.truncateTable("SAPBlueInput");
                //util.truncateTable("SAPInput");
				//String result = util.ClearUserInfo();
				//util.readLocalAuthentication();
				int userInputCount = util.getUserInfoRowCount();
				//int billInputCount = util.getBillInputDetailsCount();
				Log.e("Redirect To ==>> ", UtilAppCommon.strRedirectTo);
				//if (userInputCount > 0 && UtilAppCommon.intAppInvoked > 0) 
				//userInputCount = 0;
				if (userInputCount > 0) 
				{
					if(util.getLocalAuthentication(uname, pass))
					{
						util.getUserInfo();
						//startActivity(new Intent(getBaseContext(), ActvSetupInfo.class));
						//if(UtilAppCommon.strRedirectTo.equalsIgnoreCase("Billing"))
						//	startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
						//else
						if(UtilAppCommon.strRedirectTo.equalsIgnoreCase("Setup"))
							startActivity(new Intent(getBaseContext(), ActvDownloadInputData.class));
						else
						{

							startActivity(new Intent(getBaseContext(), ActvivityMain.class));
						}
							finish();
					}
					else
					{
						Toast.makeText(getApplicationContext(),
								"Invalid User name or password!",
								Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					if (NetworkUtil.isOnline(getApplicationContext(),null)) 
					{
						AsyncGetUserInfo asyncGetUserInfo = new AsyncGetUserInfo(
								this);
						asyncGetUserInfo.execute(credentialParam);
						UtilAppCommon.intAppInvoked = 2;
					} else {
						Toast.makeText(getApplicationContext(),
								"No internet connection!", Toast.LENGTH_LONG)
								.show();
						break;
					}
										
					/*credentialParam[1] = "¥DummyPassword¥";
					AsyncGetUserInfo asyncGetUserInfo = new AsyncGetUserInfo(
							this);
					asyncGetUserInfo.execute(credentialParam);
					UtilAppCommon.intAppInvoked = 2;*/
				}
				//finish();
			}
			break;
		case R.id.btnCancel:
			/*final Dialog dialog = new Dialog(this);
			finish();
			System.exit(-1);
			dialog.dismiss();*/
			//quitApp();
			finish();
			break;
			
		case R.id.btnPrinterTest:
			//finish();
			startActivity(new Intent(this, ActvTestPrinting.class));
			break;
		
		}
	}

	public void done() {
		finish();
	}
	
	public void onBackPressed() {
		 //quitApp();
		 finish();
		//this.moveTaskToBack(true);
		return;
	}
	
	private void quitApp() {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Exit Billing Application?");
		alertDialog
				.setMessage("Billing application will be closed...Are you sure to continue?");
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ActvLogin.this.finish();
						alertDialog.dismiss();
						UtilAppCommon.intIsLoggedIn = 0;
						UtilAppCommon.gIntAvailableInputDataCount = 0;
						UtilAppCommon.intAppInvoked = 0;
						UtilAppCommon.strBulkDataResponse = null;
						ActvivityMain xyz = new ActvivityMain();
						xyz.moveTaskToBack(true);
						/*
						 * int pid = android.os.Process.myPid();
						 * android.os.Process.killProcess(pid);
						 */
					}
				});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						alertDialog.dismiss();
					}
				});
		alertDialog.show();
	}

	
	/**
     * Function to show settings alert dialog
     * */
    public void showSettingsAlert(){
    	GPSTracker gps  = new GPSTracker(ActvLogin.this);
    	
    	if(gps.canGetLocation()){
            
            String latitude = String.valueOf(gps.getLatitude());
            String longitude = String.valueOf(gps.getLongitude());
            
            //Log.e("latitude", latitude);
            //Log.e("longitude", longitude);
            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }
}