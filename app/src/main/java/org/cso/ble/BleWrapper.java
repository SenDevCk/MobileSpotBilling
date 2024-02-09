package org.cso.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import org.cso.MSBModel.PktIndexInterface;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BleWrapper implements PktIndexInterface {



    private static final int RSSI_UPDATE_TIME_INTERVAL = 1500; // 1.5 seconds
    private static final BleWrapperUiCallbacks NULL_CALLBACK = new BleWrapperUiCallbacks.Null();
    public static long numOfPacketsSent = 0;
    public static long sendSeqNum = 0;
    public static boolean firstPacket = true;
    String currentpacket = "";
    private Activity mParent = null;
    private boolean mConnected = false;
    private String mDeviceAddress = "";
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mBluetoothDevice = null;
    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothGattService mBluetoothSelectedService = null;
    private List<BluetoothGattService> mBluetoothGattServices = null;
    private Handler mTimerHandler = new Handler();
    private boolean mTimerEnabled = false;


    /* callback object through which we are returning results to the caller */
    private BleWrapperUiCallbacks mUiCallback = null;
    /* callbacks called for any action on particular Ble Device */
    private final BluetoothGattCallback mBleCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnected = true;
                mUiCallback.uiDeviceConnected(mBluetoothGatt, mBluetoothDevice);
                // now we can start talking with the device, e.g.
                mBluetoothGatt.readRemoteRssi();
                // response will be delivered to callback object!
                // in our case we would also like automatically to call for services discovery
                startServicesDiscovery();
                // and we also want to get RSSI value to be updated periodically
                startMonitoringRssiValue();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnected = false;
                mUiCallback.uiDeviceDisconnected(mBluetoothGatt, mBluetoothDevice);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // now, when services discovery is finished, we can call getServices() for Gatt
                getSupportedServices();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // we got response regarding our request to fetch characteristic value
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // and it success, so we can get the value
                getCharacteristicValue(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // characteristic's value was updated due to enabled notification, lets get this value
            // the value itself will be reported to the UI inside getCharacteristicValue
            getCharacteristicValue(characteristic);
            // also, notify UI that notification are enabled for particular characteristic
            mUiCallback.uiGotNotification(mBluetoothGatt, mBluetoothDevice, mBluetoothSelectedService, characteristic);
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String deviceName = gatt.getDevice().getName();
            String serviceName = BleNamesResolver.resolveServiceName(characteristic.getService().getUuid().toString().toLowerCase(Locale.getDefault()));
            String charName = BleNamesResolver.resolveCharacteristicName(characteristic.getUuid().toString().toLowerCase(Locale.getDefault()));
            String description = "Device: " + deviceName + " Service: " + serviceName + " Characteristic: " + charName;
            // we got response regarding our request to write new value to the characteristic
            // let see if it failed or not
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mUiCallback.uiSuccessfulWrite(mBluetoothGatt, mBluetoothDevice, mBluetoothSelectedService, characteristic, description);
            } else {
                mUiCallback.uiFailedWrite(mBluetoothGatt, mBluetoothDevice, mBluetoothSelectedService, characteristic, description + " STATUS = " + status);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // we got new value of RSSI of the connection, pass it to the UI
                mUiCallback.uiNewRssiAvailable(mBluetoothGatt, mBluetoothDevice, rssi);
            }
        }

    };
    /* defines callback for scanning results */
    private BluetoothAdapter.LeScanCallback mDeviceFoundCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            mUiCallback.uiDeviceFound(device, rssi, scanRecord);
        }
    };

    /* creates BleWrapper object, set its parent activity and callback object */
    public BleWrapper(Activity parent, BleWrapperUiCallbacks callback) {
        this.mParent = parent;
        mUiCallback = callback;
        if (mUiCallback == null) mUiCallback = NULL_CALLBACK;
    }

    public BluetoothManager getManager() {
        return mBluetoothManager;
    }

    public BluetoothAdapter getAdapter() {
        return mBluetoothAdapter;
    }

    public BluetoothDevice getDevice() {
        return mBluetoothDevice;
    }

    public BluetoothGatt getGatt() {
        return mBluetoothGatt;
    }

    public BluetoothGattService getCachedService() {
        return mBluetoothSelectedService;
    }

    public List<BluetoothGattService> getCachedServices() {
        return mBluetoothGattServices;
    }

    public boolean isConnected() {
        return mConnected;
    }

    /* run test and check if this device has BT and BLE hardware available */
    public boolean checkBleHardwareAvailable() {
        // First check general Bluetooth Hardware:
        // get BluetoothManager...
        final BluetoothManager manager = (BluetoothManager) mParent.getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager == null) return false;
        // .. and then get adapter from manager
        final BluetoothAdapter adapter = manager.getAdapter();
        if (adapter == null) return false;

        // and then check if BT LE is also available
        boolean hasBle = mParent.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        return hasBle;
    }

    /* before any action check if BT is turned ON and enabled for us
     * call this in onResume to be always sure that BT is ON when Your
     * application is put into the foreground */
    public boolean isBtEnabled() {
        final BluetoothManager manager = (BluetoothManager) mParent.getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager == null) return false;

        final BluetoothAdapter adapter = manager.getAdapter();
        if (adapter == null) return false;

        return adapter.isEnabled();
    }

    /* start scanning for BT LE devices around */
    public void startScanning() {
        mBluetoothAdapter.startLeScan(mDeviceFoundCallback);
    }

    /* stops current scanning */
    public void stopScanning() {
        mBluetoothAdapter.stopLeScan(mDeviceFoundCallback);
    }

    /* initialize BLE and get BT Manager & Adapter */
    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mParent.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }

        if (mBluetoothAdapter == null) mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    /* connect to the device with specified address */
    public boolean connect(final String deviceAddress) {
        if (mBluetoothAdapter == null || deviceAddress == null) return false;
        mDeviceAddress = deviceAddress;

        // check if we need to connect from scratch or just reconnect to previous device
        if (mBluetoothGatt != null && mBluetoothGatt.getDevice().getAddress().equals(deviceAddress)) {
            // just reconnect
            return mBluetoothGatt.connect();
        } else {
            // connect from scratch
            // get BluetoothDevice object for specified address
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
            if (mBluetoothDevice == null) {
                // we got wrong address - that device is not available!
                return false;
            }
            // connect with remote device
            mBluetoothGatt = mBluetoothDevice.connectGatt(mParent, false, mBleCallback);
        }
        return true;
    }

    /* disconnect the device. It is still possible to reconnect to it later with this Gatt client */
    public void diconnect() {
        if (mBluetoothGatt != null) mBluetoothGatt.disconnect();
        mUiCallback.uiDeviceDisconnected(mBluetoothGatt, mBluetoothDevice);
    }

    /* close GATT client completely */
    public void close() {
        if (mBluetoothGatt != null) mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /* request new RSSi value for the connection*/
    public void readPeriodicalyRssiValue(final boolean repeat) {
        mTimerEnabled = repeat;
        // check if we should stop checking RSSI value
        if (mConnected == false || mBluetoothGatt == null || mTimerEnabled == false) {
            mTimerEnabled = false;
            return;
        }

        mTimerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothGatt == null ||
                        mBluetoothAdapter == null ||
                        mConnected == false) {
                    mTimerEnabled = false;
                    return;
                }

                // request RSSI value
                mBluetoothGatt.readRemoteRssi();
                // add call it once more in the future
                readPeriodicalyRssiValue(mTimerEnabled);
            }
        }, RSSI_UPDATE_TIME_INTERVAL);
    }

    /* starts monitoring RSSI value */
    public void startMonitoringRssiValue() {
        readPeriodicalyRssiValue(true);
    }

    /* stops monitoring of RSSI value */
    public void stopMonitoringRssiValue() {
        readPeriodicalyRssiValue(false);
    }

    /* request to discover all services available on the remote devices
     * results are delivered through callback object */
    public void startServicesDiscovery() {
        if (mBluetoothGatt != null) mBluetoothGatt.discoverServices();
    }

    /* gets services and calls UI callback to handle them
     * before calling getServices() make sure service discovery is finished! */
    public void getSupportedServices() {
        if (mBluetoothGattServices != null && mBluetoothGattServices.size() > 0)
            mBluetoothGattServices.clear();
        // keep reference to all services in local array:
        if (mBluetoothGatt != null) mBluetoothGattServices = mBluetoothGatt.getServices();

        mUiCallback.uiAvailableServices(mBluetoothGatt, mBluetoothDevice, mBluetoothGattServices);
    }

    /* get all characteristic for particular service and pass them to the UI callback */
    public void getCharacteristicsForService(final BluetoothGattService service) {
        if (service == null) return;
        List<BluetoothGattCharacteristic> chars = null;

        chars = service.getCharacteristics();
        mUiCallback.uiCharacteristicForService(mBluetoothGatt, mBluetoothDevice, service, chars);
        // keep reference to the last selected service
        mBluetoothSelectedService = service;
    }

    public int getValueFormat(BluetoothGattCharacteristic ch) {
        int properties = ch.getProperties();

        if ((BluetoothGattCharacteristic.FORMAT_FLOAT & properties) != 0)
            return BluetoothGattCharacteristic.FORMAT_FLOAT;
        if ((BluetoothGattCharacteristic.FORMAT_SFLOAT & properties) != 0)
            return BluetoothGattCharacteristic.FORMAT_SFLOAT;
        if ((BluetoothGattCharacteristic.FORMAT_SINT16 & properties) != 0)
            return BluetoothGattCharacteristic.FORMAT_SINT16;
        if ((BluetoothGattCharacteristic.FORMAT_SINT32 & properties) != 0)
            return BluetoothGattCharacteristic.FORMAT_SINT32;
        if ((BluetoothGattCharacteristic.FORMAT_SINT8 & properties) != 0)
            return BluetoothGattCharacteristic.FORMAT_SINT8;
        if ((BluetoothGattCharacteristic.FORMAT_UINT16 & properties) != 0)
            return BluetoothGattCharacteristic.FORMAT_UINT16;
        if ((BluetoothGattCharacteristic.FORMAT_UINT32 & properties) != 0)
            return BluetoothGattCharacteristic.FORMAT_UINT32;
        if ((BluetoothGattCharacteristic.FORMAT_UINT8 & properties) != 0)
            return BluetoothGattCharacteristic.FORMAT_UINT8;

        return 0;
    }

    /* request to fetch newest value stored on the remote device for particular characteristic */
    public void requestCharacteristicValue(BluetoothGattCharacteristic ch) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) return;

        mBluetoothGatt.readCharacteristic(ch);
        // new value available will be notified in Callback Object
    }

    /* get characteristic's value (and parse it for some types of characteristics)
     * before calling this You should always download the value by calling requestCharacteristicValue() */
    public void getCharacteristicValue(BluetoothGattCharacteristic ch) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null || ch == null) return;
        byte[] rawValue = ch.getValue();
        if (rawValue.length == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : rawValue) {
            sb.append(String.format("%02X ", b));
            //sb.append(",");
            //7E 04 25 01 01 1F B0 86 01 00 B0 86 01 00 90 19 20 00 00 00 00 00 00 01 00 00 00 00 00 00 00 CA 82 4C 01 02 7F
            //7E 04 1A 01 04 14 E4 12 00 00 01 00 00 00 00 00 00 01 00 00 00 00 00 00 00 41
        }
        mUiCallback.valueForCharacteristic(rawValue, firstPacket);

    }

    public void writeDataToCharacteristic(final BluetoothGattCharacteristic ch, final byte[] dataToWrite, String packettype, boolean status) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null || ch == null) return;

        // first set it locally...
        currentpacket = packettype;
        firstPacket = status;
        ch.setValue(dataToWrite);
        // ... and then "commit" changes to the peripheral
        mBluetoothGatt.writeCharacteristic(ch);
        BleWrapper.numOfPacketsSent++;
        sendSeqNum++;

    }

    /* enables/disables notification for characteristic */
    public void setNotificationForCharacteristic(BluetoothGattCharacteristic ch, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) return;

        boolean success = mBluetoothGatt.setCharacteristicNotification(ch, enabled);
        if (!success) {
            Log.e("------", "Seting proper notification status for characteristic failed!");
        } else {

        }
        System.out.println("ch value :" + ch + "," + UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));

        BluetoothGattDescriptor descriptor = ch.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        System.out.println("ch value :" + (UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")));
        System.out.println("descriptor" + descriptor.toString());
        if (descriptor != null) {
            byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            descriptor.setValue(val);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }
}
