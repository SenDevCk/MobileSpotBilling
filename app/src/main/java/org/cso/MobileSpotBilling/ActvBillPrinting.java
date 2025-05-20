package org.cso.MobileSpotBilling;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.analogics.thermalAPI.Bluetooth_Printer_2inch_prof_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.epson.eposprint.Builder;
import com.epson.eposprint.Print;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;

import org.cso.MSBAsync.AsyncImage;
import org.cso.MSBUtil.AppUtil;
import org.cso.MSBUtil.PrintUtilZebra;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.Utilities;
import org.cso.TVS.BarcodeCreater;
import org.cso.TVS.BitmapDeleteNoUseSpaceUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import HPRTAndroidSDK.HPRTPrinterHelper;

public class ActvBillPrinting extends AppCompatActivity {
	/** Called when the activity is first created. */
	private BluetoothAdapter mBluetoothAdapter = null;
	static final UUID MY_UUID = UUID.randomUUID();
	// static String address = "00:1F:B7:05:44:76";
	ZebraThermal sendDatazebra = null;
	//AnalogicImpact sendData = null;
	EpsonThermal sendDataEpson = null;
	EpsonThermalHindi sendDataEpsonHindi = null;
	SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
	String strDateTime = null;
	String strBarcodeData = ""; //UtilAppCommon.out.BillNo + UtilAppCommon.out.AmtPayableUptoDt + UtilAppCommon.out.CurrentMtrReadingNote;

	String strMonths[] = {"JAN", "FEB", "MAR", "ARP", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
	String strmonth = "";
//	AnalogicThermal sendDataAnaloginThermal = null;

	String strPrinterValue;
	String strPrinterItem;

	String BILL = "";
	// Added on-1.3.2014
	private Context context = this;

	// End add -1.3.2014
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth not available.", Toast.LENGTH_LONG)
					.show();

			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(
					this,
					"Bluetooth feature is turned off now...Please turned it on! ",
					Toast.LENGTH_LONG).show();
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			mBluetoothAdapter.enable();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//Added on 05/12/16
		/*setContentView(R.layout.meter_status_list);
	    
        ListView meterStatusListView = (ListView) findViewById(R.id.MeterStatusList);
        ClsListData meterStatus[] = new ClsListData[3];
        meterStatus[0] =  new ClsListData("English", "1");
        meterStatus[1] =  new ClsListData("Hindi", "2");
        //meterStatus[2] =  new ClsListData("Premises Lock(PL)", "PL");
        //meterStatus[3] =  new ClsListData("Reading Not Avlb(RN)", "RN");

        ArrayAdapter<ClsListData> listAdapter = new ArrayAdapter<ClsListData>(this, android.R.layout.simple_list_item_1, meterStatus);
        meterStatusListView.setAdapter(listAdapter);
        meterStatusListView.setOnItemClickListener(this);	*/

		final int intVal = -1;
		
		/*AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Printer Type");
        builder.setItems(R.array.PrintLang, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int item) {
            	//final String devicetype=context.getResources().getStringArray(R.array.PrinterType)[item];
            	int intValue = item;
            	//intVal = intValue;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();*/


		UtilDB dbObj = new UtilDB(context);
		String[] printer = dbObj.GetPrinterInfo();
		if (printer[1].compareToIgnoreCase("Zebra Thermal") == 0) {
			sendDatazebra = new ZebraThermal(printer[0]);
			Thread t = new Thread(sendDatazebra);
			t.run();
		} else if (printer[1].compareToIgnoreCase("EPSON Thermal") == 0) {
			sendDataEpson = new EpsonThermal(printer[0]);
			Thread t = new Thread(sendDataEpson);
			t.run();
		} else if (printer[1].compareToIgnoreCase("EPSON Thermal-Hin") == 0) {
			sendDataEpsonHindi = new EpsonThermalHindi(printer[0]);
			Thread t = new Thread(sendDataEpsonHindi);
			t.run();
		} else if (printer[1].compareToIgnoreCase("Analogic Thermal") == 0) {
			AnalogicThermal sendDataAnaloginThermal = new AnalogicThermal(printer[0]);
			Thread t = new Thread(sendDataAnaloginThermal);
			t.run();
		} else if (printer[1].compareToIgnoreCase("Analogic Thermal-Hin") == 0) {
			Thread t = null;
			AnalogicThermalHindi sendDataAnaloginThermal = new AnalogicThermalHindi(printer[0]);
			t = new Thread(sendDataAnaloginThermal);
			t.run();
		} else if (printer[1].compareToIgnoreCase("TVS-ENGLISH") == 0) {
			Thread t = null;
			TVSEnglish tvsEnglish = new TVSEnglish(printer[0]);
			t = new Thread(tvsEnglish);
			t.run();
		} else if (printer[1].compareToIgnoreCase("TVS-HINDI") == 0) {
			Thread t = null;
			TVSHindi tvsHindi = new TVSHindi(printer[0]);
			t = new Thread(tvsHindi);
			t.run();
		} else {
			Toast.makeText(this, "No Printer Configured  " + printer[1], Toast.LENGTH_LONG)
					.show();
			if (UtilAppCommon.blActyncBtn)
				startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
				//else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
				//	startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
			else if (UtilAppCommon.billType.equalsIgnoreCase("A"))
				startActivity(new Intent(this, ActvConsumerNbrInput.class));
			else if (UtilAppCommon.billType.equalsIgnoreCase("L"))
				startActivity(new Intent(this, ActvLegacyNbrInput.class));
			else if (UtilAppCommon.billType.equalsIgnoreCase("S"))
				startActivity(new Intent(this, ActvSequenceData.class));
			else if (UtilAppCommon.billType.equalsIgnoreCase("M"))
				startActivity(new Intent(this, MeterNbrInput.class));
			else
				startActivity(new Intent(this, ActvBillingOption.class));
		}
		// End Added on 1.3.2014
	}

/*	
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
	 		 startActivity(new Intent(this, ActvivityMain.class));
	   		finish();	   	 
	    	 Intent intent = new Intent(this, ActvivityMain.class);  
	    	 
	    	 startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));  
	         startActivity(intent);
	   		 break;
	     }
	     return true;
   }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
		ClsListData data = (ClsListData) parent.getItemAtPosition(position);
		String value =  data.getValue();
		String item = data.getDisplay();
		Intent intent = getIntent();
	    strPrinterValue = value;
	    strPrinterItem = item;
	    
		// Added on 3.7.2014
		
	}
*/

	public void getImageByCANo(String CANo) {
		String AppDir = "";

		Log.e("getImageByCANo", "Started");
		UtilDB utildb = new UtilDB(getApplicationContext());
		AppDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/SBDocs/Photos_Crop" + "/" + utildb.getSdoCode() + "/"
				+ utildb.getActiveMRU()).getPath();

		Cursor cursorImage = utildb.getUnCompressedImage(CANo);
		File file = null;
		//getUnCompressedImage
		if (cursorImage != null) {
			cursorImage.moveToFirst();
			file = new File(AppDir, cursorImage.getString(1));
		}
		//ImageProcessing imageProcessing = new ImageProcessing();

