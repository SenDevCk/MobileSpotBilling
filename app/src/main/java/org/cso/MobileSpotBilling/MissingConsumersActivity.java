package org.cso.MobileSpotBilling;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.cso.MSBAsync.AsyncMissingCons;
import org.cso.MSBAsync.AsyncUpdatePoleMobile;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class MissingConsumersActivity extends AppCompatActivity implements OnClickListener, TaskCallback {

	Button submitBtn;
	Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_missing_consumers);
		//toolbar
		toolbar = findViewById(R.id.toolbar_missing_consumer);
		toolbar.setTitle("Update Consumer Details");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

        submitBtn = (Button) findViewById(R.id.ContinueBtn);
        submitBtn.setOnClickListener(this);
        
        Button cancelBtn = (Button) findViewById(R.id.CancelBtn);
        cancelBtn.setOnClickListener(this);
	}

	@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.missing_consumers, menu);
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
		} *//*else if (id == R.id.home) {
			finish();
			return true;
		}*//*
		return true;
	}*/

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.ContinueBtn:
			
			submitBtn.setEnabled(false);
			//Toast.makeText(getApplicationContext(),"This functionality is under development!", Toast.LENGTH_LONG).show();
			
			//UtilAppCommon.acctNbr = ((EditText) findViewById(R.id.MobileNoTxt)).getText().toString();
			
			try {
				Boolean blflag = false;
				String[] strInput = new String[9];
				String strCANbr = ((EditText) findViewById(R.id.AccNoTxt)).getText().toString().trim();
				String strLegNbr = ((EditText) findViewById(R.id.LegNoTxt)).getText().toString().trim();
				String strConName = ((EditText) findViewById(R.id.ConNameTxt)).getText().toString();
				String strPoleNo = ((EditText) findViewById(R.id.PoleNoTxt)).getText().toString();
				String strMobile = ((EditText) findViewById(R.id.MobileNoTxt)).getText().toString().trim();
				
				//Log.e("Mobile", strMobile.length() + "");
				//Log.e("Pole", strPoleNo + "");
				if(strCANbr.trim().equalsIgnoreCase("") && strLegNbr.trim().equalsIgnoreCase(""))
				{
					Toast.makeText(getBaseContext(),"Please enter CA number or Legacy number.", Toast.LENGTH_LONG).show();
					return;
				}
				else if(strCANbr.length() != 9 && !strCANbr.trim().equalsIgnoreCase(""))
				{
					Toast.makeText(getBaseContext(),"Please enter a valid 9 digit account number.", Toast.LENGTH_LONG).show();
					return;
				}
				else if(!strMobile.trim().equalsIgnoreCase("") && (!isNumeric(strMobile) || strMobile.length() != 10))
				{
					Toast.makeText(getBaseContext(),"Please enter valid 10 digit mobile number.", Toast.LENGTH_LONG).show();
					return;
				}
				else if(strPoleNo.trim().equalsIgnoreCase(""))
				{
					Toast.makeText(getBaseContext(),"Please enter pole number.", Toast.LENGTH_LONG).show();
					return;
				}
				else
				{
					if(strCANbr.trim().equals(""))
						strCANbr = "";
					if(strLegNbr.trim().equals(""))
						strLegNbr = "";
					
					//UtilAppCommon.acctNbr = strCANbr;
					//UtilAppCommon.legNbr = strLegNbr;
					UtilDB util = new UtilDB(getApplicationContext());
					//if(UtilAppCommon.inSAPMsgID.contentEquals("6"))
						//util.UpdateConsumerDetails(UtilAppCommon.acctNbr, strMobile, strPoleNo);
					String nxtDate =util.getSchMtrRdgDate(); // "2015.10.31";
					
					Log.e("Miss Cons nxtDate", nxtDate);	
					
					int temp = Integer.parseInt(nxtDate.substring(5, 7));
					String strtemp = "";
					if(temp <= 9)
						strtemp = "0" + Integer.parseInt(nxtDate.substring(5, 7));
					else
						strtemp = ""  + Integer.parseInt(nxtDate.substring(5, 7));
					
					String strtemp1 = "";
					int temp1 = Integer.parseInt(nxtDate.substring(8, 10));
					if(temp1 <= 9)
						strtemp1 = "0" + Integer.parseInt(nxtDate.substring(8, 10));
					else
						strtemp1 = ""  + Integer.parseInt(nxtDate.substring(8, 10));
					
					nxtDate = strtemp1 + "." + strtemp
							+ "." + nxtDate.substring(0, 4);
					
					Log.e("Miss Cons Formatted nxtDate", nxtDate);
					strInput[0] = strCANbr;
					strInput[1] = strLegNbr;
					strInput[2] = nxtDate;
					strInput[3] = strConName;
					strInput[4] = strPoleNo;
					strInput[5] = strMobile;
					strInput[6] = util.getActiveMRU();
					strInput[7] = util.getSdoCode();
					strInput[8] = "1";
					util.InsertConsumerDetails(strInput);
					Log.e("Miss Cons Formatted Insert", "Completed");
					
					AsyncMissingCons asyncUpdateMissCons = new AsyncMissingCons(this);
					asyncUpdateMissCons.execute(strInput);
					
					}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				Log.e("Continud - Miss NFE", e.getMessage());
				e.printStackTrace();
			}
			 catch (Exception e) {
					// TODO Auto-generated catch block
				 Log.e("Continud - Miss NFE", e.getMessage());
					e.printStackTrace();
				}
			   ((EditText) findViewById(R.id.AccNoTxt)).setText("");
				((EditText) findViewById(R.id.LegNoTxt)).setText("");
				((EditText) findViewById(R.id.ConNameTxt)).setText("");
				((EditText) findViewById(R.id.MobileNoTxt)).setText("");
				((EditText) findViewById(R.id.PoleNoTxt)).setText("");
				
			break;
			
		case R.id.CancelBtn:
			finish();
			startActivity(new Intent(this, ActvivityMain.class));
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
		startActivity(new Intent(this, ActvivityMain.class));
		
	}
	
	 public void onBackPressed() {
		 // do something on back.
		 //startActivity(new Intent(this, ActvivityMain.class));
		 //finish();
		 super.onBackPressed();
	 }
	

}
