package org.cso.MobileSpotBilling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.cso.MSBAsync.AsyncGetOutputData;
import org.cso.MSBAsync.AsyncImage;
import org.cso.MSBModel.StructSAPInput;
import org.cso.MSBUtil.GPSTracker;
import org.cso.MSBUtil.NetworkUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class IntermediateActivity extends AppCompatActivity {
    Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intermediate);
				toolbar = findViewById(R.id.toolbar_intrmediate);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle(getResources().getString(R.string.app_name));
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		performOperation();
	}

	@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}


	public void performOperation()
	{

		UtilDB util = new UtilDB(getBaseContext());
		util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number");
		util.close();

		String strlocation = showSettingsAlert();
		String[] copySAPInputData = new String[14];
		String nxtDate;
		try {
			copySAPInputData[0] = UtilAppCommon.in.CONTRACT_AC_NO;
			copySAPInputData[1] = UtilAppCommon.in.INSTALLATION;
			copySAPInputData[2] = strlocation.split("¥")[0];
			copySAPInputData[3] = strlocation.split("¥")[1];
			copySAPInputData[4] = UtilAppCommon.in.PRV_READING_KWH;
			copySAPInputData[5] = "0";
			copySAPInputData[6] = "0.00";
			copySAPInputData[7] = UtilAppCommon.in.PRV_MTR_READING_NOTE;
			copySAPInputData[8] = "";
			copySAPInputData[9] = UtilAppCommon.in.SCHEDULED_BILLING_DATE;
			copySAPInputData[10] = UtilAppCommon.in.SAP_DEVICE_NO;
			copySAPInputData[13] = "0";

			Calendar today = Calendar.getInstance();
			SimpleDateFormat SAPInformat = new SimpleDateFormat("dd.MM.yyyy");
			copySAPInputData[8] = SAPInformat.format(today.getTime()) ;
			
			Calendar nxtcal = new GregorianCalendar(
					Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4)),
					Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7)),
					Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10)) - 1);
			
			int temp = Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7));
			String strtemp = "";
			String strtemp1 = "";
			if(temp <= 9)
				strtemp = "0" + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7));
			else
				strtemp = ""  + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7));
			
			temp = Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
			if(temp <= 9)
				strtemp1 = "0" + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
			else
				strtemp1 = ""  + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
									
			nxtDate = strtemp1 + "." + strtemp
					+ "." + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4));
			copySAPInputData[9] = nxtDate;
					
			//if(cursor.getString(cursor.getColumnIndex("MESSAGE10")).contains("NETWORK ISSUE"))
				copySAPInputData[11] = "1";
				UtilAppCommon.inSAPSendMsg = "1";
			//else
				//copySAPInputData[11] = "2";
				copySAPInputData[12] = "0";

			Log.e("SCHEDULED_BILLING_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
		} catch (Exception e1) {
			Toast.makeText(IntermediateActivity.this, "Step13 Completed", Toast.LENGTH_SHORT).show();
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e("MD Case", e1.getMessage());
		}
		Toast.makeText(IntermediateActivity.this, "Step14 Completed", Toast.LENGTH_SHORT).show();
		UtilDB utilDB = new UtilDB(getApplicationContext());
		try {					
			//utilDB.insertIntoSAPInput(copySAPInputData);
			utilDB.UpdateIntoSAPInput(copySAPInputData);
			UtilAppCommon.SAPIn = new StructSAPInput();
			UtilAppCommon.copySAPInputData(copySAPInputData);
			Toast.makeText(IntermediateActivity.this, "Step15 Completed", Toast.LENGTH_SHORT).show();
				//AsyncGetOutputData asyncGetOutputData = new AsyncGetOutputData(this); 20.11.15
			    AsyncGetOutputData asyncGetOutputData = new AsyncGetOutputData(this ,new OnBillGenerate() {
				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					printdlg();
					
				}
			});
				asyncGetOutputData.execute(copySAPInputData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("AsyncOut ActBill", e.getMessage());
		}
			
		try {
			if(NetworkUtil.isOnline(IntermediateActivity.this,null))
			{
				utilDB.copyToOutputStruct(UtilAppCommon.SAPIn.CANumber);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("ActBill copyOut E", e.getMessage());
		}
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("InterruptedException E", e.getMessage());
		}
		
		Intent intent = getIntent();
		setResult(RESULT_OK, intent);
		
	}

	// Added on 20.11.15
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
			 //printbill();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("Write2SbmOut E", e.getMessage());
		}
		Log.e("Write2SbmOut", "Completed");
	}

	// Added 20.11.15
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
							startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
							/*if(UtilAppCommon.billType.equalsIgnoreCase("A"))
								startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
							else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
								startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
							else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
								startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
							else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
								startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
							else
								startActivity(new Intent(getBaseContext(), ActvBillingOption.class));*/
						}
					});
			ad.show();
		}
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
	
	public void getImageByCANo(String CANo)
	{
		try {
			String AppDir = "";
			
			Log.e("getImageByCANo", "Started");
			UtilDB utildb = new UtilDB(getApplicationContext());
			/*AppDir = Environment.getExternalStorageDirectory().getPath()
					+ "/SBDocs/Photos_Crop" + "/" + utildb.getSdoCode() + "/"
					+ utildb.getActiveMRU();*/
			AppDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES+ "/SBDocs/Photos_Crop" + "/" + utildb.getSdoCode() + "/"
					+ utildb.getActiveMRU()).getPath();
			Cursor cursorImage = utildb.getUnCompressedImage(CANo);
			File file = null;
			//getUnCompressedImage
			if(cursorImage != null)
			{
				cursorImage.moveToFirst();
				file = new File(AppDir , cursorImage.getString(1));
			}
			//ImageProcessing imageProcessing = new ImageProcessing();
			
			AsyncImage asyncImage = new AsyncImage(this , () -> {
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
			
			if(credentials != null)
				asyncImage.execute(credentials);
			
			Log.e("getImageByCANo", "Completed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("getImageByCANo E", e.getMessage());
		}
	}
	
    public String showSettingsAlert(){
    	GPSTracker gps  = new GPSTracker(IntermediateActivity.this);
    	
    	if(gps.canGetLocation()){
            
            String latitude = String.valueOf(gps.getLatitude());
            String longitude = String.valueOf(gps.getLongitude());
 
            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            return String.valueOf(gps.getLatitude()) + "¥" + String.valueOf(gps.getLongitude());
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
            
            String latitude = String.valueOf(gps.getLatitude());
            String longitude = String.valueOf(gps.getLongitude());
            return String.valueOf(gps.getLatitude()) + "¥" + String.valueOf(gps.getLongitude());
        }
    }
	
}
