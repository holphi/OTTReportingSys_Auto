package com.nagra.bsm.tests;

import java.util.Hashtable;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import com.nagra.bsm.ui.*;
import com.nagra.bsm.util.CommonUtil;

public class BakTransportTests extends BaseTests {
	@Rule
	public Timeout globalTimeout = new Timeout(300000);
	
	//Utility method for quick navigation to test page.
	private RptTransportPage navigateToTestPage(String username, String password)
	{
		logger.info("Login in with User:" + username + ", Password:" + password);
		LoginPage loginPage = new LoginPage(CommonUtil.launchTestPortal());
		RptCenterPage rptCenterPage=loginPage.loginAs(username, password);
		
		logger.info("Click Administration to go to Administration tab.");
		AdminPage adminPage = rptCenterPage.goToAdmin();
		
		logger.info("Click System Maintenance->Backup Transport Configuration.");
		return adminPage.goToBakTransportConfPage();
	}
	
	@Before
	public void testSetup(){
		logger.info("============================="+testName.getMethodName()+"==============================");
	}
	
	@Test
	public void testBakTransport0100()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			rptTransportPage.setSmtp("srv-smtp1.hq.k.grp");
			rptTransportPage.setSmtpPort("25");
			rptTransportPage.setSenderEmailAddress("BSM@nagra.com");
			rptTransportPage.setSenderUserName(CommonUtil.getPropertyValue("domain_username"));
			rptTransportPage.setSenderPassword(CommonUtil.getPropertyValue("domain_password"));
			rptTransportPage.clickSaveSmtpSettings();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking Save.", "Save Successfully.", rptTransportPage.getSmtpSettingsResult());
			rptTransportPage.clickTestSmtp();
			
			SmtpValidation smtp = new SmtpValidation(rptTransportPage.getWebDriver());
			smtp.inputEmailAddress(CommonUtil.getPropertyValue("des_email"));
			smtp.clickValidate();

