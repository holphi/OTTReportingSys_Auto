package com.nagra.bsm.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;

import junit.framework.Assert;

import com.nagra.bsm.ui.AdminPage;
import com.nagra.bsm.ui.LoginPage;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.RoleEdit;
import com.nagra.bsm.ui.RoleMgrPage;
import com.nagra.bsm.ui.UserEdit;
import com.nagra.bsm.ui.UserMgrPage;

import com.nagra.bsm.tests.categorymarker.SanityCheck;

import com.nagra.bsm.util.CommonUtil;

public class RoleMgrTests extends BaseTests{
	
	@Rule
	public Timeout globalTimeout = new Timeout(300000);
	
	//Utility method for quick navigation to test page.
	private RoleMgrPage navigateToTestPage(String username, String password)
	{
		logger.info("Login in with User:" + username + ", Password:" + password);
		LoginPage loginPage = new LoginPage(CommonUtil.launchTestPortal());
		RptCenterPage rptCenterPage=loginPage.loginAs(username, password);
		
		logger.info("Click Administration to go to Administration tab.");
		AdminPage adminPage = rptCenterPage.goToAdmin();
		
		logger.info("Click Security->Role Manager.");
		return adminPage.goToRoleMgrPage();
	}

	//Remove role
	public void removeRole(String roleName)
	{
		logger.info("Remove test role" + roleName + " and associated data from DB");
		CommonUtil.removeRoleFromDb(roleName);
	}
	
	//Remove role_permission
	public void removeRolePerm(String roleName)
	{
		logger.info("Remove test role" + roleName + " associated permission from DB");
		CommonUtil.removeRolePerm(roleName);
	}
	
	@Before
	public void testSetup(){
		logger.info("============================="+testName.getMethodName()+"==============================");
	}
	
	@Test
	public void testRoleMgr0100()
	{
		String roleName = "role"+CommonUtil.getRandomStr();
		try
		{
			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
		
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			re.checkTreeNode("Report Schedule Manager");
			re.expandTreeNode("Report Manager");
			re.checkTreeNode("View Report Manager");
			re.inputComments(roleName);
			re.clickOK();
			
			//Verification
			Assert.assertEquals("Verify the message in pop-up dialog.", "Create role successfully.", re.getTextFromAlertWindow());
			re.acceptAlertWindow();
			
			roleMgrPage.dbClickRoleFromRoleList(roleName);
			
			re = new RoleEdit(roleMgrPage.getWebDriver());
			Assert.assertTrue("Verify the text in Role Edit window.",re.getRoleName().equals(roleName));
			Assert.assertTrue("The tree node Scheduler Manager is checked", re.isTreeNodeChecked("Report Schedule Manager"));
			re.expandTreeNode("Report Manager");
			Assert.assertTrue("The tree node View Report Manager is checked", re.isTreeNodeChecked("View Report Manager"));
		 }
		 catch(Exception e)
		 {
			CommonUtil.logError(e, testName);
		 }finally
		 {
			CommonUtil.closeBrowser();
			CommonUtil.removeRoleFromDb(roleName);
		 }
	}
	
	@Test
	public void testRoleMgr0200()
	{
		String roleName = "role"+CommonUtil.getRandomStr();
		try
		{
			String[] permissions = new String[]{"Report Schedule Manager", "Report Manager","Administration", "Help"};
			
			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
		
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			for(String p:permissions)
			{
				logger.info("Select permission: " + p);
				re.checkTreeNode(p);
			}
			re.inputComments(roleName);
			re.clickOK();
			
			//Verification
			Assert.assertEquals("Verify the message in pop-up dialog.", "Create role successfully.", re.getTextFromAlertWindow());
			re.acceptAlertWindow();
			roleMgrPage.dbClickRoleFromRoleList(roleName);
			re = new RoleEdit(roleMgrPage.getWebDriver());
			Assert.assertTrue("Verify the text in Role Edit window.",re.getRoleName().equals(roleName));
			for(String p:permissions)
			{
				Assert.assertTrue(String.format("Verify the permission %s is checked.", p), re.isTreeNodeChecked(p));
			}
			Assert.assertTrue("Verfify the comments is " +roleName,re.getComments().equals(roleName));
		 }
		 catch(Exception e)
		 {
			CommonUtil.logError(e, testName);
		 }finally
		 {
			CommonUtil.closeBrowser();
			CommonUtil.removeRoleFromDb(roleName);
		 }
	}
	
