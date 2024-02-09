package org.cso.MobileSpotBilling;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.widget.SimpleAdapter;

public class InteractiveArrayAdapter extends SimpleAdapter {
	 
	  private final  ArrayList<HashMap<String, String>> list;
	  private final Activity context;
	  public InteractiveArrayAdapter(Activity context, 
			  int hlreportListitem, ArrayList<HashMap<String, String>> list, 
			  String[] columns, int[] column_id) {
		  
	    super(context, list, hlreportListitem, columns, column_id );
	    this.context = context;
	    this.list = list;
	    
	  }

	 
	 


}
