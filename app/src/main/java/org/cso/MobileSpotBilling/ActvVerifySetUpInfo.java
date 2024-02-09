package org.cso.MobileSpotBilling;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class ActvVerifySetUpInfo extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	UtilDB  util;
	EditText username,password;	
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dg_verifysetupinfo);        
        showVerifySetupInfo();       
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
		 //startActivity(new Intent(this, ActvSetupInfo.class)); 
		 finish();
	     return;
	 }   
       
	public void onClick(View view) {		
			startActivity(new Intent(this, ActvSetupInfo.class));
			finish();
		}
	
	private void showVerifySetupInfo() {
		setContentView(R.layout.dg_verifysetupinfo);
		View v = findViewById(R.id.VerifySetupInfoLayout);
		v.setVisibility(View.VISIBLE);

		Button okbtn = (Button) findViewById(R.id.btnOk);
		okbtn.setOnClickListener(this);

		UtilDB util = new UtilDB(getBaseContext());
		util.getUserInfo();
		
		String luserId=UtilAppCommon.ui.IMIE_NO;
		String limie=UtilAppCommon.ui.IMIE_NO;
				
		//((TextView) findViewById(R.id.txtVwSdoCd)).setText(lsdoCd);
		//((TextView) findViewById(R.id.txtVwBinder)).setText(lbinder);
		((TextView) findViewById(R.id.txtVwImieNbr)).setText(limie);
		((TextView) findViewById(R.id.txtVwUserId)).setText(luserId);
		//((TextView) findViewById(R.id.txtVwActiveBinder)).setText(lActiveBinder);
		//((TextView) findViewById(R.id.txtVwActiveUser)).setText(lActiveUser);
		//((TextView) findViewById(R.id.txtVwBillMth)).setText(lBillMth);
		
		}
	
}