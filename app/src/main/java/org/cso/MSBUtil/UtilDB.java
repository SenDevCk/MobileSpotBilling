package org.cso.MSBUtil;

import org.cso.MSBModel.*;
import org.cso.MobileSpotBilling.ActvBilling;
import org.cso.MobileSpotBilling.ActvivityMain;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.R.bool;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.FloatMath;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.gson.Gson;

import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.content.res.Resources;

public class UtilDB extends Activity {
	private static final String DB_NAME = "MobileDb.db";
	private static final int DB_VERSION = 9;
	private String pendingRecord;
    private static final String TAG = "UtilDB";

	SQLiteDatabase db;
	MyHelper helper;
    DatabaseHandler dbhandler = null;

	public UtilDB(Context context) {

		System.out.println("context " + context);
        dbhandler = new DatabaseHandler(context, DB_NAME, DB_VERSION);
		helper = new MyHelper(context);
		db = helper.getWritableDatabase();

	}

	public class MyHelper extends SQLiteOpenHelper {

	public MyHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);


	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		System.out.println("create db----");
		try {

			Log.e("DB Input","create db----");
			db.execSQL("CREATE TABLE  if not exists BillInput(DIVISION_CODE  varchar(8), DIVISION_NAME  varchar(40), "
					+ "SUB_DIVISION_CODE  varchar(8), SUB_DIVISION_NAME  varchar(40), ROUTE_SEQUENCE_NO  varchar(10), "
					+ "MRU  varchar(8), AREA_TYPE  varchar(30), CONNECTED_POLE_NIN_NUMBER  varchar(30), CONSUMER_LEGACY_ACC_NO  varchar(20), "
					+ "CONTRACT_AC_NO  varchar(12), CONTRACT_NO  varchar(10), CONSUMER_NAME  varchar(100), ADDRESS  varchar(200), "
					+ "RATE_CATEGORY  varchar(10), BILLING_CYCLE  varchar(10), FEEDER_TYPE  varchar(30), FEEDER_NAME  varchar(40), "
					+ "SHUNT_CAPACITY_REQUIRED_FLAG  varchar(1), SHUNT_CAPACITOR_INSTALLED_FLAG  varchar(1), METER_MANUFACTURER_SR_NO  varchar(30), "
					+ "SCHEDULED_BILLING_DATE  varchar(10), SAP_DEVICE_NO  varchar(18), METER_RENT  varchar(13), "
					+ "PRE_DECIMAL_MTR  varchar(2), OVERALL_MF  varchar(12), NXT_SCH_MTR_RDR_DATE  varchar(10), MTR_CHANGE_FLAG  varchar(1), "
					+ "OLD_MTR_CONSUMPTION  varchar(50), OLD_MTR_DEMAND  varchar(50), NEW_MTR_INTIAL_READING  varchar(50), "
					+ "MTR_CHANGE_DATE  varchar(10), NEW_MTR_RENT  varchar(6), CAT_CHANGE_FLAG  varchar(1), CAT_CHANGE_DATE  varcha(10), "
					+ "OLD_CATEGORY  varchar(10), SANC_LOAD_CHANGE_FLAG  varchar(1), SANC_LOAD_CHG_DT  varchar(10), OLD_SANC_LOAD  varchar(16), "
					+ "PHASE_CODE  varchar(1), SANC_LOAD  varchar(16), CONNECTED_LOAD  varchar(16), CONTRACT_DEMAND  varchar(16), "
					+ "MMC_UNIT_OLD_RC  varchar(16), IIP_DISCOUNT_FLAG  varchar(1), SCH_MTR_READING_DT  varchar(10), PRV_MTR_READING_DT  varchar(10), "
					+ "PRV_BILL_DATE  varchar(10), PRV_READING_KWH  varchar(50), PRV_MTR_READING_NOTE  varchar(4), ED_EXEMPTED_FLAG  varchar(1), "
					+ "ARREAR_ENRGY_CHG  varchar(13), PAY_ACC  varchar(13), ARR_DPS  varchar(13), ED_ARRS  varchar(13), ARRS_OTHR  varchar(13), "
					+ "AMT_KPT_ABEY  varchar(13), PUNITIVE_BILL  varchar(13), DPS_PUNITIVE_BILL  varchar(13), MTR_READING_REASON  varchar(2), "
					+ "DPS_AMT_CURR_MON  varchar(13), INTEREST_ON_SD  varchar(13), INCENTIVE  varchar(13), SECURITY_DEPOSIT  varchar(13), "
					+ "SD_REQ  varchar(13), PROV_BILL_ADJ_AMT  varchar(13), CURR_INST_GNRL  varchar(15), CURR_MON_AMT  varchar(13), "
					+ "TOT_INST_AMT  varchar(13), LAST_PAY_AMT_MADE  varchar(13), LAST_PAY_DATE  varchar(10), LAST_RECEIPT_NO  varchar(20), "
					+ "PREV_KWH_CYCLE1  varchar(13), PREV_KWH_CYCLE2  varchar(13), PREV_KWH_CYCLE3  varchar(13), "
					+ "AVG_KWH_3_CYCLE_DR_KWH  varchar(13), AVG_KWH_12_CYCLE_KWH  varchar(13), AVG_KWH_3_CYCLE_DEMAND  varchar(13), "
					+ "AVG_KWH_12_CYCLE_DEMAND  varchar(13), MSG_1  varchar(150), MSG_2  varchar(150), CHQ_DISH_FLAG  varchar(1), "
					+ "DEMAND_FLAG  varchar(1), COM_CODE  varchar(4), NO_OF_DAYS_PREV_BILL  varchar(5), TEMP_FLAG  varchar(1), "
					+ "SEASON_FLAG  varchar(1), MONTH_SEASONAL  varchar(18), SS_FLAG  varchar(1), IIP_DTY_EXEM_FLAG  varchar(1), "
					+ "PWR_FACTOR  varchar(16), IAS_FLAG  varchar(1), INSTALLATION  varchar(12), METER_MAKE  varchar(10), "
					+ "METER_COM_CONS  varchar(1), DT_CODE  varchar(30), METER_CAP  varchar(10), LAST_ACT_READ  varchar(12), "
					+ "LAST_ACT_READ_DT  varchar(12), PREV_MON_BILL_AMT  varchar(10), FLAG_FOR_BILL_ADJUSTMENT  varchar(12), "
					+ "PL_PERIOD_ENERGY_AMT  varchar(12), PL_PERIOD_GOVT_DUTY_AMT  varchar(12), PL_PERIOD_REBATE_AMT  varchar(12), "
					+ "IIP_CHARGES  varchar(12), SHUNT_CAP_CHARGES  varchar(12), AMT_1  varchar(12), AMT_2  varchar(12), AMT_3  varchar(12), "
					+ "DATE_1  varchar(12), DATE_2  varchar(12), DATE_3  varchar(12), FLAG_1  varchar(12), FLAG_2  varchar(12),  MTR_RDR_ID  varchar(20), "						
					+ "SBM_NUMBER  varchar(16), BILLEDFLAG varchar(2),  PRIMARY KEY (CONTRACT_AC_NO )  );");
			
			Log.e("DB Output","create db----");
			 db.execSQL("CREATE TABLE  if not exists BillOutput( Company  varchar(4),  BillMonth  varchar(10),  BillNo  varchar(20),  "
			 		+ "Division  varchar(48),  SubDivision  varchar(48),  CANumber  varchar(12),  LegacyNumber  varchar(20),  MRU  varchar(8),  "
			 		+ "MtrMake  varchar(2),  Name  varchar(100),  Address  varchar(200),  PoleNo  varchar(30),  MtrNo  varchar(30),  "
			 		+ "Phase  varchar(1),  Category  varchar(10),  SanctLoad  varchar(16),  ConnectedLoad  varchar(16),  CD  varchar(16),  SD  varchar(13),  "
			 		+ "BillDays  varchar(3),  PreviusReading  varchar(50),  PrevusMtrRdgDt  varchar(10),  PreviusMtrReadingNote  varchar(4),  "
			 		+ "CurrentReading  varchar(17),  CurrentMtrRdgDt  varchar(10),  CurrentMtrReadingNote  varchar(2),  MF  varchar(12),  "
			 		+ "Consumption  varchar(17),  RecordedDemd  varchar(17),  PowerFactor  varchar(4),  MMCUnits  varchar(10),  BilledUnits  varchar(17),  "
			 		+ "Average  varchar(3),  Type  varchar(10),  PaymentOnAccount  varchar(13),  ArrearEnergyDues  varchar(13),  ArrearDPs  varchar(13),  "
			 		+ "ArrearOthers  varchar(13),  ArrearSubTotal_A  varchar(13),  CurrentEnergyCharges  varchar(13),  CurrentMonthDps  varchar(13),  "
			 		+ "FixDemdCharge  varchar(13),  ExcessDemdCharge  varchar(13),  ElectricityDuty  varchar(13),  MeterRent  varchar(13),  "
			 		+ "ShauntCapCharge  varchar(13),  OtherCharge  varchar(13),  Installment  varchar(13),  SubTotal_B  varchar(13),  InterestOnSD_C  varchar(13),  "
			 		+ "Incentive  varchar(13),  RebateOnMMC  varchar(13),  GrossTotal  varchar(13),  Rebate   varchar(13),  AmtPayableUptoDt  varchar(10),  "
			 		+ "AmtPayableUptoAmt  varchar(13),  mtPayablePYDt  varchar(10),  AmtPayablePYAmt  varchar(13),  AmtPayableAfterDt  varchar(10),  "
			 		+ "AmtPayableAfterAmt  varchar(13),  LastPaymentAmt  varchar(10),  LastPaidDate  varchar(10),  ReceiptNumber  varchar(20), MESSAGE10 varchar(20), "
			 		+ "MTR_READER_ID VARCHAR(20), AREATYPE VARCHAR(20), MobileNo varchar(10), REC_DATE_TIME varchar(30), GOVT_SUB varchar(20), INT_DISC varchar(20), METER_CGST varchar(20), METER_SGST varchar(20), PRIMARY KEY (CANumber ) ); ");
			
			 //Log.e("DB TempSAPOutput","create db----");
			 //db.execSQL("CREATE TABLE  if not exists BillOutputTemp(INSTALLATION varchar(12), TEMPOUTPUT varchar(max);");
			 /*db.execSQL("CREATE TABLE  if not exists UserInfo(company_id character varying(1),meterreader_id character varying(15),sdo_code character(4), "
					+ "MRUs character(150),bill_month character(6),imie_no character(30),activeYN character(1),active_MRU character(4), "
					+ "password character(30),CurAppVersion numeric(2,3),printerid character(30),printertype character(30));");*/
			
			 Log.e("DB UserInfo","create db----"); 
			db.execSQL("CREATE TABLE  if not exists UserInfo(meterreader_id character varying(1),METER_READER_NAME character varying(15),password character(4), "
					 + "IMEINo varchar(20),printerid character(30),printertype character(30));");
			
				
					//company_id,meterreader_id,sdo_code,MRUs,bill_month,imie_no,active_MRU,activeYN,password,CurAppVersion
			
			 Log.e("DB SAPInput","create db----");
			db.execSQL("CREATE TABLE  if not exists SAPInput(CANumber Varchar(10), Installation  Varchar(12), SCHEDULED_BILLING_DATE varchar(10), MtrReadingDate  varchar(10), MtrReadingNote  varchar(2), "
					+ " CurrentReadingKwh  varchar(17), CurrentReadingKVAH  varchar(10), MaxDemd  varchar(17), PowerFactor  varchar(4), Latitude  varchar(20), Longitude  varchar(20), ProcessedFlag varchar(2), SAP_DEVICE_NO varchar2(20), "
					+ " MobPoleFlag varchar(1), MobileNo varchar(10), PoleNo  varchar(30), SYNCFLAG varchar(2), "
					+ " MESSAGE varchar(50), MSGID varchar(2));");

			Log.e("DB SAPInput","create db----");
			db.execSQL("CREATE TABLE  if not exists SAPBlueInput(CANumber Varchar(10), Installation  Varchar(12), SCHEDULED_BILLING_DATE varchar(10), MtrReadingDate  varchar(10), MtrReadingNote  varchar(2), "
					+ " CurrentReadingKwh  varchar(17), CurrentReadingKVAH  varchar(10), MaxDemd  varchar(17), PowerFactor  varchar(4), Latitude  varchar(20), Longitude  varchar(20), ProcessedFlag varchar(2), SAP_DEVICE_NO varchar2(20), "
					+ " MobPoleFlag varchar(1), MobileNo varchar(10), PoleNo  varchar(30), SYNCFLAG varchar(2), "
					+ " MESSAGE varchar(50), MSGID varchar(2));");


			Log.e("DB SAPUpdate Consumers","create db----");
			db.execSQL("CREATE TABLE  if not exists SAPUpdateCons(strPayrollNo Varchar(10), LegacyNumber varchar(20), MtrReadingDate varchar(10), MRU  varchar(8), "
					+ " SUB_DIVISION_CODE varchar(8), CONSUMER_NAME varchar(100), PoleNo varchar(20), MobileNo varchar(10), SuccessFlag varchar(1), MESSAGE varchar(50), MSGID varchar(2));");
		
			Log.e("DB validDevice","create db----");
			db.execSQL("CREATE TABLE  if not exists validDevice(IMEINo varchar(20), DEV_VALID varchar(2), version character(20));");
			
			Log.e("DB Abnormality","create db----");
			db.execSQL("CREATE TABLE  if not exists Abnormality(CANumber Varchar(10), MtrReadingDate varchar(10), MRU varchar(8), SUB_DIVISION_CODE varchar(8), Remarks varchar(100), Abnormality varchar(30),  UploadFlag varchar(2));");
			
			Log.e("DB ImageData","create db----");
			db.execSQL("CREATE TABLE  if not exists ImageData(CANumber Varchar(10), ImageName varchar(100), ImagePath varchar(200), CompressFlag varchar(2), UploadFlag varchar(2));");
			
			// Created by Kishore
			Log.e("DB validUsr","create db----");
			db.execSQL("CREATE TABLE  if not exists validUsr(USER_VAL character(4));");

			Log.e("DB udpmdevices","create db----");
			String udpmdevTableQuery = "create table if not exists udpmdevices (udpmdevrecordid INTEGER primary key autoincrement not null, udpmdevdeviceid VARCHAR)";
			db.execSQL(udpmdevTableQuery);

			Log.e("DB udpminfo","create db----");
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
			db.execSQL(udpminfoTableQuery);


			Log.e("DB CommonBillingInfo","create db----");
			String commonBillingInfo="CREATE TABLE IF NOT EXISTS CommonBillingInfo (" +
					"PL_MAXLIMIT VARCHAR, "+
					"PL_BILLEDCOUNT VARCHAR, "+
					"MD_MAXLIMIT VARCHAR, "+
					"MD_PREVIOUSMONTH VARCHAR, "+
					"MD_BILLEDCOUNT VARCHAR );";
			db.execSQL(commonBillingInfo);
			insertIntoCommonBillingInfo(db);
			Log.e("DB CommonBillingInfo","created db----");

			Log.e("DB","create db completed");
		} catch (Exception e) {
			Log.e("onCreate",e.getMessage());
			e.printStackTrace();
		}

		System.out.println("create db success");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(MyHelper.class.getName(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("drop table if exists BillOutPut");
			db.execSQL("drop table if exists BillInput");
			// db.execSQL("drop table if exists BILLING_PERIOD" );
			db.execSQL("drop table if exists UserInfo");
			db.execSQL("drop table if exists SAPInput");
			//db.execSQL("drop table if exists BillOutputTemp");
			db.execSQL("drop table if exists SAPUpdateCons");
			db.execSQL("drop table if exists ImageData");
			db.execSQL("drop table if exists Abnormality");
			db.execSQL("drop table if exists udpmdevices");
			db.execSQL("drop table if exists udpminfo");
			db.execSQL("drop table if exists SAPBlueInput");
			onCreate(db);
		}

	}

	public void truncateTable(String table) {
		db.execSQL("DELETE FROM " + table);
	}
		
	public String insertIntoSbmOut(String uploadStatus) {

		return "";
	}


	public void insertIntoCommonBillingInfo(SQLiteDatabase db){
		Log.e("UtilDB", "Inserting Value to CommonBillingInfo");
		ContentValues contentValues=new ContentValues();
		contentValues.put("PL_MAXLIMIT", "0");
		contentValues.put("PL_BILLEDCOUNT", "0");
		contentValues.put("MD_MAXLIMIT", "0");
		contentValues.put("MD_PREVIOUSMONTH", "0");
		contentValues.put("MD_BILLEDCOUNT", "0");
		db.insert("CommonBillingInfo", null, contentValues);
	}

	public HashMap<String, Integer> getCommonBillingInfo(){
		SQLiteDatabase db=helper.getReadableDatabase();
		Cursor cursor=db.query("CommonBillingInfo", null, null,null,null,null,null);
		HashMap<String, Integer> billingInfo=new HashMap<String, Integer>();
		if(cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			billingInfo.put("PL_MAXLIMIT", Integer.parseInt(cursor.getString(cursor.getColumnIndex("PL_MAXLIMIT"))));
			billingInfo.put("PL_BILLEDCOUNT", Integer.parseInt(cursor.getString(cursor.getColumnIndex("PL_BILLEDCOUNT"))));
			billingInfo.put("MD_MAXLIMIT", Integer.parseInt(cursor.getString(cursor.getColumnIndex("MD_MAXLIMIT"))));
			billingInfo.put("MD_PREVIOUSMONTH", Integer.parseInt(cursor.getString(cursor.getColumnIndex("MD_PREVIOUSMONTH"))));
			billingInfo.put("MD_BILLEDCOUNT", Integer.parseInt(cursor.getString(cursor.getColumnIndex("MD_BILLEDCOUNT"))));
		}
		return billingInfo;
	}

	public void updatePLMDCountCommonBillingInfo(boolean isPL, boolean isMD){
		SQLiteDatabase db=helper.getWritableDatabase();
		ContentValues contentValues=new ContentValues();
		HashMap<String, Integer> currentBillingInfo=getCommonBillingInfo();

		if(isPL){
			int oldPLCount=currentBillingInfo.get("PL_BILLEDCOUNT");
			Log.e("UtilDB=>","Old PL Count=>"+oldPLCount+" New Count=>"+(oldPLCount+1)+" isPL=>"+isPL+" isMD=>"+isMD );
			contentValues.put("PL_BILLEDCOUNT",Integer.toString(oldPLCount+1));
		}
		if(isMD){
			int oldMDCount=currentBillingInfo.get("MD_BILLEDCOUNT");
			Log.e("UtilDB=>","Old MD Count=>"+oldMDCount+" New Count=>"+(oldMDCount+1)+" isPL=>"+isPL+" isMD=>"+isMD );
			contentValues.put("MD_BILLEDCOUNT",Integer.toString(oldMDCount+1));
		}
		db.update("CommonBillingInfo",contentValues,null,null);
	}


