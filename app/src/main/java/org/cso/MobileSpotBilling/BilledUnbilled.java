package org.cso.MobileSpotBilling;

import java.util.ArrayList;

import java.util.HashMap;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;  

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BilledUnbilled extends Activity{
	 TextView tvBilledUnbilledNOR;
	 ArrayList<HashMap<String, String>> mylist ;
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.bub_list);
	        
	        UtilDB util = new UtilDB(getBaseContext());
	        tvBilledUnbilledNOR=(TextView)findViewById(R.id.tvBilledUnbilledNOR);
			ListView list = (ListView) findViewById(R.id.lvBilledUnbilledList);
	        mylist =util.getBuBlistItems();
	        util.close();
	        SimpleAdapter adapter = new InteractiveArrayAdapter(this,R.layout.bub_listitems, mylist,
	        		new String[] {"MRU", "BILLED","UNBILLED","TOTAL"},
	        new int[] {R.id.bubBinder, R.id.bubBilled, R.id.bubUnbilled,R.id.bubTotal});
	        LayoutInflater inflater = getLayoutInflater();   
	        tvBilledUnbilledNOR.setText("No. Of Record Found:"+mylist.size());
	        final View header = inflater.inflate(R.layout.bub_header, (ViewGroup) (list),false);
	        list.addHeaderView(header, null, false);
	        list.setAdapter(adapter);

	        list.setOnItemClickListener(new OnItemClickListener() {
		           
				public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
					Intent intentConsumerListTest=new Intent(BilledUnbilled.this, ConsumerListTest.class);
					HashMap<String,String> map =mylist.get(position-1);
					intentConsumerListTest.putExtra("MRU",map.get("MRU").toString());
					intentConsumerListTest.putExtra("View", "UnbilledList");
					startActivity(intentConsumerListTest);
	         }
	     });
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
		 finish();
		// startActivity(new Intent(this, ActvReport.class));  
	     
	     return;
	 }
}
