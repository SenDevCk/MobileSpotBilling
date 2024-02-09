package org.cso.MSBModel;

/**
 * Created by Priya on 2/23/16.
 */
public class Info {


    private int inforecordid;
    private String infoPktHeader;
    private String infoPktinfo;
    private int infoSyncState;
    private String infoAppreaderVersion;
    private int infodeviceRefid;
    private String infoNodeid;
    private String infoPcbno;
    private String infoDateofmanufacturing;
    private String infoFirmwareVersion;
    private int infoEnergy;
    private int infoBrownoutcount;
    private int infoBrownincount;
    private int infoLvcocount;
    private int infoImcoscount;
    private int infoResetscount;
    private String infoCurrenttimestamp;
    private int infoDailyusageentycount;
    private int infodebugentriescount;
    private String infopersistedtime;

    public int getInfoserverdatasyncstatus() {
        return infoserverdatasyncstatus;
    }

    public void setInfoserverdatasyncstatus(int infoserverdatasyncstatus) {
        this.infoserverdatasyncstatus = infoserverdatasyncstatus;
    }

    private int infoserverdatasyncstatus;

    public Info() {

    }

    public Info(String infoAppreadedVersion, int infoBrownincount, int infoBrownoutcount, String infoCurrenttimestamp, int infoDailyusageentycount, String infoDateofmanufacturing, int infodeviceRefid, int infoEnergy, String infoFirmwareVersion,
                int infoImcoscount, int infoLvcocount, String infoNodeid, String infoPcbno, String infoPktHeader, String infoPktinfo,
                int infoResetscount, int infoSyncState, int infodebugentriescount) {
        this.infoAppreaderVersion = infoAppreadedVersion;
        this.infoBrownincount = infoBrownincount;
        this.infoBrownoutcount = infoBrownoutcount;
        this.infoCurrenttimestamp = infoCurrenttimestamp;
        this.infoDailyusageentycount = infoDailyusageentycount;
        this.infoDateofmanufacturing = infoDateofmanufacturing;
        this.infodeviceRefid = infodeviceRefid;
        this.infoEnergy = infoEnergy;
        this.infoFirmwareVersion = infoFirmwareVersion;
        this.infoImcoscount = infoImcoscount;
        this.infoLvcocount = infoLvcocount;
        this.infoNodeid = infoNodeid;
        this.infoPcbno = infoPcbno;
        this.infoPktHeader = infoPktHeader;
        this.infoPktinfo = infoPktinfo;
        this.infoResetscount = infoResetscount;
        this.infoSyncState = infoSyncState;
        this.infodebugentriescount = infodebugentriescount;
    }

    public String getInfopersistedtime() {
        return infopersistedtime;
    }

    public void setInfopersistedtime(String infopersistedtime) {
        this.infopersistedtime = infopersistedtime;
    }

    public int getInfodebugentriescount() {
        return infodebugentriescount;
    }

    public void setInfodebugentriescount(int infodebugentriescount) {
        this.infodebugentriescount = infodebugentriescount;
    }

    public int getInforecordid() {
        return inforecordid;
    }

    public void setInforecordid(int inforecordid) {
        this.inforecordid = inforecordid;
    }

    public String getInfoAppreaderVersion() {
        return infoAppreaderVersion;
    }

    public void setInfoAppreaderVersion(String infoAppreaderVersion) {
        this.infoAppreaderVersion = infoAppreaderVersion;
    }

    public int getInfoBrownincount() {
        return infoBrownincount;
    }

    public void setInfoBrownincount(int infoBrownincount) {
        this.infoBrownincount = infoBrownincount;
    }

    public int getInfoBrownoutcount() {
        return infoBrownoutcount;
    }

    public void setInfoBrownoutcount(int infoBrownoutcount) {
        this.infoBrownoutcount = infoBrownoutcount;
    }

    public String getInfoCurrenttimestamp() {
        return infoCurrenttimestamp;
    }

    public void setInfoCurrenttimestamp(String infoCurrenttimestamp) {
        this.infoCurrenttimestamp = infoCurrenttimestamp;
    }

    public int getInfoDailyusageentycount() {
        return infoDailyusageentycount;
    }

    public void setInfoDailyusageentycount(int infoDailyusageentycount) {
        this.infoDailyusageentycount = infoDailyusageentycount;
    }

    public String getInfoDateofmanufacturing() {
        return infoDateofmanufacturing;
    }

    public void setInfoDateofmanufacturing(String infoDateofmanufacturing) {
        this.infoDateofmanufacturing = infoDateofmanufacturing;
    }

    public int getInfodeviceRefid() {
        return infodeviceRefid;
    }

    public void setInfodeviceRefid(int infodeviceRefid) {
        this.infodeviceRefid = infodeviceRefid;
    }

    public int getInfoEnergy() {
        return infoEnergy;
    }

    public void setInfoEnergy(int infoEnergy) {
        this.infoEnergy = infoEnergy;
    }

    public String getInfoFirmwareVersion() {
        return infoFirmwareVersion;
    }

    public void setInfoFirmwareVersion(String infoFirmwareVersion) {
        this.infoFirmwareVersion = infoFirmwareVersion;
    }

    public int getInfoImcoscount() {
        return infoImcoscount;
    }

    public void setInfoImcoscount(int infoImcoscount) {
        this.infoImcoscount = infoImcoscount;
    }

    public int getInfoLvcocount() {
        return infoLvcocount;
    }

    public void setInfoLvcocount(int infoLvcocount) {
        this.infoLvcocount = infoLvcocount;
    }

    public String getInfoNodeid() {
        return infoNodeid;
    }

    public void setInfoNodeid(String infoNodeid) {
        this.infoNodeid = infoNodeid;
    }

    public String getInfoPcbno() {
        return infoPcbno;
    }

    public void setInfoPcbno(String infoPcbno) {
        this.infoPcbno = infoPcbno;
    }

    public String getInfoPktHeader() {
        return infoPktHeader;
    }

    public void setInfoPktHeader(String infoPktHeader) {
        this.infoPktHeader = infoPktHeader;
    }

    public String getInfoPktinfo() {
        return infoPktinfo;
    }

    public void setInfoPktinfo(String infoPktinfo) {
        this.infoPktinfo = infoPktinfo;
    }

    public int getInfoResetscount() {
        return infoResetscount;
    }

    public void setInfoResetscount(int infoResetscount) {
        this.infoResetscount = infoResetscount;
    }

    public int getInfoSyncState() {
        return infoSyncState;
    }

    public void setInfoSyncState(int infoSyncState) {
        this.infoSyncState = infoSyncState;
    }
}
