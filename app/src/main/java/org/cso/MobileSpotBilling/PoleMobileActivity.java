package org.cso.MobileSpotBilling;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.cso.MSBAsync.AsyncGetOutputData;
import org.cso.MSBAsync.AsyncImage;
import org.cso.MSBAsync.AsyncUpdatePoleMobile;
import org.cso.MSBUtil.ImageProcessing;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PoleMobileActivity extends AppCompatActivity implements OnClickListener, TaskCallback {

	String AppDir = "";
	Button submitBtn;
	Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pole_mobile);
		
		Log.e("PoleMobileActivity", "Started");

		toolbar = findViewById(R.id.toolbar_updt_condet);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle("Syncronize");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		//Log.e("Pole No", UtilAppCommon.in.CONNECTED_POLE_NIN_NUMBER);
		//Log.e("Mobile No", UtilAppCommon.in.METER_CAP);
		
		((EditText) findViewById(R.id.PoleNoTxt)).setText(UtilAppCommon.in.CONNECTED_POLE_NIN_NUMBER);
		((EditText) findViewById(R.id.MobileNoTxt)).setText(UtilAppCommon.in.METER_CAP);
		
        submitBtn = (Button) findViewById(R.id.ContinueBtn);
        submitBtn.setOnClickListener(this);
        
        Button cancelBtn = (Button) findViewById(R.id.CancelBtn);
        cancelBtn.setOnClickListener(this);
        
        getImageByCANo(UtilAppCommon.acctNbr);
	}
	@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.ContinueBtn:
			
			submitBtn.setEnabled(false);
			//UtilAppCommon.acctNbr = ((EditText) findViewById(R.id.MobileNoTxt)).getText().toString();
			
			Boolean blflag = false;
			String strMobile = ((EditText) findViewById(R.id.MobileNoTxt)).getText().toString().trim();
			String strPoleNo = ((EditText) findViewById(R.id.PoleNoTxt)).getText().toString().trim();
			//Log.e("Mobile", strMobile.length() + "");
			//Log.e("Pole", strPoleNo + "");
			if(strPoleNo.equalsIgnoreCase("") && strMobile.equalsIgnoreCase(""))
			{
				Toast.makeText(getBaseContext(),"Please enter Mobile No or Pole No.", Toast.LENGTH_LONG).show();
				return;
			}
			else if(!strMobile.equalsIgnoreCase("") && (!isNumeric(strMobile) || strMobile.length() != 10))
			{
				Toast.makeText(getBaseContext(),"Please enter valid 10 digit mobile number.", Toast.LENGTH_LONG).show();
				return;
			}
			else
			{
				UtilDB util = new UtilDB(getApplicationContext());
				//if(UtilAppCommon.inSAPMsgID.contentEquals("6"))
				//util.UpdateMobPoleConsumer(strMobile, strPoleNo, UtilAppCommon.acctNbr);
				
				//5000020473|30.06.2015
				String[] copySAPInputData = new String[4];
				copySAPInputData[0] = UtilAppCommon.in.INSTALLATION;
				
				int temp = Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(5, 7));
				String strtemp = "";
				if(temp <= 9)
					strtemp = "0" + Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(5, 7));
				else
					strtemp = ""  + Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(5, 7));
				String strtemp1 = "";
				
				int temp1 = Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(8, 10));
				if(temp1 <= 9)
					strtemp1 = "0" + Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(8, 10));
				else
					strtemp1 = ""  + Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(8, 10));
				
				String nxtDate = strtemp1 + "." + strtemp
						+ "." + Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(0, 4));
				copySAPInputData[1] = nxtDate;
				
				copySAPInputData[2] = strMobile;
				copySAPInputData[3] = strPoleNo;

				
				AsyncUpdatePoleMobile asyncUpdatePoleMobile = new AsyncUpdatePoleMobile(this);
				asyncUpdatePoleMobile.execute(copySAPInputData);
				
				Log.e("UtilAppCommon.billType", UtilAppCommon.billType);
				
				if(UtilAppCommon.billType.equalsIgnoreCase("A"))
					startActivity(new Intent(this, ActvConsumerNbrInput.class));
				else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
					startActivity(new Intent(this, ActvLegacyNbrInput.class));
				else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
					startActivity(new Intent(this, ActvSequenceData.class));
				else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
					startActivity(new Intent(this, MeterNbrInput.class));
				else
					startActivity(new Intent(this, ActvBillingOption.class));
			}
			break;
			
		case R.id.CancelBtn:
			finish();
			if(UtilAppCommon.billType.equalsIgnoreCase("A"))
				startActivity(new Intent(this, ActvConsumerNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
				startActivity(new Intent(this, ActvLegacyNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
				startActivity(new Intent(this, ActvSequenceData.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
				startActivity(new Intent(this, MeterNbrInput.class));
			else
				startActivity(new Intent(this, ActvBillingOption.class));
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

	@Override
	public void done() {
		// TODO Auto-generated method stub
		finish();
		if(UtilAppCommon.billType.equalsIgnoreCase("A"))
			startActivity(new Intent(this, ActvConsumerNbrInput.class));
		else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
			startActivity(new Intent(this, ActvLegacyNbrInput.class));
		else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
			startActivity(new Intent(this, ActvSequenceData.class));
		else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
			startActivity(new Intent(this, MeterNbrInput.class));
		else
			startActivity(new Intent(this, ActvBillingOption.class));
		
	}
	
	public void getImageByCANo(String CANo)
	{
		Log.e("getImageByCANo", "Started");
		UtilDB utildb = new UtilDB(getApplicationContext());
		AppDir = Environment.getExternalStorageDirectory().getPath()
				+ "/SBDocs/Photos_Crop" + "/" + utildb.getSdoCode() + "/"
				+ utildb.getActiveMRU();
		Cursor cursorImage = utildb.getUnCompressedImage(CANo);
		File file = null;
		//getUnCompressedImage
		if(cursorImage != null)
		{
			cursorImage.moveToFirst();
			file = new File(AppDir , cursorImage.getString(1));
		}
		//ImageProcessing imageProcessing = new ImageProcessing();
		
		AsyncImage asyncImage = new AsyncImage(this , () -> {
			// TODO Auto-generated method stub
		});
		
		//String strArray[] = imageProcessing.processImage(AppDir, file, this, cursorImage.getString(1), CANo);
		String credentials[] = new String[6];
		credentials[0] = CANo;
		credentials[1] = cursorImage.getString(1).substring(4, 6);
		credentials[2] = cursorImage.getString(1).substring(0, 4);
		credentials[3] = utildb.getSdoCode();
		File f = new File(AppDir);
		credentials[4] = f.getAbsolutePath() + "/" + cursorImage.getString(1);
		credentials[5] = utildb.getActiveMRU();
		
		if(credentials != null)
			asyncImage.execute(credentials);
		
		Log.e("getImageByCANo", "Completed");
	}
}
