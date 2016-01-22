/**
 * 
 */
package com.nagra.bsm.tasks;

import org.apache.log4j.Logger;

import com.nagra.bsm.ui.LoginPage;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.util.CommonUtil;

/**
 * @author tetang
 *
 */
public class LoginTasks {
	public LoginPage loginPage ;
	protected static final Logger logger = Logger.getLogger(LoginTasks.class);
	
	public LoginTasks(){
		
	}
	
	public LoginTasks(LoginPage loginPage){
		this.loginPage = loginPage;
	}
	
	//log in with default username pw
	public RptCenterPage login(){
		String username = CommonUtil.getPropertyValue("username");
		String password = CommonUtil.getPropertyValue("password");
		logger.info(new StringBuilder("Login in with username : ").append(username).append(" and password : ").append(password));
		loginPage.loginAs(username, password);
		return new RptCenterPage(loginPage.getWebDriver());
	}	
	
	public RptCenterPage login(String username, String password){
		logger.info(new StringBuilder("Login in with username : ").append(username).append(" and password : ").append(password));
		loginPage.loginAs(username, password);
		return new RptCenterPage(loginPage.getWebDriver());
	}
	
	public void logout(){
		RptCenterPage rptCenterPage = new RptCenterPage(loginPage.getWebDriver());
		logger.info("logout");
		rptCenterPage.logout();
	}
}
