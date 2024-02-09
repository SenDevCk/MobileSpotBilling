package org.cso.MSBUtil;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;



import com.google.gson.Gson;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.cso.MSBModel.StructBillInfo;
import org.cso.MSBModel.StructInput;
import org.cso.MSBModel.StructOutput;
import org.cso.MSBModel.StructSAPBlueInput;
import org.cso.MSBModel.StructSAPInput;
import org.cso.MSBModel.StructUserInfo;
import org.cso.MSBModel.StructSlabInfo;
import org.cso.MobileSpotBilling.BuildConfig;
import org.cso.MobileSpotBilling.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class UtilAppCommon {
	// public static Session mySessionObject = null;
	//Created by Kishore
	public static String userInfo=null ;
	public static String actBinder=null;
	public static int intAppInvoked=0;
	public static String strRedirectTo = "";
	//End
	public static String sdoCode = null;
	public static String binder = null;
	public static String acctNbr = null;
	public static String legNbr = null;
	public static String meterNbr = null;
	public static String strBulkDataResponse = null;
	public static Boolean bBtnGenerateClicked = false;
	public static Boolean bprintdupl = false;
	public static String billType = null;
	public static String routeSeqNo = null;
	
	public static String strLat = "";
	public static String strLong = "";
	// ...
	public static String edtMeterNbr = null;
	public static String edtOldAccTNbr = null;
	public static String edtConsumerName = null;
	public static String ConsumerName = null;
	public static String IMEI_Number = null;
	public static Boolean inSAPInputTab = false;
	
	public static String strHostName = "";

	public static String inSAPMsgID = "";
	public static String inActualSAPMsgID = "";
	public static String inSAPMsg = "";
	public static String inSAPSendMsg = "";
	public static boolean ValidDevice = false;
	public static boolean ValidVersion = false;

	public static String strAppVersion = ""; 
	public static String strActivityRedirectTo = "";
	public static boolean blActyncBtn = false;
	public static String strServerDtTm = "";
	public static String strImageCount = "";

	public static boolean blImageCapture = false;
	public static boolean blAbnormalityCheck = false;
	// ...
	public static int intIsLoggedIn = 0;
	public static int gIntAvailableInputDataCount = 0;

	public static StructInput in;
	public static StructOutput out;
	public static StructUserInfo ui;
	public static StructBillInfo bill;
	public static StructSlabInfo slab;
	public static StructSAPInput SAPIn;
	public static StructSAPBlueInput SAPBlueIn;

	public static final String MESSENGER_INTENT_KEY =
			BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";

	public static final String WORK_DURATION_KEY =
			BuildConfig.APPLICATION_ID + ".WORK_DURATION_KEY";
	
	public static void AssignSlabs(StructSlabInfo slabobj)
	{
		slab=slabobj;
		//System.out.println(new Gson().toJson(slab));
		
	}
	
	public static void copySAPInputData(String[] strSAPInputData)
	{
		Log.e("copySAPInputData", "Started");
		try {
			Log.e("CANumber", strSAPInputData[0]);
			SAPIn.CANumber = strSAPInputData[0];
			SAPIn.Installation = strSAPInputData[1];
			SAPIn.SCHEDULED_BILLING_DATE = strSAPInputData[9];
			SAPIn.Latitude = strSAPInputData[2];
			SAPIn.Longitude = strSAPInputData[3];
			SAPIn.CurrentReadingKwh = strSAPInputData[4];
			SAPIn.MaxDemd = strSAPInputData[5];
			SAPIn.PowerFactor = strSAPInputData[6];
			SAPIn.MtrReadingNote = strSAPInputData[7];
			SAPIn.MtrReadingDate = strSAPInputData[8];
			SAPIn.SAP_DEVICE_NO = strSAPInputData[10];
			SAPIn.CurrentReadingKVAh = strSAPInputData[13];
			SAPIn.ProcessedFlag = "0";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("copySAPInputData E", e.getLocalizedMessage());
		}
		Log.e("copySAPInputData", "Completed");
	}

	public static void copySAPInputData1(Cursor rsInput)
	{
		Log.e("copySAPInputData", "Started");
		try {
			SAPIn.CANumber=(rsInput.getString(0) != null) ? rsInput.getString(0).trim() : "";
			SAPIn.Installation=(rsInput.getString(1) != null) ? rsInput.getString(1).trim() : "";
			SAPIn.SCHEDULED_BILLING_DATE=(rsInput.getString(2) != null) ? rsInput.getString(2).trim() : "";
			SAPIn.MtrReadingDate=(rsInput.getString(3) != null) ? rsInput.getString(3).trim() : "";
			SAPIn.MtrReadingNote=(rsInput.getString(4) != null) ? rsInput.getString(4).trim() : "";
			SAPIn.CurrentReadingKwh=(rsInput.getString(5) != null) ? rsInput.getString(5).trim() : "";
			SAPIn.CurrentReadingKVAh=(rsInput.getString(6) != null) ? rsInput.getString(6).trim() : "";
			SAPIn.MaxDemd=(rsInput.getString(7) != null) ? rsInput.getString(7).trim() : "";
			SAPIn.PowerFactor=(rsInput.getString(8) != null) ? rsInput.getString(8).trim() : "";
			SAPIn.Latitude=(rsInput.getString(9) != null) ? rsInput.getString(9).trim() : "";
			SAPIn.Longitude=(rsInput.getString(10) != null) ? rsInput.getString(10).trim() : "";
			SAPIn.ProcessedFlag=(rsInput.getString(11) != null) ? rsInput.getString(11).trim() : "";
			SAPIn.SAP_DEVICE_NO=(rsInput.getString(12) != null) ? rsInput.getString(12).trim() : "";
			SAPIn.strMsg=(rsInput.getString(16) != null) ? rsInput.getString(16).trim() : "";
			SAPIn.MsgId=(rsInput.getString(17) != null) ? rsInput.getString(17).trim() : "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("copySAPInputData E", e.getLocalizedMessage());
		}
		Log.e("copySAPInputData", "Completed");
	}

	public static void copySAPBlueInputData(Cursor rsInput)
	{
		Log.e("copySAPInputData", "Started");
		try {
			SAPBlueIn.CANumber=(rsInput.getString(0) != null) ? rsInput.getString(0).trim() : "";
			SAPBlueIn.Installation=(rsInput.getString(1) != null) ? rsInput.getString(1).trim() : "";
			SAPBlueIn.SCHEDULED_BILLING_DATE=(rsInput.getString(2) != null) ? rsInput.getString(2).trim() : "";
			SAPBlueIn.MtrReadingDate=(rsInput.getString(3) != null) ? rsInput.getString(3).trim() : "";
			SAPBlueIn.MtrReadingNote=(rsInput.getString(4) != null) ? rsInput.getString(4).trim() : "";
			SAPBlueIn.CurrentReadingKwh=(rsInput.getString(5) != null) ? rsInput.getString(5).trim() : "";
			SAPBlueIn.CurrentReadingKVAh=(rsInput.getString(6) != null) ? rsInput.getString(6).trim() : "";
			SAPBlueIn.MaxDemd=(rsInput.getString(7) != null) ? rsInput.getString(7).trim() : "";
			SAPBlueIn.PowerFactor=(rsInput.getString(8) != null) ? rsInput.getString(8).trim() : "";
			SAPBlueIn.Latitude=(rsInput.getString(9) != null) ? rsInput.getString(9).trim() : "";
			SAPBlueIn.Longitude=(rsInput.getString(10) != null) ? rsInput.getString(10).trim() : "";
			SAPBlueIn.ProcessedFlag=(rsInput.getString(11) != null) ? rsInput.getString(11).trim() : "";
			SAPBlueIn.SAP_DEVICE_NO=(rsInput.getString(12) != null) ? rsInput.getString(12).trim() : "";
			SAPBlueIn.strMsg=(rsInput.getString(16) != null) ? rsInput.getString(16).trim() : "";
			SAPBlueIn.MsgId=(rsInput.getString(17) != null) ? rsInput.getString(17).trim() : "";
			Log.i("copySAPInputData C", SAPBlueIn.CANumber + " >> " + SAPBlueIn.Installation + " >> " + SAPBlueIn.SCHEDULED_BILLING_DATE + " >> " + SAPBlueIn.MtrReadingDate + " >> " + SAPBlueIn.MtrReadingNote + " >> " +
					SAPBlueIn.CurrentReadingKwh + " >> " + SAPBlueIn.CurrentReadingKVAh + " >> " + SAPBlueIn.MaxDemd + " >> " + SAPBlueIn.PowerFactor + " >> " + SAPBlueIn.Latitude + " >> " + SAPBlueIn.Longitude + " >> " +
					" >> " + SAPBlueIn.ProcessedFlag + " >> " + SAPBlueIn.SAP_DEVICE_NO);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("copySAPInputData E", e.getLocalizedMessage());
		}
		Log.e("copySAPInputData", "Completed");
	}
	
	public static void copyResultsetToInputClass(Cursor rsInput)
			throws SQLException {
		in.DIVISION_CODE   =  (rsInput.getString(0) != null) ? rsInput.getString(0).trim() : "";
		in.DIVISION_NAME   =  (rsInput.getString(1) != null) ? rsInput.getString(1).trim() : "";
		in.SUB_DIVISION_CODE   =  (rsInput.getString(2) != null) ? rsInput.getString(2).trim() : "";
		in.SUB_DIVISION_NAME   =  (rsInput.getString(3) != null) ? rsInput.getString(3).trim() : "";
		in.ROUTE_SEQUENCE_NO   =  (rsInput.getString(4) != null) ? rsInput.getString(4).trim() : "";
		in.MRU   =  (rsInput.getString(5) != null) ? rsInput.getString(5).trim() : "";
		in.AREA_TYPE   =  (rsInput.getString(6) != null) ? rsInput.getString(6).trim() : "";
		in.CONNECTED_POLE_NIN_NUMBER   =  (rsInput.getString(7) != null) ? rsInput.getString(7).trim() : "";
		in.CONSUMER_LEGACY_ACC_NO   =  (rsInput.getString(8) != null) ? rsInput.getString(8).trim() : "";
		in.CONTRACT_AC_NO   =  (rsInput.getString(9) != null) ? rsInput.getString(9).trim() : "";
		in.CONTRACT_NO   =  (rsInput.getString(10) != null) ? rsInput.getString(10).trim() : "";
		in.CONSUMER_NAME   =  (rsInput.getString(11) != null) ? rsInput.getString(11).trim() : "";
		in.ADDRESS   =  (rsInput.getString(12) != null) ? rsInput.getString(12).trim() : "";
		in.RATE_CATEGORY   =  (rsInput.getString(13) != null) ? rsInput.getString(13).trim() : "";
		in.BILLING_CYCLE   =  (rsInput.getString(14) != null) ? rsInput.getString(14).trim() : "";
		in.FEEDER_TYPE   =  (rsInput.getString(15) != null) ? rsInput.getString(15).trim() : "";
		in.FEEDER_NAME   =  (rsInput.getString(16) != null) ? rsInput.getString(16).trim() : "";
		in.SHUNT_CAPACITY_REQUIRED_FLAG   =  (rsInput.getString(17) != null) ? rsInput.getString(17).trim() : "";
		in.SHUNT_CAPACITOR_INSTALLED_FLAG   =  (rsInput.getString(18) != null) ? rsInput.getString(18).trim() : "";
		in.METER_MANUFACTURER_SR_NO   =  (rsInput.getString(19) != null) ? rsInput.getString(19).trim() : "";
		in.SCHEDULED_BILLING_DATE   =  (rsInput.getString(20) != null) ? rsInput.getString(20).trim() : "";
		in.SAP_DEVICE_NO   =  (rsInput.getString(21) != null) ? rsInput.getString(21).trim() : "";
		if(in.SAP_DEVICE_NO.trim().equalsIgnoreCase(""))
			in.SAP_DEVICE_NO = "";
		in.METER_RENT   =  (rsInput.getString(22) != null) ? rsInput.getString(22).trim() : "";
		in.PRE_DECIMAL_MTR   =  (rsInput.getString(23) != null) ? rsInput.getString(23).trim() : "";
		in.OVERALL_MF   =  (rsInput.getString(24) != null) ? rsInput.getString(24).trim() : "";
		in.NXT_SCH_MTR_RDR_DATE   =  (rsInput.getString(25) != null) ? rsInput.getString(25).trim() : "";
		in.MTR_CHANGE_FLAG   =  (rsInput.getString(26) != null) ? rsInput.getString(26).trim() : "";
		in.OLD_MTR_CONSUMPTION   =  (rsInput.getString(27) != null) ? rsInput.getString(27).trim() : "";
		in.OLD_MTR_DEMAND   =  (rsInput.getString(28) != null) ? rsInput.getString(28).trim() : "";
		in.NEW_MTR_INTIAL_READING   =  (rsInput.getString(29) != null) ? rsInput.getString(29).trim() : "";
		in.MTR_CHANGE_DATE   =  (rsInput.getString(30) != null) ? rsInput.getString(30).trim() : "";
		in.NEW_MTR_RENT   =  (rsInput.getString(31) != null) ? rsInput.getString(31).trim() : "";
		in.CAT_CHANGE_FLAG   =  (rsInput.getString(32) != null) ? rsInput.getString(32).trim() : "";
		in.CAT_CHANGE_DATE   =  (rsInput.getString(33) != null) ? rsInput.getString(33).trim() : "";
		in.OLD_CATEGORY   =  (rsInput.getString(34) != null) ? rsInput.getString(34).trim() : "";
		in.SANC_LOAD_CHANGE_FLAG   =  (rsInput.getString(35) != null) ? rsInput.getString(35).trim() : "";
		in.SANC_LOAD_CHG_DT   =  (rsInput.getString(36) != null) ? rsInput.getString(36).trim() : "";
		in.OLD_SANC_LOAD   =  (rsInput.getString(37) != null) ? rsInput.getString(37).trim() : "";
		in.PHASE_CODE   =  (rsInput.getString(38) != null) ? rsInput.getString(38).trim() : "";
		in.SANC_LOAD   =  (rsInput.getString(39) != null) ? rsInput.getString(39).trim() : "";
		in.CONNECTED_LOAD   =  (rsInput.getString(40) != null) ? rsInput.getString(40).trim() : "";
		in.CONTRACT_DEMAND   =  (rsInput.getString(41) != null) ? rsInput.getString(41).trim() : "";
		in.MMC_UNIT_OLD_RC   =  (rsInput.getString(42) != null) ? rsInput.getString(42).trim() : "";
		in.IIP_DISCOUNT_FLAG   =  (rsInput.getString(43) != null) ? rsInput.getString(43).trim() : "";
		in.SCH_MTR_READING_DT   =  (rsInput.getString(44) != null) ? rsInput.getString(44).trim() : "";
		in.PRV_MTR_READING_DT   =  (rsInput.getString(45) != null) ? rsInput.getString(45).trim() : "";
		in.PRV_BILL_DATE   =  (rsInput.getString(46) != null) ? rsInput.getString(46).trim() : "";
		in.PRV_READING_KWH   =  (rsInput.getString(47) != null) ? rsInput.getString(47).trim() : "";
		if(in.PRV_READING_KWH.trim().equalsIgnoreCase(""))
			in.PRV_READING_KWH = "0";
		in.PRV_MTR_READING_NOTE   =  (rsInput.getString(48) != null) ? rsInput.getString(48).trim() : "";
		if(in.PRV_MTR_READING_NOTE.trim().equalsIgnoreCase(""))
			in.PRV_MTR_READING_NOTE = "MD";
		in.ED_EXEMPTED_FLAG   =  (rsInput.getString(49) != null) ? rsInput.getString(49).trim() : "";
		in.ARREAR_ENRGY_CHG   =  (rsInput.getString(50) != null) ? rsInput.getString(50).trim() : "";
		in.PAY_ACC   =  (rsInput.getString(51) != null) ? rsInput.getString(51).trim() : "";
		in.ARR_DPS   =  (rsInput.getString(52) != null) ? rsInput.getString(52).trim() : "";
		in.ED_ARRS   =  (rsInput.getString(53) != null) ? rsInput.getString(53).trim() : "";
		in.ARRS_OTHR   =  (rsInput.getString(54) != null) ? rsInput.getString(54).trim() : "";
		in.AMT_KPT_ABEY   =  (rsInput.getString(55) != null) ? rsInput.getString(55).trim() : "";
		in.PUNITIVE_BILL   =  (rsInput.getString(56) != null) ? rsInput.getString(56).trim() : "";
		in.DPS_PUNITIVE_BILL   =  (rsInput.getString(57) != null) ? rsInput.getString(57).trim() : "";
		in.MTR_READING_REASON   =  (rsInput.getString(58) != null) ? rsInput.getString(58).trim() : "";
		in.DPS_AMT_CURR_MON   =  (rsInput.getString(59) != null) ? rsInput.getString(59).trim() : "";
		in.INTEREST_ON_SD   =  (rsInput.getString(60) != null) ? rsInput.getString(60).trim() : "";
		in.INCENTIVE   =  (rsInput.getString(61) != null) ? rsInput.getString(61).trim() : "";
		in.SECURITY_DEPOSIT   =  (rsInput.getString(62) != null) ? rsInput.getString(62).trim() : "";
		in.SD_REQ   =  (rsInput.getString(63) != null) ? rsInput.getString(63).trim() : "";
		in.PROV_BILL_ADJ_AMT   =  (rsInput.getString(64) != null) ? rsInput.getString(64).trim() : "";
		in.CURR_INST_GNRL   =  (rsInput.getString(65) != null) ? rsInput.getString(65).trim() : "";
		in.CURR_MON_AMT   =  (rsInput.getString(66) != null) ? rsInput.getString(66).trim() : "";
		in.TOT_INST_AMT   =  (rsInput.getString(67) != null) ? rsInput.getString(67).trim() : "";
		in.LAST_PAY_AMT_MADE   =  (rsInput.getString(68) != null) ? rsInput.getString(68).trim() : "";
		in.LAST_PAY_DATE   =  (rsInput.getString(69) != null) ? rsInput.getString(69).trim() : "";
		in.LAST_RECEIPT_NO   =  (rsInput.getString(70) != null) ? rsInput.getString(70).trim() : "";
		in.PREV_KWH_CYCLE1   =  (rsInput.getString(71) != null) ? rsInput.getString(71).trim() : "";
		in.PREV_KWH_CYCLE2   =  (rsInput.getString(72) != null) ? rsInput.getString(72).trim() : "";
		in.PREV_KWH_CYCLE3   =  (rsInput.getString(73) != null) ? rsInput.getString(73).trim() : "";
		in.AVG_KWH_3_CYCLE_DR_KWH   =  (rsInput.getString(74) != null) ? rsInput.getString(74).trim() : "";
		in.AVG_KWH_12_CYCLE_KWH   =  (rsInput.getString(75) != null) ? rsInput.getString(75).trim() : "";
		in.AVG_KWH_3_CYCLE_DEMAND   =  (rsInput.getString(76) != null) ? rsInput.getString(76).trim() : "";
		in.AVG_KWH_12_CYCLE_DEMAND   =  (rsInput.getString(77) != null) ? rsInput.getString(77).trim() : "";
		in.MSG_1   =  (rsInput.getString(78) != null) ? rsInput.getString(78).trim() : "";
		in.MSG_2   =  (rsInput.getString(79) != null) ? rsInput.getString(79).trim() : "";
		in.CHQ_DISH_FLAG   =  (rsInput.getString(80) != null) ? rsInput.getString(80).trim() : "";
		in.DEMAND_FLAG   =  (rsInput.getString(81) != null) ? rsInput.getString(81).trim() : "";
		in.COM_CODE   =  (rsInput.getString(82) != null) ? rsInput.getString(82).trim() : "";
		in.NO_OF_DAYS_PREV_BILL   =  (rsInput.getString(83) != null) ? rsInput.getString(83).trim() : "";
		in.TEMP_FLAG   =  (rsInput.getString(84) != null) ? rsInput.getString(84).trim() : "";
		in.SEASON_FLAG   =  (rsInput.getString(85) != null) ? rsInput.getString(85).trim() : "";
		in.MONTH_SEASONAL   =  (rsInput.getString(86) != null) ? rsInput.getString(86).trim() : "";
		in.SS_FLAG   =  (rsInput.getString(87) != null) ? rsInput.getString(87).trim() : "";
		in.IIP_DTY_EXEM_FLAG   =  (rsInput.getString(88) != null) ? rsInput.getString(88).trim() : "";
		in.PWR_FACTOR   =  (rsInput.getString(89) != null) ? rsInput.getString(89).trim() : "";
		in.IAS_FLAG   =  (rsInput.getString(90) != null) ? rsInput.getString(90).trim() : "";
		in.INSTALLATION   =  (rsInput.getString(91) != null) ? rsInput.getString(91).trim() : "";
		in.METER_MAKE   =  (rsInput.getString(92) != null) ? rsInput.getString(92).trim() : "";
		in.METER_COM_CONS   =  (rsInput.getString(93) != null) ? rsInput.getString(93).trim() : "";
		in.DT_CODE   =  (rsInput.getString(94) != null) ? rsInput.getString(94).trim() : "";
		in.METER_CAP   =  (rsInput.getString(95) != null) ? rsInput.getString(95).trim() : "";
		in.LAST_ACT_READ   =  (rsInput.getString(96) != null) ? rsInput.getString(96).trim() : "";
		in.LAST_ACT_READ_DT   =  (rsInput.getString(97) != null) ? rsInput.getString(97).trim() : "";
		in.PREV_MON_BILL_AMT   =  (rsInput.getString(98) != null) ? rsInput.getString(98).trim() : "";
		in.FLAG_FOR_BILL_ADJUSTMENT   =  (rsInput.getString(99) != null) ? rsInput.getString(99).trim() : "";
		in.PL_PERIOD_ENERGY_AMT   =  (rsInput.getString(100) != null) ? rsInput.getString(100).trim() : "";
		in.PL_PERIOD_GOVT_DUTY_AMT   =  (rsInput.getString(101) != null) ? rsInput.getString(101).trim() : "";
		in.PL_PERIOD_REBATE_AMT   =  (rsInput.getString(102) != null) ? rsInput.getString(102).trim() : "";
		in.IIP_CHARGES   =  (rsInput.getString(103) != null) ? rsInput.getString(103).trim() : "";
		in.SHUNT_CAP_CHARGES   =  (rsInput.getString(104) != null) ? rsInput.getString(104).trim() : "";
		in.AMT_1   =  (rsInput.getString(105) != null) ? rsInput.getString(105).trim() : "";
		in.AMT_2   =  (rsInput.getString(106) != null) ? rsInput.getString(106).trim() : "";
		in.AMT_3   =  (rsInput.getString(107) != null) ? rsInput.getString(107).trim() : "";
		in.DATE_1   =  (rsInput.getString(108) != null) ? rsInput.getString(108).trim() : "";
		in.DATE_2   =  (rsInput.getString(109) != null) ? rsInput.getString(109).trim() : "";
		in.DATE_3   =  (rsInput.getString(110) != null) ? rsInput.getString(110).trim() : "";
		in.FLAG_1   =  (rsInput.getString(111) != null) ? rsInput.getString(111).trim() : "";
		in.FLAG_2   =  (rsInput.getString(112) != null) ? rsInput.getString(112).trim() : "";


	}

	
	public static void copyResultsetToOutputClass(Cursor rsInput)
			throws SQLException {
		Log.e("copyResultsetToOutClass", "Start");
		try {
			out.Company  = (rsInput.getString(0) != null) ? rsInput.getString(0).trim() : ""; 
			out.BillMonth  = (rsInput.getString(1) != null) ? rsInput.getString(1).trim() : ""; 
			out.BillNo  = (rsInput.getString(2) != null) ? rsInput.getString(2).trim() : ""; 
			out.Division  = (rsInput.getString(3) != null) ? rsInput.getString(3).trim() : ""; 
			out.SubDivision  = (rsInput.getString(4) != null) ? rsInput.getString(4).trim() : ""; 
			out.CANumber  = (rsInput.getString(5) != null) ? rsInput.getString(5).trim() : ""; 
			out.LegacyNumber  = (rsInput.getString(6) != null) ? rsInput.getString(6).trim() : ""; 
			out.MRU  = (rsInput.getString(7) != null) ? rsInput.getString(7).trim() : ""; 
			out.MtrMake  = (rsInput.getString(8) != null) ? rsInput.getString(8).trim() : ""; 
			out.Name  = (rsInput.getString(9) != null) ? rsInput.getString(9).trim() : ""; 
			out.Address  = (rsInput.getString(10) != null) ? rsInput.getString(10).trim() : ""; 
			out.PoleNo  = (rsInput.getString(11) != null) ? rsInput.getString(11).trim() : ""; 
			out.MtrNo  = (rsInput.getString(12) != null) ? rsInput.getString(12).trim() : ""; 
			out.Phase  = (rsInput.getString(13) != null) ? rsInput.getString(13).trim() : ""; 
			out.Category  = (rsInput.getString(14) != null) ? rsInput.getString(14).trim() : ""; 
			out.SanctLoad  = (rsInput.getString(15) != null) ? rsInput.getString(15).trim() : ""; 
			out.ConnectedLoad  = (rsInput.getString(16) != null) ? rsInput.getString(16).trim() : ""; 
			out.CD  = (rsInput.getString(17) != null) ? rsInput.getString(17).trim() : ""; 
			out.SD  = (rsInput.getString(18) != null) ? rsInput.getString(18).trim() : ""; 
			//if(rsInput.getString(63) != null)
				//out.BillDays  = (rsInput.getString(19) != null) ? rsInput.getString(19).trim() + "(" + rsInput.getString(63).trim() + ")" : ""; 
			//else
				out.BillDays  = (rsInput.getString(19) != null) ? rsInput.getString(19).trim() : ""; 
			out.PreviusReading  = (rsInput.getString(20) != null) ? rsInput.getString(20).trim() : ""; 
			out.PrevusMtrRdgDt  = (rsInput.getString(21) != null) ? rsInput.getString(21).trim() : ""; 
			out.PreviusMtrReadingNote  = (rsInput.getString(22) != null) ? rsInput.getString(22).trim() : ""; 
			out.CurrentReading  = (rsInput.getString(23) != null) ? rsInput.getString(23).trim() : ""; 
			out.CurrentMtrRdgDt  = (rsInput.getString(24) != null) ? rsInput.getString(24).trim() : ""; 
			out.CurrentMtrReadingNote  = (rsInput.getString(25) != null) ? rsInput.getString(25).trim() : ""; 
			out.MF  = (rsInput.getString(26) != null) ? rsInput.getString(26).trim() : ""; 
			out.Consumption  = (rsInput.getString(27) != null) ? rsInput.getString(27).trim() : ""; 
			out.RecordedDemd  = (rsInput.getString(28) != null) ? rsInput.getString(28).trim() : ""; 
			out.PowerFactor  = (rsInput.getString(29) != null) ? rsInput.getString(29).trim() : ""; 
			out.MMCUnits  = (rsInput.getString(30) != null) ? rsInput.getString(30).trim() : ""; 
			out.BilledUnits  = (rsInput.getString(31) != null) ? rsInput.getString(31).trim() : ""; 
			out.Average  = (rsInput.getString(32) != null) ? rsInput.getString(32).trim() : ""; 
			out.Type  = (rsInput.getString(33) != null) ? rsInput.getString(33).trim() : ""; 
			out.PaymentOnAccount  = (rsInput.getString(34) != null) ? rsInput.getString(34).trim() : ""; 
			out.ArrearEnergyDues  = (rsInput.getString(35) != null) ? rsInput.getString(35).trim() : ""; 
			out.ArrearDPs  = (rsInput.getString(36) != null) ? rsInput.getString(36).trim() : ""; 
			out.ArrearOthers  = (rsInput.getString(37) != null) ? rsInput.getString(37).trim() : ""; 
			out.ArrearSubTotal_A  = (rsInput.getString(38) != null) ? rsInput.getString(38).trim() : ""; 
			out.CurrentEnergyCharges  = (rsInput.getString(39) != null) ? rsInput.getString(39).trim() : ""; 
			out.CurrentMonthDps  = (rsInput.getString(40) != null) ? rsInput.getString(40).trim() : ""; 
			out.FixDemdCharge  = (rsInput.getString(41) != null) ? rsInput.getString(41).trim() : ""; 
			out.ExcessDemdCharge  = (rsInput.getString(42) != null) ? rsInput.getString(42).trim() : ""; 
			out.ElectricityDuty  = (rsInput.getString(43) != null) ? rsInput.getString(43).trim() : ""; 
			out.MeterRent  = (rsInput.getString(44) != null) ? rsInput.getString(44).trim() : ""; 
			out.ShauntCapCharge  = (rsInput.getString(45) != null) ? rsInput.getString(45).trim() : ""; 
			out.OtherCharge  = (rsInput.getString(46) != null) ? rsInput.getString(46).trim() : ""; 
			out.Installment  = (rsInput.getString(47) != null) ? rsInput.getString(47).trim() : ""; 
			out.SubTotal_B  = (rsInput.getString(48) != null) ? rsInput.getString(48).trim() : ""; 
			out.InterestOnSD_C  = (rsInput.getString(49) != null) ? rsInput.getString(49).trim() : ""; 
			out.Incentive  = (rsInput.getString(50) != null) ? rsInput.getString(50).trim() : ""; 
			out.RebateOnMMC  = (rsInput.getString(51) != null) ? rsInput.getString(51).trim() : ""; 
			out.GrossTotal  = (rsInput.getString(52) != null) ? rsInput.getString(52).trim() : ""; 
			out.Rebate   = (rsInput.getString(53) != null) ? rsInput.getString(53).trim() : ""; 
			out.AmtPayableUptoDt  = (rsInput.getString(54) != null) ? rsInput.getString(54).trim() : ""; 
			out.AmtPayableUptoAmt  = (rsInput.getString(55) != null) ? rsInput.getString(55).trim() : ""; 
			out.AmtPayablePYDt  = (rsInput.getString(56) != null) ? rsInput.getString(56).trim() : ""; 
			out.AmtPayablePYAmt  = (rsInput.getString(57) != null) ? rsInput.getString(57).trim() : ""; 
			out.AmtPayableAfterDt  = (rsInput.getString(58) != null) ? rsInput.getString(58).trim() : ""; 
			out.AmtPayableAfterAmt  = (rsInput.getString(59) != null) ? rsInput.getString(59).trim() : ""; 
			out.LastPaymentAmt  = (rsInput.getString(60) != null) ? rsInput.getString(60).trim() : ""; 
			out.LastPaidDate  = (rsInput.getString(61) != null) ? rsInput.getString(61).trim() : ""; 
			out.ReceiptNumber  = (rsInput.getString(62) != null) ? rsInput.getString(62).trim() : "";
			out.MESSAGE10 = (rsInput.getString(63) != null) ? rsInput.getString(63).trim() : "";
			out.MTR_READER_ID = (rsInput.getString(64) != null) ? rsInput.getString(64).trim() : "";
			out.Area_type = (rsInput.getString(65) != null) ? rsInput.getString(65).trim() : "";
			out.MobileNo = (rsInput.getString(66) != null) ? rsInput.getString(66).trim() : "";
			out.REC_DATE_TIME = (rsInput.getString(67) != null) ? rsInput.getString(67).trim() : "";
			out.GOVT_SUB = (rsInput.getString(68) != null) ? rsInput.getString(68).trim() : "";
			out.INT_DISC = (rsInput.getString(69) != null) ? rsInput.getString(69).trim() : "";
			/**
			 * Adding lines for tariff change 2018-19
			 */

			out.METER_CGST =(rsInput.getString(70) != null) ? rsInput.getString(70).trim() : "";
			out.METER_SGST =(rsInput.getString(71) != null) ? rsInput.getString(71).trim() : "";
				/**
 				* End ading lines for tariff change
 				*/

			Log.e("ReceiptNumber ==>> ", UtilAppCommon.out.ReceiptNumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("copyResultsetToOutClass", e.getMessage());
		}
		Log.e("copyResultsetToOutClass", "Completed");
	}


	public static boolean checkiInternet(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
			return true;
		} else {
			System.out.println("Internet Connection Not Present");
			return false;
		}
	}

	public static String getDate(String requestFormat) {
		Calendar currDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(requestFormat);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
		String dateNow = formatter.format(currDate.getTime());
		return dateNow;
	}

	public static void createDuplicateBill(Cursor c) {
		Document document = new Document();
		String dirpath = Environment.getExternalStorageDirectory().getPath()
				+ "/SBDocs/PDF";
		File dir = new File(dirpath);
		if (!dir.exists()) {
			dir.mkdirs();
			System.out.println("folder created");
		}
		String filePath = dir + "/" + UtilAppCommon.out.CANumber + ".pdf";
		File file = new File(filePath);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();
			createDuplicateBillPDF(document, c);
			document.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (document != null) {
				document.close();
			}
		}
	}

	private static void createDuplicateBillPDF(Document document, Cursor c)
			throws DocumentException {
		// TODO Auto-generated method stub
		Paragraph p = new Paragraph("ELECTRICITY BILL");
		p.setAlignment("CENTER");
		document.add(p);
		document.add(Chunk.NEWLINE);

		PdfPTable table = new PdfPTable(4);
		table.addCell("Division");
		table.addCell(c.getString(1));
		table.addCell("Sub Div");
		table.addCell(UtilAppCommon.in.SUB_DIVISION_NAME);

		//table.addCell("Section");
		//table.addCell(UtilAppCommon.in.SECTION_NM);
		table.addCell("New A/C No");
		table.addCell(c.getString(1) + c.getString(2) + c.getString(3));

		table.addCell("Old A/C No ");
		table.addCell(c.getString(1));
		table.addCell("Consumer No");
		table.addCell(c.getString(0));

		table.addCell("Elect Addr");
		table.addCell(UtilAppCommon.in.ADDRESS);
		table.addCell("Bill Period");
		table.addCell(UtilAppCommon.in.ADDRESS);

		/*
		table.addCell("No Of Months:");
		table.addCell(UtilAppCommon.out.NOOFMONTHS + "");
		table.addCell("Date");
		table.addCell(UtilAppCommon.out.ISSUE_DT + "," + UtilAppCommon.out.TIMEOFBILLING);
		*/
		table.addCell("Name");
		table.addCell(UtilAppCommon.in.CONSUMER_NAME);
		table.addCell("ADDR1");
		table.addCell(UtilAppCommon.in.ADDRESS);

		//table.addCell("Addr2");
		//table.addCell(UtilAppCommon.in.ADDR2);

		//table.addCell("ADDR3");
		//table.addCell(UtilAppCommon.in.ADDR3);

		table.addCell("Security Deposit");
		table.addCell(UtilAppCommon.in.SECURITY_DEPOSIT + "");
		table.addCell("Consumer Status");
		//table.addCell(UtilAppCommon.in. + "");
		//table.addCell((UtilAppCommon.out.CSTS_CD.compareTo("R") == 0) ? "R(Regular)\n"
		//	: "D(Discnted)\n");
	}

	public static void copyResultsetToUserInfoClass(Cursor curUserInfo)
			throws SQLException {
		ui.METER_READER_ID = (curUserInfo.getString(0) != null) ? curUserInfo.getString(0).trim() : "";
		ui.METERREADER_ID = (curUserInfo.getString(1) != null) ? curUserInfo.getString(1).trim() : "";
		ui.IMIE_NO = (curUserInfo.getString(2) != null) ? curUserInfo.getString(2).trim() : "";
		ui.PASSWORD = (curUserInfo.getString(3) != null) ? curUserInfo.getString(3).trim() : "";
		ui.printerid = (curUserInfo.getString(4) != null) ? curUserInfo.getString(4).trim() : "";
		ui.printertype = (curUserInfo.getString(5) != null) ? curUserInfo.getString(5).trim() : "";
	}
	
	public static void TestcopyResultsetToUserInfoClass()
			throws SQLException {
		
		
		ui.METER_READER_ID = "SBPDCL";
		ui.METERREADER_ID = "MTRID001";
		ui.IMIE_NO = "";
		ui.PASSWORD = "12345678";
		ui.printerid = "PTR00001";
		ui.printertype = "Zebra Thermal";
		
	}
	
	
	
}
