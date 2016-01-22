package com.nagra.bsm.ui;

import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import com.nagra.bsm.util.CommonUtil;

public class ScheduleOperation extends BasePage{
	
	public ScheduleOperation(WebDriver driver)
	{
		super(driver);
		logger.info("ScheduleOperation window created.");
	}
	
	private WebElement chkAllExcel = driver.findElement(By.id("selectAllXls"));
	private WebElement chkAllPdf = driver.findElement(By.id("selectAllPdf"));
	private WebElement chkAllHtml = driver.findElement(By.id("selectAllHtml"));
	private WebElement chkEmail = driver.findElement(By.id("isEmail"));
	private WebElement chkFtp = driver.findElement(By.id("isFTP"));
	private WebElement chkHttp = driver.findElement(By.id("isHttp"));
	private WebElement txtArea = driver.findElement(By.xpath("//textarea"));
	
	public void clickTabSelectRpt()
	{
		driver.findElement(By.xpath("//span[text()='Select Report']")).click();
	}

	public void clickTabSendTo()
	{
		driver.findElement(By.xpath("//span[text()='Sending to']")).click();
	}
	
	public void clickSelRpt()
	{
		driver.findElement(By.xpath("//div[text()='Select Report']")).click();
	}
	
	public void clickSave()
	{
		driver.findElement(By.xpath("//div[text()='Save']")).click();
	}
	
	public void clickCancel()
	{
		driver.findElement(By.xpath("//div[text()='Cancel']")).click();
	}
	
	public void clickDelete()
	{
		driver.findElement(By.xpath("//div[text()='Delete']")).click();
	}
	
	public void enableRepeatEvent()
	{
		driver.findElement(By.xpath("//div[text()='Disabled']")).click();
	}
	
	public void disableRepeatEvent()
	{
		driver.findElement(By.xpath("//div[text()='Enabled']")).click();
	}
	
	public boolean chkIsARepeatEvent()
	{
		if(isElementPresent(By.xpath("//div[text()='Enabled']")))
			return true;
		else
			return false;
	}
	
	public String getTitle()
	{
		return driver.findElement(By.className("dhx_title")).getText();
	}
	
	public void chkAllExcel()
	{
		if(!chkAllExcel.isSelected())
		{
			chkAllExcel.click();
		}
	}
	
	public boolean isAllExcelChecked()
	{
		return chkAllExcel.isSelected();
	}
	
	public void chkAllPdf()
	{
		if(!chkAllPdf.isSelected())
		{
			chkAllPdf.click();
		}
	}
	
	public boolean isAllPdfChecked()
	{
		return chkAllPdf.isSelected();
	}
	
	public void chkAllHtml()
	{
		if(!chkAllHtml.isSelected())
		{
			chkAllHtml.click();
		}
	}
	
	public boolean isAllHtmlChecked()
	{
		return chkAllHtml.isSelected();
	}
	
	private WebElement locateElement(String rptName)
	{
		String pattern = String.format("//td[contains(text(),'%s')]", rptName);
		return driver.findElement(By.xpath(pattern));
	}
	
	public boolean isSpecifiedRptPresent(String rptName)
	{
		WebElement e = locateElement(rptName);
		if(e!=null)
			return true;
		else
			return false;
	}
	
	public void chkExcelFor(String rptName)
	{
		locateElement(rptName).findElement(By.xpath("../td[2]/input[1]")).click();
	}
	
	public boolean isExcelChecked(String rptName)
	{
		return locateElement(rptName).findElement(By.xpath("../td[2]/input[1]")).isSelected();
	}
	
	public void chkPdfFor(String rptName)
	{
		locateElement(rptName).findElement(By.xpath("../td[3]/input[1]")).click();
	}
	
	public boolean isPdfChecked(String rptName)
	{
		return locateElement(rptName).findElement(By.xpath("../td[3]/input[1]")).isSelected();
	}
	
	public void chkHtmlFor(String rptName)
	{
		locateElement(rptName).findElement(By.xpath("../td[4]/input[1]")).click();
	}
	
	public boolean isHtmlChecked(String rptName)
	{
		return locateElement(rptName).findElement(By.xpath("../td[4]/input[1]")).isSelected();
	}
	
	public void setTimePeriod(Calendar startDate, Calendar endDate)
	{
		setStartDate(startDate);
		setEndDate(endDate);
	}
	
