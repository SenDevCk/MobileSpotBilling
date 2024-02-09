package org.cso.MSBAsync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.cso.MobileSpotBilling.CommonDefs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DownloadInputDataAsyncTask extends AsyncTask<Void, Void, Void>{

	Context context = null;
	final String TAG = "JsonParsingAsyncTask";
	private ProgressDialog pDialog = null;
	
	public DownloadInputDataAsyncTask(Context context){
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(context);
		pDialog.setTitle("Downloading");
		pDialog.setMessage("Plaease wait...");
		pDialog.show();
	}
	
	
	@Override
	protected Void doInBackground(Void... params) {
		downloadInputData();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		super.onPostExecute(result);
		Log.i("TAG", "onPostExecute()");
		
		//pDialog.dismiss();
	}
	
	public void downloadInputData(){
		
		InputStream source = null;
		try {
			URL address = new URL(CommonDefs.URL); 
			source = address.openConnection().getInputStream(); 
		} catch (IOException e1) {
			e1.printStackTrace();
		}
               
		try {
			File sbDocsDirectory = new File(CommonDefs.SBDOC_DIR_IN_SDCARD);
			sbDocsDirectory.mkdirs();
			File outputFile = new File(sbDocsDirectory, CommonDefs.FILE_NAME);
			FileOutputStream fos = new FileOutputStream(outputFile);
			
			int nextChar;
			while ((nextChar = source.read()) != -1) {
              fos.write((char) nextChar);
              fos.flush();
			}
		} catch (Exception e) {
			Log.e(TAG, "downloadInputData() in File write", e);
		}
	}
	
}

