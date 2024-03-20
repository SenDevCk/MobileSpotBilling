package org.cso.MobileSpotBilling;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.cso.MSBUtil.UtilDB;

import java.util.ArrayList;
import java.util.HashMap;


public class ActvivityUpload extends AppCompatActivity {
    /** Called when the activity is first created. */
	
	static String strResponse; 
	ListView lvUpload;
	ImageView ivHelpIntConSts,ivHelpSrvConSts;
	Button btnHelpOK,btnHelpRefresh,btnHelpBackUP;
	ArrayList<HashMap<String, String>> mylist;
	SimpleAdapter adapter;
	Toast toast;
	Toolbar toolbar;
	
    @Override
    public void onCreate(Bundle savedInstanceState) { 
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload);
		toolbar= findViewById(R.id.toolbar_upload);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle("Upload Billed Data");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
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
	public boolean onSupportNavigateUp() {
		onBackPressed();
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