		AsyncImage asyncImage = new AsyncImage(this, () -> {
			// TODO Auto-generated method stub
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

		if (credentials != null)
			asyncImage.execute(credentials);

		Log.e("getImageByCANo", "Completed");
	}

	public void onBackPressed() {
		// do something on back.
		super.onBackPressed();
		//finish();
		// startActivity(new Intent(this, ActvivityMain.class));
		return;
	}

	class ZebraThermal extends Thread {

		private BluetoothDevice device = null;
		private BluetoothSocket btSocket = null;
		private OutputStream outStream = null;
		private OutputStreamWriter writer = null;
		private String address = null;

		public ZebraThermal(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);
			Toast.makeText(getApplicationContext(), "Connected To:" + address,
					Toast.LENGTH_LONG).show();
		}

		@SuppressLint("SuspiciousIndentation")
		@SuppressWarnings("deprecation")
		public void run() {
			try {
				Toast.makeText(getApplicationContext(), "Sending Data",
						Toast.LENGTH_LONG).show();

				ZebraPrinterConnection thePrinterConn = new BluetoothPrinterConnection(
						address);
				thePrinterConn.open();
				// Initialize

				SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy'   TIME: 'hh:mm");

				// Open the connection - physical connection is established
				// here.
				ZebraPrinter printer = ZebraPrinterFactory
						.getInstance(thePrinterConn);
				PrinterLanguage pcLanguage = printer
						.getPrinterControlLanguage();
				Toast.makeText(getApplicationContext(),
						"Language: " + pcLanguage, Toast.LENGTH_LONG).show();

				// This example prints "This is a ZPL test." near the top of the
				// label.
				String cpclData = null;
				PrintUtilZebra.LineNo = 0;
				if (UtilAppCommon.bprintdupl)
					cpclData = "! 90 200 200 1140 1\r\n";
				else
					cpclData = "! 90 200 200 1110 1\r\n";
				cpclData += "CENTER\r\n";
				cpclData += "UNDERLINE ON\r\n";

				if (UtilAppCommon.bprintdupl)
					cpclData += PrintUtilZebra.PrintLargeNext("Duplicate Bill");
				UtilAppCommon.bprintdupl = false;
				cpclData += PrintUtilZebra.PrintLargeNext(UtilAppCommon.out.Company + "CL");

				cpclData += PrintUtilZebra
						.PrintNext("*******************   ");
				cpclData += "LEFT\r\n";
				cpclData += PrintUtilZebra.PrintNext(String.format(
						" ELECTRICITY BILL :%s", UtilAppCommon.out.BillMonth));
				//cpclData += "UNDERLINE OFF\r\n";
				cpclData += PrintUtilZebra
						.PrintNext("**************************");
				cpclData += PrintUtilZebra.PrintNext(String.format(
						" DATE: %s", UtilAppCommon.out.REC_DATE_TIME));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						" DUE DATE: %s", UtilAppCommon.out.AmtPayableUptoDt));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						" CONSUMER DETAILS"));
				//cpclData += "CENTER\r\n";
				cpclData += PrintUtilZebra
						.PrintNext("**************************");
				cpclData += "LEFT\r\n";

				cpclData += PrintUtilZebra.PrintNextData("  Bill No       ",
						String.format("%s", UtilAppCommon.out.BillNo));

				cpclData += PrintUtilZebra.PrintNextData("  DIVISION       ",
						String.format("%s", UtilAppCommon.out.Division));

				cpclData += PrintUtilZebra.PrintNextData(" SUB DIVN    ",
						String.format("%s", UtilAppCommon.out.SubDivision));

				cpclData += PrintUtilZebra.PrintNextData(" CA NUMBER   ",
						String.format("%s", UtilAppCommon.in.CONTRACT_AC_NO));

				cpclData += PrintUtilZebra.PrintNextData(" LEGACY NO       ",
						String.format("%s", UtilAppCommon.in.CONSUMER_LEGACY_ACC_NO));

				cpclData += PrintUtilZebra.PrintNextData(" MRU             ",
						String.format("%10s", UtilAppCommon.in.MRU));


				cpclData += PrintUtilZebra.PrintNext(String.format(
						"NAME  : %s", UtilAppCommon.in.CONSUMER_NAME));

				int alen = 0;
				alen = UtilAppCommon.out.Address.length();
				String addr1 = "";
				String addr2 = "";
				if (alen > 26) {
					addr1 = UtilAppCommon.out.Address.substring(0, 26);
					if (alen > 52)
						addr2 = UtilAppCommon.out.Address.substring(26, 52);
					else
						addr2 = UtilAppCommon.out.Address.substring(26, alen);
				} else
					addr1 = UtilAppCommon.out.Address;


				cpclData += PrintUtilZebra.PrintNext(String.format(
						"ADDRESS  : "));

				cpclData += "LEFT\r\n";

				//cpclData += PrintUtilZebra.PrintNext(String.format(
				//		" %s", UtilAppCommon.out.Address));
				//cpclData += PrintUtilZebra.PrintNext(String.format(
				//		" %s",UtilAppCommon.out.Address.substring(0, 26)));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						" %s", addr1));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						" %s", addr2));

				cpclData += PrintUtilZebra.PrintNext(String.format(
						"MOBILE NO  : %s", UtilAppCommon.in.METER_CAP));

				cpclData += PrintUtilZebra.PrintNext(String.format(
						"AREA TYPE  : %s", UtilAppCommon.out.Area_type));

				cpclData += PrintUtilZebra.PrintNext(String.format(
						"POLE NO          : %s", UtilAppCommon.out.PoleNo));

				cpclData += PrintUtilZebra.PrintNext(String.format(
						"MTR NO: %s PH: %s", UtilAppCommon.out.MtrNo + ", " + UtilAppCommon.in.MONTH_SEASONAL, UtilAppCommon.out.Phase));


				//added 

				cpclData += PrintUtilZebra.PrintNext(String.format(
						"MTR COMP : %s", UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("C") ? "Company" : UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("O") ? "Consumer" : ""));

				//
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"CATEGORY         : %s", UtilAppCommon.in.RATE_CATEGORY));


				//String CD="";
				//String CD1="";
				String CD2 = "";
				double result = 0.0;
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 11");
				if (UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(B)")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IIM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTEV")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-ID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("PUBWW")) {
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 12");
					result = (double) (Double.parseDouble(UtilAppCommon.out.ConnectedLoad) / 0.9f);
					CD2 = String.format("%.2f", result) + "KVA";
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 13");
				} else if (UtilAppCommon.out.Category.equalsIgnoreCase("HGN")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IM")) {
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					CD2 = result + " HP";
				} else {
					/**
					 * End ading lines for tariff change
					 */
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 14");
					CD2 = result + " KW";
				}
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"LOAD:%s ", CD2));


				cpclData += PrintUtilZebra.PrintNext(String.format(
						"SD               : %s", UtilAppCommon.in.SECURITY_DEPOSIT));

				String billdays;

				if (UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) Actual")) {
					billdays = UtilAppCommon.out.BillDays + "(" + UtilAppCommon.out.MESSAGE10 + ")";
				} else {
					billdays = UtilAppCommon.out.BillDays;
				}
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"BILLED DAYS      : %s", billdays));
				cpclData += PrintUtilZebra
						.PrintNext("**************************");


				cpclData += "CENTER\r\n";
				cpclData += PrintUtilZebra
						.PrintNext("READING DETAILS");
				cpclData += PrintUtilZebra
						.PrintNext("----------------");
				cpclData += "LEFT\r\n";

				cpclData += PrintUtilZebra
						.PrintNext("\t\t      PREVIOUS \t     CURRENT");
				// 21.11.15
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"READING :   %s   \t %S", UtilAppCommon.out.PreviusReading, UtilAppCommon.out.CurrentReading));

				cpclData += PrintUtilZebra.PrintNext(String.format(
						"DATE    :   %s   \t %s", UtilAppCommon.out.PrevusMtrRdgDt, UtilAppCommon.out.CurrentMtrRdgDt));
				if (!UtilAppCommon.out.PrevusMtrRdgDt.equalsIgnoreCase("0000.00.00"))
					cpclData += PrintUtilZebra.PrintNext(String.format(
							"STATUS  :   %s   \t %s", UtilAppCommon.out.PreviusMtrReadingNote, UtilAppCommon.out.CurrentMtrReadingNote));
				//

				if (!UtilAppCommon.in.MONTH_SEASONAL.trim().equalsIgnoreCase("")) {
					cpclData += "CENTER\r\n";
					cpclData += PrintUtilZebra
							.PrintNext("BL METER READING DETAILS");
					cpclData += PrintUtilZebra
							.PrintNext("----------------");
					cpclData += "LEFT\r\n";

					cpclData += PrintUtilZebra
							.PrintNext("\t\t      PREVIOUS \t     CURRENT");
					// 21.11.15
					cpclData += PrintUtilZebra.PrintNext(String.format(
							"READING :   %s   \t %S", UtilAppCommon.out.PreviusReading, UtilAppCommon.out.CurrentReading));

					cpclData += PrintUtilZebra.PrintNext(String.format(
							"DATE    :   %s   \t %s", UtilAppCommon.out.PrevusMtrRdgDt, UtilAppCommon.out.CurrentMtrRdgDt));
					if (!UtilAppCommon.out.PrevusMtrRdgDt.equalsIgnoreCase("0000.00.00"))
						cpclData += PrintUtilZebra.PrintNext(String.format(
								"STATUS  :   %s   \t %s", UtilAppCommon.out.PreviusMtrReadingNote, UtilAppCommon.out.CurrentMtrReadingNote));
					//
				}


				//


				cpclData += "PRINT\r\n";

				thePrinterConn.write(cpclData.getBytes());
				PrintUtilZebra.LineNo = 0;

				if (UtilAppCommon.in.PRV_MTR_READING_NOTE.toUpperCase() != "RN") {
					Log.e("Inside", "Inside Photo routine");
					// Print Reading Image
					try {
					/*String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
							.getPath()
							+ "/SBDocs/Photos_Crop"
							+ "/"
							+ UtilAppCommon.sdoCode
							+ "/"
							+ UtilAppCommon.out.MRU;*/
						String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
								.getPath() + "/SBDocs/Photos/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/" + UtilAppCommon.in.MRU;

						strmonth = String.valueOf(Arrays.asList(strMonths).indexOf(UtilAppCommon.out.BillMonth) + 1);
						String PhotoPath = PhotoDir + "/" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
								UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
								"_" + UtilAppCommon.out.CANumber + ".jpg";


						System.out.println("Photopath:" + PhotoPath);


						thePrinterConn.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());


						//printer.getGraphicsUtil().printImage("/storage/sdcard0/img_tick.png",0,0,-1,-1,false);
						printer.getGraphicsUtil().printImage(PhotoPath, 100, 0, -1, -1, false);

					} catch (Exception ex) {

						System.out
								.println("Error In Photo Print: " + ex.toString());
					}
					// Print Reading Image End
				}

				cpclData = "! 90 200 200 1360 1\r\n";

				cpclData += "\n\n";

				float mf = 0, consump = 0;
				//mf=Float.parseFloat(UtilAppCommon.out.MF);
				if (!UtilAppCommon.out.MF.equalsIgnoreCase(""))
					mf = Float.parseFloat(UtilAppCommon.out.MF);
				consump = Float.parseFloat(UtilAppCommon.out.Consumption);

				cpclData += PrintUtilZebra.PrintNext(String.format(
						"MULTIPLYING FACTOR:  %.2f \n", mf));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"CONSUMPTION:  %.0f \n", consump));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"RECORDED DEMD: %s \n", UtilAppCommon.out.RecordedDemd));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"Power Factor:%s \n", UtilAppCommon.out.PowerFactor));

				float mmcunits = 0, avg = 0;
				if (UtilAppCommon.out.Category.equals("DS-II") || UtilAppCommon.out.Category.equals("NDS-IM")) {
					mmcunits = 0;
				} else {
					mmcunits = Float.parseFloat(UtilAppCommon.out.MMCUnits);
				}
				//if (UtilAppCommon.out.Type.equalsIgnoreCase("ACTUAL")||UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) Actual")||UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) MIN"))
				if (UtilAppCommon.out.CurrentMtrReadingNote.equalsIgnoreCase("OK")) {
					avg = 0;
				} else {
					avg = Float.parseFloat(UtilAppCommon.out.Average);
				}

				cpclData += PrintUtilZebra.PrintNext(String.format(
						"MIN UNITS:%.2f\tAVG.:%.2f", mmcunits, avg));

				cpclData += PrintUtilZebra.PrintNext(String.format(
						"BLD UNITS:%s\tTYPE:%s", UtilAppCommon.out.BilledUnits, UtilAppCommon.out.Type));

				cpclData += PrintUtilZebra
						.PrintNext("**************************");

				cpclData += "CENTER\r\n";
				cpclData += PrintUtilZebra
						.PrintNext("ARREAR DETAILS");
				cpclData += PrintUtilZebra
						.PrintNext("----------------");
				cpclData += "LEFT\r\n";


				float pmtonacct = 0, arrengdue = 0, arrdps = 0, arrothr = 0;
				pmtonacct = Float.parseFloat(UtilAppCommon.out.PaymentOnAccount);
				arrengdue = Float.parseFloat(UtilAppCommon.out.ArrearEnergyDues);
				arrdps = Float.parseFloat(UtilAppCommon.out.ArrearDPs);
				arrothr = Float.parseFloat(UtilAppCommon.out.ArrearOthers);
				cpclData += PrintUtilZebra.PrintNextData1("PYMT ON ACCT     ",
						String.format("%.2f", pmtonacct));

				cpclData += PrintUtilZebra.PrintNextData1("ENERGY DUES          ",
						String.format("%.2f", arrengdue));

				cpclData += PrintUtilZebra.PrintNextData1(" ARREAR DPS          ",
						String.format("%.2f", arrdps));

				cpclData += PrintUtilZebra.PrintNextData1(" OTHERS              ",
						String.format("%.2f", arrothr));


				cpclData += PrintUtilZebra.PrintNextData1(" SUB TOTAL(A)        ",
						String.format("%.2f", Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A)));
				cpclData += PrintUtilZebra
						.PrintNext("**************************");

				cpclData += "CENTER\r\n";
				cpclData += PrintUtilZebra
						.PrintNext("CURRENT BILL DETAILS");
				cpclData += PrintUtilZebra
						.PrintNext("----------------");
				cpclData += "LEFT\r\n";

				float engchg = 0;
				engchg = Float.parseFloat(UtilAppCommon.out.CurrentEnergyCharges);
				cpclData += PrintUtilZebra.PrintNextData1("ENERGY CHARGES       ",
						String.format("%.2f", engchg));

				cpclData += PrintUtilZebra.PrintNextData1("DPS   ",
						String.format("%.2f", Float.parseFloat(UtilAppCommon.out.CurrentMonthDps)));

				cpclData += PrintUtilZebra.PrintNextData1("FIXED/DEMD CHG    ",
						String.format("%.2f", Float.parseFloat(UtilAppCommon.out.FixDemdCharge)));

				cpclData += PrintUtilZebra.PrintNextData1("EXCESS DEMD CHG    ",
						String.format("%10s", UtilAppCommon.out.ExcessDemdCharge));


				cpclData += PrintUtilZebra.PrintNextData1("ELEC. DUTY    ",
						String.format("%10s", UtilAppCommon.out.ElectricityDuty));


				cpclData += PrintUtilZebra.PrintNextData1(" METER RENT        ",
						String.format("%10s", UtilAppCommon.out.MeterRent));


				cpclData += PrintUtilZebra.PrintNextData1(" SHUNT CAP.CHG ",
						String.format("%10s", UtilAppCommon.out.ShauntCapCharge));

				cpclData += PrintUtilZebra.PrintNextData1(" INSTALLMT AMT ",
						String.format("%10s", UtilAppCommon.in.CURR_MON_AMT));

				cpclData += PrintUtilZebra.PrintNextData1(" OTHER CHG      ",
						String.format("%10s", UtilAppCommon.out.OtherCharge));


				cpclData += PrintUtilZebra.PrintNextData1(" SUB TOTAL  (B)    ",
						String.format("%10s", UtilAppCommon.out.SubTotal_B));

				cpclData += PrintUtilZebra
						.PrintNext("**************************");

				cpclData += PrintUtilZebra.PrintNextData1(" INTEREST ON SD(C) ",
						String.format("%10s", UtilAppCommon.out.InterestOnSD_C));

				cpclData += PrintUtilZebra.PrintNextData1("   INCENTIVE        ",
						String.format("%10s", UtilAppCommon.out.Incentive));

				cpclData += PrintUtilZebra.PrintNextData1("  REMISSION        ",
						String.format("%10s", UtilAppCommon.out.RebateOnMMC));

				cpclData += PrintUtilZebra.PrintNextData1("  TOTAL(A+B+C)   ",
						String.format("%10s", UtilAppCommon.out.GrossTotal));

				cpclData += PrintUtilZebra
						.PrintNext("**************************");

				cpclData += PrintUtilZebra.PrintNextData1("  REBATES           ",
						String.format("%10s", UtilAppCommon.out.Rebate));

				cpclData += "CENTER\r\n";
				cpclData += PrintUtilZebra.PrintNext("AMOUNT PAYABLE");
				cpclData += PrintUtilZebra
						.PrintNext("----------------");
				cpclData += "LEFT\r\n";

				cpclData += PrintUtilZebra.PrintNextData2("  UPTO       ",
						String.format("%s", UtilAppCommon.out.AmtPayableUptoDt), String.format("%s", UtilAppCommon.out.AmtPayableUptoAmt));

				cpclData += PrintUtilZebra.PrintNextData2(" BY       ",
						String.format("%s", UtilAppCommon.out.AmtPayablePYDt), String.format("%s", UtilAppCommon.out.AmtPayablePYAmt));

				cpclData += PrintUtilZebra.PrintNextData2(" AFTER    ",
						String.format("%s", UtilAppCommon.out.AmtPayableAfterDt), String.format("%s", UtilAppCommon.out.AmtPayableAfterAmt));


				cpclData += PrintUtilZebra
						.PrintNext("**************************");

				cpclData += PrintUtilZebra.PrintNext("DETAILS OF LAST PAYMENT");
				cpclData += PrintUtilZebra
						.PrintNext("**************************");

				cpclData += PrintUtilZebra.PrintNextData1("   LAST PAID AMT     ",
						String.format("%10s", UtilAppCommon.out.LastPaymentAmt));


				cpclData += PrintUtilZebra.PrintNextData1("   LAST PAID DT      ",
						String.format("%10s", UtilAppCommon.out.LastPaidDate));


				cpclData += PrintUtilZebra.PrintNextData1("   RECEIPT NO        ",
						String.format("%12s", UtilAppCommon.out.ReceiptNumber));


				cpclData += PrintUtilZebra.PrintNextData1("   MTR RDR        ",
						String.format("%12s", UtilAppCommon.out.MTR_READER_ID));

				//dNow = new Date();
				//strDateTime = ft.format(dNow);
				cpclData += PrintUtilZebra.PrintNextData1("", String.format("Ver: %s", UtilAppCommon.strAppVersion.replace(".apk", "")));


				//cpclData += 

				cpclData += "ENDQR\r\nPRINT\r\n";

				cpclData += "PRINT\r\n";

				thePrinterConn.write(cpclData.getBytes());

				Thread.sleep(500);

				thePrinterConn.close();
				PrintUtilZebra.LineNo = 0;
				//startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));

			} catch (Exception e) {
				// Handle communications error here.
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();

			} finally {
				if (UtilAppCommon.bprintdupl)
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInputForDuplicateBill.class));
				if (UtilAppCommon.blActyncBtn)
					startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
					//else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
					//	startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("A"))
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("L"))
					startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("S"))
					startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("M"))
					startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
				else
					startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
			}

		}
	}

	/*class EpsonThermal extends Thread {

		private BluetoothDevice device = null;
		private String address = null;

		final int barcodeWidth = 2;
		final int barcodeHeight = 100;

		public EpsonThermal(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);
			Toast.makeText(getApplicationContext(), "Connected To:" + address,
					Toast.LENGTH_LONG).show();
		}

		public void run() {
			Print printer = new Print();
			int[] status = new int[1];
			int[] battery = new int[1];
			status[0] = 0;
			battery[0] = 0;

			SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yy'  TIME:'hh:mm");

			try {
				//UtilAppCommon.bprintdupl = false;
				Toast.makeText(getApplicationContext(), "Sending Data",
						Toast.LENGTH_LONG).show();
				Builder builder = new Builder("TM-P60", Builder.MODEL_ANK);
				builder.addCommand(new byte[] {0x1C, 0x28, 0x43, 0x02, 0x00, 0x30, 0x02});




				*//*Builder builder = new Builder("TM-P60", Builder.MODEL_ANK);

				builder.addCommand(new byte[] {0x1C, 0x28, 0x43, 0x02, 0x00, 0x30, 0x02});

				builder.addTextFont(Builder.FONT_A);
				*//*
				builder.addText(".\n");
				try {

					Drawable PhotoPath = getResources().getDrawable(R.drawable.chunav);

					System.out.println("Photo: " + PhotoPath);
					BitmapFactory bf = new BitmapFactory();
					builder.addImage(bf.decodeResource(getResources(), R.drawable.chunav), 0, 0, bf
									.decodeResource(getResources(), R.drawable.chunav).getWidth(),
							bf.decodeResource(getResources(), R.drawable.chunav).getHeight(),
							Builder.PARAM_DEFAULT);
					*//*builder.addImage(bf.decodeFile(PhotoPath), 0, 0, bf
									.decodeFile(PhotoPath).getWidth(),
							bf.decodeFile(PhotoPath).getHeight(),
							Builder.PARAM_DEFAULT);*//*
					Log.v("Azadi Photo Print Added", "Azadi Photo Print Added");
				} catch (Exception ex) {

					Log.v("Azadi Photo Print", ex.getMessage());
					System.out
							.println("Error In Azadi Photo Print: " + ex.toString());
				}
				builder.addTextFont(Builder.FONT_A);
				builder.addText(String.format("\n"));
				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addTextSize(2, 2);
				builder.addTextStyle(Builder.FALSE, Builder.FALSE,
						Builder.TRUE, Builder.PARAM_UNSPECIFIED);
				if( UtilAppCommon.bprintdupl)
					builder.addText("Duplicate Bill\n");

				builder.addText("\n");
				builder.addText("\n");

				builder.addCut(Builder.CUT_FEED);
				// <Send print data>

				printer.openPrinter(Print.DEVTYPE_BLUETOOTH, address,
						Print.TRUE, Print.PARAM_DEFAULT);
				printer.sendData(builder, 18000, status, battery);
				//startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));

				printer.closePrinter();
				//


			} catch (Exception e) {
				// Handle communications error here.
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e("Bill Print", e.getMessage());

			}
			finally {
				if (UtilAppCommon.bprintdupl)
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInputForDuplicateBill.class));
				if(UtilAppCommon.blActyncBtn)
					startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
					//else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
					//	startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
				else if(UtilAppCommon.billType.equalsIgnoreCase("A"))
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
		}
	}*/

	class EpsonThermal extends Thread {

		private BluetoothDevice device = null;
		private String address = null;

		final int barcodeWidth = 2;
		final int barcodeHeight = 100;

		public EpsonThermal(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);
			Toast.makeText(getApplicationContext(), "Connected To:" + address,
					Toast.LENGTH_LONG).show();
		}

		public void run() {
			Print printer = new Print();
			int[] status = new int[1];
			int[] battery = new int[1];
			status[0] = 0;
			battery[0] = 0;

			SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy'  TIME:'hh:mm");

			try {
				//UtilAppCommon.bprintdupl = false;
				Toast.makeText(getApplicationContext(), "Sending Data",
						Toast.LENGTH_LONG).show();
				Builder builder = new Builder("TM-P60", Builder.MODEL_ANK);
				builder.addCommand(new byte[]{0x1C, 0x28, 0x43, 0x02, 0x00, 0x30, 0x02});




				/*Builder builder = new Builder("TM-P60", Builder.MODEL_ANK);
				builder.addCommand(new byte[] {0x1C, 0x28, 0x43, 0x02, 0x00, 0x30, 0x02});
				builder.addTextFont(Builder.FONT_A);*/

				builder.addText(".\n");

				try {
					Drawable photoPathlogo = null;
					Bitmap azadi = Utilities.getBitmapFromDrawable(ActvBillPrinting.this, R.drawable.solar);
					//Bitmap azadi = BitmapFactory.decodeResource(getResources(), R.drawable.chunav);
					azadi = Bitmap.createScaledBitmap(azadi, 350, 220, true);
					builder.addImage(azadi, 0, 0, azadi.getWidth(), azadi.getHeight(), Builder.PARAM_DEFAULT);
					Log.v("Azadi Photo Print Added", "Azadi Photo Print Added");
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.v("Azadi Photo Print", ex.getMessage());
					System.out
							.println("Error In Azadi Photo Print: " + ex.toString());
				}
				builder.addTextFont(Builder.FONT_A);
				builder.addText(String.format("\n"));
				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addTextSize(2, 2);
				builder.addTextStyle(Builder.FALSE, Builder.FALSE,
						Builder.TRUE, Builder.PARAM_UNSPECIFIED);
				if (UtilAppCommon.bprintdupl)
					builder.addText("Duplicate Bill\n");
				UtilAppCommon.bprintdupl = false;
				builder.addText(UtilAppCommon.out.Company + "CL\n");

				builder.addTextStyle(Builder.FALSE, Builder.FALSE,
						Builder.FALSE, Builder.PARAM_UNSPECIFIED);
				builder.addTextSize(1, 1);
				builder.addText(" -------------------------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);
				//builder.addTextFont(Builder.FONT_C);
				//builder.addTextSize(2, 2);
				builder.addText(String.format(
						"  ELECTRICITY BILL:%s\n", UtilAppCommon.out.BillMonth));


				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 0");
				/**
				 * Adding lines below for change in tariff 2018-19
				 */
				String gstNo = "";
				if (UtilAppCommon.out.Company.equalsIgnoreCase("NBPD")) {
					gstNo = "10AAECN1588M2ZB";
				} else if (UtilAppCommon.out.Company.equalsIgnoreCase("SBPD")) {
					gstNo = "10AASCS2207G2ZN";
				}


				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 1");

				builder.addText(String.format(
						"  GSTIN:%s\n", gstNo));


				builder.addTextAlign(Builder.ALIGN_CENTER);
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 2");
				String hindiMessage = "प्रिय " + UtilAppCommon.out.Name + ",\n" + " कृपया विदयुत बकाया राशि\nरू "
						+ UtilAppCommon.out.AmtPayableUptoAmt.trim()
						+ " का भुगतान सुचना\nप्राप्ति के 15 दिनों के भीतर\nसुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के\nआलोक में दि."
						+ UtilAppCommon.out.AmtPayableUptoDt.trim() + "\nके पश्चात विदयुत सम्बन्ध विच्छेदित\nकर दिया जाएगा|" + "\n" +
						"                 स0 वि0 अभि0" + "\n";
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 3");
				try {

					if (Double.parseDouble(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
						Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 4");
						builder.addCommand(String.format(
								hindiMessage).getBytes("UTF-8"));

						Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 5");
						//	("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n")
					}
				} catch (NumberFormatException e) {
					try {
						Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 6");
						if (Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
							Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 7");
							builder.addCommand(String.format(
									hindiMessage).getBytes("UTF-8"));
							Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 8");
						}
					} catch (NumberFormatException e1) {
						e.printStackTrace();
					}
				}


/**
 * End adding lines for tariff change 2018-19
 */

				//cpclData += "UNDERLINE OFF\r\n";
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 9");
				builder.addText(" **************************\n");
				builder.addText(String.format(
						"  DATE: %s\n", UtilAppCommon.out.REC_DATE_TIME));
				builder.addText(String.format(
						"  DUE DATE: %s\n", UtilAppCommon.out.AmtPayableUptoDt));

				builder.addText(String.format(
						"  CONSUMER DETAILS\n"));
				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addText(" **************************\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);
				builder.addText(String.format(
						"  Bill No   : %s\n", UtilAppCommon.out.BillNo));

				builder.addText(String.format(
						"  DIVISION  : %s\n", UtilAppCommon.out.Division));

				builder.addText(String.format(
						"  SUB DIVN  : %s\n", UtilAppCommon.out.SubDivision));
				builder.addText(String.format(
						"  CA NUMBER :"));
				builder.addTextSize(2, 2);
				builder.addTextFont(Builder.FONT_C);
				//builder.addText(String.format(
				//		" CA NUMBER:%s\n", UtilAppCommon.out.CANumber));
				builder.addText(String.format(
						"%s\n", UtilAppCommon.out.CANumber));

				builder.addTextFont(Builder.FONT_A);
				builder.addTextSize(1, 1);
				builder.addText(String.format(
						"  LEGACY NO : %s\n", UtilAppCommon.out.LegacyNumber));

				builder.addText(String.format(
						"  MRU       : %s\n", UtilAppCommon.out.MRU));

				builder.addText(String.format(
						"  NAME      : %s\n", UtilAppCommon.in.CONSUMER_NAME));

				builder.addText(String.format(
						"  ADDRESS   :\n"));


				int alen = 0;
				alen = UtilAppCommon.out.Address.length();
				String addr1 = "";
				String addr2 = "";
				if (alen > 30) {
					addr1 = UtilAppCommon.out.Address.substring(0, 30);
					if (alen > 60)
						addr2 = UtilAppCommon.out.Address.substring(30, 60);
					else
						addr2 = UtilAppCommon.out.Address.substring(30, alen);
				} else
					addr1 = UtilAppCommon.out.Address;

				builder.addText(String.format(
						" %s\n", addr1));
				builder.addText(String.format(
						" %s\n\n", addr2));
				builder.addText(String.format(
						"  MOBILE NO  :   %s\n", UtilAppCommon.in.METER_CAP));
				builder.addText(String.format(
						"  AREA TYPE  :   %s\n", UtilAppCommon.out.Area_type));
				builder.addText(String.format(
						"  POLE NO    :   %s\n", UtilAppCommon.out.PoleNo));

				if (!UtilAppCommon.in.PWR_FACTOR.equalsIgnoreCase(""))
					builder.addText(String.format(
							"  MTR NO: %s  PH:%s\n", UtilAppCommon.out.MtrNo + ", " + UtilAppCommon.in.PWR_FACTOR, UtilAppCommon.out.Phase));
				else
					builder.addText(String.format(
							"  MTR NO: %s  PH:%s\n", UtilAppCommon.out.MtrNo, UtilAppCommon.out.Phase));

				builder.addText(String.format(
						"  MTR COMP : %s\n", UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("C") ? "Company" : UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("O") ? "Consumer" : ""));

				builder.addText(String.format(
						"  CATEGORY :   %s\n", UtilAppCommon.out.Category));

//					String CD = "";
//					String CD1 = "";
				String CD2 = "";
//					float val = 0;
//					if (UtilAppCommon.out.SanctLoad.length() > 0) {
//						val = Float.parseFloat(UtilAppCommon.out.SanctLoad);
//						CD = val + " KW";
//						CD1 = "";
//						CD2 = "";
//					} else if (UtilAppCommon.out.ConnectedLoad.length() > 0) {
//						val = Float.parseFloat(UtilAppCommon.out.ConnectedLoad);
//						CD = "";
//						CD1 = val + " HP";
//						CD2 = "";
//					} else if (UtilAppCommon.out.CD.length() > 0) {
//						val = Float.parseFloat(UtilAppCommon.out.CD);
//						CD = "";
//						CD1 = "";
				/**
				 * Adding lines for tariff change 2018-19
				 */
				double result = 0.0;
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 11");
				if (UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(B)")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IIM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTEV")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-ID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("PUBWW")) {
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 12");
					if(UtilAppCommon.out.CD!=null) {
						result = (double) (Double.parseDouble((UtilAppCommon.out.CD.isEmpty() || UtilAppCommon.out.CD.isBlank()) ? "0" : UtilAppCommon.out.CD) / 0.9f);
					}
					CD2 = String.format("%.2f", result) + " KVA";
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 13");
				}
				else if (UtilAppCommon.out.Category.equalsIgnoreCase("DS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("DS-IIID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(A)")) {
					result = (double) (Double.parseDouble(UtilAppCommon.out.CD));
					CD2 = result + " KW";
				}
				else if (UtilAppCommon.out.Category.equalsIgnoreCase("HGN")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IU")) {
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					CD2 = result + " HP";
				} else {
					/**
					 * End ading lines for tariff change
					 */
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 14");
					CD2 = result + " KW";
				}
				//}
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 15");
				builder.addText(String.format(
						" LOAD:%s \n", CD2));

				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 16");


				builder.addText(String.format(
						"  SD :  %s\n", UtilAppCommon.out.SD));

				String billdays;

				if (UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) Actual")) {
					billdays = UtilAppCommon.out.BillDays + "(" + UtilAppCommon.out.MESSAGE10 + ")";
				} else {
					billdays = UtilAppCommon.out.BillDays;
				}
				builder.addText(String.format(
						"  BILLED DAYS      : %s\n", billdays));

				builder.addText("****************************\n");


				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addText("READING DETAILS\n");
				builder.addText("----------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);

				builder.addText("\t PREVIOUS \tCURRENT\n");

				builder.addText(String.format(
						" READING: %s   %S\n", UtilAppCommon.out.PreviusReading, UtilAppCommon.out.CurrentReading));

				builder.addText(String.format(
						" DATE   : %s %s\n", UtilAppCommon.out.PrevusMtrRdgDt, UtilAppCommon.out.CurrentMtrRdgDt));
				if (!UtilAppCommon.out.PrevusMtrRdgDt.equalsIgnoreCase("0000.00.00"))
					builder.addText(String.format(
							" STATUS : %s \t\t %s\n", UtilAppCommon.out.PreviusMtrReadingNote, UtilAppCommon.out.CurrentMtrReadingNote));

				if (!UtilAppCommon.in.MONTH_SEASONAL.equalsIgnoreCase("")) {
					builder.addText("READING DETAILS\n");
					builder.addText("----------------\n");
					builder.addTextAlign(Builder.ALIGN_LEFT);

					builder.addText("\t PREVIOUS \tCURRENT\n");

					builder.addText(String.format(
							" READING: %s   %S\n", UtilAppCommon.in.PREV_KWH_CYCLE1, UtilAppCommon.SAPBlueIn.CurrentReadingKwh));

					builder.addText(String.format(
							" DATE   : %s %s\n", UtilAppCommon.in.DATE_1, UtilAppCommon.out.CurrentMtrRdgDt));
					if (!UtilAppCommon.in.DATE_1.equalsIgnoreCase("0000.00.00"))
						builder.addText(String.format(
								" STATUS : %s \t\t %s\n", "OK", "OK"));
				}


				// Print Reading Image
				if (UtilAppCommon.in.PRV_MTR_READING_NOTE.toUpperCase() != "RN") {
					try {
							/*String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
									.getPath()
									+ "/SBDocs/Photos_Crop"
									+ "/"
									+ UtilAppCommon.sdoCode
									+ "/"
									+ UtilAppCommon.out.MRU;*/
						String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/SBDocs/Photos/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/" + UtilAppCommon.in.MRU).getPath();
						strmonth = String.valueOf(Arrays.asList(strMonths).indexOf(UtilAppCommon.out.BillMonth) + 1);
						String PhotoPath = PhotoDir + "/" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
								UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
								"_" + UtilAppCommon.out.CANumber + ".jpg";
						System.out.println("Photo: " + PhotoPath);
						BitmapFactory bf = new BitmapFactory();
						Bitmap photobtm = bf.decodeFile(PhotoPath);
						photobtm = Bitmap.createScaledBitmap(photobtm, 350, 220, true);
						builder.addImage(photobtm, 0, 0, photobtm.getWidth(), photobtm.getHeight(), Builder.PARAM_DEFAULT);
							/*File file_crop = new File(PhotoDir, UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
									UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
									"_" + UtilAppCommon.in.CONTRACT_AC_NO + ".jpg");
							System.out.println("Photo: " + PhotoPath);
							Bitmap bitmap_crop=Utilities.getBitmapForAllVersions(getApplicationContext(),file_crop);
							builder.addImage(bitmap_crop, 0, 0, bitmap_crop.getWidth(),
									bitmap_crop.getHeight(),
									Builder.PARAM_DEFAULT);*/
					} catch (Exception ex) {

						Log.e("Bill Photo Print", ex.getMessage());
						System.out
								.println("Error In Photo Print: " + ex.toString());
					}
					// Print Reading Image End

				}
				builder.addText(String.format("\n"));

				float mf = 0, consump = 0;
				//mf=Float.parseFloat(UtilAppCommon.out.MF);
				if (!UtilAppCommon.out.MF.equalsIgnoreCase(""))
					mf = Float.parseFloat(UtilAppCommon.out.MF);
				consump = Float.parseFloat(UtilAppCommon.out.Consumption);

				builder.addText(String.format("MULTIPLYING FACTOR:  %.2f\n", mf));
				builder.addText(String.format("CONSUMPTION:  %.0f\n", consump));
				/**
				 * Adding lines below for tariff change 2018-19
				 */
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 17");
				if (UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(B)")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IIM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTEV")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-ID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("PUBWW")) {
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 18");
					builder.addText(String.format("  RECORDED DEMAND: %s\n", UtilAppCommon.out.RecordedDemd));
					builder.addText(String.format("POWAER FACTOR :%s\n", UtilAppCommon.out.PowerFactor));
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 19");
				} else {
					/**
					 * End adding lines for tariff change 2018-19
					 */
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 20");
					builder.addText(String.format("  RECORDED DEMAND: %s\n", UtilAppCommon.out.RecordedDemd));
					builder.addText(String.format(""));
					builder.addText(String.format("  POWAER FACTOR: %s\n", UtilAppCommon.out.PowerFactor));
					builder.addText(String.format(""));
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 21");
				}


				float mmcunits = 0, avg = 0;
				if (UtilAppCommon.out.Category.equals("DS-II") || UtilAppCommon.out.Category.equals("NDS-IM")) {
					mmcunits = 0;
				} else {
					mmcunits = Float.parseFloat(UtilAppCommon.out.MMCUnits);
				}
				if (UtilAppCommon.out.CurrentMtrReadingNote.equalsIgnoreCase("OK")) {
					avg = 0;
				} else {
					avg = Float.parseFloat(UtilAppCommon.out.Average);
				}

				builder.addText(String.format(
						"  MIN UNITS:%s\tAVG.:%s\n", mmcunits, avg));
				builder.addText(String.format(
						"  BLD UNITS:%s\tTYPE:%s\n", UtilAppCommon.out.BilledUnits, UtilAppCommon.out.Type));

				builder.addText(" ****************************\n");

				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addText("ARREAR DETAILS\n");
				builder.addText("----------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);

				float pmtonacct = 0, arrengdue = 0, arrdps = 0, arrothr = 0;
				pmtonacct = Float.parseFloat(UtilAppCommon.out.PaymentOnAccount);
				arrengdue = Float.parseFloat(UtilAppCommon.out.ArrearEnergyDues);
				arrdps = Float.parseFloat(UtilAppCommon.out.ArrearDPs);
				arrothr = Float.parseFloat(UtilAppCommon.out.ArrearOthers);

				builder.addText(String.format(
						"  PYMT ON ACCT      :   %.2f\n", pmtonacct));

				builder.addText(String.format(
						"  ENERGY DUES       :   %.2f\n", arrengdue));

				builder.addText(String.format(
						"  ARREAR DPS        :   %.2f\n", arrdps));

				builder.addText(String.format(
						"  OTHERS            :   %.2f\n", arrothr));

				builder.addText(String.format(
						"  SUB TOTAL(A)      :   %.2f\n", Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A)));


				builder.addText(" ****************************\n");

				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addText("CURRENT BILL DETAILS\n");
				builder.addText("----------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);

				float engchg = 0;
				engchg = Float.parseFloat(UtilAppCommon.out.CurrentEnergyCharges);

				float fixchg = 0, curdps = 0, excessdemdchg = 0, ed = 0, mr = 0, shuntcapchg = 0, othrchg = 0, subtotb = 0, govsubsidy = 0, cgst = 0, sgst = 0;
				float intonsd = 0, incentive = 0, rebonmmc = 0, grosstot = 0;

				curdps = Float.parseFloat(UtilAppCommon.out.CurrentMonthDps);
				fixchg = Float.parseFloat(UtilAppCommon.out.FixDemdCharge);
				excessdemdchg = Float.parseFloat(UtilAppCommon.out.ExcessDemdCharge);
				ed = Float.parseFloat(UtilAppCommon.out.ElectricityDuty);
				mr = Float.parseFloat(UtilAppCommon.out.MeterRent);
				/**
				 * Adding lines for tariff change 2018-19
				 */
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 22");
				cgst = Float.parseFloat(UtilAppCommon.out.METER_CGST);
				sgst = Float.parseFloat(UtilAppCommon.out.METER_SGST);
				/**
				 * End ading lines for tariff change
				 */
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 23");
				shuntcapchg = Float.parseFloat(UtilAppCommon.out.ShauntCapCharge);
				othrchg = Float.parseFloat(UtilAppCommon.out.OtherCharge);
				subtotb = Float.parseFloat(UtilAppCommon.out.SubTotal_B);
				govsubsidy = Float.parseFloat(UtilAppCommon.out.GOVT_SUB);
				if (UtilAppCommon.out.InterestOnSD_C.equalsIgnoreCase(""))
					intonsd = 0;
				else
					intonsd = Float.parseFloat(UtilAppCommon.out.InterestOnSD_C);
				if (UtilAppCommon.out.Incentive.equalsIgnoreCase(""))
					incentive = 0;
				else
					incentive = Float.parseFloat(UtilAppCommon.out.Incentive);

				rebonmmc = Float.parseFloat(UtilAppCommon.out.RebateOnMMC);
				grosstot = Float.parseFloat(UtilAppCommon.out.GrossTotal);

				float installmt_amt = 0;
				if (UtilAppCommon.in.CURR_MON_AMT.equalsIgnoreCase(""))
					installmt_amt = 0;
				else
					installmt_amt = Float.parseFloat(UtilAppCommon.in.CURR_MON_AMT);


				builder.addText(String.format(
						"  ENERGY CHARGES    :   %.2f\n", engchg));

				builder.addText(String.format(
						"  DPS               :   %.2f\n", curdps));

				builder.addText(String.format(
						"  FIXED/DEMD CHG    :   %.2f\n", fixchg));

				builder.addText(String.format(
						"  EXCESS DEMD CHG   :   %.2f\n", excessdemdchg));

				builder.addText(String.format(
						"  ELEC. DUTY        :   %.2f\n", ed));

				builder.addText(String.format(
						"  METER RENT        :   %.2f\n", mr));

				/**
				 * Adding New lines for tariff change 2018-19
				 */
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 24");
				builder.addText(String.format(
						"  CGST @ 9%%    	 : %.2f\n", cgst));
				builder.addText(String.format(
						"  SGST @ 9%%     	 : %.2f\n", sgst));
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 25");
				/**
				 * End adding lines for tariff change 2018-19
				 */

				builder.addText(String.format(
						"  SHUNT CAP. CHG    :   %.2f\n", shuntcapchg));

				builder.addText(String.format(
						"  INSTALLMT AMT     :   %.2f\n", installmt_amt));

				builder.addText(String.format(
						"  OTHER CHG         :   %.2f\n", othrchg));

				builder.addTextSize(1, 2);
				builder.addTextFont(Builder.FONT_A);

				builder.addText(String.format(
						"  STATE GOV SUBSIDY : %.2f\n", Float.parseFloat(UtilAppCommon.out.GOVT_SUB)));

				builder.addTextFont(Builder.FONT_A);
				builder.addTextSize(1, 1);


				builder.addText(String.format(
						"  SUB TOTAL(B)      :   %.2f\n", subtotb));

				builder.addText(" ****************************\n");

				builder.addText(String.format(
						"  INTEREST ON SD(C) :   %.2f\n", intonsd));

				builder.addText(String.format(
						"  INCENTIVE         :   %.2f\n", incentive));

				builder.addText(String.format(
						"  REMISSION         :   %.2f\n", rebonmmc));

				builder.addText(String.format(
						"  GROSS TOTAL(A+B+C):   %.2f\n", grosstot));

				builder.addText(" ****************************\n");

				builder.addText(String.format(
						"  REBATES     (-)   :   %s\n", UtilAppCommon.out.Rebate));


				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addText("AMOUNT PAYABLE\n");
				builder.addText("----------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);

				builder.addText(String.format(
						"  UPTO (%s)  :  %s\n", UtilAppCommon.out.AmtPayableUptoDt, UtilAppCommon.out.AmtPayableUptoAmt));

				builder.addText(String.format(
						"  PY   (%s)  :  %s\n", UtilAppCommon.out.AmtPayablePYDt, UtilAppCommon.out.AmtPayablePYAmt));

				builder.addText(String.format(
						"  AFTER(%s)  :  %s\n", UtilAppCommon.out.AmtPayableAfterDt, UtilAppCommon.out.AmtPayableAfterAmt));

				builder.addText(" ****************************\n");

				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addText("DETAILS OF LAST PAYMENT\n");
				builder.addText(" ----------------------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);

				builder.addText(String.format(
						"  LAST PAID AMT : %s\n", UtilAppCommon.out.LastPaymentAmt));

				builder.addText(String.format(
						"  LAST PAID DT  : %s\n", UtilAppCommon.out.LastPaidDate));

				builder.addText(String.format(
						"  RECEIPT NO    : %s\n", UtilAppCommon.out.ReceiptNumber));

				builder.addText(String.format(
						"   MTR RDR     : %s\n ", UtilAppCommon.out.MTR_READER_ID));


				//dNow = new Date();
				//strDateTime = ft.format(dNow);
				//builder.addText(String.format("DATE: %s\n", strDateTime));
				builder.addText(String.format("Ver: %s", UtilAppCommon.strAppVersion.replace(".apk", "")));

				builder.addText("\n");

				strBarcodeData = UtilAppCommon.acctNbr;
				//builder.addBarcode(strBarcodeData, Builder.BARCODE_JAN13 , Builder.HRI_NONE, Builder.FONT_A, 50, 100);
				//builder.addBarcode(strBarcodeData, Builder.BARCODE_CODE128 , Builder.HRI_NONE, Builder.FONT_A, 50, 100);
				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addBarcode(strBarcodeData, Builder.BARCODE_CODE39, Builder.HRI_BELOW, Builder.FONT_A, barcodeWidth, barcodeHeight);
				//nasha mukti image
//				Bitmap bt_nasha = Utilities.getBitmapFromDrawable(getApplicationContext(), R.drawable.nasha);
//				builder.addImage(bt_nasha, 0, 0, bt_nasha.getWidth(), bt_nasha.getHeight(), Builder.PARAM_DEFAULT);
				builder.addTextAlign(Builder.ALIGN_LEFT);
//				//builder.addBarcode(strBarcodeData, Builder.BARCODE_CODE93 , Builder.HRI_NONE, Builder.FONT_A, 50, 100);
				//builder.addBarcode(strBarcodeData, Builder.BARCODE_EAN8 , Builder.HRI_NONE, Builder.FONT_A, 50, 100);
				//builder.addBarcode(strBarcodeData, Builder.BARCODE_GS1_128 , Builder.HRI_NONE, Builder.FONT_A, 50, 100);

				builder.addText("\n");

				builder.addText(String.format("Consumer Helpline- 1912"));

				builder.addText("\n");

				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addText("*************************************** \n");
				builder.addText("Solar lagaen bijali bill bachaen " + "\n");
				builder.addText("*************************************** \n");
					/*builder.addTextAlign(Builder.ALIGN_LEFT);
					builder.addText("For application go to \n https://www.pmsuryaghar.gov.in \n");
					builder.addText("*************************************** \n");*/

				float flIntDisc = 0;
				if (!(UtilAppCommon.out.INT_DISC.equalsIgnoreCase("") || UtilAppCommon.out.INT_DISC.equalsIgnoreCase("null"))) {
					flIntDisc = Float.parseFloat(UtilAppCommon.out.INT_DISC);
					if (Math.abs(flIntDisc) > 0)
						builder.addText(String.format("Pay Online Rs.%s by %s to get Rs.%.2f extra rebate.", UtilAppCommon.out.AmtPayableUptoAmt,
								UtilAppCommon.out.AmtPayableUptoDt, Math.abs(flIntDisc)));
				}

				builder.addText("\n");
				builder.addText("\n");

				builder.addCut(Builder.CUT_FEED);
				// <Send print data>

				printer.openPrinter(Print.DEVTYPE_BLUETOOTH, address,
						Print.TRUE, Print.PARAM_DEFAULT);
				printer.sendData(builder, 18000, status, battery);
				//startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));

				printer.closePrinter();
				//


			} catch (Exception e) {
				// Handle communications error here.
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e("Bill Print", e.getMessage());

			} finally {
				if (UtilAppCommon.bprintdupl)
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInputForDuplicateBill.class));
				if (UtilAppCommon.blActyncBtn)
					startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
					//else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
					//	startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("A"))
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("L"))
					startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("S"))
					startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("M"))
					startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
				else
					startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
			}
		}
	}

	class AnalogicThermal extends Thread {

		private String address = null;


		public AnalogicThermal(String address) {
			this.address = address;
		}

		SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy' TIME:'hh:mm");

		@SuppressLint("SuspiciousIndentation")
		public void run() {

			try {

				AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();
				conn.openBT(address);

				Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();

				Toast.makeText(getBaseContext(), BILL, Toast.LENGTH_SHORT)
						.show();

				char lf = 0x0A;
				char cr = 0x0D;
				// char dp = 0x1D;
				// char nm = 0x13;

				// ////////Print On Paper Start////////////
				StringBuilder printerdata1 = new StringBuilder();
				StringBuilder printerdata2 = new StringBuilder();

				//Print part 1
				//Bitmap azadi = BitmapFactory.decodeResource(getResources(), R.drawable.chunav);
				//azadi = Bitmap.createScaledBitmap(azadi, 350, 220, true);
				//byte[] imagedata2=printer.prepareImageDataToPrint_VIP(address,azadi);
				//conn.printData(printerdata1.toString().getBytes());
				//if(imagedata2!=null)
				//{
				//conn.printData(imagedata2);
				//}
				if (UtilAppCommon.bprintdupl)
					//printerdata1.append(printer.font_Courier_10_VIP(String.format(
					//		"%s\n", "DUPLICATE\n   BILL")));

					printerdata1.append(printer.font_Courier_24_VIP(String.format(
							"   DUPLICATE  BILL\n")));
				UtilAppCommon.bprintdupl = false;
				printerdata1.append(printer.font_Courier_10_VIP(String.format(
						"%s\n", "  " + UtilAppCommon.out.Company + "CL")));


				String bmonth;
				bmonth = UtilAppCommon.out.BillMonth.substring(0, 4) + UtilAppCommon.out.BillMonth.substring(6, 8);
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"ELECTRICITY BILL:%s\n", bmonth)));

				/**
				 * Adding lines below for change in tariff 2018-19
				 */
				String gstNo = "";
				if (UtilAppCommon.out.Company.equalsIgnoreCase("NBPD")) {
					gstNo = "10AAECN1588M2ZB";
				} else if (UtilAppCommon.out.Company.equalsIgnoreCase("SBPD")) {
					gstNo = "10AASCS2207G2ZN";
				}

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"GSTIN:%s\n", gstNo)));

			/*printerdata2.append(printer.font_Courier_32_VIP(String.format(
					"OTHER CHG      : %12s\n",  UtilAppCommon.out.OtherCharge)));
			printerdata2.append(printer.font_Courier_32_VIP(String.format(
					"STATE GOVT SUBSIDY:%11s\n",  UtilAppCommon.out.GOVT_SUB)));
			printerdata2.append(printer.font_Double_Height_On_VIP());
			printerdata2.append(printer.font_Courier_32_VIP(String.format(
					"STATE GOVT SUBSIDY:%11s\n",  UtilAppCommon.out.GOVT_SUB)));
			printerdata2.append(printer.font_Courier_29_VIP(String.format(
					"STATE GOVT SUBSIDY:%s\n",  UtilAppCommon.out.GOVT_SUB)));
			printerdata2.append(printer.font_Double_Height_Off_VIP());
			conn.printData(printerdata1.toString().getBytes());


			conn.printData(printerdata2.toString().getBytes());
			Thread.sleep(3000);
			if(true) {
				conn.closeBT();
				return;
			}*/

			/*String hindiMessage="प्रिय "+ UtilAppCommon.out.Name + ",\n"+" कृपया विदयुत बकाया राशि\nरू "
					+ UtilAppCommon.out.AmtPayableUptoAmt.trim()
					+ " का भुगतान सुचना\nप्राप्ति के 15 दिनों के भीतर\nसुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के\nआलोक में दि."
					+ UtilAppCommon.out.AmtPayableUptoDt.trim() + "\nके पश्चात विदयुत सम्बन्ध विच्छेदित\nकर दिया जाएगा|" + "\n" +
					"                 स0 वि0 अभि0" + "\n";*/
			/*try {

				if (Double.parseDouble(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
				//	printerdata1.append(printer.font_Courier_24_VIP(String.format(hindiMessage)));
					//	("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n")
				}
			}
			catch (NumberFormatException e){
				try{
					if (Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
					//	printerdata1.append(printer.font_Courier_24_VIP(String.format(hindiMessage)));
					}
				}
				catch (NumberFormatException e1){

				}
			}
*/

