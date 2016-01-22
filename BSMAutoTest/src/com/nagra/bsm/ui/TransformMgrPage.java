package com.nagra.bsm.ui;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.*;

import com.nagra.bsm.util.CommonUtil;
//Current this page is named "Extraction Manager"
public class TransformMgrPage extends RptCenterPage
{
	
	public TransformMgrPage(WebDriver driver)
	{
		super(driver);
	}
	
	private WebElement locateItemByTransformName(String transformName) throws Exception
	{
		WebElement item = driver.findElement(By.linkText(transformName));
		//Return its parent node<tr>.
		return item.findElement(By.xpath("../.."));
	}
	public void ClickAddTransform()
	{
		driver.findElement(By.xpath("//div[text()='Add Transform']")).click();
	}
	public void ClickRemoveTransform()
	{
		driver.findElement(By.xpath("//div[text()='Remove Transform']")).click();
		
	}
	public void clickUpdateTransform(String transformName) throws Exception
	{
		WebElement transformItem = locateItemByTransformName(transformName);
		transformItem.findElement(By.xpath("./td[3]/a")).click();
	}
	
	public void clickExecuteTransform(String transformName) throws Exception{
		WebElement transformItem = locateItemByTransformName(transformName);
		transformItem.findElement(By.xpath("./td[4]/a")).click();
	}
	
	public void clickSetScheduleTransform(String transformName) {
		WebElement transformItem;
		try {
			transformItem = locateItemByTransformName(transformName);
			transformItem.findElement(By.xpath("./td[5]/a")).click();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void downloadTransformItem(String transformName, String targetFileName) throws Exception
	{
		StringSelection stringSelection = new StringSelection(targetFileName);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		
		driver.findElement(By.linkText(transformName)).click();
		
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
	public void checkOnTransform(String transformName) throws Exception
	{
		WebElement transformItem = locateItemByTransformName(transformName);
		WebElement imgItem = transformItem.findElement(By.xpath("./td[1]/img"));
		logger.info(imgItem.getAttribute("src"));
		if(imgItem.getAttribute("src").endsWith("chk0.gif"))
			imgItem.click();
	}
	
	public boolean isTransformPresent(String transformName)
	{
		return isElementPresent(By.linkText(transformName));
	}
	public boolean isElementPresent(By by)
	{
		return (driver.findElements(by).size()>0)&&(driver.findElements(by).get(0).isDisplayed());
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
	public void clickNavItem(String name)
	{
		String pattern = String.format("//div[text()"+"=\"%s\""+"]", name);
		driver.findElement(By.xpath(pattern)).click();
	}
	public String getCurrentPagingInfo()
	{
		String pattern = String.format("//div[text()"+"=\"%s\""+"]", "First");
		WebElement navItem = driver.findElement(By.xpath(pattern));
		WebElement pagingItem = navItem.findElement(By.xpath("../../div[3]"));
		return pagingItem.getText();
		
	}
	
	public void clickSetPublic(){
		driver.findElement(By.xpath("//div[text()='Set to Public']")).click();
	}
	
	public void clickSetPrivate(){
		driver.findElement(By.xpath("//div[text()='Set to Private']")).click();
	}
	
	public int getTransformCount(String transformName){
		return driver.findElements(By.linkText(transformName)).size();
	}
	
	public int getTransformTotalCount()
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
		return count;
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
