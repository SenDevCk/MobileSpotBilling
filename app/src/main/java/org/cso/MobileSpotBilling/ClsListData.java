package org.cso.MobileSpotBilling;


public class ClsListData{
	public ClsListData( String display, String value ) {
        this.display = display;
        this.value = value;
    } 

    public String getDisplay() {
        return display;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return display;
    }

    String display;
    String value;
}