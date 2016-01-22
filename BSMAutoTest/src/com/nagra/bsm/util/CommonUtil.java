/**
 * @ClassName:     CommonUtil.java
 * @Description:   Common Utility  
 * @author         alli  
 * @Date           2012/06/21 
 */

package com.nagra.bsm.util;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.regex.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.List;
import java.util.Hashtable;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.Thread;
import java.util.Calendar;

import junit.framework.Assert;

import org.junit.rules.TestName;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import org.apache.commons.net.ftp.*;

import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;

import com.thoughtworks.selenium.Selenium;

public class CommonUtil 
{
	private static Properties properties = null;
	public static WebDriver driver = null;
	private static final Logger logger = Logger.getLogger(CommonUtil.class);	
	private static FTPClient ftpClient = null;
	private static String rptPath = "/soft/bsmsoft/bsm1/bsm/current/birtreports";
	private static String transformPath = "/soft/bsmsoft/bsm1/bsm/current/pdi";
	private static String rptOutputPath = "/soft/bsmsoft/bsm1/bsm/current/data";
	private static String generatedRptPath = "/soft/bsmsoft/bsm1/bsm/current/data";
	
	static
	{
		File pFile = new File("config\\BSMConfig.xml");
		FileInputStream pInStream = null;
		try
		{
			pInStream = new FileInputStream(pFile);
			properties = new Properties();
			properties.loadFromXML(pInStream);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void logError(Exception e,TestName n)
	{
		takeScreenshot(n.getMethodName());
		logger.error("Exception thrown in test execution: ",e);		 
		Assert.fail();
	}
	
	public static void takeScreenshot(String fileName)
	{
		if(driver==null)
			return;

		String dirName="Screenshots";
		if(!(new File(dirName).isDirectory())){
			new File(dirName).mkdir();
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String name = fileName+"_"+sdf.format(new Date());
		
		File source = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try{
			FileUtils.copyFile(source, new File(dirName + File.separator + name + ".png"));
			logger.info("Take screenshot: " + name);
		}catch(IOException e)
		{
			logger.error(String.format("Exception thrown in screenshot saving operation:%s",e.getMessage()));
		}
	}
	
	public static String getPropertyValue(String key)
	{
		return properties.getProperty(key);
	}
	
	public static String getCurrDir()
	{
		return System.getProperty("user.dir");
	}
	
	public static Calendar getClientCurrDate(){
		Calendar calendar = Calendar.getInstance();
		return calendar;
	}
	
	public static int getCurrentDay(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DATE);
	}		
	
	public static void sleep(int second)
	{   
		try
		{
			logger.info(String.format("Sleep for %d seconds.", second));
			Thread.sleep(second*1000);
		}catch(Exception e)
		{}
	}
	
	public static Hashtable<String,String> getFrontPageConfDataFromDb()
	{
		Hashtable<String, String> data = new Hashtable<String, String>();
		String sql = "SELECT LEFT, TOP, WIDTH, HEIGHT, BGIMAGURL FROM FRONT_PAGE_CONFIGURATION";
		ResultSet rs = DBUtil.executeQuery(sql);
		try
		{
			if(rs.next())
			{
				data.put("LEFT", String.valueOf(rs.getFloat("LEFT")));
				data.put("TOP", String.valueOf(rs.getFloat("TOP")));
				data.put("WIDTH", String.valueOf(rs.getFloat("WIDTH")));
				data.put("HEIGHT", String.valueOf(rs.getFloat("HEIGHT")));
			}
		}catch(Exception e){
			
		}
		finally{
			DBUtil.closeDBRes();
		}
		return data;
	}
	
	public static WebDriver launchTestPortal()
	{		
		launchBrowser();
		
		if(driver!=null)
		{
			driver.manage().getCookies().clear();
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.MINUTES);
			String url = String.format("http://%s:8080/bsm", CommonUtil.getPropertyValue("host_name"));
			driver.get(url);
			logger.info("Open URL " + url);
			Selenium selenium = new WebDriverBackedSelenium(driver, url);
			selenium.windowMaximize();
		}
		return driver;
	}
	
	public static void launchBrowser()
	{
		String browser = getPropertyValue("browser").toLowerCase();
		if(browser.equals("ie"))
		{
			System.setProperty("webdriver.ie.driver", CommonUtil.getCurrDir()+"\\exe\\IEDriverServer.exe");
			DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
			ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			driver = new InternetExplorerDriver(ieCapabilities);
		}
		else if(browser.equals("firefox")){
			driver = new FirefoxDriver();	
		}		
		else if(browser.equals("chrome")){
			System.setProperty("webdriver.chrome.driver", CommonUtil.getCurrDir()+"\\exe\\chromedriver.exe");
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--start-maximized");
			driver = new ChromeDriver(options);
		}			
		logger.info("Launch Browser: " + browser);
	}
	
	//Remove user and associated data from BSM_USER
	public static int removeUserDataFromDb(String username)
	{
		//Get USER_ID from table BSM_USER
		String sql = String.format("SELECT USER_ID FROM BSM_USER WHERE USER_NAME='%s'", username);
		ResultSet rs = DBUtil.executeQuery(sql);
		int userID = 0;
		try
		{
			if(rs.next())
			{
				//Remove associated data from BSM_USER_ROLE
				userID = rs.getInt("USER_ID");
				DBUtil.closeDBRes();
				removeUserRoleDataFromDb(userID);
			}
		}catch(SQLException ex)
		{
		}
		
		//Remove user data from table BSM_USER
		sql = String.format("DELETE FROM BSM_USER WHERE USER_NAME='%s'", username);
		int ret = DBUtil.executeSQL(sql);
		return ret;
	}
	
	public static int removeAllActivityLog()
	{
		String sql = "DELETE FROM ACTIVITY_LOG";
		int ret = DBUtil.executeSQL(sql);
		return ret;
	}
	
	//Remove role from BSM_USER
	public static int removeRoleFromDb(String roleName)
	{		
		//Remove user data from table BSM_USER
		String sql = String.format("DELETE FROM BSM_ROLE WHERE ROLE_NAME='%s'", roleName);
		int ret = DBUtil.executeSQL(sql);
		return ret;
	}
	
	//Remove transform from db
	public static void removeTransformFromDb(String userName,String ...transformName)
	{
		int userID = getUserIDFromDb(userName);
		StringBuilder sqlBuilder = new StringBuilder("DELETE FROM BSM_ETL WHERE ETL_NAME='%s' AND CREATE_USER_ID='%d'");
		for(String transform : transformName)
		{
			String sql = String.format(sqlBuilder.toString(), transform, userID);
			DBUtil.executeSQL(sql);
		}
	}
	
	public static int rmScheduledEvent(String rptEventName)
	{
		//Query event it from DB;
		int event_id = Integer.parseInt(CommonUtil.getRptEventFromDb(rptEventName).get("ID")); 
		
		//Remove associated data from table BSM_SCHEDULER_EVENT_REPORT first
		String sql = String.format("DELETE FROM BSM_SCHEDULER_EVENT_REPORT WHERE SCHEDULER_EVENT_ID=%d", event_id);
		DBUtil.executeSQL(sql);
	
		//Remove report event from table BSM_SCHEDULER_EVENT
		sql = String.format("DELETE FROM BSM_SCHEDULER_EVENT WHERE EVENT_TEXT='%s'", rptEventName);
		int ret = DBUtil.executeSQL(sql);
		
		return ret;
	}
	
	public static int rmScheduledEvent()
	{
		//Remove associated data from table BSM_SCHEDULER_EVENT_REPORT first
		String sql = "DELETE FROM BSM_SCHEDULER_EVENT_REPORT";
		DBUtil.executeSQL(sql);
		
		//Remove report event from table BSM_SCHEDULER_EVENT
		sql = String.format("DELETE FROM BSM_SCHEDULER_EVENT");
		int ret = DBUtil.executeSQL(sql);
		
		return ret;
	}
	
	public static String createRoleToDb(String prefix)
	{
		String id = getRandomStr();
		String field = prefix + id;
		String sql = String.format("INSERT INTO BSM_ROLE VALUES(%d, '%s', '%s')", Integer.parseInt(id), field, field);
		
		DBUtil.executeSQL(sql);
		
		return field;
	}
	
	public static String createRoleWithPerm(String prefix, String[] permissionArr) throws Exception
	{
		String roleName = createRoleToDb(prefix);
		//String roleName = prefix + getRandomStr();
		int roleID = getRoleIDFromDb(roleName);
		
		List<String> permIdList = getPermissionIds(permissionArr);
		for(String permId: permIdList)
		{
			String sql = String.format("INSERT INTO BSM_ROLE_PERMISSION VALUES(%d, %d, '%s')", Integer.parseInt(getRandomStr()), roleID, permId);
			DBUtil.executeSQL(sql);
		}
		
		return roleName;
	}
	
	private static List<String> getPermissionIds(String[] permissionNameArr) throws Exception
	{
		List<String> permissionIdList = new ArrayList<String>();
		
		if(permissionNameArr.length==0)
			return permissionIdList;
		
		StringBuilder sqlBuilder = new StringBuilder("SELECT PERMISSION_ID FROM BSM_PERMISSION ");
		sqlBuilder.append(String.format("WHERE PERMISSION_NAME='%s' ", permissionNameArr[0]));
		
		for(int i=1;i<permissionNameArr.length;i++)
			sqlBuilder.append(String.format("OR PERMISSION_NAME='%s' ", permissionNameArr[i]));
		
		String sql = sqlBuilder.toString();
		
		logger.info(sql);
		ResultSet rs = DBUtil.executeQuery(sql);
		try
		{
			while(rs.next())
			{
				permissionIdList.add(rs.getString("PERMISSION_ID"));
			}
		}catch(SQLException ex)
		{
			throw ex;
		}finally
		{
			DBUtil.closeDBRes();
		}
		return permissionIdList;
	}
	
	/*public static String createTransformToDb(String fileName)
	{
		String id = getRandomStr();
		String sql = String.format("INSERT INTO BSM_ETL (ID, ETL_NAME) VALUES(%d, '%s')", Integer.parseInt(id), fileName);
		DBUtil.executeSQL(sql);
		return fileName;
	}*/
	
	public static String createRoleWithPerm(String prefix,String perm)
	{
		
		String roleName = createRoleToDb(prefix);
		//String roleName = prefix + getRandomStr();
		int roleID = getRoleIDFromDb(roleName);
		String sql = String.format("SELECT PERMISSION_ID FROM BSM_PERMISSION WHERE PERMISSION_NAME='%s'", perm);
		ResultSet rs = DBUtil.executeQuery(sql);
		
		String permID = "";
		try
		{
			if(rs.next())
			{
				permID = rs.getString("PERMISSION_ID");
				DBUtil.closeDBRes();
			}
		}catch(SQLException ex)
		{}
		
		String sqlinsert = String.format("INSERT INTO BSM_ROLE_PERMISSION VALUES(%d, %d, '%s')", Integer.parseInt(getRandomStr()), roleID, permID);
		DBUtil.executeSQL(sqlinsert);
		return roleName;
	}
	
	public static int removeRolePerm(String roleName)
	{
		int roleID = getRoleIDFromDb(roleName);
		String sql = String.format("DELETE FROM BSM_ROLE_PERMISSION WHERE ROLE_ID=%d",roleID);
		int ret = DBUtil.executeSQL(sql);
		return ret;
	}
	
	public static int rmGeneratedRpt(String userName)
	{
		logger.info(String.format("Remove generated reports correlates with user %s", userName));
		String sql = String.format("DELETE FROM GENERATED_REPORTS WHERE BELONG_USER='%s'", userName);
		int ret = DBUtil.executeSQL(sql);
		return ret;
	}
	
	public static int createBatchRoleDataToDb(String prefix,int count)
	{
		int ret = 0;
		for (int i=0;i <count;i++)
		{
			String roleName = createRoleToDb(prefix);
			if(!roleName.equals(""))
				ret++;
		}
		return ret;
	}
	
	public static void createBritRptDataToDb(String userName, boolean isGeneric)
	{
		logger.info("Retreive existing reports in birtreports folder and create birt rpt data to database");
		String cmd = "ls " + CommonUtil.rptPath + "/*.rptdesign";
		logger.info("Run command: " + cmd);
		String[] stdoutput = executeSSHCommand(cmd).split("\n");
		String[] rptItems = new String[stdoutput.length];
		
		for(int i=0;i<stdoutput.length;i++)
		{
			int startIndex = stdoutput[i].lastIndexOf('/') + 1;
			rptItems[i] = stdoutput[i].substring(startIndex, stdoutput[i].length());
		}
		
		//Update the name of existing report files one by one, to append user's info: eg: Activation_count_admin.rptdesign
		for(String rptItem : rptItems)
		{
			String rptDiskName = getRptDiskName(rptItem, userName);
			cmd = String.format("cp %s/%s %s/%s", rptPath, rptItem, rptPath, rptDiskName);
			executeSSHCommand(cmd);
		}
		
		insertBirtRptDataToDb(rptItems, userName, isGeneric);
	}
	
	public static int rmBirtRptDataFromDb(String userName)
	{
		logger.info("Remove birt reports data from db, also delete related disk files");
		String cmd = String.format("rm -f %s/*%s.rptdesign", rptPath, userName);
		logger.info("Remove disk files first");
		logger.info(String.format("Run command: %s", cmd));
		executeSSHCommand(cmd);
		
		String sql = String.format("DELETE FROM BSM_REPORT WHERE REPORT_DISK_NAME LIKE '%%%s.rptdesign'", userName);
		
		int ret = DBUtil.executeSQL(sql);
		return ret;
	}
	
	public static int removeAllRoleDataFromDb(String prefix)
	{
		String sql = String.format("DELETE FROM BSM_ROLE WHERE ROLE_NAME LIKE '%s%%'", prefix);
		int ret = DBUtil.executeSQL(sql);
		return ret;
	}
	
	public static int getUserIDFromDb(String userName)
	{
		String sql = String.format("SELECT USER_ID FROM BSM_USER WHERE USER_NAME='%s'", userName);
		
		ResultSet rs = DBUtil.executeQuery(sql);
		
		int userID = -9999;
		try{
			if(rs.next())
				userID = rs.getInt(1);
		}catch(SQLException ex)
		{}
		
		return userID;
	}
	
	public static void rmBatchTransformsFromDb(String[] fileList)
	{
		try
		{
			for(String file:fileList)
			{
				String sql = String.format("DELETE FROM BSM_ETL WHERE ETL_NAME = '%s'", file);
				DBUtil.executeSQL(sql);
			}
		}catch(Exception e)
		{}
	}
	
	public static int getRoleCountFromDB()
	{
		String sql = "SELECT COUNT(*) FROM BSM_ROLE";
		ResultSet rs = DBUtil.executeQuery(sql);
		int count = 0;
		try
		{
			if(rs.next())
			{
				count = rs.getInt(1);
			    DBUtil.closeDBRes();
			}
	
		}catch(SQLException ex)
		{}
		return count;
	}

	
	//Remove all test users and associated data from BSM_USER
	public static int removeAllUserDataFromDb(String prefix)
	{
		//Get USER_ID(s) from table BSM_USER
		String sql = String.format("SELECT USER_ID FROM BSM_USER WHERE USER_NAME LIKE '%s%%'", prefix);
		ResultSet rs = DBUtil.executeQuery(sql);
		int userID = 0;
		List<Integer> userList = new ArrayList<Integer>();
		try
		{
			while(rs.next())
			{
				//Remove associated data from BSM_USER_ROLE
				userID = rs.getInt("USER_ID");
				userList.add(userID);
			}
			DBUtil.closeDBRes();
		}catch(SQLException ex)
		{}
		
		//Remove all associated data from db 
		for(Integer id:userList)
		{
			removeUserRoleDataFromDb(id);
		}
		
		//Remove user data from table BSM_USER
		sql = String.format("DELETE FROM BSM_USER WHERE USER_NAME LIKE '%s%%'", prefix);
		int ret = DBUtil.executeSQL(sql);
		return ret;
	}
	
	//Create a new test user to BSM_USER with the default password 123
	public static String createUserDataToDb(String prefix,boolean isActive)
	{
		String id = getRandomStr();
		String username = prefix+id;
		int iActive = isActive?1:0;
		
		//Add user data to BSM_USER
		String sql = String.format("INSERT INTO BSM_USER VALUES(%d, '%s', '202cb962ac59075b964b07152d234b70', %d)",Integer.parseInt(id), username, iActive);
		DBUtil.executeSQL(sql);
		
		//Associate anoymous role with new created user
		sql = String.format("INSERT INTO BSM_USER_ROLE VALUES(%d, %d, %d)", Integer.parseInt(getRandomStr()), Integer.parseInt(id), 0);
		DBUtil.executeSQL(sql);
		return username;
	}
	
	public static int createBatchLog(int count)
	{
		CommonUtil.removeAllActivityLog();
		int ret=0;
		for(int i=1;i<=count;i++)
		{
			String sql = String.format("INSERT INTO ACTIVITY_LOG(ID, OPERATION_ACTIVITY, ACTIVITY_DESCRIPTION, OPERATION_TIME, OPERATOR, STATUS) VALUES(%d, 'alli', 'alli', '1900-01-01 00:00:00.0', 'alli', 1)", i);
			logger.info(sql);
			if(DBUtil.executeSQL(sql)==1)
				ret++;
		}
		return ret;
	}
	
	public static int createBatchUserDataToDb(String prefix, int count)
	{
		int ret=0;
		for(int i=0;i<count;i++)
		{
			String username = createUserDataToDb(prefix,true);
			if(!username.equals(""))
				ret++;
		}
		return ret;
	}
	
	public static int getUsersCountFromDb()
	{
		String sql = "SELECT COUNT(*) FROM BSM_USER";
		ResultSet rs = DBUtil.executeQuery(sql);
		int count = 0;
		try
		{
			if(rs.next())
			{
				count = rs.getInt(1);
				DBUtil.closeDBRes();
			}
		}catch(SQLException ex)
		{}
		return count;
	}
	
	public static int getRoleIDFromDb(String roleName)
	{
		//roleName = roleName.toUpperCase();
		String sql = String.format("SELECT ROLE_ID FROM BSM_ROLE WHERE ROLE_NAME='%s'", roleName);
		ResultSet rs = DBUtil.executeQuery(sql);
		int roleID = -9999;
		try
		{
			if(rs.next())
			{
				//Remove associated data from BSM_USER_ROLE
				roleID = rs.getInt("ROLE_ID");
				DBUtil.closeDBRes();
			}
		}catch(SQLException ex)
		{
		}
		return roleID;
	}
	
	public static String getScheduledEventIDFromDB(String eventName)
	{
		String sql = String.format("SELECT EVENT_ID FROM BSM_SCHEDULER_EVENT WHERE EVENT_TEXT='%s'", eventName);
		ResultSet  rs = DBUtil.executeQuery(sql);
		String strEventID="";
		try{
			if(rs.next())
			{
				strEventID = rs.getString("EVENT_ID");
				DBUtil.closeDBRes();
			}
		}catch(SQLException ex)
		{	
		}
		
		return strEventID;
	}
	
	public static int associateRoleDataWithUserData(int userID, int roleID)
	{
		String sql = String.format("INSERT INTO BSM_USER_ROLE VALUES(%d, %d, %d)", Integer.parseInt(getRandomStr()), userID, roleID);
		return DBUtil.executeSQL(sql);
	}
	
	public static int associateRoleDataWithUserData(String userName, String roleName)
	{
		int userId = CommonUtil.getUserIDFromDb(userName);
		int roleId = CommonUtil.getRoleIDFromDb(roleName);
		
		return associateRoleDataWithUserData(userId, roleId);
	}
	
	public static boolean isRptEventExistedInDb(String rptEventName)
	{
		if(getRptEventFromDb(rptEventName).size()>0)
			return true;
		else
			return false;
	}
	
	public static Hashtable<String, String> getRptEventFromDb(String rptEventName)
	{
		Hashtable<String, String> data = new Hashtable<String, String>();
		String sql = String.format("SELECT * FROM BSM_SCHEDULER_EVENT WHERE EVENT_TEXT='%s'", rptEventName);
		ResultSet rs = DBUtil.executeQuery(sql);
		try{
			if(rs.next())
			{
				data.put("ID", String.valueOf(rs.getInt("ID")));
				data.put("EVENT_ID", rs.getString("EVENT_ID"));
				data.put("EVENT_TEXT", rs.getString("EVENT_TEXT"));
				data.put("START_DATE", String.valueOf(rs.getDate("START_DATE")));
				data.put("END_DATE", String.valueOf(rs.getDate("END_DATE")));
				data.put("EVENT_PID", rs.getString("EVENT_PID"));
				data.put("EVENT_LENGTH", rs.getString("EVENT_LENGTH"));
				data.put("REC_PATTERN", rs.getString("REC_PATTERN"));
				data.put("REC_TYPE", rs.getString("REC_TYPE"));
				data.put("BYEMAIL", String.valueOf(rs.getInt("BYEMAIL")));
				data.put("BYFTP", String.valueOf(rs.getInt("BYFTP")));
				data.put("BYHTTP", String.valueOf(rs.getInt("BYHTTP")));
				data.put("DESEMAIL", rs.getString("DESEMAIL"));
				data.put("FTPURL", rs.getString("FTPURL"));
				data.put("FTPUSERNAME", rs.getString("FTPUSERNAME"));
				data.put("FTPPASSWORD", rs.getString("FTPPASSWORD"));
				data.put("FTPSAVEPATH", rs.getString("FTPSAVEPATH"));
				data.put("HTTPUSERNAME", rs.getString("EVENT_LENGTH"));
				data.put("HTTPPASSWORD", rs.getString("HTTPPASSWORD"));
			}
		}catch(Exception e){
			
		}
		finally{
			DBUtil.closeDBRes();
		}
		return data;
	}
	
	//Remove associated data from table BSM_USER_ROLE
	public static int removeUserRoleDataFromDb(int userID)
	{
		String sql = String.format("DELETE FROM BSM_USER_ROLE WHERE USER_ID=%d", userID);
		return DBUtil.executeSQL(sql);
	}
	
	public static String getRandomStr()
	{
		int min=1000;
		int max=10000;
		int num = (int)((max-min)*Math.random()+min);
		
		return String.valueOf(num);
	}
	
	public static int getRandomInt(){
		return (int)((Math.random()+0.0001)*10000);
	}
	
	
	public static void closeBrowser()
	{
		if(driver!=null)
		{			
			driver.quit();
			logger.info("Quit browser.");
			driver=null;
			logger.info("set driver to null");
		}else{
			logger.info("driver is null");
		}
	}
	
	private static void signInCorpMailApp()
	{
		/*String username = String.format("hq\\%s",CommonUtil.getPropertyValue("domain_username"));
		String password = CommonUtil.getPropertyValue("domain_password");*/
		
		if(driver==null)
			launchBrowser();
		
		driver.get("https://mail.nagra.com/");
		
		CommonUtil.sleep(30);
		
		/*WebElement txtUserName = driver.findElement(By.id("username"));
		WebElement txtPassword = driver.findElement(By.id("password"));
		
		driver.getTitle()
		
		//Input user name
		txtUserName.clear();
		txtUserName.sendKeys(username);
		
		//Input password
		txtPassword.clear();
		txtPassword.sendKeys(password);
		
		//Click login
		driver.findElement(By.id("SubmitCreds")).click();*/
	}
	
	public static boolean checkReceivedBsmTestMail()
	{
		signInCorpMailApp();	
		return driver.findElement(By.id("divConvTopic")).getText().trim().equals("Test email") && driver.findElement(By.id("spnFrom")).getText().trim().equals("BSM@nagra.com");
	}
	

	
	public static boolean chkReceivedScheduledRpt(String expectedTitle, String[] expectedAtmts)
	{

		signInCorpMailApp();

		driver.findElement(By.xpath("//div[@id='divVLVIL']/div[1]")).click();
		
		boolean isCorrectTitle = driver.findElement(By.id("divConvTopic")).getText().trim().equals(expectedTitle);
		logger.info(String.format("Verify the E-Mail title should equal to %s: %b", expectedTitle, isCorrectTitle));
		
		List<WebElement> lnkAtmtList = driver.findElements(By.id("lnkAtmt"));
		boolean isCorrectAtmtCount = lnkAtmtList.size() == expectedAtmts.length;
		logger.info(String.format("Verify the total Attachments should equal to %d: %b", expectedAtmts.length, isCorrectAtmtCount));
		
		boolean isRptListedInAtmts = true;
		for(WebElement lnkAtmt:lnkAtmtList)
		{
			String strAtmtName = lnkAtmt.getAttribute("title");
			for(String expectedItem:expectedAtmts)
			{
				if(strAtmtName.equals(expectedItem))
				{
					isRptListedInAtmts = true;
					break;
				}else
					isRptListedInAtmts = false;				
			}
			logger.info(String.format("Verify the rpt %s is listed in E-mail Attachment: %b", strAtmtName, isRptListedInAtmts));
		}
		
		closeBrowser();
		
		return isCorrectTitle && isCorrectAtmtCount && isRptListedInAtmts;
	}
	
	private static String getPageContentFromSrv(String webAddress, String username, String password)
	{
		StringBuffer sb = new StringBuffer();
		try
		{
			URL url = new URL(webAddress);
			URLConnection uc = url.openConnection();
			
			String authInfo = String.format("%s:%s", username, password);
			
			String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(authInfo.getBytes());
			uc.setRequestProperty ("Authorization", basicAuth);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));

			String line = null;
			while((line=br.readLine())!=null)
				sb.append(line + "\n");
		}
		catch(MalformedURLException ex)
		{
			logger.error(String.format("Can't open the url %s with following error: %s", webAddress, ex.getMessage()));
			return "";
		}
		catch(IOException e)
		{
			logger.error(String.format("Can't read web content: %s", e.getMessage()));
			return "";
		}
		return sb.toString();
	}
	
	public static boolean chkScheduledRptPresentOnFtpSrv(String expectedPrefix, String expectedPostfix)
	{
		String ftp_url = "ftp://" + CommonUtil.getPropertyValue("ftp_url") + CommonUtil.getPropertyValue("ftp_savepath");
		String ftp_username = CommonUtil.getPropertyValue("ftp_username");
		String ftp_password = CommonUtil.getPropertyValue("ftp_password");
		
		logger.info(String.format("Send request to %s", ftp_url));
		String pageSource = CommonUtil.getPageContentFromSrv(ftp_url,ftp_username,ftp_password);
		
		Pattern pattern = Pattern.compile(expectedPrefix + "[0-9][0-9]_" + expectedPostfix);
		Matcher matcher = pattern.matcher(pageSource);
		
		if(matcher.find())
			return true;
		else
			return false;
	}
	
	public static boolean chkScheduledRptPresentOnHttpSrv(String expectedPrefix, String expectedPostfix)
	{
		String http_url = CommonUtil.getPropertyValue("http_url");
		String http_username = CommonUtil.getPropertyValue("http_username");
		String http_password = CommonUtil.getPropertyValue("http_password");
		
		logger.info(String.format("Send request to %s", http_url));
		String pageSource = CommonUtil.getPageContentFromSrv(http_url, http_username, http_password);
		
		Pattern pattern = Pattern.compile(expectedPrefix + "[0-9][0-9]_" + expectedPostfix);
		Matcher matcher = pattern.matcher(pageSource);
		
		if(matcher.find())
			return true;
		return false;
	}

	public static void initFtpConn(String host, int port, String username, String password) throws IOException
	{
		try
		{
			logger.info(String.format("Establish connection to FTP server %s, %s, %s", host,username,password));
			ftpClient = new FTPClient();
			
			ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
			ftpClient.connect(host, port);
	        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
	        	ftpClient.login(username, password);
		}catch(IOException e)
		{
			throw e;
		}
	}
	
	public static void closeFtpConn() throws Exception
	{
		try
		{
			logger.info("Close connection with FTP server.");
			if((ftpClient!=null)&&(ftpClient.isConnected()))
			{
				ftpClient.logout();
				ftpClient.disconnect();
			}
		}catch(Exception e)
		{
			throw e;
		}
	}
	
	public static boolean isFileExistedOnServer(String path, String fileName) throws Exception
	{
		try
		{
			FTPFile[] files = ftpClient.listFiles(path);
			for(FTPFile f:files)
			{	if(fileName.equals(f.getName()))
					return true;
			}
			return false;
		}catch(Exception e)
		{
			throw e;
		}
	}
	
	public static int getFileCountOnServer(String path) throws Exception
	{
		try{
			int count=0;
			FTPFile[] files = ftpClient.listFiles(path);
			for(FTPFile f:files)
			{	
				if((f.getName().toLowerCase().endsWith("rptdesign")))
					count++;
			}
			return count;
		}catch(Exception e)
		{
			throw e;
		}
	}
	
    public static boolean uploadFileToServer(String localFile, String remotePath) throws Exception
    {   	
    	File file = new File(localFile);
    	if (file.exists()) 
    	{
    		String fileName = file.getName();
    		if (isFileExistedOnServer(remotePath, fileName))
    			deleteFileFromServer(remotePath+"/"+fileName);		
    		InputStream in=null;
	    	try 
	    	{
	    		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	    		ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE); 
				ftpClient.changeWorkingDirectory(remotePath);
				in= new FileInputStream(file);
				boolean ret = ftpClient.storeFile(remotePath+"/"+fileName, in);
				return ret;
			}catch(Exception e) 
			{
				throw e;
			}finally
			{
				if(in!=null)
					in.close();
			} 		
    	}
    	return false;
    }
	
	public static boolean deleteFileFromServer(String fileName) throws Exception
	{
		try
		{
			boolean ret = ftpClient.deleteFile(fileName);
			logger.info(ret);
			
			return ret;
		}catch(Exception e)
		{
			throw e;
		}
	}
	
	public static void deleteFileFromServer(String path, String prefix) throws Exception
	{
		try
		{
			prefix = prefix.toLowerCase();
			FTPFile[] files = ftpClient.listFiles(path);
			System.out.print(files.length);
			for(FTPFile f:files)
			{
				System.out.print(f.getName());
				if((f.getName().toLowerCase().startsWith(prefix)))
				{	
					System.out.print("Try to delete:"+path+"/"+prefix);
					ftpClient.deleteFile(path+"/"+f.getName());
					System.out.println(ftpClient.getReplyString());
				}
			}
		}catch(Exception e)
		{
			throw e;
		}
	}
	
	public static String getFileTimestamp(String path, String fileName) throws Exception
	{
		try
		{
			FTPFile[] files = ftpClient.listFiles(path);
			for(FTPFile f:files)
			{	if(fileName.equals(f.getName()))
			    {
					Calendar c = f.getTimestamp();
					return String.format("%d:%d:%d.%d", c.get(Calendar.HOUR),
										 c.get(Calendar.MINUTE),
										 c.get(Calendar.SECOND),
										 c.get(Calendar.MILLISECOND));
			    }
			}
			return null;
		}catch(Exception e)
		{
			throw e;
		}
	}
	
	private static void initBsmFtpConn()
	{
		try
		{
			CommonUtil.initFtpConn(CommonUtil.getPropertyValue("host_name"), 
							   	   21, 
							   	   CommonUtil.getPropertyValue("host_username"),
							   	   CommonUtil.getPropertyValue("host_password")
							   	   );
		}catch(IOException e)
		{
			logger.info(String.format("The BSM connection was not created successfully. %s", e.getMessage()));
		}
	}
	
	
	private static void closeBsmFtpConn()
	{
		try
		{
			CommonUtil.closeFtpConn();
		}catch(Exception e)
		{
			logger.info(String.format("The BSM connection was not closed successfully. %s", e.getMessage()));
		}
	}
	
	public static void createDumbRptOutputFiles(String[] fileNameArr)
	{
		for(String file : fileNameArr)
		{
			String cmd = "touch " + rptOutputPath + "/" + file;
			logger.info("Run command:" + cmd);
			String stdOutput = CommonUtil.executeSSHCommand(cmd);
			logger.info(stdOutput);
		}
	}
	
	public static void rmDumpRptOutputFiles(String[] fileNameArr)
	{
		for(String file : fileNameArr)
		{
			String cmd = "rm -f " + rptOutputPath + "/" + file;
			logger.info("Run command:" + cmd);
			String stdOutput = CommonUtil.executeSSHCommand(cmd);
		}
	}
	
	public static void uploadRptDesignFile(String localFile) throws Exception
	{
		try
		{
			CommonUtil.initBsmFtpConn();
			CommonUtil.uploadFileToServer(localFile, rptPath);
		}catch(Exception e)
		{
			logger.info(String.format("Exception happened in uploadRptDesignFile. %s", e.getMessage()));
		}
		finally
		{
			CommonUtil.closeBsmFtpConn();
		}
	}
	
	/*public static void uploadTransformDesignFile(String localFile,String fileName) throws Exception
	{
		try
		{
			CommonUtil.createTransformToDb(fileName);
			CommonUtil.initBsmFtpConn();
			CommonUtil.uploadFileToServer(localFile, transformPath);
		}
		catch(Exception e)
		{
			logger.info(String.format("Exception happened in uploadTransformDesignFile. %s", e.getMessage()));
		}
		finally
		{
			CommonUtil.closeBsmFtpConn();
		}
	}*/
	
	public static void rmOutputRptFromSrv(String prefix)
	{
		try
		{
			CommonUtil.initBsmFtpConn();
			CommonUtil.deleteFileFromServer(rptOutputPath, prefix);
		}catch(Exception e)
		{
			logger.info(String.format("Exception happened in rmOutputRptFromSrv. %s", e.getMessage()));
		}
		finally{
			CommonUtil.closeBsmFtpConn();
		}
	}
	
	public static void rmRptDesignFileFromSrv(String rptFile)
	{
		try
		{
			CommonUtil.initBsmFtpConn();
			StringBuffer rptFileUser= new StringBuffer(rptFile);
			rptFileUser = rptFileUser.insert(rptFileUser.lastIndexOf("."),"_"+CommonUtil.getPropertyValue("username"));					
			String fullpath = rptPath + "/" + rptFileUser;
			CommonUtil.deleteFileFromServer(fullpath);
		}catch(Exception e)
		{
			logger.info(String.format("Exception happened in rmRptDesignFileFromSrv. %s", e.getMessage()));
		}
		finally
		{
			CommonUtil.closeBsmFtpConn();
		}
	}
	
	public static void rmRptDesignFileFromDB(){
		
	}
	//get file from folder
	/*public static String[] getDataFromFolder (String path) 
	{
		try
		{
			File file = new File(path);
			String[] fileNameList = new String[]{};
			if(file.isDirectory())
			{
				File[] f=file.listFiles();
				if(f!=null)
					for(int i=0;i<f.length;i++)
					{
						fileNameList [i] = f[i].getName();
					}
				
			}
			return fileNameList;
		}catch(Exception e)
		{
			logger.info(String.format("Exception happened in getDataFromFolder. %s", e.getMessage()));
			return null;
		}
		
	}*/
		
	
	public static void rmBatchTransformsDesignFromServer(String[] fileList)
	{
		try
		{
			CommonUtil.initBsmFtpConn();
			for(String file:fileList)
			{
				String fullpath = transformPath + "/" + file;
				CommonUtil.deleteFileFromServer(fullpath);
			}
			
		}catch(Exception e)
		{
			logger.info(String.format("Exception happen in rmTransformDesignFromServer. %s", e.getMessage()));
		}
		finally
		{
			CommonUtil.closeBsmFtpConn();
		}
	}
	public static void rmTransformDesignFromServer(String username, String ...transformFile) 
	{
		try
		{
			CommonUtil.initBsmFtpConn();
			for(String transform:transformFile){
				String fullpath = transformPath + "/" + getTransformDiskName(transform,username);
				CommonUtil.deleteFileFromServer(fullpath);
			}			
		}catch(Exception e)
		{
			logger.info(String.format("Exception happen in rmTransformDesignFromServer. %s", e.getMessage()));
			
		}
		finally
		{
			CommonUtil.closeBsmFtpConn();
		}
	}
	
	public static void rmTransformFromBackend(String username,String ...transformFile){		
		rmTransformDesignFromServer(username,transformFile);
		removeTransformFromDb(username,transformFile);
	}
	
	public static void clearGenerateRptFromSrv()
	{
		String cmd = "rm  " + generatedRptPath + "/*.*";
		CommonUtil.executeSSHCommand(cmd);
		
		/*
		 *DISABLE FOLLOWING CODE AS THE FTP CLIENT PERFORMS UNSTABLE
		try
		{
			CommonUtil.initBsmFtpConn();
			
			ftpClient.changeWorkingDirectory(generatedRptPath);			
			
			FTPFile[] files = ftpClient.listFiles(generatedRptPath);

			for(FTPFile f : files)
			{
				if(f.isFile())
				{
					String filePath = generatedRptPath + "/" + f.getName();
					logger.info(String.format("Remove file %s", filePath));
					CommonUtil.deleteFileFromServer(filePath);
				}
			}
		}catch(Exception e)
		{
			logger.info(String.format("Exception happened in clearGenerateRptFromSrv. %s", e.getMessage()));
		}
		finally
		{
			CommonUtil.closeBsmFtpConn();
		}*/
	}
	
	public static boolean isRptDesignFilePresentOnSrv(String rptFile)
	{
		try
		{
			CommonUtil.initBsmFtpConn();
			return CommonUtil.isFileExistedOnServer(rptPath, rptFile);
		}catch(Exception e)
		{
			logger.info(String.format("Exception happened in isRptDesignFilePresentOnSrv. %s", e.getMessage()));
			return false;
		}
		finally
		{
			CommonUtil.closeBsmFtpConn();
		}
	}
	
	public static boolean isRptGeneratedOnSrv(String expectedPrefix, String expectedPostfix)
	{
	    String cmd = "ls " + generatedRptPath;
		
		String stdOutput = CommonUtil.executeSSHCommand(cmd);

		if(stdOutput.equals("ERROR")||stdOutput.equals(""))
			return false;
		
		String[] fileLst = stdOutput.split("\n");
		
		for(String f : fileLst)
			if((f.startsWith(expectedPrefix))&&(f.endsWith(expectedPostfix)))
				return true;
		
		return false;
		/*
		 * DISABLE FOLLOWING CODE DUE TO UNSTABLE ISSUE OF FTP CLIENT, REPLACE IT WITH SSH COMMAND
		 * 
		try
		{
			CommonUtil.initBsmFtpConn();			
			
			ftpClient.changeWorkingDirectory(generatedRptPath);
			FTPFile[] files = ftpClient.listFiles(generatedRptPath);
			
			logger.info("safassadsdfasdfasdfsadfasfasdf: " + files.length);
			
			for(FTPFile f : files)
			{
				if(f.isFile())
				{
					String fileName = f.getName();
					if((fileName.startsWith(expectedPrefix))&&(fileName.endsWith(expectedPostfix)))
						return true;
				}
			}
			
		}catch(Exception e)
		{
			logger.info(String.format("Exception happened in clearGenerateRptFromSrv. %s", e.getMessage()));
		}
		finally
		{
			CommonUtil.closeBsmFtpConn();
		}
		return false;
		*/
	}
	
	public static int getRptDesignFileCount()
	{
	    String cmd = "ls " + rptPath;
		String stdOutput = CommonUtil.executeSSHCommand(cmd);
		
		if(stdOutput.equals("ERROR")||stdOutput.equals(""))
			return 0;
		
		String[] fileLst = stdOutput.split("\n");
		
		int count=0;
		for(String file:fileLst)
			if(file.endsWith(".rptdesign"))
				count++;
		
		return count;
	}
	
	public static int getRptDesignFileCountFromDb() throws Exception
	{
		String sql = "SELECT COUNT(*) FROM BSM_REPORT";
		ResultSet rs = DBUtil.executeQuery(sql);
		int count = 0;
		try
		{
			if(rs.next())
				count = rs.getInt(1);
		}catch(SQLException ex)
		{
			throw ex;
		}finally{
			DBUtil.closeDBRes();
		}
		return count;
	}
	
	public static int GetRptIDFromDb(String rptName) throws Exception
	{
		String sql = String.format("SELECT ID FROM BSM_REPORT WHERE REPORT_NAME='%s'", rptName);
		ResultSet rs = DBUtil.executeQuery(sql);
		int id = 0;
		try
		{
			if(rs.next())
				id = rs.getInt(1);
		}catch(SQLException ex)
		{
			throw ex;
		}finally{
			DBUtil.closeDBRes();
		}
		return id;
	}
	
	public static List<String> getRptDesignFileCount(String prefix)
	{
		List<String> result = new ArrayList<String>();
		String cmd = "ls " + rptPath + "/" + prefix + "*";
		String stdOutput = CommonUtil.executeSSHCommand(cmd);
		
		if(stdOutput.equals("ERROR")||stdOutput.equals(""))
			return result;
		
		String[] fileLst = stdOutput.split("\n");
		
		for(String file:fileLst)
			result.add(file);
		
		return result;
	}
	
	public static int getDesignFileCount(String path)
	{
		try
		{
			CommonUtil.initBsmFtpConn();
			return CommonUtil.getFileCountOnServer(path);
		}catch (Exception e)
		{
			logger.info(String.format("Exception happened in getDesignFileCount. %s", e.getMessage()));
			return 0;
		}
		finally
		{
			CommonUtil.closeBsmFtpConn();
		}
	}
	public static String getTimestamp(String fileName)
	{
		String cmd = "ls -al " + rptPath + "/" + fileName;
		String stdOutput = CommonUtil.executeSSHCommand(cmd);
		
		if(stdOutput.equals("ERROR")||stdOutput.equals(""))
			return "Shit happens here.";
		
		String[] items = stdOutput.split(" ");
		
		return items[items.length-2];
	}
	public static String getTimestamp(String path, String fileName)
	{
		try
		{
			CommonUtil.initBsmFtpConn();
			return CommonUtil.getFileTimestamp(path, fileName);
		}catch(Exception e)
		{
			logger.info(String.format("Exception happened in getTimestamp of transform", e.getMessage()));
			return "";
		}
		finally
		{
			CommonUtil.closeBsmFtpConn();
		}
	}
	public static Calendar getRemoteTime()
	{
		SSHExec ssh = null;
		Calendar calendar = null;
		try{
			
			String host_name = CommonUtil.getPropertyValue("host_name");
			String username = CommonUtil.getPropertyValue("ftp_username");
			String password = CommonUtil.getPropertyValue("ftp_password");
			
			logger.info(String.format("Create SSH Connection to %s with %s, %s", host_name, username, password));
			ConnBean cb = new ConnBean(CommonUtil.getPropertyValue("host_name"),
									   CommonUtil.getPropertyValue("ftp_username"),
									   CommonUtil.getPropertyValue("ftp_password"));
			ssh = SSHExec.getInstance(cb);
			
			String cmd = "date " + "+\"%Y-%m-%d %H:%M:%S\"";
			
			CustomTask echo = new ExecCommand(cmd);
		
			ssh.connect();
			Result res = ssh.exec(echo);
			
			if(res.isSuccess)
			{
				String strDate = res.sysout.substring(0, res.sysout.length()-1);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = dateFormat.parse(strDate);
				calendar = Calendar.getInstance();
				calendar.setTime(date);				
			}else
			{
				throw new Exception(res.error_msg);
			}
			return calendar;
		}
		catch(Exception e)
		{
			logger.error("Execption happened: " + e.getMessage());
			return null;
		}finally
		{
			logger.info("Close SSH Connection.");
			ssh.disconnect();
		}
	}
	
	public static boolean setRemoteTime(Calendar calendar)
	{
		SSHExec ssh = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try
		{
			String host_name = CommonUtil.getPropertyValue("host_name");
			String username = CommonUtil.getPropertyValue("host_username");
			String password = CommonUtil.getPropertyValue("host_password");
			
			logger.info(String.format("Create SSH Connection to %s with %s, %s", host_name, username, password));
			
			ConnBean cb = new ConnBean(host_name, username, password);
			Date date = calendar.getTime();
			ssh = SSHExec.getInstance(cb);

			String cmd = String.format("date -s \"%s\"", dateFormat.format(date));			
			logger.info("Run command : " + cmd);

			CustomTask echo = new ExecCommand(cmd);			
			ssh.connect();
			Result res = ssh.exec(echo);
			
			if(res.isSuccess)
			{
				return true;
			}else
			{
				return false;
			}
		}catch(Exception e)
		{
			logger.error("Execption happened: " + e.getMessage());
			return false;
		}finally
		{
			logger.info("Close SSH Connection");
			ssh.disconnect();
		}
	}
	
	public  static void syncServerTimeWithClient(){
		Calendar calendar = Calendar.getInstance();
		setRemoteTime(calendar);
		logger.info("Sync server time with Client");
	}
	
	
	public static void initSmtpSettings()
	{
		//Remove default settings first.
		String sql = "DELETE FROM SYSTEM_CONFIGURATION";
		logger.info(String.format("Remove smtp configuration from DB: %s", sql));
		
		DBUtil.executeSQL(sql);
		
		//Insert SMTP settings to DB.
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO SYSTEM_CONFIGURATION(ID, EMAILADRSS, SMTPSERVER, SMTPPORT, MAILUSER, PASSWORD, SENDINGREPEAT, USERID) ");
		sqlBuilder.append("VALUES(%d, '%s', '%s', '%s', '%s', '%s', %d, %d)");

		sql = String.format(sqlBuilder.toString(), 1, CommonUtil.getPropertyValue("smpt_sender"),
							CommonUtil.getPropertyValue("smpt_server"),
							CommonUtil.getPropertyValue("smpt_port"),
							"", "", 1, 1);
		
		logger.info(String.format("Insert smtp conf to DB: %s", sql));
		
		DBUtil.executeSQL(sql);
	}
	
	public static void initTransportSettings()
	{
		//Remove default settings first.
		String sql = "DELETE FROM USER_TRANSPORT";
		logger.info(String.format("Remove Transport configuraiton from DB: %s",sql));
		
		DBUtil.executeSQL(sql);
		
		//Insert FTP settings to DB.
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO USER_TRANSPORT(ID, FTPURL, FTPUSERNAME, FTPPASSWORD, FTPSAVEPATH, HTTPURL, HTTPUSERNAME, HTTPPASSWORD, USERID) ");
		sqlBuilder.append("VALUES(%d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', %d)");
		
		sql = String.format(sqlBuilder.toString(), 1 , 
							CommonUtil.getPropertyValue("ftp_url"),
							CommonUtil.getPropertyValue("ftp_username"),
							CommonUtil.getPropertyValue("ftp_password"),
							CommonUtil.getPropertyValue("ftp_savepath"),
							CommonUtil.getPropertyValue("http_url"),
							CommonUtil.getPropertyValue("http_username"),
							CommonUtil.getPropertyValue("http_password"),
							1
							);
		
		logger.info(String.format("Insert Transport conf to DB: %s", sql));
		
		DBUtil.executeSQL(sql);
	}
	
	public static Calendar getCorrectStartDate()
	{
		Calendar c = Calendar.getInstance();
		
//		setRemoteTime(c);
		
		int minute = c.get(Calendar.MINUTE) + 1;

		do
		{
			minute++;
		}while(!(minute%5==0));
		
		c.set(Calendar.MINUTE, minute);

		return c;
	}
	
	public static String getRptTimeStamp(Calendar calendar)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
		Date date = calendar.getTime();
		return dateFormat.format(date);
	}
	
	public static String createRptEventFromDb() throws Exception
	{
		String eventName = "EVENT_" + CommonUtil.getRandomStr();
		
		//Remove default event Settings first
		for(String tableName:new String[]{"BSM_SCHEDULER_EVENT_REPORT", "BSM_SCHEDULER_EVENT"})
			DBUtil.executeSQL(String.format("DELETE FROM %s", tableName));
		
		Calendar calendar = CommonUtil.getCorrectStartDate();
		int minute = calendar.get(Calendar.MINUTE)+1;
		while((minute%15)!=0)
			minute++;
		
		calendar.set(Calendar.MINUTE, minute);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00.0");
		String startDate = dateFormat.format(calendar.getTime());
		
		minute=minute+10;
		calendar.set(Calendar.MINUTE, minute);
		String endDate = dateFormat.format(calendar.getTime());
		
		//INSERT BSM EVENT INTO BSM_SCHEDULER_EVENT
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO BSM_SCHEDULER_EVENT(ID, EVENT_ID, EVENT_TEXT, START_DATE, END_DATE, EVENT_PID, EVENT_LENGTH, REC_PATTERN, REC_TYPE,BYEMAIL, BYFTP, BYHTTP) ");
		sqlBuilder.append("VALUES (1, '9999999999999', '%s', '%s', '%s','','','','',0,0,0)");
		String sql = String.format(sqlBuilder.toString(), eventName, startDate, endDate);
		DBUtil.executeSQL(sql);
		
		//RETRIEVE A RPT ID FOR BSM_SCHEDULER_EVENT_REPORT TABLE
		int rptId = CommonUtil.GetRptIDFromDb("Activation_count.rptdesign");
		
		//INSERT ASSOCIATION DATA TO BSM_SCHEDULER_EVENT_REPORT
		sql = String.format("INSERT INTO BSM_SCHEDULER_EVENT_REPORT VALUES(1, 1, %d, 'xls')", rptId);
		DBUtil.executeSQL(sql);

		return eventName;
	}
	
	public static String createRptEventFromDb(String[] rptNameList)
	{
		String eventName = "EVENT_" + CommonUtil.getRandomStr();
		
		//Remove default event Settings first
		for(String tableName:new String[]{"BSM_SCHEDULER_EVENT_REPORT", "BSM_SCHEDULER_EVENT"})
			DBUtil.executeSQL(String.format("DELETE FROM %s", tableName));
		
		Calendar calendar = CommonUtil.getCorrectStartDate();
		int minute = calendar.get(Calendar.MINUTE)+1;
		while((minute%15)!=0)
			minute++;
		
		calendar.set(Calendar.MINUTE, minute);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00.0");
		String startDate = dateFormat.format(calendar.getTime());
		
		minute=minute+10;
		calendar.set(Calendar.MINUTE, minute);
		String endDate = dateFormat.format(calendar.getTime());
		
		//INSERT BSM EVENT INTO BSM_SCHEDULER_EVENT
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO BSM_SCHEDULER_EVENT(ID, EVENT_ID, EVENT_TEXT, START_DATE, END_DATE, EVENT_PID, EVENT_LENGTH, REC_PATTERN, REC_TYPE,BYEMAIL, BYFTP, BYHTTP) ");
		sqlBuilder.append("VALUES (1, '9999999999999', '%s', '%s', '%s','','','','',0,0,0)");
		String sql = String.format(sqlBuilder.toString(), eventName, startDate, endDate);
		DBUtil.executeSQL(sql);
		
		for(String rptName:rptNameList)
		{
			int rptID = retrieveRptId(rptName);
			int id = Integer.parseInt(CommonUtil.getRandomStr());
			sql = String.format("INSERT INTO BSM_SCHEDULER_EVENT_REPORT VALUES(%d, 1, %d, 'xls')", id, rptID);
			DBUtil.executeSQL(sql);
		}
		
		return eventName;
	}
	
	private static int retrieveRptId(String rptName)
	{
		String sql = String.format("SELECT ID FROM BSM_REPORT WHERE REPORT_NAME='%s'", rptName);
		ResultSet  rs = DBUtil.executeQuery(sql);
		int rptID=-9999;
		try{
			if(rs.next())
			{
				rptID = rs.getInt("ID");
				DBUtil.closeDBRes();
			}
		}catch(SQLException ex)
		{	
		}
		
		return rptID;
		
	}
	
	public static void createDumbBirtRpts(String[] fileNameArr, String userName)
	{
		//1. Create dumb file to specified BSM folder
		createDumbFiles(fileNameArr, userName);
		//2. Insert dumb rpt data to table BSM_REPORT
		insertDumbFileDataToDb(fileNameArr, userName);
	}
	
	public static void createDumbBirtRpts(String[] fileNameArr, String userName, boolean isGeneric)
	{
		//1. Create dumb file to specified BSM folder
		createDumbFiles(fileNameArr, userName);
		//2. Insert dumb rpt data to table BSM_REPORT
		insertBirtRptDataToDb(fileNameArr, userName, isGeneric);
	}
	
	public static void insertDumbFileDataToDb(String[] fileNameArr, String userName)
	{
		int userID = getUserIDFromDb(userName);
		StringBuilder sqlBuilder = new StringBuilder("INSERT INTO BSM_REPORT(ID, REPORT_NAME, REPORT_DISK_NAME, CREATE_USER_ID, CREATE_TIME, IS_GENERIC) ");
		sqlBuilder.append("VALUES(%d, '%s', '%s', %d, '%s', %d)");
		for(String fileName : fileNameArr)
		{
			String fileDiskName = getRptDiskName(fileName, userName);
			String sql = String.format(sqlBuilder.toString(), Integer.parseInt(CommonUtil.getRandomStr()), fileName, fileDiskName, userID, "1998-01-01 00:00:00.0", 1);
			DBUtil.executeSQL(sql);
		}
	}
	
	public static void insertBirtRptDataToDb(String[] fileNameArr, String userName, boolean isGeneric)
	{
		int userID = getUserIDFromDb(userName);
		StringBuilder sqlBuilder = new StringBuilder("INSERT INTO BSM_REPORT(ID, REPORT_NAME, REPORT_DISK_NAME, CREATE_USER_ID, CREATE_TIME, IS_GENERIC) ");
		sqlBuilder.append("VALUES(%d, '%s', '%s', %d, '%s', %d)");
		int generic = isGeneric?1:0;
		for(String fileName : fileNameArr)
		{
			String fileDiskName;
			if(!fileName.contains(userName))
				fileDiskName = getRptDiskName(fileName, userName);
			else
				fileDiskName = fileName;
			String sql = String.format(sqlBuilder.toString(), Integer.parseInt(CommonUtil.getRandomStr()), fileName, fileDiskName, userID, "1998-01-01 00:00:00.0", generic);
			DBUtil.executeSQL(sql);
		}
	}
		
		
	public static void addTransformFromBackend(String userName, boolean isGeneric, String ...transformName){
		//add transform to DB
		int userID = getUserIDFromDb(userName);
		StringBuilder sqlBuilder = new StringBuilder("INSERT INTO BSM_ETL(ID, ETL_NAME, ETL_DISK_NAME, CREATE_USER_ID, CREATE_USER, CREATE_TIME, IS_GENERIC) ");
		sqlBuilder.append("VALUES(%d, '%s', '%s', %d, '%s', '%s', %d)");
		int generic = isGeneric?1:0;
		for(String transform : transformName)
		{
			String fileDiskName = getTransformDiskName(transform, userName);
			String sql = String.format(sqlBuilder.toString(), Integer.parseInt(CommonUtil.getRandomStr()), transform, fileDiskName, userID,userName, "1998-01-01 00:00:00.0", generic);
			System.out.println(sql);
			DBUtil.executeSQL(sql);
		}
		//add transform to Server
		for(String transform : transformName)
		{
			String dumbTransName = getTransformDiskName(transform, userName);
			String cmd = "touch " + transformPath + "/" + dumbTransName;
			logger.info("Run command:" + cmd);
			String stdOutput = CommonUtil.executeSSHCommand(cmd);
			logger.info(stdOutput);
		}
	}
	
	
	
	public static void rmBirtRpts(String[] fileNameArr, String userName)
	{
		//1. Remove dumb files from specified BSM folder
		rmDumbFiles(fileNameArr, userName);
		//2. Delete dumb rpt data from table BSM_REPORT
		deleteDumbFileDataFromDb(fileNameArr, userName);
	}
	
	public static void deleteDumbFileDataFromDb(String[] fileNameArr, String userName)
	{
		int userID = getUserIDFromDb(userName);
		StringBuilder sqlBuilder = new StringBuilder("DELETE FROM BSM_REPORT WHERE REPORT_NAME='%s' AND REPORT_DISK_NAME='%s' AND CREATE_USER_ID=%d");
		for(String fileName : fileNameArr)
		{
			String fileDiskName = getRptDiskName(fileName, userName);
			String sql = String.format(sqlBuilder.toString(), fileName, fileDiskName, userID);
			DBUtil.executeSQL(sql);
		}
	}
	
	public static String getRptDiskName(String rptName, String userName)
	{
		String filePrefix = rptName.substring(0, rptName.lastIndexOf("."));
		String filePostfix = rptName.substring(rptName.lastIndexOf("."), rptName.length());
		return filePrefix + "_" + userName + filePostfix;
	}
	
	public static String getTransformDiskName(String transformFile, String userName){
		StringBuffer transformNameUser= new StringBuffer(transformFile);
		transformNameUser = transformNameUser.insert(transformNameUser.lastIndexOf("."),"_"+userName);
		return new String(transformNameUser);
	}
	
	private static void createDumbFiles(String[] fileNameArr, String userName)
	{
		for(String file : fileNameArr)
		{
			String dumbFileName = getRptDiskName(file, userName);
			String cmd = "touch " + rptPath + "/" + dumbFileName;
			logger.info("Run command:" + cmd);
			String stdOutput = CommonUtil.executeSSHCommand(cmd);
			logger.info(stdOutput);
		}
	}
	
	public static void rmFiles(String folder, String[] fileNameArr)
	{
		for(String file : fileNameArr)
		{
			String cmd = "rm -f " + folder + "/" + file;
			logger.info("Run command:" + cmd);
			CommonUtil.executeSSHCommand(cmd);
		}
	}
	
	private static void rmDumbFiles(String[] fileNameArr, String userName)
	{
		for(String file : fileNameArr)
		{
			String dumbFileName = getRptDiskName(file, userName);
			String cmd = "rm -f " + rptPath + "/" + dumbFileName;
			logger.info("Run command:" + cmd);
			String stdOutput = CommonUtil.executeSSHCommand(cmd);
			logger.info(stdOutput);
		}
	}
	
	private static String executeSSHCommand(String cmd)
	{
		SSHExec ssh = null;

		try
		{
			String host_name = CommonUtil.getPropertyValue("host_name");
			String username = CommonUtil.getPropertyValue("host_username");
			String password = CommonUtil.getPropertyValue("host_password");
			
			logger.info(String.format("Create SSH Connection to %s with %s, %s", host_name, username, password));
			
			ConnBean cb = new ConnBean(host_name, username, password);

			ssh = SSHExec.getInstance(cb);

			CustomTask echo = new ExecCommand(cmd);			
			ssh.connect();
			Result res = ssh.exec(echo);
			
			if(res.isSuccess)
			{
				return res.sysout;
			}else
			{
				logger.info("res.error_msg: " + res.error_msg);
				return res.sysout;
			}
			
		}catch(Exception e)
		{
			logger.error("Execption happened: " + e.getMessage());
		}finally
		{
			logger.info("Close SSH Connection");
			ssh.disconnect();
		}
		return "ERROR";
	}
	
	public static void setGenericDesignRpts(boolean isGeneric)
	{
		int i = isGeneric?1:0;
		String sql = String.format("UPDATE BSM_REPORT SET IS_GENERIC=%d WHERE CREATE_USER_ID=1" , i);
		DBUtil.executeSQL(sql);
	}
		
	
//	public static void main(String[] args){
//		CommonUtil cu = new CommonUtil();
//		cu.addTransformFromBackend("ad", true, "transformAdd1.ktr","transformAdd2.ktr");
//		cu.chkScheduledRptPresentOnFtpSrv("aa", "aa");
//		cu.chkScheduledRptPresentOnHttpSrv("aa", "aa");
//	}
	
}
