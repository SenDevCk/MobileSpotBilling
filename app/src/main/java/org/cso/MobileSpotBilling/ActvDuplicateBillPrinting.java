package org.cso.MobileSpotBilling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.os.Bundle;
import android.app.Activity;
import java.io.*;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.cso.MSBUtil.AppUtil;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.PrintUtilZebra;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MSBUtil.Utilities;
import org.cso.config.PrinterManager;

import com.analogics.*;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class ActvDuplicateBillPrinting extends AppCompatActivity {
	/** Called when the activity is first created. */
	private BluetoothAdapter mBluetoothAdapter = null;
	static final UUID MY_UUID = UUID.randomUUID();
	// static String address = "00:1F:B7:05:44:76";
	ZebraThermal sendDatazebra = null;
	AnalogicImpact sendData = null;
	EPSONThermal sendDataEpson = null;
	AnalogicThermal sendDataAnaloginThermal = null;
	
	String strMonths[] = {"JAN", "FEB", "MAR", "ARP", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
	String strmonth = "";
	
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
			finish();
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(this, "First Enable your BT ", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}
		// Added on 1.3.2014
		UtilDB dbObj = new UtilDB(context);
		String[] printer = dbObj.GetPrinterInfo();
		if (printer[1].compareToIgnoreCase("Zebra Thermal") == 0) {
			sendDatazebra = new ZebraThermal(printer[0]);
			Thread t = new Thread(sendDatazebra);
			t.run();
		} else if (printer[1].compareToIgnoreCase("Analogic Impact") == 0) {
			sendData = new AnalogicImpact(printer[0]);
			Thread t = new Thread(sendData);
			t.run();
		} else if (printer[1].compareToIgnoreCase("EPSON Thermal") == 0) {
			sendDataEpson = new EPSONThermal(printer[0]);
			Thread t = new Thread(sendDataEpson);
			t.run();
		} else if (printer[1].compareToIgnoreCase("Analogic Thermal") == 0) {
			sendDataAnaloginThermal = new AnalogicThermal(printer[0]);
			Thread t = new Thread(sendDataAnaloginThermal);
			t.run();
		} else {
			Toast.makeText(this, "No Printer Configured", Toast.LENGTH_LONG)
					.show();
			if(UtilAppCommon.billType.equalsIgnoreCase("A"))
				startActivity(new Intent(this, ActvConsumerNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
				startActivity(new Intent(this, ActvLegacyNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
				startActivity(new Intent(this, ActvSequenceData.class));
			else
				startActivity(new Intent(this, ActvBillingOption.class));
		}

		// End Added on 1.3.2014

	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("onResume", "ActvDuplicateBillPrinting");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e("onRestart", "ActvDuplicateBillPrinting");
	}

	public void onBackPressed() {
		// do something on back.
		finish();
		// startActivity(new Intent(this, ActvBilling.class));

		return;
	}

	class AnalogicImpact extends Thread {
		private BluetoothDevice device = null;
		private BluetoothSocket btSocket = null;
		private OutputStream outStream = null;
		private OutputStreamWriter writer = null;
		private FileOutputStream fileStream = null;
		File txtBill;
		private String address;

		public AnalogicImpact(String address) {
			this.address = address;
			
		}

		public void run() {
			
			
				try {
					
				device = mBluetoothAdapter.getRemoteDevice(address);
				btSocket =NetworkUtil.createBluetoothSocket(device,UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				
				//btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
				
				mBluetoothAdapter.cancelDiscovery();
					btSocket.connect();
					Thread.sleep(500);
					Toast.makeText(getBaseContext(),"Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
				}catch(Exception ex)
				{
					Toast.makeText(
							getBaseContext(),
							"No Bluetooth printer detected..please connect and try again! : ",
							Toast.LENGTH_LONG).show();
					startActivity(new Intent(getBaseContext(),
							ActvBillingOption.class));
				}	
				
				try
				{
					txtBill=new File(Environment.getExternalStorageDirectory()+"/SBDocs/BillText.txt");
					if(txtBill.exists())
					{
						txtBill.delete();
						txtBill.createNewFile();
					}
					else
					{
						txtBill.createNewFile();
					}
					fileStream=new FileOutputStream(txtBill);
				
					outStream = fileStream;				
					writer = new OutputStreamWriter(outStream);
				} catch (Exception e) {
					Toast.makeText(
							getBaseContext(),
							"Error in creating file",
							Toast.LENGTH_LONG).show();
					startActivity(new Intent(getBaseContext(),
							ActvBillingOption.class));
				}			

			try {
				
				String txt = "Dkr.";
				Toast.makeText(getBaseContext(), BILL, Toast.LENGTH_SHORT)
						.show();

				char lf = 0x0A;

				writer.write(lf);
				writer.flush();

				// ////////Print On Paper Start////////////

				writer.write(String.format("      %s\n", "DUPLICATE BILL"));
				writer.flush();

				writer.write(String
						.format("      %s\n", UtilAppCommon.bill.DIV));
				writer.flush();

				writer.write(String.format("      %s\n",
						UtilAppCommon.bill.SUB_DIV));
				writer.flush();

				writer.write(String.format("      %s\n",
						UtilAppCommon.bill.SECTION));
				writer.flush();

				writer.write(String.format("          %s\n",
						UtilAppCommon.bill.BILL_ACC_NO));
				writer.flush();

				writer.write(String.format("          %s\n\n\n",
						UtilAppCommon.bill.OLD_ACC_NO));
				writer.flush();

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// writer.write(String.format("           \n\n"));
				// writer.flush();

				writer.write(String.format("          %s\n",
						UtilAppCommon.bill.POLL_NO));
				writer.flush();

				writer.write(String.format("          %s\n",
						UtilAppCommon.bill.BILL_PERIOD));
				writer.flush();

				writer.write(String.format("          %s\n",
						UtilAppCommon.bill.NO_OF_MONTHS));
				writer.flush();

				writer.write(String.format("    %s\n",
						UtilAppCommon.bill.BILLDATE));
				writer.flush();

				writer.write(String.format("           \n"));
				writer.flush();

				writer.write(String.format("%s\n", UtilAppCommon.bill.NAME));
				writer.flush();

				writer.write(String.format("%s\n", UtilAppCommon.bill.ADDR1));
				writer.flush();

				writer.write(String.format("%s\n", UtilAppCommon.bill.ADDR2));
				writer.flush();

				writer.write(String.format("%s\n\n", UtilAppCommon.bill.ADDR3));
				writer.flush();

				// writer.write(String.format("           \n"));
				// writer.flush();

				writer.write(String.format("          %s\n",
						UtilAppCommon.bill.SEC_DEPOSIT));
				writer.flush();

				writer.write(String.format("            %s\n",
						UtilAppCommon.bill.CONS_STATUS));
				writer.flush();

				writer.write(String.format("      %s    %s \n",
						UtilAppCommon.bill.CATEGORY, UtilAppCommon.bill.MF));//
				writer.flush();

				//
				// if(ActvBilling.taid.charAt(0)=='1')
				// {
				// if(ActvBilling.taid.charAt(1)=='0')
				// {
				// writer.write(String.format("      DOM        %5.0f\n",
				// UtilAppCommon.in.MF));
				// writer.flush();
				//
				//
				// }
				// else
				// {
				// writer.write(String.format("      KTJ        %5.0f\n",
				// UtilAppCommon.in.MF));
				// writer.flush();
				//
				// }
				//
				//
				// }
				// else if(ActvBilling.taid.charAt(0)=='2')
				// {
				// writer.write(String.format("      COM        %5.0f\n",
				// UtilAppCommon.in.MF));
				// writer.flush();
				//
				// }
				// else
				// {
				// //(ActvBilling.taid.charAt(0)=='9')
				// writer.write(String.format("      PI         %5.0f\n",
				// UtilAppCommon.in.MF));
				// writer.flush();
				//
				// }
				//
				writer.write(String.format("    %s      %s\n",
						UtilAppCommon.bill.LOAD, UtilAppCommon.bill.PHASE));
				writer.flush();

				// if(ActvBilling.gMD == 1)
				// {
				// writer.write(String.format("    %5.2fKW      %5d\n",
				// UtilAppCommon.out.MAX_DEMD,UtilAppCommon.in.SDO_CD.charAt(3)!='0'?1:3));
				// writer.flush();
				//
				// }
				// else
				// {
				//
				// writer.write(String.format("    %5.2fKW      %5d\n",
				// UtilAppCommon.in.CONN_LOAD,UtilAppCommon.in.SDO_CD.charAt(3)!='0'?1:3));
				// writer.flush();
				//
				// }

				writer.write(String.format("           \n"));
				writer.flush();

				writer.write(String.format("    %s    %s %s\n",
						UtilAppCommon.bill.PREVIOUS_READING,
						UtilAppCommon.bill.PREVIOUS_MONTH,
						UtilAppCommon.bill.PREVIOUS_STATUS));
				writer.flush();

				writer.write(String.format("            %s\n",
						UtilAppCommon.bill.OLD_MTR_FLRDG));
				writer.flush();

				writer.write(String.format("            %s\n",
						UtilAppCommon.bill.NEW_MTR_ILRDG));
				writer.flush();

				{

					// int
					// mon=Integer.parseInt(UtilAppCommon.out.CUR_RED_DT.substring(3,5));
					// int
					// year=Integer.parseInt(UtilAppCommon.out.CUR_RED_DT.substring(6,8));

					if (UtilAppCommon.bill.CUR_MTR_STATUS.equals("L")
							|| UtilAppCommon.bill.CUR_MTR_STATUS.equals("N")
							|| UtilAppCommon.bill.CUR_MTR_STATUS.equals("O")) {

						writer.write(String.format("           %s  %s\n",
								UtilAppCommon.bill.CURMTH,
								UtilAppCommon.bill.CUR_MTR_STATUS));
						writer.flush();

					} else {

						writer.write(String.format("     %s    %s %s\n",
								UtilAppCommon.bill.CURRRDG,
								UtilAppCommon.bill.CURMTH,
								UtilAppCommon.bill.CUR_MTR_STATUS));
						writer.flush();
					}

				}

				writer.write(String.format("          %12s\n",
						UtilAppCommon.bill.METER_NO));
				writer.flush();

				writer.write(String.format("          %s\n",
						UtilAppCommon.bill.MTR_OWNER));
				writer.flush();

				writer.write(String.format("          %s\n",
						UtilAppCommon.bill.BILLBASIS));
				writer.flush();

				writer.write(String.format("          %s\n\n",
						UtilAppCommon.bill.BILL_UNITS));
				writer.flush();

				writer.write(String.format("       %s\n",
						UtilAppCommon.bill.SLAB1));
				writer.flush();

				writer.write(String.format("       %s\n",
						UtilAppCommon.bill.SLAB2));
				writer.flush();

				writer.write(String.format("       %s\n",
						UtilAppCommon.bill.SLAB3));
				writer.flush();

				writer.write(String.format("          %s\n",
						UtilAppCommon.bill.SLAB4));
				writer.flush();

				// writer.write(String.format("  \n"));
				// writer.flush();

				writer.write(String.format("         %12s\n",
						UtilAppCommon.bill.EC));
				writer.flush();

				writer.write(String.format("         %12s\n",
						UtilAppCommon.bill.FIXCHG));
				writer.flush();

				writer.write(String.format("         %12s\n",
						UtilAppCommon.bill.METERRENT));
				writer.flush();

				writer.write(String.format("         %12s\n",
						UtilAppCommon.bill.ED));
				writer.flush();

				writer.write(String.format("         %12s\n",
						UtilAppCommon.bill.DPS));
				writer.flush();

				writer.write(String.format("         %12s\n\n",
						UtilAppCommon.bill.PRE_BILL_AMT));
				writer.flush();
				// float arr_amt =
				// Float.parseFloat(UtilAppCommon.bill.ARREAR_AMT);
				writer.write(String.format("         %12s\n",
						UtilAppCommon.bill.ARREAR_AMT));
				writer.flush();

				writer.write(String.format("         %12s\n",
						UtilAppCommon.bill.ADJ_AMT));
				writer.flush();

				writer.write(String.format("         %s\n\n",
						UtilAppCommon.bill.SUNDRY_AMT));
				writer.flush();

				// {
				// long AMT;

				// AMT = Math.round((double) UtilAppCommon.out.NETAFTDUEDT*100);

				// AMT = Math.round((double) UtilAppCommon.bill.NET_AFT_DUEDT);

				writer.write(String.format("       %s\n",
						UtilAppCommon.bill.NET_AFT_DUEDT));
				writer.flush();

				// }

				writer.write(String.format("         %12s\n",
						UtilAppCommon.bill.REBOFF));
				writer.flush();

				// {
				//
				// long AMT = Math.round((double)
				// UtilAppCommon.bill.NET_BEF_DUEDT);

				writer.write(String.format("       %12s\n",
						UtilAppCommon.bill.NET_BEF_DUEDT));
				writer.flush();

				// }

				writer.write(String.format("          %s\n",
						UtilAppCommon.bill.REB_DT));
				writer.flush();

				writer.write(String.format("          %s\n",
						UtilAppCommon.bill.DUE_DT));
				writer.flush();

				if ((UtilAppCommon.bill.BOOKNO1.equals("9999"))
						&& (UtilAppCommon.bill.RECEPT_NO1.equals("1"))) {
					writer.write(String.format("    %8s    %8s\n",
							UtilAppCommon.bill.BOOKNO2,
							UtilAppCommon.bill.RECEPT_NO2));
					writer.flush();

					writer.write(String.format("    %8s    %6s\n",
							UtilAppCommon.bill.PMT_DT2,
							UtilAppCommon.bill.PMT_AMT2));
					writer.flush();

					writer.write(String.format("    %8s    %8s\n",
							UtilAppCommon.bill.BOOKNO3,
							UtilAppCommon.bill.RECEPT_NO3));
					writer.flush();

					writer.write(String.format("    %8s    %6s\n",
							UtilAppCommon.bill.PMT_DT3,
							UtilAppCommon.bill.PMT_AMT3));
					writer.flush();

					writer.write(String.format("ISD @6%% Rs.%0.2f/-\n",
							UtilAppCommon.bill.PMT_AMT1));
					writer.flush();

				} else if ((UtilAppCommon.bill.BOOKNO2.equals("9999"))
						&& (UtilAppCommon.bill.RECEPT_NO2.equals("1"))) {

					writer.write(String.format("    %8s    %8s\n",
							UtilAppCommon.bill.BOOKNO1,
							UtilAppCommon.bill.RECEPT_NO1));
					writer.flush();

					writer.write(String.format("    %8s    %6s\n",
							UtilAppCommon.bill.PMT_DT1,
							UtilAppCommon.bill.PMT_AMT1));
					writer.flush();

					writer.write(String.format("    %8s    %8s\n",
							UtilAppCommon.bill.BOOKNO3,
							UtilAppCommon.bill.RECEPT_NO3));
					writer.flush();

					writer.write(String.format("    %8s    %6sf\n",
							UtilAppCommon.bill.PMT_DT3,
							UtilAppCommon.bill.PMT_AMT3));
					writer.flush();

					writer.write(String.format("ISD @8.75%% Rs.%0.2f/-\n\n",
							UtilAppCommon.bill.PMT_AMT2));
					writer.flush();

				} else if ((UtilAppCommon.bill.BOOKNO3.equals("9999"))
						&& (UtilAppCommon.bill.RECEPT_NO3.equals("1"))) {
					writer.write(String.format("    %8s    %8s\n",
							UtilAppCommon.bill.BOOKNO1,
							UtilAppCommon.bill.RECEPT_NO1));
					writer.flush();

					writer.write(String.format("    %8s    %6sf\n",
							UtilAppCommon.bill.PMT_DT1,
							UtilAppCommon.bill.PMT_AMT1));
					writer.flush();

					writer.write(String.format("    %8s    %8s\n",
							UtilAppCommon.bill.BOOKNO2,
							UtilAppCommon.bill.RECEPT_NO2));
					writer.flush();

					writer.write(String.format("    %8s    %6sf\n",
							UtilAppCommon.bill.PMT_DT2,
							UtilAppCommon.bill.PMT_AMT2));
					writer.flush();

					writer.write(String.format("ISD @8.75%% Rs.%0.2f/-\n\n",
							UtilAppCommon.bill.PMT_AMT3));
					writer.flush();

				} else {

					writer.write(String.format("    %8s    %8s\n",
							UtilAppCommon.bill.BOOKNO1,
							UtilAppCommon.bill.RECEPT_NO1));
					writer.flush();

					writer.write(String.format("    %8s    %6s\n",
							UtilAppCommon.bill.PMT_DT1,
							UtilAppCommon.bill.PMT_AMT1));
					writer.flush();

					writer.write(String.format("    %8s    %8s\n",
							UtilAppCommon.bill.BOOKNO2,
							UtilAppCommon.bill.RECEPT_NO2));
					writer.flush();

					writer.write(String.format("    %8s    %6s\n",
							UtilAppCommon.bill.PMT_DT2,
							UtilAppCommon.bill.PMT_AMT2));
					writer.flush();

					writer.write(String.format("    %8s    %8s\n",
							UtilAppCommon.bill.BOOKNO3,
							UtilAppCommon.bill.RECEPT_NO3));
					writer.flush();

					writer.write(String.format("    %8s    %6s\n",
							UtilAppCommon.bill.PMT_DT3,
							UtilAppCommon.bill.PMT_AMT3));
					writer.flush();

				}

				if (Double.parseDouble(UtilAppCommon.bill.ASD) > 0) {

/*					writer.write(String.format("Also Pay Rs.%.2f/-\n",
							Double.parseDouble(UtilAppCommon.bill.ASD)));
					writer.flush();

					writer.write(String.format(" Towards Revised ASD \n"));
					writer.flush();

					writer.write(String.format("   Before %s\n",
							UtilAppCommon.bill.ASD_DUE_DT));
					writer.flush();*/
					writer.write("\n\n\n");
					writer.flush();

				} else {

					System.out.println("Inside aasd else\n");
					writer.write("\n\n\n");
					writer.flush();
				}

				writer.write(String.format("      %s        %s\n\n",
						UtilAppCommon.bill.SUND_CODE,
						UtilAppCommon.bill.ARR_SUND_AMT));
				writer.flush();

				writer.write("\n\n\n");
				writer.flush();
				writer.write(String.format("          %12s\n",
						UtilAppCommon.bill.DEFERRED_AMT));
				writer.flush();

				writer.write(String.format("          %12s\n",
						UtilAppCommon.bill.ARR_EC));
				writer.flush();

				writer.write(String.format("          %12s\n",
						UtilAppCommon.bill.ARR_ED));
				writer.flush();

				writer.write(String.format("          %12s\n",
						UtilAppCommon.bill.ARR_DPS));
				writer.flush();

				if (UtilAppCommon.bill.SUNDRY_AMT_FLAG == "Q") {
					writer.write(String.format("Sundry Code 'Q'=RC FEES"));
					writer.write("\n\n\n\n");
					writer.flush();
				} else {
					writer.write("\n\n\n\n\n");
					writer.flush();
				}

				writer.write(lf);
				writer.flush();
				writer.write(lf);
				writer.flush();
				writer.write(lf);
				writer.flush();
				writer.write(lf);
				writer.flush();
				writer.close();
				outStream.close();

				// Added On 20-8-2014
				StringBuilder text = new StringBuilder();

				try {
					Scanner br = new Scanner(txtBill);
					do {

						String strLine = br.nextLine();
						text.append(strLine);
						text.append("\n");

					} while (br.hasNextLine());

				} catch (IOException e) {
					// You'll need to add proper error handling here
				}

				String msg = text.toString();
				outStream = btSocket.getOutputStream();
				outStream.write(msg.getBytes());
				Thread.sleep(1000);
				outStream.close();

				// msg += "\n";
				// finish();
				if(UtilAppCommon.billType.equalsIgnoreCase("A"))
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
				else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
					startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
				else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
					startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
				else
					startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
				// startActivity(new Intent(this, ActvBillingOption.class));

			} catch (Exception e) {
				Toast.makeText(getBaseContext(),
						"exception in sendMsg : " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				try {
					btSocket.close();
				} catch (IOException e2) {
					Toast.makeText(getBaseContext(),
							"exception in closing socket : " + e2.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		public void closeSocket() {
			try {
				btSocket.close();
			} catch (IOException e2) {
				Toast.makeText(getBaseContext(),
						"exception in closing socket : " + e2.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}
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

				cpclData = "! 90 200 200 1020 1\r\n";
				cpclData += "CENTER\r\n";
				cpclData += "UNDERLINE ON\r\n";

				cpclData += PrintUtilZebra.PrintLargeNext("Duplicate Bill");
				
				cpclData += PrintUtilZebra.PrintLargeNext(UtilAppCommon.out.Company+"CL");
				cpclData += PrintUtilZebra
						.PrintNext("*********************");
				cpclData += "LEFT\r\n";
				cpclData += PrintUtilZebra.PrintNext(String.format(
						" ELECTRICITY BILL :%s", UtilAppCommon.out.BillMonth));
				//cpclData += "UNDERLINE OFF\r\n";
				cpclData += PrintUtilZebra
						.PrintNext("*****************************");
				cpclData += PrintUtilZebra.PrintNext(String.format(
						" DATE: %s", ft.format(new java.util.Date())));
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
						String.format("%s", UtilAppCommon.out.CANumber));
				
				
				cpclData += PrintUtilZebra.PrintNextData(" LEGACY NO       ",
						String.format("%s", UtilAppCommon.out.LegacyNumber));
				
			
				cpclData += PrintUtilZebra.PrintNextData(" MRU             ",
						String.format("%10s", UtilAppCommon.out.MRU));
				
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"NAME    : %s", UtilAppCommon.out.Name));
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"ADDRESS  : "));
				cpclData += "LEFT\r\n";
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						" %s", UtilAppCommon.out.Address.substring(0, 24)));
			
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"AREA TYPE  : %s", UtilAppCommon.out.Area_type));
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"POLE NO          : %s", UtilAppCommon.out.PoleNo));
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"MTR NO: %s PH: %s", UtilAppCommon.out.MtrNo,UtilAppCommon.out.Phase ));
	
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"MTR COMP :%s", UtilAppCommon.out.MtrMake
								.equalsIgnoreCase("C") ? "Company" : UtilAppCommon.out.MtrMake
										.equalsIgnoreCase("O") ? "Consumer" : ""));
				
				//
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"CATEGORY         : %s", UtilAppCommon.out.Category));
				

			String CD="";
			String CD1="";
			String CD2="";
			float val=0;
			if (UtilAppCommon.out.SanctLoad.length() > 0)
			{
				val = Float.parseFloat(UtilAppCommon.out.SanctLoad);
				CD = val + " KW";
			    CD1 ="";
			    CD2 ="";
			}
			else if (UtilAppCommon.out.ConnectedLoad.length() > 0)
			{
				val = Float.parseFloat(UtilAppCommon.out.ConnectedLoad);
				CD = "";
			    CD1 =val +" HP";
			    CD2 ="";
			}
			else if (UtilAppCommon.out.CD.length() > 0)
			{
				val = Float.parseFloat(UtilAppCommon.out.CD);
				CD = "";CD1 = "";
				 CD2 = val+" KW";		
			}
			cpclData += PrintUtilZebra.PrintNext(String.format(
					"SL:%s    CL:%s   CD:%s ", CD,  CD1, CD2));

				cpclData += PrintUtilZebra.PrintNext(String.format(
						"SD               : %s", UtilAppCommon.out.SD));
				
			
				String billdays;
				
				if(UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) Actual"))
				{
				billdays= UtilAppCommon.out.BillDays+"("+UtilAppCommon.out.MESSAGE10+")";
				}
				else
				{
					billdays= UtilAppCommon.out.BillDays;
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
				   
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"READING :   %s   \t %S", UtilAppCommon.out.PreviusReading, UtilAppCommon.out.CurrentReading));
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"DATE    :   %s   \t %s", UtilAppCommon.out.PrevusMtrRdgDt, UtilAppCommon.out.CurrentMtrRdgDt));
				
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"STATUS  :   %s   \t     %s", UtilAppCommon.out.PreviusMtrReadingNote, UtilAppCommon.out.CurrentMtrReadingNote));
				
				cpclData += "PRINT\r\n";

				thePrinterConn.write(cpclData.getBytes());
				PrintUtilZebra.LineNo = 0;
				// Print Reading Image
				try {
					String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES+ "/SBDocs/Photos_Crop"
							+ "/"
							+ UtilAppCommon.sdoCode
							+ "/"
							+ UtilAppCommon.out.MRU)
							.getAbsolutePath();
					//getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/SBDocs/Photos/"+UtilAppCommon.in.SUB_DIVISION_CODE+"/"+ UtilAppCommon.in.MRU)
					//String PhotoPath = PhotoDir + "/"+ UtilAppCommon.out.CANumber + ".jpg";
					/*String PhotoPath = PhotoDir + "/BSB" + "_" + 
							UtilAppCommon.sdoCode + UtilAppCommon.out.MRU + 
									UtilAppCommon.out.CANumber + ".jpg";*/
					strmonth = String.valueOf(Arrays.asList(strMonths).indexOf(UtilAppCommon.out.BillMonth) + 1);
					//String PhotoPath = PhotoDir + "/" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + 
					//		UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + 
					//		"_" + UtilAppCommon.out.CANumber  + ".jpg";
					//String PhotoPath = PhotoDir +
							 // "/201510_" + UtilAppCommon.out.CANumber  + ".jpg";
					File filenow=new File(PhotoDir,"/201510_" + UtilAppCommon.out.CANumber  + ".jpg");
					Log.e("Photo Path", filenow.getAbsolutePath());
					
					thePrinterConn.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
					
					
					//printer.getGraphicsUtil().printImage("/storage/sdcard0/img_tick.png",0,0,-1,-1,false);
					//printer.getGraphicsUtil().printImage(PhotoPath, 100, 0, -1,-1, false);
					Bitmap img_btm=Utilities.getBitmapForAllVersions(getApplicationContext(),filenow);
					printer.getGraphicsUtil().printImage(img_btm,100, 0, -1,-1, false);
				} catch (Exception ex) {

					System.out
							.println("Error In Photo Print: " + ex.toString());
				}
				// Print Reading Image End

				cpclData = "! 90 200 200 1330 1\r\n";
				
				cpclData += "\n\n";
				
				float mf=0,consump=0;
				mf=Float.parseFloat(UtilAppCommon.out.MF);
				consump=Float.parseFloat(UtilAppCommon.out.Consumption);
				cpclData += PrintUtilZebra.PrintNext(String.format("MULTIPLYING FACTOR:  %.2f", mf));
				cpclData += PrintUtilZebra.PrintNext(String.format("CONSUMPTION:  %.0f",consump));

				cpclData += PrintUtilZebra.PrintNext(String.format("RECORDED DEMD: %s", UtilAppCommon.out.RecordedDemd));
				cpclData += PrintUtilZebra.PrintNext(String.format("POWER FACTOR:%s",  UtilAppCommon.out.PowerFactor));

				float mmcunits=0,avg=0;
				if (UtilAppCommon.out.Category.equals("DS-II")||UtilAppCommon.out.Category.equals("NDS-IM"))
				{
					mmcunits = 0;
				}
					else
					{
						mmcunits = Float.parseFloat(UtilAppCommon.out.MMCUnits);
				}
				//if (UtilAppCommon.out.Type.equalsIgnoreCase("ACTUAL")||UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) Actual")||UtilAppCommon.out.Type.equalsIgnoreCase("(PL Adj.) MMC"))
				if (UtilAppCommon.out.CurrentMtrReadingNote.equalsIgnoreCase("OK"))	
						{
					     avg = 0;
					     }
				else
				{
					avg = Float.parseFloat(UtilAppCommon.out.Average);
				}
				cpclData += PrintUtilZebra.PrintNext(String.format(
						"MMC UNITS:%.2f\tAVG.:%.2f", mmcunits, avg));
				
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
				
				float pmtonacct=0,arrengdue=0,arrdps=0,arrothr=0;
				pmtonacct=Float.parseFloat(UtilAppCommon.out.PaymentOnAccount);
				arrengdue=Float.parseFloat(UtilAppCommon.out.ArrearEnergyDues);
				arrdps=Float.parseFloat(UtilAppCommon.out.ArrearDPs);
				arrothr=Float.parseFloat( UtilAppCommon.out.ArrearOthers);
				cpclData += PrintUtilZebra.PrintNextData1("PYMT ON ACCT     ",
						String.format("%.2f", pmtonacct));
					
				cpclData += PrintUtilZebra.PrintNextData1("ENERGY DUES          ",
						String.format("%.2f", arrengdue));	
				
				cpclData += PrintUtilZebra.PrintNextData1(" ARREAR DPS          ",
						String.format("%.2f", arrdps));	
			
				cpclData += PrintUtilZebra.PrintNextData1(" OTHERS              ",
						String.format("%.2f", arrothr));	
			
				
				cpclData += PrintUtilZebra.PrintNextData1(" SUB TOTAL(A)        ",
						String.format("%10s", UtilAppCommon.out.ArrearSubTotal_A));	
				cpclData += PrintUtilZebra
						.PrintNext("**************************");
				
				cpclData += "CENTER\r\n";
				cpclData += PrintUtilZebra
						.PrintNext("CURRENT BILL DETAILS");
				cpclData += PrintUtilZebra
						.PrintNext("----------------");
				cpclData += "LEFT\r\n";
				
				float engchg=0;
				engchg=Float.parseFloat(UtilAppCommon.out.CurrentEnergyCharges);
				cpclData += PrintUtilZebra.PrintNextData1("ENERGY CHARGES       ",
						String.format("%.2f", engchg));	
				

				cpclData += PrintUtilZebra.PrintNextData1("DPS     ",
						String.format("%10s", UtilAppCommon.out.CurrentMonthDps));	
				
				cpclData += PrintUtilZebra.PrintNextData1("FIXED/DEMD CHG    ",
						String.format("%10s", UtilAppCommon.out.FixDemdCharge));	
				
				cpclData += PrintUtilZebra.PrintNextData1("EXCESS DEMD CHG    ",
						String.format("%10s", UtilAppCommon.out.ExcessDemdCharge));
				
				
				cpclData += PrintUtilZebra.PrintNextData1("ELEC. DUTY    ",
						String.format("%10s", UtilAppCommon.out.ElectricityDuty));
			
				
				cpclData += PrintUtilZebra.PrintNextData1(" METER RENT        ",
						String.format("%10s", UtilAppCommon.out.MeterRent));
				
				
				
				cpclData += PrintUtilZebra.PrintNextData1(" SHUNT CAP. CHG ",
						String.format("%10s", UtilAppCommon.out.ShauntCapCharge));
				
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
				

				cpclData += PrintUtilZebra.PrintNextData1("  REBATES ON MMC        ",
						String.format("%10s", UtilAppCommon.out.RebateOnMMC));
				

				cpclData += PrintUtilZebra.PrintNextData1("TOTAL(A+B+C)   ",
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
						String.format("%s", UtilAppCommon.out.AmtPayableUptoDt),String.format("%s", UtilAppCommon.out.AmtPayableUptoAmt));
				
				cpclData += PrintUtilZebra.PrintNextData2(" BY       ",
						String.format("%s", UtilAppCommon.out.AmtPayablePYDt),String.format("%s", UtilAppCommon.out.AmtPayablePYAmt));
	
				cpclData += PrintUtilZebra.PrintNextData2(" AFTER    ",
						String.format("%s", UtilAppCommon.out.AmtPayableAfterDt),String.format("%s", UtilAppCommon.out.AmtPayableAfterAmt));		
							
				cpclData += PrintUtilZebra
						.PrintNext("**************************");
				
				cpclData += PrintUtilZebra.PrintNext("DETAILS OF LAST PAYMENT");
				cpclData += PrintUtilZebra
						.PrintNext("**************************");
				//cpclData += "LEFT\r\n";
				
				cpclData += PrintUtilZebra.PrintNextData1("   LAST PAID AMT     ",
						String.format("%10s", UtilAppCommon.out.LastPaymentAmt));
				
				cpclData += PrintUtilZebra.PrintNextData1("   LAST PAID DT      ",
						String.format("%10s", UtilAppCommon.out.LastPaidDate));

				cpclData += PrintUtilZebra.PrintNextData1("   RECEIPT NO        ",
						String.format("%12s", UtilAppCommon.out.ReceiptNumber));
				
				cpclData += PrintUtilZebra.PrintNextData1("   MTR RDR        ",
						String.format("%12s", UtilAppCommon.out.MTR_READER_ID));
				
				cpclData += "ENDQR\r\nPRINT\r\n";

				cpclData += "PRINT\r\n";

				thePrinterConn.write(cpclData.getBytes());

				Thread.sleep(500);

				thePrinterConn.close();
				PrintUtilZebra.LineNo = 0;

	/*			if(UtilAppCommon.billType.equalsIgnoreCase("A"))
					startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
				else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
					startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
				else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
					startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
				else*/
					startActivity(new Intent(getBaseContext(), ActvBillingOption.class));

			} catch (Exception e) {
				// Handle communications error here.
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();

			}

		}
	}

	class EPSONThermal extends Thread {

		private BluetoothDevice device = null;
		private String address = null;

		public EPSONThermal(String address) {
			this.address = address;
			device = mBluetoothAdapter.getRemoteDevice(address);
			Toast.makeText(getApplicationContext(), "Connected To:" + address,
					Toast.LENGTH_LONG).show();
		}

		public void run() {
			//if (PrinterManager.connectPrinter(address)) {
				//Print printer = PrinterManager.getPrinter();
				Print printer = new Print();
				int[] status = new int[1];
				int[] battery = new int[1];
				status[0] = 0;
				battery[0] = 0;
				try {
					Toast.makeText(getApplicationContext(), "Sending Data",
							Toast.LENGTH_LONG).show();
					Builder builder = new Builder("TM-P60", Builder.MODEL_ANK);
					builder.addCommand(new byte[]{0x1C, 0x28, 0x43, 0x02, 0x00, 0x30, 0x02});
				/*try {
					//Bitmap azadi = BitmapFactory.decodeResource(getResources(), R.drawable.chunav);
					Bitmap azadi = Utilities.getBitmapFromDrawable(getApplicationContext(),R.drawable.chunav);
					azadi = Bitmap.createScaledBitmap(azadi, 350, 220, true);
					builder.addImage(azadi, 0, 0, azadi.getWidth(), azadi.getHeight(), Builder.PARAM_DEFAULT);
					Log.v("Azadi Photo Print Added", "Azadi Photo Print Added");
				} catch (Exception ex) {
					Log.v("Azadi Photo Print", ex.getMessage());
					System.out
							.println("Error In Azadi Photo Print: " + ex.toString());
				}*/
					builder.addTextFont(Builder.FONT_A);
					builder.addTextAlign(Builder.ALIGN_CENTER);
					builder.addTextSize(2, 2);
					builder.addTextStyle(Builder.FALSE, Builder.FALSE,
							Builder.TRUE, Builder.PARAM_UNSPECIFIED);
					if (UtilAppCommon.ui.METER_READER_ID == "1")
						builder.addText("NESCO\n");
					else if (UtilAppCommon.ui.METER_READER_ID == "2")
						builder.addText("WESCO\n");
					else
						builder.addText("SOUTHCO\n");

					builder.addTextSize(1, 1);
					builder.addText("DUPLICATE BILL\n------------------------\n");
					builder.addTextStyle(Builder.FALSE, Builder.FALSE,
							Builder.FALSE, Builder.PARAM_UNSPECIFIED);

					builder.addTextAlign(Builder.ALIGN_LEFT);

					builder.addText(String.format("  Division   : %s\n",
							UtilAppCommon.bill.DIV));

					builder.addText(String.format("  Sub Divn   : %s\n",
							UtilAppCommon.bill.SUB_DIV));

					builder.addText(String.format("  Section    : %s\n",
							UtilAppCommon.bill.SECTION));

					builder.addText(String.format("  New A/C No  : %s\n",
							UtilAppCommon.bill.BILL_ACC_NO));

					builder.addText(String.format("  Old A/C No  : %s\n",
							UtilAppCommon.bill.OLD_ACC_NO));

					builder.addText(String.format("  Pole No       :  %s\n",
							UtilAppCommon.bill.POLL_NO));

					builder.addText(String.format("  Bill Period  : %s\n",
							UtilAppCommon.bill.BILL_PERIOD));

					builder.addText(String.format("  No of Mths   : %s\n",
							UtilAppCommon.bill.NO_OF_MONTHS));

					builder.addText(String.format("  Bill Dt      : %s\n",
							UtilAppCommon.bill.BILLDATE));

					builder.addText(String.format("  Name & Address   :   \n"));

					builder.addText(String
							.format("  %s\n", UtilAppCommon.bill.NAME));

					builder.addText(String.format("  %s\n",
							UtilAppCommon.bill.ADDR1));

					builder.addText(String.format("  %s\n",
							UtilAppCommon.bill.ADDR2));

					builder.addText(String.format("  %s\n",
							UtilAppCommon.bill.ADDR3));

					// builder.addText(String.format("           "));
					//

					builder.addText(String.format("  Security Deposit  : %s\n",
							UtilAppCommon.bill.SEC_DEPOSIT));

					builder.addText(String.format("  Consumer Status   : %s\n",
							UtilAppCommon.bill.CONS_STATUS));

					builder.addText(String.format("  Category : %s\n", UtilAppCommon.bill.CATEGORY));//
					builder.addText(String.format("  MULTIPLYING FACTOR : %s\n", UtilAppCommon.bill.MF));//

					builder.addText(String.format("  Load: %s    PH: %s\n",
							UtilAppCommon.bill.LOAD, UtilAppCommon.bill.PHASE));

					builder.addText(String
							.format("    Reading      Month    Status \n"));

					builder.addText(String.format("      %s      %s    %s\n",
							UtilAppCommon.bill.PREVIOUS_READING,
							UtilAppCommon.bill.PREVIOUS_MONTH,
							UtilAppCommon.bill.PREVIOUS_STATUS));

					builder.addText(String.format("              %s\n",
							UtilAppCommon.bill.OLD_MTR_FLRDG));

					builder.addText(String.format("              %s\n",
							UtilAppCommon.bill.NEW_MTR_ILRDG));

					{

						// int
						// mon=Integer.parseInt(UtilAppCommon.out.CUR_RED_DT.substring(3,5));
						// int
						// year=Integer.parseInt(UtilAppCommon.out.CUR_RED_DT.substring(6,8));

						if (UtilAppCommon.bill.CUR_MTR_STATUS.equals("L")
								|| UtilAppCommon.bill.CUR_MTR_STATUS.equals("N")
								|| UtilAppCommon.bill.CUR_MTR_STATUS.equals("O")) {

							builder.addText(String.format(
									"             %s      %s\n",
									UtilAppCommon.bill.CURMTH,
									UtilAppCommon.bill.CUR_MTR_STATUS));

						} else {

							builder.addText(String.format(
									"       %s        %s    %s\n",
									UtilAppCommon.bill.CURRRDG,
									UtilAppCommon.bill.CURMTH,
									UtilAppCommon.bill.CUR_MTR_STATUS));

						}
					}

					// Print Reading Image
					try {
					/*String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
							.getPath()
							+ "/SBDocs/Photos_Crop"
							+ "/"
							+ UtilAppCommon.bill.BILL_ACC_NO.substring(0, 4)
							+ "/"
							+ UtilAppCommon.bill.BILL_ACC_NO.substring(4, 8);*/
					/*String PhotoPath = PhotoDir + "/" + "MSB"
							+ UtilAppCommon.bill.BILL_ACC_NO + ".jpg";*/
						String PhotoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/SBDocs/Photos/" + UtilAppCommon.in.SUB_DIVISION_CODE + "/" + UtilAppCommon.in.MRU).getAbsolutePath();

						strmonth = String.valueOf(Arrays.asList(strMonths).indexOf(UtilAppCommon.out.BillMonth) + 1);
//					String PhotoPath = PhotoDir + "/" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
//							UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
//							"_" + UtilAppCommon.in.CONTRACT_AC_NO;


//						File file_crop = new File(PhotoDir, UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) +
//								UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) +
//								"_" + UtilAppCommon.in.CONTRACT_AC_NO + ".jpg");
						//Uri uri = AppUtil.getFileUri(ActvDuplicateBillPrinting.this, file_crop);
						//Bitmap bitmap_crop = MediaStore.Images.Media.getBitmap(ActvDuplicateBillPrinting.this.getContentResolver(), uri);
//						Bitmap bitmap_crop= Utilities.getBitmapForAllVersions(ActvDuplicateBillPrinting.this,file_crop);
//						System.out.println("Photo: " + PhotoPath);
//						builder.addImage(bitmap_crop, 0, 0, bitmap_crop.getWidth(),
//								bitmap_crop.getHeight(),
//								Builder.PARAM_DEFAULT);

					} catch (Exception ex) {
						System.err.println("Error In Photo Print: " + ex.getMessage());
					}
					// Print Reading Image End

					builder.addText(String.format("  Mtr No      :%12s\n",
							UtilAppCommon.bill.METER_NO));

					builder.addText(String.format("  Mtr Owner   :%s\n",
							UtilAppCommon.bill.MTR_OWNER));

					builder.addText(String.format("  Bill Basis  : %s\n",
							UtilAppCommon.bill.BILLBASIS));

					builder.addText(String.format("  Bill Units  : %s\n",
							UtilAppCommon.bill.BILL_UNITS));

					builder.addText(String.format("  Bill Slabs :\n"));

					builder.addText(String.format("   1:  %s\n",
							UtilAppCommon.bill.SLAB1));

					builder.addText(String.format("    2:  %s\n",
							UtilAppCommon.bill.SLAB2));

					builder.addText(String.format("    3:  %s\n",
							UtilAppCommon.bill.SLAB3));

					builder.addText(String.format("    4:  %s\n",
							UtilAppCommon.bill.SLAB4));

					builder.addText(String.format("   Energy Chg    :%12s\n",
							UtilAppCommon.bill.EC));

					builder.addText(String.format("   Fix/Dem Chg   :%12s\n",
							UtilAppCommon.bill.FIXCHG));

					builder.addText(String.format("   Meter Rent    :%12s\n",
							UtilAppCommon.bill.METERRENT));

					builder.addText(String.format("   Elec.Duty     :%12s\n",
							UtilAppCommon.bill.ED));

					builder.addText(String.format("   DPS           :%12s\n",
							UtilAppCommon.bill.DPS));

					builder.addText(String.format("   Pres Bill Amt :%12s\n",
							UtilAppCommon.bill.PRE_BILL_AMT));

					// float arr_amt =
					// Float.parseFloat(UtilAppCommon.bill.ARREAR_AMT);
					builder.addText(String.format("   Arrears        :%12s\n",
							UtilAppCommon.bill.ARREAR_AMT));

					builder.addText(String.format("   Sundry Adj     :%12s\n",
							UtilAppCommon.bill.ADJ_AMT));

					builder.addText(String.format("   Adj.Amount     :%12s\n",
							UtilAppCommon.bill.SUNDRY_AMT));

					builder.addText(String.format("   After Due Date\n"));

					builder.addText(String.format("   #Net Amt        :%12s\n",
							UtilAppCommon.bill.NET_AFT_DUEDT));

					builder.addText(String.format("    By Due Date\n"));

					builder.addText(String.format("    Rebate       :%12s\n",
							UtilAppCommon.bill.REBOFF));

					builder.addText(String.format("   #Net Amt       :%12s\n",
							UtilAppCommon.bill.NET_BEF_DUEDT));

					builder.addText(String.format("    Rebate Date      :%s\n",
							UtilAppCommon.bill.REB_DT));

					builder.addText(String.format("    Due Date         :%s\n",
							UtilAppCommon.bill.DUE_DT));

					builder.addText(String.format("    Last Payment Details \n"));

					if ((UtilAppCommon.bill.BOOKNO1.equals("9999"))
							&& (UtilAppCommon.bill.RECEPT_NO1.equals("1"))) {
						builder.addText(String.format("   BKNO:%8s    Rcpt:%8s\n",
								UtilAppCommon.bill.BOOKNO2,
								UtilAppCommon.bill.RECEPT_NO2));

						builder.addText(String.format("   DT:  %8s    Amt: %6s\n",
								UtilAppCommon.bill.PMT_DT2,
								UtilAppCommon.bill.PMT_AMT2));

						builder.addText(String.format("    %8s    %8s\n",
								UtilAppCommon.bill.BOOKNO3,
								UtilAppCommon.bill.RECEPT_NO3));

						builder.addText(String.format("    %8s    %6s\n",
								UtilAppCommon.bill.PMT_DT3,
								UtilAppCommon.bill.PMT_AMT3));

						builder.addText(String.format("ISD @6%% Rs.%0.2f/-\n",
								UtilAppCommon.bill.PMT_AMT1));

					} else if ((UtilAppCommon.bill.BOOKNO2.equals("9999"))
							&& (UtilAppCommon.bill.RECEPT_NO2.equals("1"))) {

						builder.addText(String.format("      %8s      %8s\n",
								UtilAppCommon.bill.BOOKNO1,
								UtilAppCommon.bill.RECEPT_NO1));

						builder.addText(String.format("    %8s    %6s\n",
								UtilAppCommon.bill.PMT_DT1,
								UtilAppCommon.bill.PMT_AMT1));

						builder.addText(String.format("    %8s    %8s\n",
								UtilAppCommon.bill.BOOKNO3,
								UtilAppCommon.bill.RECEPT_NO3));

						builder.addText(String.format("    %8s    %6sf\n",
								UtilAppCommon.bill.PMT_DT3,
								UtilAppCommon.bill.PMT_AMT3));

						builder.addText(String.format("ISD @6%% Rs.%0.2f/-\n",
								UtilAppCommon.bill.PMT_AMT2));

					} else if ((UtilAppCommon.bill.BOOKNO3.equals("9999"))
							&& (UtilAppCommon.bill.RECEPT_NO3.equals("1"))) {
						builder.addText(String.format("    %8s    %8s\n",
								UtilAppCommon.bill.BOOKNO1,
								UtilAppCommon.bill.RECEPT_NO1));

						builder.addText(String.format("    %8s    %6sf\n",
								UtilAppCommon.bill.PMT_DT1,
								UtilAppCommon.bill.PMT_AMT1));

						builder.addText(String.format("    %8s    %8s\n",
								UtilAppCommon.bill.BOOKNO2,
								UtilAppCommon.bill.RECEPT_NO2));

						builder.addText(String.format("    %8s    %6sf\n",
								UtilAppCommon.bill.PMT_DT2,
								UtilAppCommon.bill.PMT_AMT2));

						builder.addText(String.format("ISD @6%% Rs.%0.2f/-\n",
								UtilAppCommon.bill.PMT_AMT3));

					} else {

						builder.addText(String.format(
								"    BKNO: %8s     RCPT: %8s\n",
								UtilAppCommon.bill.BOOKNO1,
								UtilAppCommon.bill.RECEPT_NO1));

						builder.addText(String.format(
								"    DT:  %8s      Amt:%6s\n",
								UtilAppCommon.bill.PMT_DT1,
								UtilAppCommon.bill.PMT_AMT1));

						builder.addText(String.format("    %8s    %8s\n",
								UtilAppCommon.bill.BOOKNO2,
								UtilAppCommon.bill.RECEPT_NO2));

						builder.addText(String.format("    %8s    %6s\n",
								UtilAppCommon.bill.PMT_DT2,
								UtilAppCommon.bill.PMT_AMT2));

						builder.addText(String.format("    %8s    %8s\n",
								UtilAppCommon.bill.BOOKNO3,
								UtilAppCommon.bill.RECEPT_NO3));

						builder.addText(String.format("    %8s    %6s\n",
								UtilAppCommon.bill.PMT_DT3,
								UtilAppCommon.bill.PMT_AMT3));

					}

					builder.addText(String.format("  Arrear Sundry Details"));

					builder.addText(String.format("          %s          %s\n",
							UtilAppCommon.bill.SUND_CODE,
							UtilAppCommon.bill.ARR_SUND_AMT));

					builder.addText("");

					builder.addText(String.format("    Deferred Amt    :%12s\n",
							UtilAppCommon.bill.DEFERRED_AMT));

					builder.addText(String.format("    Arrear Details\n"));

					builder.addText(String.format("    Arr EC      :%12s\n",
							UtilAppCommon.bill.ARR_EC));

					builder.addText(String.format("    Arr ED      :%12s\n",
							UtilAppCommon.bill.ARR_ED));

					builder.addText(String.format("    Arr DPS     :%12s\n",
							UtilAppCommon.bill.ARR_DPS));

					if (UtilAppCommon.bill.SUNDRY_AMT_FLAG == "Q") {
						builder.addText(String.format("Sundry Code 'Q'=RC FEES\n"));

					}

					builder.addCut(Builder.CUT_FEED);

					// <Send print data>

//					printer.openPrinter(Print.DEVTYPE_BLUETOOTH, address,
//							Print.TRUE, Print.PARAM_DEFAULT);
					printer.sendData(builder, 10000, status, battery);

					startActivity(new Intent(getBaseContext(),
							ActvBillingOption.class));

				} catch (Exception e) {
					// Handle communications error here.
					Toast.makeText(getApplicationContext(), e.toString(),
							Toast.LENGTH_LONG).show();

				}

			}
	}

	class AnalogicThermal extends Thread {
		private BluetoothDevice device = null;
		private BluetoothSocket btSocket = null;
		private OutputStream outStream = null;
		private OutputStreamWriter writer = null;

		private FileOutputStream fileStream = null;
		File txtBill;
		private String address;

		public AnalogicThermal(String address) {
			this.address = address;
		}
	
		@SuppressLint("MissingPermission")
        public void run() {
			try {
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				device = mBluetoothAdapter.getRemoteDevice(address);
								
				btSocket =NetworkUtil.createBluetoothSocket(device,UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				mBluetoothAdapter.cancelDiscovery();
				btSocket.connect();	
				Thread.sleep(1000);
				Toast.makeText(getBaseContext(),
					"Connected to " + device.getName(), Toast.LENGTH_SHORT)
					.show();
//				outStream = btSocket.getOutputStream();
//				writer = new OutputStreamWriter(outStream);
			} catch (Exception e) {
				Toast.makeText(
						getBaseContext(),
						"No Bluetooth printer detected..please connect and try again! : ",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(getBaseContext(),
						ActvBillingOption.class));
			}
			
			try
			{
				txtBill=new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/SBDocs/BillText.txt").getAbsolutePath());
				if(txtBill.exists())
				{
					txtBill.delete();
					txtBill.createNewFile();
				}
				else
				{
					txtBill.createNewFile();
				}
				fileStream=new FileOutputStream(txtBill);
			
				outStream = fileStream;				
				writer = new OutputStreamWriter(outStream);
			} catch (Exception e) {
				Toast.makeText(
						getBaseContext(),
						"Error in creating file",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(getBaseContext(),
						ActvBillingOption.class));
			}	

			try {
				Bluetooth_Printer_2inch_ThermalAPI printer = new Bluetooth_Printer_2inch_ThermalAPI();

				
				System.out.println(mBluetoothAdapter.getAddress());

				String txt = "Dkr.";
				Toast.makeText(getBaseContext(), BILL, Toast.LENGTH_SHORT)
						.show();

				char lf = 0x0A;
				char cr = 0x0D;
				writer.write(lf);
				writer.flush();

				// ////////Print On Paper Start////////////
				if (UtilAppCommon.ui.METER_READER_ID.compareTo("1") == 0) {
					writer.write(printer.font_Courier_10(String.format(
							"   %s\n", "NESCO")));
				} else if (UtilAppCommon.ui.METER_READER_ID.compareTo("2") == 0) {
					writer.write(printer.font_Courier_10(String.format(
							"   %s\n", "WESCO")));
				} else if (UtilAppCommon.ui.METER_READER_ID.compareTo("3") == 0) {
					writer.write(printer.font_Courier_10(String.format(" %s\n",
							"SOUTHCO")));
				} else {
					writer.write(printer.font_Courier_10(String.format(
							"   %s\n", "E-BILL")));
				}

				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_19(String.format("  %s\n",
						"DUPLICATE BILL")));
				writer.write(printer.font_Courier_19("----------------\n"));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Division : %s\n", UtilAppCommon.bill.DIV)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Sub Divn : %s\n", UtilAppCommon.bill.SUB_DIV)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Section  : %s\n  \n", UtilAppCommon.bill.SECTION)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_19(String
						.format("New A/C No: \n")));
				writer.write(printer.font_Courier_19(String.format(
						"    %s\n  \n", UtilAppCommon.bill.BILL_ACC_NO)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Old A/C No: %s\n  \n  \n", UtilAppCommon.bill.OLD_ACC_NO)));
				writer.write(cr);
				writer.flush();

				// writer.write(printer.font_Courier_24(String.format("           \n\n"));
				// writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"          %s\n", UtilAppCommon.bill.POLL_NO)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Bill Period: %s\n", UtilAppCommon.bill.BILL_PERIOD)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"No of Mths : %s\n", UtilAppCommon.bill.NO_OF_MONTHS)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Bill Dt    : %s\n", UtilAppCommon.bill.BILLDATE)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String
						.format("Name & Address   :   \n  \n")));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format("%s\n",
						UtilAppCommon.bill.NAME)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format("%s\n",
						UtilAppCommon.bill.ADDR1)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format("%s\n",
						UtilAppCommon.bill.ADDR2)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format("%s\n  \n",
						UtilAppCommon.bill.ADDR3)));
				writer.write(cr);
				writer.flush();

				// writer.write(printer.font_Courier_24(String.format("           \n"));
				// writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Security Dep: %s\n", UtilAppCommon.bill.SEC_DEPOSIT)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Consumer Sts: %s\n", UtilAppCommon.bill.CONS_STATUS)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format("Category:  \n", UtilAppCommon.bill.CATEGORY)));//
				writer.write(printer.font_Courier_24(String.format("MULTIPLYING FACTOR: %s  \n", UtilAppCommon.bill.MF)));//
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Load: %s  PH: %s\n", UtilAppCommon.bill.LOAD,
						UtilAppCommon.bill.PHASE)));
				writer.write(cr);
				writer.flush();

				// if(ActvBilling.gMD == 1)
				// {
				// writer.write(printer.font_Courier_24(String.format("    %5.2fKW      %5d\n",
				// UtilAppCommon.out.MAX_DEMD,UtilAppCommon.in.SDO_CD.charAt(3)!='0'?1:3));
				// writer.flush();
				//
				// }
				// else
				// {
				//
				// writer.write(printer.font_Courier_24(String.format("    %5.2fKW      %5d\n",
				// UtilAppCommon.in.CONN_LOAD,UtilAppCommon.in.SDO_CD.charAt(3)!='0'?1:3));
				// writer.flush();
				//
				// }

				writer.write(printer.font_Courier_24(String
						.format("Reading   Month  Status \n  \n")));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"    %s    %s %s\n",
						UtilAppCommon.bill.PREVIOUS_READING,
						UtilAppCommon.bill.PREVIOUS_MONTH,
						UtilAppCommon.bill.PREVIOUS_STATUS)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"            %s\n", UtilAppCommon.bill.OLD_MTR_FLRDG)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"            %s\n", UtilAppCommon.bill.NEW_MTR_ILRDG)));
				writer.write(cr);
				writer.flush();

				{

					// int
					// mon=Integer.parseInt(UtilAppCommon.out.CUR_RED_DT.substring(3,5));
					// int
					// year=Integer.parseInt(UtilAppCommon.out.CUR_RED_DT.substring(6,8));

					if (UtilAppCommon.bill.CUR_MTR_STATUS.equals("L")
							|| UtilAppCommon.bill.CUR_MTR_STATUS.equals("N")
							|| UtilAppCommon.bill.CUR_MTR_STATUS.equals("O")) {

						writer.write(printer.font_Courier_24(String.format(
								"         %s  %s\n  \n",
								UtilAppCommon.bill.CURMTH,
								UtilAppCommon.bill.CUR_MTR_STATUS)));
						writer.write(cr);
						writer.flush();

					} else {

						writer.write(printer.font_Courier_24(String.format(
								"   %s    %s %s\n  \n  \n  ",
								UtilAppCommon.bill.CURRRDG,
								UtilAppCommon.bill.CURMTH,
								UtilAppCommon.bill.CUR_MTR_STATUS)));
						writer.write(cr);
						writer.flush();
					}

				}

				writer.write(printer.font_Courier_24(String.format(
						"Mtr No    :%12s\n", UtilAppCommon.bill.METER_NO)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Mtr Owner :%s\n", UtilAppCommon.bill.MTR_OWNER)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Bill Basis: %s\n", UtilAppCommon.bill.BILLBASIS)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Bill Units : %s\n  \n", UtilAppCommon.bill.BILL_UNITS)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String
						.format("Bill Slabs :\n")));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format("1:  %s\n",
						UtilAppCommon.bill.SLAB1)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format("2:  %s\n",
						UtilAppCommon.bill.SLAB2)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format("3:  %s\n",
						UtilAppCommon.bill.SLAB3)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format("4:  %s\n",
						UtilAppCommon.bill.SLAB4)));
				writer.write(cr);
				writer.flush();

				// writer.write(printer.font_Courier_24(String.format("  \n"));
				// writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Energy Chg  :%10s\n", UtilAppCommon.bill.EC)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Fix/Dem Chg :%10s\n", UtilAppCommon.bill.FIXCHG)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Meter Rent  :%10s\n", UtilAppCommon.bill.METERRENT)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Elec.Duty   :%10s\n", UtilAppCommon.bill.ED)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"DPS         :%10s\n", UtilAppCommon.bill.DPS)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Pres Bill Amt:%10s\n  \n",
						UtilAppCommon.bill.PRE_BILL_AMT)));
				writer.write(cr);
				writer.flush();
				// float arr_amt =
				// Float.parseFloat(UtilAppCommon.bill.ARREAR_AMT);
				writer.write(printer.font_Courier_24(String.format(
						"Arrears      :%10s\n", UtilAppCommon.bill.ARREAR_AMT)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Sundry Adj   :%10s\n", UtilAppCommon.bill.ADJ_AMT)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Adj.Amount   :%s\n  \n", UtilAppCommon.bill.SUNDRY_AMT)));
				writer.write(cr);
				writer.flush();

				// {
				// long AMT;

				// AMT = Math.round((double) UtilAppCommon.out.NETAFTDUEDT*100);

				// AMT = Math.round((double) UtilAppCommon.bill.NET_AFT_DUEDT);
				writer.write(printer.font_Courier_24(String
						.format("After Due Date\n")));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_19(String.format(
						"#Net Amt:%10s\n", UtilAppCommon.bill.NET_AFT_DUEDT)));
				writer.write(cr);
				writer.flush();

				// }
				writer.write(printer.font_Courier_24(String
						.format("By Due Date\n")));
				writer.write(cr);
				writer.flush();
				writer.write(printer.font_Courier_24(String.format(
						"         %12s\n", UtilAppCommon.bill.REBOFF)));
				writer.write(cr);
				writer.flush();

				// {
				//
				// long AMT = Math.round((double)
				// UtilAppCommon.bill.NET_BEF_DUEDT);

				writer.write(printer.font_Courier_19(String.format(
						"#Net Amt:%10s\n", UtilAppCommon.bill.NET_BEF_DUEDT)));
				writer.write(cr);
				writer.flush();

				// }

				writer.write(printer.font_Courier_24(String.format(
						"Rebate Date    :%s\n", UtilAppCommon.bill.REB_DT)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Due Date       :%s\n", UtilAppCommon.bill.DUE_DT)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String
						.format("Last Payment Details \n  \n")));
				writer.write(cr);
				writer.flush();

				if ((UtilAppCommon.bill.BOOKNO1.equals("9999"))
						&& (UtilAppCommon.bill.RECEPT_NO1.equals("1"))) {
					writer.write(printer.font_Courier_24(String.format(
							"BKNO:%8s Rcpt:%8s\n", UtilAppCommon.bill.BOOKNO2,
							UtilAppCommon.bill.RECEPT_NO2)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"DT:  %8s Amt: %6s\n", UtilAppCommon.bill.PMT_DT2,
							UtilAppCommon.bill.PMT_AMT2)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %8s\n", UtilAppCommon.bill.BOOKNO3,
							UtilAppCommon.bill.RECEPT_NO3)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %6s\n", UtilAppCommon.bill.PMT_DT3,
							UtilAppCommon.bill.PMT_AMT3)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"ISD @6%% Rs.%0.2f/-\n",
							UtilAppCommon.bill.PMT_AMT1)));
					writer.write(cr);
					writer.flush();

				} else if ((UtilAppCommon.bill.BOOKNO2.equals("9999"))
						&& (UtilAppCommon.bill.RECEPT_NO2.equals("1"))) {

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %8s\n", UtilAppCommon.bill.BOOKNO1,
							UtilAppCommon.bill.RECEPT_NO1)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %6s\n", UtilAppCommon.bill.PMT_DT1,
							UtilAppCommon.bill.PMT_AMT1)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %8s\n", UtilAppCommon.bill.BOOKNO3,
							UtilAppCommon.bill.RECEPT_NO3)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %6sf\n", UtilAppCommon.bill.PMT_DT3,
							UtilAppCommon.bill.PMT_AMT3)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"ISD @6%% Rs.%0.2f/-\n  \n",
							UtilAppCommon.bill.PMT_AMT2)));
					writer.write(cr);
					writer.flush();

				} else if ((UtilAppCommon.bill.BOOKNO3.equals("9999"))
						&& (UtilAppCommon.bill.RECEPT_NO3.equals("1"))) {
					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %8s\n", UtilAppCommon.bill.BOOKNO1,
							UtilAppCommon.bill.RECEPT_NO1)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %6sf\n", UtilAppCommon.bill.PMT_DT1,
							UtilAppCommon.bill.PMT_AMT1)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %8s\n", UtilAppCommon.bill.BOOKNO2,
							UtilAppCommon.bill.RECEPT_NO2)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %6sf\n", UtilAppCommon.bill.PMT_DT2,
							UtilAppCommon.bill.PMT_AMT2)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"ISD @6%% Rs.%0.2f/-\n  \n",
							UtilAppCommon.bill.PMT_AMT3)));
					writer.write(cr);
					writer.flush();

				} else {

					writer.write(printer.font_Courier_24(String.format(
							"BKNO: %8s RCPT: %8s\n",
							UtilAppCommon.bill.BOOKNO1,
							UtilAppCommon.bill.RECEPT_NO1)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"DT:  %8s  Amt:%6s\n", UtilAppCommon.bill.PMT_DT1,
							UtilAppCommon.bill.PMT_AMT1)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %8s\n", UtilAppCommon.bill.BOOKNO2,
							UtilAppCommon.bill.RECEPT_NO2)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %6s\n", UtilAppCommon.bill.PMT_DT2,
							UtilAppCommon.bill.PMT_AMT2)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %8s\n", UtilAppCommon.bill.BOOKNO3,
							UtilAppCommon.bill.RECEPT_NO3)));
					writer.write(cr);
					writer.flush();

					writer.write(printer.font_Courier_24(String.format(
							"    %8s    %6s\n", UtilAppCommon.bill.PMT_DT3,
							UtilAppCommon.bill.PMT_AMT3)));
					writer.write(cr);
					writer.flush();

				}

				writer.write(printer.font_Courier_24(String
						.format("Arrear Sundry Details\n")));

				writer.write(printer.font_Courier_24(String.format(
						"      %s        %s\n  \n", UtilAppCommon.bill.SUND_CODE,
						UtilAppCommon.bill.ARR_SUND_AMT)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24("\n  \n \n"));
				writer.write(cr);
				writer.flush();
				writer.write(printer.font_Courier_24(String.format(
						"Deferred Amt  :%12s\n",
						UtilAppCommon.bill.DEFERRED_AMT)));
				writer.write(cr);
				writer.flush();
				writer.write(printer.font_Courier_24(String
						.format("Arrear Details\n")));
				writer.write(cr);
				writer.flush();
				writer.write(printer.font_Courier_24(String.format(
						"Arr EC    :%12s\n", UtilAppCommon.bill.ARR_EC)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Arr ED    :%12s\n", UtilAppCommon.bill.ARR_ED)));
				writer.write(cr);
				writer.flush();

				writer.write(printer.font_Courier_24(String.format(
						"Arr DPS   :%12s\n", UtilAppCommon.bill.ARR_DPS)));
				writer.write(cr);
				writer.flush();

				if (UtilAppCommon.bill.SUNDRY_AMT_FLAG == "Q") {
					writer.write(printer.font_Courier_24(String
							.format("Sundry Code 'Q'=RC FEES")));
					writer.write(printer.font_Courier_24("  \n  \n  \n  \n"));
					writer.write(cr);
					writer.flush();
				} else {
					writer.write(printer.font_Courier_24("  \n  \n  \n  \n  \n"));
					writer.write(cr);
					writer.flush();
				}
				writer.write(printer.font_Courier_24(".  \n"));
				writer.write(cr);
				writer.flush();
				writer.write(printer.font_Courier_24(".  \n"));
				writer.write(cr);
				writer.flush();
				writer.write(printer.font_Courier_24(".  \n"));
				writer.write(cr);
				writer.flush();
				writer.write(printer.font_Courier_24(".  \n"));
				writer.write(cr);
				writer.flush();
				writer.write(printer.font_Courier_24(".  \n"));
				writer.write(cr);
				writer.flush();
				writer.write(printer.font_Courier_24(".  \n"));
				writer.write(cr);
				writer.flush();
				writer.close();
				outStream.close();

				
				
				//12-16-2014
				
				
				StringBuilder text = new StringBuilder();

				try {
					Scanner br = new Scanner(txtBill);
					do {

						String strLine = br.nextLine();
						text.append(strLine);

					} while (br.hasNextLine());

				} catch (IOException e) {
					// You'll need to add proper error handling here
				}

				String msg = text.toString();
				outStream = btSocket.getOutputStream();
				outStream.write(msg.getBytes());
				Thread.sleep(1000);
				outStream.close();
				
				
				// finish();
				startActivity(new Intent(getBaseContext(),
						ActvBillingOption.class));
				// startActivity(new Intent(this, ActvBillingOption.class));

			} catch (IOException e) {
				Toast.makeText(getBaseContext(),
						"exception in sendMsg : " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				try {
					btSocket.close();
				} catch (IOException e2) {
					Toast.makeText(getBaseContext(),
							"exception in closing socket : " + e2.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void closeSocket() {
			try {
				btSocket.close();
			} catch (IOException e2) {
				Toast.makeText(getBaseContext(),
						"exception in closing socket : " + e2.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
