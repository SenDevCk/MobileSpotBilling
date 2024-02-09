package org.cso.MobileSpotBilling;

import org.cso.MSBUtil.CryptographyUtil;
import org.cso.MSBUtil.GPSLocation;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MeterNbrInput extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_nbr_input);
		
		UtilAppCommon.billType = "M";
		UtilDB util = new UtilDB(getBaseContext());		
		UtilAppCommon.sdoCode=util.getSdoCode();
		UtilAppCommon.binder=util.getActiveMRU();
		//((TextView) findViewById(R.id.subDivisionTxt)).setText("NA");				
		//((TextView) findViewById(R.id.binderTxt)).setText("NA");
		((TextView) findViewById(R.id.subDivisionTxt)).setText(UtilAppCommon.sdoCode);				
		((TextView) findViewById(R.id.binderTxt)).setText(UtilAppCommon.binder);
        
        Button submitBtn = (Button) findViewById(R.id.btnGenerate);
        submitBtn.setOnClickListener(this);

        GPSLocation gps = new GPSLocation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.meter_nbr_input, menu);
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

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("Image Capture", "" + UtilAppCommon.blImageCapture);
		UtilAppCommon.bBtnGenerateClicked = true;
		if(UtilAppCommon.blImageCapture)	{
			startActivity(new Intent(this, ActvBilling.class));
		}
	}


    public void onBackPressed() {
	     // do something on back.
		 startActivity(new Intent(this, ActvBillingOption.class));  
		 finish();
	     return;
	 }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		
		switch(v.getId()){
		case R.id.btnGenerate:
	
			UtilAppCommon.inSAPMsgID = "";
			UtilAppCommon.inSAPMsg = "";
			
			String EncData="";
			String ActualData="";
			String CheckFieldData="";
			//To Assign User info to the struct variable
			
			UtilDB util = new UtilDB(getBaseContext());		
			util.getUserInfo();
			//End

			
			UtilAppCommon.meterNbr = ((EditText) findViewById(R.id.MeterNbrEdit)).getText().toString();
			
			Log.e("UtilAppCommon.MeterNbrEdit:::::", UtilAppCommon.meterNbr);
			/*			
			
			String CheckFieldData=util.GetConsumerInfoByField( UtilAppCommon.sdoCode, UtilAppCommon.binder,UtilAppCommon.acctNbr, "name");*/
			// Modified  by Kishore
			  if(!UtilAppCommon.meterNbr.isEmpty())
	          {
					
					 EncData= CryptographyUtil.Encrypt(util.GetConsumerInfoByField( UtilAppCommon.meterNbr, "Meter", "CONSUMER_NAME"));
					
					 ActualData=CryptographyUtil.Decrypt(EncData);
					 //Log.e("Consumer Name", ActualData);					 
					 
					 CheckFieldData=util.GetConsumerInfoByField( UtilAppCommon.meterNbr, "Meter", "CONSUMER_NAME");
	           }
			 
			
			if(UtilAppCommon.meterNbr.isEmpty())
			{
					Toast.makeText(getBaseContext(), "Please enter value for account number field", Toast.LENGTH_LONG).show();
			}	
			else if(!util.getBillInputDetails(UtilAppCommon.meterNbr, "Meter"))
			{
				Toast.makeText(getBaseContext(), "Please enter a valid Meter  number", Toast.LENGTH_LONG).show();
			}
			else if(ActualData.compareTo(CheckFieldData)!=0)
			{
				Toast.makeText(getBaseContext(), "Checksum key did not match!", Toast.LENGTH_LONG).show();
			}
			else
			{	
				UtilAppCommon.bBtnGenerateClicked = true;
				System.out.println("sudhir BTngenerated clicked on Entry form"+UtilAppCommon.bBtnGenerateClicked);
				startActivity(new Intent(this, ActvBilling.class));
				
				/*if(NetworkUtil.isOnline(MeterNbrInput.this,null))
				{
					UtilAppCommon.bBtnGenerateClicked = true;
					System.out.println("sudhir BTngenerated clicked on Entry form"+UtilAppCommon.bBtnGenerateClicked);
					startActivity(new Intent(this, ActvBilling.class));
				}
				else
				{
					AlertDialog.Builder altDialog = new AlertDialog.Builder(MeterNbrInput.this);
					altDialog.setTitle("Mobile data is off !");
					altDialog.setMessage("Do you still want to continue ?"); // here																				// add
					altDialog.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									UtilAppCommon.bBtnGenerateClicked = true;
									startActivity(new Intent(MeterNbrInput.this, ActvBilling.class));
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
				break;*/	
			}			
			break;
		}
		
	}
}
