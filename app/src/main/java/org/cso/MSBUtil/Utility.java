package org.cso.MSBUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.provider.Settings;

import org.cso.MSBModel.PktIndexInterface;

import java.util.Date;

/**
 * Created by Thirumalaivelu on 28-12-2015.
 */
public class Utility implements PktIndexInterface {

    private static int getFromByteArray(byte[] b, int offset, int length, int cnt) { //LITTLE ENDIAN
        int retVal = 0;
        if (b.length < offset + length) {
            return -1;
        } else {

//            int v = 0;
            int index = length - 1;
            int shift = (length - 1) * 8;
//            int shift = length;
            while (length > 0) {
//                int retVal1 = (b[(index) + offset] & 0xFF) << shift;
                int retVal1 = (b[(offset + length - 1) + (cnt * 16)] & 0xFF) << shift;
                length--;
                retVal = retVal | retVal1;
                shift -= 8;
            }
        }
        return retVal;
    }


    public static long getFromByteArray(byte[] b, int offset, int length) { //LITTLE ENDIAN
        Long retVal = 0L;
        if (b.length < offset + length) {
            return -1;
        } else {

//            int v = 0;
            int index = length - 1;
            long shift = (length - 1) * 8;
//            int shift = length;
            while (length > 0) {
//                long retVal1 = (b[offset + length - 1] & 0xFF) << shift;
                Long retVal1 = (long) (b[offset + length - 1] & 0xFF) << shift;
                length--;
                retVal = retVal | retVal1;
                shift -= 8;
            }
        }
        return retVal.longValue();
    }

    private static Date getDateForDay(long dayValue, long maxDayNumber) {
        long perDayInMilliSec = 24 * 60 * 60 * 1000;
//        Date d = new Date(2016 - 1900, 0, 1, 18, 30, 30);
        Date d = new Date();
        if (dayValue == maxDayNumber) {
            //return d;
        } else {
            d.setTime(d.getTime() - (perDayInMilliSec * (maxDayNumber - dayValue)));
        }
        System.out.println("Day Value : " + dayValue + ", Max Day Number :" + maxDayNumber + ", Date:" + d);
        return d;
    }

