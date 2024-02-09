package org.cso.MSBAsync;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.UtilSvrData;
import org.cso.MobileSpotBilling.TaskCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncMissingCons extends  AsyncTask<String, Void, String[]> {

	UtilDB utildb;
	SQLiteDatabase db;
	
	private ProgressDialog pDialog = null;
	private Context context;
	public String glbVar = "";
	TaskCallback taskCallback;
	
	public AsyncMissingCons(Context ctx) {
		try {
			this.context=ctx;
			taskCallback=(TaskCallback) ctx;
			utildb=new UtilDB(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("AsyncMissingCons => ", e.getMessage());
		}
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(context);
		pDialog.setTitle("Connecting to server.");
		pDialog.setMessage("Please wait...");
		pDialog.show();
	}

	@Override
	protected String[] doInBackground(String... params) {
		// TODO Auto-generated method stub
		glbVar = getOutputData(params);
		return null;
	}
	
	private String getOutputData(String... SAPInput) {
		// TODO Auto-generated method stub
		int icntr = 0;
		glbVar = "";
		try 
		{
			//Log.e("getOutputData", "Started");
			
			UtilSvrData  sv=new UtilSvrData();
			String jsonTxt=sv.updateMissingCons(SAPInput);			
			           
	        if(jsonTxt.equalsIgnoreCase("Network Issue / Not Reachable"))
			{
	        	utildb.UpdateConsumerDetails("7", SAPInput[0]);
	        	glbVar = "0";
	        	return "";
			}
	        	        
	        JSONObject jsonData = new JSONObject(jsonTxt);
			String strFlag = jsonData.getString("FLAG");
			if(strFlag == null || strFlag.equals(""))
				strFlag = "7";
			utildb.UpdateConsumerDetails(strFlag, SAPInput[0]);
			if(jsonData.getString("FLAG").equalsIgnoreCase("1"))
			{	
				glbVar = "1";
			}
			else
			{
				glbVar = "0";
			}
		   
		} catch (JSONException e) 
		{	
			Log.e("TAG AsyncMissingCons E", "" + e.getMessage());
		}
		//Log.e("getOutputData", "Completed");
		return "";
	}

	
	@Override
	protected void onPostExecute(String[] result)
	{
		super.onPostExecute(result);
		//Log.i("TAG", "onPostExecute()");
		pDialog.dismiss();
		if(glbVar.equalsIgnoreCase("1"))
			Toast.makeText(context, "Consumer data updated." , Toast.LENGTH_LONG).show();
		//taskCallback.done();
	}	

}
