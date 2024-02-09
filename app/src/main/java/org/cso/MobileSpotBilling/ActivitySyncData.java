package org.cso.MobileSpotBilling;

//import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.cso.MSBAsync.AsyncAbnormality;
import org.cso.MSBAsync.AsyncGetOutputData;
import org.cso.MSBAsync.AsyncImage;
import org.cso.MSBAsync.AsyncMissingCons;
import org.cso.MSBModel.StructSAPInput;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView.BufferType;
import android.widget.Toast;

//import javax.imageio.*;

public class ActivitySyncData extends Activity implements OnClickListener, TaskCallback {

    String photoId = "";
    File file, compfile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync_bills);
		
		Button calculateBillBtn = (Button) findViewById(R.id.btnSyncData);
	    calculateBillBtn.setOnClickListener(this);
	    
	    Button btnSyncDetails = (Button) findViewById(R.id.btnSyncCons);
	    btnSyncDetails.setOnClickListener(this);
	    
	    Button btnSyncAbnorm = (Button) findViewById(R.id.btnSyncAbnorm);
	    btnSyncAbnorm.setOnClickListener(this);
	    
	    Button btnUploadImages = (Button) findViewById(R.id.btnUploadImages);
	    btnUploadImages.setOnClickListener(this);
	    
	    
	    
	    //
	}
	
	private ProgressDialog pDialog = null;
	private Context context;
	int cnt;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_sync_data, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}


		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
	
		switch (id) {
		case R.id.btnSyncData:
			Log.e("btnSyncData ==>> ", "Started");
			UtilDB util = new UtilDB(getApplicationContext());
			StoreByteImage();
			Cursor c = util.getBilledRouteSequence("");
			if(c.getCount() > 0)
			{
				//ProgressDialog pDialog = null;
				Toast.makeText(getApplicationContext(), " Synchronisation in progress. Please wait", Toast.LENGTH_LONG).show();
				UtilAppCommon.blActyncBtn = true;
				startActivity(new Intent(ActivitySyncData.this, SeqSyncActivity.class));
				//finish();
				//UtilAppCommon.blActyncBtn = false;
			}
			else
				Toast.makeText(getApplicationContext(), " No data to synchronize.", Toast.LENGTH_LONG).show();
			//ProgressDialog pDialog = null;
			Log.e("btnSyncData ==>> ", "Completed");
			break;
			
		case R.id.btnSyncCons:
			int cnt=0;
			try {
								
				UtilDB utilDB = new UtilDB(getApplicationContext());
				Cursor cursor = utilDB.getMissedCons();
				//Log.e("Sync Miss Cons Insert", "Completed   " + cursor.getCount());
				
				if(cursor!= null)
				{
					cnt = cursor.getCount();
					cursor.moveToFirst();
					do
					{				
						Boolean blflag = false;
						String[] strInput = new String[9];
						String strCANbr,strLegNbr;
						if(cursor.getString(0).trim().equals(""))
							strCANbr = "";
						else
							strCANbr = cursor.getString(0);
						
						if(cursor.getString(1).trim().equals(""))
							strLegNbr = "";
						else
							strLegNbr = cursor.getString(1);

						strInput[0] = strCANbr;
						strInput[1] = strLegNbr;
						strInput[2] = cursor.getString(2);
						strInput[3] = cursor.getString(5);
						strInput[4] = cursor.getString(6);
						strInput[5] = cursor.getString(7);
						strInput[6] = cursor.getString(3);
						strInput[7] = cursor.getString(4);
						strInput[8] = "2";

						//util.InsertConsumerDetails(strInput);
						Log.e("Sync Miss Cons Insert", "Completed");
						
						AsyncMissingCons asyncUpdateMissCons = new AsyncMissingCons(this);
						asyncUpdateMissCons.execute(strInput);
					}while(cursor.moveToNext());
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				Log.e("Sync Missed - NFE", e.getMessage());
				e.printStackTrace();
			}
			 catch (Exception e) {
					// TODO Auto-generated catch block
				 Log.e("Sync Missed - E", e.getMessage());
					e.printStackTrace();
				}
			
			Toast.makeText(getApplicationContext(),cnt + " No of missing consumers synchronized.", Toast.LENGTH_LONG).show();
			break;
			
		case R.id.btnSyncAbnorm:
			
			cnt=0;
			try {
								
				UtilDB utilDB = new UtilDB(getApplicationContext());
				Cursor cursor = utilDB.getAbnormalityCons();
				//Log.e("Sync Miss Cons Insert", "Completed   " + cursor.getCount());
				
				if(cursor!= null)
				{
					cnt = cursor.getCount();
					cursor.moveToFirst();
					do
					{				
						Boolean blflag = false;
						String[] strInput = new String[7];
						String strCANbr,strLegNbr;
						if(cursor.getString(0).trim().equals(""))
							strCANbr = "";
						else
							strCANbr = cursor.getString(0);

						strInput[0] = cursor.getString(1);
						strInput[1] = cursor.getString(2);
						strInput[2] = cursor.getString(3);
						strInput[3] = cursor.getString(4);
						strInput[4] = cursor.getString(5);
						strInput[5] = cursor.getString(6);
						strInput[6] = "0";

						//util.InsertConsumerDetails(strInput);
						Log.e("Sync Abnorm Cons Insert", "Completed");
						
						AsyncAbnormality asyncAbnormality = new AsyncAbnormality(this);
						asyncAbnormality.execute(strInput);
					}while(cursor.moveToNext());
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				Log.e("Sync Abnormality - NFE", e.getMessage());
				e.printStackTrace();
			}
			 catch (Exception e) {
					// TODO Auto-generated catch block
				 Log.e("Sync Abnormality - E", e.getMessage());
					e.printStackTrace();
				}
			
			Toast.makeText(getApplicationContext(),cnt + " No of abnormality consumers synchronized.", Toast.LENGTH_LONG).show();
			break;
		
		case R.id.btnUploadImages:
				StoreByteImage();
			break;
		}
	}
	
	public boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Float.parseFloat(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
		  Log.e("IsNumeric", nfe.getMessage());
		  return false; 
	  }  
	  return true;  
	}
