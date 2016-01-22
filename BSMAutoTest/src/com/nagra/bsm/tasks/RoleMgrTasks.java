/**
 * 
 */
package com.nagra.bsm.tasks;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.nagra.bsm.ui.RoleEdit;
import com.nagra.bsm.ui.RoleMgrPage;

/**
 * @author tetang
 *
 */
public class RoleMgrTasks extends AdministrationTasks {
	private RoleMgrPage roleMgrPage;
	
	public RoleMgrTasks(RoleMgrPage roleMgrPage){
		this.roleMgrPage = roleMgrPage;
	}

public RoleMgrTasks() {
		// TODO Auto-generated constructor stub
	}

	//not finish yet
	public String createRole(String rolename, String comment , String... permissions){
		logger.info("Click Create Role button");
		roleMgrPage.clickCreateRole();
		RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
		logger.info("Input role name");
		re.inputRoleName(rolename);
		// set permission is to be done
		logger.info("Input comment");
		re.inputComments(comment);
		logger.info("Click OK button");
		re.clickOK();
		String msg = roleMgrPage.getTextFromAlertWindow();
		roleMgrPage.acceptAlertWindow();
		if (re.isWindowPresent("Role Edit")){
			re.closeWindow();
		}
		logger.info("Alert Message: " + msg);
		return msg;
	}
	
	public String removeRole(String... roles){
		logger.info("Check on users to be removed");
		for(String role : roles){
			logger.info("Select the user :" + role);
			roleMgrPage.clickRoleFromRoleList(role);
		}		
		logger.info("Click Remove Role Button");
		roleMgrPage.clickRemoveRole();
		String msg1 = roleMgrPage.getTextFromAlertWindow();
		logger.info("Alert Message: " + msg1);
		roleMgrPage.acceptAlertWindow();
		String msg2 = roleMgrPage.getTextFromAlertWindow();
		logger.info("Alert Message: " + msg2);
		roleMgrPage.acceptAlertWindow();
		return msg2;
	}
	
	public Boolean chkRoleExists(String roleName){
		return roleMgrPage.checkRoleExistsInUserList(roleName);
	}
	
	public void gotoRoleMgr(){
		this.roleMgrPage = gotoRoleMgrPage();
	}
	
	public List<WebElement> getVisiableBtns(){
		return roleMgrPage.getVisiableBtns();
	}	
}
