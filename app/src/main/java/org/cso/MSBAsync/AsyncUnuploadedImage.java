package org.cso.MSBAsync;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.cso.MSBUtil.ImageFileFilter;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.UtilSvrData;
import org.cso.MobileSpotBilling.OnBillGenerate;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;

/**
 * Created by 41008931 on 09/10/2017.
 */

public class AsyncUnuploadedImage extends AsyncTask<String, Void, String>  {


    UtilDB utildb;
    SQLiteDatabase db;

    private ProgressDialog pDialog = null;
    private Context context;
    public String glbVar = "";
    //TaskCallback taskCallback;
    OnBillGenerate mCallback;
    public AsyncUnuploadedImage(Context ctx) {
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
        Log.e("AsyncUnUploadImage => ", "doInBackground ==>>  Started");
        UtilSvrData sv=new UtilSvrData();
        byte array[] = null;
        String img_str = "";
        String jsonTxt = "";
        String[] sparams = new String[5];
        try
        {
            File file = new File(params[0]);

            if(file.isDirectory())
            {
                //File[] imageFiles = file.listFiles(new ImageFileFilter(this.context));
                File[] imageFiles = file.listFiles();
                for(int jcntr = 0; jcntr < imageFiles.length; jcntr++) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFiles[jcntr].getAbsolutePath());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] image = stream.toByteArray();
                    img_str = Base64.encodeToString(image, 0);
                    glbVar = "0";
                    String fileName = imageFiles[jcntr].getName();
                    //Log.i("Unuploaded Img", "Image File Name ==>> " + fileName);
                    String[] strParams = imageFiles[jcntr].getName().split("_");
                    //Log.i("Unuploaded Img", "CA Image File Name ==>> " + strParams[3]);
                    sparams[0] = strParams[3].replace(".jpg", "");
                    sparams[1] = strParams[2].substring(4,6);
                    sparams[2] = strParams[2].substring(0,4);
                    sparams[3] = strParams[1];
                    sparams[4] = strParams[0];

                    //Log.i("Unuploaded Img", "Parameters File Name ==>> " + sparams[0] + " = " + sparams[1] + " = " + sparams[2] + " = " + sparams[3] + " = " + sparams[4] );

                    jsonTxt = sv.updateImage(sparams[0], sparams[1], sparams[2], sparams[4], img_str, sparams[3]);
                    if (jsonTxt.equalsIgnoreCase("Network Issue / Not Reachable")) {
                        return "";
                    }

                    JSONObject jsonData = new JSONObject(jsonTxt);
                    String strFlag = jsonData.getString("FLAG");
                    String strCANo = jsonData.getString("CANo");
                    //String strCANo = params[0];
                    if (strCANo.equals("") || strCANo == null)
                        strCANo = sparams[0];
                    if (strFlag.equalsIgnoreCase("1")) {
                        utildb.UpdateUploadImage(strCANo);
                        glbVar = "1";
                        imageFiles[jcntr].delete();
                        //Toast.makeText(context, "Uploading " + utildb.getNonUploadedImageCount() + " of " + UtilAppCommon.strImageCount, Toast.LENGTH_LONG).show();
                    } else {
                        glbVar = "0";
                    }
                }
            }


        }
        catch(Exception e)
        {
            Log.e("AsyncUnUploadImage => ", "doInBackground ==>> IO " + e.getMessage());
        }


        return jsonTxt;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*pDialog = new ProgressDialog(context);
        pDialog.setTitle("Connecting to server.");
        pDialog.setMessage("Please wait...");
        pDialog.show();*/
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        //Log.i("TAG", "onPostExecute()");
        //pDialog.dismiss();
        //if(glbVar.equalsIgnoreCase("1"))
        //	Toast.makeText(context, "Image Uploaded." , Toast.LENGTH_LONG).show();
        //taskCallback.done();
        //mCallback.onFinish();
    }

    @Override
    protected void onCancelled() {
        // TODO Auto-generated method stub
        //super.onCancelled();
        return;
    }


}