	@Test
	public void testRoleMgr0300()
	{
		String roleName = "role"+CommonUtil.getRandomStr();
		try
		{
			String[] permissions = new String[]{"Report Schedule Manager", "Report Manager","Administration", "Help"};
			
			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			
			re.inputComments(roleName);
			re.clickOK();
			
			//Verification
			Assert.assertEquals("Verify the message in pop-up dialog.", "Create role successfully.", re.getTextFromAlertWindow());
			re.acceptAlertWindow();
			roleMgrPage.dbClickRoleFromRoleList(roleName);
			re = new RoleEdit(roleMgrPage.getWebDriver());
			Assert.assertTrue("Verify the text in Role Edit window.",re.getRoleName().equals(roleName));
			for(String p:permissions)
			{
				Assert.assertFalse(String.format("Verify the permission %s is not checked.", p), re.isTreeNodeChecked(p));
			}
			Assert.assertTrue("Verfify the comments is " +roleName,re.getComments().equals(roleName));
		 }
		 catch(Exception e)
		 {
			CommonUtil.logError(e, testName);
		 }finally
		 {
			CommonUtil.closeBrowser();
			CommonUtil.removeRoleFromDb(roleName);
		 }
	}
	
	@Test
	@Category(SanityCheck.class)
	public void testRoleMgr0400()
	{
		String roleName = "role"+CommonUtil.getRandomStr();
		try
		{

			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			
			re.expandTreeNode("Report Schedule Manager");
			re.checkTreeNode("View Report Schedule Manager");
			
			re.expandTreeNode("Report Manager");
			re.checkTreeNode("View Report Manager");
			
			re.inputComments(roleName);
			re.clickOK();
			
			//Verification
			Assert.assertEquals("Verify the message in pop-up dialog.", "Create role successfully.", re.getTextFromAlertWindow());
			re.acceptAlertWindow();
			roleMgrPage.dbClickRoleFromRoleList(roleName);
			re = new RoleEdit(roleMgrPage.getWebDriver());
			Assert.assertTrue("Verify the text in Role Edit window.",re.getRoleName().equals(roleName));
			re.expandTreeNode("Report Schedule Manager");
			re.expandTreeNode("Report Manager");
			Assert.assertTrue("Verify the permission View Report Scheduler Manager is checked.", re.isTreeNodeChecked("View Report Schedule Manager"));
			Assert.assertTrue("Verify the permission View Report Manager is checked.", re.isTreeNodeChecked("View Report Manager"));
			Assert.assertTrue("Verfify the comments is " +roleName,re.getComments().equals(roleName));
		 }
		 catch(Exception e)
		 {
			CommonUtil.logError(e, testName);
		 }finally
		 {
			CommonUtil.closeBrowser();
			CommonUtil.removeRoleFromDb(roleName);
		 }
	}
	
	@Test
	public void testRoleMgr0500()
	{
		String roleName = CommonUtil.createRoleToDb("role");
		try
		{
			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			re.clickOK();
			
			//Verification
			String msg = re.getTextFromAlertWindow();
			re.acceptAlertWindow();
			Assert.assertEquals("Verify the message in pop-up dialog.", "The role name you entered exists,please choose another one.", msg);
		 }
		 catch(Exception e)
		 {
			CommonUtil.logError(e, testName);
		 }finally
		 {
			CommonUtil.closeBrowser();
			CommonUtil.removeRoleFromDb(roleName);
		 }
	}
	
