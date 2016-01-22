package com.nagra.bsm.ui;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RoleEdit extends BasePage{
	
	public RoleEdit(WebDriver driver)
	{
		super(driver);
		logger.info("User Edit window created.");
	}
	
	private WebElement getTxtRoleName()
	{
		return driver.findElement(By.id("roleNameInput"));
	}
	
	private WebElement getTxtComments()
	{
		return driver.findElement(By.id("roleCommnetInput"));
	}

	private WebElement getBtnOK()
	{
		return driver.findElement(By.xpath("//div[text()='Ok']"));
	}
	
	private WebElement getBtnCancel()
	{
		return driver.findElement(By.xpath("//div[text()='Cancel']"));
	}
	
	private WebElement getDivClose()
	{
		return driver.findElement(By.xpath("//div[@title='Close']"));
	}
	
	public void inputRoleName(String roleName)
	{
		logger.info(String.format("Input %s to the text box.",roleName));
		getTxtRoleName().sendKeys(roleName);
	}
	public void setRoleName(String rolename)
	{
		logger.info("Input rolename:" + rolename);
		getTxtRoleName().clear();
		getTxtRoleName().sendKeys(rolename);
	}
	
	public void inputComments(String comments)
	{
		logger.info(String.format("Input %s to the comment text box.",comments));
		getTxtComments().sendKeys(comments);
	}
	public void setComments(String comments)
	{
		logger.info("Input comments:" + comments);
		getTxtComments().clear();
		getTxtComments().sendKeys(comments);
	}
	
	public String getRoleName()
	{
		return getTxtRoleName().getAttribute("value");
	}
	
	public String getComments()
	{
		return getTxtComments().getAttribute("value");
	}
	
	public void expandTreeNode(String name)
	{
		String pattern = String.format("//span[text()=\"%s\"]", name);
		List<WebElement> l = driver.findElements(By.xpath(pattern));
		WebElement spanItem = l.get(l.size()-1); 
		WebElement imgItem = spanItem.findElement(By.xpath("../../td[1]/img[1]"));
		String imgSrc = imgItem.getAttribute("src");
		if(imgSrc.endsWith("plus4.gif")||imgSrc.endsWith("plus3.gif"))
		{
			logger.info(String.format("Expand tree node: %s", name));
			imgItem.click();
		}
	}
	
	public void collapseTreeNode(String name)
	{
		String pattern = String.format("//span[text()=\"%s\"]", name);
		List<WebElement> l = driver.findElements(By.xpath(pattern));
		WebElement spanItem = l.get(l.size()-1);
		WebElement imgItem = spanItem.findElement(By.xpath("../../td[1]/img[1]"));
		String imgSrc = imgItem.getAttribute("src");
		if(imgSrc.endsWith("minus4.gif")||imgSrc.endsWith("minus3.gif"))
		{
			logger.info(String.format("Collapse tree node: %s", name));
			imgItem.click();
		}
	}
	
	public void checkTreeNode(String name)
	{	
		String pattern = String.format("//span[text()=\"%s\"]", name);
		List<WebElement> l = driver.findElements(By.xpath(pattern));
		WebElement spanItem = l.get(l.size()-1);
		WebElement imgItem = spanItem.findElement(By.xpath("../../td[2]/img[1]"));
		String imgSrc = imgItem.getAttribute("src");
		if(imgSrc.endsWith("iconUncheckAll.gif")||imgSrc.endsWith("iconCheckGray.gif"))
		{
			logger.info(String.format("Check tree node: %s", name));
			imgItem.click();
		}
	}
	
	public void unCheckTreeNode(String name)
	{
		String pattern = String.format("//span[text()=\"%s\"]", name);
		List<WebElement> l = driver.findElements(By.xpath(pattern));
		WebElement spanItem = l.get(l.size()-1);
		WebElement imgItem = spanItem.findElement(By.xpath("../../td[2]/img[1]"));
		String imgSrc = imgItem.getAttribute("src");
		if(imgSrc.endsWith("iconCheckAll.gif"))
		{
			logger.info(String.format("Uncheck tree node: %s", name));
			imgItem.click();
		}
	}
	
	public boolean isTreeNodeChecked(String name)
	{
		String pattern = String.format("//span[text()=\"%s\"]", name);
		List<WebElement> l = driver.findElements(By.xpath(pattern));
		WebElement spanItem = l.get(l.size()-1);
		WebElement imgItem = spanItem.findElement(By.xpath("../../td[2]/img[1]"));
		String imgSrc = imgItem.getAttribute("src");
		if(imgSrc.endsWith("iconCheckAll.gif")||imgSrc.endsWith("iconCheckGray.gif"))
			return true;
		else
			return false;
	}
	
	public void clickOK()
	{
		logger.info("Click Button OK.");
		getBtnOK().click();
	}
	
	public void clickCancel()
	{
		logger.info("Click Button Cancel.");
		getBtnCancel().click();
	}
	
	public void closeWindow()
	{
		logger.info("Close User Edit Window.");
		getDivClose().click();
	}
}
