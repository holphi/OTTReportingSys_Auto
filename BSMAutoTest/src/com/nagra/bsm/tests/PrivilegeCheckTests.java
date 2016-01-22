/**
 * 
 */
package com.nagra.bsm.tests;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.openqa.selenium.WebElement;

import com.nagra.bsm.tasks.AdministrationTasks;
import com.nagra.bsm.tasks.BrowserTasks;
import com.nagra.bsm.tasks.ExtraScheduleMgrTasks;
import com.nagra.bsm.tasks.ExtractionMgrTasks;
import com.nagra.bsm.tasks.LoginTasks;
import com.nagra.bsm.tasks.PrivilegeTasks;
import com.nagra.bsm.tasks.RoleMgrTasks;
import com.nagra.bsm.tasks.RptMgrTasks;
import com.nagra.bsm.tasks.RptScheduleMgrTasks;
import com.nagra.bsm.tasks.UserMgrTasks;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.RptFormat;
import com.nagra.bsm.util.CommonUtil;

/**
 * @author tetang
 * 
 */
public class PrivilegeCheckTests extends BaseTests {
	private static String testRptDataDir = CommonUtil.getCurrDir()
			+ "\\testdata\\RptTemplate\\";
	private static String testTfmDataDir = CommonUtil.getCurrDir()+"\\testdata\\Transform\\";
	StringBuffer roleName = new StringBuffer("Rol");
	StringBuffer userName = new StringBuffer("Usr");
	String pwd = "admin";
	BrowserTasks br = new BrowserTasks();
	LoginTasks lg = new LoginTasks();
	PrivilegeTasks privilegeTasks = new PrivilegeTasks();
	RptCenterPage rptCenterPg;

	@Rule
	public Timeout globalTimeout = new Timeout(900000);

	@BeforeClass
	public static void suiteSetup(){
		BrowserTasks br = new BrowserTasks(); 
		br.launchBrowser();
		//open login page
		LoginTasks lg = new LoginTasks();
		lg.loginPage = br.getURL();
		//login
		RptCenterPage rptCenterPg;
		rptCenterPg = lg.login();
		RptMgrTasks rptMgr = new RptMgrTasks();
		rptMgr.gotoRptMgr(rptCenterPg);
		String rpt = "SDP_NMP.rptdesign";
		if(!rptMgr.isRptExisted(rpt)){
			rptMgr.addRpt(testRptDataDir, "SDP_Base.rptlibrary");
			rptMgr.addRpt(testRptDataDir, rpt);
		}
		rptMgr.setRptPublic(rpt);
		br.closeBrowser();
		br.launchBrowser();
		lg.loginPage = br.getURL();
		//login		
		rptCenterPg = lg.login();
		ExtractionMgrTasks extMgr = new ExtractionMgrTasks();
		extMgr.gotoExtractionManager(rptCenterPg);
		String tfm = "TransformOraToOra.ktr";		
		if(!extMgr.isTfmExisted(tfm)){
				extMgr.addTransform(testTfmDataDir, tfm);
		}
		extMgr.setTfmToPublic(tfm);		
		br.closeBrowser();	
		CommonUtil.syncServerTimeWithClient();
	}
	
	@Before
	public void testSetup() {
		String testcaseName = testName.getMethodName();
		logger.info(String
				.format("\n=============================%s==============================",
						testcaseName));
		roleName.append(testcaseName.substring("SinglePrivilegeTests".length()));
		String role = roleName.toString();
		privilegeTasks.insertRole(role, testcaseName);
		userName.append(testcaseName.substring("SinglePrivilegeTests".length()));
		String user = userName.toString();
		privilegeTasks.insertUser(user, pwd, true);
		privilegeTasks.grantRolesToUser(user, role);
		br.launchBrowser();
		// open login page
		lg.loginPage = br.getURL();
	}

	@After
	public void testTeardown() {
		br.closeBrowser();
		logger.info("Close browaer");
		privilegeTasks.removeUser(userName.toString());
		privilegeTasks.removeRole(roleName.toString());
	}

