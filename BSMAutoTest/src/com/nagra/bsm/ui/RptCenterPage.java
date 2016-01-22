/**
 * @ClassName:     ReportCenterPage
 * @Description:   -  
 * @author         alli  
 * @Date           2012/06/21 
 */

package com.nagra.bsm.ui;

import java.util.List;

import org.openqa.selenium.*;

import com.nagra.bsm.util.CommonUtil;

public class RptCenterPage extends BasePage{
	
	public RptCenterPage(WebDriver driver)
	{
		super(driver);
	}
	
    public void goToHome()
    {
    	driver.findElement(By.xpath("//div[@id='tabbar']/div/div/div/div/div[3]")).click();
    }

    public RptScheduleMgrPage goToRptScheduleMgr()
    {
    	driver.findElement(By.xpath("//span[text()"+"=\"Report Schedule Manager\""+"]")).click();
    	return new RptScheduleMgrPage(driver);
    }

    public RptMgrPage goToReportMgr()
    {
    	driver.findElement(By.xpath("//span[text()"+"=\"Report Manager\""+"]")).click();
    	return new RptMgrPage(driver);
    }
    public TransformMgrPage goToTransformMgr()
    {	
    	CommonUtil.sleep(3);
    	driver.findElement(By.xpath("//span[text()"+"=\"Extraction Manager\""+"]")).click();
    	return new TransformMgrPage(driver);
    }

    public ExtractionScheduleMgrPage goToExtraScheduleMgr(){
    	driver.findElement(By.xpath("//span[text()='Extraction Schedule Manager']")).click();
    	return new ExtractionScheduleMgrPage(driver);
    }
    
    public AdminPage goToAdmin()
    {
    	CommonUtil.sleep(3);
    	driver.findElement(By.xpath("//span[text()"+"=\"Administration\""+"]")).click();
    	return new AdminPage(driver);
    }

    public void goToHelp()
    {
    	driver.findElement(By.xpath("//span[text()"+"=\"Help\""+"]")).click();
    }
    
    public LoginPage logout()
    {
    	driver.findElement(By.id("userNameLabel")).click();
    	WebElement we = driver.findElement(By.xpath("//*[@id='menu']/table/tbody/tr[6]/td/div"));    	
    	we.click();
    	logger.info("log out");
    	return new LoginPage(driver);
    }
    
    public void clickChangePassword()
    {
    	driver.findElement(By.id("userImg")).click();
    	driver.findElement(By.xpath("//*[@id='menu']/table/tbody/tr[2]/td")).click();
    }
    
    public boolean isMenuItemPresent(String name)
    {
    	String text = "//span[text()"+"=\"" + name +"\""+"]";
    	return isElementPresent(By.xpath(text));
    }
    
    public int getMenuItemCount()
    {
    	CommonUtil.sleep(2);
    	List<WebElement> menuItems = driver.findElements(By.xpath("//div[contains(@class,'dhx_tab_element')]"));
    	return menuItems.size();
    }
    
    public String getHdImgInfo(){
    	return this.driver.findElement(By.id("branding")).getAttribute("style");
    }
    
    public List<WebElement> getMainTabs(){
    	return this.driver.findElements(By.xpath("//div[@class='dhx_tabbar_row']/div/span"));
    }
}