	public int InsertOutput(JSONArray jarray) 
		{
			int intCounter=0;
			db=helper.getWritableDatabase();
			try{
			for (int i = 0; i < jarray.length(); i++) 
		    {
			JSONObject jsonData = jarray.getJSONObject(i);
			System.out.println("Pre InsertOutput ==>> "+jsonData);
			String  record=new String();
			Log.e("InsertOutput ","Start");
/*			record=jsonData.getString("ANLAGE")+"|"+jsonData.getString("GERNR")+"|"+jsonData.getString("ADATSOLL")+
			"|"+jsonData.getString("METER_READER_NOT")+"|"+jsonData.getString("BILL_STATUS")+"|"+jsonData.getString("TOT_UNIT_BILLED")+
			"|"+jsonData.getString("BILL_DAYS")+"|"+jsonData.getString("PYMT_ON_ACCOUNT")+"|"+jsonData.getString("ARREAR_ENERGY")+
			"|"+jsonData.getString("ARREAR_ED")+"|"+jsonData.getString("ARREAR_DPS")+"|"+jsonData.getString("ARREAR_PUNITIVE_AMT")+
			"|"+jsonData.getString("ARREAR_PUNITIVE_DPS")+"|"+jsonData.getString("ARREAR_OTHER_CHG")+"|"+jsonData.getString("TOTAL_ARREAR")+
			"|"+jsonData.getString("CURR_DPS_KEPT_ON_ABEY")+"|"+jsonData.getString("CURRENT_MONTH_DPS")+"|"+jsonData.getString("FIXED_DEMAND_CHRG")+
			"|"+jsonData.getString("EXCESS_DEMAND_CHRG")+"|"+jsonData.getString("ENERGY_CHARGE")+"|"+jsonData.getString("ENERGY_CHARGE_1H1")+
			"|"+jsonData.getString("ENERGY_CHARGE_2H1")+"|"+jsonData.getString("ENERGY_CHARGE_3H1")+"|"+jsonData.getString("MINIMUM_CHARGE")+
			"|"+jsonData.getString("ELECTRICITY_DUTY")+"|"+jsonData.getString("METER_RENT")+"|"+jsonData.getString("SHUNT_CAP_CHARGE")+
			"|"+jsonData.getString("OTHER_CHARGES")+"|"+jsonData.getString("INSTALLMENT_AMOUNT")+"|"+jsonData.getString("TOTAL_CURRENT_AMOUNT")+
			"|"+jsonData.getString("NET_AMT_PYBL")+"|"+jsonData.getString("NET_AMT_PBL_BY_DUE_DT")+"|"+jsonData.getString("NET_AMT_PBL_BTWN_DUE_DT")+
			"|"+jsonData.getString("AMT_PYBL_AFTR_DUE_DATE")+"|"+jsonData.getString("LAST_PYMT_AMT")+"|"+jsonData.getString("LAST_PYMT_RECPT_NO")+
			"|"+jsonData.getString("LAST_PYMT_DT")+"|"+jsonData.getString("REGISTER")+"|"+jsonData.getString("CURR_MTR_RDNG_DATE")+
			"|"+jsonData.getString("PRE_MTR_RDNG")+"|"+jsonData.getString("CURR_MTR_RDNG")+"|"+jsonData.getString("DATE_OF_INVOICE")+
			"|"+jsonData.getString("BILL_MONTH")+"|"+jsonData.getString("DUE_DATE")+"|"+jsonData.getString("POWER_FACTOR")+
			"|"+jsonData.getString("PPI_LEVIABLE")+"|"+jsonData.getString("FLAG")+"|"+jsonData.getString("STATUS")+
			"|"+jsonData.getString("REASON")+"|"+jsonData.getString("MESSAGE");*/
		
			Calendar today = Calendar.getInstance();

			today = new GregorianCalendar(
					Integer.parseInt(jsonData.getString("DUE_DATE").substring(0, 4)),
					Integer.parseInt(jsonData.getString("DUE_DATE").substring(5, 7)) - 1,
					Integer.parseInt(jsonData.getString("DUE_DATE").substring(8, 10)));
			
			Log.e("DUE DATE SPLIT", jsonData.getString("DUE_DATE").substring(0, 4) + " == " + jsonData.getString("DUE_DATE").substring(5, 7) + " == " + jsonData.getString("DUE_DATE").substring(8, 10));
			
			SimpleDateFormat SAPInformat = new SimpleDateFormat("dd.MM.yyyy");
			//today.add(Calendar.DATE, 15);
			Log.e("today", today.getTime().toString());
			String lsDate15 = SAPInformat.format(today.getTime()) ;
			today.add(Calendar.DATE, 10);
			String lsDate25 = SAPInformat.format(today.getTime()) ;
			
			Log.e("DUE_DATE", jsonData.getString("DUE_DATE").toString());
			Log.e("lsDate15", lsDate15);
			Log.e("lsDate25", lsDate25);
			
			String strAVG_KWH_12_CYCLE_KWH = "0";
			String strAVG_KWH_3_CYCLE_DR_KWH = "0";
			
			if(!UtilAppCommon.in.AVG_KWH_12_CYCLE_KWH.equalsIgnoreCase(""))
				strAVG_KWH_12_CYCLE_KWH = UtilAppCommon.in.AVG_KWH_12_CYCLE_KWH;
			if(!UtilAppCommon.in.AVG_KWH_3_CYCLE_DR_KWH.equalsIgnoreCase(""))
				strAVG_KWH_3_CYCLE_DR_KWH = UtilAppCommon.in.AVG_KWH_3_CYCLE_DR_KWH;
			
			Log.e("AVG_KWH_12_CYCLE_KWH", UtilAppCommon.in.AVG_KWH_12_CYCLE_KWH);
			Log.e("AVG_KWH_3_CYCLE_DR_KWH", UtilAppCommon.in.AVG_KWH_3_CYCLE_DR_KWH);	
			Double avg;
			if (UtilAppCommon.SAPIn.MtrReadingNote.equalsIgnoreCase("MD"))
				avg = (Double.parseDouble(jsonData.getString("BILL_DAYS")) * Double.parseDouble(strAVG_KWH_12_CYCLE_KWH));
			else
				avg = (Double.parseDouble(jsonData.getString("BILL_DAYS")) * Double.parseDouble(strAVG_KWH_3_CYCLE_DR_KWH));

			avg = Math.ceil(avg);
			
						
			record=   UtilAppCommon.in.COM_CODE + "|" 
					+ jsonData.getString("BILL_MONTH")+ "|"
					+ jsonData.getString("INVOICENO")+ "|"
					+ UtilAppCommon.in.DIVISION_CODE + "-" + UtilAppCommon.in.DIVISION_NAME + "|"
					+ UtilAppCommon.in.SUB_DIVISION_CODE + "-" + UtilAppCommon.in.SUB_DIVISION_NAME + "|"
					+ UtilAppCommon.in.CONTRACT_AC_NO + "|"
					+ UtilAppCommon.in.CONSUMER_LEGACY_ACC_NO + "|"
					+ UtilAppCommon.in.MRU + "|"
					+ UtilAppCommon.in.METER_COM_CONS + "|"
					+ UtilAppCommon.in.CONSUMER_NAME + "|"
					+ UtilAppCommon.in.ADDRESS + "|"
					+ UtilAppCommon.in.CONNECTED_POLE_NIN_NUMBER + "|"
					+ UtilAppCommon.in.METER_MANUFACTURER_SR_NO + "|"
					+ UtilAppCommon.in.PHASE_CODE + "|"
					+ UtilAppCommon.in.RATE_CATEGORY + "|"
					+ UtilAppCommon.in.SANC_LOAD + "|"
					+ UtilAppCommon.in.CONNECTED_LOAD + "|"
					+ UtilAppCommon.in.CONTRACT_DEMAND + "|"
					+ UtilAppCommon.in.SECURITY_DEPOSIT + "|"
					+ jsonData.getString("BILL_DAYS")+"|"
					+ jsonData.getString("PRE_MTR_RDNG") + "|"
					+ jsonData.getString("PRE_MTR_RDNG_DATE") + "|"
					+ UtilAppCommon.in.PRV_MTR_READING_NOTE + "|"
					+ jsonData.getString("CURR_MTR_RDNG") + "|"
					+ jsonData.getString("CURR_MTR_RDNG_DATE") + "|"
					+ UtilAppCommon.SAPIn.MtrReadingNote + "|"
					+ UtilAppCommon.in.OVERALL_MF + "|"
					+(Float.parseFloat(jsonData.getString("CURR_MTR_RDNG")) - Float.parseFloat(jsonData.getString("PRE_MTR_RDNG"))) + "|"
					+ UtilAppCommon.SAPIn.MaxDemd + "|"
					+ UtilAppCommon.SAPIn.PowerFactor + "|"
					+ jsonData.getString("MINIMUM_UNIT")+ "|"
					+ jsonData.getString("TOT_UNIT_BILLED")+ "|"
					+ avg + "" + "|"
					+ jsonData.getString("BILL_STATUS") + "|"
					+ jsonData.getString("PYMT_ON_ACCOUNT") + "|"
					+ (Float.parseFloat(jsonData.getString("ARREAR_ENERGY")) + Float.parseFloat(jsonData.getString("ARREAR_ED"))) + "|"
					+ jsonData.getString("ARREAR_DPS") + "|"
					+ (Float.parseFloat(jsonData.getString("ARREAR_PUNITIVE_AMT"))+ Float.parseFloat(jsonData.getString("ARREAR_PUNITIVE_DPS"))+ Float.parseFloat(jsonData.getString("ARREAR_OTHER_CHG"))) + "|"
					+ jsonData.getString("TOTAL_ARREAR") + "|"
					+ (Float.parseFloat(jsonData.getString("ENERGY_CHARGE"))+ Float.parseFloat(jsonData.getString("ENERGY_CHARGE_1H1"))+ Float.parseFloat(jsonData.getString("ENERGY_CHARGE_2H1"))+ Float.parseFloat(jsonData.getString("ENERGY_CHARGE_3H1"))) + "|"
					+ jsonData.getString("CURRENT_MONTH_DPS") + "|"
					+ jsonData.getString("FIXED_DEMAND_CHRG") + "|"
					+ jsonData.getString("EXCESS_DEMAND_CHRG") + "|"
					+ jsonData.getString("ELECTRICITY_DUTY") + "|"
					+ jsonData.getString("METER_RENT") + "|"
					+ jsonData.getString("SHUNT_CAP_CHARGE") + "|"
					+ jsonData.getString("OTHER_CHARGES") + "|"
					+ jsonData.getString("INSTALLMENT_AMOUNT") + "|"
					+ jsonData.getString("TOTAL_CURRENT_AMOUNT") + "|"
					+ UtilAppCommon.in.INTEREST_ON_SD + "|"
					+ UtilAppCommon.in.INCENTIVE + "|"
					+ jsonData.getString("INCENTIVE_UNDER_IIP") + "|"
					+ jsonData.getString("NET_AMT_PYBL") + "|"
					+ jsonData.getString("PPI_LEVIABLE") + "|"
					+ lsDate15 + "|"
					+ jsonData.getString("NET_AMT_PBL_BY_DUE_DT") + "|"
					+ lsDate25 + "|"
					+ jsonData.getString("NET_AMT_PBL_BTWN_DUE_DT") + "|"
					+ lsDate25 + "|"
					+ jsonData.getString("AMT_PYBL_AFTR_DUE_DATE") + "|"
					+ jsonData.getString("LAST_PYMT_AMT") + "|"
					+ jsonData.getString("LAST_PYMT_DT") + "|"
					+ jsonData.getString("LAST_PYMT_RECPT_NO")+ "|"
					+ jsonData.getString("MESSAGE10") + "|"
					+ UtilAppCommon.ui.METER_READER_ID + "|"
					+ UtilAppCommon.in.AREA_TYPE + "|"
					+ UtilAppCommon.in.METER_CAP + "|"
					+ jsonData.getString("REC_DATE_TIME") + "|"
					+ jsonData.getString("GOVT_SUB") + "|"
					+ jsonData.getString("INT_DISC")+ "|"
					+ jsonData.getString("METER_CGST")+ "|"
					+ jsonData.getString("METER_SGST");
	
			Log.e("Output Record => ", record);
			
			//Inserting records into Application Database in the handset 
			insertIntoBillOutput(record,db);
			intCounter=intCounter+1;
		}
		}catch(Exception ex)
		{
			intCounter=0;
			Log.e("InsertOutput", ex.getMessage());
		}
		return intCounter;
	}
	
