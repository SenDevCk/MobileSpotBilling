package org.cso.MSBModel;

/**
 * Created by Jeeva on 12/24/2015.
 */
public class udpmdevice_gettersetter {

    public String getudpmdevice_recid() {
        return udpmdevice_recid;
    }

    public void setudpmdevice_recid(String udpmdevice_recid) {
        this.udpmdevice_recid = udpmdevice_recid;
    }

    public String getudpmdevice_udpmid() {
        return udpmdevice_udpmid;
    }

    public void setudpmdevice_udpmid(String udpmdevice_udpmid) {
        this.udpmdevice_udpmid = udpmdevice_udpmid;
    }

    public String udpmdevice_recid;
    public String udpmdevice_udpmid;
    public String udpmdevice_version;
    //UC_FDBK
    public String udpmdevice_cumEnergyReading;
    //UC_FDBK

    public String getudpmdevice_manufacturer() {
        return udpmdevice_manufacturer;
    }

    public void setudpmdevice_manufacturer(String udpmdevice_manufacturer) {
        this.udpmdevice_manufacturer = udpmdevice_manufacturer;
    }

    public String getudpmdevice_version() {
        return udpmdevice_version;
    }

    public void setudpmdevice_version(String udpmdevice_version) {
        this.udpmdevice_version = udpmdevice_version;
    }

    public String getudpmdevice_instalationdate() {
        return udpmdevice_instalationdate;
    }

    public void setudpmdevice_instalationdate(String udpmdevice_instalationdate) {
        this.udpmdevice_instalationdate = udpmdevice_instalationdate;
    }

    public String udpmdevice_manufacturer;
    public String udpmdevice_instalationdate;

    public String getudpm_status() {
        return udpm_status;
    }

    public void setudpm_status(String udpm_status) {
        this.udpm_status = udpm_status;
    }

    public String udpm_status;

//    public udpmdevice_gettersetter(String udpmdevice_recid, String udpmdevice_udpmid, String udpmdevice_version, String udpmdevice_manufacturer, String udpmdevice_instalationdate, String udpm_status)
// {
//UC_FDBK
    public udpmdevice_gettersetter(String udpmdevice_recid, String udpmdevice_udpmid, String udpmdevice_version, String udpmdevice_manufacturer, String udpmdevice_instalationdate, String udpm_status, String cumEnergy) {
//UC_FDBK
        this.udpmdevice_recid = udpmdevice_recid;
        this.udpmdevice_udpmid = udpmdevice_udpmid;
        this.udpmdevice_version=udpmdevice_version;
        this.udpmdevice_manufacturer=udpmdevice_manufacturer;
        this.udpmdevice_instalationdate=udpmdevice_instalationdate;
        this.udpm_status=udpm_status;
        //UC_FDBK
        this.udpmdevice_cumEnergyReading = cumEnergy;
        //UC_FDBK
    }

    public udpmdevice_gettersetter() {
        // TODO Auto-generated constructor stub
    }
}
