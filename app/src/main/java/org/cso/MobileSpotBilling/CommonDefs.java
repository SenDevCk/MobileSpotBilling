package org.cso.MobileSpotBilling;

import android.os.Environment;

public class CommonDefs {
	
	
	public static final String SBDOC_DIR_IN_SDCARD = Environment.getExternalStorageDirectory().getPath()+"/SBDocs/";
	public static final String FILE_NAME = "INPUTDATA0001.txt";
	
	public static final String FILE_NAME_IN_APP_DIR = "INPUTDATA0001.txt";
	//public static final String URL = "http://www.odishadiscoms.com/webservices/SBAService.asmx?op=GetInputData";
	public static final String URL = "http://www.odishadiscoms.com/webservices/SBAService.asmx/GetInputData";
}