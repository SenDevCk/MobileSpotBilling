package org.cso.MobileSpotBilling;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import org.cso.MSBAsync.AsyncBluetoothReading;
import org.cso.MSBUtil.GPSTracker;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DcMeterReading extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText inputName;
    private TextInputLayout inputLayoutName;
    private Button btn_read_meter;
    String login_id;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_dc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        invalidateOptionsMenu();

        btn_read_meter = (Button) findViewById(R.id.btn_enter_ac_meter);
        TextView dc_meter_date = (TextView) findViewById(R.id.dc_meter_date_reading);
        TextView udpm_id = (TextView) findViewById(R.id.udpm_id);
        TextView dc_meter_reading = (TextView) findViewById(R.id.dc_meter_reading);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String timestamp_dc = preferences.getString("timestamp_dc", "");

        String dcMeterId = preferences.getString("dcMeterId", "");
        String dcMeterReading = preferences.getString("dcMeterReading", "");
        udpm_id.setText(dcMeterId);
        dc_meter_reading.setText(dcMeterReading);

        dc_meter_date.setText(timestamp_dc);

        /*btn_read_meter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();

            }
        });*/

        btn_read_meter.setOnClickListener(v -> {
            final String strBluetoothInput[] = new String[13];
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String timestamp_ac = df.format(c.getTime());


            Calendar nxtcal = new GregorianCalendar(
                    Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(0, 4)),
                    Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(5, 7)),
                    Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(8, 10)) - 1);

            //UtilAppCommon.in.SCHEDULED_BILLING_DATE = "2017.07.31";
            int temp = Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(5, 7));
            String strtemp = "";
            if(temp <= 9)
                strtemp = "0" + Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(5, 7));
            else
                strtemp = ""  + Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(5, 7));

            String strtemp1 = "";

            int temp1 = Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(8, 10));
            if(temp1 <= 9)
                strtemp1 = "0" + Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(8, 10));
            else
                strtemp1 = ""  + Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(8, 10));

            String nxtDate = strtemp1 + "." + strtemp
                    + "." + Integer.parseInt(UtilAppCommon.in.SCH_MTR_READING_DT.substring(0, 4));

            String strlocation = showSettingsAlert();
            SimpleDateFormat SAPInformat = new SimpleDateFormat("dd.MM.yyyy");
            strBluetoothInput[0] = UtilAppCommon.in.INSTALLATION;
            strBluetoothInput[1] = nxtDate;
            strBluetoothInput[2] = UtilAppCommon.in.MONTH_SEASONAL;
            strBluetoothInput[3] = "OK";      //MR Note
            strBluetoothInput[4] = ((TextView) findViewById(R.id.dc_meter_reading)).getText().toString();
            strBluetoothInput[5] = "0";                         //Recorded Demand
            strBluetoothInput[6] = "";                         //Power Factor
            strBluetoothInput[7] = strlocation.split("¥")[0];   //Latitude
            strBluetoothInput[8] = strlocation.split("¥")[1];   //Longitude
            strBluetoothInput[9] = "1";                         //Flag
            strBluetoothInput[10] = "0";                        //KVAH
            strBluetoothInput[11] = UtilAppCommon.in.CONTRACT_AC_NO;    //CA Number
            strBluetoothInput[12] = "0";

            //HardCoded Value For Testing

            /*strBluetoothInput[0] = "5001603503";
            strBluetoothInput[1] = "31.07.2017";
            strBluetoothInput[2] = "000000000102486228";
            strBluetoothInput[3] = "OK";      //MR Note
            strBluetoothInput[4] = ((TextView) findViewById(R.id.dc_meter_reading)).getText().toString();
            strBluetoothInput[5] = "0";                         //Recorded Demand
            strBluetoothInput[6] = "";                         //Power Factor
            strBluetoothInput[7] = strlocation.split("¥")[0];   //Latitude
            strBluetoothInput[8] = strlocation.split("¥")[1];   //Longitude
            strBluetoothInput[9] = "1";                         //Flag
            strBluetoothInput[10] = "0";                        //KVAH
            strBluetoothInput[11] = "100742011";    //CA Number*/

            //

             if(strBluetoothInput[4] != null){
                try {
                    double reading = Double.parseDouble(strBluetoothInput[4]);
                    reading/=100;
                    strBluetoothInput[4]=Double.toString(reading);
                    Toast.makeText(DcMeterReading.this, "The reading send to server "+strBluetoothInput[4], Toast.LENGTH_SHORT).show();
                }
                catch (NumberFormatException e){
                    Toast.makeText(DcMeterReading.this, "Cant convert the reading "+strBluetoothInput[4]+" Number Format Exception ", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(DcMeterReading.this, "Reading from DC Meter is null.", Toast.LENGTH_SHORT).show();
            }

            UtilDB utilDB = new UtilDB(getApplicationContext());
            utilDB.insertIntoSAPBlueInput(strBluetoothInput);
            utilDB.getSAPBlueInput(UtilAppCommon.in.CONTRACT_AC_NO);

            AsyncBluetoothReading asyncBluetoothReading = new AsyncBluetoothReading(DcMeterReading.this);
            asyncBluetoothReading.execute(strBluetoothInput);

            UtilAppCommon.bBtnGenerateClicked = true;
            Intent in = new Intent(getApplicationContext(), ActvBilling.class);
            in.putExtra("flblue", true);
            //Intent in = getIntent();
            //setResult(RESULT_OK, in);
            startActivity(in);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "DcMeterReading");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart", "DcMeterReading");
    }

    public void dialog() {

        final String strBluetoothInput[] = new String[13];

        dialog = new Dialog(DcMeterReading.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_custom_date);
        //dialog.setCancelable(false);
        Button btn_cancel = (Button) dialog.findViewById(R.id.button_cancel);
        Button btn_ok = (Button) dialog.findViewById(R.id.button_ok);
        inputLayoutName = (TextInputLayout) dialog.findViewById(R.id.input_layout_name);
        inputName = (EditText) dialog.findViewById(R.id.input_name);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputName, InputMethodManager.SHOW_IMPLICIT);*/
        btn_cancel.setOnClickListener(v -> dialog.dismiss());

        btn_ok.setOnClickListener(v -> {
            if (!validateName()) {
                return;
            }
            else {
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String timestamp_ac = df.format(c.getTime());

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ac_meter_reading",inputName.getText().toString());
                editor.putString("timestamp_ac", timestamp_ac);
                editor.apply();

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

                String nxtDate = strtemp1 + "." + strtemp
                        + "." + Integer.parseInt(UtilAppCommon.in.SCHEDULED_BILLING_DATE.substring(0, 4));

                String strlocation = showSettingsAlert();
                SimpleDateFormat SAPInformat = new SimpleDateFormat("dd.MM.yyyy");
                strBluetoothInput[0] = UtilAppCommon.in.INSTALLATION;
                strBluetoothInput[1] = nxtDate;
                strBluetoothInput[2] = UtilAppCommon.in.SAP_DEVICE_NO;
                strBluetoothInput[3] = "OK";      //MR Note
                strBluetoothInput[4] = ((TextView) findViewById(R.id.dc_meter_reading)).getText().toString();
                strBluetoothInput[5] = "0";                         //Recorded Demand
                strBluetoothInput[6] = "0";                         //Power Factor
                strBluetoothInput[7] = strlocation.split("¥")[0];   //Latitude
                strBluetoothInput[8] = strlocation.split("¥")[1];   //Longitude
                strBluetoothInput[9] = "1";                         //Flag
                strBluetoothInput[10] = "0";                        //KVAH
                strBluetoothInput[11] = UtilAppCommon.in.CONTRACT_AC_NO;    //CA Number
                strBluetoothInput[12] = "0";

                //HardCoded Value For Testing
                /*strBluetoothInput[0] = "5001603503";
                strBluetoothInput[1] = "31.07.2017";
                strBluetoothInput[2] = "102486228";
                strBluetoothInput[3] = "OK";      //MR Note
                strBluetoothInput[4] = ((TextView) findViewById(R.id.dc_meter_reading)).getText().toString();
                strBluetoothInput[5] = "0";                         //Recorded Demand
                strBluetoothInput[6] = "0";                         //Power Factor
                strBluetoothInput[7] = strlocation.split("¥")[0];   //Latitude
                strBluetoothInput[8] = strlocation.split("¥")[1];   //Longitude
                strBluetoothInput[9] = "1";                         //Flag
                strBluetoothInput[10] = "0";                        //KVAH
                strBluetoothInput[11] = "100742011";    //CA Number*/


                if(strBluetoothInput[4] != null){
                    try {
                        double reading = Double.parseDouble(strBluetoothInput[4]);
                        reading/=100;
                        strBluetoothInput[4]=Double.toString(reading);
                        Toast.makeText(DcMeterReading.this, "The reading send to server "+strBluetoothInput[4], Toast.LENGTH_SHORT).show();
                    }
                    catch (NumberFormatException e){
                        Toast.makeText(DcMeterReading.this, "Cant convert the reading "+strBluetoothInput[4]+" Number Format Exception ", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(DcMeterReading.this, "Reading from DC Meter is null.", Toast.LENGTH_SHORT).show();
                }
                UtilDB utilDB = new UtilDB(getApplicationContext());
                utilDB.insertIntoSAPBlueInput(strBluetoothInput);

                AsyncBluetoothReading asyncBluetoothReading = new AsyncBluetoothReading(DcMeterReading.this);
                asyncBluetoothReading.execute(strBluetoothInput);

                Intent in = new Intent(getApplicationContext(), ActvBilling.class);
                startActivity(in);
            }

        });

        dialog.show();
    }

    public String showSettingsAlert(){
        GPSTracker gps  = new GPSTracker(DcMeterReading.this);

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


    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
            login_id=inputName.getText().toString();
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.action_home:
                Intent intent = new Intent(getApplicationContext(), ActivityBT_Reading.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

        }
        invalidateOptionsMenu();
        return true;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;


            }
        }
    }

}
