/**
 * @ClassName:     UserEdit
 * @Description:   -  
 * @author         alli  
 * @Date           2012/06/21 
 */

package com.nagra.bsm.ui;

import java.util.List;

import org.openqa.selenium.*;

//Div Window for User Edit
public class UserEdit extends BasePage {
	
	public UserEdit(WebDriver driver)
	{
		super(driver);
		logger.info("User Edit window created.");
	}
	
	private WebElement getTxtUserName()
	{
		return driver.findElement(By.id("userNameInput"));
	}
	private WebElement getTxtPassword()
	{
		return driver.findElement(By.id("passwordInput"));
	}
	private WebElement getTxtPasswordConfirm()
	{
		return driver.findElement(By.id("passwordConfirmInput"));
	}
	private WebElement getRdBtnStatusActive()
	{
		return driver.findElement(By.id("stautsActiveInput"));
	}
	private List<WebElement> getTrRoleList()
	{
		return driver.findElements(By.xpath("//div[@id='roleListDiv']/div[2]/table/tbody/tr"));
	}
	private WebElement getRdBtnStatusInActive()
	{
		return driver.findElement(By.id("stautsInactiveInput"));
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
	
	public void setUserName(String username)
	{
		logger.info("Input username: " + username);
		getTxtUserName().clear();
		getTxtUserName().sendKeys(username);
	}
		
	public void setPassword(String password)
	{
		logger.info("Input password: " + password);
		getTxtPassword().clear();
		getTxtPassword().sendKeys(password);
	}
	
	public void setConfirmPassword(String passwordConfirm)
	{
		logger.info("Input confirmation password: " + passwordConfirm);
		getTxtPasswordConfirm().clear();
		getTxtPasswordConfirm().sendKeys(passwordConfirm);
	}
	
	public void setStatus(boolean isActive)
	{
		if(isActive)
		{
			logger.info("Select Active.");
			getRdBtnStatusActive().click();
		}
		else
		{
			logger.info("Select InActive.");
			getRdBtnStatusInActive().click();
		}	
	}
	
	public void setRole(String[] roleNameArr)
	{
		for(String roleName:roleNameArr)
		{
			for(int i=1;i<getTrRoleList().size();i++)
			{
				WebElement item = getTrRoleList().get(i);
				if(item.findElement(By.xpath("./td[3]")).getText().equalsIgnoreCase(roleName))
				{
					logger.info("Select role: " + roleName);
					item.findElement(By.xpath("./td[1]/img")).click();
				}
			}
		}
	}
	
	public boolean isSpecifiedRoleActive(String[] roleNameArr)
	{
		for(String roleName:roleNameArr)
		{
			for(int i=1;i<getTrRoleList().size();i++)
			{
				WebElement item = getTrRoleList().get(i);
				if(item.findElement(By.xpath("./td[3]")).getText().equalsIgnoreCase(roleName))
				{
					logger.info("Check role status: " + roleName);
					if(item.findElement(By.xpath("./td[1]/img")).getAttribute("src").endsWith("item_chk1.gif"))
						continue;
					else
						return false;
				}
			}
		}
		return true;
	}
	
	public void inputUserData(String username, String password, String passwordConfirm, boolean isActive, String[] roleNameArr)
	{
		setUserName(username);
		setPassword(password);
		setConfirmPassword(passwordConfirm);
		setStatus(isActive);
		setRole(roleNameArr);
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
