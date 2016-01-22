/**
 * @ClassName:     BasePage
 * @Description:   -  
 * @author         alli  
 * @Date           2012/06/21 
 */

package com.nagra.bsm.ui;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;

import com.nagra.bsm.util.CommonUtil;

public abstract class BasePage {
	
	protected WebDriver driver = null;
	protected static final Logger logger = Logger.getLogger(BasePage.class);
	
	public BasePage(WebDriver driver) {
		this.driver = driver;
	}
	
	public String getTitle() {
		return this.driver.getTitle();
	}
	
	public WebDriver getWebDriver() {
		return this.driver;
	}
	
	public String getPageSource() {
		return this.driver.getPageSource();
	}
	
	public String getURL() {
		return this.driver.getCurrentUrl();
	}
	
	public void acceptAlertWindow() 
	{
		logger.info("Click OK to close pop-up dialog.");
		driver.findElement(By.xpath("//div[@class='d-buttons']/input[@value='Ok']")).click();
	} 

	public void cancelAlertWindow(){
		logger.info("Click Cancel to close pop-up dialog.");
		driver.findElement(By.xpath("//div[@class='d-buttons']/input[@value='Cancel']")).click();
	}
	
	public String getTextFromAlertWindow()
	{
		return driver.findElement(By.xpath("//td[@class='d-main']/div[@class='d-content']")).getText();
	}
	
	public void waitForElementPresent(By by) 
	{  
        for(int i=0; i<60; i++) 
        {  
        	if(isElementPresent(by)) 
        	{  
        		break;  
        	}else 
        	{  
        		try{  
        			driver.wait(1000);  
        		}catch (InterruptedException e) 
        		{  
        			e.printStackTrace();  
                }  
          	}  
        }
    }  
	
    public boolean isElementPresent(By by) 
    {  
        return (driver.findElements(by).size()>0)&&(driver.findElements(by).get(0).isDisplayed());  
    }  
    
    public boolean isWindowPresent(String title)
    {
    	return isElementPresent(By.xpath("//div[text()"+"=\"" + title + "\""+"]"));
    }
    
	public void dissmissAlertWindow()
	{
		logger.info("Click OK to close pop-up dialog.");
		driver.findElement(By.xpath("//div[@class='d-buttons']/input[@value='Cancel']")).click();
	}
	
/*	public boolean isDialogPresent(){
		String text = null;
		text=driver.findElement(By.xpath("//td[@class='d-main']/div[@class='d-content']")).getText();
		if(text!=null){
			return true;
		}
		return false;
	}
*/
	
	
	//get a value on Report Manager and Extraction Manager
	public String getValueFromTable(String fileName, int index){
		WebElement item = driver.findElement(By.linkText(fileName));
		WebElement rowItem = item.findElement(By.xpath("../.."));
		String i = new Integer(index).toString();
		String txt = rowItem.findElement(By.xpath("./td[index]".replace("index", i))).getText();
		return txt;
	}
	
	public List<WebElement> getElemFromAttr(String attrName,String sttrValue){
		String pattern = String.format("//*[@%s='%s']", attrName, sttrValue);
		return driver.findElements(By.xpath(pattern));
	}
	
	public WebElement getElemFromText(String text){
		String pattern = String.format("//*[text()='%s']", text);
		return driver.findElement(By.xpath(pattern));
	}
	
	public Alert getAlertWin(){
		CommonUtil.sleep(3);
		return driver.switchTo().alert();
	}
	
	public void doubleClick(Point p){
		try {
			Robot robot = new Robot();
			robot.mouseMove(p.x, p.y);					
			CommonUtil.sleep(1);
			robot.mousePress(InputEvent.BUTTON1_MASK);			
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.mousePress(InputEvent.BUTTON1_MASK);			
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			logger.error("fail to double click on point");
			e.printStackTrace();
		}		
	}
	
	public void Click(Point p){
		try {
			Robot robot = new Robot();
			moveMouseToPoint(p);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (AWTException e) {
			logger.error("fail to click point");
			e.printStackTrace();
		}		
	}
	
	public void moveMouseToPoint(Point p){
		Robot robot;
		try {
			robot = new Robot();
			robot.mouseMove(p.x, p.y);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			logger.error("fail to move mouse to point");
			e.printStackTrace();
		}		
	}
	
	public void moveMouseTo(WebElement we){
		moveMouseToPoint(we.getLocation());
	}
	
	public String getTxtFrmAlertWin(Alert alert){
		String msg = alert.getText().trim();
		logger.info(msg);
		return msg;
	}
	
	public void ConfirmOnAlert(Alert alert){
		alert.accept();
		logger.info("Confirm on Alert.");
	}
	
	public void CancelOnAlert(Alert alert){
		alert.dismiss();
		logger.info("Cancel on Alert.");
	}
	
	
/*	public void scrollAndClick(WebElement we){		
//		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",we);
		String js = String.format("scroll(0,%s)", we.getLocation().getY());
		WebElement cal = driver.findElement(By.xpath("//div[@class='dhx_cal_data']"));
		((JavascriptExecutor) driver).executeScript(js,cal);		
		CommonUtil.sleep(1);
		we.click();
	}
*/	
	public void scrollIntoView(WebElement we){
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",we);
		CommonUtil.sleep(1);
	}
}
