/**
 * 
 */
package com.nagra.bsm.ui;

import com.nagra.bsm.util.CommonUtil;

/**
 * @author tetang
 * 
 */
public class EmailSetting extends RptTransSetting {
	public String emailAddr;

	public EmailSetting(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
	public EmailSetting(){
		this.emailAddr = CommonUtil.getPropertyValue("des_email");
	}
}
