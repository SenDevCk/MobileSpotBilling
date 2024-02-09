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

public class AsyncUpdatePoleMobile extends  AsyncTask<String, Void, String[]> {

	UtilDB utildb;
	SQLiteDatabase db;
	
	private ProgressDialog pDialog = null;
	private Context context;
	public String glbVar = "";
	TaskCallback taskCallback;
	
	public AsyncUpdatePoleMobile(Context ctx)
	{
		try {
			this.context=ctx;
			taskCallback=(TaskCallback) ctx;
			utildb=new UtilDB(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("AsyncUpdatePoleMobile => ", e.getMessage());
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
		getOutputData(params);
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
			String jsonTxt=sv.updatePoleMobile(SAPInput);			
			           
	        JSONArray ja;
	        
	        if(jsonTxt.equalsIgnoreCase("Network Issue / Not Reachable"))
			{
	        	utildb.UpdateMobPoleFlag("7", UtilAppCommon.acctNbr);
	        	glbVar = "0";
	        	return "";
			}
		
			JSONObject jsonData = new JSONObject(jsonTxt);
			String strFlag = jsonData.getString("MESSAGE");
			if(strFlag == null || strFlag.equals(""))
				strFlag = "N";
			utildb.UpdateMobPoleFlag(strFlag, UtilAppCommon.acctNbr);
			if(jsonData.getString("MESSAGE").equalsIgnoreCase("Y"))
			{	
				glbVar = "1";
			}
			else
			{
				glbVar = "0";
			}
	   
		} catch (JSONException e) 
		{	
			Log.e("TAG getOutputData", "" + e.getMessage());
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
			Toast.makeText(context, "Mobile No. & Pole No. updated - "+ UtilAppCommon.in.CONTRACT_AC_NO , Toast.LENGTH_LONG).show();
		//taskCallback.done();
	}	

}
