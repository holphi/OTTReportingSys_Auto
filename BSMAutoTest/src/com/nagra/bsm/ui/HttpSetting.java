/**
 * 
 */
package com.nagra.bsm.ui;

import com.nagra.bsm.util.CommonUtil;

/**
 * @author tetang
 * 
 */
public class HttpSetting extends RptTransSetting {
	public String httpUrl, username, password;

	public HttpSetting(String httpUrl, String usr, String pwd) {
		this.httpUrl = httpUrl;
		this.username = usr;
		this.password = pwd;
	}
	
	public HttpSetting(String httpUrl) {
		this.httpUrl = httpUrl;
		this.username = "";
		this.password = "";
	}
	
	public HttpSetting(){
		this.httpUrl = CommonUtil.getPropertyValue("http_url");
		this.username = CommonUtil.getPropertyValue("http_username");
		this.password = CommonUtil.getPropertyValue("http_password");

	}
}
