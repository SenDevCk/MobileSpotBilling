package org.cso.MobileSpotBilling;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActvOptConsumerSearch extends Activity implements OnClickListener
{
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.optconsumersearch);
	    
	    
	    Button serchMeterBtn = (Button) findViewById(R.id.btnMeterNbrSearch);
        Button serchNameBtn = (Button)findViewById(R.id.btnConsNameSearch);
        Button serchOldAcctNbrBtn = (Button)findViewById(R.id.btnOldAcctNbrSearch);
        
        serchMeterBtn.setOnClickListener(this);
        serchNameBtn.setOnClickListener(this);
        serchOldAcctNbrBtn.setOnClickListener(this);
        
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
		 		 startActivity(new Intent(ActvOptConsumerSearch.this, ActvivityMain.class));*/
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
			switch(id)
			{
			case R.id.btnMeterNbrSearch:
				startActivity(new Intent(this,ActvSrcByMtrNo.class));
				break;
				
			case R.id.btnConsNameSearch:
				startActivity(new Intent(this,ActvSrcByConName.class));
				break;
				
			case R.id.btnOldAcctNbrSearch:
				startActivity(new Intent(this,ActvSrcByConNo.class));
				break;
				
			
			
			}
			
		}
	   
	   private void showMeterNbrSearch() 
	   {
	   
	   	final Dialog dialog = new Dialog(this);
	   	dialog.setContentView(R.layout.dg_serchbymeterno);
	   	dialog.setTitle("Enter Meter Number:");
	   	dialog.show();
	   	

	   	 final EditText edtMeterNbr = (EditText)dialog.findViewById(R.id.editMeterNbr);
	   	
	
	   	  	
	   	Button okButton = (Button) dialog.findViewById(R.id.btnSearchMtrNbr);	   	

	   	okButton.setOnClickListener(new OnClickListener() 
	   	{
	   		public void onClick(View v) 
	   		{
	   		
	   			if(edtMeterNbr.length()==0)
	   			{
						Toast.makeText(ActvOptConsumerSearch.this , "Please Enter Meter Number", Toast.LENGTH_LONG).show();
						//errTxt.setText("Please Enter Account number");
				}		
	   			else
	   			{
	   				dialog.dismiss();	   					
	   			}
	   		}
	   		});
	 	   		
	   }
	   
	   private void showConsNameSearch() 
	   {
	   	
	   	final Dialog dialog = new Dialog(this);
	   	dialog.setContentView(R.layout.dg_searchbyconsumername);
	   	dialog.setTitle("Enter Consumer Name:");
	   	dialog.show();
	   	
	    final EditText edtConsumerName = (EditText)dialog.findViewById(R.id.edtTxtUserName);
	   	
	   	Button okButton = (Button) dialog.findViewById(R.id.btnSearchConsName);

	   	okButton.setOnClickListener(new OnClickListener() 
	   	{
	   		
	   		public void onClick(View v) 
	   		{
	   			if(edtConsumerName.length()==0)
		   		{
		   			Toast.makeText(ActvOptConsumerSearch.this , "Please Enter Consumer Name", Toast.LENGTH_LONG).show();
		   		}
	   			
	   			else
	   			{
	   			dialog.dismiss();   			
	   		}}
	   	});
	   	
	   		
	   }
	   
	   public void onBackPressed() {
			 finish();
			 startActivity(new Intent(this, ActvBillingOption.class));  		     
		     return;
		 }
	   
	   
	   private void showOldAcctNbrSearch() 
	   {
	   	
	   	final Dialog dialog = new Dialog(this);
	   	dialog.setContentView(R.layout.dg_searchbyoldacountno);
	   	dialog.setTitle("Enter Old Account Number:");
	   	dialog.show();
	   	
	   	
	    final EditText edtOldAccNbr = (EditText)dialog.findViewById(R.id.edtOldAccnbr);

	   	Button okButton = (Button) dialog.findViewById(R.id.btnSearchOldAcctNbr);
	   	
	   	okButton.setOnClickListener(new OnClickListener() 
	   	{
	   		
	   		public void onClick(View v) 
	   		{
	   			if(edtOldAccNbr.length()==0)
		   		{
		   			Toast.makeText(ActvOptConsumerSearch.this , "Please Enter Old Account Number", Toast.LENGTH_LONG).show();
					//errTxt.setText("Please Enter Account number");
		   		}
	   			else
	   			{
	   			dialog.dismiss();   			
	   		}}

	   	});
	   	
	   
	   }
	   
	   
	   
}