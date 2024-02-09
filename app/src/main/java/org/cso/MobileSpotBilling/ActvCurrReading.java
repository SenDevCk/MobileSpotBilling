package org.cso.MobileSpotBilling;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.cso.MSBAsync.AsyncGetOutputData;
import org.cso.MSBAsync.AsyncImage;
import org.cso.MSBModel.StructSAPInput;
import org.cso.MSBUtil.GPSTracker;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActvCurrReading extends Activity implements OnClickListener,TaskCallback{

    String strMtrStatus;
    Button calculateBillBtn;
    boolean blFlag = true, blDialFlag = false;
    LinearLayout linelayPowFact = null, linelayKVAH = null, linelayMaxDemandKVA=null, linelayKWH=null, linelayMaxDemand=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        System.out.println("**********Inside ActvCurrReading*********** ");
        setContentView(R.layout.currreadinginput);

        linelayPowFact = (LinearLayout) findViewById(R.id.PowFactorLineLayout);
        linelayKVAH = (LinearLayout) findViewById(R.id.CurrentReadingKVAHLineLayout);
        linelayMaxDemandKVA=(LinearLayout)findViewById(R.id.maxDemandKVALayout);
        linelayKWH=(LinearLayout)findViewById(R.id.CurrentReadingKWH);
        linelayMaxDemand=(LinearLayout)findViewById(R.id.LinearLayoutMaxDemand);

        Intent intent = getIntent();
        strMtrStatus = intent.getExtras().getString("MeterStatus");
        if(!intent.getExtras().getString("MeterStatus").equalsIgnoreCase("Ok"))
        {
            ((EditText) findViewById(R.id.CurrentReadingEdit)).setEnabled(false);
            if(intent.getExtras().getString("MeterStatus").equalsIgnoreCase("MD") || intent.getExtras().getString("MeterStatus").equalsIgnoreCase("PL"))
                ((EditText) findViewById(R.id.CurrentReadingEdit)).setText(UtilAppCommon.in.PRV_READING_KWH);
            else
            {
                ((EditText) findViewById(R.id.CurrentReadingEdit)).setText("0");
                ((EditText) findViewById(R.id.MaxDemandEdit)).setEnabled(false);
                ((EditText) findViewById(R.id.MaxDemandEdit)).setText("0.00");
                ((EditText) findViewById(R.id.PowFactorEdit)).setEnabled(false);
                ((EditText) findViewById(R.id.PowFactorEdit)).setText("0.00");
            }
        }
        else
        {
            ((EditText) findViewById(R.id.PowFactorEdit)).setText("0.00");
            if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("NDS-IID(B)") ||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("SS-I")
                     || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IM"))
            {

                ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setEnabled(false);
                ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setText("0");
                ((EditText) findViewById(R.id.PowFactorEdit)).setEnabled(true);
                ((EditText) findViewById(R.id.PowFactorEdit)).setText("0.00");
                linelayKVAH.setVisibility(View.INVISIBLE);
            }
            else if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("HGN") /*||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTEV")*/){
                linelayMaxDemand.setVisibility(View.GONE);
                ((EditText) findViewById(R.id.MaxDemandEdit)).setEnabled(false);
                ((EditText) findViewById(R.id.MaxDemandEdit)).setText("0.00");
                ((EditText) findViewById(R.id.CurrentReadingEdit)).setText("0");
                ((EditText) findViewById(R.id.PowFactorEdit)).setText("0.00");
                ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setEnabled(false);
                ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setText("0");
                linelayKVAH.setVisibility(View.INVISIBLE);

            }
            else if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("DS-III(D)")){
                ((EditText) findViewById(R.id.CurrentReadingEdit)).setText("0");
                ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setEnabled(false);
                ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setText("0");
                ((EditText) findViewById(R.id.PowFactorEdit)).setEnabled(false);
                ((EditText) findViewById(R.id.PowFactorEdit)).setText("0.00");
                linelayKVAH.setVisibility(View.INVISIBLE);
                linelayPowFact.setVisibility(View.INVISIBLE);

            }
            else if(!(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-ID") ||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-IID") ||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("PUBWW") ||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IIM")||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTEV")))  //LTEV added
            {
                /**
                 * Setting Visibility of KVAH to visible hence commenting code below and adding enabled to KVAH*/
                // ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setEnabled(false);

                ((EditText) findViewById(R.id.CurrentReadingEdit)).setText("0");
                ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setEnabled(false);
                ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setText("0");
                ((EditText) findViewById(R.id.PowFactorEdit)).setEnabled(false);
                ((EditText) findViewById(R.id.PowFactorEdit)).setText("0.00");

                linelayKVAH.setVisibility(View.INVISIBLE);
                linelayPowFact.setVisibility(View.INVISIBLE);




                /**
                 * Setting Visibility of KVAH to visible hence commenting code below and adding visibility to KVAH*/
                //linelayKVAH.setVisibility(View.INVISIBLE);



            }
            else if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-ID") ||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-IID") ||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("PUBWW")||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IIM") ||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTEV"))    //LTEV added
            {
                linelayMaxDemand.setVisibility(View.GONE);
                ((EditText) findViewById(R.id.MaxDemandEdit)).setEnabled(false);
                ((EditText) findViewById(R.id.MaxDemandEdit)).setText("0.00");
                ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setEnabled(true);
                ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setText("0");
                ((EditText)findViewById(R.id.MaxDemandKVAEdit)).setText("0.00");
                linelayMaxDemandKVA.setVisibility(View.VISIBLE);
                ((EditText) findViewById(R.id.CurrentReadingEdit)).setText("0");
                ((EditText) findViewById(R.id.PowFactorEdit)).setEnabled(false);
                ((EditText) findViewById(R.id.PowFactorEdit)).setText("0.00");
                linelayKVAH.setVisibility(View.VISIBLE);
                linelayPowFact.setVisibility(View.INVISIBLE);
                //((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setText("0");
            }
            else
            {
                ((EditText) findViewById(R.id.PowFactorEdit)).setEnabled(false);
                ((EditText) findViewById(R.id.PowFactorEdit)).setText("0.00");
                linelayPowFact.setVisibility(View.INVISIBLE);
            }
		   /*else if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-ID") || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-IID") || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("PUBWW"))
		   {
			   ((EditText) findViewById(R.id.PowFactorEdit)).setEnabled(false);
			   ((EditText) findViewById(R.id.PowFactorEdit)).setText("0.00");
			   //((EditText) findViewById(R.id.CurrentReadingEditKVAH)).setText("0");
		   }
		   else*/



    	   /*if(!UtilAppCommon.in.RATE_CATEGORY.endsWith("D"))
    		{
            	((EditText) findViewById(R.id.MaxDemandEdit)).setEnabled(false);
            	((EditText) findViewById(R.id.MaxDemandEdit)).setText("0");
    		}
    		if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("DS-IM")||UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("NDS-IM")||UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("KJ_BPL_RM")||UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("KJ_BPL_U"))
    		{
            	((EditText) findViewById(R.id.PowFactorEdit)).setEnabled(false);
            	((EditText) findViewById(R.id.PowFactorEdit)).setText("0.00");
    		}    	*/
        }

        ((TextView) findViewById(R.id.NameTxt1)).setText(String.format(""+ UtilAppCommon.in.CONSUMER_NAME));
        ((TextView) findViewById(R.id.CurrentStatusTxt)).setText(""+ intent.getExtras().getString("MeterStatus"));
        String prvrdg="";
        prvrdg=String.valueOf(getIntent().getExtras().getDouble("prvrdg"));

        ((TextView) findViewById(R.id.PrevReadingTxt)).setText(""+prvrdg.substring(0,prvrdg.length()-2));
        calculateBillBtn = (Button) findViewById(R.id.CalculateBtn);
        calculateBillBtn.setOnClickListener(this);
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
	   		/*finish();
	 		 startActivity(new Intent(this, ActvivityMain.class));*/
                finish();
                Intent intent = new Intent(this, ActvivityMain.class);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                startActivity(intent);
                break;
        }
        return true;
    }
    public void onBackPressed() {
        // do something on back.
        finish();
        //startActivity(new Intent(this, ActvBillingOption.class));

        return;
    }
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.CalculateBtn:

                Log.e("Current Read", "Started");
                try {
                    double iPowFactor = 0;
                    double iMaxDemand = 0;
                    double iReading = 0;
                    double iPrevRead = 0;
                    double iLastActual = 0;
                    double iPreDecimal = 0;
                    double iMaxDemandKVA = 0;


                    blFlag = true;
                    final Intent intent = getIntent();
                    final String readingKVAH = ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).getText().toString();
                    final String reading = ((EditText) findViewById(R.id.CurrentReadingEdit)).getText().toString();
                    final String demand = ((EditText) findViewById(R.id.MaxDemandEdit)).getText().toString();
                    String powFactor = ((EditText) findViewById(R.id.PowFactorEdit)).getText().toString();
                    final String maxDemandKVA=((EditText) findViewById(R.id.MaxDemandKVAEdit)).getText().toString();

                    NumberFormat formatter = new DecimalFormat("#0.00");
                    iPowFactor = Double.parseDouble(powFactor);
                    iMaxDemand = Double.parseDouble(demand);
                    try {
                        iMaxDemandKVA = Double.parseDouble(maxDemandKVA);
                    }
                    catch (Exception e){
                        iMaxDemandKVA = 0.0;
                    }

                    if(reading.trim().equalsIgnoreCase("") && readingKVAH.trim().equalsIgnoreCase(""))
                    {
                        Toast.makeText(getApplicationContext(),
                                "Please enter value for reading",
                                Toast.LENGTH_LONG).show();
                        //break;
                        blFlag = false;
                        return;
                    }
                    else{
                        if(!reading.trim().equalsIgnoreCase("")){

                        }
                    }

                    if(reading.trim().equalsIgnoreCase("") && readingKVAH.trim().equalsIgnoreCase(""))
                    {
                        Toast.makeText(getApplicationContext(),
                                "Please enter value for reading",
                                Toast.LENGTH_LONG).show();
                        //break;
                        blFlag = false;
                    }
                    else
                    {


                        if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-ID")
                                || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-IID")
                                || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("PUBWW")
                                || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IIM")||
                                UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTEV")){   //LTEV added
                            if(readingKVAH.trim().equalsIgnoreCase(""))
                            {
                                Toast.makeText(getApplicationContext(),
                                        "Please enter value for reading",
                                        Toast.LENGTH_LONG).show();
                                //break;
                                blFlag = false;
                                return;
                            }
                            else{
                                iReading=Double.parseDouble(readingKVAH);
                            }

                            if(Double.parseDouble(reading) > Double.parseDouble(readingKVAH)){
                                Toast.makeText(getApplicationContext(),
                                        "KVAH reading must be greater than equal to KWH",
                                        Toast.LENGTH_LONG).show();
                                //break;
                                blFlag = false;
                                return;
                            }

//                            if(iMaxDemand > 0.0 && iMaxDemandKVA > 0.0){
//                                if(iMaxDemand < iMaxDemandKVA){
//                                    iMaxDemand=iMaxDemandKVA;
//                                }
//                                else{
//                                    Toast.makeText(getApplicationContext(),
//                                            "Enter correct value for Max Demand",
//                                            Toast.LENGTH_LONG).show();
//                                    blFlag=false;
//                                    return;
//                                }
//                            }
//                            else{
//                                Toast.makeText(getApplicationContext(),
//                                        "Please value for Max Demand",
//                                        Toast.LENGTH_LONG).show();
//                                blFlag=false;
//                                return;
//                            }



                            if(iMaxDemandKVA <= 0.0){
                                Toast.makeText(getApplicationContext(),
                                        "Please value for Max Demand",
                                        Toast.LENGTH_LONG).show();
                                blFlag=false;
                                return;
                            }else{
                                iMaxDemand=iMaxDemandKVA;
                            }


                        }
                        else
                            iReading = Double.parseDouble(reading);

                        if(UtilAppCommon.in.PRV_READING_KWH.toString().trim().equalsIgnoreCase("") ||
                                UtilAppCommon.in.PRV_READING_KWH == null){
                            iPrevRead = 0;

                        }
                        else
                        {
                            try {
                                iPrevRead = Double.parseDouble(UtilAppCommon.in.PRV_READING_KWH.toString().trim());
                            } catch (NumberFormatException e) {
                                iPrevRead = 0; // your default value
                                Log.e("Current Start", "UtilAppCommon.in.PRV_READING_KWH -- 12 ==>> " + e.getMessage());
                            }

                        }

                        iLastActual = Double.parseDouble(UtilAppCommon.in.LAST_ACT_READ);
                        iPreDecimal = Double.parseDouble(UtilAppCommon.in.PRE_DECIMAL_MTR);
                    }

                    if(UtilAppCommon.in.PRV_READING_KWH.toString().trim().equalsIgnoreCase("") ||
                            UtilAppCommon.in.PRV_READING_KWH == null)
                        iPrevRead = 0;
				
				/*if(iMaxDemand <= 0)
				{
					Toast.makeText(getApplicationContext(),
							"Please enter value for Max Demand greater than 0.1",
							Toast.LENGTH_LONG).show();
							//break;
					blFlag = false;
				}				
				
				else */
                    if((UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("NDS-IID(B)") ||
                            UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("SS-I")
                           /* || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IIM")*/ ||
                            UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IM")
                            || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("HGN")
                           /* || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTEV")*/)
                    && ((iPowFactor = powerFactorValue(powFactor)) == -1.0 ))
                    {
                        blFlag = false;
                    }

                    else if((UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("NDS-IID(B)") ||
                            UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("SS-I")
                            /*|| UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IIM")*/ ||
                            UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IM"))
                            && (Double.parseDouble(reading) <= 0))
                    {
                        Toast.makeText(getApplicationContext(),
                                "Please enter value for Reading KWH more than 0.",
                                Toast.LENGTH_LONG).show();
                        //break;
                        blFlag = false;
                    }

                    else if((iMaxDemand <= 0 || iMaxDemand > 200) &&
                            !UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("HGN") /*&&
                            !UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTEV")*/)
                    {
                        Toast.makeText(getApplicationContext(),
                                "Please enter Max Demand value more than 0 and less than or equal to 200.",
                                Toast.LENGTH_LONG).show();
                        //break;
                        blFlag = false;
                    }
                    else if(strMtrStatus.equalsIgnoreCase("ok") && reading.length() > iPreDecimal)
                    {
                        Toast.makeText(getApplicationContext(),
                                "Please enter reading upto "+ UtilAppCommon.in.PRE_DECIMAL_MTR + " digits. Please re-enter reading again.",
                                Toast.LENGTH_LONG).show();
                        //break;
                        blFlag = false;
                    }
                    else if((UtilAppCommon.in.PRV_MTR_READING_NOTE.equalsIgnoreCase("ok") && iReading < iPrevRead) ||
                            (UtilAppCommon.in.PRV_MTR_READING_NOTE.equalsIgnoreCase("pl") && iReading < iLastActual))
                    {
                        final AlertDialog ad1 = new AlertDialog.Builder(this)
                                .create();
                        ad1.setTitle("Confirm");
                        ad1.setMessage("Current reading entered is less than previous reading. Want to proceed?");
                        ad1.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                        ad1.dismiss();
                                        if(blFlag)
                                            billing();
                                        //blDialFlag = true;
                                        //billdlg();

                                    }
                                });
                        ad1.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
                                new DialogInterface.OnClickListener() {

                                    //private AdapterView<ListAdapter> meterStatusListView;

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                        ad1.dismiss();
                                        //finish();
                                        //blFlag = false;
                                        //blDialFlag = true;
                                        return;

                                    }
                                });
                        ad1.show();
                    }
                    else
                    {
                        billing();
                    }
                    //if(blFlag)

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("Current Read E", e.getMessage());
                }
                break;
        }
    }

    public double powerFactorValue(String powFactor){
        double iPowFactor = -1.0;
        if(powFactor == null || powFactor.trim().length() == 0){
            Toast.makeText(getApplicationContext(),
                    "Please enter value for Power Factor",
                    Toast.LENGTH_LONG).show();
            //break;
            blFlag = false;
        }
        else{
            int indexOfDecimal = powFactor.indexOf(".");
            if (indexOfDecimal >= 0) {
                if ((powFactor.length() - (indexOfDecimal + 1)) > 2) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter value for Power Factor upto 2 decimal places only",
                            Toast.LENGTH_LONG).show();
                    //break;
                    blFlag = false;
                } else {
                    if(powFactor.indexOf(".") == 0){
                        powFactor="0"+powFactor;
                    }
                    iPowFactor = Double.parseDouble(powFactor);
                    if (iPowFactor >= 1 || iPowFactor <= 0) {
                        iPowFactor = -1.0;
                        Toast.makeText(getApplicationContext(),
                                "Please enter value for Power Factor between 0 and 1",
                                Toast.LENGTH_LONG).show();
                        //break;
                        blFlag = false;
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Please enter value for Power Factor between 0 and 1",
                        Toast.LENGTH_LONG).show();
                //break;
                blFlag = false;
            }

        }
        return iPowFactor;
    }

    public void billing()
    {
        try

        {
            final Intent intent = getIntent();
            double iPowFactor = 0;
            final String reading = ((EditText) findViewById(R.id.CurrentReadingEdit)).getText().toString();
            final String readingKVAH = ((EditText) findViewById(R.id.CurrentReadingEditKVAH)).getText().toString();
            String demand = ((EditText) findViewById(R.id.MaxDemandEdit)).getText().toString();
            String demandKVA = ((EditText) findViewById(R.id.MaxDemandKVAEdit)).getText().toString();
            final String powFactor = ((EditText) findViewById(R.id.PowFactorEdit)).getText().toString();

            if(UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-ID")
                    || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTIS-IID")
                    || UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("PUBWW")
                    ||UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("IAS-IIM")||
                    UtilAppCommon.in.RATE_CATEGORY.equalsIgnoreCase("LTEV")){
                demand=demandKVA;
            }
            calculateBillBtn.setEnabled(false);

            String strlocation = showSettingsAlert();
            NumberFormat formatter = new DecimalFormat("#0.00");
            iPowFactor = Double.parseDouble(powFactor);

            String[] copySAPInputData = new String[14];
            String nxtDate;
            try {
                UtilAppCommon.inSAPSendMsg = "1";
                Log.e("Pow Factor ==>> ", copySAPInputData[6] + "  <==>  " + powFactor);

                Calendar today = Calendar.getInstance();
                SimpleDateFormat SAPInformat = new SimpleDateFormat("dd.MM.yyyy");

                Calendar nxtcal = new GregorianCalendar(
                        Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4)),
                        Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7)),
                        Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10)) - 1);

                int temp = Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7));
                String strtemp = "";
                if(temp <= 9)
                    strtemp = "0" + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7));
                else
                    strtemp = ""  + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7));

                String strtemp1 = "";

                int temp1 = Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
                if(temp1 <= 9)
                    strtemp1 = "0" + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
                else
                    strtemp1 = ""  + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));

                nxtDate = strtemp1 + "." + strtemp
                        + "." + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4));



                copySAPInputData[0] = UtilAppCommon.in.CONTRACT_AC_NO;				//cursor.getString(0);	CANumber
                copySAPInputData[1] = UtilAppCommon.in.INSTALLATION;				//cursor.getString(1); 	Installation
                copySAPInputData[2] = strlocation.split("¥")[0];					//cursor.getString(8);	Latitude
                copySAPInputData[3] = strlocation.split("¥")[1];					//cursor.getString(9);	Longitude
                copySAPInputData[4] = reading;										//cursor.getString(5);	CurrentReadingKwh
                copySAPInputData[5] = demand;										//cursor.getString(7);	MaxDemd
                copySAPInputData[6] = formatter.format(iPowFactor);					//cursor.getString(8);	PowerFactor
                copySAPInputData[7] = strMtrStatus;									//cursor.getString(4);	MtrReadingNote
                copySAPInputData[8] = SAPInformat.format(today.getTime()) ;			//cursor.getString(3);	MtrReadingDate
                copySAPInputData[9] = nxtDate;										//cursor.getString(2);	SCHEDULED_BILLING_DATE
                copySAPInputData[10] = UtilAppCommon.in.SAP_DEVICE_NO;				//cursor.getString(12);	SAP_DEVICE_NO
                copySAPInputData[11] = "1";											//"2"					ProcessedFlag
                copySAPInputData[12] = "0";											//"0"
                copySAPInputData[13] = readingKVAH;									//cursor.getString(6);	CurrentReadingKVAH

                Log.e("SCHEDULED_BILLING_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(5, 7) + "-" + UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(8, 10));
                Log.e("SCHEDULED_BILLING_DATE", UtilAppCommon.in.SCHEDULED_BILLING_DATE + " <==> " + nxtDate);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                Log.e("Case 5 Process E", e1.getMessage());
            }

            UtilDB utilDB = new UtilDB(getApplicationContext());
            try {

                //utilDB.insertIntoSAPInput(copySAPInputData);
                utilDB.UpdateIntoSAPInput(copySAPInputData);
                UtilAppCommon.SAPIn = new StructSAPInput();
                UtilAppCommon.copySAPInputData(copySAPInputData);

                //AsyncGetOutputData asyncGetOutputData = new AsyncGetOutputData(this);
                // 20.11.15
                AsyncGetOutputData asyncGetOutputData = new AsyncGetOutputData(this,new OnBillGenerate() {

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
                utilDB.copyToOutputStruct(UtilAppCommon.SAPIn.CANumber);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("ActBill copyOut E", e.getMessage());
            }
								
/*					intent.putExtra("Latitude", strlocation.split("¥")[0]);
		    intent.putExtra("Longitude",strlocation.split("¥)[1]);
			intent.putExtra("currentReading", reading);
			intent.putExtra("maxDemand", demand);
			intent.putExtra("powerFactor", powFactor);
			intent.putExtra("MeterStatus", strMtrStatus);*/
            setResult(RESULT_OK, intent);
            //Log.e("Current Read", "Completed");
            //finish();

        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Curr Read Bill E", e.getMessage());
        }
    }

    public String showSettingsAlert(){
        GPSTracker gps  = new GPSTracker(ActvCurrReading.this);

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
    // 20.11.15
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
    //20.11.15
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
                Log.e("Write2SbmOut - Count", "Valid - " + cnt );
                util1.getOutputBillRecord(UtilAppCommon.acctNbr);
                startActivity(new Intent(this, ActvBillPrinting.class)); // used to print bill through printer
            }
            else
            {
                startActivity(new Intent(this, ActvMsgPrinting.class)); // used to print bill through printer
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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("Write2SbmOut E", e.getMessage());
        }
        Log.e("Write2SbmOut", "Completed");
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

    public void done()
    {
        finish();
//		startActivity(new Intent(this, ActvivityMain.class));
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
