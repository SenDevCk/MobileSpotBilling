package org.cso.MobileSpotBilling;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.cso.MSBUtil.DatabaseHelper;
import org.cso.MSBUtil.UtilDB;
import org.cso.ble.BleNamesResolver;
import org.cso.ble.BleWrapper;
import org.cso.ble.BleWrapperUiCallbacks;
import org.cso.config.AppController;
import org.cso.config.ConnectionDetector;
//import com.chakra.meterreader.dbs.DatabaseHelper;
import org.cso.MSBModel.Info;
import org.cso.MSBModel.PktIndexInterface;
import org.cso.MSBUtil.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/*import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;*/

public class PeripheralActivity extends AppCompatActivity implements BleWrapperUiCallbacks, PktIndexInterface {
    public static final String UDPMID = "udpmid";
    public static final String UDPMNAME = "name";
    public static final String UDPMIDNAME = "udpmidname";
    public static final String CONFIG = "config";
    public static final String RED = "#FF0000";
    public static final String GREEN = "#008000";
    public static final String AMBER = "#FFC200";
    public static final String MYPREFERENCES = "MyPrefs";
    public static final String IPADDRESS = "ipaddress";
    public static final String PORT = "port";
    public static final String SELECTLANGUAGE = "language";
    public static final String CHECKBOX = "checkbox";
    public static final String EXTRAS_DEVICE_NAME = "BLE_DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "BLE_DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_RSSI = "BLE_DEVICE_RSSI";
    private static final String TAGS = "DEBUG PACKET INFO";
    private static final String TAG = "PERIPHERAL ACTIVITY";
    private static final int OGH_PACKET_DATA_LENGTH_INDEX = 2;
    public static long numofdebugdata = 0;
    public static long numofdailyusgeydata = 0;
    public static long numOfPacketsReceived = 0;
    public static long numOfPacketsInError = 0;
    public static long sendSeqNum = 0;
    public static long numOfNoReplyPackets = 0;
    public static long rcvSeqNum = 0;
    public static boolean firstPacketSkiped = true;
    static byte[] completeDataArray = new byte[1024];
    static boolean firstPacket = true;
    private static RelativeLayout rl;
    private static String pktSend = null;
    public String deviceNodeid;
    public boolean debugdataReceived = false;
    public boolean dailyUsagedataReceived = false;
    public boolean livefeeddataReceived = false;
    public boolean breakLoop = false;
    String ip;
    String port;
    SharedPreferences sharedpreferences;
    int statusvalue = 0;
    String currentTimeStamplive;
    DatabaseHelper hp;
    BluetoothGattCharacteristic btChar = null;
    BluetoothGattService serviceCall;
    int numofpacketsize;
    Info infoParams = new Info();
    AlertDialog alertDialogshowMessage;
    ConnectionDetector cd;
    boolean isInternetPresent;
    byte[] encodeByte;
    byte[] encodeBytelanguage;
    private List<Info> udpmInfoList;
    private ArrayList selectedItemName = new ArrayList();
    private ArrayList sentItemNamesList = new ArrayList();
    private ArrayList errorPacket = new ArrayList();
    private ArrayList sucessPacket = new ArrayList();
    private HashMap<Integer, Integer> errorPktHMap = new HashMap<>();
    private int dataSendingLoop = 0;
    private int iEnergy = -1;
    //private int NUM_DATA_IN_PKT = 6;
    private int NUM_DAILYUSAGE_DATA_IN_PKT = 11;
    private int NUM_DEBUG_DATA_IN_PKT = 15;
    private int NUM_DEBUG_DATA_TOTAL_PKT_SIZE = 8;
    private ListType mListType = ListType.GATT_SERVICES;
    private String mDeviceName;
    private String mDeviceAddress;
    private String mDeviceRSSI;
    private BleWrapper mBleWrapper;
    private TextView mDeviceNameView;
    private TextView mDeviceRssiView;
    private TextView mDeviceStatus;
    private Button readButton;

    private boolean mNotificationEnabled = false;
    private String StartTime;
    private LinearLayout viewlinear;
    private String recordidStat;
    private SimpleDateFormat df;
    private byte[] dateTimeByte = new byte[5];
    private ProgressDialog pDialog;

    private Intent in = null;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //  private GoogleApiClient client;
    private static byte[] copyDataPartFromPkt(byte[] completByteArray) {
        if (completByteArray == null) {
            return null;
        }
        if (completByteArray.length < UDPM_INFO_PACKET_DATA_START_INDEX - 1) {
            return null;
        }
        byte[] dataPartArray = new byte[completByteArray.length - UDPM_INFO_PACKET_DATA_START_INDEX];
        System.arraycopy(completByteArray, UDPM_INFO_PACKET_DATA_START_INDEX, dataPartArray, 0, dataPartArray.length);
        return dataPartArray;
    }

