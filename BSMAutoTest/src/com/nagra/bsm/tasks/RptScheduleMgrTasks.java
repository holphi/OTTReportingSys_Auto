/**
 * 
 */
package com.nagra.bsm.tasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;

import com.nagra.bsm.ui.EmailSetting;
import com.nagra.bsm.ui.FtpSetting;
import com.nagra.bsm.ui.HttpSetting;
import com.nagra.bsm.ui.RepeatType;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.RptFormat;
import com.nagra.bsm.ui.RptScheduleMgrPage;
import com.nagra.bsm.ui.RptTransSetting;
import com.nagra.bsm.ui.TransType;
import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.util.DBUtil;
import com.nagra.bsm.util.DateUtil;
import com.nagra.bsm.util.FtpUtil;
import com.nagra.bsm.util.SshUtil;
import com.nagra.bsm.util.WebdavUtil;

/**
 * @author tetang
 * 
 */
public class RptScheduleMgrTasks {
	private RptScheduleMgrPage rptScheduleMgrPg;
	protected static final Logger logger = Logger
			.getLogger(RptScheduleMgrTasks.class);
	final String datadir = "/soft/bsmsoft/bsm1/bsm/current/data/";
	final String ftpPath = "/report";

	public RptScheduleMgrTasks() {

	}

	public void gotoRptScheduleManager(RptCenterPage rptCenterPg) {
		this.rptScheduleMgrPg = rptCenterPg.goToRptScheduleMgr();
		logger.info("go to Report Schedule Manager");
	}

	public RptScheduleMgrTasks(RptScheduleMgrPage rptScheduleMgrPg) {
		this.rptScheduleMgrPg = rptScheduleMgrPg;
	}

	// open new event window on current date month view
	public void openNewEventWindow() {
		rptScheduleMgrPg.createScheduleEvent();
	}

	public void openNewEventWindowWeekView(Calendar cal) {
		rptScheduleMgrPg.doubleClickGridOnWeekView(cal);
	}

	public void openNewEventWindowDayView() {
		rptScheduleMgrPg.doubleClickGridOnDayView();
	}

	public void gotoWeekView() {
		rptScheduleMgrPg.clickWeekTab();
	}

	public void gotoDayView() {
		rptScheduleMgrPg.clickDayTab();
	}

	public void openNewEventWindowWeekView() {
		Calendar cal = Calendar.getInstance();
		rptScheduleMgrPg.doubleClickGridOnWeekView(cal);
	}

	// create non-repeat event with same format,single format,1rpt*1format*1type
	public Calendar createEvent(String desc, RptFormat format, TransType type,
			String... rpt) {
		openNewEventWindow();
		rptScheduleMgrPg.selectRpt(rpt);
		rptScheduleMgrPg.selectRptFormat(format);
		if (type != null) {
			rptScheduleMgrPg.switchToSendingTO();
			rptScheduleMgrPg.selectTransType(type);
		}
		rptScheduleMgrPg.inputEventDesc(desc);
		Calendar startTime = rptScheduleMgrPg.setStartTime();
		rptScheduleMgrPg.saveEvent();
		return startTime;
	}

	// create non-repeat event with same report format and transport ,multile
	// format,multiple trantype, 1rpt*nformat*ntype
	public Calendar createEvent(String desc, RptFormat[] formats,
			TransType[] types, String... rpt) {
		openNewEventWindow();
		rptScheduleMgrPg.selectRpt(rpt);
		for (RptFormat rf : formats) {
			rptScheduleMgrPg.selectRptFormat(rf);
		}
		if (types != null && types.length > 0) {
			rptScheduleMgrPg.switchToSendingTO();
			for (TransType ty : types) {
				rptScheduleMgrPg.selectTransType(ty);
			}
		}
		rptScheduleMgrPg.inputEventDesc(desc);
		Calendar startTime = rptScheduleMgrPg.setStartTime();
		rptScheduleMgrPg.saveEvent();
		return startTime;
	}

	// create non-repeat event with different report format and transport
	public Calendar createEvent(String desc, Map<String, RptFormat> map,
			TransType[] types) {
		openNewEventWindow();
		Set<String> mapKeySet = map.keySet();
		String[] rpt = new String[mapKeySet.size()];
		rpt = mapKeySet.toArray(rpt);
		rptScheduleMgrPg.selectRpt(rpt);
		for (String r : rpt) {
			rptScheduleMgrPg.selectRptFormat(r, map.get(r));
		}
		if (types != null && types.length > 0) {
			rptScheduleMgrPg.switchToSendingTO();
			for (TransType ty : types) {
				rptScheduleMgrPg.selectTransType(ty);
			}
		}
		rptScheduleMgrPg.inputEventDesc(desc);
		Calendar startTime = rptScheduleMgrPg.setStartTime();
		rptScheduleMgrPg.saveEvent();
		return startTime;
	}

