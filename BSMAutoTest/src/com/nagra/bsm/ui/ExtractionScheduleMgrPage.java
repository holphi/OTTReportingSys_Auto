package com.nagra.bsm.ui;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import com.nagra.bsm.util.CommonUtil;

public class ExtractionScheduleMgrPage extends RptCenterPage{
	protected static final Logger logger = Logger.getLogger(ExtractionScheduleMgrPage.class);

	public ExtractionScheduleMgrPage(WebDriver driver){
		super(driver);
	}
	
	public void clickToday(){
		driver.findElement(By.xpath("//div[@id='transformScheduler']//div[text()='Today']")).click();
	}		
	
	public void doubleClickOnGrid(){
		WebElement body_today = driver.findElement(By.xpath("//td[contains(@class,'dhx_now')]/div[2]"));
		
		Robot robot;
		try {
			robot = new Robot();
			robot.mouseMove(body_today.getLocation().x + 100,
					body_today.getLocation().y + 130);
			CommonUtil.sleep(2);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		Actions builder = new Actions(getWebDriver());
		Action dbClick = builder.doubleClick(body_today).build();
		dbClick.perform();
		logger.info("Open new event window");
	}
	
	public void clickSelectTransformBtn(){
		driver.findElement(By.xpath("//div[text()='Select Transform']")).click();
		logger.info("open select transform window");
	}
	
	private WebElement getTransformGrid(){
		return driver.findElement(By.id("transformGridDiv"));
	}
	
	private WebElement getTransformItem(String transformName){
		String xpath = String.format(".//td[text()='%s']", transformName);
		return getTransformGrid().findElement(By.xpath(xpath));
	}
	
	private WebElement getTransformChkbox(String transformName){
		return getTransformItem(transformName).findElement(By.xpath("../td[1]/img"));
	}
	
	public boolean isChkedOn(WebElement transformChkbox){
		String attr = transformChkbox.getAttribute("src");
		if(attr.endsWith("chk1.gif"))
			return true;
		else
			return false;
	}	
		
	public void chkOnTransform(String transform){
		WebElement chkbox = getTransformChkbox(transform);
		if(!isChkedOn(chkbox)){
			chkbox.click();
		}
		logger.info(String.format("Check on transform %s", transform));
	}
	
	public void chkOffTransform(String transform){
		WebElement chkbox = getTransformChkbox(transform);
		if(isChkedOn(chkbox)){
			chkbox.click();
		}
		logger.info(String.format("Check off transform %s", transform));
	}
	
	public void closeSelectTransformWindow(){
		driver.findElement(By.id("transformGridBtnOk")).click();
		logger.info("Close select transform window");
	}
	
	
/*	public List<String> getSelectedTransformName() {
		WebElement selectedTransGrid = driver.findElement(By
				.id("schedulerTransformSelectingGridDiv"));
		List<WebElement> rows = selectedTransGrid.findElements(By
				.xpath(".//td[text()]"));
		List<String> names = null;
		WebElement we = null;
		String name;
		if (!rows.isEmpty()) {
			Iterator<WebElement> it = rows.iterator();
			while (it.hasNext()) {
				we = it.next();
				name = we.getText();
				if (name.endsWith(".ktr")) {
					names.add(name);
				}
			}
		} else {
			logger.error("No transform selected.");
		}
		return names;
	}
*/
	
	public boolean isTransformPresentInSelectedGrid(String transform){
		WebElement selectedTransGrid = driver.findElement(By
				.id("schedulerTransformSelectingGridDiv"));
		String xpath = String.format(".//td[text()='%s']", transform);
		try{
			if (selectedTransGrid.findElement(By.xpath(xpath)).isDisplayed())
				return true;	
			else return false;
		}catch(Exception e){
			return false;
		}
	}
	
	public void inputEventDesc(String desc){
		WebElement we = driver.findElement(By.tagName("textarea"));
		we.clear();
		we.sendKeys(desc);
		logger.info(String.format("Input event description:", desc));
	}
	
	private WebElement getRepeatSwitchBtn(){
		return driver.findElement(By.className("dhx_custom_button"));
	}
	
	public boolean isRepeatEnabled(WebElement RepeatSwitchBtn){
		String text = RepeatSwitchBtn.getText().trim();
		logger.debug(String.format("Button text :", text));
		if (text.equalsIgnoreCase("Enabled")){
			return true;
		}else{
			return false;
		}
	}
	
	public void clickRepeatSwitchBtn(WebElement RepeatSwitchBtn){
		RepeatSwitchBtn.click();
	}
	
	public void enableRepeat(){
		WebElement RepeatBtn = getRepeatSwitchBtn();
		if (!isRepeatEnabled(RepeatBtn)){
			clickRepeatSwitchBtn(RepeatBtn);
			logger.info("Enable repeat");
		}
	}
	
	public void disableRepeat(){
		WebElement RepeatBtn = getRepeatSwitchBtn();
		if (isRepeatEnabled(RepeatBtn)){
			clickRepeatSwitchBtn(RepeatBtn);
			logger.info("disnable repeat");
		}
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
		driver.findElements(By.name("end")).get(2).click();
		WebElement enddate = driver.findElement(By.name("date_of_end"));
		enddate.clear();
		enddate.sendKeys(dateofend);
	}

	private WebElement getSaveBtn(){
		return driver.findElement(By.xpath("//div[text()='Save']"));
	}
	
	public void clickSave() {
		getSaveBtn().click();
		logger.info("Click save btn");
	}
	
	public boolean isSaveBtnDisplayed(){
		return getSaveBtn().isDisplayed();
	}
	
	private WebElement getDeleteBtn(){
		return driver.findElement(By.className("dhx_delete_btn"));
	}
	
	public void clickDelete(){
		getDeleteBtn().click();
		logger.info("Click Delete btn");
	}
	
	public boolean isDeleteBtnDisplayed(){
		return getDeleteBtn().isDisplayed();
	}
	
	private WebElement getCancelBtn(){
		return driver.findElement(By.className("dhx_cancel_btn"));
	}
	
	public void clickCancel(){
		getCancelBtn().click();
		logger.info("Click Cancel btn");
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
	
	public WebElement getEventItem(String eventText){
		String xpath = String.format("//div[contains(text(),'%s')]", eventText);
		return driver.findElements(By.xpath(xpath)).get(0);			
	}
	
	public void doubleClickEvent(String eventText){
		WebElement we = getEventItem(eventText);
		Point p =  we.getLocation();
		doubleClick(new Point(p.x +50,p.y+88));				
		logger.info("Double click event to open it");
	}
	
	public void clickDetailsBtn(){
		driver.findElement(By.xpath("//div[@title='Details']")).click();
	}
	
	public void clickEvent(String eventText){
		WebElement we = getEventItem(eventText);
		we.click();
	}
	
	public void scrollTo(WebElement we){
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",we);
		CommonUtil.sleep(1);
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
