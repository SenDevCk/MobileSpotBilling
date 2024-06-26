package org.cso.MobileSpotBilling;


import org.cso.MSBAsync.AsyncUpdatePoleMobile;
import org.cso.MSBUtil.CryptographyUtil;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SeqSyncActivity extends AppCompatActivity implements OnClickListener {

    int itmpCounter = 0;
    int iInitCounter = 0;
    int iMaxCounter = 0;
    Cursor cursor = null;
    int seqNo = 1, maxCount = 0;

    Button btnProceed;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seq_sync);

        toolbar = findViewById(R.id.toolbar_syncbyseq);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
        toolbar.setTitle("Sync By Sequence");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
            UtilDB util = new UtilDB(getBaseContext());
            UtilAppCommon.sdoCode = util.getSdoCode();
            UtilAppCommon.binder = util.getActiveMRU();

            ((TextView) findViewById(R.id.subDivisionTxt)).setText(UtilAppCommon.sdoCode);
            ((TextView) findViewById(R.id.binderTxt)).setText(UtilAppCommon.binder);

            //GPSLocation gps = new GPSLocation();
            //Log.e("GPS ==>> ", gps.getLocation(getApplicationContext()));

            Button btnPrevious = (Button) findViewById(R.id.btnPrevious);
            Button btnNext = (Button) findViewById(R.id.btnNext);
            btnProceed = (Button) findViewById(R.id.btnProceed);

            btnPrevious.setOnClickListener(this);
            btnNext.setOnClickListener(this);
            btnProceed.setOnClickListener(this);

            UtilAppCommon.billType = "";

            UtilAppCommon.inSAPSendMsg = "";
            UtilAppCommon.inSAPMsgID = "";
            UtilAppCommon.inSAPMsg = "";

            cursor = util.getBilledRouteSequence("");
            itmpCounter = 1;
            iInitCounter = itmpCounter;
            iMaxCounter = cursor.getCount();
            cursor.moveToFirst();
            ((TextView) findViewById(R.id.SeqNbrText)).setText(itmpCounter + " / " + iMaxCounter);
            ((TextView) findViewById(R.id.AcctNbrText)).setText(cursor.getString(0));
            UtilAppCommon.acctNbr = cursor.getString(0);

            btnPrevious.setEnabled(false);

            if (itmpCounter == iMaxCounter)
                ((Button) findViewById(R.id.btnNext)).setEnabled(false);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Sequence Create ", e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "SeqSyncActivity");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart", "SeqSyncActivity");
    }

    @Override
    public boolean onSupportNavigateUp() {
        //  closePrinter();
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        String EncData = "";
        String ActualData = "";
        String CheckFieldData = "";

        UtilDB util = new UtilDB(getBaseContext());
        maxCount = cursor.getCount();

        switch (v.getId()) {
            case R.id.btnPrevious:
                try {
                    btnProceed.setEnabled(true);
                    seqNo--;
                    cursor.moveToPrevious();

                    ((TextView) findViewById(R.id.SeqNbrText)).setText(seqNo + " / " + maxCount);
                    ((TextView) findViewById(R.id.AcctNbrText)).setText(cursor.getString(0));
                    UtilAppCommon.acctNbr = cursor.getString(0);

                    if (seqNo <= 1)
                        ((Button) findViewById(R.id.btnPrevious)).setEnabled(false);
                    else
                        ((Button) findViewById(R.id.btnPrevious)).setEnabled(true);

                    if (seqNo == maxCount)
                        ((Button) findViewById(R.id.btnNext)).setEnabled(false);
                    else
                        ((Button) findViewById(R.id.btnNext)).setEnabled(true);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    Log.e("btnPrevious E ==>> ", e1.getMessage());
                    e1.printStackTrace();
                }
                break;

            case R.id.btnNext:
                btnProceed.setEnabled(true);
                try {
                    seqNo++;
                    cursor.moveToNext();

                    ((TextView) findViewById(R.id.SeqNbrText)).setText(seqNo + " / " + maxCount);
                    ((TextView) findViewById(R.id.AcctNbrText)).setText(cursor.getString(0));
                    UtilAppCommon.acctNbr = cursor.getString(0);

                    if (seqNo <= 1)
                        ((Button) findViewById(R.id.btnPrevious)).setEnabled(false);
                    else
                        ((Button) findViewById(R.id.btnPrevious)).setEnabled(true);

                    if (seqNo == maxCount)
                        ((Button) findViewById(R.id.btnNext)).setEnabled(false);
                    else
                        ((Button) findViewById(R.id.btnNext)).setEnabled(true);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("btnNext E ==>> ", e.getMessage());
                    e.printStackTrace();
                }

                break;

            case R.id.btnProceed:
                //Get the Proceed to Consumer Processing
                //To Assign User info to the struct variable
                Log.e("btnProceed ==>> ", "Started");
                btnProceed.setEnabled(false);
                UtilAppCommon.inSAPMsgID = "";
                UtilAppCommon.inSAPMsg = "";
                util.getUserInfo();
                if (!UtilAppCommon.acctNbr.isEmpty()) {
                    EncData = CryptographyUtil.Encrypt(util.GetConsumerInfoByField(UtilAppCommon.acctNbr, "Accno", "CONSUMER_NAME"));
                    ActualData = CryptographyUtil.Decrypt(EncData);
                    CheckFieldData = util.GetConsumerInfoByField(UtilAppCommon.acctNbr, "Accno", "CONSUMER_NAME");
                    // Toast.makeText(getBaseContext(), "Security key :"+ActualData+ "\n CheckFieldData: "+CheckFieldData, Toast.LENGTH_LONG).show();
                }

                if (UtilAppCommon.acctNbr.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter value for legacy number field", Toast.LENGTH_LONG).show();
                } else if (!util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number")) {
                    Toast.makeText(getBaseContext(), "Selected CA Number is not valid", Toast.LENGTH_LONG).show();
                } else if (ActualData.compareTo(CheckFieldData) != 0) {
                    Toast.makeText(getBaseContext(), "Checksum key did not match!", Toast.LENGTH_LONG).show();
                } else {
                    UtilAppCommon.bBtnGenerateClicked = true;
                    Intent intent = new Intent(this, ActvBilling.class);
                    startActivity(intent);
                    //finish();
                    //UtilAppCommon.bBtnGenerateClicked = true;
                    //System.out.println("sudhir BTngenerated clicked on Entry form"+UtilAppCommon.bBtnGenerateClicked);
                    //intent.putExtra("BillingType", "Legacy");

                }
                Log.e("btnProceed ==>> ", "Completed");
                break;
        }

    }

}
