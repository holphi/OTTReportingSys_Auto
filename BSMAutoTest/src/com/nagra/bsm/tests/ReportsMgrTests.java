package com.nagra.bsm.tests;

import java.io.File;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;


import junit.framework.Assert;

import com.nagra.bsm.ui.*;
import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.tasks.BrowserTasks;
import com.nagra.bsm.tasks.LoginTasks;
import com.nagra.bsm.tasks.PrivilegeTasks;
import com.nagra.bsm.tasks.RptMgrTasks;
import com.nagra.bsm.tasks.RptScheduleMgrTasks;
import com.nagra.bsm.tests.categorymarker.SanityCheck;

public class ReportsMgrTests extends BaseTests {
	
	private String testDataDir = CommonUtil.getCurrDir()+"\\testdata\\RptTemplate\\";
			
	//Utility method for quick navigation to test page.
	private RptMgrPage navigateToTestPage(String username, String password)
	{
		logger.info("Login in with User:" + username + ", Password:" + password);

		LoginPage loginPage = new LoginPage(CommonUtil.launchTestPortal());

		RptCenterPage rptCenterPage= loginPage.loginAs(username, password);
		
		logger.info("Click Reports Manager to go to Reports Mgr tab.");
		return rptCenterPage.goToReportMgr();
	}
	
	
	@AfterClass
	public static void suiteTearDown()
	{
		CommonUtil.rmGeneratedRpt(CommonUtil.getPropertyValue("username"));
		CommonUtil.rmBirtRptDataFromDb(CommonUtil.getPropertyValue("username"));
	}
	
	@Before
	public void testSetup(){
		logger.info("============================="+testName.getMethodName()+"==============================");
	}
	
	@Test(timeout=600000)
	@Category(SanityCheck.class)
	public void testRptMgr0100()
	{		
		String fileName = "TestReportAdd.rptdesign";
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		try
		{	
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username,password);
			
			rptMgrPage.clickAddRpt();
			Assert.assertTrue("Verify the upload window should be poped up.", rptMgrPage.isWindowPresent("Upload"));
			
			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
			
			rptUpload.setFilePath(testDataDir+fileName);
			rptUpload.clickUpload();
			CommonUtil.sleep(6);
			Assert.assertTrue(rptUpload.isFileListedInContainer(fileName));
			Assert.assertEquals("Done", rptUpload.getUploadMsg(fileName));
			
			rptUpload.closeWindow();
//			rptMgrPage.clickNavItem("Last");
			
			Assert.assertTrue("Verify the upload has been uploaded to specified folder.",rptMgrPage.isRptItemPresent(fileName));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{fileName}, username);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr0200()
	{
		String fileName = "TestReportAdd.rptdesign";
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(new String[]{fileName}, username, true);
			CommonUtil.sleep(60);
			//String fileDiskName = CommonUtil.getRptDiskName(fileName, username);			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			
			rptMgrPage.clickAddRpt();
			Assert.assertTrue("Verify the upload window should be poped up.", rptMgrPage.isWindowPresent("Upload"));
			
			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
			
			rptUpload.setFilePath(testDataDir+fileName);
			rptUpload.clickUpload();
			CommonUtil.sleep(6);
			String msg = rptMgrPage.getTextFromAlertWindow();
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the pop up message", "You have uploaded a report template with same name.", msg);
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{fileName}, username);
		}
	}

	@Test(timeout=600000)
	public void testRptMgr0300()
	{
		String[] fileList = new String[]{"TestReportAdd3.rptdesign", "TestReportAdd4.rptdesign", "TestReportAdd5.rptdesign"};
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		try
		{
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);		
			rptMgrPage.clickAddRpt();

			Assert.assertTrue("Verify the upload window should be poped up.", rptMgrPage.isWindowPresent("Upload"));
			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
			
			for(String file:fileList)
			{
				String fullName = testDataDir + file;
				logger.info(String.format("Add file: %s",fullName));
				rptUpload.setFilePath(fullName);
			}
			rptUpload.clickUpload();
			
			//Verification
			for(String file:fileList)
				Assert.assertEquals(String.format("Verify the status of the file %s should be Done.",file), "Done", rptUpload.getUploadMsg(file));
			CommonUtil.sleep(6);
			rptUpload.closeWindow();
			
			rptMgrPage.clickNavItem("Last");
			for(String file:fileList)
				Assert.assertTrue(String.format("Verify the file %s should be in the list.",file), rptMgrPage.isRptItemPresent(file));
						
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(fileList, username);
		}
	}
	
	@Test(timeout=600000)
	@Category(SanityCheck.class)
	public void testRptMgr0400()
	{
		String fileName = "TestReportAdd.rptdesign";
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(new String[]{fileName}, username, true);
			CommonUtil.sleep(60);
			String rptDiskName = CommonUtil.getRptDiskName(fileName, username);
			String orginDate = CommonUtil.getTimestamp(rptDiskName);

			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			
			rptMgrPage.clickNavItem("Last");
			rptMgrPage.clickUpdateRpt(fileName);
			Assert.assertTrue("Verify the upload window should be poped up.", rptMgrPage.isWindowPresent("Upload"));
			
			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir+fileName);
			rptUpload.clickUpload();
			CommonUtil.sleep(6);
			
			Assert.assertTrue(rptUpload.isFileListedInContainer(fileName));
			Assert.assertEquals("Done", rptUpload.getUploadMsg(fileName));
			
			rptUpload.closeWindow();

			String updatedDate = CommonUtil.getTimestamp(rptDiskName);
			Assert.assertFalse("Verify the file has been updated.", orginDate.equals(updatedDate));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{fileName}, username);
		}
	}
	
	
	@Ignore	
	@Test(timeout=300000)
	@Category(SanityCheck.class)
	public void testRptMgr0500()
	{		
		String fileName = "TestReportAdd.rptdesign";
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		//Set-up
		CommonUtil.createDumbBirtRpts(new String[]{fileName}, username, true);
		CommonUtil.sleep(60);
		String rptDiskName = CommonUtil.getRptDiskName(fileName, username);
		String orginDate = CommonUtil.getTimestamp(rptDiskName);

		try
		{
			String targetfileName = String.format("temp_%s.rptdesign", CommonUtil.getRandomStr());
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
			 		 								   CommonUtil.getPropertyValue("password"));
			
			rptMgrPage.downloadRptItem(fileName, testDataDir+targetfileName);
			
			//Verify the file exists on client side;
			File f = new File(testDataDir+targetfileName);
			Assert.assertTrue("Verify the specified file could be downloaded", f.exists());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}

	
	@Ignore	
	@Test(timeout=300000)
	@Category(SanityCheck.class)
	public void testRptMgr0600()
	{
		String fileName = "TestReportAdd.rptdesign";
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		//Set-up
		CommonUtil.createDumbBirtRpts(new String[]{fileName}, username, true);
		CommonUtil.sleep(60);
		String rptDiskName = CommonUtil.getRptDiskName(fileName, username);
		String orginDate = CommonUtil.getTimestamp(rptDiskName);
		try
		{
			String targetfileName = String.format("temp_%s.rptdesign", CommonUtil.getRandomStr());
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
			 		 								   CommonUtil.getPropertyValue("password"));
			
			rptMgrPage.downloadRptItem(fileName, testDataDir+targetfileName);
			
			//Verify the file exists on client side;
			File f = new File(testDataDir+targetfileName);
			Assert.assertTrue("Verify the specified file could be downloaded", f.exists());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}	
	
	@Test(timeout=600000)
	@Category(SanityCheck.class)
	public void testRptMgr0700()
	{
		String eventName = "Rpt_event_"+CommonUtil.getRandomStr();
		String rptName = "Activation_count.rptdesign";
		try
		{
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
			 		 								   CommonUtil.getPropertyValue("password"));
			RptMgrTasks rptMgr = new RptMgrTasks(rptMgrPage);
			if(!rptMgr.isRptPresent(rptName)){
				rptMgr.addRpt(testDataDir, rptName);				
			}								
			rptMgrPage.clickSetSchedule(rptName);			
			ScheduleOperation scheduleOp = new ScheduleOperation(rptMgrPage.getWebDriver());
			
			//Verify the specified report is listed in Schedule operation window.
			Assert.assertTrue("Verify the specified rpt is listed in Schedule operation window.", scheduleOp.isSpecifiedRptPresent(rptName));
			
			logger.info(String.format("On Schedule operation window: input new event name %s", eventName));
			scheduleOp.setEventName(eventName);
			logger.info(String.format("On Schedule operation window: check Excel for %s", rptName));
			scheduleOp.chkExcelFor(rptName);
			scheduleOp.setStartDate(getStartDate());
			scheduleOp.clickSave();
						
			//Verify from DB, check the rpt event is created successfully
			CommonUtil.sleep(3);
			Assert.assertTrue("Verify the rpt event is created successfully.", CommonUtil.isRptEventExistedInDb(eventName));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
			CommonUtil.rmScheduledEvent(eventName);
		}
	}
	