			CommonUtil.sleep(8);
			Assert.assertEquals("Verify the output message after clicking Validate.", "Email was successfully sent.", rptTransportPage.getSmtpSettingsResult());
			Assert.assertTrue("Verify the destination has received the e-mail", CommonUtil.checkReceivedBsmTestMail());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport0200()
	{
		try{
			
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setSmtp("srv");
			rptTransportPage.setSmtpPort("25");
			rptTransportPage.setSenderEmailAddress("BSM@nagra.com");
			rptTransportPage.setSenderUserName(CommonUtil.getPropertyValue("domain_username"));
			rptTransportPage.setSenderPassword(CommonUtil.getPropertyValue("domain_password"));
			rptTransportPage.clickSaveSmtpSettings();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking Save.", "Save Successfully.", rptTransportPage.getSmtpSettingsResult());
			rptTransportPage.clickTestSmtp();
			
			SmtpValidation smtp = new SmtpValidation(rptTransportPage.getWebDriver());
			smtp.inputEmailAddress("a@nagra.com");
			smtp.clickValidate();
			
			CommonUtil.sleep(6);
			Assert.assertEquals("Verify the output message after clicking validate", "Can´t send test email to all email adresses, please check.", rptTransportPage.getSmtpSettingsResult());
		
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
	    }finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport0300()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setFtpIp("vm81653");
			rptTransportPage.setFtpPath("/report");
			rptTransportPage.setFtpUserName("bsm");
			rptTransportPage.setFtpPassword("bsm");
			rptTransportPage.clickSaveFtpSettings();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking Validate.", "Save Successfully.", rptTransportPage.getFtpSettingsResult());
			rptTransportPage.clickTestFtp();
			CommonUtil.sleep(6);
			Assert.assertEquals("Verify the output message after clicking Validate.", "Test Successfully.", rptTransportPage.getFtpSettingsResult());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport0400()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setFtpIp("vm");
			rptTransportPage.setFtpPath("/report");
			rptTransportPage.setFtpUserName("");
			rptTransportPage.setFtpPassword("");
			rptTransportPage.clickSaveFtpSettings();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking Validate.", "Save Successfully.", rptTransportPage.getFtpSettingsResult());
			rptTransportPage.clickTestFtp();
			CommonUtil.sleep(5);
			Assert.assertEquals("Verify the output message after clicking Validate.", "Can´t connect the FTP server.", rptTransportPage.getFtpSettingsResult());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport0500()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setHttpServerUrl("http://vm81653/incoming/");
			rptTransportPage.setHttpLoginName("xampp");
			rptTransportPage.setHttpLoginPassword("xampp");
			rptTransportPage.clickSaveHttpSettings();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking Validate.", "Save Successfully.", rptTransportPage.getHttpSettingsResult());
			rptTransportPage.clickTestHttp();
			CommonUtil.sleep(5);
			Assert.assertEquals("Verify the output message after clicking Validate.", "Test Successfully.", rptTransportPage.getHttpSettingsResult());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport0600()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setHttpServerUrl("http://vm");
			rptTransportPage.setHttpLoginName("xampp");
			rptTransportPage.setHttpLoginPassword("xampp");
			rptTransportPage.clickSaveHttpSettings();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking Validate.", "Save Successfully.", rptTransportPage.getHttpSettingsResult());
			rptTransportPage.clickTestHttp();
			CommonUtil.sleep(5);
			Assert.assertEquals("Verify the output message after clicking Validate.", "Http Server can´t be connected,please check.", rptTransportPage.getHttpSettingsResult());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport0700()
	{
		try{
			CommonUtil.removeAllActivityLog();
			
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setSendingCount("3");
			rptTransportPage.clickSaveRepeatCount();
			
			Assert.assertEquals("Verify the output message after clicking Save.", "Save Successfully.", rptTransportPage.getSendingCountResult());
			logger.info("Verify report transport log");
			rptTransportPage.verifyBakTransLog();
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport0800()
	{
		try{
			//Remove all activity log
			CommonUtil.removeAllActivityLog();
			
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setSendingCount("3");
			rptTransportPage.clickSaveRepeatCount();
			
			Assert.assertEquals("Verify the output message after clicking Save.", "Save Successfully.", rptTransportPage.getSendingCountResult());
			logger.info("Verify report transport log");
			rptTransportPage.verifyBakTransLog();
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport0900()
	{
		try{
			//Remove all activity log
			CommonUtil.removeAllActivityLog();
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setSendingCount("3");
			rptTransportPage.clickSaveRepeatCount();
			
			Assert.assertEquals("Verify the output message after clicking Save.", "Save Successfully.", rptTransportPage.getSendingCountResult());
			logger.info("Verify report transport log");
			rptTransportPage.verifyBakTransLog();
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport1000()
	{
		try{
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
																   CommonUtil.getPropertyValue("password"));
			
			//Pre-condition execution
			rptTransportPage.setSmtp("srv-smtp1.hq.k.grp");
			rptTransportPage.setSmtpPort("25");
			rptTransportPage.setSenderEmailAddress("BSM@nagra.com");
			rptTransportPage.setSenderUserName(CommonUtil.getPropertyValue("domain_username"));
			rptTransportPage.setSenderPassword(CommonUtil.getPropertyValue("domain_password"));
			rptTransportPage.clickSaveSmtpSettings();
			
			//Test execution
			rptTransportPage.setDesEmail(CommonUtil.getPropertyValue("des_email"));
			rptTransportPage.clickSaveDesEmail();
			rptTransportPage.clickTestEmail();
			
			CommonUtil.sleep(10);
			Assert.assertEquals("Verify the output message after clicking Save.", "Email was successfully sent.", rptTransportPage.getDesEmailSettingsResult());
			Assert.assertTrue("Verify the test account does recieve the test mail", CommonUtil.checkReceivedBsmTestMail());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport1100()
	{
		try{
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
																   CommonUtil.getPropertyValue("password"));
			
			//Pre-condition execution
			rptTransportPage.setSmtp("srv-smtp1.hq.k.grp");
			rptTransportPage.setSmtpPort("25");
			rptTransportPage.setSenderEmailAddress("BSM@nagra.com");
			rptTransportPage.setSenderUserName(CommonUtil.getPropertyValue("domain_username"));
			rptTransportPage.setSenderPassword(CommonUtil.getPropertyValue("domain_password"));
			rptTransportPage.clickSaveSmtpSettings();
			
			//Test execution
			rptTransportPage.setDesEmail("abc@nagra.com;def@nagra.com");
			rptTransportPage.clickSaveDesEmail();
			rptTransportPage.clickTestEmail();
			
			CommonUtil.sleep(10);
			Assert.assertEquals("Verify the output message after clicking Save.", "Email was successfully sent.", rptTransportPage.getDesEmailSettingsResult());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport1200()
	{
		try
		{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
					   											   CommonUtil.getPropertyValue("password"));
			rptTransportPage.clickResetSmptSettings();
			
			//Verification
			Assert.assertEquals("Verify the message output after clicking Smtp Reset button.", "Reset Successfully.", rptTransportPage.getSmtpSettingsResult());
			Assert.assertTrue("Verify the Smtp textbox has been cleard.", rptTransportPage.getSmtp().length()==0);
			Assert.assertTrue("Verify the Smtp port textbox has been cleard.", rptTransportPage.getSmtpPort().length()==0);
			Assert.assertTrue("Verify the Sender email address textbox has been cleard.", rptTransportPage.getSenderEmailAddress().length()==0);
			Assert.assertTrue("Verify the Sender username textbox has been cleard.", rptTransportPage.getSenderUserName().length()==0);
			Assert.assertTrue("Verify the Sender email address textbox has been cleard", rptTransportPage.getSenderEmailAddress().length()==0);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport1300()
	{
		try
		{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
					   											   CommonUtil.getPropertyValue("password"));
			rptTransportPage.clickResetRepeatCount();
			
			//Verification
			Assert.assertEquals("Verify the message output after clicking Sending count Reset button.", "Reset Successfully.", rptTransportPage.getSendingCountResult());
			Assert.assertTrue("Verify the Sending repeat count textbox has been cleard.", rptTransportPage.getSendingCount().length()==0);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport1400()
	{
		try
		{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
					   											   CommonUtil.getPropertyValue("password"));
			rptTransportPage.clickResetRepeatCount();
			
			//Verification
			Assert.assertEquals("Verify the message output after clicking Sending count Reset button.", "Reset Successfully.", rptTransportPage.getSendingCountResult());
			Assert.assertTrue("Verify the Sending repeat count textbox has been cleard.", rptTransportPage.getSendingCount().length()==0);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport1500()
	{
		try
		{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
					   											   CommonUtil.getPropertyValue("password"));
			rptTransportPage.setFtpIp(CommonUtil.getPropertyValue("ftp_url"));
			rptTransportPage.clickResetFtp();
			
			//Verification
			Assert.assertEquals("Verify the message output after clicking Ftp Reset button.", "Reset Successfully.", rptTransportPage.getFtpSettingsResult());
			Assert.assertTrue("Verify the Ftp Url textbox has been cleard.", rptTransportPage.getFtpIp().length()==0);
			Assert.assertTrue("Verify the Ftp Path textbox has been cleard.", rptTransportPage.getFtpPath().length()==0);
			Assert.assertTrue("Verify the Ftp User name textbox has been cleard.", rptTransportPage.getFtpUserName().length()==0);
			Assert.assertTrue("Verify the Ftp password textbox has been cleard.", rptTransportPage.getFtpPassword().length()==0);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testBakTransport1600()
	{
		try
		{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
					   											   CommonUtil.getPropertyValue("password"));
			rptTransportPage.setHttpServerUrl(CommonUtil.getPropertyValue("http_url"));
			rptTransportPage.clickResetHttp();
			
			//Verification
			Assert.assertEquals("Verify the message output after clicking Ftp Reset button.", "Reset Successfully.", rptTransportPage.getHttpSettingsResult());
			Assert.assertTrue("Verify the Http Url textbox has been cleard.", rptTransportPage.getHttpServerUrl().length()==0);
			Assert.assertTrue("Verify the Http User name textbox has been cleard.", rptTransportPage.getHttpLoginName().length()==0);
			Assert.assertTrue("Verify the Http Password textbox has been cleard.", rptTransportPage.getHttpLoginPassword().length()==0);
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
}
