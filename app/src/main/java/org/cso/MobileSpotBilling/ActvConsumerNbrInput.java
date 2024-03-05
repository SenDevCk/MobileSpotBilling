package org.cso.MobileSpotBilling;

import org.cso.MSBUtil.CryptographyUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActvConsumerNbrInput extends AppCompatActivity implements OnClickListener{
		EditText editText_accNo;
		Toolbar toolbar;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumernbrinput);
		toolbar= findViewById(R.id.toolbar_nbr_input_rechek);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle("Bill By Account Number");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
        UtilAppCommon.billType = "A";
        
		UtilAppCommon.inSAPSendMsg = "";
		UtilAppCommon.inSAPMsgID = "";
		UtilAppCommon.inSAPMsg = "";
        
        //Dt.28.09.15
		UtilDB util = new UtilDB(getBaseContext());		
		UtilAppCommon.sdoCode=util.getSdoCode();
		UtilAppCommon.binder=util.getActiveMRU();
		//((TextView) findViewById(R.id.subDivisionTxt)).setText("NA");				
		//((TextView) findViewById(R.id.binderTxt)).setText("NA");
		((TextView) findViewById(R.id.subDivisionTxt)).setText(UtilAppCommon.sdoCode);				
		((TextView) findViewById(R.id.binderTxt)).setText(UtilAppCommon.binder);
		editText_accNo = (EditText) findViewById(R.id.AcctNbrEdit);
		if(UtilAppCommon.acctNbr != null && UtilAppCommon.acctNbr.length() > 0){
			editText_accNo.setText(UtilAppCommon.acctNbr);
		}
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
	protected void onResume() {
		super.onResume();
		Log.i("Image Capture", "" + UtilAppCommon.blImageCapture);
		UtilAppCommon.bBtnGenerateClicked = true;
		if(UtilAppCommon.blImageCapture)	{
			UtilAppCommon.billType = "A";
			startActivity(new Intent(this, ActvBilling.class));
		}
	}

    
	public void onClick(View v) {
		TextView errTxt = (TextView)findViewById(R.id.nullBinderSubdivErrLbl);
		
		Log.e("Help", "");
		
        //Log.e("Latitide", lat);
        //Log.e("Longitude", lon);
		
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

			
			UtilAppCommon.acctNbr = editText_accNo.getText().toString();
			
			//Log.e("UtilAppCommon.acctNbr:::::", UtilAppCommon.acctNbr);
			/*			
			
			String CheckFieldData=util.GetConsumerInfoByField( UtilAppCommon.sdoCode, UtilAppCommon.binder,UtilAppCommon.acctNbr, "name");*/
			// Modified  by Kishore
			  if(!UtilAppCommon.acctNbr.isEmpty())
	          {
					
					 EncData= CryptographyUtil.Encrypt(util.GetConsumerInfoByField( UtilAppCommon.acctNbr, "Accno", "CONSUMER_NAME"));
					
					 ActualData=CryptographyUtil.Decrypt(EncData);
					 //Log.e("Consumer Name", ActualData);					 
					 
					 CheckFieldData=util.GetConsumerInfoByField( UtilAppCommon.acctNbr, "Accno", "CONSUMER_NAME");
	           }
			 
			
			if(UtilAppCommon.acctNbr.isEmpty())
			{
					Toast.makeText(getBaseContext(), "Please enter value for account number field", Toast.LENGTH_LONG).show();
			}	
			else if(!util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number"))
			{
				Toast.makeText(getBaseContext(), "Please enter a valid account number", Toast.LENGTH_LONG).show();
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
				/*if(NetworkUtil.isOnline(ActvConsumerNbrInput.this,null))
				{
					UtilAppCommon.bBtnGenerateClicked = true;
					System.out.println("sudhir BTngenerated clicked on Entry form"+UtilAppCommon.bBtnGenerateClicked);
					startActivity(new Intent(this, ActvBilling.class));
				}
				else
				{
					AlertDialog.Builder altDialog = new AlertDialog.Builder(ActvConsumerNbrInput.this);
					altDialog.setTitle("Mobile data is off !");
					altDialog.setMessage("Do you still want to continue ?"); // here																				// add
					altDialog.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									UtilAppCommon.bBtnGenerateClicked = true;
									startActivity(new Intent(ActvConsumerNbrInput.this, ActvBilling.class));
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
				break;	*/
			}			
			break;
		}
	}

}
