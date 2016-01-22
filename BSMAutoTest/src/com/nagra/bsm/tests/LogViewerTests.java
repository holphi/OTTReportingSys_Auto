package com.nagra.bsm.tests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import junit.framework.Assert;

import com.nagra.bsm.tasks.BrowserTasks;
import com.nagra.bsm.tasks.LogViewerTasks;
import com.nagra.bsm.tasks.LoginTasks;
import com.nagra.bsm.tasks.PrivilegeTasks;
import com.nagra.bsm.tasks.RptMgrTasks;
import com.nagra.bsm.tasks.RptScheduleMgrTasks;
import com.nagra.bsm.tasks.TransportConfTasks;
import com.nagra.bsm.ui.AdminPage;
import com.nagra.bsm.ui.FrontConfPage;
import com.nagra.bsm.ui.LoginPage;
import com.nagra.bsm.ui.LogViewerPage;
import com.nagra.bsm.ui.RoleEdit;
import com.nagra.bsm.ui.RoleMgrPage;
import com.nagra.bsm.ui.RptFormat;
import com.nagra.bsm.ui.RptMgrPage;
import com.nagra.bsm.ui.RptTransportPage;
import com.nagra.bsm.ui.RptUpload;
import com.nagra.bsm.ui.ScheduleOperation;
import com.nagra.bsm.ui.TransType;
import com.nagra.bsm.ui.UserMgrPage;
import com.nagra.bsm.ui.UserEdit;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.util.CommonUtil;

public class LogViewerTests extends BaseTests {

	private static String testDataDir = CommonUtil.getCurrDir()
			+ "\\testdata\\";

	// Utility method for quick navigation to test page.
	private LogViewerPage navigateToTestPage(String username, String password) {
		logger.info("Login in with User:" + username + ", Password:" + password);
		LoginPage loginPage = new LoginPage(CommonUtil.launchTestPortal());
		RptCenterPage rptCenterPage = loginPage.loginAs(username, password);

		logger.info("Click Administration to go to Administration tab.");
		AdminPage adminPage = rptCenterPage.goToAdmin();

		logger.info("Click Log -> Log Viewer.");
		return adminPage.goToLogViewerPage();
	}

	@Rule
	public Timeout globalTimeout = new Timeout(300000);

	@BeforeClass
	public static void suiteSetup() {
		RptMgrTasks rptMgr = new RptMgrTasks();
		String rpt = "SDP_NMP.rptdesign";
		if (!rptMgr.isRptExisted(rpt)) {
			BrowserTasks br = new BrowserTasks();
			br.launchBrowser();
			// open login page
			LoginTasks lg = new LoginTasks();
			lg.loginPage = br.getURL();
			// login
			RptCenterPage rptCenterPg;
			rptCenterPg = lg.login();			
			rptMgr.gotoRptMgr(rptCenterPg);			
			rptMgr.addRpt(testDataDir + "RptTemplate\\", rpt);
			rptMgr.addRpt(testDataDir + "RptTemplate\\", "SDP_Base.rptlibrary");
			br.closeBrowser();
		}		
	}

	@Before
	public void testSetup() {
		logger.info("=============================" + testName.getMethodName()
				+ "==============================");
	}

	@Test
	public void testLogViewer0100() {
		try {
			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			Assert.assertTrue("Verify the bottom navigation bar is displayed.",
					logPage.isNavBarDisplayed());
			String[] columnHeaders = new String[] { "ID", "Action",
					"Description", "Operator", "Date", "Status" };
			for (String column : columnHeaders) {
				Assert.assertTrue(String.format(
						"Verify the Column %s is displayed.", column), logPage
						.isColumnHeaderDisplayed(column));
			}
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
		}
	}

