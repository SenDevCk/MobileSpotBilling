package org.cso.MobileSpotBilling;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.cso.MSBAsync.AsyncImage;
import org.cso.MSBAsync.AsyncUnuploadedImage;
import org.cso.MSBUtil.GPSTracker;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;
import org.cso.TVS.BarcodeCreater;
import org.cso.TVS.BitmapDeleteNoUseSpaceUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.widget.ActivityChooserView;
import androidx.core.content.ContextCompat;

import HPRTAndroidSDK.HPRTPrinterHelper;

public class ActvivityMain extends Activity implements OnClickListener, TaskCallback {
	/** Called when the activity is first created. */
	UtilDB util;
	static String strResponse;
	// static ActvivityMain xyy;
	Toast toast;
	ArrayList<HashMap<String, String>> mylist;
	SimpleAdapter adapter;
    boolean showalert=true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(this.TELEPHONY_SERVICE);*/
		mylist = new ArrayList<HashMap<String, String>>();

		//UtilAppCommon.IMEI_Number = telephonyManager.getDeviceId();
		UtilAppCommon.IMEI_Number = getImei();
		//UtilAppCommon.IMEI_Number = "358021057987855";	//Valid Device
		//UtilAppCommon.IMEI_Number = "858021057987855";	//Invalid Device
		UtilAppCommon.intAppInvoked = 1;
		
		/*if(!UtilAppCommon.ValidDevice)
		{
			AsyncValidateDevice asyncValidate = new AsyncValidateDevice(this);
			asyncValidate.execute(UtilAppCommon.IMEI_Number);
			finish();
		}*/



		setContentView(R.layout.main);
		File file = null;
		String AppDir = Environment.getExternalStorageDirectory().getPath()
				+ "/SBDocs";
		String billDir = AppDir + "/Pdf";
		String uploadDir = AppDir + "/InputFiles";
		String downloadDir = AppDir + "/Download";
		String outDir = AppDir + "/OutFiles";
		String photoDir = AppDir + "/Photos";
		String photoUnuploadDir = AppDir + "/.PhotosUnuploaded";

