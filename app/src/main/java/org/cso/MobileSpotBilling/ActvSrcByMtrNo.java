package org.cso.MobileSpotBilling;

import java.util.ArrayList;
import java.util.HashMap;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ActvSrcByMtrNo extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */
    UtilDB util;
    EditText editMeterNbr;
    Button btnSearchMtrNbr;
    ListView list;
    View header;
    SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dg_serchbymeterno);
        toolbar = findViewById(R.id.toolbar_serbymetno);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
        toolbar.setTitle("Search By Meter NUmber");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        String bellowMonth = "1";
        System.out.println("bellow mnth is ::::::   " + bellowMonth);
        list = (ListView) findViewById(R.id.lvActvSrcByMtrNo);
        //tvConsumerListTestNOR=(TextView)findViewById(R.id.tvConsumerListTestNOR);
        LayoutInflater inflater = getLayoutInflater();
        header = inflater.inflate(R.layout.mtrsrclistheader, (ViewGroup) (list), false);
        list.addHeaderView(header, null, false);


        editMeterNbr = (EditText) findViewById(R.id.editMeterNbr);
        btnSearchMtrNbr = (Button) findViewById(R.id.btnSearchMtrNbr);

        btnSearchMtrNbr.setOnClickListener(v -> search());

        list.setOnItemClickListener((parent, view, position, id) -> {

            HashMap<String, String> map = mylist.get(position - 1);

            Toast.makeText(getApplicationContext(), map.get("acc_no").toString(), Toast.LENGTH_SHORT).show();
            UtilAppCommon.acctNbr = map.get("acc_no").toString();
            gotoBilling();
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "ActvSrcByMtrNo");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart", "ActvSrcByMtrNo");
    }
	@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}

    void gotoBilling() {
        UtilDB util = new UtilDB(getBaseContext());
        UtilAppCommon.binder = util.getActiveBinder();
        UtilAppCommon.sdoCode = util.getSdocd();
        if (UtilAppCommon.acctNbr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please Enter value for Account Number", Toast.LENGTH_LONG).show();
        } else if (!util.getBillInputDetails(UtilAppCommon.acctNbr, "CA Number")) {

            Toast.makeText(getBaseContext(), "Please Enter Valid Account Number", Toast.LENGTH_LONG).show();
        } else {
            //finish();
            System.out.println("Going to ActvBilling");
            startActivity(new Intent(this, ActvBilling.class));

        }
    }

    void search() {
        UtilDB util = new UtilDB(getBaseContext());
        list = (ListView) findViewById(R.id.lvActvSrcByMtrNo);

        editMeterNbr = (EditText) findViewById(R.id.editMeterNbr);
        //mylist=util.getSearchResultByMeter(editMeterNbr.getText().toString()) ;
        util.close();

        adapter = new InteractiveArrayAdapter(this, R.layout.mtrsearchlistitem, mylist,
                new String[]{"meter_no", "sdo_cd", "binder", "acc_no", "name"},
                new int[]{R.id.tvMrtSrcListItemMtrNo, R.id.tvMrtSrcListItemSDO, R.id.tvMrtSrcListItemBinder, R.id.tvMrtSrcListItemAccNo, R.id.tvMrtSrcListItemName});
        list.setAdapter(adapter);

    }




}