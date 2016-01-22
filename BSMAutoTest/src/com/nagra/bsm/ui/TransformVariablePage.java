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
public class TransformVariablePage extends AdminPage{
	
	public TransformVariablePage(WebDriver driver){
		super(driver);
		logger.info("Backup Page object created.");
	}
	
	public void clickCreateBtn(){
		driver.findElement(By.xpath("//div[text()='Create']")).click();
	}
	
	public void clickUpdateBtn(){
		driver.findElement(By.xpath("//div[text()='Update']")).click();
	}
	
	public void clickDeleteBtn(){
		driver.findElement(By.xpath("//div[text()='Delete']")).click();
	}
	
	public void inputVariableNameAndValue(String name,String value){
		WebElement VarName = driver.findElement(By.id("parameterNameInput"));
		VarName.clear();
		VarName.sendKeys(name);
		WebElement VarValue = driver.findElement(By.id("parameterValueInput"));
		VarValue.clear();
		VarValue.sendKeys(value);
		driver.findElement(By.id("btnOk")).click();
	}
}
