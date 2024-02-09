package org.cso.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import org.cso.MobileSpotBilling.R;

import java.util.ArrayList;

public class DeviceListAdapter extends BaseAdapter {
    public static final String UDPMIDNAME = "udpmidname";
    public static final String UDPMNAME = "name";
    public static final String CONFIG = "config";
    public static final String MYPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    private ArrayList<BluetoothDevice> mDevices;
    private ArrayList<byte[]> mRecords;
    private ArrayList<Integer> mRSSIs;
    private LayoutInflater mInflater;
    private Context context;

    public DeviceListAdapter(Activity par, Context cn) {
        super();
        mDevices = new ArrayList<BluetoothDevice>();
        mRecords = new ArrayList<byte[]>();
        mRSSIs = new ArrayList<Integer>();
        mInflater = par.getLayoutInflater();
        context = cn;
    }

    public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (mDevices.contains(device) == false) {
            mDevices.add(device);
            mRSSIs.add(rssi);
            mRecords.add(scanRecord);

        }
    }

    public DeviceListAdapter(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (mDevices.contains(device) == false) {
            mDevices.add(device);
            mRSSIs.add(rssi);
            mRecords.add(scanRecord);
        }
    }

    /**/
    public BluetoothDevice getDevice(int index) {
        return mDevices.get(index);
    }

    public int getRssi(int index) {
        return mRSSIs.get(index);
    }

    public void clearList() {
        mDevices.clear();
        mRSSIs.clear();
        mRecords.clear();
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return getDevice(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get already available view or create new if necessary
        FieldReferences fields;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_scanning_item, null);
            fields = new FieldReferences();

            fields.deviceName = (TextView) convertView.findViewById(R.id.deviceName);
            fields.deviceRssi = (TextView) convertView.findViewById(R.id.deviceRssi);
            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }

        // set proper values into the view
        BluetoothDevice device = mDevices.get(position);
        int rssi = mRSSIs.get(position);
        String rssiString = (rssi == 0) ? "N/A" : rssi + " db";
        String name = device.getName();
        String address = device.getAddress();
        System.out.println("device"+device.getName()+","+device.getUuids());
     //   if (device.getUuids().equals("d973f2f0-b19e-11e2-9e96-0800200c9a66")) {

            if (name == null || name.length() <= 0) name = "Unknown Device";



        sharedpreferences = context.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String devicename = sharedpreferences.getString(UDPMNAME,"");
        String deviceID = sharedpreferences.getString(UDPMIDNAME, "");

        if (deviceID.equals(name)) {
            fields.deviceName.setText(devicename);

        }else{
            fields.deviceName.setText(name);

        }



        fields.deviceRssi.setText(rssiString);

        return convertView;
    }

    private class FieldReferences {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
    }
}
