package org.cso.config;

import com.epson.eposprint.Print;

/**
 * Created by Chandan Singh on 6/5/2025.
 */
public class PrinterManager {
    private static Print printer;
    private static boolean isConnected = false;

    public static boolean connectPrinter(String address) {
        if (!isConnected) {
            try {
                printer = new Print();
                printer.openPrinter(Print.DEVTYPE_BLUETOOTH, address, Print.TRUE, Print.PARAM_DEFAULT);
                isConnected = true;
                return true;
            } catch (Exception e) {
                isConnected = false;
                return false;
            }
        }
        return true;
    }

    public static Print getPrinter() {
        return printer;
    }

    public static void disconnectPrinter() {
        if (isConnected && printer != null) {
            try {
                printer.closePrinter(); // Add this to avoid leaving connection hanging
            } catch (Exception e) {
                e.printStackTrace();
            }
            isConnected = false;
        }
    }
}

