package org.cso.MSBAsync;

import org.cso.MSBUtil.NetworkUtil;
//import org.cso.MSBUtil.ServiceUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.UtilSvrData;
import org.cso.MobileSpotBilling.ActvBillingOption;
import org.cso.MobileSpotBilling.IntermediateActivity;
import org.cso.MobileSpotBilling.TaskCallback;
import org.cso.MobileSpotBilling.OnBillGenerate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.FieldNamingStrategy;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncGetOutputData extends  AsyncTask<String, Void, String[]> {

	UtilDB utildb;
	SQLiteDatabase db;
	
	private ProgressDialog pDialog = null;
	private Context context;
	public String glbVar = "";
	private String jText = "";
	public Boolean blVar = false;
	//TaskCallback taskCallback; 20.11.15
	OnBillGenerate  mCallback;
	private String strRetry = "";
	public AsyncGetOutputData(Context ctx,OnBillGenerate  mCallback)
	{
		try {
			this.context=ctx;
			//taskCallback=(TaskCallback) ctx; 20.11.15
			this.mCallback=mCallback;
			utildb=new UtilDB(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("AsyncGetOutputData => ", e.getMessage());
		}
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//if(!UtilAppCommon.blActyncBtn)
		{
			Toast.makeText(context, "Step16 Completed", Toast.LENGTH_SHORT).show();
			pDialog = new ProgressDialog(context);
			pDialog.setTitle("Connecting to server.");
			pDialog.setMessage("Please wait...");
			pDialog.show();
		}
	}

	@Override
	protected String[] doInBackground(String param[]) {

		try {
			// TODO Auto-generated method stub
			//ServiceUtil svcObj = new ServiceUtil();
			glbVar = getOutputData(param);
			if((glbVar.equalsIgnoreCase("Network Issue / Not Reachable") || glbVar.equalsIgnoreCase("No Output Data")) && strRetry.equals(""))
			{
				//UtilAppCommon.strHostName =	/*"http://125.16.220.4/"*/"https://www.bihardiscom.co.in/";		//Production
				//UtilAppCommon.strHostName =	"http://220.225.3.133/";		//Development
				UtilAppCommon.strHostName =	"http://112.133.239.225/";		//Development  New
				if(param[11].equalsIgnoreCase("1") || param[11].equalsIgnoreCase("2"))
					param[11] = "2";
				glbVar = getOutputData(param);
				strRetry = "1";
				//Toast.makeText(context, "Retrying to fetch billing data", Toast.LENGTH_LONG).show();
			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("AsyncOutdoInBackground ", "" + e.getMessage());
			return null;
		}
	}

	@SuppressLint("DefaultLocale")
	private String getOutputData(String... SAPInput) {
		// TODO Auto-generated method stub
		int icntr = 0;
		glbVar = "";
		try 
		{//Toast.makeText(context, "Step17 Completed", Toast.LENGTH_SHORT).show();
			//Log.e("getOutputData", "Started");
			UtilAppCommon.strServerDtTm = "";
			UtilSvrData sv=new UtilSvrData();
			Log.v("AsyncGetOutputData", "********************* Step1 *******");
			Cursor curDetails = utildb.getMobPoleDetails(UtilAppCommon.acctNbr);
			Log.v("AsyncGetOutputData", "********************* Step2 *******");
			String strMobNo = ""; 
			String strPoleNo = "";
			
			if(curDetails != null)
			{
				Log.v("AsyncGetOutputData", "*********************Step3******* Cursor not null");
				strMobNo = curDetails.getString(0);
				if(strMobNo != "" && strMobNo != null && UtilAppCommon.in.METER_CAP.equalsIgnoreCase(strMobNo))
					strMobNo = "";
				strPoleNo = curDetails.getString(1);
			}

			Log.v("AsyncGetOutputData", "********************* Step4 *******After Cursor");
			String jsonTxt = "";
			//if(NetworkUtil.isConnectedToServer("http://220.225.3.133/BiharSBMService/MobiletoMW.asmx", 10000))
				jsonTxt=sv.getOutputData(SAPInput, strMobNo, strPoleNo);
			//else
			//	jsonTxt = "Network Issue / Not Reachable";
			jText = jsonTxt;
			Log.v("AsyncOutput", "******************"+jsonTxt);
			if(jsonTxt.equalsIgnoreCase("Network Issue / Not Reachable"))
			{
				    blVar = true;
				    glbVar = jsonTxt;
				    UtilAppCommon.inActualSAPMsgID = "7";
					UtilAppCommon.inSAPMsgID = "7";
					UtilAppCommon.SAPIn.MsgId = "7";
					UtilAppCommon.inSAPMsg = "Request Being Processed - Network Issue".toUpperCase();
					UtilAppCommon.SAPIn.strMsg = "Request being process - Network Issue".toUpperCase();
					utildb.UpdateSAPInputMsg(UtilAppCommon.SAPIn.CANumber, UtilAppCommon.SAPIn.MsgId, UtilAppCommon.inSAPMsg);
					
					return jsonTxt;
			}
			else
			{
		        JSONArray ja;
			
				ja = new JSONArray(jsonTxt);
				for (int i = 0; i < ja.length(); i++) 
			    {
					JSONObject jsonData = ja.getJSONObject(i);
					Log.e("MESSAGE_ID ", jsonData.getString("MESSAGE_ID").toString());
					UtilAppCommon.strServerDtTm = jsonData.getString("REC_DATE_TIME").toString();
					//Log.e("CANumber ", UtilAppCommon.SAPIn.CANumber);
					if(NetworkUtil.isOnline(context,null)) 
					{
						UtilAppCommon.inActualSAPMsgID = jsonData.getString("MESSAGE_ID").toString();
						if(jsonData.getString("MESSAGE_ID").equalsIgnoreCase("0") || jsonData.getString("MESSAGE_ID").equalsIgnoreCase("7") || jsonData.getString("MESSAGE_ID").equalsIgnoreCase("8") || 
								jsonData.getString("MESSAGE_ID").equalsIgnoreCase("9") || jsonData.getString("MESSAGE_ID").equalsIgnoreCase("10") || jsonData.getString("MESSAGE_ID").equalsIgnoreCase("3"))
						{
							UtilAppCommon.inSAPMsgID = "7";
							UtilAppCommon.SAPIn.MsgId = "7";
						}
						else
						{
							UtilAppCommon.inSAPMsgID = jsonData.getString("MESSAGE_ID").toString();
							UtilAppCommon.SAPIn.MsgId = jsonData.getString("MESSAGE_ID").toString();
						}
						
						
						UtilAppCommon.inSAPMsg = jsonData.getString("MESSAGE").toString();
						UtilAppCommon.SAPIn.strMsg = jsonData.getString("MESSAGE").toString();
					}
					else
					{
						UtilAppCommon.inActualSAPMsgID = "7";
						UtilAppCommon.inSAPMsgID = "7";
						UtilAppCommon.SAPIn.MsgId = "7";
						
						UtilAppCommon.inSAPMsg = "Request cannot processed - Offline".toUpperCase();
						UtilAppCommon.SAPIn.strMsg = "Request cannot processed - Offline".toUpperCase();
					}
					
					utildb.UpdateSAPInputMsg(UtilAppCommon.SAPIn.CANumber, UtilAppCommon.SAPIn.MsgId, UtilAppCommon.inSAPMsg);
					if(jsonData.getString("STATUS").equalsIgnoreCase("SUCCESS") && jsonData.getString("MESSAGE_ID").equalsIgnoreCase("6"))
					{	
						UtilAppCommon.SAPIn.ProcessedFlag = "1";
						icntr = utildb.InsertOutput(ja);
						utildb.UpdateInputTable(UtilAppCommon.SAPIn.CANumber, UtilAppCommon.inSAPMsgID);
						glbVar = "1";
						if(UtilAppCommon.blActyncBtn)
							utildb.UpdateSAPInputSyncFlag(UtilAppCommon.SAPIn.CANumber);						
					}
					else
					{
						if(!(UtilAppCommon.inSAPMsgID.equalsIgnoreCase("0") || UtilAppCommon.inSAPMsgID.equalsIgnoreCase("1") || UtilAppCommon.inSAPMsgID.equalsIgnoreCase("2") 
								|| UtilAppCommon.inSAPMsgID.equalsIgnoreCase("7") || UtilAppCommon.inSAPMsgID.equalsIgnoreCase("8") || 
								UtilAppCommon.inSAPMsgID.equalsIgnoreCase("9") || UtilAppCommon.inSAPMsgID.equalsIgnoreCase("10")))
						{
							utildb.UpdateInputTable(UtilAppCommon.SAPIn.CANumber, UtilAppCommon.inSAPMsgID);
							strRetry = "1";
						}
						Log.e("No Output Data", UtilAppCommon.SAPIn.CANumber);
						glbVar = "0";
						return "No Output Data";
					}
			    }
			} 
		} 
		catch (JSONException e) 
		{	
			Log.e("Async Out JSONException", "" + e.getMessage());
			Toast.makeText(context, "JSon Output Issue", Toast.LENGTH_LONG).show();
		}
		catch (Exception e) 
		{	
			Log.e("Async Out Exception", "" + e.getMessage());
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
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
		if(blVar && glbVar.length()>0)
			Toast.makeText(context, glbVar, Toast.LENGTH_LONG).show();	
		 //if(glbVar.equalsIgnoreCase("0"))
		//	Toast.makeText(context, "Output data not available for CA - "+ UtilAppCommon.in.CONTRACT_AC_NO , Toast.LENGTH_LONG).show();
		//if(UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK")) 20.11.15
			//taskCallback.done();

		  mCallback.onFinish();
	}	
}
