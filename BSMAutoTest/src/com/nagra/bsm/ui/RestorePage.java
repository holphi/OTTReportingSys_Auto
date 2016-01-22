/**
 * 
 */
package com.nagra.bsm.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author tetang
 *
 */
public class RestorePage extends AdminPage{

	public RestorePage(WebDriver driver) {
		super(driver);
		logger.info("Backup Page object created.");
	}

	public void restore(String bakfile){
		logger.info("Input backup file path");
		driver.findElement(By.id("fileRestore")).sendKeys(bakfile);
		logger.info("Execute Restore");
		driver.findElement(By.id("btnExeRestore")).click();		
	}
}
