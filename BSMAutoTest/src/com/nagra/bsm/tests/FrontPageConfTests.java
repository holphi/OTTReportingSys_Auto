package com.nagra.bsm.tests;

import java.util.Hashtable;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;

import com.nagra.bsm.tests.categorymarker.SanityCheck;
import com.nagra.bsm.ui.AdminPage;
import com.nagra.bsm.ui.LoginPage;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.FrontConfPage;
import com.nagra.bsm.util.CommonUtil;


public class FrontPageConfTests extends BaseTests{

	@Rule
	public Timeout globalTimeout = new Timeout(300000);
	
	private static String testDataDir = CommonUtil.getCurrDir()+"\\testdata\\Image\\";
	
	//Utility method for quick navigation to test page.
	private FrontConfPage navigateToTestPage(String username, String password)
	{
		logger.info("Login in with User:" + username + ", Password:" + password);
		LoginPage loginPage = new LoginPage(CommonUtil.launchTestPortal());
		RptCenterPage rptCenterPage = loginPage.loginAs(username, password);
		
		logger.info("Click Administration to go to Administration tab.");
		AdminPage adminPage = rptCenterPage.goToAdmin();
		
		logger.info("Click Security->Front configuration page.");
		return adminPage.goToFrontPageConfPage();
	}

	@Before
	public void testSetup(){
		logger.info("============================="+testName.getMethodName()+"==============================");
	}
	
