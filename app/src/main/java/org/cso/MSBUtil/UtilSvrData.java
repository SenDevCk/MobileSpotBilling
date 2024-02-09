package org.cso.MSBUtil;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.HttpTransportSE;
import org.cso.MSBUtil.UtilAppCommon;

import android.annotation.SuppressLint;
import android.util.Log;

public class UtilSvrData {
	
	private String SOAPAction;
	private String SOAPMethod ;
	private String SOAPNamespace;
	private String SOAPAddress;
	
	//private String DevHostName = "http://220.225.3.133/";
	private String DevHostName = "http://112.133.239.225/";   //Dev New
	//private String ProHostName = "http://220.225.3.132/";		//DC IP Address Old
	private String ProHostName = /*"http://220.225.3.149/"*/"https://www.bihardiscom.co.in/";		//DC IP Address New
	//private String ProHostName = "http://220.227.230.133/";		//DR IP Address
	
	//Old Airtel Down IP
	//private String strDevAlternateHost = "http://125.16.220.2/";
	//private String strProAlternateHost = "http://125.16.220.2/"; 125.16.220.4
	
	//New Airtel IP
	private String strDevAlternateHost = "http://125.16.220.4/";
	private String strProAlternateHost = /*"http://125.16.220.4/"*/"https://www.bihardiscom.co.in/";
	
	//private String strHostNameConstant = "http://220.225.3.133/";		//Development
	private String strHostNameConstant = "http://112.133.239.225/";		//Development  New
	//private String strHostNameConstant = /*"http://220.225.3.149/"*/"https://www.bihardiscom.co.in/";		//Production	//DC IP Address
	//private String strHostNameConstant = "http://220.227.230.133/";		//Production	//DR IP Address
	
	private SoapObject request=null;
	private SoapObject response=null;
	private Object ObjectResponse=null;
	private SoapSerializationEnvelope envelope=null;
	private HttpTransportSE httpTransport=null;
	
	public UtilSvrData(){
		if(UtilAppCommon.strHostName.equals(""))
			UtilAppCommon.strHostName = DevHostName;
			//UtilAppCommon.strHostName = ProHostName;
	}
		
	public String getJsonInputData(String strParam)
	{
		ObjectResponse=null;
/*		SOAPAction = "http://odishadiscoms.com/GetJson";	
		SOAPMethod =  "GetJson";
		SOAPNamespace = "http://odishadiscoms.com/";
		SOAPAddress =  "http://odishadiscoms.com/webservices/sbaService.asmx?WSDL";*/

		SOAPAction = "http://tempuri.org/Return_MWtoMobile";	
		SOAPMethod =  "Return_MWtoMobile";
		SOAPNamespace = "http://tempuri.org/";
		SOAPAddress =  UtilAppCommon.strHostName + "biharsbmservice/mw2sbm.asmx?WSDL";		
		//SOAPAddress =  "http://220.225.3.133/biharsbmservice/mw2sbm.asmx?WSDL";
		Log.v("UtilServData", "*******************UtilAppCommon.strHostName**** "+UtilAppCommon.strHostName);
		Log.v("", "**********getJsonInputData*****SOAP Addrsss  "+SOAPAddress);
		try
		{
			request = new SoapObject(SOAPNamespace,SOAPMethod);
			
			request.addProperty("strIMEINo", UtilAppCommon.IMEI_Number);
			//request.addProperty("strIMEINo", "911450251440843");
			request.addProperty("apkFileName", strParam);
			//request.addProperty("apkFileName", "VP1.96.apk");
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			httpTransport = new HttpTransportSE(SOAPAddress, 1000 * 90);
			httpTransport.call(SOAPAction, envelope);
			Thread.sleep(2000);
			ObjectResponse= envelope.getResponse();
		}
		catch (Exception ex)
		{
			 Log.e("getJsonInputData E", ex.toString());
			 return ex.toString();
		}
		
		Log.e("Response Object", ObjectResponse.toString());
		return ObjectResponse.toString();
				
	}
		
