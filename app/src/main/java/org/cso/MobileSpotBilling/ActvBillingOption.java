package org.cso.MobileSpotBilling;

import java.io.File;


import java.io.FileOutputStream;
import java.io.IOException;

import org.cso.MSBUtil.GPSLocation;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.UtilDB.MyHelper;
import org.cso.MobileSpotBilling.R;
import org.json.JSONObject;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
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

public class ActvBillingOption extends AppCompatActivity implements OnClickListener{
    /** Called when the activity is first created. */
	SQLiteDatabase db;
	MyHelper helper;
	Toolbar toolbar;
	private BluetoothAdapter mBluetoothAdapter = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
       
        setContentView(R.layout.billingoption);
		toolbar= findViewById(R.id.toolbar_bill_opti);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle("Billing");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button ByAcctNbr = (Button) findViewById(R.id.btnAcctNbr);
        ByAcctNbr.setOnClickListener(this);
        
        Button ByLegacyNbr = (Button) findViewById(R.id.btnLegacyNbr);
        ByLegacyNbr.setOnClickListener(this);
        
        Button ByMeter = (Button) findViewById(R.id.btnMeter);
        ByMeter.setOnClickListener(this);
        
        Button BySequence = (Button) findViewById(R.id.btnSequence);
        BySequence.setOnClickListener(this);
        
        Button ByRouteOrder = (Button) findViewById(R.id.btnRouteOrder);
        ByRouteOrder.setOnClickListener(this);
        
        Button ConsumerSearch = (Button) findViewById(R.id.btnConsumerSearch);
        ConsumerSearch.setOnClickListener(this);
        
        Button PrintDupBill = (Button) findViewById(R.id.btnPrintDupBill);
        PrintDupBill.setOnClickListener(this);
        Button btnChangeBinder = (Button) findViewById(R.id.btnChangeBinder);
        btnChangeBinder.setOnClickListener(this);
        Button btnBillingOptionRecheck = (Button) findViewById(R.id.btnBillingOptionRecheck);
        btnBillingOptionRecheck.setOnClickListener(this);
        
        UtilAppCommon.strRedirectTo = "";
        
      /* Button changeBInder = (Button) findViewById(R.id.btnChangeBinder);
       changeBInder.setOnClickListener(this);*/
        
      //showModifySetup();
        
    }


	@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}
	public void onClick(View v) {
		Intent intent = new Intent(this,ActvBilling.class);
			switch(v.getId()){
		
		case R.id.btnAcctNbr:
			intent.putExtra("btn", "A");
			UtilAppCommon.billType = "A";
			startActivity(new Intent(this, ActvConsumerNbrInput.class));
			//finish();
			break;
			
		case R.id.btnLegacyNbr:
			intent.putExtra("btn", "L");
			UtilAppCommon.billType = "L";
			startActivity(new Intent(this, ActvLegacyNbrInput.class));
			//finish();
			break;
			
		case R.id.btnSequence:
			UtilDB util = new UtilDB(getBaseContext());
			if(util.getUnbilledRouteSequence("") != null)
			{
				intent.putExtra("btn", "S");
				UtilAppCommon.billType = "S";
				startActivity(new Intent(this, ActvSequenceData.class));
				//finish();
			}
			else
				Toast.makeText(getApplicationContext(),"Please check unbilled consumer & bill by CA number!", Toast.LENGTH_LONG).show();
			break;
			
		case R.id.btnMeter:
			intent.putExtra("btn", "M");
			UtilAppCommon.billType = "M";
			startActivity(new Intent(this, MeterNbrInput.class));
			//finish();
			break;
			
		case R.id.btnRouteOrder:
			/*finish();
			intent.putExtra("btn", "W");
			startActivity(new Intent(this, ActvConsumerNbrInput.class));
			*/
			Toast.makeText(getApplicationContext(),"This functionality is under development!", Toast.LENGTH_LONG).show();
			break;
		case R.id.btnConsumerSearch:
			intent.putExtra("btn", "C");
			startActivity(new Intent(this, ActvOptConsumerSearch.class));
			break;
		case R.id.btnPrintDupBill:

			mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

			if (mBluetoothAdapter == null) {
						Toast.makeText(this, "Bluetooth is not available.",Toast.LENGTH_LONG).show();
						return;
					}
					else if (!mBluetoothAdapter.isEnabled()) {
						Toast.makeText(this,
						"Please enable your BlueTooth and execute this program again.",
						Toast.LENGTH_LONG).show();
						return;
					}
					else{
						//intent.putExtra("btn", "A");
						UtilAppCommon.billType = "";
						startActivity(new Intent(this, ActvConsumerNbrInputForDuplicateBill.class));
						//finish();
					}
			break;
			
		case R.id.btnChangeBinder:
			Intent ChangeBinder =new Intent(this, ActvModifySetUpInfo.class);
			ChangeBinder.putExtra("From","Billing");
			startActivity(ChangeBinder);			
			break;	
		case R.id.btnBillingOptionRecheck:
			intent.putExtra("btn", "A");						
			startActivity(new Intent(this, ActvConsumerNbrInputRecheck.class));
			break;
		}
		
	}

	public void onBackPressed() {
		// do something on back.
		//finish();
		//startActivity(new Intent(this, ActvivityMain.class));

		super.onBackPressed();
		return;
	}
	
}
