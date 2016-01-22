/**
 * 
 */
package com.nagra.bsm.tasks;

import java.util.List;

import org.openqa.selenium.WebElement;

import net.neoremind.sshxcute.core.Logger;

import com.nagra.bsm.ui.UserEdit;
import com.nagra.bsm.ui.UserMgrPage;

/**
 * @author tetang
 *
 */
public class UserMgrTasks extends AdministrationTasks{
	private UserMgrPage userMgrPage;
	
	public UserMgrTasks(UserMgrPage userMgrPage){
		this.userMgrPage = userMgrPage ;
	}

	public UserMgrTasks() {
		// TODO Auto-generated constructor stub
	}

	public String createUser(String username, String pw, String confirmpw, boolean status, String... roles){		
		logger.info("Click Create User Button");
		userMgrPage.clickCreateUser();		
		logger.info("Input user data.");
		UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
		userEdit.inputUserData(username, pw, confirmpw, status, roles);
		logger.info("Click OK button");
		userEdit.clickOK();
		String msg = userMgrPage.getTextFromAlertWindow();
		userMgrPage.acceptAlertWindow();
		if (userEdit.isWindowPresent("User Edit")){
			userEdit.closeWindow();
		}
		logger.info("Alert Message: " + msg);
		return msg;
	}
	
	public String removeUser(String... users){
		logger.info("Check on users to be removed");
		for(String user : users){
			logger.info("Select the user :" + user);
			userMgrPage.clickUserFromUserList(user);
		}		
		logger.info("Click Remove User Button");
		userMgrPage.clickRemoveUser();
		String msg1 = userMgrPage.getTextFromAlertWindow();
		userMgrPage.acceptAlertWindow();
		logger.info("Alert Message: " + msg1);
		String msg2 = userMgrPage.getTextFromAlertWindow();
		userMgrPage.acceptAlertWindow();
		logger.info("Alert Message: " + msg2);
		return msg2;
	}
	
	public Boolean chkUserExists(String userName){
		return userMgrPage.checkUserExistsInUserList(userName);
	}
	
	public List<WebElement> getVisiableBtns(){
		return userMgrPage.getVisiableBtns();
	}		
	
	public void gotoUserMgr(){
		this.userMgrPage = gotoUserMgrPage();
	}
}
