package org.cso.MobileSpotBilling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.os.Bundle;
import android.app.Activity;
import java.io.*;
import android.view.Menu;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.PrintUtilZebra;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.UtilDB.MyHelper;
import org.cso.MobileSpotBilling.ActvBillPrinting.AnalogicThermal;
//import org.cso.MobileSpotBilling.ActvDuplicateBillPrinting.AnalogicThermal;

import com.analogics.Bluetooth_Printer_2inch_ThermalAPI;
import com.analogics.thermalAPI.Bluetooth_Printer_2inch_prof_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
//import com.analogics.professional.thermalAPI.AnalogicsThermalPrinterProf;
//import com.analogics.professional.thermalAPI.Bluetooth_Printer_2inch_prof_ThermalAPI;
import com.epson.eposprint.Builder;
import com.epson.eposprint.Print;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ActvSummaryPrinting extends Activity {
	/** Called when the activity is first created. */
	private BluetoothAdapter mBluetoothAdapter = null;
	static final UUID MY_UUID = UUID.randomUUID();
	// static String address = "00:1F:B7:05:44:76";
	ZebraThermal sendDatazebra = null;
	//AnalogicImpact sendData = null;
	EpsonThermal sendDataEpson = null;
//	AnalogicThermal sendDataAnaloginThermal = null;

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
			mBluetoothAdapter.enable();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Added on 3.7.2014
		UtilDB dbObj = new UtilDB(getApplicationContext());
		String[] printer = dbObj.GetPrinterInfo();
		if (printer[1].compareToIgnoreCase("Zebra Thermal") == 0) {
			sendDatazebra = new ZebraThermal(printer[0]);
			Thread t = new Thread(sendDatazebra);
			t.run();
		//} else if (printer[1].compareToIgnoreCase("Analogic Impact") == 0) {
		//	sendData = new AnalogicImpact(printer[0]);
		//	Thread t = new Thread(sendData);
		//	t.run();
		} else if (printer[1].compareToIgnoreCase("EPSON Thermal") == 0 || printer[1].compareToIgnoreCase("EPSON Thermal-Hin") == 0) {
			sendDataEpson = new EpsonThermal(printer[0]);
			Thread t = new Thread(sendDataEpson);
			t.run();

		} else if (printer[1].compareToIgnoreCase("Analogic Thermal") == 0 || printer[1].compareToIgnoreCase("Analogic Thermal-Hin") == 0) {
			 
			AnalogicThermal sendDataAnaloginThermal = new AnalogicThermal(printer[0]);
			Thread t = new Thread(sendDataAnaloginThermal);
			t.run();
		} else {
			Toast.makeText(this, "No Printer Configured", Toast.LENGTH_LONG)
					.show();
			startActivity(new Intent(getBaseContext(), ActvReport.class));
/*			if(UtilAppCommon.blActyncBtn)
				startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
			else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
				startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("A"))
				startActivity(new Intent(this, ActvConsumerNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
				startActivity(new Intent(this, ActvLegacyNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
				startActivity(new Intent(this, ActvSequenceData.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
				startActivity(new Intent(this, MeterNbrInput.class));
			else
				startActivity(new Intent(this, ActvBillingOption.class));*/
		}
		
		// End Added on 1.3.2014

	}

	public void onBackPressed() {
		// do something on back.
		finish();
		// startActivity(new Intent(this, ActvivityMain.class));
		return;
	}

	
	class ZebraThermal extends Thread {
		//UtilDB dbObj = new UtilDB(getApplicationContext());
		private BluetoothDevice device = null;
		private BluetoothSocket btSocket = null;
		private OutputStream outStream = null;
		private OutputStreamWriter writer = null;
		private String address = null;
		UtilDB dbObj = new UtilDB(getApplicationContext());
		public ZebraThermal(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);
			Toast.makeText(getApplicationContext(), "Connected To:" + address,
					Toast.LENGTH_LONG).show();
		}

		@SuppressWarnings("deprecation")
		public void run() {
			try {
				Toast.makeText(getApplicationContext(), "Sending Data",
						Toast.LENGTH_LONG).show();

				ZebraPrinterConnection thePrinterConn = new BluetoothPrinterConnection(
						address);
				thePrinterConn.open();
				// Initialize

				 SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yy'   TIME: 'hh:mm");
				
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
				Cursor c = dbObj.summarylist();
				
				cpclData = "! 90 200 200 "+((c.getCount()*30)+130)+" 1\r\n";
				cpclData += "CENTER\r\n";
			
				//cpclData += "UNDERLINE ON\r\n";
				cpclData += PrintUtilZebra
						.PrintNext("Billing Summary    ");
				cpclData += PrintUtilZebra
						.PrintNext("*******************   ");


				cpclData += "LEFT\r\n";
				cpclData += PrintUtilZebra
						.PrintNext("Category TotalCons Billed  Units Amt ");
				cpclData += PrintUtilZebra
						.PrintNext("-------- -------- -------- ------ -----");
                          int cnt=0;
						if (c.moveToFirst()) {
							do {
						
								cpclData += PrintUtilZebra.PrintNext(String.format(
										"%s   %s   %s   %s   %s",c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4)));
							} while (c.moveToNext());
						}
				
			
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

			}
			
			finally {
	
					startActivity(new Intent(getBaseContext(), ActvReport.class));
			}
			return;

		}
	}

	class EpsonThermal extends Thread {

		private BluetoothDevice device = null;
		private String address = null;
		UtilDB dbObj = new UtilDB(getApplicationContext());
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
			Cursor c = dbObj.summarylist();
			SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yy'  TIME:'hh:mm");
			
			try {
				Toast.makeText(getApplicationContext(), "Sending Data",
						Toast.LENGTH_LONG).show();
				Builder builder = new Builder("TM-P60", Builder.MODEL_ANK);
				builder.addTextFont(Builder.FONT_A);

				builder.addTextAlign(Builder.ALIGN_CENTER);
				//builder.addTextSize(2, 2);
				//builder.addTextStyle(Builder.FALSE, Builder.FALSE,
				//		Builder.TRUE, Builder.PARAM_UNSPECIFIED);

				//builder.addText(UtilAppCommon.out.Company + "\n");

				//builder.addTextStyle(Builder.FALSE, Builder.FALSE,
				//		Builder.FALSE, Builder.PARAM_UNSPECIFIED);
				
				builder.addTextSize(1, 1);
				builder.addText("Billing Summary\n");
				builder.addText("------------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);
				builder.addText("Category TotalCons Billed  Units Amt\n");
				builder.addText("------------------------------------\n");
				 
                int cnt=0;
				if (c.moveToFirst()) {
					do {
						builder.addText(String.format(
								"%s   %s   %s   %s   %s\n", c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4)));
					} while (c.moveToNext());
				}
				

				builder.addCut(Builder.CUT_FEED);
				// <Send print data>

				printer.openPrinter(Print.DEVTYPE_BLUETOOTH, address,
						Print.TRUE, Print.PARAM_DEFAULT);
				printer.sendData(builder, 10000, status, battery);
				printer.closePrinter();

			} catch (Exception e) {
				// Handle communications error here.
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();

			}

			finally {
				
				startActivity(new Intent(getBaseContext(), ActvReport.class));
		}
		return;
		}
	}

	
	class AnalogicThermal extends Thread {
		private String address = null;

		UtilDB dbObj = new UtilDB(getApplicationContext());
		public AnalogicThermal(String address) {
			this.address = address;
		}
		
		SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yy' TIME:'hh:mm");
		
		public void run() {
			
		try {
			
			 AnalogicsThermalPrinter conn= new AnalogicsThermalPrinter();
				conn.openBT(address);
				
				Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();
			
				Toast.makeText(getBaseContext(), BILL, Toast.LENGTH_SHORT)
						.show();

				char lf = 0x0A;
				char cr = 0x0D;
				Cursor c = dbObj.summarylist();
				// char dp = 0x1D;
				// char nm = 0x13;

				// ////////Print On Paper Start////////////
				StringBuilder printerdata1=new StringBuilder();
				
				//Print part 1
	

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"     %s\n","Billing Summary")));	
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"     %s\n","---------------")));	

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n","Cat Cons  Bld Units Amt")));	
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n","-----------------------")));
				

				

                int cnt=0;
				if (c.moveToFirst()) {
					do {
						
						printerdata1.append(printer.font_Courier_24_VIP(String.format(
								"%s %s %s %s %s\n",c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4))));
					} while (c.moveToNext());
				}
				

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						".    \n ")));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						".    \n ")));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						".    \n ")));
				
				conn.printData(printerdata1.toString().getBytes());		
				
				Thread.sleep(3000);
				conn.closeBT();
				
				
		} catch (Exception e) {
			// Handle communications error here.
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();

		}
		
		finally {
			
			startActivity(new Intent(getBaseContext(), ActvReport.class));
	}
	return;

	}
	}
	

}