	@Test
	public void testLogViewer0200() {
		BrowserTasks br = new BrowserTasks();
		LoginTasks lg = new LoginTasks();
		try {
			String[] expected = { "Create report schedule event", "", "admin",
					"1" };
			CommonUtil.syncServerTimeWithClient();
			CommonUtil.removeAllActivityLog();
			br.launchBrowser();
			// open login page
			lg.loginPage = br.getURL();
			// login
			RptCenterPage rptCenterPg = lg.login();
			RptScheduleMgrTasks sch = new RptScheduleMgrTasks();
			sch.gotoRptScheduleManager(rptCenterPg);
			sch.createEvent("LogViewer0200", RptFormat.EXCEL, null,
					"SDP_NMP.rptdesign");
			CommonUtil.sleep(3);

			LogViewerTasks logTasks = new LogViewerTasks();
			ArrayList<String[]> log = logTasks.getLogRecordInDB();
			Assert.assertTrue(logTasks.verifyLog(expected, log.get(0)));
		} finally {
			br.closeBrowser();
			RptScheduleMgrTasks.rmRptEventFromBackend("LogViewer0200");
		}
	}

	@Test
	public void testLogViewer0300() {
		BrowserTasks br = new BrowserTasks();
		LoginTasks lg = new LoginTasks();
		String eventText = "LogViewer0300";
		try {
			String[] expected = { "Update report schedule event", "", "admin",
					"1" };
			CommonUtil.syncServerTimeWithClient();
			CommonUtil.removeAllActivityLog();
			br.launchBrowser();
			// open login page
			lg.loginPage = br.getURL();
			// login
			RptCenterPage rptCenterPg = lg.login();
			RptScheduleMgrTasks schTasks = new RptScheduleMgrTasks();
			schTasks.gotoRptScheduleManager(rptCenterPg);
			schTasks.createEvent(eventText, RptFormat.EXCEL, null,
					"SDP_NMP.rptdesign");
			CommonUtil.sleep(1);
			schTasks.openEvent(eventText);
			schTasks.setEventDesc(eventText + "Update");
			schTasks.saveEvent();
			CommonUtil.sleep(1);
			LogViewerTasks logTasks = new LogViewerTasks();
			ArrayList<String[]> log = logTasks.getLogRecordInDB();
			Assert.assertTrue(logTasks.verifyLog(expected, log.get(0)));
		} finally {
			br.closeBrowser();
			RptScheduleMgrTasks.rmRptEventFromBackend(eventText + "Update");
		}
	}

	@Test
	public void testLogViewer0400() {
		BrowserTasks br = new BrowserTasks();
		LoginTasks lg = new LoginTasks();
		String eventText = "LogViewer0400";
		try {
			String[] expected = { "Remove report schedule event", "", "admin",
					"1" };
			CommonUtil.syncServerTimeWithClient();
			CommonUtil.removeAllActivityLog();
			br.launchBrowser();
			// open login page
			lg.loginPage = br.getURL();
			// login
			RptCenterPage rptCenterPg = lg.login();
			RptScheduleMgrTasks schTasks = new RptScheduleMgrTasks();
			schTasks.gotoRptScheduleManager(rptCenterPg);
			schTasks.createEvent(eventText, RptFormat.EXCEL, null,
					"SDP_NMP.rptdesign");
			CommonUtil.sleep(1);
			schTasks.openEvent(eventText);
			schTasks.clickDelete();
			CommonUtil.sleep(1);
			LogViewerTasks logTasks = new LogViewerTasks();
			ArrayList<String[]> log = logTasks.getLogRecordInDB();
			Assert.assertTrue(logTasks.verifyLog(expected, log.get(0)));
		} finally {
			br.closeBrowser();
		}
	}

