/**
 * 
 */
package com.nagra.bsm.ui;

import com.nagra.bsm.util.CommonUtil;

/**
 * @author tetang
 * 
 */
public class FtpSetting extends RptTransSetting {
	public String ftpip, savePath, username, password;

	public FtpSetting(String ftpip, String savePath, String usr, String pwd) {
		this.ftpip = ftpip;
		this.savePath = savePath;
		this.username = usr;
		this.password = pwd;
	}

	public FtpSetting() {
		this.ftpip = CommonUtil.getPropertyValue("ftp_url");
		this.savePath = CommonUtil.getPropertyValue("ftp_savepath");
		this.username = CommonUtil.getPropertyValue("ftp_username");
		this.password = CommonUtil.getPropertyValue("ftp_password");
	}
	
	public FtpSetting(String ftpip, String savePath) {
		this.ftpip = ftpip;
		this.savePath = savePath;
		this.username = "";
		this.password = "";
	}
}
