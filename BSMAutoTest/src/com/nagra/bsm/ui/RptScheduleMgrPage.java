package com.nagra.bsm.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import com.nagra.bsm.util.CommonUtil;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RptScheduleMgrPage extends RptCenterPage {

	public RptScheduleMgrPage(WebDriver driver) {
		super(driver);
		logger.info("ScheduleMgrPage object created.");
	}

	/*
	 * Click tab to change the view style
	 */
	public void clickTab(String tabName) {
		logger.info("Click Tab: " + tabName);
		driver.findElement(
				By.xpath(String.format("//div[text()" + "=\"%s\"" + "]",
						tabName))).click();

		if (tabName.equals("Day") || tabName.equals("Week")) {
			if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 13) {
				try {
					Robot robot = new Robot();

					int screenWidth = Toolkit.getDefaultToolkit()
							.getScreenSize().width;
					int screenHeight = Toolkit.getDefaultToolkit()
							.getScreenSize().height;

					robot.mouseMove(screenWidth / 2, screenHeight / 2);

					robot.mouseWheel(50);
					CommonUtil.sleep(2);

				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * Set event schedule(There's a bug that the specified element can't be
	 * clicked.)
	 */
	public void createScheduleEvent() {	
/*		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd");

		String today = sdf.format(date);

		WebElement header_today = driver.findElement(By.xpath(String.format(
				"//div[text()" + "=\"%s\"" + "]", today)));
*/
		WebElement body_today = driver.findElement(By.xpath("//td[contains(@class,'dhx_now')]/div[2]"));		
		// Protection code for moving mouse to specified element.
		try {
			Robot robot = new Robot();
			robot.mouseMove(body_today.getLocation().x + 100,
					body_today.getLocation().y + 130);
			CommonUtil.sleep(2);
		} catch (Exception e) {
		}

		Actions builder = new Actions(getWebDriver());
		Action dbClick = builder.doubleClick(body_today).build();
		dbClick.perform();
		logger.info("Open new event window");
	}
	

	public void createScheduleEvent(Calendar startDate) {
		Date date = startDate.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd");

		String today = sdf.format(date);

		WebElement header_today = driver.findElement(By.xpath(String.format(
				"//div[text()" + "=\"%s\"" + "]", today)));
		WebElement body_today = header_today.findElement(By.xpath("../div[2]"));

		// Protection code for moving mouse to specified element.
		try {
			Robot robot = new Robot();
			robot.mouseMove(body_today.getLocation().x + 100,
					body_today.getLocation().y + 130);
			CommonUtil.sleep(2);
		} catch (Exception e) {
		}

		Actions builder = new Actions(getWebDriver());
		Action dbClick = builder.doubleClick(body_today).build();
		dbClick.perform();
	}

	public void dbClickEvent(String eventName) {
		WebElement divEventName = driver.findElement(By.xpath(String.format(
				"//div[contains(text(),'%s')]", eventName)));

		// Protection code for moving mouse to specified element.
		try {
			Robot robot = new Robot();
			robot.mouseMove(divEventName.getLocation().x + 20,
					divEventName.getLocation().y + 130);
			CommonUtil.sleep(1);

			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.delay(10);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);

			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.delay(10);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);

		} catch (Exception e) {
		}
	}

//	public void deleteEvent(String eventName) {
//		// dbClickEvent(eventName);
//		driver.findElement(By.xpath("//div[@title='Delete']")).click();
//	}
//
//	public void editEvent(String eventName) {
//		// dbClickEvent(eventName);
//		driver.findElement(By.xpath("//div[@title='Details']")).click();
//	}	
	
	private WebElement getCancelBtn(){
		return driver.findElement(By.className("dhx_cancel_btn"));
	}
	
	public void clickCancelOnEvent(){
		getCancelBtn().click();
		logger.info("Click Cancel button on event");
	}
	
	private WebElement getDeleteBtn(){
		return driver.findElement(By.className("dhx_delete_btn"));
	}
	
	public void clickDeleteOnEvent(){
		getDeleteBtn().click();
		logger.info("Click Delete button on event");		
	}
	
	public void clickDetailsBtn(){
		driver.findElement(By.xpath("//div[@title='Details']")).click();
	}

	public void clickToday() {
		driver.findElement(By.className("dhx_cal_today_button")).click();
		logger.info("Click Today button to focus on current date");
	}
	
	private List<WebElement> getEventItem(String eventId){
		String xpath =String.format( "//div[contains(@event_id, '%s')]", eventId);
		return driver.findElements(By.xpath(xpath));
	}
	
	public void doubleClickEvent(String eventId){
		List<WebElement> we = getEventItem(eventId);		
		Point p =  we.get(0).getLocation();
		doubleClick(new Point(p.x +50,p.y+88));				
		logger.info("Double click event to open it");
	}
	
	public void clickEvent(String eventText){				
		String xpath = String.format("//div[text()='%s']", eventText);
		WebElement we = driver.findElements(By.xpath(xpath)).get(0);
//		scrollAndClick(we);
		we.click();
	}	
	
	public void scrollTo(WebElement we){
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",we);
		CommonUtil.sleep(1);
	}
		

	public boolean isSpecifiedEventPresent(String eventName) {
		return isElementPresent(By.xpath(String.format(
				"//div[contains(text(),'%s')]", eventName)));
	}

	// open new event window on current date month view
	public void openNewEventWindow() {
		logger.info("Open new event window");
		int day = CommonUtil.getCurrentDay();
		String xpath = String.format("//div[@class='dhx_month_head' and text()"
				+ "=\"%s\"" + "]//../div[2]", new Integer(day).toString());
		WebElement grid = driver.findElement(By.xpath(xpath));
		Actions builder = new Actions(getWebDriver());
		Action dbClick = builder.doubleClick(grid).build();
		dbClick.perform();
	}

	public void selectRpt(String... rptName) {
		logger.info("Open Select Report window");
		driver.findElement(By.xpath("//div[text()='Select Report']")).click();
		logger.info("Check on Report templates");
		ReportSelector rptSelector = new ReportSelector(driver);
		rptSelector.selectReport(rptName);
		logger.info("Click OK to close select report window");
		driver.findElement(By.id("schedulerRptGridBtnOk")).click();
	}
	
	public void unselectRpt(String... rptName){
		logger.info("Open Select Report window");
		driver.findElement(By.xpath("//div[text()='Select Report']")).click();
		WebElement rptGrid = driver.findElement(By.id("schedulerRptGridDiv"));
		String xpath;
		WebElement rptitem, chkitem;
		for(String rpt:rptName){
			xpath = String.format(".//td[text()='%s']", rpt);
			rptitem = rptGrid.findElement(By.xpath(xpath));
			chkitem = rptitem.findElement(By.xpath("../td[1]/img"));
			if (chkitem.getAttribute("src").endsWith("chk1.gif")) {
				logger.info(String.format(
						"On Report Select window, uncheck on %s", rpt));
				chkitem.click();
			}
		}
		
		logger.info("Click OK to close select report window");
		driver.findElement(By.id("schedulerRptGridBtnOk")).click();
	}
	
	

	// check on format for all reports from selected reports table header
	public void selectRptFormat(RptFormat format) {
		switch (format) {
		case EXCEL:
			logger.info("select Excel foramt for all reports");
			driver.findElement(By.id("selectAllXls")).click();
			break;
		case PDF:
			logger.info("select PDF foramt for all reports");
			driver.findElement(By.id("selectAllPdf")).click();
			break;
		case HTML:
			logger.info("select HTML foramt for all reports");
			driver.findElement(By.id("selectAllHtml")).click();
			break;
		case CSV:
			logger.info("select CSV foramt for all reports");
			driver.findElement(By.id("selectAllCsv")).click();
			break;
		}
	}

	// check on format for all reports from selected reports table header
	public void selectRptFormat(RptFormat[] format) {
		for (RptFormat f : format) {
			selectRptFormat(f);
		}
	}

	// check on format for a single report
	public void selectRptFormat(String rpt, RptFormat format) {
		WebElement rptTemplate = getElemFromText(rpt);
		switch (format) {
		case EXCEL:
			logger.info(String.format("Select EXCEL format for report %s", rpt));
			rptTemplate.findElement(By.xpath("../td[2]/input")).click();
			break;
		case PDF:
			logger.info(String.format("Select PDF format for report %s", rpt));
			rptTemplate.findElement(By.xpath("../td[3]/input")).click();
			break;
		case HTML:
			logger.info(String.format("Select HTML format for report %s", rpt));
			rptTemplate.findElement(By.xpath("../td[4]/input")).click();
			break;
		case CSV:
			logger.info(String.format("Select CSV format for report %s", rpt));
			rptTemplate.findElement(By.xpath("../td[5]/input")).click();
			break;
		}
	}

	// check on multiple format for a single report
	public void selectRptFormat(String rpt, RptFormat[] format) {
		for (RptFormat f : format) {
			selectRptFormat(rpt, f);
		}
	}

	public void switchToSendingTO() {
		logger.info("Go to Sending to tab");
		driver.findElement(By.xpath("//span[text()='Sending to']")).click();
	}

	// check on transport type and use default value
	public void selectTransType(TransType type) {
		switch (type) {
		case EMAIL:
			logger.info("Check on transport type: Email");
			driver.findElement(By.id("isEmail")).click();
			break;
		case FTP:
			logger.info("Check on transport type: FTP");
			driver.findElement(By.id("isFTP")).click();
			break;
		case HTTP:
			logger.info("Check on transport type: HTTP");
			driver.findElement(By.id("isHttp")).click();
			break;
		}
	}

	// check on transport type and use default value
	public void selectTransType(TransType... types) {
		for (TransType t : types) {
			selectTransType(t);
		}
	}

	public void inputEventDesc(String desc) {
		WebElement eventDesc = driver.findElement(By.xpath("//textarea"));
		eventDesc.clear();
		eventDesc.sendKeys(desc);
		logger.info(String.format("Input event desc: %s", desc));
	}

	public void enableRepeat() {
		driver.findElement(By.className("dhx_custom_button")).click();
		CommonUtil.sleep(1);
		logger.info("Enable or disable Repeat button");
	}

	public Calendar setStartTime() {
		WebElement timePeriod = driver.findElement(By
				.className("dhx_section_time"));
		WebElement startTime = timePeriod.findElement(By.xpath("./select[1]"));
		Select sel = new Select(startTime);
		Calendar cal = CommonUtil.getCorrectStartDate();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date date = cal.getTime();
		String time = sdf.format(date);
		sel = new Select(timePeriod.findElement(By.xpath(".//select[1]")));
		sel.selectByVisibleText(time);
		logger.info("Set start time");
		return cal;
	}

	public Calendar setStartDate(Calendar cal) {
		WebElement timePeriod = driver.findElement(By
				.className("dhx_section_time"));
		WebElement startDay = timePeriod.findElement(By.xpath("./select[2]"));
		Select selDay = new Select(startDay);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		selDay.selectByIndex(day - 1);
		logger.info(String.format("Set start day: %d", day));
		WebElement startMon = timePeriod.findElement(By.xpath("./select[3]"));
		Select selMon = new Select(startMon);
		int mon = cal.get(Calendar.MONTH);
		selMon.selectByIndex(mon);
		logger.info(String.format("Set start month: %d", mon));
		WebElement startYear = timePeriod.findElement(By.xpath("./select[4]"));
		Select selYear = new Select(startYear);
		int year = cal.get(Calendar.YEAR);
		selYear.selectByVisibleText(Integer.toString(year));
		logger.info(String.format("Set start year: %d", year));
		WebElement finishDay = timePeriod.findElement(By.xpath("./select[6]"));
		selDay = new Select(finishDay);
		selDay.selectByIndex(day - 1);
		logger.info(String.format("Set finish day: %d", day));
		WebElement finishMon = timePeriod.findElement(By.xpath("./select[7]"));
		selMon = new Select(finishMon);
		selMon.selectByIndex(mon);
		logger.info(String.format("Set finish month: %d", mon));
		WebElement finishYear = timePeriod.findElement(By.xpath("./select[8]"));
		selYear = new Select(finishYear);
		selYear.selectByVisibleText(Integer.toString(year));
		logger.info(String.format("Set finish year: %d", year));
		Calendar time = setStartTime();
		cal.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		return cal;
	}

	public void setRepeatCount(int occurences_count) {
		driver.findElements(By.name("end")).get(1).click();
		WebElement count = driver.findElement(By.name("occurences_count"));
		count.clear();
		count.sendKeys(Integer.toString(occurences_count));
	}

	public void setEndBy(String dateofend) {
		driver.findElement(By.id("rptEndBy")).click();
		WebElement enddate = driver.findElement(By.name("date_of_end"));
		enddate.clear();
		enddate.sendKeys(dateofend);
	}

	public void saveEvent() {
		driver.findElement(By.xpath("//div[text()='Save']")).click();
		logger.info("Save event");
	}

	public void setRepeatRule(RepeatType repeat) {
		if (repeat instanceof Daily) {
			driver.findElement(By.xpath("//input[@value='day']")).click();
			logger.info("Select repeat type: Daily");
			Daily da = (Daily) repeat;
			setDailyRule(da);
		} else if (repeat instanceof Weekly) {
			driver.findElement(By.xpath("//input[@value='week']")).click();
			logger.info("Select repeat type: Weekly");
			Weekly we = (Weekly) repeat;
			setWeeklyRule(we);
		} else if (repeat instanceof Monthly) {
			// driver.findElement(By.xpath("//input[@value='month']")).click();
			// monthly is selected by default.
			logger.info("Select repeat type: Monthly");
			Monthly mon = (Monthly) repeat;
			setMonthlyRule(mon);
		} else if (repeat instanceof Yearly) {
			driver.findElement(By.xpath("//input[@value='year']")).click();
			logger.info("Select repeat type: Yearly");
			Yearly ye = (Yearly) repeat;
			setYearlyRule(ye);
		} else {
			logger.error("Cannot get repeat type");
		}
	}

	public void setDailyRule(Daily daily) {
		if (daily.interval < 0) {
			logger.info("select Every workday");
			// Every workday is selected by default.
		} else {
			logger.info("repeat every x day");
			driver.findElement(
					By.xpath("//div[@id='dhx_repeat_day']//input[@value='d']"))
					.click();
			WebElement day = driver.findElement(By
					.xpath("//div[@id='dhx_repeat_day']/input"));
			day.clear();
			day.sendKeys(Integer.toString(daily.interval));
		}
	}

	public void setWeeklyRule(Weekly weekly) {
		logger.info("repeat every x week");
		WebElement week = driver.findElement(By
				.xpath("//input[@name='week_count']"));
		week.clear();
		week.sendKeys(Integer.toString(weekly.interval));
		String xpath;
		for (String w : weekly.weekdays) {
			xpath = String.format("//label[text()='%s']/input", w);
			WebElement wd = driver.findElement(By.xpath(xpath));
			wd.click();
		}
	}

	public void setMonthlyRule(Monthly monthly) {
		if (monthly.weekNo < 0) {
			logger.info("repeat x day every month");
			driver.findElement(
					By.xpath("//div[@id='dhx_repeat_month']//input[@value='d']"))
					.click();
			WebElement md = driver.findElement(By
					.xpath("//input[@name='month_day']"));
			md.clear();
			md.sendKeys(Integer.toString(monthly.repeatday));
		} else {
			logger.info("repeat on x weekday y every monyh");
			// It is selected by default
			WebElement wn = driver.findElement(By
					.xpath("//input[@name='month_week2']"));
			wn.clear();
			wn.sendKeys(Integer.toString(monthly.weekNo));
			WebElement wds = driver.findElement(By
					.xpath("//div[@id='dhx_repeat_month']/select"));
			Select sel = new Select(wds);
			sel.selectByVisibleText(monthly.weekday);
		}
	}

	public void setYearlyRule(Yearly yearly) {
		if (yearly.no < 0) {
			logger.info("repeate x day month y every year");
			driver.findElement(
					By.xpath("//div[@id='dhx_repeat_year']//input[@value='d']"))
					.click();
			WebElement yd = driver.findElement(By
					.xpath("//input[@name='year_day']"));
			yd.clear();
			yd.sendKeys(Integer.toString(yearly.repeatday));
			WebElement ym = driver.findElement(By
					.xpath("//select[@name='year_month']"));
			Select sel = new Select(ym);
			sel.selectByVisibleText(yearly.month);
		} else {
			logger.info("repeate x weekday month y every year");
			// It is selected by default
			WebElement wn = driver.findElement(By
					.xpath("//input[@name='year_week2']"));
			wn.clear();
			wn.sendKeys(Integer.toString(yearly.no));
			WebElement ywd = driver.findElement(By
					.xpath("//select[@name='year_day2']"));
			Select wdsel = new Select(ywd);
			wdsel.selectByVisibleText(yearly.weekday);
			WebElement ym = driver.findElement(By
					.xpath("//select[@name='year_month2']"));
			Select monsel = new Select(ym);
			monsel.selectByVisibleText(yearly.month);
		}
	}

	private WebElement getEmailTextfield() {
		return driver.findElement(By.id("Email"));
	}

	public void setEmailAddr(String email) {
		WebElement emailField = getEmailTextfield();
		if (!emailField.isEnabled()) {
			selectTransType(TransType.EMAIL);
		}
		emailField.clear();
		emailField.sendKeys(email);
		logger.info("Input email address: " + email);
	}

	public String getEmailAddr() {
		WebElement emailField = getEmailTextfield();
		return emailField.getText().trim();
	}

	private WebElement getFtpIPTextfield() {
		return driver.findElement(By.id("FtpUrl"));
	}

	private WebElement getFtpPathTextfield() {
		return driver.findElement(By.id("FtpSavePath"));
	}

	private WebElement getFtpUsrTextfield() {
		return driver.findElement(By.id("FtpName"));
	}

	private WebElement getFtpPwdTextfield() {
		return driver.findElement(By.id("FtpPassword"));
	}

	public void setFtp(String ftpurl, String path, String username,
			String password) {
		WebElement ftpUrlField = getFtpIPTextfield();
		if (!ftpUrlField.isEnabled()) {
			selectTransType(TransType.FTP);
		}
		ftpUrlField.clear();
		ftpUrlField.sendKeys(ftpurl);
		logger.info("Input FTP url: " + ftpurl);
		WebElement ftpPath = getFtpPathTextfield();
		ftpPath.clear();
		ftpPath.sendKeys(path);
		logger.info("Input FTP path: " + path);
		WebElement ftpUsr = getFtpUsrTextfield();
		ftpUsr.clear();
		ftpUsr.sendKeys(username);
		logger.info("Input FTP username: " + username);
		WebElement ftpPwd = getFtpPwdTextfield();
		ftpPwd.clear();
		ftpPwd.sendKeys(password);
		logger.info("Input FTP password: " + password);
	}

	public String getFtpUrl() {
		WebElement ftpUrlField = getFtpIPTextfield();
		return ftpUrlField.getText().trim();
	}

	private WebElement getHttpUrlTextfield() {
		return driver.findElement(By.id("WebDavUrl"));
	}

	private WebElement getHttpUsrTextfield() {
		return driver.findElement(By.id("WebDavUser"));
	}

	private WebElement getHttpPwdTextfield() {
		return driver.findElement(By.id("WebDavPassword"));
	}

	public void setHttp(String httpurl, String username, String password) {
		WebElement httpurlfield = getHttpUrlTextfield();
		if (!httpurlfield.isEnabled()) {
			selectTransType(TransType.HTTP);
		}
		httpurlfield.clear();
		httpurlfield.sendKeys(httpurl);
		logger.info("Input HTTP url: " + httpurl);
		WebElement httpUsr = getHttpUsrTextfield();
		httpUsr.clear();
		httpUsr.sendKeys(username);
		logger.info("Input HTTP username: " + username);
		WebElement httpPwd = getHttpPwdTextfield();
		httpPwd.clear();
		httpPwd.sendKeys(password);
		logger.info("Input FTP password: " + password);
	}

	public String getHttpUrl() {
		WebElement httpUrlField = getHttpUrlTextfield();
		return httpUrlField.getText().trim();
	}

	public void setTransTypeSetting(RptTransSetting transSetting) {
		if (transSetting instanceof EmailSetting){
			EmailSetting emailSet = (EmailSetting) transSetting;
			setEmailAddr(emailSet.emailAddr);
		}else if (transSetting instanceof FtpSetting){
			FtpSetting ftpSet = (FtpSetting) transSetting;
			setFtp(ftpSet.ftpip, ftpSet.savePath, ftpSet.username, ftpSet.password);
		}else if (transSetting instanceof HttpSetting){
			HttpSetting httpSet = (HttpSetting) transSetting;
			setHttp(httpSet.httpUrl, httpSet.password, httpSet.password);
		}else
			logger.fatal("Cannot get report transport type.");
	}
	
	public void selectTransType(RptTransSetting transSetting){
		if (transSetting instanceof EmailSetting){			
			selectTransType(TransType.EMAIL);
		}else if (transSetting instanceof FtpSetting){			
			selectTransType(TransType.FTP);
		}else if (transSetting instanceof HttpSetting){			
			selectTransType(TransType.HTTP);
		}else
			logger.fatal("Cannot get report transport type.");
	}
	
	public void clickWeekTab(){
		driver.findElement(By.name("week_tab")).click();
	}
	
	public void clickDayTab(){
		driver.findElement(By.name("day_tab")).click();
	}
	
	public void doubleClickGridOnWeekView(Calendar cal){
		int x = getDaySlot(cal).getLocation().getX();
		int y = getClockSlot(cal).getLocation().getY();
		Robot robot;
		try {
			robot = new Robot();
			robot.mouseMove(x+20, y+10);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void doubleClickGridOnDayView(){
		Calendar cal = Calendar.getInstance();
		int x = getClockSlot(cal).getLocation().getX();
		int y = getClockSlot(cal).getLocation().getY();
		Robot robot;
		try {
			robot = new Robot();
			robot.mouseMove(x+80, y+10);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public WebElement getClockSlot(Calendar cal){
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		Date date = cal.getTime();
		String hour = sdf.format(date);		
		String xpath = String.format("//div[text()='%s:00']",hour);
		return driver.findElement(By.xpath(xpath));
	}
	
	public WebElement getDaySlot(Calendar cal){
		int dayno = cal.get(Calendar.DAY_OF_WEEK);
		if (dayno > 1){
			dayno = dayno-1 ;
		}else{
			dayno = 7;
		}
		String xpath = String.format("//div[@class='dhx_cal_data']/div[%s]", new Integer(dayno).toString());
		return driver.findElement(By.xpath(xpath));		
	}
	
	public WebElement getDialogbox(){
		return driver.findElement(By.className("d-dialog"));
	}
	
	public String getDialogText(WebElement dialogbox){
		WebElement we = dialogbox.findElement(By.xpath(".//tr[2]"));
		return we.getText();
	}
	
	public void closeDialog(WebElement dialogbox){
		WebElement we = dialogbox.findElement(By.xpath(".//tr[3]//input"));
		we.click();
	}
	
	public List<WebElement> getDisplayedBtns(){
		List<WebElement> we = driver.findElements(By.className("dhx_btn_set"));
		Iterator<WebElement> it = we.iterator();
		while(it.hasNext()){
			if(!it.next().isDisplayed()){
				it.remove();
			}		
		}		
		return we;
	}
}
