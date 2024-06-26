package org.cso.MobileSpotBilling;

import org.cso.MSBUtil.CryptographyUtil;
import org.cso.MSBUtil.GPSLocation;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActvLegacyNbrInput extends AppCompatActivity implements OnClickListener{
	Toolbar toolbar;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legacynbrinput);
		toolbar= findViewById(R.id.toolbar_bill_bylagecy);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle("Bill By Account Number");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
        UtilDB util = new UtilDB(getBaseContext());	
        UtilAppCommon.sdoCode=util.getSdoCode();
		UtilAppCommon.binder=util.getActiveMRU();
		
		UtilAppCommon.billType = "L";
		
		UtilAppCommon.inSAPSendMsg = "";
		UtilAppCommon.inSAPMsgID = "";
		UtilAppCommon.inSAPMsg = "";
		
		((TextView) findViewById(R.id.subDivisionTxt)).setText(UtilAppCommon.sdoCode);				
		((TextView) findViewById(R.id.binderTxt)).setText(UtilAppCommon.binder);
		
		Button submitBtn = (Button) findViewById(R.id.btnGenerate);
        submitBtn.setOnClickListener(this);
        
        //GPSLocation gps = new GPSLocation();
        //Log.e("GPS ==>> ", gps.getLocation(getApplicationContext()));
    }

	@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e("onRestart", "ActvLegacyNbrInput");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("onResume", "ActvLegacyNbrInput");
		Log.i("Image Capture", "" + UtilAppCommon.blImageCapture);
		UtilAppCommon.bBtnGenerateClicked = true;
		if(UtilAppCommon.blImageCapture)	{
			startActivity(new Intent(this, ActvBilling.class));
		}
	}

    public void onClick(View v) {
		TextView errTxt = (TextView)findViewById(R.id.nullBinderSubdivErrLbl);
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
			
			UtilAppCommon.legNbr = ((EditText) findViewById(R.id.AcctNbrEdit)).getText().toString();
			Log.e("Legacy Number>", ((EditText) findViewById(R.id.AcctNbrEdit)).getText().toString());
			//System.out.println("UtilAppCommon.acctNbr:::::"+UtilAppCommon.acctNbr);

		   if(!UtilAppCommon.legNbr.isEmpty())
          	{
				 EncData= CryptographyUtil.Encrypt(util.GetConsumerInfoByField( ((EditText) findViewById(R.id.AcctNbrEdit)).getText().toString(), "Legacy", "CONSUMER_NAME"));
				
				 ActualData=CryptographyUtil.Decrypt(EncData);
				
				 CheckFieldData=util.GetConsumerInfoByField( ((EditText) findViewById(R.id.AcctNbrEdit)).getText().toString(), "Legacy", "CONSUMER_NAME");
				// Toast.makeText(getBaseContext(), "Security key :"+ActualData+ "\n CheckFieldData: "+CheckFieldData, Toast.LENGTH_LONG).show();
           	}	
						
			if(UtilAppCommon.legNbr.isEmpty())
			{
					Toast.makeText(getBaseContext(), "Please enter value for legacy number field", Toast.LENGTH_LONG).show();
			}	
			else if(!util.getBillInputDetails(UtilAppCommon.legNbr, "Legacy"))
			{
				Toast.makeText(getBaseContext(), "Please enter a valid legacy number", Toast.LENGTH_LONG).show();
			}
			else if(ActualData.compareTo(CheckFieldData)!=0)
			{
				Toast.makeText(getBaseContext(), "Checksum key did not match!", Toast.LENGTH_LONG).show();
			}
			else
			{	
				Intent intent = new Intent(this, ActvBilling.class);
				UtilAppCommon.bBtnGenerateClicked = true;
				System.out.println("sudhir BTngenerated clicked on Entry form"+UtilAppCommon.bBtnGenerateClicked);
				intent.putExtra("BillingType", "Legacy");
				startActivity(intent);
				
				/*if(NetworkUtil.isOnline(ActvLegacyNbrInput.this,null))
				{
					Intent intent = new Intent(this, ActvBilling.class);
					UtilAppCommon.bBtnGenerateClicked = true;
					System.out.println("sudhir BTngenerated clicked on Entry form"+UtilAppCommon.bBtnGenerateClicked);
					intent.putExtra("BillingType", "Legacy");
					startActivity(intent);
				}
				else
				{
					AlertDialog.Builder altDialog = new AlertDialog.Builder(ActvLegacyNbrInput.this);
					altDialog.setTitle("Mobile data is off !");
					altDialog.setMessage("Do you still want to continue ?"); // here																				// add
					altDialog.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									UtilAppCommon.bBtnGenerateClicked = true;
									startActivity(new Intent(ActvLegacyNbrInput.this, ActvBilling.class));
								}
							});
					altDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
					altDialog.show();
				}
				break;		*/		
			}			
			break;
		}
		
	}
	
}
