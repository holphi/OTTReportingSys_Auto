package com.nagra.bsm.ui;

import org.openqa.selenium.*;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.List;

import com.nagra.bsm.util.CommonUtil;

public class RptUpload extends BasePage {
	
	public RptUpload(WebDriver driver)
	{
		super(driver);
		logger.info("RptUpload window created.");
	}
	
//by Alex Li
	/*	public void setFilePath(String filePath) throws Exception
	{
		logger.info("Set file path to " + filePath);
		StringSelection stringSelection = new StringSelection(filePath);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		driver.findElement(By.xpath("//span/nobr[contains(text(),'Add file')]")).click();
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
			throw e;
		}
	}
*/

//by Teresa Tang
	public void setFilePath(String filePath){
		logger.info("Set file path to " + filePath);
		driver.findElement(By.xpath("//div[@class='btnAddDiv']/input")).sendKeys(filePath);					
		CommonUtil.sleep(3);
	}
	
	public void clickUpload()
	{
		driver.findElement(By.xpath("//span/nobr[contains(text(),'Upload')]")).click();
	}

	public void clickClean()
	{
		driver.findElement(By.xpath("//span/nobr[contains(text(),'Clean')]")).click();
	}
	
	public void removeFile(String fileName)
	{
		List<WebElement> trList = getRptTrList();
		for(int i=0;i<trList.size();i++)
		{
			WebElement tr = trList.get(i);
			WebElement divFileName = tr.findElement(By.xpath(".//td/table/tbody/tr[1]/td[2]/div/div"));
			if(divFileName.getText().equals(fileName))
			{
				WebElement imgRemove = tr.findElement(By.xpath(".//td/table/tbody/tr[1]/td[3]"));
				imgRemove.click();
			}
		}
	}
	
	private List<WebElement> getRptTrList()
	{
		return driver.findElements(By.xpath("//table[@id='tblListFiles']/tbody/tr"));
	}
	
	public boolean isFileListedInContainer(String fileName)
	{
		List<WebElement> trList = getRptTrList();
		for(int i=0;i<trList.size();i++)
		{
			WebElement tr = trList.get(i);
			WebElement divFileName = tr.findElement(By.xpath(".//td/table/tbody/tr[1]/td[2]/div/div"));
			if(divFileName.getText().equals(fileName))
					return true;
		}
		return false;
	}
	
	public void closeWindow()
	{
		driver.findElement(By.xpath("//div[@title='Close']")).click();
	}
	
	public boolean isAddBtnEnabled()
	{
		if(isElementPresent(By.xpath("//input[@id='file1']")))
			return true;
		else
			return false;
	}
	
	public String getUploadMsg(String fileName)
	{
		List<WebElement> trList = getRptTrList();
		for(int i=0;i<trList.size();i++)
		{
			WebElement tr = trList.get(i);
			WebElement divFileName = tr.findElement(By.xpath(".//td/table/tbody/tr[1]/td[2]/div/div"));
			if(divFileName.getText().equals(fileName))
			{
				WebElement divMsg = tr.findElement(By.xpath(".//td/table/tbody/tr[2]/td[1]/font"));
				return divMsg.getText();
			}
		}
		return "";
	}
}
