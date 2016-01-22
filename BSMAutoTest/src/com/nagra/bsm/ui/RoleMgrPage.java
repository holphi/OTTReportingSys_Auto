/**
 * @ClassName:     UserMgrPage
 * @Description:   -  
 * @author         alli  
 * @Date           2012/06/21 
 */
package com.nagra.bsm.ui;

import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import com.nagra.bsm.util.CommonUtil;

public class RoleMgrPage extends AdminPage {
	
	public RoleMgrPage(WebDriver driver)
	{
		super(driver);
		logger.info("RoleMgrPage object created.");
	}
	
	//Basic elements
	private WebElement getDivCreateRole()
	{
		return driver.findElement(By.xpath("//div[text()='Create Role']"));
	}
	
	private WebElement getDivUpdateRole()
	{
		return driver.findElement(By.xpath("//div[text()='Update Role']"));
	}
	
	private WebElement getDivRemoveRole()
	{
		return driver.findElement(By.xpath("//div[text()='Remove Role']"));
	}
	
	private List<WebElement> getTrUserList()
	{
		return driver.findElements(By.xpath("//table[@class='obj row20px']/tbody/tr"));
	}
	
	private Select getSelState()
	{
		return new Select(driver.findElement(By.id("roleFilterInput")));
	}
	
	public void selectRole(String roleName)
	{
		getSelState().selectByVisibleText(roleName);
	}
	
	public void clickCreateRole()
	{
		getDivCreateRole().click();
	}
	
	public void clickUpdateRole()
	{
		getDivUpdateRole().click();
	}
	
	public void clickRemoveRole()
	{
		getDivRemoveRole().click();
	}
		
	public void clickRoleFromRoleList(String roleName)
	{
		roleName = roleName.toLowerCase();
		for(int i=1;i<getTrUserList().size();i++)
		{
			WebElement item=getTrUserList().get(i);
			if(item.findElement(By.xpath("./td[2]")).getText().toLowerCase().equals(roleName))
			{
				item.click();
				return;
			}
		}
	}
	
	public String createSinglePermission(String parentNode, String permission)
	{
		String roleName = "role" + CommonUtil.getRandomStr();
		
		logger.info("On RoleMgrPage, click Create Role to create a new role.");
		this.clickCreateRole();
		
		RoleEdit roleEdit = new RoleEdit(this.getWebDriver());
		
		logger.info(String.format("On RoleEdit window, input new role name: %s", roleName));
		roleEdit.inputRoleName(roleName);
		roleEdit.expandTreeNode(parentNode);
		logger.info(String.format("On RoleEdit window, click on tree node: %s", permission));
		roleEdit.checkTreeNode(permission);
		logger.info("On RoleEdit window, click OK to save new role");
		roleEdit.clickOK();
		logger.info("On RoleEdit window, accept the alert window.");
		roleEdit.acceptAlertWindow();
		
		return roleName;
	}
	
	public String createSinglePermission(String[] parentNodes, String permission)
	{
		String roleName = "role" + CommonUtil.getRandomStr();
		
		logger.info("On RoleMgrPage, click Create Role to create a new role.");
		this.clickCreateRole();
		
		RoleEdit roleEdit = new RoleEdit(this.getWebDriver());
		
		logger.info(String.format("On RoleEdit window, input new role name: %s", roleName));
		roleEdit.inputRoleName(roleName);
		
		CommonUtil.sleep(30);
		
		for(String item : parentNodes)
		{
			logger.info(String.format("On RoleEdit window, expand node %s", item));
			roleEdit.expandTreeNode(item);
		}
		
		logger.info(String.format("On RoleEdit window, click on tree node: %s", permission));
		roleEdit.checkTreeNode(permission);
		logger.info("On RoleEdit window, click OK to save new role");
		roleEdit.clickOK();
		logger.info("On RoleEdit window, accept the alert window.");
		roleEdit.acceptAlertWindow();
		
		return roleName;
	}
	
	public void dbClickRoleFromRoleList(String roleName)
	{
		roleName = roleName.toLowerCase();
		for(int i=1;i<getTrUserList().size();i++)
		{
			WebElement item=getTrUserList().get(i);
			if(item.findElement(By.xpath("./td[2]")).getText().toLowerCase().equals(roleName))
			{
				Actions builder = new Actions(getWebDriver());
				Action dbClick = builder.doubleClick(item).build();
				dbClick.perform();
				return;
			}
		}
	}
	
	public int getRolesCountInUserList()
	{
		return getTrUserList().size()-1;
	}
	
	public boolean checkRoleExistsInUserList(String roleName)
	{
		roleName = roleName.toLowerCase();
		for(int i=1;i<getTrUserList().size();i++)
		{
			WebElement item = getTrUserList().get(i);
			if(item.findElement(By.xpath("./td[2]")).getText().toLowerCase().equals(roleName))
			{
				return true;
			}
		}
		return false;
	}
	
	public List<WebElement> getVisiableBtns(){
		return driver.findElements(By.cssSelector("div.dhx_toolbar_btn.def"));
	}
}
