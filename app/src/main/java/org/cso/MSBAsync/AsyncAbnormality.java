package org.cso.MSBAsync;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.UtilSvrData;
import org.cso.MobileSpotBilling.ActvLogin;
import org.cso.MobileSpotBilling.TaskCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncAbnormality extends  AsyncTask<String, Void, String[]> {

	UtilDB utildb;
	SQLiteDatabase db;
	private ProgressDialog pDialog = null;
	private Context context;
	public String glbVar;
	TaskCallback taskCallback;
	public AsyncAbnormality(Context ctx) {
		// TODO Auto-generated constructor stub
		this.context=ctx;
		taskCallback=(TaskCallback) ctx;
		utildb=new UtilDB(context);
	}
	
	private String getOutputData(String... strAbnormality) 
	{
		// TODO Auto-generated method stub
		int icntr = 0;
		glbVar = "";
		try 
		{
			Log.e("getOutputData", "Started");
			
			UtilSvrData  sv=new UtilSvrData();
			String jsonTxt=sv.updateAbnormality(strAbnormality);			
			           
	        JSONArray ja;
	        
	        if(jsonTxt.equalsIgnoreCase("Network Issue / Not Reachable"))
			{
	        	utildb.UpdatAbnormalityFlag("7", UtilAppCommon.acctNbr);
	        	glbVar = "0";
	        	return "";
			}
		
			JSONObject jsonData = new JSONObject(jsonTxt);
			String strFlag = jsonData.getString("FLAG");
			if(strFlag == null || strFlag.equals(""))
				strFlag = "2";
			utildb.UpdatAbnormalityFlag(strFlag, UtilAppCommon.acctNbr);
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
			Log.e("AsyncAbnormality", "getOutput ==>> " + e.getMessage());
		}
		//Log.e("getOutputData", "Completed");
		return "";
	}


	@Override
	protected String[] doInBackground(String... params) {
		// TODO Auto-generated method stub
		//ServiceUtil svcObj = new ServiceUtil();
		
		getOutputData(params);
		
		return null;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//pDialog = new ProgressDialog(context);
		//pDialog.setTitle("Connecting to server.");
		//pDialog.setMessage("Please wait...");
		//pDialog.show();
	}
	
	@Override
	protected void onPostExecute(String[] result)
	{
		super.onPostExecute(result);
		
		//pDialog.dismiss();
		//if(glbVar.equalsIgnoreCase("1"))
		//	Toast.makeText(context, "Abnormality data updated." , Toast.LENGTH_LONG).show();
	}

}
