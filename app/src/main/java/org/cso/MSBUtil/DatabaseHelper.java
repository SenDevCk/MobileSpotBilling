package org.cso.MSBUtil;

/**
 * Created by admin on 2/17/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.cso.MSBModel.Info;
import org.cso.MSBModel.Udpmdevice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Thirumalaivelu on 23-12-2015.
 */
public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    public static DatabaseHelper dbhelper = null;
    private final String DATABASE_NAME = "CHAKRA_UDPM_LFDC";
    DateFormat dateFormat;
    Date date;
    DatabaseHandler dbhandler = null;
    private int DATABASE_VERSION = 1;
    // Logger log = CLogger.getInstance().getLogger(DatabaseHelper.class);

    private DatabaseHelper(Context context) {
        dbhandler = new DatabaseHandler(context, DATABASE_NAME, DATABASE_VERSION);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = new Date();
        createTableIfNotExists();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (dbhelper == null) {
            synchronized (DatabaseHelper.class) {
                if (dbhelper == null) {
                    dbhelper = new DatabaseHelper(context);
                }
            }
        }
        return dbhelper;
    }

    private void createTableIfNotExists() {

        String udpmdevTableQuery = "create table if not exists udpmdevices (udpmdevrecordid INTEGER primary key autoincrement not null, udpmdevdeviceid VARCHAR)";
        dbhandler.createTable(udpmdevTableQuery);

        String udpminfoTableQuery = "CREATE TABLE IF NOT EXISTS udpminfo (" +
                "udpminforecordid INTEGER PRIMARY KEY AUTOINCREMENT not null," +
                "udpminfodevicerefid VARCHAR," +
                "udpminfonodeid VARCHAR, " +
                "udpminfopcbno VARCHAR, " +
                "udpminfodateofmanufacturing DATE, " +
                "udpminfofirmwareversion VARCHAR," +
                "udpminfoenergy INTEGER," +
                "udpminfobrownoutcount INTEGER, " +
                "udpminfobrownincount INTEGER," +
                "udpminfolvcocount INTEGER, " +
                "udpminfoimcocount INTEGER, " +
                "udpminforesetcount INTEGER," +
                "udpminfocurrenttimestamp DATETIME," +
                "udpminfodailyusagecount INTEGER," +
                "udpminfodebugentriescount INTEGER," +
                "udpminfopktheader VARCHAR," +
                "udpminforawdata VARCHAR," +
                "udpminfosyncstate VARCHAR," +
                "udpminfoappreaderversion VARCHAR," +
                "udpminfopersistedtime DATETIME," +
                "udpminfoserverdatasyncstatus INTEGER," +
                "FOREIGN KEY (udpminfodevicerefid) references udpmdevices(udpmdevrecordid) ON DELETE CASCADE" + ")";
        dbhandler.createTable(udpminfoTableQuery);

        String udpmlivefeedTableQuery = "CREATE TABLE IF NOT EXISTS  udpmlivefeed (udpmliverecordid INTEGER PRIMARY KEY AUTOINCREMENT," +
                "udpmlivedevicerefid VARCHAR," +
                "udpmlivevoltage INTEGER," +
                "udpmlivecurrent INTEGER," +
                "udpmlivelvcostatus INTEGER," +
                "udpmliveenergycount INTEGER," +
                "udpmlivebrownoutcount INTEGER," +
                "udpmlivebrownincount INTEGER," +
                "udpmliveoverloadcutoffcount INTEGER, " +
                "udpmliveimcocount INTEGER," +
                "udpmliveresetcount INTEGER," +
                "udpmlivemoduletemperature INTEGER," +
                "udpmlivepktheader VARCHAR," +
                "udpmliverawdata VARCHAR," +
                "udpmlivesyncstate VARCHAR," +
                "udpmlivepersistedtime DATETIME," +
                "udpmlivecurrenttime DATETIME," +
                "udpmliveserverdatasyncstatus INTEGER," +
                "FOREIGN KEY(udpmlivedevicerefid) references udpmdevices(udpmdevrecordid) ON DELETE CASCADE" + ")";
        dbhandler.createTable(udpmlivefeedTableQuery);

        String udpmDebugTableQuery = "CREATE TABLE IF NOT EXISTS  udpmdebug (udpmdebugrecordid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "udpmdebugdevicerefid VARCHAR," +
                "udpmdebugvoltage INTEGER," +
                "udpmdebugcurrent INTEGER," +
                "udpmdebuglvcostatus INTEGER," +
                "udpmdebugevent INTEGER," +
                "udpmdebugmoduleTemperature INTEGER," +
                " udpmdebugtimestamp DATE," +
                "udpmdebugpktheader VARCHAR," +
                "udpmdebugrawdata VARCHAR," +
                "udpmdebugsyncstate VARCHAR," +
                "udpmdebugpersistedtime DATETIME," +
                "udpmdebugserverdatasyncstatus INTEGER," +
                "FOREIGN KEY(udpmdebugdevicerefid) references udpmdevices(udpmdevrecordid) ON DELETE CASCADE" + ")";
        dbhandler.createTable(udpmDebugTableQuery);

        String udpmDailyUsageTableQuery = "CREATE TABLE IF NOT EXISTS  udpmdailyusage (udpmdailyusagerecordid INTEGER PRIMARY KEY AUTOINCREMENT," +
                "udpmdailyusagedevicerefid VARCHAR," +
                "udpmdailyusagetimestamp DATE," +
                "udpmdailyusageenergy INTEGER," +
                "udpmdailyusagepeakload INTEGER," +
                "udpmdailyusagepeakloadtime TIME," +
                "udpmdailyusagemaxtemp INTEGER," +
                "udpmdailyusagemaxtemptime TIME," +
                "udpmdailyusagepktheader VARCHAR," +
                "udpmdailyusagerawdata VARCHAR," +
                "udpmdailyusagesyncstate VARCHAR," +
                "udpmdailyusagepersistedtime DATETIME," +
                "udpmdailyusageserverdatasyncstatus INTEGER,"+
                "FOREIGN KEY(udpmdailyusagedevicerefid) references udpmdevices(udpmdevrecordid) ON DELETE CASCADE" + ")";
        dbhandler.createTable(udpmDailyUsageTableQuery);


        String readstatisticsTableQuery = "CREATE TABLE IF NOT EXISTS udpmreadstatistics (" +
                "udpmstatrecid INTEGER PRIMARY KEY AUTOINCREMENT not null, " +
                "udpmstatudpmrefid INTEGER, " +
                "udpmstatudpmid INTEGER ," +
                "udpmstatsendpkt INTEGER, " +
                "udpmstatreceivedpkt INTEGER, " +
                "udpmstaterror INTEGER, " +
                "udpmstatnoreply INTEGER, " +
                "udpmstatserverdatasyncstatus INTEGER, " +
                "udpmstatisticsduration TEXT,"+
                "udpmstatpersistime TEXT," +
                "FOREIGN KEY(udpmstatudpmrefid )references udpminfo(udpminforecordid) ON DELETE CASCADE" +
                ");";
        dbhandler.createTable(readstatisticsTableQuery);


        String readstatisticserrorpacket = "CREATE TABLE IF NOT EXISTS udpmpkterrorstatistics (" +
                "udpmpkterrorstatrecid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "udpmpkterrorstatrefid INTEGER, " +
                "udpmpkterrorcode INTEGER ," +
                "udpmpkterrorcount INTEGER, " +
                "udpmpkterrormessage INTEGER, " +
                "FOREIGN KEY(udpmpkterrorstatrefid )references udpmreadstatistics(udpmstatrecid) ON DELETE CASCADE" +
                ");";
        dbhandler.createTable(readstatisticserrorpacket);


    }


    public void setServerDataSyncStatus(String tableName, String colName, String wherearg, String recid) {
        dbhandler.setIntoDb(tableName, colName, wherearg, recid);
    }

    public void setServerDataSyncStatus(String query) {
        dbhandler.setIntoDb(query);
    }

    public void clearReadData() {
        String tableNamesArr[] = {"udpminfo", "udpmdevices", "udpmlivefeed", "udpmdailyusage", "udpmdebug", "udpmreadstatistics"};
        for (String table : tableNamesArr) {
            dbhandler.deleteFromDb(table);
        }
    }

    //**************************************Add /select device udpm**********************************************************
    public String checkUdpmIdAvailabilityAndInsert(String udpmid) {
        // int id = Integer.parseInt(udpmid);
        String recId = "";
        System.out.println("udpmid" + udpmid);
        String query = "select udpmdevrecordid from udpmdevices where udpmdevdeviceid =" + udpmid;
        System.out.println("checkUdpmIdAvailabilityAndInsert :" + query);
        Cursor cursor = dbhandler.getFromDb(query);
        int cnt = cursor.getCount();
        if (cnt == 0) {
            ContentValues values = new ContentValues();
            values.put("udpmdevdeviceid", udpmid);
            dbhandler.insertIntoDb("udpmdevices", values);
            cursor = dbhandler.getFromDb(query);
            if (cursor != null && cursor.moveToFirst()) {
                recId = cursor.getString(cursor.getColumnIndex("udpmdevrecordid"));
            }
        }

        if (cursor != null && cursor.moveToFirst()) {
            recId = cursor.getString(cursor.getColumnIndex("udpmdevrecordid"));
        }
        System.out.println("UDPM Record ID " + recId);
        // log.info("OGH Record ID " + recId);
        return recId;
    }

    public int checkUDPMIdAvailabilityRefid(int udpmid) {
        // int id = Integer.parseInt(udpmid);
        System.out.println("udpmid" + udpmid);
        String query = "select udpmdevrecordid from udpmdevices where udpmdevdeviceid =" + udpmid;
        System.out.println("checkUdpmIdAvailabilityAndInsert :" + query);
        Cursor cursor = dbhandler.getFromDb(query);
        int cnt = cursor.getCount();
        int ref = 0;
        if (cursor.moveToFirst()) {
            do {
                Udpmdevice val = new Udpmdevice();
                val.setUdpmdevicerecid(cursor.getInt(0));
                ref = val.getUdpmdevicerecid();
            } while (cursor.moveToNext());
        }

        return ref;

    }

    public List<Udpmdevice> getAllUdpmdevices() {

        List<Udpmdevice> UdpmdeviceList = new ArrayList<Udpmdevice>();
        // Select All Query
        String selectQuery = "SELECT * FROM udpmdevices";

        Cursor cursor = dbhandler.getFromDb(selectQuery);
        if (cursor.moveToFirst()) {
            do {
                Udpmdevice Udpmdevice = new Udpmdevice();
                Udpmdevice.setUdpmdevicerecid(cursor.getInt(0));
                Udpmdevice.setUdpmdevdeviceid(cursor.getString(1));
                UdpmdeviceList.add(Udpmdevice);
            } while (cursor.moveToNext());
        }
        return UdpmdeviceList;
    }

    //**************************************End Add device udpm**************************************************************


    /******************************************
     * Add and select from udpm info
     **********************************************************/
    public void addUdpmInfo(Info infoValue) {
        Log.i(TAG, "UDPM BLE COMM MOD: Inside addUdpmInfo in DatabaseHelper ");
        ContentValues info = new ContentValues();
        info.put("udpminfodevicerefid", infoValue.getInfodeviceRefid());
        info.put("udpminfonodeid ", infoValue.getInfoNodeid());
        checkUdpmIdAvailabilityAndInsert(infoValue.getInfoNodeid());
        info.put("udpminfopcbno", infoValue.getInfoPcbno());
        info.put("udpminfodateofmanufacturing", infoValue.getInfoDateofmanufacturing());
        info.put("udpminfofirmwareVersion", infoValue.getInfoFirmwareVersion());
        info.put("udpminfoenergy", infoValue.getInfoEnergy());
        info.put("udpminfobrownoutcount", infoValue.getInfoBrownoutcount());
        info.put("udpminfobrownincount", infoValue.getInfoBrownincount());
        info.put("udpminfolvcocount", infoValue.getInfoLvcocount());
        info.put("udpminfoimcocount", infoValue.getInfoImcoscount());
        info.put("udpminforesetcount", infoValue.getInfoResetscount());
        info.put("udpminfocurrenttimestamp", infoValue.getInfoCurrenttimestamp());
        info.put("udpminfodailyusagecount", infoValue.getInfoDailyusageentycount());
        info.put("udpminfodebugentriescount", infoValue.getInfodebugentriescount());
        info.put("udpminfopktheader", infoValue.getInfoPktHeader());
        info.put("udpminforawdata", infoValue.getInfoPktinfo());
        info.put("udpminfosyncstate", infoValue.getInfoSyncState());
        info.put("udpminfoappreaderversion", infoValue.getInfoAppreaderVersion());
        info.put("udpminfopersistedtime", dateFormat.format(date));
        info.put("udpminfoserverdatasyncstatus", infoValue.getInfoserverdatasyncstatus());
        System.out.println("udpminfoserverdatasyncstatus :" + infoValue.getInfoserverdatasyncstatus());
        dbhandler.insertIntoDb("UDPMinfo", info);
        Log.i(TAG, "UDPMINFO:" + info);

    }

    /*Select all udpminfo using deviceRefid)*/

    public List<Info> getAllUdpmInfo(int udpmid) {
        List<Info> devList = new ArrayList<>();
        Log.i(TAG, "UDPM BLE COMM MOD: Inside getAllUdpmInfo in DatabaseHelper ");
        String query = "SELECT * FROM udpminfo WHERE udpminfodevicerefid = ? order by udpminfocurrenttimestamp desc limit 1";
        Cursor cursor = dbhandler.getFromDb(query, new String[]{udpmid + ""});
        if (cursor.moveToFirst()) {
            do {
                Info info = new Info();
                info.setInforecordid(cursor.getInt(0));
                info.setInfodeviceRefid(cursor.getInt(1));
                info.setInfoNodeid(cursor.getString(2));
                info.setInfoPcbno(cursor.getString(3));
                info.setInfoDateofmanufacturing(cursor.getString(4));
                info.setInfoFirmwareVersion(cursor.getString(5));
                info.setInfoEnergy(cursor.getInt(6));
                info.setInfoBrownoutcount(cursor.getInt(7));
                info.setInfoBrownincount(cursor.getInt(8));
                info.setInfoLvcocount(cursor.getInt(9));
                info.setInfoImcoscount(cursor.getInt(10));
                info.setInfoResetscount(cursor.getInt(11));
                info.setInfoCurrenttimestamp(cursor.getString(12));
                info.setInfoDailyusageentycount(cursor.getInt(13));
                info.setInfodebugentriescount(cursor.getInt(14));
                info.setInfoPktHeader(cursor.getString(15));
                info.setInfoPktinfo(cursor.getString(16));
                info.setInfoSyncState(cursor.getInt(17));
                info.setInfoAppreaderVersion(cursor.getString(18));
                info.setInfopersistedtime(cursor.getString(19));
                devList.add(info);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "UDPMINFO:" + devList);
        return devList;
    }

    public List<Info> getAllUdpmInfoRecid(int recid) {
        List<Info> devList = new ArrayList<>();
        String query = "SELECT * FROM udpminfo WHERE udpminfoserverdatasyncstatus != 0 AND udpminfodevicerefid = ?";
        Cursor cursor = dbhandler.getFromDb(query, new String[]{recid + ""});
        if (cursor.moveToFirst()) {
            do {
                Info info = new Info();
                info.setInforecordid(cursor.getInt(0));
                info.setInfodeviceRefid(cursor.getInt(1));
                info.setInfoNodeid(cursor.getString(2));
                info.setInfoPcbno(cursor.getString(3));
                info.setInfoDateofmanufacturing(cursor.getString(4));
                info.setInfoFirmwareVersion(cursor.getString(5));
                info.setInfoEnergy(cursor.getInt(6));
                info.setInfoBrownoutcount(cursor.getInt(7));
                info.setInfoBrownincount(cursor.getInt(8));
                info.setInfoLvcocount(cursor.getInt(9));
                info.setInfoImcoscount(cursor.getInt(10));
                info.setInfoResetscount(cursor.getInt(11));
                info.setInfoCurrenttimestamp(cursor.getString(12));
                info.setInfoDailyusageentycount(cursor.getInt(13));
                info.setInfodebugentriescount(cursor.getInt(14));
                info.setInfoPktHeader(cursor.getString(15));
                info.setInfoPktinfo(cursor.getString(16));
                info.setInfoSyncState(cursor.getInt(17));
                info.setInfoAppreaderVersion(cursor.getString(18));
                info.setInfopersistedtime(cursor.getString(19));
                info.setInfoserverdatasyncstatus(cursor.getInt(20));
                devList.add(info);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "UDPMINFO:" + devList);
        return devList;
    }

    /*Select all udpminfo for resync the data  */
    public List<Info> getUdpmInfoForResync(String udpmid) {
        List<Info> devList = new ArrayList<>();
        int recid = checkUDPMIdAvailabilityRefid(Integer.parseInt(udpmid));

        String query = "  SELECT * FROM udpminfo WHERE udpminfodevicerefid = ?";

        Cursor cursor = dbhandler.getFromDb(query, new String[]{"" + recid});

        if (cursor.moveToFirst()) {
            do {
                Info info = new Info();
                info.setInforecordid(cursor.getInt(0));
                System.out.println("udpminfo values" + cursor.getInt(0));
                info.setInfodeviceRefid(cursor.getInt(1));
                info.setInfoNodeid(cursor.getString(2));
                info.setInfoPcbno(cursor.getString(3));
                info.setInfoDateofmanufacturing(cursor.getString(4));
                info.setInfoFirmwareVersion(cursor.getString(5));
                info.setInfoEnergy(cursor.getInt(6));
                info.setInfoBrownoutcount(cursor.getInt(7));
                info.setInfoBrownincount(cursor.getInt(8));
                info.setInfoLvcocount(cursor.getInt(9));
                info.setInfoImcoscount(cursor.getInt(10));
                info.setInfoResetscount(cursor.getInt(11));
                info.setInfoCurrenttimestamp(cursor.getString(12));
                info.setInfoDailyusageentycount(cursor.getInt(13));
                info.setInfodebugentriescount(cursor.getInt(14));
                info.setInfoPktHeader(cursor.getString(15));
                info.setInfoPktinfo(cursor.getString(16));
                info.setInfoSyncState(cursor.getInt(17));
                info.setInfoAppreaderVersion(cursor.getString(18));
                info.setInfopersistedtime(cursor.getString(19));
                info.setInfoserverdatasyncstatus(cursor.getInt(20));
                devList.add(info);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "UDPMINFO:" + devList);
        return devList;

    }
    //**************************************End udpmInfo**************************************************************





    public String getUDPMRecIdFromUDPMDeviceId(String udpmId) {
        String devRecId = null;
        String query = "select udpmdevrecordid from udpmdevices where udpmdevdeviceid=" + udpmId;
        Cursor cursor = dbhandler.getFromDb(query);

        int cnt = cursor.getCount();

        if (cursor != null && cursor.moveToFirst()) {
            devRecId = cursor.getString(cursor.getColumnIndex("udpmdevrecordid"));
        }
        return devRecId;
    }

    public int getUDPMRecidFromdeviceid(String deviceid) {
        int deviceId = 0;
        String query = "select udpmdevrecordid  from udpmdevices where udpmdevdeviceid=" + deviceid;
        Cursor cursor = dbhandler.getFromDb(query);

        int cnt = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                Udpmdevice val = new Udpmdevice();
                deviceId = val.getUdpmdevicerecid();
            } while (cursor.moveToNext());
        }

        return deviceId;
    }

    public String getUDPMVersionIdFromUDPMRecId(String udpmRecId) {
        String versionId = "venn";
        String query = "select udpmversion from udpminfo where udpmdevicerefid=" + udpmRecId;
        Cursor cursor = dbhandler.getFromDb(query);


        if (cursor != null && cursor.moveToFirst()) {
            versionId = cursor.getString(cursor.getColumnIndex("udpmversion"));
        }
        return versionId;
    }

    public boolean checkUDPMDevicesIsPresent() {
        boolean data = false;
        Cursor udpmdevice = dbhandler.getFromDb("SELECT * FROM  udpmdevices");
        if (udpmdevice != null) {
            if (udpmdevice.moveToFirst()) {
                data = true;
            }
        }
        return data;
    }

    public boolean checkAllServerdata() {
        boolean data = false;

        Cursor udpminfocursor = dbhandler.getFromDb("SELECT * FROM  udpminfo  WHERE udpminfoserverdatasyncstatus = 1");

        if (udpminfocursor != null) {
            if (udpminfocursor.moveToFirst()) {
                data = true;
                return data;
            }
        }

        Cursor livefeedcursor = dbhandler.getFromDb("SELECT * FROM  udpmlivefeed  WHERE udpmliveserverdatasyncstatus = 1");
        if (livefeedcursor != null) {
            if (livefeedcursor.moveToFirst()) {
                data = true;
                return data;
            }
        }

        Cursor debugcursor = dbhandler.getFromDb("SELECT * FROM  udpmdebug  WHERE udpmdebugserverdatasyncstatus = 1");
        if (debugcursor != null) {
            if (debugcursor.moveToFirst()) {
                data = true;
                return data;
            }
        }


        Cursor dailyusage = dbhandler.getFromDb("SELECT * FROM  udpmdailyusage  WHERE udpmdailyusageserverdatasyncstatus = 1");
        if (dailyusage != null) {
            if (dailyusage.moveToFirst()) {
                data = true;
                return data;
            }
        }
        return data;
    }

    public boolean checkServerdata(String oghRecId) {
        boolean data = false;
        // Cursor cursor_ = dbhandler.getServerStatusFromDb("SELECT * FROM oghdevices od join oghinfo oi on oi.oghdevicerefid=od.oghdevrecordid WHERE oghserverdatasyncstatus = 1 and oghdevrecordid = ? ", new String[]{oghRecId});
        // Cursor cursor_ = dbhandler.getServerStatusFromDb("SELECT * FROM oghdevices od join oghinfo oi on oi.oghdevicerefid=od.oghdevrecordid join oghenergy oe on oe.energyoghrefid = od.oghdevrecordid join oghlivefeed ol on ol.liveoghrefid=od.oghdevrecordid join oghvctdata ov on ov.vctoghrefid=od.oghdevrecordid join oghpowerdata op on op.poweroghrefid=od.oghdevrecordid WHERE oghserverdatasyncstatus = 1 and energyserverdatasyncstatus= 1 and liveserverdatasyncstatus = 1 and vctserverdatasyncstatus = 1 and powerserverdatasyncstatus = 1 and oghdevrecordid = ? ", new String[]{oghRecId});

        Cursor udpminfocursor = dbhandler.getServerStatusFromDb("SELECT * FROM  udpminfo  WHERE udpminfoserverdatasyncstatus = 1 and udpminfodevicerefid = ? ", new String[]{oghRecId});

        if (udpminfocursor != null) {
            if (udpminfocursor.moveToFirst()) {
                data = true;
                return data;
            }
        }

        Cursor livefeedcursor = dbhandler.getServerStatusFromDb("SELECT * FROM  udpmlivefeed  WHERE udpmliveserverdatasyncstatus = 1 and udpmlivedevicerefid = ? ", new String[]{oghRecId});
        if (livefeedcursor != null) {
            if (livefeedcursor.moveToFirst()) {
                data = true;
                return data;
            }
        }

        Cursor debug = dbhandler.getServerStatusFromDb("SELECT * FROM  udpmdebug  WHERE udpmdebugserverdatasyncstatus = 1 and udpmdebugdevicerefid = ? ", new String[]{oghRecId});
        if (debug != null) {
            if (debug.moveToFirst()) {
                data = true;
                return data;
            }
        }


        Cursor dailyusage = dbhandler.getServerStatusFromDb("SELECT * FROM  udpmdailyusage  WHERE udpmdailyusageserverdatasyncstatus = 1 and udpmdailyusagedevicerefid = ? ", new String[]{oghRecId});
        if (dailyusage != null) {
            if (dailyusage.moveToFirst()) {
                data = true;
                return data;
            }
        }


        return data;
    }

    /* public boolean checkSinleOghServerdata(String oghid) {
         boolean data=false;

         // Cursor oghinfocursor= dbhandler.getFromDb("SELECT * FROM  oghinfo  WHERE oghserverdatasyncstatus = 1");

         String recid = checkUdpmIdAvailabilityAndInsert(oghid);
         System.out.println("OGH ID "+oghid+"Recid is "+recid);

         Cursor oghinfocursor = dbhandler.getFromDb("SELECT * FROM oghinfo WHERE oghserverdatasyncstatus = 1 AND oghdevicerefid = ?", new String[]{recid});

         if(oghinfocursor!=null) {
             if (oghinfocursor.moveToFirst()) {
                 data=true;
                 return data;
             }
         }

         Cursor livefeedcursor= dbhandler.getFromDb("SELECT * FROM  oghlivefeed  WHERE liveserverdatasyncstatus = 1 AND liveoghrefid = ?", new String[]{recid});
         if(livefeedcursor!=null) {
             if (livefeedcursor.moveToFirst()) {
                 data=true;
                 return data;
             }
         }

         Cursor energycursor= dbhandler.getFromDb("SELECT * FROM  oghenergy  WHERE energyserverdatasyncstatus = 1 AND energyoghrefid = ?", new String[]{recid});
         if(energycursor!=null) {
             if (energycursor.moveToFirst()) {
                 data=true;
                 return data;
             }
         }


         Cursor vctcursor= dbhandler.getFromDb("SELECT * FROM  oghvctdata  WHERE vctserverdatasyncstatus = 1 AND vctoghrefid = ?", new String[]{recid});
         if(vctcursor!=null) {
             if (vctcursor.moveToFirst()) {
                 data=true;
                 return data;
             }
         }

         Cursor powercursor= dbhandler.getFromDb("SELECT * FROM  oghpowerdata  WHERE powerserverdatasyncstatus = 1 AND poweroghrefid = ?", new String[]{recid});
         if(powercursor!=null) {
             if (powercursor.moveToFirst()) {
                 data=true;
                 return data;
             }
         }

         Cursor statisticscursor= dbhandler.getFromDb("SELECT * FROM  oghreadstatistics  WHERE statisticsserverdatasyncstatus = 1 and statisticsoghid = ? ", new String[]{oghid});
         if(statisticscursor!=null) {
             if (statisticscursor.moveToFirst()) {
                 data=true;
                 return data;
             }
         }


         return data;
     }*/
    public boolean checkudpmDevicesIsPresent() {
        boolean data = false;
        Cursor oghdevice = dbhandler.getFromDb("SELECT * FROM  udpmdevices");
        if (oghdevice != null) {
            if (oghdevice.moveToFirst()) {
                data = true;
            }
        }
        return data;
    }



    public List<String> Listofmonth(String year, String udpmId) {
        List<String> month = new ArrayList<>();
        String udpmRecId = getUDPMRecIdFromUDPMDeviceId(udpmId);
        String query = "select distinct(strftime('%m', udpmdailyusagetimestamp)) from udpmdailyusage where udpmdailyusagedevicerefid= '" + udpmRecId + "' and udpmdailyusagetimestamp like " + "'%" + year + "%'";
        Cursor cursor = dbhandler.getFromDb(query, null);
        if (cursor.moveToFirst()) {
            do {
                month.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return month;
    }

    public List<Integer> Listofmonthdate(String year, String month, String udpmId) {
        List<Integer> date = new ArrayList<>();
        String udpmRecId = getUDPMRecIdFromUDPMDeviceId(udpmId);
        String query = "select distinct(strftime('%d', udpmdailyusagetimestamp)),  udpmdailyusagetimestamp from udpmdailyusage where udpmdailyusagedevicerefid= '" + udpmRecId + "' and udpmdailyusagetimestamp like " + "'%" + year + "-" + month + "%' order by udpmdailyusagetimestamp desc";
        Cursor cursor = dbhandler.getFromDb(query, null);
        if (cursor.moveToFirst()) {
            do {
                date.add(Integer.parseInt(cursor.getString(0)));
                System.out.println("date in db:" + cursor.getString(0));
                System.out.println("full date in db:" + cursor.getString(1));
            } while (cursor.moveToNext());
        }

        return date;
    }

    public int sumofenegry(String year, String month, String udpmId) {
        int sum = 0;
        String udpmRecId = getUDPMRecIdFromUDPMDeviceId(udpmId);
        String query = "SELECT SUM(udpmdailyusageenergy) FROM udpmdailyusage WHERE udpmdailyusagedevicerefid= '" + udpmRecId + "' and udpmdailyusagetimestamp like " + "'%" + year + "-" + month + "%'";
        Cursor cursor = dbhandler.getFromDb(query, null);
        if (cursor.moveToFirst()) {
            do {
                sum = (cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        return sum;
    }

    public List<Integer> selectdataformonth(String year, String month, String udpmId) {
        List<Integer> energyList = new ArrayList<>();
        String udpmRecId = getUDPMRecIdFromUDPMDeviceId(udpmId);
        String query = "SELECT udpmdailyusageenergy FROM  udpmdailyusage WHERE udpmdailyusagedevicerefid= '" + udpmRecId + "' and udpmdailyusagetimestamp like " + "'%" + year + "-" + month + "%' order by udpmdailyusagetimestamp desc";
        Cursor cursor = dbhandler.getFromDb(query, null);
        if (cursor.moveToFirst()) {
            do {
                energyList.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        return energyList;
    }

    public int findMaxMonth(String year, String udpmId) {
        String maxMonth = "0";
        String udpmRecId = getUDPMRecIdFromUDPMDeviceId(udpmId);
        String query = "select max(udpmdailyusageenergysum)udpmdailyusageenergysummax,udpmdailyusagetimestamptimemonth from (select sum(udpmdailyusageenergy) as udpmdailyusageenergysum,strftime('%m',udpmdailyusagetimestamp) as udpmdailyusagetimestamptimemonth from udpmdailyusage where udpmdailyusagedevicerefid= '" + udpmRecId + "' and udpmdailyusagetimestamp like '%" + year + "%' group by strftime('%m',udpmdailyusagetimestamp))temp";
        System.out.println("query: " + query);
        Cursor cursor = dbhandler.getFromDb(query, null);
        if (cursor.moveToFirst()) {
            do {
                maxMonth = cursor.getString(1);
                System.out.println("Max_Month" + cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return Integer.parseInt(maxMonth);
    }

    public int findMinMonth(String year, String udpmId) {
        String minMonth = "0";
        String udpmRecId = getUDPMRecIdFromUDPMDeviceId(udpmId);
        String query = "select min(udpmdailyusageenergysum)udpmdailyusageenergysummin,udpmdailyusagetimestamptimemonth from (select sum(udpmdailyusageenergy) as udpmdailyusageenergysum,strftime('%m',udpmdailyusagetimestamp) as udpmdailyusagetimestamptimemonth from udpmdailyusage where udpmdailyusagedevicerefid= '" + udpmRecId + "' and udpmdailyusagetimestamp like '%" + year + "%' group by strftime('%m',udpmdailyusagetimestamp))temp";
        System.out.println("query: " + query);
        Cursor cursor = dbhandler.getFromDb(query, null);
        if (cursor.moveToFirst()) {
            do {
                minMonth = cursor.getString(1);
                System.out.println("Min_Month" + cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return Integer.parseInt(minMonth);
    }


    public String findMindate(String year, String month, String udpmId) {
        String mindate = null;
        String udpmRecId = getUDPMRecIdFromUDPMDeviceId(udpmId);
        String query = "select min(udpmdailyusageenergy),udpmdailyusagetimestamp from udpmdailyusage where udpmdailyusagedevicerefid= '" + udpmRecId + "' and udpmdailyusagetimestamp like " + "'" + year + "-" + month + "%'";
        Cursor cursor = dbhandler.getFromDb(query, null);
        if (cursor.moveToFirst()) {
            do {
                mindate = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        return mindate;
    }

    public String findMaxdate(String year, String month, String udpmId) {
        String maxdate = null;
        String udpmRecId = getUDPMRecIdFromUDPMDeviceId(udpmId);
        String query = "select max(udpmdailyusageenergy),udpmdailyusagetimestamp from udpmdailyusage where udpmdailyusagedevicerefid= '" + udpmRecId + "' and udpmdailyusagetimestamp like " + "'" + year + "-" + month + "%'";
        Cursor cursor = dbhandler.getFromDb(query, null);
        if (cursor.moveToFirst()) {
            do {
                maxdate = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        return maxdate;
    }

    public void clearSelectedIdFromDb(String recordid) {
        String query = "Delete from udpmdevices where udpmdevrecordid =" + recordid;
        System.out.println("checkUdpmIdAvailabilityAndInsert :" + query);
        Cursor cursor = dbhandler.getFromDb(query);
        int cnt = cursor.getCount();
        if (cnt == 0) {

        }

    }
}