/**
 * End adding lines for tariff change 2018-19
 */
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "***********************")));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"DATE:%s\n", UtilAppCommon.out.REC_DATE_TIME)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"DUE DATE:%s\n", UtilAppCommon.out.AmtPayableUptoDt)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "   CONSUMER DETAILS")));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "***********************")));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"Bill No  : %s\n", UtilAppCommon.out.BillNo)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"DIVISION : %s\n", UtilAppCommon.out.Division)));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"SUB DIVN : %s\n", UtilAppCommon.out.SubDivision)));
				printerdata1.append(printer.font_Courier_19_VIP(String.format(
						"CA NUMBER:%s\n", UtilAppCommon.out.CANumber)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"LEGACY NO: %s\n", UtilAppCommon.out.LegacyNumber)));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"MRU      : %s\n", UtilAppCommon.out.MRU)));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"NAME     : %s\n", UtilAppCommon.in.CONSUMER_NAME)));


				int alen = 0;
				alen = UtilAppCommon.out.Address.length();
				String addr1 = "";
				String addr2 = "";
				if (alen > 24) {
					addr1 = UtilAppCommon.out.Address.substring(0, 24);
					if (alen > 48)
						addr2 = UtilAppCommon.out.Address.substring(24, 48);
					else
						addr2 = UtilAppCommon.out.Address.substring(24, alen);
				} else
					addr1 = UtilAppCommon.out.Address;


				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "ADDRESS  : ")));


				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", addr1)));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", addr2)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"MOBILE NO  : %s\n", UtilAppCommon.in.METER_CAP)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"AREA TYPE  : %s\n", UtilAppCommon.out.Area_type)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"POLE NO    : %s\n", UtilAppCommon.out.PoleNo)));

				if (!UtilAppCommon.in.PWR_FACTOR.equalsIgnoreCase(""))
					printerdata1.append(printer.font_Courier_24_VIP(String.format(
							"MTR NO: %s PH:%s\n", UtilAppCommon.out.MtrNo + ", " + UtilAppCommon.in.PWR_FACTOR, UtilAppCommon.out.Phase)));
				else
					printerdata1.append(printer.font_Courier_24_VIP(String.format(
							"MTR NO: %s PH:%s\n", UtilAppCommon.out.MtrNo, UtilAppCommon.out.Phase)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"MTR COMP   : %s\n", UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("C") ? "Company" : UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("O") ? "Consumer" : "")));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"CATEGORY   : %s\n", UtilAppCommon.out.Category)));


				//String CD="";
				//String CD1="";
				String CD2 = "";
				float val = 0;
				double result = 0.0;
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 11");
				if (UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(B)")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IIM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTEV")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-ID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("PUBWW")) {
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 12");
					result = (double) (Double.parseDouble(UtilAppCommon.out.ConnectedLoad) / 0.9f);
					CD2 = String.format("%.2f", result) + "KVA";
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 13");
				} else if (UtilAppCommon.out.Category.equalsIgnoreCase("HGN")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IM")) {
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					CD2 = result + " HP";
				} else {
					/**
					 * End ading lines for tariff change
					 */
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 14");
					CD2 = result + " KW";
				}
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"LOAD:%s\n", CD2)));


				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"SD     : %s\n", UtilAppCommon.in.SECURITY_DEPOSIT)));


				String billdays;

				if (UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) Actual")) {
					billdays = UtilAppCommon.out.BillDays + "(" + UtilAppCommon.out.MESSAGE10 + ")";
				} else {
					billdays = UtilAppCommon.out.BillDays;
				}

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"BILLED DAYS  : %s\n", billdays)));


				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "***********************")));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "  READING DETAILS")));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "  ----------------")));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "     PREVIOUS    CURRENT")));


				printerdata1.append(printer.font_Courier_32_VIP(String.format(
						"READING   :  %s       %s\n", UtilAppCommon.out.PreviusReading, UtilAppCommon.out.CurrentReading)));

				printerdata1.append(printer.font_Courier_32_VIP(String.format(
						"DATE      :   %s   %s\n", UtilAppCommon.out.PrevusMtrRdgDt, UtilAppCommon.out.CurrentMtrRdgDt)));

				if (!UtilAppCommon.out.PrevusMtrRdgDt.equalsIgnoreCase("0000.00.00"))
					printerdata1.append(printer.font_Courier_32_VIP(String.format(
							"STATUS    :  %s       %s\n", UtilAppCommon.out.PreviusMtrReadingNote, UtilAppCommon.out.CurrentMtrReadingNote)));

				printerdata2.append(printer.font_Courier_24_VIP(".  \n"));

				if (!UtilAppCommon.in.MONTH_SEASONAL.equalsIgnoreCase("")) {
					printerdata1.append(printer.font_Courier_24_VIP(String.format(
							"%s\n", "***********************")));
					printerdata1.append(printer.font_Courier_24_VIP(String.format(
							"%s\n", " BLUETOOTH READING DETAILS")));
					printerdata1.append(printer.font_Courier_24_VIP(String.format(
							"%s\n", "  ----------------")));

					printerdata1.append(printer.font_Courier_24_VIP(String.format(
							"%s\n", "     PREVIOUS      CURRENT")));


					printerdata1.append(printer.font_Courier_32_VIP(String.format(
							"READING   :  %s        %s\n", UtilAppCommon.in.PREV_KWH_CYCLE1, UtilAppCommon.SAPBlueIn.CurrentReadingKwh)));

					printerdata1.append(printer.font_Courier_32_VIP(String.format(
							"DATE         :   %s   %s\n", UtilAppCommon.in.DATE_1, UtilAppCommon.out.CurrentMtrRdgDt)));

					//if(!UtilAppCommon.out.PrevusMtrRdgDt.equalsIgnoreCase("0000.00.00"))
					printerdata1.append(printer.font_Courier_32_VIP(String.format(
							"STATUS    :  %s         %s\n", "OK", "OK")));

				}
				printerdata2.append(printer.font_Courier_24_VIP(".  \n"));
				// 2nd block after image

				float mf = 0, consump = 0;
				//mf=Float.parseFloat(UtilAppCommon.out.MF);
				if (!UtilAppCommon.out.MF.equalsIgnoreCase(""))
					mf = Float.parseFloat(UtilAppCommon.out.MF);
				consump = Float.parseFloat(UtilAppCommon.out.Consumption);

				printerdata2.append(printer.font_Courier_24_VIP(String.format("MULTIPLYING FACTOR:%.2f", mf)));
				printerdata2.append(printer.font_Courier_24_VIP(String.format("\n")));
				printerdata2.append(printer.font_Courier_24_VIP(String.format("CONSUMPTION:%.2f", consump)));
				printerdata2.append(printer.font_Courier_24_VIP(String.format("\n")));
				/**
				 * Adding lines below for tariff change 2018-19
				 */
				if (UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(B)")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IIM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTEV")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-ID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("PUBWW")) {
					printerdata2.append(printer.font_Courier_24_VIP(String.format("RECORDED DEMD:%s", UtilAppCommon.out.RecordedDemd)));
					printerdata2.append(printer.font_Courier_24_VIP(String.format("\n")));
					printerdata2.append(printer.font_Courier_24_VIP(String.format("POWER FACTOR:%s", UtilAppCommon.out.PowerFactor)));
					printerdata2.append(printer.font_Courier_24_VIP(String.format("\n")));
				} else {
					/**
					 * End adding lines for tariff change 2018-19
					 */
					printerdata2.append(printer.font_Courier_24_VIP(String.format("RECORDED DEMD:%s", UtilAppCommon.out.RecordedDemd)));
					printerdata2.append(printer.font_Courier_24_VIP(String.format("\n")));
					printerdata2.append(printer.font_Courier_24_VIP(String.format("POWER FACTOR:%s", UtilAppCommon.out.PowerFactor)));
					printerdata2.append(printer.font_Courier_24_VIP(String.format("\n")));

				}
				float mmcunits = 0, avg = 0;
				//int mmcunits=0,avg=0;
				if (UtilAppCommon.out.Category.equals("DS-II") || UtilAppCommon.out.Category.equals("NDS-IM")) {
					mmcunits = 0;
				} else {
					mmcunits = Float.parseFloat(UtilAppCommon.out.MMCUnits);
					//mmcunits = Integer.parseInt(UtilAppCommon.out.MMCUnits);
				}


				if (UtilAppCommon.out.CurrentMtrReadingNote.equalsIgnoreCase("OK")) {
					avg = 0;
				} else {
					avg = Float.parseFloat(UtilAppCommon.out.Average);
					//avg = Integer.parseInt(UtilAppCommon.out.Average);
				}

				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"MIN UNIT:%.2f AVG:%.2f\n", mmcunits, avg)));
				//printerdata2.append(printer.font_Courier_24_VIP(String.format(
				//		"MIN UNIT:%d AVG:%d\n", mmcunits,avg)));
				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"BLD UNITS:%s TYPE:%s\n", UtilAppCommon.out.BilledUnits, UtilAppCommon.out.Type)));

				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "***********************")));
				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "   ARREAR DETAILS")));
				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "   ---------------")));

				float pmtonacct = 0, arrengdue = 0, arrdps = 0, arrothr = 0;
				pmtonacct = Float.parseFloat(UtilAppCommon.out.PaymentOnAccount);
				arrengdue = Float.parseFloat(UtilAppCommon.out.ArrearEnergyDues);
				arrdps = Float.parseFloat(UtilAppCommon.out.ArrearDPs);
				arrothr = Float.parseFloat(UtilAppCommon.out.ArrearOthers);

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"PYMT ON ACCT   :  %12.2f\n", pmtonacct)));
				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"ENERGY DUES    :  %12.2f\n", arrengdue)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"ARREAR DPS     :  %12.2f\n", arrdps)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"OTHERS         :  %12.2f\n", arrothr)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"SUB TOTAL(A)   :  %12.2f\n", Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A))));

				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "***********************")));

				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "  CURRENT BILL DETAILS")));
				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "  -------------------")));

				float engchg = 0;
				engchg = Float.parseFloat(UtilAppCommon.out.CurrentEnergyCharges);

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"ENERGY CHARGES : %12.2f\n", engchg)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"DPS            : %12.2f\n", Float.parseFloat(UtilAppCommon.out.CurrentMonthDps))));
				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"FIXED/DEMD CHG : %12s\n", UtilAppCommon.out.FixDemdCharge)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"EXCESS DEMDCHG : %12s\n", UtilAppCommon.out.ExcessDemdCharge)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"ELEC. DUTY     : %12s\n", UtilAppCommon.out.ElectricityDuty)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"METER RENT     : %12s\n", UtilAppCommon.out.MeterRent)));
				/**
				 * Adding New lines for tariff change 2018-19
				 */

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"CGST @ 9%%      : %12s\n", UtilAppCommon.out.METER_CGST)));
				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"SGST @ 9%%      : %12s\n", UtilAppCommon.out.METER_SGST)));

				/**
				 * End adding lines for tariff change 2018-19
				 */
				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"SHUNT CAP.CHG  : %12s\n", UtilAppCommon.out.ShauntCapCharge)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"INSTALLMT AMT  : %12s\n", UtilAppCommon.in.CURR_MON_AMT)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"OTHER CHG      : %12s\n", UtilAppCommon.out.OtherCharge)));

				/*printerdata2.append(printer.font_SansSerif_32_VIP(String.format(
						"STATE GOVT SUBSIDY:%11s\n",  UtilAppCommon.out.GOVT_SUB)));*/
				printerdata2.append(printer.font_Double_Height_On_VIP());
			/*printerdata2.append(printer.font_Courier_27_VIP(String.format(
					"STATE GOVT SUBSIDY:%s\n",  UtilAppCommon.out.GOVT_SUB)));
			printerdata2.append(printer.font_Courier_29_VIP(String.format(
					"STATE GOVT SUBSIDY:%s\n",  UtilAppCommon.out.GOVT_SUB)));*/
				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"STATE GOVT SUBSIDY: %s\n", UtilAppCommon.out.GOVT_SUB)));
				printerdata2.append(printer.font_Double_Height_Off_VIP());
				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"SUB TOTAL (B)  : %12s\n", UtilAppCommon.out.SubTotal_B)));

				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "***********************")));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"INT. ON SD(C)   : %12s\n", UtilAppCommon.out.InterestOnSD_C)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"INCENTIVE       : %12s\n", UtilAppCommon.out.Incentive)));
				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"REMISSION   : %12s\n", UtilAppCommon.out.RebateOnMMC)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"TOTAL(A+B+C)    : %12s\n", UtilAppCommon.out.GrossTotal)));

				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "***********************")));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"REBATE         :  %10s\n", UtilAppCommon.out.Rebate)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"%s\n", "   AMOUNT PAYABLE ")));

				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "   --------------")));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"UPTO :%10s  %9s\n", UtilAppCommon.out.AmtPayableUptoDt, UtilAppCommon.out.AmtPayableUptoAmt)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"BY   :%10s  %9s\n", UtilAppCommon.out.AmtPayablePYDt, UtilAppCommon.out.AmtPayablePYAmt)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"AFTER:%10s  %9s\n", UtilAppCommon.out.AmtPayableAfterDt, UtilAppCommon.out.AmtPayableAfterAmt)));


				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "***********************")));
				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"%s\n", "DETAILS OF LAST PAYMENT")));

				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "-----------------------")));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"LAST PAID AMT:%8s\n", UtilAppCommon.out.LastPaymentAmt)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"LAST PAID DT :%10s\n", UtilAppCommon.out.LastPaidDate)));
				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"RECEIPT NO   :%12s\n", UtilAppCommon.out.ReceiptNumber)));

				printerdata2.append(printer.font_Courier_32_VIP(String.format(
						"MTR RDR :%10s\n", UtilAppCommon.out.MTR_READER_ID)));

				printerdata2.append(printer.font_Courier_24_VIP(String.format(
						"Ver: %s\n\n", UtilAppCommon.strAppVersion.replace(".apk", ""))));

				strBarcodeData = UtilAppCommon.acctNbr;
				printerdata2.append(printer.barcode_Code_39_VIP(strBarcodeData));

				//015034547330000160026122016000016000
				//printerdata2.append(printer.barcode_Code_39_VIP("015034547330000160026122016000016000"));

				//printerdata2.append(printer.font_Courier_24_VIP(String.format("\n%s", " ")));

				printerdata2.append(printer.font_Courier_24_VIP(String.format("Consumer Helpline- 1912")));
				printerdata2.append(printer.font_Courier_24_VIP(String.format("%s\n", " ")));


				float flIntDisc = 0;
				if (!(UtilAppCommon.out.INT_DISC.equalsIgnoreCase("") || UtilAppCommon.out.INT_DISC.equalsIgnoreCase("null"))) {
					flIntDisc = Float.parseFloat(UtilAppCommon.out.INT_DISC);
					if (Math.abs(flIntDisc) > 0) {
						printerdata2.append(printer.font_Courier_24_VIP(String.format("Pay Online Rs.%s   by %s to get    Rs.%.2f extra rebate.\n", UtilAppCommon.out.AmtPayableUptoAmt,
								UtilAppCommon.out.AmtPayableUptoDt, Math.abs(flIntDisc))));
						printerdata2.append(printer.font_Courier_24_VIP(String.format("%s\n\n\n", " ")));
					}
				}


				// image
				if (UtilAppCommon.in.PRV_MTR_READING_NOTE.toUpperCase() != "RN") {
					// Print Reading Image
					//byte[] imagedata=null;
					//try {
					//String PhotoDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/SBDocs/Photos/"+UtilAppCommon.in.SUB_DIVISION_CODE+"/"+ UtilAppCommon.in.MRU)
					//.getPath();
					//strmonth = String.valueOf(Arrays.asList(strMonths).indexOf(UtilAppCommon.out.BillMonth) + 1);
					//String PhotoPath = PhotoDir + "/" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
					//UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
					//"_" + UtilAppCommon.out.CANumber  + ".jpg";
					//imagedata=printer.prepareImageDataToPrint_VIP(address,PhotoPath);
					//File file_crop = new File(PhotoPath);
					//Bitmap bitmap_crop=Utilities.getBitmapForAllVersions(context,file_crop);
					//imagedata=printer.prepareImageDataToPrint_VIP(address,bitmap_crop);
					//}catch (Exception e) {
					// TODO: handle exception
					//e.printStackTrace();
					//}


					conn.printData(printerdata1.toString().getBytes());

					//if(imagedata!=null)
					//{
					//conn.printData(imagedata);
					//}
					conn.printData(printerdata2.toString().getBytes());
					Thread.sleep(3000);
					conn.closeBT();
				}

			} catch (Exception e) {
				// Handle communications error here.
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();
				Log.e("BillPrinting", "Analogic Thermal ==>> " + e.getMessage());

			} finally {
				if (UtilAppCommon.bprintdupl)
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInputForDuplicateBill.class));
				if (UtilAppCommon.blActyncBtn)
					startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
