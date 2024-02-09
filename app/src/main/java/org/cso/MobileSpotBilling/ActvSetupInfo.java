package org.cso.MobileSpotBilling;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.cso.MSBAsync.AsyncGetUserInfo;
import org.cso.MSBAsync.AsyncImage;
import org.cso.MSBUtil.AppUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;


import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ActvSetupInfo  extends Activity implements OnClickListener
{
	Activity activity = null;
	private SpinnerData binderItems[];
	
	
	//Bluetooth
	private BluetoothAdapter mBluetoothAdapter = null;
	private Set<BluetoothDevice> devices;
	private Context context=this;
	SimpleAdapter adapter;
	HashMap<String, String> devicemap=null;
	ArrayList<HashMap<String, String>> devicesadapter;
	private static final int PERMISSION_BLUETOOTH = 12;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
		UtilAppCommon.strImageCount = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupinfo); 
        activity = this;
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devicesadapter = new ArrayList<HashMap<String,String>>();

		if (ContextCompat.checkSelfPermission(ActvSetupInfo.this, Manifest.permission.BLUETOOTH)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(ActvSetupInfo.this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
		}


        Button verifysetupbtn = (Button)findViewById(R.id.btnVerifySetup);
        verifysetupbtn.setOnClickListener(this);
        
        Button downloadDataBtn=  (Button)findViewById(R.id.btnDownloadInputData);
        downloadDataBtn.setOnClickListener(this);		
		
        Button editsetupbtn = (Button) findViewById(R.id.btnModifySetup);
        editsetupbtn.setOnClickListener(this);
        
        Button btnSetupSDD = (Button) findViewById(R.id.btnSetupSDD);
        btnSetupSDD.setOnClickListener(this);
        
        Button btnSetupPrinter = (Button) findViewById(R.id.btnSetupPrinter);
        btnSetupPrinter.setOnClickListener(this);
        
        Button btnTestPrinter = (Button) findViewById(R.id.btnPrinterTest);
        btnTestPrinter.setOnClickListener(this);
        
        Button btnUpdateUserInfo = (Button) findViewById(R.id.btnClearData);
        btnUpdateUserInfo.setOnClickListener(this);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {


			case PERMISSION_BLUETOOTH: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission granted!
					// you may now do the action that requires this permission


				} else {
					// permission denied
					ActivityCompat.requestPermissions(ActvSetupInfo.this,
							new String[]{Manifest.permission.BLUETOOTH},
							PERMISSION_BLUETOOTH);
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
	   		/*finish();
	 		 startActivity(new Intent(ActvSetupInfo.this, ActvivityMain.class));*/
	   		 finish();	   	 
	    	 Intent intent = new Intent(this, ActvivityMain.class);  
	    	 startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));  
	         startActivity(intent);
	 		 break;
	   	 }
	     return true;
   }
   
   
   public void onClick(View v) 
	{
		// TODO Auto-generated method stub
	   final UtilDB util = new UtilDB(getBaseContext());
	   
		int id = v.getId();
		switch(id)
		{
		case R.id.btnModifySetup:
			//showModifySetup();
			startActivity(new Intent(this, ActvModifySetUpInfo.class));
			break;
			
			
		case R.id.btnVerifySetup:				
			//showVerifySetup();
			startActivity(new Intent(this, ActvVerifySetUpInfo.class));
			break;
		case R.id.btnSetupSDD:				
			/*
			final String[] Binders = UtilAppCommon.ui.MRUs.split("\\$");
		
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
	        builder.setTitle("Select Binder");
	        builder.setItems(Binders, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					final String Binder=Binders[arg1];
					
					AlertDialog.Builder altDialog = new AlertDialog.Builder(ActvSetupInfo.this);
					altDialog.setTitle(" Please Confirm");
					altDialog.setMessage("Are you sure to set single date for this binder ?"); // here																				// add
					altDialog.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									
									Toast.makeText(getApplicationContext(), Binder,Toast.LENGTH_LONG).show();
									UtilDB dbObj=new UtilDB(context);
									dbObj.InsertSingleDuedate(UtilAppCommon.ui.SDO_CD, Binder, UtilAppCommon.ui.BILL_MONTH,"Y");
									dbObj.close();
									
								}
							});
					
					altDialog.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
					altDialog.show();
				}

			});
	        AlertDialog alert = builder.create();
	        alert.show();
			*/
			
			break;
		case R.id.btnClearData:	

			 final Intent intent = new Intent(this, ActvivityMain.class);  
			 		 
				AlertDialog.Builder altDialog = new AlertDialog.Builder(ActvSetupInfo.this);
				altDialog.setTitle(" Please Confirm");
				altDialog.setMessage("Are you sure to clear Userinfo along with data ?\nClick OK to clear all user data"); // here																				// add
				altDialog.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								String result=util.ClearUserInfo();
								util.truncateTable("BillInput");
								util.truncateTable("BillOutput");
								util.truncateTable("SAPInput");
								Toast.makeText(getApplicationContext(), result,Toast.LENGTH_LONG).show();
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);	 
								startActivity(intent);
							}
						});
				
				altDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				altDialog.show();
			break;	
		case R.id.btnDownloadInputData:
			//showDownLoadDataWin();
			UtilDB utildb = new UtilDB(getBaseContext());
			Cursor cursorImage = utildb.getNonUploadedImage();
			if(cursorImage != null)
			{
				StoreByteImage();
			}
			else
			{
				final AlertDialog ad = new AlertDialog.Builder(this)
						.create();
				ad.setTitle("Confirm");
				ad.setMessage("Existing data will be deleted before downloading new data?");
				ad.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
						new DialogInterface.OnClickListener() {
			
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								ad.dismiss();
								showDownLoadDataWin();
							}
						});
				ad.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
						new DialogInterface.OnClickListener() {
			
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								ad.dismiss();
							}
						});
				ad.show();
			}
			
			break;
		case R.id.btnSetupPrinter:
			SetupPrinter();
			break;
			
		case R.id.btnPrinterTest:
			
			break;
		}
		
		
	}
   
   public void showDownLoadDataWin() 
   {
	   	UtilDB util = new UtilDB(getBaseContext());
		String result = util.ClearUserInfo();
		util.truncateTable("BillInput");
		util.truncateTable("BillOutput");
		util.truncateTable("SAPInput");
		UtilAppCommon.strRedirectTo = "Setup";
		DeleteFolders();
		startActivity(new Intent(this,ActvLogin.class));
		//startActivity(new Intent(getBaseContext(), ActvDownloadInputData.class));
   }
   
   public void onBackPressed() {
		 startActivity(new Intent(this, ActvivityMain.class));
		 finish();
	     return;
	 }
  /* @Override
	public void onBackPressed() {
	 Intent setIntent = new Intent(Intent.ACTION_MAIN);
	 setIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 finish();
	 startActivity(setIntent);

	}*/
   void SetupPrinter()
   {

	   devicesadapter.clear();
	   mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       adapter=new InteractiveArrayAdapter(this,R.layout.rowlayout, devicesadapter,
       		new String[] {"Name"},
       new int[] {R.id.tvLvRowName});
		if (mBluetoothAdapter == null) {
		Toast.makeText(this, "Bluetooth not available.",Toast.LENGTH_LONG).show();
		return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
			return;
		} 
		
		devices=mBluetoothAdapter.getBondedDevices();
		
		if (devices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : devices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	devicemap=new HashMap<String,String>();
		    	devicemap.put("Name", device.getName());
		    	devicemap.put("Address", device.getAddress());
		    	devicesadapter.add(devicemap);
		    }
		}
		else
		{
			devicesadapter.clear();
			Toast.makeText(getApplicationContext(),"No Device is Paired", Toast.LENGTH_SHORT);
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Printer Type");
        builder.setItems(R.array.PrinterType, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
            	final String devicetype=context.getResources().getStringArray(R.array.PrinterType)[item];
            	
            	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		        builder.setTitle("Select Printing Device");
		        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int item) {
		            	HashMap<String, String> map=devicesadapter.get(item);
		            	  
		              	Toast.makeText(context,map.get("Name")+" : "+ map.get("Address"), Toast.LENGTH_LONG).show();
		              	UtilDB dbobj=new UtilDB(context);
		              	dbobj.UpdatePrinterInfo(map.get("Address"), devicetype);
		              	String[] Printer=dbobj.GetPrinterInfo();
		              	Toast.makeText(context,"Printer Configured Successfully\n"+Printer[0]+" : "+Printer[1], Toast.LENGTH_LONG).show();
		              	
		            }
		        });
		        AlertDialog alert = builder.create();
		        alert.show();           	
            }
        });
        AlertDialog alert = builder.create();
        alert.show();	   
   }
   
   private void showModifySetup() 
   {
	 	final Dialog dialog = new Dialog(this);
	   	dialog.setContentView(R.layout.dg_modifysetupinfo);
	   	dialog.setTitle("Modify Setup Info:");
	   	dialog.show();
	   	
	   	UtilDB util = new UtilDB(getBaseContext());
	   	UtilAppCommon.sdoCode= util.getSdocd();	
	   	System.out.println("UtilAppCommon.sdoCode inside showModifySetup activity  "+UtilAppCommon.sdoCode);
	   	
	   	
		util.close();  	
	   	
		
	   	
	   	UtilDB util2 = new UtilDB(getBaseContext());
	   	
	   	String binder_list = util2.binderlist();
	   	
		util2.close();
		System.out.println("binder list is "+binder_list);	
		
		
		//Spinner s=(Spinner) findViewById(R.id.BinderEdit);
		Spinner s=(Spinner) findViewById(R.id.spnBinderList);
		
		String binder_code[] = binder_list.split("\\$");
		
		binderItems=new SpinnerData[binder_code.length];
		
		System.out.println("binder item list length is "+binder_code.length);
		
		for(int i=0;i<binder_code.length;i++)
		{
			binderItems[i]=new SpinnerData(binder_code[i]);
			System.out.println("binder     ===="+binder_code[i]);
		}
		
		
		ArrayAdapter<SpinnerData> adapter = new ArrayAdapter<SpinnerData>(this,android.R.layout.simple_spinner_item,binderItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		
	
	   	Button okButton = (Button) dialog.findViewById(R.id.btnOk);
	
	   	okButton.setOnClickListener(new OnClickListener() 
	   	{
	   		public void onClick(View v) 
	   		{
	   			dialog.dismiss();   			
	   		}
	   	});
   }
   
	public void DeleteFolders()
	{
		File file = null;
		String AppDir = Environment.getExternalStorageDirectory().getPath()
				+ "/SBDocs";
		String billDir = AppDir + "/Pdf";
		String uploadDir = AppDir + "/InputFiles";
		String downloadDir = AppDir + "/DOWNLOAD";
		String outDir = AppDir + "/OUTFILES";
		String photoDir = AppDir + "/PHOTOs";
		String photoCropDir = AppDir + "/Photos_Crop";
		String photoCompressedDir = AppDir + "/Photos_Compressed";
		String photoUunploadDir = AppDir + "/PhotosUnuploaded";
	
		file = new File(billDir);
		if (file.exists()) {
			deleteDir(file);
			//Log.e(billDir, "" + file.delete());
		}
		file = new File(uploadDir);
		if (file.exists()) {
			deleteDir(file);
			//Log.e(uploadDir, "" + file.delete());
		}
		file = new File(downloadDir);
		if (file.exists()) {
			deleteDir(file);
			//Log.e(downloadDir, "" + file.delete());
		}
		file = new File(outDir);
		if (file.exists()) {
			deleteDir(file);
			//Log.e(outDir, "" + file.delete());
		}
		file = new File(photoDir);
		if (file.exists()) {
			deleteDir(file);
			//Log.e(photoDir, "" + file.delete());
		}

		/*Commented On 091017 for Separate Uploading of Images*/
		/*file = new File(photoUunploadDir);
		if (file.exists()) {
			deleteDir(file);
			//Log.e(photoDir, "" + file.delete());
		}*/

		file = new File(photoCompressedDir);
		if (file.exists()) {
			deleteDir(file);
			//Log.e(photoDir, "" + file.delete());
		}

		file = new File(photoCropDir);
		if (file.exists()) {
			deleteDir(file);
			//Log.e(AppDir, "" + file.delete());
		}

		/*Commented On 091017 for Separate Uploading of Images*/
		/*file = new File(AppDir);
		if (file.exists()) {
			deleteDir(file);
			//Log.e(AppDir, "" + file.delete());
		}*/



	}
	
	void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            deleteDir(f);
	        }
	    }
	    file.delete();
	}
	
	public boolean StoreByteImage() 
	{
		try 
		{
			File file;
			UtilDB utildb = new UtilDB(getApplicationContext());
			Cursor cursorImage = utildb.getNonUploadedImage();
			UtilAppCommon.strImageCount = utildb.getNonUploadedImageCount();
			if(cursorImage != null)
			{
				cursorImage.moveToFirst();
				do
				{
					/*String AppDir = Environment.getExternalStorageDirectory().getPath()
							+ "/SBDocs/Photos_Compressed" + "/" + utildb.getSdoCode() + "/"
							+ utildb.getActiveMRU();*/
					
					//Temporary Change Added
					String AppDir = Environment.getExternalStorageDirectory().getPath()
							+ "/SBDocs/Photos_Crop" + "/" + utildb.getSdoCode() + "/"
							+ utildb.getActiveMRU();
					
					file = new File(AppDir , cursorImage.getString(1));
					File f = new File(AppDir);
					String fullPath = f.getAbsolutePath();
					String credentials[] = new String[6];
					credentials[0] = cursorImage.getString(0);
					credentials[1] = cursorImage.getString(1).substring(4, 6);
					credentials[2] = cursorImage.getString(1).substring(0, 4);
					credentials[3] = utildb.getSdoCode();
					credentials[4] = fullPath + "/" + cursorImage.getString(1);
					credentials[5] = utildb.getActiveMRU();
					
					//Log.e("AsyncImage Call"," Month ==>> " + cursorImage.getString(1).substring(4, 6));
					//Log.e("AsyncImage Call"," Year ==>> " + cursorImage.getString(1).substring(0, 4));
					AsyncImage asyncImage = new AsyncImage(this,new OnBillGenerate() {
						
						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							
						}
					});
					asyncImage.execute(credentials);
				}while(cursorImage.moveToNext());
			}
			else
				Toast.makeText(getApplicationContext(),"No Image to Upload", Toast.LENGTH_LONG).show();
		}//create data file
		/*catch (FileNotFoundException e) 
		{
		   Log.e("DATAFILE", "File Not Found Error = " + e.getMessage());
		   e.printStackTrace();
		   return false;
		}//data file error 
		catch (IOException e) 
		{
		   Log.e("DATAFILE", "IOException Error" + e.getMessage());
		   e.printStackTrace();
		   return false;
		}//data file error
*/		catch (Exception e) 
		{
		   Log.e("Sync Data", "Image File E = " + e.getMessage());
		   e.printStackTrace();
		   return false;
		}//data file error 
		    return true;
	}//storebyteimage  


   
}



  
     