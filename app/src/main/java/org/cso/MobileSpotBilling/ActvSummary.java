package org.cso.MobileSpotBilling;

import java.util.ArrayList;

import java.util.HashMap;

import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;  

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ActvSummary extends Activity{
	 TextView tvSummaryNOR;
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.summary);
	        
	        UtilDB util = new UtilDB(getBaseContext());
	        tvSummaryNOR=(TextView)findViewById(R.id.tvSummaryNOR);
			ListView list = (ListView) findViewById(R.id.lvSummaryList);
	        //ArrayList<HashMap<String, String>> mylist =util.getSummary();
			ArrayList<HashMap<String, String>> mylist =util.getSummaryReport1();
	        util.close();
	        //SimpleAdapter adapter = new InteractiveArrayAdapter(this,R.layout.summary_listitems, mylist,
	        //		new String[] {"MRU","TOTAL", "BILLED","UNBILLED","POSTED","PENDING"},
	        SimpleAdapter adapter = new InteractiveArrayAdapter(this,R.layout.summary_listitems, mylist,
	    	        		new String[] {"Category","TotalCons", "Billed","Units","BilledAmt","Pending"},
	    	        		new int[] {R.id.tvsummaryBinder,R.id.tvsummaryTotal, R.id.tvsummaryBilled, R.id.tvsummaryUnbilled,R.id.tvsummaryPosted,R.id.tvsummaryPending});
	        		//new int[] {R.id.tvsummaryBinder,R.id.tvsummaryTotal, R.id.tvsummaryBilled, R.id.tvsummaryUnbilled,R.id.tvsummaryPosted,R.id.tvsummaryPending});
	       

	        LayoutInflater inflater = getLayoutInflater();   
	        tvSummaryNOR.setText("No. Of Record Found:"+mylist.size());
	        final View header = inflater.inflate(R.layout.summarylist_header, (ViewGroup) (list),false);
	        list.addHeaderView(header, null, false);
	        list.setAdapter(adapter);
	    }
	    @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
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
	     // do something on back.
		 startActivity(new Intent(this, ActvivityMain.class));
		 finish();
	 }
	
}