//				else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
//					startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("A"))
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("L"))
					startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("S"))
					startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("M"))
					startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
				else
					startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
			}

		}
	}

	class EpsonThermalHindi extends Thread {

		private BluetoothDevice device = null;
		private String address = null;

		final int barcodeWidth = 2;
		final int barcodeHeight = 100;

		public EpsonThermalHindi(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);
			Toast.makeText(getApplicationContext(), "Connected To:" + address,
					Toast.LENGTH_LONG).show();
		}

		SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy' TIME:'hh:mm");

		@SuppressLint("SuspiciousIndentation")
		public void run() {
			Print printer = new Print();
			int[] status = new int[1];
			int[] battery = new int[1];
			status[0] = 0;
			battery[0] = 0;

			SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy'  TIME:'hh:mm");

			try {
				//UtilAppCommon.bprintdupl = false;
				Toast.makeText(getApplicationContext(), "Sending Data",
						Toast.LENGTH_LONG).show();
				Builder builder = new Builder("TM-P60", Builder.MODEL_ANK);

				builder.addCommand(new byte[]{0x1C, 0x28, 0x43, 0x02, 0x00, 0x30, 0x02});
				builder.addText("\n");

				try {

					//Drawable PhotoPath = getResources().getDrawable(R.drawable.chunav);

					//System.out.println("Photo: " + PhotoPath);
					//BitmapFactory bf = new BitmapFactory();
//					builder.addImage(bf.decodeResource(getResources(), R.drawable.chunav), 0, 0, bf
//									.decodeResource(getResources(), R.drawable.chunav).getWidth(),
//							bf.decodeResource(getResources(), R.drawable.chunav).getHeight(),
//							Builder.PARAM_DEFAULT);
					/*builder.addImage(bf.decodeFile(PhotoPath), 0, 0, bf
									.decodeFile(PhotoPath).getWidth(),
							bf.decodeFile(PhotoPath).getHeight(),
							Builder.PARAM_DEFAULT);*/
					Bitmap bt_logo = Utilities.getBitmapFromDrawable(getApplicationContext(), R.drawable.solar);
					builder.addImage(bt_logo, 0, 0, bt_logo.getWidth(), bt_logo.getHeight(), Builder.PARAM_DEFAULT);
					Log.v("Azadi Photo Print Added", "Azadi Photo Print Added");
				} catch (Exception ex) {

					Log.v("Azadi Photo Print", ex.getMessage());
					System.out
							.println("Error In Azadi Photo Print: " + ex.toString());
				}
				builder.addCommand(String.format("\n").getBytes("UTF-8"));

				builder.addTextFont(Builder.FONT_A);

				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addTextSize(2, 2);
				builder.addTextStyle(Builder.FALSE, Builder.FALSE,
						Builder.TRUE, Builder.PARAM_UNSPECIFIED);
				if (UtilAppCommon.bprintdupl)
					builder.addText("Duplicate Bill\n");
				UtilAppCommon.bprintdupl = false;
				builder.addText(UtilAppCommon.out.Company + "CL\n");

				builder.addTextStyle(Builder.FALSE, Builder.FALSE,
						Builder.FALSE, Builder.PARAM_UNSPECIFIED);
				builder.addTextSize(1, 1);
				builder.addText(" -------------------------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);


				String bmonth;
				bmonth = UtilAppCommon.out.BillMonth.substring(0, 4) + UtilAppCommon.out.BillMonth.substring(6, 8);
				//printerdata1.append(printer.font_Courier_24_VIP(String.format(
				//		"ELECTRICITY BILL:%s\n", bmonth)));
				builder.addCommand(String.format("विदयुत विपत्र माह : %s\n", bmonth).getBytes("UTF-8"));
				//builder.addText(String.format("बिधुत वील माह           : %s\n",bmonth));

				/**
				 * Adding lines below for change in tariff 2018-19
				 */
				String gstNo = "";
				if (UtilAppCommon.out.Company.equalsIgnoreCase("NBPD")) {
					gstNo = "10AAECN1588M2ZB";
				} else if (UtilAppCommon.out.Company.equalsIgnoreCase("SBPD")) {
					gstNo = "10AASCS2207G2ZN";
				}


				builder.addCommand(String.format(
						"जीएसटीआईएन:%s\n\n", gstNo).getBytes("UTF-8"));
				builder.addTextAlign(Builder.ALIGN_CENTER);
				String hindiMessage = "प्रिय " + UtilAppCommon.out.Name + ",\n" + " कृपया विदयुत बकाया राशि\nरू "
						+ UtilAppCommon.out.AmtPayableUptoAmt.trim()
						+ " का भुगतान सुचना\nप्राप्ति के 15 दिनों के भीतर\nसुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के\nआलोक में दि."
						+ UtilAppCommon.out.AmtPayableUptoDt.trim() + "\nके पश्चात विदयुत सम्बन्ध विच्छेदित\nकर दिया जाएगा|" + "\n" +
						"                 स0 वि0 अभि0" + "\n";
				try {

					if (Double.parseDouble(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
						builder.addCommand(String.format(
								hindiMessage).getBytes("UTF-8"));
						//	("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n")
					}
				} catch (NumberFormatException e) {
					try {
						if (Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
							builder.addCommand(String.format(
									hindiMessage).getBytes("UTF-8"));
						}
					} catch (NumberFormatException e1) {

					}
				}


/**
 * End adding lines for tariff change 2018-19
 */


				builder.addText(String.format("**************************\n"));

				builder.addCommand(String.format("दिनांक   : %s\n", UtilAppCommon.out.REC_DATE_TIME).getBytes("UTF-8"));
				builder.addCommand(String.format("दॆय  तिथि : %s\n", UtilAppCommon.out.AmtPayableUptoDt).getBytes("UTF-8"));

				builder.addCommand(String.format("कनेक्शन का विवरण  \n").getBytes("UTF-8"));
				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addText(" **************************\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);
				builder.addCommand(String.format("विपत्र संख्या       : %s\n", UtilAppCommon.out.BillNo).getBytes("UTF-8"));

				builder.addCommand(String.format("प्रमंडल/कोड    :%s\n", UtilAppCommon.out.Division).getBytes("UTF-8"));

				builder.addCommand(String.format("अवर प्रमंडल    :%s\n", UtilAppCommon.out.SubDivision).getBytes("UTF-8"));
				builder.addCommand(String.format("उपभोक्ता संख्या : ").getBytes("UTF-8"));
				builder.addTextSize(2, 2);
				builder.addTextFont(Builder.FONT_C);
				//builder.addText(String.format(
				//		" CA NUMBER:%s\n", UtilAppCommon.out.CANumber));
				builder.addText(String.format("%s\n", UtilAppCommon.out.CANumber));

				builder.addTextFont(Builder.FONT_A);
				builder.addTextSize(1, 1);
				builder.addCommand(String.format("कंज्युमर आईडी      :%s\n", UtilAppCommon.out.LegacyNumber).getBytes("UTF-8"));

				builder.addCommand(String.format("एम.आर.यू संख्या :%s\n", UtilAppCommon.out.MRU).getBytes("UTF-8"));

				builder.addCommand(String.format("नाम :%s\n", UtilAppCommon.in.CONSUMER_NAME).getBytes("UTF-8"));

				builder.addCommand(String.format("पता :").getBytes("UTF-8"));


				int alen = 0;
				alen = UtilAppCommon.out.Address.length();
				String addr1 = "";
				String addr2 = "";
				if (alen > 30) {
					addr1 = UtilAppCommon.out.Address.substring(0, 30);
					if (alen > 60)
						addr2 = UtilAppCommon.out.Address.substring(30, 60);
					else
						addr2 = UtilAppCommon.out.Address.substring(30, alen);
				} else
					addr1 = UtilAppCommon.out.Address;

				builder.addCommand(String.format(
						" %s\n", addr1).getBytes("UTF-8"));
				builder.addCommand(String.format(
						" %s\n", addr2).getBytes("UTF-8"));
				builder.addCommand(String.format("मोबाइल संख्या  :%s\n", UtilAppCommon.in.METER_CAP).getBytes("UTF-8"));
				builder.addCommand(String.format("क्षेत्र         :%s\n", UtilAppCommon.out.Area_type).getBytes("UTF-8"));
				builder.addCommand(String.format("पोल कोड     :%s\n", UtilAppCommon.out.PoleNo).getBytes("UTF-8"));
				if (!UtilAppCommon.in.PWR_FACTOR.equalsIgnoreCase(""))
					builder.addCommand(String.format("मीटर संख्या :%s  फेज:%s\n", UtilAppCommon.out.MtrNo + ", " + UtilAppCommon.in.PWR_FACTOR, UtilAppCommon.out.Phase).getBytes("UTF-8"));
				else
					builder.addCommand(String.format("मीटर संख्या :%s  फेज:%s\n", UtilAppCommon.out.MtrNo, UtilAppCommon.out.Phase).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"मीटर ओनर : %s\n", UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("C") ? "Company" : UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("O") ? "Consumer" : "").getBytes("UTF-8"));

				builder.addCommand(String.format(
						"उपभोक्ता श्रेणी : %s\n", UtilAppCommon.out.Category).getBytes("UTF-8"));

				String CD2 = "";
				double result = 0.0;
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 11");
				if (UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(B)")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IIM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTEV")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-ID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("PUBWW")) {
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 12");
					result = (double) (Double.parseDouble(UtilAppCommon.out.CD) / 0.9f);
					CD2 = String.format("%.2f", result) + " KVA";
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 13");
				} else if (UtilAppCommon.out.Category.equalsIgnoreCase("HGN")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IU")) {
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					CD2 = result + " HP";
				} else if (UtilAppCommon.out.Category.equalsIgnoreCase("DS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("DS-IIID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(A)")) {
					result = (double) (Double.parseDouble(UtilAppCommon.out.CD));
					CD2 = result + " KW";
				}else {
					/**
					 * End ading lines for tariff change
					 */
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 14");
					CD2 = result + " KW";
				}

				builder.addCommand(String.format(
						"स्वीकृत भार :%s\n", CD2).getBytes("UTF-8"));


				builder.addCommand(String.format(
						"जमानत की जमा राशि  :%s\n", UtilAppCommon.out.SD).getBytes("UTF-8"));

				String billdays;

				if (UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) Actual")) {
					billdays = UtilAppCommon.out.BillDays + "(" + UtilAppCommon.out.MESSAGE10 + ")";
				} else {
					billdays = UtilAppCommon.out.BillDays;
				}
				builder.addCommand(String.format(
						"कुल विपत्र दिवस  :%s\n", billdays).getBytes("UTF-8"));

				builder.addText("****************************\n");


				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addCommand("मान पठन स्थिति \n".getBytes("UTF-8"));
				builder.addCommand("-------------------------\n".getBytes("UTF-8"));
				builder.addTextAlign(Builder.ALIGN_LEFT);

				builder.addCommand("        पूर्व         वर्तमान  \n".getBytes("UTF-8"));

				builder.addCommand(String.format(
						"पठन   : %s     %s\n", UtilAppCommon.out.PreviusReading, UtilAppCommon.out.CurrentReading).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"दिनांक : %s  %s\n", UtilAppCommon.out.PrevusMtrRdgDt, UtilAppCommon.out.CurrentMtrRdgDt).getBytes("UTF-8"));
				if (!UtilAppCommon.out.PrevusMtrRdgDt.equalsIgnoreCase("0000.00.00"))
					builder.addCommand(String.format(
							"पठन स्थिति : %s   %s \n", UtilAppCommon.out.PreviusMtrReadingNote, UtilAppCommon.out.CurrentMtrReadingNote).getBytes("UTF-8"));


				if (!UtilAppCommon.in.MONTH_SEASONAL.equalsIgnoreCase("")) {
					builder.addText("****************************\n");
					builder.addTextAlign(Builder.ALIGN_CENTER);
					builder.addCommand("ब्ल्यूटुथ पठन स्थिति \n".getBytes("UTF-8"));
					builder.addCommand("-------------------------\n".getBytes("UTF-8"));
					builder.addTextAlign(Builder.ALIGN_LEFT);

					builder.addCommand("        पूर्व         वर्तमान  \n".getBytes("UTF-8"));

					builder.addCommand(String.format(
							"पठन   : %s     %s\n", UtilAppCommon.in.PREV_KWH_CYCLE1, UtilAppCommon.SAPBlueIn.CurrentReadingKwh).getBytes("UTF-8"));

					builder.addCommand(String.format(
							"दिनांक : %s  %s\n", UtilAppCommon.in.DATE_1, UtilAppCommon.out.CurrentMtrRdgDt).getBytes("UTF-8"));
					if (!UtilAppCommon.in.DATE_1.equalsIgnoreCase("0000.00.00"))
						builder.addCommand(String.format("बस्तु स्थिति : %s   %s \n", "OK", "OK").getBytes("UTF-8"));

				}

				// Print Reading Image
				if (UtilAppCommon.in.PRV_MTR_READING_NOTE.toUpperCase() != "RN") {
					try {
					/*String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
							.getPath()
							+ "/SBDocs/Photos_Crop"
							+ "/"
							+ UtilAppCommon.sdoCode
							+ "/"
							+ UtilAppCommon.out.MRU;*/
						String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/SBDocs/Photos/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/" + UtilAppCommon.in.MRU)
								.getPath();
						strmonth = String.valueOf(Arrays.asList(strMonths).indexOf(UtilAppCommon.out.BillMonth) + 1);
						String PhotoPath = PhotoDir + "/" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
								UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
								"_" + UtilAppCommon.out.CANumber + ".jpg";

						System.out.println("Photo: " + PhotoPath);
						//BitmapFactory bf = new BitmapFactory();

//					builder.addImage(bf.decodeFile(PhotoPath), 0, 0, bf
//							.decodeFile(PhotoPath).getWidth(),
//							bf.decodeFile(PhotoPath).getHeight(),
//							Builder.PARAM_DEFAULT);
						File filedir = new File(PhotoDir, UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
								UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
								"_" + UtilAppCommon.out.CANumber + ".jpg");
						Bitmap btmImg = Utilities.getBitmapForAllVersions(context, filedir);
						builder.addImage(btmImg, 0, 0, btmImg.getWidth(),
								btmImg.getHeight(),
								Builder.PARAM_DEFAULT);
					} catch (Exception ex) {

						System.out
								.println("Error In Photo Print: " + ex.toString());
					}
					// Print Reading Image End

				}
				builder.addCommand(String.format("\n").getBytes("UTF-8"));

				float mf = 0, consump = 0;
				//mf=Float.parseFloat(UtilAppCommon.out.MF);
				if (!UtilAppCommon.out.MF.equalsIgnoreCase(""))
					mf = Float.parseFloat(UtilAppCommon.out.MF);
				consump = Float.parseFloat(UtilAppCommon.out.Consumption);

				builder.addCommand(String.format(
						"गुणक    :%.2f खपत :%.2f\n", mf, consump).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"दर्ज मांग  :%s पीएफ्  :%s \n", UtilAppCommon.out.RecordedDemd, UtilAppCommon.out.PowerFactor).getBytes("UTF-8"));


				float mmcunits = 0, avg = 0;
				if (UtilAppCommon.out.Category.equals("DS-II") || UtilAppCommon.out.Category.equals("NDS-IM")) {
					mmcunits = 0;
				} else {
					mmcunits = Float.parseFloat(UtilAppCommon.out.MMCUnits);
				}
				if (UtilAppCommon.out.CurrentMtrReadingNote.equalsIgnoreCase("OK")) {
					avg = 0;
				} else {
					avg = Float.parseFloat(UtilAppCommon.out.Average);
				}

				builder.addCommand(String.format(
						"मासिक न्युनतम प्रभार:%.2f  \n", mmcunits).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"औसत    :%.2f \nकुल विपत्र दिवस   :%s\n", avg, UtilAppCommon.out.BilledUnits).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"विपत्र का आधार  :%s \n", UtilAppCommon.out.Type).getBytes("UTF-8"));


				builder.addText(" ****************************\n");

				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addCommand("बकाया का विवरण   \n".getBytes("UTF-8"));
				builder.addCommand("--------------------------\n".getBytes("UTF-8"));
				builder.addTextAlign(Builder.ALIGN_LEFT);

				float pmtonacct = 0, arrengdue = 0, arrdps = 0, arrothr = 0;
				pmtonacct = Float.parseFloat(UtilAppCommon.out.PaymentOnAccount);
				arrengdue = Float.parseFloat(UtilAppCommon.out.ArrearEnergyDues);
				arrdps = Float.parseFloat(UtilAppCommon.out.ArrearDPs);
				arrothr = Float.parseFloat(UtilAppCommon.out.ArrearOthers);

				builder.addCommand(String.format(
						"अग्रिम जमा         :%.2f\n", pmtonacct).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"ऊर्जा  बकाया        :%.2f\n", arrengdue).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"विलंबअधिभार बकाया   :%.2f\n", arrdps).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"अन्य  प्रभार         :%.2f\n", arrothr).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"कुल बकाया (अ)     :%.2f\n", Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A)).getBytes("UTF-8"));


				builder.addText(" ****************************\n");

				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addCommand("वर्तमान बिपत्र का विवरण       \n".getBytes("UTF-8"));
				builder.addCommand("---------------------------\n".getBytes("UTF-8"));
				builder.addTextAlign(Builder.ALIGN_LEFT);

				float engchg = 0;
				engchg = Float.parseFloat(UtilAppCommon.out.CurrentEnergyCharges);

				float fixchg = 0, curdps = 0, excessdemdchg = 0, ed = 0, mr = 0, shuntcapchg = 0, othrchg = 0, subtotb = 0, govsubsidy = 0, cgst = 0, sgst = 0;
				;
				float intonsd = 0, incentive = 0, rebonmmc = 0, grosstot = 0;

				curdps = Float.parseFloat(UtilAppCommon.out.CurrentMonthDps);
				fixchg = Float.parseFloat(UtilAppCommon.out.FixDemdCharge);
				excessdemdchg = Float.parseFloat(UtilAppCommon.out.ExcessDemdCharge);
				ed = Float.parseFloat(UtilAppCommon.out.ElectricityDuty);
				mr = Float.parseFloat(UtilAppCommon.out.MeterRent);
				/**
				 * Adding lines for tariff change 2018-19
				 */

				cgst = Float.parseFloat(UtilAppCommon.out.METER_CGST);
				sgst = Float.parseFloat(UtilAppCommon.out.METER_SGST);
				/**
				 * End ading lines for tariff change
				 */
				shuntcapchg = Float.parseFloat(UtilAppCommon.out.ShauntCapCharge);
				othrchg = Float.parseFloat(UtilAppCommon.out.OtherCharge);
				govsubsidy = Float.parseFloat(UtilAppCommon.out.GOVT_SUB);
				subtotb = Float.parseFloat(UtilAppCommon.out.SubTotal_B);
				if (UtilAppCommon.out.InterestOnSD_C.equalsIgnoreCase(""))
					intonsd = 0;
				else
					intonsd = Float.parseFloat(UtilAppCommon.out.InterestOnSD_C);
				if (UtilAppCommon.out.Incentive.equalsIgnoreCase(""))
					incentive = 0;
				else
					incentive = Float.parseFloat(UtilAppCommon.out.Incentive);

				rebonmmc = Float.parseFloat(UtilAppCommon.out.RebateOnMMC);
				grosstot = Float.parseFloat(UtilAppCommon.out.GrossTotal);

				float installmt_amt = 0;
				if (UtilAppCommon.in.CURR_MON_AMT.equalsIgnoreCase(""))
					installmt_amt = 0;
				else
					installmt_amt = Float.parseFloat(UtilAppCommon.in.CURR_MON_AMT);


				builder.addCommand(String.format(
						"ऊर्जा शुल्क          :%.2f\n", engchg).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"वर्तमान विलंब अधिभार:%.2f\n", curdps).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"फीक्सड/डिमांड प्रभार :%.2f\n", fixchg).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"आधिक्य डिमांड प्रभार :%.2f\n", excessdemdchg).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"विदयुत  शुल्क      :%.2f\n", ed).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"मीटर किराया       :%.2f\n", mr).getBytes("UTF-8"));
				/**
				 * Adding New lines for tariff change 2018-19
				 */
				builder.addCommand(String.format(
						"सीजीएसटी @ 9%%    : %.2f\n", cgst).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"एसजीएसटी @ 9%%    : %.2f\n", sgst).getBytes("UTF-8"));
				/**
				 * End adding lines for tariff change 2018-19
				 */
				builder.addCommand(String.format(
						"कैपिसिटर प्रभार     :%.2f\n", shuntcapchg).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"किस्त की राशि     :%.2f\n", installmt_amt).getBytes("UTF-8"));

				builder.addCommand(String.format(
						"अन्य शुल्क        :%.2f\n", othrchg).getBytes("UTF-8"));
				builder.addTextSize(1, 2);
				builder.addTextFont(Builder.FONT_A);
				builder.addCommand(String.format(
						"राज्य सरकार अनुदान:%.2f\n", govsubsidy).getBytes("UTF-8"));
				builder.addTextFont(Builder.FONT_A);
				builder.addTextSize(1, 1);
				builder.addCommand(String.format(
						"कुल अभिनिर्धारण (बी):%.2f\n", subtotb).getBytes("UTF-8"));
				builder.addText(" ****************************\n");
				builder.addCommand(String.format(
						"जमानत राशि पर सूद(सी):%.2f\n", intonsd).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"इन्सेंटिव्स          :%.2f\n", incentive).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"रीमिशन           :%.2f\n", rebonmmc).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"उप-जोड(ए+बी+सी) :%.2f\n", grosstot).getBytes("UTF-8"));
				builder.addText(" ***********************\n");
				builder.addCommand(String.format(
						"छूट की राशि    :%s\n", UtilAppCommon.out.Rebate).getBytes("UTF-8"));
				builder.addText(" ***********************\n");
				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addCommand("कूल मांग    \n".getBytes("UTF-8"));
				builder.addText("----------------------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);
				builder.addCommand(String.format(
						"%s तक रूo  : %s\n", UtilAppCommon.out.AmtPayableUptoDt, UtilAppCommon.out.AmtPayableUptoAmt).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"%s तक रूo  : %s\n", UtilAppCommon.out.AmtPayablePYDt, UtilAppCommon.out.AmtPayablePYAmt).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"%s पश्चात रूo : %s\n", UtilAppCommon.out.AmtPayableAfterDt, UtilAppCommon.out.AmtPayableAfterAmt).getBytes("UTF-8"));

				builder.addText("****************************\n");
				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addCommand("पिछले भुगतान का  विवरण     \n".getBytes("UTF-8"));
				builder.addText(" ----------------------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);
				builder.addCommand(String.format(
						"भुगतान की राशि  :%s\n", UtilAppCommon.out.LastPaymentAmt).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"भुगतान दिनांक    :%s\n", UtilAppCommon.out.LastPaidDate).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"रसीद  संख्या  :%s\n", UtilAppCommon.out.ReceiptNumber).getBytes("UTF-8"));
				builder.addCommand(String.format(
						"मीटर रीडर आईडी :%s\n", UtilAppCommon.out.MTR_READER_ID).getBytes("UTF-8"));
				//dNow = new Date();
				//strDateTime = ft.format(dNow);
				//builder.addCommand(String.format("DATE: %s\n", strDateTime));
				builder.addText(String.format("Ver: %s", UtilAppCommon.strAppVersion.replace(".apk", "")));
				builder.addText("\n");
				strBarcodeData = UtilAppCommon.acctNbr;
				//builder.addBarcode(strBarcodeData, Builder.BARCODE_JAN13 , Builder.HRI_NONE, Builder.FONT_A, 50, 100);
				//builder.addBarcode(strBarcodeData, Builder.BARCODE_CODE128 , Builder.HRI_NONE, Builder.FONT_A, 50, 100);
				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addBarcode(strBarcodeData, Builder.BARCODE_CODE39, Builder.HRI_BELOW, Builder.FONT_A, barcodeWidth, barcodeHeight);
				//nasha mukti msg
				//Bitmap bt_nasha = Utilities.getBitmapFromDrawable(getApplicationContext(), R.drawable.nasha);
				//builder.addImage(bt_nasha, 0, 0, bt_nasha.getWidth(), bt_nasha.getHeight(), Builder.PARAM_DEFAULT);
				//Log.v("Azadi Photo Print Added", "Azadi Photo Print Added");
				builder.addTextAlign(Builder.ALIGN_LEFT);
				//builder.addBarcode(strBarcodeData, Builder.BARCODE_CODE93 , Builder.HRI_NONE, Builder.FONT_A, 50, 100);
				//builder.addBarcode(strBarcodeData, Builder.BARCODE_EAN8 , Builder.HRI_NONE, Builder.FONT_A, 50, 100);
				//builder.addBarcode(strBarcodeData, Builder.BARCODE_GS1_128 , Builder.HRI_NONE, Builder.FONT_A, 50, 100);
				builder.addText("\n");
				builder.addText(String.format("Consumer Helpline- 1912"));
				builder.addText("\n");
				builder.addText("\n");
				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addText("*****************************\n");
				builder.addText("सोलर लगाए बिजली बिल बचाए ! \n");
				builder.addText("*****************************\n");
