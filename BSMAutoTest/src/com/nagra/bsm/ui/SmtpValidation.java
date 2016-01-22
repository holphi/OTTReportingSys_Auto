package com.nagra.bsm.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SmtpValidation extends BasePage {

	public SmtpValidation(WebDriver driver)
	{
		super(driver);
		logger.info("Smtp validation window created.");
	}
	
	public void inputEmailAddress(String value)
	{
		WebElement element = driver.findElement(By.id("sendingtoEmail"));
		element.clear();
		element.sendKeys(value);
	}
	
	public void clickValidate()
	{
		driver.findElement(By.xpath("//div[text()='Validate']")).click();
	}
	
	public void clickCancel()
	{
		driver.findElement(By.xpath("//div[text()='Cancel']")).click();
	}
	
	public void closeWindow()
	{
		driver.findElement(By.xpath("//div[@title='Close']")).click();
	}
}