	@After
	public void testTeardown(){
		CommonUtil.closeBrowser();
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf0100()
	{
		try
		{
			//Test execution
			String img = "800x600.jpg";
			FrontConfPage frontPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password"));
			logger.info("Click Browse and set backgroud image");
			frontPage.setBackGroundImg(testDataDir+img);
			frontPage.clickUploadForBgImg();
			CommonUtil.sleep(5);
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getBgImgUploadConfInfo());
			Assert.assertEquals("Verify Image width equals 800.", "800", frontPage.getCurrentImgWidth());
			Assert.assertEquals("Verify image height equals 600.", "600", frontPage.getCurrentImgHeight());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrBgImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
//			logger.info(loginPage.getBgImagInfo());
			Assert.assertTrue("Verify the image takes affact in login image",loginPage.getBgImagInfo().contains(img));
			
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
	public void testFrontPageConf0200()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "1024x768.jpg";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setBackGroundImg(testDataDir+img);
			frontPage.clickUploadForBgImg();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getBgImgUploadConfInfo());
			Assert.assertEquals("Verify Image width equals 1024.", "1024", frontPage.getCurrentImgWidth());
			Assert.assertEquals("Verify image height equals 768.", "768", frontPage.getCurrentImgHeight());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrBgImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			Assert.assertTrue("Verify the image takes affact in login image",loginPage.getBgImagInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=6000000)
	public void testFrontPageConf0300()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "1440x900.jpg";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setBackGroundImg(testDataDir+img);
			frontPage.clickUploadForBgImg();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getBgImgUploadConfInfo());
			Assert.assertEquals("Verify Image width equals 1440.", "1440", frontPage.getCurrentImgWidth());
			Assert.assertEquals("Verify image height equals 900.", "900", frontPage.getCurrentImgHeight());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrBgImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			Assert.assertTrue("Verify the image takes affact in login image",loginPage.getBgImagInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf0400()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "1024x768.bmp";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setBackGroundImg(testDataDir+img);
			frontPage.clickUploadForBgImg();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getBgImgUploadConfInfo());
			Assert.assertEquals("Verify Image width equals 1024.", "1024", frontPage.getCurrentImgWidth());
			Assert.assertEquals("Verify image height equals 768.", "768", frontPage.getCurrentImgHeight());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrBgImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			Assert.assertTrue("Verify the image take affact in login image",loginPage.getBgImagInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf0500()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "1024x768.gif";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setBackGroundImg(testDataDir+img);
			frontPage.clickUploadForBgImg();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getBgImgUploadConfInfo());
			Assert.assertEquals("Verify Image width equals 1024.", "1024", frontPage.getCurrentImgWidth());
			Assert.assertEquals("Verify image height equals 768.", "768",  frontPage.getCurrentImgHeight());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrBgImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			Assert.assertTrue("Verify the image take affact in login image",loginPage.getBgImagInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf0600()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "1024x768.png";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setBackGroundImg(testDataDir+img);
			frontPage.clickUploadForBgImg();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getBgImgUploadConfInfo());
			Assert.assertEquals("Verify Image width equals 1024.", "1024", frontPage.getCurrentImgWidth());
			Assert.assertEquals("Verify image height equals 768.", "768", frontPage.getCurrentImgHeight());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrBgImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			Assert.assertTrue("Verify the image take affact in login image",loginPage.getBgImagInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	
	@Test(timeout=600000)
	public void testFrontPageConf0700()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "170x120.jpg";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setBackGroundImg(testDataDir+img);
			frontPage.clickUploadForBgImg();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getBgImgUploadConfInfo());
			Assert.assertEquals("Verify Image width equals 170.", "170", frontPage.getCurrentImgWidth());
			Assert.assertEquals("Verify image height equals 120.", "120", frontPage.getCurrentImgHeight());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrBgImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			Assert.assertTrue("Verify the image take affact in login image",loginPage.getBgImagInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf0800()
	{
		try
		{
			//Test execution
			FrontConfPage frontPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password"));
			frontPage.inputLeftMarginValue("-23.0");
			frontPage.clickSave();
			
			frontPage.inputLeftMarginValue("23.0");
			
			//Verification
			Assert.assertEquals("Verify the error message should be output.", 
					            "There must be a non-empty number that is greater than 0 in left margin box.",
					            frontPage.getLeftMarginErrorMsg().trim());
			frontPage.inputTopMarginValue("-23.0");
			frontPage.clickSave();
			Assert.assertEquals("Verify the error message should be output.",
								"There must be a non-empty number that is greater than 0 in top margin box.",
								frontPage.getTopMarginErrorMsg().trim());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf0801()
	{
		try
		{
			//Test execution
			FrontConfPage frontPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password"));
			frontPage.inputLeftMarginValue("abc");
			frontPage.clickSave();
			
			frontPage.inputLeftMarginValue("23.0");
			
			//Verification
			Assert.assertEquals("Verify the error message should be output.",
							   "There must be a non-empty number that is greater than 0 in left margin box.",
							   frontPage.getLeftMarginErrorMsg().trim());
			frontPage.inputTopMarginValue("&$#");
			frontPage.clickSave();
			Assert.assertEquals("Verify the error message should be output.",
								"There must be a non-empty number that is greater than 0 in top margin box.",
								frontPage.getTopMarginErrorMsg().trim());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf0900()
	{
		try
		{
			//Test execution
			FrontConfPage frontPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password"));
			frontPage.inputLeftMarginValue("-1");
			frontPage.clickSave();
			
			frontPage.inputLeftMarginValue("23.0");
			
			//Verification
			Assert.assertEquals("Verify the error message should be output.",
					            "There must be a non-empty number that is greater than 0 in left margin box.",
							    frontPage.getLeftMarginErrorMsg().trim());
			frontPage.inputTopMarginValue("-1");
			frontPage.clickSave();
			Assert.assertEquals("Verify the error message should be output.",
							    "There must be a non-empty number that is greater than 0 in top margin box.",
							    frontPage.getTopMarginErrorMsg().trim());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf1000()
	{
		try
		{
			//Test execution
			FrontConfPage frontPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password"));
			frontPage.inputLeftMarginValue("0");
			frontPage.inputTopMarginValue("0");
			frontPage.clickSave();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the message should be output.","Save successfully.",frontPage.getSavingConfInfo().trim());
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf1200()
	{
		try
		{
			//Test execution
			FrontConfPage frontPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password"));
			frontPage.restoreToDefaultBGImg();
			String leftMargin = "730.0", topMargin="100.0";
			frontPage.inputLeftMarginValue(leftMargin);
			frontPage.inputTopMarginValue(topMargin);
			frontPage.clickSave();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the message should be output.", "Save successfully.", frontPage.getSavingConfInfo().trim());
			
			frontPage.logout();
			Hashtable<String, String> data = CommonUtil.getFrontPageConfDataFromDb();
			Assert.assertEquals("Verify the left margin should be saved in DB.", leftMargin, data.get("LEFT"));
			Assert.assertEquals("Verify the top margin should be save in DB.", topMargin, data.get("TOP"));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf1300()
	{
		try
		{
			//Test execution
			FrontConfPage frontPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password"));
			frontPage.restoreToDefaultBGImg();
			String leftMargin = "0.0", topMargin="260.0";
			frontPage.inputLeftMarginValue(leftMargin);
			frontPage.inputTopMarginValue(topMargin);
			frontPage.clickSave();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the message should be output.", "Save successfully.", frontPage.getSavingConfInfo().trim());
			
			frontPage.logout();
			Hashtable<String, String> data = CommonUtil.getFrontPageConfDataFromDb();
			Assert.assertEquals("Verify the left margin should be saved in DB.", leftMargin, data.get("LEFT"));
			Assert.assertEquals("Verify the top margin should be save in DB.", topMargin, data.get("TOP"));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	@Category(SanityCheck.class)
	public void testFrontPageConf1400()
	{
		try
		{
			//Test execution
			FrontConfPage frontPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password"));
			frontPage.restoreToDefaultBGImg();
			String leftMargin = "0.0", topMargin="260.0";
			frontPage.inputLeftMarginValue(leftMargin);
			frontPage.inputTopMarginValue(topMargin);
			frontPage.clickSave();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the message should be output.","Save successfully.",frontPage.getSavingConfInfo().trim());
			
			frontPage.logout();
			Hashtable<String, String> data = CommonUtil.getFrontPageConfDataFromDb();
			Assert.assertEquals("Verify the left margin should be saved in DB.", leftMargin, data.get("LEFT"));
			Assert.assertEquals("Verify the top margin should be save in DB.",topMargin, data.get("TOP"));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf1500()
	{
		try
		{
			//Test execution
			FrontConfPage frontPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password"));
			frontPage.restoreToDefaultBGImg();
			String leftMargin = "720.0", topMargin="260.0";
			frontPage.inputLeftMarginValue(leftMargin);
			frontPage.inputTopMarginValue(topMargin);
			frontPage.clickSave();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the message should be output.","Save successfully.", frontPage.getSavingConfInfo().trim());
			
			frontPage.logout();
			Hashtable<String, String> data = CommonUtil.getFrontPageConfDataFromDb();
			Assert.assertEquals("Verify the left margin should be saved in DB.", leftMargin, data.get("LEFT"));
			Assert.assertEquals("Verify the top margin should be save in DB.", topMargin ,data.get("TOP"));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf1600()
	{
		try
		{
			//Test execution
			FrontConfPage frontPage = navigateToTestPage(CommonUtil.getPropertyValue("username"), CommonUtil.getPropertyValue("password"));
			frontPage.restoreToDefaultBGImg();
			String leftMargin = "100.0", topMargin="300.0";
			frontPage.inputLeftMarginValue(leftMargin);
			frontPage.inputTopMarginValue(topMargin);
			frontPage.clickSave();
			CommonUtil.sleep(2);
			//Verification
			Assert.assertEquals("Verify the message should be output.", "Save successfully.", frontPage.getSavingConfInfo().trim());
			
			frontPage.logout();
			Hashtable<String, String> data = CommonUtil.getFrontPageConfDataFromDb();
			Assert.assertEquals("Verify the left margin should be saved in DB.", leftMargin, data.get("LEFT"));
			Assert.assertEquals("Verify the top margin should be save in DB.", topMargin, data.get("TOP"));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	@Category(SanityCheck.class)
	public void testFrontPageConf1700()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "800x90.jpg";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setHeaderImg(testDataDir+img);
			frontPage.clickUploadForHeaderImg();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getHeaderImgUploadConfInfo());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrHeaderImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			RptCenterPage rptCenterPage = loginPage.loginAs(username, password);
			Assert.assertTrue("Verify the header image has been updated.",rptCenterPage.getHdImgInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf1800()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "1024x99.jpg";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setHeaderImg(testDataDir+img);
			frontPage.clickUploadForHeaderImg();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getHeaderImgUploadConfInfo());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrHeaderImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			RptCenterPage rptCenterPage = loginPage.loginAs(username, password);
			Assert.assertTrue("Verify the header image has been updated.",rptCenterPage.getHdImgInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf1900()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "1400x120.jpg";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setHeaderImg(testDataDir+img);
			frontPage.clickUploadForHeaderImg();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getHeaderImgUploadConfInfo());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrHeaderImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			RptCenterPage rptCenterPage = loginPage.loginAs(username, password);
			Assert.assertTrue("Verify the header image has been updated.",rptCenterPage.getHdImgInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf2000()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "1024x99.bmp";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setHeaderImg(testDataDir+img);
			frontPage.clickUploadForHeaderImg();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getHeaderImgUploadConfInfo());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrHeaderImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			RptCenterPage rptCenterPage = loginPage.loginAs(username, password);
			Assert.assertTrue("Verify the header image has been updated.",rptCenterPage.getHdImgInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf2100()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "1024x99.gif";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setHeaderImg(testDataDir+img);
			frontPage.clickUploadForHeaderImg();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getHeaderImgUploadConfInfo());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrHeaderImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			RptCenterPage rptCenterPage = loginPage.loginAs(username, password);
			Assert.assertTrue("Verify the header image has been updated.",rptCenterPage.getHdImgInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf2200()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "1024x99.png";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setHeaderImg(testDataDir+img);
			frontPage.clickUploadForHeaderImg();
			
			//Verification
			Assert.assertEquals("Verify the output message after clicking upload.", "Upload successfully.", frontPage.getHeaderImgUploadConfInfo());
			Assert.assertTrue("Verify current image path.", frontPage.getCurrHeaderImgPath().endsWith(img));
			frontPage.logout();
			
			LoginPage loginPage = new LoginPage(frontPage.getWebDriver());
			RptCenterPage rptCenterPage = loginPage.loginAs(username, password);
			Assert.assertTrue("Verify the header image has been updated.", rptCenterPage.getHdImgInfo().contains(img));
			
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf2300()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "a.doc";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setHeaderImg(testDataDir+img);
			frontPage.clickUploadForHeaderImg();
			
			//Verification
			Assert.assertEquals("Verify the error message should be output.", 
								"The image file should be .png,.gif,.jpg or .bmp.",
								frontPage.getUploadHeaderImgErrMsg());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
	
	@Test(timeout=600000)
	public void testFrontPageConf2400()
	{
		try
		{
			String username = CommonUtil.getPropertyValue("username");
			String password = CommonUtil.getPropertyValue("password");
			//Test execution
			String img = "a.doc";
			FrontConfPage frontPage = navigateToTestPage(username, password);
			logger.info("Click Browse and set backgroud image");
			frontPage.setBackGroundImg(testDataDir+img);
			frontPage.clickUploadForBgImg();
			
			//Verification
			Assert.assertEquals("Verify the error message should be output.", 
								"The image file should be .png,.gif,.jpg or .bmp.",
								frontPage.getUploadBgImgErrMsg());
		}catch(Exception e)
		{
			CommonUtil.logError(e, testName);
		}finally
		{
			CommonUtil.closeBrowser();
		}
	}
}
