package org.cso.MobileSpotBilling;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.cso.MSBAsync.AsyncBluetoothReading;
import org.cso.MSBAsync.AsyncGetOutputData;
import org.cso.MSBAsync.AsyncGetUserInfo;
import org.cso.MSBAsync.AsyncImage;
import org.cso.MSBAsync.AsyncUnuploadedImage;
import org.cso.MSBAsync.AsyncUpdatePoleMobile;
import org.cso.MSBModel.StructOutput;
import org.cso.MSBModel.StructSAPInput;
import org.cso.MSBUtil.AppUtil;
import org.cso.MSBUtil.GPSLocation;
import org.cso.MSBUtil.GPSTracker;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.AlreadyConnectedException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.cso.MobileSpotBilling.R;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.SpotColor;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.text.Layout;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

// import for android printer
import android.os.Bundle;
import android.app.Activity;
import java.io.*;
import android.view.Menu;
import java.lang.reflect.Method;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActvBilling extends Activity implements OnClickListener, TaskCallback {
	Context context = this;
	String currentStatus;
	double currentReading;
	private String mobileno="";
	public static String taid;
	private static String gLine;
	private static String gLine1;
	private static String gLine2;
	private String gLine3;
	private String gLine4;
	private static int BillMonth;
	
	public String strMobileNo = "", strPoleNo = "";

	static String  photoAddressSaved=null;
	static String photoFolderSaved=null;

	private int BillYear,consadd,billprint;
	
	private static int BillYear2;
	private static int gDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
			30, 31 };
	public static String unimonth[] = { " ", "Jan", "Feb", "Mar", "Apr", "May",
			"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	public static String bimonth[] = { " ", "Dec-Jan", "Jan-Feb", "Feb-Mar",
			"Mar-Apr", "Apr-May", "May-Jun", "Jun-Jul", "Jul-Aug", "Aug-Sep",
			"Sep-Oct", "Oct-Nov", "Nov-Dec" };
	
	private static String date1;
	private String date2;
	private long gUTBB;
	private long gNBM;
	private float gNBM_FC;
	private long gNBD;
	private long gLoad;
	private double gMaxDem;
	private int UBSrNo;
	public static double prvrdg;
	public static String prvrdgdt,prvrdgdt_fc;
	public static String prvmtrsts;
	private int FC4nxt60dys;
	private long OLD_MTR_CSPT;
	private long CUR_MTR_CSPT = 0;
	private int MulbyNBM;
	private static int RC;
	private static int SC;
	private int Cycle;
	
	private int gCheckProvisionalToDefective;
	private int nxtbilprcs;

    private int gch=0;
	private int check = 0;
	private int gDCSTS;
	public static int gMD;

	public static String uploadStatus;

	public static String pSlab1 = "";
	public static String pSlab2 = "";
	public static String pSlab3 = "";
	public static String pSlab4 = "";

	int alreadyBilled = 0;
	boolean chkpdc = false;
	//private static Creator

	// ////////////////New Tariff and Slab Introduced in April 2012
	// ////////////////////////

	// ////////Old tariff on RST10-11 /////////////////
	private double gTariffRate_1011[][][] = {
			// fixed charge energy charge//
			/* DOM */{ { 20.00, 15.00, 0, 30.00 }, { 1.40, 3.10, 4.10, 0 } },
			/* COM */{ { 30.00, 25.00, 0, 0 }, { 4.20, 5.30, 5.90, 0 } },
			/* PI */{ { 50.00, 0, 0, 0 }, { 4.20, 0, 0, 0 } }, };

	// ////////Old tariff on RST11-12 /////////////////
	private double gTariffRate_1112[][][] = {
			// fixed charge energy charge//
			/* DOM */{ { 20.00, 15.00, 0, 30.00 }, { 1.40, 3.50, 4.30, 4.80 } },
			/* COM */{ { 30.00, 25.00, 0, 0 }, { 4.80, 5.90, 6.60, 0 } },
			/* PI */{ { 50.00, 0, 0, 0 }, { 4.80, 0, 0, 0 } }, };
	private int gSlablim_1112[][] = { { 50, 200, 400 }, { 100, 300, 10000000 } };

	// ///////// New tariff on RST12-13 ////////////////
	private double gTariffRate_1213[][][] = {
			// fixed charge energy charge//
			/* DOM */{ { 20.00, 15.00, 0, 60.00 }, { 2.20, 3.90, 4.90, 5.30 } },
			/* COM */{ { 30.00, 25.00, 0, 0 }, { 5.00, 6.10, 6.80, 0 } },
			/* PI */{ { 50.00, 0, 0, 0 }, { 5.30, 0, 0, 0 } }, };
	private int gSlablim_1213[][] = { { 50, 200, 400 }, { 100, 300, 10000000 } };

	private double gTariffRate_1314[][][] = {
			// fixed charge energy charge//
			/* DOM */{ { 20.00, 20.00, 0, 65.00 }, { 2.30, 4.00, 5.00, 5.40 } },
			/* COM */{ { 30.00, 30.00, 0, 0 }, { 5.10, 6.20, 6.90, 0 } },
			/* PI */{ { 50.00, 0, 0, 0 }, { 5.40, 0, 0, 0 } }, };
	private int gSlablim_1314[][] = { { 50, 200, 400 }, { 100, 300, 10000000 } };

	private double gTariffRate_1516[][][] = {
			// fixed charge energy charge//
			/* DOM */{ { 25.00, 25.00, 0, 75.00 }, { 2.50, 4.50, 5.50, 5.90 } },
			/* COM */{ { 40.00, 40.00, 0, 0 }, { 5.50, 6.70, 7.40, 0 } },
			/* PI */{ { 50.00, 0, 0, 0 }, { 5.40, 0, 0, 0 } }, };
	private int gSlablim_1516[][] = { { 50, 200, 400 }, { 100, 300, 10000000 } };
	

	
	private static String gSoftwareVersion;
	private int val;
	int Round = 0;
	long RoundRdg = 0;
	String billby = null;
	String photoId = null;
	String photoTakenTime = null;
	// Camera Variables
	File file, cropfile, unUploadedFile;
	private static final int PIC_CROP = 200;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		gSoftwareVersion = "Mob " + AppUtil.GetVersion(this);
		System.out.println("blImageCapture Pre => " + UtilAppCommon.blImageCapture);
		UtilAppCommon.inSAPMsgID = "";
		//Log.e("onCreate", UtilAppCommon.bBtnGenerateClicked + "");
		if (UtilAppCommon.bBtnGenerateClicked) {
			Intent in = getIntent();
			boolean flblue = in.getBooleanExtra("flblue", false);
			System.out.println("flblue  => " + flblue);
			if(UtilAppCommon.blImageCapture)	{
				UtilAppCommon.blImageCapture = false;
				System.out.println("blImageCapture Post => " + UtilAppCommon.blImageCapture);
				postCaptureImage();
			}
			else if(flblue)	{
				captureImage();
			}
			else if (!validateDoubleBilling()) {

				AsyncUnuploadedImage asyncUnuploadedImage = new AsyncUnuploadedImage(this);
				asyncUnuploadedImage.execute(AppUtil.unUploadedFilePath(this));

				showLayout(); // displays consumer details n allow users to
								// proceed
				UtilAppCommon.bBtnGenerateClicked = false;
			}
		} else {
			//startActivity(new Intent(this, ActvivityMain.class));
			//if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1"))
			//	startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
			//else
			if(UtilAppCommon.billType.equalsIgnoreCase("A"))
				startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
				startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
				startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
				startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
			else
				startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
			UtilAppCommon.inSAPSendMsg = "";
			finish();
		}

	}

	/*private boolean validateDoubleBilling() {

		Log.e("validateDoubleBilling", "Start");
		boolean bIsDblBilled = false;
		boolean bIsBlueDblBilled = false;
		
		UtilDB utilsTochkDb = new UtilDB(getBaseContext());

		final Intent intentdupill = new Intent(this,
				ActvBillPrinting.class);
		String lsAccountNo = "";
		
		//if(UtilAppCommon.billType.equals("L"))
		//	lsAccountNo = UtilAppCommon.legNbr;
		//else
			lsAccountNo = UtilAppCommon.acctNbr;
		
		Log.e("AccountNo", lsAccountNo);
		try {
			if (utilsTochkDb.checkDoubleBilling(lsAccountNo)) {
				UtilDB util1 = new UtilDB(getBaseContext());
				util1.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number");
				util1.close();
					
				bIsDblBilled = true;
				AlertDialog.Builder altDialog = new AlertDialog.Builder(
						ActvBilling.this);
				altDialog.setTitle(" Consumer Already Billed");
				altDialog.setMessage("Do You Want Duplicate Bill "); // here
																	 // add
																	 // your
																	 // message
				altDialog.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								//
								UtilAppCommon.acctNbr = String.format("%s", UtilAppCommon.acctNbr);
								UtilAppCommon.bprintdupl = true;
								
								// 23.12.13 for duplicate bill printing
								UtilDB utilDupBillPrint = new UtilDB(getBaseContext());
								utilDupBillPrint.getOutputBillRecord(UtilAppCommon.acctNbr);
								utilDupBillPrint.getSAPBlueInput(UtilAppCommon.acctNbr);
								startActivity(intentdupill);

							}
						});

				altDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// altDialog.
								startActivity(new Intent(getBaseContext(),
										ActvConsumerNbrInput.class));
								finish();
							}
						});
				altDialog.show();
				utilsTochkDb.close();
			}
			else
			{
				UtilDB util = new UtilDB(getBaseContext());
				util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number");
				util.close();
				//getImageByCANo(lsAccountNo);
				//Bluetooth Meter Reading
				Cursor cursorBlue = utilsTochkDb.checkDoubleinSAPBlueInput(lsAccountNo);
				if (cursorBlue != null)
				{
					bIsBlueDblBilled = true;
					String strMsgId = "";
					//Log.e("MESSAGE", cursor.getString(cursor.getColumnIndex("MSGID")));
					if(cursorBlue.getString(cursorBlue.getColumnIndex("MSGID")) != null)
					{
						strMsgId = cursorBlue.getString(cursorBlue.getColumnIndex("MSGID"));
					}
					String[] copySAPInputData = new String[12];
					String nxtDate;
					try {
						copySAPInputData[0] = cursorBlue.getString(1);
						copySAPInputData[1] = cursorBlue.getString(2);
						copySAPInputData[2] = UtilAppCommon.in.MONTH_SEASONAL; // cursorBlue.getString(3);
						copySAPInputData[3] = cursorBlue.getString(4);
						copySAPInputData[4] = cursorBlue.getString(5);
						copySAPInputData[5] = cursorBlue.getString(6);
						copySAPInputData[6] = cursorBlue.getString(7);
						copySAPInputData[7] = cursorBlue.getString(8);
						copySAPInputData[8] = cursorBlue.getString(9);
						copySAPInputData[9] = cursorBlue.getString(10);
                        copySAPInputData[11] = cursorBlue.getString(0);
						if(strMsgId.equalsIgnoreCase("0") || strMsgId.equalsIgnoreCase("2"))
						{
                            copySAPInputData[10] = "2";
							UtilAppCommon.inSAPSendMsg = "2";
						}
						else
						{
                            copySAPInputData[10] = "2";
							UtilAppCommon.inSAPSendMsg = "1";
						}

						//Log.e("SCHEDULED_BILLING_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						Log.e("validateDoubleBill Copy", e1.getMessage());
					}

					UtilDB utilDB = new UtilDB(getApplicationContext());
					try {

						utilDB.getSAPBlueInput(UtilAppCommon.acctNbr);
						//UtilAppCommon.SAPBlueIn = new StructSAPInput();
						//UtilAppCommon.cop(copySAPInputData);
						//if(NetworkUtil.isOnline(ActvBilling.this,null))
						{

							// 20.11.15
							AsyncBluetoothReading asyncBluetoothReading = new AsyncBluetoothReading(this);
							asyncBluetoothReading.execute(copySAPInputData);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("AsyncOut ActBill", e.getMessage());
					}
				}

				//Normal Meter Reading
				Cursor cursor = utilsTochkDb.checkDoubleinSAPInput(lsAccountNo);
				if (cursor != null)
				{
					bIsDblBilled = true;
					String strMsgId = "";
					//Log.e("MESSAGE", cursor.getString(cursor.getColumnIndex("MSGID")));
					if(cursor.getString(cursor.getColumnIndex("MSGID")) != null)
					{
						strMsgId = cursor.getString(cursor.getColumnIndex("MSGID"));
					}
					String[] copySAPInputData = new String[14];
					String nxtDate;
					try {
						copySAPInputData[0] = cursor.getString(0);
						copySAPInputData[1] = cursor.getString(1);
						copySAPInputData[2] = cursor.getString(9);
						copySAPInputData[3] = cursor.getString(10);	
						copySAPInputData[4] = cursor.getString(5);
						copySAPInputData[5] = cursor.getString(7);
						copySAPInputData[6] = cursor.getString(8);
						copySAPInputData[7] = cursor.getString(4);
						copySAPInputData[8] = cursor.getString(3);
						copySAPInputData[9] = cursor.getString(2);
						copySAPInputData[10] = cursor.getString(12);
						copySAPInputData[12] = "0";
						copySAPInputData[13] = cursor.getString(6);
						if(strMsgId.equalsIgnoreCase("1") || strMsgId.equalsIgnoreCase("2") || strMsgId.equalsIgnoreCase("7") || strMsgId.equalsIgnoreCase("3"))
						{
							copySAPInputData[11] = "2";
							UtilAppCommon.inSAPSendMsg = "2";
						}
						else
						{
							copySAPInputData[11] = "1";
							UtilAppCommon.inSAPSendMsg = "1";
						}
						
						Log.e("SCHEDULED_BILLING_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						Log.e("validateDoubleBill Copy", e1.getMessage());
					}
	
					//UtilDB utilDB = new UtilDB(getApplicationContext());
					try {					
						
						//utilDB.insertIntoSAPInput(copySAPInputData);
						UtilAppCommon.SAPIn = new StructSAPInput();
						UtilAppCommon.copySAPInputData(copySAPInputData);
							//if(NetworkUtil.isOnline(ActvBilling.this,null))
							{
						
								 // 20.11.15
									AsyncGetOutputData asyncGetOutputData = new AsyncGetOutputData(this,new OnBillGenerate() {
									
									@Override
									public void onFinish() {
										// TODO Auto-generated method stub
										
										printdlg();
										
									}
								});
								
								asyncGetOutputData.execute(copySAPInputData);
							}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("AsyncOut ActBill", e.getMessage());
					}
				}
				if(bIsDblBilled && bIsBlueDblBilled)
				{
					try {
						//if(NetworkUtil.isOnline(ActvBilling.this,null))
						{
							util.copyToOutputStruct(UtilAppCommon.SAPIn.CANumber);
						}
						//printbill();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("ActBill copyOut E", e.getMessage());
					}

					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("InterruptedException E", e.getMessage());
					}

					//if(UtilAppCommon.SAPIn.ProcessedFlag.equals("1"))
					{
						// need to be change for photo
						getImageByCANo(UtilAppCommon.acctNbr);
						final AlertDialog ad = new AlertDialog.Builder(this)
								.create();
						ad.setTitle("Confirm");
						ad.setMessage("Confirm to print");
						ad.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										ad.dismiss();
										Write2SbmOut();
										//startActivity(new Intent(ctx, ActvBillPrinting.class));
									}
								});
						ad.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										ad.dismiss();
										// startActivity(getIntent());
										//startActivity(new Intent(getApplicationContext(), ActvBillingOption.class));
										//startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
										if(UtilAppCommon.billType.equalsIgnoreCase("A"))
											startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
										else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
											startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
										else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
											startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
										else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
											startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
										else
											startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
									}
								});
						ad.show();
					}
					//printbill();
				}
				//bIsDblBilled = false;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("validateDoubleBilling E", e.getMessage());
		}
		//Log.e("validateDoubleBilling", "End");
		return bIsDblBilled;
	}*/


	/*private boolean validateDoubleBilling() {
		Log.e("*********"+this.getClass().getSimpleName()+"==>>", "****==>"+Thread.currentThread().getStackTrace()[2].getMethodName());
		Log.e("validateDoubleBilling", "Start");
		boolean bIsDblBilled = false;

		UtilDB utilsTochkDb = new UtilDB(getBaseContext());

		final Intent intentdupill = new Intent(this,
				ActvBillPrinting.class);
		String lsAccountNo = "";

		//if(UtilAppCommon.billType.equals("L"))
		//	lsAccountNo = UtilAppCommon.legNbr;
		//else
			lsAccountNo = UtilAppCommon.acctNbr;

		Log.e("AccountNo", lsAccountNo);
		try {
			if (utilsTochkDb.checkDoubleBilling(lsAccountNo)) {
				UtilDB util1 = new UtilDB(getBaseContext());
				util1.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number");
				util1.close();

				bIsDblBilled = true;
				AlertDialog.Builder altDialog = new AlertDialog.Builder(
						ActvBilling.this);
				altDialog.setTitle(" Consumer Already Billed");
				altDialog.setMessage("Do You Want Duplicate Bill "); // here
																	 // add
																	 // your
																	 // message
				altDialog.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								//
								UtilAppCommon.acctNbr = String.format("%s", UtilAppCommon.acctNbr);
								UtilAppCommon.bprintdupl = true;

								// 23.12.13 for duplicate bill printing
								UtilDB utilDupBillPrint = new UtilDB(getBaseContext());
								utilDupBillPrint.getOutputBillRecord(UtilAppCommon.acctNbr);
								utilDupBillPrint.getSAPBlueInput(UtilAppCommon.acctNbr);
								startActivity(intentdupill);

							}
						});

				altDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// altDialog.
								startActivity(new Intent(getBaseContext(),
										ActvConsumerNbrInput.class));
								finish();
							}
						});
				altDialog.show();
				utilsTochkDb.close();
			}
			else
			{
				UtilDB util = new UtilDB(getBaseContext());
				util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number");
				util.close();
				//getImageByCANo(lsAccountNo);
				//Bluetooth Meter Reading
				Cursor cursorBlue = utilsTochkDb.checkDoubleinSAPBlueInput(lsAccountNo);
				if (cursorBlue != null)
				{
					Log.e("validateDoubleBilling", " cursor Blue Not Null");
					bIsDblBilled = true;
					String strMsgId = "";
					//Log.e("MESSAGE", cursor.getString(cursor.getColumnIndex("MSGID")));
					if(cursorBlue.getString(cursorBlue.getColumnIndex("MSGID")) != null)
					{
						strMsgId = cursorBlue.getString(cursorBlue.getColumnIndex("MSGID"));
					}
					String[] copySAPInputData = new String[12];
					String nxtDate;
					try {
						copySAPInputData[0] = cursorBlue.getString(1);
						copySAPInputData[1] = cursorBlue.getString(2);
						copySAPInputData[2] = UtilAppCommon.in.MONTH_SEASONAL; // cursorBlue.getString(3);
						copySAPInputData[3] = cursorBlue.getString(4);
						copySAPInputData[4] = cursorBlue.getString(5);
						copySAPInputData[5] = cursorBlue.getString(6);
						copySAPInputData[6] = cursorBlue.getString(7);
						copySAPInputData[7] = cursorBlue.getString(8);
						copySAPInputData[8] = cursorBlue.getString(9);
						copySAPInputData[9] = cursorBlue.getString(10);
                        copySAPInputData[11] = cursorBlue.getString(0);
						if(strMsgId.equalsIgnoreCase("0") || strMsgId.equalsIgnoreCase("2"))
						{
                            copySAPInputData[10] = "2";
							UtilAppCommon.inSAPSendMsg = "2";
						}
						else
						{
                            copySAPInputData[10] = "2";
							UtilAppCommon.inSAPSendMsg = "1";
						}

						//Log.e("SCHEDULED_BILLING_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						Log.e("validateDoubleBill Copy", e1.getMessage());
					}

					UtilDB utilDB = new UtilDB(getApplicationContext());
					try {

						utilDB.getSAPBlueInput(UtilAppCommon.acctNbr);
						//UtilAppCommon.SAPBlueIn = new StructSAPInput();
						//UtilAppCommon.cop(copySAPInputData);
						//if(NetworkUtil.isOnline(ActvBilling.this,null))
						{

							// 20.11.15
							AsyncBluetoothReading asyncBluetoothReading = new AsyncBluetoothReading(this);
							asyncBluetoothReading.execute(copySAPInputData);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("AsyncOut ActBill", e.getMessage());
					}
				}

				//Normal Meter Reading
				Cursor cursor = utilsTochkDb.checkDoubleinSAPInput(lsAccountNo);
				if (cursor != null)
				{
					bIsDblBilled = true;
					String strMsgId = "";
					//Log.e("MESSAGE", cursor.getString(cursor.getColumnIndex("MSGID")));
					if(cursor.getString(cursor.getColumnIndex("MSGID")) != null)
					{
						strMsgId = cursor.getString(cursor.getColumnIndex("MSGID"));
					}
					String[] copySAPInputData = new String[14];
					String nxtDate;
					try {
						copySAPInputData[0] = cursor.getString(0);
						copySAPInputData[1] = cursor.getString(1);
						copySAPInputData[2] = cursor.getString(9);
						copySAPInputData[3] = cursor.getString(10);
						copySAPInputData[4] = cursor.getString(5);
						copySAPInputData[5] = cursor.getString(7);
						copySAPInputData[6] = cursor.getString(8);
						copySAPInputData[7] = cursor.getString(4);
						copySAPInputData[8] = cursor.getString(3);
						copySAPInputData[9] = cursor.getString(2);
						copySAPInputData[10] = cursor.getString(12);
						copySAPInputData[12] = "0";
						copySAPInputData[13] = cursor.getString(6);
						if(strMsgId.equalsIgnoreCase("1") || strMsgId.equalsIgnoreCase("2") || strMsgId.equalsIgnoreCase("7") || strMsgId.equalsIgnoreCase("3"))
						{
							copySAPInputData[11] = "2";
							UtilAppCommon.inSAPSendMsg = "2";
						}
						else
						{
							copySAPInputData[11] = "1";
							UtilAppCommon.inSAPSendMsg = "1";
						}

						Log.e("SCHEDULED_BILLING_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						Log.e("validateDoubleBill Copy", e1.getMessage());
					}

					//UtilDB utilDB = new UtilDB(getApplicationContext());
					try {

						//utilDB.insertIntoSAPInput(copySAPInputData);
						UtilAppCommon.SAPIn = new StructSAPInput();
						UtilAppCommon.copySAPInputData(copySAPInputData);
							//if(NetworkUtil.isOnline(ActvBilling.this,null))
							{

								 // 20.11.15
									AsyncGetOutputData asyncGetOutputData = new AsyncGetOutputData(this,new OnBillGenerate() {

									@Override
									public void onFinish() {
										// TODO Auto-generated method stub

										printdlg();

									}
								});

								asyncGetOutputData.execute(copySAPInputData);
							}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("AsyncOut ActBill", e.getMessage());
					}
				}
				if(bIsDblBilled)
				{
					try {
						//if(NetworkUtil.isOnline(ActvBilling.this,null))
						{
							util.copyToOutputStruct(UtilAppCommon.SAPIn.CANumber);
						}
						//printbill();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("ActBill copyOut E", e.getMessage());
					}

					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("InterruptedException E", e.getMessage());
					}

					//if(UtilAppCommon.SAPIn.ProcessedFlag.equals("1"))
					{
						// need to be change for photo
						getImageByCANo(UtilAppCommon.acctNbr);
						final AlertDialog ad = new AlertDialog.Builder(this)
								.create();
						ad.setTitle("Confirm");
						ad.setMessage("Confirm to print");
						ad.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										ad.dismiss();
										Write2SbmOut();
										//startActivity(new Intent(ctx, ActvBillPrinting.class));
									}
								});
						ad.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										ad.dismiss();
										// startActivity(getIntent());
										//startActivity(new Intent(getApplicationContext(), ActvBillingOption.class));
										//startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
										if(UtilAppCommon.billType.equalsIgnoreCase("A"))
											startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
										else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
											startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
										else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
											startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
										else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
											startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
										else
											startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
									}
								});
						ad.show();
					}
					//printbill();
				}
				//bIsDblBilled = false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("validateDoubleBilling E", e.getMessage());
		}
		//Log.e("validateDoubleBilling", "End");
		return bIsDblBilled;
	}
*/

	//This one checks if billo/p is not having value but SAPinput and SAPBlue input has value, then it resends
	// the data to middleware accordingly follow whole process
	private boolean validateDoubleBilling() {

		Log.e("validateDoubleBilling", "Start");
		boolean bIsDblBilled = false;
		boolean bIsBlueDblBilled = false;

		UtilDB utilsTochkDb = new UtilDB(getBaseContext());

		final Intent intentdupill = new Intent(this,
				ActvBillPrinting.class);
		String lsAccountNo = "";

		//if(UtilAppCommon.billType.equals("L"))
		//	lsAccountNo = UtilAppCommon.legNbr;
		//else
		lsAccountNo = UtilAppCommon.acctNbr;

		Log.e("AccountNo", lsAccountNo);
		try {
			if (utilsTochkDb.checkDoubleBilling(lsAccountNo)) {
				UtilDB util1 = new UtilDB(getBaseContext());
				util1.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number");
				util1.close();

				bIsDblBilled = true;
				AlertDialog.Builder altDialog = new AlertDialog.Builder(
						ActvBilling.this);
				altDialog.setTitle(" Consumer Already Billed");
				altDialog.setMessage("Do You Want Duplicate Bill "); // here
				// add
				// your
				// message
				altDialog.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								//
								UtilAppCommon.acctNbr = String.format("%s", UtilAppCommon.acctNbr);
								UtilAppCommon.bprintdupl = true;

								// 23.12.13 for duplicate bill printing
								UtilDB utilDupBillPrint = new UtilDB(getBaseContext());
								utilDupBillPrint.getOutputBillRecord(UtilAppCommon.acctNbr);
								utilDupBillPrint.getSAPBlueInput(UtilAppCommon.acctNbr);
								startActivity(intentdupill);

							}
						});

				altDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// altDialog.
								startActivity(new Intent(getBaseContext(),
										ActvConsumerNbrInput.class));
								finish();
							}
						});
				altDialog.show();
				utilsTochkDb.close();
			}
			else
			{
				UtilDB util = new UtilDB(getBaseContext());
				util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number");
				util.close();
				//getImageByCANo(lsAccountNo);
				//Bluetooth Meter Reading
				Cursor cursorBlue = utilsTochkDb.checkDoubleinSAPBlueInput(lsAccountNo);
				if (cursorBlue != null)
				{
					bIsBlueDblBilled = true;
					String strMsgId = "";
					//Log.e("MESSAGE", cursor.getString(cursor.getColumnIndex("MSGID")));
					if(cursorBlue.getString(cursorBlue.getColumnIndex("MSGID")) != null)
					{
						strMsgId = cursorBlue.getString(cursorBlue.getColumnIndex("MSGID"));
					}
					String[] copySAPInputData = new String[12];
					String nxtDate;
					try {
						copySAPInputData[0] = cursorBlue.getString(1);
						copySAPInputData[1] = cursorBlue.getString(2);
						copySAPInputData[2] = UtilAppCommon.in.MONTH_SEASONAL; // cursorBlue.getString(3);
						copySAPInputData[3] = cursorBlue.getString(4);
						copySAPInputData[4] = cursorBlue.getString(5);
						copySAPInputData[5] = cursorBlue.getString(6);
						copySAPInputData[6] = cursorBlue.getString(7);
						copySAPInputData[7] = cursorBlue.getString(8);
						copySAPInputData[8] = cursorBlue.getString(9);
						copySAPInputData[9] = cursorBlue.getString(10);
						copySAPInputData[11] = cursorBlue.getString(0);
						if(strMsgId.equalsIgnoreCase("0") || strMsgId.equalsIgnoreCase("2"))
						{
							copySAPInputData[10] = "2";
							UtilAppCommon.inSAPSendMsg = "2";
						}
						else
						{
							copySAPInputData[10] = "2";
							UtilAppCommon.inSAPSendMsg = "1";
						}

						//Log.e("SCHEDULED_BILLING_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						Log.e("validateDoubleBill Copy", e1.getMessage());
					}

					UtilDB utilDB = new UtilDB(getApplicationContext());
					try {

						utilDB.getSAPBlueInput(UtilAppCommon.acctNbr);
						//UtilAppCommon.SAPBlueIn = new StructSAPInput();
						//UtilAppCommon.cop(copySAPInputData);
						//if(NetworkUtil.isOnline(ActvBilling.this,null))
						{

							// 20.11.15
							AsyncBluetoothReading asyncBluetoothReading = new AsyncBluetoothReading(this);
							asyncBluetoothReading.execute(copySAPInputData);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("AsyncOut ActBill", e.getMessage());
					}
				}

				//Normal Meter Reading
				Cursor cursor = utilsTochkDb.checkDoubleinSAPInput(lsAccountNo);
				if (cursor != null)
				{
					bIsDblBilled = true;
					String strMsgId = "";
					//Log.e("MESSAGE", cursor.getString(cursor.getColumnIndex("MSGID")));
					if(cursor.getString(cursor.getColumnIndex("MSGID")) != null)
					{
						strMsgId = cursor.getString(cursor.getColumnIndex("MSGID"));
					}
					String[] copySAPInputData = new String[14];
					String nxtDate;
					try {
						copySAPInputData[0] = cursor.getString(0);
						copySAPInputData[1] = cursor.getString(1);
						copySAPInputData[2] = cursor.getString(9);
						copySAPInputData[3] = cursor.getString(10);
						copySAPInputData[4] = cursor.getString(5);
						copySAPInputData[5] = cursor.getString(7);
						copySAPInputData[6] = cursor.getString(8);
						copySAPInputData[7] = cursor.getString(4);
						copySAPInputData[8] = cursor.getString(3);
						copySAPInputData[9] = cursor.getString(2);
						copySAPInputData[10] = cursor.getString(12);
						copySAPInputData[12] = "0";
						copySAPInputData[13] = cursor.getString(6);
						if(strMsgId.equalsIgnoreCase("1") || strMsgId.equalsIgnoreCase("2") || strMsgId.equalsIgnoreCase("7") || strMsgId.equalsIgnoreCase("3"))
						{
							copySAPInputData[11] = "2";
							UtilAppCommon.inSAPSendMsg = "2";
						}
						else
						{
							copySAPInputData[11] = "1";
							UtilAppCommon.inSAPSendMsg = "1";
						}

						Log.e("SCHEDULED_BILLING_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						Log.e("validateDoubleBill Copy", e1.getMessage());
					}

					//UtilDB utilDB = new UtilDB(getApplicationContext());
					try {

						//utilDB.insertIntoSAPInput(copySAPInputData);
						UtilAppCommon.SAPIn = new StructSAPInput();
						UtilAppCommon.copySAPInputData(copySAPInputData);
						//if(NetworkUtil.isOnline(ActvBilling.this,null))
						{

							// 20.11.15
							AsyncGetOutputData asyncGetOutputData = new AsyncGetOutputData(this,new OnBillGenerate() {

								@Override
								public void onFinish() {
									// TODO Auto-generated method stub

									printdlg();

								}
							});

							asyncGetOutputData.execute(copySAPInputData);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("AsyncOut ActBill", e.getMessage());
					}
				}
				if(bIsDblBilled && bIsBlueDblBilled)
				{
					try {
						//if(NetworkUtil.isOnline(ActvBilling.this,null))
						{
							util.copyToOutputStruct(UtilAppCommon.SAPIn.CANumber);
						}
						//printbill();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("ActBill copyOut E", e.getMessage());
					}

					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("InterruptedException E", e.getMessage());
					}

					//if(UtilAppCommon.SAPIn.ProcessedFlag.equals("1"))
					{
						// need to be change for photo
						getImageByCANo(UtilAppCommon.acctNbr);
						final AlertDialog ad = new AlertDialog.Builder(this)
								.create();
						ad.setTitle("Confirm");
						ad.setMessage("Confirm to print");
						ad.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										ad.dismiss();
										Write2SbmOut();
										//startActivity(new Intent(ctx, ActvBillPrinting.class));
									}
								});
						ad.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										ad.dismiss();
										// startActivity(getIntent());
										//startActivity(new Intent(getApplicationContext(), ActvBillingOption.class));
										//startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
										if(UtilAppCommon.billType.equalsIgnoreCase("A"))
											startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
										else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
											startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
										else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
											startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
										else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
											startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
										else
											startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
									}
								});
						ad.show();
					}
					//printbill();
				}
				//bIsDblBilled = false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("validateDoubleBilling E", e.getMessage());
		}
		//Log.e("validateDoubleBilling", "End");
		return bIsDblBilled;
	}

	private void showLayout() {
		UtilDB util = new UtilDB(getBaseContext());
		
		if(UtilAppCommon.billType.equals("L"))
			util.getBillInputDetails(UtilAppCommon.legNbr, "Legacy");
		else
			util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number");
		
		util.close();
		//Log.e("showConsumerDetails", "Call");
		showConsumerDetails();
		//finish();
		//Log.e("showLayout", "Completed");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.home:
			finish();
			Intent intent = new Intent(this, ActvivityMain.class);
			startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			startActivity(intent);
			break;
		}
		return true;
	}

	public void onBackPressed() {

		try {
			file.delete();
		} catch (Exception ex) {
		}
		finish();

		startActivity(new Intent(this, ActvBillingOption.class));
		return;
	}

	public void onClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {

		case R.id.ContinueBtn:
			UtilDB utilDB = new UtilDB(context);
			String strMobile = ((EditText) findViewById(R.id.MobileNoTxt)).getText().toString().trim();
			String strPole = ((EditText) findViewById(R.id.PoleNoTxt)).getText().toString().trim();
			if(strMobile.equalsIgnoreCase(""))
			{
				Toast.makeText(getBaseContext(),"Please enter Mobile No.", Toast.LENGTH_LONG).show();
				return;
			}
			else if(!strMobile.equalsIgnoreCase("") && (!isNumeric(strMobile) || strMobile.length() != 10))
			{
				Toast.makeText(getBaseContext(),"Please enter valid 10 digit mobile number.", Toast.LENGTH_LONG).show();
				return;
			}
			else if(!strMobile.equalsIgnoreCase("") && !(strMobile.startsWith("6") || strMobile.startsWith("7") || strMobile.startsWith("8") || strMobile.startsWith("9")))
			{
				Toast.makeText(getBaseContext(),"Please enter mobile number starting with 6 / 7 / 8 / 9.", Toast.LENGTH_LONG).show();
				return;
			}
			else if(!strMobile.equalsIgnoreCase("") && (utilDB.MobileNumberCheck(strMobile.trim())))
			{
				Toast.makeText(getBaseContext(),"Mobile number already exist.", Toast.LENGTH_LONG).show();
				return;
			}
			else if(!strPole.equalsIgnoreCase("") && (!isNumeric(strPole) || strPole.length() != 10) )
			{
				Toast.makeText(getBaseContext(),"Please enter valid 10 digit pole number.", Toast.LENGTH_LONG).show();
				return;
			}
			else
			{
				UtilDB util = new UtilDB(getApplicationContext());
				//if(UtilAppCommon.inSAPMsgID.contentEquals("6"))
				util.InsertMobPoleConsumer(strMobile, strPole, UtilAppCommon.acctNbr);
				
				strMobileNo = strMobile;
				strPoleNo = strPole;
				Toast.makeText(ActvBilling.this, "Step1 Completed", Toast.LENGTH_SHORT).show();
				GPSTracker gps  = new GPSTracker(ActvBilling.this);
		    	if(gps.canGetLocation()) {
					Toast.makeText(ActvBilling.this, "Step2 Completed", Toast.LENGTH_SHORT).show();
					StartBilling();
				}
		    	else
		    	{
		    		Toast.makeText(getBaseContext(),"Please start GPS location in setting to continue further.", Toast.LENGTH_LONG).show();
		    	}
			}
			this.finish();
			break;
		case R.id.Confirm:
			// show remarks list
			startActivityForResult(new Intent(this, ActvRemarks.class), 5);
			// finish();
			break;
		case R.id.CancelSubmit:
			showLayout();
			break;

		}
	}
	
	public boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Float.parseFloat(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
		  Log.e("IsNumeric", nfe.getMessage());
		  return false; 
	  }  
	  return true;  
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			
			case 1:
				currentStatus = data.getExtras().getString("meterStatus");
				Log.e("currentStatus", currentStatus);
				Log.e("meterStatusId" , data.getExtras().getString("meterStatusId"));
				//Start New Code
				
				  
				 if(data.getExtras().getString("meterStatusId").equalsIgnoreCase("pl") ||
						 data.getExtras().getString("meterStatusId").equalsIgnoreCase("md") ||
						 data.getExtras().getString("meterStatusId").equalsIgnoreCase("rn"))
				 {
					getImageByCANo(UtilAppCommon.acctNbr);
					final AlertDialog ad = new AlertDialog.Builder(this)
								.create();
					ad.setTitle("Confirm");
					ad.setMessage("Confirm to print");
					ad.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
							new DialogInterface.OnClickListener() {
	
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									ad.dismiss();
									Write2SbmOut();
									//startActivity(new Intent(ctx, ActvBillPrinting.class));
								}
							});
					ad.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
							new DialogInterface.OnClickListener() {
	
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									ad.dismiss();
									// startActivity(getIntent());
									//startActivity(new Intent(getApplicationContext(), ActvBillingOption.class));
									//startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
									//if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1"))
									//	startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
									//else 
									if(UtilAppCommon.billType.equalsIgnoreCase("A"))
										startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
									else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
										startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
									else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
										startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
									else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
										startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
									else
										startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
								}
							});
					ad.show();
					//printbill();
				}
				  
				  
				 
				
				else
				{
					//End Of New Code
					Intent intent = new Intent(this, ActvCurrReading.class);
					intent.putExtra("MeterStatus", data.getExtras().getString("meterStatusId"));
					startActivityForResult(intent, 5);
				}
				//}
				break;
			case 4 :
				/*intent = new Intent(this, ActvCurrReading.class);
				intent.putExtra("MeterStatus", data.getExtras().getString("meterStatusId"));
				startActivityForResult(intent, 5);*/
				break;
			case 5:
				final Context ctx = this;
				{
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("InterruptedException E", e.getMessage());
					}
					getImageByCANo(UtilAppCommon.acctNbr);
					// need to be change for photo
					//printbill();
					final AlertDialog ad1 = new AlertDialog.Builder(this)
							.create();
					ad1.setTitle("Confirm");
					ad1.setMessage("Confirm to print");
					ad1.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									ad1.dismiss();
									Write2SbmOut();
									//if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1"))
									//	startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
									//else
										if(UtilAppCommon.billType.equalsIgnoreCase("A"))
										startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
									else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
										startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
									else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
										startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
									else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
										startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
									else
										startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
								}
							});
					ad1.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									ad1.dismiss();
									// startActivity(getIntent());
									//startActivity(new Intent(ctx, ActvBillingOption.class));
									//if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1"))
									//	startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
									//else 
										if(UtilAppCommon.billType.equalsIgnoreCase("A"))
										startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
									else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
										startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
									else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
										startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
									else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
										startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
									else
										startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
									finish();
								}
							});
					ad1.show();
					//printbill();
				}
				//else
				//{
					//Toast.makeText(context, "Output data not available for CA - "+ UtilAppCommon.in.CONTRACT_AC_NO , Toast.LENGTH_LONG).show();
					//startActivity(new Intent(ctx, ActvBillingOption.class));
				//}
					//
				finish();
				break;
				
			case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
				Log.v("ActvBilling", " result After image Capture confirmation "+ resultCode);
				if (resultCode == RESULT_OK) {
					Log.v("ActvBilling", " result ok After image Capture confirmation");
					Timestamp timestamp = new Timestamp(
							System.currentTimeMillis());
					photoTakenTime = String.valueOf(timestamp);
					Toast.makeText(getApplicationContext(),
							"Photo Saved Successfully", Toast.LENGTH_LONG)
							.show();


					UtilDB dbObj = new UtilDB(context);
					String[] printer = dbObj.GetPrinterInfo();
					dbObj.close();
					if (printer[0] != null || printer[1] != null) {
						if (printer[1].compareToIgnoreCase("Analogic Impact") == 0) {
							Log.v("ActvBilling", "After image Capture confirmation billing");
							billing();
							// 11.02.16
						//} else if (printer[1]
						//		.compareToIgnoreCase("Analogic Thermal") == 0) {
						//	billing();
						} else {
							//StoreByteImage();
							//billing();
							Log.v("ActvBilling", "After image Capture confirmation");
							CropImage();
						}
					}
					else
					{

						Toast.makeText(getApplicationContext(), "Please configure printer", Toast.LENGTH_LONG)
								.show();
						finish();
					}

					// Preview image
					// String imageInSD = file.getAbsolutePath();
					// //ImageView imgMtrReadingPic =
					// (ImageView)findViewById(R.id.imgMtrReadingPic);
					// imgMtrReadingPic.setImageBitmap(bitmap);

				}
				if (resultCode == RESULT_CANCELED) {
					// user cancelled Image capture
					Toast.makeText(getApplicationContext(),
							"User cancelled image capture", Toast.LENGTH_SHORT)
							.show();
					file.delete();
					finish();
				}
				break;
			case PIC_CROP:
				//Log.e("PIC_CROP", "Inside ==>> " + resultCode);
				if (resultCode == RESULT_OK) {
					//getImageByCANo(UtilAppCommon.acctNbr);
					Log.i("ActvBilling", " ************Result After Cropping 1********");
					String AppDir = Environment.getExternalStorageDirectory().getPath()
							+ "/SBDocs/Photos_Crop" + "/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/"
							+ UtilAppCommon.in.MRU;
					Log.i("ActvBilling", " ************Result After Cropping 2********");
					String AppUnuploadedDir = Environment.getExternalStorageDirectory().getPath()
							+ "/SBDocs/.PhotosUnuploaded/";
					Log.i("ActvBilling", " ************Result After Cropping 3********");
					File dir = new File(new File(AppUnuploadedDir).getAbsolutePath());
					if (!dir.exists()) {
						dir.mkdirs();
					}
					Log.i("ActvBilling", " ************Result After Cropping 4********");
					String Unuploaded_photoId = UtilAppCommon.in.SUB_DIVISION_CODE + "_"
							+ UtilAppCommon.in.MRU + "_" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(                                                                                                            0, 4) +
							UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
							"_" + UtilAppCommon.in.CONTRACT_AC_NO;
					Log.i("ActvBilling", " ************Result After Cropping 5********");
					File croppedImage = new File(new File(AppDir).getAbsolutePath(), photoId + ".jpg" );
					Log.i("ActvBilling", " ************Result After Cropping 6********");
					unUploadedFile = new File(new File(AppUnuploadedDir).getAbsolutePath(), Unuploaded_photoId + ".jpg");
					Log.i("ActvBilling", " ************Result After Cropping 7********");
					try
					{
						Log.i("ActvBilling", " ************Result After Cropping 8********");
						FileUtils.copyFile(croppedImage, unUploadedFile);
						Log.i("ActvBilling", " ************Result After Cropping 9********");
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					billing();
				} else {
					cropfile.delete();
				}
				break;

			case 6:
				captureImage();
				break;

			}



		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
   public void showSettingsAlert(){
    	GPSTracker gps  = new GPSTracker(ActvBilling.this);
    	
    	if(gps.canGetLocation()){
            
            //UtilAppCommon.strLat = String.valueOf(gps.getLatitude()).substring(0, 5);
            //UtilAppCommon.strLong = String.valueOf(gps.getLongitude()).substring(0, 5);
            
            UtilAppCommon.strLat = String.valueOf(gps.getLatitude());
            UtilAppCommon.strLong = String.valueOf(gps.getLongitude());
 
            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
            
            //UtilAppCommon.strLat = String.valueOf(gps.getLatitude()).substring(0, 5);
            //UtilAppCommon.strLong = String.valueOf(gps.getLongitude()).substring(0, 5);
            UtilAppCommon.strLat = String.valueOf(gps.getLatitude());
            UtilAppCommon.strLong = String.valueOf(gps.getLongitude());
        }
    }
	
	private int billing() {
		// TODO Auto-generated method stub
		long lMul = 0, i, Digits = 0;

		String ch;
		char meterstat;
		long nbd;
		System.out.println("Inside billing method \n");
		//initilize();
		Log.e("preMtrStatus", UtilAppCommon.in.SCHEDULED_BILLING_DATE);
		
		String preMtrStatus = UtilAppCommon.in.PRV_MTR_READING_NOTE;
		Log.e("preMtrStatus", preMtrStatus);
			
		UtilAppCommon.out.CurrentMtrReadingNote = "";
		//System.out.println("previous reading--" + prvrdg);
		// if(prvmtrsts.equals("U"))
		Toast.makeText(ActvBilling.this, "Step8 Completed", Toast.LENGTH_SHORT).show();
		if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("DS-IU") || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IIU") || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IU")
				|| UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("KJ_BPL_UNM") || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("NDS-IU") || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("SS-II")
				|| preMtrStatus.equalsIgnoreCase("rn") || preMtrStatus.equalsIgnoreCase("md"))	{
            Toast.makeText(ActvBilling.this, "Step9 Completed", Toast.LENGTH_SHORT).show();
		    Intent intent = new Intent(this, IntermediateActivity.class);
			startActivityForResult(intent, 5);
		}
		
		else if (preMtrStatus.equalsIgnoreCase("ok") || preMtrStatus.equalsIgnoreCase("pa") || preMtrStatus.equalsIgnoreCase("pl")) {
			// Changed by Dkr
            Toast.makeText(ActvBilling.this, "Step10 Completed", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this,ActvMeterStatusMenu.class);
			//intent.putExtra("strMobileNo",strMobileNo);
			//intent.putExtra("strPoleNo",strPoleNo);
			startActivityForResult(intent, 1);
			//System.out.println("Enter Current Meter Status: ");
		}
		/*else if()
		{
			Intent intent = new Intent(this, IntermediateActivity.class);
			//intent.putExtra("strMobileNo",strMobileNo);
			//intent.putExtra("strPoleNo",strPoleNo);
			startActivityForResult(intent, 5);
		}*/
		else 
		{
            Toast.makeText(ActvBilling.this, "Step11 Completed", Toast.LENGTH_SHORT).show();
			UtilAppCommon.out.CurrentMtrReadingNote = UtilAppCommon.in.PRV_MTR_READING_NOTE;
			final AlertDialog alertDialog = new AlertDialog.Builder(this)
					.create();
			alertDialog.setTitle("Message");
			alertDialog
					.setMessage(String
							.format("Prev Meter Status %s Assigned as Current Meter Status",
									prvmtrsts));
			System.out.println("Prev Mtr Sts Assigned as current mtr sts");
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							alertDialog.dismiss();
						}
					});
				alertDialog.show();
			// upto this

			//NextToMtrSts();
		}
		return 0;
	}
	
	private void Write2SbmOut() {
		// TODO Auto-generated method stub
		Log.e("Write2SbmOut", "Start");
		
		try {
			Log.e("Write2SbmOut", "In Try Start");
			Log.e("Msg -- Id", UtilAppCommon.inSAPMsgID);
			//RC = SC = 0;// Re initialise rc ans sc value
			Log.e("Write2SbmOut", "In Try");
			//finish();
			UtilDB util1 = new UtilDB(this);
			//printbill();
			int cnt;
			cnt = util1.getBillOutputRowCount(UtilAppCommon.acctNbr);
			System.out.println("Output cnt .."+cnt);
			if(cnt != 0)
			{
				util1.getOutputBillRecord(UtilAppCommon.acctNbr);
				startActivity(new Intent(this, ActvBillPrinting.class)); // used to print bill through printer
				finish();
			}
			else
			{
				startActivity(new Intent(this, ActvMsgPrinting.class));
				/*if(UtilAppCommon.inSAPMsgID.toString().equals("4") || UtilAppCommon.inSAPMsgID.toString().equals("5"))
					startActivity(new Intent(this, ActvMsgPrinting.class)); // used to print bill through printer
				else
				{
					String strMsg = "";
					if(UtilAppCommon.inActualSAPMsgID.toString().equals("1"))
						strMsg = "MRO Not Found";
					if(UtilAppCommon.inActualSAPMsgID.toString().equals("2"))
						strMsg = "Reading not uploaded";
					if(UtilAppCommon.inActualSAPMsgID.toString().equals("3"))
						strMsg = "Bill not created";
					else
						strMsg = "Request being process";
					Toast.makeText(getBaseContext(), strMsg + ", please try again after sometime.", Toast.LENGTH_LONG).show();
					startActivity(new Intent(this, ActvBillingOption.class));
				}	*/
				finish();
			}
			// printbill();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("Write2SbmOut E", e.getMessage());
		}
		Log.e("Write2SbmOut", "Completed");
	}
	
  	private void createTableMsg(Document document) throws DocumentException {
		  	Log.e("createTableMsg", "Start");
			Paragraph p = new Paragraph(" ELECTRICITY BILL");
			p.setAlignment("CENTER");
			document.add(p);
			document.add(Chunk.NEWLINE);

			PdfPTable table = new PdfPTable(2);
			table.addCell("COMPANY :");
			table.addCell(UtilAppCommon.in.COM_CODE+"CL");
			table.addCell("   ");
			table.addCell( "   ");
			table.addCell("CA NUMBER");
			table.addCell(UtilAppCommon.in.CONTRACT_AC_NO);

			table.addCell("MRU ");
			table.addCell(UtilAppCommon.in.MRU);
			table.addCell("NAME");
			table.addCell(UtilAppCommon.in.CONSUMER_NAME);
			table.addCell("Category");
			table.addCell(UtilAppCommon.in.RATE_CATEGORY);

			String mtrsts="";
			if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("PL"))
				mtrsts = "House Lock";
			else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("RN"))
				mtrsts = "Reading Not Available";
			else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("MD"))
				mtrsts = "Mtr.Defective";
			else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
					mtrsts = "Actual";
			
			table.addCell("Current Mtr Sts:");
			table.addCell( mtrsts);
			
			if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
			{
			table.addCell("MTR Rdg Dt:");
			table.addCell(UtilAppCommon.SAPIn.MtrReadingDate);

			table.addCell("Current Mtr Rdg(Kwh):");
			table.addCell( UtilAppCommon.SAPIn.CurrentReadingKwh);
			table.addCell("Max Demd:");
			table.addCell( UtilAppCommon.SAPIn.MaxDemd);
			table.addCell("PF:");
			table.addCell( UtilAppCommon.SAPIn.PowerFactor);

			}
			table.addCell("    ");
			table.addCell("       ");
			// table.addCell("ADDR1");
			table.addCell("The Bill could not be generated now.");
			table.addCell("Please collect your bill .");
			
			table.addCell("From "+UtilAppCommon.in.COM_CODE+"CL Website" );
			table.addCell("after 2 days");
				
			table.addCell("Message Id" );
			table.addCell(UtilAppCommon.SAPIn.MsgId);	
			table.addCell("Message " );
			table.addCell(UtilAppCommon.SAPIn.strMsg);	
			
			document.add(table);

			Log.e("createTableMsg", "End");
		}

	private void printbill() {
		// TODO Auto-generated method stub
		Log.e("printBill -- MSG ID", UtilAppCommon.SAPIn.MsgId);
		Document document = new Document();
		String dirpath = Environment.getExternalStorageDirectory().getPath()
				+ "/SBDocs/Pdf";
		File dir = new File(dirpath);
		if (!dir.exists()) {
			dir.mkdirs();
			System.out.println("folder created");
		}
		String filePath = dir + "/" + UtilAppCommon.in.CONTRACT_AC_NO + ".pdf";
				//+ UtilAppCommon.in.MRU + 
		File file = new File(filePath);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();
			if(UtilAppCommon.inSAPMsgID.equalsIgnoreCase("6"))
			{
			   createTable1(document);
			}
			else
				createTableMsg(document);
			document.close();
			// ADDED BY DKR
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("printbill", e.getMessage());
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("printbill", e.getMessage());
		} finally {
			if (document != null) {
				document.close();
				startActivity(new Intent(this, PoleMobileActivity.class));
			}
		}
		startActivity(getIntent());
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		startActivity(intent);

		// startActivity(getIntent());
	}	

	void StartBilling()
	{
		Toast.makeText(ActvBilling.this, "Step3 Completed", Toast.LENGTH_SHORT).show();
		Log.e("StartBilling", "Billing Started");
		gCheckProvisionalToDefective = 0;
		taid = UtilAppCommon.in.RATE_CATEGORY;

		String preMtrStatus = UtilAppCommon.in.PRV_MTR_READING_NOTE;
		try
		{ // show meter status list for valid tariff code

			// Modified on 25-8-2014
			
			//System.out.println(UtilAppCommon.ui.BILL_MONTH.substring(0, 4)	+ "-" + UtilAppCommon.ui.BILL_MONTH.substring(4, 6));
			//Log.e("NXT_SCH_MTR_RDR_DATE", UtilAppCommon.in.NXT_SCH_MTR_RDR_DATE.substring(0, 4) + "-" + UtilAppCommon.in.NXT_SCH_MTR_RDR_DATE.substring(5, 7) + "-" + UtilAppCommon.in.NXT_SCH_MTR_RDR_DATE.substring(8, 10));
			//Log.e("SCH_MTR_RDR_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
			Calendar nxtcal = null, schcal = null;
			Boolean before = false, equal = false, equalbillmth = false;
			Log.e("StartBilling","Reading Nxt Date");
			Toast.makeText(ActvBilling.this, "Step4 Completed", Toast.LENGTH_SHORT).show();
            Toast.makeText(ActvBilling.this, "Next Scheduled MR Date "+UtilAppCommon.in.NXT_SCH_MTR_RDR_DATE, Toast.LENGTH_SHORT).show();
			if(!UtilAppCommon.in.NXT_SCH_MTR_RDR_DATE.trim().equalsIgnoreCase(""))
			{
				Toast.makeText(ActvBilling.this, "Step5 Completed", Toast.LENGTH_SHORT).show();
				Log.e("StartBilling","Inside Nxt Date");
				
				Log.e("NXT_SCH_MTR_RDR_DATE>>", UtilAppCommon.in.NXT_SCH_MTR_RDR_DATE);
				Log.e("SCH_MTR_RDR_DATE>>", UtilAppCommon.in.SCHEDULED_BILLING_DATE);
				
				nxtcal = new GregorianCalendar(
						Integer.parseInt(UtilAppCommon.in.NXT_SCH_MTR_RDR_DATE.substring(0, 4)),
						Integer.parseInt(UtilAppCommon.in.NXT_SCH_MTR_RDR_DATE.substring(5, 7)) - 1, 
						Integer.parseInt(UtilAppCommon.in.NXT_SCH_MTR_RDR_DATE.substring(8, 10)));
				
				schcal = new GregorianCalendar(
						Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4)),
						Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7)) - 1, 
						Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10)));

				Calendar today = Calendar.getInstance();
				
				today = new GregorianCalendar(today.get(Calendar.YEAR), today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH));
				before = today.getTime().before(nxtcal.getTime());
				equal = today.getTime().equals(nxtcal.getTime());				
				equalbillmth = today.after(schcal) && today.before(nxtcal);
				SimpleDateFormat SAPInformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
				
				Log.e("Date Time Equal => ", equal + "");
				Log.e("Format Today => ", SAPInformat.format(today.getTime()));
				Log.e("Nxt => ", SAPInformat.format(nxtcal.getTime()));
				Log.e("Sch => ", SAPInformat.format(schcal.getTime()));
				Log.e("Date eqbm => ", equalbillmth + "");
				Toast.makeText(ActvBilling.this, "Step6 Completed", Toast.LENGTH_SHORT).show();
			}
			Toast.makeText(ActvBilling.this, "Step7 Completed", Toast.LENGTH_SHORT).show();
			Log.e("StartBilling","Done Reading Nxt Date");
			if(UtilAppCommon.in.NXT_SCH_MTR_RDR_DATE.trim().equalsIgnoreCase("") && preMtrStatus.equalsIgnoreCase("md")) {
                Toast.makeText(ActvBilling.this, "Step12 Completed", Toast.LENGTH_SHORT).show();
                billing();
            }
			//else if ((before || equal) && equalbillmth)
			else if (before || equal)
			//else
			{
                Toast.makeText(ActvBilling.this, "Step13 Completed", Toast.LENGTH_SHORT).show();
				UtilDB utilDB = new UtilDB(this.context);
				Log.i("ActvBilling","StartBilling ==>> " + UtilAppCommon.in.MONTH_SEASONAL + "  <<==>> " + utilDB.getSAPBlueInput(UtilAppCommon.in.CONTRACT_AC_NO));
				if(!UtilAppCommon.in.MONTH_SEASONAL.equalsIgnoreCase("") && utilDB.getSAPBlueInput(UtilAppCommon.in.CONTRACT_AC_NO) == null)
				{
                    Toast.makeText(ActvBilling.this, "Step14 Completed", Toast.LENGTH_SHORT).show();
					Intent inBTReading = new Intent(ActvBilling.this, ActivityBT_Reading.class);
					startActivity(inBTReading);
				}
				else
					if (preMtrStatus.equalsIgnoreCase("ok") || preMtrStatus.equalsIgnoreCase("pa") || preMtrStatus.equalsIgnoreCase("pl") || preMtrStatus.equalsIgnoreCase("md")) {
					// Changed by Dkr
                        Toast.makeText(ActvBilling.this, "Step15 Completed", Toast.LENGTH_SHORT).show();
					captureImage();
				}
				else if(preMtrStatus.equalsIgnoreCase("rn")) {
                        Toast.makeText(ActvBilling.this, "Step16 Completed", Toast.LENGTH_SHORT).show();
                        billing();
                    }
			}
		}
		catch(Exception e)
		//Log.e("StartBilling", "Completed");
		{
            Toast.makeText(ActvBilling.this, "Step17 Completed", Toast.LENGTH_SHORT).show();
			Log.e("StartBilling E", e.getMessage());
		}
	}
	
	private void showConsumerDetails() {
		setContentView(R.layout.consumerdetails);
		
		View customerLayout = findViewById(R.id.CustomerDetailsLayout);
		customerLayout.setVisibility(View.VISIBLE);

		Button continueBtn = (Button) findViewById(R.id.ContinueBtn);
		continueBtn.setOnClickListener(this);

		// String consumerId = UtilAppCommon.in.CONS_REF;
		String meterNo = UtilAppCommon.in.METER_MANUFACTURER_SR_NO;
		String name = UtilAppCommon.in.CONSUMER_NAME;
		String tariffCode = UtilAppCommon.in.RATE_CATEGORY;
		//String accNo = UtilAppCommon.in.SUB_DIVISION_CODE + UtilAppCommon.in.MRU + UtilAppCommon.in.CONTRACT_AC_NO;
		String accNo = UtilAppCommon.in.CONTRACT_AC_NO;
		String address = UtilAppCommon.in.ADDRESS;
		String conLoad = UtilAppCommon.in.CONNECTED_LOAD+UtilAppCommon.in.SANC_LOAD;
		String prevMeterStatus = UtilAppCommon.in.PRV_MTR_READING_NOTE + "  (Seq:"+UtilAppCommon.in.ROUTE_SEQUENCE_NO+")";
		String strBlueMeter = UtilAppCommon.in.MONTH_SEASONAL.equalsIgnoreCase("") ? "" : String.valueOf(Integer.parseInt(UtilAppCommon.in.MONTH_SEASONAL));
		strBlueMeter = ", " + strBlueMeter;

		((TextView) findViewById(R.id.AccNoTxt)).setText(accNo);
		((TextView) findViewById(R.id.NameTxt)).setText(name);
		((TextView) findViewById(R.id.AddressTxt)).setText(address);
		((TextView) findViewById(R.id.MeterNoTxt)).setText(meterNo + strBlueMeter);
		((TextView) findViewById(R.id.TariffCodeTxt)).setText(tariffCode);
		((TextView) findViewById(R.id.MaxLoadTxt)).setText(conLoad);
		((TextView) findViewById(R.id.PrevMeterStatusTxt)).setText(prevMeterStatus);
		((EditText) findViewById(R.id.PoleNoTxt)).setText(UtilAppCommon.in.CONNECTED_POLE_NIN_NUMBER);
		((EditText) findViewById(R.id.MobileNoTxt)).setText(UtilAppCommon.in.METER_CAP);
		

		Log.e("showConsumerDetails", "End");
	}

	private long NoOfDigits() {
		long Count = 1;
		long Dig = 0;
		Dig = (long) prvrdg;
		while (Dig != 0) {
			Count *= 10;
			Dig = (Dig / 10);
		}
		return ((Count - 1));
	}

	private double roundDoubleUptoTwoDecimal(double x, int numDecimals) {
		long y = (long) x;
		long i = 0;
		double z, m, q, r;

		z = x - y;
		m = 1;
		for (i = 0; i < numDecimals; i++) {
			m = m * 10;
		}
		q = (double) (z * m);
		r = (double) ((long) (q < 0 ? (q - 0.5) : (q + 0.5)));

		return (double) (y) + (1.0 / m) * r;
	}

	private static long DateDifference(String DateF, String DateT) {

		int dayF = 0, monF = 0, yearF = 0;
		int dayT = 0, monT = 0, yearT = 0, totDays = 0;

		dayF = Integer.parseInt(DateF.substring(6, 8));
		monF = Integer.parseInt(DateF.substring(4, 6));
		yearF = Integer.parseInt(DateF.substring(0, 4));

		dayT = Integer.parseInt(DateT.substring(6, 8));
		monT = Integer.parseInt(DateT.substring(4, 6));
		yearT = Integer.parseInt(DateT.substring(0, 4));

		if (yearT >= yearF)
			totDays = days(yearF, yearT, monF, monT, dayF, dayT);
		else
			totDays = days(yearT, yearF, monT, monF, dayT, dayF);

		return totDays;

	}

	public static long getMonthsBetweenDates(Date startDate, Date endDate) {
		if (startDate.getTime() > endDate.getTime()) {
			Date temp = startDate;
			startDate = endDate;
			endDate = temp;
		}
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		
		Long timediff= endDate.getTime()- startDate.getTime();
		
		long monthsBetween=timediff/Long.parseLong("2592000000");
		return monthsBetween;

	}

	public static int days(int y1, int y2, int m1, int m2, int d1, int d2) {
		int count = 0, i;
		for (i = y1; i < y2; i++) {
			if (i % 4 == 0)
				count += 366;
			else
				count += 365;
		}

		count -= month(m1, y1);
		count -= d1;
		count += month(m2, y2);
		count += d2;
		if (count < 0)
			count = count * -1;

		return count;
	}

	public static int month(int a, int yy) {
		int mon[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int x = 0, c;
		for (c = 0; c < a - 1; c++) {
			if (c == 1) {
				if (yy % 4 == 0)
					x += 29;
				else
					x += 28;
			} else
				x += mon[c];
		}
		return (x);
	}

	private static boolean IsLeapYear(int year) {
		boolean isLeapYear = ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0));
		return isLeapYear;
	}

	private static String convyyyymmdd(String a) {
		String b = "";
		b = "20" + a.substring(6, 8) + a.substring(3, 5) + a.substring(0, 2);
		return b;
	}

	@SuppressWarnings("deprecation")
	private void captureImage() {
		// billing();
		//new Intent(this, )
        Toast.makeText(ActvBilling.this, "Step17 Completed", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, Ori)
		try {
			String AppDir = Environment.getExternalStorageDirectory().getPath()
					+ "/SBDocs/Photos";

			String sdocdfilepath = AppDir + "/" + UtilAppCommon.in.SUB_DIVISION_CODE;
			File sdofile = new File(sdocdfilepath);
			if (!sdofile.exists()) {
                Toast.makeText(ActvBilling.this, "Step18 Completed", Toast.LENGTH_SHORT).show();
				sdofile.mkdir();
			}
			String binderfilepath = AppDir + "/" + UtilAppCommon.in.SUB_DIVISION_CODE
					+ "/" + UtilAppCommon.in.MRU;
			File binderfile = new File(binderfilepath);
			if (!binderfile.exists()) {
                Toast.makeText(ActvBilling.this, "Step19 Completed", Toast.LENGTH_SHORT).show();
				binderfile.mkdir();
			}
/*			photoId = "BSB" + "_" + UtilAppCommon.in.SUB_DIVISION_CODE + UtilAppCommon.in.MRU
					+ UtilAppCommon.in.CONTRACT_AC_NO;
			*/
			photoId = UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
					UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + 
					"_" + UtilAppCommon.in.CONTRACT_AC_NO;

			Log.v("ActvBilling","Taking Photo Image Address1 "+sdocdfilepath+" "+sdofile.exists());
			Log.v("ActvBilling","Taking Photo Image Address2 "+binderfilepath+" "+binderfile.exists());
			UtilAppCommon.out.PHOTO_ID = photoId;
			//UtilAppCommon.out.Company = UtilAppCommon.ui.CO;
			System.out.println("photo id ::::::: " + photoId);
			file = new File(binderfile, photoId + ".jpg");
			photoFolderSaved=binderfile.getAbsolutePath();
			photoAddressSaved=file.getAbsolutePath();
            Toast.makeText(ActvBilling.this, "Step20 Completed", Toast.LENGTH_SHORT).show();
			List<ResolveInfo> resInfoList = this.getPackageManager().
					queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
			for (ResolveInfo resolveInfo : resInfoList) {
				String packageName = resolveInfo.activityInfo.packageName;
				this.grantUriPermission(packageName, AppUtil.getFileUri(this, file) ,
						Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getFileUri(this, file));
			//intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
			
			
			//Code Added for Image Compression
			/*Uri imgUri = Uri.fromFile(file);
			if (imgUri.toString().startsWith("file://")) {
	               //ten = ten.substring(11);
	            }
	            Bitmap mBitmap = BitmapFactory.decodeFile(imgUri.toString());                 
                Display display = getWindowManager().getDefaultDisplay();
                int newHeight = display.getHeight();
                int newWidth = display.getWidth(); 
                Bitmap resized = Bitmap.createScaledBitmap(mBitmap, newHeight, newWidth, false);                   

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                resized.compress(CompressFormat.JPEG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();

                StoreByteImage(bitmapdata, 100);
			//End of Code
			*/
			UtilAppCommon.blImageCapture = true;
			Log.i("ActvBilling Capt", " B isFinishing ==>> " + isFinishing());
			startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
			Log.i("ActvBilling Capt", "A isFinishing ==>> " + isFinishing());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public boolean StoreByteImage() {
        try {
        	        	
        	Log.e("Photo file", file.getPath());
        	Log.e("APhoto file", file.getAbsolutePath());
        	Uri imgUri = Uri.fromFile(file);
        	
        	if (imgUri.toString().startsWith("file://")) {
                //ten = ten.substring(11);
             }
        	
        	Bitmap mBitmap = BitmapFactory.decodeFile(file.getPath());                 
            Display display = getWindowManager().getDefaultDisplay();
            int newHeight = 96; //display.getHeight();
            int newWidth = 96; //display.getWidth(); 
            Bitmap resized = Bitmap.createScaledBitmap(mBitmap, newHeight, newWidth, false);                   
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            resized.compress(CompressFormat.JPEG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();

            String AppDir = Environment.getExternalStorageDirectory().getPath()
    				+ "/SBDocs/Photos_Crop" + "/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/"
    				+ UtilAppCommon.in.MRU;

            Log.e("AppDir file",AppDir);
    		File f = new File(AppDir);
    		String fullPath = f.getAbsolutePath();
    		
			File dir = new File(fullPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			Log.e("AppDir fullPath",fullPath);
    	   cropfile = new File(fullPath, photoId + ".jpg");

          
           FileOutputStream fileOutputStream = null;
           BitmapFactory.Options options=new BitmapFactory.Options();
           options.inSampleSize = 1;  
           Bitmap myImage = BitmapFactory.decodeByteArray(bitmapdata, 0,
        		   bitmapdata.length,options);
           fileOutputStream = new FileOutputStream( fullPath + "/" + photoId + ".jpg");
           BufferedOutputStream bos1 = new BufferedOutputStream(   
                 fileOutputStream);
           myImage.compress(CompressFormat.JPEG, 0, bos1);
           bos1.flush();
           bos1.close();
         }//create data file
         catch (FileNotFoundException e) 
         {
            Log.e("DATAFILE", "File Not Found Error = " + e.getMessage());
            e.printStackTrace();
            return false;
         }//data file error 
         catch (IOException e) 
         {
            Log.e("DATAFILE", "IOException Error" + e.getMessage());
            e.printStackTrace();
            return false;
         }//data file error
         return true;
      }//storebyteimage  

	
	void CropImage() {

		try {
			Log.v("ActvBilling","**************************"+photoFolderSaved);
			Log.v("ActvBilling","**************************"+photoAddressSaved);

			File file=new File(photoFolderSaved);
			Log.v("ActvBilling","**************************1");
			File fileList[]=file.listFiles();
			Log.v("ActvBilling","*************************2");
			File file2=new File(photoAddressSaved);
			Log.v("ActvBilling","**************************"+file.getAbsolutePath());
			Log.v("ActvBilling","**************************"+file2.getAbsolutePath());
			//Log.v("ActvBilling","**************************"+file2.getAbsolutePath());

			for(File fileName : fileList){
				Log.v("ActvBilling","**************************"+fileName.getAbsolutePath());
				if(!fileName.getAbsolutePath().equalsIgnoreCase(file2.getAbsolutePath())){
					Log.v("ActvBilling","*****************File Name Not Matched*********"+fileName.getAbsolutePath());
					fileName.delete();
				}
			}


			UtilAppCommon.blImageCapture = false;
			UtilDB utildb = new UtilDB(context);
			photoId = UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
					UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
					"_" + UtilAppCommon.in.CONTRACT_AC_NO;



			String AppDir = Environment.getExternalStorageDirectory().getPath()
					+ "/SBDocs/Photos_Crop" + "/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/"
					+ UtilAppCommon.in.MRU;



			File f = new File(AppDir);
			String fullPath = f.getAbsolutePath();

			File dir = new File(fullPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String inputData = "";
			inputData = UtilAppCommon.acctNbr + "~" + photoId + ".jpg" + "~" + AppDir + "~0~0";
			utildb.insertIntoImageData(inputData);

			Log.i("ActvBilling"," Crop Image Full Path ==>> " + fullPath);
			cropfile = new File(fullPath, photoId + ".jpg");
			//

			String binderfile = Environment.getExternalStorageDirectory().getPath()
					+ "/SBDocs/Photos" + "/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/" + UtilAppCommon.in.MRU;
			if(!new File(binderfile).exists()){
				Log.i("ActvBilling","************************Not finding*** "+binderfile);
			}
			if(!new File(Environment.getExternalStorageDirectory().getPath()).exists()){
				Log.i("ActvBilling","************************Not finding*** 1");
			}
			if(!new File(Environment.getExternalStorageDirectory().getPath() + "/SBDocs").exists()){
				Log.i("ActvBilling","************************Not finding*** 2");
			}
			if(!new File(Environment.getExternalStorageDirectory().getPath() + "/SBDocs/Photos").exists()){
				Log.i("ActvBilling","************************Not finding*** 3");
			}
			if(!new File(Environment.getExternalStorageDirectory().getPath() + "/SBDocs/Photos"+ "/" + UtilAppCommon.in.SUB_DIVISION_CODE).exists()){
				Log.i("ActvBilling","************************Not finding*** 4");
			}
			if(!new File(Environment.getExternalStorageDirectory().getPath() + "/SBDocs/Photos"+ "/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/" + UtilAppCommon.in.MRU).exists()){
				Log.i("ActvBilling","************************Not finding*** 5");
			}
			String secondaryFile=Environment.getExternalStorageDirectory().getPath() + "/SBDocs/PHOTOs"+ "/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/" + UtilAppCommon.in.MRU;

			if(!new File(binderfile, photoId + ".jpg").exists())
			{
				if(!new File(secondaryFile, photoId + ".jpg").exists()) {
					Log.i("ActvBilling", "After crop Not finding image hence exiting " + binderfile);
					Log.i("ActvBilling", "After crop Not finding image hence exiting " + secondaryFile);
					//showLayout();
					finish();
				}
			}
			else {


				file = new File(binderfile, photoId + ".jpg");
				Log.i("ActvBilling", " ************Going for Cropping********" + file.getAbsolutePath());
				Intent cropIntent = new Intent("com.android.camera.action.CROP");
				// indicate image type and Uri
				cropIntent.setDataAndType(AppUtil.getFileUri(this, file), "image/*");
				cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
				List<ResolveInfo> resInfoList = this.getPackageManager().
						queryIntentActivities(cropIntent, PackageManager.MATCH_DEFAULT_ONLY);
				for (ResolveInfo resolveInfo : resInfoList) {
					String packageName = resolveInfo.activityInfo.packageName;
					this.grantUriPermission(packageName, AppUtil.getFileUri(this, cropfile) ,
							Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
				}

				// set crop properties
				cropIntent.putExtra("crop", "true");
				// indicate aspect of desired crop
				cropIntent.putExtra("aspectX", 400);
				cropIntent.putExtra("aspectY", 225);
				cropIntent.putExtra("outputX", 400);
				cropIntent.putExtra("outputY", 225);
				cropIntent.putExtra("scale", true);
				// retrieve data on return
				cropIntent.putExtra("return-data", true);
				// start the activity - we handle returning in onActivityResult
				cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getFileUri(this, cropfile));
				//cropIntent.setDataAndType(AppUtil.getFileUri(this, cropfile), "image/*");
				if (Build.VERSION.SDK_INT >= 24) {
					try {
						Method m1 = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
						m1.invoke(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Log.i("ActvBilling", " ************Going for Cropping********" + cropfile.getAbsolutePath());
				//Log.e("Activity", "cropIntent");
				startActivityForResult(cropIntent, PIC_CROP);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("ActvBilling", " CropImage ==>>" + e.getMessage());
		}

	}

	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	private static String getDate(String reqformate) {
		Calendar currentDate = Calendar.getInstance();
		// SimpleDateFormat formatter = new
		// SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat(reqformate);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}

	private static String AddDate(String date, int addDays) {
		int day;
		int mon;
		int year;

		System.out.println("Date : " + date);
		day = Integer.parseInt(date.substring(0, 2));
		mon = Integer.parseInt(date.substring(3, 5));
		year = Integer.parseInt(date.substring(6, 8));

		day = day + addDays;

		if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
			if (mon == 2 && day > 29) {
				day = day - 29;
				mon = mon + 1;
			}
			if ((mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8
					|| mon == 10 || mon == 12)
					&& (day > 31)) {
				day = day - 31;
				mon = mon + 1;
				if (mon > 12) {
					mon = mon - 12;
					year = year + 1;
				}
			}
			if ((mon == 4 || mon == 6 || mon == 9 || mon == 11) && (day > 30)) {
				day = day - 30;
				mon = mon + 1;
				if (mon > 12) {
					mon = mon - 12;
					year = year + 1;
				}

			}
		} else {
			if (mon == 2 && day > 28) {
				day = day - 28;
				mon = mon + 1;
			}
			if ((mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8
					|| mon == 10 || mon == 12)
					&& (day > 31)) {
				day = day - 31;
				mon = mon + 1;
				if (mon > 12) {
					mon = mon - 12;
					year = year + 1;
				}
			}

			if ((mon == 4 || mon == 6 || mon == 9 || mon == 11) && (day > 30)) {
				day = day - 30;
				mon = mon + 1;
				if (mon > 12) {
					mon = mon - 12;
					year = year + 1;
				}
			}
		}

		date = String.format("%02d/%02d/%02d", day, mon, year);
		// System.out.printf("AFTER ADDING %d Days : %s\n",addDays,date);

		return date;
	}


private void createTable1(Document document) throws DocumentException {
		Log.e("createTable1", "Started");
		try {
			Paragraph p = new Paragraph(" ELECTRICITY BILL");
			p.setAlignment("CENTER");
			document.add(p);
			document.add(Chunk.NEWLINE);

			PdfPTable table = new PdfPTable(2);
			table.addCell("COMPANY :");
			table.addCell(UtilAppCommon.out.Company+"CL");
			table.addCell("BILL FOR");
			table.addCell( UtilAppCommon.out.BillMonth);
			table.addCell("CONSUMER DETAILS");
			table.addCell( "");

			table.addCell("BILL NO");
			table.addCell(UtilAppCommon.out.BillNo);

			table.addCell("DIVISION");
			table.addCell(UtilAppCommon.out.Division);
			table.addCell("Sub Div");
			table.addCell( UtilAppCommon.out.SubDivision);
			
			table.addCell("CA NUMBER");
			table.addCell(UtilAppCommon.out.CANumber);

			table.addCell("LEGACY No");
			table.addCell(UtilAppCommon.out.LegacyNumber);

			table.addCell("MRU ");
			table.addCell(UtilAppCommon.out.MRU);
			table.addCell("NAME");
			table.addCell(UtilAppCommon.out.Name);

			table.addCell("Address");
			table.addCell(UtilAppCommon.out.Address);
			table.addCell("Pole No");
			table.addCell(UtilAppCommon.out.PoleNo);

			table.addCell("METER NO:  PH:");
			table.addCell(UtilAppCommon.out.MtrNo+" "+UtilAppCommon.out.Phase);
			table.addCell("MTR COMP");
			table.addCell( UtilAppCommon.out.MtrMake
					.equals("C") ? "Company" : UtilAppCommon.out.MtrMake
							.equals("S") ? "Consumer" : "");

			table.addCell("CATEGORY");
			table.addCell(UtilAppCommon.out.Category);
			// table.addCell("ADDR1");
			table.addCell("SL  CL  CD");
			table.addCell(UtilAppCommon.out.SanctLoad+" "+UtilAppCommon.out.ConnectedLoad+" "+ UtilAppCommon.out.CD);
			table.addCell("SD");
			table.addCell(UtilAppCommon.out.SD);
			table.addCell("BILLED DAYS ");
			// table.addCell("ADDR3");
			table.addCell(UtilAppCommon.out.BillDays);
			table.addCell("READING DETAILS");
			table.addCell("");
			table.addCell("PREVIOUS");
			table.addCell("CURRENT");
			table.addCell("READING");
			table.addCell("DATE STS");
			table.addCell(UtilAppCommon.out.PreviusReading);
			table.addCell(UtilAppCommon.out.PrevusMtrRdgDt+" "+UtilAppCommon.out.PreviusMtrReadingNote);
			table.addCell(UtilAppCommon.out.CurrentReading);
			table.addCell(UtilAppCommon.out.CurrentMtrRdgDt+" "+UtilAppCommon.out.CurrentMtrReadingNote);


			PdfPTable table2 = new PdfPTable(2);
			table2.addCell("MF :");
			table2.addCell(UtilAppCommon.out.MF);

			table2.addCell("CONSUMPTION:");
			table2.addCell(UtilAppCommon.out.Consumption);

			
			table2.addCell("RECD DEMD:   PF:");
			table2.addCell(UtilAppCommon.out.RecordedDemd+"  "+ UtilAppCommon.out.PowerFactor);

			PdfPTable table3 = new PdfPTable(2);
			String mmcunits,avg;
			if (UtilAppCommon.out.Category.equals("DS-II")||UtilAppCommon.out.Category.equals("NDS-I"))
			{
				mmcunits = "0";
			}
				else
				{
					mmcunits = UtilAppCommon.out.MMCUnits;
			}
			if (UtilAppCommon.out.Type.equalsIgnoreCase("ACTUAL"))
					{
				     avg ="";
				     }
			else
			{
				avg = UtilAppCommon.out.Average;
			}
						
			table3.addCell("MMC UNITS:  AVG:");
			table3.addCell(mmcunits+ "  "+ avg);

			table3.addCell("BLD UNITS: TYPE :");
			table3.addCell(UtilAppCommon.out.BilledUnits+" "+ UtilAppCommon.out.Type);

			table3.addCell("ARREAR DETAILS:");
			table3.addCell("");

			table3.addCell("PYMT ON ACCT:");
			table3.addCell(UtilAppCommon.out.PaymentOnAccount);

			table3.addCell("ENERGY DUES:");
			table3.addCell(UtilAppCommon.out.ArrearEnergyDues);
			table3.addCell("ARREAR DPS:");
			table3.addCell(UtilAppCommon.out.ArrearDPs);

			table3.addCell("OTHERS  :");
			table3.addCell(UtilAppCommon.out.ArrearOthers);
			table3.addCell("SUB TOTAL(A) ");
			table3.addCell(UtilAppCommon.out.ArrearSubTotal_A);	

			table3.addCell("CURRENT BILL DETAILS ");
			table3.addCell("");	

			table3.addCell("ENERGY CHG ");
			table3.addCell(UtilAppCommon.out.CurrentEnergyCharges);	

			table3.addCell("DPS ");
			table3.addCell(UtilAppCommon.out.CurrentMonthDps);	

			table3.addCell("FIXED/DEMD CHG");
			table3.addCell(UtilAppCommon.out.FixDemdCharge);	

			table3.addCell("EXCESS DEMD CHG");
			table3.addCell(UtilAppCommon.out.ExcessDemdCharge);	

			table3.addCell("ELEC. DUTY");
			table3.addCell(UtilAppCommon.out.ElectricityDuty);	
			
			table3.addCell("MTR RENT");
			table3.addCell(UtilAppCommon.out.MeterRent);	
			
			table3.addCell("SHUNT CAP.CHG");
			table3.addCell(UtilAppCommon.out.ShauntCapCharge);	
			
			table3.addCell("OTHER CHG");
			table3.addCell(UtilAppCommon.out.OtherCharge);
			
			table3.addCell("SUB TOTAL  (B)  ");
			table3.addCell(UtilAppCommon.out.SubTotal_B);
			
			table3.addCell("NTEREST ON SD(C) ");
			table3.addCell(UtilAppCommon.out.InterestOnSD_C);
			
			table3.addCell("INCENTIVE   ");
			table3.addCell(UtilAppCommon.out.Incentive);
			
			table3.addCell("REBATES ON MMC  ");
			table3.addCell("0.00");
			
			table3.addCell("TOTAL(A+B+C)");
			table3.addCell(UtilAppCommon.out.GrossTotal);
			table3.addCell("REBATES (-)");
			table3.addCell(UtilAppCommon.out.Rebate);
			
			table3.addCell("AMT PAYABLE");
			table3.addCell("");
			
			table3.addCell("UPTO");
			table3.addCell(UtilAppCommon.out.AmtPayableUptoDt+":"+UtilAppCommon.out.AmtPayableUptoAmt);
			table3.addCell("BY");
			table3.addCell(UtilAppCommon.out.AmtPayablePYDt+":"+UtilAppCommon.out.AmtPayablePYAmt);
			table3.addCell("AFTER");
			table3.addCell(UtilAppCommon.out.AmtPayableAfterDt+":"+UtilAppCommon.out.AmtPayableAfterAmt);
			
			table3.addCell("DETAILS OF LAST PAYMENT");
			table3.addCell("");
			
			table3.addCell(" LAST PAID AMT");
			table3.addCell(UtilAppCommon.out.LastPaymentAmt);
			
			table3.addCell(" LAST PAID DT");
			table3.addCell(UtilAppCommon.out.LastPaidDate);	
			table3.addCell(" RECEIPT NO");
			table3.addCell(UtilAppCommon.out.ReceiptNumber);	
			
			table3.addCell("MTR RDR ");
			table3.addCell(UtilAppCommon.out.MTR_READER_ID);	
	
			
			document.add(table);
			document.add(table2);
			document.add(table3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("createTable1", e.getMessage());
			e.printStackTrace();
		}
		Log.e("createTable1", "Completed");
	}

// added 20.11.15
private void printdlg(){
	{
		// need to be change for photo
		getImageByCANo(UtilAppCommon.acctNbr);
		final AlertDialog ad = new AlertDialog.Builder(this)
				.create();
		ad.setTitle("Confirm");
		ad.setMessage("Confirm to print");
		ad.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						ad.dismiss();
						Write2SbmOut();
						//startActivity(new Intent(ctx, ActvBillPrinting.class));
					}
				});
		ad.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						ad.dismiss();
						// startActivity(getIntent());
						//startActivity(new Intent(getApplicationContext(), ActvBillingOption.class));
						if(UtilAppCommon.billType.equalsIgnoreCase("A"))
							startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
						else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
							startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
						else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
							startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
						else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
							startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
						else
							startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
					}
				});
		ad.show();
	}
}


@Override
	public void done() {
		// TODO Auto-generated method stub
		if(UtilAppCommon.billType.equalsIgnoreCase("A"))
			startActivity(new Intent(this, ActvConsumerNbrInput.class));
		else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
			startActivity(new Intent(this, ActvLegacyNbrInput.class));
		else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
			startActivity(new Intent(this, ActvSequenceData.class));
		else
			startActivity(new Intent(this, ActvBillingOption.class));
	}	
	

	public void getImageByCANo(String CANo)
	{


		String AppDir = null;
		try {
			Log.e("getImageByCANo", "Started");
			UtilDB utildb = new UtilDB(getApplicationContext());
			AppDir = Environment.getExternalStorageDirectory().getPath()
					+ "/SBDocs/Photos_Crop" + "/" + utildb.getSdoCode() + "/"
					+ utildb.getActiveMRU();
			Cursor cursorImage = utildb.getUnCompressedImage(CANo);
			File file = null;
			//getUnCompressedImage
			if(cursorImage != null)
			{
				cursorImage.moveToFirst();
				file = new File(AppDir , cursorImage.getString(1));
			}
			//ImageProcessing imageProcessing = new ImageProcessing();

			AsyncImage asyncImage = new AsyncImage(this ,new OnBillGenerate() {
				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
				}
			});

			//String strArray[] = imageProcessing.processImage(AppDir, file, this, cursorImage.getString(1), CANo);
			String credentials[] = new String[6];
			credentials[0] = CANo;
			credentials[1] = cursorImage.getString(1).substring(4, 6);
			credentials[2] = cursorImage.getString(1).substring(0, 4);
			credentials[3] = utildb.getSdoCode();
			File f = new File(AppDir);
			credentials[4] = f.getAbsolutePath() + "/" + cursorImage.getString(1);
			credentials[5] = utildb.getActiveMRU();

			if(credentials != null)
				asyncImage.execute(credentials);

			Log.e("getImageByCANo", "Completed");
		} catch (Exception e) {
			Log.e("getImageByCANo", "Error ==>> " + e.getMessage());
			e.printStackTrace();
		}
	}

		@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	private void postCaptureImage()	{

		UtilAppCommon.blImageCapture = false;
		Timestamp timestamp = new Timestamp(
				System.currentTimeMillis());
		photoTakenTime = String.valueOf(timestamp);
		/*Toast.makeText(getApplicationContext(),
				"Photo Saved Successfully", Toast.LENGTH_LONG)
				.show();*/

		UtilDB dbObj = new UtilDB(context);
		String[] printer = dbObj.GetPrinterInfo();
		dbObj.close();
		if (printer[0] != null || printer[1] != null) {
			if (printer[1].compareToIgnoreCase("Analogic Impact") == 0) {
				Log.v("ActvBolling", "PostCapture Image when printer[1].compareToIgnoreCase(Analogic Impact)");
				billing();
				// 11.02.16
				//} else if (printer[1]
				//		.compareToIgnoreCase("Analogic Thermal") == 0) {
				//	billing();
			} else {
				Log.v("ActvBolling", "PostCapture Image when NOT NOT printer[1].compareToIgnoreCase(Analogic Impact)");
				//StoreByteImage();
				//billing();
				CropImage();
			}
		}
		else
		{

			Toast.makeText(getApplicationContext(), "Please configure printer", Toast.LENGTH_LONG)
					.show();
			finish();
		}

	}
}
