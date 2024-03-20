package org.cso.MobileSpotBilling;

import java.util.ArrayList;

import java.util.HashMap;

import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActvSummary extends AppCompatActivity {
    //TextView tvSummaryNOR;
    Toolbar toolbar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);
        toolbar = findViewById(R.id.toolbar_summary);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
        toolbar.setTitle("Summary");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        UtilDB util = new UtilDB(getBaseContext());
        //tvSummaryNOR = (TextView) findViewById(R.id.tvSummaryNOR);
        ListView list = (ListView) findViewById(R.id.lvSummaryList);
        //ArrayList<HashMap<String, String>> mylist =util.getSummary();
        ArrayList<HashMap<String, String>> mylist = util.getSummaryReport1();
        util.close();
        //SimpleAdapter adapter = new InteractiveArrayAdapter(this,R.layout.summary_listitems, mylist,
        //		new String[] {"MRU","TOTAL", "BILLED","UNBILLED","POSTED","PENDING"},
        SimpleAdapter adapter = new InteractiveArrayAdapter(this, R.layout.summary_listitems, mylist,
                new String[]{"Category", "TotalCons", "Billed", "Units", "BilledAmt", "Pending"},
                new int[]{R.id.tvsummaryBinder, R.id.tvsummaryTotal, R.id.tvsummaryBilled, R.id.tvsummaryUnbilled, R.id.tvsummaryPosted, R.id.tvsummaryPending});
        //new int[] {R.id.tvsummaryBinder,R.id.tvsummaryTotal, R.id.tvsummaryBilled, R.id.tvsummaryUnbilled,R.id.tvsummaryPosted,R.id.tvsummaryPending});


        LayoutInflater inflater = getLayoutInflater();
        //tvSummaryNOR.setText("No. Of Record Found:" + mylist.size());
        toolbar.setSubtitle("No. Of Record Found : " + mylist.size());
        final View header = inflater.inflate(R.layout.summarylist_header, (ViewGroup) (list), false);
        list.addHeaderView(header, null, false);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        //  closePrinter();
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        // do something on back.
        super.onBackPressed();
        // startActivity(new Intent(this, ActvivityMain.class));
        //finish();
    }

}
