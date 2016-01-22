/**
 * 
 */
package com.nagra.bsm.tests;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;

import com.nagra.bsm.tasks.BrowserTasks;
import com.nagra.bsm.tasks.ExtraScheduleMgrTasks;
import com.nagra.bsm.tasks.ExtractionMgrTasks;
import com.nagra.bsm.tasks.LoginTasks;
import com.nagra.bsm.tasks.RptMgrTasks;
import com.nagra.bsm.tests.categorymarker.SanityCheck;
import com.nagra.bsm.ui.LoginPage;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.RptMgrPage;

import com.nagra.bsm.ui.RptUpload;
import com.nagra.bsm.ui.TransformMgrPage;
import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.util.DBUtil;
import com.nagra.bsm.util.DateUtil;

/**
 * @author tetang Extraction Manager manage transforms.
 */
public class ExtractionMgrTests extends BaseTests {
	private static String testDataDir = CommonUtil.getCurrDir()
			+ "\\testdata\\Transform\\";
	BrowserTasks br = new BrowserTasks();
	LoginTasks lg = new LoginTasks();
	RptCenterPage rptCenterPg;
	ExtractionMgrTasks extrMgrTasks = new ExtractionMgrTasks();
	private static String role1 = "";
	private static String user1 = "";
	private static String pw1 = "123";
	String userName = CommonUtil.getPropertyValue("username");
	String password = CommonUtil.getPropertyValue("password");
	String fileName = null;

	@Rule
	public Timeout globalTimeout = new Timeout(300000);	
	
	@BeforeClass
	public static void setupUser() {

		try {
			role1 = CommonUtil.createRoleWithPerm("ExtrMgrRole", new String[] {
					"Extraction Manager", "View Transform Manager",
					"Add Transform", "Download Transform", "Update Transform",
					"Remove Transform", "Execute Transform",
					"Set Transform To Public", "Set Transform To Private" });
			user1 = CommonUtil.createUserDataToDb("user", true);
			CommonUtil.associateRoleDataWithUserData(user1, role1);
		} catch (Exception e) {
			logger.error("Set up user failed");
		} 
	}

	@AfterClass
	public static void clearUser() {
		CommonUtil.removeUserDataFromDb(user1);
		CommonUtil.removeRolePerm(role1);
		CommonUtil.removeRoleFromDb(role1);
	}

	@Before
	public void testSetup() {
		logger.info("=============================" + testName.getMethodName()
				+ "==============================");
		br.launchBrowser();
		// open login page
		lg.loginPage = br.getURL();
		// login
		rptCenterPg = lg.login();
	}
	
	@After
	public void testTeardown(){
		br.closeBrowser();
	}

