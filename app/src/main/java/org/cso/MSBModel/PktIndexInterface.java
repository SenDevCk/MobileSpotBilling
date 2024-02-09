package org.cso.MSBModel;

/**
 * Created by Priya on 12/24/2015.
 */
public interface PktIndexInterface {

    //*******************************************************Info*******************************************************
    public static final int UDPM_INFO_UDPMSWVERSION = 0;
    public static final int UDPM_INFO_UDPMSWVERSION_LENGTH = 4;

    public static final int UDPM_INFO_PCBNO = 4;
    public static final int UDPM_INFO__PCBNO_LENGTH = 4;

    public static final int UDPM_INFO_DATEOFMANUFACTURING = 8;
    public static final int UDPM_INFO_DATEOFMANUFACTURING_LENGTH = 2;

    public static final int UDPM_INFO_FIRMWAREVERSION = 10;
    public static final int UDPM_INFO_FIRMWAREVERSION_LENGTH = 1;

    public static final int UDPM_INFO_ENEGRY = 11;
    public static final int UDPM_INFO_ENEGRY_LENGTH = 4;

    public static final int UDPM_INFO_BROWNOUT = 15;
    public static final int UDPM_INFO_BROWNOUT_LENGTH = 2;

    public static final int UDPM_INFO_BROWNIN = 17;
    public static final int UDPM_INFO_BROWNIN_LENGTH = 2;

    public static final int UDPM_INFO_OLVCO = 19;
    public static final int UDPM_INFO_OLVCOS_LENGTH = 2;

    public static final int UDPM_INFO_IMCOS = 21;
    public static final int UDPM_INFO_IMCOS_LENGTH = 2;

    public static final int UDPM_INFO_RESET = 23;
    public static final int UDPM_INFO_RESET_LENGTH = 2;

    public static final int UDPM_INFO_CURRENTTIMESTAMP = 25;
    public static final int UDPM_INFO_CURRENTTIMESTAMP_LENGTH = 4;

    public static final int UDPM_INFO_DAILYUSAGE = 29;
    public static final int UDPM_INFO_DAILYUSAGE_LENGTH = 1;

    public static final int UDPM_INFO_DEBUGENTRIES = 30;
    public static final int UDPM_INFO_DEBUGENTRIES_LENGTH = 1;

    public static final int UDPM_INFO_PACKET_DATA_START_INDEX = 6;
    public static final int UDPM_INFO_PACKET_PACKET_DATA_LENGTH_INDEX = 2;
    public static final int UDPM_INFO_PACKET_SIZE = 31;
    public static final int UDPM_PACKET_SIZE = 20;
    //*******************************************************End Info*******************************************************


    //*******************************************************Debug feed*****************************************************
    public static final int UDPM_DEBUG_VOLTAGE = 0;
    public static final int UDPM_DEBUG_VOLTAGE_LENGTH = 2;

    public static final int UDPM_DEBUG_CURRENT = 2;
    public static final int UDPM_DEBUG_CURRENT_LENGTH = 2;



    public static final int UDPM_DEBUG_MODULETEMPATURE = 4;
    public static final int UDPM_DEBUG_MODULETEMPATURE_LENGTH = 4;


  //  public static final int UDPM_DEBUG_PACKET_DATA_START_INDEX = 6;
   // public static final int UDPM_DEBUG_PACKET_DATA_LENGTH_INDEX = 2;
    public static final int UDPM_DEBUGE_PACKET_SIZE = 8;


    //*******************************************************End Debug*******************************************************

    //*******************************************************Daily Usage ****************************************************


    public static final int UDPM_DAILYUSAGE_ENEGRY = 0;
    public static final int UDPM_DAILYUSAGE_ENEGRY_LENGTH = 2;

    public static final int UDPM_DAILYUSAGE_PEAKLOAD = 2;
    public static final int UDPM_DAILYUSAGE_PEAKLOAD_LENGTH = 2;

    public static final int UDPM_DAILYUSAGE_PEAKLOADTIME = 4;
    public static final int UDPM_DAILYUSAGE_PEAKLOADTIME_LENGTH = 2;

    public static final int UDPM_DAILYUSAGE_MAXTEMP = 6;
    public static final int UDPM_DAILYUSAGE_MAXTEMP_LENGTH = 1;

    public static final int UDPM_DAILYUSAGE_MAXTEMPTIME = 7;
    public static final int UDPM_DAILYUSAGE_MAXTEMPTIME_LENGTH = 2;

    public static final int UDPM_DAILYUSAGE_TIMESTAMP = 9;
    public static final int UDPM_DAILYUSAGE_TIMESTAMP_LENGTH = 2;


    public static final int UDPM_DAILYUSAGE_PACKET_DATA_START_INDEX = 6;
    public static final int UDPM_DAILYUSAGE_PACKET_DATA_LENGTH_INDEX = 2;
    public static final int UDPM_DAILYUSAGE_PACKET_SIZE = 11;

    //*******************************************************End Daily Usage***************************************************

    //*******************************************************Live Feed ********************************************************
    public static final int UDPM_LIVEFEED_VOLTAGE = 0;
    public static final int UDPM_LIVEFEED_VOLTAGE_LENGTH = 2;

