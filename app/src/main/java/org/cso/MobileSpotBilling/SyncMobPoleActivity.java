package org.cso.MobileSpotBilling;

import org.cso.MSBAsync.AsyncUpdatePoleMobile;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SyncMobPoleActivity extends AppCompatActivity implements TaskCallback {

	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync_mob_pole);
		toolbar = findViewById(R.id.toolbar_Syncmobpole);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle("Data");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		performOperation();
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("onResume", "SyncMobPoleActivity");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e("onRestart", "SyncMobPoleActivity");
	}
	@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}


	@SuppressWarnings("null")
	public void performOperation()
	{
		UtilDB util = new UtilDB(getBaseContext());
		Cursor curs = null;
		String nxtDate = util.getSchMtrRdgDate();
		curs = util.getUnSyncedMobPol();
		if(curs != null) {
			String[] copySAPInputData = new String[4];
			copySAPInputData[0] = curs.getString(1);
			int temp = Integer.parseInt(nxtDate.substring(5, 7));
			String strtemp = "";
			if (temp <= 9)
				strtemp = "0" + Integer.parseInt(nxtDate.substring(5, 7));
			else
				strtemp = "" + Integer.parseInt(nxtDate.substring(5, 7));
			String strtemp1 = "";

			int temp1 = Integer.parseInt(nxtDate.substring(8, 10));
			if (temp1 <= 9)
				strtemp1 = "0" + Integer.parseInt(nxtDate.substring(8, 10));
			else
				strtemp1 = "" + Integer.parseInt(nxtDate.substring(8, 10));

			nxtDate = strtemp1 + "." + strtemp
					+ "." + Integer.parseInt(nxtDate.substring(0, 4));
			copySAPInputData[1] = nxtDate;
			copySAPInputData[2] = curs.getString(13);
			copySAPInputData[3] = curs.getString(14);

			AsyncUpdatePoleMobile asyncUpdatePoleMobile = new AsyncUpdatePoleMobile(this);
			asyncUpdatePoleMobile.execute(copySAPInputData);
		}
	}
	
	@Override
	public void done() {
		// TODO Auto-generated method stub
		finish();
		UtilAppCommon.blActyncBtn = false;
		startActivity(new Intent(this, SeqSyncActivity.class));
	}
}
