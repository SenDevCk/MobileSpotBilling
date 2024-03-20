package org.cso.MobileSpotBilling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.cso.MSBUtil.CryptographyUtil;
import org.cso.MSBUtil.UtilAppCommon;

import org.cso.MSBUtil.UtilDB;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActvConsumerNbrInputForDuplicateBill extends AppCompatActivity implements OnClickListener {
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumernbrinputforduplicatebill);
        toolbar = findViewById(R.id.toolbar_dblctbillprint);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
        toolbar.setTitle("Duplicate Bill Printing");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        UtilAppCommon.bprintdupl = false;
        UtilDB util = new UtilDB(getBaseContext());
        UtilAppCommon.sdoCode = util.getSdoCode();
        UtilAppCommon.binder = util.getActiveMRU();


        ((TextView) findViewById(R.id.subDivisionTxt)).setText(UtilAppCommon.sdoCode);
        ((TextView) findViewById(R.id.binderTxt)).setText(UtilAppCommon.binder);


        Button submitBtn =  findViewById(R.id.btnGenerateDupl);
        submitBtn.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        //  closePrinter();
        onBackPressed();
        return true;
    }

    @SuppressLint("SuspiciousIndentation")
    public void onBackPressed() {
        // do something on back.
        //finish();
        super.onBackPressed();
        if (UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1"))
            startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
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

    public void onClick(View v) {
        TextView errTxt = (TextView) findViewById(R.id.nullBinderSubdivErrLbl);
        switch (v.getId()) {
            case R.id.btnGenerateDupl:


                UtilAppCommon.inSAPMsgID = "";
                UtilAppCommon.inSAPMsg = "";

                String EncData = "";
                String ActualData = "";
                String CheckFieldData = "";
                //To Assign User info to the struct variable

                UtilDB util = new UtilDB(getBaseContext());
                util.getUserInfo();
                //End


                UtilAppCommon.acctNbr = ((EditText) findViewById(R.id.AcctNbrEditDupl)).getText().toString();

                //Log.e("UtilAppCommon.acctNbr:::::", UtilAppCommon.acctNbr);
			/*			
			
			String CheckFieldData=util.GetConsumerInfoByField( UtilAppCommon.sdoCode, UtilAppCommon.binder,UtilAppCommon.acctNbr, "name");*/
                // Modified  by Kishore
                if (!UtilAppCommon.acctNbr.isEmpty()) {

                    EncData = CryptographyUtil.Encrypt(util.GetConsumerInfoByField(UtilAppCommon.acctNbr, "Accno", "CONSUMER_NAME"));

                    ActualData = CryptographyUtil.Decrypt(EncData);
                    //Log.e("Consumer Name", ActualData);

                    CheckFieldData = util.GetConsumerInfoByField(UtilAppCommon.acctNbr, "Accno", "CONSUMER_NAME");
                }
                UtilDB utilDB = new UtilDB(this);
                int outputCnt = utilDB.getBillOutputRowCount(UtilAppCommon.acctNbr);
                if (UtilAppCommon.acctNbr.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter value for account number field", Toast.LENGTH_LONG).show();
                } else if (!util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number")) {
                    Toast.makeText(getBaseContext(), "Please enter a valid account number", Toast.LENGTH_LONG).show();
                } else if (ActualData.compareTo(CheckFieldData) != 0) {
                    Toast.makeText(getBaseContext(), "Checksum key did not match!", Toast.LENGTH_LONG).show();
                }
			/*else if(outputCnt > 0)
				{
					UtilAppCommon.bBtnGenerateClicked = true;
					System.out.println("sudhir BTngenerated clicked on Entry form"+UtilAppCommon.bBtnGenerateClicked);
					//startActivity(new Intent(this, ActvBilling.class));

					utilDB.getBillInputDetails(UtilAppCommon.acctNbr,"CA Number");
					utilDB.getOutputBillRecord(UtilAppCommon.acctNbr);
					utilDB.getSAPInput(UtilAppCommon.acctNbr);
					utilDB.getSAPBlueInput(UtilAppCommon.acctNbr);
					startActivity(new Intent(this, ActvBillPrinting.class)); // used to print bill through printer
					finish();
				}
				else if(utilDB.checkDoubleinSAPInput(UtilAppCommon.acctNbr) != null )
				{
					UtilAppCommon.bBtnGenerateClicked = true;
					System.out.println("sudhir BTngenerated clicked on Entry form"+UtilAppCommon.bBtnGenerateClicked);
					startActivity(new Intent(this, ActvBilling.class));
				}
				else{
					Toast.makeText(getBaseContext(), "Consumer not billed yet!", Toast.LENGTH_LONG).show();
				}*/
                else {
                    UtilAppCommon.bBtnGenerateClicked = true;
                    System.err.println("sudhir BTngenerated clicked on Entry form" + UtilAppCommon.bBtnGenerateClicked);
                    startActivity(new Intent(this, ActvBilling.class));
                }
                break;
        }

    }

    private void printbill() {
        // TODO Auto-generated method stub
        Document document = new Document();
        String dirpath = Environment.getExternalStorageDirectory().getPath()
                + "/SBDocs/Pdf";
        File dir = new File(dirpath);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("folder created");
        }
        String filePath = dir + "/" + UtilAppCommon.out.CANumber + ".pdf";

        File file = new File(filePath);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            createTable1(document);
            document.close();
            // ADDED BY DKR
            startActivity(new Intent(this, ActvConsumerNbrInput.class));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
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
            table.addCell(UtilAppCommon.out.Company + "CL");
            table.addCell("BILL FOR");
            table.addCell(UtilAppCommon.out.BillMonth);
            table.addCell("CONSUMER DETAILS");
            table.addCell("");

            table.addCell("BILL NO");
            table.addCell(UtilAppCommon.out.BillNo);

            table.addCell("DIVISION");
            table.addCell(UtilAppCommon.out.Division);
            table.addCell("Sub Div");
            table.addCell(UtilAppCommon.out.SubDivision);

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
            table.addCell(UtilAppCommon.out.MtrNo + " " + UtilAppCommon.out.Phase);
            table.addCell("MTR COMP");
            table.addCell(UtilAppCommon.out.MtrMake
                    .equals("C") ? "Company" : UtilAppCommon.out.MtrMake
                    .equals("S") ? "Consumer" : "");

            table.addCell("CATEGORY");
            table.addCell(UtilAppCommon.out.Category);
            // table.addCell("ADDR1");
            table.addCell("SL  CL  CD");
            table.addCell(UtilAppCommon.out.SanctLoad + " " + UtilAppCommon.out.ConnectedLoad + " " + UtilAppCommon.out.CD);
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
            table.addCell(UtilAppCommon.out.PrevusMtrRdgDt + " " + UtilAppCommon.out.PreviusMtrReadingNote);
            table.addCell(UtilAppCommon.out.CurrentReading);
            table.addCell(UtilAppCommon.out.CurrentMtrRdgDt + " " + UtilAppCommon.out.CurrentMtrReadingNote);


            PdfPTable table2 = new PdfPTable(2);
            table2.addCell("MF :");
            table2.addCell(UtilAppCommon.out.MF);

            table2.addCell("CONSUMPTION:");
            table2.addCell(UtilAppCommon.out.Consumption);


            table2.addCell("RECD DEMD:   PF:");
            table2.addCell(UtilAppCommon.out.RecordedDemd + "  " + UtilAppCommon.out.PowerFactor);

            PdfPTable table3 = new PdfPTable(2);
            String mmcunits, avg;
            if (UtilAppCommon.out.Category.equals("DS-II") || UtilAppCommon.out.Category.equals("NDS-I")) {
                mmcunits = "0";
            } else {
                mmcunits = UtilAppCommon.out.MMCUnits;
            }
            if (UtilAppCommon.out.Type.equalsIgnoreCase("ACTUAL")) {
                avg = "";
            } else {
                avg = UtilAppCommon.out.Average;
            }

            table3.addCell("MMC UNITS:  AVG:");
            table3.addCell(mmcunits + "  " + avg);

            table3.addCell("BLD UNITS: TYPE :");
            table3.addCell(UtilAppCommon.out.BilledUnits + " " + UtilAppCommon.out.Type);

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
            table3.addCell(UtilAppCommon.out.AmtPayableUptoDt + ":" + UtilAppCommon.out.AmtPayableUptoAmt);
            table3.addCell("BY");
            table3.addCell(UtilAppCommon.out.AmtPayablePYDt + ":" + UtilAppCommon.out.AmtPayablePYAmt);
            table3.addCell("AFTER");
            table3.addCell(UtilAppCommon.out.AmtPayableAfterDt + ":" + UtilAppCommon.out.AmtPayableAfterAmt);

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


}