    public static final int UDPM_LIVEFEED_CURRENT = 2;
    public static final int UDPM_LIVEFEED_CURRENT_LENGTH = 2;

    public static final int UDPM_LIVEFEED_LVCOS = 4;
    public static final int UDPM_LIVEFEED_LVCOS_LENGTH = 1;

    public static final int UDPM_LIVEFEED_ENEGRY = 5;
    public static final int UDPM_LIVEFEED_ENEGRY_LENGTH = 4;

    public static final int UDPM_LIVEFEED_BROWNOUT = 9;
    public static final int UDPM_LIVEFEED_BROWNOUT_LENGTH = 2;

    public static final int UDPM_LIVEFEED_BROWNIN = 11;
    public static final int UDPM_LIVEFEED_BROWNIN_LENGTH = 2;

    public static final int UDPM_LIVEFEED_OLOCOS = 13;
    public static final int UDPM_LIVEFEED_OLOCOS_LENGTH = 2;

    public static final int UDPM_LIVEFEED_IMCOS = 15;
    public static final int UDPM_LIVEFEED_IMCOS_LENGTH = 2;

    public static final int UDPM_LIVEFEED_RESET = 17;
    public static final int UDPM_LIVEFEED_RESET_LENGTH = 2;

    public static final int UDPM_LIVEFEED_MODULETEMPERATURE = 19;
    public static final int UDPM_LIVEFEED_MODULETEMAPTURE_LENGTH = 1;


    public static final int UDPM_LIVEFEED_PACKET_DATA_START_INDEX = 6;
    public static final int UDPM_LIVEFEED_PACKET_DATA_LENGTH_INDEX = 2;
    public static final int UDPM_LIVEFEED_PACKET_SIZE = 20;
    //*******************************************************End Live Feed ****************************************************

    //**************************************************ERROR Codes************************************************************
    public static final int ERR_INVALID_RESPONSE = 1;
    public static final int ERR_LENGTH = 2;
    public static final int ERR_TIME_FAIL = 3;
    public static final int ERR_DATE_FAIL = 4;
    public static final int ERR_FRAM_READ = 5;
    public static final int ERR_FRAM_WRITE = 6;
    public static final int ERR_INVALID_OFFSET = 7;
    public static final int ERR_INCOMPLETE_PKT = 8;
    public static final int ERR_FLASH_READ = 9;
    public static final int ERR_FLASH_WRITE = 10;
    public static final int ERR_INVALID_RANGE = 11;



    //**************************************************End ERROR Codes********************************************************

    //***************************************************INFO Bit Masking******************************************************
    public static final int INFO_MANUFACTURE_MONTH_MASK_V1 = 0x0F;
    public static final int INFO_MANUFACTURE_DATE_MASK_V1 = 0x1F;
    public static final int INFO_MANUFACTURE_YEAR_MASK_V1 = 0x7F;

    public static final int INFO_CYRRENT_TIME_DATE_MASK_V1 = 0x1F;
    public static final int INFO_CYRRENT_TIME_MONTH_MASK_V1 = 0x0F;
    public static final int INFO_CYRRENT_TIME_YEAR_MASK_V1 = 0x7F;

    public static final int INFO_CYRRENT_TIME_HOUR_MASK_V1 = 0x1F;
    public static final int INFO_CYRRENT_TIME_MIN_MASK_V1 = 0x3F;


    //***************************************************DEBUG Bit Masking******************************************************
    public static final int DEBUG_MODULE_TEMP_MASK_V1 = 0xFF;
    public static final int DEBUG_CURRENT_LVCO_STATUS_MASK_V1 = 0x01;
    public static final int DEBUG_CURRENT_EVENT_STATUS_MASK_V1 = 0x07;
    public static final int DEBUG_CURRENT_TIME_MONTH_MASK_V1 = 0x0F;
    public static final int DEBUG_CURRENT_TIME_DAY_MASK_V1 = 0x1F;
    public static final int DEBUG_CURRENT_TIME_HOUR_MASK_V1 = 0x1F;
    public static final int DEBUG_CURRENT_MIN_YEAR_MASK_V1 = 0x3F;

    //***************************************************DAILYUSAGE Bit Masking******************************************************
    public static final int DAILYUSAGE_TIME_HOUR_MASK_V1 = 0x3F;
    public static final int DAILYUSAGE_TIME_MIN_MASK_V1 = 0x1F;
    public static final int DAILYUSAGE_TIME_DATE_MASK_V1 = 0x1F;
    public static final int DAILYUSAGE_TIME_MONTH_MASK_V1 = 0x0F;
    public static final int DAILYUSAGE_TIME_YEAR_MASK_V1 = 0x7F;


    //***************************************************DAILYUSAGE Bit Masking******************************************************
    public static final int DATETIME_TIME_SECOND_MASK_V1 = 0x3F;
    public static final int DATETIME_TIME_MIN_MASK_V1 = 0x3F;
    public static final int DATETIME_TIME_HOUR_MASK_V1 = 0x1F;

    public static final int DATETIME_TIME_DATE_MASK_V1 = 0x1F;
    public static final int DATETIME_TIME_MONTH_MASK_V1 = 0x0F;
    public static final int DATETIME_TIME_YEAR_MASK_V1 = 0x7F;


}