	public void setStartDate(Calendar startDate)
	{
		WebElement item = driver.findElement(By.className("dhx_section_time"));
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date date = null;
		Select sel = null;
		
		//Set start year
		sel = new Select(item.findElement(By.xpath(".//select[4]")));
		sel.selectByVisibleText(String.valueOf(startDate.get(Calendar.YEAR)));
	    
		//Set start month
		sel = new Select(item.findElement(By.xpath(".//select[3]")));
		sel.selectByValue(String.valueOf(startDate.get(Calendar.MONTH)));
		
		//Set start day
		sel = new Select(item.findElement(By.xpath(".//select[2]")));
		sel.selectByVisibleText(String.valueOf(startDate.get(Calendar.DAY_OF_MONTH)));
		
		//Set start time
		date = startDate.getTime();
		String time = sdf.format(date);
		sel = new Select(item.findElement(By.xpath(".//select[1]")));
		sel.selectByVisibleText(time);
	}
	
	public void setEndDate(Calendar endDate)
	{
		WebElement item = driver.findElement(By.className("dhx_section_time"));
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date date = null;
		Select sel = null;
		
		//Set end year
		sel = new Select(item.findElement(By.xpath(".//select[8]")));
		sel.selectByVisibleText(String.valueOf(endDate.get(Calendar.YEAR)));
		
		//Set end month
		sel = new Select(item.findElement(By.xpath(".//select[7]")));
		sel.selectByValue(String.valueOf(endDate.get(Calendar.MONTH)));
		
		//Set end day
		sel = new Select(item.findElement(By.xpath(".//select[6]")));
		sel.selectByVisibleText(String.valueOf(endDate.get(Calendar.DAY_OF_MONTH)));
		
		//Set end time
		date = endDate.getTime();
		String time = sdf.format(date);
		sel = new Select(item.findElement(By.xpath(".//select[5]")));
		sel.selectByVisibleText(time);
	}
	
	private Calendar getCalendar(int year, int month, int day, int hour, int min)
	{
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		
		return cal;
	}
	
	public Calendar getStartDate()
	{
		Select sel = null;
		
		WebElement item = driver.findElement(By.className("dhx_section_time"));
		
		//Get Year
		sel = new Select(item.findElement(By.xpath(".//select[4]")));
		int year = Integer.parseInt(sel.getFirstSelectedOption().getText());

		//Get Month
		sel = new Select(item.findElement(By.xpath(".//select[3]")));
		int month = Integer.parseInt(sel.getFirstSelectedOption().getAttribute("value"));
		
		//Get Day
		sel = new Select(item.findElement(By.xpath(".//select[2]")));
		int day = Integer.parseInt(sel.getFirstSelectedOption().getText());

		//Get Hour & Minute
		sel = new Select(item.findElement(By.xpath(".//select[1]")));
		String time = sel.getFirstSelectedOption().getText();
		int hour = Integer.parseInt(time.substring(0,time.indexOf(":")));
		int minute = Integer.parseInt(time.substring(time.indexOf(":")+1, time.length()));
		
		return this.getCalendar(year, month, day, hour, minute);
	}
	
	public Calendar getEndDate()
	{
		Select sel = null;
		
		WebElement item = driver.findElement(By.className("dhx_section_time"));
		
		//Get Year
		sel = new Select(item.findElement(By.xpath(".//select[8]")));
		int year = Integer.parseInt(sel.getFirstSelectedOption().getText());

		//Get Month
		sel = new Select(item.findElement(By.xpath(".//select[7]")));
		int month = Integer.parseInt(sel.getFirstSelectedOption().getAttribute("value"));
		
		//Get Day
		sel = new Select(item.findElement(By.xpath(".//select[6]")));
		int day = Integer.parseInt(sel.getFirstSelectedOption().getText());

		//Get Hour & Minute
		sel = new Select(item.findElement(By.xpath(".//select[5]")));
		String time = sel.getFirstSelectedOption().getText();
		int hour = Integer.parseInt(time.substring(0,time.indexOf(":")));
		int minute = Integer.parseInt(time.substring(time.indexOf(":")+1, time.length()));
		
		return this.getCalendar(year, month, day, hour, minute);
	}
	
