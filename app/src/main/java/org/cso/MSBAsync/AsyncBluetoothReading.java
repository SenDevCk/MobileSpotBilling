package org.cso.MSBAsync;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.UtilSvrData;
import org.cso.MobileSpotBilling.OnBillGenerate;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by 41008931 on 01/09/2017.
 */

public class AsyncBluetoothReading extends AsyncTask<String, Void, String[]> {

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

    public AsyncBluetoothReading(Context ctx)
    {
        try {
            this.context=ctx;
            //taskCallback=(TaskCallback) ctx; 20.11.15
            //this.mCallback=mCallback;
            utildb=new UtilDB(context);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("AsyncGetOutputData => ", e.getMessage());
        }
    }

    @Override
    protected String[] doInBackground(String... params) {

        try {
            // TODO Auto-generated method stub
            //ServiceUtil svcObj = new ServiceUtil();
            UtilSvrData sv=new UtilSvrData();

            glbVar = sv.updateBluetoothReading(params);

            if((glbVar.equalsIgnoreCase("Network Issue / Not Reachable") || glbVar.equalsIgnoreCase("No Output Data")) && strRetry.equals(""))
            {
                //UtilAppCommon.strHostName =	/*"http://125.16.220.4/"*/"https://www.bihardiscom.co.in/";		//Production
                //UtilAppCommon.strHostName =	"http://220.225.3.133/";		//Development
                UtilAppCommon.strHostName =	"http://112.133.239.225/";		//Development New
                //param[11] = "2";
                glbVar = sv.updateBluetoothReading(params);
                strRetry = "1";
                Toast.makeText(context, "Retrying to fetch billing data", Toast.LENGTH_LONG).show();
                return null;
            }

            JSONArray ja;

            ja = new JSONArray(glbVar);
            for (int i = 0; i < ja.length(); i++)
            {
                JSONObject jsonData = ja.getJSONObject(i);
                glbVar = "Bluetooth Meter Reading Not Uploaded Successfully.";
                if(jsonData.get("SAPFLAG").equals("1"))
                {
                    utildb.UpdateSAPBlueInputSyncFlag(UtilAppCommon.SAPBlueIn.CANumber);
                    //utildb.UpdateSAPBlueInputSyncFlag("100742011");
                    glbVar = "Bluetooth Meter Reading Uploaded.";
                    blVar = true;
                }
                else
                {
                    utildb.UpdateSAPBlueInputMsg(UtilAppCommon.SAPBlueIn.CANumber,"0", "Failed");
                    //utildb.UpdateSAPBlueInputMsg("100742011","0", "Failed");
                }
            }
            return null;
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("AsyncOutdoInBackground ", "" + e.getMessage());
            return null;
        }

            //return new String[0];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //if(!UtilAppCommon.blActyncBtn)
        {
            pDialog = new ProgressDialog(context);
            pDialog.setTitle("Connecting to server.");
            pDialog.setMessage("Please wait...");
            //pDialog.show();
        }
    }

    @Override
    protected void onPostExecute(String[] result)
    {
        super.onPostExecute(result);
        //Log.i("TAG", "onPostExecute()");
        //pDialog.dismiss();
        if(blVar && glbVar.length()>0)
            Toast.makeText(context, glbVar, Toast.LENGTH_LONG).show();
        //if(glbVar.equalsIgnoreCase("0"))
        //	Toast.makeText(context, "Output data not available for CA - "+ UtilAppCommon.in.CONTRACT_AC_NO , Toast.LENGTH_LONG).show();
        //if(UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK")) 20.11.15
        //taskCallback.done();

        //mCallback.onFinish();
    }
}
