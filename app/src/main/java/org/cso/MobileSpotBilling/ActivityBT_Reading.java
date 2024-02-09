package org.cso.MobileSpotBilling;

import android.Manifest;



import android.os.Bundle;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputLayout;

import org.cso.MSBAsync.AsyncBluetoothReading;
import org.cso.MSBUtil.GPSTracker;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.adapter.DeviceListAdapter;
import org.cso.ble.BleWrapper;
import org.cso.ble.BleWrapperUiCallbacks;
import org.cso.config.AppController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class ActivityBT_Reading extends AppCompatActivity {

    public static final String MYPREFERENCES = "MyPrefs";
    private static final int REQUEST_ENABLE_BT = 0;
    private static final long SCANNING_TIMEOUT = 5 * 1000;
    private static final int ENABLE_BT_REQUEST_ID = 1;
    public static String TAG = "SCANNING ACTIVITY";
    public static String appReaderVersion = "";
    public static boolean synctoServer = false;
    SharedPreferences sharedPreferences;
    ListView currentDeviceList;
    TextView connectedTxt;
    TextView availableMeterMessageTxt;
    private ProgressDialog pDialog;
    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private DeviceListAdapter mDevicesListAdapter = null;
    private BleWrapper mBleWrapper = null;
    private BluetoothAdapter bluetoothAdapter;
    Dialog dialog;
    private EditText input_dc;
    private TextInputLayout input_layout_dc;
    public static final String IPADDRESS = "ipaddress";
    public static final String PORT = "port";

    private GoogleApiClient client;

    public static SpannableString bold(String s) {
        SpannableString spanString = new SpannableString(s);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0,
                spanString.length(), 0);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        return spanString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        AppController.changeLocale();

        //getSupportActionBar().setTitle((getResources().getString(R.string.bt_meter)));
        //invalidateOptionsMenu();

        setContentView(R.layout.activity_scanner);

        String[] PermissionsLocation = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (int i = 0; i < PermissionsLocation.length; i++) {

            if (ContextCompat.checkSelfPermission(ActivityBT_Reading.this, PermissionsLocation[i]) !=
                    PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityBT_Reading.this,
                        PermissionsLocation[i])) {

                } else {

                    ActivityCompat.requestPermissions(ActivityBT_Reading.this,
                            new String[]{PermissionsLocation[i]},
                            i);
                    if (PermissionsLocation[i] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        try {
                            //clog = CLogger.initLoggerInstance(SplashScreen.this);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }

        currentDeviceList = (ListView) findViewById(R.id.scanndevicelist);
        connectedTxt = (TextView) findViewById(R.id.nodata);
        availableMeterMessageTxt = (TextView) findViewById(R.id.availableMeterMessage);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        sharedPreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);

        mBleWrapper = new BleWrapper(this, new BleWrapperUiCallbacks.Null() {
            @Override
            public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
                handleFoundDevice(device, rssi, record);
            }
        });
        if (mBleWrapper.checkBleHardwareAvailable() == false) {
            bleMissing();
        }



        currentDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long ids) {
                // TODO Auto-generated method stub
                if (bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    final BluetoothDevice device = mDevicesListAdapter.getDevice(position);
                    if (device == null) return;
                    final Intent intent = new Intent(ActivityBT_Reading.this, PeripheralActivity.class);
                    intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_NAME, device.getName());
                    intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                    intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_RSSI, mDevicesListAdapter.getRssi(position));
                    if (mScanning) {
                        mScanning = false;
                        invalidateOptionsMenu();
                        mBleWrapper.stopScanning();
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //startActivityForResult(intent, 6);
                    //if(device.getName().equalsIgnoreCase("123164"))
                    if(device.getName().equalsIgnoreCase(UtilAppCommon.in.PWR_FACTOR))
                        startActivity(intent);
                    else
                        Toast.makeText(getApplicationContext(), "Please select a valid bluetooth meter.", Toast.LENGTH_LONG).show();
                    //finish();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio f
        // or more information.
        //  client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    /* add device to the current list of devices */
    private void handleFoundDevice(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        // adding to the UI have to happen in UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                availableMeterMessageTxt.setVisibility(View.INVISIBLE);
                mDevicesListAdapter.addDevice(device, rssi, scanRecord);
                mDevicesListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scanning, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nometerfound:

                final String strBluetoothInput[] = new String[13];
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String timestamp_ac = df.format(c.getTime());

                String strlocation = showSettingsAlert();
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


                SimpleDateFormat SAPInformat = new SimpleDateFormat("dd.MM.yyyy");
                strBluetoothInput[0] = UtilAppCommon.in.INSTALLATION;
                strBluetoothInput[1] = nxtDate;
                strBluetoothInput[2] = UtilAppCommon.in.MONTH_SEASONAL;
                strBluetoothInput[3] = "BT";      //MR Note
                strBluetoothInput[4] = UtilAppCommon.in.PREV_KWH_CYCLE1;
                strBluetoothInput[5] = "0";                         //Recorded Demand
                strBluetoothInput[6] = "";                         //Power Factor
                strBluetoothInput[7] = strlocation.split("¥")[0];   //Latitude
                strBluetoothInput[8] = strlocation.split("¥")[1];   //Longitude
                strBluetoothInput[9] = "1";                         //Flag
                strBluetoothInput[10] = "0";                        //KVAH
                strBluetoothInput[11] = UtilAppCommon.in.CONTRACT_AC_NO;    //CA Number
                strBluetoothInput[12] = "0";

                Toast.makeText(ActivityBT_Reading.this, "No meter found hence previous reading "+strBluetoothInput[4]+" is taken.", Toast.LENGTH_SHORT).show();

                /*
                strBluetoothInput[0] = "5001603503";
                strBluetoothInput[1] = "31.07.2017";
                strBluetoothInput[2] = "102486228";
                strBluetoothInput[3] = "OK";      //MR Note
                strBluetoothInput[4] = "500";
                strBluetoothInput[5] = "0";                         //Recorded Demand
                strBluetoothInput[6] = "0";                         //Power Factor
                strBluetoothInput[7] = strlocation.split("¥")[0];   //Latitude
                strBluetoothInput[8] = strlocation.split("¥")[1];   //Longitude
                strBluetoothInput[9] = "1";                         //Flag
                strBluetoothInput[10] = "0";                        //KVAH
                strBluetoothInput[11] = "100742011";
                */

                UtilDB utilDB = new UtilDB(getApplicationContext());
                utilDB.insertIntoSAPBlueInput(strBluetoothInput);
                utilDB.getSAPBlueInput(UtilAppCommon.in.CONTRACT_AC_NO);
                //utilDB.getSAPBlueInput(strBluetoothInput[11]);

                AsyncBluetoothReading asyncBluetoothReading = new AsyncBluetoothReading(ActivityBT_Reading.this);
                asyncBluetoothReading.execute(strBluetoothInput);

                UtilAppCommon.bBtnGenerateClicked = true;
                Intent in = new Intent(getApplicationContext(), ActvBilling.class);
                in.putExtra("flblue", true);
                startActivity(in);
                this.finish();


                break;
/*            case R.id.serversetting:
                sharedPreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
                String ipAddress = sharedPreferences.getString(IPADDRESS, "dev.solardc.in");
                String port = sharedPreferences.getString(PORT, "7071");
                dialogForServerSetting(getResources().getString(R.string.setting_title), ipAddress, port);
                break;*/
        }
        invalidateOptionsMenu();
        return true;
    }

    public void dialogForServerSetting(String title, String ip, final String port) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_serversetting);
        dialog.setCancelable(false);
        final EditText ipaddress = (EditText) dialog.findViewById(R.id.ipaddress);
        final EditText portaddress = (EditText) dialog.findViewById(R.id.port);
        ipaddress.setText(ip);
        ipaddress.setSelection(ip.length());
        portaddress.setText(port);
        portaddress.setSelection(port.length());
        final Button okBtn = (Button) dialog.findViewById(R.id.ok);
        final Button cancelBtn = (Button) dialog.findViewById(R.id.cancel);

        RelativeLayout rl = (RelativeLayout) dialog.findViewById(R.id.rlmain);

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sharedPreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(IPADDRESS, ipaddress.getText().toString());
                editor.putString(PORT, portaddress.getText().toString());
                editor.apply();

                SharedPreferences.Editor serverioeditor = sharedPreferences.edit();
                serverioeditor.putString(IPADDRESS, ipaddress.getText().toString());
                serverioeditor.putString(PORT, portaddress.getText().toString());
                serverioeditor.commit();
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
    }


    private void btDisabled() {
        Toast.makeText(this, "Sorry, Bluetooth has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void bleMissing() {
        Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.changeLocale();
        // on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
        if (mBleWrapper.isBtEnabled() == false) {
            // BT is not turned on - ask user to make it enabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            // see onActivityResult to check what is the status of our request
        }

        // initialize BleWrapper object
        mBleWrapper.initialize(); //TODO if initialize returns false, what to do?
        if (mBleWrapper.initialize() == false) {
            finish();
        }
        mDevicesListAdapter = new DeviceListAdapter(this, ActivityBT_Reading.this);

        System.out.println(currentDeviceList.getCount());


        // Automatically start scanning for devices
        mScanning = true;
        // remember to add timeout for scanning to not run it forever and drain the battery
        addScanningTimeout();
        mBleWrapper.startScanning();


        currentDeviceList.setAdapter(mDevicesListAdapter);
        invalidateOptionsMenu();
        if (currentDeviceList.getCount() != 0) {

            availableMeterMessageTxt.setVisibility(View.INVISIBLE);
        } else {

        }
    }


    /* make sure that potential scanning will take no longer
    * than <SCANNING_TIMEOUT> seconds from now on */
    private void addScanningTimeout() {
        Runnable timeout = new Runnable() {
            @Override
            public void run() {
                if (mBleWrapper == null) return;
                mScanning = false;
                mBleWrapper.stopScanning();
                invalidateOptionsMenu();
            }
        };
        mHandler.postDelayed(timeout, SCANNING_TIMEOUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScanning = false;
        mBleWrapper.stopScanning();
        invalidateOptionsMenu();

        mDevicesListAdapter.clearList();
    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       /* client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Scanning Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.chakra.mydcmeter/http/host/path")
        );*/
        //AppIndex.AppIndexApi.start(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Scanning Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://org.cso.MobileSpotBilling/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Scanning Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://org.cso.MobileSpotBilling/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        SharedPreferences settings = getSharedPreferences("settings", 0);
        boolean isChecked = settings.getBoolean("checkbox", false);




      /*  // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Scanning Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.chakra.mydcmeter/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    public void dialog() {

        dialog = new Dialog(ActivityBT_Reading.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dc_manual);
        //dialog.setCancelable(false);
        Button btn_cancel = (Button) dialog.findViewById(R.id.button_cancel_dc);
        Button btn_ok = (Button) dialog.findViewById(R.id.button_ok_dc);
        input_layout_dc = (TextInputLayout) dialog.findViewById(R.id.input_layout_dc);
        input_dc = (EditText) dialog.findViewById(R.id.input_dc);
        requestFocus(input_dc);
        /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input_dc, InputMethodManager.SHOW_IMPLICIT);*/
        input_dc.addTextChangedListener(new MyTextWatcher(input_dc));

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName()) {
                    return;
                }
                else {
                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                    String timestamp_ac = df.format(c.getTime());

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("dcMeterReading",input_dc.getText().toString());
                    editor.putString("timestamp_dc",timestamp_ac);
                    editor.apply();
                    //Intent in = new Intent(getApplicationContext(), DcMeterReading.class);
                    //startActivity(in);
                }

            }
        });

        dialog.show();
    }

    private boolean validateName() {
        if (input_dc.getText().toString().trim().isEmpty()) {
            //input_layout_dc.setError(getString(R.string.err_msg_name));
            requestFocus(input_dc);
            return false;
        } else {
            input_layout_dc.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.input_dc:
                    validateName();
                    break;


            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 6:

                    Intent in = getIntent();
                    setResult(RESULT_OK, in);
                    finish();
                    break;
            }
        }
    }

    public String showSettingsAlert(){
        GPSTracker gps  = new GPSTracker(ActivityBT_Reading.this);

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

}