	public void verifyStartDateEquals(Calendar cal)
	{
		Select sel = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date date = null;
		
		WebElement item = driver.findElement(By.className("dhx_section_time"));
		
		//Verify year
		sel = new Select(item.findElement(By.xpath(".//select[4]")));
		int year = Integer.parseInt(sel.getFirstSelectedOption().getText());
		Assert.assertEquals("Verify the year is correct.", cal.get(Calendar.YEAR), year);
		
		//Verify month
		sel = new Select(item.findElement(By.xpath(".//select[3]")));
		int month = Integer.parseInt(sel.getFirstSelectedOption().getAttribute("value"));
		Assert.assertEquals("Verify the month is correct.", cal.get(Calendar.MONTH), month);
		
		//Verify day
		sel = new Select(item.findElement(By.xpath(".//select[2]")));
		int day = Integer.parseInt(sel.getFirstSelectedOption().getText());
		Assert.assertEquals("Verify the day is correct.", cal.get(Calendar.DAY_OF_MONTH), day);
		
		//Verify hour & minute
		date = cal.getTime();
		String expTime = sdf.format(date);
		
		sel = new Select(item.findElement(By.xpath(".//select[1]")));
		String actualTime = sel.getFirstSelectedOption().getText();
		
		Assert.assertEquals("Verify the time is correct.", expTime, actualTime);
	}
	
	public void verifyEndDateEquals(Calendar cal)
	{
		Select sel = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date date = null;
		
		WebElement item = driver.findElement(By.className("dhx_section_time"));
		
		//Verify year
		sel = new Select(item.findElement(By.xpath(".//select[8]")));
		int year = Integer.parseInt(sel.getFirstSelectedOption().getText());
		Assert.assertEquals("Verify the year is correct.", cal.get(Calendar.YEAR), year);
		
		//Verify month
		sel = new Select(item.findElement(By.xpath(".//select[7]")));
		int month = Integer.parseInt(sel.getFirstSelectedOption().getAttribute("value"));
		Assert.assertEquals("Verify the month is correct.", cal.get(Calendar.MONTH), month);
		
		//Verify day
		sel = new Select(item.findElement(By.xpath(".//select[6]")));
		int day = Integer.parseInt(sel.getFirstSelectedOption().getText());
		Assert.assertEquals("Verify the day is correct.", cal.get(Calendar.DAY_OF_MONTH), day);
		
		//Verify hour & minute
		date = cal.getTime();
		String expTime = sdf.format(date);
		
		sel = new Select(item.findElement(By.xpath(".//select[5]")));
		String actualTime = sel.getFirstSelectedOption().getText();
		
		Assert.assertEquals("Verify the time is correct.", expTime, actualTime);
	}
	
	public void clickSelectRpt()
	{
		driver.findElement(By.xpath("//div[text()=\"Select Report\"]")).click();
	}
	
	public void chkEmail()
	{
		if(!chkEmail.isSelected())
			chkEmail.click();
	}
	
	public void chkFtp()
	{
		if(!chkFtp.isSelected())
			chkFtp.click();
	}
	
	public void clearFtpFields()
	{
		driver.findElement(By.id("FtpUrl")).clear();
		driver.findElement(By.id("FtpSavePath")).clear();
		driver.findElement(By.id("FtpName")).clear();
		driver.findElement(By.id("FtpPassword")).clear();
	}
	
	public void clearHttpFields()
	{
		driver.findElement(By.id("WebDavUrl")).clear();
		driver.findElement(By.id("WebDavUser")).clear();
		driver.findElement(By.id("WebDavPassword")).clear();
	}
	
	public void chkHttp()
	{
		if(!chkHttp.isSelected())
			chkHttp.click();
	}
	
	public boolean isEmailChecked()
	{
		return chkEmail.isSelected();
	}
	
	public boolean isFtpChecked()
	{
		return chkFtp.isSelected();
	}
	
	public boolean isHttpChecked()
	{
		return chkHttp.isSelected();
	}
	
	public void setNoEndDate()
	{
		driver.findElements(By.xpath("//input[@name='end']")).get(0).click();
	}
	
	public void setOccurencyCount(int occurencyCount)
	{
		String value = String.valueOf(occurencyCount);
		
		driver.findElements(By.xpath("//input[@name='end']")).get(1).click();
		driver.findElement(By.xpath("//input[@name='occurences_count']")).clear();
		driver.findElement(By.xpath("//input[@name='occurences_count']")).sendKeys(value);
	}
	
	public int getOccurencyCount()
	{
		return Integer.parseInt(driver.findElement(By.xpath("//input[@name='occurences_count']")).getAttribute("value"));
	}
	
	public void setEndBy(String date)
	{
		driver.findElements(By.xpath("//input[@name='end']")).get(2).click();
		driver.findElement(By.xpath("//input[@name='date_of_end']")).clear();
		driver.findElement(By.xpath("//input[@name='date_of_end']")).sendKeys(date);
	}
		
	public void setEventName(String eventName)
	{
		txtArea.clear();
		txtArea.sendKeys(eventName);
	}
	