// Added 20.11.15
	private void printdlg(){
		{
			// need to be change for photo

			final AlertDialog ad = new AlertDialog.Builder(this)
					.create();
			ad.setTitle("Confirm");
			ad.setMessage("Confirm to print");
			ad.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							ad.dismiss();
							Write2SbmOut();
							//startActivity(new Intent(ctx, ActvBillPrinting.class));
						}
					});
			ad.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							ad.dismiss();
							// startActivity(getIntent());
							//startActivity(new Intent(getApplicationContext(), ActvBillingOption.class));
							/*
							if(UtilAppCommon.billType.equalsIgnoreCase("A"))
								startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
							else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
								startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
							else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
								startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
							else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
								startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
							else
								startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
							
							*/
						}
					});
			ad.show();
		}
	}
	
	// 20.11.15
	private void Write2SbmOut() {
		// TODO Auto-generated method stub
		Log.e("Write2SbmOut", "Start");
		
		try {
			Log.e("Write2SbmOut", "In Try Start");
			Log.e("Msg -- Id", UtilAppCommon.inSAPMsgID);
			//RC = SC = 0;// Re initialise rc ans sc value
			Log.e("Write2SbmOut", "In Try");
			//finish();
			UtilDB util1 = new UtilDB(this);
			//printbill();
			int cnt;
			cnt = util1.getBillOutputRowCount(UtilAppCommon.acctNbr);
			System.out.println("Output cnt .."+cnt);
			if(cnt != 0)
			{
				util1.getOutputBillRecord(UtilAppCommon.acctNbr);
				//22.11.15
				try {
					util1.copyToOutputStruct(UtilAppCommon.SAPIn.CANumber);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("ActBill copyOut E", e.getMessage());
				}
				//startActivity(new Intent(this, ActivitySyncData.class)); // used to print bill through printer
			}
			else
			{
				if(UtilAppCommon.inSAPMsgID.toString().equals("4") || UtilAppCommon.inSAPMsgID.toString().equals("5"))
					startActivity(new Intent(this, ActvMsgPrinting.class)); // used to print bill through printer
				else
				{
					String strMsg = "";
					if(UtilAppCommon.inActualSAPMsgID.toString().equals("1"))
						strMsg = "MRO Not Found";
					if(UtilAppCommon.inActualSAPMsgID.toString().equals("2"))
						strMsg = "Reading not uploaded";
					if(UtilAppCommon.inActualSAPMsgID.toString().equals("3"))
						strMsg = "Bill not created";
					else
						strMsg = "Network Issue";
					Toast.makeText(getBaseContext(), strMsg + ", please try again after sometime.", Toast.LENGTH_LONG).show();
					//startActivity(new Intent(this, ActvBillingOption.class));
				}	
				//finish();
			}
			 //printbill();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("Write2SbmOut E", e.getMessage());
		}
		Log.e("Write2SbmOut", "Completed");
	}
	
	@Override
	public void done() {
		// TODO Auto-generated method stub
		finish();
	}
	
   public void onBackPressed() {
		 startActivity(new Intent(this, ActvivityMain.class));
		 finish();
	     return;
	 }
   
	public boolean StoreByteImage() 
	{
		try 
		{
			UtilDB utildb = new UtilDB(getApplicationContext());
			Cursor cursorImage = utildb.getNonUploadedImage();
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
