package org.cso.MobileSpotBilling;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.util.*;
import java.text.SimpleDateFormat;

import org.cso.MSBAsync.AsyncImage;
import org.cso.MSBUtil.PrintUtilZebra;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
//import org.cso.MobileSpotBilling.ActvDuplicateBillPrinting.AnalogicThermal;

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
import com.zj.btsdk.BluetoothService;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import HPRTAndroidSDK.HPRTPrinterHelper;

public class ActvMsgPrinting extends AppCompatActivity {
	/** Called when the activity is first created. */
	private BluetoothAdapter mBluetoothAdapter = null;
	static final UUID MY_UUID = UUID.randomUUID();
	// static String address = "00:1F:B7:05:44:76";
	ZebraThermal sendDatazebra = null;
	//AnalogicImpact sendData = null;
	EpsonThermal sendDataEpson = null;
//	AnalogicThermal sendDataAnaloginThermal = null;

	Date dNow = new Date();
	SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
	String strDateTime = null;
	
	String BILL = "";
	// Added on-1.3.2014
	private Context context = this;
	String strBarcodeData = "",strmonth = "";
	String strMonths[] = {"JAN", "FEB", "MAR", "ARP", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

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
		
		if(UtilAppCommon.SAPIn.MsgId == null || UtilAppCommon.SAPIn.MsgId.trim().equalsIgnoreCase(""))
		{
			UtilAppCommon.SAPIn.MsgId = "7";
		}
		
		if(UtilAppCommon.SAPIn.strMsg == null || UtilAppCommon.SAPIn.strMsg.trim().equalsIgnoreCase(""))
		{
			UtilAppCommon.SAPIn.strMsg = "Problem in request processing".toUpperCase();
		}
		
		//Toast.makeText(this, UtilAppCommon.SAPIn.strMsg.trim(), Toast.LENGTH_LONG).show();
		
		//Added on 3.7.2014
		UtilDB dbObj = new UtilDB(context);
		String[] printer = dbObj.GetPrinterInfo();
		if (UtilAppCommon.SAPIn.strMsg.trim().toUpperCase().startsWith("MR BLOCKED")) {
			Toast.makeText(this, "MR blocked, please contact Supervisor.", Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, ActvivityMain.class));
		} else if (printer[1].compareToIgnoreCase("Zebra Thermal") == 0) {
			sendDatazebra = new ZebraThermal(printer[0]);
			Thread t = new Thread(sendDatazebra);
			t.run();
		//} else if (printer[1].compareToIgnoreCase("Analogic Impact") == 0) {
		//	sendData = new AnalogicImpact(printer[0]);
		//	Thread t = new Thread(sendData);
		//	t.run();
		} else if (printer[1].compareToIgnoreCase("Epson Thermal") == 0 || printer[1].compareToIgnoreCase("Epson Thermal-Hin") == 0) {
			sendDataEpson = new EpsonThermal(printer[0]);
			Thread t = new Thread(sendDataEpson);
			t.run();
		} else if (printer[1].compareToIgnoreCase("Mini Portable Thermal") == 0) {
			MiniThermal sendDataMiniThermal = new MiniThermal(printer[0]);
			Thread t = new Thread(sendDataMiniThermal);
			t.run();
		} else if (printer[1].compareToIgnoreCase("Analogic Thermal") == 0 || printer[1].compareToIgnoreCase("Analogic Thermal-Hin") == 0) {
			AnalogicThermal sendDataAnaloginThermal = new AnalogicThermal(printer[0]);
			Thread t = new Thread(sendDataAnaloginThermal);
			t.run();
		}
		else if (printer[1].compareToIgnoreCase("TVS-HINDI") == 0 || printer[1].compareToIgnoreCase("TVS-ENGLISH") == 0)
		{
			Thread t = null;
			TVSPrinter tvsPrinter = new TVSPrinter(printer[0]);
			t = new Thread(tvsPrinter);
			t.run();
		}else {
			Toast.makeText(this, "No Printer Configured", Toast.LENGTH_LONG)
					.show();
			if(UtilAppCommon.blActyncBtn)
				startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
			//else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
			//	startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("A"))
				startActivity(new Intent(this, ActvConsumerNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
				startActivity(new Intent(this, ActvLegacyNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
				startActivity(new Intent(this, ActvSequenceData.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
				startActivity(new Intent(this, MeterNbrInput.class));
			else
				startActivity(new Intent(this, ActvBillingOption.class));
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

				cpclData = "! 90 200 200 580 1\r\n";
				cpclData += "CENTER\r\n";
			
				//cpclData += "UNDERLINE ON\r\n";

				String reading = UtilAppCommon.SAPIn.CurrentReadingKwh;
				if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-ID")
	                    || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-IID")
	                    || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("PUBWW"))
				{
					reading = UtilAppCommon.SAPIn.CurrentReadingKVAh;
				}
				
				cpclData += PrintUtilZebra.PrintLargeNext(UtilAppCommon.in.COM_CODE+"CL");

				cpclData += PrintUtilZebra
						.PrintNext("*******************   ");
				cpclData += "LEFT\r\n";
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"CA Number :%s",UtilAppCommon.in.CONTRACT_AC_NO));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"MRU :%s",UtilAppCommon.in.MRU));
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"Consumer Name :%s",UtilAppCommon.in.CONSUMER_NAME));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"Category    :%s",UtilAppCommon.in.RATE_CATEGORY));
		
				String mtrsts="";
				if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("PL"))
					mtrsts = "House Lock";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("RN"))
					mtrsts = "Reading Not Available";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("MD"))
					mtrsts = "Mtr.Defective";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
						mtrsts = "Actual";
	
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"Curr Mtr Sts   :%s",mtrsts));
	
				if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
				{
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"Curr Rdg Dt :%s",UtilAppCommon.SAPIn.MtrReadingDate));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"Curr Rdg  (Kwh/Kvarh) :%s",reading));
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"Max.Demd :%s",UtilAppCommon.SAPIn.MaxDemd));
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"PF :%s",UtilAppCommon.SAPIn.PowerFactor));
				}

				cpclData += "LEFT\r\n\n";
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"The Bill could not be generated now."));
				//cpclData += "UNDERLINE OFF\r\n";
				cpclData += PrintUtilZebra
						.PrintNext("Please collect your bill from ");
				cpclData += PrintUtilZebra
						.PrintNext(UtilAppCommon.in.COM_CODE+"CL Website after 2 days");
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"Code   :%s",UtilAppCommon.SAPIn.MsgId));
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"Msg   :%s",UtilAppCommon.SAPIn.strMsg));
				
				dNow = new Date();
				strDateTime = ft.format(dNow);
				cpclData += PrintUtilZebra.PrintNextData1("DATE: ",
						String.format("%s", UtilAppCommon.strServerDtTm));
				cpclData += PrintUtilZebra.PrintNextData1("",
						String.format("Ver: %s", UtilAppCommon.strAppVersion));
				
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
	}

	class EpsonThermal extends Thread {

		private BluetoothDevice device = null;
		private String address = null;

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

			try 
			{
				Toast.makeText(getApplicationContext(), "Sending Data",
						Toast.LENGTH_LONG).show();
				Builder builder = new Builder("TM-P60", Builder.MODEL_ANK);
				builder.addTextFont(Builder.FONT_A);

				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addTextSize(2, 2);
				builder.addTextStyle(Builder.FALSE, Builder.FALSE,
						Builder.TRUE, Builder.PARAM_UNSPECIFIED);

				builder.addText(UtilAppCommon.in.COM_CODE+"CL" + "\n");
				builder.addTextStyle(Builder.FALSE, Builder.FALSE,
						Builder.FALSE, Builder.PARAM_UNSPECIFIED);
				builder.addTextSize(1, 1);
				builder.addText("--------------------------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);
				
				builder.addText(String.format(
						" CA Number    :%s\n", UtilAppCommon.in.CONTRACT_AC_NO));
				
				builder.addText(String.format(
						" MRU          :%s\n", UtilAppCommon.in.MRU));	
				
				builder.addText(String.format(
						" Consumer Name:%s\n", UtilAppCommon.in.CONSUMER_NAME));		

			    builder.addText(String.format(
						" Category     :%s\n", UtilAppCommon.in.RATE_CATEGORY));	

				String mtrsts="";
				if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("PL"))
					mtrsts = "House Lock";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("RN"))
					mtrsts = "Reading Not Available";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("MD"))
					mtrsts = "Mtr.Defective";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
						mtrsts = "Actual";

				builder.addText(String.format(
						" Curr Mtr Sts    :%s\n", mtrsts));	

				if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
				{
					builder.addText(String.format(
							" Curr  Rdg Dt    :%s\n", UtilAppCommon.SAPIn.MtrReadingDate));		
					builder.addText(String.format(
							" Curr  Rdg(Kwh/Kvarh)  :%s\n", UtilAppCommon.SAPIn.CurrentReadingKwh));			
					builder.addText(String.format(
							" Max.Demd        :%s\n", UtilAppCommon.SAPIn.MaxDemd));	
					builder.addText(String.format(
							" PF              :%s\n", UtilAppCommon.SAPIn.PowerFactor));
				}
	
				builder.addText("The Bill could not be generated now.\n");
				builder.addText("Please collect your bill from \n");
				builder.addText(UtilAppCommon.in.COM_CODE+"CL Website after 2 days"+"\n" );	
	
				builder.addText(String.format(
						" Code         :%s\n", UtilAppCommon.SAPIn.MsgId));
	
				builder.addText(String.format(
						" Msg         :%s\n", UtilAppCommon.SAPIn.strMsg));
	
				dNow = new Date();
				strDateTime = ft.format(dNow);
				if(UtilAppCommon.strServerDtTm.equalsIgnoreCase(""))
					builder.addText(String.format("LOCAL DATE: %s\n", strDateTime));
				else
					builder.addText(String.format("DATE: %s\n", UtilAppCommon.strServerDtTm));
				builder.addText(String.format("Ver: %s", UtilAppCommon.strAppVersion));
	
				builder.addText("\n");
	
				builder.addText(String.format("Consumer Helpline- 1912"));
	
				builder.addText("\n");
	
				builder.addCut(Builder.CUT_FEED);
				// <Send print data>
	
				printer.openPrinter(Print.DEVTYPE_BLUETOOTH, address, Print.TRUE, Print.PARAM_DEFAULT);
				printer.sendData(builder, 10000, status, battery);
					
				printer.closePrinter();

			} catch (Exception e) {
				// Handle communications error here.
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();

			}
			finally {
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
	}

	class EpsonThermalHindi extends Thread {

		private BluetoothDevice device = null;
		private String address = null;

		public EpsonThermalHindi(String address) {
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

			try 
			{
				Toast.makeText(getApplicationContext(), "Sending Data",
						Toast.LENGTH_LONG).show();
				Builder builder = new Builder("TM-P60", Builder.MODEL_ANK);
				builder.addTextFont(Builder.FONT_A);

				builder.addTextAlign(Builder.ALIGN_CENTER);
				builder.addTextSize(2, 2);
				builder.addTextStyle(Builder.FALSE, Builder.FALSE,
						Builder.TRUE, Builder.PARAM_UNSPECIFIED);

				builder.addText(UtilAppCommon.in.COM_CODE+"CL" + "\n");
				builder.addTextStyle(Builder.FALSE, Builder.FALSE,
						Builder.FALSE, Builder.PARAM_UNSPECIFIED);
				builder.addTextSize(1, 1);
				builder.addText("--------------------------------\n");
				builder.addTextAlign(Builder.ALIGN_LEFT);
				
				builder.addText(String.format(
						" CA Number    :%s\n", UtilAppCommon.in.CONTRACT_AC_NO));
				
				builder.addText(String.format(
						" MRU          :%s\n", UtilAppCommon.in.MRU));	
				
				builder.addText(String.format(
						" Consumer Name:%s\n", UtilAppCommon.in.CONSUMER_NAME));		

			    builder.addText(String.format(
						" Category     :%s\n", UtilAppCommon.in.RATE_CATEGORY));	

				String mtrsts="";
				if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("PL"))
					mtrsts = "House Lock";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("RN"))
					mtrsts = "Reading Not Available";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("MD"))
					mtrsts = "Mtr.Defective";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
						mtrsts = "Actual";

				builder.addText(String.format(
						" Curr Mtr Sts    :%s\n", mtrsts));	
				String reading = UtilAppCommon.SAPIn.CurrentReadingKwh;
				if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-ID")
	                    || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-IID")
	                    || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("PUBWW"))
				{
					reading = UtilAppCommon.SAPIn.CurrentReadingKVAh;
				}

				if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
				{
					builder.addText(String.format(
							" Curr  Rdg Dt    :%s\n", UtilAppCommon.SAPIn.MtrReadingDate));		
					builder.addText(String.format(
							" Curr  Rdg(Kwh/Kvarh)  :%s\n", reading));			
					builder.addText(String.format(
							" Max.Demd        :%s\n", UtilAppCommon.SAPIn.MaxDemd));	
					builder.addText(String.format(
							" PF              :%s\n", UtilAppCommon.SAPIn.PowerFactor));
				}
	
				builder.addText("The Bill could not be generated now.\n");
				builder.addText("Please collect your bill from \n");
				builder.addText(UtilAppCommon.in.COM_CODE+"CL Website after 2 days"+"\n" );	
	
				builder.addText(String.format(
						" Code         :%s\n", UtilAppCommon.SAPIn.MsgId));
	
				builder.addText(String.format(
						" Msg         :%s\n", UtilAppCommon.SAPIn.strMsg));
	
				dNow = new Date();
				strDateTime = ft.format(dNow);
				if(UtilAppCommon.strServerDtTm.equalsIgnoreCase(""))
					builder.addText(String.format("LOCAL DATE: %s\n", strDateTime));
				else
					builder.addText(String.format("DATE: %s\n", UtilAppCommon.strServerDtTm));
				builder.addText(String.format("Ver: %s", UtilAppCommon.strAppVersion));
	
				builder.addText("\n");
	
				builder.addText(String.format("Consumer Helpline- 1912"));
	
				builder.addText("\n");
	
				builder.addCut(Builder.CUT_FEED);
				// <Send print data>
	
				printer.openPrinter(Print.DEVTYPE_BLUETOOTH, address, Print.TRUE, Print.PARAM_DEFAULT);
				printer.sendData(builder, 10000, status, battery);
					
				printer.closePrinter();

			} catch (Exception e) {
				// Handle communications error here.
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();

			}
			finally {
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
	}

    class AnalogicThermal extends Thread {
		
		private String address = null;


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
				// char dp = 0x1D;
				// char nm = 0x13;

				// ////////Print On Paper Start////////////
				StringBuilder printerdata1=new StringBuilder();
				
				//Print part 1
	
				printerdata1.append(printer.font_Courier_10_VIP(String.format(
							"%s\n", "  "+UtilAppCommon.in.COM_CODE+"CL")));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n", "************************")));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"CA NUMBER: %s\n", UtilAppCommon.in.CONTRACT_AC_NO)));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"MRU      : %s\n", UtilAppCommon.in.MRU)));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"NAME     : %s\n", UtilAppCommon.in.CONSUMER_NAME)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"CATEGORY : %s\n", UtilAppCommon.in.RATE_CATEGORY)));


				String mtrsts="";
				if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("PL"))
					mtrsts = "House Lock";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("RN"))
					mtrsts = "Reading Not Available";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("MD"))
					mtrsts = "Mtr.Defective";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
						mtrsts = "Actual";

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"Curr Mtr Sts: %s\n", mtrsts)));

				if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
				{

					printerdata1.append(printer.font_Courier_32_VIP(String.format(
							"Curr  Rdg Dt : %s\n", UtilAppCommon.SAPIn.MtrReadingDate)));
					printerdata1.append(printer.font_Courier_24_VIP(String.format(
							"Curr Rdg(Kwh): %s\n", UtilAppCommon.SAPIn.CurrentReadingKwh)));
					printerdata1.append(printer.font_Courier_24_VIP(String.format(
							"Max.Demd     : %s\n", UtilAppCommon.SAPIn.MaxDemd)));
					printerdata1.append(printer.font_Courier_24_VIP(String.format(
							"PF           : %s\n\n",  UtilAppCommon.SAPIn.PowerFactor)));
				}
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n",  "The Bill could not be")));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n",  "generated now. Please")));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n",  "collect your bill from ")));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n",  UtilAppCommon.in.COM_CODE+"CL Website ")));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"%s\n\n",  "after 2 days ")));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"Code     : %s\n",  UtilAppCommon.SAPIn.MsgId)));
				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"Msg      : %s\n",  UtilAppCommon.SAPIn.strMsg)));

				dNow = new Date();
				strDateTime = ft.format(dNow);
				printerdata1.append(printer.font_Courier_32_VIP(String.format(
						"DATE: %s \t\n", UtilAppCommon.strServerDtTm)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						"Ver : %s\n\n", UtilAppCommon.strAppVersion)));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						".   \n")));

				printerdata1.append(printer.font_Courier_24_VIP(String.format("Consumer Helpline- 1912")));

				printerdata1.append(printer.font_Courier_24_VIP(String.format(
						".   \n")));

				conn.printData(printerdata1.toString().getBytes());				

				Thread.sleep(3000);
				conn.closeBT();

		}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
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
    }
       
    class MiniThermal extends Thread {
    	
    	private String address = null;
    	private BluetoothDevice device = null;

		public MiniThermal(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);
		}

		SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yy' TIME:'hh:mm");
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			BluetoothService bluetoothService = new BluetoothService(getApplicationContext(), null);
			
			if(bluetoothService.isAvailable())
			{
				bluetoothService.connect(device);
				
				String strMessage = "";
				strMessage = UtilAppCommon.in.COM_CODE+"CL";
				strMessage += "\n*******************   ";
				
				bluetoothService.sendMessage(strMessage, "GBK");
				bluetoothService.stop();
			}
			else
				Toast.makeText(context,"Bluetooth feature is turned off now...Please turned it on! ",	Toast.LENGTH_LONG).show();
		}
    }

	class TVSPrinter extends Thread {

		private String address = null;
		BluetoothDevice device = null;
		HPRTPrinterHelper hprtPrinterHelper = null;

		public TVSPrinter(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);

			if (mBluetoothAdapter == null) {
				Toast.makeText(ActvMsgPrinting.this, "Bluetooth not available.", Toast.LENGTH_SHORT).show();

				return ;
			}
			if (!mBluetoothAdapter.isEnabled()) {
				Toast.makeText(ActvMsgPrinting.this,
						"Bluetooth feature is turned off now...Please turned it on! ", Toast.LENGTH_SHORT).show();
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
			SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yy' TIME:'hh:mm");
			try {



				Toast.makeText(getBaseContext(), BILL, Toast.LENGTH_SHORT)
						.show();

				char lf = 0x0A;
				char cr = 0x0D;
				// char dp = 0x1D;
				// char nm = 0x13;

				// ////////Print On Paper Start////////////
				StringBuilder printerdata1=new StringBuilder();

				//Print part 1

				printerdata1.append(String.format(
						"%s\n", "  "+UtilAppCommon.in.COM_CODE+"CL"));

				printerdata1.append(String.format(
						"%s\n", "************************"));

				printerdata1.append(String.format(
						"CA NUMBER: %s\n", UtilAppCommon.in.CONTRACT_AC_NO));
				printerdata1.append(String.format(
						"MRU      : %s\n", UtilAppCommon.in.MRU));
				printerdata1.append(String.format(
						"NAME     : %s\n", UtilAppCommon.in.CONSUMER_NAME));

				printerdata1.append(String.format(
						"CATEGORY : %s\n", UtilAppCommon.in.RATE_CATEGORY));


				String mtrsts="";
				if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("PL"))
					mtrsts = "House Lock";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("RN"))
					mtrsts = "Reading Not Available";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("MD"))
					mtrsts = "Mtr.Defective";
				else if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
					mtrsts = "Actual";

				printerdata1.append(String.format(
						"Curr Mtr Sts: %s\n", mtrsts));

				if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("OK"))
				{

					printerdata1.append(String.format(
							"Curr  Rdg Dt : %s\n", UtilAppCommon.SAPIn.MtrReadingDate));
					printerdata1.append(String.format(
							"Curr Rdg(Kwh): %s\n", UtilAppCommon.SAPIn.CurrentReadingKwh));
					printerdata1.append(String.format(
							"Max.Demd     : %s\n", UtilAppCommon.SAPIn.MaxDemd));
					printerdata1.append(String.format(
							"PF           : %s\n\n",  UtilAppCommon.SAPIn.PowerFactor));
				}
				printerdata1.append(String.format(
						"%s\n",  "The Bill could not be"));
				printerdata1.append(String.format(
						"%s\n",  "generated now. Please"));
				printerdata1.append(String.format(
						"%s\n",  "collect your bill from "));
				printerdata1.append(String.format(
						"%s\n",  UtilAppCommon.in.COM_CODE+"CL Website "));
				printerdata1.append(String.format(
						"%s\n\n",  "after 2 days "));

				printerdata1.append(String.format(
						"Code     : %s\n",  UtilAppCommon.SAPIn.MsgId));
				printerdata1.append(String.format(
						"Msg      : %s\n",  UtilAppCommon.SAPIn.strMsg));

				dNow = new Date();
				strDateTime = ft.format(dNow);
				printerdata1.append(String.format(
						"DATE: %s \t\n", UtilAppCommon.strServerDtTm));

				printerdata1.append(String.format(
						"Ver : %s\n\n", UtilAppCommon.strAppVersion));

				printerdata1.append(String.format(
						".   \n"));

				printerdata1.append(String.format("Consumer Helpline- 1912"));

				printerdata1.append(String.format(
						".   \n"));

				hprtPrinterHelper.WriteData( printerdata1.toString().getBytes("UTF-8"));


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



	}
    
    
    
	public void getImageByCANo(String CANo)
	{
		String AppDir = "";
		
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
	}



}
