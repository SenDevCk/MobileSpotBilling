package org.cso.MobileSpotBilling;

import java.util.ArrayList;

import java.util.HashMap;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BilledUnbilled extends AppCompatActivity {
    TextView tvBilledUnbilledNOR;
    ArrayList<HashMap<String, String>> mylist;
    Toolbar toolbar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bub_list);
        toolbar = findViewById(R.id.toolbar_billlist);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
        toolbar.setTitle("Billing List");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        UtilDB util = new UtilDB(getBaseContext());
        tvBilledUnbilledNOR = (TextView) findViewById(R.id.tvBilledUnbilledNOR);
        ListView list = (ListView) findViewById(R.id.lvBilledUnbilledList);
        mylist = util.getBuBlistItems();
        util.close();
        SimpleAdapter adapter = new InteractiveArrayAdapter(this, R.layout.bub_listitems, mylist,
                new String[]{"MRU", "BILLED", "UNBILLED", "TOTAL"},
                new int[]{R.id.bubBinder, R.id.bubBilled, R.id.bubUnbilled, R.id.bubTotal});
        LayoutInflater inflater = getLayoutInflater();
        tvBilledUnbilledNOR.setText("No. Of Record Found:" + mylist.size());
        final View header = inflater.inflate(R.layout.bub_header, (ViewGroup) (list), false);
        list.addHeaderView(header, null, false);
        list.setAdapter(adapter);

        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intentConsumerListTest = new Intent(BilledUnbilled.this, ConsumerListTest.class);
            HashMap<String, String> map = mylist.get(position - 1);
            intentConsumerListTest.putExtra("MRU", map.get("MRU").toString());
            intentConsumerListTest.putExtra("View", "UnbilledList");
            startActivity(intentConsumerListTest);
        });
    }

	@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}


}
