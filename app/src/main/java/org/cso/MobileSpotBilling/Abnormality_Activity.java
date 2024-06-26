package org.cso.MobileSpotBilling;


import org.cso.MSBAsync.AsyncAbnormality;
import org.cso.MSBAsync.AsyncMissingCons;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Abnormality_Activity extends AppCompatActivity implements OnClickListener, TaskCallback {

	Spinner spAbnormality = null;
	Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_abnormality);

		toolbar = findViewById(R.id.toolbar_abnormality);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle("Abnormality Details");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		Button submitBtn = (Button) findViewById(R.id.ContinueBtn);
        submitBtn.setOnClickListener(this);
        
        Button cancelBtn = (Button) findViewById(R.id.CancelBtn);
        cancelBtn.setOnClickListener(this);
        
        spAbnormality = (Spinner) findViewById(R.id.AbnormalitySpn);

		if(UtilAppCommon.blAbnormalityCheck)
		{
			((EditText) findViewById(R.id.AccNoTxt)).setText(UtilAppCommon.in.CONTRACT_AC_NO);
			((EditText) findViewById(R.id.AccNoTxt)).setEnabled(false);
			UtilAppCommon.blAbnormalityCheck = false;
		}
        //spAbnormality.setOnItemSelectedListener(this);
	}
	@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}


	@Override
	public void done() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//return;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
		case R.id.ContinueBtn:
			
			UtilDB util = new UtilDB(getApplicationContext());
			String strCANbr = ((EditText) findViewById(R.id.AccNoTxt)).getText().toString().trim();
			String strRemarks = ((EditText) findViewById(R.id.RemarksTxt)).getText().toString().trim();
			String strAbnormality = (String) spAbnormality.getSelectedItem().toString();
			int intAbnormality = spAbnormality.getSelectedItemPosition();

	    	   ((EditText) findViewById(R.id.AccNoTxt)).setText("");
	    	   ((EditText) findViewById(R.id.RemarksTxt)).setText("");   
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
			
			String strMru = util.getActiveMRU();
			String strSDO = util.getSdoCode();
			if(strCANbr.length() != 9)
			{
				Toast.makeText(getBaseContext(),"Please enter a valid 9 digit account number.", Toast.LENGTH_LONG).show();
				//return;
			}
			/*if(util.checkCANumberAbnormality(strCANbr))
			{
				Log.e("checkCANumber", "Valid");
				Toast.makeText(getBaseContext(),"Account number already entered.", Toast.LENGTH_LONG).show();
				//return;
			}*/
			else if(util.checkCANumber(strCANbr))
			{
				Log.e("checkCANumber", "Valid");
				UtilAppCommon.acctNbr = strCANbr;
				String strInput[] = new String[7];
				strInput[0] = strCANbr;
				strInput[1] = nxtDate;
				strInput[2] = strMru;
				strInput[3] = strSDO;
				strInput[4] = strRemarks;
				strInput[5] = String.valueOf(intAbnormality);
				strInput[6] = "0";
				//Log.e("checkstrinput", strinput1);
				try {
					//if(util.checkCANumberAbnormality(strCANbr)) // 05.05.16
					 if(util.getAbnormalityCount(strCANbr)==0)
					{
					   	util.InsertAbnormalityDetails(strInput);
					   	AsyncAbnormality asyncAbnormality = new AsyncAbnormality(this);
					   	asyncAbnormality.execute(strInput);
						finish();
					}
					else{
						 util.UpdateAbnormalityDetails(strInput[5],strInput[4],strInput[6],strInput[0]);
						 AsyncAbnormality asyncAbnormality = new AsyncAbnormality(this);
						 asyncAbnormality.execute(strInput);
						 finish();
					 }
						//Toast.makeText(getBaseContext(), "Abnormality Already Entered for this consumer", Toast.LENGTH_LONG).show();
					
					//else
					//	util.UpdateAbnormalityDetails(strInput, strAbnormality, strRemarks, strCANbr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("AbnormalityDetails ", "AbnormalityDetails ==>> " + e.getMessage());
				}
	
			}
			else
			{
				Toast.makeText(getBaseContext(), "Account number not present in MRU.", Toast.LENGTH_LONG).show();
				//return;
			}
			finish();
			break;
		
		case R.id.CancelBtn:
			finish();
			//startActivity(new Intent(this, ActvivityMain.class));
			break;
		}		
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("onResume","Abnormality_Activity");
	}
}
