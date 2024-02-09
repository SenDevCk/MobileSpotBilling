package org.cso.MSBAsync;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.UtilSvrData;
import org.cso.MobileSpotBilling.OnBillGenerate;
import org.cso.MobileSpotBilling.TaskCallback;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class AsyncImage extends  AsyncTask<String, Void, String> {

	UtilDB utildb;
	SQLiteDatabase db;
	
	private ProgressDialog pDialog = null;
	private Context context;
	public String glbVar = "";
	//TaskCallback taskCallback;
	OnBillGenerate  mCallback;
	public AsyncImage(Context ctx ,OnBillGenerate  mCallback) {
		// TODO Auto-generated constructor stub
		try {
			this.context=ctx;
			//taskCallback=(TaskCallback) ctx;
			this.mCallback=mCallback;
			utildb=new UtilDB(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("AsyncImage => ", e.getMessage());
		}
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		Log.e("AsyncImage => ", "doInBackground ==>>  Started");
		UtilSvrData  sv=new UtilSvrData();
		byte array[] = null;
		String img_str = "";
		String jsonTxt = "";
		try
		{
			File file = new File(params[4]);
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
			byte[] image = stream.toByteArray();
			img_str = Base64.encodeToString(image, 0);
			glbVar = "0";
			jsonTxt=sv.updateImage(params[0], params[1], params[2], params[3], img_str, params[5]);			
			if(jsonTxt.equalsIgnoreCase("Network Issue / Not Reachable"))
			{
	        	return "";
			}
			
			JSONObject jsonData = new JSONObject(jsonTxt);
			String strFlag = jsonData.getString("FLAG");
			String strCANo = jsonData.getString("CANo");
			//String strCANo = params[0];
			if(strCANo.equals("") || strCANo == null)
				strCANo = params[0];
			if(strFlag.equalsIgnoreCase("1"))
			{
				utildb.UpdateUploadImage(strCANo);
				glbVar = "1";
				Toast.makeText(context,"Uploading " + utildb.getNonUploadedImageCount() +  " of " + UtilAppCommon.strImageCount , Toast.LENGTH_LONG).show();
			}
			else
			{
				glbVar = "0";
			}
			
		}
		catch(Exception e)
		{
			Log.e("AsyncImage => ", "doInBackground ==>> IO " + e.getMessage());
		}
			
		
		return jsonTxt;
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
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
		//Log.i("TAG", "onPostExecute()");
		pDialog.dismiss();
		//if(glbVar.equalsIgnoreCase("1"))
		//	Toast.makeText(context, "Image Uploaded." , Toast.LENGTH_LONG).show();
		//taskCallback.done();
		mCallback.onFinish();
	}
	
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		//super.onCancelled();
		return;
	}
	
	


}
