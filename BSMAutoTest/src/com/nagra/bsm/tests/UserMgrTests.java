package com.nagra.bsm.tests;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;

import com.nagra.bsm.ui.*;
import com.nagra.bsm.util.CommonUtil;

import com.nagra.bsm.tasks.BrowserTasks;
import com.nagra.bsm.tests.categorymarker.SanityCheck;

public class UserMgrTests extends BaseTests{


	//Utility method for quick navigation to test page.
	private UserMgrPage navigateToTestPage(String username, String password)
	{
		logger.info("Login in with User:" + username + ", Password:" + password);
		LoginPage loginPage = new LoginPage(CommonUtil.launchTestPortal());
		RptCenterPage reportCenterPage = loginPage.loginAs(username, password);
		
		logger.info("Click Administration to go to Administration tab.");
		AdminPage adminPage = reportCenterPage.goToAdmin();
		
		logger.info("Click Security->User Manager.");
		return adminPage.goToUserMgrPage();
	}
	
	@Rule
	public Timeout globalTimeout = new Timeout(300000);

	@Before
	public void testSetup(){
		logger.info("============================="+testName.getMethodName()+"==============================");
	}
	
	@Test
	@Category(SanityCheck.class)
	public void testUserMgr0100()
	{		
		String username = "user"+CommonUtil.getRandomStr();
		String password = "123";
		try
		{

			//Test execution
			logger.info("Log in as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
								  			 CommonUtil.getPropertyValue("password"));
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, password, password, true, new String[]{"admin"});
			userEdit.clickOK();
		
			//Verification
			Assert.assertEquals("Verfiy the message of pop-up dialog.",
					            "Create User Successfully.",
						  	    userMgrPage.getTextFromAlertWindow());
			userMgrPage.acceptAlertWindow();
			Assert.assertTrue("Verify the new created user should be in user list.", userMgrPage.checkUserExistsInUserList(username));
			Assert.assertTrue("Verify the status should be Active", userMgrPage.isUserActiveInUserList(username));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(username);
		}
	}
	
	@Test
	public void testUserMgr0200()
	{
		String username = "user"+CommonUtil.getRandomStr();
		try
		{

			logger.info("Login as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							  	CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, "123", "456", true, new String[]{});
			userEdit.clickOK();
		
			//Verification
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the message of pop-up dialog.", "The passwords you entered do not match. Please try again." , msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(username);
		}
	}
	
	@Test
	public void testUserMgr0300()
	{
		String username = "user" + CommonUtil.getRandomStr();
		String password = "123";
		
		try{

			logger.info("Login as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							  	CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, password, password, false, new String[]{"ADMIN"});
			userEdit.clickOK();		
			userMgrPage.acceptAlertWindow();

			//Verification
			Assert.assertTrue("Verify the new created user should be in user list.", userMgrPage.checkUserExistsInUserList(username));
			Assert.assertFalse("Verify the status should be InActive", userMgrPage.isUserActiveInUserList(username));

			LoginPage loginPage = userMgrPage.logout();
			loginPage.loginAs(username, password);
			Assert.assertTrue("Verify the error msg output",loginPage.isErrorMsgOutput());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(username);
		}
	}
	
	@Test
	public void testUserMgr0400()
	{
		try{

			logger.info("Login as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							  	CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData("", "123", "123", true, new String[]{"admin"});
			userEdit.clickOK();
		
			//Verification
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the message of pop-up dialog.", "Please input user name.",  msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testUserMgr0500()
	{
		try
		{

			logger.info("Login as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							  	CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			String username="u"+CommonUtil.getRandomStr();
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, "", "123", true, new String[]{"admin"});
			userEdit.clickOK();
		
			//Verification
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the message of pop-up dialog.", "Please input password.", msg);
			logger.info("Close user edit window");
			userEdit.closeWindow();
			Assert.assertFalse("Verify the user should not be in user list.", userMgrPage.checkUserExistsInUserList(username));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testUserMgr0600()
	{
		try
		{

			logger.info("Login as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							  	CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			String username = "u" + CommonUtil.getRandomStr();
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, "123", "", true, new String[]{"admin"});
			userEdit.clickOK();
		
			//Verification
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the message of pop-up dialog.", 
							  "The passwords you entered do not match. Please try again.",
							  msg);
			logger.info("Close user edit window");
			userEdit.closeWindow();
			Assert.assertFalse("Verify the user should not be in user list.", userMgrPage.checkUserExistsInUserList(username));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testUserMgr0700()
	{
		String username="user" + CommonUtil.getRandomStr();
		try
		{

			logger.info("Login as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							  CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, "123", "123", true, new String[]{});
			userEdit.clickOK();
		
			//Verification
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the message of pop-up dialog.", "Create User Successfully.", msg);
			Assert.assertTrue("Verify the user should not be in user list.", userMgrPage.checkUserExistsInUserList(username));
		}
		catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
			removeUser(username);
		}
	}
	
	@Test
	public void testUserMgr0800()
	{
		try
		{

			logger.info("Login as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							  	  CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			String username = "admin";
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, "123", "123", true, new String[]{});
			userEdit.clickOK();
		
			//Verification
			logger.info("Verify the message of pop-up dialog.");
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the message of pop-up dialog.", "The user name you entered exists,please choose another one.", msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testUserMgr0801()
	{
		String user="";
		try
		{
			user = createTestAccount(true);
		
			logger.info("Login as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							  	  CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(user.toUpperCase(), "123", "123", true, new String[]{});
			userEdit.clickOK();
		
			//Verification
			logger.info("Verify the message of pop-up dialog.");
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the message of pop-up dialog.", 
					          "The user name you entered exists,please choose another one." ,
					          msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr0900()
	{
		String username="u"+CommonUtil.getRandomStr();
		String password="123";
		try{

			logger.info("Log in as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
								  CommonUtil.getPropertyValue("password"));
			
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, password, password, true, new String[]{"admin"});
			userEdit.clickOK();
			
			//Verification
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verfiy the message of pop-up dialog.",
					          "Create User Successfully.",
					          msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(username);
		}
	}
	
	@Test
	public void testUserMgr1000()
	{
		String username="u"+CommonUtil.getRandomStr();
		String password="123";
		try
		{

			logger.info("Log in as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
								  CommonUtil.getPropertyValue("password"));
			
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, password, password, true, new String[]{"ADMIN","USER"});
			userEdit.clickOK();
			
			//Verification
			logger.info("Verfiy the message of pop-up dialog.");
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verfiy the message of pop-up dialog.",
						 	  "Create User Successfully.",
							  msg);
			
			LoginPage loginPage = userMgrPage.logout();
			loginPage.loginAs(username, password);
			logger.info("Verify the speicified user sign in successfully.");
			Assert.assertEquals("Verify the speicified user sign in successfully.",
								"Welcome | Business and Security Monitoring",
								loginPage.getTitle());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(username);
		}
	}
	
	@Test
	public void testUserMgr1100()
	{
		try
		{

			logger.info("Log in as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
								  CommonUtil.getPropertyValue("password"));
			
			//Test execution
			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			String username="u"+CommonUtil.getRandomStr();
			String password="123";
			logger.info("Input new user data.");
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, password, password, true, new String[]{"admin"});
			userEdit.clickCancel();
			
			//Verification
			logger.info("Verfiy the specified user should not be in user list.");
			Assert.assertFalse("Verfiy the specified user should not be in user list.",
							  userMgrPage.checkUserExistsInUserList(username));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testUserMgr1200()
	{
		try{

			logger.info("Log in as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
								  CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Click Update User directly without user selected.");
			userMgrPage.clickUpdateUser();
		
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			logger.info("Verify the message text in alert window.");
			Assert.assertEquals("Verify the message text in alert window.",
					          "Please select a record to edit.",
						  	  msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testUserMgr1300()
	{
		try
		{

			logger.info("Log in as admin with create user permission.");
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							  	CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Click Update User directly without user selected.");
			userMgrPage.clickUpdateUser();
		
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
		
			logger.info("Verify the message text in alert window.");
			Assert.assertEquals("Verify the message text in alert window.",
					            "Please select a record to edit.",
						  	    msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testUserMgr1400()
	{
		String user = "";
		
		try{
			user = createTestAccount(true);
			

			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
								  CommonUtil.getPropertyValue("password"));
		
			//Test execution
			logger.info("Select the specified user: "+user);
			userMgrPage.clickUserFromUserList(user);
			logger.info("Click Update User to update the selected user.");
			userMgrPage.clickUpdateUser();	
		
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
		
			String password = "456";
			userEdit.setPassword(password);
			userEdit.setConfirmPassword(password);
			userEdit.clickOK();
		
			//Verification
			//1.Verify the update window message
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			logger.info("Verify the message text in alert window.");
			Assert.assertEquals("Verify the message text in alert window.",
					          "Update User Successfully.",
					          msg);
			//2.Verify the user could be sign successfully.
			LoginPage loginPage = userMgrPage.logout();
			loginPage.loginAs(user, password);
			Assert.assertEquals("Verify the speicified user sign in successfully.",
								"Welcome | Business and Security Monitoring",
								loginPage.getTitle());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	@Category(SanityCheck.class)
	public void testUserMgr1500() 
	{		
		String user = "";
		try
		{
			user = createTestAccount(true);
			String password = "123";
			

			UserMgrPage userMgrPage =  navigateToTestPage(user,
				  			   	   password);
			
			//Test execution
			logger.info("Click Change password to update the password of specified user.");
			userMgrPage.clickChangePassword();
			String newPwd = "456";
			ChangePwd changePwd= new ChangePwd(userMgrPage.getWebDriver());
			changePwd.inputOldPwd(password);
			changePwd.inputNewPwd(newPwd);
			changePwd.inputConfirmPwd(newPwd);
			changePwd.clickOK();
		
			//Verification
			//1. Verify the message from alert window.
			String msg = changePwd.getTextFromAlertWindow();
			changePwd.acceptAlertWindow();
			logger.info("Verify the text from alter window.");
			Assert.assertEquals("Verify the text from alter window.",
					            "Change password successfully.",
					            msg);
			//2. Verify the user could sign in with updated password
			LoginPage loginPage = userMgrPage.logout();
			loginPage.loginAs(user, newPwd);
			logger.info("Verify the speicified user sign in successfully.");
			Assert.assertEquals("Verify the speicified user sign in successfully.",
								"Welcome | Business and Security Monitoring",
								loginPage.getTitle());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr1501()
	{
		String user = "";
		try{
			user = createTestAccount(true);
			String password = "123";
			
			//Test execution
			UserMgrPage userMgrPage =  navigateToTestPage(user,
					  			   password);
			
			logger.info("Click Change password to update the password of specified user.");
			userMgrPage.clickChangePassword();
			String newPwd = "456";
			ChangePwd changePwd= new ChangePwd(userMgrPage.getWebDriver());
			changePwd.inputOldPwd("111");
			changePwd.inputNewPwd(newPwd);
			changePwd.inputConfirmPwd(newPwd);
			changePwd.clickOK();
	
			//Verification
			//1. Verify the message from alert window.
			String msg = changePwd.getErrorMsgFromOldPwd();
			Assert.assertEquals("Verify error message.", "Old password does not match.", msg);
		}
		catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr1502()
	{
		String user = "";
		try{
			user = createTestAccount(true);
			String password = "123";
			
			UserMgrPage userMgrPage =  navigateToTestPage(user,
					  			   password);
			//Test execution
			logger.info("Click Change password to update the password of specified user.");
			userMgrPage.clickChangePassword();
			ChangePwd changePwd= new ChangePwd(userMgrPage.getWebDriver());
			changePwd.inputOldPwd(password);
			changePwd.inputNewPwd("");
			changePwd.clickOK();
	
			//Verification
			//1. Verify the message from alert window.
			String msg = changePwd.getErrorMsgFromNewPwd();
			Assert.assertEquals("Verify error message.",
					            "New password should not be empty.", 
					            msg);
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr1503()
	{
		String user = "";
		try{
			user = createTestAccount(true);
			String password = "123";
			
			UserMgrPage userMgrPage =  navigateToTestPage(user,
								   password);
			
			//Test execution
			logger.info("Click Change password to update the password of specified user.");
			userMgrPage.clickChangePassword();
			ChangePwd changePwd= new ChangePwd(userMgrPage.getWebDriver());
			changePwd.inputOldPwd(password);
			changePwd.inputNewPwd("456");
			changePwd.inputNewPwd("789");
			changePwd.clickOK();
	
			//Verification
			//1. Verify the message from alert window.
			String msg = changePwd.getErrorMsgFromConfirmPwd();
			Assert.assertEquals("Verify error message.", "The two passwords you input is not the same.", msg);
		
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr1600()
	{
		String user = "";

		try{
			user=createTestAccount(true);
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
	  			  			  CommonUtil.getPropertyValue("password"));
			
			//Test execution
			logger.info("Select the user " + user +" from the user list" );
			userMgrPage.clickUserFromUserList(user);
			logger.info("Click Update user to update the specified user:" + user);
			userMgrPage.clickUpdateUser();
			
			UserEdit ue = new UserEdit(userMgrPage.getWebDriver());
			ue.setPassword("567");
			ue.setConfirmPassword("456");
			ue.clickOK();
			
			//Verification
			//1. Verify the message from alert window.
			String msg = ue.getTextFromAlertWindow();
			ue.acceptAlertWindow();
			Assert.assertEquals("Verify error message.", "The passwords you entered do not match. Please try again.", msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}

	}
	
	@Test
	public void testUserMgr1700()
	{
		String user="";

		try{
			//Test execution
			user = createTestAccount(true);
			
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			  CommonUtil.getPropertyValue("password"));
			
			logger.info("Select the user " + user +" from the user list" );
			userMgrPage.clickUserFromUserList(user);
			logger.info("Click Update user to update the specified user:" + user);
			userMgrPage.clickUpdateUser();
			UserEdit ue = new UserEdit(userMgrPage.getWebDriver());
			ue.setStatus(false);
			ue.clickOK();
			
			//Verification
			//1. Verify the message from alert window.
			String msg = ue.getTextFromAlertWindow();
			ue.acceptAlertWindow();
			Assert.assertEquals("Verify error message.", "Update User Successfully.", msg);
			//2. Verify the specified user is not active.
			Assert.assertFalse("Verify the specified user " + user +" is inactive", userMgrPage.isUserActiveInUserList(user));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr1800()
	{
		String user = "";
		try{
			user = createTestAccount(true);
			
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			  CommonUtil.getPropertyValue("password"));
			
			//Test execution
			logger.info("Select the user " + user +" from the user list" );
			userMgrPage.clickUserFromUserList(user);
			logger.info("Click Update user to update the specified user:" + user);
			userMgrPage.clickUpdateUser();
			
			UserEdit ue = new UserEdit(userMgrPage.getWebDriver());
			logger.info("Check off the role badmin");
			ue.setRole(new String[]{"ADMIN"});
			logger.info("Check the role user");
			ue.setRole(new String[]{"USER"});
			ue.clickOK();
			logger.info("Accept the alert window");
			ue.acceptAlertWindow();
	
			userMgrPage.clickUserFromUserList(user);
			userMgrPage.clickUpdateUser();
			
			//Verification
			ue = new UserEdit(userMgrPage.getWebDriver());
			//1. Verify the role badmin is unchecked.
			Assert.assertFalse("Verfiy the role badmin is unchecked.", ue.isSpecifiedRoleActive(new String[]{"ADMIN"}));
			//2. Verify the role user is checked.
			Assert.assertTrue("Verify the role user is checked.", ue.isSpecifiedRoleActive(new String[]{"USER"}));
			ue.closeWindow();
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr1900()
	{
		String user = "";
		
		try{
			user = createTestAccount(true);
			
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			  CommonUtil.getPropertyValue("password"));
			
			//Test execution
			logger.info("Select the user " + user +" from the user list" );
			userMgrPage.clickUserFromUserList(user);
			logger.info("Click Update user to update the specified user:" + user);
			userMgrPage.clickUpdateUser();
			
			UserEdit ue = new UserEdit(userMgrPage.getWebDriver());
			logger.info("Update password.");
			ue.setPassword("456");
			ue.setConfirmPassword("456");
			logger.info("Update status");
			ue.setStatus(false);
			logger.info("Check off the role badmin");
			ue.setRole(new String[]{"ADMIN"});
			logger.info("Check the role user");
			ue.setRole(new String[]{"USER"});
			ue.clickOK();
			
			//Verification
			String msg = ue.getTextFromAlertWindow();
			logger.info("Verify the specified user could be updated");
			ue.acceptAlertWindow();
			Assert.assertEquals("Verify the message of alert window.", "Update User Successfully.", msg);
			logger.info("Verify the user's status is set to inactive");
			Assert.assertFalse("Verify the user's status is set to inactive",userMgrPage.isUserActiveInUserList(user));
			userMgrPage.clickUserFromUserList(user);
			userMgrPage.clickUpdateUser();
			logger.info("Verify the role has also been updated.");
			ue = new UserEdit(userMgrPage.getWebDriver());
			Assert.assertFalse("Verfiy the role badmin is unchecked.", ue.isSpecifiedRoleActive(new String[]{"ADMIN"}));
			Assert.assertTrue("Verify the role user is checked.", ue.isSpecifiedRoleActive(new String[]{"USER"}));
			ue.closeWindow();
		}
		catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	@Category(SanityCheck.class)
	public void testUserMgr2000()
	{
		String user = "";
		try
		{
			user = createTestAccount(true);
			
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			  CommonUtil.getPropertyValue("password"));
			
			//Test execution
			logger.info("Select the user " + user +" from the user list" );
			userMgrPage.clickUserFromUserList(user);
			logger.info("Click Update user to update the specified user:" + user);
			userMgrPage.clickUpdateUser();
			
			UserEdit ue = new UserEdit(userMgrPage.getWebDriver());
			logger.info("Update password.");
			ue.setPassword("456");
			ue.setConfirmPassword("456");
			logger.info("Update status");
			ue.setStatus(false);
			logger.info("Check off the role badmin");
			ue.setRole(new String[]{"ADMIN"});
			logger.info("Check the role user");
			ue.setRole(new String[]{"USER"});
			ue.clickCancel();
			
			//Verification
			logger.info("Verify the user's status is stil active");
			Assert.assertTrue("Verify the user's status is stil active", userMgrPage.isUserActiveInUserList(user));
			userMgrPage.clickUserFromUserList(user);
			userMgrPage.clickUpdateUser();
			logger.info("Verify the role list are not updated.");
			ue = new UserEdit(userMgrPage.getWebDriver());
			Assert.assertTrue("Verfiy the role badmin is unchecked.", ue.isSpecifiedRoleActive(new String[]{"ADMIN"}));
			Assert.assertFalse("Verify the role user is checked.", ue.isSpecifiedRoleActive(new String[]{"USER"}));
			ue.closeWindow();
		}
		catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr2100()
	{
		try{

			//Test execution
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			  CommonUtil.getPropertyValue("password"));
			
			logger.info("Click Remove user directly.");
			userMgrPage.clickRemoveUser();
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			
			//Verification
			logger.info("Verify the error message of alert window.");
			Assert.assertEquals("Verify the error message of alert window.", "Please select a record to edit.", msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}

	}
	
	@Test
	public void testUserMgr2200()
	{
		String user = "";
		
		try{
			user = createTestAccount(true);
			String password = "123";
			
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			  CommonUtil.getPropertyValue("password"));
			
			//Test execution
			logger.info("Select the user :" + user);
			userMgrPage.clickUserFromUserList(user);
			logger.info("Click remove user.");
			userMgrPage.clickRemoveUser();
			
			//Verification
			//1. Verify the error message;
			logger.info("Verify the pop-up message");
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the error message;", "Are you sure to remove user?", msg);
			
			//2. Verify the user is removed from user list;
			Assert.assertFalse("Verify the user is removed from user list", userMgrPage.checkUserExistsInUserList(user));
			
			//3. Verify the deleted user could not sign in;
			LoginPage l = userMgrPage.logout();
			l.loginAs(user, password);
			Assert.assertTrue("Verify the error in login page",l.isErrorMsgOutput());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr2300()
	{
		String user = "";

		try{
			user = createTestAccount(false);
			String password = "123";
			
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			  CommonUtil.getPropertyValue("password"));
			
			//Test execution
			logger.info("Select the user :" + user);
			userMgrPage.clickUserFromUserList(user);
			logger.info("Click remove user.");
			userMgrPage.clickRemoveUser();
			
			//Verification
			//1. Verify the error message;
			logger.info("Verify the pop-up message");
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			userMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the error message;", "Are you sure to remove user?", msg);
			
			//2. Verify the user is removed from user list;
			Assert.assertFalse("Verify the user is removed from user list;", userMgrPage.checkUserExistsInUserList(user));

			//3. Verify the deleted user could not sign in;
			LoginPage l = userMgrPage.logout();
			l.loginAs(user, password);
			Assert.assertTrue("Verify the error in login page",l.isErrorMsgOutput());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr2500()
	{
		String user = "";
		try{
			user = createTestAccount(true);
			String password = "123";
			
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			  CommonUtil.getPropertyValue("password"));
			
			//Test execution
			logger.info("Select the user :" + user);
			userMgrPage.clickUserFromUserList(user);
			logger.info("Click remove user.");
			userMgrPage.clickRemoveUser();
			userMgrPage.dissmissAlertWindow();
			
			//Verification
			//1. Verify the user is not removed from user list;
			Assert.assertTrue("Verify the user is removed from user list;", userMgrPage.checkUserExistsInUserList(user));
			//2. Verify the user could still sign in;
			LoginPage l = userMgrPage.logout();
			l.loginAs(user, password);
			Assert.assertEquals("Verify the speicified user sign in successfully.",
								"Welcome | Business and Security Monitoring",
								l.getTitle());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr2700()
	{
		String user = "";
		
		try
		{
			user = createTestAccount(true);
			
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			  CommonUtil.getPropertyValue("password"));
			
			//Verification
			//1. Verify the user should present in user list if input full name;
			userMgrPage.inputUserFilterInfo(user);
			Assert.assertEquals("Verify the user count should be only 1.", 1 ,userMgrPage.getUsersCountInUserList());
			Assert.assertTrue("Verify the user should present in user list",userMgrPage.checkUserExistsInUserList(user));
			
			//2. Verify the user should present in user list if input keywords;
			userMgrPage.inputUserFilterInfo(user.substring(1, user.length()));
			Assert.assertEquals("Verify the user presents in user list.", 1 ,userMgrPage.getUsersCountInUserList());
			Assert.assertTrue("Verify the user should present in user list",userMgrPage.checkUserExistsInUserList(user));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr2800()
	{
		String user = "";
		
		try{
			user = createTestAccount(false);
			
			//Test execution
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
								  CommonUtil.getPropertyValue("password"));

			//Verify the user in inactive user list
			userMgrPage.selectState("Inactive");
			Assert.assertFalse("Verify the inactive user " + user + "in user list.",
						   		userMgrPage.isUserActiveInUserList(user)
							   );
		
			userMgrPage.selectState("Active");
			Assert.assertFalse("Verify the inactive user "+ user + "is not in the list.",
						        userMgrPage.checkUserExistsInUserList(user)
						   		);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr2900()
	{
		String user = "";
		
		try{
			user = createTestAccount(true);
			
			//Test execution
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
	  			  			  	CommonUtil.getPropertyValue("password"));
		
			userMgrPage.inputUserFilterInfo(user.substring(1, user.length()));
			//Verify the user in inactive user list
			userMgrPage.selectState("Active");
			Assert.assertTrue("Verify the inactive user " + user + "in user list.",
						   		userMgrPage.isUserActiveInUserList(user)
						   	  );
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}

	@Test
	public void testUserMgr3100()
	{
		String prefix = "alli";
		try{	
		    
			CommonUtil.createBatchUserDataToDb(prefix, 30);
		    String pattern = "Records from %d to %d";
			String expectedText="";
			int total = CommonUtil.getUsersCountFromDb();
		    
			//Test execution
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
	  			  			  	CommonUtil.getPropertyValue("password"));

			//Verification
			logger.info("Verifiy the default page.");
			Assert.assertFalse("Verify First button is disabled.",userMgrPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled.",userMgrPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled.",userMgrPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled.",userMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 1, 24);
			Assert.assertEquals("Verify the current paging info", userMgrPage.getCurrentPagingInfo(),expectedText);
			
			logger.info("Click Next and verifiy.");
			userMgrPage.clickNavItem("Next");
			logger.info("Verifiy the page.");
			Assert.assertTrue("Verify First button is enabled.",userMgrPage.isNavItemEnabled("First"));
			Assert.assertTrue("Verify Previous button is enabled.",userMgrPage.isNavItemEnabled("Previous"));
			Assert.assertFalse("Verify Next button is disabled.",userMgrPage.isNavItemEnabled("Next"));
			Assert.assertFalse("Verify Last button is disabled.",userMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 25, total);
			Assert.assertEquals("Verify the current paging info", userMgrPage.getCurrentPagingInfo(),expectedText);
			
			logger.info("Click First and verifiy.");
			userMgrPage.clickNavItem("Previous");
			logger.info("Verifiy the page.");
			Assert.assertFalse("Verify First button is disabled.",userMgrPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled.",userMgrPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled.",userMgrPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled.",userMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 1, 24);
			Assert.assertEquals("Verify the current paging info", userMgrPage.getCurrentPagingInfo(),expectedText);
			
			logger.info("Click Last and verifiy.");
			userMgrPage.clickNavItem("Last");
			logger.info("Verifiy the page.");
			Assert.assertTrue("Verify First button is enabled.",userMgrPage.isNavItemEnabled("First"));
			Assert.assertTrue("Verify Previous button is enabled.",userMgrPage.isNavItemEnabled("Previous"));
			Assert.assertFalse("Verify Next button is disabled.",userMgrPage.isNavItemEnabled("Next"));
			Assert.assertFalse("Verify Last button is disabled.",userMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 25, total);
			Assert.assertEquals("Verify the current paging info", userMgrPage.getCurrentPagingInfo(),expectedText);
			
			logger.info("Click First and verifiy.");
			userMgrPage.clickNavItem("First");
			logger.info("Verifiy the page.");
			Assert.assertFalse("Verify First button is disabled.",userMgrPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled.",userMgrPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled.",userMgrPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled.",userMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 1, 24);
			Assert.assertEquals("Verify the current paging info", userMgrPage.getCurrentPagingInfo(),expectedText);
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
			CommonUtil.removeAllUserDataFromDb(prefix);
		}
	}
	
	@Test
	public void testUserMgr3200()
	{
		String user = "";
		
		try{
			user = createTestAccount(true);
			String password = "123";

			UserMgrPage userMgrPage = navigateToTestPage(user, password);
			
			//Test execution
			logger.info("Choose admin from user list");
			userMgrPage.clickUserFromUserList("admin");
			userMgrPage.clickRemoveUser();
			
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			
			//Verification
			//Verify the User Edit Window is present.
			logger.info("Verify the error message of alert window.");
			Assert.assertEquals("Verify the error message of alert window.", "Can not remove user admin.", msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr3300()
	{
		String user = "";
		try
		{
			user = createTestAccount(true);
			String password = "123";
			
			UserMgrPage userMgrPage = navigateToTestPage(user,
		  			  			  password);
			//Test execution
			logger.info("Choose admin from user list");
			userMgrPage.clickUserFromUserList("admin");
			userMgrPage.clickUpdateUser();
			
			String msg = userMgrPage.getTextFromAlertWindow();
			userMgrPage.acceptAlertWindow();
			
			//Verification
			//Verify the User Edit Window is present.
			logger.info("Verify the error message of alert window.");
			Assert.assertEquals("Verify the error message of alert window.", "Only admin user can edit his profile.", msg);
		}
		catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr3400()
	{
		try{
			
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			                         CommonUtil.getPropertyValue("password"));
			
			//Test execution
			userMgrPage.clickUserFromUserList(CommonUtil.getPropertyValue("username"));
			userMgrPage.clickUpdateUser();
			
			UserEdit ue = new UserEdit(userMgrPage.getWebDriver());
			ue.setPassword(CommonUtil.getPropertyValue("password"));
			ue.setConfirmPassword(CommonUtil.getPropertyValue("password"));
			ue.clickOK();
			
			String msg = ue.getTextFromAlertWindow();
			ue.acceptAlertWindow();
			
			//Verification
			//Verify the User Edit Window is present.
			logger.info("Verify the message of alert window.");
			Assert.assertEquals("Verify the message of alert window.", "Update User Successfully.", msg);
		}
		catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testUserMgr3500()
	{
		String user = "";
		try
		{
			user = createTestAccount(true);
			
			//Test execution
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
	  			  			  	  CommonUtil.getPropertyValue("password"));
		
			userMgrPage.dbClickUserFromUserList(user);
		
			//Verification
			//Verify the User Edit Window is present.
			logger.info("Verify the User Edit Window is present.");
			Assert.assertTrue("Verify the User Edit Window is present.", userMgrPage.isWindowPresent("User Info"));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			removeUser(user);
		}
	}
	
	@Test
	public void testUserMgr3600()
	{
		try
		{
			//Test execution
			UserMgrPage userMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
		  			  			  CommonUtil.getPropertyValue("password"));
			
			userMgrPage.clickCreateUser();
			UserEdit ue = new UserEdit(userMgrPage.getWebDriver());
			ue.setUserName("test@!#");
			ue.clickOK();
			
			//Verification
			//Verify the error msg.
			logger.info("Verify the error msg.");
			String msg = ue.getTextFromAlertWindow();
			ue.acceptAlertWindow();
			Assert.assertEquals("Verify the error msg.", "The valid user name only contains numbers and characters.", msg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testUserMgr3700(){
		BrowserTasks br = new BrowserTasks();
		try{			
			br.launchBrowser();
			LoginPage lg = br.getURL();
			lg.loginAs("admin", "123");
			Assert.assertTrue("Verify login failed",lg.isErrorMsgOutput());
		}finally{
			br.closeBrowser();
		}		
	}
	
	@Test
	public void testUserMgr3800(){
		BrowserTasks br = new BrowserTasks();
		try{			
			br.launchBrowser();
			LoginPage lg = br.getURL();
			lg.cancelLogin("admin", "admin");
			Assert.assertEquals("Verify didn't login","Login",br.getPageTitle());
		}finally{
			br.closeBrowser();
		}		
	}
	
	//Utility method for creating an active user.
	private String createTestAccount(boolean isActive)
	{
		String username = CommonUtil.createUserDataToDb("user",isActive);
		//Get user id
		logger.info("Created a new test account " + username + " to Db");
		int userID = Integer.parseInt(username.substring(4, username.length()));
		//Assign admin role to specified user
		int roleID = CommonUtil.getRoleIDFromDb("ADMIN");
		CommonUtil.associateRoleDataWithUserData(userID, roleID);
		
		return username;
	}
	
	//Utility method for user removal.
	public void removeUser(String username)
	{
		logger.info("Remove test account " + username + " and associated data from DB");
		CommonUtil.removeUserDataFromDb(username);
	}
}
