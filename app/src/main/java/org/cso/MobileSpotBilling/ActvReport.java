package org.cso.MobileSpotBilling;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActvReport extends Activity implements OnClickListener{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports); 
        Button MeterChangeList = (Button) findViewById(R.id.btnMtrChangeList);
        MeterChangeList.setOnClickListener(this); 

        Button unbilledConsList = (Button) findViewById(R.id.btnNewConsumerList);
        unbilledConsList.setOnClickListener(this); 

        Button NewConsumerList = (Button) findViewById(R.id.btnUnbilledConsumerList);
        NewConsumerList.setOnClickListener(this); 
        
        Button ReconnectionListList = (Button) findViewById(R.id.btnReconnectionList);
        ReconnectionListList.setOnClickListener(this); 
   
        Button SummaryList = (Button) findViewById(R.id.btnSummaryList);
        SummaryList.setOnClickListener(this); 
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
   	 		 startActivity(new Intent(ActvReport.this, ActvivityMain.class));*/
   	   		 finish();	   	 
   	   		 Intent intent = new Intent(this, ActvivityMain.class);  
   	   		 startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));  
   	   		 startActivity(intent);
   	 		 break;
   	   	 }
   	     return true;
       }
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		
		int id = v.getId();
		Intent intentConsumerListTest=new Intent(this, ConsumerListTest.class);

		switch(id)
		{
		case R.id.btnNewConsumerList:
			//finish();
			intentConsumerListTest.putExtra("View", "UnBilledConsumerList");
			startActivity(new Intent(this, BilledUnbilled.class));
			break;
		case R.id.btnReconnectionList:
			//finish();
			intentConsumerListTest.putExtra("View", "OutSortConsumerList");
			startActivity(intentConsumerListTest);
			break;
		case R.id.btnUnbilledConsumerList:
			//finish();

			startActivity(new Intent(this, ActvUnbilledListPrinting.class));
			break;
		case R.id.btnMtrChangeList:
			//finish();
			intentConsumerListTest.putExtra("View", "NetworkFailureList");
			startActivity(intentConsumerListTest);
			break;
		case R.id.btnSummaryList:
			//finish();
			startActivity(new Intent(this, ActvSummaryPrinting.class));
			break;
		}
		
		
	}
	private void DisplayUnbilledConsumerList() 
	{
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dg_rptconsumerlist);
		dialog.setTitle("Unbilled Consumer List:");
		dialog.show();		
		
		Button okButton = (Button) dialog.findViewById(R.id.btnOk);
		okButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				dialog.dismiss();				
			}
		});
	}
	private void DisplayReconnectionList() 
	{
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dg_rptconsumerlist);
		dialog.setTitle("Reconnection List:");
		dialog.show();		
		
		Button okButton = (Button) dialog.findViewById(R.id.btnOk);		
		
		okButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				dialog.dismiss();				
			}
		});
	}
	private void DisplayNewConsumerList() 
	{
		
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dg_rptconsumerlist);
		dialog.setTitle("New Consumer List:");
		dialog.show();		
		
		Button okButton = (Button) dialog.findViewById(R.id.btnOk);		
		

		okButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				dialog.dismiss();				
			}
		});
	}
     
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		startActivity(new Intent(this, ActvivityMain.class));
		finish();
	}
	}
