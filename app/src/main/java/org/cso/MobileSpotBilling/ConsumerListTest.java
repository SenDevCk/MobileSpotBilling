package org.cso.MobileSpotBilling;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

import org.cso.MSBAsync.AsyncGetOutputData;
import org.cso.MSBModel.StructSAPInput;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ConsumerListTest extends AppCompatActivity {
    TextView tvConsumerListTestNOR;
    EditText tveditTextSearch;
    ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tempMyList = new ArrayList<HashMap<String, String>>();
    Context context = this;
    ListView list;
    Toolbar toolbar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cons_list);
        toolbar = findViewById(R.id.toolbar_conlist);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
        toolbar.setTitle("Abnormality Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        String bellowMonth = "1";
        System.out.println("bellow mnth is ::::::   " + bellowMonth);
        UtilDB util = new UtilDB(getBaseContext());
        tvConsumerListTestNOR = (TextView) findViewById(R.id.tvConsumerListTestNOR);
        tvConsumerListTestNOR.setVisibility(View.GONE);
       // tvConsumerListHeader = (TextView) findViewById(R.id.tvConsumerListHeader);
        tveditTextSearch = (EditText) findViewById(R.id.tveditTextSearch);
        list = (ListView) findViewById(R.id.lvConsumerListTestConList);
        String getView = getIntent().getExtras().getString("View");


        if (getView.compareTo("NewConsumerList") == 0) {
            mylist = util.getNewConsumerList(bellowMonth);
            toolbar.setTitle("New Consumer List");

        }
        if (getView.compareTo("OutSortConsumerList") == 0) {
            mylist = util.getOutSortConsumerList(bellowMonth);
            toolbar.setTitle("OutSort Consumer List");

        }
        if (getView.compareTo("NetworkFailureList") == 0) {
            mylist = util.getNetworkFailureList(bellowMonth);
            toolbar.setTitle("Network Failure List");

        }

        if (getView.compareTo("UnbilledList") == 0)
        //if(getView.compareTo("UnBilledConsumerList")==0)
        {

            String SelectedBINDER = getIntent().getExtras().getString("BINDER");
            mylist = util.getUnbilledConsList(SelectedBINDER);
            toolbar.setTitle("Unbilled Consumer List");

        }


        util.close();

        populateList();
        setListAdapter(tempMyList);
        tveditTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                populateTempList();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        //  closePrinter();
        onBackPressed();
        return true;
    }


    public void populateList() {
        if (mylist == null || mylist.size() == 0) {
            return;
        }

        for (int i = 0; i < mylist.size(); i++) {
            tempMyList.add(mylist.get(i));
        }
    }

    public void populateTempList() {
        ArrayList<HashMap<String, String>> listToShow = new ArrayList<>();
        String search = tveditTextSearch.getText().toString().trim();
        if (tveditTextSearch.getText().toString().trim().length() == 0) {
            setListAdapter(tempMyList);
        } else {
            for (HashMap<String, String> hashMap : tempMyList) {
                if (hashMap == null || hashMap.size() == 0) {
                    continue;
                }
                for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                    if (matchIgnoreCase(entry.getValue(), search)) {
                        listToShow.add(hashMap);
                        break;
                    }
                }
            }
            setListAdapter(listToShow);
        }
    }

    public boolean matchIgnoreCase(String containerString, String containedString) {
        if (containerString == null) {
            return false;
        }
        return containerString.toLowerCase().contains(containedString.toLowerCase());
    }

    public void setListAdapter(final ArrayList<HashMap<String, String>> listToShow) {
        SimpleAdapter adapter = new InteractiveArrayAdapter(this, R.layout.newcons_listitems, listToShow,
                new String[]{"ACC_NO", "NAME", "ADDR", "METER_MANUFACTURER_SR_NO"},
                //new int[] {R.id.newConsBinder,R.id.newConsAccNo, R.id.newConsName, R.id.newConsAddr});
                new int[]{R.id.newConsAccNo, R.id.newConsName, R.id.newConsAddr, R.id.newConsMeterNo});
        //tvConsumerListTestNOR.setText("Number of record(s) found :    " + listToShow.size());
          toolbar.setSubtitle("Records : "+listToShow.size());
        //LayoutInflater inflater = getLayoutInflater();

        // final View header = inflater.inflate(R.layout.newcons_header, (ViewGroup) (list),false);
        //list.addHeaderView(header, null, false);
        list.setAdapter(adapter);


        list.setOnItemClickListener((parent, view, position, id) -> {

            // HashMap<String, String> map=listToShow.get(position-1);
            HashMap<String, String> map = listToShow.get(position);

            Toast.makeText(getApplicationContext(), map.get("ACC_NO").toString(), Toast.LENGTH_SHORT).show();
            //UtilAppCommon.acctNbr= map.get("ACC_NO").toString();
            UtilAppCommon.acctNbr = map.get("ACC_NO").trim();
            gotoBilling();
            // billing();
        });
    }

    void gotoBilling() {
        String getView = getIntent().getExtras().getString("View");
        UtilDB util = new UtilDB(getBaseContext());
        if (UtilAppCommon.acctNbr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please Enter value for Account Number", Toast.LENGTH_LONG).show();
        } else if (!util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number")) {

            Toast.makeText(getBaseContext(), "Please Enter Valid Account Number", Toast.LENGTH_LONG).show();
        } else if (getView.compareTo("MtrChangeList") == 0 || getView.compareTo("NewConsumerList") == 0 || getView.compareTo("ReconnectionList") == 0) {
            return;
        } else {
            //finish();
            //startActivity(new Intent(this, ActvBilling.class));
            startActivity(new Intent(this, ActvConsumerNbrInput.class));

        }
    }


    void billing() {
        try {
            UtilDB utilDB = new UtilDB(getApplicationContext());
            System.out.println("Consumer No:......" + UtilAppCommon.acctNbr);
            Cursor cursor = utilDB.getUnbilledDatacons(UtilAppCommon.acctNbr);
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    //String strCANo = cursor.getString(cursor.getColumnIndex("CANumber"));
                    //utilDB.getBillInputDetails(strCANo, "CA Number");
                    utilDB.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number");
                    String[] copySAPInputData = new String[12];
                    copySAPInputData[0] = cursor.getString(0);
                    copySAPInputData[1] = cursor.getString(1);
                    copySAPInputData[2] = cursor.getString(8);
                    copySAPInputData[3] = cursor.getString(9);
                    copySAPInputData[4] = cursor.getString(5);
                    copySAPInputData[5] = cursor.getString(6);
                    copySAPInputData[6] = cursor.getString(7);
                    copySAPInputData[7] = cursor.getString(4);
                    copySAPInputData[8] = cursor.getString(3);
                    copySAPInputData[9] = cursor.getString(2);
                    copySAPInputData[10] = cursor.getString(11);
                    copySAPInputData[11] = "2";
                    UtilAppCommon.SAPIn = new StructSAPInput();
                    UtilAppCommon.copySAPInputData(copySAPInputData);


                    System.out.println("1:" + cursor.getString(0));
                    System.out.println("2:" + cursor.getString(1));
                    System.out.println("3:" + cursor.getString(2));
                    System.out.println("4:" + cursor.getString(3));
                    System.out.println("5:" + cursor.getString(4));
                    System.out.println("6:" + cursor.getString(5));
                    System.out.println("7:" + cursor.getString(6));
                    //utilDB.getOutputBillRecord(strCANo);
                    //20.11.15
                    AsyncGetOutputData asyncGetOutputData = new AsyncGetOutputData(this, new OnBillGenerate() {

                        @Override
                        public void onFinish() {
                            // TODO Auto-generated method stub

                            printdlg();

                        }
                    });

                    asyncGetOutputData.execute(copySAPInputData);
                    Thread.sleep(10000);


                } while (cursor.moveToNext());
            } else {
                Toast.makeText(getApplicationContext(), "No data to synchronize", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("btnSummary E", e.getMessage());
        } finally {
            finish();
            //startActivity(new Intent(ActivitySyncData.this, ActvivityMain.class));
        }
    }


    private void printdlg() {
        {
            // need to be change for photo

            final AlertDialog ad = new AlertDialog.Builder(this)
                    .create();
            ad.setTitle("Confirm");
            ad.setMessage("Confirm to print");
            ad.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                    (dialog, which) -> {
                        // TODO Auto-generated method stub
                        ad.dismiss();
                        Write2SbmOut();
                        //startActivity(new Intent(ctx, ActvBillPrinting.class));
                    });
            ad.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
                    (dialog, which) -> {
                        // TODO Auto-generated method stub
                        ad.dismiss();
                        // startActivity(getIntent());
                        //startActivity(new Intent(getApplicationContext(), ActvBillingOption.class));
                        if (UtilAppCommon.billType.equalsIgnoreCase("A"))
                            startActivity(new Intent(getBaseContext(), ActvConsumerNbrInput.class));
                        else if (UtilAppCommon.billType.equalsIgnoreCase("L"))
                            startActivity(new Intent(getBaseContext(), ActvLegacyNbrInput.class));
                        else if (UtilAppCommon.billType.equalsIgnoreCase("S"))
                            startActivity(new Intent(getBaseContext(), ActvSequenceData.class));
                        else if (UtilAppCommon.billType.equalsIgnoreCase("M"))
                            startActivity(new Intent(getBaseContext(), MeterNbrInput.class));
                        else
                            startActivity(new Intent(getBaseContext(), ActvBillingOption.class));
                    });
            ad.show();
        }
    }

    // 20.11.15
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
            System.out.println("Output cnt .." + cnt);
            if (cnt != 0) {
                util1.getOutputBillRecord(UtilAppCommon.acctNbr);
                //22.11.15
                try {
                    util1.copyToOutputStruct(UtilAppCommon.SAPIn.CANumber);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("ActBill copyOut E", e.getMessage());
                }
                startActivity(new Intent(this, ActvBillPrinting.class)); // used to print bill through printer
            } else
                startActivity(new Intent(this, ActvMsgPrinting.class)); // used to print bill through printer
            //printbill();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("Write2SbmOut E", e.getMessage());
        }
        Log.e("Write2SbmOut", "Completed");
    }

}