    public static byte[] copyDataPartFromPkt(byte[] completByteArray) {
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

    public static long getValueOfBinaryString(String binaryStr) {

        long res = 0;
        if (binaryStr.length() < 64) {
            res = Long.parseLong(binaryStr, 2);
        } else {
            res = Long.parseLong(binaryStr.substring(1), 2);
            if (binaryStr.charAt(0) == '1') {
                res |= (1L << 63);
            }
        }
        return res;
    }

    public static int checkForErrorsInIncommingPkt(byte[] arr) {
        int errorCode = 0;
        int SOP = 126;
        int ERR_PKT_TYPE = 7;
        int length = arr.length;
        if (length == 4 && arr[0] == SOP && arr[1] == ERR_PKT_TYPE) {
            int valueInTagPos = arr[3];
            switch (valueInTagPos) {
                case ERR_INVALID_RESPONSE:
                    errorCode = ERR_INVALID_RESPONSE;
                    break;
                case ERR_TIME_FAIL:
                    errorCode = ERR_TIME_FAIL;
                    break;
                case ERR_DATE_FAIL:
                    errorCode = ERR_TIME_FAIL;
                    break;
                case ERR_LENGTH:
                    errorCode = ERR_LENGTH;
                    break;
                case ERR_FLASH_READ:
                    errorCode = ERR_FLASH_READ;
                    break;
                case ERR_FLASH_WRITE:
                    errorCode = ERR_FLASH_WRITE;
                    break;
                case ERR_INVALID_RANGE:
                    errorCode = ERR_INVALID_RANGE;
                    break;
                case ERR_FRAM_READ:
                    errorCode = ERR_FRAM_READ;
                    break;
                case ERR_FRAM_WRITE:
                    errorCode = ERR_FRAM_WRITE;
                    break;
                case ERR_INVALID_OFFSET:
                    errorCode = ERR_INVALID_OFFSET;
                    break;
                case ERR_INCOMPLETE_PKT:
                    errorCode = ERR_INCOMPLETE_PKT;
                    break;
                default:
                    errorCode = 0;
            }
            System.out.println("OGH BLE COMM MOD: checkForErrorsInIncommingPkt returns " + errorCode);
        }

        return errorCode;
    }

    public static byte[] formUDPMInfoPkt() {
        byte[] Tx_buffer_udpminfo = new byte[7];
        Tx_buffer_udpminfo[0] = 0x7E;// SOP
        Tx_buffer_udpminfo[1] = 0x03; // PKT_TYP
        Tx_buffer_udpminfo[2] = 0x07;// PKT_LEN
        Tx_buffer_udpminfo[3] = 0x01;// TAG_COUNT
        Tx_buffer_udpminfo[4] = 0x01;// TAG_TYPE
        Tx_buffer_udpminfo[5] = 0x00;// TAG_LEN
        Tx_buffer_udpminfo[6] = 0x00;// TAG_VAL[0]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_udpminfo);
        return Tx_buffer_udpminfo;
    }

    public static byte[] formUDPMDebugPkt(int count) {
        byte[] Tx_buffer_udpmdebug = new byte[7];
        Tx_buffer_udpmdebug[0] = 0x7E;// SOP
        Tx_buffer_udpmdebug[1] = 0x01; // PKT_TYP
        Tx_buffer_udpmdebug[2] = 0x07;// PKT_LEN
        Tx_buffer_udpmdebug[3] = 0x01;// TAG_COUNT
        Tx_buffer_udpmdebug[4] = 0x02;// TAG_TYPE
        Tx_buffer_udpmdebug[5] = 0x01;// TAG_LEN
        // byte[] countArray = convertSampletoByteArray(Integer.parseInt(count));
        Tx_buffer_udpmdebug[6] = (byte) count;//Integer.parseInt(count) ;// TAG_VAL[1]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_udpmdebug);
        return Tx_buffer_udpmdebug;
        /*0x7E
        0x01
        0x07
        0x01
        0x02
        0x01
        OFFSET*/
    }

    public static byte[] formUDPMDailyusagePk(int count) {
        byte[] Tx_buffer_udpmdailyusage = new byte[7];
        Tx_buffer_udpmdailyusage[0] = 0x7E;// SOP
        Tx_buffer_udpmdailyusage[1] = 0x01; // PKT_TYP
        Tx_buffer_udpmdailyusage[2] = 0x07;// PKT_LEN
        Tx_buffer_udpmdailyusage[3] = 0x01;// TAG_COUNT
        Tx_buffer_udpmdailyusage[4] = 0x03;// TAG_TYPE
        Tx_buffer_udpmdailyusage[5] = 0x01;// TAG_LEN

        Tx_buffer_udpmdailyusage[6] = (byte) count;
        ;// TAG_VAL[1]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_udpmdailyusage);
            /*0x7E
0x01
0x07
0x01
0x03
0x01
OFFSET
*/
        return Tx_buffer_udpmdailyusage;
    }

    public static byte[] formUDPMlivefeedPkt() {
        byte[] Tx_buffer_udpmlivefeed = new byte[7];
        Tx_buffer_udpmlivefeed[0] = 0x7E;// SOP
        Tx_buffer_udpmlivefeed[1] = 0x03; // PKT_TYP
        Tx_buffer_udpmlivefeed[2] = 0x07;// PKT_LEN
        Tx_buffer_udpmlivefeed[3] = 0x01;// TAG_COUNT
        Tx_buffer_udpmlivefeed[4] = 0x04;// TAG_TYPE
        Tx_buffer_udpmlivefeed[5] = 0x00;// TAG_LEN
        Tx_buffer_udpmlivefeed[6] = 0x00;// TAG_VAL[0]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_udpmlivefeed);
        return Tx_buffer_udpmlivefeed;
        /*0x7E
0x03
0x07
0x01
0x04
0x00
0x00
*/
    }


    public static byte[] dateTimeSetPkt(byte[] datetime) {
         byte[] Tx_buffer_dateTimeSet = new byte[12];
        Tx_buffer_dateTimeSet[0] = 0x7E;// SOP
        Tx_buffer_dateTimeSet[1] = 0x05; // PKT_TYP
        Tx_buffer_dateTimeSet[2] = 0x0B;// PKT_LEN
        Tx_buffer_dateTimeSet[3] = 0x01;// TAG_COUNT
        Tx_buffer_dateTimeSet[4] = 0x01;// TAG_TYPE
        Tx_buffer_dateTimeSet[5] = 0x05;// TAG_LEN
        Tx_buffer_dateTimeSet[6] = (datetime[4]);// TAG_VAL[0]
        Tx_buffer_dateTimeSet[7] = (datetime[3]);
        Tx_buffer_dateTimeSet[8] = (datetime[2]);
        Tx_buffer_dateTimeSet[9] = (datetime[1]);
        Tx_buffer_dateTimeSet[10] =(datetime[0]);
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_dateTimeSet+","+datetime[0]+","+datetime[1]+","+ datetime[2]+","+ datetime[3]+","+ datetime[4]);
        return Tx_buffer_dateTimeSet;
    /*    0x7E
        0x05
        0x0B
        0x01
        0x01
        0x05*/
      //  7E 06 0B 01 01 05 01 E5 20 D7 78 7E 06 0B 01 01 05 01 E5 20 D7 78
    }