//	@Test(timeout=600000)           //Comment out this test cases.Beacuse feature is changed and other test cases will cover it.
	@Category(SanityCheck.class)
	public void testRptMgr0800()
	{
		String[] rptFileList = new String[]{"Activation_count_20120226_003500_1334653245502.zip", "Activation_count_20161226_003500_1334653228502.zip", "SDP_BUYS_20111226_004000_1334653228521.zip"};
		try
		{
			String rptName = "Activation_count.rptdesign";
			
			//Set-up
			CommonUtil.createDumbRptOutputFiles(rptFileList);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
					   								   CommonUtil.getPropertyValue("password"));
			
			rptMgrPage.clickViewRpt(rptName);
						
			GeneratedFiles g = new GeneratedFiles(rptMgrPage.getWebDriver());
			//Verify the window Generated Files should be present.
			String windowTxt = "Generated Files";
			Assert.assertTrue("Verify the window Generated Files should be present.", g.isWindowPresent(windowTxt));
			
			//Verify the first two items of report file list should be listed
			for(int i=0;i<rptFileList.length-1;i++)
				Assert.assertTrue(String.format("Verify the output report %s should be in the list", 
								  rptFileList[i]),g.isOutputRptPresent(rptFileList[i]));
			
			//Verify the last item should not be in the list
			Assert.assertFalse(String.format("Verify the output report %s should be in the list", 
					  rptFileList[2]),g.isOutputRptPresent(rptFileList[2]));
			
			//Verify the specified item could be downloaded
			String targetFileName = testDataDir + "tmp_" + CommonUtil.getRandomStr() + ".zip";
			logger.info(String.format("On Generated File window, click item %s",rptFileList[0]));
			g.downloadOutputRpt(rptFileList[0], targetFileName);
			
			File f = new File(targetFileName);
			Assert.assertTrue("Verify the specified file could be downloaded", f.exists());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
			CommonUtil.rmDumpRptOutputFiles(rptFileList);
		}
	}
	
	@Test(timeout=600000)
	@Category(SanityCheck.class)
	public void testRptMgr0900()
	{
		String rptName = "TestReportRemove.rptdesign";
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(new String[]{rptName}, username, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			
			rptMgrPage.clickNavItem("Last");
			rptMgrPage.checkRpt(rptName);
			rptMgrPage.clickRemoveRpt();
			
			String msg = rptMgrPage.getTextFromAlertWindow();
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the prompt message.", "Are you sure to remove report?", msg);
			
			msg = rptMgrPage.getTextFromAlertWindow();
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation message", "Remove report successfully.", msg);
			
			rptMgrPage.clickNavItem("Last");
			Assert.assertFalse("Verify the report should not be present in rpt list", rptMgrPage.isRptItemPresent(rptName));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{rptName}, username);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr0901()
	{
		String rptName = "TestReportRemove.rptdesign";
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(new String[]{rptName}, username, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			
			rptMgrPage.clickNavItem("Last");
			rptMgrPage.checkRpt(rptName);
			rptMgrPage.clickRemoveRpt();
						
			rptMgrPage.dissmissAlertWindow();
			
			Assert.assertTrue("Verify the rpt item should still be listed.", rptMgrPage.isRptItemPresent(rptName));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{rptName}, username);
		}
	}	
	
	@Test(timeout=600000)
	public void testRptMgr1000()
	{
		String[] rptList = new String[]{"ARemove1.rptdesign", "ARemove2.rptdesign", "ARemove3.rptdesign"};
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		
		try{
			//Set-up: Create 3 dumb rpt-design files
			CommonUtil.createDumbBirtRpts(rptList, username, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			
			rptMgrPage.clickNavItem("First");
			for(String rpt:rptList)
				rptMgrPage.checkRpt(rpt);

			rptMgrPage.clickRemoveRpt();
			rptMgrPage.acceptAlertWindow();
			rptMgrPage.acceptAlertWindow();
			
			rptMgrPage.clickNavItem("First");
			
			for(String rpt:rptList)
				Assert.assertFalse(String.format("Verify the file %s should not be in the list", rpt),
						           rptMgrPage.isRptItemPresent(rpt));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(rptList, username);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr1100()
	{					
			String fileName = "_TestRptMgr1100.rptdesign";
			String[] newRptName = new String[35];
			String username = CommonUtil.getPropertyValue("username");

			String pattern = "Records from %d to %d";
			String expectedText = "";

			try {
				// Test SetUp, add transforms

				for (int i = 0; i < 35; i++) {

					newRptName[i] = ((new Integer(i).toString() + fileName));

				}			
			
			CommonUtil.createDumbBirtRpts(newRptName, username, true);
				
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username,
			 		 								   CommonUtil.getPropertyValue("password"));
			
			//Verification
			logger.info("Verifiy the default page.");
			Assert.assertFalse("Verify First button is disabled.", rptMgrPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled.", rptMgrPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled.", rptMgrPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled.", rptMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 1, 27);
			Assert.assertEquals("Verify the current paging info", rptMgrPage.getCurrentPagingInfo(),expectedText);
			
			logger.info("Click Last and verifiy.");
			rptMgrPage.clickNavItem("Last");
			logger.info("Verifiy the page.");
			Assert.assertTrue("Verify First button is enabled.",rptMgrPage.isNavItemEnabled("First"));
			Assert.assertTrue("Verify Previous button is enabled.",rptMgrPage.isNavItemEnabled("Previous"));
			Assert.assertFalse("Verify Next button is disabled.",rptMgrPage.isNavItemEnabled("Next"));
			Assert.assertFalse("Verify Last button is disabled.",rptMgrPage.isNavItemEnabled("Last"));
			
			logger.info("Click First and verifiy.");
			rptMgrPage.clickNavItem("First");
			logger.info("Verifiy the page.");
			Assert.assertFalse("Verify First button is disabled.",rptMgrPage.isNavItemEnabled("First"));
			Assert.assertFalse("Verify Previous button is disabled.",rptMgrPage.isNavItemEnabled("Previous"));
			Assert.assertTrue("Verify Next button is enabled.",rptMgrPage.isNavItemEnabled("Next"));
			Assert.assertTrue("Verify Last button is enabled.",rptMgrPage.isNavItemEnabled("Last"));
			expectedText = String.format(pattern, 1, 27);
			Assert.assertEquals("Verify the current paging info", rptMgrPage.getCurrentPagingInfo(),expectedText);
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(newRptName, username);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr1200()
	{
		String rptName = "TestReportRemove2.rptdesign";
		String eventName = "Rpt_event_"+CommonUtil.getRandomStr();
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		try{
			//Set-up
			CommonUtil.createDumbBirtRpts(new String[]{rptName}, username, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			rptMgrPage.clickNavItem("Last");
			rptMgrPage.clickSetSchedule(rptName);

			ScheduleOperation scheduleOp = new ScheduleOperation(rptMgrPage.getWebDriver());
			
			logger.info(String.format("On Schedule operation window: input new event name %s", eventName));
			scheduleOp.setEventName(eventName);
			logger.info(String.format("On Schedule operation window: check Excel for %s", rptName));
			scheduleOp.chkExcelFor(rptName);
			scheduleOp.setStartDate(getStartDate());
			scheduleOp.clickSave();

			rptMgrPage.checkRpt(rptName);
			rptMgrPage.clickRemoveRpt();
			rptMgrPage.acceptAlertWindow();
			
			String msg = rptMgrPage.getTextFromAlertWindow();
			rptMgrPage.acceptAlertWindow();
			
			Hashtable<String,String> rptEvent = CommonUtil.getRptEventFromDb(eventName);
			String event_id = rptEvent.get("EVENT_ID");
			
			Assert.assertEquals("Verify the output error message.", String.format("The report %s is the last report of scheduler event %s(event id:%s) can not be removed.", rptName, eventName, event_id), 
								msg);	
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmScheduledEvent(eventName);
			CommonUtil.rmBirtRpts(new String[]{rptName}, username);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr1300()
	{
		String fileName = "a.doc";
		try
		{	
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
			 		 									   CommonUtil.getPropertyValue("password"));
			
			rptMgrPage.clickAddRpt();
			Assert.assertTrue("Verify the upload window should be poped up.", rptMgrPage.isWindowPresent("Upload"));
			
			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
			
			rptUpload.setFilePath(testDataDir+fileName);
			//rptUpload.clickUpload();
			CommonUtil.sleep(3);
			
			String msg = rptUpload.getTextFromAlertWindow();
			
			CommonUtil.sleep(3);
			rptUpload.acceptAlertWindow();
			rptUpload.closeWindow();
			CommonUtil.sleep(3);
			
			Assert.assertEquals("Verify the output error message.","You may upload only .rptdesign or .rptlibrary file,please retry.", msg);
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			//CommonUtil.rmRptDesignFileFromSrv(fileName);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr1400()
	{
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		String[] rptArr = new String[]{"A_Remove1.rptdesign", "A_Remove2.rptdesign", "A_Remove3.rptdesign"};
		String rptEventName = "RptMgr1400";
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(rptArr, username, true);	
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			
			RptMgrTasks rptTasks = new RptMgrTasks(rptMgrPage);
			
			rptTasks.setSchedule(rptArr[0], RptFormat.EXCEL, rptEventName);
			CommonUtil.sleep(2);
			
			String rptEventID =RptScheduleMgrTasks.getEventID(rptEventName);
			
			//Check on reports to be deleted one by one
			for(String rptName:rptArr)
				rptMgrPage.checkRpt(rptName);
			
			rptMgrPage.clickRemoveRpt();
			
			//Accept delete operation
			rptMgrPage.acceptAlertWindow();
			
			logger.info("Verify the error message of Alert window.");
			String actualMsg = rptMgrPage.getTextFromAlertWindow();
			rptMgrPage.acceptAlertWindow();
			String expectedMsg = String.format("The report %s is the last report of scheduler event %s(event id:%s) can not be removed.", rptArr[0], rptEventName, rptEventID);
			Assert.assertEquals("Verify the error message of Alert window", expectedMsg, actualMsg);
			
			logger.info("Verify the reports should not be removed.");
			
			for(String rpt : rptArr)
				Assert.assertTrue(String.format("Verify the report %s should be present.", rpt),rptMgrPage.isRptItemPresent(rpt));

		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			RptScheduleMgrTasks.rmRptEventFromBackend(rptEventName);
			CommonUtil.rmBirtRpts(rptArr, username);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr1500()
	{
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		String[] rptArr = new String[]{"A_Remove1.rptdesign", "A_Remove2.rptdesign", "A_Remove3.rptdesign"};
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(rptArr, username, true);
			CommonUtil.createRptEventFromDb(rptArr);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			
			//Check on reports to be deleted one by one
			rptMgrPage.checkRpt(rptArr[0]);
			rptMgrPage.checkRpt(rptArr[1]);
			
			rptMgrPage.clickRemoveRpt();
			
			//Accept delete operation
			rptMgrPage.acceptAlertWindow();
			
			logger.info("Verify the confirmation of Alert window.");
			String actualMsg = rptMgrPage.getTextFromAlertWindow();
			rptMgrPage.acceptAlertWindow();
			String expectedMsg = "Remove report successfully.";
			Assert.assertEquals("Verify the confirmation message of Alert window", expectedMsg, actualMsg);
			
			logger.info("Verify the reports should be removed");
			Assert.assertFalse(rptMgrPage.isRptItemPresent(rptArr[0]));
			Assert.assertFalse(rptMgrPage.isRptItemPresent(rptArr[1]));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmScheduledEvent();
			CommonUtil.rmBirtRpts(rptArr, username);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr1600()
	{
		String defaultPwd = "123";
		String user1="",role="";
		String rptName = "3080.rptdesign";
		try{	
			//Set-up
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
																	  "Add Report",
																	  "View Report Manager", 
																	  "Set Report Template To Public",
																	  "Set Report Template To Private"});
			
			user1 = CommonUtil.createUserDataToDb("user", true);
			CommonUtil.associateRoleDataWithUserData(user1, role);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(user1, defaultPwd);
			rptMgrPage.addReport(rptName);
			
			//Verification: 1. Verify it is "No" in Generic Report Template column of "3080.rptdesign".
			Assert.assertFalse(String.format("Verify the new added report %s should be Private defaultly.", rptName), rptMgrPage.isGenericRpt(rptName));
			
			//Test execution
			rptMgrPage.checkRpt(rptName);
			rptMgrPage.clickSetToPublic();
			
			//Verification: 2. Verify pop up message "Are you sure to set selected report templates generic?".
			String actualResult = rptMgrPage.getTextFromAlertWindow();
			String expectedResult = "Are you sure to set selected report templates generic?";
			Assert.assertEquals("Verify the confirmation text from Alert window.", expectedResult, actualResult);
			rptMgrPage.acceptAlertWindow();
			
			//Verification: 3. Verify pop up message "Selected Report templates has been set to generic".
			actualResult = rptMgrPage.getTextFromAlertWindow();
			expectedResult = "Selected Report templates has been set to generic.";
			Assert.assertEquals("Verify the confirmation text from Alert window.", expectedResult, actualResult);
			rptMgrPage.acceptAlertWindow();
			
			//Verification: 4. Verify it is "Yes" in Generic Report Template column of "3080.rptdesign".
			Assert.assertTrue(String.format("Verify the new added report %s should be updated to public.", rptName), rptMgrPage.isGenericRpt(rptName));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{rptName}, user1);
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr1700()
	{
		String defaultPwd = "123";
		String user1="",role="";
		String[] rptArr = new String[]{"3080.rptdesign", "8516.rptdesign"};
		try{	
			//Set-up
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
																	  "Add Report",
																	  "View Report Manager", 
																	  "Set Report Template To Public",
																	  "Set Report Template To Private"});
			
			user1 = CommonUtil.createUserDataToDb("user", true);
			CommonUtil.associateRoleDataWithUserData(user1, role);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(user1, defaultPwd);
			for(String rpt:rptArr)
				rptMgrPage.addReport(rpt);
			
			//Verification: 1. Verify it is "No" in Generic Report Template column of "3080.rptdesign".
			for(String rpt:rptArr)
				Assert.assertFalse(String.format("Verify the new added report %s should be Private defaultly.", rpt), rptMgrPage.isGenericRpt(rpt));
			
			//Test execution
			for(String rpt:rptArr)
				rptMgrPage.checkRpt(rpt);
			
			rptMgrPage.clickSetToPublic();
			
			//Verification: 2. Verify pop up message "Are you sure to set selected report templates generic?".
			String actualResult = rptMgrPage.getTextFromAlertWindow();
			String expectedResult = "Are you sure to set selected report templates generic?";
			Assert.assertEquals("Verify the confirmation text from Alert window.", expectedResult, actualResult);
			rptMgrPage.acceptAlertWindow();
			
			//Verification: 3. Verify pop up message "Selected Report templates has been set to generic".
			actualResult = rptMgrPage.getTextFromAlertWindow();
			expectedResult = "Selected Report templates has been set to generic.";
			Assert.assertEquals("Verify the confirmation text from Alert window.", expectedResult, actualResult);
			rptMgrPage.acceptAlertWindow();
			
			//Verification: 3. Verify it is "No" in Generic Report Template column of "3080.rptdesign".
			for(String rpt:rptArr)
				Assert.assertTrue(String.format("Verify the new added report %s should be Private defaultly.", rpt), rptMgrPage.isGenericRpt(rpt));
		
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(rptArr, user1);
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr1800()
	{
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		String[] rptArr = new String[]{"A_Remove1.rptdesign"};
		String user1="", role="";
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(rptArr, username, true);
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "View Report Manager"});
			user1 = CommonUtil.createUserDataToDb("user", true);
			CommonUtil.associateRoleDataWithUserData(user1, role);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			
			//Check on reports to be deleted one by one
			rptMgrPage.checkRpt(rptArr[0]);
			for(String rpt:rptArr)
				rptMgrPage.checkRpt(rpt);
			
			rptMgrPage.clickSetToPrivate();
			
			//Verification: 1. Verify pop up message "Are you sure to set selected report templates non-generic?".
			String actualResult = rptMgrPage.getTextFromAlertWindow();
			String expectedResult = "Are you sure to set selected report templates non-generic?";
			Assert.assertEquals("Verify the confirmation text from Alert window.", expectedResult, actualResult);
			rptMgrPage.acceptAlertWindow();
			
			//Verification: 2. Verify pop up message "Selected Report templates has been set to non-generic".
			actualResult = rptMgrPage.getTextFromAlertWindow();
			expectedResult = "Selected Report templates has been set to non-generic.";
			Assert.assertEquals("Verify the confirmation text from Alert window.", expectedResult, actualResult);
			rptMgrPage.acceptAlertWindow();
			
			//Verification: 3. Verify it is "No" in Generic Report Template column of "A_Remove1.rptdesign".
			for(String rpt:rptArr)
				Assert.assertFalse(String.format("Verify the report %s should be updated to Private.", rpt), rptMgrPage.isGenericRpt(rpt));
			
			//Close the browser and sign in with another user
			CommonUtil.closeBrowser();
			
			//Verification: 4. Login as user1 and verify user1 can not see "TestReportAdd1.rptdesign","TestReportAdd2.rptdesign", "TestReportAdd3.rptdesign" in report template list.
			rptMgrPage = navigateToTestPage(user1, "123");
			for(String rpt:rptArr)
				Assert.assertFalse(String.format("Verify the report %s should not be listed.", rpt), 
								   rptMgrPage.isRptItemPresent(rpt));;
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmScheduledEvent();
			CommonUtil.rmBirtRpts(rptArr, username);
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr1900()
	{
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");
		String[] rptArr = new String[]{"A_Remove1.rptdesign", "A_Remove2.rptdesign", "A_Remove3.rptdesign"};
		String user1="", role="";
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(rptArr, username, true);
			
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "View Report Manager"});
			user1 = CommonUtil.createUserDataToDb("user", true);
			CommonUtil.associateRoleDataWithUserData(user1, role);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			
			//Check on reports to be update one by one
			rptMgrPage.checkRpt(rptArr[0]);
			for(String rpt:rptArr)
				rptMgrPage.checkRpt(rpt);
			
			rptMgrPage.clickSetToPrivate();
			
			//Verification: 1. Verify pop up message "Are you sure to set selected report templates non-generic?".
			String actualResult = rptMgrPage.getTextFromAlertWindow();
			String expectedResult = "Are you sure to set selected report templates non-generic?";
			Assert.assertEquals("Verify the confirmation text from Alert window.", expectedResult, actualResult);
			rptMgrPage.acceptAlertWindow();
			
			//Verification: 2. Verify pop up message "Selected Report templates has been set to non-generic".
			actualResult = rptMgrPage.getTextFromAlertWindow();
			expectedResult = "Selected Report templates has been set to non-generic.";
			Assert.assertEquals("Verify the confirmation text from Alert window.", expectedResult, actualResult);
			rptMgrPage.acceptAlertWindow();
			
			//Verification: 3. Verify it is "No" in Generic Report Template column of "A_Remove1.rptdesign".
			for(String rpt:rptArr)
				Assert.assertFalse(String.format("Verify the report %s should be updated to Private.", rpt), rptMgrPage.isGenericRpt(rpt));
			
			//Close the browser and sign in with another user
			CommonUtil.closeBrowser();
			
			//Verification: 4. Login as user1 and verify user1 can not see "TestReportAdd1.rptdesign","TestReportAdd2.rptdesign", "TestReportAdd3.rptdesign" in report template list.
			rptMgrPage = navigateToTestPage(user1, "123");
			for(String rpt:rptArr)
				Assert.assertFalse(String.format("Verify the report %s should not be listed.", rpt), 
								   rptMgrPage.isRptItemPresent(rpt));;
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmScheduledEvent();
			CommonUtil.rmBirtRpts(rptArr, username);
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr2000()
	{
		String defaultPwd = "123";
		String user1="", user2="", role="";
		String rptName = "A_Remove1.rptdesign";
		try{	
			//Set-up
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
																	  "Add Report",
																	  "View Report Manager", 
																	  "Set Report Template To Public",
																	  "Set Report Template To Private"});
			
			user1 = CommonUtil.createUserDataToDb("user", true);
			user2 = CommonUtil.createUserDataToDb("user", true);
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.associateRoleDataWithUserData(user2, role);
			//Create a non-generic dump report;
			CommonUtil.createDumbBirtRpts(new String[]{rptName}, user1, false);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(user1, defaultPwd);
			
			//Test execution
			rptMgrPage.checkRpt(rptName);
			rptMgrPage.setRptToPublic(new String[]{rptName});
			
			//Close browser and set sign in with user2
			CommonUtil.closeBrowser();
			
			//Test execution
			rptMgrPage = navigateToTestPage(user2, defaultPwd);
			rptMgrPage.checkRpt(rptName);
			rptMgrPage.setRptToPrivate(new String[]{rptName});
			
			//Verification
			Assert.assertFalse(String.format("Verify the report %s should not be listed in current page.", rptName), rptMgrPage.isRptItemPresent(rptName));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{rptName}, user1);
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeUserDataFromDb(user2);	
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr2100()
	{
		String[] rptArr = new String[]{"A_Remove1.rptdesign", "A_Remove2.rptdesign", "A_Remove3.rptdesign"};
		String user1="", user2="", role="";
		try
		{
			//Set-up
			
			user1 = CommonUtil.createUserDataToDb("user", true);
			user2 = CommonUtil.createUserDataToDb("user", true);
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "Add Report",
					  												  "View Report Manager", 
					  												  "Set Report Template To Public",
					  												  "Set Report Template To Private"});
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.associateRoleDataWithUserData(user2, role);
			CommonUtil.createDumbBirtRpts(rptArr, user1, false);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(user1, "123");
			
			//Check on reports to be deleted one by one
			rptMgrPage.setRptToPublic(rptArr);
			
			logger.info("Close browser and sign in with another user");
			CommonUtil.closeBrowser();
			rptMgrPage = navigateToTestPage(user2,"123");
			rptMgrPage.setRptToPrivate(rptArr);
			
			//Verification
			for(String rpt:rptArr)
				Assert.assertFalse(String.format("The report %s should not be listed in current page.",rpt), rptMgrPage.isRptItemPresent(rpt));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(rptArr, user1);
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeUserDataFromDb(user2);	
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr2200()
	{
		String rptName = "A_Remove1.rptdesign";
		String user1="", role="";
		try
		{
			//Set-up
			
			user1 = CommonUtil.createUserDataToDb("user", true);
			
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "Add Report",
					  												  "View Report Manager", 
					  												  "Remove Report",
					  												  "Set Report Template To Public",
					  												  "Set Report Template To Private"});
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.createDumbBirtRpts(new String[]{rptName}, user1, false);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(user1, "123");
			rptMgrPage.checkRpt(rptName);
			
			rptMgrPage.clickRemoveRpt();
			
			//Verification 1
			String acutalMsg = rptMgrPage.getTextFromAlertWindow();
			String expectedMsg = "Are you sure to remove report?";
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation before remvoe a private report", expectedMsg, acutalMsg);
			
			//Verfication 2
			acutalMsg = rptMgrPage.getTextFromAlertWindow();
			expectedMsg = "Remove report successfully.";
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation after removing the private report", expectedMsg, acutalMsg);
			
			//Verification 3
			Assert.assertFalse(String.format("Verify the report %s should not be present in current page", rptName), rptMgrPage.isRptItemPresent(rptName));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr2300()
	{
		String rptName = "A_Remove1.rptdesign";
		String user1="", role="";
		try
		{
			//Set-up
			
			user1 = CommonUtil.createUserDataToDb("user", true);
			
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "Add Report",
					  												  "View Report Manager", 
					  												  "Remove Report",
					  												  "Set Report Template To Public",
					  												  "Set Report Template To Private"});
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.createDumbBirtRpts(new String[]{rptName}, user1, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(user1, "123");
			rptMgrPage.checkRpt(rptName);
			
			rptMgrPage.clickRemoveRpt();
			
			//Verification 1
			String acutalMsg = rptMgrPage.getTextFromAlertWindow();
			String expectedMsg = "Are you sure to remove report?";
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation before remvoe a private report", expectedMsg, acutalMsg);
			
			//Verfication 2
			acutalMsg = rptMgrPage.getTextFromAlertWindow();
			expectedMsg = "Remove report successfully.";
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation after removing the private report", expectedMsg, acutalMsg);
			
			//Verification 3
			Assert.assertFalse(String.format("Verify the report %s should not be present in current page", rptName), rptMgrPage.isRptItemPresent(rptName));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr2400()
	{
		String[] rptArr = new String[]{"A_Remove1.rptdesign", "A_Remove2.rptdesign"};
		String user1="", role="";
		try
		{
			//Set-up
			
			user1 = CommonUtil.createUserDataToDb("user", true);
			
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "Add Report",
					  												  "View Report Manager", 
					  												  "Remove Report",
					  												  "Set Report Template To Public",
					  												  "Set Report Template To Private"});
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.createDumbBirtRpts(new String[]{rptArr[0]}, user1, false);
			CommonUtil.createDumbBirtRpts(new String[]{rptArr[1]}, user1, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(user1, "123");
			rptMgrPage.checkRpt(rptArr);
			
			rptMgrPage.clickRemoveRpt();
			
			//Verification 1
			String acutalMsg = rptMgrPage.getTextFromAlertWindow();
			String expectedMsg = "Are you sure to remove report?";
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation before remvoe a private report", expectedMsg, acutalMsg);
			
			//Verfication 2
			acutalMsg = rptMgrPage.getTextFromAlertWindow();
			expectedMsg = "Remove report successfully.";
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation after removing the private report", expectedMsg, acutalMsg);
			
			//Verification 3
			for(String rpt:rptArr)
				Assert.assertFalse(String.format("Verify the report %s should not be present in current page", rpt), rptMgrPage.isRptItemPresent(rpt));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr2500()
	{
		String[] rptArr = new String[]{"A_Remove1.rptdesign", "A_Remove2.rptdesign"};
		String user1="", user2="", role="";
		try
		{
			//Set-up
			
			user1 = CommonUtil.createUserDataToDb("user", true);
			user2 = CommonUtil.createUserDataToDb("user", true);
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "Add Report",
					  												  "View Report Manager", 
					  												  "Remove Report",
					  												  "Set Report Template To Public",
					  												  "Set Report Template To Private"});
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.associateRoleDataWithUserData(user2, role);
			CommonUtil.createDumbBirtRpts(rptArr, user1, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(user2, "123");
			rptMgrPage.checkRpt(rptArr);
			
			rptMgrPage.clickRemoveRpt();
			
			//Verification 1
			String acutalMsg = rptMgrPage.getTextFromAlertWindow();
			String expectedMsg = "Are you sure to remove report?";
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation before remvoe a private report", expectedMsg, acutalMsg);
			
			//Verfication 2
			acutalMsg = rptMgrPage.getTextFromAlertWindow();
			expectedMsg = "Remove report successfully.";
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation after removing the private report", expectedMsg, acutalMsg);
			
			//Verification 3
			for(String rpt:rptArr)
				Assert.assertFalse(String.format("Verify the report %s should not be present in current page", rpt), rptMgrPage.isRptItemPresent(rpt));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeUserDataFromDb(user2);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr2600()
	{
		String[] rptArr = new String[]{"A_Remove1.rptdesign", "A_Remove2.rptdesign"};
		String user1="", user2="", role="";
		try
		{
			//Set-up
			user1 = CommonUtil.createUserDataToDb("user", true);
			user2 = CommonUtil.createUserDataToDb("user", true);
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "Add Report",
					  												  "View Report Manager", 
					  												  "Remove Report",
					  												  "Set Report Template To Public",
					  												  "Set Report Template To Private"});
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.associateRoleDataWithUserData(user2, role);
			CommonUtil.createDumbBirtRpts(new String[]{rptArr[0]}, user1, true);
			CommonUtil.createDumbBirtRpts(new String[]{rptArr[1]}, user2, false);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(user2, "123");
			rptMgrPage.checkRpt(rptArr);
			rptMgrPage.clickRemoveRpt();
			
			//Verification 1
			String acutalMsg = rptMgrPage.getTextFromAlertWindow();
			String expectedMsg = "Are you sure to remove report?";
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation before remvoe a private report", expectedMsg, acutalMsg);
			
			//Verification 2
			acutalMsg = rptMgrPage.getTextFromAlertWindow();
			expectedMsg = "Remove report successfully.";
			rptMgrPage.acceptAlertWindow();
			Assert.assertEquals("Verify the confirmation after removing the private report", expectedMsg, acutalMsg);
			
			//Verification 3
			for(String rpt:rptArr)
				Assert.assertFalse(String.format("Verify the report %s should not be present in current page", rpt), rptMgrPage.isRptItemPresent(rpt));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeUserDataFromDb(user2);
			CommonUtil.removeRolePerm(role); 
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr2700()
	{
		String rpt = "3080.rptdesign";
		String user1="", role="";
		String admin = CommonUtil.getPropertyValue("username");
		String password = CommonUtil.getPropertyValue("password");
		
		try
		{
			//Set-up
			user1 = CommonUtil.createUserDataToDb("user", true);
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "Add Report",
					  												  "View Report Manager", 
					  												  "Remove Report",
					  												  "Set Report Template To Public",
					  												  "Set Report Template To Private"});
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.createDumbBirtRpts(new String[]{rpt}, user1, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(admin, password);
			
			logger.info(String.format("Upload report %s", rpt));
			rptMgrPage.addReport(rpt);
			
			//Verification
			String prefix = rpt.substring(0, rpt.indexOf("."));
			List<String> results = CommonUtil.getRptDesignFileCount(prefix);
			Assert.assertEquals("Verify there should be 2 birt-reports stored in BSM server-side", 2, results.size());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{rpt}, user1);
			CommonUtil.rmBirtRpts(new String[]{rpt}, admin);
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr2800()
	{
		String user1="", user2="", role="";
		String rpt1 = "rpt1.rptdesign", rpt2 = "rpt2.rptdesign";
		
		try{
			//Set-up
			user1 = CommonUtil.createUserDataToDb("user", true);
			user2 = CommonUtil.createUserDataToDb("user", true);
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "Report Schedule Manager",
					  												  "View Report Schedule Manager",
					  												  "Create Report Event"});
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.associateRoleDataWithUserData(user2, role);
			CommonUtil.setGenericDesignRpts(false);
			
			CommonUtil.createDumbBirtRpts(new String[]{rpt1}, user1, true);
			CommonUtil.createDumbBirtRpts(new String[]{rpt2}, user2, false);
			
			//Test execution
			RptScheduleMgrPage rptScheduleMgrPage = navigateToTestPage(user2, "123").goToRptScheduleMgr();
			rptScheduleMgrPage.createScheduleEvent(getStartDate());
			
			ScheduleOperation scheduleOp = new ScheduleOperation(rptScheduleMgrPage.getWebDriver());
			scheduleOp.clickSelectRpt();
	
			ReportSelector rptSelector = new ReportSelector(scheduleOp.getWebDriver());
			
			String log = "Verify the report %s should be present in Report Selection window.";
			Assert.assertTrue(String.format(log, rpt1), rptSelector.isRptItemPresent(rpt1));
			Assert.assertTrue(String.format(log, rpt2), rptSelector.isRptItemPresent(rpt2));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{rpt1}, user1);
			CommonUtil.rmBirtRpts(new String[]{rpt2}, user2);
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeUserDataFromDb(user2);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
			CommonUtil.setGenericDesignRpts(true);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr2900()
	{
		String user1="", user2="", role="";
		String rpt1 = "rpt1.rptdesign", rpt2 = "rpt2.rptdesign";
		
		try{
			//Set-up
			user1 = CommonUtil.createUserDataToDb("user", true);
			user2 = CommonUtil.createUserDataToDb("user", true);
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "Report Schedule Manager",
					  												  "View Report Schedule Manager",
					  												  "Create Report Event"});
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.associateRoleDataWithUserData(user2, role);
			CommonUtil.setGenericDesignRpts(false);
			
			CommonUtil.createDumbBirtRpts(new String[]{rpt1}, user1, true);
			CommonUtil.createDumbBirtRpts(new String[]{rpt2}, user2, false);
			
			int expFileCount = CommonUtil.getRptDesignFileCountFromDb(); 
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
													   CommonUtil.getPropertyValue("password"));
			
			int actFileCount = rptMgrPage.getFileCount();
			
			//Verification
			Assert.assertEquals("Verify the count number of birt-reports.", expFileCount, actFileCount);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{rpt1}, user1);
			CommonUtil.rmBirtRpts(new String[]{rpt2}, user2);
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeUserDataFromDb(user2);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
			CommonUtil.setGenericDesignRpts(true);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr3000()
	{
		String user1="", user2="", role="";
		String rpt1 = "rpt1.rptdesign", rpt2 = "rpt2.rptdesign";
		
		try{
			//Set-up
			user1 = CommonUtil.createUserDataToDb("user", true);
			user2 = CommonUtil.createUserDataToDb("user", true);
			role = CommonUtil.createRoleWithPerm("role", new String[]{"Report Manager",
					  												  "Report Schedule Manager",
					  												  "View Report Schedule Manager",
					  												  "Create Report Event"});
			CommonUtil.associateRoleDataWithUserData(user1, role);
			CommonUtil.associateRoleDataWithUserData(user2, role);
			CommonUtil.setGenericDesignRpts(false);
			
			CommonUtil.createDumbBirtRpts(new String[]{rpt1}, user1, true);
			CommonUtil.createDumbBirtRpts(new String[]{rpt2}, user2, false);
						
			//Test execution
			RptScheduleMgrPage rptScheduleMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password")).goToRptScheduleMgr();
			rptScheduleMgrPage.createScheduleEvent(getStartDate());
			
			ScheduleOperation scheduleOp = new ScheduleOperation(rptScheduleMgrPage.getWebDriver());
			scheduleOp.clickSelectRpt();
		
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{rpt1}, user1);
			CommonUtil.rmBirtRpts(new String[]{rpt2}, user2);
			CommonUtil.removeUserDataFromDb(user1);
			CommonUtil.removeUserDataFromDb(user2);
			CommonUtil.removeRolePerm(role);
			CommonUtil.removeRoleFromDb(role);
			CommonUtil.setGenericDesignRpts(true);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr3100()
	{
		try{
			RptMgrPage rptMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), 
													   CommonUtil.getPropertyValue("password"));
			
			rptMgrPage.clickSetToPublic();
			
			String actualErrMsg = rptMgrPage.getTextFromAlertWindow();
			String expectedErrMsg = "Please select reports to operate.";
			
			Assert.assertEquals("Verify the error message.", expectedErrMsg, actualErrMsg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr3200()
	{
		try{
			RptMgrPage rptMgrPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), 
													   CommonUtil.getPropertyValue("password"));
			
			rptMgrPage.clickSetToPrivate();
			
			String actualErrMsg = rptMgrPage.getTextFromAlertWindow();
			String expectedErrMsg = "Please select reports to operate.";
			
			Assert.assertEquals("Verify the error message.", expectedErrMsg, actualErrMsg);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr3300()
	{
		String rpt1 = "3080.rptdesign", rpt2 = "8516.rptdesign";
		String admin = CommonUtil.getPropertyValue("username");
		String password = CommonUtil.getPropertyValue("password");
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(new String[]{rpt1}, admin, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(admin, password);
			
			logger.info(String.format("Upload report %s", rpt2));
			
			rptMgrPage.clickUpdateRpt(rpt1);
			
			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir+rpt2);
			
			//Verification: The Add File Button should be disabled
			String expectedErrMsg = "You may only update a report template with same name.";
			String actualErrMsg = rptUpload.getTextFromAlertWindow();			
			Assert.assertEquals("Verify the error message.", expectedErrMsg, actualErrMsg);

		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{rpt1}, admin);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr3400()
	{
		String rpt = "3080.rptdesign";
		String admin = CommonUtil.getPropertyValue("username");
		String password = CommonUtil.getPropertyValue("password");
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(new String[]{rpt}, admin, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(admin, password);
			
			logger.info(String.format("Upload report %s", rpt));
			
			rptMgrPage.clickUpdateRpt(rpt);
			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir+rpt);
			
			//Verification: The Add File Button should be disabled
			Assert.assertFalse("The Button Add File should be disabled.", rptUpload.isAddBtnEnabled());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
			CommonUtil.rmBirtRpts(new String[]{rpt}, admin);
		}
	}
	
	@Test(timeout=600000)
	public void testRptMgr3600()
	{
		String[] rpt = {"Activation_count.rptdesign"};
		String username =  CommonUtil.getPropertyValue("username");
		String password =  CommonUtil.getPropertyValue("password");				
		try
		{
			//Set-up
			CommonUtil.createDumbBirtRpts(rpt, username, true);
			
			//Test execution
			RptMgrPage rptMgrPage = navigateToTestPage(username, password);
			
			logger.info(String.format("Upload report %s", rpt[0]));
			
			rptMgrPage.clickAddRpt();
			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir+rpt[0]);
			
			String expectedErrMsg = "You have uploaded a report template with same name.";
			
			//Verification: The Add File Button should be disabled
			Assert.assertEquals("Verify the error message.", expectedErrMsg, rptUpload.getTextFromAlertWindow());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	private Calendar getStartDate()
	{
		Calendar c = Calendar.getInstance();
		int minute = c.get(Calendar.MINUTE);
		
		do{
			minute++;
		}while(!(minute%5==0));
		
		c.set(Calendar.MINUTE, minute);
		return c;
	}
	
	@Test
	public void testRptMgr3700(){
		PrivilegeTasks ptasks =new PrivilegeTasks();
		BrowserTasks br = new BrowserTasks();
		String user = "RptMgr"+CommonUtil.getRandomStr();
		String pwd = "123";
		String rpt = "SDP_NMP.rptdesign";
		String desc = "RptMgr3700";
		RptFormat format = RptFormat.PDF;
		try{		
			ptasks.insertUser(user, pwd, true);
			ptasks.grantRolesToUser(user,"ADMIN");
			br.launchBrowser();		
			LoginTasks lg = new LoginTasks();
			lg.loginPage = br.getURL();
			//login
			RptCenterPage rptCenterPg = lg.login(user,pwd);
			RptMgrTasks rptTasks = new RptMgrTasks();
			rptTasks.gotoRptMgr(rptCenterPg);
			if(!rptTasks.isRptPresent(rpt)){			
				rptTasks.addRpt(testDataDir, rpt);
				rptTasks.addRpt(testDataDir, "SDP_Base.rptlibrary");
			}
			Calendar eventTime = rptTasks.setSchedule(rpt, format, desc);
			RptScheduleMgrTasks rptSchTasks = new RptScheduleMgrTasks();
			rptSchTasks.triggerEvent(eventTime);
			CommonUtil.sleep(60);
			String eventId = RptScheduleMgrTasks.getEventID(desc);
			Pattern exptRpt = rptSchTasks.getExceptedGenRptPattern(rpt, format, eventId, eventTime);
			List<String> generated = rptTasks.viewGeneratedFiles(rpt);
			Matcher matcher = exptRpt.matcher(generated.get(0));
			Assert.assertEquals(1, generated.size());
			Assert.assertTrue(matcher.matches());
		}finally{
			br.closeBrowser();
			ptasks.removeUser(user);
			RptScheduleMgrTasks.rmRptEventFromBackend(desc);
		}		
	}

	@Test
	public void testRptMgr3800() {
		BrowserTasks br = new BrowserTasks();
		String rptzip = "RptDesign.zip";
		String rpt1 = "Boolean_report.rptdesign";
		String rpt2 = "Date_report.rptdesign";
		String rpt3 = "Float_report.rptdesign";
		try {
			br.launchBrowser();
			LoginTasks lg = new LoginTasks();
			lg.loginPage = br.getURL();
			// login
			RptCenterPage rptCenterPg = lg.login();
			RptMgrTasks rptTasks = new RptMgrTasks();
			rptTasks.gotoRptMgr(rptCenterPg);
			rptTasks.batchUpload(testDataDir, rptzip);
			Assert.assertTrue(rptTasks.isRptExisted(rpt1));
			Assert.assertTrue(rptTasks.isRptExisted(rpt2));
			Assert.assertTrue(rptTasks.isRptExisted(rpt3));
		} finally {
			br.closeBrowser();
		}
	}
	
	@Test
	public void testRptMgr3900() {
		BrowserTasks br = new BrowserTasks();
		String rptzip = "RptDesign.zip";
		String rpt = "SDP_NMP.rptdesign";		
		try {
			br.launchBrowser();
			LoginTasks lg = new LoginTasks();
			lg.loginPage = br.getURL();
			// login
			RptCenterPage rptCenterPg = lg.login();
			RptMgrTasks rptTasks = new RptMgrTasks();
			rptTasks.gotoRptMgr(rptCenterPg);
			rptTasks.batchUpload(testDataDir, rptzip);
			Assert.assertTrue(rptTasks.isRptUpdated(rpt, "admin"));			
		} finally {
			br.closeBrowser();
		}
	}
	
	@Test
	public void testRptMgr4000() {
		String username = CommonUtil.getPropertyValue("username");
		String password = CommonUtil.getPropertyValue("password");
		String rptzip = "SDP_NMP.rptdesign";
		try {

			RptMgrPage rptMgrPage = navigateToTestPage(username, password);

			rptMgrPage.clickBatchUpload();
			RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
			rptUpload.setFilePath(testDataDir + rptzip);
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
}
