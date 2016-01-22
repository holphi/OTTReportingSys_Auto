/**
 * 
 */
package com.nagra.bsm.tasks;

import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.RptTransportPage;
import com.nagra.bsm.util.CommonUtil;

/**
 * @author tetang
 *
 */
public class TransportConfTasks extends AdministrationTasks {
   private RptTransportPage rptTransPage;
   
   public TransportConfTasks(){
	   
   }   
   
   public TransportConfTasks(RptTransportPage rptTransPage){
	   this.rptTransPage = rptTransPage ;
   }
   
   public void gotoRptTransConf(RptCenterPage rptCenterPg){
	   this.rptTransPage = rptCenterPg.goToAdmin().goToRptTransportConfPage();
	   logger.info("Go to Report Transport Configure");
   }
	
   
   public void gotoBakTransConf(RptCenterPage rptCenterPg){
	   this.rptTransPage = rptCenterPg.goToAdmin().goToBakTransportConfPage();
	   logger.info("Go to Backup Transport Configure");
   }
   
	public void setSmtpSetting(String smtp, String port, String senderEmail, String username, String password){
		rptTransPage.setSmtp(smtp);		
		logger.info("Set SMTP: " + smtp);
		rptTransPage.setSmtpPort(port);
		logger.info("Set SMTP port: " + port);
		rptTransPage.setSenderEmailAddress(senderEmail);
		logger.info("Set Sender Email Address: " + senderEmail);
		rptTransPage.setSenderUserName(username);
		logger.info("Set Sender domain username: " + username );
		rptTransPage.setSenderPassword(password);
		logger.info("Set Sender domain password : " + password);
		rptTransPage.clickSaveSmtpSettings();
		logger.info("Save Smtp Seetings");
	}

	//default smtp settings without username password
	public void setSmtpSetting(){
		String smtp = CommonUtil.getPropertyValue("smpt_server");
		String port = CommonUtil.getPropertyValue("smpt_port");
		String senderEmail = CommonUtil.getPropertyValue("smpt_sender");
		setSmtpSetting(smtp,port,senderEmail,"","");
	}
	
	/*
	 * Set default email address
	 */
	public void setDesEmail(){		
		String email = CommonUtil.getPropertyValue("des_email");
		setDesEmail(email);
	}
	
	public void setDesEmail(String address){				
		rptTransPage.setDesEmail(address);
		logger.info("Set destination email address");
		rptTransPage.clickSaveDesEmail();
		logger.info("Save destination email address Settings");
	}
	
	public void setFtpSetting(String ftpip, String ftppath, String username, String password){
		rptTransPage.setFtpIp(ftpip);
		logger.info("Set Ftp ip: "+ ftpip);
		rptTransPage.setFtpPath(ftppath);
		logger.info("Set Ftp path: "+ ftppath);
		rptTransPage.setFtpUserName(username);
		logger.info("Set Ftp username: "+ username);
		rptTransPage.setFtpPassword(password);
		logger.info("Set Ftp password: "+password);
		rptTransPage.clickSaveFtpSettings();		
	}
	/*
	 * set default FTP settings
	 */
	public void setFtpSetting(){
		String ftpip = CommonUtil.getPropertyValue("ftp_url");		
		String ftppath = CommonUtil.getPropertyValue("ftp_savepath");
		String username = CommonUtil.getPropertyValue("ftp_username");
		String password = CommonUtil.getPropertyValue("ftp_password");
		setFtpSetting(ftpip,ftppath,username,password);
	}
	
	/*
	 * set anonymous ftp with default hostname.
	 */
	public void setFtpSetting(String ftppath){
		String ftpip = CommonUtil.getPropertyValue("ftp_url");		
		setFtpSetting(ftpip,ftppath,"","");		
	}
	
	public void setHttpSetting(String httpurl, String username, String password){
		rptTransPage.setHttpServerUrl(httpurl);
		logger.info("Set Http URL: " + httpurl);
		rptTransPage.setHttpLoginName(username);
		logger.info("Set Http username: " + username);
		rptTransPage.setHttpLoginPassword(password);
		logger.info("Set Http password: " + password);
		rptTransPage.clickSaveHttpSettings();		
	}
	
	/*
	 * default HTTP settings with anonymous login: http://vm81653/webdav/report/
	 */	
	public void setHttpSetting(String httpurl){
		setHttpSetting(httpurl,"","");
	}
	
	public void setHttpSetting(){
		String httpurl = CommonUtil.getPropertyValue("http_url");		
		String username = CommonUtil.getPropertyValue("http_username");
		String password = CommonUtil.getPropertyValue("http_password");
		setHttpSetting(httpurl,username,password);
	}
	
	public void resetHttpSetting(){
		rptTransPage.clickResetHttp();
		logger.info("Reset Http Settings");
	}
	
	public void resetFtpSetting(){
		rptTransPage.clickResetFtp();
		logger.info("Reset Ftp Settings");
	}
	
	public void resetSmtpSettings(){
		rptTransPage.clickResetSmptSettings();
		logger.info("Reset Smtp Settings");
	}
	
	public void resetDesEmail(){
		rptTransPage.clickResetDesEmail();
		logger.info("Reset destination emaill address");
	}
	
	public void resetRepeatCount(){
		rptTransPage.clickResetRepeatCount();
		logger.info("Reset repeat count");
	}
}
