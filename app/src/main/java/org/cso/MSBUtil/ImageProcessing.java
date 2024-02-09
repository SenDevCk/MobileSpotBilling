package org.cso.MSBUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.cso.MSBAsync.AsyncImage;
import org.cso.MobileSpotBilling.OnBillGenerate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.app.Activity;
import android.content.Context;

public class ImageProcessing 
{

	File fileCompressImage = null;
	
	public ImageProcessing() {
		// TODO Auto-generated constructor stub
		//processImage(input, ctx, strName, strCANo);
	}
	
	@SuppressWarnings("unused")
	public String[] processImage(String strFilePath, File input, Context ctx, String strName, String strCANo)
	{
		Log.e("processImage", "Started ==>> " + input.length() + " == " + 30 * 1024);
		File file = input;
		UtilDB utildb = new UtilDB(ctx);
	
		String AppDir = strFilePath; 
		File f = new File(AppDir);
		String fullPath = f.getAbsolutePath();
		
		if(false)
		{
			if(file.length() > 30 * 1024)
			{
				try 
				{
					Log.e("Photo file", file.getPath());
					Log.e("APhoto file", file.getAbsolutePath());
					Uri imgUri = Uri.fromFile(file);
		
					if (imgUri.toString().startsWith("file://")) {
					   //ten = ten.substring(11);
					        }
		
				   Bitmap mBitmap = BitmapFactory.decodeFile(file.getPath());
				   ByteArrayOutputStream bos = new ByteArrayOutputStream();
				   //resized.compress(CompressFormat.JPEG, 0, bos);
				   mBitmap.compress(CompressFormat.JPEG, 100, bos);
				   byte[] bitmapdata = bos.toByteArray();
							   		
				   	//cursorImage.getString(2);
				   AppDir = Environment.getExternalStorageDirectory().getPath()
					+ "/SBDocs/Photos_Compressed" + "/" + utildb.getSdoCode() + "/"
					+ utildb.getActiveMRU();
					f = new File(AppDir);
					fullPath = f.getAbsolutePath();
	
				    Log.e("AppDir file",AppDir);
					
					File dir = new File(fullPath);
					if (!dir.exists()) 
					{
						dir.mkdirs();
					}
					Log.e("AppDir fullPath",fullPath);
					File compfile = new File(fullPath, strName);
					
					FileOutputStream fileOutputStream = null;
					BitmapFactory.Options options=new BitmapFactory.Options();
					options.inSampleSize = 1;  
					Bitmap myImage = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length,options);
					fileOutputStream = new FileOutputStream( fullPath + "/" + strName);
					BufferedOutputStream bos1 = new BufferedOutputStream(fileOutputStream);
					myImage.compress(CompressFormat.JPEG, 100, bos1);
					bos1.flush();
					bos1.close();
					
				}
			
				catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
        utildb.UpdateCompressedImage(strCANo);
   
        //Temporary Code Added
        AppDir = Environment.getExternalStorageDirectory().getPath()
				+ "/SBDocs/Photos_Crop" + "/" + utildb.getSdoCode() + "/"
				+ utildb.getActiveMRU();
		f = new File(AppDir);
		fullPath = f.getAbsolutePath();
		//Temporary Code Added
		
		
    	Log.e("AsyncImage Call"," Started");
		String credentials[] = new String[6];
		credentials[0] = strCANo;
		credentials[1] = strName.substring(4, 6);
		credentials[2] = strName.substring(0, 4);
		credentials[3] = utildb.getSdoCode();
		credentials[4] = fullPath + "/" + strName;
		credentials[5] = utildb.getActiveMRU();
		Log.e("processImage", "Completed");
		return credentials;

		
		//return null;		
	}

}
