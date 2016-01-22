/**
 * 
 */
package com.nagra.bsm.tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;

import com.nagra.bsm.ui.ExtractionScheduleMgrPage;
import com.nagra.bsm.ui.RepeatType;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.util.DBUtil;
import com.nagra.bsm.util.DateUtil;
import com.nagra.bsm.util.SshUtil;

/**
 * @author tetang
 *
 */
public class ExtraScheduleMgrTasks {
protected static final Logger logger = Logger.getLogger(ExtraScheduleMgrTasks.class);
private ExtractionScheduleMgrPage pg;

public void goToExtraScheduleMgr(RptCenterPage rptCenterPg){
	this.pg = rptCenterPg.goToExtraScheduleMgr();
	logger.info("Go to Extration Schedule Manager");
}

public void openNewEventWindow(){
	pg.doubleClickOnGrid();
}

public void selectTransforms(String ...transforms){
	pg.clickSelectTransformBtn();
	for(String trfm : transforms){
		pg.chkOnTransform(trfm);
	}
	pg.closeSelectTransformWindow();
}

public void unselectedTransform(String ...transforms){
	pg.clickSelectTransformBtn();
	for(String trfm : transforms){
		pg.chkOffTransform(trfm);
	}
	pg.closeSelectTransformWindow();
}

public void setEventDesc(String desc){
	pg.inputEventDesc(desc);
}

public void setRepeatRule(RepeatType repeat){
	pg.enableRepeat();
	pg.setRepeatRule(repeat);
}

public void disableRepeat(){
	pg.disableRepeat();
	logger.info("disable repeat btn");
}

public void saveEvent(){
	pg.clickSave();
	logger.info("Click Save btn");
}

public void deleteEvent(){
	pg.clickDelete();
	logger.info("Click Delete btn");
}

public void cancelEdit(){
	pg.clickCancel();
	logger.info("Click Cancel btn");
}

public Calendar setEventStartTime(){
	return pg.setStartTime();
}

public Calendar setEventStartDateTime(Calendar cal){
	return pg.setStartDate(cal);
}

public Calendar createEvent(String desc, String ...transforms){
	openNewEventWindow();
	selectTransforms(transforms);
	setEventDesc(desc);
	Calendar eventTime = setEventStartTime();
	saveEvent();
	return eventTime;
}

public Calendar createEvent(String desc, RepeatType repeat, Calendar startDate, String ...transforms){
	startDate = DateUtil.getFirstOccurDate(startDate, repeat);
	DateUtil.setLocalTime(startDate);
	pg.clickToday();
	openNewEventWindow();
	selectTransforms(transforms);
	setEventDesc(desc);
	if(repeat!= null){
		setRepeatRule(repeat);
	}	
	Calendar eventTime = setEventStartDateTime(startDate);
	saveEvent();
	return eventTime;
}

public void triggerEvent(Calendar eventTime){
	Calendar cal = (Calendar) eventTime.clone();
	cal.add(Calendar.MINUTE, -1);
	cal.set(Calendar.SECOND, 58);
	CommonUtil.setRemoteTime(cal);
	CommonUtil.sleep(60);
}

public static void cleanEventDB(){
	String sql = "delete from BSM_SCHEDULER_EVENT_ETL";
	DBUtil.executeSQL(sql);
	sql = "delete from BSM_SCHEDULER_EVENT where EVENT_TYPE = 2";
	DBUtil.executeSQL(sql);	
}

public static void cleanDesOraTable(String table){
	DBUtil.cleanOraTable(table);
}

public boolean verifyTransformToOracleSucceed(String srcTable, String desTable){
	int srcCount, desCount;
	srcCount = DBUtil.getOraTableRowCount(srcTable);
	desCount = DBUtil.getOraTableRowCount(desTable);
	System.out.println(srcCount);
	System.out.println(desCount);
	if (desCount>-1 && srcCount == desCount){
		return true;
	}else{
		return false;
	}	
}

public boolean verifyTransformIsSelected(String transform){
	pg.clickSelectTransformBtn();
	boolean isSelected = pg.isTransformPresentInSelectedGrid(transform);
	pg.closeSelectTransformWindow();
	return isSelected;
}

public boolean verifyExtEventExistsInDB(String eventText){
	CommonUtil.sleep(3);
	String eventId = null;
	eventId = DBUtil.getExtEventID(eventText);
	if(eventId!= null){
		return true;
	}else
		return false;
}

public boolean verifyRecordCountInMongo(String srcTable, String desCollection){
	int srcCount, desCount;
	boolean verify = false;
	srcCount = DBUtil.getOraTableRowCount(srcTable);
	int i=0;
	while(verify == false && i<3){
		desCount = DBUtil.getMongoCollectionRecordCount(desCollection);
		System.out.println(srcCount);
		System.out.println(desCount);
		if(srcCount == desCount){
			return true;
		}
		i=i+1;
		CommonUtil.sleep(60);
	}
	return verify;			
}

public void cleanOraTable(String table){
	DBUtil.cleanOraTable(table);	
}

public void removeMongoCollection(String ...collection){
	DBUtil.removeMongoCollection(collection);
}

public int getIndexMaxNum(String table, String indexColumn){
	String sql = String.format("Select * from %s order by %s desc", table,indexColumn);	
	ResultSet rs = DBUtil.executeQureyInOra(sql);
	int maxIndex = 0;
	try {		
		if(rs.next()){					
			maxIndex = rs.getInt(indexColumn);
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	DBUtil.closeOraConnection();
	return maxIndex;
}

public void inserRowInOracle(String sql,int value){
	DBUtil.insertIntoOra(sql, value);
}

public void goToWeekView(){
	pg.clickWeekTab();
	logger.info("Go to week view");
}

public void goToDayView(){
	pg.clickDayTab();
	logger.info("Go to day view");
}

public void openNewEventWindowWeekView() {
	Calendar cal = Calendar.getInstance();
	pg.doubleClickGridOnWeekView(cal);
	logger.info("Open new event window on week view");
}

public void openNewEventWindowDayView() {
	pg.doubleClickGridOnDayView();
	logger.info("Open new event window on Day view");
}

public void openEvent(String eventText){
	pg.doubleClickEvent(eventText);
	logger.info("open event");
}

public void openEventWeekView(String eventText){
	pg.clickEvent(eventText);
	pg.clickDetailsBtn();
	logger.info("Open event window");
}

public void scrollTo(Calendar cal){
	pg.scrollTo(pg.getClockSlot(cal));
}

public String confirmAlert() {
	Alert alert = pg.getAlertWin();
	String msg = pg.getTxtFrmAlertWin(alert);
	pg.ConfirmOnAlert(alert);
	return msg;
}

public String cancelAlert() {
	Alert alert = pg.getAlertWin();
	String msg = pg.getTxtFrmAlertWin(alert);
	pg.CancelOnAlert(alert);
	return msg;
}

public String confirmDialog(){
	WebElement dialogbox =  pg.getDialogbox();
	String msg = pg.getDialogText(dialogbox);
	pg.closeDialog(dialogbox);
	return msg;
}

public void setEndByDate(String endBydate){
	pg.setEndBy(endBydate);
	logger.info("Set end by date");
}

public static void restoreOraSrcTable(){
	String sql = "delete from cor_device_test_src where DEV_SPID = 2";
	DBUtil.executeSqlInOra(sql);
	DBUtil.closeOraConnection();
	logger.info("restore source table in oracle");
}

public static void startMongoService(){
	SshUtil.login();
	SshUtil.exec("/opt/mongodb-linux-x86_64-2.2.0/bin/mongod &");
	SshUtil.exit();
}

public List<WebElement> getDisplayedBtnsOnEventWin(){
	return pg.getDisplayedBtns();
}

}