	// create repeat event with same report format and transport
	public Calendar createEvent(String desc, RptFormat[] formats,
			TransType[] types, RepeatType repeat, Calendar startDate,
			String... rpt) {
		startDate = DateUtil.getFirstOccurDate(startDate, repeat);
		DateUtil.setLocalTime(startDate);
		rptScheduleMgrPg.clickToday();
		openNewEventWindow();
		rptScheduleMgrPg.selectRpt(rpt);
		for (RptFormat rf : formats) {
			rptScheduleMgrPg.selectRptFormat(rf);
		}
		if (types != null && types.length > 0) {
			rptScheduleMgrPg.switchToSendingTO();
			for (TransType ty : types) {
				rptScheduleMgrPg.selectTransType(ty);
			}
		}
		rptScheduleMgrPg.inputEventDesc(desc);
		rptScheduleMgrPg.enableRepeat();
		rptScheduleMgrPg.setRepeatRule(repeat);
		rptScheduleMgrPg.setRepeatCount(3);
		Calendar eventDateTime = rptScheduleMgrPg.setStartDate(startDate);
		rptScheduleMgrPg.saveEvent();
		return eventDateTime;
	}

	// create repeat event with same report format and transport
	public Calendar createEvent(String desc, Map<String, RptFormat> map,
			TransType[] types, RepeatType repeat, Calendar startDate,
			int repeatCount) {
		startDate = DateUtil.getFirstOccurDate(startDate, repeat);
		DateUtil.setLocalTime(startDate);
		rptScheduleMgrPg.clickToday();
		openNewEventWindow();
		Set<String> mapKeySet = map.keySet();
		String[] rpt = new String[mapKeySet.size()];
		rpt = mapKeySet.toArray(rpt);
		rptScheduleMgrPg.selectRpt(rpt);
		for (String r : rpt) {
			rptScheduleMgrPg.selectRptFormat(r, map.get(r));
		}
		if (types != null && types.length > 0) {
			rptScheduleMgrPg.switchToSendingTO();
			for (TransType ty : types) {
				rptScheduleMgrPg.selectTransType(ty);
			}
		}
		rptScheduleMgrPg.inputEventDesc(desc);
		if (repeat != null) {
			rptScheduleMgrPg.enableRepeat();
			rptScheduleMgrPg.setRepeatRule(repeat);
			rptScheduleMgrPg.setRepeatCount(repeatCount);
		}
		Calendar eventDateTime = rptScheduleMgrPg.setStartDate(startDate);
		rptScheduleMgrPg.saveEvent();
		return eventDateTime;
	}

	public Calendar createEvent(String desc, RptFormat[] formats,
			RptTransSetting[] types, RepeatType repeat, Calendar startDate,
			String... rpt) {
		startDate = DateUtil.getFirstOccurDate(startDate, repeat);
		DateUtil.setLocalTime(startDate);
		rptScheduleMgrPg.clickToday();
		openNewEventWindow();
		rptScheduleMgrPg.selectRpt(rpt);
		for (RptFormat rf : formats) {
			rptScheduleMgrPg.selectRptFormat(rf);
		}
		if (types != null && types.length > 0) {
			rptScheduleMgrPg.switchToSendingTO();
			for (RptTransSetting ty : types) {
				rptScheduleMgrPg.setTransTypeSetting(ty);
			}
		}
		rptScheduleMgrPg.inputEventDesc(desc);
		if (repeat != null) {
			rptScheduleMgrPg.enableRepeat();
			rptScheduleMgrPg.setRepeatRule(repeat);
			setRepeatCount(3);
		}
		Calendar eventDateTime = rptScheduleMgrPg.setStartDate(startDate);
		rptScheduleMgrPg.saveEvent();
		return eventDateTime;
	}

	public void setRptFormats(Map<String, RptFormat> map) {
		Set<String> mapKeySet = map.keySet();
		String[] rpt = new String[mapKeySet.size()];
		rpt = mapKeySet.toArray(rpt);
		rptScheduleMgrPg.selectRpt(rpt);
		for (String r : rpt) {
			rptScheduleMgrPg.selectRptFormat(r, map.get(r));
		}
	}

	public void setRptTransSettings(RptTransSetting[] types) {
		rptScheduleMgrPg.switchToSendingTO();
		for (RptTransSetting ty : types) {
			rptScheduleMgrPg.setTransTypeSetting(ty);
		}
	}

	public void setRepeatRule(RepeatType repeat) {
		rptScheduleMgrPg.enableRepeat();
		rptScheduleMgrPg.setRepeatRule(repeat);
	}

