package org.cso.MobileSpotBilling;

import java.io.File;

import org.cso.MSBAsync.AsyncGetInputData;
import org.cso.MSBAsync.AsyncGetUserInfo;
import org.cso.MSBModel.StructUserInfo;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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



public class ActvDownloadInputData extends Activity implements OnClickListener,TaskCallback{
    /** Called when the activity is first created. */
	UtilDB  util;
	EditText username,password;	
	String Param = "";
	TextView txtVersion;
	
    @SuppressLint("MissingInflatedId")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dg_downloadinputdata);
        
        txtVersion = (TextView) findViewById(R.id.TxtVersion);
        UtilAppCommon.strRedirectTo = "";
        createFolders();
        inputDataDownloadDetails();
       
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
	 		startActivity(new Intent(this, ActvivityMain.class));*/
	   		finish();	   	 
	    	 Intent intent = new Intent(this, ActvivityMain.class);  
	    	 startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));  
	         startActivity(intent);
	   		break;
	     }
	     return true;
    }
    
    public void onBackPressed() {		 
		 finish();
	     return;
	 }   
       
	public void onClick(View view) {
		int id = view.getId();
		switch(id){
		case R.id.btnOk:

			String Param[] = new String[2];
			UtilDB util= new UtilDB(getBaseContext());   			
			int lIntAvailableInputDataCount = util.getBillInputDetailsCount();
			Param[0] = UtilAppCommon.IMEI_Number;
			Param[1] = UtilAppCommon.strAppVersion;
			Log.e("App Version", UtilAppCommon.strAppVersion);
			final Boolean blFlag = false;
			
			Log.e("Input Count", lIntAvailableInputDataCount + "  ==>>  " + Param);
				if(lIntAvailableInputDataCount > 0)
				{   				
					Toast.makeText(getApplicationContext(),"Input data for the selected MRU is already downloaded!", Toast.LENGTH_LONG).show();
				}
				else
				{
					if(NetworkUtil.isOnline(getApplicationContext(),null))
					{	
						/*if(!UtilAppCommon.ValidVersion)
							Toast.makeText(getApplicationContext(),"Updated the application before downloading new MRU", Toast.LENGTH_LONG).show();
						else
						{*/
							AsyncGetInputData asyncGetInputData=new AsyncGetInputData(this);
			   				asyncGetInputData.execute(Param);
			   				util.updateUserInfo();
						//}
					}
					else   					
					{
						Toast.makeText(getApplicationContext(),"No internet connection!", Toast.LENGTH_LONG).show();
					}   				
					//finish();
				}
			break;
			
		case R.id.btnCancel:			
			finish();
			break;		 
		}
	}
	
	private void inputDataDownloadDetails() 
	   {
			setContentView(R.layout.dg_downloadinputdata);

			View v = findViewById(R.id.downloadInputDataLayout);
			v.setVisibility(View.VISIBLE);

			Button btnOk = (Button) findViewById(R.id.btnOk);
			btnOk.setOnClickListener(this);

			Button btnCancel = (Button) findViewById(R.id.btnCancel);
			btnCancel.setOnClickListener(this);
			
			UtilDB util = new UtilDB(getBaseContext());		
			util.getUserInfo();
			
			String limie = UtilAppCommon.IMEI_Number;

			((TextView) findViewById(R.id.txtVwImie)).setText(limie);
	   }
	
	public void done() 
	{
		finish();
//		startActivity(new Intent(this, ActvivityMain.class));
	}
	
	public void createFolders()
	{
		File file = null;
		String AppDir = Environment.getExternalStorageDirectory().getPath()
				+ "/SBDocs";
		String billDir = AppDir + "/Pdf";
		String uploadDir = AppDir + "/InputFiles";
		String downloadDir = AppDir + "/DOWNLOAD";
		String outDir = AppDir + "/OUTFILES";
		String photoDir = AppDir + "/PHOTOs";

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
	}
}