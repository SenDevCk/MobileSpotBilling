package org.cso.MobileSpotBilling;


import org.cso.MobileSpotBilling.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ActvRemarks extends Activity implements OnItemClickListener{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remarks);
		setTitle("Remarks");
		ListView remarksListView = (ListView) findViewById(R.id.RemarksList);
        ClsListData remarks[] = new ClsListData[13];
             
        
        remarks[0] =  new ClsListData("Load To Be Enhanced", "A");
        remarks[1] =  new ClsListData("Malpractice", "B");
        remarks[2] =  new ClsListData("Category To beChanged", "C");
        remarks[3] =  new ClsListData("Govt. Consumer", "D");
        remarks[4] =  new ClsListData("DISCTD and Availing PWR", "E");
        remarks[5] =  new ClsListData("suspected Bypass", "F");
        remarks[6] =  new ClsListData("Meter Changed at Site", "G");
        remarks[7] =  new ClsListData("Meter Beyond EyeSight", "H");
        remarks[8] =  new ClsListData("Meter Running & Sts Def.", "I");
        remarks[9] =  new ClsListData("Meter No Mismatch", "J");
        remarks[10] =  new ClsListData("Consumer Not Found", "K");        
        remarks[11] =  new ClsListData("Others", "L");
        remarks[12] =  new ClsListData("No Remarks", " ");
        
        ArrayAdapter<ClsListData> listAdapter = new ArrayAdapter<ClsListData>(this, android.R.layout.simple_list_item_1, remarks);
        remarksListView.setAdapter(listAdapter);
        remarksListView.setOnItemClickListener(this);
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
		// startActivity(new Intent(this, ActvCurrReading.class));  
	     
	     return;
	 }
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		ClsListData data = (ClsListData) parent.getItemAtPosition(position);
		String value =  data.getValue();
		String item = data.getDisplay();
		Intent intent = getIntent();
		intent.putExtra("remarks", item);
		intent.putExtra("remarksId", value);
		setResult(RESULT_OK, intent);
		finish();
		
	}
}