	@Test
	public void testRoleMgr0600()
	{
		String roleName = "role" + CommonUtil.getRandomStr();
		String userName = "user" + CommonUtil.getRandomStr();
		String password = "123";
		try
		{
			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password")
												 		 );
			roleMgrPage.clickCreateRole();
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			re.checkTreeNode("Report Schedule Manager");
			re.checkTreeNode("Administration");
			re.clickOK();
			re.acceptAlertWindow();
			
			//Create new user and assign the role just created.
			UserMgrPage userMgrPage = roleMgrPage.goToUserMgrPage();
			userMgrPage.clickCreateUser();
			UserEdit ue = new UserEdit(userMgrPage.getWebDriver());
			ue.inputUserData(userName, password, password, true, new String[]{roleName});
			ue.clickOK();
			ue.acceptAlertWindow();
			userMgrPage.logout();
			
			//Login as new created user
			LoginPage loginPage = new LoginPage(userMgrPage.getWebDriver());
			RptCenterPage rptCenterPage = loginPage.loginAs(userName, password);
			
			//Verification
			Assert.assertTrue("Verify the tab Admin is present.", rptCenterPage.isMenuItemPresent("Administration"));
			Assert.assertTrue("Verify the tab Schedule manager is present.", rptCenterPage.isMenuItemPresent("Report Schedule Manager"));
			Assert.assertFalse("Verify the tab Report Manager is not present.", rptCenterPage.isMenuItemPresent("Report Manager"));
			Assert.assertFalse("Verify the tab Help is not present.", rptCenterPage.isMenuItemPresent("Help"));
		}
		 catch(Exception e)
		 {
			CommonUtil.logError(e, testName);
		 }finally
		 {
			CommonUtil.closeBrowser();
			CommonUtil.removeRoleFromDb(roleName);
			CommonUtil.removeUserDataFromDb(userName);
		 }
	}
	
	@Test
	public void testRoleMgr0700()
	{
		String roleName = "role"+CommonUtil.getRandomStr();
		try
		{
			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			
			re.checkTreeNode("Report Schedule Manager");
			re.checkTreeNode("Help");
			re.inputComments(roleName);
			re.clickCancel();
			
			//Verification
			Assert.assertFalse(String.format("Verify the role %s is not in role list", roleName),roleMgrPage.checkRoleExistsInUserList(roleName));
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
	public void testRoleMgr0800()
	{
		String roleName = "role"+CommonUtil.getRandomStr();
		try
		{
			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			
			re.checkTreeNode("Report Schedule Manager");
			re.clickOK();
			re.acceptAlertWindow();
			
			roleMgrPage.dbClickRoleFromRoleList(roleName);
			re = new RoleEdit(roleMgrPage.getWebDriver());
			re.expandTreeNode("Report Schedule Manager");
			String[] permissions = new String[]{"View Report Schedule Manager","Create Report Event","Update Report Event", "Remove Report Event"};
			for(String p:permissions)
			{
				Assert.assertTrue(String.format("Verify the permission %s is checked.", p), re.isTreeNodeChecked(p));
			}
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
	public void testRoleMgr0900()
	{
		try
		{
			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			
			re.expandTreeNode("Report Schedule Manager");
			re.checkTreeNode("View Report Schedule Manager");
			Assert.assertTrue("Verify the parent permission Scheduler Manager is checked", re.isTreeNodeChecked("View Report Schedule Manager"));
			re.unCheckTreeNode("Report Schedule Manager");
			re.checkTreeNode("Report Schedule Manager");
			re.unCheckTreeNode("Report Schedule Manager");
			Assert.assertFalse("Verify the sub permission View Scheduler Manager is not checked", re.isTreeNodeChecked("View Report Schedule Manager"));
			
			CommonUtil.sleep(10);
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
	public void testRoleMgr1000()
	{
		try
		{
			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			
			re.expandTreeNode("Report Schedule Manager");
			re.checkTreeNode("View Report Schedule Manager");
			Assert.assertTrue("Verify the parent permission Scheduler Manager is checked", re.isTreeNodeChecked("View Report Schedule Manager"));
			Assert.assertTrue("Verify the permission Scheduler Manager is checked", re.isTreeNodeChecked("View Report Schedule Manager"));
			String[] unCheckedPermissions = new String[]{"Update Report Event", "Remove Report Event", "Create Report Event"};
			for(String p:unCheckedPermissions)
			{
				Assert.assertFalse(String.format("Verify the permission %s is checked.", p), re.isTreeNodeChecked(p));
			}
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
	public void testRoleMgr1100()
	{
		try
		{
			//Test execution
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
												 		 CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			
			re.expandTreeNode("Report Schedule Manager");
			re.checkTreeNode("View Report Schedule Manager");
			Assert.assertTrue("Verify the parent permission Scheduler Manager is checked", re.isTreeNodeChecked("View Report Schedule Manager"));
			Assert.assertTrue("Verify the permission Scheduler Manager is checked", re.isTreeNodeChecked("View Report Schedule Manager"));
			String[] unCheckedPermissions = new String[]{"Update Report Event", "Remove Report Event", "Create Report Event"};
			for(String p:unCheckedPermissions)
			{
				Assert.assertFalse(String.format("Verify the permission %s is checked.", p), re.isTreeNodeChecked(p));
			}
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
	public void testRoleMgr1200()
	{
		try
		{
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickUpdateRole();
			
			String msg = roleMgrPage.getTextFromAlertWindow();
			roleMgrPage.acceptAlertWindow();
			
			Assert.assertEquals("Verify message text on alert window", "Please select a record to edit.", msg);
			
		}catch(Exception e)
		 {
			CommonUtil.logError(e, testName);
		 }finally
		 {
			CommonUtil.closeBrowser();
		 }
	}
	
	@Test
	public void testRoleMgr1300()
	{
		String roleNameNew="";
		try
		{
			String roleName = CommonUtil.createRoleToDb("role");
			roleNameNew = "role"+CommonUtil.getRandomStr();
			
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickRoleFromRoleList(roleName);
			roleMgrPage.clickUpdateRole();			
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			
			re.setRoleName(roleNameNew);
			re.clickOK();
			
			//Verification
			//1.Verify message from alert window
			String msg= re.getTextFromAlertWindow();
			re.acceptAlertWindow();
			Assert.assertEquals("Verify message on alert window", "Update role successfully.", msg);
		
			//2.Verify role updated list on Role Manager page
			Assert.assertTrue("Verify role updated list on page", roleMgrPage.checkRoleExistsInUserList(roleNameNew));
			
		}
		catch(Exception e)
		 {
			CommonUtil.logError(e, testName);
		 }finally
		 {
			CommonUtil.closeBrowser();
			removeRole(roleNameNew);
		 }
		
	}
	
	@Test
	public void testRoleMgr1400()
	{
		String roleNameExist="";
		String roleName="";
		try
		{
			roleNameExist = CommonUtil.createRoleToDb("role");
			roleName = CommonUtil.createRoleToDb("role");
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			
			roleMgrPage.clickRoleFromRoleList(roleName);
			roleMgrPage.clickUpdateRole();
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			
			re.setRoleName(roleNameExist);
			re.clickOK();
			
			//Verification
			//Verify message on alert window
			String msg = re.getTextFromAlertWindow();
			re.acceptAlertWindow();
			Assert.assertEquals("Verify message on alert window", "The role name you entered exists,please choose another one.", msg);
			
		}
		catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
			removeRole(roleName);
			removeRole(roleNameExist);
		}
	}
	
	@Test
	@Category(SanityCheck.class)
	public void testRoleMgr1500()
	{
		String roleName="";
		try
		{
			//Create a role with Schedule Manager permission
			String pUnCheck = "Report Schedule Manager";
			String pCheck = "Help";
			roleName = CommonUtil.createRoleWithPerm("role", pUnCheck);
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			
			roleMgrPage.clickRoleFromRoleList(roleName);
			roleMgrPage.clickUpdateRole();
			
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.unCheckTreeNode(pUnCheck);
			re.checkTreeNode(pCheck);
			re.clickOK();
			
			//Verification
			//1.Verify message on alert window.
			String msg = re.getTextFromAlertWindow();
			re.acceptAlertWindow();
			Assert.assertEquals("Verify message on alert window:", "Update role successfully.", msg);
			//2.Verify permission 
			roleMgrPage.dbClickRoleFromRoleList(roleName);
			re = new RoleEdit(roleMgrPage.getWebDriver());
			Assert.assertTrue("Verify permission" + pCheck + "is checked:", re.isTreeNodeChecked(pCheck));
			Assert.assertFalse("Verify permission" + pUnCheck + "is unchecked:", re.isTreeNodeChecked(pUnCheck));
			//re.clickCancel();
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
			removeRolePerm(roleName);
			removeRole(roleName);
			
		}
	}
	@Test
	public void testRoleMgr1600()
	{
		String roleName = "";
		String commentsNew = "Update comments";
		try
		{
			roleName = CommonUtil.createRoleToDb("role");
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			
			roleMgrPage.clickRoleFromRoleList(roleName);
			roleMgrPage.clickUpdateRole();
			
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.setComments(commentsNew);
			re.clickOK();
			
			//Verification
			//1.Verify message on alert window.
			String msg = re.getTextFromAlertWindow();
			re.acceptAlertWindow();
			Assert.assertEquals("Verify message on alert window", "Update role successfully.", msg);
			//2.Verify comment edited
			roleMgrPage.dbClickRoleFromRoleList(roleName);
			re = new RoleEdit(roleMgrPage.getWebDriver());
			Assert.assertTrue("Verify comments is:" + commentsNew, re.getComments().equals(commentsNew));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
			removeRole(roleName);
		}
	}
	
	@Test
	public void testRoleMgr1700()
	{
		String roleName = "";
		String roleNameNew = "";
		try
		{
			roleName = CommonUtil.createRoleToDb("role");
			roleNameNew = "role" +CommonUtil.getRandomStr();
			
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickRoleFromRoleList(roleName);
			roleMgrPage.clickUpdateRole();
			
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.setRoleName(roleNameNew);
			re.clickCancel();
			
			//Verification
			//Role is not updated
			Assert.assertTrue("Role" + roleName + "is not updated.", roleMgrPage.checkRoleExistsInUserList(roleName));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
			removeRole(roleName);
		}
	}
	
	@Test
	public void testRoleMgr1900()
	{
		String roleName = "";
		try
		{
			roleName = CommonUtil.createRoleToDb("role");
			
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickRoleFromRoleList(roleName);
			roleMgrPage.clickRemoveRole();
			
			//Verification
			//1.Verify message on alert window
			String msg = roleMgrPage.getTextFromAlertWindow();
			roleMgrPage.dissmissAlertWindow();
			Assert.assertEquals("Verify message on alert window.", "Are you sure to remove Role?", msg);
			
			//2.Verify role is not removed
			Assert.assertTrue("Role" + roleName + "is not removed.", roleMgrPage.checkRoleExistsInUserList(roleName));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally 
		{
			CommonUtil.closeBrowser();
			removeRole(roleName);
		}
	}
	
	@Test
	@Category(SanityCheck.class)
	public void testRoleMgr2000()
	{
		String roleName = "";
		try
		{
			roleName = CommonUtil.createRoleToDb("role");
			
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickRoleFromRoleList(roleName);
			roleMgrPage.clickRemoveRole();
			
			//Verification
			//1.Verify message on alert window
			String msg = roleMgrPage.getTextFromAlertWindow();
			roleMgrPage.acceptAlertWindow();
			String msg1 = roleMgrPage.getTextFromAlertWindow();
			roleMgrPage.acceptAlertWindow(); 
			Assert.assertEquals("Verify message on alert window.", "Are you sure to remove Role?", msg);
			Assert.assertEquals("Verify message on alert window.", "Remove role successfully.", msg1);
			
			//2.Verify role is not removed
			Assert.assertFalse("Role" + roleName + "is removed.", roleMgrPage.checkRoleExistsInUserList(roleName));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally 
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRoleMgr2100()
	{
		String roleName = "";
		String userName = "usertest";
		try
		{
			roleName = CommonUtil.createRoleToDb("role");
			
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			UserMgrPage userMgrPage = roleMgrPage.goToUserMgrPage();
			userMgrPage.clickCreateUser();
			
			UserEdit ue = new UserEdit(userMgrPage.getWebDriver());
			ue.inputUserData(userName, "123", "123", true, new String[]{roleName});
			ue.clickOK();
			ue.acceptAlertWindow();
			
			roleMgrPage = userMgrPage.goToRoleMgrPage();
			roleMgrPage.clickRoleFromRoleList(roleName);
			roleMgrPage.clickRemoveRole();
			
			//Verification
			//1.Verify message on alert window
			String msg = roleMgrPage.getTextFromAlertWindow();
			roleMgrPage.acceptAlertWindow();
			String msg1 = roleMgrPage.getTextFromAlertWindow();
			roleMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify message on alert window", "Some users has been assigned to this role,still remove Role?", msg);
			Assert.assertEquals("Verify message on alert window", "Remove role successfully.", msg1);
			//2.Verify role is not on the list
			Assert.assertFalse("Verify role " + roleName + " is not on the list." , roleMgrPage.checkRoleExistsInUserList(roleName));
			//3.Verify user login successfully
			LoginPage loginPage = roleMgrPage.logout();
			loginPage.loginAs(userName, "123");
			Assert.assertEquals("Verify the speicified user sign in successfully.",
					"Welcome | Business and Security Monitoring",
					loginPage.getTitle());
		}catch (Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.removeUserDataFromDb(userName);
		
		}
		
	}
	
	@Test
	public void testRoleMgr2101()
	{
		String roleName = "";
		String userName = "usertest";
		try
		{
			roleName = CommonUtil.createRoleToDb("role");
			
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			UserMgrPage userMgrPage = roleMgrPage.goToUserMgrPage();
			userMgrPage.clickCreateUser();
			
			UserEdit ue = new UserEdit(userMgrPage.getWebDriver());
			ue.inputUserData(userName, "123", "123", true, new String[]{roleName});
			ue.clickOK();
			ue.acceptAlertWindow();
			
			roleMgrPage = userMgrPage.goToRoleMgrPage();
			roleMgrPage.clickRoleFromRoleList(roleName);
			roleMgrPage.clickRemoveRole();
			
			//Verification
			//1.Verify message on alert window
			String msg = roleMgrPage.getTextFromAlertWindow();
			roleMgrPage.dissmissAlertWindow();
			Assert.assertEquals("Verify message on alert window", "Some users has been assigned to this role,still remove Role?", msg);
			//2.Verify role is not on the list
			Assert.assertTrue("Verify role " + roleName + " is still on the list." , roleMgrPage.checkRoleExistsInUserList(roleName));
			
		}catch (Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.removeRoleFromDb(roleName);
			CommonUtil.removeUserDataFromDb(userName);
		
		}
	}

	@Test
	public void testRoleMgr2200()
	{
		String roleName = "";
		try
		{
			roleName = CommonUtil.createRoleToDb("role");
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			roleMgrPage.selectRole(roleName);
			
			//Verification
			//Verify only roleName lists on page.
			Assert.assertTrue("Verify only " + roleName + " in the role list.", roleMgrPage.checkRoleExistsInUserList(roleName));
			
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
			removeRole(roleName);
		}
	}
	@Test
	public void testRoleMgr2300()
	{
		String prefix = "role";
		try
		{
			CommonUtil.createBatchRoleDataToDb(prefix, 30);
			String pattern = "Records from %d to %d";
			String expectedText = "";
			
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			int total = CommonUtil.getRoleCountFromDB()-1;
			//Verification 
			logger.info("Verify default page");
			Assert.assertFalse("Verify First button is disabled.", roleMgrPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled", roleMgrPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled", roleMgrPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled", roleMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern,1,24);
			Assert.assertEquals("Verify current page info", expectedText, roleMgrPage.getCurrentPagingInfo());
			
			logger.info("Click Next and verify");
			roleMgrPage.clickNavItem("Next");
			Assert.assertTrue("Verify First button is enabled.", roleMgrPage.isNavItemEnabled("First"));
			Assert.assertTrue("Verify Previous button is enabled", roleMgrPage.isNavItemEnabled("Previous"));
			Assert.assertFalse("Verify Next button is disabled", roleMgrPage.isNavItemEnabled("Next"));
			Assert.assertFalse("Verify Last button is disabled", roleMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 25, total);
			Assert.assertEquals("Verify current page info", expectedText, roleMgrPage.getCurrentPagingInfo());
			
			logger.info("Click First and verify");
			roleMgrPage.clickNavItem("First");
			Assert.assertFalse("Verify First button is disabled.", roleMgrPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled", roleMgrPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled", roleMgrPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled", roleMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern,1,24);
			Assert.assertEquals("Verify current page info", expectedText, roleMgrPage.getCurrentPagingInfo());
			
			logger.info("Click Last and verify");
			roleMgrPage.clickNavItem("Last");
			Assert.assertTrue("Verify First button is enabled.", roleMgrPage.isNavItemEnabled("First"));
			Assert.assertTrue("Verify Previous button is enabled", roleMgrPage.isNavItemEnabled("Previous"));
			Assert.assertFalse("Verify Next button is disabled", roleMgrPage.isNavItemEnabled("Next"));
			Assert.assertFalse("Verify Last button is disabled", roleMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 25, total);
			Assert.assertEquals("Verify current page info", expectedText, roleMgrPage.getCurrentPagingInfo());
			
			logger.info("Click Previous and verify");
			roleMgrPage.clickNavItem("Previous");
			Assert.assertFalse("Verify First button is disabled.", roleMgrPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled", roleMgrPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled", roleMgrPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled", roleMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern,1,24);
			Assert.assertEquals("Verify current page info", expectedText, roleMgrPage.getCurrentPagingInfo() );
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.removeAllRoleDataFromDb(prefix);
		}
	}
	
	@Test
	public void testRoleMgr2400()
	{
		try
		{
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickRoleFromRoleList("ADMIN");
			roleMgrPage.clickRemoveRole();
			
			//Verification
			//1.Verify message on alert window
			String msg = roleMgrPage.getTextFromAlertWindow();
			roleMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify message on alert window.", "Can not remove role admin.", msg);
			
			//2.Verify role admin is still on the role list.
			Assert.assertTrue("Verify role admin is still on role list.", roleMgrPage.checkRoleExistsInUserList("ADMIN"));
		}
		catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
		}
	}
	@Test
	public void testRoleMgr2500()
	{
		try
		{
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickRoleFromRoleList("ADMIN");
			roleMgrPage.clickUpdateRole();
			
			//Verification
			//1.Verify message on alert window
			String msg = roleMgrPage.getTextFromAlertWindow();
			roleMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify message on alert window.", "Can not update role admin.", msg);
			
		}
		catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
		}
	}
	@Test
	public void testRoleMgr2600()
	{
		String roleName = "";
		try
		{
			roleName = CommonUtil.createRoleToDb("role");
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			roleMgrPage.dbClickRoleFromRoleList(roleName);
			
			RoleEdit re = new RoleEdit (roleMgrPage.getWebDriver());
			Assert.assertTrue("Verify text in rolename input box", re.getRoleName().equals(roleName));
			Assert.assertFalse("Verify permission of " + roleName , re.isTreeNodeChecked("Report Schedule Manager"));
			Assert.assertFalse("Verify permission of " + roleName , re.isTreeNodeChecked("Report Manager"));
			Assert.assertFalse("Verify permission of " + roleName , re.isTreeNodeChecked("Administration"));
			Assert.assertFalse("Verify permission of " + roleName , re.isTreeNodeChecked("Help"));
			Assert.assertTrue("Verify text in comments box", re.getComments().equals(roleName));
			
			re.closeWindow();
		}
		catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
			removeRole(roleName);
		}
	}
	
	@Test
	public void testRoleMgr2700()
	{
		try
		{
			RoleMgrPage roleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),CommonUtil.getPropertyValue("password"));
			roleMgrPage.clickCreateRole();
			
			RoleEdit re = new RoleEdit (roleMgrPage.getWebDriver());
			re.inputRoleName("role27#");
			re.checkTreeNode("Report Schedule Manager");
			re.expandTreeNode("Report Manager");
			re.checkTreeNode("View Report Manager");
			re.inputComments("role27#");
			re.clickOK();
			
			//Verification
			//Verify message on alert window
			String msg = re.getTextFromAlertWindow();
			re.acceptAlertWindow();
			Assert.assertEquals("Verify message on alert window", "The valid role name only contains numbers and letters.", msg);
			re.closeWindow();
		
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}
		finally
		{
			CommonUtil.closeBrowser();
		}
	}


	
}