    public static byte[] enegryErasePkt() {
        byte[] Tx_buffer_enegryErase = new byte[7];
        Tx_buffer_enegryErase[0] = 0x7E;// SOP
        Tx_buffer_enegryErase[1] = 0x05; // PKT_TYP
        Tx_buffer_enegryErase[2] = 0x07;// PKT_LEN
        Tx_buffer_enegryErase[3] = 0x01;// TAG_COUNT
        Tx_buffer_enegryErase[4] = 0x03;// TAG_TYPE
        Tx_buffer_enegryErase[5] = 0x01;// TAG_LEN
        Tx_buffer_enegryErase[6] = 0x01;// TAG_VAL[0]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_enegryErase);
        return Tx_buffer_enegryErase;
        /*0x7E
0x05
0x07
0x01
0x03
0x01
0x00


*/
    }

    public static byte[] brownOutErasePkt() {
        byte[] Tx_buffer_brownout = new byte[7];
        Tx_buffer_brownout[0] = 0x7E;// SOP
        Tx_buffer_brownout[1] = 0x05; // PKT_TYP
        Tx_buffer_brownout[2] = 0x07;// PKT_LEN
        Tx_buffer_brownout[3] = 0x01;// TAG_COUNT
        Tx_buffer_brownout[4] = 0x04;// TAG_TYPE
        Tx_buffer_brownout[5] = 0x01;// TAG_LEN
        Tx_buffer_brownout[6] = 0x01;// TAG_VAL[0]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_brownout);
        return Tx_buffer_brownout;
        /*0x7E
0x05
0x07
0x01
0x04
0x01
0x00


*/
    }

    public static byte[] brownInErasePkt() {
        byte[] Tx_buffer_brownin = new byte[7];
        Tx_buffer_brownin[0] = 0x7E;// SOP
        Tx_buffer_brownin[1] = 0x05; // PKT_TYP
        Tx_buffer_brownin[2] = 0x07;// PKT_LEN
        Tx_buffer_brownin[3] = 0x01;// TAG_COUNT
        Tx_buffer_brownin[4] = 0x05;// TAG_TYPE
        Tx_buffer_brownin[5] = 0x01;// TAG_LEN
        Tx_buffer_brownin[6] = 0x01;// TAG_VAL[0]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_brownin);
        return Tx_buffer_brownin;
        /*0x7E
0x05
0x07
0x01
0x05
0x01
0x00


*/
    }

    public static byte[] overloadErasePkt() {
        byte[] Tx_buffer_overload = new byte[7];
        Tx_buffer_overload[0] = 0x7E;// SOP
        Tx_buffer_overload[1] = 0x05; // PKT_TYP
        Tx_buffer_overload[2] = 0x07;// PKT_LEN
        Tx_buffer_overload[3] = 0x01;// TAG_COUNT
        Tx_buffer_overload[4] = 0x06;// TAG_TYPE
        Tx_buffer_overload[5] = 0x01;// TAG_LEN
        Tx_buffer_overload[6] = 0x01;// TAG_VAL[0]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_overload);
        return Tx_buffer_overload;
        /*0x7E
0x05
0x07
0x01
0x06
0x01
0x00


*/
    }

    public static byte[] immediateCountErasePkt() {
        byte[] Tx_buffer_immediateCount = new byte[7];
        Tx_buffer_immediateCount[0] = 0x7E;// SOP
        Tx_buffer_immediateCount[1] = 0x05; // PKT_TYP
        Tx_buffer_immediateCount[2] = 0x07;// PKT_LEN
        Tx_buffer_immediateCount[3] = 0x01;// TAG_COUNT
        Tx_buffer_immediateCount[4] = 0x07;// TAG_TYPE
        Tx_buffer_immediateCount[5] = 0x01;// TAG_LEN
        Tx_buffer_immediateCount[6] = 0x01;// TAG_VAL[0]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_immediateCount);
        return Tx_buffer_immediateCount;
        /*0x7E
0x05
0x07
0x01
0x07
0x01
0x00


*/
    }

