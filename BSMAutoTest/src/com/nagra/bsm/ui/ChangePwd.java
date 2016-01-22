package com.nagra.bsm.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ChangePwd extends BasePage {
	
	public ChangePwd(WebDriver driver)
	{
		super(driver);
		logger.info("Change password window created.");
	}
	
	public void inputOldPwd(String pwd)
	{
		logger.info("Input old password.");
		WebElement e = driver.findElement(By.id("oldPwd"));
		e.clear();
		e.sendKeys(pwd);
	}
	
	public void inputNewPwd(String pwd)
	{
		logger.info("Input new password.");
		WebElement e = driver.findElement(By.id("newPwd"));
		e.clear();
		e.sendKeys(pwd);
	}
	
	public void inputConfirmPwd(String pwd)
	{
		logger.info("Input confirmation password.");
		WebElement e = driver.findElement(By.id("confirmPwd"));
		e.clear();
		e.sendKeys(pwd);
	}
	
	public void clickOK()
	{
		driver.findElement(By.xpath("//div[text()"+"=\"OK\""+"]")).click();
	}
	
	public String getErrorMsgFromOldPwd()
	{
		if(isElementPresent(By.id("oldPwdMsg")))
			return driver.findElement(By.id("oldPwdMsg")).getText();
		else
			return "";
	}
	
	public String getErrorMsgFromNewPwd()
	{
		if(isElementPresent(By.id("newPwdMsg")))
			return driver.findElement(By.id("newPwdMsg")).getText();
		else
			return "";
	}
	
	public String getErrorMsgFromConfirmPwd()
	{
		if(isElementPresent(By.id("confirmPwdMsg")))
			return driver.findElement(By.id("confirmPwdMsg")).getText();
		else
			return "";
	}
	
	public void closeWindow()
	{
		driver.findElement(By.xpath("//div[@title"+"=\"Close\""+"]")).click();
	}
}
