package org.cso.MobileSpotBilling;

import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.MobileSpotBilling.R;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ActvModifySetUpInfo extends AppCompatActivity implements OnClickListener {
    /**
     * Called when the activity is first created.
     */
    UtilDB util;
    EditText username, password;
    String selectedBinder;
    private SpinnerData binderItems[];
    Toolbar toolbar;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dg_modifysetupinfo);

        toolbar = findViewById(R.id.toolbar_modsetup_info);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
        toolbar.setTitle("Data");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        showModifySetupInfo();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //  closePrinter();
        onBackPressed();
        return true;
    }

    public void onClick(View view) {
        //startActivity(new Intent(this, ActvSetupInfo.class));
        //finish();
        int id = view.getId();
        switch (id) {
            case R.id.btnUpdate:

                Spinner s = (Spinner) findViewById(R.id.spnBinderList);
                selectedBinder = (String) s.getSelectedItem().toString();
                util = new UtilDB(this);
                util.updateMRU(selectedBinder.toString());
                UtilAppCommon.actBinder = selectedBinder.toString();
                Toast.makeText(this, "Binder number updated successfully", Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.btnCancel:
                finish();
                break;

        }

    }

    private void showModifySetupInfo() {
        setContentView(R.layout.dg_modifysetupinfo);
        View v = findViewById(R.id.ModifySetupInfoLayout);
        v.setVisibility(View.VISIBLE);

        Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);


        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        UtilDB util = new UtilDB(getBaseContext());
        util.getUserInfo();


        String luserId = UtilAppCommon.ui.IMIE_NO;
        String limie = UtilAppCommon.ui.IMIE_NO;

        //((TextView) findViewById(R.id.txtVwSdoCd)).setText(lsdoCd);

        //PopulatingBinders
        Spinner s = (Spinner) findViewById(R.id.spnBinderList);
        selectedBinder = (String) s.getSelectedItem();
        String binderList[] = new String[1]; // lbinder.split("\\$");

        binderItems = new SpinnerData[binderList.length];

        System.out.println("binder item list length is " + binderList.length);

        for (int i = 0; i < binderList.length; i++) {
            binderItems[i] = new SpinnerData(binderList[i].trim());
            System.out.println("binder     ====" + binderList[i]);
        }

        System.out.println("binder item list length is-Sudhir " + binderItems);

        ArrayAdapter<SpinnerData> adapter = new ArrayAdapter<SpinnerData>(this, android.R.layout.simple_spinner_item, binderItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        //End

        ((TextView) findViewById(R.id.txtVwImieNbr)).setText(limie);
        ((TextView) findViewById(R.id.txtVwUserId)).setText(luserId);
        //((TextView) findViewById(R.id.txtVwActiveBinder)).setText(lActiveBinder);
        //((TextView) findViewById(R.id.txtVwActiveUser)).setText(lActiveUser);
        //((TextView) findViewById(R.id.txtVwBillMth)).setText(lBillMth);

    }

}