    public static byte[] resetCountErasePkt() {
        byte[] Tx_buffer_reasetCount = new byte[7];
        Tx_buffer_reasetCount[0] = 0x7E;// SOP
        Tx_buffer_reasetCount[1] = 0x05; // PKT_TYP
        Tx_buffer_reasetCount[2] = 0x07;// PKT_LEN
        Tx_buffer_reasetCount[3] = 0x01;// TAG_COUNT
        Tx_buffer_reasetCount[4] = 0x08;// TAG_TYPE
        Tx_buffer_reasetCount[5] = 0x01;// TAG_LEN
        Tx_buffer_reasetCount[6] = 0x01;// TAG_VAL[0]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_reasetCount);
        return Tx_buffer_reasetCount;
        /*0x7E
0x05
0x07
0x01
0x08
0x01
0x00


*/
    }


    public static byte[] splCustomeridSetPkt() {
        byte[] Tx_buffer_reasetCount = new byte[7];
        Tx_buffer_reasetCount[0] = 0x7E;// SOP
        Tx_buffer_reasetCount[1] = 0x08; // PKT_TYP
        Tx_buffer_reasetCount[2] = 0x08;// PKT_LEN
        Tx_buffer_reasetCount[3] = 0x01;// TAG_COUNT
        Tx_buffer_reasetCount[4] = 0x09;// TAG_TYPE
        Tx_buffer_reasetCount[5] = 0x04;// TAG_LEN

        Tx_buffer_reasetCount[6] = 0x01;// TAG_VAL[0]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_reasetCount);
        return Tx_buffer_reasetCount;
        /*0x7E
0x7E
0x08
0x08
0x01
0x09
0x04



*/
    }

    public static byte[] splAcMeterReadingSetPkt() {
        byte[] Tx_buffer_reasetCount = new byte[7];
        Tx_buffer_reasetCount[0] = 0x7E;// SOP
        Tx_buffer_reasetCount[1] = 0x08; // PKT_TYP
        Tx_buffer_reasetCount[2] = 0x08;// PKT_LEN
        Tx_buffer_reasetCount[3] = 0x01;// TAG_COUNT
        Tx_buffer_reasetCount[4] = 0x0A;// TAG_TYPE
        Tx_buffer_reasetCount[5] = 0x04;// TAG_LEN

        Tx_buffer_reasetCount[6] = 0x01;// TAG_VAL[0]
        System.out.println("OGH BLE COMM MOD: " + Tx_buffer_reasetCount);
        return Tx_buffer_reasetCount;
        /*0x7E
0x7E
0x08
0x08
0x01
0x0A
0x04



*/
    }

    public static byte[] convertSampletoByteArray(int count) {
        byte zero = (byte) (count & 0xFF);
        byte one = (byte) ((count >> 8) & 0xFF);
        byte[] arr = {zero, one};
        return arr;
    }

    public static void printPkt(byte[] rawValue) {
        if (rawValue == null) {
            System.out.print("Null Pkt!! ");
            return;
        }
//        System.out.println("OGH BLE COMM MOD: Inside printPkt ");
        for (int i = 0; i < rawValue.length; i++) {
            String hexString = String.format("%02x", rawValue[i]);
            System.out.print(hexString + "(" + rawValue[i] + ") ");
//            System.out.print("OGH BLE COMM MOD: Printing Pkt " + hexString + "(" + rawValue[i] + ") ");
//        	 System.out.println("I am jeeva packet printpkt to hexString"+hexString + "(" + rawValue[i] + ") ");

        }
        System.out.println();

    }

    public static int getNumberOfDebugRecordsInPkt(byte[] debugDataPartArray) {
        int retVal = 0;
        if (debugDataPartArray == null) {
            return retVal;
        }
        if (debugDataPartArray.length % UDPM_DEBUGE_PACKET_SIZE != 0) {
            //invalid packet;
            return retVal;
        } else {
            retVal = debugDataPartArray.length / UDPM_DEBUGE_PACKET_SIZE;

        }
        return retVal;
    }

    public static int getNumberOfDailyUsageRecordsInPkt(byte[] dailyUsagePartArray) {
        int retVal = 0;
        if (dailyUsagePartArray == null) {
            return retVal;
        }
        if (dailyUsagePartArray.length % UDPM_DAILYUSAGE_PACKET_SIZE != 0) {
            //invalid packet;
            return retVal;
        } else {
            retVal = dailyUsagePartArray.length / UDPM_DAILYUSAGE_PACKET_SIZE;
        }
        return retVal;
    }



}
