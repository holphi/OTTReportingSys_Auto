package com.nagra.bsm.tests;

import java.util.Hashtable;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import com.nagra.bsm.ui.*;
import com.nagra.bsm.util.CommonUtil;

public class RptTransportTests extends BaseTests {
	
	//Utility method for quick navigation to test page.
	private RptTransportPage navigateToTestPage(String username, String password)
	{
		logger.info("Login in with User:" + username + ", Password:" + password);
		LoginPage loginPage = new LoginPage(CommonUtil.launchTestPortal());
		RptCenterPage rptCenterPage=loginPage.loginAs(username, password);
		
		logger.info("Click Administration to go to Administration tab.");
		AdminPage adminPage = rptCenterPage.goToAdmin();
		
		logger.info("Click Server->Report Transport.");
		return adminPage.goToRptTransportConfPage();
	}
	
	@Rule
	public Timeout globalTimeout = new Timeout(300000);
	
	@Before
	public void testSetup(){
		logger.info("============================="+testName.getMethodName()+"==============================");
	}
	
	@Test
	public void testRptTransport0100()
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
	public void testRptTransport0101()
	{
		try{
			
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			rptTransportPage.setSmtp("srv-smtp1.hq.k.grp");
			rptTransportPage.setSmtpPort("25");
			rptTransportPage.setSenderEmailAddress("BSM@nagra.com");
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
	public void testRptTransport0102()
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
			smtp.inputEmailAddress("a@nagra.com");
			smtp.clickCancel();
			Assert.assertFalse("Verify the validation window is closed.", rptTransportPage.isWindowPresent("Validate SMTP Settings"));
		
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport0200()
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
			Assert.assertFalse("Verify the output message after clicking validate", rptTransportPage.getSmtpSettingsResult().contains("Successfully"));
		
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
	    }finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport0300()
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
	public void testRptTransport0301()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setFtpIp("vm81653");
			rptTransportPage.setFtpPath("/report");
			rptTransportPage.setFtpUserName("");
			rptTransportPage.setFtpPassword("");
			rptTransportPage.clickSaveFtpSettings();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking Validate.", "Save Successfully.", rptTransportPage.getFtpSettingsResult());
			rptTransportPage.clickTestFtp();
			CommonUtil.sleep(5);
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
	public void testRptTransport0400()
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
			Assert.assertFalse("Verify the output message after clicking Validate.", rptTransportPage.getFtpSettingsResult().contains("Successfully"));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport0401()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setFtpIp("vm");
			rptTransportPage.setFtpPath("/report");
			rptTransportPage.setFtpUserName("fake");
			rptTransportPage.setFtpPassword("123");
			rptTransportPage.clickSaveFtpSettings();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking Validate.", "Save Successfully.", rptTransportPage.getFtpSettingsResult());
			rptTransportPage.clickTestFtp();
			CommonUtil.sleep(5);
			Assert.assertFalse("Verify the output message after clicking Validate.",rptTransportPage.getFtpSettingsResult().contains("Successfully"));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport0500()
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
	public void testRptTransport0501()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setHttpServerUrl("http://vm81653/webdav/");
			rptTransportPage.setHttpLoginName("");
			rptTransportPage.setHttpLoginPassword("");
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
	public void testRptTransport0600()
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
			Assert.assertFalse("Verify the output message after clicking Validate.", rptTransportPage.getHttpSettingsResult().contains("Successfully"));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport0601()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setHttpServerUrl("http://vm81653/incoming/");
			rptTransportPage.setHttpLoginName("xampp");
			rptTransportPage.setHttpLoginPassword("xamppa");
			rptTransportPage.clickSaveHttpSettings();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking Validate.", "Save Successfully.", rptTransportPage.getHttpSettingsResult());
			rptTransportPage.clickTestHttp();
			CommonUtil.sleep(5);
			Assert.assertEquals("Verify the output message after clicking Validate.", "User name or password is not correct.", rptTransportPage.getHttpSettingsResult());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport0700()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			rptTransportPage.setSmtp("");
			rptTransportPage.setSmtpPort("25");
			rptTransportPage.setSenderEmailAddress("BSM@nagra.com");
			rptTransportPage.clickSaveSmtpSettings();
			Assert.assertEquals("Verify the error message besides smtp textbox.", "This field is required", rptTransportPage.getWarningInfo("Smtp"));
			
			rptTransportPage.setSmtp("srv-smtp1.hq.k.grp");
			rptTransportPage.clickSaveSmtpSettings();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking Save button.", "Save Successfully.", rptTransportPage.getSmtpSettingsResult());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport0701()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setSmtp("srv-smtp1.hq.k.grp");
			rptTransportPage.setSmtpPort("");
			rptTransportPage.setSenderEmailAddress("BSM@nagra.com");
			rptTransportPage.clickSaveSmtpSettings();
			//Verification
			Assert.assertEquals("Verify the error message besides smtp port textbox.", "This field is required", rptTransportPage.getWarningInfo("Port"));
			rptTransportPage.setSmtpPort("25");
			rptTransportPage.clickSaveSmtpSettings();
			Assert.assertEquals("Verify the output message after clicking Save button.", "Save Successfully.", rptTransportPage.getSmtpSettingsResult());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport0702()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setSmtp("srv-smtp1.hq.k.grp");
			rptTransportPage.setSmtpPort("25");
			rptTransportPage.setSenderEmailAddress("");
			rptTransportPage.clickSaveSmtpSettings();
			//Verification
			Assert.assertEquals("Verify the error message besides smtp port textbox.", "This field is required", rptTransportPage.getWarningInfo("Email"));
			rptTransportPage.setSenderEmailAddress("BSM@nagra.com");
			rptTransportPage.clickSaveSmtpSettings();
			Assert.assertEquals("Verify the output message after clicking Save button.", "Save Successfully.", rptTransportPage.getSmtpSettingsResult());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport0800()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setFtpIp("");
			rptTransportPage.setFtpPath("/report");
			rptTransportPage.setFtpUserName("bsm");
			rptTransportPage.setFtpPassword("bsm");
			rptTransportPage.clickSaveFtpSettings();
			//Verification
			Assert.assertEquals("Verify the error message besides ftp url textbox.", "This field is required", rptTransportPage.getWarningInfo("Ftp"));
			rptTransportPage.setFtpIp("vm81653");
			rptTransportPage.clickSaveFtpSettings();
			Assert.assertEquals("Verify the output message after clicking Save.", "Save Successfully.", rptTransportPage.getFtpSettingsResult());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport0900()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setHttpServerUrl("");
			rptTransportPage.setHttpLoginName("xampp");
			rptTransportPage.setHttpLoginPassword("xampp");
			rptTransportPage.clickSaveHttpSettings();
			//Verification
			Assert.assertEquals("Verify the error message besides http textbox.", "This field is required", rptTransportPage.getWarningInfo("Http"));
			rptTransportPage.setHttpServerUrl("http://vm81653/incoming/");
			rptTransportPage.clickSaveHttpSettings();
			Assert.assertEquals("Verify the output message after clicking Save.", "Save Successfully.", rptTransportPage.getHttpSettingsResult());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport1000()
	{
		try{
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setDesEmail("");
			rptTransportPage.clickSaveDesEmail();
			//Verification
			Assert.assertEquals("Verify the error message besides http textbox.", "This field is required", rptTransportPage.getWarningInfo("DesEmail"));
			rptTransportPage.setDesEmail("test@nagra.com");
			rptTransportPage.clickSaveDesEmail();
			Assert.assertEquals("Verify the output message after clicking Save.", "Save Successfully.", rptTransportPage.getDesEmailSettingsResult());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport1001()
	{
		try{
			String expectedRst = "Email format is not correct.";
			//Test execution
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
							   									   CommonUtil.getPropertyValue("password"));
			
			rptTransportPage.setDesEmail("abc");
			rptTransportPage.clickSaveDesEmail();
			//Verification
			Assert.assertEquals("Verify the error message besides http textbox.", expectedRst, rptTransportPage.getWarningInfo("DesEmail"));
			rptTransportPage.setDesEmail("abc@");
			rptTransportPage.clickSaveDesEmail();
			//Verification
			Assert.assertEquals("Verify the error message besides http textbox.", expectedRst, rptTransportPage.getWarningInfo("DesEmail"));
			rptTransportPage.setDesEmail("abc@nagra");
			rptTransportPage.clickSaveDesEmail();
			//Verification
			Assert.assertEquals("Verify the error message besides http textbox.", expectedRst, rptTransportPage.getWarningInfo("DesEmail"));
			rptTransportPage.setDesEmail("@nagra.com");
			rptTransportPage.clickSaveDesEmail();
			//Verification
			Assert.assertEquals("Verify the error message besides http textbox.", expectedRst, rptTransportPage.getWarningInfo("DesEmail"));
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport1100()
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
			rptTransportPage.verifyTransLog();
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport1200()
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
			rptTransportPage.verifyTransLog();
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport1300()
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
			rptTransportPage.verifyTransLog();
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport1400()
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
	public void testRptTransport1401()
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
	public void testRptTransport1500()
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
			rptTransportPage.setDesEmail("abc@abc.com");
			rptTransportPage.clickSaveDesEmail();
			Assert.assertEquals("Verify the output message after clicking Save.", "Save Successfully.", rptTransportPage.getDesEmailSettingsResult());
			rptTransportPage.clickTestEmail();
			CommonUtil.sleep(10);
			Assert.assertFalse("Verify the output message after clicking Test Email Button.", rptTransportPage.getDesEmailSettingsResult().contains("Successfully"));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport1600()
	{
		try{
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
																   CommonUtil.getPropertyValue("password"));
			
			//Test execution
			rptTransportPage.setSendingCount("");
			rptTransportPage.clickSaveRepeatCount();
			Assert.assertEquals("Verify the warning info output after clicking Save Button.", "This field is required", rptTransportPage.getWarningInfo("Repeat"));
			rptTransportPage.setSendingCount("3");
			rptTransportPage.clickSaveRepeatCount();
			Assert.assertEquals("Verify the output message after clicking Save Button.", "Save Successfully.", rptTransportPage.getSendingCountResult());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport1700()
	{
		try{
			RptTransportPage rptTransportPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
																   CommonUtil.getPropertyValue("password"));
			
			String value = "";
			
			//Test execution
			value = "abc";
			rptTransportPage.setSendingCount(value);
			Assert.assertFalse(String.format("Verify the value %s is not acceptable.", value), rptTransportPage.getSendingCount().equals(value));
			
			value = "-1";
			rptTransportPage.setSendingCount(value);
			Assert.assertFalse(String.format("Verify the value %s is not acceptable.", value), rptTransportPage.getSendingCount().equals(value));
			
			value = "1.3";
			rptTransportPage.setSendingCount(value);
			Assert.assertFalse(String.format("Verify the value %s is not acceptable.", value), rptTransportPage.getSendingCount().equals(value));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test
	public void testRptTransport1701()
	{
		Assert.fail("Not finished yet!");
	}
	
	@Test
	public void testRptTransport1800()
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
	public void testRptTransport1900()
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
	public void testRptTransport2000()
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
	public void testRptTransport2100()
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
	public void testRptTransport2200()
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
