package org.cso.MobileSpotBilling;

import java.util.ArrayList;
import java.util.HashMap;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class ActvSrcByConName extends Activity {
    /** Called when the activity is first created. */
	UtilDB  util;
	EditText edtTxtConName;	
	Button btnSearchConsName;
	ListView list ;
	 View header;
	SimpleAdapter adapter;
	ArrayList<HashMap<String, String>> mylist=new ArrayList<HashMap<String,String>>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dg_searchbyconsumername);
        Intent intent = getIntent();
		String bellowMonth = "1";
		System.out.println("bellow mnth is ::::::   "+bellowMonth);
		list = (ListView) findViewById(R.id.lvSrcByConName);
		//tvConsumerListTestNOR=(TextView)findViewById(R.id.tvConsumerListTestNOR);
		LayoutInflater inflater = getLayoutInflater();  
        header = inflater.inflate(R.layout.newcons_header, (ViewGroup) (list),false);
        list.addHeaderView(header, null, false);
		
		
		
        edtTxtConName =(EditText)findViewById(R.id.edtTxtConName);
        btnSearchConsName=(Button)findViewById(R.id.btnSearchConsName);
        
        btnSearchConsName.setOnClickListener(v -> search());
        
        list.setOnItemClickListener((parent, view, position, id) -> {

			HashMap<String, String> map=mylist.get(position-1);

Toast.makeText(getApplicationContext(), map.get("ACC_NO").toString(), Toast.LENGTH_SHORT).show();
UtilAppCommon.acctNbr= map.get("ACC_NO").toString();
gotoBilling();
});
       
    }
    void gotoBilling()
  	{
		  UtilDB util = new UtilDB(getBaseContext());
		  UtilAppCommon.binder= util.getActiveBinder();
			UtilAppCommon.sdoCode= util.getSdocd();
  		if(UtilAppCommon.acctNbr.isEmpty())
  		{
  				Toast.makeText(getBaseContext(), "Please Enter value for Account Number", Toast.LENGTH_LONG).show();
  		}	
  		else if(!util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number"))
  		{
  			
  			Toast.makeText(getBaseContext(), "Please Enter Valid Account Number", Toast.LENGTH_LONG).show();
  		}
  		else
  		{				
  				//finish();
  			  	System.out.println("Going to ActvBilling");
  				startActivity(new Intent(this, ActvBilling.class));
  										
  		}			
  	}
	
    void search()
    {
    	UtilDB util = new UtilDB(getBaseContext());
    	list = (ListView) findViewById(R.id.lvSrcByConName);
    	
    	edtTxtConName =(EditText)findViewById(R.id.edtTxtConName);
    	mylist=util.getSrcListByConsName(edtTxtConName.getText().toString()) ;
        util.close();
        
        adapter = new InteractiveArrayAdapter(this,R.layout.newcons_listitems, mylist,
        		new String[] {"BINDER","ACC_NO", "NAME","ADDR"},
        new int[] {R.id.newConsBinder,R.id.newConsAccNo, R.id.newConsName, R.id.newConsAddr});
        list.setAdapter(adapter);
    	
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
       
	

	
}