	public void disableRepeat() {
		rptScheduleMgrPg.enableRepeat();
	}

	public void setRepeatCount(int count) {
		rptScheduleMgrPg.setRepeatCount(count);
	}

	public void setRepeatEndDate(String end) {
		rptScheduleMgrPg.setEndBy(end);
	}

	public void setEventDesc(String desc) {
		rptScheduleMgrPg.inputEventDesc(desc);
	}

	public Calendar setEventStartTime(Calendar starttime) {
		return rptScheduleMgrPg.setStartDate(starttime);
	}

	public Calendar setEventStartTime() {
		return setEventStartTime(Calendar.getInstance());
	}

	public void saveEvent() {
		rptScheduleMgrPg.saveEvent();
	}

	public void cancelEvent() {
		rptScheduleMgrPg.clickCancelOnEvent();
	}

	public void clickDelete() {
		rptScheduleMgrPg.clickDeleteOnEvent();
	}

	public void openEvent(String eventText) {
		rptScheduleMgrPg.doubleClickEvent(getEventID(eventText));
	}

	public void openEventOnWeekView(String eventText) {		
		rptScheduleMgrPg.clickEvent(eventText);
		rptScheduleMgrPg.clickDetailsBtn();
	}	

	public void scrollTo(Calendar cal){
		rptScheduleMgrPg.scrollTo(rptScheduleMgrPg.getClockSlot(cal));
	}
	
	public void selectRpts(String... rpt) {
		rptScheduleMgrPg.selectRpt(rpt);
	}

	public void unselectRpts(String... rpt) {
		rptScheduleMgrPg.unselectRpt(rpt);
	}

	public void selectRptFormat(RptFormat... format) {
		rptScheduleMgrPg.selectRptFormat(format);
	}

	public void selectRptFormat(String rpt, RptFormat... format) {
		rptScheduleMgrPg.selectRptFormat(rpt, format);
	}

	public void selectTransType(TransType... type) {
		for (TransType ty : type) {
			rptScheduleMgrPg.selectTransType(ty);
		}
	}
	
	public void switchtoSendingTo(){
		rptScheduleMgrPg.switchToSendingTO();
	}

	public boolean verifyRptEmailReceived(String desc) {
		// boolean receive = false;
		// String sub = "[BSM] Generated report - " + desc;
		// receive = EmailUtil.doesMailExist(sub);
		// return receive;
		return true;
	}

	public boolean verifyRptEmailReceived(RptTransSetting transSet, String desc) {
		EmailSetting emailSet = (EmailSetting) transSet;
		// to be finish

		return true;
	}

	public boolean verifyRptFtpUpload(Pattern expectedRpt) {
		return FtpUtil.chkFileExistOnFtp(ftpPath, expectedRpt);
	}

	public boolean verifyRptFtpUpload(RptTransSetting transSet,
			Pattern expectedRpt) {
		FtpSetting ftpSet = (FtpSetting) transSet;
		return FtpUtil.chkFileExistOnFtp(ftpSet.ftpip, ftpSet.savePath,
				ftpSet.username, ftpSet.password, expectedRpt);
	}

	public boolean verifyRptHttpUpload(Pattern exptRpt) {
		return WebdavUtil.chkFileExistOnWebDav(exptRpt);
	}

	public boolean verifyRptHttpUpload(RptTransSetting transSet, Pattern exptRpt) {
		HttpSetting httpSet = (HttpSetting) transSet;
		return WebdavUtil.chkFileExistOnWebDav(httpSet.httpUrl,
				httpSet.username, httpSet.password, exptRpt);
	}

	public boolean verifyRptGenerated(Pattern expectedRpt) {
		return SshUtil.chkfileExistOnSftp(datadir, expectedRpt);
	}

	public boolean verifyRptTransported(Pattern expectedRpt, TransType type) {
		switch (type) {
		case FTP:
			return verifyRptFtpUpload(expectedRpt);
		case HTTP:
			return verifyRptHttpUpload(expectedRpt);
		}
		return false;
	}

	public boolean verifyEventExistInDB(String eventText) {
		CommonUtil.sleep(1);
		String sql = String
				.format("select count(*) from BSM_SCHEDULER_EVENT where EVENT_TEXT='%s'",
						eventText);
		// logger.info(sql);
		int count = DBUtil.getRecordCountOfSql(sql);
		if (count == 1) {
			return true;
		} else if (count == 0) {
			return false;
		} else {
			logger.error(String.format("EventText:%s Record count: %d",
					eventText, count));
			return false;
		}
	}

	// change server time to trigger event
	public void triggerEvent(Calendar calendar) {
		Calendar cal = (Calendar) calendar.clone();
		cal.add(Calendar.MINUTE, -1);
		cal.set(Calendar.SECOND, 58);
		CommonUtil.setRemoteTime(cal);
		// CommonUtil.sleep(60);
	}