	@Test(timeout = 600000)
	@Category(SanityCheck.class)
	// TransformMgrCase0100 :User can add a transform.
	public void testExtractionMgr0100() {
		fileName = "TestTransformAdd.ktr";
		try {

			extrMgrTasks.gotoExtractionManager(rptCenterPg);

			// Test execution
			extrMgrTasks.transMgrPage.ClickAddTransform();
			Assert.assertTrue("Verify the upload window should be poped up.",
					extrMgrTasks.transMgrPage.isWindowPresent("Upload"));

			RptUpload rptUpload = new RptUpload(
					extrMgrTasks.transMgrPage.getWebDriver());

			rptUpload.setFilePath(testDataDir + fileName);
			rptUpload.clickUpload();
			CommonUtil.sleep(6);
			Assert.assertTrue(rptUpload.isFileListedInContainer(fileName));
			Assert.assertEquals("Done", rptUpload.getUploadMsg(fileName));

			rptUpload.closeWindow();

			Assert.assertTrue(
					"Verify the upload has been uploaded to specified folder.",
					extrMgrTasks.transMgrPage.isTransformPresent(fileName));

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase0200:A user cannot add transform with same transform name
	// twice.
	public void testExtractionMgr0200() {
		fileName = "TestTransformAdd1.ktr";

		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName);
			CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.transMgrPage.ClickAddTransform();
			Assert.assertTrue("Verify the upload window should be poped up.",
					extrMgrTasks.transMgrPage.isWindowPresent("Upload"));

			RptUpload rptUpload = new RptUpload(
					extrMgrTasks.transMgrPage.getWebDriver());

			rptUpload.setFilePath(testDataDir + fileName);
			rptUpload.clickUpload();
			CommonUtil.sleep(6);

			String msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the pop up message",
					"You have uploaded a PDI transform with same name.", msg);

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase0300:User can add multiple transform one-off.
	public void testExtractionMgr0300() {
		String[] fileList = new String[] { "TestTransformAdd2.ktr",
				"TestTransformAdd3.ktr", "TestTransformAdd4.ktr" };

		try {
			// Test execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.transMgrPage.ClickAddTransform();

			Assert.assertTrue("Verify the upload window should be poped up.",
					extrMgrTasks.transMgrPage.isWindowPresent("Upload"));
			RptUpload rptUpload = new RptUpload(
					extrMgrTasks.transMgrPage.getWebDriver());

			for (String file : fileList) {
				String fullName = testDataDir + file;
				logger.info(String.format("Add file: %s", fullName));
				rptUpload.setFilePath(fullName);
			}
			rptUpload.clickUpload();

			// Verification
			for (String file : fileList)
				Assert.assertEquals(String.format(
						"Verify the status of the file %s should be Done.",
						file), "Done", rptUpload.getUploadMsg(file));
			CommonUtil.sleep(6);
			rptUpload.closeWindow();
			for (String file : fileList)
				Assert.assertTrue(String.format(
						"Verify the file %s should be in the list.", file),
						extrMgrTasks.transMgrPage.isTransformPresent(file));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileList);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase0400:User can update a transform.
	public void testExtractionMgr0400() {
		fileName = "TestTransformUpdate.ktr";

		try {

			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName);
			CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.updateTransform(testDataDir, fileName);
			logger.info(String.format("Update file: %s", fileName));
			StringBuilder sqlBuilder = new StringBuilder(
					"select UPDATE_TIME FROM BSM_ETL WHERE ETL_NAME='%s' AND CREATE_USER='%s'");
			String sql = String.format(sqlBuilder.toString(), fileName,
					userName);
			System.out.println(sql);
			ResultSet res = DBUtil.executeQuery(sql);
			Timestamp timestamp = null;
			while (res.next()) {
				timestamp = res.getTimestamp(1);
			}
			Assert.assertNotNull("Verify transform is updated", timestamp);
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Ignore
	@Test(timeout = 600000)
	// TransformMgrCase0500:User can download a transform.
	public void testExtractionMgr0500() {
		fileName = "TestTransformDownload.ktr";

		// set up
		CommonUtil.addTransformFromBackend(userName, true, fileName);
		// CommonUtil.sleep(60);

		try {
			String targetfileName = String.format("temp_%s.ktr",
					CommonUtil.getRandomStr());
			// Test execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.transMgrPage.downloadTransformItem(fileName,
					testDataDir + targetfileName);

			// Verify the file exists on client side;
			File f = new File(testDataDir + targetfileName);
			Assert.assertTrue("Verify the specified file could be downloaded",
					f.exists());
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase0600:User can remove transform which is not used to any
	// event.
	public void testExtractionMgr0600() {
		fileName = "TestTransformRemove.ktr";

		// set up
		CommonUtil.addTransformFromBackend(userName, true, fileName);
		// CommonUtil.sleep(60);
		// test execution
		try {
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.removeTransform(fileName);
			Assert.assertEquals("Verify the prompt message.",
					"Are you sure to remove transform?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation message",
					"Remove transform successfully.", msg);
			Assert.assertFalse(
					"Verify the transform should not be present in transform list",
					extrMgrTasks.transMgrPage.isTransformPresent(fileName));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase0700:User must choose a transform when remove a transform
	public void testExtractionMgr0700() {
		fileName = "TestTransformRemove1.ktr";

		// set up
		CommonUtil.addTransformFromBackend(userName, true, fileName);
		// CommonUtil.sleep(60);
		// test execution
		try {
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.removeTransform();
			Assert.assertEquals("Verify the prompt message.",
					"Please select transforms to remove.", msg);
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase0800:User can remove multiple transforms which are not
	// used to any transform scheduel event one-off.
	public void testExtractionMgr0800() {
		String filelist[] = { "TestTransformRemove2.ktr",
				"TestTransformRemove3.ktr", "TestTransformRemove4.ktr" };

		// set up
		CommonUtil.addTransformFromBackend(userName, true, filelist);
		// CommonUtil.sleep(60);
		// test execution
		try {
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.removeTransform(filelist);
			Assert.assertEquals("Verify the prompt message.",
					"Are you sure to remove transform?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation message",
					"Remove transform successfully.", msg);
			for (String trans : filelist) {
				Assert.assertFalse(
						"Verify the transform should not be present in transfrom list",
						extrMgrTasks.transMgrPage.isTransformPresent(trans));
			}

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), filelist);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase0900:User can not remove transform which is used to an
	// event.
	public void testExtractionMgr0900() {
		fileName = "testExtractionMgr0900.ktr";
		CommonUtil.addTransformFromBackend(userName, true, fileName);
		try {
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.setSchedule("ExtractionMgr0900", fileName);
			extrMgrTasks.transMgrPage.checkOnTransform(fileName);
			extrMgrTasks.transMgrPage.ClickRemoveTransform();
			CommonUtil.sleep(3);
			String msg;			
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			CommonUtil.sleep(3);
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertTrue("Verify the prompt message.",
					msg.contains("can not be removed."));
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			Assert.assertTrue(
					"Verify the transform still is present in transform list",
					extrMgrTasks.transMgrPage.isTransformPresent(fileName));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			ExtraScheduleMgrTasks.cleanEventDB();
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
		
	}

	@Test(timeout = 600000)
	// TransformMgrCase1000:Transform which is not used to any transform
	// schedule event will not be removed if Cancel.
	public void testExtractionMgr1000() {
		fileName = "TestTransformRemove5.ktr";

		// set up
		CommonUtil.addTransformFromBackend(userName, true, fileName);
		// CommonUtil.sleep(60);
		// test execution
		try {
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.transMgrPage.checkOnTransform(fileName);
			extrMgrTasks.transMgrPage.ClickRemoveTransform();
			CommonUtil.sleep(3);
			String msg;
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals("Verify the prompt message.",
					"Are you sure to remove transform?", msg);
			extrMgrTasks.transMgrPage.cancelAlertWindow();
			Assert.assertTrue(
					"Verify the transform still is present in transform list",
					extrMgrTasks.transMgrPage.isTransformPresent(fileName));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase1100:User can execute a transform.
	public void testExtractionMgr1100() {
		fileName = "TransformOraToMongo.ktr";

		try {
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			if(!extrMgrTasks.isTfmExisted(fileName)){
				logger.info("Add Transform TransformExecuteToMongo.ktr first.");
				extrMgrTasks.addTransform(testDataDir, fileName);
			}			
			logger.info("Clean Mongo DB");
			DBUtil.getMongoConnection();
			if (DBUtil.isMongoCollectionExist("cor_device_test_des")) {
				DBUtil.removeMongoCollection("cor_device_test_des");
			}
			extrMgrTasks.transMgrPage.checkOnTransform(fileName);
			extrMgrTasks.transMgrPage.clickExecuteTransform(fileName);
			CommonUtil.sleep(20);
			String msg;
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals("Verify the prompt message.",
					"Execute successfully.", msg);
			Assert.assertTrue("Verify data transformed",
					DBUtil.isMongoCollectionExist("cor_device_test_des"));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			DBUtil.removeMongoCollection("cor_device_test_des");
			DBUtil.closeMongoConnection();
			CommonUtil.rmTransformFromBackend(userName, fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase1200:User can set schedule for certain transform
	public void testExtractionMgr1200() {
		fileName = "TransformOraToOra.ktr";
		CommonUtil.addTransformFromBackend(userName, true, fileName);
		try {
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.setSchedule("ExtractionMgr1200", fileName);
			Assert.assertTrue((new ExtraScheduleMgrTasks()).verifyExtEventExistsInDB("ExtractionMgr1200"));
		}catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			DBUtil.removeMongoCollection("SDP_NMP_Clients");
			DBUtil.closeMongoConnection();
			ExtraScheduleMgrTasks.cleanEventDB();
			CommonUtil.rmTransformFromBackend(userName, fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase1300:User cannot only upload .ktr file.
	public void testExtractionMgr1300() {
		fileName = "1024x768.jpg";

		try {
			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.transMgrPage.ClickAddTransform();
			Assert.assertTrue("Verify the upload window should be poped up.",
					extrMgrTasks.transMgrPage.isWindowPresent("Upload"));

			RptUpload rptUpload = new RptUpload(
					extrMgrTasks.transMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir + fileName);
			rptUpload.clickUpload();
			CommonUtil.sleep(6);
			String msg = null;
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals("Verify the pop up message",
					"You may upload only .ktr file,please retry.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
		}

	}

	@Test(timeout = 600000)
	// TransformMgrCase1400:Paging bar works correctly on Transform Manager.
	public void testExtractionMgr1400() {
		String fileName = "_TestTransform1400.ktr";
		String[] newTransName = new String[35];

		String pattern = "Records from %d to %d";
		String expectedText = "";

		try {
			// Test SetUp, add transforms

			for (int i = 0; i < 35; i++) {

				newTransName[i] = ((new Integer(i).toString() + fileName));

			}
			CommonUtil.addTransformFromBackend(userName, false, newTransName);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);

			// Verification
			logger.info("Verifiy the First page.");
			Assert.assertFalse("Verify First button is disabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 1, 27);
			Assert.assertEquals("Verify the current paging info",
					extrMgrTasks.transMgrPage.getCurrentPagingInfo(),
					expectedText);

			logger.info("Click Last and verifiy.");
			extrMgrTasks.transMgrPage.clickNavItem("Last");
			logger.info("Verifiy the page.");
			Assert.assertTrue("Verify First button is enabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("First"));
			Assert.assertTrue("Verify Previous button is enabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("Previous"));
			Assert.assertFalse("Verify Next button is disabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("Next"));
			Assert.assertFalse("Verify Last button is disabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("Last"));

			logger.info("Click First and verifiy.");
			extrMgrTasks.transMgrPage.clickNavItem("First");
			logger.info("Verifiy the page.");
			Assert.assertFalse("Verify First button is disabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled.",
					extrMgrTasks.transMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 1, 27);
			Assert.assertEquals("Verify the current paging info",
					extrMgrTasks.transMgrPage.getCurrentPagingInfo(),
					expectedText);
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), newTransName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase1500:User can not remove multiple transforms (one is used
	// to an event ,the other is not used to event).
	public void testExtractionMgr1500() {
		String fileName[] = {"TransformOraToOra.ktr","TestTransformRemove2.ktr",
				"TestTransformRemove3.ktr"};
		CommonUtil.addTransformFromBackend(userName, true, fileName);
		try {
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.setSchedule("ExtractionMgr1500", fileName[0]);			
			CommonUtil.sleep(3);			
			String msg;
			extrMgrTasks.removeTransform(fileName);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			CommonUtil.sleep(3);
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertTrue("Verify the prompt message.",
					msg.contains("can not be removed."));			
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			Assert.assertTrue(
					"Verify the transform still is present in transform list",
					extrMgrTasks.transMgrPage.isTransformPresent(fileName[0]));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			ExtraScheduleMgrTasks.cleanEventDB();
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase1600:Default status of transform which is uploaded is
	// private.
	public void testExtractionMgr1600() {
		fileName = "TestTransformAdd1.ktr";

		try {
			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.addTransform(testDataDir, fileName);
			Assert.assertFalse("Verify transform is not Generic",
					extrMgrTasks.isTransformGeneric(fileName));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase1700:User can set a transfrom to public which is uploaded
	// by himself
	public void testExtractionMgr1700() {
		String fileName = "TestTransform1700.ktr";

		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, false, fileName);
			// CommonUtil.sleep(60);
			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.setTransformPublic(fileName);
			Assert.assertEquals(
					"Are you sure to set selected transforms generic?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals("Selected transform has been set to generic.",
					msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			br.closeBrowser();
			br.launchBrowser();
			// open login page
			lg.loginPage=br.getURL();
			// login
			rptCenterPg = lg.login(user1, pw1);
			TransformMgrPage transMgrPage2 = rptCenterPg.goToTransformMgr();
			ExtractionMgrTasks extrMgrTasks2 = new ExtractionMgrTasks(
					transMgrPage2);
			Assert.assertTrue("Verify transform is Generic",
					extrMgrTasks2.isTransformGeneric(fileName));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase1800:User can set mutiple transfroms to public which are
	// uploaded by himself
	public void testExtractionMgr1800() {
		String fileName[] = { "TestTransform1800a.ktr",
				"TestTransform1800b.ktr", "TestTransform1800c.ktr" };

		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, false, fileName);
			// CommonUtil.sleep(60);
			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.setTransformPublic(fileName);
			Assert.assertEquals(
					"Are you sure to set selected transforms generic?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals("Selected transform has been set to generic.",
					msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			br.closeBrowser();
			br.launchBrowser();
			// open login page
			lg.loginPage=br.getURL();
			// login
			rptCenterPg = lg.login(user1, pw1);
			TransformMgrPage transMgrPage2 = rptCenterPg.goToTransformMgr();
			ExtractionMgrTasks extrMgrTasks2 = new ExtractionMgrTasks(
					transMgrPage2);
			for (String trans : fileName) {
				Assert.assertTrue("Verify transform is Generic",
						extrMgrTasks2.isTransformGeneric(trans,userName));
			}
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase1900:User can set a transfrom to private which is
	// uploaded by himself
	public void testExtractionMgr1900() {
		String fileName = "TestTransform1900.ktr";

		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName);
			// CommonUtil.sleep(60);
			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.setTransformPrivate(fileName);
			Assert.assertEquals(
					"Are you sure to set selected transforms non-generic?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals(
					"Selected transform has been set to non-generic.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			Assert.assertFalse("Verify transform is non-Generic",
					extrMgrTasks.isTransformGeneric(fileName));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase2000:User can set mutiple transfroms to private which are
	// uploaded by himself
	public void testExtractionMgr2000() {
		String fileName[] = { "TestTransform2000a.ktr",
				"TestTransform2000b.ktr", "TestTransform2000c.ktr" };

		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName);
			// CommonUtil.sleep(60);
			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.setTransformPrivate(fileName);
			Assert.assertEquals(
					"Are you sure to set selected transforms non-generic?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals(
					"Selected transform has been set to non-generic.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			for (String file : fileName) {
				Assert.assertFalse("Verify transform is non-Generic",
						extrMgrTasks.isTransformGeneric(file));
			}
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase2100:User can set a transfrom to private which is
	// uploaded by another user.
	public void testExtractionMgr2100() {
		String fileName = "TestTransform2100.ktr";

		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName);
			// CommonUtil.sleep(60);
			br.closeBrowser();
			br.launchBrowser();
			// open login page
			lg.loginPage=br.getURL();
			// login
			rptCenterPg = lg.login(user1, pw1);
			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.setTransformPrivate(fileName);
			Assert.assertEquals(
					"Are you sure to set selected transforms non-generic?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();			
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals(
					"Selected transform has been set to non-generic.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			CommonUtil.sleep(3);
			Assert.assertFalse("Verify transform is non-Generic",
					extrMgrTasks.isTransformGeneric(fileName,userName));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {		
		//	CommonUtil.rmTransformFromBackend(
		//			CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase2200:User can set multiple transfroms to private which is
	// uploaded by another user
	public void testExtractionMgr2200() {
		String fileName[] = { "TestTransform2200a.ktr",
				"TestTransform2200b.ktr", "TestTransform2200c.ktr" };

		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName);
			// CommonUtil.sleep(60);

			// Test Execution
			br.closeBrowser();
			br.launchBrowser();
			// open login page
			lg.loginPage=br.getURL();
			// login
			rptCenterPg = lg.login(user1, pw1);
			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.setTransformPrivate(fileName);
			Assert.assertEquals(
					"Are you sure to set selected transforms non-generic?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals(
					"Selected transform has been set to non-generic.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();			
			CommonUtil.sleep(3);
			for (String file : fileName) {
				Assert.assertFalse("Verify transform is non-Generic",
						extrMgrTasks.isTransformGeneric(file,userName));
			}
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase2300:User can remove a private transform which is
	// uploaded by himself.
	public void testExtractionMgr2300() {

		String fileName = "TestTransform2300.ktr";

		try {
			// set up
			CommonUtil.addTransformFromBackend(user1, false, fileName);
			// CommonUtil.sleep(60);

			// Test Execution
			br.closeBrowser();
			br.launchBrowser();
			// open login page
			lg.loginPage=br.getURL();
			// login
			rptCenterPg = lg.login(user1, pw1);
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.removeTransform(fileName);
			Assert.assertEquals("Are you sure to remove transform?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals("Remove transform successfully.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			CommonUtil.sleep(3);
			Assert.assertFalse("Verify transform is not in transform list.",
					extrMgrTasks.isTfmExisted(fileName,user1));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase2400:User can remove a public transform which is uploaded
	// by himself
	public void testExtractionMgr2400() {
		String fileName = "TestTransform2400.ktr";

		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName);
			// CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.removeTransform(fileName);
			Assert.assertEquals("Are you sure to remove transform?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals("Remove transform successfully.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			Assert.assertFalse("Verify transform is not in transform list.",
					extrMgrTasks.transMgrPage.isTransformPresent(fileName));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase2500:User can remove mutiple transforms which are
	// uploaded by himsef. (one is private and one is public).
	public void testExtractionMgr2500() {
		String fileName[] = { "TestTransform2500a.ktr",
				"TestTransform2500b.ktr" };

		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName[0]);
			CommonUtil.addTransformFromBackend(userName, true, fileName[1]);
			// CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.removeTransform(fileName);
			Assert.assertEquals("Are you sure to remove transform?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals("Remove transform successfully.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			for (String file : fileName) {
				Assert.assertFalse(
						"Verify transform is not in transform list.",
						extrMgrTasks.transMgrPage.isTransformPresent(file));
			}
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase2600:User can remove a public transform which is uploaded
	// by another user
	public void testExtractionMgr2600() {
		String fileName = "TestTransform2600.ktr";

		try {
			// set up
			CommonUtil.addTransformFromBackend(user1, true, fileName);
			// CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.removeTransform(fileName);
			Assert.assertEquals("Are you sure to remove transform?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals("Remove transform successfully.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			Assert.assertFalse("Verify transform is not in transform list.",
					extrMgrTasks.transMgrPage.isTransformPresent(fileName));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase2700:User can remove multiple transforms one is private
	// and one is public and uploaded by another user.
	public void testExtractionMgr2700() {
		String fileName[] = { "TestTransform2700a.ktr",
				"TestTransform2700b.ktr" };

		try {
			// set up
			CommonUtil.addTransformFromBackend(user1, true, fileName[0]);
			CommonUtil.addTransformFromBackend(userName, false, fileName[1]);
			// CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.removeTransform(fileName);
			Assert.assertEquals("Are you sure to remove transform?", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals("Remove transform successfully.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
			for (String file : fileName) {
				Assert.assertFalse(
						"Verify transform is not in transform list.",
						extrMgrTasks.transMgrPage.isTransformPresent(file));
			}
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase2800:Different users can upload transform with same name
	// and the transforms won't be overwrite .
	public void testExtractionMgr2800() {
		String fileName = "TestTransform2800.ktr";

		try {
			// set up
			CommonUtil.addTransformFromBackend(user1, true, fileName);
			// CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.addTransform(testDataDir, fileName);
			Assert.assertEquals(
					"Verify there are two transform with same name in transform list.",
					2, extrMgrTasks.transMgrPage.getTransformCount(fileName));
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(user1, fileName);
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase2900:User (not admin) can only see transform which is
	// public or uploded by himself when create an event .
	public void testExtractionMgr2900() {
		String fileName[] = { "TestTransform2900a.ktr",
				"TestTransform2900b.ktr", "TestTransform2900c.ktr",
				"TestTransform2900d.ktr", };
		String user2900 = CommonUtil.createUserDataToDb("user", true);
		CommonUtil.associateRoleDataWithUserData(user2900, role1);
		try {
			br.closeBrowser();
			br.launchBrowser();
			// open login page
			lg.loginPage=br.getURL();
			// login
			rptCenterPg = lg.login(user2900, pw1);
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			int iniCount = extrMgrTasks.transMgrPage
					.getTransformTotalCount();
			CommonUtil.addTransformFromBackend(userName, false, fileName[0]);
			CommonUtil.addTransformFromBackend(userName, true, fileName[1]);
			CommonUtil.addTransformFromBackend(userName, false, fileName[2]);
			CommonUtil.addTransformFromBackend(user2900, false, fileName[0]);
			CommonUtil.addTransformFromBackend(user2900, false, fileName[2]);
			CommonUtil.addTransformFromBackend(user2900, true, fileName[3]);
			br.refreshPage();
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			int actCountPage = extrMgrTasks.transMgrPage
					.getTransformTotalCount(); // get transform total count from
												// page
			Assert.assertEquals("Verify form Page, 4 new transform display.",
					4, actCountPage - iniCount);

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(user2900, fileName[0], fileName[2],
					fileName[3]);
			CommonUtil.rmTransformFromBackend(userName, fileName[0],
					fileName[1]);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase3000:admin can see all the transforms when create an
	// event .
	public void testExtractionMgr3000() {
		String fileName[] = { "TestTransform3100a.ktr",
				"TestTransform3100b.ktr", "TestTransform3100c.ktr",
				"TestTransform3100d.ktr", };

		try {
			int iniCount = DBUtil.getRowCountOfTable("BSM_ETL");
			CommonUtil.addTransformFromBackend("admin", false, fileName[0]);
			CommonUtil.addTransformFromBackend("admin", true, fileName[1]);
			CommonUtil.addTransformFromBackend(user1, false, fileName[0]);
			CommonUtil.addTransformFromBackend(user1, false, fileName[2]);
			CommonUtil.addTransformFromBackend(user1, true, fileName[3]);
			int actCount = DBUtil.getRowCountOfTable("BSM_ETL"); // get transform
																// total count
																// from DB			
			Assert.assertEquals("Verify form DB, 5 new transform created.", 5,
					actCount - iniCount);
			br.closeBrowser();
			br.launchBrowser();
			lg.loginPage = br.getURL();
			rptCenterPg = lg.login("admin", "admin");
			ExtraScheduleMgrTasks extraSchTasks = new ExtraScheduleMgrTasks();
			extraSchTasks.goToExtraScheduleMgr(rptCenterPg);
			extraSchTasks.openNewEventWindow();
			try{
				extraSchTasks.selectTransforms(fileName);
			}catch(Exception e){
				Assert.fail();
			}			
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(user1, fileName[0], fileName[2],
					fileName[3]);
			CommonUtil
					.rmTransformFromBackend("admin", fileName[0], fileName[1]);
		}
	}

	@Test(timeout = 600000)
	// TransformMgrCase3100:admin can see all the transforms on extraction
	// manager page.
	public void testExtractionMgr3100() {
		String fileName[] = { "TestTransform3100a.ktr",
				"TestTransform3100b.ktr", "TestTransform3100c.ktr",
				"TestTransform3100d.ktr", };

		try {
			int iniCount = DBUtil.getRowCountOfTable("BSM_ETL");
			CommonUtil.addTransformFromBackend("admin", false, fileName[0]);
			CommonUtil.addTransformFromBackend("admin", true, fileName[1]);
			CommonUtil.addTransformFromBackend(user1, false, fileName[0]);
			CommonUtil.addTransformFromBackend(user1, false, fileName[2]);
			CommonUtil.addTransformFromBackend(user1, true, fileName[3]);
			int actCount = DBUtil.getRowCountOfTable("BSM_ETL"); // get transform
																// total count
																// from DB
			Assert.assertEquals("Verify form DB, 5 new transform created.", 5,
					actCount - iniCount);
			br.closeBrowser();
			br.launchBrowser();
			lg.loginPage = br.getURL();
			rptCenterPg = lg.login("admin", "admin");
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			int actCountPage = extrMgrTasks.transMgrPage
					.getTransformTotalCount(); // get transform total count from
												// page
			Assert.assertEquals("Verify form Page, 5 new transform created.",
					5, actCountPage - iniCount);

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(user1, fileName[0], fileName[2],
					fileName[3]);
			CommonUtil
					.rmTransformFromBackend("admin", fileName[0], fileName[1]);
		}

	}

	@Test(timeout = 600000)
	// TransformMgrCase3200:User must choose one or more transforms if he want
	// to set private transform to public
	public void testExtractionMgr3200() {
		String fileName = "TestTransform3200.ktr";

		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName);
			// CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.setTransformPrivate();
			Assert.assertEquals("Please select transforms to operate.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// /TransformMgrCase3300:User must choose one or more transforms if he want
	// to set public transform to private
	public void testExtractionMgr3300() {
		String fileName = "TestTransform3300.ktr";
		try {
			// set up
			CommonUtil.addTransformFromBackend(userName, false, fileName);
			// CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			String msg = extrMgrTasks.setTransformPublic();
			Assert.assertEquals("Please select transforms to operate.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// /TransformMgrCase3400:User is only allowed to upload transform with the
	// same name,when user update a transform.
	public void testExtractionMgr3400() {
		String fileName = "TestTransform3400.ktr";

		try {

			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName);
			// CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.transMgrPage.clickUpdateTransform(fileName);
			Assert.assertTrue("Verify the upload window should be poped up.",
					extrMgrTasks.transMgrPage.isWindowPresent("Upload"));

			RptUpload rptUpload = new RptUpload(
					extrMgrTasks.transMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir + "TestTransformAdd.ktr");
			String msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals(
					"You may only update a PDI transform with same name.", msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// /TransformMgrCase3500:User is only allowed to upload only one transform
	// with the same name,when user update a transform.
	public void testExtractionMgr3500() {
		String fileName = "TestTransform3500.ktr";

		try {

			// set up
			CommonUtil.addTransformFromBackend(userName, true, fileName);
			// CommonUtil.sleep(60);

			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.transMgrPage.clickUpdateTransform(fileName);
			Assert.assertTrue("Verify the upload window should be poped up.",
					extrMgrTasks.transMgrPage.isWindowPresent("Upload"));
			RptUpload rptUpload = new RptUpload(
					extrMgrTasks.transMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir + fileName);
			Assert.assertTrue(rptUpload.isFileListedInContainer(fileName));
			CommonUtil.sleep(3);
			// Verification: The Add File Button should be disabled
			Assert.assertFalse("The Button Add File should be disabled.",
					rptUpload.isAddBtnEnabled());
			rptUpload.closeWindow();

		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}

	@Test(timeout = 600000)
	// /TransformMgrCase3600:User can not upload two transforms with the same
	// name,when he addes transforms.
	public void testExtractionMgr3600() {
		String fileName = "TestTransform3600.ktr";

		try {
			// Test Execution
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.transMgrPage.ClickAddTransform();
			Assert.assertTrue("Verify the upload window should be poped up.",
					extrMgrTasks.transMgrPage.isWindowPresent("Upload"));
			RptUpload rptUpload = new RptUpload(
					extrMgrTasks.transMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir + fileName);
			Assert.assertTrue(rptUpload.isFileListedInContainer(fileName));
			CommonUtil.sleep(3);
			rptUpload.setFilePath(testDataDir + fileName);
			String msg = extrMgrTasks.transMgrPage.getTextFromAlertWindow();
			Assert.assertEquals(
					"A PDI transform with same name has been added in upload list.",
					msg);
			extrMgrTasks.transMgrPage.acceptAlertWindow();
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {			
			CommonUtil.rmTransformFromBackend(
					CommonUtil.getPropertyValue("username"), fileName);
		}
	}
	
	@Test
	public void testExtractionMgr3700() {		
		String rptzip = "Transform.zip";		
		String tfm1 = "TransformOraToOra3.ktr";
		String tfm2 = "TransformOraToOra4.ktr";
		try {
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.batchUpload(testDataDir, rptzip);
			Assert.assertTrue(extrMgrTasks.isTfmExisted(tfm1));
			Assert.assertTrue(extrMgrTasks.isTfmExisted(tfm2));			
		} finally {
			br.closeBrowser();
		}
	}
	
	@Test
	public void testExtractionMgr3800() {		
		String tfmzip = "Transform.zip";
		String tfm = "TransformOraToOra3.ktr";		
		try {					
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			extrMgrTasks.batchUpload(testDataDir, tfmzip);
			Assert.assertTrue(extrMgrTasks.isTfmUpdated(tfm, "admin"));			
		} finally {
			br.closeBrowser();
		}
	}
	
	@Test
	public void testExtractionMgr3900() {
		String username = CommonUtil.getPropertyValue("username");
		String password = CommonUtil.getPropertyValue("password");
		String tfmzip = "TransformOraToMongo.ktr";
		try {

			extrMgrTasks.gotoExtractionManager(rptCenterPg);

			extrMgrTasks.transMgrPage.clickBatchUpload();
			RptUpload rptUpload = new RptUpload(extrMgrTasks.transMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir + tfmzip);
			rptUpload.clickUpload();
			String expectedErrMsg = "You may upload only .zip file,please retry.";

			// Verification: The Add File Button should be disabled
			Assert.assertEquals("Verify the error message.", expectedErrMsg,
					rptUpload.getTextFromAlertWindow());
		} catch (Exception e) {
			CommonUtil.logError(e, testName);
		} finally {
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testExtractionMgr4000(){
		String transform = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";
		String msg;
		try{
			//clean up
			ExtraScheduleMgrTasks extrSchtasks = new ExtraScheduleMgrTasks();
			extrSchtasks.removeMongoCollection(desCollection);
			
			extrMgrTasks.gotoExtractionManager(rptCenterPg);
			
			if(!extrMgrTasks.isTfmExisted(transform)){
				extrMgrTasks.addTransform(testDataDir, transform);
			}
			int index = extrSchtasks.getIndexMaxNum(srcTable, "DEV_UID");
			String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";				
			for (int i = 0; i < 3; i++) {
				msg = extrMgrTasks.executeTransform(transform);
				if (!msg.contains("successfully")){
					Assert.fail("Execute failed");
				}
				Assert.assertTrue(extrSchtasks.verifyRecordCountInMongo(srcTable, desCollection));				
				index = index +1;								
				extrSchtasks.inserRowInOracle(sql,index);
			}						
		}finally{
			
		}
	}
}