//					builder.addTextAlign(Builder.ALIGN_LEFT);
//					builder.addText("आवेदन हेतु https://www.pmsuryaghar.gov.in \n");
//					builder.addText("*****************************\n");

				float flIntDisc = 0;
				if (!(UtilAppCommon.out.INT_DISC.equalsIgnoreCase("") || UtilAppCommon.out.INT_DISC.equalsIgnoreCase("null"))) {
					flIntDisc = Float.parseFloat(UtilAppCommon.out.INT_DISC);
					if (Math.abs(flIntDisc) > 0)
						builder.addCommand(String.format("देय तिथि %s तक विपत्र राशि रू %s का ऑनलाईन भुगतान करेँ एव पाये रू"
								+ " %.2f अतिरिक्त छुट।", UtilAppCommon.out.AmtPayableUptoDt, UtilAppCommon.out.AmtPayableUptoAmt, Math.abs(flIntDisc)).getBytes("UTF-8"));
				}
				builder.addText("\n");
				builder.addText("\n");
				builder.addCut(Builder.CUT_FEED);
				// <Send print data>
				//builder.
				printer.openPrinter(Print.DEVTYPE_BLUETOOTH, address, Print.TRUE, Print.PARAM_DEFAULT);
				printer.sendData(builder, 21000, status, battery);
				//startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
				printer.closePrinter();
				//
			} catch (Exception e) {
				e.printStackTrace();
				// Handle communications error here.
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();
				Log.e("Epson Hindi Printing", e.getMessage().toString());

			} finally {
				if (UtilAppCommon.bprintdupl)
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInputForDuplicateBill.class));
				if (UtilAppCommon.blActyncBtn)
					startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
					//else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
					//	startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("A"))
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("L"))
					startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("S"))
					startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("M"))
					startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
				else
					startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
			}
		}
	}

	class AnalogicThermalHindi extends Thread {

		private String address = null;


		public AnalogicThermalHindi(String address) {
			this.address = address;
		}

		SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy' TIME:'hh:mm");

		public void run() {

			try {

				// AnalogicsThermalPrinterProf conn= new AnalogicsThermalPrinterProf();
				AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();

				conn.openBT(address);

				Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();


				Toast.makeText(getBaseContext(), BILL, Toast.LENGTH_SHORT)
						.show();

				char lf = 0x0A;
				char cr = 0x0D;
				// char dp = 0x1D;
				// char nm = 0x13;

				// ////////Print On Paper Start////////////
				StringBuilder printerdata1 = new StringBuilder();
				StringBuilder printerdata2 = new StringBuilder();
				StringBuilder printerdata3 = new StringBuilder();
				StringBuilder printerdata4 = new StringBuilder();
				StringBuilder printerdata5 = new StringBuilder();
				StringBuilder printerdata6 = new StringBuilder();
				StringBuilder printerdata7 = new StringBuilder();
				StringBuilder printerbarcode = new StringBuilder();
				//Print part 1


				String bmonth;
				bmonth = UtilAppCommon.out.BillMonth.substring(0, 4) + UtilAppCommon.out.BillMonth.substring(6, 8);

				printerdata1.append(String.format("बिधुत विपत्र माह    : %s\n", bmonth));

				/**
				 * Adding lines below for change in tariff 2018-19
				 */
				String gstNo = "";
				if (UtilAppCommon.out.Company.equalsIgnoreCase("NBPD")) {
					gstNo = "10AAECN1588M2ZB";
				} else if (UtilAppCommon.out.Company.equalsIgnoreCase("SBPD")) {
					gstNo = "10AASCS2207G2ZN";
				}


				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"जीएसटीसंo:%s\n", gstNo)));

				String hindiMessage = "प्रिय " + UtilAppCommon.out.Name + ",\n" + " कृपया विदयुत बकाया राशि\nरू "
						+ UtilAppCommon.out.AmtPayableUptoAmt.trim()
						+ " का भुगतान सुचना\nप्राप्ति के 15 दिनों के भीतर\nसुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के\nआलोक में दि."
						+ UtilAppCommon.out.AmtPayableUptoDt.trim() + "\nके पश्चात विदयुत सम्बन्ध विच्छेदित\nकर दिया जाएगा|" + "\n" +
						"                 स0 वि0 अभि0" + "\n";
				try {

					if (Double.parseDouble(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
						printerdata1.append((String.format(
								hindiMessage)));
						//	("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n")
					}
				} catch (NumberFormatException e) {
					try {
						if (Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
							printerdata1.append((String.format(
									hindiMessage)));
						}
					} catch (NumberFormatException e1) {

					}
				}


/**
 * End adding lines for tariff change 2018-19
 */
				printerdata1.append(String.format("********************************\n"));


				printerdata1.append(String.format("दिनांक              : %s\n", UtilAppCommon.out.REC_DATE_TIME));
				printerdata1.append(String.format("दॆय  तिथि          : %s\n", UtilAppCommon.out.AmtPayableUptoDt));

				printerdata1.append(String.format("कनेक्शन का  विवरण          \n"));
				printerdata1.append(String.format("********************************* \n"));
				printerdata1.append(String.format("विपत्र संख्या       : %s\n", UtilAppCommon.out.BillNo));
				printerdata1.append(String.format("प्रमंडल/कोड       :%s\n", UtilAppCommon.out.Division));
				printerdata1.append(String.format("अबर प्रमंडल     :%s", UtilAppCommon.out.SubDivision));

				printerdata2.append(String.format("कंज्युमर आईडी   :%s\n", UtilAppCommon.out.LegacyNumber));
				printerdata2.append(String.format("एम.आर.यू संख्या :%s\n", UtilAppCommon.out.MRU));
				printerdata2.append(String.format("नाम              :%s\n", UtilAppCommon.in.CONSUMER_NAME));

				int alen = 0;
				alen = UtilAppCommon.out.Address.length();
				String addr1 = "";
				String addr2 = "";
				if (alen > 24) {
					addr1 = UtilAppCommon.out.Address.substring(0, 24);
					if (alen > 48)
						addr2 = UtilAppCommon.out.Address.substring(24, 48);
					else
						addr2 = UtilAppCommon.out.Address.substring(24, alen);
				} else
					addr1 = UtilAppCommon.out.Address;

				printerdata2.append(String.format("पता : "));
				printerdata2.append(String.format(" %s\n", addr1));
				printerdata2.append(String.format("%s\n", addr2));
				printerdata2.append(String.format("मोबाइल संख्या  :%s\n", UtilAppCommon.in.METER_CAP));
				printerdata2.append(String.format("क्षेत्र                :%s\n", UtilAppCommon.out.Area_type));
				printerdata2.append(String.format("पोल कोड        :%s\n", UtilAppCommon.out.PoleNo));
				if (!UtilAppCommon.in.PWR_FACTOR.equalsIgnoreCase(""))
					printerdata2.append(String.format("मीटर का नाम :%s फेज:%s\n", UtilAppCommon.out.MtrNo + ", " + UtilAppCommon.in.PWR_FACTOR, UtilAppCommon.out.Phase));
				else
					printerdata2.append(String.format("मीटर का नाम :%s फेज:%s\n", UtilAppCommon.out.MtrNo, UtilAppCommon.out.Phase));

				printerdata2.append(String.format(
						"मीटर ओनर         : %s\n", UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("C") ? "Company" : UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("O") ? "Consumer" : ""));
				printerdata2.append(String.format("उपभोक्ता श्रेणी     :%s\n", UtilAppCommon.out.Category));

				String CD = "";
				String CD1 = "";
				String CD2 = "";
				float val = 0;
				if (UtilAppCommon.out.SanctLoad.length() > 0) {
					val = Float.parseFloat(UtilAppCommon.out.SanctLoad);
					CD = val + " KW";
					CD1 = "";
					CD2 = "";
				} else if (UtilAppCommon.out.ConnectedLoad.length() > 0) {
					val = Float.parseFloat(UtilAppCommon.out.ConnectedLoad);
					CD = "";
					CD1 = val + " HP";
					CD2 = "";
				} else if (UtilAppCommon.out.CD.length() > 0) {
					val = Float.parseFloat(UtilAppCommon.out.CD);
					CD = "";
					CD1 = "";
					/**
					 * Adding lines for tariff change 2018-19
					 */
					if (UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-ID")
							|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-IID")
							|| UtilAppCommon.out.Category.equalsIgnoreCase("PUBWW")) {


						CD2 = val + " KW" + "-" + Math.ceil((double) (val / 0.9f)) + "KVA";
					} else {
						/**
						 * End ading lines for tariff change
						 */
						CD2 = val + " KW";
					}
				}

				printerdata2.append(String.format("स्वीकृत भार:%s %s संविदा मांग :%s\n", CD, CD1, CD2));
				printerdata2.append(String.format("जमानत की  जमा राशि   :%s\n", UtilAppCommon.in.SECURITY_DEPOSIT));

				String billdays;

				if (UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) Actual")) {
					billdays = UtilAppCommon.out.BillDays + "(" + UtilAppCommon.out.MESSAGE10 + ")";
				} else {
					billdays = UtilAppCommon.out.BillDays;
				}
				printerdata2.append(String.format("कुल विपत्र दीवस            :%s\n", billdays));
				printerdata2.append(String.format("********************************* \n"));
				printerdata2.append(String.format("पठन स्थिति    :\n"));
				printerdata2.append(String.format("मीटर   पठन  का  विवरण :\n"));
				printerdata2.append(String.format(" -------------------------------\n"));
				printerdata2.append(String.format("          पूर्व                         वर्तमान  \n"));
				printerdata2.append(String.format("पठन      : %s       %s \n", UtilAppCommon.out.PreviusReading, UtilAppCommon.out.CurrentReading));
				printerdata2.append(String.format("दिनांक   : %s  %s \n", UtilAppCommon.out.PrevusMtrRdgDt, UtilAppCommon.out.CurrentMtrRdgDt));
				if (!UtilAppCommon.out.PrevusMtrRdgDt.equalsIgnoreCase("0000.00.00"))
					printerdata2.append(String.format("बस्तु स्थिति   : %s            %s \n", UtilAppCommon.out.PreviusMtrReadingNote, UtilAppCommon.out.CurrentMtrReadingNote));

				if (!UtilAppCommon.in.MONTH_SEASONAL.equalsIgnoreCase("")) {
					printerdata2.append(String.format("********************************* \n"));
					printerdata2.append(String.format("पठन स्थिति    :\n"));
					printerdata2.append(String.format("ब्ल्यूटुथ  मीटर   पठन  का  विवरण :\n"));
					printerdata2.append(String.format(" -------------------------------\n"));
					printerdata2.append(String.format("          पूर्व                         वर्तमान  \n"));
					printerdata2.append(String.format("पठन      : %s       %s \n", UtilAppCommon.in.PREV_KWH_CYCLE1, UtilAppCommon.SAPBlueIn.CurrentReadingKwh));
					printerdata2.append(String.format("दिनांक   : %s  %s \n", UtilAppCommon.in.DATE_1, UtilAppCommon.out.CurrentMtrRdgDt));
					if (!UtilAppCommon.in.DATE_1.equalsIgnoreCase("0000.00.00"))
						printerdata2.append(String.format("बस्तु स्थिति   : %s            %s \n", "OK", "OK"));
				}

				// 2nd block after image


				float mf = 0, consump = 0;
				//mf=Float.parseFloat(UtilAppCommon.out.MF);
				if (!UtilAppCommon.out.MF.equalsIgnoreCase(""))
					mf = Float.parseFloat(UtilAppCommon.out.MF);
				consump = Float.parseFloat(UtilAppCommon.out.Consumption);

				printerdata3.append(String.format("गुणक     : %.2f     खपत :%.2f \n", mf, consump));
				printerdata3.append(String.format("दर्ज मांग  :%s     पीएफ्  :%s \n", UtilAppCommon.out.RecordedDemd, UtilAppCommon.out.PowerFactor));


				float mmcunits = 0, avg = 0;
				if (UtilAppCommon.out.Category.equals("DS-II") || UtilAppCommon.out.Category.equals("NDS-IM")) {
					mmcunits = 0;
				} else {
					mmcunits = Float.parseFloat(UtilAppCommon.out.MMCUnits);
				}


				if (UtilAppCommon.out.CurrentMtrReadingNote.equalsIgnoreCase("OK")) {
					avg = 0;
				} else {
					avg = Float.parseFloat(UtilAppCommon.out.Average);

				}

				printerdata3.append(String.format("मासिक न्युनतम प्रभारकार्ड          :%.2f \n", mmcunits));
				printerdata3.append(String.format("औसत   :%.2f  बिल इकाई  :%s\n", avg, UtilAppCommon.out.BilledUnits));
				printerdata3.append(String.format("विपत्र का आधार  :%s \n", UtilAppCommon.out.Type));
				printerdata3.append(String.format("*******************************\n"));
				printerdata3.append(String.format("बकाया का विवरण \n"));

				printerdata3.append(String.format(" ------------------------------\n"));
				float pmtonacct = 0, arrengdue = 0, arrdps = 0, arrothr = 0;
				pmtonacct = Float.parseFloat(UtilAppCommon.out.PaymentOnAccount);
				arrengdue = Float.parseFloat(UtilAppCommon.out.ArrearEnergyDues);
				arrdps = Float.parseFloat(UtilAppCommon.out.ArrearDPs);
				arrothr = Float.parseFloat(UtilAppCommon.out.ArrearOthers);


				printerdata3.append(String.format("अग्रिम जमा                    :%12.2f\n", pmtonacct));
				printerdata3.append(String.format("ऊर्जा  बकाया                 :%12.2f\n", arrengdue));
				printerdata3.append(String.format("विलंब  अधिभार  बकाया  :%12.2f\n", arrdps));
				printerdata3.append(String.format("अन्य  प्रभार                    :%12.2f\n", arrothr));
				printerdata3.append(String.format("कुल  बकाया (अ)         :%12.2f\n", Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A)));

				printerdata3.append(String.format("********************************* \n"));
				printerdata3.append(String.format("वर्तमान     बिपत्र का    विवरण                       \n"));
				printerdata3.append(String.format("********************************* \n"));

				float engchg = 0;
				engchg = Float.parseFloat(UtilAppCommon.out.CurrentEnergyCharges);


				printerdata3.append(String.format("ऊर्जा शुल्क                              : %10.2f\n", engchg));
				printerdata3.append(String.format("वर्तमान माह का विलंब अधिभार  : %10.2f\n", Float.parseFloat(UtilAppCommon.out.CurrentMonthDps)));
				printerdata3.append(String.format("फीक्सड चार्ज/डिमांड  चार्ज        : %10s\n", UtilAppCommon.out.FixDemdCharge));
				printerdata3.append(String.format("आधिक्य डिमांड प्रभार               : %10s\n", UtilAppCommon.out.ExcessDemdCharge));
				printerdata3.append(String.format("बिधुत  शुल्क                             : %10s\n", UtilAppCommon.out.ElectricityDuty));
				printerdata3.append(String.format("मीटर किराया                            : %10s\n", UtilAppCommon.out.MeterRent));
				/**
				 * Adding New lines for tariff change 2018-19
				 */

				printerdata3.append(String.format(
						" केoजीएसटी @ 9%%     :   %10s\n", UtilAppCommon.out.METER_CGST));
				printerdata3.append(String.format(
						" राoजीएसटी @ 9%%     :   %10s\n", UtilAppCommon.out.METER_SGST));

				/**
				 * End adding lines for tariff change 2018-19
				 */
				printerdata3.append(String.format("कैपिसिटर प्रभार                         : %10s\n", UtilAppCommon.out.ShauntCapCharge));
				printerdata3.append(String.format("किस्त की राशि                          : %10s\n", UtilAppCommon.in.CURR_MON_AMT));
				printerdata3.append(String.format("अन्य शुल्क                               : %10s", UtilAppCommon.out.OtherCharge));

				printerdata4.append(String.format("राज्य  सरकार अनुदान        :%10s", UtilAppCommon.out.GOVT_SUB));

				printerdata5.append(String.format("कुल   अभिनिर्धारण (बी)    :%12s\n", UtilAppCommon.out.SubTotal_B));


				printerdata5.append(String.format("******************************** \n"));

				printerdata5.append(String.format("जमानत  राशि    पर सूद   (सी) :%10s\n", UtilAppCommon.out.InterestOnSD_C));
				printerdata5.append(String.format("इन्सेंटिव्स                                    :%10s\n", UtilAppCommon.out.Incentive));
				printerdata5.append(String.format("रीमिशन                         :%10s\n", UtilAppCommon.out.RebateOnMMC));

				printerdata5.append(String.format("उप-जोड(ए+बी+सी)      :%10s\n", UtilAppCommon.out.GrossTotal));

				printerdata5.append(String.format("************************************* \n"));

				printerdata5.append(String.format("छूट की राशि                               :%10s\n", UtilAppCommon.out.Rebate));


				printerdata5.append(String.format("कूल मांग    \n"));

				printerdata5.append(String.format("******************************** \n"));

				printerdata5.append(String.format("%10s तक     :  %9s\n ", UtilAppCommon.out.AmtPayableUptoDt, UtilAppCommon.out.AmtPayableUptoAmt));

				printerdata5.append(String.format("%10s तक      :  %9s\n ", UtilAppCommon.out.AmtPayablePYDt, UtilAppCommon.out.AmtPayablePYAmt));

				printerdata5.append(String.format("%10s पश्चात   :  %9s\n ", UtilAppCommon.out.AmtPayableAfterDt, UtilAppCommon.out.AmtPayableAfterAmt));


				printerdata5.append(String.format("पीछले भुगतान का  विवरण    \n\n"));
				printerdata5.append(String.format("**************************** \n"));


				printerdata5.append(String.format("भुगतान    की   राशि            :%s\n", UtilAppCommon.out.LastPaymentAmt));


				printerdata5.append(String.format("भुगतान      दिनांक              :%s\n", UtilAppCommon.out.LastPaidDate));


				printerdata5.append(String.format("रसीद  संख्या  :%s\n", UtilAppCommon.out.ReceiptNumber));

				printerdata5.append(String.format("मीटर रीडर आईडी             :%s\n", UtilAppCommon.out.MTR_READER_ID));

				printerdata5.append(String.format("Ver             :%10s", UtilAppCommon.strAppVersion.replace(".apk", "")));


				strBarcodeData = UtilAppCommon.acctNbr;
				//printerdata4.append(printer.barcode_Code_39_VIP(strBarcodeData));

				//printerdata4.append(printer.font_Courier_24_VIP(String.format("Consumer Helpline- 1912")));
				//printerdata4.append(String.format("Consumer Helpline- 1912"));
				// image 
				printerdata6.append(String.format("Consumer Helpline- 1912 \n\n"));
				float flIntDisc = 0;
				if (!(UtilAppCommon.out.INT_DISC.equalsIgnoreCase("") || UtilAppCommon.out.INT_DISC.equalsIgnoreCase("null"))) {
					flIntDisc = Float.parseFloat(UtilAppCommon.out.INT_DISC);
					if (Math.abs(flIntDisc) > 0) {
						//printerdata6.append(String.format("देय  तिथि  %s तक  विपत्र  राशि  रू %s का ऑनलाईन  भुगतान करेँ एव पाये  "
						//		+ " रू %s अतिरिक्त  छुट",UtilAppCommon.out.AmtPayableUptoDt, UtilAppCommon.out.AmtPayableUptoAmt, Math.abs(flIntDisc)));	
						printerdata6.append(String.format("देय  तिथि  %s तक  विपत्र  राशि \n ", UtilAppCommon.out.AmtPayableUptoDt));
						printerdata6.append(String.format("रू %s का ऑनलाईन  भुगतान करेँ \n", UtilAppCommon.out.AmtPayableUptoAmt));
						printerdata6.append(String.format("एव पाये  रू %s अतिरिक्त  छुट", Math.abs(flIntDisc)));
					}
				}

				printerdata7.append(printer.font_Courier_24_VIP(String.format("%s\n\n\n", " ")));


				if (UtilAppCommon.in.PRV_MTR_READING_NOTE.toUpperCase() != "RN") {
					// Print Reading Image
					byte[] imagedata = null;
					try {
					/*String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
							.getPath()
							+ "/SBDocs/Photos_Crop"
							+ "/"
							+ UtilAppCommon.sdoCode
							+ "/"
							+ UtilAppCommon.out.MRU;*/
						String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/SBDocs/Photos/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/" + UtilAppCommon.in.MRU)
								.getPath();

						strmonth = String.valueOf(Arrays.asList(strMonths).indexOf(UtilAppCommon.out.BillMonth) + 1);
						String PhotoPath = PhotoDir + "/" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
								UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
								"_" + UtilAppCommon.out.CANumber + ".jpg";


						imagedata = printer.prepareImageDataToPrint_VIP(address, PhotoPath);
						//imagedata=printer.prepareImageDataToPrint_VIP(address,Utilities.getBitmapForAllVersions(getApplicationContext(),new File(PhotoPath)));
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

					if (UtilAppCommon.bprintdupl)
						conn.multiLinguallinePrint_ver_2_0_printer(address, String.format("      %s", "DUPLICATE BILL"), 30, Typeface.DEFAULT_BOLD);

					UtilAppCommon.bprintdupl = false;

					//conn.multiLinguallinePrint_ver_2_0_printer(address, String.format("          %s", UtilAppCommon.out.Company+"CL"), 40,Typeface.DEFAULT_BOLD);
					conn.multiLingualPrint_ver_2_0_printer(address, String.format("          %s", UtilAppCommon.out.Company + "CL"), 40, Typeface.DEFAULT_BOLD);

					conn.multiLingualPrint_ver_2_0_printer(address, printerdata1.toString(), 23, Typeface.SANS_SERIF);
					conn.multiLingualPrint_ver_2_0_printer(address, String.format("खाता संख्या     :%s", UtilAppCommon.out.CANumber), 30, Typeface.DEFAULT_BOLD);


					conn.multiLingualPrint_ver_2_0_printer(address, printerdata2.toString(), 23, Typeface.SANS_SERIF);


					if (imagedata != null) {
						conn.printData(imagedata);
					}
					conn.multiLingualPrint_ver_2_0_printer(address, printerdata3.toString(), 23, Typeface.SANS_SERIF);
					conn.multiLingualPrint_ver_2_0_printer(address, printerdata4.toString(), 27, Typeface.DEFAULT_BOLD);
					conn.multiLingualPrint_ver_2_0_printer(address, printerdata5.toString(), 23, Typeface.SANS_SERIF);


					//conn.printData(printerbarcode.toString().getBytes());
					printerbarcode.append(printer.barcode_Code_39_VIP(UtilAppCommon.out.CANumber));

					printerbarcode.append(String.format("%s\n", "."));
					conn.printData(printerbarcode.toString());

					conn.multiLingualPrint_ver_2_0_printer(address, printerdata6.toString(), 23, Typeface.SANS_SERIF);

					//conn.multiLinguallinePrint_ver_2_0_printer(address, String.format("      %s","."), 23,Typeface.DEFAULT_BOLD);
					conn.printData(printerdata7.toString().getBytes());

					//	conn.printData(printerdata4.toString().getBytes());
					Thread.sleep(1000);
					conn.closeBT();
				}

			} catch (Exception e) {
				// Handle communications error here.
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Hindi Printing Not Working Properly", Toast.LENGTH_LONG).show();
				Log.e("", e.getMessage());

			} finally {
				if (UtilAppCommon.bprintdupl)
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInputForDuplicateBill.class));
				if (UtilAppCommon.blActyncBtn)
					startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
