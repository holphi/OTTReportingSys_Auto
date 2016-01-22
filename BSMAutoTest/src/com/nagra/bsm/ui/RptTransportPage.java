package com.nagra.bsm.ui;

import java.util.Hashtable;

import junit.framework.Assert;

import org.openqa.selenium.*;

import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.ui.*;

public class RptTransportPage extends AdminPage {

	public RptTransportPage(WebDriver driver)
	{
		super(driver);
		logger.info("ReportTransportPage object created.");
	}
	
	//Basic elements
	//SMTP SETTINGS
	private WebElement txtSmtp = driver.findElement(By.id("smtp"));
	public void setSmtp(String value)
	{
		this.txtSmtp.clear();
		this.txtSmtp.sendKeys(value);
	}
	public String getSmtp()
	{
		return this.txtSmtp.getText().trim();
	}
	
	private WebElement txtSmtpPort = driver.findElement(By.id("emailPort"));
	public void setSmtpPort(String value)
	{
		this.txtSmtpPort.clear();
		this.txtSmtpPort.sendKeys(value);
	}
	public String getSmtpPort()
	{
		return this.txtSmtpPort.getText().trim();
	}
	
	private WebElement txtEmailAddress = driver.findElement(By.id("emailAdress"));
	public void setSenderEmailAddress(String value)
	{
		this.txtEmailAddress.clear();
		this.txtEmailAddress.sendKeys(value);
	}
	public String getSenderEmailAddress()
	{
		return this.txtEmailAddress.getText().trim();
	}
	
	private WebElement txtUser = driver.findElement(By.id("emailUser"));
	public void setSenderUserName(String value)
	{
		this.txtUser.clear();
		this.txtUser.sendKeys(value);
	}
	public String getSenderUserName()
	{
		return this.txtUser.getText().trim();
	}
	
	private WebElement txtPassword = driver.findElement(By.id("emailPassword"));
	public void setSenderPassword(String value)
	{
		this.txtPassword.clear();
		this.txtPassword.sendKeys(value);
	}
	public String getSenderPassword()
	{
		return this.txtPassword.getText().trim();
	}
	
	public String getSmtpSettingsResult()
	{
		return driver.findElement(By.id("infoLabel")).getText().trim();
	}
	
	public void clickSaveSmtpSettings()
	{
		driver.findElement(By.id("btnSaveSmtp")).click();
	}
	
	public void clickTestSmtp()
	{
		driver.findElement(By.id("btnTestSmtp")).click();
	}
	
	public void clickResetSmptSettings()
	{
		driver.findElement(By.id("btnResetSmtp")).click();
	}
	
	//SENDING REPORT REPEAT TIMES
	private WebElement txtSendingCount = driver.findElement(By.id("sendingRepeat"));
	public void setSendingCount(String value)
	{
		this.txtSendingCount.clear();
		this.txtSendingCount.sendKeys(value);
	}
	public String getSendingCount()
	{
		return this.txtSendingCount.getText().trim();
	}
	
	public String getSendingCountResult()
	{
		return driver.findElement(By.id("infoLabel2")).getText().trim();
	}
	
	public void clickSaveRepeatCount()
	{
		driver.findElement(By.id("btnSaveRepeatTimes")).click();
	}
	
	public void clickResetRepeatCount()
	{
		driver.findElement(By.id("btnResetRepeatTimes")).click();
	}
	
	//REPORT SENDING DEFAULT VALUES
	private WebElement txtDesEmail = driver.findElement(By.id("DesEmail"));
	public void setDesEmail(String value)
	{
		this.txtDesEmail.clear();
		this.txtDesEmail.sendKeys(value);
	}
	public String getDesEmail()
	{
		return this.txtDesEmail.getText().trim();
	}
	
	public void clickSaveDesEmail()
	{
		driver.findElement(By.id("btnSaveDefaultEmail")).click();
	}
	
	public String getDesEmailSettingsResult()
	{
		return driver.findElement(By.id("infoLabel3")).getText().trim();
	}
	
	public void clickTestEmail()
	{
		driver.findElement(By.id("btnTestDefaultEmail")).click();
	}
	
	public void clickResetDesEmail()
	{
		driver.findElement(By.id("btnRestDefaultEmail")).click();
	}
	
	//REPORT SENDING DEFAULT FTP VALUES
	private WebElement txtFtpIp = driver.findElement(By.id("ftpUrl"));
	public void setFtpIp(String value)
	{
		this.txtFtpIp.clear();
		this.txtFtpIp.sendKeys(value);
	}
	public String getFtpIp()
	{
		return this.txtFtpIp.getText().trim();
	}    
	
	private WebElement txtFtpPath = driver.findElement(By.id("ftpSavePath"));
	public void setFtpPath(String value)
	{
		this.txtFtpPath.clear();
		this.txtFtpPath.sendKeys(value);
	}
	public String getFtpPath()
	{
		return this.txtFtpPath.getText().trim();
	}
	
