package org.cso.MobileSpotBilling;

import org.cso.MSBUtil.CryptographyUtil;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
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

public class ActvConsumerNbrInputRecheck extends Activity implements OnClickListener{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumernbrinput);
        ((TextView)findViewById(R.id.tvConsnuminputTitle)).setText("Re-checking request");
        UtilDB util = new UtilDB(getBaseContext());
		UtilAppCommon.sdoCode= util.getSdocd();				
		util.close();
		
		UtilDB util2 = new UtilDB(getBaseContext());
		UtilAppCommon.binder= util2.getActiveBinder();		
		util2.close();
		
		((TextView) findViewById(R.id.subDivisionTxt)).setText(UtilAppCommon.sdoCode);				
		((TextView) findViewById(R.id.binderTxt)).setText(UtilAppCommon.binder);
		
        
        
       Button submitBtn = (Button) findViewById(R.id.btnGenerate);
       submitBtn.setText("Request");
        submitBtn.setOnClickListener(this);
        
    }
 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
	     // do something on back.
		 startActivity(new Intent(this, ActvBillingOption.class));  
		 finish();
	     return;
	 }
	public void onClick(View v) {
		TextView errTxt = (TextView)findViewById(R.id.nullBinderSubdivErrLbl);
		switch(v.getId()){
		case R.id.btnGenerate:
	
			String EncData="";
			String ActualData="";
			String CheckFieldData="";
			//To Assign User info to the struct variable
			
			UtilDB util = new UtilDB(getBaseContext());		
			util.getUserInfo();
			//End
			
			UtilAppCommon.acctNbr = ((EditText) findViewById(R.id.AcctNbrEdit)).getText().toString();
			System.out.println("UtilAppCommon.acctNbr:::::"+UtilAppCommon.acctNbr);
			/*
			String EncData= CryptographyUtil.Encrypt(util.GetConsumerInfoByField( UtilAppCommon.sdoCode, UtilAppCommon.binder,UtilAppCommon.acctNbr, "name"));
			
			String ActualData=CryptographyUtil.Decrypt(EncData);
			
			String CheckFieldData=util.GetConsumerInfoByField( UtilAppCommon.sdoCode, UtilAppCommon.binder,UtilAppCommon.acctNbr, "name");*/
			// Modified  by Kishore
      /* if(!UtilAppCommon.acctNbr.isEmpty())
          {
				 EncData= CryptographyUtil.Encrypt(util.GetConsumerInfoByField( UtilAppCommon.sdoCode, UtilAppCommon.binder,UtilAppCommon.acctNbr, "name"));
				
				 ActualData=CryptographyUtil.Decrypt(EncData);
				
				 CheckFieldData=util.GetConsumerInfoByField( UtilAppCommon.sdoCode, UtilAppCommon.binder,UtilAppCommon.acctNbr, "name");
				// Toast.makeText(getBaseContext(), "Security key :"+ActualData+ "\n CheckFieldData: "+CheckFieldData, Toast.LENGTH_LONG).show();
           }*/
			 
			
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
				
				if(NetworkUtil.isOnline(ActvConsumerNbrInputRecheck.this,null))
				{
					final String[] remarks={"Wrong Punching",
										"Meter Jumping",
										"Cancel"};
					AlertDialog.Builder builder = new AlertDialog.Builder(ActvConsumerNbrInputRecheck.this);
					builder.setTitle("Select Remark");
					builder.setItems(remarks, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, final int item) {
							
							if(item!=2)
							{
								
								final Dialog inputdialog = new Dialog(ActvConsumerNbrInputRecheck.this);
					            inputdialog.setContentView(R.layout.form_inputtext_dialog);
					            inputdialog.setTitle("Enter actual reading:");
					            inputdialog.setCancelable(true);

					            (inputdialog.findViewById(R.id.btnDialogOK)).setOnClickListener(new OnClickListener() {
					            @Override
					                public void onClick(View v) {
					            	if(((EditText)inputdialog.findViewById(R.id.etDialogText)).getText().toString().length()>0)
									  {
										  //AsyncRecheckReq asyncrecheckobj=new AsyncRecheckReq(ActvConsumerNbrInputRecheck.this, UtilAppCommon.ui.COMPANY_ID, UtilAppCommon.in.CONTRACT_AC_NO, UtilAppCommon.in.BILL_MTH,Integer.parseInt(((EditText)inputdialog.findViewById(R.id.etDialogText)).getText().toString()), remarks[item]);
										  //asyncrecheckobj.execute();
										  Toast.makeText(ActvConsumerNbrInputRecheck.this, "Sending request", Toast.LENGTH_LONG).show();
										  inputdialog.dismiss();
									  }else
									  {
										  Toast.makeText(ActvConsumerNbrInputRecheck.this, "Invalid reading", Toast.LENGTH_LONG).show();
									  }
					                }
					            }); 
					            (inputdialog.findViewById(R.id.btnDialogCancel)).setOnClickListener(new OnClickListener() {
					                @Override
					                    public void onClick(View v) {
					                      
					                          inputdialog.dismiss();
					                    }
					                }); 
					            inputdialog.show();
								
							}
							else
							{
								dialog.dismiss();
							}
							
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
				else
				{
					AlertDialog.Builder altDialog = new AlertDialog.Builder(ActvConsumerNbrInputRecheck.this);
					altDialog.setTitle("Mobile data is off !");
					altDialog.setMessage("Please turn on internet ."); // here																				// add
					altDialog.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
					
					altDialog.show();
				}
				break;					
			}			
			break;
		}
		
	}
	
}
