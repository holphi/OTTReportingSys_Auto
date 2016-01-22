/**
 * @ClassName:     LoginPage
 * @Description:   -  
 * @author         alli  
 * @Date           2012/06/21 
 */

package com.nagra.bsm.ui;

import org.openqa.selenium.*;

public class LoginPage extends BasePage{
	
	public LoginPage(WebDriver driver)
	{
		super(driver);
	}
	
    private WebElement getTxtUserName()
    {
    	return driver.findElement(By.id("username"));
    }
    
    private WebElement getTxtPassword()
    {
    	return driver.findElement(By.xpath("//form[@id='loginForm']/table/tbody/tr[3]/td[2]/input"));
    }
    
    private WebElement getBtnOK()
    {
    	return driver.findElement(By.xpath("//input[@value='OK']"));
    }
    
    private WebElement getBtnCancel()
    {
    	return driver.findElement(By.xpath("//input[@value='Cancel']"));
    }
	
    public RptCenterPage loginAs(String username, String password)
    {
    	logger.info("Input username " + username);
    	getTxtUserName().sendKeys(username);
    	
    	logger.info("Input password " + password);
    	getTxtPassword().sendKeys(password);
    	
    	logger.info("Click OK button.");
    	getBtnOK().click();
    	
    	return new RptCenterPage(this.driver);
    }
    
    public RptCenterPage cancelLogin(String username, String password)
    {
    	logger.info("Input username " + username);
    	getTxtUserName().sendKeys(username);
    	
    	logger.info("Input password " + password);
    	getTxtPassword().sendKeys(password);
    	
    	logger.info("Click OK button.");
    	getBtnCancel().click();
    	
    	return new RptCenterPage(this.driver);
    }
    
    public boolean isErrorMsgOutput()
    {
        return this.getPageSource().indexOf("Login Failed")!=-1;
    }
    
    public String getBgImagInfo(){
    	return this.driver.findElement(By.id("loginTab")).getAttribute("style");
    }
}