	private WebElement txtFtpUserName = driver.findElement(By.id("ftpUserName"));
	public void setFtpUserName(String value)
	{
		this.txtFtpUserName.clear();
		this.txtFtpUserName.sendKeys(value);
	}
	public String getFtpUserName()
	{
		return this.txtFtpUserName.getText().trim();
	}
	
	private WebElement txtFtpPassword = driver.findElement(By.id("ftpPassWord"));
	public void setFtpPassword(String value)
	{
		this.txtFtpPassword.clear();
		this.txtFtpPassword.sendKeys(value);
	}
	public String getFtpPassword()
	{
		return this.txtFtpPassword.getText().trim();
	}
	
	public String getFtpSettingsResult()
	{
		return driver.findElement(By.id("infoLabel4")).getText().trim();
	}
	
	public void clickSaveFtpSettings()
	{
		WebElement we = driver.findElement(By.id("btnSaveFtp"));	
		scrollIntoView(we);
		we.click();
		logger.info("Click Save btn for Ftp Settings");
	}
	
	public void clickTestFtp()
	{
		driver.findElement(By.id("btnTestFtp")).click();
	}
	
	public void clickResetFtp()
	{
		WebElement we = driver.findElement(By.id("btnResetFtp"));
		scrollIntoView(we);
		we.click();
	}
	
	//REPORT SENDING DEFAULT HTTP VALUE
	private WebElement txtHttpServerUrl = driver.findElement(By.id("webdavUrl"));
	public void setHttpServerUrl(String value)
	{
		this.txtHttpServerUrl.clear();
		this.txtHttpServerUrl.sendKeys(value);
	}
	public String getHttpServerUrl()
	{
		return this.txtHttpServerUrl.getText().trim();
	}
	
	private WebElement txtHttpLoginName = driver.findElement(By.id("webdavUserName"));
	public void setHttpLoginName(String value)
	{
		this.txtHttpLoginName.clear();
		this.txtHttpLoginName.sendKeys(value);
	}
	public String getHttpLoginName()
	{
		return this.txtHttpLoginName.getText().trim();
	}
	
	private WebElement txtHttpLoginPassword = driver.findElement(By.id("webdavPassword"));	
	public void setHttpLoginPassword(String value)
	{
		this.txtHttpLoginPassword.clear();
		this.txtHttpLoginPassword.sendKeys(value);
	}
	public String getHttpLoginPassword()
	{
		return this.txtHttpLoginPassword.getText().trim();
	}

	public String getHttpSettingsResult()
	{
		return driver.findElement(By.id("infoLabel5")).getText().trim();
	}
	
	public void clickSaveHttpSettings()
	{
		WebElement we = driver.findElement(By.id("btnSaveHttp"));		
		scrollIntoView(we);
		we.click();
		logger.info("Click Save btn for Http Settings");
	}
	
	public void clickTestHttp()
	{
		driver.findElement(By.id("btnTestHttp")).click();
	}
	
	public void clickResetHttp()
	{
		WebElement we = driver.findElement(By.id("btnResetHttp"));
		scrollIntoView(we);
		we.click();
	}
	
	public String getWarningInfo(String field)
	{
		String pattern = String.format("warning%s", field);
		return driver.findElement(By.id(pattern)).getText().trim();
	}
	public void verifyTransLog() throws Exception
	{
		LogViewerPage logPage = this.goToLogViewerPage();
		Assert.assertEquals("Verify there's should be a log item on Log View Page", 1, logPage.getLogCountInCurrPage());
		Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
		Assert.assertEquals("Verify column Action.", "Save Report Transport", logItem.get("Action"));
		Assert.assertEquals("Verify column Description.", "", logItem.get("Description"));
		Assert.assertEquals("Verify column Operator.", CommonUtil.getPropertyValue("username"), logItem.get("Operator"));
		Assert.assertEquals("Verify column Status.", "success", logItem.get("Status"));
		
	}
	
	public void verifyBakTransLog() throws Exception
	{
		LogViewerPage logPage = this.goToLogViewerPage();
		Assert.assertEquals("Verify there's should be a log item on Log View Page", 1, logPage.getLogCountInCurrPage());
		Hashtable<String, String> logItem = logPage.getLogItemByIndex(1);
		Assert.assertEquals("Verify column Action.", "Save backup transport configuration", logItem.get("Action"));
		Assert.assertEquals("Verify column Description.", "", logItem.get("Description"));
		Assert.assertEquals("Verify column Operator.", CommonUtil.getPropertyValue("username"), logItem.get("Operator"));
		Assert.assertEquals("Verify column Status.", "success", logItem.get("Status"));
		
	}
}