		file = new File(AppDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(billDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(uploadDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(downloadDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(outDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(photoDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(photoUnuploadDir);
		if (!file.exists()) {
			file.mkdirs();
		}

		String[] unUploadedFolder = new String[1];

		unUploadedFolder[0] = file.getAbsolutePath();
		AsyncUnuploadedImage asyncUnuploadedImage = new AsyncUnuploadedImage(this);
		asyncUnuploadedImage.execute(unUploadedFolder);


		//DeleteFolders();
		//UtilDB utilDB = new UtilDB(getApplicationContext());
		//utilDB.insertDummyImageData();
		//utilDB.updateRecompressedImage();
		
		Button billingBtn = (Button) findViewById(R.id.btnBilling);
		billingBtn.setOnClickListener(this);

		Button sendDataBtn = (Button) findViewById(R.id.btnUploadData);
		sendDataBtn.setOnClickListener(this);
		
		Button abnormalityBtn = (Button) findViewById(R.id.btnAbnormality);
		abnormalityBtn.setOnClickListener(this);
		
		Button syncBtn = (Button) findViewById(R.id.btnSync);
		syncBtn.setOnClickListener(this);
		
		Button btnMissingCon = (Button) findViewById(R.id.btnMissingCons);
        btnMissingCon.setOnClickListener(this);

		Button summeryBtn = (Button) findViewById(R.id.btnSummary);
		summeryBtn.setOnClickListener(this);

		Button ReportBtn = (Button) findViewById(R.id.btnReport);
		ReportBtn.setOnClickListener(this);

		Button setupBtn = (Button) findViewById(R.id.btnSetup);
		setupBtn.setOnClickListener(this);
		Button btnHelp = (Button) findViewById(R.id.btnHelp);
		btnHelp.setOnClickListener(this);

		Button quitBtn = (Button) findViewById(R.id.btnQuit);
		quitBtn.setOnClickListener(this);

		Button dummyPrint = (Button) findViewById(R.id.btnDummyPrint);
		dummyPrint.setOnClickListener(this);
		
		//showSettingsAlert();
		
		//callAsynchronousTask();
        /*GPSLocation gps = new GPSLocation();
		String strLocation = gps.getLocation(getApplicationContext());
		Log.e("strLocation", strLocation);
		if(strLocation.equalsIgnoreCase("Disabled"))
		{
			showSettingsAlert();
		}*/
		
	}

	public String getImei(){
		String  imei = null;
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				imei = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


			} else {
				TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
						PackageManager.PERMISSION_GRANTED) {
					//imei = telephonyManager.getDeviceId();
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
						assert telephonyManager != null;
						imei = telephonyManager.getDeviceId();
					} else {
						imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
					}
				}

			}
		}catch (Exception e){
			e.printStackTrace();
			imei = null;
		}
		return imei;
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
		int lIntAvailableInputDataCount = 0;
		util = new UtilDB(getBaseContext());
		lIntAvailableInputDataCount = util.getBillInputDetailsCount();
		switch (id) {
		case R.id.btnBilling:


			if (lIntAvailableInputDataCount > 0) {

				startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
				//UtilAppCommon.strRedirectTo = "Billing";
				//startActivity(new Intent(getBaseContext(), ActvLogin.class));
				finish();
			} else {
				toast = Toast.makeText(getApplicationContext(),
						"Please download the input data for Billing!",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			break;
		case R.id.btnSummary:
			if (lIntAvailableInputDataCount > 0) {
				ArrayList<HashMap<String, String>> mylist =util.getSummaryReport1();
				if(mylist.size() >= 1)
				{
					startActivity(new Intent(this, ActvSummary.class));
					finish();
				}
				else
					Toast.makeText(getApplicationContext(),
							"No summary available!", Toast.LENGTH_LONG);
			} 
			else 
			{
				Toast.makeText(getApplicationContext(),
						"No summary available!", Toast.LENGTH_LONG);
			}
			break;
		case R.id.btnSetup:
				Log.e("btnSetup Value"," btnSetup");
				//UtilAppCommon.strRedirectTo = "Setup";
				startActivity(new Intent(this, ActvSetupInfo.class));
				finish();
			break;
			
		case R.id.btnMissingCons:
			

			if (lIntAvailableInputDataCount > 0) {
				startActivity(new Intent(this, MissingConsumersActivity.class));
				//UtilAppCommon.strRedirectTo = "Billing";
				//startActivity(new Intent(getBaseContext(), ActvLogin.class));
				finish();
			} else {
				toast = Toast.makeText(getApplicationContext(),
						"Please download the input data for Missing Conumers!",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			//UtilAppCommon.billType = "MS";
			
			break;
			
		case R.id.btnAbnormality:
			
			if (lIntAvailableInputDataCount > 0) {
				startActivity(new Intent(this, Abnormality_Activity.class));
				//UtilAppCommon.strRedirectTo = "Billing";
				//startActivity(new Intent(getBaseContext(), ActvLogin.class));
				//finish();
			} else {
				toast = Toast.makeText(getApplicationContext(),
						"Please download the input data for Updating Abnormality!",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			//UtilAppCommon.billType = "MS";
			
			break;
			
		case R.id.btnSync:
			if (lIntAvailableInputDataCount > 0 && NetworkUtil.isOnline(ActvivityMain.this,null)) {
				startActivity(new Intent(this, ActivitySyncData.class));
				//UtilAppCommon.strRedirectTo = "Billing";
				//startActivity(new Intent(getBaseContext(), ActvLogin.class));
				finish();
			} 
			else if(!NetworkUtil.isOnline(ActvivityMain.this,null))
			{
				Toast.makeText(getApplicationContext(), "Please turn on Mobile Data", Toast.LENGTH_LONG).show();
			}
			else {
				toast = Toast.makeText(getApplicationContext(),
						"Please download the input data for Billing!",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			//UtilAppCommon.billType = "MS";
			//else
				//Toast.makeText(getApplicationContext(),
				//		"No data available for synchronize!", Toast.LENGTH_LONG);
		break;

		case R.id.btnUploadData:
			/*
			 * util=new UtilDB(this) ; String
			 * str=util.getUnpostedBilledData(this);
			 * System.out.println("sudhir@@@@@@@@@@@=======:::::-"+ str);
			 */
			
			/*final String[] mode = { "Upload All", "Binder Wise", "Consumer Wise", "Cancel" };
			if (NetworkUtil.isOnline(getApplicationContext(),null)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ActvivityMain.this);
				builder.setTitle("Select Data Upload Option");
				builder.setIcon(android.R.drawable.stat_sys_upload);
				builder.setItems(mode, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						Intent ActUpload=new Intent(ActvivityMain.this, ActvivityUpload.class);
						if (item == 0) {
							SaveBulkDataAsync asyncsavebulkdata = new SaveBulkDataAsync(
									ActvivityMain.this, mode[item], null);
							asyncsavebulkdata.execute("");
						} else if (item == 1 || item==2) {
							ActUpload.putExtra("mode", mode[item]);
							startActivity(ActUpload);
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();

				
			} else {
				Toast.makeText(getApplicationContext(),
						"No internet connection!", Toast.LENGTH_LONG).show();
			}*/
			
			break;

		case R.id.btnReport:
			// To fetch the count info from BillInput table.
			/*
			 * util= new UtilDB(getBaseContext());
			 * lIntAvailableInputDataCount=util.getBillInputDetailsCount();
			 */

			if (lIntAvailableInputDataCount > 0) {
				startActivity(new Intent(this, ActvReport.class));
				finish();
			} else {
				toast = Toast
						.makeText(
								getApplicationContext(),
								"To view reports,please download the input data from server!",
								Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			break;
		case R.id.btnHelp:
			startActivity(new Intent(this, ActvivityHelp.class));
			finish();
			break;
		case R.id.btnQuit:
			
			  //util=new UtilDB(this) ; 
			  //util.insertIntoTrfFC();
			  //util.Calc_Fc("1000", "30-11-13","31-12-14",2, 2, 0);
			  //util.insertIntoTrfEC();
			  //util.Calc_Ec("1000","30-11-13","31-12-14", 5000, 12);
			
			quitApp();
			
			break;
			case R.id.btnDummyPrint:
				//createReceiptDataOfflineFortvsHindi();
				break;
		}
	}

	public void onBackPressed() {
		 //quitApp();
		 finish();
		this.moveTaskToBack(true);
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
						ActvivityMain.this.finish();
						alertDialog.dismiss();
						UtilAppCommon.intIsLoggedIn = 0;
						UtilAppCommon.gIntAvailableInputDataCount = 0;
						UtilAppCommon.intAppInvoked = 0;
						UtilAppCommon.strBulkDataResponse = null;
						ActvivityMain xyz = new ActvivityMain();
						xyz.moveTaskToBack(false);
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
    	GPSTracker gps  = new GPSTracker(ActvivityMain.this);
    	
    	if(gps.canGetLocation()){
            
            String latitude = String.valueOf(gps.getLatitude());
            String longitude = String.valueOf(gps.getLongitude());
            
            Log.e("latitude", latitude);
            Log.e("longitude", longitude);
            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    	
    }
    
	public void done() {
		// TODO Auto-generated method stub
		finish();
	}

	private boolean createReceiptDataOfflineFortvsHindi() {
		BluetoothAdapter mBluetoothAdapter = null;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth not available.", Toast.LENGTH_LONG)
					.show();

			return false;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(
					this,
					"Bluetooth feature is turned off now...Please turned it on! ",
					Toast.LENGTH_LONG).show();
			mBluetoothAdapter.enable();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		UtilDB dbObj = new UtilDB(ActvivityMain.this);
		String[] printer = dbObj.GetPrinterInfo();

		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(printer[0]);
    	HPRTPrinterHelper hprtPrinterHelper = new HPRTPrinterHelper();
		try {
			int portOpen = hprtPrinterHelper.PortOpen("Bluetooth," + printer[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String method = "";
		String gstno = "";
		StringBuilder textData = new StringBuilder();
		try {
			String website = "";
			String header = null;
			header = "             NBPDCL";
			website = "www.nbpdcl.co.in";
			gstno = "NBGSTNO12457896";
			//textData.append(header + "\n");
			//textData.append("**********************\n");
			//textData.append("खाता नंबर     :   " + "1234" + "\n");
			/*textData.append("प्रमंडल         :   " + "1234" + "\n");
			textData.append("अवर प्रमंडल     :   " + "1234" + "\n");
			textData.append("उपभोक्ता  संख्या :   " + "123456789" + "\n");
			textData.append("खाता          :   " + "123546987" + "\n");*/
			textData.append("उपभोक्ता नाम   :   " + "123456" + "\n");
			String meterStatus = "";
			String meterReading = "";
			meterStatus = "Average";
			meterReading = "NA";
			hprtPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00});
			hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
			textData.delete(0, textData.length());
			textData.append("Meter Status : " + meterStatus + " ( 123456 ) " + "\n");
			/*textData.append("वर्तमान पढ़ने की तारीख : " + "1234654" + "\n");
			textData.append("वर्तमान रीडिंग (Kwh)   :  " + "1234654" + "\n");
			textData.append("पिछले  रीडिंग (Kwh)   :  " + "1234654" + "\n");*/
			String msg = " अत्यधिक उच्च खपत रिकॉर्ड\n. ";
			String amt = "123465";
			textData.append("अद्यतन बिल के लिए " + website + "\n पर जाएँ " + "या संबंधित अवर प्रमंडल / प्रमंडल   कार्यालय से संपर्क करें\n");
			textData.append(String.format("देय  तिथि  %s तक  विपत्र  राशि \n " ,amt));
			/*textData.append("एरर कोड    :        " + " 123456 " + "\n");
			textData.append("मीटर वाचक  :       " + " 123456 " + "\n");*/
			textData.append("हेल्पलाइन नं. :       " + "1912" + "\n");
			textData.append("***********************************\n");
			/*textData.append(("निर्वाचक सूची  का विशेष संक्षिप्त \n पुनरीक्षण कार्यक्रम,2020 \n" +
					"मतदाता सत्यापन  कार्यक्रम (इवीपी)  \n" +
					"   01-09-2019 से 18-11-2019 \n" +
					"मैं  करूंगा  अपनी प्रविष्टियां \n100 प्रतिशत सही\n\n\n\n\n").getBytes("UTF-8"));*/
			textData.append("****************************\n");
			//    boolean t=conn.isConnected();
			//conn.printData(textData.toString());
			hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
			textData.delete(0, textData.length());
			//
			printimage();
			showBarCode();
			textData.append("****************************\n");

			hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
			textData.delete(0, textData.length());

		} catch (Exception e) {
			e.printStackTrace();
			/*ShowMsg.showException(e, method, mContext);
			Utiilties.writeIntoLog(Log.getStackTraceString(e));*/
			return false;
		}
		textData = null;

		return true;
	}

	public void printimage() {
		//Log.e("bmp_print", String.valueOf(bmp_print));
		String PhotoDir = Environment.getExternalStorageDirectory()
				.getPath()
				+ "/DCIM/Camera/20210707_124237.jpg";
		File f =new File(PhotoDir.toString());
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		Bitmap bmp_print = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
		bmp_print = getResizedBitmap(bmp_print, 400);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bmp_print.compress(Bitmap.CompressFormat.JPEG, 90, out);

		DecimalFormat df = new DecimalFormat("0.00");
		int printWidth = 570;
		if (bmp_print.getWidth() >= printWidth) {

			float c = Float.valueOf(df.format((float) bmp_print.getWidth() / printWidth));

			int newHight =
					Integer.parseInt(new DecimalFormat("0").format(bmp_print.getHeight() / c));
			//bmp_print = zoomImg(bmp_print, printWidth, newHight);
			//Log.e("bmp_print2", bmp_print.toString());

		} else {

			float c = Float.valueOf(df.format((float) printWidth / bmp_print.getWidth()));

			Log.e("", "c：" + c);

			int newHight1 =
					Integer.parseInt(new DecimalFormat("0").format(c * bmp_print.getHeight()));
			bmp_print = zoomImg(bmp_print, printWidth, newHight1);

		}

		try {
			HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x01});
			//  Log.e("printnu", String.valueOf(1));
			for (int i = 0; i < 1; i++) {
				Log.e("bmp_print3", bmp_print.toString());
				HPRTPrinterHelper.PrintBitmap(bmp_print, (byte) 1, (byte) 0, 203);
				HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
				Thread.sleep(500);

			}
			//恢复居左对齐
			HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x00});
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
			e.printStackTrace();
		}
	}
	public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
		int width = 200;
		int height = 125;

		float bitmapRatio = (float) width / (float) height;
		if (bitmapRatio > 1) {
			// width = maxSize;
			height = (int) (width / bitmapRatio);
		} else {
			//height = maxSize;
			width = (int) (height * bitmapRatio);
		}
		return Bitmap.createScaledBitmap(image, width, height, true);
	}

    private void showBarCode() {
        Bitmap btMap_bar1 = BarcodeCreater.creatBarcode(this, "1010123545".trim(), 380, 50,
                true, 1);
        btMap_bar1 = BitmapDeleteNoUseSpaceUtil.deleteNoUseWhiteSpace(btMap_bar1);
        //btMap_bar1 = zoomImg(btMap_bar1, 570, btMap_bar1.getHeight());
        if (btMap_bar1 == null) {
            Toast.makeText(ActvivityMain.this, "no barcode", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            HPRTPrinterHelper.PrintBitmap(btMap_bar1, (byte) 0, (byte) 0, 203);
            //HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        return newbm;
    }

}