	public String getUserInfo(String strIME,String strPass)
	{
		ObjectResponse=null;
		
		SOAPAction = "http://tempuri.org/GetMobileMeterReaderDetail";	
		SOAPMethod =  "GetMobileMeterReaderDetail";
		SOAPNamespace = "http://tempuri.org/";
		SOAPAddress =  UtilAppCommon.strHostName + "biharsbmservice/ReturnSBMMeterReaderDetail.asmx?WSDL";	
		//SOAPAddress =  "http://220.225.3.133/biharsbmservice/ReturnSBMMeterReaderDetail.asmx?WSDL";		//Devep
		Log.v("UtilServData", "*******************UtilAppCommon.strHostName**** "+UtilAppCommon.strHostName);
		Log.v("", "**********getUserInfo*****SOAP Addrsss  "+SOAPAddress);
		try
		{
			request = new SoapObject(SOAPNamespace,SOAPMethod);
			//request.addProperty("USerID", strIME);
			Log.e("IMEI", UtilAppCommon.IMEI_Number);
			//Log.e("Passkey", strPass);
			
			request.addProperty("IMEINo", UtilAppCommon.IMEI_Number);
			//request.addProperty("IMEINo", "353835062918885");			
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			httpTransport = new HttpTransportSE(SOAPAddress, 1000 * 90);
			
			httpTransport.call(SOAPAction, envelope);
			
			ObjectResponse= envelope.getResponse();
			Log.e("Response" , ObjectResponse.toString());
		}
		catch (SocketTimeoutException ste)
		{
			Log.e("SocketTimeoutEx", ste.toString());
			return "Network Issue / Not Reachable";
		}
		catch (Exception ex)
		{
			Log.e("getUserInfo E", ex.toString());
			return ex.toString();
		}
		
		return ObjectResponse.toString();		
	}
	
	public String getValidDevice(String strIME)
	{
		ObjectResponse=null;
		SOAPAction = "http://tempuri.org/ValidateIMEI";	
		SOAPMethod =  "ValidateIMEI";
		SOAPNamespace = "http://tempuri.org/";
		SOAPAddress =  UtilAppCommon.strHostName + "biharsbmservice/ValidateIMEINo.asmx?WSDL"; //Prod
		//SOAPAddress =  "http://220.225.3.133/biharsbmservice/ValidateIMEINo.asmx?WSDL"; //Devp
		Log.v("UtilServData", "*******************UtilAppCommon.strHostName**** "+UtilAppCommon.strHostName);
		Log.v("", "*********getValidDevice******SOAP Addrsss  "+SOAPAddress);
		try
		{
			request = new SoapObject(SOAPNamespace,SOAPMethod);
			//request.addProperty("USerID", strIME);
			Log.e("IMEINo", UtilAppCommon.IMEI_Number);
			Log.e("SOAPAddress", SOAPAddress);
			Log.e("IP Address",UtilAppCommon.strHostName + " == " +  strHostNameConstant);
			//Log.e("Passkey", strPass);
			
			request.addProperty("IMEINo", UtilAppCommon.IMEI_Number);
			//request.addProperty("IMEINo", "353835062918882");
			//request.addProperty("Passkey", strPass);			
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			httpTransport = new HttpTransportSE(SOAPAddress, 1000 * 90);
			
			httpTransport.call(SOAPAction, envelope);				
			
			ObjectResponse= envelope.getResponse();
			Log.e("Response" , ObjectResponse.toString());
		}
		catch (SocketTimeoutException ste)
		{
			//Log.e("getValidDevice E",UtilAppCommon.strHostName + " == " +  strHostNameConstant);
			if(UtilAppCommon.strHostName.equals(strHostNameConstant))
			{
				Log.e("getValDev STE", ste.toString());
				UtilAppCommon.strHostName = DevHostName;
				//UtilAppCommon.strHostName = strProAlternateHost;
				//Log.e("getValidDevice E strProAlternateHost", strProAlternateHost);
				return "Timeout";
			}			
			else
			{
				Log.e("getValDev STE", ste.toString());
				//UtilAppCommon.strHostName = strProAlternateHost;
				return "Network Issue";
			}
			
		}
		catch (SocketException se)
		{
			Log.e("getValidDevice SE", se.toString());
			if(UtilAppCommon.strHostName.equals(strHostNameConstant))
			{
				UtilAppCommon.strHostName = DevHostName;
				//UtilAppCommon.strHostName = strProAlternateHost;
			}
			return "Network Issue";
		}
		catch (Exception ex)
		{
			Log.e("getValidDevice EX", ex.toString());
			return ex.toString();
		}
		
		return ObjectResponse.toString();		
	}