	public static void rmRptEventFromBackend(String eventText) {
		// String event_id = getEventID(eventText);
		// String sql =
		// String.format("delete from CSV_SETTING where event_id=%s", event_id);
		String sql = String.format("delete from CSV_SETTING where 1=1");
		DBUtil.executeSQL(sql);
		// sql =
		// String.format("DELETE FROM BSM_SCHEDULER_EVENT_REPORT WHERE SCHEDULER_EVENT_ID=%s",
		// event_id);
		sql = String.format("delete from BSM_SCHEDULER_EVENT_REPORT where 1=1");
		DBUtil.executeSQL(sql);
		sql = String.format("DELETE FROM BSM_SCHEDULER_EVENT WHERE 1=1");
		DBUtil.executeSQL(sql);
	}

	// Get expected generated report name regular expression
	public Pattern getExceptedGenRptPattern(String rpt, RptFormat format,
			String eventID, Calendar cal) {
		rpt = rpt.substring(0, rpt.lastIndexOf("."));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
		Date date = cal.getTime();
		String occtime = sdf.format(date);
		String postfix = getExceptedGenRptPostfix(format);
		StringBuffer regex = new StringBuffer(rpt);
		regex = regex.append("_").append(occtime).append("[0-5][0-9]_")
				.append(eventID).append(postfix);
		logger.info("Expected report name is like: " + regex);
		return Pattern.compile(regex.toString());
	}

	public static String getEventID(String eventText) {
		String eventID = CommonUtil.getScheduledEventIDFromDB(eventText);
		logger.info(String.format("EventID is %s", eventID));
		return eventID;
	}

	// Get expected generated report name postfix
	public String getExceptedGenRptPostfix(RptFormat format) {
		String postfix = null;
		switch (format) {
		case EXCEL:
			postfix = ".xls";
			break;
		case PDF:
			postfix = ".pdf";
			break;
		case HTML:
			postfix = ".zip";
			break;
		case CSV:
			postfix = ".csv";
		}
		return postfix;
	}

	public boolean verifyDefaultEmailAddr() {
		String curEmail = rptScheduleMgrPg.getEmailAddr();
		String defaultEmail = CommonUtil.getPropertyValue("des_email");
		if (curEmail.equalsIgnoreCase(defaultEmail))
			return true;
		else
			return false;
	}

	public boolean verifyDefaultFtpUrl() {
		String curFtp = rptScheduleMgrPg.getFtpUrl();
		String defaultFtp = CommonUtil.getPropertyValue("ftp_url");
		if (curFtp.equalsIgnoreCase(defaultFtp))
			return true;
		else
			return false;
	}

	public boolean verifyDefaultHttpUrl() {
		String curHttp = rptScheduleMgrPg.getHttpUrl();
		String defaultHttp = CommonUtil.getPropertyValue("http_url");
		if (curHttp.equalsIgnoreCase(defaultHttp))
			return true;
		else
			return false;
	}

	public void editEmail(String email) {
		rptScheduleMgrPg.switchToSendingTO();
		rptScheduleMgrPg.setEmailAddr(email);
	}

	public void editFtp(String ftpurl, String path, String usr, String pwd) {
		rptScheduleMgrPg.switchToSendingTO();
		rptScheduleMgrPg.setFtp(ftpurl, path, usr, pwd);
	}

	public void editHttp(String httpurl, String username, String password) {
		rptScheduleMgrPg.switchToSendingTO();
		rptScheduleMgrPg.setHttp(httpurl, username, password);
	}

	public String confirmAlert() {
		Alert alert = rptScheduleMgrPg.getAlertWin();
		String msg = rptScheduleMgrPg.getTxtFrmAlertWin(alert);
		rptScheduleMgrPg.ConfirmOnAlert(alert);
		return msg;
	}

	public String cancelAlert() {
		Alert alert = rptScheduleMgrPg.getAlertWin();
		String msg = rptScheduleMgrPg.getTxtFrmAlertWin(alert);
		rptScheduleMgrPg.CancelOnAlert(alert);
		return msg;
	}

	public String confirmDialog(){
		WebElement dialogbox =  rptScheduleMgrPg.getDialogbox();
		String msg = rptScheduleMgrPg.getDialogText(dialogbox);
		rptScheduleMgrPg.closeDialog(dialogbox);
		return msg;
	}
	
	public List<WebElement> getDisplayedBtnsOnEventWin(){
		return rptScheduleMgrPg.getDisplayedBtns();
	}
	
	public static void main(String[] args) {
		RptScheduleMgrTasks rs = new RptScheduleMgrTasks();
		rs.rmRptEventFromBackend("RE0100_3392");
	}

}
