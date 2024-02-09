package org.cso.MSBModel;

/**
 * Created by Priya on 12/24/2015.
 */
public class Udpmdevice {
    public int Udpmdevicerecid;
    public String udpmdevdeviceid;

    public Udpmdevice(String udpmdevdeviceid, int udpmdevicerecid) {
        this.udpmdevdeviceid = udpmdevdeviceid;
        Udpmdevicerecid = udpmdevicerecid;
    }

    public Udpmdevice() {
        // TODO Auto-generated constructor stub
    }

    public Udpmdevice(String udpmdevdeviceid) {
        this.udpmdevdeviceid = udpmdevdeviceid;
    }


    public String getUdpmdevdeviceid() {
        return udpmdevdeviceid;
    }

    public void setUdpmdevdeviceid(String udpmdevdeviceid) {
        this.udpmdevdeviceid = udpmdevdeviceid;
    }

    public int getUdpmdevicerecid() {
        return Udpmdevicerecid;
    }

    public void setUdpmdevicerecid(int udpmdevicerecid) {
        Udpmdevicerecid = udpmdevicerecid;
    }


}
