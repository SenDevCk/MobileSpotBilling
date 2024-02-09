/**
 * 
 */
package org.cso.MobileSpotBilling;

import android.app.Application;

/**
 * @author S-Patra
 *
 */
public final class GlobalVar extends Application
{
 private String isValidUsr;

public String getIsValidUsr() {
	return isValidUsr;
}

public void setIsValidUsr(String isValidUsr) {
	this.isValidUsr = isValidUsr;
}

}
