package org.cso.MobileSpotBilling;

import java.util.ArrayList;
import java.util.HashMap;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ActvSrcByConNo extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */
    UtilDB util;
    EditText edtOldAccnbr;
    Button btnSearchOldAcctNbr;
    ListView list;
    View header;
    SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
    Toolbar toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dg_searchbyoldacountno);
        toolbar = findViewById(R.id.toolbar_serby_oldconno);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
        toolbar.setTitle("Search By Old-Consumer Number");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        String bellowMonth = "1";
        System.out.println("bellow mnth is ::::::   " + bellowMonth);
        list = (ListView) findViewById(R.id.lvSrcByAcNo);
        //tvConsumerListTestNOR=(TextView)findViewById(R.id.tvConsumerListTestNOR);
        LayoutInflater inflater = getLayoutInflater();
        header = inflater.inflate(R.layout.newcons_header, (ViewGroup) (list), false);
        list.addHeaderView(header, null, false);


        edtOldAccnbr = (EditText) findViewById(R.id.edtOldAccnbr);
        btnSearchOldAcctNbr = (Button) findViewById(R.id.btnSearchOldAcctNbr);

        btnSearchOldAcctNbr.setOnClickListener(v -> search());
        list.setOnItemClickListener((parent, view, position, id) -> {

            HashMap<String, String> map = mylist.get(position - 1);

            Toast.makeText(getApplicationContext(), map.get("ACC_NO").toString(), Toast.LENGTH_SHORT).show();
            UtilAppCommon.acctNbr = map.get("ACC_NO").toString();
            gotoBilling();

// String.format("%04d", straccno);
        });

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
            System.err.println("Going to ActvBilling");
            startActivity(new Intent(this, ActvBilling.class));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "ActvSrcByConNo");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart", "ActvSrcByConNo");
    }

    void search() {
        UtilDB util = new UtilDB(getBaseContext());
        list = (ListView) findViewById(R.id.lvSrcByAcNo);

        edtOldAccnbr = (EditText) findViewById(R.id.edtOldAccnbr);
        mylist = util.getSrcListByOldAccNo(edtOldAccnbr.getText().toString());
        util.close();

        adapter = new InteractiveArrayAdapter(this, R.layout.newcons_listitems, mylist,
                new String[]{"BINDER", "ACC_NO", "NAME", "ADDR"},
                new int[]{R.id.newConsBinder, R.id.newConsAccNo, R.id.newConsName, R.id.newConsAddr});
        list.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        //  closePrinter();
        onBackPressed();
        return true;
    }


}