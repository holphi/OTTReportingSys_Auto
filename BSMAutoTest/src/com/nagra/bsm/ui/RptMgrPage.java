package com.nagra.bsm.ui;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.*;

import com.nagra.bsm.util.CommonUtil;

public class RptMgrPage extends AdminPage
{
	//private WebElement rowItem = null;
	
	public RptMgrPage(WebDriver driver)
	{
		super(driver);
	}
	
	public void clickAddRpt()
	{
		driver.findElement(By.xpath("//div[text()='Add Report Template']")).click();
	}
	
	public void clickSetToPublic()
	{
		driver.findElement(By.xpath("//div[text()='Set to Public']")).click();
	}
	
	public void clickSetToPrivate()
	{
		driver.findElement(By.xpath("//div[text()='Set to Private']")).click();
	}
	
	public void clickRemoveRpt()
	{
		driver.findElement(By.xpath("//div[text()='Remove Report Template']")).click();
	}
	
	private WebElement locateItemByRptName(String rptName) throws Exception
	{
		WebElement item = driver.findElement(By.linkText(rptName));
		//Return its parent node <tr>
		return item.findElement(By.xpath("../.."));
	}
	
	public boolean isRptItemPresent(String rptName)
	{
		return isElementPresent(By.linkText(rptName));
	}
	
	public void downloadRptItem(String rptName, String targetFileName) throws Exception
	{
		StringSelection stringSelection = new StringSelection(targetFileName);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		
		driver.findElement(By.linkText(rptName)).click();
		
		try
		{
			Robot robot = new Robot();
			//Move the key focus to Save button;
			robot.keyPress(KeyEvent.VK_LEFT);
			robot.keyRelease(KeyEvent.VK_LEFT);
			CommonUtil.sleep(2);
			//Press Save button;
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			CommonUtil.sleep(2);
			//Paste the target folder
			robot.keyPress(KeyEvent.VK_CONTROL); 
			robot.keyPress(KeyEvent.VK_V); 
			robot.keyRelease(KeyEvent.VK_V); 
			robot.keyRelease(KeyEvent.VK_CONTROL);
			//Press Enter to download file to target folder
			robot.keyPress(KeyEvent.VK_ENTER); 
			robot.keyRelease(KeyEvent.VK_ENTER);
			CommonUtil.sleep(8);
			robot.keyPress(KeyEvent.VK_ENTER); 
			robot.keyRelease(KeyEvent.VK_ENTER);
			//Protection code for handling modal dialog present exception
			CommonUtil.sleep(3);
		}catch(Exception ex)
		{
			
		}
	}
	
	public void checkRpt(String rptName) throws Exception
	{
		WebElement rptItem = locateItemByRptName(rptName);
		WebElement imgItem = rptItem.findElement(By.xpath("./td[1]/img"));
		if(imgItem.getAttribute("src").endsWith("chk0.gif")){
			imgItem.click();
		}
		logger.info("Check on report");	
	}
	
	public void checkRpt(String[] rptArr) throws Exception
	{
		for(String rpt:rptArr)
			this.checkRpt(rpt);
	}
	
	public void unCheckRpt(String rptName) throws Exception
	{
		WebElement rptItem = locateItemByRptName(rptName);
		WebElement imgItem = rptItem.findElement(By.xpath("./td[1]/img"));
		if(imgItem.getAttribute("src").endsWith("chk1.gif")){
			imgItem.click();
		}
		logger.info("Uncheck report");	
	}
	
	public boolean isRptItemChecked(String rptName) throws Exception
	{
		WebElement rptItem = locateItemByRptName(rptName);
		WebElement imgItem = rptItem.findElement(By.xpath("./td[1]/img"));
		if(imgItem.getAttribute("src").endsWith("chk0.gif"))
			return false;
		else
			return true;
	}
	
	public void clickUpdateRpt(String rptName) throws Exception
	{
		WebElement rptItem = locateItemByRptName(rptName);
		rptItem.findElement(By.xpath("./td[3]/a")).click();
		logger.info("Click on Update");
	}
	
	public void clickGenerateRpt(String rptName) throws Exception
	{
		WebElement rptItem = locateItemByRptName(rptName);
		rptItem.findElement(By.xpath("./td[4]/a")).click();
		logger.info("Click on Generate");
	}
	
	public void clickSetSchedule(String rptName) throws Exception
	{
		WebElement rptItem = locateItemByRptName(rptName);
		rptItem.findElement(By.xpath("./td[5]/a")).click();
		logger.info("Click on Set Schedule");
	}
	
	public void clickViewRpt(String rptName) throws Exception
	{
		WebElement rptItem = locateItemByRptName(rptName);
		rptItem.findElement(By.xpath("./td[6]/a")).click();
		logger.info("Click on View");
	}
	
	public boolean isGenericRpt(String rptName) throws Exception
	{
		WebElement rptItem = locateItemByRptName(rptName);
		String txt = rptItem.findElement(By.xpath("./td[7]")).getText();
		if(txt.equalsIgnoreCase("yes"))
			return true;
		else
			return false;
	}
	
	public void clickNavItem(String name)
	{
		String pattern = String.format("//div[text()"+"=\"%s\""+"]", name);
		driver.findElement(By.xpath(pattern)).click();
		logger.info(new StringBuilder("Click on paging bar: ").append(name));
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
	
	public int getFileCount()
	{
		int count = 0;
		int currNum = 0;
		
		while(this.isNavItemEnabled("Next")){
			//Exclude header row
			currNum = driver.findElements(By.xpath("//div[@class='objbox']/table/tbody/tr")).size() - 1;
			count = count + currNum;
			this.clickNavItem("Next");
		}
		currNum = driver.findElements(By.xpath("//div[@class='objbox']/table/tbody/tr")).size() - 1;
		count = count + currNum;
	
		this.clickNavItem("First");
		return count;
	}
	
	public void addReport(String rptName) throws Exception
	{
		this.clickAddRpt();
		
		RptUpload rptUpload = new RptUpload(this.getWebDriver());
		rptUpload.setFilePath(CommonUtil.getCurrDir() + "\\testdata\\RptTemplate\\" + rptName);
		rptUpload.clickUpload();
		CommonUtil.sleep(5);
		rptUpload.closeWindow();
	}
	
	public void setRptToPublic(String[] rptArr) throws Exception
	{
		this.checkRpt(rptArr);
		this.clickSetToPublic();
		this.acceptAlertWindow();
		this.acceptAlertWindow();
	}
	
	public void setRptToPrivate(String[] rptArr) throws Exception
	{
		this.checkRpt(rptArr);
		this.clickSetToPrivate();
		this.acceptAlertWindow();
		this.acceptAlertWindow();
	}
	
	
	public boolean isBtnPresent(String text)
	{
		return isElementPresent(By.xpath(String.format("//div[text()='%s']", text)));
	}
	
	public WebElement getSelectAll(){
		return driver.findElement(By.id("selectAll"));
	}
	
	public void chkOnSelectAll(){
		getSelectAll().click();
		logger.info("Check on Select All check box");
	}
	
	public List<WebElement> getVisiableBtns(){
		List<WebElement> we = driver.findElements(By.cssSelector("div.dhx_toolbar_btn.def"));
		Iterator<WebElement> it = we.iterator();		
		while(it.hasNext()){
			if(!(it.next().isDisplayed())){
				it.remove();
			}
		}
		return we;		
	}
	
	public void clickBatchUpload(){
		driver.findElement(By.xpath("//div[text()='Batch Upload Template']")).click();
	}
	
}
