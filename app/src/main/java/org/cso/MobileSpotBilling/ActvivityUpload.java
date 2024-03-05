package org.cso.MobileSpotBilling;


import java.io.File;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

//import org.cso.MSBAsync.SaveBulkDataAsync;
import org.cso.MSBUtil.AppUtil;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;

import com.itextpdf.text.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
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
import android.view.Gravity;
import android.view.LayoutInflater;
//import android.service.textservice.SpellCheckerService.Session;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
//import android.view.textservice.SuggestionsInfo;
//import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; 
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class ActvivityUpload extends AppCompatActivity {
    /** Called when the activity is first created. */
	
	static String strResponse; 
	ListView lvUpload;
	ImageView ivHelpIntConSts,ivHelpSrvConSts;
	Button btnHelpOK,btnHelpRefresh,btnHelpBackUP;
	ArrayList<HashMap<String, String>> mylist;
	SimpleAdapter adapter;
	Toast toast;
	
    @Override
    public void onCreate(Bundle savedInstanceState) { 
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload); 
        lvUpload=(ListView)findViewById(R.id.lvUpload);
        mylist = new ArrayList<HashMap<String, String>>();
        
       final Context c=this;
       final String Mode=getIntent().getExtras().getString("mode");
       
       if(Mode.compareToIgnoreCase("Binder Wise")==0)
       {
    	  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	   ViewBinderSummary();
       }
       else if(Mode.compareToIgnoreCase("Consumer Wise")==0)
       {
    	   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	   ViewBilledUnpostedCons();
       }
       
       lvUpload.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
		   // TODO Auto-generated method stub
			   if(Mode.compareToIgnoreCase("Binder Wise")==0)
			  {
				   HashMap<String, String> map = new HashMap<String, String>();
				   map = mylist.get(arg2-1);

				   toast = Toast.makeText(getApplicationContext(),
						   map.get("BINDER"), Toast.LENGTH_LONG);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
			   /*	SaveBulkDataAsync asyncsavebulkdata = new SaveBulkDataAsync(
						   ActvivityUpload.this, Mode, map.get("BINDER"));
				   asyncsavebulkdata.execute("");*/
			  }
			  else if(Mode.compareToIgnoreCase("Consumer Wise")==0)
			  {
				  HashMap<String, String> map = new HashMap<String, String>();
				   map = mylist.get(arg2-1);

				   toast = Toast.makeText(getApplicationContext(),
						   map.get("CONS_REF"), Toast.LENGTH_LONG);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
				   /*SaveBulkDataAsync asyncsavebulkdata = new SaveBulkDataAsync(
						   ActvivityUpload.this, Mode, map.get("CONS_REF"));
				   asyncsavebulkdata.execute("");*/
			  }
	   });
       
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
	 		 startActivity(new Intent(this, ActvivityHelp.class));*/
	   		finish();	   	 
	    	 Intent intent = new Intent(this, ActvivityMain.class);  
	    	 startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));  
	         startActivity(intent);
	 		 break;
	   	 }
	     return true;
    }
    

    void ViewBinderSummary() {
    	lvUpload=(ListView)findViewById(R.id.lvUpload);
		UtilDB util = new UtilDB(getBaseContext());
		mylist.clear();
		mylist = util.getSummary();
		util.close();
		adapter = new InteractiveArrayAdapter(this,
				R.layout.upload_billed_listitems, mylist, new String[] {
						"BINDER", "BILLED", "PENDING" }, new int[] {
						R.id.tvUploadBinder, R.id.tvUploadBilled,
						R.id.tvUploadPending });
		LayoutInflater inflater = getLayoutInflater();   
	    View header = inflater.inflate(R.layout.upload_billed_header, (ViewGroup)(lvUpload),false);
	    lvUpload.addHeaderView(header,null, false); 

		lvUpload.setAdapter(adapter);
		
	}

	void ViewBilledUnpostedCons() {
		lvUpload=(ListView)findViewById(R.id.lvUpload);
		UtilDB util = new UtilDB(getBaseContext());
		mylist.clear();
		mylist = util.getBilledUnPostedConsList();
		util.close();
		adapter = new InteractiveArrayAdapter(this, R.layout.newcons_listitems,
				mylist, new String[] { "BINDER", "ACC_NO", "NAME", "ADDR" },
				new int[] { R.id.newConsBinder, R.id.newConsAccNo,
						R.id.newConsName, R.id.newConsAddr });
		LayoutInflater inflater = getLayoutInflater();   
	    View header = inflater.inflate(R.layout.upload_cons_header, (ViewGroup)(lvUpload),false);
	    lvUpload.addHeaderView(header,null, false); 
	    lvUpload.setAdapter(adapter);

	}
}