	public void setRepeatFrequency(String value)
	{
		logger.info(String.format("Set checkbox %s on.",value));
		String pattern = String.format("//label[contains(text(),'%s')]", value);
		WebElement item = driver.findElement(By.xpath(pattern));
		item.findElement(By.xpath(".//input[1]")).click();
	}
	
	public void setDesEmailAddress(String address)
	{
		chkDesEmail();
		WebElement txtItem = driver.findElement(By.id("Email"));
		txtItem.clear();
		txtItem.sendKeys(address);
	}
	
	public void chkDesEmail()
	{
		logger.info("Check on E-mail.");
		WebElement item = driver.findElement(By.id("isEmail"));
		if(!item.isSelected())
			item.click();
	}
	
	public void unChkEmail()
	{
		logger.info("Check on E-mail.");
		WebElement item = driver.findElement(By.id("isEmail"));
		if(item.isSelected())
			item.click();
	}
	
	public String getEventName()
	{
		return txtArea.getText().trim();
	}
	
	public void setDailyRepeat(int dayCount)
	{
		String day = String.valueOf(dayCount);

		enableDailyRepeat();
		
		driver.findElements(By.xpath("//input[@name='day_type']")).get(0).click();
		WebElement txtDayCount = driver.findElement(By.xpath("//input[@name='day_count']"));
		txtDayCount.clear();
		txtDayCount.sendKeys(day);
	}
	
	public int getDailyRepeat()
	{
		return Integer.parseInt(driver.findElement(By.xpath("//input[@name='day_count']")).getAttribute("value"));
	}
	
	public int getWeeklyRepeat()
	{
		return Integer.parseInt(driver.findElement(By.xpath("//input[@name='week_count']")).getAttribute("value"));
	}
	
	public boolean isWeekDayChecked(String[] weekDayArr)
	{
		for(String weekDay : weekDayArr)
		{
			WebElement lblWeekDay = driver.findElement(By.xpath(String.format("//label[contains(text(),'%s')]", weekDay)));
			if(!lblWeekDay.findElement(By.xpath(".//input")).isSelected())
				return false;
		}
		return true;
	}
	
	public boolean isDailyRepeatChecked()
	{
		return driver.findElements(By.xpath("//input[@name='day_type']")).get(0).isSelected();
	}
	
	public boolean isWeeklyRepeatChecked()
	{
		return driver.findElements(By.xpath("//input[@name='day_type']")).get(1).isSelected();
	}
	
	public void setDailyRepeat()
	{
		enableDailyRepeat();
		driver.findElements(By.xpath("//input[@name='day_type']")).get(1).click();
	}
	
	private void enableDailyRepeat()
	{
		enableRepeatEvent();
		driver.findElements(By.xpath("//input[@name='repeat']")).get(0).click();
	}
	
	public void setWeeklyRepeat(int weekCount, String[] weekDayArr)
	{
		//Enable weekly repeat
		if(!this.chkIsARepeatEvent())
		   enableRepeatEvent();
		
		driver.findElements(By.xpath("//input[@name='repeat']")).get(1).click();
		
		//Set week count
		String count = String.valueOf(weekCount);
		WebElement txtWeekCount = driver.findElement(By.xpath("//input[@name='week_count']"));
		txtWeekCount.clear();
		txtWeekCount.sendKeys(count);
		
		//Choose week day
		for(String weekDay : weekDayArr)
		{
			WebElement lblWeekDay = driver.findElement(By.xpath(String.format("//label[contains(text(),'%s')]", weekDay)));
			logger.info(String.format("Check on %s", weekDay));
			lblWeekDay.findElement(By.xpath(".//input")).click();
		}
	}
	
	public void acceptAlertWindow() 
	{
		try
		{
			CommonUtil.sleep(3);
			Alert alert = driver.switchTo().alert();
			alert.accept();
			CommonUtil.sleep(3);
		}catch(Exception e)
		{}
	} 

	public void cancelAlertWindow(){
		logger.info("Click Cancel to close pop-up dialog.");
		try
		{
			CommonUtil.sleep(3);
			Alert alert = driver.switchTo().alert();
			alert.dismiss();
			CommonUtil.sleep(3);
		}catch(Exception e)
		{}
	}
	
	public String getTextFromAlertWindow()
	{
		try
		{
			CommonUtil.sleep(3);
			Alert alert = driver.switchTo().alert();
			return alert.getText().trim();
		}catch(Exception e)
		{
			return "";
		}
	}
	
	public boolean isBtnPresent(String text)
	{
		return isElementPresent(By.xpath(String.format("//div[text()='%s']", text)));
	}
	
	public void setMonthlyRepeat()
	{

	}
	
	public void setYearlyRepeat()
	{

	}
}
