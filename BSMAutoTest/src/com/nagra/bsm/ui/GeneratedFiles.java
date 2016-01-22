package com.nagra.bsm.ui;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.*;

import com.nagra.bsm.util.CommonUtil;

public class GeneratedFiles extends BasePage{
	
	public GeneratedFiles(WebDriver driver)
	{
		super(driver);
	}
	
	public boolean isOutputRptPresent(String linkText)
	{
		return isElementPresent(By.linkText(linkText));
	}
	
	public void clickItem(String linkText)
	{
		driver.findElement(By.linkText(linkText));
	}
	
	public void closeWindow()
	{
		driver.findElement(By.xpath("//div[@title='Close']")).click();
	}
	
	public void downloadOutputRpt(String outputRptName, String targetFileName)
	{
		StringSelection stringSelection = new StringSelection(targetFileName);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		
		driver.findElement(By.linkText(outputRptName)).click();
		
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
	
	
	public List<String> getGeneratedFiles(){
		List<String> files = new ArrayList<String>();
		List<WebElement> we = driver.findElements(By.xpath("//div[@id='generatedFilesGrid']//a")) ;
		Iterator<WebElement> it = we.iterator();		
		while(it.hasNext()){		
			files.add(it.next().getText());
		}
		return files;
	}
}
