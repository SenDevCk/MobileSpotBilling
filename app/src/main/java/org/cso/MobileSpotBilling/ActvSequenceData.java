package org.cso.MobileSpotBilling;


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
import android.widget.TextView;
import android.widget.Toast;

import org.cso.MSBAsync.AsyncGetOutputData;
import org.cso.MSBModel.StructSAPInput;
import org.cso.MSBUtil.CryptographyUtil;
import org.cso.MSBUtil.GPSTracker;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ActvSequenceData extends Activity implements OnClickListener {

	int itmpCounter = 0;
	int iInitCounter = 0;
	int iMaxCounter = 0;
	boolean cancelPressed = false;
	int valuePassed=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sequencedata);

		/*Log.i("Image Capture", "" + UtilAppCommon.blImageCapture);
		if(UtilAppCommon.blImageCapture)	{
			startActivity(new Intent(this, ActvBilling.class));
		}*/

		try {

			UtilDB util = new UtilDB(getBaseContext());	
			UtilAppCommon.sdoCode = util.getSdoCode();
			UtilAppCommon.binder = util.getActiveMRU();		
						
			((TextView) findViewById(R.id.subDivisionTxt)).setText(UtilAppCommon.sdoCode);				
			((TextView) findViewById(R.id.binderTxt)).setText(UtilAppCommon.binder);
			
			//GPSLocation gps = new GPSLocation();
			//Log.e("GPS ==>> ", gps.getLocation(getApplicationContext()));
			
			Button btnPrevious = (Button) findViewById(R.id.btnPrevious);
			Button btnNext = (Button) findViewById(R.id.btnNext);
			Button btnProceed = (Button) findViewById(R.id.btnProceed);
			
			btnPrevious.setOnClickListener(this);
			btnNext.setOnClickListener(this);
			btnProceed.setOnClickListener(this);
			
			UtilAppCommon.billType = "S";
			
			UtilAppCommon.inSAPSendMsg = "";
			UtilAppCommon.inSAPMsgID = "";
			UtilAppCommon.inSAPMsg = "";
			
			UtilAppCommon.routeSeqNo = util.getUnbilledRouteSequence("");
			itmpCounter = Integer.parseInt(UtilAppCommon.routeSeqNo);
			iInitCounter = itmpCounter;
			iMaxCounter = Integer.parseInt(util.getMaxRouteSequence());
			((TextView) findViewById(R.id.AcctNbrText)).setText(UtilAppCommon.acctNbr);
			((TextView) findViewById(R.id.SeqNbrText)).setText(UtilAppCommon.routeSeqNo);
			((TextView) findViewById(R.id.consumerNameTxt)).setText(UtilAppCommon.ConsumerName);
			if(Integer.parseInt(UtilAppCommon.routeSeqNo) <= iInitCounter)
				btnPrevious.setEnabled(false);
			
			if(Integer.parseInt(UtilAppCommon.routeSeqNo) == iMaxCounter)
				((Button)findViewById(R.id.btnNext)).setEnabled(false);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("Sequence Create ", e.getMessage());
		}
        
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.act_demo, menu);
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
    public void onBackPressed() {
	     // do something on back.
		 startActivity(new Intent(this, ActvBillingOption.class));  
		 finish();
	     return;
	 }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String EncData="";
		String ActualData="";
		String CheckFieldData="";
		
		int seqNo;
		
		UtilDB util = new UtilDB(getBaseContext());
		
		switch(v.getId()){
		case R.id.btnPrevious:
			if(UtilAppCommon.routeSeqNo != null)
			{
				seqNo = Integer.parseInt(UtilAppCommon.routeSeqNo);
				itmpCounter = seqNo;
			}
			else
			{
				seqNo = itmpCounter;
			}
			
			do
			{
				seqNo = seqNo - 1;
				//Log.e("Sequence", seqNo + "");
				
				UtilAppCommon.routeSeqNo = util.getUnbilledRouteSequence(String.valueOf(seqNo));
				//Get the Previous Consumer  
				//Get the Proceed to Consumer Processing  
				//To Assign User info to the struct variable
			}
			while(UtilAppCommon.routeSeqNo == null);
			
			((TextView) findViewById(R.id.SeqNbrText)).setText(UtilAppCommon.routeSeqNo);
			((TextView) findViewById(R.id.AcctNbrText)).setText(UtilAppCommon.acctNbr);
			((TextView) findViewById(R.id.consumerNameTxt)).setText(UtilAppCommon.ConsumerName);
			if(Integer.parseInt(UtilAppCommon.routeSeqNo) <= iInitCounter)
				((Button)findViewById(R.id.btnPrevious)).setEnabled(false);
			else
				((Button)findViewById(R.id.btnPrevious)).setEnabled(true);
			
			if(Integer.parseInt(UtilAppCommon.routeSeqNo) == iMaxCounter)
				((Button)findViewById(R.id.btnNext)).setEnabled(false);
			else
				((Button)findViewById(R.id.btnNext)).setEnabled(true);
			break;
		
		case R.id.btnNext:
			UtilDB utilDB = new UtilDB(this);

			utilDB.getBillInputDetails(UtilAppCommon.acctNbr,"CA Number");
			AlertDialog.Builder dialogNext=new AlertDialog.Builder(ActvSequenceData.this);
			dialogNext.setTitle("Select Reason for Non-Billing");
			dialogNext.setItems(R.array.reasonForNextSequence, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					valuePassed=which+3;
					dialog.dismiss();
					secondConfirmation(which+3);
				}
			});
			dialogNext.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancelPressed =true;
					dialog.dismiss();
				}
			});
			dialogNext.create();
			dialogNext.show();

			
			break;
			
		case R.id.btnProceed:
			//Get the Proceed to Consumer Processing  
			//To Assign User info to the struct variable
			
			try {
				UtilAppCommon.inSAPMsgID = "";
				UtilAppCommon.inSAPMsg = "";
				
				util.getUserInfo();
				//End
				
				//UtilAppCommon.legNbr = ((EditText) findViewById(R.id.AcctNbrText)).getText().toString();
				//Log.e("Legacy Number>", ((EditText) findViewById(R.id.AcctNbrText)).getText().toString());
				//System.out.println("UtilAppCommon.acctNbr:::::"+UtilAppCommon.acctNbr);

				if(!UtilAppCommon.acctNbr.isEmpty())
				{
					 EncData= CryptographyUtil.Encrypt(util.GetConsumerInfoByField( UtilAppCommon.acctNbr, "Accno", "CONSUMER_NAME"));

					ActualData=CryptographyUtil.Decrypt(EncData);

					CheckFieldData=util.GetConsumerInfoByField( UtilAppCommon.acctNbr, "Accno", "CONSUMER_NAME");
					// Toast.makeText(getBaseContext(), "Security key :"+ActualData+ "\n CheckFieldData: "+CheckFieldData, Toast.LENGTH_LONG).show();
				}	
							
				if(UtilAppCommon.acctNbr.isEmpty())
				{
						Toast.makeText(getBaseContext(), "Please enter value for legacy number field", Toast.LENGTH_LONG).show();
				}	
				else if(!util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number"))
				{
					Toast.makeText(getBaseContext(), "Selected CA Number is not valid", Toast.LENGTH_LONG).show();
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
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Seq Proceed E", e.getMessage());
			}
			
			break;
		}
			
	}

	public void secondConfirmation(final  int number){
		AlertDialog.Builder dialogSecond=new AlertDialog.Builder(ActvSequenceData.this);
		dialogSecond.setMessage("Are you sure you want to skip this consumer?");
		dialogSecond.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				processedValueForNext(number);
			}
		});
		dialogSecond.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogSecond.create();
		dialogSecond.show();
	}

	public void shouldCallNextConsumer(){

		UtilDB util = new UtilDB(this);
		int seqNo;
		if(UtilAppCommon.routeSeqNo != null)
		{
			seqNo = Integer.parseInt(UtilAppCommon.routeSeqNo);
			itmpCounter = seqNo;
		}
		else
		{
			seqNo = itmpCounter;
		}
		if(!cancelPressed) {
			do {
				seqNo = seqNo + 1;
				//Log.e("Sequence", seqNo + "");

				UtilAppCommon.routeSeqNo = util.getUnbilledRouteSequence(String.valueOf(seqNo));
				//Get the Previous Consumer
				//Get the Proceed to Consumer Processing
				//To Assign User info to the struct variable
			}
			while (UtilAppCommon.routeSeqNo == null);
		}

		((TextView) findViewById(R.id.SeqNbrText)).setText(UtilAppCommon.routeSeqNo);
		((TextView) findViewById(R.id.AcctNbrText)).setText(UtilAppCommon.acctNbr);
		((TextView) findViewById(R.id.consumerNameTxt)).setText(UtilAppCommon.ConsumerName);
		if(Integer.parseInt(UtilAppCommon.routeSeqNo) <= iInitCounter)
			((Button)findViewById(R.id.btnPrevious)).setEnabled(false);
		else
			((Button)findViewById(R.id.btnPrevious)).setEnabled(true);

		if(Integer.parseInt(UtilAppCommon.routeSeqNo) == iMaxCounter)
			((Button)findViewById(R.id.btnNext)).setEnabled(false);
		else
			((Button)findViewById(R.id.btnNext)).setEnabled(true);

	}


	public void processedValueForNext(int value){
		UtilDB utilDB = new UtilDB(getApplicationContext());

		String strlocation = showSettingsAlert();
		String[] copySAPInputData = new String[14];
		String nxtDate;
		try
		{
			copySAPInputData[0] = UtilAppCommon.in.CONTRACT_AC_NO;
			copySAPInputData[1] = UtilAppCommon.in.INSTALLATION;
			copySAPInputData[2] = strlocation.split("¥")[0];
			copySAPInputData[3] = strlocation.split("¥")[1];
			copySAPInputData[4] = UtilAppCommon.in.PRV_READING_KWH;
			copySAPInputData[5] = "0";
			copySAPInputData[6] = "0.00";
			copySAPInputData[7] = UtilAppCommon.in.PRV_MTR_READING_NOTE;
			copySAPInputData[8] = "";
			copySAPInputData[9] = UtilAppCommon.in.SCHEDULED_BILLING_DATE;
			copySAPInputData[10] = UtilAppCommon.in.SAP_DEVICE_NO;

			Calendar today = Calendar.getInstance();
			SimpleDateFormat SAPInformat = new SimpleDateFormat("dd.MM.yyyy");
			copySAPInputData[8] = SAPInformat.format(today.getTime()) ;

			Calendar nxtcal = new GregorianCalendar(
					Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4)),
					Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7)),
					Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10)) - 1);

			int temp = Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7));
			String strtemp = "";
			String strtemp1 = "";
			if(temp <= 9)
				strtemp = "0" + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7));
			else
				strtemp = ""  + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7));

			int temp1 = Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
			if(temp1 <= 9)
				strtemp1 = "0" + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
			else
				strtemp1 = ""  + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));

			nxtDate = strtemp1 + "." + strtemp
					+ "." + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4));
			copySAPInputData[9] = nxtDate;

			//if(cursor.getString(cursor.getColumnIndex("MESSAGE10")).contains("NETWORK ISSUE"))
			copySAPInputData[11] = Integer.toString(value);
			UtilAppCommon.inSAPSendMsg = "1";
			//else
			//copySAPInputData[11] = "2";
			copySAPInputData[12] = "0";
			copySAPInputData[13] = "0";

			Log.e("SCHEDULED_BILLING_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e("MD Case", e1.getMessage());
		}

		try {

			utilDB.insertIntoSAPInput(copySAPInputData);
			//utilDB.UpdateIntoSAPInput(copySAPInputData);
			UtilAppCommon.SAPIn = new StructSAPInput();
			UtilAppCommon.copySAPInputData(copySAPInputData);

			shouldCallNextConsumer();
			//if(NetworkUtil.isOnline(ActvBilling.this,null))
			{
				//AsyncGetOutputData asyncGetOutputData = new AsyncGetOutputData(this); 20.11.15
				AsyncGetOutputData asyncGetOutputData = new AsyncGetOutputData(this ,new OnBillGenerate() {

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						//printdlg();

					}
				});
				asyncGetOutputData.execute(copySAPInputData);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("AsyncOut ActBill", e.getMessage());
		}

	}
	public String showSettingsAlert(){
		GPSTracker gps  = new GPSTracker(ActvSequenceData.this);

		if(gps.canGetLocation()){

			String latitude = String.valueOf(gps.getLatitude());
			String longitude = String.valueOf(gps.getLongitude());

			// \n is for new line
			//Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
			return String.valueOf(gps.getLatitude()) + "¥" + String.valueOf(gps.getLongitude());
		}else{
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();

			String latitude = String.valueOf(gps.getLatitude());
			String longitude = String.valueOf(gps.getLongitude());
			return String.valueOf(gps.getLatitude()) + "¥" + String.valueOf(gps.getLongitude());
		}
	}
}