//				else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
//					startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("A"))
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("L"))
					startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("S"))
					startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("M"))
					startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
				else
					startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
			}

		}
	}

	class TVSEnglish extends Thread {

		private String address = null;
		BluetoothDevice device = null;
		HPRTPrinterHelper hprtPrinterHelper = null;

		public TVSEnglish(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);

			if (mBluetoothAdapter == null) {
				Toast.makeText(ActvBillPrinting.this, "Bluetooth not available.", Toast.LENGTH_SHORT).show();

				return;
			}
			if (!mBluetoothAdapter.isEnabled()) {
				Toast.makeText(ActvBillPrinting.this,
						"Bluetooth feature is turned off now...Please turned it on! ", Toast.LENGTH_SHORT).show();
				if (ActivityCompat.checkSelfPermission(ActvBillPrinting.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				mBluetoothAdapter.enable();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}

			try {
				hprtPrinterHelper = new HPRTPrinterHelper();
				int portOpen = hprtPrinterHelper.PortOpen("Bluetooth," + address);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy' TIME:'hh:mm");

		public void run() {

			try {

				// AnalogicsThermalPrinterProf conn= new AnalogicsThermalPrinterProf();
				AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();

				conn.openBT(address);

				Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();


				Toast.makeText(getBaseContext(), BILL, Toast.LENGTH_SHORT)
						.show();

				char lf = 0x0A;
				char cr = 0x0D;
				// char dp = 0x1D;
				// char nm = 0x13;

				// ////////Print On Paper Start////////////
				StringBuilder printerdata1 = new StringBuilder();
				StringBuilder printerdata2 = new StringBuilder();
				StringBuilder printerdata3 = new StringBuilder();
				StringBuilder printerdata4 = new StringBuilder();
				StringBuilder printerdata5 = new StringBuilder();
				StringBuilder printerdata6 = new StringBuilder();
				StringBuilder printerdata7 = new StringBuilder();
				StringBuilder printerbarcode = new StringBuilder();
				//Print part 1


				String bmonth;
				bmonth = UtilAppCommon.out.BillMonth.substring(0, 4) + UtilAppCommon.out.BillMonth.substring(6, 8);

				printerdata1.append("ELECTRICITY BILL: " + bmonth + "\n");

				/**
				 * Adding lines below for change in tariff 2018-19
				 */
				String gstNo = "";
				if (UtilAppCommon.out.Company.equalsIgnoreCase("NBPD")) {
					gstNo = "10AAECN1588M2ZB";
				} else if (UtilAppCommon.out.Company.equalsIgnoreCase("SBPD")) {
					gstNo = "10AASCS2207G2ZN";
				}


				printerdata1.append(String.format("GSTIN:%s\n", gstNo));

				String hindiMessage = "प्रिय " + UtilAppCommon.out.Name + ",\n" + " कृपया विदयुत बकाया राशि\nरू "
						+ UtilAppCommon.out.AmtPayableUptoAmt.trim()
						+ " का भुगतान सुचना\nप्राप्ति के 15 दिनों के भीतर\nसुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के\nआलोक में दि."
						+ UtilAppCommon.out.AmtPayableUptoDt.trim() + "\nके पश्चात विदयुत सम्बन्ध\nविच्छेदित कर दिया जाएगा|" + "\n" +
						"                 स0 वि0 अभि0" + "\n";
				try {

					if (Double.parseDouble(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
						printerdata1.append((String.format(
								hindiMessage)));
						//	("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n")
					}
				} catch (NumberFormatException e) {
					try {
						if (Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
							printerdata1.append((String.format(hindiMessage)));
						}
					} catch (NumberFormatException e1) {

					}
				}


				//* End adding lines for tariff change 2018-19

				printerdata1.append(String.format("********************************\n"));


				printerdata1.append(String.format("DATE : %s\n", UtilAppCommon.out.REC_DATE_TIME));
				printerdata1.append(String.format("DUE DATE:  %s\n", UtilAppCommon.out.AmtPayableUptoDt));

				printerdata1.append(String.format("CONSUMER DETAILS \n"));
				printerdata1.append(String.format("********************************\n"));
				printerdata1.append(String.format("Bill No: %s\n", UtilAppCommon.out.BillNo));
				printerdata1.append(String.format("DIVISION:%s\n", UtilAppCommon.out.Division));
				printerdata1.append(String.format("SUB DIVN:%s\n", UtilAppCommon.out.SubDivision));
				printerdata1.append(String.format("CA NUMBER:%s\n", UtilAppCommon.out.CANumber));
				printerdata1.append(String.format("LEGACY NO:%s\n", UtilAppCommon.out.LegacyNumber));
				printerdata1.append(String.format("MRU :%s\n", UtilAppCommon.out.MRU));
				printerdata1.append(String.format("NAME :%s\n", UtilAppCommon.in.CONSUMER_NAME));

				int alen = 0;
				alen = UtilAppCommon.out.Address.length();
				String addr1 = "";
				String addr2 = "";
				if (alen > 24) {
					addr1 = UtilAppCommon.out.Address.substring(0, 24);
					if (alen > 48)
						addr2 = UtilAppCommon.out.Address.substring(24, 48);
					else
						addr2 = UtilAppCommon.out.Address.substring(24, alen);
				} else
					addr1 = UtilAppCommon.out.Address;

				printerdata1.append(String.format("ADDRESS: "));
				printerdata1.append(String.format(" %s\n", addr1));
				printerdata1.append(String.format("%s\n", addr2));
				printerdata1.append(String.format("MOBILE NO:%s\n", UtilAppCommon.in.METER_CAP));
				printerdata1.append(String.format("AREA TYPE:%s\n", UtilAppCommon.out.Area_type));
				printerdata1.append(String.format("POLE NO:%s\n", UtilAppCommon.out.PoleNo));
				if (!UtilAppCommon.in.PWR_FACTOR.equalsIgnoreCase(""))
					printerdata1.append(String.format("MTR NO:%s \nPHASE:%s\n", UtilAppCommon.out.MtrNo + ", " + UtilAppCommon.in.PWR_FACTOR, UtilAppCommon.out.Phase));
				else
					printerdata1.append(String.format("MTR NO:%s \nPHASE:%s\n", UtilAppCommon.out.MtrNo, UtilAppCommon.out.Phase));

				printerdata1.append(String.format("MTR COMP : %s\n", UtilAppCommon.out.MtrMake
						.equalsIgnoreCase("C") ? "Company" : UtilAppCommon.out.MtrMake
						.equalsIgnoreCase("O") ? "Consumer" : ""));
				printerdata1.append(String.format("CATEGORY :%s\n", UtilAppCommon.out.Category));

				//String CD="";
				//String CD1="";
				String CD2 = "";
				float val = 0;
				double result = 0.0;
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 11");
				if (UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(B)")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IIM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTEV")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-ID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("PUBWW")) {
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 12");
					result = (double) (Double.parseDouble(UtilAppCommon.out.CD) / 0.9f);
					CD2 = String.format("%.2f", result) + "KVA";
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 13");
				} else if (UtilAppCommon.out.Category.equalsIgnoreCase("HGN")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IU")) {
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					CD2 = result + " HP";
				} else if (UtilAppCommon.out.Category.equalsIgnoreCase("DS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("DS-IIID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(A)")) {
					result = (double) (Double.parseDouble(UtilAppCommon.out.CD));
					CD2 = result + " KW";
				}else {
					/**
					 * End ading lines for tariff change
					 */
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 14");
					CD2 = result + " KW";
				}

				//printerdata1.append(String.format("LOAD :%s\n",CD,CD1));
				printerdata1.append(String.format("LOAD :%s\n", CD2));
				printerdata1.append(String.format("SD :%s\n", UtilAppCommon.out.SD));

				String billdays;

				if (UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) Actual")) {
					billdays = UtilAppCommon.out.BillDays + "(" + UtilAppCommon.out.MESSAGE10 + ")";
				} else {
					billdays = UtilAppCommon.out.BillDays;
				}
				printerdata2.append(String.format("\nBILLED DAYS :%s\n", billdays));
				printerdata2.append(String.format("********************************\n"));
				printerdata2.append(String.format("READING DETAILS  \n"));
				printerdata2.append(String.format(" -----------------------------\n"));
				printerdata2.append(String.format("      PREVIOUS   CURRENT\n"));
				printerdata2.append(String.format("READING   : %s    %s\n", UtilAppCommon.out.PreviusReading, UtilAppCommon.out.CurrentReading));
				printerdata2.append(String.format("DATE : %s  %s\n", UtilAppCommon.out.PrevusMtrRdgDt, UtilAppCommon.out.CurrentMtrRdgDt));
				if (!UtilAppCommon.out.PrevusMtrRdgDt.equalsIgnoreCase("0000.00.00"))
					printerdata2.append(String.format("STATUS : %s          %s \n", UtilAppCommon.out.PreviusMtrReadingNote, UtilAppCommon.out.CurrentMtrReadingNote));

				if (!UtilAppCommon.in.MONTH_SEASONAL.equalsIgnoreCase("")) {
					printerdata2.append(String.format("******************************* \n"));
					printerdata2.append(String.format("BLUETOOTH READING DETAILS:\n"));
					//printerdata2.append(String.format("ब्ल्यूटुथ  मीटर   पठन  का  विवरण :\n"));
					printerdata2.append(String.format(" ------------------------------\n"));
					printerdata2.append(String.format("   PREVIOUS     CURRENT \n"));
					printerdata2.append(String.format("READING  : %s     %s \n", UtilAppCommon.in.PREV_KWH_CYCLE1, UtilAppCommon.SAPBlueIn.CurrentReadingKwh));
					printerdata2.append(String.format("DATE: %s  %s \n", UtilAppCommon.in.DATE_1, UtilAppCommon.out.CurrentMtrRdgDt));
					if (!UtilAppCommon.in.DATE_1.equalsIgnoreCase("0000.00.00"))
						printerdata2.append(String.format("STATUS : %s     %s \n", "OK", "OK"));
				}

				// 2nd block after image


				float mf = 0, consump = 0;
				//mf=Float.parseFloat(UtilAppCommon.out.MF);
				if (!UtilAppCommon.out.MF.equalsIgnoreCase(""))
					mf = Float.parseFloat(UtilAppCommon.out.MF);
				consump = Float.parseFloat(UtilAppCommon.out.Consumption);

				printerdata3.append(String.format("MULTIPLYING FACTOR : %.2f", mf));
				printerdata3.append(String.format("\n"));
				printerdata3.append(String.format("CONSUMPTION:%.2f", consump));
				printerdata3.append(String.format("\n"));
				printerdata3.append(String.format("RECORDED DEMD :%s ", UtilAppCommon.out.RecordedDemd));
				printerdata3.append(String.format("\n"));
				printerdata3.append(String.format("POWER FACTOR:%s", UtilAppCommon.out.PowerFactor));
				printerdata3.append(String.format("\n"));

				float mmcunits = 0, avg = 0;
				if (UtilAppCommon.out.Category.equals("DS-II") || UtilAppCommon.out.Category.equals("NDS-IM")) {
					mmcunits = 0;
				} else {
					mmcunits = Float.parseFloat(UtilAppCommon.out.MMCUnits);
				}


				if (UtilAppCommon.out.CurrentMtrReadingNote.equalsIgnoreCase("OK")) {
					avg = 0;
				} else {
					avg = Float.parseFloat(UtilAppCommon.out.Average);

				}

				printerdata3.append(String.format("MIN UNIT:%.2f \n", mmcunits));
				printerdata3.append(String.format("AVG :%.2f \nBLD UNITS :%s\n", avg, UtilAppCommon.out.BilledUnits));
				printerdata3.append(String.format("TYPE:%s \n", UtilAppCommon.out.Type));
				printerdata3.append(String.format("******************************\n"));
				printerdata3.append(String.format("    ARREAR DETAILS\n"));

				printerdata3.append(String.format(" -----------------------------\n"));
				float pmtonacct = 0, arrengdue = 0, arrdps = 0, arrothr = 0;
				pmtonacct = Float.parseFloat(UtilAppCommon.out.PaymentOnAccount);
				arrengdue = Float.parseFloat(UtilAppCommon.out.ArrearEnergyDues);
				arrdps = Float.parseFloat(UtilAppCommon.out.ArrearDPs);
				arrothr = Float.parseFloat(UtilAppCommon.out.ArrearOthers);


				printerdata3.append(String.format("PYMT ON ACCT %1.2f\n", pmtonacct));
				printerdata3.append(String.format("ENERGY DUES:%1.2f\n", arrengdue));
				printerdata3.append(String.format("ARREAR DPS:%1.2f\n", arrdps));
				printerdata3.append(String.format("OTHERS :%1.2f\n", arrothr));
				printerdata3.append(String.format("SUB TOTAL(A):%1.2f\n", Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A)));

				printerdata3.append(String.format("******************************\n"));
				printerdata3.append(String.format("CURRENT BILL DETAILS\n"));
				printerdata3.append(String.format("****************************** \n"));

				float engchg = 0;
				engchg = Float.parseFloat(UtilAppCommon.out.CurrentEnergyCharges);
				float fixchg = 0, curdps = 0, excessdemdchg = 0, ed = 0, mr = 0, shuntcapchg = 0, othrchg = 0, subtotb = 0, govsubsidy = 0, cgst = 0, sgst = 0;
				;
				float intonsd = 0, incentive = 0, rebonmmc = 0, grosstot = 0;

				curdps = Float.parseFloat(UtilAppCommon.out.CurrentMonthDps);
				fixchg = Float.parseFloat(UtilAppCommon.out.FixDemdCharge);
				excessdemdchg = Float.parseFloat(UtilAppCommon.out.ExcessDemdCharge);
				ed = Float.parseFloat(UtilAppCommon.out.ElectricityDuty);
				mr = Float.parseFloat(UtilAppCommon.out.MeterRent);
				/**
				 * Adding lines for tariff change 2018-19
				 */
				cgst = Float.parseFloat(UtilAppCommon.out.METER_CGST);
				sgst = Float.parseFloat(UtilAppCommon.out.METER_SGST);
				/**
				 * End ading lines for tariff change
				 */
				shuntcapchg = Float.parseFloat(UtilAppCommon.out.ShauntCapCharge);
				othrchg = Float.parseFloat(UtilAppCommon.out.OtherCharge);
				govsubsidy = Float.parseFloat(UtilAppCommon.out.GOVT_SUB);
				subtotb = Float.parseFloat(UtilAppCommon.out.SubTotal_B);
				if (UtilAppCommon.out.InterestOnSD_C.equalsIgnoreCase(""))
					intonsd = 0;
				else
					intonsd = Float.parseFloat(UtilAppCommon.out.InterestOnSD_C);
				if (UtilAppCommon.out.Incentive.equalsIgnoreCase(""))
					incentive = 0;
				else
					incentive = Float.parseFloat(UtilAppCommon.out.Incentive);

				rebonmmc = Float.parseFloat(UtilAppCommon.out.RebateOnMMC);
				grosstot = Float.parseFloat(UtilAppCommon.out.GrossTotal);

				float installmt_amt = 0;
				if (UtilAppCommon.in.CURR_MON_AMT.equalsIgnoreCase(""))
					installmt_amt = 0;
				else
					installmt_amt = Float.parseFloat(UtilAppCommon.in.CURR_MON_AMT);

				printerdata3.append(String.format("ENERGY CHARGES : %1.2f\n", engchg));
				printerdata3.append(String.format("DPS: %1.2f\n", curdps));
				printerdata3.append(String.format("FIXED/DEMD CHG: %1s\n", fixchg));
				printerdata3.append(String.format("EXCESS DEMDCHG: %1s\n", excessdemdchg));
				printerdata3.append(String.format("ELEC. DUTY    : %1s\n", ed));
				printerdata3.append(String.format("METER RENT    : %1s\n", mr));
				printerdata3.append(String.format("CGST @ 9%%    :%1s\n", cgst));
				printerdata3.append(String.format("SGST @ 9%%    :%1s\n", sgst));
				printerdata3.append(String.format("SHUNT CAP.CHG :%1s\n", shuntcapchg));
				printerdata3.append(String.format("INSTALLMT AMT :%1s\n", installmt_amt));
				printerdata3.append(String.format("OTHER CHG     :%1s\n", othrchg));
				/**
				 * Adding New lines for tariff change 2018-19
				 */


				/**
				 * End adding lines for tariff change 2018-19
				 */


				printerdata4.append(String.format("STATE GOVT SUBSIDY:%1s\n", govsubsidy));

				printerdata5.append(String.format("SUB TOTAL(B):%1s\n", subtotb));


				printerdata5.append(String.format("*****************************\n"));

				printerdata5.append(String.format("INT. ON SD(C):%1s\n", intonsd));
				printerdata5.append(String.format("INCENTIVE   :%1s\n", incentive));
				printerdata5.append(String.format("REMISSION   :%1s\n", rebonmmc));

				printerdata5.append(String.format("TOTAL(A+B+C):%1s\n", grosstot));

				printerdata5.append(String.format("******************************* \n"));

				printerdata5.append(String.format("REBATE :%10s\n", UtilAppCommon.out.Rebate));


				printerdata5.append(String.format("AMOUNT PAYABLE  \n"));

				printerdata5.append(String.format("*****************************\n"));

				printerdata5.append(String.format("%s UPTO  रूo :%1s\n ", UtilAppCommon.out.AmtPayableUptoDt, UtilAppCommon.out.AmtPayableUptoAmt));

				printerdata5.append(String.format("%s BY रूo :%1s\n ", UtilAppCommon.out.AmtPayablePYDt, UtilAppCommon.out.AmtPayablePYAmt));

				printerdata5.append(String.format("%s AFTER रूo:%1s\n ", UtilAppCommon.out.AmtPayableAfterDt, UtilAppCommon.out.AmtPayableAfterAmt));


				printerdata5.append(String.format("DETAILS OF LAST PAYMENT \n"));
				printerdata5.append(String.format("*************************** \n"));


				printerdata5.append(String.format("LAST PAID AMT:%s\n", UtilAppCommon.out.LastPaymentAmt));


				printerdata5.append(String.format("LAST PAID DT:%s\n", UtilAppCommon.out.LastPaidDate));


				printerdata5.append(String.format("RECEIPT NO:%s\n", UtilAppCommon.out.ReceiptNumber));

				printerdata5.append(String.format("MTR RDR :%s\n", UtilAppCommon.out.MTR_READER_ID));

				printerdata5.append(String.format("Ver     :%10s \n", UtilAppCommon.strAppVersion.replace(".apk", "")));


				strBarcodeData = UtilAppCommon.acctNbr;
				//printerdata4.append(printer.barcode_Code_39_VIP(strBarcodeData));

				//printerdata4.append(printer.font_Courier_24_VIP(String.format("Consumer Helpline- 1912")));
				//printerdata4.append(String.format("Consumer Helpline- 1912"));
				// image
				printerdata6.append(String.format("Consumer Helpline- 1912 \n\n"));
				if (UtilAppCommon.out.Company.equalsIgnoreCase("NBPD")) {
					printerdata6.append(String.format("********************************* \n"));
					printerdata6.append(String.format("Solar lagaen bijali bill bachaen " + "\n"));
					printerdata6.append(String.format("****************************** \n"));
					//printerdata6.append(String.format("For application go to https://www.pmsuryaghar.gov.in \n"));
				}
				float flIntDisc = 0;
				if (!(UtilAppCommon.out.INT_DISC.equalsIgnoreCase("") || UtilAppCommon.out.INT_DISC.equalsIgnoreCase("null"))) {
					flIntDisc = Float.parseFloat(UtilAppCommon.out.INT_DISC);
					if (Math.abs(flIntDisc) > 0) {
						printerdata6.append(String.format("Pay Online रूo.%s   by %s to get रूo.%.2f extra rebate.", UtilAppCommon.out.AmtPayableUptoDt, UtilAppCommon.out.AmtPayableUptoAmt, Math.abs(flIntDisc)));
						//printerdata6.append(String.format("देय  तिथि  %s तक  विपत्र  राशि  " ,UtilAppCommon.out.AmtPayableUptoDt));
						//printerdata6.append(String.format("रू %s का ऑनलाईन  भुगतान करेँ ",UtilAppCommon.out.AmtPayableUptoAmt));
						//printerdata6.append(String.format("एव पाये  रू %s अतिरिक्त  छुट",Math.abs(flIntDisc)));
					}
				}


				printerdata7.append(String.format("%s\n\n\n", " "));

				String PhotoPath = null;
				if (UtilAppCommon.in.PRV_MTR_READING_NOTE.toUpperCase() != "RN") {
					// Print Reading Image
					byte[] imagedata = null;
					try {
                        /*String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                .getPath()
                                + "/SBDocs/Photos_Crop"
                                + "/"
                                + UtilAppCommon.sdoCode
                                + "/"
                                + UtilAppCommon.out.MRU;*/
						String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/SBDocs/Photos/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/" + UtilAppCommon.in.MRU).getPath();
						strmonth = String.valueOf(Arrays.asList(strMonths).indexOf(UtilAppCommon.out.BillMonth) + 1);
						PhotoPath = PhotoDir + "/" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
								UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
								"_" + UtilAppCommon.out.CANumber + ".jpg";


						imagedata = printer.prepareImageDataToPrint_VIP(address, PhotoPath);


					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					//hprtPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00});
					tvsPrintImageAzadi();
					if (UtilAppCommon.bprintdupl) {
						//conn.multiLinguallinePrint_ver_2_0_printer(address, String.format("      %s", "DUPLICATE BILL"), 30, Typeface.DEFAULT_BOLD);
						hprtPrinterHelper.WriteData(String.format("   %s\n", "DUPLICATE BILL").getBytes("UTF-8"));
					}

					UtilAppCommon.bprintdupl = false;

					//conn.multiLinguallinePrint_ver_2_0_printer(address, String.format("          %s", UtilAppCommon.out.Company+"CL"), 40,Typeface.DEFAULT_BOLD);
					hprtPrinterHelper.WriteData(String.format("          %s\n", UtilAppCommon.out.Company + "CL").getBytes("UTF-8"));

					hprtPrinterHelper.WriteData(printerdata1.toString().getBytes("UTF-8"));
					hprtPrinterHelper.WriteData(String.format("CA NUMBER  :%s", UtilAppCommon.out.CANumber).getBytes("UTF-8"));


					hprtPrinterHelper.WriteData(printerdata2.toString().getBytes("UTF-8"));

					if (PhotoPath != null && PhotoPath.length() > 0) {
						tvsPrintImage(PhotoPath);
					}
                    /*if(imagedata!=null)
                    {
                        conn.printData(imagedata);
                    }*/
					hprtPrinterHelper.WriteData(printerdata3.toString().getBytes("UTF-8"));
					hprtPrinterHelper.WriteData(printerdata4.toString().getBytes("UTF-8"));
					hprtPrinterHelper.WriteData(printerdata5.toString().getBytes("UTF-8"));


					//conn.printData(printerbarcode.toString().getBytes());
                    /*Bitmap btMap_bar1 = BarcodeCreater.creatBarcode(this, UtilAppCommon.out.CANumber.trim(), widht, height,
                            true, 1);*/
					//printerbarcode.append(printer.barcode_Code_39_VIP(UtilAppCommon.out.CANumber));

					//printerbarcode.append(String.format("%s\n","."));
					try {
						Bitmap btMap_bar1 = BarcodeCreater.creatBarcode(ActvBillPrinting.this, UtilAppCommon.out.CANumber.trim(), 380, 50,
								true, 1);
						btMap_bar1 = BitmapDeleteNoUseSpaceUtil.deleteNoUseWhiteSpace(btMap_bar1);
						//btMap_bar1 = zoomImg(btMap_bar1, 570, btMap_bar1.getHeight());
						if (btMap_bar1 == null) {
							Toast.makeText(ActvBillPrinting.this, "no barcode", Toast.LENGTH_SHORT).show();
							return;
						}
						try {
							HPRTPrinterHelper.PrintBitmap(btMap_bar1, (byte) 0, (byte) 0, 203);
							//HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					hprtPrinterHelper.WriteData(printerbarcode.toString().getBytes("UTF-8"));

					hprtPrinterHelper.WriteData(printerdata6.toString().getBytes("UTF-8"));

					//conn.multiLinguallinePrint_ver_2_0_printer(address, String.format("      %s","."), 23,Typeface.DEFAULT_BOLD);
					hprtPrinterHelper.WriteData(printerdata7.toString().getBytes("UTF-8"));

					//	conn.printData(printerdata4.toString().getBytes());
					Thread.sleep(1000);
					//conn.closeBT();
				}

			} catch (Exception e) {
				// Handle communications error here.
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "English Printing Not Working Properly", Toast.LENGTH_LONG).show();
				Log.e("", e.getMessage());

			} finally {
				if (UtilAppCommon.bprintdupl)
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInputForDuplicateBill.class));
				if (UtilAppCommon.blActyncBtn)
					startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
//				else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
//					startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("A"))
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("L"))
					startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("S"))
					startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("M"))
					startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
				else
					startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
			}

		}
	}


	class TVSHindi extends Thread {

		private String address = null;
		BluetoothDevice device = null;
		HPRTPrinterHelper hprtPrinterHelper = null;

		public TVSHindi(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);

			if (mBluetoothAdapter == null) {
				Toast.makeText(ActvBillPrinting.this, "Bluetooth not available.", Toast.LENGTH_SHORT).show();

				return;
			}
			if (!mBluetoothAdapter.isEnabled()) {
				Toast.makeText(ActvBillPrinting.this,
						"Bluetooth feature is turned off now...Please turned it on! ", Toast.LENGTH_SHORT).show();
				if (ActivityCompat.checkSelfPermission(ActvBillPrinting.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				mBluetoothAdapter.enable();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}

			hprtPrinterHelper = new HPRTPrinterHelper();
			try {
				int portOpen = hprtPrinterHelper.PortOpen("Bluetooth," + address);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}


		public void run() {
			SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy' TIME:'hh:mm");
			try {


				Toast.makeText(getBaseContext(), BILL, Toast.LENGTH_SHORT)
						.show();

				char lf = 0x0A;
				char cr = 0x0D;
				// char dp = 0x1D;
				// char nm = 0x13;

				// ////////Print On Paper Start////////////
				StringBuilder printerdata1 = new StringBuilder();
				StringBuilder printerdata2 = new StringBuilder();
				StringBuilder printerdata3 = new StringBuilder();
				StringBuilder printerdata4 = new StringBuilder();
				StringBuilder printerdata5 = new StringBuilder();
				StringBuilder printerdata6 = new StringBuilder();
				StringBuilder printerdata7 = new StringBuilder();
				StringBuilder printerbarcode = new StringBuilder();
				//Print part 1


				String bmonth;
				bmonth = UtilAppCommon.out.BillMonth.substring(0, 4) + UtilAppCommon.out.BillMonth.substring(6, 8);

				printerdata1.append("बिधुत विपत्र माह   : " + bmonth + "\n");

				/**
				 * Adding lines below for change in tariff 2018-19
				 */
				String gstNo = "";
				if (UtilAppCommon.out.Company.equalsIgnoreCase("NBPD")) {
					gstNo = "10AAECN1588M2ZB";
				} else if (UtilAppCommon.out.Company.equalsIgnoreCase("SBPD")) {
					gstNo = "10AASCS2207G2ZN";
				}


				printerdata1.append(String.format("जीएसटीआईएन:%s\n", gstNo));

				String hindiMessage = "प्रिय " + UtilAppCommon.out.Name + ",\n" + " कृपया विदयुत बकाया राशि\nरू "
						+ UtilAppCommon.out.AmtPayableUptoAmt.trim()
						+ " का भुगतान सुचना\nप्राप्ति के 15 दिनों के भीतर\nसुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के\nआलोक में दि."
						+ UtilAppCommon.out.AmtPayableUptoDt.trim() + "\nके पश्चात विदयुत सम्बन्ध\nविच्छेदित कर दिया जाएगा|" + "\n" +
						"                 स0 वि0 अभि0" + "\n";
				try {

					if (Double.parseDouble(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
						printerdata1.append((String.format(
								hindiMessage)));
						//	("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n")
					}
				} catch (NumberFormatException e) {
					try {
						if (Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A) > 1000) {
							printerdata1.append((String.format(hindiMessage)));
						}
					} catch (NumberFormatException e1) {

					}
				}


				//* End adding lines for tariff change 2018-19

				printerdata1.append(String.format("********************************\n"));


				printerdata1.append(String.format("दिनांक     : %s\n", UtilAppCommon.out.REC_DATE_TIME));
				printerdata1.append(String.format("दॆय तिथि   :  %s\n", UtilAppCommon.out.AmtPayableUptoDt));

				printerdata1.append(String.format("कनेक्शन का  विवरण  \n"));
				printerdata1.append(String.format("********************************\n"));
				printerdata1.append(String.format("विपत्र संख्या  : %s\n", UtilAppCommon.out.BillNo));
				printerdata1.append(String.format("प्रमंडल/कोड  :%s\n", UtilAppCommon.out.Division));
				printerdata1.append(String.format("अवर प्रमंडल  :%s\n", UtilAppCommon.out.SubDivision));
				printerdata1.append(String.format("उपभोक्ता संख्या:%s\n", UtilAppCommon.out.CANumber));
				printerdata1.append(String.format("कंज्युमर आईडी :%s\n", UtilAppCommon.out.LegacyNumber));
				printerdata1.append(String.format("एम.आर.यू संख्या :%s\n", UtilAppCommon.out.MRU));
				printerdata1.append(String.format("नाम :%s\n", UtilAppCommon.in.CONSUMER_NAME));

				int alen = 0;
				alen = UtilAppCommon.out.Address.length();
				String addr1 = "";
				String addr2 = "";
				if (alen > 24) {
					addr1 = UtilAppCommon.out.Address.substring(0, 24);
					if (alen > 48)
						addr2 = UtilAppCommon.out.Address.substring(24, 48);
					else
						addr2 = UtilAppCommon.out.Address.substring(24, alen);
				} else
					addr1 = UtilAppCommon.out.Address;

				printerdata1.append(String.format("पता : "));
				printerdata1.append(String.format(" %s\n", addr1));
				printerdata1.append(String.format("%s\n", addr2));
				printerdata1.append(String.format("मोबाइल संख्या:%s\n", UtilAppCommon.in.METER_CAP));
				printerdata1.append(String.format("क्षेत्र        :%s\n", UtilAppCommon.out.Area_type));
				printerdata1.append(String.format("पोल कोड    :%s\n", UtilAppCommon.out.PoleNo));
				if (!UtilAppCommon.in.PWR_FACTOR.equalsIgnoreCase(""))
					printerdata1.append(String.format("मीटर संख्या :%s फेज:%s\n", UtilAppCommon.out.MtrNo + ", " + UtilAppCommon.in.PWR_FACTOR, UtilAppCommon.out.Phase));
				else
					printerdata1.append(String.format("मीटर संख्या :%s फेज:%s\n", UtilAppCommon.out.MtrNo, UtilAppCommon.out.Phase));

				printerdata1.append(String.format("मीटर ओनर    : %s\n", UtilAppCommon.out.MtrMake
						.equalsIgnoreCase("C") ? "Company" : UtilAppCommon.out.MtrMake
						.equalsIgnoreCase("O") ? "Consumer" : ""));
				printerdata1.append(String.format("उपभोक्ता श्रेणी  :%s\n", UtilAppCommon.out.Category));

				/*String CD = "";
				String CD1 = "";
				String CD2 = "";
				float val = 0;
				if (UtilAppCommon.out.SanctLoad.length() > 0) {
					val = Float.parseFloat(UtilAppCommon.out.SanctLoad);
					CD = val + " KW";
					CD1 = "";
					CD2 = "";
				} else if (UtilAppCommon.out.ConnectedLoad.length() > 0) {
					val = Float.parseFloat(UtilAppCommon.out.ConnectedLoad);
					CD = "";
					CD1 = val + " HP";
					CD2 = "";
				} else if (UtilAppCommon.out.CD.length() > 0) {
					val = Float.parseFloat(UtilAppCommon.out.CD);
					CD = "";
					CD1 = "";*/
					/**
					 * Adding lines for tariff change 2018-19
					 */
				String CD2 = "";
				float val = 0;
				double result = 0.0;
				Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 11");
				if (UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(B)")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IIM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTEV")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-ID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("LTIS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("PUBWW")) {
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 12");
					result = (double) (Double.parseDouble(UtilAppCommon.out.CD) / 0.9f);
					CD2 = String.format("%.2f", result) + "KVA";
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 13");
				} else if (UtilAppCommon.out.Category.equalsIgnoreCase("HGN")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IM")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("IAS-IU")) {
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					CD2 = result + " HP";
				} else if (UtilAppCommon.out.Category.equalsIgnoreCase("DS-IID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("DS-IIID")
						|| UtilAppCommon.out.Category.equalsIgnoreCase("NDS-IID(A)")) {
					result = (double) (Double.parseDouble(UtilAppCommon.out.CD));
					CD2 = result + " KW";
				}else {
					/**
					 * End ading lines for tariff change
					 */
					result = (double) (Double.parseDouble(UtilAppCommon.out.SanctLoad));
					Log.v("ActvBillPrinting", "************************Printing Stage Checkpoint 14");
					CD2 = result + " KW";
				}


				//printerdata1.append(String.format("स्वीकृत भार   :%s \n", CD, CD1));
				printerdata1.append(String.format("संविदा मांग    :%s\n", CD2));
				printerdata1.append(String.format("जमानत की जमा राशि:%s\n", UtilAppCommon.out.SD));

				String billdays;

				if (UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) Actual")) {
					billdays = UtilAppCommon.out.BillDays + "(" + UtilAppCommon.out.MESSAGE10 + ")";
				} else {
					billdays = UtilAppCommon.out.BillDays;
				}
				printerdata2.append(String.format("\nकुल विपत्र दीवस   :%s\n", billdays));
				printerdata2.append(String.format("********************************\n"));
				printerdata2.append(String.format("मान पठन स्थिति  \n"));
				//printerdata2.append(String.format("मीटर   पठन  का  विवरण :\n"));
				printerdata2.append(String.format(" -----------------------------\n"));
				printerdata2.append(String.format("           पूर्व       वर्तमान\n"));
				printerdata2.append(String.format("पठन      : %s     %s\n", UtilAppCommon.out.PreviusReading, UtilAppCommon.out.CurrentReading));
				printerdata2.append(String.format("दिनांक : %s  %s\n", UtilAppCommon.out.PrevusMtrRdgDt, UtilAppCommon.out.CurrentMtrRdgDt));
				if (!UtilAppCommon.out.PrevusMtrRdgDt.equalsIgnoreCase("0000.00.00"))
					printerdata2.append(String.format("पठन स्थिति   : %s          %s \n", UtilAppCommon.out.PreviusMtrReadingNote, UtilAppCommon.out.CurrentMtrReadingNote));

				if (!UtilAppCommon.in.MONTH_SEASONAL.equalsIgnoreCase("")) {
					printerdata2.append(String.format("******************************* \n"));
					printerdata2.append(String.format("ब्ल्यूटुथ पठन स्थिति  :\n"));
					printerdata2.append(String.format("ब्ल्यूटुथ  मीटर   पठन  का  विवरण :\n"));
					printerdata2.append(String.format(" ------------------------------\n"));
					printerdata2.append(String.format("       पूर्व         वर्तमान  \n"));
					printerdata2.append(String.format("पठन      : %s     %s \n", UtilAppCommon.in.PREV_KWH_CYCLE1, UtilAppCommon.SAPBlueIn.CurrentReadingKwh));
					printerdata2.append(String.format("दिनांक : %s  %s \n", UtilAppCommon.in.DATE_1, UtilAppCommon.out.CurrentMtrRdgDt));
					if (!UtilAppCommon.in.DATE_1.equalsIgnoreCase("0000.00.00"))
						printerdata2.append(String.format("बस्तु स्थिति   : %s     %s \n", "OK", "OK"));
				}

				// 2nd block after image


				float mf = 0, consump = 0;
				//mf=Float.parseFloat(UtilAppCommon.out.MF);
				if (!UtilAppCommon.out.MF.equalsIgnoreCase(""))
					mf = Float.parseFloat(UtilAppCommon.out.MF);
				consump = Float.parseFloat(UtilAppCommon.out.Consumption);

				printerdata3.append(String.format("गुणक   : %.2f  खपत :%.2f\n", mf, consump));
				printerdata3.append(String.format("दर्ज मांग  :%s   पीएफ्  :%s\n", UtilAppCommon.out.RecordedDemd, UtilAppCommon.out.PowerFactor));


				float mmcunits = 0, avg = 0;
				if (UtilAppCommon.out.Category.equals("DS-II") || UtilAppCommon.out.Category.equals("NDS-IM")) {
					mmcunits = 0;
				} else {
					mmcunits = Float.parseFloat(UtilAppCommon.out.MMCUnits);
				}


				if (UtilAppCommon.out.CurrentMtrReadingNote.equalsIgnoreCase("OK")) {
					avg = 0;
				} else {
					avg = Float.parseFloat(UtilAppCommon.out.Average);

				}

				printerdata3.append(String.format("मासिक न्युनतम प्रभार :%.2f \n", mmcunits));
				printerdata3.append(String.format("औसत   :%.2f \nबिल इकाई  :%s\n", avg, UtilAppCommon.out.BilledUnits));
				printerdata3.append(String.format("विपत्र का आधार  :%s \n", UtilAppCommon.out.Type));
				printerdata3.append(String.format("******************************\n"));
				printerdata3.append(String.format("     बकाया का विवरण \n"));

				printerdata3.append(String.format(" -----------------------------\n"));
				float pmtonacct = 0, arrengdue = 0, arrdps = 0, arrothr = 0;
				pmtonacct = Float.parseFloat(UtilAppCommon.out.PaymentOnAccount);
				arrengdue = Float.parseFloat(UtilAppCommon.out.ArrearEnergyDues);
				arrdps = Float.parseFloat(UtilAppCommon.out.ArrearDPs);
				arrothr = Float.parseFloat(UtilAppCommon.out.ArrearOthers);


				printerdata3.append(String.format("अग्रिम जमा :%1.2f\n", pmtonacct));
				printerdata3.append(String.format("ऊर्जा  बकाया:%1.2f\n", arrengdue));
				printerdata3.append(String.format("विलंब अधिभार बकाया :%1.2f\n", arrdps));
				printerdata3.append(String.format("अन्य  प्रभार :%1.2f\n", arrothr));
				printerdata3.append(String.format("कुल  बकाया (अ):%1.2f\n", Float.parseFloat(UtilAppCommon.out.ArrearSubTotal_A)));

				printerdata3.append(String.format("******************************\n"));
				printerdata3.append(String.format("वर्तमान   बिपत्र का  विवरण\n"));
				printerdata3.append(String.format("****************************** \n"));

				float engchg = 0;
				engchg = Float.parseFloat(UtilAppCommon.out.CurrentEnergyCharges);
				float fixchg = 0, curdps = 0, excessdemdchg = 0, ed = 0, mr = 0, shuntcapchg = 0, othrchg = 0, subtotb = 0, govsubsidy = 0, cgst = 0, sgst = 0;
				;
				float intonsd = 0, incentive = 0, rebonmmc = 0, grosstot = 0;

				curdps = Float.parseFloat(UtilAppCommon.out.CurrentMonthDps);
				fixchg = Float.parseFloat(UtilAppCommon.out.FixDemdCharge);
				excessdemdchg = Float.parseFloat(UtilAppCommon.out.ExcessDemdCharge);
				ed = Float.parseFloat(UtilAppCommon.out.ElectricityDuty);
				mr = Float.parseFloat(UtilAppCommon.out.MeterRent);
/**
 * Adding lines for tariff change 2018-19
 */

				cgst = Float.parseFloat(UtilAppCommon.out.METER_CGST);
				sgst = Float.parseFloat(UtilAppCommon.out.METER_SGST);
				/**
				 * End ading lines for tariff change
				 */
				shuntcapchg = Float.parseFloat(UtilAppCommon.out.ShauntCapCharge);
				othrchg = Float.parseFloat(UtilAppCommon.out.OtherCharge);
				govsubsidy = Float.parseFloat(UtilAppCommon.out.GOVT_SUB);
				subtotb = Float.parseFloat(UtilAppCommon.out.SubTotal_B);
				if (UtilAppCommon.out.InterestOnSD_C.equalsIgnoreCase(""))
					intonsd = 0;
				else
					intonsd = Float.parseFloat(UtilAppCommon.out.InterestOnSD_C);
				if (UtilAppCommon.out.Incentive.equalsIgnoreCase(""))
					incentive = 0;
				else
					incentive = Float.parseFloat(UtilAppCommon.out.Incentive);

				rebonmmc = Float.parseFloat(UtilAppCommon.out.RebateOnMMC);
				grosstot = Float.parseFloat(UtilAppCommon.out.GrossTotal);

				float installmt_amt = 0;
				if (UtilAppCommon.in.CURR_MON_AMT.equalsIgnoreCase(""))
					installmt_amt = 0;
				else
					installmt_amt = Float.parseFloat(UtilAppCommon.in.CURR_MON_AMT);

				printerdata3.append(String.format("ऊर्जा शुल्क   : %1.2f\n", engchg));
				printerdata3.append(String.format("वर्तमान विलंब अधिभार: %1.2f\n", curdps));
				printerdata3.append(String.format("फीक्सड/डिमांड प्रभार: %1s\n", fixchg));
				printerdata3.append(String.format("आधिक्य डिमांड प्रभार: %1s\n", excessdemdchg));
				printerdata3.append(String.format("विदयुत  शुल्क    : %1s\n", ed));
				printerdata3.append(String.format("मीटर किराया     : %1s\n", mr));
				printerdata3.append(String.format("सीजीएसटी @ 9%% :%1s\n", cgst));
				printerdata3.append(String.format("एसजीएसटी @ 9%% :%1s\n", sgst));
				printerdata3.append(String.format("कैपिसिटर प्रभार   :%1s\n", shuntcapchg));
				printerdata3.append(String.format("किस्त की राशि   :%1s\n", installmt_amt));
				printerdata3.append(String.format("अन्य शुल्क      :%1s\n", othrchg));
				/**
				 * Adding New lines for tariff change 2018-19
				 */


				/**
				 * End adding lines for tariff change 2018-19
				 */


				printerdata4.append(String.format("राज्य सरकार अनुदान :%1s\n", govsubsidy));

				printerdata5.append(String.format("कुल अभिनिर्धारण (बी):%1s\n", subtotb));


				printerdata5.append(String.format("*****************************\n"));

				printerdata5.append(String.format("जमानत राशि पर सूद (सी) :%1s\n", intonsd));
				printerdata5.append(String.format("इन्सेंटिव्स     :%1s\n", incentive));
				printerdata5.append(String.format("रीमिशन      :%1s\n", rebonmmc));

				printerdata5.append(String.format("उप-जोड(ए+बी+सी):%1s\n", grosstot));

				printerdata5.append(String.format("******************************* \n"));

				printerdata5.append(String.format("छूट की राशि   :%10s\n", UtilAppCommon.out.Rebate));


				printerdata5.append(String.format("कूल मांग    \n"));

				printerdata5.append(String.format("*****************************\n"));

				printerdata5.append(String.format("%s तक  रूo :%1s\n ", UtilAppCommon.out.AmtPayableUptoDt, UtilAppCommon.out.AmtPayableUptoAmt));

				printerdata5.append(String.format("%s तक रूo :%1s\n ", UtilAppCommon.out.AmtPayablePYDt, UtilAppCommon.out.AmtPayablePYAmt));

				printerdata5.append(String.format("%s पश्चात रूo:%1s\n ", UtilAppCommon.out.AmtPayableAfterDt, UtilAppCommon.out.AmtPayableAfterAmt));


				printerdata5.append(String.format("पीछले भुगतान का  विवरण    \n"));
				printerdata5.append(String.format("*************************** \n"));


				printerdata5.append(String.format("भुगतान की राशि  :%s\n", UtilAppCommon.out.LastPaymentAmt));


				printerdata5.append(String.format("भुगतान दिनांक  :%s\n", UtilAppCommon.out.LastPaidDate));


				printerdata5.append(String.format("रसीद  संख्या  :%s\n", UtilAppCommon.out.ReceiptNumber));

				printerdata5.append(String.format("मीटर रीडर आईडी :%s\n", UtilAppCommon.out.MTR_READER_ID));

				printerdata5.append(String.format("Ver     :%10s \n", UtilAppCommon.strAppVersion.replace(".apk", "")));


				strBarcodeData = UtilAppCommon.acctNbr;
				//printerdata4.append(printer.barcode_Code_39_VIP(strBarcodeData));

				//printerdata4.append(printer.font_Courier_24_VIP(String.format("Consumer Helpline- 1912")));
				//printerdata4.append(String.format("Consumer Helpline- 1912"));
				// image
				printerdata6.append(String.format("Consumer Helpline- 1912 \n\n"));
				float flIntDisc = 0;
				if (!(UtilAppCommon.out.INT_DISC.equalsIgnoreCase("") || UtilAppCommon.out.INT_DISC.equalsIgnoreCase("null"))) {
					flIntDisc = Float.parseFloat(UtilAppCommon.out.INT_DISC);
					if (Math.abs(flIntDisc) > 0) {
						//printerdata6.append(String.format("देय  तिथि  %s तक  विपत्र  राशि  रू %s का ऑनलाईन  भुगतान करेँ एव पाये  "
						//		+ " रू %s अतिरिक्त  छुट",UtilAppCommon.out.AmtPayableUptoDt, UtilAppCommon.out.AmtPayableUptoAmt, Math.abs(flIntDisc)));
						printerdata6.append(String.format("देय  तिथि  %s तक  विपत्र  राशि  ", UtilAppCommon.out.AmtPayableUptoDt));
						printerdata6.append(String.format("रू %s का ऑनलाईन  भुगतान करेँ ", UtilAppCommon.out.AmtPayableUptoAmt));
						printerdata6.append(String.format("एव पाये  रू %s अतिरिक्त  छुट", Math.abs(flIntDisc)));
					}
				}


				printerdata7.append(String.format("%s\n\n\n", " "));

				String PhotoPath = null;
				if (UtilAppCommon.in.PRV_MTR_READING_NOTE.toUpperCase() != "RN") {
					// Print Reading Image
					byte[] imagedata = null;
					try {
                       /* String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                .getPath()
                                + "/SBDocs/Photos_Crop"
                                + "/"
                                + UtilAppCommon.sdoCode
                                + "/"
                                + UtilAppCommon.out.MRU;*/
						String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
								.getPath() + "/SBDocs/Photos/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/" + UtilAppCommon.in.MRU;


						strmonth = String.valueOf(Arrays.asList(strMonths).indexOf(UtilAppCommon.out.BillMonth) + 1);
						PhotoPath = PhotoDir + "/" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
								UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
								"_" + UtilAppCommon.out.CANumber + ".jpg";

						//imagedata=printer.prepareImageDataToPrint_VIP(address,PhotoPath);

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					//hprtPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00});
					tvsPrintImageAzadi();

					if (UtilAppCommon.bprintdupl) {
						//conn.multiLinguallinePrint_ver_2_0_printer(address, String.format("      %s", "DUPLICATE BILL"), 30, Typeface.DEFAULT_BOLD);
						hprtPrinterHelper.WriteData(String.format("      %s\n", "DUPLICATE BILL").getBytes("UTF-8"));
					}

					UtilAppCommon.bprintdupl = false;

					//conn.multiLinguallinePrint_ver_2_0_printer(address, String.format("          %s", UtilAppCommon.out.Company+"CL"), 40,Typeface.DEFAULT_BOLD);
					hprtPrinterHelper.WriteData(String.format("          %s\n", UtilAppCommon.out.Company + "CL").getBytes("UTF-8"));

					hprtPrinterHelper.WriteData(printerdata1.toString().getBytes("UTF-8"));
					hprtPrinterHelper.WriteData(String.format("खाता संख्या     :%s\n", UtilAppCommon.out.CANumber).getBytes("UTF-8"));


					hprtPrinterHelper.WriteData(printerdata2.toString().getBytes("UTF-8"));

					if (PhotoPath != null && PhotoPath.length() > 0) {
						tvsPrintImage(PhotoPath);
					}
                    /*if(imagedata!=null)
                    {
                        conn.printData(imagedata);
                    }*/
					hprtPrinterHelper.WriteData(printerdata3.toString().getBytes("UTF-8"));
					hprtPrinterHelper.WriteData(printerdata4.toString().getBytes("UTF-8"));
					hprtPrinterHelper.WriteData(printerdata5.toString().getBytes("UTF-8"));


					//conn.printData(printerbarcode.toString().getBytes());
                    /*Bitmap btMap_bar1 = BarcodeCreater.creatBarcode(this, UtilAppCommon.out.CANumber.trim(), widht, height,
                            true, 1);*/
					//printerbarcode.append(printer.barcode_Code_39_VIP(UtilAppCommon.out.CANumber));

					//printerbarcode.append(String.format("%s\n","."));
					try {
						Bitmap btMap_bar1 = BarcodeCreater.creatBarcode(ActvBillPrinting.this, UtilAppCommon.out.CANumber.trim(), 380, 50,
								true, 1);
						btMap_bar1 = BitmapDeleteNoUseSpaceUtil.deleteNoUseWhiteSpace(btMap_bar1);
						//btMap_bar1 = zoomImg(btMap_bar1, 570, btMap_bar1.getHeight());
						if (btMap_bar1 == null) {
							Toast.makeText(ActvBillPrinting.this, "no barcode", Toast.LENGTH_SHORT).show();
							return;
						}
						try {
							HPRTPrinterHelper.PrintBitmap(btMap_bar1, (byte) 0, (byte) 0, 203);
							//HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					hprtPrinterHelper.WriteData(printerbarcode.toString().getBytes("UTF-8"));

					hprtPrinterHelper.WriteData(printerdata6.toString().getBytes("UTF-8"));

					//conn.multiLinguallinePrint_ver_2_0_printer(address, String.format("      %s","."), 23,Typeface.DEFAULT_BOLD);
					hprtPrinterHelper.WriteData(printerdata7.toString().getBytes("UTF-8"));

					//	conn.printData(printerdata4.toString().getBytes());
					Thread.sleep(1000);

				}

			} catch (Exception e) {
				// Handle communications error here.
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Hindi Printing Not Working Properly", Toast.LENGTH_LONG).show();
				Log.e("", e.getMessage());

			} finally {
				if (UtilAppCommon.bprintdupl)
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInputForDuplicateBill.class));
				if (UtilAppCommon.blActyncBtn)
					startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
//				else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
//					startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("A"))
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("L"))
					startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("S"))
					startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
				else if (UtilAppCommon.billType.equalsIgnoreCase("M"))
					startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
				else
					startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
			}

		}


		public void showTVSPrintBarCode(HPRTPrinterHelper hprtPrinterHelper, String conId) {
			Bitmap btMap_bar1 = BarcodeCreater.creatBarcode(ActvBillPrinting.this, conId.trim(), 380, 50,
					true, 1);


			btMap_bar1 = BitmapDeleteNoUseSpaceUtil.deleteNoUseWhiteSpace(btMap_bar1);
			//btMap_bar1 = zoomImg(btMap_bar1, 570, btMap_bar1.getHeight());
			if (btMap_bar1 == null) {
				Toast.makeText(ActvBillPrinting.this, "no barcode", Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				hprtPrinterHelper.PrintBitmap(btMap_bar1, (byte) 0, (byte) 0, 203);
				//HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
			} catch (Exception e) {
				e.printStackTrace();
			}

		}


	}

	class TVSHindi2 extends Thread {

		private String address = null;
		BluetoothDevice device = null;
		HPRTPrinterHelper hprtPrinterHelper = null;

		public TVSHindi2(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);

			if (mBluetoothAdapter == null) {
				Toast.makeText(ActvBillPrinting.this, "Bluetooth not available.", Toast.LENGTH_SHORT).show();

				return;
			}
			if (!mBluetoothAdapter.isEnabled()) {
				Toast.makeText(ActvBillPrinting.this,
						"Bluetooth feature is turned off now...Please turned it on! ", Toast.LENGTH_SHORT).show();
				if (ActivityCompat.checkSelfPermission(ActvBillPrinting.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				mBluetoothAdapter.enable();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}

			hprtPrinterHelper = new HPRTPrinterHelper();
			try {
				int portOpen = hprtPrinterHelper.PortOpen("Bluetooth," + address);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}



		public void run() {
			try{
					tvsPrintImageAzadi();



					//	conn.printData(printerdata4.toString().getBytes());
					Thread.sleep(1000);



			} catch (Exception e) {
				// Handle communications error here.
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Hindi Printing Not Working Properly",	Toast.LENGTH_LONG).show();
				Log.e("", e.getMessage());

			}

			finally {
				if (UtilAppCommon.bprintdupl)
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInputForDuplicateBill.class));
				if(UtilAppCommon.blActyncBtn)
					startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
//				else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
//					startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
				else if(UtilAppCommon.billType.equalsIgnoreCase("A"))
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

		}


		public  void showTVSPrintBarCode(HPRTPrinterHelper hprtPrinterHelper, String conId) {
			Bitmap btMap_bar1 = BarcodeCreater.creatBarcode(ActvBillPrinting.this, conId.trim(), 380, 50,
					true, 1);


			btMap_bar1 = BitmapDeleteNoUseSpaceUtil.deleteNoUseWhiteSpace(btMap_bar1);
			//btMap_bar1 = zoomImg(btMap_bar1, 570, btMap_bar1.getHeight());
			if (btMap_bar1 == null) {
				Toast.makeText(ActvBillPrinting.this, "no barcode", Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				hprtPrinterHelper.PrintBitmap(btMap_bar1, (byte) 0, (byte) 0, 203);
				//HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
			} catch (Exception e) {
				e.printStackTrace();
			}

		}


	}

	public void tvsPrintImageAzadi() {

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		Bitmap bmp_print = BitmapFactory.decodeResource(getResources(), R.drawable.chunav);
		bmp_print = getResizedBitmap(bmp_print, 400);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bmp_print.compress(Bitmap.CompressFormat.JPEG, 100, out);

		DecimalFormat df = new DecimalFormat("0.00");
		int printWidth = 350;
		bmp_print = zoomImg(bmp_print, printWidth, printWidth);
		/*if (bmp_print.getWidth() >= printWidth) {

			float c = Float.valueOf(df.format((float) bmp_print.getWidth() / printWidth));

			int newHight =
					Integer.parseInt(new DecimalFormat("0").format(bmp_print.getHeight() / c));
			//bmp_print = zoomImg(bmp_print, printWidth, newHight);
			//Log.e("bmp_print2", bmp_print.toString());

		} else {

			float c = Float.valueOf(df.format((float) printWidth / bmp_print.getWidth()));

			Log.e("", "c：" + c);

			int newHight1 =
					Integer.parseInt(new DecimalFormat("0").format(c * bmp_print.getHeight()));
			//bmp_print = zoomImg(bmp_print, printWidth, newHight1);
			bmp_print = zoomImg(bmp_print, printWidth, printWidth);

		}*/

		try {
			HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x01});
			//  Log.e("printnu", String.valueOf(1));
			for (int i = 0; i < 1; i++) {
				Log.e("bmp_print3", bmp_print.toString());
				HPRTPrinterHelper.PrintBitmap(bmp_print, (byte) 1, (byte) 0, 203);
				HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
				Thread.sleep(500);

			}
			//恢复居左对齐
			HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x00});
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
			e.printStackTrace();
		}
	}

    public void tvsPrintImage(String addressImage) {
        //Log.e("bmp_print", String.valueOf(bmp_print));
			/*String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
					.getPath()
					+ "/DCIM/Camera/20210707_124237.jpg";*/
        File f =new File(addressImage.toString());
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        Bitmap bmp_print = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
        bmp_print = getResizedBitmap(bmp_print, 400);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp_print.compress(Bitmap.CompressFormat.JPEG, 90, out);

        DecimalFormat df = new DecimalFormat("0.00");
        int printWidth = 570;
        if (bmp_print.getWidth() >= printWidth) {

            float c = Float.valueOf(df.format((float) bmp_print.getWidth() / printWidth));

            int newHight =
                    Integer.parseInt(new DecimalFormat("0").format(bmp_print.getHeight() / c));
            //bmp_print = zoomImg(bmp_print, printWidth, newHight);
            //Log.e("bmp_print2", bmp_print.toString());

        } else {

            float c = Float.valueOf(df.format((float) printWidth / bmp_print.getWidth()));

            Log.e("", "c：" + c);

            int newHight1 =
                    Integer.parseInt(new DecimalFormat("0").format(c * bmp_print.getHeight()));
            bmp_print = zoomImg(bmp_print, printWidth, newHight1);

        }

        try {
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x01});
            //  Log.e("printnu", String.valueOf(1));
            for (int i = 0; i < 1; i++) {
                Log.e("bmp_print3", bmp_print.toString());
                HPRTPrinterHelper.PrintBitmap(bmp_print, (byte) 1, (byte) 0, 203);
                HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
                Thread.sleep(500);

            }
            //恢复居左对齐
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x00});
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
            e.printStackTrace();
        }
    }




    public Bitmap getImageBitmap(File f){
        try {
            Log.e("mBitmap", String.valueOf(f));
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            Bitmap mBitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
            mBitmap = getResizedBitmap(mBitmap, 400);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

            return mBitmap;

        } catch (Exception e) {
            Log.e("error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = 200;
        int height = 70;

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            // width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            //height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void printimage(Bitmap bmp_print) {
        Log.e("bmp_print", String.valueOf(bmp_print));

        DecimalFormat df = new DecimalFormat("0.00");
        int printWidth = 570;
        if (bmp_print.getWidth() >= printWidth) {

            float c = Float.valueOf(df.format((float) bmp_print.getWidth() / printWidth));

            int newHight =
                    Integer.parseInt(new DecimalFormat("0").format(bmp_print.getHeight() / c));
            bmp_print = zoomImg(bmp_print, printWidth, newHight);
            Log.e("bmp_print2", bmp_print.toString());

        } else {

            float c = Float.valueOf(df.format((float) printWidth / bmp_print.getWidth()));

            Log.e("", "c：" + c);

            int newHight1 =
                    Integer.parseInt(new DecimalFormat("0").format(c * bmp_print.getHeight()));
            bmp_print = zoomImg(bmp_print, printWidth, newHight1);

        }

        try {
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x01});
            //  Log.e("printnu", String.valueOf(1));
            for (int i = 0; i < 1; i++) {
                Log.e("bmp_print3", bmp_print.toString());
                HPRTPrinterHelper.PrintBitmap(bmp_print, (byte) 1, (byte) 0, 203);
                HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
                Thread.sleep(500);

            }
            //恢复居左对齐
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x00});
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
            e.printStackTrace();
        }
    }


    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        return newbm;
    }


    
}
