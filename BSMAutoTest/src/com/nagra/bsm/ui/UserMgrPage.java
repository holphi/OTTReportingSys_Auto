/**
 * @ClassName:     UserMgrPage
 * @Description:   -  
 * @author         alli  
 * @Date           2012/06/21 
 */
package com.nagra.bsm.ui;

import java.util.List;
import java.lang.Thread;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public class UserMgrPage extends AdminPage {
	
	public UserMgrPage(WebDriver driver)
	{
		super(driver);
	}
	
	//Basic elements
	private WebElement getDivCreateUser()
	{
		return driver.findElement(By.xpath("//div[text()='Create User']"));
	}
	private WebElement getDivUpdateUser()
	{
		return driver.findElement(By.xpath("//div[text()='Update User']"));
	}
	private WebElement getDivRemoveUser()
	{
		return driver.findElement(By.xpath("//div[text()='Remove User']"));
	}
	private List<WebElement> getTrUserList()
	{
		return driver.findElements(By.xpath("//table[@class='obj row20px']/tbody/tr"));
	}
	
	private Select getSelState()
	{
		return new Select(driver.findElement(By.id("userState")));
	}
	
	public void inputUserFilterInfo(String username)
	{
		try
		{
			WebElement item = driver.findElement(By.id("userNameFilter"));
			item.clear();
			for(char ch:username.toCharArray())
			{
				String key = String.valueOf(ch);
				item.sendKeys(key);
				Thread.sleep(1000);
			}
		}catch(Exception e)
		{}
	}
	
	public void selectState(String state)
	{
		getSelState().selectByVisibleText(state);
	}
	
	
	public void clickCreateUser()
	{
		getDivCreateUser().click();
	}
	
	public void clickUpdateUser()
	{
		getDivUpdateUser().click();
	}
	
	public void clickRemoveUser()
	{
		getDivRemoveUser().click();
	}
		
	public void clickUserFromUserList(String username)
	{
		username = username.toLowerCase();
		for(int i=1;i<getTrUserList().size();i++)
		{
			WebElement item=getTrUserList().get(i);
			logger.info(item.findElement(By.xpath("./td[2]")).getText());
			if(item.findElement(By.xpath("./td[2]")).getText().toLowerCase().equals(username))
			{
				item.click();
				return;
			}
		}
	}
	
	public void dbClickUserFromUserList(String username)
	{
		username = username.toLowerCase();
		for(int i=1;i<getTrUserList().size();i++)
		{
			WebElement item=getTrUserList().get(i);
			if(item.findElement(By.xpath("./td[2]")).getText().toLowerCase().equals(username))
			{
				Actions builder = new Actions(getWebDriver());
				Action dbClick = builder.doubleClick(item).build();
				dbClick.perform();
				return;
			}
		}
	}
	
	public int getUsersCountInUserList()
	{
		return getTrUserList().size()-1;
	}
	
	public boolean checkUserExistsInUserList(String username)
	{
		username = username.toLowerCase();
		for(int i=1;i<getTrUserList().size();i++)
		{
			WebElement item = getTrUserList().get(i);
			if(item.findElement(By.xpath("./td[2]")).getText().toLowerCase().equals(username))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isUserActiveInUserList(String username)
	{
		username=username.toLowerCase();
		for(int i=1;i<getTrUserList().size();i++)
		{
			WebElement item = getTrUserList().get(i);
			if(item.findElement(By.xpath("./td[2]")).getText().toLowerCase().equals(username))
			{
				if(item.findElement(By.xpath("./td[3]")).getText().toLowerCase().equals("active"))
					return true;
				else
					return false;
			}
		}
		return false;
	}
	
	public void associateRole(String userName, String roleName)
	{
		logger.info("Navigate to User Mgr Page.");
		logger.info(String.format("On RoleMgrPage, choose user %s and click Update User.", userName));
		this.clickUserFromUserList(userName);
		this.clickUpdateUser();
		UserEdit userEdit = new UserEdit(this.getWebDriver());
		userEdit.setRole(new String[]{roleName});
		userEdit.clickOK();
		userEdit.acceptAlertWindow();
	}
	
	public List<WebElement> getVisiableBtns(){
		return driver.findElements(By.cssSelector("div.dhx_toolbar_btn.def"));
	}
	
}