	public long insertIntoBillOutput(String input,SQLiteDatabase dbobj) {
		db = dbobj;
		long rowId = 0;
		String[] values = input.split("[|]");
		//Log.e("insertIntoBillOutput", "Start");
		Log.e("data length----", "" + values.length);
		int length = values.length;
		SQLiteStatement stmt = db
				.compileStatement("INSERT OR IGNORE INTO BillOutput VALUES(  "
						+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
						+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
						+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
						+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
		try 
		{
			for (int i = 0; i < length ; i++) {
				stmt.bindString(i + 1, values[i].trim());
		}
			rowId = stmt.executeInsert();
		} catch (Exception e) {
			Log.e("InsertIntoBillOutput", e.getMessage());
			e.printStackTrace();
		}
		Log.e("InsertIntoBillOutput", "Done");
		return rowId;
	}
	
	@SuppressLint("NewApi")
	public void updateRecompressedImage() {
		db = helper.getWritableDatabase();
		try {
			
			String query1=String.format("UPDATE ImageData SET CompressFlag='0'");
			SQLiteStatement stmt=db.compileStatement(query1);
			stmt.executeUpdateDelete();
			
			//Log.e("rowId==>> ", rowId + "");
		} catch (Exception e) {
			Log.e("UpdateCompressedImage E", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void insertIntoImageData(String inputData)
	{
		db = helper.getWritableDatabase();
		String[] values = inputData.split("[~]");
		System.out.println("data length----" + values.length);
		//int length = values.length;
		//Log.e("insertIntoImageData ", inputData + " ==>> " + values.length);

		SQLiteStatement stmt = db.compileStatement("INSERT INTO ImageData (CANumber, ImageName, ImagePath ,CompressFlag ,UploadFlag) "
				+ " VALUES(?,?,?,?,?);");

			try {
				for (int i = 0; i < values.length; i++) {
					stmt.bindString(i + 1, values[i].trim());
				}
				stmt.executeInsert();
			} catch (Exception e) {
				Log.e("insertIntoImageData E ", e.getMessage());
				e.printStackTrace();
			}

		//return "";
	}

	@SuppressLint("NewApi")
	public Cursor getUnCompressedImage()
	{
		try {
			db = helper.getWritableDatabase();
			String query1=String.format("SELECT * FROM ImageData WHERE CompressFlag = '0'");
			Cursor c = db.rawQuery(query1,null);
			if(c.moveToFirst())
			{
				return c;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("getUnCompressedImage E", e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@SuppressLint("NewApi")
	public Cursor getNonUploadedImage()
	{
		try {
			db = helper.getWritableDatabase();
			String query1=String.format("SELECT * FROM ImageData WHERE UploadFlag != '1'");
			Cursor c = db.rawQuery(query1,null);
			if(c.moveToFirst())
			{
				return c;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("getNonUploadedImage E", e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@SuppressLint("NewApi")
	public String getNonUploadedImageCount()
	{

		try {
			String strCount = "";
			db = helper.getWritableDatabase();
			SQLiteStatement stmt = db.compileStatement("SELECT count(*) FROM ImageData WHERE UploadFlag != '1'");
			//String query1=String.format("SELECT count(*) FROM ImageData WHERE UploadFlag != '1'");
			//Cursor c = db.rawQuery(query1,null);
			strCount = stmt.simpleQueryForString();
			return strCount;

		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			Log.e("getNonUploadedImage E", e.getMessage());
			e.printStackTrace();
			return "0";
		}
	}


	@SuppressLint("NewApi")
	public Cursor getUnCompressedImage(String AccNo)
	{
		try {
			db = helper.getWritableDatabase();
			String query1=String.format("SELECT * FROM ImageData WHERE CANumber='%s'", AccNo);
			Cursor c = db.rawQuery(query1,null);
			if(c.moveToFirst())
			{
				return c;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("getUnCompressedImage E", e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@SuppressLint("NewApi")
	public void UpdateCompressedImage(String AccNo) {
		db = helper.getWritableDatabase();
		try {

			String query1=String.format("UPDATE ImageData SET CompressFlag='1' WHERE CANumber='%s'", AccNo);
			SQLiteStatement stmt=db.compileStatement(query1);
			stmt.executeUpdateDelete();

			//Log.e("rowId==>> ", rowId + "");
		} catch (Exception e) {
			Log.e("UpdateCompressedImage E", e.getMessage());
			e.printStackTrace();
		}
	}
	// End

	@SuppressLint("NewApi")
	public void UpdateUploadImage(String AccNo) {
		db = helper.getWritableDatabase();
		try {

			String query1=String.format("UPDATE ImageData SET UploadFlag='1' WHERE CANumber='%s'", AccNo);
			SQLiteStatement stmt=db.compileStatement(query1);
			stmt.executeUpdateDelete();

			//Log.e("rowId==>> ", rowId + "");
		} catch (Exception e) {
			Log.e("UpdateUploadImage E", e.getMessage());
			e.printStackTrace();
		}
	}
	// End

	@SuppressLint("NewApi")
	public void UpdateInputTable(String AccNo, String strBillFlg) {
		Log.e("UpdateInputTable ==>> ", "Started");
		db = helper.getWritableDatabase();
		long rowId = 0;

		try {

			String query1=String.format("UPDATE BillInput SET BILLEDFLAG='%s' WHERE CONTRACT_AC_NO='%s'",strBillFlg, AccNo);
			SQLiteStatement stmt=db.compileStatement(query1);
			stmt.executeUpdateDelete();

			//Log.e("rowId==>> ", rowId + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("UpdateInputTable ==>> ", "Completed");
	}
	// End

	public Cursor copyToOutputStruct(String AccNo)
	{
		try {
			String DueDate="";
			db = helper.getReadableDatabase();
			String query=String.format("SELECT * FROM BillOutput WHERE CANumber ='%s' ", AccNo);
			Cursor c = db.rawQuery(query,null);
			if(c.moveToFirst())
			{
				UtilAppCommon.out = new StructOutput();
				UtilAppCommon.copyResultsetToOutputClass(c);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("copyToOutputStruct E", e.getMessage());
		}
		return null;
	}

	public int InsertInputBulk(JSONArray jarray)
	{
		int intCounter=0;
		String strNoRouteSeq = "";
		db=helper.getWritableDatabase();
		try{
		for (int i = 0; i < jarray.length(); i++)
	    {
			//Log.e("UtilDB ==>> ", "InsertInputBulk Len ==>> " + jarray.length());
			JSONObject jsonData = jarray.getJSONObject(i);

			if( i == 0 || i == 1 ){
				Log.e("UtilDB fetch pl md "+i, "Fetching PL_COUNT, MD_COUNT");
				ContentValues contentValues=new ContentValues();

				contentValues.put("PL_MAXLIMIT",jsonData.getString("PL_COUNT").toString().trim());
				contentValues.put("MD_MAXLIMIT",jsonData.getString("MD_COUNT").toString().trim());
				contentValues.put("MD_PREVIOUSMONTH",jsonData.getString("PREV_MD_COUNT").toString().trim());
				Log.e("UtilDB Fetch PLMax", "PL is "+jsonData.getString("PL_COUNT").toString().trim());
				Log.e("UtilDB Fetch MDMax", "MD is "+jsonData.getString("MD_COUNT").toString().trim());
				Log.e("UtilDB Fetch MDPREv", "MDPREV is "+jsonData.getString("PREV_MD_COUNT").toString().trim());
				db.update("CommonBillingInfo",contentValues,null,null);
			}

			System.out.println("Pre Concatinatiokjjkjkjkkjkkln"+jsonData);
			String  record=new String();
			System.out.println("Pre Concatination");

			String strPrevMtdRead = jsonData.getString("PRV_READING_KWH");
			if(jsonData.getString("PRV_READING_KWH").toString().trim().equalsIgnoreCase("") || jsonData.getString("PRV_READING_KWH").toString() == null)
				strPrevMtdRead = "0";

			String strLastActRead = jsonData.getString("LAST_ACT_READ");
			if(jsonData.getString("LAST_ACT_READ").toString().trim().equalsIgnoreCase("") || jsonData.getString("LAST_ACT_READ").toString() == null)
				strLastActRead = "0";

			String strAVG_KWH_3_CYCLE_DR_KWH = "0";
			String strAVG_KWH_12_CYCLE_KWH = "0";

			String preMeterStatus = jsonData.getString("PRV_MTR_READING_NOTE");
			if(preMeterStatus.equalsIgnoreCase("pa") || preMeterStatus.equalsIgnoreCase("pl"))
				preMeterStatus = "PL";
			else
				preMeterStatus = jsonData.getString("PRV_MTR_READING_NOTE");

			if(!jsonData.getString("AVG_KWH_3_CYCLE_DR_KWH").equalsIgnoreCase(""))
				strAVG_KWH_3_CYCLE_DR_KWH = jsonData.getString("AVG_KWH_3_CYCLE_DR_KWH");

			if(!jsonData.getString("AVG_KWH_12_CYCLE_KWH").equalsIgnoreCase(""))
				strAVG_KWH_12_CYCLE_KWH = jsonData.getString("AVG_KWH_12_CYCLE_KWH");

			record=jsonData.getString("DIVISION_CODE")+"|"+jsonData.getString("DIVISION_NAME")+"|"+jsonData.getString("SUB_DIVISION_CODE")+
			"|"+jsonData.getString("SUB_DIVISION_NAME")+"|"+jsonData.getString("ROUTE_SEQUENCE_NO")+"|"+jsonData.getString("MRU")+
			"|"+jsonData.getString("AREA_TYPE")+"|"+jsonData.getString("CONNECTED_POLE_NIN_NUMBER")+"|"+jsonData.getString("CONSUMER_LEGACY_ACC_NO")+
			"|"+jsonData.getString("CONTRACT_AC_NO")+"|"+jsonData.getString("CONTRACT_NO")+"|"+jsonData.getString("CONSUMER_NAME")+
			"|"+jsonData.getString("ADDRESS")+"|"+jsonData.getString("RATE_CATEGORY")+"|"+jsonData.getString("BILLING_CYCLE")+
			"|"+jsonData.getString("FEEDER_TYPE")+"|"+jsonData.getString("FEEDER_NAME")+"|"+jsonData.getString("SHUNT_CAPACITY_REQUIRED_FLAG")+
			"|"+jsonData.getString("SHUNT_CAPACITOR_INSTALLED_FLAG")+"|"+jsonData.getString("METER_MANUFACTURER_SR_NO")+
			"|"+jsonData.getString("SCHEDULED_BILLING_DATE")+"|"+jsonData.getString("SAP_DEVICE_NO")+"|"+jsonData.getString("METER_RENT")+
			"|"+jsonData.getString("PRE_DECIMAL_MTR")+"|"+jsonData.getString("OVERALL_MF")+"|"+jsonData.getString("NXT_SCH_MTR_RDR_DATE")+
			"|"+jsonData.getString("MTR_CHANGE_FLAG")+"|"+jsonData.getString("OLD_MTR_CONSUMPTION")+"|"+jsonData.getString("OLD_MTR_DEMAND")+
			"|"+jsonData.getString("NEW_MTR_INTIAL_READING")+"|"+jsonData.getString("MTR_CHANGE_DATE")+"|"+jsonData.getString("NEW_MTR_RENT")+
			"|"+jsonData.getString("CAT_CHANGE_FLAG")+"|"+jsonData.getString("CAT_CHANGE_DATE")+"|"+jsonData.getString("OLD_CATEGORY")+
			"|"+jsonData.getString("SANC_LOAD_CHANGE_FLAG")+"|"+jsonData.getString("SANC_LOAD_CHG_DT")+"|"+jsonData.getString("OLD_SANC_LOAD")+
			"|"+jsonData.getString("PHASE_CODE")+"|"+jsonData.getString("SANC_LOAD")+"|"+jsonData.getString("CONNECTED_LOAD")+
			"|"+jsonData.getString("CONTRACT_DEMAND")+"|"+jsonData.getString("MMC_UNIT_OLD_RC")+"|"+jsonData.getString("IIP_DISCOUNT_FLAG")+
			"|"+jsonData.getString("SCH_MTR_READING_DT")+"|"+jsonData.getString("PRV_MTR_READING_DT")+"|"+jsonData.getString("PRV_BILL_DATE")+
			"|"+strPrevMtdRead+"|"+preMeterStatus+"|"+jsonData.getString("ED_EXEMPTED_FLAG")+
			"|"+jsonData.getString("ARREAR_ENRGY_CHG")+"|"+jsonData.getString("PAY_ACC")+"|"+jsonData.getString("ARR_DPS")+"|"+jsonData.getString("ED_ARRS")+
			"|"+jsonData.getString("ARRS_OTHR")+"|"+jsonData.getString("AMT_KPT_ABEY")+"|"+jsonData.getString("PUNITIVE_BILL")+
			"|"+jsonData.getString("DPS_PUNITIVE_BILL")+"|"+jsonData.getString("MTR_READING_REASON")+"|"+jsonData.getString("DPS_AMT_CURR_MON")+
			"|"+jsonData.getString("INTEREST_ON_SD")+"|"+jsonData.getString("INCENTIVE")+"|"+jsonData.getString("SECURITY_DEPOSIT")+
			"|"+jsonData.getString("SD_REQ")+"|"+jsonData.getString("PROV_BILL_ADJ_AMT")+"|"+jsonData.getString("CURR_INST_GNRL")+
			"|"+jsonData.getString("CURR_MON_AMT")+"|"+jsonData.getString("TOT_INST_AMT")+"|"+jsonData.getString("LAST_PAY_AMT_MADE")+
			"|"+jsonData.getString("LAST_PAY_DATE")+"|"+jsonData.getString("LAST_RECEIPT_NO")+"|"+jsonData.getString("PREV_KWH_CYCLE1")+
			"|"+jsonData.getString("PREV_KWH_CYCLE2")+"|"+jsonData.getString("PREV_KWH_CYCLE3")+"|"+strAVG_KWH_3_CYCLE_DR_KWH+
			"|"+strAVG_KWH_12_CYCLE_KWH+"|"+jsonData.getString("AVG_KWH_3_CYCLE_DEMAND")+"|"+jsonData.getString("AVG_KWH_12_CYCLE_DEMAND")+
			"|"+jsonData.getString("MSG_1")+"|"+jsonData.getString("MSG_2")+"|"+jsonData.getString("CHQ_DISH_FLAG")+"|"+jsonData.getString("DEMAND_FLAG")+
			"|"+jsonData.getString("COM_CODE")+"|"+jsonData.getString("NO_OF_DAYS_PREV_BILL")+"|"+jsonData.getString("TEMP_FLAG")+
			"|"+jsonData.getString("SEASON_FLAG")+"|"+jsonData.getString("MONTH_SEASONAL")+"|"+jsonData.getString("SS_FLAG")+
			"|"+jsonData.getString("IIP_DTY_EXEM_FLAG")+"|"+jsonData.getString("PWR_FACTOR")+"|"+jsonData.getString("IAS_FLAG")+
			"|"+jsonData.getString("INSTALLATION")+"|"+jsonData.getString("METER_MAKE")+"|"+jsonData.getString("METER_COM_CONS")+
			"|"+jsonData.getString("DT_CODE")+"|"+jsonData.getString("METER_CAP")+"|"+strLastActRead+
			"|"+jsonData.getString("LAST_ACT_READ_DT")+"|"+jsonData.getString("PREV_MON_BILL_AMT")+"|"+jsonData.getString("FLAG_FOR_BILL_ADJUSTMENT")+
			"|"+jsonData.getString("PL_PERIOD_ENERGY_AMT")+"|"+jsonData.getString("PL_PERIOD_GOVT_DUTY_AMT")+
			"|"+jsonData.getString("PL_PERIOD_REBATE_AMT")+"|"+jsonData.getString("IIP_CHARGES")+"|"+jsonData.getString("SHUNT_CAP_CHARGES")+
			"|"+jsonData.getString("AMT_1")+"|"+jsonData.getString("AMT_2")+"|"+jsonData.getString("AMT_3")+"|"+jsonData.getString("DATE_1")+
			"|"+jsonData.getString("DATE_2")+"|"+jsonData.getString("DATE_3")+"|"+jsonData.getString("FLAG_1")+"|"+jsonData.getString("FLAG_2")+
			"|"+jsonData.getString("MTR_RDR_ID")+"|"+jsonData.getString("SBM_NUMBER")+"|0";

			//Log.e("UtilDB ==>> ", "InsertInputBulk strLastActRead ==>> " + strLastActRead);
			//Log.i("TAG", record);

			if(jsonData.getString("ROUTE_SEQUENCE_NO").equalsIgnoreCase(""))
			{
				strNoRouteSeq =  jsonData.getString("CONTRACT_AC_NO") + "$%$" + strNoRouteSeq;
			}

			//Inserting records into Application Database in the handset
			insertIntoBillInput(record,db);
			intCounter=intCounter+1;
	    }
		Log.e("strNoRouteSeq ==>> ", strNoRouteSeq);
		updateRouteSequence(strNoRouteSeq);
	}
	catch(Exception ex)
	{
		Log.e("InsertInputBulk E ==>> ", ex.getMessage());
		intCounter=0;
	}
	return intCounter;
}

	@SuppressLint("NewApi")
	public String updateRouteSequence(String strNoRouteSeq) {
		db = helper.getReadableDatabase();
		try {
			int maxRouteSeq = Integer.parseInt(getMaxRouteSequence());
			String[] values = strNoRouteSeq.split("[$%$]");
			for (int i = 0; i < values.length - 1; i++) {
				maxRouteSeq++;
				Log.e("maxRouteSeq ==>> ", maxRouteSeq + "");
				String query1=String.format("UPDATE BillInput SET ROUTE_SEQUENCE_NO='%s' WHERE CONTRACT_AC_NO='%s'", String.valueOf(maxRouteSeq), values[i]);
				SQLiteStatement stmt=db.compileStatement(query1);
				stmt.executeUpdateDelete();					
			}
		} 
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	public long insertIntoBillInput(String input,SQLiteDatabase dbobj) {
		db = dbobj;
		long rowId = 0;
		String[] values = input.split("[|]");
		Log.e("insertIntoBillInput", "Start ==>> " + values.length);
		int length = values.length;
		SQLiteStatement stmt = db
				.compileStatement("INSERT OR IGNORE INTO BillInput VALUES(  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
						+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
						+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
						+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
		
		try {
			for (int i = 0; i < length - 1; i++) {
				stmt.bindString(i + 1, values[i].trim());
			}

			rowId = stmt.executeInsert();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("insertIntoBillInput", e.getMessage());
		}

		return rowId;
	}

	public long insertIntoSAPInput(String[] input) {
		db = helper.getWritableDatabase();
		long rowId = 0;
		String useCount = "";
		String[] values = input;
		System.out.println("data length----" + values.length);
		int length = values.length;

		SQLiteStatement stmt = db.compileStatement("INSERT INTO SAPInput (CANumber,Installation,Latitude,Longitude,CurrentReadingKwh,CurrentReadingKVAH,MaxDemd,PowerFactor,MtrReadingNote,MtrReadingDate,SCHEDULED_BILLING_DATE,SAP_DEVICE_NO,ProcessedFlag,SyncFlag) "
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
			try {
				for (int i = 0; i < input.length; i++) {
					stmt.bindString(i + 1, values[i].trim());
				}
				rowId = stmt.executeInsert();
			} catch (Exception e) {
				e.printStackTrace();
			}

		return rowId;
	}

	public long UpdateIntoSAPInput(String[] input) {
		db = helper.getWritableDatabase();
		long rowId = 0;
		String useCount = "";
		String[] values = input;
		System.out.println("data length----" + values.length);
		int length = values.length;

		try {
			String query1= String.format("Update SAPInput Set Installation = '%s',Latitude = '%s',Longitude = '%s', "
					+ "CurrentReadingKwh = '%s',CurrentReadingKVAH = '%s',MaxDemd = '%s',PowerFactor = '%s',MtrReadingNote = '%s',MtrReadingDate = '%s', "
					+ "SCHEDULED_BILLING_DATE = '%s',SAP_DEVICE_NO = '%s',ProcessedFlag = '%s',SyncFlag = '%s' Where CANumber = '%s'",
					values[1], values[2], values[3], values[4], values[13], values[5], values[6], values[7], values[8], values[9], values[10], values[11], values[12], values[0]);
			Log.e("UpdateIntoSAPInput", " ==>> " + query1);
			SQLiteStatement stmt = db.compileStatement(query1);

					rowId = stmt.executeInsert();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UpdateIntoSAPInput E", e.getMessage());
		}
		return rowId;
	}
	
	@SuppressLint("NewApi")
	public void UpdateSAPInputMsg(String accNo,String strMsgID ,String strMsg)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			String query1=String.format("UPDATE SAPInput SET MESSAGE='%s', MSGID='%s' WHERE CANumber='%s'",strMsg, strMsgID, accNo);
			SQLiteStatement stmt=db.compileStatement(query1);
			stmt.executeUpdateDelete();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("UpdateSAPInputMsg E", e.getMessage());
			e.printStackTrace();
		}
	}

    @SuppressLint("NewApi")
    public void UpdateSAPBlueInputMsg(String accNo,String strMsgID ,String strMsg)
    {
        try {
            db = helper.getWritableDatabase();
            //String str = strMsg.
            String query1=String.format("UPDATE SAPBlueInput SET MESSAGE='%s', MSGID='%s' WHERE CANumber='%s'",strMsg, strMsgID, accNo);
            SQLiteStatement stmt=db.compileStatement(query1);
            stmt.executeUpdateDelete();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            Log.e("UpdateSAPInputMsg E", e.getMessage());
            e.printStackTrace();
        }
    }


    @SuppressLint("NewApi")
	public void UpdateSAPInputSyncFlag(String accNo)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			String query1=String.format("UPDATE SAPInput SET SYNCFLAG='1' WHERE CANumber='%s'", accNo);
			SQLiteStatement stmt=db.compileStatement(query1);
			stmt.executeUpdateDelete();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("UpdateSAPInputMsg E", e.getMessage());
			e.printStackTrace();
		}
	}

	//BlueTooth SAP Reading Insert
	public long insertIntoSAPBlueInput(String[] input) {
		db = helper.getWritableDatabase();
		long rowId = 0;
		String useCount = "";
		String[] values = input;
		System.out.println("data length----" + values.length);
		int length = values.length;

		SQLiteStatement stmt = db.compileStatement("INSERT INTO SAPBlueInput (Installation,SCHEDULED_BILLING_DATE,SAP_DEVICE_NO,MtrReadingNote,CurrentReadingKwh,MaxDemd,PowerFactor,Latitude,Longitude,ProcessedFlag,CurrentReadingKVAH,CANumber,SyncFlag) "
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);");
		try {
			for (int i = 0; i < input.length; i++) {
				stmt.bindString(i + 1, values[i].trim());
			}
			rowId = stmt.executeInsert();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rowId;
	}

	public long UpdateIntoSAPBlueInput(String[] input) {
		db = helper.getWritableDatabase();
		long rowId = 0;
		String useCount = "";
		String[] values = input;
		System.out.println("data length----" + values.length);
		int length = values.length;

		try {
			String query1= String.format("Update SAPBlueInput Set Installation = '%s',SCHEDULED_BILLING_DATE = '%s',SAP_DEVICE_NO = '%s', "
							+ "MtrReadingNote = '%s',CurrentReadingKwh = '%s',MaxDemd = '%s',PowerFactor = '%s',Latitude = '%s',Longitude = '%s', "
							+ "ProcessedFlag = '%s',SyncFlag = '%s' Where CANumber = '%s'",
					values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8], values[9], values[10], values[11]);
			Log.e("UpdateIntoSAPBlueInput", " ==>> " + query1);
			SQLiteStatement stmt = db.compileStatement(query1);

			rowId = stmt.executeInsert();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UpdateIntoSAPInput E", e.getMessage());
		}
		return rowId;
	}

	@SuppressLint("NewApi")
	public void UpdateSAPBlueInputSyncFlag(String accNo)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			String query1=String.format("UPDATE SAPBlueInput SET SYNCFLAG='1' WHERE CANumber='%s'", accNo);
			SQLiteStatement stmt=db.compileStatement(query1);
			stmt.executeUpdateDelete();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("UpdateSAPInputMsg E", e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	public Cursor getUnbilledData()
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			//String query1=String.format("SELECT * FROM SAPInput WHERE MSGID IN ('1', '2', '7')");
			String query1=String.format("SELECT * FROM SAPInput WHERE MSGID IN ('0', '1', '2', '7', '8', '9', '10')");
			Cursor c = db.rawQuery(query1,null);
			if(c.moveToFirst())
			{
				return c;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("getUnbilledData E", e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public Cursor getUnbilledDatacons(String accno)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			//String query1=String.format("SELECT * FROM SAPInput WHERE MSGID IN ('1', '2', '7')");
			String query1=String.format("SELECT * FROM SAPInput WHERE  CANumber='%s',accno");
			Cursor c = db.rawQuery(query1,null);
			if(c.moveToFirst())
			{
				return c;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("getUnbilledData E", e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	@SuppressLint("NewApi")
	public Cursor getMissedCons()
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			//String query1=String.format("SELECT * FROM SAPInput WHERE MSGID IN ('1', '2', '7')");
			String query1=String.format("SELECT * FROM SAPUpdateCons WHERE MSGID IN ('0', '7') OR MSGID is NULL");
			Cursor c = db.rawQuery(query1,null);
			if(c.moveToFirst())
			{
				return c;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("getMissedCons E", e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	@SuppressLint("NewApi")
	public void InsertConsumerDetails(String[] input)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			//String query1=String.format("UPDATE BillOutput SET MobileNo='%s', PoleNo='%s' WHERE CANumber='%s'",MobileNo, PoleNo, accNo);
			/*SQLiteStatement stmt = db.compileStatement("INSERT INTO SAPInput (CANumber,Installation,Latitude,Longitude,CurrentReadingKwh,MaxDemd,PowerFactor,MtrReadingNote,MtrReadingDate,SCHEDULED_BILLING_DATE,SAP_DEVICE_NO,ProcessedFlag) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?);");*/
			String query1=String.format("INSERT INTO SAPUpdateCons (CANumber, LegacyNumber, MtrReadingDate, CONSUMER_NAME, PoleNo, MobileNo, MRU, SUB_DIVISION_CODE, SuccessFlag) "
					+ " VALUES(?,?,?,?,?,?,?,?,?);");
			SQLiteStatement stmt=db.compileStatement(query1);
			for (int i = 0; i < input.length; i++) {
					stmt.bindString(i + 1, input[i].trim());
				}			
			
				stmt.executeInsert();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("InsertConsumerDetails E", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	public void InsertAbnormalityDetails(String[] input)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			//String query1=String.format("UPDATE BillOutput SET MobileNo='%s', PoleNo='%s' WHERE CANumber='%s'",MobileNo, PoleNo, accNo);
			/*SQLiteStatement stmt = db.compileStatement("INSERT INTO SAPInput (CANumber,Installation,Latitude,Longitude,CurrentReadingKwh,MaxDemd,PowerFactor,MtrReadingNote,MtrReadingDate,SCHEDULED_BILLING_DATE,SAP_DEVICE_NO,ProcessedFlag) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?);");*/
			String query1=String.format("INSERT INTO Abnormality(CANumber, MtrReadingDate, MRU, SUB_DIVISION_CODE, Remarks, Abnormality, UploadFlag) "
					+ " VALUES(?,?,?,?,?,?,?);");
			SQLiteStatement stmt=db.compileStatement(query1);
			for (int i = 0; i < input.length; i++) {
					stmt.bindString(i + 1, input[i].trim());
				}			
			
				stmt.executeInsert();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("InsertAbltyDetailsE", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	public void UpdateAbnormalityDetails(String strabnormality, String strRemarks, String strSuccessFlg, String strCANo)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			String query1=String.format("UPDATE SAPUpdateCons SET UploadFlag='%s', Abnormality='%s' Remarks='%s' WHERE CANumber='%s'",strSuccessFlg, strabnormality, strRemarks, strCANo);
			SQLiteStatement stmt=db.compileStatement(query1);			
			stmt.executeUpdateDelete();
			Log.e("UpdateAbnormalityDtls", strCANo + " Update");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("UpdAbnormalityDtlsE", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	public void UpdateConsumerDetails(String strSuccessFlg, String strCANo)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			String query1=String.format("UPDATE SAPUpdateCons SET MSGID='%s' WHERE CANumber='%s'",strSuccessFlg, strCANo);
			SQLiteStatement stmt=db.compileStatement(query1);			
			stmt.executeUpdateDelete();
			Log.e("UpdateConsumerDetails", strCANo + " Update");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("UpdateConsumerDetails E", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	public void InsertMobPoleConsumer(String MobileNo, String PoleNo, String accNo)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			//String query1=String.format("UPDATE SAPInput SET MobileNo='%s', PoleNo='%s' WHERE CANumber='%s'",MobileNo, PoleNo, accNo);
			String strQuery = "Select * from SAPInput where CANumber ='" + accNo + "'";
			Cursor getData = db.rawQuery(strQuery, null);
			String query1="";
			if(getData != null && getData.getCount() > 0)
				query1=String.format("UPDATE SAPInput SET MobileNo='%s', PoleNo='%s' WHERE CANumber='%s'",MobileNo, PoleNo, accNo);
			else
				query1=String.format("INSERT INTO SAPInput (MobileNo, PoleNo, CANumber) values('" + MobileNo + "' , '"+ PoleNo + "' , '"+ accNo + "')");
			//INSERT INTO validDevice (IMEINo, DEV_VALID) VALUES(?,?);
			SQLiteStatement stmt=db.compileStatement(query1);			
			stmt.executeUpdateDelete();
			Log.e("InsertMobPoleConsCmpltd", "Executed");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("InsertMobPoleConsumer E", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	public void UpdateMobPoleFlag(String strflag, String accNo)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			String query1=String.format("UPDATE SAPInput SET MobPoleFlag='%s' WHERE CANumber='%s'", strflag, accNo);
			SQLiteStatement stmt=db.compileStatement(query1);			
			stmt.executeUpdateDelete();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("UpdateConsumerDetails E", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	public Cursor getMobPoleDetails(String accNo)
	{
		try {
			db = helper.getReadableDatabase();
			//String str = strMsg.
			
			String query1=String.format("SELECT MobileNo,PoleNo FROM SAPInput WHERE CANumber='%s'", accNo);
			Cursor c = db.rawQuery(query1,null);
			if(c.moveToFirst())
			{
				return c;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("getMobPoleDetails E", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressLint("NewApi")
	public void UpdatAbnormalityFlag(String strflag, String accNo)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			String query1=String.format("UPDATE Abnormality SET UploadFlag='%s' WHERE CANumber='%s'", strflag, accNo);
			SQLiteStatement stmt=db.compileStatement(query1);			
			stmt.executeUpdateDelete();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("UpdateConsumerDetails E", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	public void UpdateVersion(String strVersion)
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			String query1=String.format("UPDATE validDevice SET version='%s'", strVersion);
			SQLiteStatement stmt=db.compileStatement(query1);			
			stmt.executeUpdateDelete();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("UpdateVersion E", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void getVersion() {
		db = helper.getReadableDatabase();
		SQLiteStatement stmt = db
				.compileStatement("select distinct version from validDevice;");
		
		try {
			UtilAppCommon.strAppVersion = stmt.simpleQueryForString();
			System.out.println("Version & stmt ::   " + UtilAppCommon.strAppVersion + " ---  " + stmt);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	public Cursor getUnsyncedData()
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			//String query1=String.format("SELECT * FROM SAPInput WHERE MSGID IN ('1', '2', '7')");
			String query1=String.format("SELECT * FROM SAPUpdateCons WHERE MSGID IN ('1', '2', '7') OR SuccessFlag <> '1' ");
			Cursor c = db.rawQuery(query1,null);
			if(c.moveToFirst())
			{
				return c;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("getUnbilledData E", e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public long insertIntovalidDevice(String input)
	{
		db = helper.getWritableDatabase();
		long rowId = 0;
		String useCount = "";
		String[] values = input.split("[|]");
		int length = values.length;
		SQLiteStatement stmtGetUserInfo = db
				.compileStatement("SELECT COUNT(*) FROM validDevice");
		useCount = stmtGetUserInfo.simpleQueryForString();
		
		if (Integer.parseInt(useCount) <= 0) 
		{
			SQLiteStatement stmt = db
					.compileStatement("INSERT INTO validDevice (IMEINo, DEV_VALID) VALUES(?,?);");

			try {
				for (int i = 0; i < length; i++) {
					stmt.bindString(i + 1, values[i].trim());
					Log.e(i + 1 + "", values[i].trim());
				}

				rowId = stmt.executeInsert();		
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("valid Device E ", e.getMessage());
			}
		}
				
		return 0;
	}
	
	public long insertIntoUserInfo(String input) {
		db = helper.getWritableDatabase();
		long rowId = 0;
		String useCount = "";
		String[] values = input.split("[|]");
		System.out.println("data length----" + values.length);
		Log.e("User Insert ","data length----" + values.length);
		int length = values.length;
		SQLiteStatement stmtGetUserInfo = db
				.compileStatement("SELECT COUNT(*) FROM UserInfo");
		useCount = stmtGetUserInfo.simpleQueryForString();

		if (Integer.parseInt(useCount) <= 0) {
			SQLiteStatement stmt = db
					.compileStatement("INSERT INTO UserInfo (meterreader_id,METER_READER_NAME,password,IMEINo) VALUES(?,?,?,?);");

			try {
				for (int i = 0; i < length; i++) {
					stmt.bindString(i + 1, values[i].trim());
					Log.e(i + 1 + "", values[i].trim());
				}

				rowId = stmt.executeInsert();		
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("User Insert E ", e.getMessage());
			}
		}
		
		Log.e("User Insert ", "Data Inserted Successfully");
		return rowId;

	}
	
	public void InsertSingleDuedate(String SDO_CD,String BINDER,String BILL_MTH,String Single_date_flag)
	{
		db = helper.getReadableDatabase();
		String query1=String.format("SELECT due_dt FROM bill_duedt WHERE sdo_cd='%s' AND binder='%s' AND bill_mth='%s' ", SDO_CD,BINDER,BILL_MTH);
		Cursor c=db.rawQuery(query1,null);
		if(!c.moveToFirst())
		{
			db = helper.getWritableDatabase();
			String query=String.format("INSERT INTO bill_duedt (sdo_cd,binder,bill_mth,Single_date_flag) VALUES ('%s','%s','%s','%s')", SDO_CD,BINDER,BILL_MTH,Single_date_flag);
			SQLiteStatement stmt=db.compileStatement(query);
			stmt.executeInsert();
		}
	}
	
	@SuppressLint("NewApi")
	public void UpdateSingleDuedate(String SDO_CD,String BINDER,String BILL_MTH,String due_dt)
	{
		db = helper.getWritableDatabase();
		String query1=String.format("UPDATE bill_duedt SET due_dt='%s' WHERE sdo_cd='%s' AND binder='%s' AND bill_mth='%s' ",due_dt, SDO_CD,BINDER,BILL_MTH);
		SQLiteStatement stmt=db.compileStatement(query1);
		stmt.executeUpdateDelete();
		
	}

	public String GetSingleDuedate(String SDO_CD,String BINDER,String BILL_MTH)
	{
		String DueDate="";
		db = helper.getReadableDatabase();
		String query=String.format("SELECT due_dt FROM bill_duedt WHERE sdo_cd='%s' AND binder='%s' AND bill_mth='%s' ", SDO_CD,BINDER,BILL_MTH);
		Cursor c=db.rawQuery(query,null);
		if(c.moveToFirst())
		{
			DueDate=c.isNull(0)?"":c.getString(0);
		}
		
		return DueDate;
	}
	
	public void UpdatePrinterInfo(String PrinterID, String PrinterType) {
		db = helper.getWritableDatabase();
		long rowId = 0;

		try {
			
			//String query1=String.format("UPDATE UserInfo SET printerid=%s,printertype=%s;", PrinterID, PrinterType);
			//SQLiteStatement stmt = db.compileStatement(query1);
			SQLiteStatement stmt = db.compileStatement("UPDATE UserInfo SET  printerid=?,printertype=?;");
			Log.e("PrinterID", PrinterID);
			Log.e("PrinterType", PrinterType);
			
			stmt.bindString(1, PrinterID);
			stmt.bindString(2, PrinterType);
			rowId = stmt.executeInsert();
			Log.e("rowId==>> ", rowId + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// End
	
	public String getSBMOUtDeatils() {
		db = helper.getReadableDatabase();
		SQLiteStatement stmt = db
				.compileStatement("select CONS_REF from BillOutput where SDO_CD = ?;");
		String name = null;
		try {
			stmt.bindString(1, "9");
			name = stmt.simpleQueryForString();
			System.out.println("name & stmt ::   " + name + " ---  " + stmt);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return name;
	}
//28.09.15
	public String getSdoCode() {
		db = helper.getReadableDatabase();
		SQLiteStatement stmt = db
				.compileStatement("SELECT DISTINCT sub_division_code  FROM BillInput");
		String sdocode = null;
		try {
			sdocode = stmt.simpleQueryForString();
			System.out.println("sdocode & stmt ::   " + sdocode + " ---  "
					+ stmt);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return sdocode;
	}

	public String getActiveMRU() {
		db = helper.getReadableDatabase();
		SQLiteStatement stmt = db
				.compileStatement("SELECT DISTINCT MRU FROM BillInput");
		String sdocode = null;
		try {
			sdocode = stmt.simpleQueryForString();
			System.out.println("MRU & stmt ::   " + sdocode + " ---  "
					+ stmt);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return sdocode;
	}
	
	public String getSchMtrRdgDate() {
		db = helper.getReadableDatabase();
		SQLiteStatement stmt = db
				.compileStatement("SELECT DISTINCT SCH_MTR_READING_DT FROM BillInput");
		String sdocode = null;
		try {
			sdocode = stmt.simpleQueryForString();
			System.out.println("SCH_MTR_READING_DT & stmt ::   " + sdocode + " ---  "
					+ stmt);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return sdocode;
	}
	
	public boolean checkCANumber(String accno) {
		try 
		{
			db = helper.getReadableDatabase();
			String query = null;
			boolean bIsValidConsumer = false;
			
			int acno = Integer.parseInt(accno);
			query = String.format("select * from BillInput where CONTRACT_AC_NO = '%s';", acno);
			
			Cursor c = db.rawQuery(query, null);
			
			if (c.moveToFirst()) {
				return true;
			}
			return bIsValidConsumer;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		
	}
	
// update checkCANumberAbnormality function

	public boolean checkCANumberAbnormality(String accno) {
		try 
		{
			db = helper.getReadableDatabase();
			String query = null;
			boolean bIsValidConsumer = false;
			
			int acno = Integer.parseInt(accno);
			query = String.format("select * from Abnormality where CANumber = '%s';", acno);
			
			Cursor c = db.rawQuery(query, null);
			
			if (c.moveToFirst()) {
				//return true;
				bIsValidConsumer = true;
			}
			return bIsValidConsumer;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	public boolean getBillInputDetails(String accno, String Field) {
		db = helper.getReadableDatabase();
		String query = null;
		boolean bIsValidConsumer = false;

		System.out.println("Entering into the database part \n");


		if(Field.equalsIgnoreCase("CA Number"))
		{
			int acno = Integer.parseInt(accno);
			query = String.format("select * from BillInput where CONTRACT_AC_NO = '%s';", acno);
		}
		else if(Field.equalsIgnoreCase("Legacy"))
				query = String.format("select * from BillInput where CONSUMER_LEGACY_ACC_NO = '%s';", accno);
		else if(Field.equalsIgnoreCase("Meter"))
			query = String.format("select * from BillInput where METER_MANUFACTURER_SR_NO = '%s';", accno);
		//else if(Field.equalsIgnoreCase("Sequence"))
		//	query = String.format("select * from BillInput where ROUTE_SEQUENCE_NO = '%s';", accno);

		//System.out.println("query---" + query);
		Cursor c = db.rawQuery(query, null);

		JSONObject result = new JSONObject();
		try {
			UtilAppCommon.in = null;
			UtilAppCommon.out = null;
			//UtilAppCommon.bill = null;
			if (c.moveToFirst()) {

				bIsValidConsumer = true;// Sudhir

				do {
					UtilAppCommon.in = new StructInput();
					UtilAppCommon.out = new StructOutput();
					// dkr
					//UtilAppCommon.bill = new StructBillInfo();
					UtilAppCommon.copyResultsetToInputClass(c);
					int colCount = 93;
					for (int i = 0; i < colCount; i++) {
						//Log.e("column name == ", c.getColumnName(i)	+ "-----" + c.getString(i));
						result.put(c.getColumnName(i), c.getString(i).trim());
					}

				} while (c.moveToNext());
				Log.e("Pole No", UtilAppCommon.in.CONNECTED_POLE_NIN_NUMBER);
			}
			Log.e("getBillInputDetails-> ","Completed");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("gtBillInputDtls E-> ","Completed");
		}

		return bIsValidConsumer;

	}

	public int getBillInputDetailsCount() {
		db = helper.getReadableDatabase();
		String query = null;

		int intCount = 0;

		System.out.println("Entering into the database part \n");

		//String bndr = getactiveMRU();
		//String sdo = getSubDivision();
		//String bill_mth = getCurrBillMonth();
		//bill_mth = bill_mth == "" ? "0000" : bill_mth;
		//Log.e("MRU", bndr);
		//Log.e("sdo", sdo);
		query = String.format("select * from BillInput"); //and bill_mth=" + bill_mth);
		// query = String.format("select * from BillInput ");
		//System.out.println("query---" + query);
		Log.e("query---" , query);
		//intCount = 1;
		Cursor c = db.rawQuery(query, null);
		intCount = c.getCount();
		return intCount;

	}
	
	public int getUserInfoRowCount() {
		db = helper.getReadableDatabase();
		String query = null;

		int intUserInfoRowCount = 0;
        try {
			System.out.println("Entering into the database part \n");

			query = String.format("select * from UserInfo");
			System.out.println("query---" + query);
			Cursor c = db.rawQuery(query, null);
			intUserInfoRowCount = c.getCount();
		}catch (Exception ex){
			ex.printStackTrace();
			Log.e("Userinfo",ex.getMessage());
		}
		//intUserInfoRowCount=1;
		return intUserInfoRowCount;

	}

	public String getUnpostedBilledData(Context context) {
		JSONArray all_record_of_cust = new JSONArray();
		if (UtilAppCommon.checkiInternet(context)) {
			db = helper.getReadableDatabase();
			String query = String
					.format("select * from BillOutput where DATASENDINGSTATUS = '%s' AND CONS_REF!='' ORDER BY BINDER LIMIT 0,20;",
							'N');
			// String query =
			// String.format("select * from BillOutput where binder='0103'");

			System.out.println("query---" + query);
			Cursor c = db.rawQuery(query, null);
			int count = c.getCount();
			System.out.println("count ===   Sudhir Unposted   : " + count);

			int totalSuccess = 0;
			int totalFailed = 0;
			int totalAB = 0;
			try {
				if (c.moveToFirst()) {

					do {
						JSONObject cust_info = new JSONObject();
						cust_info.put("CONS_REF", c.getString(0));
						cust_info.put("SDO_CD", c.getString(1));
						cust_info.put("BINDER", c.getString(2));
						cust_info.put("ACC_NO", c.getString(3));
						cust_info.put("METERNO", c.getString(4) == null ? ""
								: c.getString(4));
						cust_info.put(
								"MTRINSTL_DATE",
								c.getString(5) == null ? "1900-01-01" : c
										.getString(5));
						cust_info.put("METERTYPE", c.getString(6) == null ? ""
								: c.getString(6));
						cust_info.put(
								"MF",
								c.getString(7) == null ? 0 : Double
										.parseDouble(c.getString(7)));
						cust_info.put("INITIAL_RED", c.getString(8) == null ? 0
								: Double.parseDouble(c.getString(8)));
						cust_info.put(
								"FINAL_RED_OLDMETER",
								c.getString(9) == null ? 0 : Double
										.parseDouble(c.getString(9)));
						cust_info.put("OLDMETER_STAT",
								c.getString(10) == null ? "" : c.getString(10));
						cust_info.put("CSTS_CD", c.getString(11) == null ? ""
								: c.getString(11));
						cust_info.put("CUR_METER_STAT",
								c.getString(12) == null ? "" : c.getString(12));
						cust_info.put("CURRRDG", c.getString(13) == null ? 0
								: Double.parseDouble(c.getString(13)));
						cust_info.put(
								"CUR_RED_DT",
								c.getString(14) == null ? "1900-01-01" : c
										.getString(14));

						cust_info.put(
								"BILL_DEMAND",
								c.getString(15) == null ? 0 : Double
										.parseDouble(c.getString(15)));
						cust_info.put("NEW_TRF_CD",
								c.getString(16) == null ? "" : c.getString(16));
						cust_info.put("BILL_UNITS", c.getString(17) == null ? 0
								: Integer.parseInt(c.getString(17)));
						cust_info.put("ENGCHG", c.getString(18) == null ? 0
								: Double.parseDouble(c.getString(18)));
						cust_info.put("FIXCHG", c.getString(19) == null ? 0
								: Double.parseDouble(c.getString(19)));
						cust_info.put("METERRENT", c.getString(20) == null ? 0
								: Double.parseDouble(c.getString(20)));
						cust_info.put("ED", c.getString(21) == null ? 0
								: Double.parseDouble(c.getString(21)));
						cust_info.put("NEWBD", c.getString(22) == null ? 0
								: Double.parseDouble(c.getString(22)));
						cust_info.put("NEWED", c.getString(23) == null ? 0
								: Double.parseDouble(c.getString(23)));
						cust_info.put("NEWDPS", c.getString(24) == null ? 0
								: Double.parseDouble(c.getString(24)));
						cust_info.put("NEWOTH", c.getString(25) == null ? 0
								: Double.parseDouble(c.getString(25)));
						cust_info.put("REFBLUNITS", c.getString(26) == null ? 0
								: Integer.parseInt(c.getString(26)));
						cust_info.put("REFBLBD", c.getString(27) == null ? 0
								: Double.parseDouble(c.getString(27)));
						cust_info.put("REFBLED", c.getString(28) == null ? 0
								: Double.parseDouble(c.getString(28)));
						cust_info.put("REFBLDPS", c.getString(29) == null ? 0
								: Double.parseDouble(c.getString(29)));
						cust_info.put(
								"REFDEDUNITS",
								c.getString(30) == null ? 0 : Integer
										.parseInt(c.getString(30)));
						cust_info.put("REFDEDBD", c.getString(31) == null ? 0
								: Double.parseDouble(c.getString(31)));
						cust_info.put("REFDEDED", c.getString(32) == null ? 0
								: Double.parseDouble(c.getString(32)));
						cust_info.put("REFDEDDPS", c.getString(33) == null ? 0
								: Double.parseDouble(c.getString(33)));
						cust_info.put("REB_OFF", c.getString(34) == null ? 0
								: Double.parseDouble(c.getString(34)));
						cust_info.put(
								"NETBEFDUEDT",
								c.getString(35) == null ? 0 : Double
										.parseDouble(c.getString(35)));
						cust_info.put(
								"NETAFTDUEDT",
								c.getString(36) == null ? 0 : Double
										.parseDouble(c.getString(36)));
						cust_info.put("BILLBASIS", c.getString(37) == null ? ""
								: c.getString(37));
						cust_info.put("NOOFMONTHS", c.getString(38) == null ? 0
								: Integer.parseInt(c.getString(38)));
						cust_info.put(
								"REB_DT",
								c.getString(39) == null ? "1900-01-01" : c
										.getString(39));
						cust_info.put(
								"DUE_DT",
								c.getString(40) == null ? "1900-01-01" : c
										.getString(40));
						cust_info.put(
								"ISSUE_DT",
								c.getString(41) == null ? "1900-01-01" : c
										.getString(41));
						cust_info.put("BILLPERIOD",
								c.getString(42) == null ? "" : c.getString(42));
						cust_info.put("BILLSERIALNO",
								c.getString(43) == null ? "" : c.getString(43));
						cust_info.put("OLDCSTS_CD",
								c.getString(44) == null ? "" : c.getString(44));
						cust_info.put("BILL_MTH", c.getString(45) == null ? ""
								: c.getString(45));
						cust_info.put("REMARKS", c.getString(46) == null ? ""
								: c.getString(46));
						cust_info.put("MACHINE_SRL_NO",
								c.getString(47) == null ? "" : c.getString(47));
						cust_info.put("MTR_READER_ID",
								c.getString(48) == null ? "" : c.getString(48));
						cust_info.put("MTR_READER_NAME",
								c.getString(49) == null ? "" : c.getString(49));
						cust_info.put("REB_OYT", c.getString(50) == null ? 0
								: Double.parseDouble(c.getString(50)));
						cust_info.put("REB_RTSWHT", c.getString(51) == null ? 0
								: Double.parseDouble(c.getString(51)));
						cust_info.put("DOM_SPLREB", c.getString(52) == null ? 0
								: Double.parseDouble(c.getString(52)));
						cust_info.put("ENGCHG_OLDTRF", 0);
						cust_info.put("FIXCHG_OLDTRF", 0);
						cust_info.put("ED_OLDTRF", 0);
						cust_info.put("NEWBD_OLDTRF", 0);
						cust_info.put("NEWED_OLDTRF", 0);
						cust_info.put("NEWDPS_OLDTRF", 0);
						cust_info.put("NEWOTH_OLDTRF", 0);
						cust_info.put("REFBLBD_OLDTRF", 0);
						cust_info.put("REFBLED_OLDTRF", 0);
						cust_info.put("REFBLDPS_OLDTRF", 0);
						cust_info.put("REFDEDBD_OLDTRF", 0);
						cust_info.put("REFDEDED_OLDTRF", 0);
						cust_info.put("REFDEDDPS_OLDTRF", 0);
						cust_info.put("REB_OFF_OLDTRF", 0);
						cust_info.put("NETBEFDUEDT_OLDTRF", 0);
						cust_info.put("NETAFTDUEDT_OLDTRF", 0);
						// cust_info.put("REB_HOSTEL", c.getString(69) == null
						// ?0:Double.parseDouble(c.getString(69)));
						cust_info.put("REB_HOSTEL", 0);
						cust_info.put("MAX_DEMD", c.getString(70) == null ? 0
								: Double.parseDouble(c.getString(70)));
						cust_info.put("APP_VERSION",
								c.getString(71) == null ? "" : c.getString(71));
						cust_info.put("BILL_PHASE", "1");
						cust_info.put("PHOTOID", c.getString(73) == null ? ""
								: c.getString(73));
						// cust_info.put("PHOTOTAKENDATE","1900-01-01");
						cust_info.put(
								"PHOTOTAKENDATE",
								c.getString(41) == null ? "1900-01-01" : c
										.getString(41));
						// cust_info.put("AGENCY_CODE", UtilAppCommon.ui.USER_ID
						// != null ?
						// UtilAppCommon.ui.USER_ID.substring(1,3):null);
						cust_info.put("AGENCY_CODE",
								c.getString(49) == null ? "" : c.getString(49)
										.substring(1, 3));
						// cust_info.put("COMPANY_ID",1);
						cust_info.put("COMPANY_ID",
								c.getString(76) == null ? "" : c.getString(76));
						cust_info.put("DateLastMaint", "1900-01-01");
						cust_info.put("ModifiedBy",
								c.getString(49) == null ? "" : c.getString(49));
						cust_info.put("Sessionid", "345");

						System.out.println("cust_info::" + cust_info);
						all_record_of_cust.put(cust_info);
					} while (c.moveToNext());
					System.out.println("SAVE BULK DATA  TO SERVER::"
							+ all_record_of_cust.toString());

				}

			} catch (Exception ex) {
				// TODO: handle exception
				ex.printStackTrace();
				all_record_of_cust = null;
				return "";
			}
		}
		return all_record_of_cust.toString();
	}

	public void updateStatus() {
		db = helper.getWritableDatabase();

		// String
		// qry="UPDATE BillOutPut  set DATASENDINGSTATUS='Y' where DATASENDINGSTATUS='N' AND CONS_REF=!''";
		//String qry = "UPDATE BillInPut  set DATASENDINGSTATUS='Y' where CONS_REF in (select CONS_REF from BillOutput where DATASENDINGSTATUS = 'N'  AND CONS_REF!='' ORDER BY BINDER LIMIT 0,20)";
		//db.execSQL(qry);
	}

	public String GetSyncData() {
		/*
		JSONArray all_record_of_cust = new JSONArray();

		db = helper.getReadableDatabase();
		String query = String
				.format("select CONS_REF from BillOutput where DATASENDINGSTATUS = '%s' AND CONS_REF!='' ORDER BY BINDER ;",
						'N');
		// String query =
		// String.format("select * from BillOutput where binder='0103'");

		System.out.println("query---" + query);
		Cursor c = db.rawQuery(query, null);

		JSONStringer data = new JSONStringer();
		try {
			if (c.moveToFirst()) {

				do {
					all_record_of_cust.put(c.getString(0));
				} while (c.moveToNext());
				System.out.println("SAVE BULK DATA  TO SERVER::"
						+ all_record_of_cust.toString());
			}
			data.object().key("Consumers").value(all_record_of_cust.toString())
					.endObject();

		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
			all_record_of_cust = null;
			return data.toString();
		}
		return data.toString();
		*/
		return "";
	}

	public void SyncDataSendingStatus(String Consumers) {

		db = helper.getWritableDatabase();
		String qry1 = "";
		qry1 = String
					.format("UPDATE BillOutPut  set DATASENDINGSTATUS='Y' where CONS_REF IN (%s);",Consumers);
		
		db.execSQL(qry1);

	}

	public String getUnpostedBilledDataByBinder(Context context, String BINDER) {
		JSONArray all_record_of_cust = new JSONArray();
		if (UtilAppCommon.checkiInternet(context)) {
			db = helper.getReadableDatabase();
			String query = String
					.format("select * from BillOutput where BINDER='%s' AND DATASENDINGSTATUS = '%s' AND CONS_REF!='' ORDER BY BINDER LIMIT 0,20;",
							BINDER, 'N');
			// String query =
			// String.format("select * from BillOutput where binder='0103'");

			System.out.println("query---" + query);
			Cursor c = db.rawQuery(query, null);
			int count = c.getCount();
			System.out.println("count ===   Sudhir Unposted   : " + count);

			int totalSuccess = 0;
			int totalFailed = 0;
			int totalAB = 0;
			try {
				if (c.moveToFirst()) {

					do {
						JSONObject cust_info = new JSONObject();
						cust_info.put("CONS_REF", c.getString(0));
						cust_info.put("SDO_CD", c.getString(1));
						cust_info.put("BINDER", c.getString(2));
						cust_info.put("ACC_NO", c.getString(3));
						cust_info.put("METERNO", c.getString(4) == null ? ""
								: c.getString(4));
						cust_info.put(
								"MTRINSTL_DATE",
								c.getString(5) == null ? "1900-01-01" : c
										.getString(5));
						cust_info.put("METERTYPE", c.getString(6) == null ? ""
								: c.getString(6));
						cust_info.put(
								"MF",
								c.getString(7) == null ? 0 : Double
										.parseDouble(c.getString(7)));
						cust_info.put("INITIAL_RED", c.getString(8) == null ? 0
								: Double.parseDouble(c.getString(8)));
						cust_info.put(
								"FINAL_RED_OLDMETER",
								c.getString(9) == null ? 0 : Double
										.parseDouble(c.getString(9)));
						cust_info.put("OLDMETER_STAT",
								c.getString(10) == null ? "" : c.getString(10));
						cust_info.put("CSTS_CD", c.getString(11) == null ? ""
								: c.getString(11));
						cust_info.put("CUR_METER_STAT",
								c.getString(12) == null ? "" : c.getString(12));
						cust_info.put("CURRRDG", c.getString(13) == null ? 0
								: Double.parseDouble(c.getString(13)));
						cust_info.put(
								"CUR_RED_DT",
								c.getString(14) == null ? "1900-01-01" : c
										.getString(14));

						cust_info.put(
								"BILL_DEMAND",
								c.getString(15) == null ? 0 : Double
										.parseDouble(c.getString(15)));
						cust_info.put("NEW_TRF_CD",
								c.getString(16) == null ? "" : c.getString(16));
						cust_info.put("BILL_UNITS", c.getString(17) == null ? 0
								: Integer.parseInt(c.getString(17)));
						cust_info.put("ENGCHG", c.getString(18) == null ? 0
								: Double.parseDouble(c.getString(18)));
						cust_info.put("FIXCHG", c.getString(19) == null ? 0
								: Double.parseDouble(c.getString(19)));
						cust_info.put("METERRENT", c.getString(20) == null ? 0
								: Double.parseDouble(c.getString(20)));
						cust_info.put("ED", c.getString(21) == null ? 0
								: Double.parseDouble(c.getString(21)));
						cust_info.put("NEWBD", c.getString(22) == null ? 0
								: Double.parseDouble(c.getString(22)));
						cust_info.put("NEWED", c.getString(23) == null ? 0
								: Double.parseDouble(c.getString(23)));
						cust_info.put("NEWDPS", c.getString(24) == null ? 0
								: Double.parseDouble(c.getString(24)));
						cust_info.put("NEWOTH", c.getString(25) == null ? 0
								: Double.parseDouble(c.getString(25)));
						cust_info.put("REFBLUNITS", c.getString(26) == null ? 0
								: Integer.parseInt(c.getString(26)));
						cust_info.put("REFBLBD", c.getString(27) == null ? 0
								: Double.parseDouble(c.getString(27)));
						cust_info.put("REFBLED", c.getString(28) == null ? 0
								: Double.parseDouble(c.getString(28)));
						cust_info.put("REFBLDPS", c.getString(29) == null ? 0
								: Double.parseDouble(c.getString(29)));
						cust_info.put(
								"REFDEDUNITS",
								c.getString(30) == null ? 0 : Integer
										.parseInt(c.getString(30)));
						cust_info.put("REFDEDBD", c.getString(31) == null ? 0
								: Double.parseDouble(c.getString(31)));
						cust_info.put("REFDEDED", c.getString(32) == null ? 0
								: Double.parseDouble(c.getString(32)));
						cust_info.put("REFDEDDPS", c.getString(33) == null ? 0
								: Double.parseDouble(c.getString(33)));
						cust_info.put("REB_OFF", c.getString(34) == null ? 0
								: Double.parseDouble(c.getString(34)));
						cust_info.put(
								"NETBEFDUEDT",
								c.getString(35) == null ? 0 : Double
										.parseDouble(c.getString(35)));
						cust_info.put(
								"NETAFTDUEDT",
								c.getString(36) == null ? 0 : Double
										.parseDouble(c.getString(36)));
						cust_info.put("BILLBASIS", c.getString(37) == null ? ""
								: c.getString(37));
						cust_info.put("NOOFMONTHS", c.getString(38) == null ? 0
								: Integer.parseInt(c.getString(38)));
						cust_info.put(
								"REB_DT",
								c.getString(39) == null ? "1900-01-01" : c
										.getString(39));
						cust_info.put(
								"DUE_DT",
								c.getString(40) == null ? "1900-01-01" : c
										.getString(40));
						cust_info.put(
								"ISSUE_DT",
								c.getString(41) == null ? "1900-01-01" : c
										.getString(41));
						cust_info.put("BILLPERIOD",
								c.getString(42) == null ? "" : c.getString(42));
						cust_info.put("BILLSERIALNO",
								c.getString(43) == null ? "" : c.getString(43));
						cust_info.put("OLDCSTS_CD",
								c.getString(44) == null ? "" : c.getString(44));
						cust_info.put("BILL_MTH", c.getString(45) == null ? ""
								: c.getString(45));
						cust_info.put("REMARKS", c.getString(46) == null ? ""
								: c.getString(46));
						cust_info.put("MACHINE_SRL_NO",
								c.getString(47) == null ? "" : c.getString(47));
						cust_info.put("MTR_READER_ID",
								c.getString(48) == null ? "" : c.getString(48));
						cust_info.put("MTR_READER_NAME",
								c.getString(49) == null ? "" : c.getString(49));
						cust_info.put("REB_OYT", c.getString(50) == null ? 0
								: Double.parseDouble(c.getString(50)));
						cust_info.put("REB_RTSWHT", c.getString(51) == null ? 0
								: Double.parseDouble(c.getString(51)));
						cust_info.put("DOM_SPLREB", c.getString(52) == null ? 0
								: Double.parseDouble(c.getString(52)));
						cust_info.put("ENGCHG_OLDTRF", 0);
						cust_info.put("FIXCHG_OLDTRF", 0);
						cust_info.put("ED_OLDTRF", 0);
						cust_info.put("NEWBD_OLDTRF", 0);
						cust_info.put("NEWED_OLDTRF", 0);
						cust_info.put("NEWDPS_OLDTRF", 0);
						cust_info.put("NEWOTH_OLDTRF", 0);
						cust_info.put("REFBLBD_OLDTRF", 0);
						cust_info.put("REFBLED_OLDTRF", 0);
						cust_info.put("REFBLDPS_OLDTRF", 0);
						cust_info.put("REFDEDBD_OLDTRF", 0);
						cust_info.put("REFDEDED_OLDTRF", 0);
						cust_info.put("REFDEDDPS_OLDTRF", 0);
						cust_info.put("REB_OFF_OLDTRF", 0);
						cust_info.put("NETBEFDUEDT_OLDTRF", 0);
						cust_info.put("NETAFTDUEDT_OLDTRF", 0);
						// cust_info.put("REB_HOSTEL", c.getString(69) == null
						// ?0:Double.parseDouble(c.getString(69)));
						cust_info.put("REB_HOSTEL", 0);
						cust_info.put("MAX_DEMD", c.getString(70) == null ? 0
								: Double.parseDouble(c.getString(70)));
						cust_info.put("APP_VERSION",
								c.getString(71) == null ? "" : c.getString(71));
						cust_info.put("BILL_PHASE", "1");
						cust_info.put("PHOTOID", c.getString(73) == null ? ""
								: c.getString(73));
						// cust_info.put("PHOTOTAKENDATE","1900-01-01");
						cust_info.put(
								"PHOTOTAKENDATE",
								c.getString(41) == null ? "1900-01-01" : c
										.getString(41));
						// cust_info.put("AGENCY_CODE", UtilAppCommon.ui.USER_ID
						// != null ?
						// UtilAppCommon.ui.USER_ID.substring(1,3):null);
						cust_info.put("AGENCY_CODE",
								c.getString(49) == null ? "" : c.getString(49)
										.substring(1, 3));
						// cust_info.put("COMPANY_ID",1);
						cust_info.put("COMPANY_ID",
								c.getString(76) == null ? "" : c.getString(76));
						cust_info.put("DateLastMaint", "1900-01-01");
						cust_info.put("ModifiedBy",
								c.getString(49) == null ? "" : c.getString(49));
						cust_info.put("Sessionid", "345");

						System.out.println("cust_info::" + cust_info);
						all_record_of_cust.put(cust_info);
					} while (c.moveToNext());
					System.out.println("SAVE BULK DATA  TO SERVER::"
							+ all_record_of_cust.toString());

				}

			} catch (Exception ex) {
				// TODO: handle exception
				ex.printStackTrace();
				all_record_of_cust = null;
				return "";
			}
		}
		return all_record_of_cust.toString();
	}

	public void updateSendingStsByBinder(String BINDER) {
		db = helper.getReadableDatabase();

		// String
		// qry="UPDATE BillOutPut  set DATASENDINGSTATUS='Y' where DATASENDINGSTATUS='N' AND CONS_REF=!''";
		String qry = String
				.format("UPDATE BillOutPut  set DATASENDINGSTATUS='Y' where CONS_REF in (select CONS_REF from BillOutput where BINDER='%s' AND DATASENDINGSTATUS = 'N'  AND CONS_REF!='' ORDER BY BINDER LIMIT 0,20)",
						BINDER);
		Cursor c = db.rawQuery(qry, null);
		String output = "";
		if (c.moveToFirst()) {
			output = c.getString(0);
			System.out.println("output:" + output);
		}
	}

	// Added on 25Aug14
	public ArrayList<HashMap<String, String>> getBilledUnPostedConsList() {
		db = helper.getReadableDatabase();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		String qry = String
				.format("select I.cons_ref,I.binder,I.acc_no,I.name,I.addr1 from BillInput I INNER JOIN BillOutPut O ON I.[CONS_REF]=O.[CONS_REF] and I.[bill_mth]=O.[BILL_MTH] where O.[DATASENDINGSTATUS]='N' order by I.binder,I.acc_no,I.name");
		System.out.println("query---" + qry);
		Cursor c = db.rawQuery(qry, null);

		HashMap<String, String> map = null;
		if (c.moveToFirst()) {
			do {

				map = new HashMap<String, String>();
				map.put("CONS_REF", c.getString(0));
				map.put("BINDER", c.getString(1));
				map.put("ACC_NO", c.getString(2));
				map.put("NAME", c.getString(3));
				map.put("ADDR", c.getString(4));
				list.add(map);

			} while (c.moveToNext());
		}

		return list;
	}
	// End Add

	public String getUnpostedBilledConData(Context context, String CONS_REF) {
		JSONArray all_record_of_cust = new JSONArray();
		if (UtilAppCommon.checkiInternet(context)) {
			db = helper.getReadableDatabase();
			String query = String
					.format("select * from BillOutput where DATASENDINGSTATUS='N' AND CONS_REF='%s'",
							CONS_REF);
			// String query =
			// String.format("select * from BillOutput where binder='0103'");

			System.out.println("query---" + query);
			Cursor c = db.rawQuery(query, null);
			int count = c.getCount();
			System.out.println("count ===   Sudhir Unposted   : " + count);

			try {
				if (c.moveToFirst()) {

					do {
						JSONObject cust_info = new JSONObject();
						cust_info.put("CONS_REF", c.getString(0));
						cust_info.put("SDO_CD", c.getString(1));
						cust_info.put("BINDER", c.getString(2));
						cust_info.put("ACC_NO", c.getString(3));
						cust_info.put("METERNO", c.getString(4) == null ? ""
								: c.getString(4));
						cust_info.put(
								"MTRINSTL_DATE",
								c.getString(5) == null ? "1900-01-01" : c
										.getString(5));
						cust_info.put("METERTYPE", c.getString(6) == null ? ""
								: c.getString(6));
						cust_info.put(
								"MF",
								c.getString(7) == null ? 0 : Double
										.parseDouble(c.getString(7)));
						cust_info.put("INITIAL_RED", c.getString(8) == null ? 0
								: Double.parseDouble(c.getString(8)));
						cust_info.put(
								"FINAL_RED_OLDMETER",
								c.getString(9) == null ? 0 : Double
										.parseDouble(c.getString(9)));
						cust_info.put("OLDMETER_STAT",
								c.getString(10) == null ? "" : c.getString(10));
						cust_info.put("CSTS_CD", c.getString(11) == null ? ""
								: c.getString(11));
						cust_info.put("CUR_METER_STAT",
								c.getString(12) == null ? "" : c.getString(12));
						cust_info.put("CURRRDG", c.getString(13) == null ? 0
								: Double.parseDouble(c.getString(13)));
						cust_info.put(
								"CUR_RED_DT",
								c.getString(14) == null ? "1900-01-01" : c
										.getString(14));

						cust_info.put(
								"BILL_DEMAND",
								c.getString(15) == null ? 0 : Double
										.parseDouble(c.getString(15)));
						cust_info.put("NEW_TRF_CD",
								c.getString(16) == null ? "" : c.getString(16));
						cust_info.put("BILL_UNITS", c.getString(17) == null ? 0
								: Integer.parseInt(c.getString(17)));
						cust_info.put("ENGCHG", c.getString(18) == null ? 0
								: Double.parseDouble(c.getString(18)));
						cust_info.put("FIXCHG", c.getString(19) == null ? 0
								: Double.parseDouble(c.getString(19)));
						cust_info.put("METERRENT", c.getString(20) == null ? 0
								: Double.parseDouble(c.getString(20)));
						cust_info.put("ED", c.getString(21) == null ? 0
								: Double.parseDouble(c.getString(21)));
						cust_info.put("NEWBD", c.getString(22) == null ? 0
								: Double.parseDouble(c.getString(22)));
						cust_info.put("NEWED", c.getString(23) == null ? 0
								: Double.parseDouble(c.getString(23)));
						cust_info.put("NEWDPS", c.getString(24) == null ? 0
								: Double.parseDouble(c.getString(24)));
						cust_info.put("NEWOTH", c.getString(25) == null ? 0
								: Double.parseDouble(c.getString(25)));
						cust_info.put("REFBLUNITS", c.getString(26) == null ? 0
								: Integer.parseInt(c.getString(26)));
						cust_info.put("REFBLBD", c.getString(27) == null ? 0
								: Double.parseDouble(c.getString(27)));
						cust_info.put("REFBLED", c.getString(28) == null ? 0
								: Double.parseDouble(c.getString(28)));
						cust_info.put("REFBLDPS", c.getString(29) == null ? 0
								: Double.parseDouble(c.getString(29)));
						cust_info.put(
								"REFDEDUNITS",
								c.getString(30) == null ? 0 : Integer
										.parseInt(c.getString(30)));
						cust_info.put("REFDEDBD", c.getString(31) == null ? 0
								: Double.parseDouble(c.getString(31)));
						cust_info.put("REFDEDED", c.getString(32) == null ? 0
								: Double.parseDouble(c.getString(32)));
						cust_info.put("REFDEDDPS", c.getString(33) == null ? 0
								: Double.parseDouble(c.getString(33)));
						cust_info.put("REB_OFF", c.getString(34) == null ? 0
								: Double.parseDouble(c.getString(34)));
						cust_info.put(
								"NETBEFDUEDT",
								c.getString(35) == null ? 0 : Double
										.parseDouble(c.getString(35)));
						cust_info.put(
								"NETAFTDUEDT",
								c.getString(36) == null ? 0 : Double
										.parseDouble(c.getString(36)));
						cust_info.put("BILLBASIS", c.getString(37) == null ? ""
								: c.getString(37));
						cust_info.put("NOOFMONTHS", c.getString(38) == null ? 0
								: Integer.parseInt(c.getString(38)));
						cust_info.put(
								"REB_DT",
								c.getString(39) == null ? "1900-01-01" : c
										.getString(39));
						cust_info.put(
								"DUE_DT",
								c.getString(40) == null ? "1900-01-01" : c
										.getString(40));
						cust_info.put(
								"ISSUE_DT",
								c.getString(41) == null ? "1900-01-01" : c
										.getString(41));
						cust_info.put("BILLPERIOD",
								c.getString(42) == null ? "" : c.getString(42));
						cust_info.put("BILLSERIALNO",
								c.getString(43) == null ? "" : c.getString(43));
						cust_info.put("OLDCSTS_CD",
								c.getString(44) == null ? "" : c.getString(44));
						cust_info.put("BILL_MTH", c.getString(45) == null ? ""
								: c.getString(45));
						cust_info.put("REMARKS", c.getString(46) == null ? ""
								: c.getString(46));
						cust_info.put("MACHINE_SRL_NO",
								c.getString(47) == null ? "" : c.getString(47));
						cust_info.put("MTR_READER_ID",
								c.getString(48) == null ? "" : c.getString(48));
						cust_info.put("MTR_READER_NAME",
								c.getString(49) == null ? "" : c.getString(49));
						cust_info.put("REB_OYT", c.getString(50) == null ? 0
								: Double.parseDouble(c.getString(50)));
						cust_info.put("REB_RTSWHT", c.getString(51) == null ? 0
								: Double.parseDouble(c.getString(51)));
						cust_info.put("DOM_SPLREB", c.getString(52) == null ? 0
								: Double.parseDouble(c.getString(52)));
						cust_info.put("ENGCHG_OLDTRF", 0);
						cust_info.put("FIXCHG_OLDTRF", 0);
						cust_info.put("ED_OLDTRF", 0);
						cust_info.put("NEWBD_OLDTRF", 0);
						cust_info.put("NEWED_OLDTRF", 0);
						cust_info.put("NEWDPS_OLDTRF", 0);
						cust_info.put("NEWOTH_OLDTRF", 0);
						cust_info.put("REFBLBD_OLDTRF", 0);
						cust_info.put("REFBLED_OLDTRF", 0);
						cust_info.put("REFBLDPS_OLDTRF", 0);
						cust_info.put("REFDEDBD_OLDTRF", 0);
						cust_info.put("REFDEDED_OLDTRF", 0);
						cust_info.put("REFDEDDPS_OLDTRF", 0);
						cust_info.put("REB_OFF_OLDTRF", 0);
						cust_info.put("NETBEFDUEDT_OLDTRF", 0);
						cust_info.put("NETAFTDUEDT_OLDTRF", 0);
						// cust_info.put("REB_HOSTEL", c.getString(69) == null
						// ?0:Double.parseDouble(c.getString(69)));
						cust_info.put("REB_HOSTEL", 0);
						cust_info.put("MAX_DEMD", c.getString(70) == null ? 0
								: Double.parseDouble(c.getString(70)));
						cust_info.put("APP_VERSION",
								c.getString(71) == null ? "" : c.getString(71));
						cust_info.put("BILL_PHASE", "1");
						cust_info.put("PHOTOID", c.getString(73) == null ? ""
								: c.getString(73));
						// cust_info.put("PHOTOTAKENDATE","1900-01-01");
						cust_info.put(
								"PHOTOTAKENDATE",
								c.getString(41) == null ? "1900-01-01" : c
										.getString(41));
						// cust_info.put("AGENCY_CODE", UtilAppCommon.ui.USER_ID
						// != null ?
						// UtilAppCommon.ui.USER_ID.substring(1,3):null);
						cust_info.put("AGENCY_CODE",
								c.getString(49) == null ? "" : c.getString(49)
										.substring(1, 3));
						// cust_info.put("COMPANY_ID",1);
						cust_info.put("COMPANY_ID",
								c.getString(76) == null ? "" : c.getString(76));
						cust_info.put("DateLastMaint", "1900-01-01");
						cust_info.put("ModifiedBy",
								c.getString(49) == null ? "" : c.getString(49));
						cust_info.put("Sessionid", "345");

						System.out.println("cust_info::" + cust_info);
						all_record_of_cust.put(cust_info);
					} while (c.moveToNext());
					System.out.println("SAVE BULK DATA  TO SERVER::"
							+ all_record_of_cust.toString());

				}

			} catch (Exception ex) {
				// TODO: handle exception
				ex.printStackTrace();
				all_record_of_cust = null;
				return "";
			}
		}
		return all_record_of_cust.toString();
	}

	public void updateDataSendingStatus(String cons_ref) {
		db = helper.getReadableDatabase();
		String qry = "UPDATE BillOutPut  set DATASENDINGSTATUS='Y' where cons_ref='"
				+ cons_ref + "'";
		// String
		// qry="UPDATE BillOutPut  set DATASENDINGSTATUS='Y' where CONS_REF NOT IN (1211000509,1211000509,1211000509,1211000509,1211000509) and DATASENDINGSTATUS='F' ";
		Cursor c = db.rawQuery(qry, null);
		String output = "";
		if (c.moveToFirst()) {
			output = c.getString(0);
			System.out.println("output:" + output);
		}

	}

	public void getDataForOutFileGen(Context context) {
		db = helper.getReadableDatabase();
		BufferedWriter bwrBilling = null;
		FileWriter outfileWriter = null;
		BufferedWriter bakbwrBilling = null;
		FileWriter bakoutfileWriter = null;
		try {
			String dirpath = Environment.getExternalStorageDirectory()
					.getPath() + "/SBDocs/OUTFILES";
			String extdirpath = "storage/extSdCard" + "/SBDocs/OUTFILES";
			if (Build.BRAND.compareTo("samsung") == 0) {
				try {
					File extdir = new File(extdirpath);
					if (!extdir.exists()) {
						extdir.mkdirs();
						System.out.println("folder created");
					} else {
						File files1[] = extdir.listFiles();
						for (int index = 0; index < files1.length; index++) {

							boolean wasDeleted = files1[index].delete();
							if (!wasDeleted) {
								// Deal with error
							}
						}
					}
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
			}

			File dir = new File(dirpath);
			if (!dir.exists()) {
				dir.mkdirs();
				System.out.println("folder created");
			} else {
				File files[] = dir.listFiles();
				for (int index = 0; index < files.length; index++) {

					boolean wasDeleted = files[index].delete();
					if (!wasDeleted) {
						// Deal with error
					}
				}
			}
			String filename = null;
			// filename = UtilAppCommon.getDate("ddMMyyyy|HHmmss");

			String query = "select * from BillOutput";
			System.out.println("query---" + query);
			Cursor c = db.rawQuery(query, null);
			int count = c.getCount();
			System.out.println("Total Rcord is     : " + count);
			int datac = 1;
			if (count > 0) {
				if (c.moveToFirst()) {

					do {
						if (c.getString(0) == null || c.getString(0) == "null"
								|| c.getString(0) == "") {
						} else {

							filename = c.getString(49) + "_" + c.getString(45);
							outfileWriter = new FileWriter(dirpath + "/"
									+ filename + ".OUT", true);
							bwrBilling = new BufferedWriter(outfileWriter);

							if (Build.BRAND.compareTo("samsung") == 0) {
								try {
									bakoutfileWriter = new FileWriter(
											extdirpath + "/" + filename
													+ ".OUT", true);
									bakbwrBilling = new BufferedWriter(
											bakoutfileWriter);
								} catch (Exception ex) {
									System.out.println(ex.toString());
								}

							}
							StringBuffer sb = new StringBuffer();
							sb.append(c.getString(0) + "|");
							sb.append(c.getString(1) + "|");
							sb.append(c.getString(2) + "|");
							sb.append(c.getString(3) + "|");
							sb.append(c.getString(4) + "|");
							sb.append((c.getString(5) == null ? "" : c
									.getString(5)) + "|");
							sb.append((c.getString(6) == null ? "" : c
									.getString(6)) + "|");
							sb.append((c.getString(7) == null ? "" : c
									.getString(7)) + "|");
							sb.append((c.getString(8) == null ? "" : c
									.getString(8)) + "|");
							sb.append((c.getString(9) == null ? "" : c
									.getString(9)) + "|");
							sb.append((c.getString(10) == null ? "" : c
									.getString(10)) + "|");
							sb.append((c.getString(11) == null ? "" : c
									.getString(11)) + "|");
							sb.append((c.getString(12) == null ? "" : c
									.getString(12)) + "|");
							sb.append((c.getString(13) == null ? "" : c
									.getString(13)) + "|");
							sb.append((c.getString(14) == null ? "" : c
									.getString(14)) + "|");
							sb.append((c.getString(15) == null ? "" : c
									.getString(15)) + "|");
							sb.append((c.getString(16) == null ? "" : c
									.getString(16)) + "|");
							sb.append((c.getString(17) == null ? "" : c
									.getString(17)) + "|");
							sb.append((c.getString(18) == null ? "" : c
									.getString(18)) + "|");
							sb.append((c.getString(19) == null ? "" : c
									.getString(19)) + "|");
							sb.append((c.getString(20) == null ? "" : c
									.getString(20)) + "|");
							sb.append((c.getString(21) == null ? "" : c
									.getString(21)) + "|");
							sb.append((c.getString(22) == null ? "" : c
									.getString(22)) + "|");
							sb.append((c.getString(23) == null ? "" : c
									.getString(23)) + "|");
							sb.append((c.getString(24) == null ? "" : c
									.getString(24)) + "|");
							sb.append((c.getString(25) == null ? "" : c
									.getString(25)) + "|");
							sb.append((c.getString(26) == null ? "" : c
									.getString(26)) + "|");
							sb.append((c.getString(27) == null ? "" : c
									.getString(27)) + "|");
							sb.append((c.getString(28) == null ? "" : c
									.getString(28)) + "|");
							sb.append((c.getString(29) == null ? "" : c
									.getString(29)) + "|");
							sb.append((c.getString(30) == null ? "" : c
									.getString(30)) + "|");
							sb.append((c.getString(31) == null ? "" : c
									.getString(31)) + "|");
							sb.append((c.getString(32) == null ? "" : c
									.getString(32)) + "|");
							sb.append((c.getString(33) == null ? "" : c
									.getString(33)) + "|");
							sb.append((c.getString(34) == null ? "" : c
									.getString(34)) + "|");
							sb.append((c.getString(35) == null ? "" : c
									.getString(35)) + "|");
							sb.append((c.getString(36) == null ? "" : c
									.getString(36)) + "|");
							sb.append((c.getString(37) == null ? "" : c
									.getString(37)) + "|");
							sb.append((c.getString(38) == null ? "" : c
									.getString(38)) + "|");
							sb.append((c.getString(39) == null ? "" : c
									.getString(39)) + "|");
							sb.append((c.getString(40) == null ? "" : c
									.getString(40)) + "|");
							sb.append((c.getString(41) == null ? "" : c
									.getString(41)) + "|");
							sb.append((c.getString(42) == null ? "" : c
									.getString(42)) + "|");
							sb.append((c.getString(43) == null ? "" : c
									.getString(43)) + "|");
							sb.append((c.getString(44) == null ? "" : c
									.getString(44)) + "|");
							sb.append((c.getString(45) == null ? "" : c
									.getString(45)) + "|");
							sb.append((c.getString(46) == null ? "" : c
									.getString(46)) + "|");
							sb.append((c.getString(47) == null ? "" : c
									.getString(47)) + "|");
							sb.append((c.getString(48) == null ? "" : c
									.getString(48)) + "|");
							sb.append((c.getString(49) == null ? "" : c
									.getString(49)) + "|");
							sb.append((c.getString(50) == null ? "" : c
									.getString(50)) + "|");
							sb.append((c.getString(51) == null ? "" : c
									.getString(51)) + "|");
							sb.append((c.getString(52) == null ? "" : c
									.getString(52)) + "|");
							sb.append((c.getString(53) == null ? "" : c
									.getString(53)) + "|");
							sb.append((c.getString(54) == null ? "" : c
									.getString(54)) + "|");
							sb.append((c.getString(55) == null ? "" : c
									.getString(55)) + "|");
							sb.append((c.getString(56) == null ? "" : c
									.getString(56)) + "|");
							sb.append((c.getString(57) == null ? "" : c
									.getString(57)) + "|");
							sb.append((c.getString(58) == null ? "" : c
									.getString(58)) + "|");
							sb.append((c.getString(59) == null ? "" : c
									.getString(59)) + "|");
							sb.append((c.getString(60) == null ? "" : c
									.getString(60)) + "|");
							sb.append((c.getString(61) == null ? "" : c
									.getString(61)) + "|");
							sb.append((c.getString(62) == null ? "" : c
									.getString(62)) + "|");
							sb.append((c.getString(63) == null ? "" : c
									.getString(63)) + "|");
							sb.append((c.getString(64) == null ? "" : c
									.getString(64)) + "|");
							sb.append((c.getString(65) == null ? "" : c
									.getString(65)) + "|");
							sb.append((c.getString(66) == null ? "" : c
									.getString(66)) + "|");
							sb.append((c.getString(67) == null ? "" : c
									.getString(67)) + "|");
							sb.append((c.getString(68) == null ? "" : c
									.getString(68)) + "|");
							sb.append((c.getString(69) == null ? "" : c
									.getString(69)) + "|");
							sb.append((c.getString(70) == null ? "" : c
									.getString(70)) + "|");
							sb.append((c.getString(71) == null ? "" : c
									.getString(71)) + "|");
							sb.append((c.getString(72) == null ? "" : c
									.getString(72)) + "|");
							sb.append((c.getString(73) == null ? "" : c
									.getString(73)) + "|");
							sb.append((c.getString(74) == null ? "" : c
									.getString(74)) + "|");
							sb.append((c.getString(75) == null ? "" : c
									.getString(75)) + "|");
							sb.append((c.getString(76) == null ? "" : c
									.getString(76)) + "|");
							sb.append((c.getString(77) == null ? "" : c
									.getString(77)) + "|");
							sb.append((c.getString(78) == null ? "" : c
									.getString(78)));
							// sb.append((c.getString(79) == null
							// ?"":c.getString(79))+"|");
							// sb.append((c.getString(80) == null
							// ?"":c.getString(80)));
							System.out.println(datac
									+ " record inserted.......");
							bwrBilling.write(sb.toString());
							bwrBilling.newLine();
							try {
								if (Build.BRAND.compareTo("samsung") == 0) {
									if (bakbwrBilling != null) {
										bakbwrBilling.write(sb.toString());
										bakbwrBilling.newLine();
										bakbwrBilling.flush();
									}
								}
							} catch (Exception Ex) {
								System.out.println("Error: " + Ex.toString());
							}
							bwrBilling.flush();

						}
						datac++;

					} while (c.moveToNext());

				}

				bwrBilling.close();
				outfileWriter.close();
				if (Build.BRAND.compareTo("samsung") == 0) {
					bakbwrBilling.close();
					bakoutfileWriter.close();
				}
				Toast.makeText(
						context,
						"Outfile generated successfully. In SBDocs/OUTFILES Folder",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, "No Record Found....",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		helper.close();
	}

	
	public ArrayList<HashMap<String, String>> getSummary() {
        db = helper.getReadableDatabase();
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        String qry = String
                        .format("select  tt1.mru ,coalesce(tt2.billed_consumer,0) as billed,tt1.total_consumer-coalesce(tt2.billed_consumer,0) as unbilled_consumer , "
                                        + "tt1.total_consumer ,coalesce(tt3.posted,0) as posted,coalesce(tt4.pending,0) as pending from "
                                        + "(select mru,count(*) as total_consumer from BillInput group by mru order by MRU) as tt1 left outer join "
                                        + "(select MRU,count(*) as billed_consumer from BillOutput group by MRU ) as tt2 ON  tt1.mru = tt2.mru left outer join "
                                        + "(select  mru,COUNT(*) AS posted from BillOutput    group by mru ) AS tt3 ON  tt1.mru = tt3.mru left outer join "
                                        + "(select  mru,COUNT(*) AS pending from BillOutput     group by mru ) AS tt4 ON  tt1.mru = tt4.mru");

        // String
        // qry=String.format("select sdo_cd,binder,acc_no from BillOutput where CUR_METER_STAT = '%s'","L");
        System.out.println("query---" + qry);
        Cursor c = db.rawQuery(qry, null);

        HashMap<String, String> map = null;
        if (c.moveToFirst()) {
                do {

                        
                        map = new HashMap<String, String>();
                        map.put("MRU", c.getString(0));
                        map.put("BILLED", c.getString(1));
                        map.put("UNBILLED", c.getString(2));
                        map.put("TOTAL", c.getString(3));
                        map.put("POSTED", c.getString(4));
                        //map.put("PENDING", c.getString(6));
                        list.add(map);

                } while (c.moveToNext());
        }

        return list;
}

	
	public ArrayList<HashMap<String, String>> getSummaryReport() {
		db = helper.getReadableDatabase();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		String qry = String
				.format("select t2.SUB_DIVISION_CODE,t1.MRU,t1.billed, t2.total,t1.unit from "
						+ "(select MRU, count(*) as billed,sum(BILL_UNITS) as unit from BillOutput group by MRU,SUB_DIVISION_CODE) as t1 "
						+ "left outer join "
						+ "(select MRU, count(*) as total from BillInput group by MRU ) as t2 "
						+ "on and t1.MRU = t2.MRU");
		System.out.println("query---" + qry);
		Cursor c = db.rawQuery(qry, null);

		HashMap<String, String> map = null;
		if (c.moveToFirst()) {
			do {

				map = new HashMap<String, String>();
				map.put("SDO", c.getString(0));
				map.put("MRU", c.getString(1));
				map.put("billed", c.getString(2));
				map.put("total", c.getString(3));
				map.put("units", c.getString(4));
				list.add(map);

			} while (c.moveToNext());
		}

		return list;
	}

	public ArrayList<HashMap<String, String>> getBuBlistItems() {
		db = helper.getReadableDatabase();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		/*
		String qry = String
				.format("select  tt1.MRU ,coalesce(tt2.billed_consumer,0) as billed,tt1.total_consumer-coalesce(tt2.billed_consumer,0) as unbilled_consumer , "
						+ "tt1.total_consumer from (select MRU,count(*) as total_consumer from BillInput group by  MRU ) as tt1 "
						+ "left outer join  (select MRU,count(*) as billed_consumer from BillOutput group by MRU ) as tt2 "
						+ "on tt1.MRU = tt2.MRU");*/
		
		
		String qry = String
				.format("SELECT x.mru,coalesce(x.billed,0)+coalesce(y.outsort,0) billed , "
		         + "(x.total-(coalesce(x.billed,0)+coalesce(y.outsort,0))) unbilled_consumer ,"
		         + " x.total Total FROM "
		         + " ( select t2.mru as 'mru',t2.total,t1.billed from "
		         + " (select mru , count(*) as total from BillInput group by mru ) as t2 "
		         + " left outer join "
		         + " (select mru, count(*) as billed from BillOutput group by mru) as t1 "
		         + " on  t1.mru = t2.mru) X LEFT JOIN "
		         + " ( select a.mru as 'mru',count(*) outsort from billinput a,sapinput b "
		         + " where a.contract_ac_no=b.CAnumber and b.Msgid='4' group by a.mru)  Y "
		         + "ON "
		         + "X.mru=Y.mru ");
		
		System.out.println("query---" + qry);
		Cursor c = db.rawQuery(qry, null);

		HashMap<String, String> map = null;
		if (c.moveToFirst()) {
			do {

				map = new HashMap<String, String>();
				map.put("MRU", c.getString(0));
				map.put("BILLED", c.getString(1));
				map.put("UNBILLED", c.getString(2));
				map.put("TOTAL", c.getString(3));
				list.add(map);

			} while (c.moveToNext());
		}

		return list;
	}

	public int getBillOutputRowCount(String accno) {
		db = helper.getReadableDatabase();
		String query = null;

		int intbillRowCount = 0;

		Log.e("getBillOutputRowCount", "Entering Start");
		query = String.format("select * from BillOutput where CANumber = '%s' ;",accno);
		System.out.println("query---" + query);
		Cursor c = db.rawQuery(query, null);
		intbillRowCount = c.getCount();
		//intUserInfoRowCount=1;
		return intbillRowCount;

	}
	
	public int getsapinputRowCount(String accno) {
		db = helper.getReadableDatabase();
		String query = null;

		int intbillRowCount = 0;

		System.out.println("Entering into the database part \n");
		query = String.format("select * from sapinput where CANumber = '%s' ;",accno);
		System.out.println("query---" + query);
		Cursor c = db.rawQuery(query, null);
		intbillRowCount = c.getCount();
		//intUserInfoRowCount=1;
		return intbillRowCount;

	}
	
	public ArrayList<HashMap<String, String>> getNetworkFailureList(
			String bellowMonth) {
		db = helper.getReadableDatabase();
		int days = Integer.parseInt(bellowMonth);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		String qry = String
				.format("select a.MRU,a.contract_ac_no ACCNO,a.consumer_name,a.address, a.METER_MANUFACTURER_SR_NO from billinput a,sapinput b "+
		                "where a.contract_ac_no=b.CANumber and b.MSGID='7'");
		System.out.println("query---" + qry);
		Cursor c = db.rawQuery(qry, null);

		HashMap<String, String> map = null;
		if (c.moveToFirst()) {
			do {

				map = new HashMap<String, String>();
				map.put("MRU", c.getString(0));
				map.put("ACC_NO", c.getString(1));
				map.put("NAME", c.getString(2));
				map.put("ADDR", c.getString(3));
				map.put("METER_MANUFACTURER_SR_NO", c.getString(4));
				list.add(map);

			} while (c.moveToNext());
		}

		return list;
	}
	
	public void getDuplicateBillRecord() {
		db = helper.getReadableDatabase();

		try {
			String qry = String
					.format("select * from BillOutput where binder ='%s' and acc_no = '%04d'");
			Cursor c = db.rawQuery(qry, null);
			System.out.println("total  : " + c.getCount());
			UtilAppCommon.in = null;
			UtilAppCommon.out = null;
			if (c.moveToFirst()) {
				do {
					System.out.println(c.getString(0));
					UtilAppCommon.in = new StructInput();
					UtilAppCommon.out = new StructOutput();
					UtilAppCommon.copyResultsetToInputClass(c);
				} while (c.moveToNext());
			} else {
				System.out.println("nullllllllllllllllllllllll");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// ---Added on 17Dec by Sudhir
	public void getUserInfo() {
		db = helper.getReadableDatabase();

		try {
			String qry = String.format("select * from UserInfo");
			Cursor curUserInfo = db.rawQuery(qry, null);
			System.out.println("total  : " + curUserInfo.getCount());
			Log.e("getUserInfo => ","total  : " + curUserInfo.getCount());
			UtilAppCommon.ui = null;
			
			if (curUserInfo.moveToFirst()) {
				do {
					System.out.println(curUserInfo.getString(0));
					UtilAppCommon.ui = new StructUserInfo();
					UtilAppCommon.copyResultsetToUserInfoClass(curUserInfo);
				} while (curUserInfo.moveToNext());
			} else {
				UtilAppCommon.TestcopyResultsetToUserInfoClass();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// End Add
	public String[] GetPrinterInfo() {
		db = helper.getReadableDatabase();
		String[] printer = new String[2];

		try {
			String qry = String
					.format("select printerid,printertype from UserInfo");
			Cursor curprinterInfo = db.rawQuery(qry, null);

			if (curprinterInfo.moveToFirst()) {
				printer[0] = curprinterInfo.getString(0);
				printer[1] = curprinterInfo.getString(1);
			}
			curprinterInfo.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return printer;
		}

		return printer;
	}

	public ArrayList<HashMap<String, String>> getNewConsumerList(
			String bellowMonth) {
		db = helper.getReadableDatabase();
		int days = Integer.parseInt(bellowMonth);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		String qry = String
				.format("select binder,acc_no,name,addr1, METER_MANUFACTURER_SR_NO from BillInput where sc_date !=''");

		System.out.println("query---" + qry);
		Cursor c = db.rawQuery(qry, null);

		HashMap<String, String> map = null;
		if (c.moveToFirst()) {
			do {

				map = new HashMap<String, String>();
				map.put("BINDER", c.getString(0));
				map.put("ACC_NO", c.getString(1));
				map.put("NAME", c.getString(2));
				map.put("ADDR", c.getString(3));
				map.put("METER_MANUFACTURER_SR_NO", c.getString(4));
				list.add(map);

			} while (c.moveToNext());
		}

		return list;
	}
	
	// Added on 15Mar14
	public ArrayList<HashMap<String, String>> getUnbilledConsList(String BINDER) {
		db = helper.getReadableDatabase();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		String qry = String
				.format(" select MRU,contract_ac_no,consumer_name,address, METER_MANUFACTURER_SR_NO from BillInput where  contract_ac_no not in ( select CANumber from SAPInput where MSGID in ('3','4','5','6')) ");
						//BINDER);
		System.out.println("query---" + qry);
		Cursor c = db.rawQuery(qry, null);

		HashMap<String, String> map = null;
		if (c.moveToFirst()) {
			do {

				map = new HashMap<String, String>();
				map.put("MRU", c.getString(0));
				map.put("ACC_NO", c.getString(1));
				map.put("NAME", c.getString(2));
				map.put("ADDR", c.getString(3));
				map.put("METER_MANUFACTURER_SR_NO", c.getString(4));
				list.add(map);

			} while (c.moveToNext());
		}

		return list;
	}
	
	
	// End Add

	public boolean checkDoubleBilling(String acNo) {
		//Log.e("validateDoubleBilling", "Start");
		db = helper.getReadableDatabase();
		boolean isBilled = false;
		
		String query = null;
		
		query = String.format("select * from BillOutput where CANumber = '%s';", acNo);
		Cursor c = db.rawQuery(query, null);
		//Log.e("checkDoubleBilling =>> ", query);

		try {

			if (c.moveToFirst()) {
				isBilled = true;
				//Log.e("checkDoubleBilling", "Already Billed " + acNo);
			}
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("checkDoubleBilling", "" + e.getMessage());
		}		
		return isBilled;
	}

	public Cursor checkDoubleinSAPInput(String acNo)
	{
		String query = null;
		query = String.format("select * from SAPInput where CANumber = '%s' and SCHEDULED_BILLING_DATE IS NOT NULL;", acNo);
		Log.e("checkDoubleBilling =>> ", query);
		Cursor c = db.rawQuery(query, null);
		try {

			if (c.moveToFirst()) {
			
				UtilAppCommon.inSAPInputTab = true;
				return c;
			}
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("checkDoubleinSAPOutput", "" + e.getMessage());
		}
		return null;
	}

	public Cursor checkDoubleinSAPBlueInput(String acNo)
	{
		String query = null;
		query = String.format("select * from SAPBlueInput where CANumber = '%s' and SCHEDULED_BILLING_DATE IS NOT NULL;", acNo);
		Log.e("checkDoubleBilling =>> ", query);
		Cursor c = db.rawQuery(query, null);
		try {

			if (c.moveToFirst()) {

				UtilAppCommon.inSAPInputTab = true;
				return c;
			}
		}

		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("checkDoubleinSAPOutput", "" + e.getMessage());
		}
		return null;
	}
	
	public Cursor unbilledlist()
	{
		String query = null;
		query = String.format("select Contract_ac_no,CONSUMER_LEGACY_ACC_NO from BillInput where Contract_ac_no not in (Select CANumber from SAPInput where MSGID in ('3','4','5','6'))");
		//Log.e("checkDoubleBilling =>> ", query);
		Cursor c = db.rawQuery(query, null);
		try {

			if (c.moveToFirst()) {
				return c;
			}
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("checkDoubleinSAPOutput", "" + e.getMessage());
		}
		return null;
	}
	
	
	public Cursor summarylist()
	{
		String query = null;
		query = String
		.format("SELECT x.category,x.total TotalCons,coalesce(x.billed,0)+coalesce(y.outsort,0) Billed , " 
				+" coalesce(x.unit,0) Units,coalesce(x.billAmt,0) BilledAmt,x.total-(coalesce(x.billed,0)+coalesce(y.outsort,0)) Pending FROM "
				+ "( select t2.rate_category as 'category',t2.total,t1.billed,t1.unit,t1.BillAmt from  "
		+ "(select Rate_Category , count(*) as total from BillInput group by Rate_Category ) as t2  "
		+ "left outer join "
		+ "(select Category, count(*) as billed,sum(BILLEDUNITS) as unit,sum(subtotal_b) as BillAmt from BillOutput group by Category) as t1 "
		+ "on  t1.category = t2.rate_category) X LEFT JOIN "
		+ "( select a.rate_category as 'category',count(*) outsort from billinput a,sapinput b "
		+ "where a.contract_ac_no=b.CAnumber and b.Msgid in ('3','4','5') )  Y "
        + "ON "
		+ " X.category=Y.category	");

		Cursor c = db.rawQuery(query, null);
		try {

			if (c.moveToFirst()) {
				return c;
			}
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("checkDoubleinSAPOutput", "" + e.getMessage());
		}
		return null;
	}

	public Cursor getSAPInput(String acNo)
	{
		String query = null;
		query = String.format("select * from SAPInput where CANumber = '%s';", acNo);
		//Log.e("checkDoubleBilling =>> ", query);
		Cursor c = db.rawQuery(query, null);
		try {
			UtilAppCommon.SAPIn = null;
			if (c.moveToFirst()) {
				UtilAppCommon.SAPIn = new StructSAPInput();
				UtilAppCommon.copySAPInputData1(c);
				return c;
			}
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("checkDoubleinSAPOutput", "" + e.getMessage());
		}
		return null;
	}

	public Cursor getSAPBlueInput(String acNo)
	{
		String query = null;
		query = String.format("select * from SAPBlueInput where CANumber = '%s';", acNo);
		//Log.e("checkDoubleBilling =>> ", query);
		Cursor c = db.rawQuery(query, null);
		try {
			UtilAppCommon.SAPBlueIn = null;
			if (c.moveToFirst()) {
				UtilAppCommon.SAPBlueIn = new StructSAPBlueInput();
				UtilAppCommon.copySAPBlueInputData(c);
				return c;
			}
		}

		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("checkDoubleinSAPBOutput", "" + e.getMessage());
		}
		return null;
	}
	
	
	@SuppressLint("NewApi")
	public boolean deleteFromSAPInput(String acNo){
		try {
			db = helper.getWritableDatabase();
			String query=String.format("Delete from SAPInput WHERE CANumber='%s' ", acNo);
			SQLiteStatement stmt=db.compileStatement(query);
			stmt.executeUpdateDelete();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("deleteFromSAPInput", e.getMessage());
			return false;
		}
		return true;
	}
	
	public String getSdocd() {
		db = helper.getReadableDatabase();

		String sdo_cd = null;
		try {
			Cursor c = db.rawQuery("SELECT sdo_code  FROM UserInfo", null);
			if (c.moveToFirst()) {
				sdo_cd = c.getString(0);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return sdo_cd;

	}

	public String getUserORMrId() {
		db = helper.getReadableDatabase();
		SQLiteStatement stmt = db
				.compileStatement("SELECT user_id FROM UserInfo");
		String user_id = null;
		try {
			user_id = stmt.simpleQueryForString();
			stmt.execute();
			System.out.println("sdocode & stmt ::   " + user_id + " ---  "
					+ stmt);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return user_id;

	}

	public String getActiveBinder() {
		db = helper.getReadableDatabase();
		SQLiteStatement stmt = db
				.compileStatement("SELECT DISTINCT active_MRU  FROM UserInfo");
		String active_binder = null;

		try {
			active_binder = stmt.simpleQueryForString();
			System.out.println("active_binder & stmt ::   " + active_binder
					+ " ---  " + stmt);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return active_binder;
	}

	public String binderlist() {
		db = helper.getReadableDatabase();
		SQLiteStatement stmt = db
				.compileStatement("SELECT DISTINCT MRUs  FROM UserInfo");
		String active_binder = null;

		try {
			active_binder = stmt.simpleQueryForString();
			System.out.println("binder & stmt ::   " + active_binder + " ---  "
					+ stmt);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return active_binder;
	}

	public ArrayList<String> getBinderList(String sdo_cd) {
		System.out.println("inside get Binder LIst.....");
		db = helper.getReadableDatabase();

		Cursor c = db.query(true, "BillInput", new String[] { "MRU" },
				"SUB_DIVISION_CODE='" + sdo_cd + "'", null, null, null, "MRU", null);
		System.out.println("Cursor ========" + c.toString());

		StringBuffer binder_code = new StringBuffer();

		if (c.moveToFirst()) {

			do {
				System.out.println("binder ==========" + binder_code);
				if (!(c.getString(0).equals("binder"))) {
					binder_code.append(c.getString(0) + ",");
				}

				System.out.println("binder_code  after append =========="
						+ binder_code);

			} while (c.moveToNext());

		}

		ArrayList<String> result = new ArrayList<String>();
		result.add(binder_code.toString());

		return result;

	}

	private static String convyyyymmdd(String a) {
		String b = "";
		b = "20" + a.substring(6, 8) + a.substring(3, 5) + a.substring(0, 2);
		return b;
	}
	
	public void getbilldata(String accno) {
		db = helper.getReadableDatabase();

		String query;
		query = String.format(
				"select * from BillOutput where CANumber = '%s' ;", accno);

		System.out.println("query---" + query);
		Cursor c = db.rawQuery(query, null);

		System.out.println("Records" + c.getCount());
		JSONObject result = new JSONObject();
		try {

			//UtilAppCommon.bill = null;
			if (c.moveToFirst()) {

				do {

					//UtilAppCommon.bill = new StructBillInfo();
					//UtilAppCommon.copyResultsetToBillInfoClass(c);
					int colCount = 69;
					for (int i = 0; i < colCount; i++) {
						System.out
								.println("column name == " + c.getColumnName(i)
										+ "-----" + c.getString(i));
						result.put(c.getColumnName(i),
								c.getString(i) != null ? c.getString(i).trim()
										: " ");

					}
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private static String convddmmyyyy(String a) {
		String b = "";
		// b = "20" + a.substring(6, 8) + a.substring(3, 5) + a.substring(0, 2);
		b = a.substring(0, 2) + a.substring(3, 5) + "20" + a.substring(6, 8);
		return b;
	}

	public static long getMonthsBetweenDates(Date startDate, Date endDate) {
		if (startDate.getTime() > endDate.getTime()) {
			Date temp = startDate;
			startDate = endDate;
			endDate = temp;
		}
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		
		Long timediff= endDate.getTime()- startDate.getTime();
		
		
//		int yearDiff = endCalendar.get(Calendar.YEAR)
//				- startCalendar.get(Calendar.YEAR);
//		int monthsBetween = endCalendar.get(Calendar.MONTH)
//				- startCalendar.get(Calendar.MONTH) + (12 * yearDiff);
//		
//		if (endCalendar.get(Calendar.DAY_OF_MONTH) >= startCalendar
//				.get(Calendar.DAY_OF_MONTH))
//			monthsBetween = monthsBetween + 1;
		
		long monthsBetween=timediff/Long.parseLong("2592000000");
		return monthsBetween;

	}

	public void insertIntoUserVal(String isValid) {
		db = helper.getWritableDatabase();
		long rowId = 0;
		// SQLiteStatement stmt =
		// db.compileStatement("INSERT INTO UserInfo VALUES(  ?,?,?,?,?,?,?,?,?,?);");
		String str = "INSERT INTO validUsr VALUES(" + isValid + ")";
		SQLiteStatement vldUser = db.compileStatement(str);
		vldUser.executeInsert();
	}
	
	public Boolean getLocalDevValidation(String IMEINo) {
		db = helper.getReadableDatabase();
		String query = null;

		int intUserInfoRowCount = 0;

		System.out.println("Entering into the database part \n");

		query = String.format(
				"select * from validDevice WHERE IMEINo='%s' AND DEV_VALID ='1'", IMEINo);
		System.out.println("query---" + query);
		Cursor c = db.rawQuery(query, null);
		intUserInfoRowCount = c.getCount();
		//intUserInfoRowCount = 1;
		if (intUserInfoRowCount > 0) {
			Log.e("Valid Device", "True");
			return true;
		} else {
			Log.e("Valid Device", "False");
			return false;
		}
	}

	public Boolean getLocalAuthentication(String UserName, String Password) {
		db = helper.getReadableDatabase();
		String query = null;

		int intUserInfoRowCount = 0;

		System.out.println("Entering into the database part \n");
        try {
			query = "select * from UserInfo where meterreader_id=? and password=?";
			Cursor c = db.rawQuery(query, new String[]{UserName.trim().toUpperCase(), Password.trim()});
			/*query = String.format(
					"select * from UserInfo WHERE meterreader_id='%s' AND password='%s' ", UserName.trim(), Password.trim());
			System.out.println("query---" + query);*/
			//Cursor c = db.rawQuery(query, null);
			intUserInfoRowCount = c.getCount();
		}catch (Exception ex){
			ex.printStackTrace();
			Log.e("UserInfo",ex.getMessage());
			return false;
		}
		//intUserInfoRowCount = 1;
		if (intUserInfoRowCount > 0) {
			Log.e("Authenticate", "True");
			return true;
		} else {
			Log.e("Authenticate", "False");
			return false;
		}
	}
	
	public int readLocalAuthentication() {
		db = helper.getReadableDatabase();
		String query = null;

		int intUserInfoRowCount = 0;

		//System.out.println("Entering into the database part \n");

		query = String.format("select * from UserInfo");
		//System.out.println("query---" + query);
		Cursor c = db.rawQuery(query, null);
		intUserInfoRowCount = c.getCount();
		Log.e("intUserInfoRowCnt ==>> ", intUserInfoRowCount + "");
		
		c.moveToFirst();
		if (c.moveToFirst()) {
			do {

				Log.e("User Count ==>> ", c.getString(0) + "");
				Log.e("User Count ==>> ", c.getString(1) + "");
				Log.e("User Count ==>> ", c.getString(2) + "");
				Log.e("User Count ==>> ", c.getString(3) + "");
				Log.e("User Count ==>> ", c.getString(4) + "");
			
			} while (c.moveToNext());
		}
		
		

		return intUserInfoRowCount;
	}

	public int getUserVal() {
		String qry = String.format("select * from validUsr");
		Cursor curUserInfo = db.rawQuery(qry, null);
		return curUserInfo.getCount();
	}

	// BIKASH
	public ArrayList<HashMap<String, String>> getSrcListByConsName(
			String ConName) {
		db = helper.getReadableDatabase();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		String qry = "select MRU, CONTRACT_AC_NO, NAME, ADDRESS from billinput where name like '%"
				+ ConName + "%'";
		System.out.println("query---" + qry);
		Cursor c = db.rawQuery(qry, null);

		HashMap<String, String> map = null;
		if (c.moveToFirst()) {
			do {

				map = new HashMap<String, String>();
				map.put("BINDER", c.getString(0));
				map.put("ACC_NO", c.getString(1));
				map.put("NAME", c.getString(2));
				map.put("ADDR", c.getString(3));
				list.add(map);

			} while (c.moveToNext());
		}

		return list;
	}

	public ArrayList<HashMap<String, String>> getSrcListByOldAccNo(
			String OldAccNo) {
		db = helper.getReadableDatabase();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		String qry = "select MRU, CONTRACT_AC_NO, NAME, ADDRESS from billinput where CONSUMER_LEGACY_ACC_NO = '"
				+ OldAccNo + "'";
		System.out.println("query---" + qry);
		Cursor c = db.rawQuery(qry, null);

		HashMap<String, String> map = null;
		if (c.moveToFirst()) {
			do {

				map = new HashMap<String, String>();
				map.put("BINDER", c.getString(0));
				map.put("ACC_NO", c.getString(1));
				map.put("NAME", c.getString(2));
				map.put("ADDR", c.getString(3));
				list.add(map);

			} while (c.moveToNext());
		}

		return list;
	}

	public int updateMRU(String binder) {
		try {

			db = helper.getReadableDatabase();
			System.out.println("String binder33333333333########" + binder);

			SQLiteStatement stmt = db
					.compileStatement("UPDATE UserInfo set active_MRU='"
							+ binder.trim() + "'");
			String sdo_cd = null;
			sdo_cd = stmt.simpleQueryForString();
			stmt.execute();

		} catch (Exception e) {
			System.out.println("DataBase Updated Exception::@@@@@@@@@@@@@@@@");
		}
		return 0;
	}

	public int updateUserInfo() {
		try {

			db = helper.getReadableDatabase();
			//System.out.println("String binder33333333333########" + binder);

			String strMRU = getActiveMRU();
			String strSDO = getSdoCode();
			SQLiteStatement stmt = db
					.compileStatement("UPDATE UserInfo set active_MRU='" + strMRU + "', sdo_code='" + strSDO + "'");
			String sdo_cd = null;
			sdo_cd = stmt.simpleQueryForString();
			stmt.execute();

		} catch (Exception e) {
			System.out.println("DataBase Updated Exception::@@@@@@@@@@@@@@@@");
		}
		return 0;
	}
	
	public String getUpostedDataCount() {
		db = helper.getReadableDatabase();

		String qry = "select count(*) from BillOutPut where DATASENDINGSTATUS='N'";
		System.out.println("query---" + qry);
		Cursor c = db.rawQuery(qry, null);
		String output = "";

		if (c.moveToFirst()) {
			output = c.getString(0);
		}

		return output;
	}

	public String getactiveMRU() {
		db = helper.getReadableDatabase();

		try {
			String qry = String.format("select active_MRU from UserInfo");
			Cursor curUserInfo = db.rawQuery(qry, null);
			System.out.println("total  : " + curUserInfo.getCount());
			// UtilAppCommon.ui = null;
			String actBndr;
			if (curUserInfo.moveToFirst()) {
				do {
					System.out.println(curUserInfo.getString(0));
					actBndr = curUserInfo.getString(0);
					return actBndr;
				} while (curUserInfo.moveToNext());
			} else {
				System.out.println("nullllllllllllllllllllllll");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	public String getSubDivision() {
		db = helper.getReadableDatabase();

		try {
			String qry = String.format("select sdo_code from UserInfo");
			Cursor curUserInfo = db.rawQuery(qry, null);
			System.out.println("total  : " + curUserInfo.getCount());
			// UtilAppCommon.ui = null;
			String sdo_code;
			if (curUserInfo.moveToFirst()) {
				do {
					System.out.println(curUserInfo.getString(0));
					sdo_code = curUserInfo.getString(0);
					return sdo_code;
				} while (curUserInfo.moveToNext());
			} else {
				System.out.println("nullllllllllllllllllllllll");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}
	
	// Database update method created by Kishore
	public String getCurrBillMonth() {
		db = helper.getReadableDatabase();

		try {
			String qry = String.format("select bill_month from UserInfo");
			Cursor curUserInfo = db.rawQuery(qry, null);
			System.out.println("total  : " + curUserInfo.getCount());
			// UtilAppCommon.ui = null;
			String actBndr;
			if (curUserInfo.moveToFirst()) {
				do {
					System.out.println(curUserInfo.getString(0));
					actBndr = curUserInfo.getString(0);
					return actBndr;
				} while (curUserInfo.moveToNext());
			} else {
				System.out.println("nullllllllllllllllllllllll");
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("UtilDB", "getCurrBillMonth ==>> " + e.getMessage());
		}
		return "";
	}

	public int getUnpostedDataCount() {
		db = helper.getReadableDatabase();
		String query = null;

		int intUserInfoRowCount = 0;

		// System.out.println("Entering into the database part \n");

		query = String
				.format("select count(*) from BillOutPut where DATASENDINGSTATUS='N'");
		System.out.println("query---" + query);
		Cursor c = db.rawQuery(query, null);
		intUserInfoRowCount = Integer.parseInt(c.getString(0));

		return intUserInfoRowCount;

	}

		public String ClearUserInfo() {

		db = helper.getReadableDatabase();

		db.execSQL("BEGIN TRANSACTION");
		try {
			db.execSQL("DELETE FROM UserInfo");
			//db.execSQL("DELETE FROM bill_duedt");

		} catch (Exception ex) {
			db.execSQL("ROLLBACK");
			return "User info data could not cleared";
		}
		db.execSQL("COMMIT");
		return "User info data cleared successfully";

	}

	public String ClearBilledData() {
		String bill_mth = getCurrBillMonth();
		db = helper.getReadableDatabase();
		//String Month = new DateFormatSymbols().getMonths()[Integer
		//		.parseInt(bill_mth.substring(4, 6)) - 1];

		try {
			//db.execSQL(String.format(
			//		"DELETE FROM BillInfo where  BILL_PERIOD != '%s'", Month
			//				+ bill_mth.substring(2, 4)));
			db.execSQL(String.format("DELETE FROM BillInput"));
			db.execSQL(String.format("DELETE FROM BillOutPut"));
			//db.execSQL("DELETE FROM bill_duedt");
		} catch (Exception ex) {
			return "Data could not cleared";
		}
		return "Data cleared successfully";

	}

	private double roundDoubleUptoTwoDecimal(double x, int numDecimals) {
		long y = (long) x;
		long i = 0;
		double z, m, q, r;

		z = x - y;
		m = 1;
		for (i = 0; i < numDecimals; i++) {
			m = m * 10;
		}
		q = (double) (z * m);
		r = (double) ((long) (q < 0 ? (q - 0.5) : (q + 0.5)));

		return (double) (y) + (1.0 / m) * r;
	}
		
	public String GetConsumerInfoByField(String strParameter, String Field, String returnParam) {
		
		String value;
		Log.e("GetConsumerInfoByField", strParameter + "  <>  " + Field + "  <>  " + returnParam);
		try {
			String qry = "";			
			db = helper.getReadableDatabase();
			value = null;
			
			if(Field.equalsIgnoreCase("Accno"))	{
				qry = String.format("select * from BillInput where CONTRACT_AC_NO='%s'", strParameter.toUpperCase());
			}
			
			if(Field.equalsIgnoreCase("Legacy"))	{
				qry = String.format("select * from BillInput where CONSUMER_LEGACY_ACC_NO='%s'", strParameter.toUpperCase());
			}
			
			if(Field.equalsIgnoreCase("Meter"))	{
				qry = String.format("select * from BillInput where METER_MANUFACTURER_SR_NO ='%s'", strParameter.toUpperCase());
			}
			
			Log.e("qry ==>> ", qry);
			Cursor c = null;
			c = db.rawQuery(qry, null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				value = c.getString(c.getColumnIndex(returnParam));
				Log.e("value ==>> ", value);
				UtilAppCommon.legNbr = c.getString(c.getColumnIndex("CONSUMER_LEGACY_ACC_NO"));
				UtilAppCommon.acctNbr = c.getString(c.getColumnIndex("CONTRACT_AC_NO"));
				UtilAppCommon.meterNbr = c.getString(c.getColumnIndex("METER_MANUFACTURER_SR_NO"));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("GetConsumerInfoByField" , e.getMessage());
			return "";
		}
		
		return value;
	}
	
	 public void getOutputBillRecord(String strCANumber) {
         db = helper.getReadableDatabase();

         try {
                 String qry = String
                                 .format("select * from BillOutput where CANumber = '%s'",strCANumber);
                 Cursor c = db.rawQuery(qry, null);
                 //System.out.println("total Output Count : " + c.getCount());
                 UtilAppCommon.out = null;
                 if (c.moveToFirst()) {
                         do 
                         {
                             System.out.println(c.getString(0));
                             UtilAppCommon.out = new StructOutput();
                             UtilAppCommon.copyResultsetToOutputClass(c);
                         } while (c.moveToNext());
                 } else {
                         System.out.println("nullllllllllllllllllllllll");
                 }
         } catch (Exception e) {
                 // TODO: handle exception
        	 Log.e("getOutputBillRecord", e.getMessage());
               e.printStackTrace();
         }
 }
	
	
	public String getUnbilledRouteSequence(String strRouteSeqNo)
	{
		try {
			String value = "";
			String qry = "";
			if(strRouteSeqNo.equals(""))
				qry = String
			    //.format("Select * from BillInput where ROUTE_SEQUENCE_NO In (select CAST(Min(CAST(ROUTE_SEQUENCE_NO AS INT)) AS VARCHAR(10)) from BillInput  where (ROUTE_SEQUENCE_NO is not NULL and ROUTE_SEQUENCE_NO!='') and contract_ac_no not in(select canumber from sapinput))");
			.format("Select * from BillInput where ROUTE_SEQUENCE_NO In ( select CAST(Min(CAST(ROUTE_SEQUENCE_NO AS INT)) AS VARCHAR(10)) from BillInput where (ROUTE_SEQUENCE_NO is not null and ROUTE_SEQUENCE_NO !='') and contract_ac_no not in(select canumber from sapinput where SCHEDULED_BILLING_DATE IS NOT NULL))");
			
			
			else
				qry = String
					.format("Select * from BillInput where ROUTE_SEQUENCE_NO ='%s' and contract_ac_no not in(select canumber from sapinput where SCHEDULED_BILLING_DATE IS NOT NULL)", strRouteSeqNo);
			
			Cursor c = null;
			c = db.rawQuery(qry, null);
			
			if (c.moveToFirst()) {
				//c.moveToFirst();
				value = c.getString(c.getColumnIndex("ROUTE_SEQUENCE_NO"));
			}
			else
				value = null;
			
			UtilAppCommon.legNbr = c.getString(c.getColumnIndex("CONSUMER_LEGACY_ACC_NO"));
			UtilAppCommon.acctNbr = c.getString(c.getColumnIndex("CONTRACT_AC_NO"));
			UtilAppCommon.ConsumerName = c.getString(c.getColumnIndex("CONSUMER_NAME"));
			return value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("getUnbilledRouteSeqE", e.getMessage());
			return null;
		}
	}

	public Cursor getBilledRouteSequence(String strRouteSeqNo)
	{
		try {
			String value = "";
			String qry = "";
				qry = String.format("Select * from SAPInput where MSGID In ('0', '1', '2', '7', '8', '9', '10' )");
			
			Cursor c = null;
			c = db.rawQuery(qry, null);
			
			if (c.moveToFirst()) {
				return c;
			}
			
			
			return c;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("getBilledRouteSeqE", e.getMessage());
			return null;
		}
	}
	
	public Cursor getUnSyncedMobPol()
	{
		try {
			String value = "";
			String qry = "";
				qry = String.format("Select * from SAPInput where MobPoleFlag != 'Y' ");
			
			Cursor c = null;
			c = db.rawQuery(qry, null);
			
			if (c.moveToFirst()) {
				return c;
			}
			
			
			return c;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("getBilledRouteSeq", e.getMessage());
			return null;
		}
	}

	
	public String getMaxRouteSequence()
	{
		try {
			String value = "";
			String qry = "";
			qry = String.format("Select * from BillInput where ROUTE_SEQUENCE_NO In ( select CAST(MAX(CAST(ROUTE_SEQUENCE_NO AS INT)) AS VARCHAR(10)) from BillInput where contract_ac_no not in(select canumber from sapinput))");
			
			//qry = String.format("Select * from BillInput where ROUTE_SEQUENCE_NO In (select CAST(Min(CAST(ROUTE_SEQUENCE_NO AS INT)) AS VARCHAR(10)) from BillInput  where (ROUTE_SEQUENCE_NO is not NULL and ROUTE_SEQUENCE_NO!='') and contract_ac_no not in(select canumber from sapinput))");

			Cursor c = null;
			c = db.rawQuery(qry, null);
			
			if (c.getCount() > 0) {
				c.moveToFirst();
				value = c.getString(c.getColumnIndex("ROUTE_SEQUENCE_NO"));
			}

			return value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("getMaxRouteSequence E", e.getMessage());
			return null;
		}
	}
	
public ArrayList<HashMap<String, String>> getSummaryReport1() {
		db = helper.getReadableDatabase();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		
		String qry = String
		.format("SELECT x.category,x.total TotalCons,coalesce(x.billed,0)+coalesce(y.outsort,0) Billed , " 
				+" coalesce(x.unit,0) Units,coalesce(x.billAmt,0) BilledAmt,x.total-(coalesce(x.billed,0)+coalesce(y.outsort,0)) Pending FROM "
				+ "( select t2.rate_category as 'category',t2.total,t1.billed,t1.unit,t1.BillAmt from  "
		+ "(select Rate_Category , count(*) as total from BillInput group by Rate_Category ) as t2  "
		+ "left outer join "
		+ "(select Category, count(*) as billed,sum(BILLEDUNITS) as unit,sum(subtotal_b) as BillAmt from BillOutput group by Category) as t1 "
		+ "on  t1.category = t2.rate_category) X LEFT JOIN "
		+ "( select a.rate_category as 'category',count(*) outsort from billinput a,sapinput b "
		+ "where a.contract_ac_no=b.CAnumber and b.Msgid in ('3','4','5') )  Y "
        + "ON "
		+ " X.category=Y.category	");
			
		System.out.println("query---" + qry);
		Cursor c = db.rawQuery(qry, null);

		HashMap<String, String> map = null;
		if (c.moveToFirst()) {
			do {

				map = new HashMap<String, String>();
				map.put("Category", c.getString(0));
				map.put("TotalCons", c.getString(1));
				map.put("Billed", c.getString(2));
				map.put("Units", c.getString(3));
				map.put("BilledAmt", c.getString(4));
				map.put("Pending", c.getString(5));
				list.add(map);

			} while (c.moveToNext());
		}

		return list;
	}





	//Add this function
public ArrayList<HashMap<String, String>> getOutSortConsumerList(
				String bellowMonth) {
			db = helper.getReadableDatabase();
			int days = Integer.parseInt(bellowMonth);
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

			String qry = String
					.format("select a.MRU,a.contract_ac_no ACCNO,a.consumer_name,a.address, a.METER_MANUFACTURER_SR_NO from billinput a,sapinput b "+
			                "where a.contract_ac_no=b.CANumber and b.MSGID in('3','4','5')");
			System.out.println("query---" + qry);
			Cursor c = db.rawQuery(qry, null);

			HashMap<String, String> map = null;
			if (c.moveToFirst()) {
				do {

					map = new HashMap<String, String>();
					map.put("MRU", c.getString(0));
					map.put("ACC_NO", c.getString(1));
					map.put("NAME", c.getString(2));
					map.put("ADDR", c.getString(3));
					map.put("METER_MANUFACTURER_SR_NO", c.getString(4));
					list.add(map);

				} while (c.moveToNext());
			}

			return list;
		}
		
	// added  getAbnormalityCount function

	public int getAbnormalityCount(String accno) {
		db = helper.getReadableDatabase();
		String query = null;

		int intUserInfoRowCount = 0;
		int acno = Integer.parseInt(accno);
		query = String.format("select * from Abnormality where CANumber = '%s';", acno);
		System.out.println("query---" + query);
		Cursor c = db.rawQuery(query, null);
		intUserInfoRowCount = c.getCount();
		//intUserInfoRowCount=1;
		return intUserInfoRowCount;

	}
	
	@SuppressLint("NewApi")
	public Cursor getAbnormalityCons()
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			//String query1=String.format("SELECT * FROM SAPInput WHERE MSGID IN ('1', '2', '7')");
			String query1=String.format("SELECT * FROM Abnormality WHERE UploadFlag <> '2'");
			Cursor c = db.rawQuery(query1,null);
			if(c.moveToFirst())
			{
				return c;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("getAbnormalityCons E", e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	@SuppressLint("NewApi")
	public boolean MobileNumberCheck(String strMobileNo)
	{
		try {
			MobileNumberTrace();
			db = helper.getWritableDatabase();
			//String str = strMsg.
			//String query1=String.format("SELECT * FROM SAPInput WHERE MSGID IN ('1', '2', '7')");
			String query1=String.format("Select METER_CAP as Count from BillInput Where METER_CAP = '%s'", strMobileNo);
			Cursor c1 = db.rawQuery(query1,null);
			//Log.e("UtilDB","MobileNumberCheck ==>> c1.Count ==>>  " + query1 + "  ==  " + c1.getCount());
			if(c1.moveToFirst())
			{
				//Log.e("MobileNumberCheck", "C1 Count ==>> " +  c1.getCount());
				if(c1.getCount() > 3)
					return true;
			}

			String query2=String.format("Select MobileNo from SAPInput Where MobileNo = '%s'", strMobileNo);
			Cursor c2 = db.rawQuery(query2,null);
			//Log.e("UtilDB","MobileNumberCheck ==>> c2.Count ==>>  " + query2 + "  ==  " + c2.getCount());
			if(c2.moveToFirst())
			{
				
				if(c2.getCount() > 3)
					return true;
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("MobileNumberCheck E", e.getMessage());
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	
	@SuppressLint("NewApi")
	public boolean MobileNumberTrace()
	{
		try {
			db = helper.getWritableDatabase();
			//String str = strMsg.
			//String query1=String.format("SELECT * FROM SAPInput WHERE MSGID IN ('1', '2', '7')");
			String query1=String.format("Select METER_CAP as Count from BillInput");
			Cursor c1 = db.rawQuery(query1,null);
			Log.e("UtilDB","MobileNumberTrace ==>> c1.Count ==>>  " + query1 + "  ==  " + c1.getCount());
			if(c1.moveToFirst())
			{
				//Log.e("MobileNumberCheck", "C1 Count ==>> " +  c1.getCount());
				do
				{
					Log.e("UtilDB","MobileNumberTrace ==>> c1 ==>>  " + c1.getString(0));
				}
				while(c1.moveToNext());
			}

			String query2=String.format("Select MobileNo from SAPInput");
			Cursor c2 = db.rawQuery(query2,null);
			Log.e("UtilDB","MobileNumberCheck ==>> c2.Count ==>>  " + query2 + "  ==  " + c2.getCount());
			if(c2.moveToFirst())
			{
				do
				{
					Log.e("UtilDB","MobileNumberTrace ==>> c2 ==>>  " + c2.getString(0));
				}
				while(c2.moveToNext());
				
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e("MobileNumberTrace E", e.getMessage());
			e.printStackTrace();
			return true;
		}
		return false;
	}

    //**************************************Add /select device udpm**********************************************************
    public String checkUdpmIdAvailabilityAndInsert(String udpmid) {
        // int id = Integer.parseInt(udpmid);
        String recId = "";
        System.out.println("udpmid" + udpmid);
        String query = "select udpmdevrecordid from udpmdevices where udpmdevdeviceid =" + udpmid;
        System.out.println("checkUdpmIdAvailabilityAndInsert :" + query);
        Cursor cursor = db.rawQuery(query, null);
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


}