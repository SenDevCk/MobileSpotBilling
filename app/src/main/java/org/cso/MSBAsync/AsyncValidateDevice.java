package org.cso.MSBAsync;


import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cso.MSBUtil.NetworkUtil;
//import org.cso.MSBUtil.ServiceUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.UtilSvrData;
import org.cso.MobileSpotBilling.ActvBillingOption;
import org.cso.MobileSpotBilling.ActvLogin;
import org.cso.MobileSpotBilling.ActvSetupInfo;
import org.cso.MobileSpotBilling.ActvivityMain;
import org.cso.MobileSpotBilling.TaskCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.JsonParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncValidateDevice extends  AsyncTask<String, Void, String[]>
{
	
	UtilDB utildb;
	SQLiteDatabase db;
	private ProgressDialog pDialog = null;
	private Context context;
	public String glbVar;
	TaskCallback taskCallback;
	public AsyncValidateDevice(Context ctx)
	{
		this.context=ctx;
		taskCallback=(TaskCallback) ctx;
		utildb=new UtilDB(context);
	}
	
	public String getValidIMEI(String strIMEI, String strVersion){
		
		/*URL url;
		UtilDB util;*/
		try {
			
			
			UtilSvrData  sv=new UtilSvrData();
			
			String jsonTxt=sv.getValidDevice(strIMEI);	
			Log.e("jsonTxt - ",jsonTxt);
			if((jsonTxt.equalsIgnoreCase("Timeout") || jsonTxt.equalsIgnoreCase("Network Issue")) && UtilAppCommon.strHostName != "http://125.16.220.4/")
			{
				 jsonTxt=sv.getValidDevice(strIMEI);
				 //return "Invalid";
			}
			if(jsonTxt.equalsIgnoreCase("Network Issue"))
    		{
				if(utildb.getLocalDevValidation(strIMEI))
				{
					UtilAppCommon.ValidDevice = true;
	    			UtilAppCommon.strRedirectTo = "Main";
	    			return "Valid";
				}
				else
				{
					UtilAppCommon.ValidDevice = false;
					return "Invalid";
				}
    		}			
			
        	JSONObject jsonData = new JSONObject(jsonTxt);
    		String  record=new String();    		// pick the imei no of the device	    	
    		Log.e("FILENAME - ", jsonData.getString("FILENAME"));
    		Log.e("strVersion - ",strVersion);
    		//UtilAppCommon.strAppVersion = jsonData.getString("FILENAME");
    		if(jsonData.getString("FILENAME").equalsIgnoreCase(strVersion))
    		{
    			Log.e("STATUS - ", "Inside FILENAME");
    			UtilAppCommon.ValidVersion = true;
    			utildb.UpdateVersion(jsonData.getString("FILENAME").toString());
    		}
    		if(jsonData.getString("FLAG").equalsIgnoreCase("1") && jsonData.getString("STATUS").equalsIgnoreCase("SUCCESS"))
    		{
    			Log.e("STATUS - ", "Inside If");
    			UtilAppCommon.ValidDevice = true;
    			UtilAppCommon.strRedirectTo = "Main";
    			utildb.truncateTable("validDevice");
    			utildb.insertIntovalidDevice(UtilAppCommon.IMEI_Number + "|1");
    			return "Valid";
    		}
		} 
		catch (Exception e) {
			
			Log.e("getValidIMEI E", e.getMessage());
			UtilAppCommon.strRedirectTo = "";
			return "Invalid";
		}
		UtilAppCommon.strRedirectTo = "";
		return "Invalid";
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
	protected String[] doInBackground(String... param) {
		//ServiceUtil svcObj = new ServiceUtil();
		glbVar=getValidIMEI(param[0], param[1]);
     
		return null;
	}
	
	@Override
	protected void onPostExecute(String[] result)
	{
		super.onPostExecute(result);
		//Log.i("TAG", "onPostExecute()");
		pDialog.dismiss();
		
		/*if(glbVar.equalsIgnoreCase("Valid"))
		{
			//context.startActivity(new Intent(context, ActvSetupInfo.class));
			context.startActivity(new Intent(context, ActvivityMain.class));
		}
		else
		{
			Toast.makeText(context,"This the not an authorised device to run this application", Toast.LENGTH_LONG).show();
			taskCallback.done();
		}*/
		if(!UtilAppCommon.ValidDevice)
		{
			//Toast.makeText(context,"This the not an authorised device to run this application", Toast.LENGTH_LONG).show();
			context.startActivity(new Intent(context, ActvLogin.class));
			taskCallback.done();
		}
		else
		{
			if(!UtilAppCommon.ValidVersion);
				//Toast.makeText(context,"Request to updated the application before downloading new MRU", Toast.LENGTH_LONG).show();
			context.startActivity(new Intent(context, ActvLogin.class));
			taskCallback.done();
		}
		
	}
}