    public static void printPkt(byte[] rawValue, String pktSend) {
        // TODO Auto-generated method stub
        if (rawValue == null) {


            return;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : completeDataArray) {
            sb.append(String.format("%02X ", b));
            //sb.append(",");
        }
        //log.debug(TAG + ": " + pktSend + ": Date and time pkt :" + sb.toString());

        for (int i = 0; i < rawValue.length; i++) {
            String hexString = String.format("%02x", rawValue[i]);
        }


    }

    public static String printPacket(byte[] rawValue, String pktSend, String type) {
        // TODO Auto-generated method stub
        String hexString = null;
        if (rawValue == null) {


        }
        StringBuilder sb = new StringBuilder();
        for (byte b : rawValue) {
            sb.append(String.format("%02X ", b));
            //sb.append(",");
        }


        for (int i = 0; i < rawValue.length; i++) {
            hexString = String.format("%02x", rawValue[i]);
        }
        return hexString;

    }

    public static SpannableString bold(String s) {
        SpannableString spanString = new SpannableString(s);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0,
                spanString.length(), 0);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        return spanString;
    }

    /*Here valueForCharacteristic interface is implemented this method is called by bleWapper callback,
    This method is used for calculate the number packet received and concat the received  packet  using accumalateArray(),
    and validate the received packet is valid packet or not and pass the received packet  to parse using ParseDataFromDevice
    * */
    public void valueForCharacteristic(final byte[] value, boolean first) {


        if (value.length != 0) {
            int code = Utility.checkForErrorsInIncommingPkt(value);

            if (code != 0) {
                if (mBleWrapper.numOfPacketsSent == 1) {
                    final String msg = getErrMsg(code);

                    readButton.setEnabled(false);
                    readButton.setBackgroundColor(0xff888888);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            printPacket(value, pktSend, "Reponse with error");
                            errorPacket.add(pktSend);
                            if (statusvalue == 2) {
                                alert("Date and time are failed to reset", "Failure");
                            }
                        }
                    });
                }
                int errorcount = 0;
                for (int i = 1; i < 11; i++) {
                    try {
                        errorcount = errorPktHMap.get(i);
                    } catch (Exception e) {

                    }

                    if (i == code) {
                        errorcount++;
                    }
                    errorPktHMap.put(i, errorcount);
                }
                numOfPacketsInError++;
                breakLoop = true;
                return;
            }
        }
        if (value.length == 0) {
            runOnUiThread((Runnable) () -> {
            });
            return;
        }

        if (first == true) {
            numofpacketsize = getNumOfPackets(value);
            accumalateArray(value);
            firstPacket = false;
            mBleWrapper.firstPacket = false;

        } else {
            accumalateArray(value);
        }

        numofpacketsize--;
        if (numofpacketsize == 0) {
            byte[] completeVal = copyDataPartFromPkt(completeDataArray);
            numOfPacketsReceived++;

            printPkt(completeVal, pktSend);
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("DATA", completeVal);
            hm.put("PKT_SEND", pktSend);
            if (pktSend.equalsIgnoreCase("infoRequest")) {
                hm.put("POJO", infoParams);
                hm.put("value", String.valueOf(numOfPacketsReceived));
            }
            new ParseDataFromDevice().execute(hm);
            firstPacket = true;
            printValues();
            if (statusvalue == 1) {
                eraseData(btChar, firstPacket);
            } else {
                writeData(btChar, firstPacket);
            }
        }
    }

    public void alert(String message, String title) {
        alertDialogshowMessage.dismiss();
        alertDialogshowMessage = new AlertDialog.Builder(PeripheralActivity.this).create();
        alertDialogshowMessage.setTitle(title);
        alertDialogshowMessage.setMessage(message);
        alertDialogshowMessage.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialogshowMessage.dismiss();
                        selectedItemName.clear();
                        errorPacket.clear();
                        sucessPacket.clear();
                        sentItemNamesList.clear();

                    }
                });

        alertDialogshowMessage.show();
    }

    private void printValues() {
        //log.debug(infoParams.toString());

    }

    public void uiDeviceConnected(final BluetoothGatt gatt, final BluetoothDevice device) {
        runOnUiThread((Runnable) () -> {
            mDeviceStatus.setText(getResources().getString(R.string.connected));
            readButton.setEnabled(true);
            readButton.setBackground(getResources().getDrawable(R.drawable.buttonstyle));


        });
    }

    public void uiDeviceDisconnected(final BluetoothGatt gatt, final BluetoothDevice device) {
        runOnUiThread((Runnable) () -> {

            mDeviceStatus.setText(getResources().getString(R.string.disconnected));
            mListType = ListType.GATT_SERVICES;

            readButton.setEnabled(false);
            readButton.setBackgroundColor(0xff888888);




        });
    }


    public void uiNewRssiAvailable(final BluetoothGatt gatt, final BluetoothDevice device, final int rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final Toast t = Toast.makeText(PeripheralActivity.this, "Please move closer to device", Toast.LENGTH_SHORT);

                if (rssi < -75 || rssi > 75) {
                    mDeviceRssiView.setBackgroundColor(Color.parseColor(RED));
                    mDeviceRSSI = rssi + " db";
                    mDeviceRssiView.setText(mDeviceRSSI);
                    t.show();
                } else if (rssi < -65 || rssi > 65) {
                    mDeviceRssiView.setBackgroundColor(Color.parseColor(AMBER));
                    mDeviceRSSI = rssi + " db";
                    mDeviceRssiView.setText(mDeviceRSSI);
                    t.cancel();
                } else if (rssi > -65 || rssi < 65) {
                    mDeviceRssiView.setBackgroundColor(Color.parseColor(GREEN));
                    mDeviceRSSI = rssi + " db";
                    mDeviceRssiView.setText(mDeviceRSSI);
                    t.cancel();
                }


            }
        });
    }

    public void uiAvailableServices(final BluetoothGatt gatt, final BluetoothDevice device, final List<BluetoothGattService> services) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListType = ListType.GATT_SERVICES;
                for (BluetoothGattService service : mBleWrapper.getCachedServices()) {
                    String uuid = service.getUuid().toString().toLowerCase(Locale.getDefault());
                    String name = BleNamesResolver.resolveServiceName(uuid);
                    if (uuid.equalsIgnoreCase("d973f2f0-b19e-11e2-9e96-0800200c9a66")) {
                        serviceCall = service;

                        readButton.setEnabled(true);
                        readButton.setBackground(getResources().getDrawable(R.drawable.buttonstyle));

                    } else {
                        readButton.setEnabled(false);
                        readButton.setBackgroundColor(0xff888888);

                    }
                }
            }
        });
    }

    public void uiCharacteristicForService(final BluetoothGatt gatt, final BluetoothDevice device, final BluetoothGattService service, final List<BluetoothGattCharacteristic> chars) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mListType = ListType.GATT_CHARACTERISTICS;
                for (BluetoothGattCharacteristic ch : chars) {
                    String blech = BleNamesResolver.resolveCharacteristicName(ch.getUuid().toString().toLowerCase(Locale.getDefault()));
                    if (blech.equalsIgnoreCase("UDPM Notify")) {

                        mBleWrapper.setNotificationForCharacteristic(ch, true);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                    } else {

                    }
                    if (blech.equalsIgnoreCase("UDPM Write")) {

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String persisttime = df.format(c.getTime());
                        btChar = ch;
                        if (statusvalue == 0) {
                            readButton.setBackgroundColor(Color.parseColor("#ef4444"));
                            writeData(ch, true);

                        }

                    }
                }
            }
        });
    }

    private void writeData(final BluetoothGattCharacteristic blegattch, final boolean first) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pktSend == null) {
                    //log.info("Sending InfoRequest data");
                    firstPacket = first;
                    fetchInfoRequestParams(blegattch);
                }
            }
        });
    }

    /*Used to send request to erase packet with the condition ,same packet won't  send again*/
    private void eraseData(final BluetoothGattCharacteristic blegattch, final boolean first) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!sentItemNamesList.containsAll(selectedItemName)) {

                    if (selectedItemName.contains("Brown-Out") & !sentItemNamesList.contains("Brown-Out")) {
                        byte[] brownOutRequest = Utility.brownOutErasePkt();
                        pktSend = "Brown-Out";
                        mBleWrapper.writeDataToCharacteristic(blegattch, brownOutRequest, "vt", true);
                        sentItemNamesList.add("Brown-Out");
                    } else if (selectedItemName.contains("Power-On") & !sentItemNamesList.contains("Power-On")) {
                        byte[] brownInRequest = Utility.brownInErasePkt();
                        pktSend = "Power-On";
                        firstPacket = first;

                        mBleWrapper.writeDataToCharacteristic(blegattch, brownInRequest, "Power-On", true);
                        sentItemNamesList.add("Power-On");
                    } else if (selectedItemName.contains("Overload Cut-off") & !sentItemNamesList.contains("Overload Cut-off")) {
                        byte[] overloadRequest = Utility.overloadErasePkt();
                        pktSend = "Overload Cut-off";
                        firstPacket = first;

                        mBleWrapper.writeDataToCharacteristic(blegattch, overloadRequest, "Overload Cut-off", true);
                        sentItemNamesList.add("Overload Cut-off");
                    } else if (selectedItemName.contains("Immediate Cut-off") & !sentItemNamesList.contains("Immediate Cut-off")) {
                        byte[] immediatecutoffRequest = Utility.immediateCountErasePkt();
                        firstPacket = first;
                        pktSend = "Immediate Cut-off";

                        mBleWrapper.writeDataToCharacteristic(blegattch, immediatecutoffRequest, "Immediate Cut-off", true);
                        sentItemNamesList.add("Immediate Cut-off");
                    } else if (selectedItemName.contains("Reset") & !sentItemNamesList.contains("Reset")) {
                        byte[] resetRequest = Utility.resetCountErasePkt();
                        firstPacket = first;
                        pktSend = "Reset";

                        mBleWrapper.writeDataToCharacteristic(blegattch, resetRequest, "Reset", true);
                        sentItemNamesList.add("Reset");
                    } else if (selectedItemName.contains("Cumulative Energy") & !sentItemNamesList.contains("Cumulative Energy")) {
                        firstPacket = first;
                        byte[] EnegryRequest = Utility.enegryErasePkt();
                        pktSend = "Cumulative Energy";

                        mBleWrapper.writeDataToCharacteristic(blegattch, EnegryRequest, "Cumulative Energy", true);
                        sentItemNamesList.add("Cumulative Energy");
                    }
                }
            }
        });
    }

    private void fetchInfoRequestParams(BluetoothGattCharacteristic blegattch) {


        byte[] infoRequest = Utility.formUDPMInfoPkt();
        pktSend = "infoRequest";
        firstPacket = true;

        /*************************************************write UDPM info**********************************/
        try {
            breakLoop = false;
            BleWrapper.numOfPacketsSent = 0;
            PeripheralActivity.numOfPacketsInError = 0;
            PeripheralActivity.numOfPacketsReceived = 0;
            PeripheralActivity.numOfNoReplyPackets = 0;
            PeripheralActivity.sendSeqNum = 0;
            PeripheralActivity.rcvSeqNum = 0;
            errorPktHMap.clear();

            mBleWrapper.writeDataToCharacteristic(blegattch, infoRequest, pktSend, firstPacket);
            numofdebugdata = -1;
            numofdailyusgeydata = -1;
            int retryCnt = 20;
            while (true) {

                Thread.sleep(150);
                if (numofdebugdata != -1 || breakLoop == true || retryCnt == 0) {
                    break;
                }
                retryCnt--;
            }
            if (retryCnt == 0 && numofdebugdata == -1) {
                numOfNoReplyPackets++;

                // showMesg("No reply for OGH Info packet. Please retry");
                return;
            } else {
                // showMesg("Reply obtained for OGH Info packet");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fetchdebugRequestParams(BluetoothGattCharacteristic blegattch, boolean first) {

        dataSendingLoop = 0;
        breakLoop = false;

        //   ************************************************write UDPM DEBUG********************************
        int totalLoop = (int) numofdebugdata / NUM_DEBUG_DATA_IN_PKT;
        int totalmod = (int) numofdebugdata % NUM_DEBUG_DATA_IN_PKT;
        if (totalmod != 0) {
            totalLoop++;
        }

        if (numofdebugdata < NUM_DEBUG_DATA_IN_PKT)
            totalLoop = 1;
        boolean firstPacketForDebugDataSendingInLoop = true;
        int dcnt = 0;
        while (dataSendingLoop < totalLoop) {
            pktSend = "debug";
            firstPacket = first;
            breakLoop = false;
            if (firstPacketForDebugDataSendingInLoop == false)
                //dcnt = dataSendingLoop * NUM_DATA_IN_PKT + 1;
                dcnt = dataSendingLoop * NUM_DEBUG_DATA_IN_PKT;
            byte[] debugPkt = Utility.formUDPMDebugPkt(dcnt);
            debugdataReceived = false;
            mBleWrapper.writeDataToCharacteristic(blegattch, debugPkt, pktSend, firstPacket);
            int retryCnt = 20;
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (debugdataReceived == true || breakLoop == true || retryCnt == 0) {
                    break;
                }
                retryCnt--;
            }
            if (retryCnt == 0 && debugdataReceived == false) {
                numOfNoReplyPackets++;
            }
            dataSendingLoop++;
            firstPacketForDebugDataSendingInLoop = false;
        }
    }



    public void uiCharacteristicsDetails(final BluetoothGatt gatt, final BluetoothDevice device, final BluetoothGattService service, final BluetoothGattCharacteristic characteristic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListType = ListType.GATT_CHARACTERISTIC_DETAILS;
                //log.debug("characteristic :" + characteristic);
            }
        });
    }

    public void uiNewValueForCharacteristic(final BluetoothGatt gatt, final BluetoothDevice device, final BluetoothGattService service, final BluetoothGattCharacteristic characteristic, final String strValue, final int intValue, final byte[] rawValue, final String timestamp) {
    }

    private void accumalateArray(byte[] byteArrayPart) {
        // TODO Auto-generated method stub
        int startAt = 0;
        if (firstPacket == true) { // store in a new array
            completeDataArray = new byte[byteArrayPart.length];
            System.arraycopy(byteArrayPart, 0, completeDataArray, startAt, byteArrayPart.length);
        } else {
            byte[] newCompleteDataArray = new byte[completeDataArray.length + byteArrayPart.length]; //declare a new array to hold existing data and incoming data
            System.arraycopy(completeDataArray, 0, newCompleteDataArray, startAt, completeDataArray.length); // copy existing data to new array
            System.arraycopy(byteArrayPart, 0, newCompleteDataArray, completeDataArray.length, byteArrayPart.length); // append incoming data
            completeDataArray = new byte[completeDataArray.length + byteArrayPart.length]; // declare new array to hold all the accumulated data
            System.arraycopy(newCompleteDataArray, 0, completeDataArray, 0, newCompleteDataArray.length); // copy new array to global data
        }
    }

    int getNumOfPackets(byte[] pktFromOGH) {
        int numOfPkt = -1;
        if (pktFromOGH == null) {
            return numOfPkt;
        }
        if (pktFromOGH.length < OGH_PACKET_DATA_LENGTH_INDEX + 1) {
            return numOfPkt;
        }
        int pktLength = pktFromOGH[OGH_PACKET_DATA_LENGTH_INDEX];
        numOfPkt = pktLength / UDPM_PACKET_SIZE;
        if (pktLength % UDPM_PACKET_SIZE != 0) {
            numOfPkt++;
        }
        return numOfPkt;
    }

    public void uiSuccessfulWrite(final BluetoothGatt gatt, final BluetoothDevice device, final BluetoothGattService service, final BluetoothGattCharacteristic ch, final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    public void uiFailedWrite(final BluetoothGatt gatt,
                              final BluetoothDevice device,
                              final BluetoothGattService service,
                              final BluetoothGattCharacteristic ch,
                              final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //  Toast.makeText(getApplicationContext(), "Writing to " + description + " FAILED!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void uiGotNotification(final BluetoothGatt gatt, final BluetoothDevice device, final BluetoothGattService service, final BluetoothGattCharacteristic ch) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // at this moment we only need to send this "signal" do characteristic's details view
                //   mCharDetailsAdapter.setNotificationEnabledForService(ch);
            }
        });
    }

    @Override
    public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
        // no need to handle that in this Activity (here, we are not scanning)
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.changeLocale();
        setContentView(R.layout.activity_peripheral);
        pktSend = null;
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle((getResources().getString(R.string.app_name)));
        connectViewsVariables();
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        final Intent intent = getIntent();
        in = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceRSSI = intent.getIntExtra(EXTRAS_DEVICE_RSSI, 0) + "";
        int rssi = intent.getIntExtra(EXTRAS_DEVICE_RSSI, 0);
        if (rssi < -75 || rssi > 75) {
            mDeviceRssiView.setBackgroundColor(Color.parseColor(RED));
            mDeviceRSSI = rssi + " db";
            mDeviceRssiView.setText(mDeviceRSSI);

        } else if (rssi < -65 || rssi > 65) {
            mDeviceRssiView.setBackgroundColor(Color.parseColor(AMBER));
            mDeviceRSSI = rssi + " db";
            mDeviceRssiView.setText(mDeviceRSSI);

        } else if (rssi > -65 || rssi < 65) {
            mDeviceRssiView.setBackgroundColor(Color.parseColor(GREEN));
            mDeviceRSSI = rssi + " db";
            mDeviceRssiView.setText(mDeviceRSSI);
        }

        sharedpreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String devicename = sharedpreferences.getString(UDPMNAME, "");
        final String deviceID = sharedpreferences.getString(UDPMIDNAME, "");
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        if (mDeviceName.equals(deviceID)) {
            mDeviceNameView.setText(devicename);
            //getSupportActionBar().setTitle(devicename);
        } else {
            mDeviceNameView.setText(mDeviceName);
            //getSupportActionBar().setTitle(mDeviceName);
        }
        //hp = DatabaseHelper.getInstance(PeripheralActivity.this);
        String deviceVersionStr = Build.VERSION.RELEASE;
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        alertDialogshowMessage = new AlertDialog.Builder(PeripheralActivity.this).create();
        readButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                /*showDeviceswithImage(deviceID,"1");*/
                statusvalue = 0;
                mBleWrapper.getCharacteristicsForService(serviceCall);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                StartTime = df.format(c.getTime());

            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //  client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.changeLocale();
        if (mBleWrapper == null) mBleWrapper = new BleWrapper(this, this);
        mBleWrapper.initialize();
        if (mBleWrapper.initialize() == false) {
            finish();
        }
        mListType = ListType.GATT_SERVICES;
        mDeviceStatus.setText("connecting ...");
        mBleWrapper.connect(mDeviceAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBleWrapper.stopMonitoringRssiValue();
        mBleWrapper.diconnect();
        mBleWrapper.close();
    }

    public void setLog(int value) {
    }

    private void connectViewsVariables() {
        mDeviceNameView = (TextView) findViewById(R.id.peripheral_name);
        /*mDeviceAddressView = (TextView) findViewById(R.id.peripheral_address);*/
        mDeviceRssiView = (TextView) findViewById(R.id.peripheral_rssi);
        mDeviceStatus = (TextView) findViewById(R.id.peripheral_status);
        readButton = (Button) findViewById(R.id.button);

        SharedPreferences settings = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        boolean isChecked = settings.getBoolean(CHECKBOX, false);
        System.out.println("isChecked" + isChecked);
        if (isChecked == true) {

        } else {
            System.out.println("isChecked2" + ActivityBT_Reading.synctoServer);
        }



    }

    @Override
    public void onBackPressed() {
        Intent nextactivity = new Intent(this, ActivityBT_Reading.class);
        nextactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(nextactivity);
        finish();
    }

    private String getErrMsg(int code) {
        String errMessage = null;
        switch (code) {
            case ERR_INVALID_RESPONSE:
                errMessage = "ERR_INVALID_RESPONSE";
                break;
            case ERR_TIME_FAIL:
                errMessage = "ERR_TIME_FAIL";
                break;
            case ERR_DATE_FAIL:
                errMessage = "ERR_DATE_FAIL";
                break;
            case ERR_LENGTH:
                errMessage = "ERR_LENGTH";
                break;
            case ERR_FLASH_READ:
                errMessage = "ERR_FLASH_READ";
                break;
            case ERR_FLASH_WRITE:
                errMessage = "ERR_FLASH_WRITE";
                break;
            case ERR_INVALID_RANGE:
                errMessage = "ERR_INVALID_RANGE";
                break;
            case ERR_FRAM_READ:
                errMessage = "ERR_FRAM_READ";
                break;
            case ERR_FRAM_WRITE:
                errMessage = "ERR_FRAM_WRITE";
                break;
            case ERR_INVALID_OFFSET:
                errMessage = "ERR_INVALID_OFFSET";
                break;
            case ERR_INCOMPLETE_PKT:
                errMessage = "ERR_INCOMPLETE_PKT";
                break;
            default:
                errMessage = "Unknown error";
        }
        return errMessage;
    }

    public enum ListType {
        GATT_SERVICES,
        GATT_CHARACTERISTICS,
        GATT_CHARACTERISTIC_DETAILS
    }

    class ParseDataFromDevice extends AsyncTask<HashMap, Integer, String> implements PktIndexInterface {
        String pktSendpase = null;

        protected void onPreExecute() {
            //Show progress Dialog here
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        cd = new ConnectionDetector(getApplicationContext());
                        isInternetPresent = cd.isConnectingToInternet();
                        /*if (isInternetPresent) {
                            if (pktSendpase.equalsIgnoreCase("infoRequest")) {
                                showDeviceswithImage(mDeviceName, "1");
                            }
                        } else {*/
                        if (pktSendpase.equalsIgnoreCase("infoRequest")) {


                            UtilDB db = new UtilDB(PeripheralActivity.this);

                            //String refid = db.checkUdpmIdAvailabilityAndInsert(deviceNodeid);
                            //udpmInfoList = db.getAllUdpmInfoRecid(Integer.parseInt(refid));

                            //if (udpmInfoList.isEmpty()) {
                            //} else {
                                //--------------------------------------------start oghinfo json ---------------------------------------------------------
                                //for (Info infoValues : udpmInfoList) {

                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = preferences.edit();
                                    //editor.putString("dcMeterReading", String.valueOf(infoValues.getInfoEnergy()));
                                    editor.putString("dcMeterReading", String.valueOf(iEnergy));
                                    editor.putString("dcMeterId", deviceNodeid);
                                    editor.putString("timestamp_dc", StartTime);
                                    editor.apply();

                                    Intent intent = new Intent(PeripheralActivity.this, DcMeterReading.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    /*intent.putExtra("udpmid", deviceNodeid);
                                    intent.putExtra("config", "");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
                                    startActivity(intent);
                                    //startActivityForResult(intent, 6);


                                    //--------------------------------------------End oghinfo json ---------------------------------------------------------
                                //}

                            //}


                            finish();
                        }
                        // }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private int getFromByteArray(byte[] b, int offset, int length) { //LITTLE ENDIAN
            Long retVal = 0L;
            if (b.length < offset + length) {
                return -1;
            } else {
                long shift = (length - 1) * 8;
                while (length > 0) {
                    Long retVal1 = (long) (b[offset + length - 1] & 0xFF) << shift;
                    length--;
                    retVal = retVal | retVal1;
                    shift -= 8;
                }
            }
            return retVal.intValue();
        }

        @Override
        protected String doInBackground(HashMap... params) {
            pktSendpase = (String) params[0].get("PKT_SEND");
            byte[] data = (byte[]) params[0].get("DATA");
            String value = (String) params[0].get("value");
            StringBuilder packetHeader = new StringBuilder();
            int count = 0;
            for (byte b : completeDataArray) {
                packetHeader.append(String.format("%02X ", b));
                count++;
                if (count == 6)
                    break;
            }
            if (pktSendpase.equalsIgnoreCase("infoRequest")) {
                Info infoparam = (Info) params[0].get("POJO");
                parseudpmInfofeed(data, infoparam, packetHeader.toString());
            }
            publishProgress(Integer.parseInt(String.valueOf(numOfPacketsReceived)));
            return null;
        }

        public void parseudpmInfofeed(byte[] b, Info info, String packetHeader) {
            if (b.length < UDPM_INFO_PACKET_SIZE) {
                return;
            }
            int oghswversion = getFromByteArray(b, UDPM_INFO_UDPMSWVERSION, UDPM_INFO_UDPMSWVERSION_LENGTH);
            info.setInfoNodeid(Integer.toString(oghswversion));
            String pcbno = Integer.toString(getFromByteArray(b, UDPM_INFO_PCBNO, UDPM_INFO__PCBNO_LENGTH));
            info.setInfoPcbno(pcbno);
            StringBuilder packetrawdata = new StringBuilder();
            for (byte rawdata : b) {
                packetrawdata.append(String.format("%02X ", rawdata));
            }
            info.setInfoPktinfo(packetrawdata.toString());
            //log.debug(TAG + ": " + "Info  packet:" + "Device id :" + mDeviceName + " " + packetrawdata);

            info.setInfoPktHeader(packetHeader);
            long timestamp = getFromByteArray(b, UDPM_INFO_DATEOFMANUFACTURING, UDPM_INFO_DATEOFMANUFACTURING_LENGTH);
            long year = (timestamp) & INFO_MANUFACTURE_YEAR_MASK_V1;
            long month = (timestamp >> 7) & INFO_MANUFACTURE_MONTH_MASK_V1;
            long date = (timestamp >> 11) & INFO_MANUFACTURE_DATE_MASK_V1;
            String manufDate = String.valueOf(date);
            if (manufDate.length() < 2) {
                manufDate = "0" + manufDate;
            }
            String manufmonth = String.valueOf(month);
            if (manufmonth.length() < 2) {
                manufmonth = "0" + manufmonth;
            }
            //@bug fixed  in this version UDV 0.11 for date formation
            //String dateofmanufacture = "20" + String.valueOf(year) + "-" + manufDate + "-" + manufmonth;
            String dateofmanufacture = "20" + String.valueOf(year) + "-" + manufmonth + "-" + manufDate;
            info.setInfoDateofmanufacturing(dateofmanufacture);
            String frimware = Integer.toString(getFromByteArray(b, UDPM_INFO_FIRMWAREVERSION, UDPM_INFO_FIRMWAREVERSION_LENGTH));
            info.setInfoFirmwareVersion(frimware);
            int enegry = getFromByteArray(b, UDPM_INFO_ENEGRY, UDPM_INFO_ENEGRY_LENGTH);
            iEnergy = enegry;
            Log.i("PeripheralActivity", "UDPM_INFO_ENEGRY ==>> " + iEnergy);
            if(iEnergy <= 0)
            {
                //iEnergy = Integer.parseInt(UtilAppCommon.in.PREV_KWH_CYCLE1);
                iEnergy = 0;
            }
           /* iEnergy=enegry=enegry/100;
            Toast.makeText(PeripheralActivity.this, "UDPM_ENEGRY_READING AFTER DIVIDING BY 100 ==>> " + iEnergy, Toast.LENGTH_SHORT).show();
            Log.i("PeripheralActivity", "UDPM_INFO_ENEGRY AFTER DIVIDING BY 100 ==>> " + iEnergy);*/

            info.setInfoEnergy(enegry);
            int brownOut = getFromByteArray(b, UDPM_INFO_BROWNOUT, UDPM_INFO_BROWNOUT_LENGTH);
            info.setInfoBrownoutcount(brownOut);
            int brownIn = getFromByteArray(b, UDPM_INFO_BROWNIN, UDPM_INFO_BROWNIN_LENGTH);
            info.setInfoBrownincount(brownIn);
            int OLCOs = getFromByteArray(b, UDPM_INFO_OLVCO, UDPM_INFO_OLVCOS_LENGTH);
            info.setInfoLvcocount(OLCOs);
            int imCos = getFromByteArray(b, UDPM_INFO_IMCOS, UDPM_INFO_IMCOS_LENGTH);
            info.setInfoImcoscount(imCos);
            int resets = getFromByteArray(b, UDPM_INFO_RESET, UDPM_INFO_RESET_LENGTH);
            info.setInfoResetscount(resets);
            long currentTimeStamplong = getFromByteArray(b, UDPM_INFO_CURRENTTIMESTAMP, UDPM_INFO_CURRENTTIMESTAMP_LENGTH);
            long currentTimeMin = (currentTimeStamplong) & INFO_CYRRENT_TIME_MIN_MASK_V1;
            long currentTimeHour = (currentTimeStamplong >> 6) & INFO_CYRRENT_TIME_HOUR_MASK_V1;
            long currentTimeYear = (currentTimeStamplong >> 11) & INFO_CYRRENT_TIME_YEAR_MASK_V1;
            long currentTimeMonth = (currentTimeStamplong >> 18) & INFO_CYRRENT_TIME_MONTH_MASK_V1;
            long currentTimeDate = (currentTimeStamplong >> 22) & INFO_CYRRENT_TIME_DATE_MASK_V1;
            String currentDate = String.valueOf(currentTimeDate);
            if (currentDate.length() < 2) {
                currentDate = "0" + currentDate;
            }
            String currentMonth = String.valueOf(currentTimeMonth);
            if (currentMonth.length() < 2) {
                currentMonth = "0" + currentMonth;
            }
            String currentHour = String.valueOf(currentTimeHour);
            if (currentHour.length() < 2) {
                currentHour = "0" + currentHour;
            }
            String currenMin = String.valueOf(currentTimeMin);
            if (currenMin.length() < 2) {
                currenMin = "0" + currenMin;
            }
            String currentTimeStamp = "20" + String.valueOf(currentTimeYear) + "-" + currentMonth + "-" + currentDate + " " + currentHour + ":" + currenMin;
            info.setInfoCurrenttimestamp(currentTimeStamp);
            currentTimeStamplive = currentTimeStamp;
            int dailyUsageLength = getFromByteArray(b, UDPM_INFO_DAILYUSAGE, UDPM_INFO_DAILYUSAGE_LENGTH);
            info.setInfoDailyusageentycount(dailyUsageLength);
            numofdailyusgeydata = dailyUsageLength;
            int debugEntries = getFromByteArray(b, UDPM_INFO_DEBUGENTRIES, UDPM_INFO_DEBUGENTRIES_LENGTH);
            info.setInfodebugentriescount(debugEntries);
            numofdebugdata = debugEntries;
            info.setInfoserverdatasyncstatus(1);
            deviceNodeid = Integer.toString(oghswversion);
            String refid = null;
            try {
                //refid = hp.checkUdpmIdAvailabilityAndInsert(deviceNodeid);
                //info.setInfodeviceRefid(Integer.parseInt(refid));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //hp.addUdpmInfo(info);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 6:
                    //Intent in = new Intent(getApplicationContext(), ActivityBT_Reading.class);
                    //Intent in = getIntent();
                    //setResult(RESULT_OK, in);
                    //startActivity(in);
                    finish();
                    break;
            }
        }
    }
}