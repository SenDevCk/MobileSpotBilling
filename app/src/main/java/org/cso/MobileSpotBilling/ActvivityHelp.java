package org.cso.MobileSpotBilling;


import java.io.File;


import java.io.IOException;

import org.cso.MSBUtil.AppUtil;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;

import com.itextpdf.text.Utilities;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
//import android.service.textservice.SpellCheckerService.Session;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
//import android.view.textservice.SuggestionsInfo;
//import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; 
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ActvivityHelp extends AppCompatActivity {
    /** Called when the activity is first created. */
	
	static String strResponse; 
	TextView tvHelpUPDataCnt,tvHelpImei,tvHelpVersion,tvHelpDataNetwork;
	ImageView ivHelpIntConSts,ivHelpSrvConSts;
	Button btnHelpOK,btnHelpRefresh,btnReprint,btnHelpSync;
	Toolbar toolbar;
	
    @Override
    public void onCreate(Bundle savedInstanceState) { 
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_help);

		toolbar= findViewById(R.id.toolbar_help);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle("Help");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnHelpOK=(Button)findViewById(R.id.btnHelpOK);
        btnHelpRefresh=(Button)findViewById(R.id.btnHelpRefresh);
        btnReprint=(Button)findViewById(R.id.btnHelpReprint);
        btnHelpSync=(Button)findViewById(R.id.btnHelpSync);
        
        //UnpostedDataCount();
        SetInternetStatus();
        SetServerStatus();
        SetIMEI_Version();
        SetDataNetwork();
       
        final Context c=this;
        btnHelpOK.setOnClickListener(v -> {
			//startActivity(new Intent(getApplicationContext(), ActvivityMain.class));
			finish();
		});
        btnHelpRefresh.setOnClickListener(v -> {
			 //UnpostedDataCount();
			 SetInternetStatus();
			 SetServerStatus();
			 SetIMEI_Version();
			 SetDataNetwork();
		});
        btnReprint.setOnClickListener(v -> {
			UtilDB utilObj=new UtilDB(getApplicationContext());
			utilObj.getDataForOutFileGen(getApplicationContext());
		});

	 }


	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

    void UnpostedDataCount()
    {
    	UtilDB util=new UtilDB(getBaseContext());
    	tvHelpUPDataCnt=(TextView)findViewById(R.id.tvHelpUPDataCnt);
    	tvHelpUPDataCnt.setText(util.getUpostedDataCount());
    }
    void SetInternetStatus()
    {
    	ivHelpIntConSts=(ImageView) findViewById(R.id.ivHelpIntConSts);
		boolean status=NetworkUtil.isOnline(getApplicationContext(),null);
		if(status)
		{
			ivHelpIntConSts.setImageResource(R.drawable.img_tick);
		}
		else {
			ivHelpIntConSts.setImageResource(R.drawable.img_cross);
		}
    	//Toast.makeText(getApplicationContext(), String.valueOf(status),Toast.LENGTH_SHORT).show();
    }
	@SuppressLint("NewApi")
	void SetServerStatus()
	{
		 AsyncCheckServer task=new AsyncCheckServer();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			task.execute();
		}
	}
   
    void SetIMEI_Version()
    {
    	
    	tvHelpDataNetwork=(TextView)findViewById(R.id.tvHelpDataNetwork);
    	tvHelpImei=(TextView)findViewById(R.id.tvHelpImei);
    	tvHelpImei.setText(UtilAppCommon.IMEI_Number);
    	tvHelpVersion=(TextView)findViewById(R.id.tvHelpVersion);
    	tvHelpVersion.setText(AppUtil.GetVersion(ActvivityHelp.this)); 	    	
    }
    
    void SetDataNetwork()
    {
    	TelephonyManager telephonyManager;
    	String strNetwork = "";
    	telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    	int networkType = telephonyManager.getNetworkType();
    	Log.e("Network Type", " ==>> " + networkType);
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            	strNetwork = "2G";
            	break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            	strNetwork = "3G";
            	break;
            case TelephonyManager.NETWORK_TYPE_LTE:
            	strNetwork = "4G";
            	break;
            default:
            	strNetwork = "No Data Network";
        }
        if(NetworkUtil.isOnline(getApplicationContext(),null))
        	tvHelpDataNetwork.setText(strNetwork);
        else
        	tvHelpDataNetwork.setText("No Data Network");
    }
    
    

    
    private class AsyncCheckServer extends AsyncTask<Void, Void, Void> {
    	
    	boolean status=false;
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            btnHelpSync=(Button)findViewById(R.id.btnHelpSync);
            ivHelpSrvConSts=(ImageView)findViewById(R.id.ivHelpSrvConSts);
            ivHelpSrvConSts.setImageResource(R.drawable.img_wait);
            btnHelpSync.setEnabled(false);
        }
 
        @SuppressLint("SuspiciousIndentation")
		@Override
        protected Void doInBackground(Void... arg0) {
        	//status=NetworkUtil.isConnectedToServer("http://220.225.3.133/BiharSBMService/MobiletoMW.asmx", 10000);	//Devp
        	status=NetworkUtil.isConnectedToServer("http://220.225.3.149/BiharSBMService/MobiletoMW.asmx", 5000);		//Prod
        	if(!status)
        		status=NetworkUtil.isConnectedToServer("http://125.16.220.4/BiharSBMService/MobiletoMW.asmx", 30000);		//Prod
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if(status)
    		{
    			ivHelpSrvConSts.setImageResource(R.drawable.img_tick);
    			btnHelpSync.setEnabled(true);
    		}
    		else {
    			ivHelpSrvConSts.setImageResource(R.drawable.img_cross);
    			btnHelpSync.setEnabled(false);
    		}
        }
    }
    
    @Override
    public void onBackPressed() {
		super.onBackPressed();
	}
}