	@Test
	public void SinglePrivilegeTests0100() {
		String permission = "View Report Schedule Manager";
		String[] expectTabs = { "Home", "Report Schedule Manager" };
		String[] expectBtns = { "Cancel" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// login
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RptScheduleMgrTasks rptSchTasks = new RptScheduleMgrTasks();
		rptSchTasks.gotoRptScheduleManager(rptCenterPg);
		rptSchTasks.openNewEventWindow();
		CommonUtil.sleep(3);
		List<WebElement> actualDisplayedBtns = rptSchTasks
				.getDisplayedBtnsOnEventWin();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualDisplayedBtns));
	}

	@Test
	public void SinglePrivilegeTests0200() {
		String permission = "Create Report Event";
		String[] expectTabs = { "Home", "Report Schedule Manager" };
		String[] expectBtns = { "Save", "Cancel" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// login
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RptScheduleMgrTasks rptSchTasks = new RptScheduleMgrTasks();
		rptSchTasks.gotoRptScheduleManager(rptCenterPg);
		rptSchTasks.openNewEventWindow();
		CommonUtil.sleep(3);
		List<WebElement> actualDisplayedBtns = rptSchTasks
				.getDisplayedBtnsOnEventWin();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualDisplayedBtns));
	}

	@Test
	public void SinglePrivilegeTests0300() {
		String permission = "Update Report Event";
		String[] expectTabs = { "Home", "Report Schedule Manager" };
		String[] expectBtns = { "Save", "Cancel" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// create a event for test
		CommonUtil.syncServerTimeWithClient();
		rptCenterPg = lg.login("admin", "admin");
		RptScheduleMgrTasks rptSchTasks = new RptScheduleMgrTasks();
		rptSchTasks.gotoRptScheduleManager(rptCenterPg);
		rptSchTasks.createEvent("PTest0300", RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		try {
			br.closeBrowser();
			// Verify
			br.launchBrowser();
			lg.loginPage = br.getURL();
			rptCenterPg = lg.login(user, pwd);
			List<WebElement> actualTabs = privilegeTasks
					.getMainTabs(rptCenterPg);
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectTabs, actualTabs));
			rptSchTasks.gotoRptScheduleManager(rptCenterPg);
			rptSchTasks.openEvent("PTest0300");
			CommonUtil.sleep(3);
			List<WebElement> actualDisplayedBtns = rptSchTasks
					.getDisplayedBtnsOnEventWin();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns, actualDisplayedBtns));
		} finally {
			RptScheduleMgrTasks.rmRptEventFromBackend("PTest0300");
		}

	}

	@Test
	public void SinglePrivilegeTests0400() {
		String permission = "Remove Report Event";
		String[] expectBtns = { "Cancel", "Delete" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// create a event for test
		CommonUtil.syncServerTimeWithClient();
		rptCenterPg = lg.login("admin", "admin");
		RptScheduleMgrTasks rptSchTasks = new RptScheduleMgrTasks();
		rptSchTasks.gotoRptScheduleManager(rptCenterPg);
		rptSchTasks.openNewEventWindow();
		rptSchTasks.createEvent("PTest0400", RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		try {
			br.closeBrowser();
			// Verify
			br.launchBrowser();
			lg.loginPage = br.getURL();
			rptCenterPg = lg.login(user, pwd);
			rptSchTasks.gotoRptScheduleManager(rptCenterPg);
			rptSchTasks.openEvent("PTest0400");
			CommonUtil.sleep(3);
			List<WebElement> actualDisplayedBtns = rptSchTasks
					.getDisplayedBtnsOnEventWin();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns, actualDisplayedBtns));
		} finally {
			RptScheduleMgrTasks.rmRptEventFromBackend("PTest0400");
		}
	}

	@Test
	public void SinglePrivilegeTests0500() {
		String permission = "View Log Viewer";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectNodes = { "Log", "Log Viewer" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		AdministrationTasks adminTasks = new AdministrationTasks();
		adminTasks.gotoAdministration(rptCenterPg);
		List<WebElement> actualDisplayedNodes = adminTasks
				.getDisplayedTreeNodes();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectNodes, actualDisplayedNodes));
	}

	@Test
	public void SinglePrivilegeTests0600() {
		String permission = "View Report Manager";
		String[] expectTabs = { "Home", "Report Manager" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RptMgrTasks rptTasks = new RptMgrTasks();
		rptTasks.gotoRptMgr(rptCenterPg);
		List<WebElement> actualBtns = rptTasks.getVisiableBtns();
		Assert.assertTrue(actualBtns.isEmpty());
	}

	@Test
	public void SinglePrivilegeTests0700() {
		String permission = "Add Report";
		String[] expectTabs = { "Home", "Report Manager" };
		String[] expectBtns = { "Add Report Template" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RptMgrTasks rptTasks = new RptMgrTasks();
		rptTasks.gotoRptMgr(rptCenterPg);
		List<WebElement> actualBtns = rptTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests0800() {
		String permission = "Download Report";
		String[] expectTabs = { "Home", "Report Manager" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
	}

	@Test
	public void SinglePrivilegeTests0900() {
		String permission = "Update Report";
		String[] expectTabs = { "Home", "Report Manager" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
	}

	@Test
	public void SinglePrivilegeTests1000() {
		String permission = "Generate Report";
		String[] expectTabs = { "Home", "Report Manager" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
	}

	@Test
	public void SinglePrivilegeTests1100() {
		String permission = "View Generated Files";
		String[] expectTabs = { "Home", "Report Manager" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
	}

	@Test
	public void SinglePrivilegeTests1200() {
		String permission = "View User Manager";
		String[] expectTabs = { "Home", "Administration" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		UserMgrTasks usrTasks = new UserMgrTasks();
		usrTasks.gotoAdministration(rptCenterPg);
		usrTasks.gotoUserMgr();
		List<WebElement> actualBtns = usrTasks.getVisiableBtns();
		Assert.assertTrue(actualBtns.isEmpty());
	}

	@Test
	public void SinglePrivilegeTests1300() {
		String permission = "Create User";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectBtns = { "Create User" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		UserMgrTasks usrTasks = new UserMgrTasks();
		usrTasks.gotoAdministration(rptCenterPg);
		usrTasks.gotoUserMgr();
		List<WebElement> actualBtns = usrTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests1400() {
		String permission = "Update User";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectBtns = { "Update User" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		UserMgrTasks usrTasks = new UserMgrTasks();
		usrTasks.gotoAdministration(rptCenterPg);
		usrTasks.gotoUserMgr();
		List<WebElement> actualBtns = usrTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests1500() {
		String permission = "Remove User";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectBtns = { "Remove User" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		UserMgrTasks usrTasks = new UserMgrTasks();
		usrTasks.gotoAdministration(rptCenterPg);
		usrTasks.gotoUserMgr();
		List<WebElement> actualBtns = usrTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests1600() {
		String permission = "View Role Manager";
		String[] expectTabs = { "Home", "Administration" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RoleMgrTasks rolTasks = new RoleMgrTasks();
		rolTasks.gotoAdministration(rptCenterPg);
		rolTasks.gotoRoleMgr();
		List<WebElement> actualBtns = rolTasks.getVisiableBtns();
		Assert.assertTrue(actualBtns.isEmpty());
	}

	@Test
	public void SinglePrivilegeTests1700() {
		String permission = "Create Role";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectBtns = { "Create Role" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RoleMgrTasks rolTasks = new RoleMgrTasks();
		rolTasks.gotoAdministration(rptCenterPg);
		rolTasks.gotoRoleMgr();
		List<WebElement> actualBtns = rolTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests1800() {
		String permission = "Update Role";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectBtns = { "Update Role" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RoleMgrTasks rolTasks = new RoleMgrTasks();
		rolTasks.gotoAdministration(rptCenterPg);
		rolTasks.gotoRoleMgr();
		List<WebElement> actualBtns = rolTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests1900() {
		String permission = "Remove Role";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectBtns = { "Remove Role" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RoleMgrTasks rolTasks = new RoleMgrTasks();
		rolTasks.gotoAdministration(rptCenterPg);
		rolTasks.gotoRoleMgr();
		List<WebElement> actualBtns = rolTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests2000() {
		String permission = "View Report Purge Configuration";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectNodes = { "Scheduled Tasks",
				"Report Purge Configuration" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		AdministrationTasks adminTasks = new AdministrationTasks();
		adminTasks.gotoAdministration(rptCenterPg);
		List<WebElement> actualDisplayedNodes = adminTasks
				.getDisplayedTreeNodes();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectNodes, actualDisplayedNodes));
	}

	@Test
	public void SinglePrivilegeTests2100() {
		String permission = "Help";
		String[] expectTabs = { "Home", "Help" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
	}

	@Test
	public void SinglePrivilegeTests2200() {
		String permission = "Remove Report";
		String[] expectTabs = { "Home", "Report Manager" };
		String[] expectBtns = { "Remove Report Template" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RptMgrTasks rptTasks = new RptMgrTasks();
		rptTasks.gotoRptMgr(rptCenterPg);
		List<WebElement> actualBtns = rptTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests2300() {
		String permission = "Save Report Purge Configuration";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectNodes = { "Scheduled Tasks",
				"Report Purge Configuration" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		AdministrationTasks adminTasks = new AdministrationTasks();
		adminTasks.gotoAdministration(rptCenterPg);
		List<WebElement> actualDisplayedNodes = adminTasks
				.getDisplayedTreeNodes();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectNodes, actualDisplayedNodes));
	}

	@Test
	public void SinglePrivilegeTests2400() {
		String permission = "Save Report Transport Configuration";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectNodes = { "Server", "Report Transport Configuration" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		AdministrationTasks adminTasks = new AdministrationTasks();
		adminTasks.gotoAdministration(rptCenterPg);
		List<WebElement> actualDisplayedNodes = adminTasks
				.getDisplayedTreeNodes();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectNodes, actualDisplayedNodes));
	}

	@Test
	public void SinglePrivilegeTests2500() {
		String permission = "View Report Transport Configuration";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectNodes = { "Server", "Report Transport Configuration" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		AdministrationTasks adminTasks = new AdministrationTasks();
		adminTasks.gotoAdministration(rptCenterPg);
		List<WebElement> actualDisplayedNodes = adminTasks
				.getDisplayedTreeNodes();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectNodes, actualDisplayedNodes));
	}

	@Test
	public void SinglePrivilegeTests2600() {
		String permission = "View Front Page Configuration";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectNodes = { "Server", "Front Page" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		AdministrationTasks adminTasks = new AdministrationTasks();
		adminTasks.gotoAdministration(rptCenterPg);
		List<WebElement> actualDisplayedNodes = adminTasks
				.getDisplayedTreeNodes();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectNodes, actualDisplayedNodes));
	}

	@Test
	public void SinglePrivilegeTests2700() {
		String permission = "Save Front Page Configuration";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectNodes = { "Server", "Front Page" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		AdministrationTasks adminTasks = new AdministrationTasks();
		adminTasks.gotoAdministration(rptCenterPg);
		List<WebElement> actualDisplayedNodes = adminTasks
				.getDisplayedTreeNodes();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectNodes, actualDisplayedNodes));
	}

	@Test
	public void SinglePrivilegeTests2800() {
		String permission = "Upload Front Page Image";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectNodes = { "Server", "Front Page" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		AdministrationTasks adminTasks = new AdministrationTasks();
		adminTasks.gotoAdministration(rptCenterPg);
		List<WebElement> actualDisplayedNodes = adminTasks
				.getDisplayedTreeNodes();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectNodes, actualDisplayedNodes));
	}

	@Test
	public void SinglePrivilegeTests2900() {
		String permission = "Upload Header Image";
		String[] expectTabs = { "Home", "Administration" };
		String[] expectNodes = { "Server", "Front Page" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		AdministrationTasks adminTasks = new AdministrationTasks();
		adminTasks.gotoAdministration(rptCenterPg);
		List<WebElement> actualDisplayedNodes = adminTasks
				.getDisplayedTreeNodes();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectNodes, actualDisplayedNodes));
	}

	@Test
	public void SinglePrivilegeTests3000() {
		String permission = "View Extraction Schedule Manager";
		String[] expectTabs = { "Home", "Extraction Schedule Manager" };
		String[] expectBtns = { "Cancel" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// login
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		ExtraScheduleMgrTasks extraSchTasks = new ExtraScheduleMgrTasks();
		extraSchTasks.goToExtraScheduleMgr(rptCenterPg);
		extraSchTasks.openNewEventWindow();
		CommonUtil.sleep(3);
		List<WebElement> actualDisplayedBtns = extraSchTasks
				.getDisplayedBtnsOnEventWin();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualDisplayedBtns));
	}

	@Test
	public void SinglePrivilegeTests3100() {
		String permission = "Create transform Event";
		String[] expectTabs = { "Home", "Extraction Schedule Manager" };
		String[] expectBtns = { "Save", "Cancel" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// login
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		ExtraScheduleMgrTasks extraSchTasks = new ExtraScheduleMgrTasks();
		extraSchTasks.goToExtraScheduleMgr(rptCenterPg);
		extraSchTasks.openNewEventWindow();
		CommonUtil.sleep(3);
		List<WebElement> actualDisplayedBtns = extraSchTasks
				.getDisplayedBtnsOnEventWin();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualDisplayedBtns));
	}

	@Test
	public void SinglePrivilegeTests3200() {
		String permission = "Update transform Event";
		String[] expectTabs = { "Home", "Extraction Schedule Manager" };
		String[] expectBtns = { "Save", "Cancel" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// create a event for test
		rptCenterPg = lg.login("admin", "admin");
		ExtraScheduleMgrTasks extraSchTasks = new ExtraScheduleMgrTasks();
		extraSchTasks.goToExtraScheduleMgr(rptCenterPg);
		extraSchTasks.createEvent("PTest3200", "TransformOraToOra.ktr");
		try {

			br.closeBrowser();
			// Verify
			br.launchBrowser();
			lg.loginPage = br.getURL();
			rptCenterPg = lg.login(user, pwd);
			List<WebElement> actualTabs = privilegeTasks
					.getMainTabs(rptCenterPg);
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectTabs, actualTabs));
			extraSchTasks.goToExtraScheduleMgr(rptCenterPg);
			extraSchTasks.openEvent("PTest3200");
			CommonUtil.sleep(3);
			List<WebElement> actualDisplayedBtns = extraSchTasks
					.getDisplayedBtnsOnEventWin();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns, actualDisplayedBtns));
		} finally {
			ExtraScheduleMgrTasks.cleanEventDB();
		}
	}

	@Test
	public void SinglePrivilegeTests3300() {
		String permission = "Remove transform Event";
		String[] expectTabs = { "Home", "Extraction Schedule Manager" };
		String[] expectBtns = { "Cancel", "Delete" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// create a event for test
		rptCenterPg = lg.login("admin", "admin");
		ExtraScheduleMgrTasks extraSchTasks = new ExtraScheduleMgrTasks();
		extraSchTasks.goToExtraScheduleMgr(rptCenterPg);
		extraSchTasks.createEvent("PTest3300", "TransformOraToOra.ktr");
		try {

			br.closeBrowser();
			// Verify
			br.launchBrowser();
			lg.loginPage = br.getURL();
			rptCenterPg = lg.login(user, pwd);
			List<WebElement> actualTabs = privilegeTasks
					.getMainTabs(rptCenterPg);
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectTabs, actualTabs));
			extraSchTasks.goToExtraScheduleMgr(rptCenterPg);
			extraSchTasks.openEvent("PTest3300");
			CommonUtil.sleep(3);
			List<WebElement> actualDisplayedBtns = extraSchTasks
					.getDisplayedBtnsOnEventWin();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns, actualDisplayedBtns));

		} finally {
			ExtraScheduleMgrTasks.cleanEventDB();
		}
	}

	@Test
	public void SinglePrivilegeTests3400() {
		String permission = "View Extraction Manager";
		String[] expectTabs = { "Home", "Extraction Manager" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		ExtractionMgrTasks extraTasks = new ExtractionMgrTasks();
		extraTasks.gotoExtractionManager(rptCenterPg);
		List<WebElement> actualBtns = extraTasks.getVisiableBtns();
		Assert.assertTrue(actualBtns.isEmpty());
	}

	@Test
	public void SinglePrivilegeTests3500() {
		String permission = "Add Transform";
		String[] expectTabs = { "Home", "Extraction Manager" };
		String[] expectBtns = { "Add Transform" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		ExtractionMgrTasks extraTasks = new ExtractionMgrTasks();
		extraTasks.gotoExtractionManager(rptCenterPg);
		List<WebElement> actualBtns = extraTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests3600() {
		String permission = "Remove Transform";
		String[] expectTabs = { "Home", "Extraction Manager" };
		String[] expectBtns = { "Remove Transform" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		ExtractionMgrTasks extraTasks = new ExtractionMgrTasks();
		extraTasks.gotoExtractionManager(rptCenterPg);
		List<WebElement> actualBtns = extraTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests3700() {
		String permission = "Update Transform";
		String[] expectTabs = { "Home", "Extraction Manager" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
	}

	@Test
	public void SinglePrivilegeTests3800() {
		String permission = "Execute Transform";
		String[] expectTabs = { "Home", "Extraction Manager" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
	}

	@Test
	public void SinglePrivilegeTests3900() {
		String permission = "Download Transform";
		String[] expectTabs = { "Home", "Extraction Manager" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
	}

	@Test
	public void SinglePrivilegeTests4000() {
		String permission = "Set Report Template to Private";
		String[] expectTabs = { "Home", "Report Manager" };
		String[] expectBtns = { "Set to Private" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RptMgrTasks rptTasks = new RptMgrTasks();
		rptTasks.gotoRptMgr(rptCenterPg);
		List<WebElement> actualBtns = rptTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests4100() {
		String permission = "Set Report Template to Public";
		String[] expectTabs = { "Home", "Report Manager" };
		String[] expectBtns = { "Set to Public" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RptMgrTasks rptTasks = new RptMgrTasks();
		rptTasks.gotoRptMgr(rptCenterPg);
		List<WebElement> actualBtns = rptTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests4200() {
		String permission = "Set Transform to Private";
		String[] expectTabs = { "Home", "Extraction Manager" };
		String[] expectBtns = { "Set to Private" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		ExtractionMgrTasks extraTasks = new ExtractionMgrTasks();
		extraTasks.gotoExtractionManager(rptCenterPg);
		List<WebElement> actualBtns = extraTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests4300() {
		String permission = "Set Transform to Public";
		String[] expectTabs = { "Home", "Extraction Manager" };
		String[] expectBtns = { "Set to Public" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		ExtractionMgrTasks extraTasks = new ExtractionMgrTasks();
		extraTasks.gotoExtractionManager(rptCenterPg);
		List<WebElement> actualBtns = extraTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests4400() {
		String permission = "Batch Upload Report";
		String[] expectTabs = { "Home", "Report Manager" };
		String[] expectBtns = { "Batch Upload Template" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		RptMgrTasks rptTasks = new RptMgrTasks();
		rptTasks.gotoRptMgr(rptCenterPg);
		List<WebElement> actualBtns = rptTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void SinglePrivilegeTests4500() {
		String permission = "Batch Upload Transform";
		String[] expectTabs = { "Home", "Extraction Manager" };
		String[] expectBtns = { "Batch Upload Template" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		rptCenterPg = lg.login(user, pwd);
		List<WebElement> actualTabs = privilegeTasks.getMainTabs(rptCenterPg);
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectTabs, actualTabs));
		ExtractionMgrTasks extraTasks = new ExtractionMgrTasks();
		extraTasks.gotoExtractionManager(rptCenterPg);
		List<WebElement> actualBtns = extraTasks.getVisiableBtns();
		Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
				expectBtns, actualBtns));
	}

	@Test
	public void multiplePrivilegeTest0100() {
		String[] permission = { "View Report Schedule Manager",
				"Create Report Event", "Update Report Event" };
		String[] expectTabs = { "Home", "Report Schedule Manager" };
		String[] expectBtns = { "Save", "Cancel" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// create a event for test
		rptCenterPg = lg.login(user, pwd);
		RptScheduleMgrTasks rptSchTasks = new RptScheduleMgrTasks();
		rptSchTasks.gotoRptScheduleManager(rptCenterPg);
		rptSchTasks.createEvent("MPTest0100", RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		try {
			br.closeBrowser();
			// Verify
			br.launchBrowser();
			lg.loginPage = br.getURL();
			rptCenterPg = lg.login(user, pwd);
			List<WebElement> actualTabs = privilegeTasks
					.getMainTabs(rptCenterPg);
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectTabs, actualTabs));
			rptSchTasks.gotoRptScheduleManager(rptCenterPg);
			rptSchTasks.openEvent("MPTest0100");
			CommonUtil.sleep(3);
			List<WebElement> actualDisplayedBtns = rptSchTasks
					.getDisplayedBtnsOnEventWin();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns, actualDisplayedBtns));
		} finally {
			RptScheduleMgrTasks.rmRptEventFromBackend("MPTest0100");
		}
	}

	@Test
	public void multiplePrivilegeTest1500() {
		String[] permission = { "View Report Schedule Manager",
				"Create Report Event", "Update Report Event", "Add Report",
				"Remove Report" };
		String[] expectTabs = { "Home", "Report Schedule Manager",
				"Report Manager" };
		String[] expectBtns = { "Save", "Cancel" };
		String[] expectBtns2 = { "Add Report Template",
				"Remove Report Template" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// create a event for test
		rptCenterPg = lg.login(user, pwd);
		RptScheduleMgrTasks rptSchTasks = new RptScheduleMgrTasks();
		rptSchTasks.gotoRptScheduleManager(rptCenterPg);
		rptSchTasks.createEvent("MPTest1500", RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		try {
			br.closeBrowser();
			// Verify
			CommonUtil.sleep(3);
			br.launchBrowser();
			lg.loginPage = br.getURL();
			rptCenterPg = lg.login(user, pwd);
			List<WebElement> actualTabs = privilegeTasks
					.getMainTabs(rptCenterPg);
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectTabs, actualTabs));
			rptSchTasks.gotoRptScheduleManager(rptCenterPg);
			rptSchTasks.openEvent("MPTest1500");
			CommonUtil.sleep(3);
			List<WebElement> actualDisplayedBtns = rptSchTasks
					.getDisplayedBtnsOnEventWin();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns, actualDisplayedBtns));
			rptSchTasks.cancelEvent();
			RptMgrTasks rptTasks = new RptMgrTasks();
			rptTasks.gotoRptMgr(rptCenterPg);
			List<WebElement> actualBtns = rptTasks.getVisiableBtns();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns2, actualBtns));
		} finally {
			RptScheduleMgrTasks.rmRptEventFromBackend("MPTest1500");
		}
	}

	@Test
	public void multiplePrivilegeTest1900() {
		String[] permission = { "View Extraction Manager",
				"Create Transform Event" };
		String[] expectTabs = { "Home", "Extraction Schedule Manager",
				"Extraction Manager" };
		String user = userName.toString();
		String role = roleName.toString();
		privilegeTasks.addPermissionToRole(role, permission);
		// create a event for test
		try {
			rptCenterPg = lg.login(user, pwd);
			List<WebElement> actualTabs = privilegeTasks
					.getMainTabs(rptCenterPg);
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectTabs, actualTabs));
			ExtractionMgrTasks extraTasks = new ExtractionMgrTasks();
			extraTasks.gotoExtractionManager(rptCenterPg);
			extraTasks.setSchedule("MPTest1900", "TransformOraToOra.ktr");
		} catch (Exception e) {
			Assert.fail("Fail to set schedule");
		} finally {
			RptScheduleMgrTasks.rmRptEventFromBackend("MPTest1900");
		}
	}

	@Test
	public void multiplePrivilegeMultipleRoleTest0100() {
		String[] permission1 = { "View Report Schedule Manager",
				"Create Report Event", "Update Report Event" };
		String[] permission2 = {"Remove Report Event","Remove Report"};
		String[] expectTabs = { "Home", "Report Schedule Manager",
				"Report Manager" };
		String[] expectBtns = { "Save", "Cancel","Delete"};
		String[] expectBtns2 = { "Remove Report Template"};
		String user = userName.toString();
		String role1 = roleName.toString();
		String role2 = role1 +"2";
		privilegeTasks.addPermissionToRole(role1, permission1);
		privilegeTasks.insertRole(role2, "Another role of multiplePrivilegeMultipleRoleTest0100");
		privilegeTasks.addPermissionToRole(role2, permission2);
		privilegeTasks.grantRolesToUser(user, role2);
		rptCenterPg = lg.login(user, pwd);
		RptScheduleMgrTasks rptSchTasks = new RptScheduleMgrTasks();
		rptSchTasks.gotoRptScheduleManager(rptCenterPg);
		rptSchTasks.createEvent("MPMRTest0100", RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		try {
			br.closeBrowser();
			// Verify
			CommonUtil.sleep(3);
			br.launchBrowser();
			lg.loginPage = br.getURL();
			rptCenterPg = lg.login(user, pwd);
			List<WebElement> actualTabs = privilegeTasks
					.getMainTabs(rptCenterPg);
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectTabs, actualTabs));
			rptSchTasks.gotoRptScheduleManager(rptCenterPg);
			rptSchTasks.openEvent("MPMRTest0100");
			CommonUtil.sleep(3);
			List<WebElement> actualDisplayedBtns = rptSchTasks
					.getDisplayedBtnsOnEventWin();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns, actualDisplayedBtns));
			rptSchTasks.cancelEvent();
			RptMgrTasks rptTasks = new RptMgrTasks();
			rptTasks.gotoRptMgr(rptCenterPg);
			List<WebElement> actualBtns = rptTasks.getVisiableBtns();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns2, actualBtns));
		} finally {
			RptScheduleMgrTasks.rmRptEventFromBackend("MPMRTest0100");
		}
	}
	
	@Test
	public void multiplePrivilegeMultipleRoleTest0200() {
		String[] permission1 = { "Report Schedule Manager"};
		String[] permission2 = {"View Report Schedule Manager",
				"Create Report Event", "Remove Report"};
		String[] expectTabs = { "Home", "Report Schedule Manager",
				"Report Manager" };
		String[] expectBtns = { "Save", "Cancel","Delete"};
		String[] expectBtns2 = { "Remove Report Template"};
		String user = userName.toString();
		String role1 = roleName.toString();
		String role2 = role1 +"2";
		privilegeTasks.addPermissionToRole(role1, permission1);
		privilegeTasks.insertRole(role2, "Another role of multiplePrivilegeMultipleRoleTest0100");
		privilegeTasks.addPermissionToRole(role2, permission2);
		privilegeTasks.grantRolesToUser(user, role2);
		rptCenterPg = lg.login(user, pwd);
		RptScheduleMgrTasks rptSchTasks = new RptScheduleMgrTasks();
		rptSchTasks.gotoRptScheduleManager(rptCenterPg);
		rptSchTasks.createEvent("MPMRTest0200", RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		try {
			br.closeBrowser();
			// Verify
			CommonUtil.sleep(3);
			br.launchBrowser();
			lg.loginPage = br.getURL();
			rptCenterPg = lg.login(user, pwd);
			List<WebElement> actualTabs = privilegeTasks
					.getMainTabs(rptCenterPg);
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectTabs, actualTabs));
			rptSchTasks.gotoRptScheduleManager(rptCenterPg);
			rptSchTasks.openEvent("MPMRTest0200");
			CommonUtil.sleep(3);
			List<WebElement> actualDisplayedBtns = rptSchTasks
					.getDisplayedBtnsOnEventWin();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns, actualDisplayedBtns));
			rptSchTasks.cancelEvent();
			RptMgrTasks rptTasks = new RptMgrTasks();
			rptTasks.gotoRptMgr(rptCenterPg);
			List<WebElement> actualBtns = rptTasks.getVisiableBtns();
			Assert.assertTrue(privilegeTasks.verifyExpectElementTextDisplay(
					expectBtns2, actualBtns));
		} finally {
			RptScheduleMgrTasks.rmRptEventFromBackend("MPMRTest0200");
		}
	}
}
