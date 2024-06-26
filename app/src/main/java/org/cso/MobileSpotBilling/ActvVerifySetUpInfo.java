package org.cso.MobileSpotBilling;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ActvVerifySetUpInfo extends AppCompatActivity implements OnClickListener{
    /** Called when the activity is first created. */
	UtilDB  util;
	EditText username,password;	
		Toolbar toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dg_verifysetupinfo);
		toolbar = findViewById(R.id.toolbar_versetinfo);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle("Setup information");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
        showVerifySetupInfo();       
    }

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("onResume", "ActvVerifySetUpInfo");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e("onRestart", "ActvVerifySetUpInfo");
	}
	@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}
    
    public void onBackPressed() {
		//startActivity(new Intent(this, ActvSetupInfo.class));
		super.onBackPressed();
		//finish();
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