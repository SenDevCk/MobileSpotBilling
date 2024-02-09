package org.cso.MSBAsync;

//import org.cso.MSBUtil.ServiceUtil;
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
public class AsyncGetInputData extends  AsyncTask<String, Void, String>{
	
	UtilDB utildb;
	SQLiteDatabase db;
	private ProgressDialog pDialog = null;
	private Context context;
	int intCounter=0;
	TaskCallback  taskCallback;
	String checkData,NoOfConsumer,strMsg;
	public AsyncGetInputData(Context ctx)
	{
		this.context=ctx;
		taskCallback=(TaskCallback) ctx;
		utildb=new UtilDB(context);
	}
	
	public String getInputData(String strParam){		
	
		try {
		
			UtilSvrData  utilsvrdata=new UtilSvrData();

			//String[] jsonInputData=utilsvrdata.getJsonInputData().split("\\|",2);
			String jsonInputData=utilsvrdata.getJsonInputData(strParam);
			 
		    JSONArray jarray;
		    
		    checkData=jsonInputData;
		    
		
		    jarray = new JSONArray(jsonInputData);
		
	        int ilen= jarray.length();
	        if(ilen > 0)
			{
	        	Log.e("Check Data ==>> ", "Length ==>> " + ilen);
	    		intCounter=utildb.InsertInputBulk(jarray);
			}
	        else
	        {
	        	JSONObject jsonData = new JSONObject(jsonInputData);
	        	strMsg = jsonData.getString("REASON").toString();
	        	Log.e("Check Data ==>> ", "Reason ==>> " + strMsg);
	        	return jsonData.getString("REASON");	        	
	        }
			//utildb.ClearBilledData();
				
			}
		catch(Exception ex)
		{
			utildb.close();
		}
		return "";
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(context);
		pDialog.setTitle("Downloading input data!");
		pDialog.setMessage("Please wait...");
		pDialog.show();
	}

	@Override
	protected String doInBackground(String... param) {

		strMsg = getInputData(param[1]);
		
		/*
		try {
			ServiceUtil svcObj = new ServiceUtil();
			
			//String[] jsonInputData=svcObj.getInputDataJSON(param[0],param[1]).split("},{");
			String jsonInputData=svcObj.getInputDataJSON(param[0],param[1]);
						
		    JSONArray jarray;
		    
		    checkData=jsonInputData;
		    
		    Log.e("Check Data ==>> ", checkData);
		    
		   // NoOfConsumer=jsonInputData[1];
		   //Log.e("Check Data ==>> ", jsonInputData[1]);
		    
		    //jarray = new JSONArray(checkData);
		    jarray = new JSONArray(jsonInputData);
		
	        int ilen= jarray.length();
			//if(ilen >0&&ilen==Integer.parseInt(NoOfConsumer))
	        if(ilen > 0)
			{
	    		intCounter=utildb.InsertInputBulk(jarray);
			}
			utildb.ClearBilledData();
		}
		catch(Exception ex)
		{
			utildb.close();
		}		
		*/
		return strMsg;
	}
	
	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
		//Log.e("onPostExecute", result);
		Log.i("TAG", "onPostExecute()");
		pDialog.dismiss();
		
		if(intCounter>0)
		{
			Toast.makeText(context,intCounter+" numbers of input Data downloaded successfully! /n Configure Printer before billing", Toast.LENGTH_LONG).show();
		}
		else if(strMsg.toUpperCase().startsWith("Invalid/Unknown Apk Version".toUpperCase()))
		{
			Toast.makeText(context,"Failed to download input data! Please update version.", Toast.LENGTH_LONG).show();
		}
		else if(!strMsg.trim().equalsIgnoreCase(""))
		{
			Toast.makeText(context,strMsg, Toast.LENGTH_LONG).show();
		}
		else 
		{
			Toast.makeText(context,"Failed to download input data!", Toast.LENGTH_LONG).show();
		}
		taskCallback.done();
	}
	
}
