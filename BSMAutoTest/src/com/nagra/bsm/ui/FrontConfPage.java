/**
 * @ClassName:     FrontConfPage
 * @Description:   -  
 * @author         alli  
 * @Date           2012/07/04 
 */
package com.nagra.bsm.ui;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import org.openqa.selenium.*;

import com.nagra.bsm.util.CommonUtil;

public class FrontConfPage extends AdminPage {
	
	public FrontConfPage(WebDriver driver)
	{
		super(driver);
		logger.info("FrontConfPage object created.");
	}
	
	public void clickUploadForBgImg()
	{
		logger.info("Click Upload button for background image.");
		driver.findElements(By.xpath("//div[text()='Upload']")).get(0).click();
		CommonUtil.sleep(5);
	}
	
	public void clickUploadForHeaderImg()
	{
		logger.info("Click Upload button.");
		driver.findElements(By.xpath("//div[text()='Upload']")).get(1).click();
		CommonUtil.sleep(5);
	}
	
	public void clickSave()
	{
		logger.info("Click Save button.");
		driver.findElement(By.xpath("//div[text()='Save']")).click();
	}
	
	public void inputLeftMarginValue(String value)
	{
		logger.info("Input left margin value: " + value);
		driver.findElement(By.id("leftMargin")).clear();
		driver.findElement(By.id("leftMargin")).sendKeys(value);
	}
	
	public void inputTopMarginValue(String value)
	{
		logger.info("Input top margin value: " + value);
		driver.findElement(By.id("topMargin")).clear();
		driver.findElement(By.id("topMargin")).sendKeys(value);
	}
	
	public String getUploadBgImgErrMsg()
	{
		return driver.findElement(By.id("uploadMsg")).getText().trim();
	}
	
	public String getUploadHeaderImgErrMsg()
	{
		return driver.findElement(By.id("headerImgUploadMsg")).getText().trim();
	}
	
	public String getLeftMarginErrorMsg()
	{
		return driver.findElement(By.id("leftMarginMsgContainer")).getText();
	}
	
	public String getTopMarginErrorMsg()
	{
		return driver.findElement(By.id("topMarginMsg")).getText();
	}
	
	public String getSavingConfInfo()
	{
		return driver.findElement(By.id("savingConfInfoLabel")).getText();
	}
	
	public String getBgImgUploadConfInfo()
	{
		return driver.findElement(By.id("infoLabel")).getText();
	}
	
	public String getHeaderImgUploadConfInfo()
	{
		return driver.findElement(By.id("headerImgUploadInfoLabel")).getText();
	}
	
	public String getCurrentImgWidth()
	{
		return driver.findElement(By.id("imgWidth")).getText();
	}
	
	public String getCurrentImgHeight()
	{
		return driver.findElement(By.id("imgHeight")).getText();
	}
	

 // @Author Alex Li
	public void setBackGroundImgIE(String path)
	{
		logger.info("Set front page background image file to: " + path);
		
		StringSelection stringSelection = new StringSelection(path);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		driver.findElement(By.xpath("//div[@id='subFileBGImgDiv']//div[text()='Browse']")).click();
		try
		{
			Robot robot = new Robot(); 
			robot.keyPress(KeyEvent.VK_CONTROL); 
			robot.keyPress(KeyEvent.VK_V); 
			robot.keyRelease(KeyEvent.VK_V); 
			robot.keyRelease(KeyEvent.VK_CONTROL); 
			robot.keyPress(KeyEvent.VK_ENTER); 
			robot.keyRelease(KeyEvent.VK_ENTER);
			//Protection code for handling modal dialog present exception
			CommonUtil.sleep(3);
		}
		catch(Exception e)
		{
			
		}
	}

	//@author Teresa Tang
	public void setBackGroundImgChrome(String path){
		logger.info("Set front page background image file to: " + path);
		driver.findElement(By.id("fileBGImg")).sendKeys(path);
		CommonUtil.sleep(3);
	}
	
	public void setBackGroundImg(String path){
		String browser = CommonUtil.getPropertyValue("browser");
		if (browser.equalsIgnoreCase("ie")){
			//setBackGroundImgIE(path);
			setBackGroundImgChrome(path);
		}else if (browser.equalsIgnoreCase("chrome")){
			setBackGroundImgChrome(path);
		}else if (browser.equalsIgnoreCase("firefox")){
			setBackGroundImgChrome(path);
		}else {
			logger.error("unknow browser type: " + browser);
		}			
	}

/* @Author: Alex Li
	public void setHeaderImgIE(String path)
	{
		logger.info("Set header image file to: " + path);
		StringSelection stringSelection = new StringSelection(path);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		driver.findElements(By.xpath("//div[text()='Browse']")).get(1).click();
		try
		{
			Robot robot = new Robot(); 
			robot.keyPress(KeyEvent.VK_CONTROL); 
			robot.keyPress(KeyEvent.VK_V); 
			robot.keyRelease(KeyEvent.VK_V); 
			robot.keyRelease(KeyEvent.VK_CONTROL); 
			robot.keyPress(KeyEvent.VK_ENTER); 
			robot.keyRelease(KeyEvent.VK_ENTER);
			//Protection code for handling modal dialog present exception
			CommonUtil.sleep(3);
		}
		catch(Exception e)
		{
			
		}
	}
*/
	
	//@author Teresa Tang
		public void setHeaderImg(String path){
			logger.info("Set front page background image file to: " + path);
			driver.findElement(By.id("fileHeaderImg")).sendKeys(path);
			CommonUtil.sleep(3);
		}
				
	public String getCurrBgImgPath()
	{
		return driver.findElement(By.id("currentImg")).getText().trim();
	}
	
	public String getCurrHeaderImgPath()
	{
		return driver.findElement(By.id("currentHeaderImg")).getText().trim();
	}
		
	public void restoreToDefaultBGImg()
	{
		String img = "SplashStandard700x260.jpg";
		if(!getCurrBgImgPath().endsWith(img))
		{
			String imgPath = CommonUtil.getCurrDir()+"\\testdata\\Image\\"+img;
			setBackGroundImg(imgPath);
			clickUploadForBgImg();
		}
	}
}