	@Test
	public void testLogViewer0600() {
		String fileName = "TestReportAdd.rptdesign";
		try {
			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			RptMgrPage rptMgrPage = logPage.goToReportMgr();
			rptMgrPage.clickAddRpt();

			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());

			rptUpload.setFilePath(testDataDir + "RptTemplate\\" + fileName);
			rptUpload.clickUpload();
			CommonUtil.sleep(6);

			rptUpload.closeWindow();

			logPage = rptMgrPage.goToAdmin().goToLogViewerPage();

			CommonUtil.sleep(3);

			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.", "Add report template",
					logItem.get("Action"));
			Assert.assertEquals("Verify column Description.", fileName,
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			CommonUtil.rmRptDesignFileFromSrv(fileName);
		}
	}

	@Test
	public void testLogViewer0700() {
		String fileName = "TestReportAdd.rptdesign";
		String username = CommonUtil.getPropertyValue("username");
		String password = CommonUtil.getPropertyValue("password");
		try {
			// Remove all activity log
			CommonUtil.removeAllActivityLog();
			// Upload report design template by FTP
			CommonUtil.createDumbBirtRpts(new String[] { fileName }, username);

			// Test execution
			LogViewerPage logPage = navigateToTestPage(username, password);

			RptMgrPage rptMgrPage = logPage.goToReportMgr();

			rptMgrPage.clickNavItem("Last");
			rptMgrPage.clickUpdateRpt(fileName);

			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir + "RptTemplate\\" + fileName);
			rptUpload.clickUpload();
			CommonUtil.sleep(6);

			rptUpload.closeWindow();

			logPage = rptMgrPage.goToAdmin().goToLogViewerPage();

			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.",
					"Update report template", logItem.get("Action"));
			Assert.assertEquals("Verify column Description.", fileName,
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[] { fileName }, username);
		}
	}

	@Test
	public void testLogViewer0800() {
		String rptName = "TestReportRemove2.rptdesign";
		String eventName = "Rpt_event_" + CommonUtil.getRandomStr();
		String username = CommonUtil.getPropertyValue("username");
		String password = CommonUtil.getPropertyValue("password");
		try {
			// Set-up
			CommonUtil.createDumbBirtRpts(new String[] { rptName }, username);
			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			// Test execution
			LogViewerPage logPage = navigateToTestPage(username, password);

			RptMgrPage rptMgrPage = logPage.goToReportMgr();
			rptMgrPage.clickNavItem("Last");

			rptMgrPage.clickSetSchedule(rptName);

			ScheduleOperation scheduleOp = new ScheduleOperation(
					rptMgrPage.getWebDriver());

			logger.info(String.format(
					"On Schedule operation window: input new event name %s",
					eventName));
			scheduleOp.setEventName(eventName);
			logger.info(String
					.format("On Schedule operation window: check Excel for %s",
							rptName));
			scheduleOp.chkExcelFor(rptName);
			scheduleOp.setStartDate(getStartDate());
			scheduleOp.clickSave();

			logPage = rptMgrPage.goToAdmin().goToLogViewerPage();

			CommonUtil.sleep(3);

			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.",
					"Create report schedule event", logItem.get("Action"));
			Assert.assertTrue("Verify column Description.",
					logItem.get("Description").contains(eventName));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			CommonUtil.rmScheduledEvent(eventName);
			CommonUtil.rmBirtRpts(new String[] { rptName }, username);
		}
	}

	@Test
	public void testLogViewer0900() {
		String roleName = "role" + CommonUtil.getRandomStr();
		try {
			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			RoleMgrPage roleMgrPage = logPage.goToRoleMgrPage();

			roleMgrPage.clickCreateRole();

			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			re.checkTreeNode("Report Schedule Manager");
			re.inputComments(roleName);
			re.clickOK();
			re.acceptAlertWindow();

			logPage = roleMgrPage.goToLogViewerPage();

			Assert.assertEquals(
					"Verify there's should be a log item on Log View Page", 1,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.", "Create role",
					logItem.get("Action"));
			Assert.assertEquals("Verify column Description.",
					String.format("Role name:%s", roleName),
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			CommonUtil.removeRoleFromDb(roleName);
		}
	}

	@Test
	public void testLogViewer1000() {
		String roleName = "role" + CommonUtil.getRandomStr();
		try {
			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			RoleMgrPage roleMgrPage = logPage.goToRoleMgrPage();

			roleMgrPage.clickCreateRole();

			// Create role
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			re.checkTreeNode("Report Schedule Manager");
			re.inputComments(roleName);
			re.clickOK();
			re.acceptAlertWindow();

			// Choose role will be updated
			roleMgrPage.clickRoleFromRoleList(roleName);

			// Update role
			roleMgrPage.clickUpdateRole();
			re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputComments("updated");
			re.clickOK();
			re.acceptAlertWindow();

			logPage = roleMgrPage.goToLogViewerPage();
			Assert.assertEquals(
					"Verify there's should be two log items on Log View Page",
					2, logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(2);
			Assert.assertEquals("Verify column Action.", "Update role",
					logItem.get("Action"));
			Assert.assertEquals("Verify column Description.",
					String.format("Role name:%s", roleName),
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			CommonUtil.removeRoleFromDb(roleName);
		}
	}

	@Test
	public void testLogViewer1100() {
		String roleName = "role" + CommonUtil.getRandomStr();
		try {
			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			RoleMgrPage roleMgrPage = logPage.goToRoleMgrPage();

			roleMgrPage.clickCreateRole();

			// Create role
			RoleEdit re = new RoleEdit(roleMgrPage.getWebDriver());
			re.inputRoleName(roleName);
			re.checkTreeNode("Report Schedule Manager");
			re.inputComments(roleName);
			re.clickOK();
			re.acceptAlertWindow();

			// Choose role will be updated
			roleMgrPage.clickRoleFromRoleList(roleName);

			// Remove role
			roleMgrPage.clickRemoveRole();
			roleMgrPage.acceptAlertWindow();
			roleMgrPage.acceptAlertWindow();

			logPage = roleMgrPage.goToLogViewerPage();

			Assert.assertEquals(
					"Verify there's should be 2 log items on Log View Page", 2,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(2);
			Assert.assertEquals("Verify column Action.", "Remove role",
					logItem.get("Action"));
			Assert.assertEquals("Verify column Description.",
					String.format("Role name:%s", roleName),
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			CommonUtil.removeRoleFromDb(roleName);
		}
	}

	@Test
	public void testLogViewer1200() {
		String username = "user" + CommonUtil.getRandomStr();
		String password = "123";
		try {
			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			UserMgrPage userMgrPage = logPage.goToUserMgrPage();

			logger.info("Click Create User to create a new user.");
			userMgrPage.clickCreateUser();
			logger.info("Input new user data.");
			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.inputUserData(username, password, password, true,
					new String[] { "admin" });
			userEdit.clickOK();
			userEdit.acceptAlertWindow();

			logPage = userMgrPage.goToLogViewerPage();
			Assert.assertEquals(
					"Verify there's should be a log item on Log View Page", 1,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.", "Create user",
					logItem.get("Action"));
			Assert.assertEquals("Verify column Description.",
					String.format("User name:%s", username),
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			CommonUtil.removeUserDataFromDb(username);
		}
	}

	@Test
	public void testLogViewer1300() {
		String username = "";
		try {
			username = CommonUtil.createUserDataToDb("user", true);

			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			UserMgrPage userMgrPage = logPage.goToUserMgrPage();
			userMgrPage.clickUserFromUserList(username);
			userMgrPage.clickUpdateUser();

			UserEdit userEdit = new UserEdit(userMgrPage.getWebDriver());
			userEdit.clickOK();
			userEdit.acceptAlertWindow();
			logPage = userMgrPage.goToLogViewerPage();

			Assert.assertEquals(
					"Verify there's should be a log item on Log View Page", 1,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.", "Update user",
					logItem.get("Action"));
			Assert.assertEquals("Verify column Description.",
					String.format("User name:%s", username),
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			CommonUtil.removeUserDataFromDb(username);
		}
	}

	@Test
	public void testLogViewer1400() {
		String username = "";
		try {
			username = CommonUtil.createUserDataToDb("user", true);

			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			UserMgrPage userMgrPage = logPage.goToUserMgrPage();
			userMgrPage.clickUserFromUserList(username);
			userMgrPage.clickRemoveUser();

			userMgrPage.acceptAlertWindow();
			userMgrPage.acceptAlertWindow();

			logPage = userMgrPage.goToLogViewerPage();

			Assert.assertEquals(
					"Verify there's should be a log item on Log View Page", 1,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.", "Remove user",
					logItem.get("Action"));
			Assert.assertEquals("Verify column Description.",
					String.format("User name:%s", username),
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			CommonUtil.removeUserDataFromDb(username);
		}
	}

	@Test
	public void testLogViewer1500() {
		BrowserTasks br = new BrowserTasks();
		LoginTasks lg = new LoginTasks();
		try {
			String[] expected = { "Send Report by Email", "", "admin", "1" };
			CommonUtil.syncServerTimeWithClient();
			CommonUtil.removeAllActivityLog();
			br.launchBrowser();
			// open login page
			lg.loginPage = br.getURL();
			// login
			RptCenterPage rptCenterPg = lg.login();
			RptScheduleMgrTasks sch = new RptScheduleMgrTasks();
			sch.gotoRptScheduleManager(rptCenterPg);
			Calendar eventTime = sch.createEvent("LogViewer1500",
					RptFormat.EXCEL, TransType.EMAIL, "SDP_NMP.rptdesign");
			sch.triggerEvent(eventTime);
			CommonUtil.sleep(60);
			LogViewerTasks logTasks = new LogViewerTasks();
			ArrayList<String[]> log = logTasks.getLogRecordInDB();
			Assert.assertTrue(logTasks.verifyLog(expected, log.get(2)));
		} finally {
			br.closeBrowser();
			RptScheduleMgrTasks.rmRptEventFromBackend("LogViewer1500");
		}
	}

	@Test
	public void testLogViewer1600() {
		BrowserTasks br = new BrowserTasks();
		LoginTasks lg = new LoginTasks();
		try {
			String[] expected = { "Send Report by FTP", "", "admin", "1" };
			CommonUtil.syncServerTimeWithClient();
			CommonUtil.removeAllActivityLog();
			br.launchBrowser();
			// open login page
			lg.loginPage = br.getURL();
			// login
			RptCenterPage rptCenterPg = lg.login();
			RptScheduleMgrTasks sch = new RptScheduleMgrTasks();
			sch.gotoRptScheduleManager(rptCenterPg);
			Calendar eventTime = sch.createEvent("LogViewer1600",
					RptFormat.EXCEL, TransType.FTP, "SDP_NMP.rptdesign");
			sch.triggerEvent(eventTime);
			CommonUtil.sleep(60);
			LogViewerTasks logTasks = new LogViewerTasks();
			ArrayList<String[]> log = logTasks.getLogRecordInDB();
			Assert.assertTrue(logTasks.verifyLog(expected, log.get(2)));
		} finally {
			br.closeBrowser();
			RptScheduleMgrTasks.rmRptEventFromBackend("LogViewer1600");
		}
	}

	@Test
	public void testLogViewer1700() {
		BrowserTasks br = new BrowserTasks();
		LoginTasks lg = new LoginTasks();
		try {
			String[] expected = { "Send Report by HTTP", "", "admin", "1" };
			CommonUtil.syncServerTimeWithClient();
			CommonUtil.removeAllActivityLog();
			br.launchBrowser();
			// open login page
			lg.loginPage = br.getURL();
			// login
			RptCenterPage rptCenterPg = lg.login();
			RptScheduleMgrTasks sch = new RptScheduleMgrTasks();
			sch.gotoRptScheduleManager(rptCenterPg);
			Calendar eventTime = sch.createEvent("LogViewer1700",
					RptFormat.EXCEL, TransType.FTP, "SDP_NMP.rptdesign");
			sch.triggerEvent(eventTime);
			CommonUtil.sleep(60);
			LogViewerTasks logTasks = new LogViewerTasks();
			ArrayList<String[]> log = logTasks.getLogRecordInDB();
			Assert.assertTrue(logTasks.verifyLog(expected, log.get(2)));
		} finally {
			br.closeBrowser();
			RptScheduleMgrTasks.rmRptEventFromBackend("LogViewer1700");
		}
	}

	@Test
	public void testLogViewer1800() {
		String user = "logV1800";
		PrivilegeTasks privilegeTasks = new PrivilegeTasks();
		privilegeTasks.insertUser(user, "123", true);
		privilegeTasks.grantRolesToUser(user, "ADMIN");
		BrowserTasks br = new BrowserTasks();
		LoginTasks lg = new LoginTasks();
		try {
			String[] expected = { "", "", user, "1" };
			CommonUtil.syncServerTimeWithClient();
			CommonUtil.removeAllActivityLog();
			br.launchBrowser();
			// open login page
			lg.loginPage = br.getURL();
			// login
			RptCenterPage rptCenterPg = lg.login(user, "123");
			TransportConfTasks transConf = new TransportConfTasks();
			transConf.gotoRptTransConf(rptCenterPg);
			transConf.setFtpSetting();
			CommonUtil.sleep(1);
			LogViewerTasks logTasks = new LogViewerTasks();
			ArrayList<String[]> log = logTasks.getLogRecordInDB();
			Assert.assertTrue(logTasks.verifyLog(expected, log.get(0)));
		} finally {
			br.closeBrowser();
		}
	}

	@Test
	public void testLogViewer1900() {
		try {
			String pattern = "Records from %d to %d";
			String expectedText = "";
			int total = CommonUtil.createBatchLog(30);

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			// Verification
			logger.info("Verifiy the default page.");
			Assert.assertFalse("Verify First button is disabled.",
					logPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled.",
					logPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled.",
					logPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled.",
					logPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 1, 28);
			Assert.assertEquals("Verify the current paging info",
					logPage.getCurrentPagingInfo(), expectedText);

			logger.info("Click Next and verifiy.");
			logPage.clickNavItem("Next");
			logger.info("Verifiy the page.");
			Assert.assertTrue("Verify First button is enabled.",
					logPage.isNavItemEnabled("First"));
			Assert.assertTrue("Verify Previous button is enabled.",
					logPage.isNavItemEnabled("Previous"));
			Assert.assertFalse("Verify Next button is disabled.",
					logPage.isNavItemEnabled("Next"));
			Assert.assertFalse("Verify Last button is disabled.",
					logPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 29, total);
			Assert.assertEquals("Verify the current paging info",
					logPage.getCurrentPagingInfo(), expectedText);

			logger.info("Click First and verifiy.");
			logPage.clickNavItem("Previous");
			logger.info("Verifiy the page.");
			Assert.assertFalse("Verify First button is disabled.",
					logPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled.",
					logPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled.",
					logPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled.",
					logPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 1, 28);
			Assert.assertEquals("Verify the current paging info",
					logPage.getCurrentPagingInfo(), expectedText);

			logger.info("Click Last and verifiy.");
			logPage.clickNavItem("Last");
			logger.info("Verifiy the page.");
			Assert.assertTrue("Verify First button is enabled.",
					logPage.isNavItemEnabled("First"));
			Assert.assertTrue("Verify Previous button is enabled.",
					logPage.isNavItemEnabled("Previous"));
			Assert.assertFalse("Verify Next button is disabled.",
					logPage.isNavItemEnabled("Next"));
			Assert.assertFalse("Verify Last button is disabled.",
					logPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 29, total);
			Assert.assertEquals("Verify the current paging info",
					logPage.getCurrentPagingInfo(), expectedText);

			logger.info("Click First and verifiy.");
			logPage.clickNavItem("First");
			logger.info("Verifiy the page.");
			Assert.assertFalse("Verify First button is disabled.",
					logPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled.",
					logPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled.",
					logPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled.",
					logPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 1, 28);
			Assert.assertEquals("Verify the current paging info",
					logPage.getCurrentPagingInfo(), expectedText);

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			// CommonUtil.removeAllActivityLog();
		}
	}

	@Test
	public void testLogViewer2000() {
		String rptTemplateName = "SDP_NMP.rptdesign";
		try {
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			RptMgrPage rptMgrPage = logPage.goToReportMgr();
			rptMgrPage.downloadRptItem(rptTemplateName, testDataDir
					+ CommonUtil.getRandomStr() + ".rptdesign");

			CommonUtil.closeBrowser();

			logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			Assert.assertEquals(
					"Verify there's should be a log item on Log View Page", 1,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.",
					"Download report template", logItem.get("Action"));
			Assert.assertEquals("Verify column Description.",
					String.format("Report template is %s", rptTemplateName),
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
		}
	}

	@Test
	public void testLogViewer2100() {
		String rptTemplate = "TestReportRemove2.rptdesign";
		String username = CommonUtil.getPropertyValue("username");
		String password = CommonUtil.getPropertyValue("password");
		try {
			CommonUtil.removeAllActivityLog();
			CommonUtil
					.createDumbBirtRpts(new String[] { rptTemplate }, "admin");
			logger.info(CommonUtil.getUserIDFromDb(username));
			LogViewerPage logPage = navigateToTestPage(username, password);

			RptMgrPage rptMgrPage = logPage.goToReportMgr();
			rptMgrPage.clickNavItem("Last");
			rptMgrPage.checkRpt(rptTemplate);
			rptMgrPage.clickRemoveRpt();
			rptMgrPage.acceptAlertWindow();
			rptMgrPage.acceptAlertWindow();

			CommonUtil.closeBrowser();

			logPage = navigateToTestPage(username, password);

			Assert.assertEquals(
					"Verify there's should be a log item on Log View Page", 1,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.", "Remove Report",
					logItem.get("Action"));
			Assert.assertEquals("Verify column Description.", rptTemplate,
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[] { rptTemplate }, username);
		}
	}

	@Test
	public void testLogViewer2200() {
		try {
			String margin = "200.0";
			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));
			// Test execution
			FrontConfPage frontPage = logPage.goToFrontPageConfPage();
			frontPage.inputLeftMarginValue(margin);
			frontPage.inputTopMarginValue(margin);
			frontPage.clickSave();

			logPage = frontPage.goToLogViewerPage();

			Assert.assertEquals(
					"Verify there's should be a log item on Log View Page", 1,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.",
					"Save front page configuration", logItem.get("Action"));
			Assert.assertEquals(
					"Verify column Description.",
					String.format(
							"Margin to left of logo is %s and margin to top of logo is %s",
							margin, margin), logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
		}
	}

	@Test
	public void testLogViewer2300() {
		String img = "800x600.jpg";
		try {
			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));

			// Test execution

			FrontConfPage frontPage = logPage.goToFrontPageConfPage();
			logger.info("Click Browse and set backgroud image");
			frontPage.setBackGroundImg(testDataDir + "Image\\" + img);
			frontPage.clickUploadForBgImg();

			logPage = frontPage.goToLogViewerPage();

			Assert.assertEquals(
					"Verify there's should be a log item on Log View Page", 1,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.",
					"Upload background image", logItem.get("Action"));
			Assert.assertEquals("Verify column Description.",
					String.format("Uploaded image is imgs/login/%s", img),
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
		}
	}

	@Test
	public void testLogViewer2400() {
		try {
			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));
			// Test execution
			String img = "1024x99.jpg";
			FrontConfPage frontPage = logPage.goToFrontPageConfPage();
			frontPage.setHeaderImg(testDataDir + "Image\\" + img);
			frontPage.clickUploadForHeaderImg();
			logPage = frontPage.goToLogViewerPage();

			Assert.assertEquals(
					"Verify there's should be a log item on Log View Page", 1,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.", "Upload header image",
					logItem.get("Action"));
			Assert.assertEquals("Verify column Description.",
					String.format("Uploaded image is imgs/general/%s", img),
					logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
		}
	}

	@Test
	public void testLogViewer2500() {
		try {
			// Remove all activity log
			CommonUtil.removeAllActivityLog();

			LogViewerPage logPage = navigateToTestPage(
					CommonUtil.getPropertyValue("username"),
					CommonUtil.getPropertyValue("password"));
			// Test execution
			// Test execution
			RptTransportPage rptTransPage = logPage.goToRptTransportConfPage();

			rptTransPage.setSmtp("srv-smtp1.hq.k.grp");
			rptTransPage.setSmtpPort("25");
			rptTransPage.setSenderEmailAddress("BSM@nagra.com");
			rptTransPage.setSenderUserName(CommonUtil
					.getPropertyValue("domain_username"));
			rptTransPage.setSenderPassword(CommonUtil
					.getPropertyValue("domain_password"));
			rptTransPage.clickSaveSmtpSettings();
			CommonUtil.sleep(3);
			logPage = rptTransPage.goToLogViewerPage();

			Assert.assertEquals(
					"Verify there's should be a log item on Log View Page", 1,
					logPage.getLogCountInCurrPage());
			Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
			Assert.assertEquals("Verify column Action.",
					"Save Report Transport", logItem.get("Action"));
			Assert.assertEquals("Verify column Description.",
					"Save Report Transport", logItem.get("Description"));
			Assert.assertEquals("Verify column Operator.",
					CommonUtil.getPropertyValue("username"),
					logItem.get("Operator"));
			Assert.assertEquals("Verify column Status.", "success",
					logItem.get("Status"));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
		}
	}

	private Calendar getStartDate() {
		Calendar c = Calendar.getInstance();
		int minute = c.get(Calendar.MINUTE);

		do {
			minute++;
		} while (!(minute % 5 == 0));

		c.set(Calendar.MINUTE, minute);
		return c;
	}
}
