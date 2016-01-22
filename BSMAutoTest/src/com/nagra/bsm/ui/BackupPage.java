/**
 * 
 */
package com.nagra.bsm.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author tetang
 *
 */
public class BackupPage extends AdminPage{
	public BackupPage(WebDriver driver){
		super(driver);
		logger.info("Backup Page object created.");
	}

	public void selectTypeScheduler(){
		driver.findElement(By.id("scheduler")).click();
		logger.info("Click scheduler radio button");
	}
	
	public void selectTypeManual(){
		driver.findElement(By.id("manually")).click();
		logger.info("Click manually radio button");
	}
	
	public void executeBackup(String bakpath){
		driver.findElement(By.id("btnBackupManually")).click();
		logger.info("Click Execute backup button");
	}
	
	public void setCronExpression(String expression){
		WebElement cronField = driver.findElement(By.id("expression"));
		cronField.clear();
		cronField.sendKeys(expression);
		logger.info("Input Cron Expression");
	}
	
	public void clickSaveButton(){
		driver.findElement(By.xpath("//div[text()=")).click();
		logger.info("Click Save button");
	}
	
	public void clickResetButton(){
		driver.findElement(By.id("btnReset")).click();
		logger.info("Click Save button");
	}
}
