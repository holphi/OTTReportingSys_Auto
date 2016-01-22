/**
 * @ClassName:     SettingsPage
 * @Description:   -  
 * @author         alli  
 * @Date           2012/06/21 
 */

package com.nagra.bsm.ui;

import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.*;

public class AdminPage extends RptCenterPage{
	
	public AdminPage(WebDriver driver)
	{
		super(driver);
		logger.info("AdminPage object created.");
	}
	
	public UserMgrPage goToUserMgrPage()
	{
		logger.info("Click User Manager.");
		driver.findElement(By.xpath("//span[text()"+"=\"User Manager\""+"]")).click();
		return new UserMgrPage(driver);
	}
	
	public RoleMgrPage goToRoleMgrPage()
	{
		logger.info("Click Role Manager.");
		driver.findElement(By.xpath("//span[text()"+"=\"Role Manager\""+"]")).click();
		return new RoleMgrPage(driver);
	}
	
	public LogViewerPage goToLogViewerPage()
	{
		logger.info("Click Log Viewer.");
		driver.findElement(By.xpath("//span[text()"+"=\"Log Viewer\""+"]")).click();
		return new LogViewerPage(driver);
	}
	
	public RptTransportPage goToRptTransportConfPage()
	{
		logger.info("Click Report Transport.");
		driver.findElement(By.xpath("//span[text()"+"=\"Report Transport Configuration\""+"]")).click();
		return new RptTransportPage(driver);
	}
	
	public RptTransportPage goToBakTransportConfPage()
	{
		logger.info("Click Backup Transport Configuration.");
		driver.findElement(By.xpath("//span[text()"+"=\"Backup Transport Configuration\""+"]")).click();
		return new RptTransportPage(driver);
	}
	
	public FrontConfPage goToFrontPageConfPage()
	{
		logger.info("Click Front Page Configuration.");
		driver.findElement(By.xpath("//span[text()"+"=\"Front Page\""+"]")).click();
		return new FrontConfPage(driver);
	}
	
	public BackupPage gotoBsmConfBakPage(){
		logger.info("Click BSM Configuration Backup");
		driver.findElement(By.xpath("//span[text()"+"=\"BSM Configuration Backup\""+"]")).click();
		return new BackupPage(driver);
	}
	
	public BackupPage gotoRptBakPage(){
		logger.info("Click Report Template Backup");
		driver.findElement(By.xpath("//span[text()"+"=\"Report Template Backup\""+"]")).click();
		return new BackupPage(driver);
	}
	
	public BackupPage gotoPdiTfmBakPage(){
		logger.info("Click PDI Transform Backup");
		driver.findElement(By.xpath("//span[text()"+"=\"PDI Transform Backup\""+"]")).click();
		return new BackupPage(driver);
	}
	
	public RestorePage gotoBsmCfgRestorePage(){
		logger.info("Click BSM Configuration Restore");
		driver.findElement(By.xpath("//span[text()"+"=\"BSM Configuration Restore\""+"]")).click();
		return new RestorePage(driver);
	}
	
	public RestorePage gotoRptRestorePage(){
		logger.info("Click Report Template Restore");
		driver.findElement(By.xpath("//span[text()"+"=\"Report Template Restore\""+"]")).click();
		return new RestorePage(driver);
	}
	
	public RestorePage gotoPdiTfmRestorePage(){
		logger.info("Click PDI Transform Restore");
		driver.findElement(By.xpath("//span[text()"+"=\"PDI Transform Restore\""+"]")).click();
		return new RestorePage(driver);
	}
	
	public void goToReportPurgeConfPage()
	{
		driver.findElement(By.xpath("//span[text()"+"=\"Report Purge Configuration\""+"]")).click();
	}
	
	public TransformVariablePage goToTransformVariablePage(){
		driver.findElement(By.xpath("//span[text()='Transform Variable']")).click();
		return new TransformVariablePage(driver);
	}
		
	
	public void clickNavItem(String name)
	{
		String pattern = String.format("//div[text()"+"=\"%s\""+"]", name);
		driver.findElement(By.xpath(pattern)).click();
	}
	
	public boolean isNavItemEnabled(String name)
	{
		String pattern = String.format("//div[text()"+"=\"%s\""+"]", name);
		WebElement navItem = driver.findElement(By.xpath(pattern));
		WebElement imgItem = navItem.findElement(By.xpath("../img"));
		if(imgItem.getAttribute("src").endsWith("dis.gif"))
			return false;
		else
			return true;
	}
	
	public String getCurrentPagingInfo()
	{
		String pattern = String.format("//div[text()"+"=\"%s\""+"]", "First");
		WebElement navItem = driver.findElement(By.xpath(pattern));
		WebElement pagingItem = navItem.findElement(By.xpath("../../div[3]"));
		return pagingItem.getText();
	}
	
	public boolean isNavBarDisplayed()
	{
		return driver.findElement(By.id("recinfoArea")).isDisplayed();
	}
	
	public boolean isColumnHeaderDisplayed(String columnName)
	{
		String pattern = String.format("//div[text()"+"=\"%s\""+"]", columnName);
		return driver.findElement(By.xpath(pattern)).isDisplayed();
	}
	
	public List<WebElement> getAdminTreeRow(){
		List<WebElement> we = driver.findElements(By.xpath("//span[contains(@class,'TreeRow')]"));
		we.remove(0);
		return we;
	}
}
