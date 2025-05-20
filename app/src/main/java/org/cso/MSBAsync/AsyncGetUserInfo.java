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
import org.cso.MobileSpotBilling.ActvDownloadInputData;
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncGetUserInfo extends  AsyncTask<String, Void, String[]>
{
	
	UtilDB utildb;
	SQLiteDatabase db;
	private ProgressDialog pDialog = null;
	private Context context;
	public String glbVar;
	public Boolean blVar = false;
	TaskCallback taskCallback;
	public AsyncGetUserInfo(Context ctx)
	{
		this.context=ctx;
		taskCallback=(TaskCallback) ctx;
		utildb=new UtilDB(context);
	}
	
	public String getUserData(String strIMEI,String strPass){
		
		/*URL url;
		UtilDB util;*/
		try {
			
			
			UtilSvrData  sv=new UtilSvrData();
			
			String jsonTxt=sv.getUserInfo(strIMEI,strPass);				
			
			if(jsonTxt.equalsIgnoreCase("Network Issue / Not Reachable"))
			{
				blVar = true;
				return jsonTxt;
			}
			UtilAppCommon.userInfo=jsonTxt.toString();			
                       
	        JSONArray ja;
		
			ja = new JSONArray(jsonTxt);
		
	        int ilen= ja.length();
			if(ilen >0)
			{
	
		    	for (int i = 0; i < ja.length(); i++) 
		        {
		    		JSONObject jsonData = ja.getJSONObject(i);    		
		    		String  record=new String();    		// pick the imei no of the device	    	
		    	
		    		/*record=jsonData.getString("CompanyID")+"|"+jsonData.getString("MeterReaderID")+"|"+jsonData.getString("SDO")+"|"+jsonData.getString("Binder")+
		    				  "|"+"201311"+"|"+jsonData.getString("IMEINumber")+"|"+"Y"+"|"+jsonData.getString("ActiveBinder")+
		    				  "|"+strUserName+"|"+strPass;*/
		    		
		    		/*record=jsonData.getString("CompanyID")+"|"+jsonData.getString("MeterReaderID")+"|"+jsonData.getString("SDO")+"|"+jsonData.getString("MRU")+
		  				  "|"+jsonData.getString("SlNo")+"|"+jsonData.getString("IMEINumber")+"|"+"Y"+"|"+jsonData.getString("ActiveMRU")+
		  				  "|"+jsonData.getString("ActiveYN")+"|"+jsonData.getString("FromDate")+"|"+jsonData.getString("DateLastMaint");
		    		*/
		    		record=jsonData.getString("METER_READER_ID")+"|"+jsonData.getString("METER_READER_NAME")+"|"+jsonData.getString("PASSCODE")+"|"+UtilAppCommon.IMEI_Number;
		    		
		    		Log.e("User Record", record);
		    		if(jsonData.getString("METER_READER_ID").equalsIgnoreCase(strIMEI) && jsonData.getString("PASSCODE").equals(strPass))
		    		{
		    			Log.e("Record Details:", record);
		    			utildb.insertIntoUserInfo(record);
		    			utildb.getUserInfo();
		    			blVar = false;
		    		}
		    		else
		    		{
		    			glbVar = "Please enter valid user name and password ! DeviceId : "+UtilAppCommon.IMEI_Number.toUpperCase();
		    			blVar = true;
		    			Log.e("Record Details:", "Invalid User/Password");
		    			return glbVar;
		    		}
		        }
			}     
			return jsonTxt;	    
		} 	
		catch (JSONException e) {
			
			Log.d("TAG", e.getMessage());
		}
		
		return "";
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
		glbVar=getUserData(param[0],param[1]);

		return null;
	}
	
	@Override
	protected void onPostExecute(String[] result)
	{
		super.onPostExecute(result);
		//Log.i("TAG", "onPostExecute()");
		pDialog.dismiss();
		System.out.println("UserInput Result::"+glbVar.length());
		Log.e("strRedirectTo", UtilAppCommon.strRedirectTo);
		
		if(blVar && glbVar.length()>0) {
			//Toast.makeText(context, glbVar, Toast.LENGTH_LONG).show();
			AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
			builder1.setMessage(""+glbVar);
			builder1.setCancelable(true);
			builder1.setPositiveButton(
					"OK",
					(dialog, id) -> dialog.cancel());
			AlertDialog alert11 = builder1.create();
			alert11.show();
		}
		else if(glbVar.length()>0 && !blVar)
		{
			taskCallback.done();
			//context.startActivity(new Intent(context, ActvSetupInfo.class));
			//context.startActivity(new Intent(context, ActvivityMain.class));
			if(UtilAppCommon.strRedirectTo.equalsIgnoreCase("Setup"))
				context.startActivity(new Intent(context, ActvDownloadInputData.class));
			else
				context.startActivity(new Intent(context, ActvivityMain.class));
		}
		else
		{
			Toast.makeText(context,"Please enter valid user name and password!", Toast.LENGTH_LONG).show();			
		}
	}
	
}
