/**
 * 
 */
package com.nagra.bsm.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;

import com.nagra.bsm.ui.BasePage;
import com.nagra.bsm.ui.LoginPage;
import com.nagra.bsm.util.CommonUtil;
import com.thoughtworks.selenium.Selenium;

/**
 * @author tetang
 *
 */
public class BrowserTasks {
	public WebDriver driver;
	protected static final Logger logger = Logger.getLogger(BrowserTasks.class);
	
	public BrowserTasks(){
		
	}
	
	public BrowserTasks(BasePage page){
		this.driver = page.getWebDriver();
	}
	
	public BrowserTasks(WebDriver driver){
		this.driver = driver;
	}
	
	public WebDriver getDriverFromPage(BasePage page){
		return page.getWebDriver();
	}	
	
	public void launchBrowser(){
		CommonUtil.launchBrowser();
		this.driver = CommonUtil.driver;
	}
	
	public void launchBrowser(WebDriver driver){
		CommonUtil.driver = driver;
		CommonUtil.launchBrowser();
	}
	
	public void closeBrowser(){
		try{
			CommonUtil.driver = this.driver;
			CommonUtil.closeBrowser();
			logger.info("Close browser.");
		}finally{		
			killIEDriverServerProcess();
		}	
	}	
	
	public LoginPage getURL(){
		if(driver!=null)
		{
			driver.manage().getCookies().clear();
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.MINUTES);
			String url = String.format("http://%s:8080/bsm", CommonUtil.getPropertyValue("host_name"));
			driver.get(url);
			logger.info("Open URL " + url);
			Selenium selenium = new WebDriverBackedSelenium(driver, url);
			selenium.windowMaximize();
			return new LoginPage(driver);
		}else
			{
			logger.error("driver is null");
			return null;
			}		
	}
	
	public LoginPage reloadLoginPage(){
		getURL();
		LoginPage loginPage = new LoginPage(this.driver);
		return loginPage;
	}
	
	private void killIEDriverServerProcess(){
		String cmd= "tasklist /nh /FI \"IMAGENAME eq IEDriverServer.exe\"";
		try {
			Process plist = Runtime.getRuntime().exec(cmd);
			InputStreamReader inReader = new InputStreamReader(plist.getInputStream());
			try {
				plist.waitFor();
				plist.destroy();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			BufferedReader br = new BufferedReader(inReader);
			String line = null;
			while((line = br.readLine()) != null){				
				if(line.indexOf("IEDriverServer.exe")!=-1){
					Process pkill = Runtime.getRuntime().exec("taskkill /F /T /IM IEDriverServer.exe");	
					try {
						pkill.waitFor();
						pkill.destroy();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public String getPageTitle(){
		return driver.getTitle();
	}
	
	public void refreshPage(){
		driver.navigate().refresh();
	}
}
