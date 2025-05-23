package org.cso.MobileSpotBilling;


import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.UUID;

import org.cso.MSBUtil.PrintUtilZebra;
import org.cso.MSBUtil.UtilDB;

import com.analogics.thermalAPI.Bluetooth_Printer_2inch_prof_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.epson.eposprint.Builder;
import com.epson.eposprint.Print;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ActvUnbilledListPrinting extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    static final UUID MY_UUID = UUID.randomUUID();
    // static String address = "00:1F:B7:05:44:76";
    ZebraThermal sendDatazebra = null;
    //AnalogicImpact sendData = null;
    EpsonThermal sendDataEpson = null;
//	AnalogicThermal sendDataAnaloginThermal = null;

    String BILL = "";
    // Added on-1.3.2014
    private Context context = this;

    // End add -1.3.2014
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not available.", Toast.LENGTH_LONG)
                    .show();

            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(
                    this,
                    "Bluetooth feature is turned off now...Please turned it on! ",
                    Toast.LENGTH_LONG).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mBluetoothAdapter.enable();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Added on 3.7.2014
        UtilDB dbObj = new UtilDB(getApplicationContext());
        String[] printer = dbObj.GetPrinterInfo();
        if (printer[1]==null) {
            Toast.makeText(this, "No Printer Configured", Toast.LENGTH_LONG)
                    .show();
            finish();
        }
        else if (printer[1].compareToIgnoreCase("Zebra Thermal") == 0) {
            System.out.println("Zebra Thermal "+printer[0]+" "+printer[1]);
            sendDatazebra = new ZebraThermal(printer[0]);
            Thread t = new Thread(sendDatazebra);
            t.run();
            //} else if (printer[1].compareToIgnoreCase("Analogic Impact") == 0) {
            //	sendData = new AnalogicImpact(printer[0]);
            //	Thread t = new Thread(sendData);
            //	t.run();
        } else if (printer[1].compareToIgnoreCase("EPSON Thermal") == 0) {
            System.out.println("EPSON Thermal "+printer[0]+" "+printer[1]);
            sendDataEpson = new EpsonThermal(printer[0]);
            Thread t = new Thread(sendDataEpson);
            t.run();
        } else if (printer[1].compareToIgnoreCase("Analogic Thermal") == 0) {
            AnalogicThermal sendDataAnaloginThermal = new AnalogicThermal(printer[0]);
            Thread t = new Thread(sendDataAnaloginThermal);
            t.run();
        }
       /* else if (printer[1].compareToIgnoreCase("TVS-ENGLISH") == 0) {
            ActvMsgPrinting.TVSPrinter tvsPrinter = new ActvMsgPrinting().new TVSPrinter(printer[0]);
            Thread t = new Thread(tvsPrinter);
            t.run();
        }
        else if (printer[1].compareToIgnoreCase("TVS-HINDI") == 0) {
            ActvMsgPrinting.TVSPrinter tvsPrinter = new ActvMsgPrinting().new TVSPrinter(printer[0]);
            Thread t = new Thread(tvsPrinter);
            t.run();
        }*/
        else {
            Toast.makeText(this, "No Printer Configured", Toast.LENGTH_LONG)
                    .show();
            finish();
            //startActivity(new Intent(getBaseContext(), ActvReport.class));
/*			if(UtilAppCommon.blActyncBtn)
				startActivity(new Intent(getBaseContext(), SyncMobPoleActivity.class));
			else if(UtilAppCommon.inSAPSendMsg.equalsIgnoreCase("1") && !UtilAppCommon.blActyncBtn)
				startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("A"))
				startActivity(new Intent(this, ActvConsumerNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("L"))
				startActivity(new Intent(this, ActvLegacyNbrInput.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("S"))
				startActivity(new Intent(this, ActvSequenceData.class));
			else if(UtilAppCommon.billType.equalsIgnoreCase("M"))
				startActivity(new Intent(this, MeterNbrInput.class));
			else
				startActivity(new Intent(this, ActvBillingOption.class));*/
        }

        // End Added on 1.3.2014

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "ActvUnbilledListPrinting");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart", "ActvUnbilledListPrinting");
    }
    class ZebraThermal extends Thread {

        private BluetoothDevice device = null;
        private BluetoothSocket btSocket = null;
        private OutputStream outStream = null;
        private OutputStreamWriter writer = null;
        private String address = null;
        UtilDB dbObj = new UtilDB(getApplicationContext());

        public ZebraThermal(String address) {
            this.address = address;
            try {
                device = mBluetoothAdapter.getRemoteDevice(address);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "Connected To:" + address,
                    Toast.LENGTH_LONG).show();
        }

        @SuppressWarnings("deprecation")
        public void run() {
            try {
                Toast.makeText(getApplicationContext(), "Sending Data",
                        Toast.LENGTH_LONG).show();

                ZebraPrinterConnection thePrinterConn = new BluetoothPrinterConnection(
                        address);
                thePrinterConn.open();
                // Initialize

                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy'   TIME: 'hh:mm");

                // Open the connection - physical connection is established
                // here.
                ZebraPrinter printer = ZebraPrinterFactory
                        .getInstance(thePrinterConn);
                PrinterLanguage pcLanguage = printer
                        .getPrinterControlLanguage();
                Toast.makeText(getApplicationContext(),
                        "Language: " + pcLanguage, Toast.LENGTH_LONG).show();

                // This example prints "This is a ZPL test." near the top of the
                // label.
                String cpclData = null;
                PrintUtilZebra.LineNo = 0;
                Cursor c = dbObj.unbilledlist();

                cpclData = "! 90 200 200 " + ((c.getCount() * 30) + 100) + " 1\r\n";
                cpclData += "CENTER\r\n";

                //cpclData += "UNDERLINE ON\r\n";
                cpclData += PrintUtilZebra
                        .PrintNext("UnBilled Consumer List   ");
                cpclData += PrintUtilZebra
                        .PrintNext("*******************   ");


                cpclData += "LEFT\r\n";
                cpclData += PrintUtilZebra
                        .PrintNext("Srl   CANumber Legacy Number ");
                int cnt = 0;
                if (c.moveToFirst()) {
                    do {
                        cnt++;
                        cpclData += PrintUtilZebra.PrintNext(String.format(
                                "%s  %s  %s ", cnt, c.getString(0), c.getString(1)));
                    } while (c.moveToNext());
                }


                cpclData += "ENDQR\r\nPRINT\r\n";

                cpclData += "PRINT\r\n";

                thePrinterConn.write(cpclData.getBytes());

                Thread.sleep(500);

                thePrinterConn.close();
                PrintUtilZebra.LineNo = 0;


                //startActivity(new Intent(getBaseContext(), PoleMobileActivity.class));

            } catch (Exception e) {
                // Handle communications error here.
                Toast.makeText(getApplicationContext(), e.toString(),
                        Toast.LENGTH_LONG).show();

            } finally {

                //startActivity(new Intent(getBaseContext(), ActvReport.class));
                finish();
            }
            return;

        }
    }

    class EpsonThermal extends Thread {

        private BluetoothDevice device = null;
        private String address = null;
        UtilDB dbObj = new UtilDB(getApplicationContext());

        public EpsonThermal(String address) {
            this.address = address;
            device = mBluetoothAdapter.getRemoteDevice(address);
            Toast.makeText(getApplicationContext(), "Connected To:" + address,
                    Toast.LENGTH_LONG).show();
        }

        public void run() {
            Print printer = new Print();
            int[] status = new int[1];
            int[] battery = new int[1];
            status[0] = 0;
            battery[0] = 0;
            Cursor c = dbObj.unbilledlist();
            SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy'  TIME:'hh:mm");

            try {
                Toast.makeText(getApplicationContext(), "Sending Data",
                        Toast.LENGTH_LONG).show();
                Builder builder = new Builder("TM-P60", Builder.MODEL_ANK);
                builder.addTextFont(Builder.FONT_A);

                builder.addTextAlign(Builder.ALIGN_CENTER);
                builder.addText("Unbilled Consumer List\n");
                builder.addText("----------------------\n");
                builder.addTextAlign(Builder.ALIGN_LEFT);

                builder.addText("Srl   CANumber Legacy Number\n");


                int cnt = 0;
                if (c.moveToFirst()) {
                    do {
                        cnt++;
                        builder.addText(String.format(
                                "%s   %s   %s   \n", cnt, c.getString(0), c.getString(1)));
                    } while (c.moveToNext());
                }


                builder.addCut(Builder.CUT_FEED);
                // <Send print data>

                printer.openPrinter(Print.DEVTYPE_BLUETOOTH, address,
                        Print.TRUE, Print.PARAM_DEFAULT);
                printer.sendData(builder, 10000, status, battery);

                printer.closePrinter();
				/*startActivity(new Intent(getBaseContext(),
						ActvBillingOption.class));
*/
            } catch (Exception e) {
                // Handle communications error here.
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(),
                        Toast.LENGTH_LONG).show();

            } finally {

                //startActivity(new Intent(getBaseContext(), ActvReport.class));
                finish();
            }
            return;

        }
    }


    class AnalogicThermal extends Thread {
        private String address = null;

        UtilDB dbObj = new UtilDB(getApplicationContext());

        public AnalogicThermal(String address) {
            this.address = address;
        }

        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy' TIME:'hh:mm");

        public void run() {

            try {

                AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();
                conn.openBT(address);

                Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();

                Toast.makeText(getBaseContext(), BILL, Toast.LENGTH_SHORT)
                        .show();

                char lf = 0x0A;
                char cr = 0x0D;
                Cursor c = dbObj.unbilledlist();
                // char dp = 0x1D;
                // char nm = 0x13;

                // ////////Print On Paper Start////////////
                StringBuilder printerdata1 = new StringBuilder();

                //Print part 1


                printerdata1.append(printer.font_Courier_24_VIP(String.format(
                        "     %s\n", "Unbilled Consumer List")));
                printerdata1.append(printer.font_Courier_24_VIP(String.format(
                        "     %s\n", "---------------------")));

                printerdata1.append(printer.font_Courier_24_VIP(String.format(
                        "%s\n", "Srl CANumber LegacyNbr")));

                printerdata1.append(printer.font_Courier_24_VIP(String.format(
                        "%s\n", "----------------------")));

                int cnt = 0;
                if (c.moveToFirst()) {
                    do {
                        cnt++;
                        printerdata1.append(printer.font_Courier_24_VIP(String.format(
                                "%s  %s  %s \n", cnt, c.getString(0), c.getString(1))));

                    } while (c.moveToNext());
                }
                printerdata1.append(printer.font_Courier_24_VIP(String.format(
                        ".    \n ")));
                printerdata1.append(printer.font_Courier_24_VIP(String.format(
                        ".    \n ")));

                conn.printData(printerdata1.toString().getBytes());

                Thread.sleep(3000);
                conn.closeBT();


            } catch (Exception e) {
                // Handle communications error here.
                Toast.makeText(getApplicationContext(), e.toString(),
                        Toast.LENGTH_LONG).show();

            } finally {

                //startActivity(new Intent(getBaseContext(), ActvReport.class));
                finish();
            }
            return;

        }
    }

}