	@SuppressLint("DefaultLocale")
	public String getOutputData(String[] SAPInput, String strMob, String strPole)
	{
		ObjectResponse=null;

		SOAPAction = "http://tempuri.org/Get_MobileData";	
		SOAPMethod =  "Get_MobileData";
		SOAPNamespace = "http://tempuri.org/";
		SOAPAddress =  UtilAppCommon.strHostName + "BiharSBMService/MobiletoMW.asmx?WSDL";	//Prod
		//SOAPAddress =  "http://220.225.3.133/BiharSBMService/MobiletoMW.asmx?WSDL";	//Devp
		Log.v("UtilServData", "*******************UtilAppCommon.strHostName**** "+UtilAppCommon.strHostName);
		Log.v("UtilServData", "********getOutputData*******SOAP Addrsss  "+SOAPAddress);
		try
		{
			//Log.e("Util getOutputData", "Started");
			Log.e("UtilServData", SOAPAddress);
			request = new SoapObject(SOAPNamespace,SOAPMethod);
			//5000365114|15.07.2017|100270701|MD||0.0|0.00|19.1181586|72.8661005|2|1000||6566886666
			//5000019952|28.03.2017|10044180|OK|15228|0.87|0.00|19.1181586|72.8661005|1|1000||6566886666
			//Installation|SCHEDULED_BILLING_DATE|SAP_DEVICE|Meter_Status|Reading|Demand|PowerFactor|Lat|Long|Bill_Flag|KVAH|MobileNo|PoleNo
			
			String strParamValue = SAPInput[1] + "|" + SAPInput[9] + "|" + SAPInput[10] + "|" + SAPInput[7] + "|" + SAPInput[4] + 
					"|" + SAPInput[5] + "|" + SAPInput[6] + "|" + SAPInput[2] + "|" + SAPInput[3] + "|" + SAPInput[11] + "|" + SAPInput[13] + "|" + strMob + "|" + strPole;
			
			//strParamValue = strParamValue + "|" + UtilAppCommon.IMEI_Number;
			
			request.addProperty("MobileData", strParamValue);
			request.addProperty("IMEINo", UtilAppCommon.IMEI_Number);
			
			Log.e("getOutputData Param ", strParamValue);
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			httpTransport = new HttpTransportSE(SOAPAddress, 1000 * 120);
			//Log.e("strParamValue", "4");
			httpTransport.call(SOAPAction, envelope);	
			//Log.e("strParamValue", "5");
			ObjectResponse= envelope.getResponse();
			Log.e("Response OutputData" , ObjectResponse.toString());
		}
		catch (SocketTimeoutException ste)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("getOutputData STE", ste.getMessage());
			UtilAppCommon.strHostName = strDevAlternateHost;
			//UtilAppCommon.strHostName = strProAlternateHost;
			return "Network Issue / Not Reachable";
		}
		catch (SocketException se)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("getOutputData SE", se.getMessage());
			UtilAppCommon.strHostName = strDevAlternateHost;
			//UtilAppCommon.strHostName = strProAlternateHost;
			return "Network Issue / Not Reachable";
		}
		catch (Exception ex)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("getOutputData E", ex.getMessage());
			return "Network Issue / Not Reachable";
		}
		
		Log.e("Util getOutputData", "Completed");
		return ObjectResponse.toString();

	}

	@SuppressLint("DefaultLocale")
	public String updatePoleMobile(String[] SAPInput)
	{
		ObjectResponse=null;

		SOAPAction = "http://tempuri.org/UpdateMobPoleNumber";	
		SOAPMethod =  "UpdateMobPoleNumber";
		SOAPNamespace = "http://tempuri.org/";
		SOAPAddress =  UtilAppCommon.strHostName + "BiharSBMService/UpdateMobile_Pole_No.asmx?WSDL";	//Prod
		//SOAPAddress =  "http://220.225.3.133/BiharSBMService/UpdateMobile_Pole_No.asmx?WSDL";	//Devp
		Log.v("UtilServData", "*******************UtilAppCommon.strHostName**** "+UtilAppCommon.strHostName);
		Log.v("UtilServData", "*********updatePoleMobile******SOAP Addrsss  "+SOAPAddress);
		try
		{
			//Log.e("Util getOutputData", "Started");
			request = new SoapObject(SOAPNamespace,SOAPMethod);
			
			String strParamValue = SAPInput[0] + "|" + SAPInput[1] + "|" + SAPInput[2] + "|" + SAPInput[3];
			
			//strParamValue = strParamValue + "|" + UtilAppCommon.IMEI_Number;
			
			request.addProperty("MobileData", strParamValue);
			request.addProperty("IMEINo", UtilAppCommon.IMEI_Number);
			Log.e("getOutputData Param ", strParamValue);
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			httpTransport = new HttpTransportSE(SOAPAddress, 1000 * 90);
			//Log.e("strParamValue", "4");
			httpTransport.call(SOAPAction, envelope);	
			//Log.e("strParamValue", "5");
			ObjectResponse= envelope.getResponse();
			Log.e("Response OutputData" , ObjectResponse.toString());
		}
		catch(SocketException se)
		{
			Log.e("updatePoleMobile SE", se.getMessage());
			return  "Network Issue / Not Reachable";
		}
		catch (SocketTimeoutException ste)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updatePoleMobile STE", ste.getMessage());
			return  "Network Issue / Not Reachable";
		}
		catch (Exception ex)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updatePoleMobile E", ex.getMessage());
			return  "Network Issue / Not Reachable";
		}
		
		Log.e("Util getOutputData", "Completed");
		return ObjectResponse.toString();

	}

	@SuppressLint("DefaultLocale")
	public String updateMissingCons(String[] SAPInput)
	{
		ObjectResponse=null;

		SOAPAction = "http://tempuri.org/UpdateMissingConsumerDetail";	
		SOAPMethod =  "UpdateMissingConsumerDetail";
		SOAPNamespace = "http://tempuri.org/";
		SOAPAddress =  UtilAppCommon.strHostName + "biharsbmservice/UpdateMissingConsumer.asmx?WSDL";	//Prod
		//SOAPAddress =  "http://220.225.3.133/biharsbmservice/UpdateMissingConsumer.asmx?WSDL";	//Devp
		Log.v("UtilServData", "*******************UtilAppCommon.strHostName**** "+UtilAppCommon.strHostName);
		Log.v("UtilServData", "********updateMissingCons*******SOAP Addrsss  "+SOAPAddress);
		try
		{
			//Log.e("Util getOutputData", "Started");
			request = new SoapObject(SOAPNamespace,SOAPMethod);
			
			String strParamValue = SAPInput[0] + "|" + SAPInput[1] + "|" + SAPInput[2] + "|" + SAPInput[3]
					 + "|" + SAPInput[5]  + "|" + SAPInput[6] + "|" + SAPInput[7] + "|" + SAPInput[8] + "|" + SAPInput[4];
			
			//strParamValue = strParamValue + "|" + UtilAppCommon.IMEI_Number;
			
			request.addProperty("MobileData", strParamValue);
			request.addProperty("IMEINo", UtilAppCommon.IMEI_Number);
			Log.e("updatePoleMobile Param ", strParamValue);
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			httpTransport = new HttpTransportSE(SOAPAddress, 1000 * 90);
			//Log.e("strParamValue", "4");
			httpTransport.call(SOAPAction, envelope);	
			//Log.e("strParamValue", "5");
			ObjectResponse= envelope.getResponse();
			Log.e("Response OutputData" , ObjectResponse.toString());
		}
		catch (SocketTimeoutException ste)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updatePoleMobile STE", ste.getMessage());
			return "Network Issue / Not Reachable";
		}
		catch (SocketException se)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updatePoleMobile STE", se.getMessage());
			return "Network Issue / Not Reachable";
		}
		catch (Exception ex)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updateMissingCons E", ex.getMessage());
			return "Network Issue / Not Reachable";
		}
		
		Log.e("updateMissingCons", "Completed");
		return ObjectResponse.toString();

	}

	public String updateImage(String strCANo, String strBillMon, String strBillYear, String strSDOCode, String byteImage, String strMRUCode)
	{
		ObjectResponse=null;

		SOAPAction = "http://tempuri.org/UploadDatewiseCAImage";	
		SOAPMethod =  "UploadDatewiseCAImage";
		SOAPNamespace = "http://tempuri.org/";
		//220.225.3.132
		//SOAPAddress =  "http://220.225.3.132/biharsbmservice/UploadImage.asmx?WSDL";	//Prod
		SOAPAddress =  UtilAppCommon.strHostName + "biharsbmservice/UploadImage.asmx?WSDL";	//Prod
		//SOAPAddress =  "http://220.225.3.133/biharsbmservice/UploadImage.asmx?WSDL";	//Devp
		Log.v("UtilServData", "*******************UtilAppCommon.strHostName**** "+UtilAppCommon.strHostName);
		Log.v("UtilServData", "**********updateImage*****SOAP Addrsss  "+SOAPAddress);
		
		try
		{
			Log.e("Util updateImage", "Started");
			request = new SoapObject(SOAPNamespace,SOAPMethod);
			
			request.addProperty("imageArray", byteImage);
			request.addProperty("CANo", strCANo);
			request.addProperty("billMonth", strBillMon);
			request.addProperty("billYear", strBillYear);
			request.addProperty("subDvCode", strSDOCode);
			request.addProperty("MRU", strMRUCode);
			
			request.addProperty("IMEINo", UtilAppCommon.IMEI_Number);
			//strParamValue = strParamValue + "|" + UtilAppCommon.IMEI_Number;
						
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			httpTransport = new HttpTransportSE(SOAPAddress, 1000 * 90);
			//Log.e("strParamValue", "4");
			httpTransport.call(SOAPAction, envelope);	
			Log.e("strParamValue", "5");
			ObjectResponse= envelope.getResponse();
			Log.e("Response OutputData" , ObjectResponse.toString());
		}
		catch (SocketTimeoutException ste)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updateImage STE", ste.getMessage());
			return "Network Issue / Not Reachable";
		}
		catch (SocketException se)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updateImage STE", se.getMessage());
			return "Network Issue / Not Reachable";
		}
		catch (Exception ex)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updateImage E", ex.getMessage());
			return "Network Issue / Not Reachable";
		}
		
		Log.e("updateImage", "Completed");
		return ObjectResponse.toString();
		
	}

	@SuppressLint("DefaultLocale")
	public String updateAbnormality(String[] strAbnormality)
	{
		ObjectResponse=null;

		SOAPAction = "http://tempuri.org/ReturnMeterAbnormality";	
		SOAPMethod =  "ReturnMeterAbnormality";
		SOAPNamespace = "http://tempuri.org/";
		SOAPAddress =  UtilAppCommon.strHostName + "biharsbmservice/CaptureMeterAbnormality.asmx?WSDL";	//Prod
		//SOAPAddress =  "http://220.225.3.133/biharsbmservice/UpdateMissingConsumer.asmx?WSDL";	//Devp
		Log.v("UtilServData", "*******************UtilAppCommon.strHostName**** "+UtilAppCommon.strHostName);
		Log.v("UtilServData", "*********updateAbnormality******SOAP Addrsss  "+SOAPAddress);
		try
		{
			Log.e("Util updateAbnormality",strAbnormality[0] + "@" + strAbnormality[1] + "@" + strAbnormality[2] + "@" + strAbnormality[3] + "@" + strAbnormality[4] + "@" + strAbnormality[5] + "@" + UtilAppCommon.IMEI_Number);
			request = new SoapObject(SOAPNamespace,SOAPMethod);
						
			//strParamValue = strParamValue + "|" + UtilAppCommon.IMEI_Number;
			
			request.addProperty("caNo", strAbnormality[0].trim());
			request.addProperty("schMrDate", strAbnormality[1].trim());
			request.addProperty("MRU", strAbnormality[2].trim());
			request.addProperty("dvCode", strAbnormality[3].trim());
			request.addProperty("remarks", strAbnormality[4].trim());
			request.addProperty("abnormality", strAbnormality[5].trim());
			request.addProperty("IMEINo", UtilAppCommon.IMEI_Number);
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			httpTransport = new HttpTransportSE(SOAPAddress, 1000 * 90);
			//Log.e("strParamValue", "4");
			httpTransport.call(SOAPAction, envelope);	
			//Log.e("strParamValue", "5");
			ObjectResponse= envelope.getResponse();
			//Log.e("Response updateAbnormality" , ObjectResponse.toString());
		}
		catch (SocketTimeoutException ste)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updateAbnormality STE", ste.getMessage());
			return "Network Issue / Not Reachable";
		}
		catch (SocketException se)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updateAbnormality STE", se.getMessage());
			return "Network Issue / Not Reachable";
		}
		catch (Exception ex)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("updateAbnormality E", ex.getMessage());
			return "Network Issue / Not Reachable";
		}
		
		Log.e("updateAbnormality", "Completed");
		return ObjectResponse.toString();

	}


	public String updateBluetoothReading(String[] SAPInput)	{
		ObjectResponse=null;

		SOAPAction = "http://tempuri.org/GetUDMFlag";
		SOAPMethod =  "GetUDMFlag";
		SOAPNamespace = "http://tempuri.org/";
		SOAPAddress =  UtilAppCommon.strHostName + "biharsbmservice/udmsvc.asmx?WSDL";	//Prod
		//SOAPAddress =  "http://220.225.3.133/BiharSBMService/MobiletoMW.asmx?WSDL";	//Devp
		Log.v("UtilServData", "*******************UtilAppCommon.strHostName**** "+UtilAppCommon.strHostName);
		Log.v("UtilServData", "**********updateBluetoothReading*****SOAP Addrsss  "+SOAPAddress);
		try
		{
			//Log.e("Util getOutputData", "Started");
			Log.e("Util Address Output", SOAPAddress);
			request = new SoapObject(SOAPNamespace,SOAPMethod);
			//5000365114|15.07.2017|100270701|MD||0.0|0.00|19.1181586|72.8661005|2|1000||6566886666
			//5000019952|28.03.2017|10044180|OK|15228|0.87|0.00|19.1181586|72.8661005|1|1000||6566886666
			//Installation|SCHEDULED_MR_DATE|SAP_DEVICE|Meter_Status|Reading|RecordedDemand|PF|Lat|Long|Bill_Flag|KVAH

			String strParamValue = SAPInput[0] + "|" + SAPInput[1] + "|" + SAPInput[2] + "|" + SAPInput[3] + "|" + SAPInput[4] +
					"|" + SAPInput[5] + "|" + SAPInput[6] + "|" + SAPInput[7] + "|" + SAPInput[8] + "|" + SAPInput[9] + "|" + SAPInput[10];

			//strParamValue = strParamValue + "|" + UtilAppCommon.IMEI_Number;

			request.addProperty("mobileData", strParamValue);
			//request.addProperty("IMEINo", UtilAppCommon.IMEI_Number);

			Log.e("getBlueData Param ", strParamValue);
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			httpTransport = new HttpTransportSE(SOAPAddress, 1000 * 60);
			//Log.e("strParamValue", "4");
			httpTransport.call(SOAPAction, envelope);
			//Log.e("strParamValue", "5");
			ObjectResponse= envelope.getResponse();
			Log.i("Response getBlueData" , ObjectResponse.toString());
		}
		catch (SocketTimeoutException ste)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("getBlueData STE", ste.getMessage());
			//UtilAppCommon.strHostName = strProAlternateHost;
			UtilAppCommon.strHostName = DevHostName;
			return "Network Issue / Not Reachable";
		}
		catch (SocketException se)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("getBlueData SE", se.getMessage());
			//UtilAppCommon.strHostName = strProAlternateHost;
			UtilAppCommon.strHostName = DevHostName;
			return "Network Issue / Not Reachable";
		}
		catch (Exception ex)
		{
			//System.out.println("getOutputData E ==>> " + ex.getMessage());
			Log.e("getBlueData E", ex.getMessage());
			return "Network Issue / Not Reachable";
		}

		Log.e("Util getBlueData", "Completed");
		return ObjectResponse.toString();
	}
}
