/**
 * 
 */
package com.nagra.bsm.tests;

import java.util.Calendar;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import com.nagra.bsm.tasks.BrowserTasks;
import com.nagra.bsm.tasks.LoginTasks;
import com.nagra.bsm.tasks.RptMgrTasks;
import com.nagra.bsm.tasks.RptScheduleMgrTasks;
import com.nagra.bsm.tasks.TransportConfTasks;
import com.nagra.bsm.ui.Daily;
import com.nagra.bsm.ui.EmailSetting;
import com.nagra.bsm.ui.FtpSetting;
import com.nagra.bsm.ui.HttpSetting;
import com.nagra.bsm.ui.Monthly;
import com.nagra.bsm.ui.RepeatType;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.RptFormat;
import com.nagra.bsm.ui.RptTransSetting;
import com.nagra.bsm.ui.TransType;
import com.nagra.bsm.ui.Weekly;
import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.util.DateUtil;

/**
 * @author tetang
 * 
 */
public class RptScheduleMgrTestPart2 extends BaseTests {
	private static String testDataDir = CommonUtil.getCurrDir()+"\\testdata\\RptTemplate\\";
	BrowserTasks br = new BrowserTasks();
	LoginTasks lg = new LoginTasks();
	RptCenterPage rptCenterPg;
	String desc = null;
	StringBuffer description = new StringBuffer("RE");
	String eventId = null;
	Calendar startDate = null;
	Calendar eventTime = null;

	@Rule
	public Timeout globalTimeout = new Timeout(300000);

	@BeforeClass
	public static void suiteSetup(){
		BrowserTasks br = new BrowserTasks(); 
		br.launchBrowser();
		//open login page
		LoginTasks lg = new LoginTasks();
		lg.loginPage = br.getURL();
		//login
		RptCenterPage rptCenterPg;
		rptCenterPg = lg.login();
		RptMgrTasks rptMgr = new RptMgrTasks();
		rptMgr.gotoRptMgr(rptCenterPg);
		String[] rpts = new String[]{"SDP_BUYS.rptdesign","SDP_NMP.rptdesign","SDP_USERS.rptdesign"};
		rptMgr.addRpt(testDataDir, "SDP_Base.rptlibrary");
		for(String r:rpts){
			if(!rptMgr.isRptExisted(r)){
				rptMgr.addRpt(testDataDir, r);
			}
		}		
		br.closeBrowser();		
	}
	
	@Before
	public void testSetup() {
		logger.info("\n============================="
				+ testName.getMethodName() + "==============================");
		CommonUtil.syncServerTimeWithClient();
		description.append(testName.getMethodName().substring(
				"testRptScheduleMgr".length()));
		description.append("_");
		description.append(CommonUtil.getRandomStr());
		desc = description.toString();
		br.launchBrowser();
		// open login page
		lg.loginPage = br.getURL();
		// login
		rptCenterPg = lg.login();
	}

	@After
	public void testTeardown() {
		br.closeBrowser();
		logger.info("Close browaer");
		DateUtil.syncLocalTime();
		// CommonUtil.sleep(30); //wait client time sync process complete.
		RptScheduleMgrTasks.rmRptEventFromBackend(desc);
		logger.info("remove event");
	}

	// @AfterClass
	public static void suiteTeardown() {
		CommonUtil.syncServerTimeWithClient();
	}

	@Test
	public void testRptScheduleMgr2000() {
		logger.info("Schedule Mgr Case 2000:User can create event on Week View and send report to multiple Email address.");
		String rpt[] = { "SDP_NMP.rptdesign" };
		RptFormat format[] = { RptFormat.PDF };
		RptTransSetting[] rptTrans = { new EmailSetting(
				"teresa.tang@nagra.com;teresa.tang@nagra.com") };
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.gotoWeekView();
		tasks.openNewEventWindowWeekView();
		tasks.selectRpts(rpt);
		tasks.selectRptFormat(format);
		tasks.setRptTransSettings(rptTrans);
		tasks.setEventDesc(desc);
		eventTime = tasks.setEventStartTime();
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = tasks.getExceptedGenRptPattern(rpt[0], format[0],
				eventId, eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(tasks.verifyRptEmailReceived(desc));
	}
	
	@Test
	public void testRptScheduleMgr2001() {
		logger.info("Schedule Mgr Case 2001:User can create repeat event on Week View");
		String rpt[] = { "SDP_NMP.rptdesign" };
		RptFormat format[] = { RptFormat.PDF };
		RptTransSetting[] rptTrans = { new EmailSetting(
				"teresa.tang@nagra.com;teresa.tang@nagra.com") };
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.gotoWeekView();
		tasks.openNewEventWindowWeekView();
		tasks.selectRpts(rpt);
		tasks.selectRptFormat(format);
		tasks.setRptTransSettings(rptTrans);
		tasks.setEventDesc(desc);
		RepeatType repeat = new Daily(1);
		tasks.setRepeatRule(repeat);
		tasks.setRepeatCount(3);
		eventTime = tasks.setEventStartTime();
		tasks.saveEvent();
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<3;i++){
			tasks.triggerEvent(eventTime);
			exptRpt1 = tasks.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(tasks.verifyRptGenerated(exptRpt1));							
			Assert.assertTrue(tasks.verifyRptEmailReceived(desc));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}			
	}

	@Test
	public void testRptScheduleMgr2100() {
		logger.info("Schedule Mgr Case 2100:User can create event on Day View and send report to multiple Email address.");
		String rpt[] = { "SDP_NMP.rptdesign" };
		RptFormat format[] = { RptFormat.PDF };
		RptTransSetting[] rptTrans = { new EmailSetting(
				"teresa.tang@nagra.com;teresa.tang@nagra.com") };
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.gotoDayView();
		tasks.openNewEventWindowDayView();
		tasks.selectRpts(rpt);
		tasks.selectRptFormat(format);
		tasks.setRptTransSettings(rptTrans);
		tasks.setEventDesc(desc);
		eventTime = tasks.setEventStartTime();
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = tasks.getExceptedGenRptPattern(rpt[0], format[0],
				eventId, eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(tasks.verifyRptEmailReceived(desc));
	}

	@Test
	public void testRptScheduleMgr2200() {
		logger.info("Schedule Mgr Case 2200:User can delete a non-repeat event in month view.");
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.createEvent(desc, RptFormat.EXCEL, null, "SDP_NMP.rptdesign");
		tasks.openEvent(desc);
		tasks.clickDelete();
		String msg = tasks.confirmAlert();
		Assert.assertEquals("Event will be deleted permanently, are you sure?",
				msg);
		Assert.assertFalse(tasks.verifyEventExistInDB(desc));
	}

	@Test
	public void testRptScheduleMgr2201() {
		logger.info("Schedule Mgr Case 2201:Event will not be delete if cancel to delete.");
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.createEvent(desc, RptFormat.EXCEL, null, "SDP_NMP.rptdesign");
		tasks.openEvent(desc);
		tasks.clickDelete();
		String msg = tasks.cancelAlert();
		Assert.assertEquals("Event will be deleted permanently, are you sure?",
				msg);
		Assert.assertTrue(tasks.verifyEventExistInDB(desc));
	}

	@Test
	public void testRptScheduleMgr2300() {
		logger.info("Schedule Mgr Case 2300:User can delete a non-repeat event in week view.");
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		eventTime = tasks.createEvent(desc, RptFormat.EXCEL, null, "SDP_NMP.rptdesign");
		tasks.gotoWeekView();
		tasks.scrollTo(eventTime);
		tasks.openEventOnWeekView(desc);
		tasks.clickDelete();
		String msg = tasks.confirmAlert();
		Assert.assertEquals("Event will be deleted permanently, are you sure?",
				msg);
		Assert.assertFalse(tasks.verifyEventExistInDB(desc));
	}

	@Test
	public void testRptScheduleMgr2301() {
		logger.info("Schedule Mgr Case 2301:User can delete a repeat event in week view.");
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		TransType[] type = null;
		eventTime = tasks.createEvent(desc, new RptFormat[] { RptFormat.PDF }, type,
				repeat, startDate, "SDP_NMP.rptdesign");
		tasks.gotoWeekView();
		tasks.scrollTo(eventTime);
		tasks.openEventOnWeekView(desc);
		tasks.clickDelete();
		String msg = tasks.confirmAlert();
		Assert.assertEquals("Event will be deleted permanently, are you sure?",
				msg);
		Assert.assertFalse(tasks.verifyEventExistInDB(desc));
	}

	@Test
	public void testRptScheduleMgr2400() {
		logger.info("Schedule Mgr Case 2400:User can delete a non-repeat event in day view.");
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		eventTime = tasks.createEvent(desc, RptFormat.EXCEL, null, "SDP_NMP.rptdesign");
		tasks.gotoDayView();
		tasks.scrollTo(eventTime);
		tasks.openEventOnWeekView(desc);
		tasks.clickDelete();
		String msg = tasks.confirmAlert();
		Assert.assertEquals("Event will be deleted permanently, are you sure?",
				msg);
		Assert.assertFalse(tasks.verifyEventExistInDB(desc));
	}

	@Test
	public void testRptScheduleMgr2401() {
		logger.info("Schedule Mgr Case 2401:User can delete a non-repeat event in day view.");
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		TransType[] type = null;
		eventTime = tasks.createEvent(desc, new RptFormat[] { RptFormat.PDF }, type,
				repeat, startDate, "SDP_NMP.rptdesign");
		tasks.gotoDayView();
		tasks.scrollTo(eventTime);
		tasks.openEventOnWeekView(desc);
		tasks.clickDelete();
		String msg = tasks.confirmAlert();
		Assert.assertEquals("Event will be deleted permanently, are you sure?",
				msg);
		Assert.assertFalse(tasks.verifyEventExistInDB(desc));
	}

	// User can delete a repeat event.
	@Test
	public void testRptScheduleMgr2500() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		TransType[] type = null;
		tasks.createEvent(desc, new RptFormat[] { RptFormat.PDF }, type,
				repeat, startDate, "SDP_NMP.rptdesign");
		tasks.openEvent(desc);
		tasks.clickDelete();
		String msg = tasks.confirmAlert();
		Assert.assertEquals("Event will be deleted permanently, are you sure?",
				msg);
		Assert.assertFalse(tasks.verifyEventExistInDB(desc));
	}

	// User can change event selected reports.
	@Test
	public void testRptScheduleMgr2600() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		eventTime = tasks.createEvent(desc, RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		tasks.openEvent(desc);
		tasks.unselectRpts("SDP_NMP.rptdesign");
		tasks.selectRpts("SDP_BUYS.rptdesign");
		tasks.selectRptFormat(RptFormat.EXCEL);
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = tasks.getExceptedGenRptPattern("SDP_NMP.rptdesign",
				RptFormat.EXCEL, eventId, eventTime);
		Assert.assertFalse(tasks.verifyRptGenerated(exptRpt1));
		exptRpt1 = tasks.getExceptedGenRptPattern("SDP_BUYS.rptdesign",
				RptFormat.EXCEL, eventId, eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(exptRpt1));
	}

	@Test
	public void testRptScheduleMgr2601() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		eventTime = tasks.createEvent(desc, RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		tasks.openEvent(desc);
		tasks.unselectRpts("SDP_NMP.rptdesign");
		tasks.selectRpts("SDP_BUYS.rptdesign");
		tasks.selectRptFormat(RptFormat.EXCEL);
		tasks.cancelEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = tasks.getExceptedGenRptPattern("SDP_BUYS.rptdesign",
				RptFormat.EXCEL, eventId, eventTime);
		Assert.assertFalse(tasks.verifyRptGenerated(exptRpt1));
	}

	@Test
	public void testRptScheduleMgr2700() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		eventTime = tasks.createEvent(desc, RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		tasks.openEvent(desc);
		tasks.selectRptFormat("SDP_NMP.rptdesign", new RptFormat[] {
				RptFormat.EXCEL, RptFormat.PDF });
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = tasks.getExceptedGenRptPattern("SDP_NMP.rptdesign",
				RptFormat.EXCEL, eventId, eventTime);
		Assert.assertFalse(tasks.verifyRptGenerated(exptRpt1));
		Pattern exptRpt2 = tasks.getExceptedGenRptPattern("SDP_NMP.rptdesign",
				RptFormat.PDF, eventId, eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(exptRpt2));
	}

	@Test
	public void testRptScheduleMgr2800() {
		String rpt = "SDP_NMP.rptdesign";
		RptFormat format = RptFormat.EXCEL;
		TransType type = TransType.HTTP;
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		eventTime = tasks.createEvent(desc, format, type, rpt);
		tasks.openEvent(desc);
		tasks.switchtoSendingTo();
		tasks.selectTransType(TransType.HTTP, TransType.FTP);
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = tasks.getExceptedGenRptPattern(rpt, format, eventId,
				eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(tasks.verifyRptFtpUpload(exptRpt1));
	}

	@Test
	public void testRptScheduleMgr2900() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		eventTime = tasks.createEvent(desc, RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		tasks.openEvent(desc);
		eventTime.add(Calendar.MINUTE, 5);
		eventTime.set(Calendar.SECOND, 10);
		tasks.setEventStartTime(eventTime);
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = tasks.getExceptedGenRptPattern("SDP_NMP.rptdesign",
				RptFormat.EXCEL, eventId, eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(exptRpt1));
	}

	@Test
	public void testRptScheduleMgr3000() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		eventTime = tasks.createEvent(desc, RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		tasks.openEvent(desc);
		RepeatType repeat = new Daily(1);
		tasks.setRepeatRule(repeat);
		tasks.saveEvent();
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1;
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			exptRpt1 = tasks.getExceptedGenRptPattern("SDP_NMP.rptdesign",
					RptFormat.EXCEL, eventId, eventTime);
			Assert.assertTrue(tasks.verifyRptGenerated(exptRpt1));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}

	@Test
	public void testRptScheduleMgr3001() {
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		RptFormat format[] = { RptFormat.PDF };
		TransType type[] = null;
		eventTime = tasks.createEvent(desc, format, type, repeat, startDate,
				"SDP_NMP.rptdesign");
		tasks.openEvent(desc);
		tasks.disableRepeat();
		tasks.saveEvent();
		eventId = RptScheduleMgrTasks.getEventID(desc);
		tasks.triggerEvent(eventTime);
		Pattern exptRpt1 = tasks.getExceptedGenRptPattern("SDP_NMP.rptdesign",
				RptFormat.PDF, eventId, eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(exptRpt1));
		eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		tasks.triggerEvent(eventTime);
		exptRpt1 = tasks.getExceptedGenRptPattern("SDP_NMP.rptdesign",
				RptFormat.PDF, eventId, eventTime);
		Assert.assertFalse(tasks.verifyRptGenerated(exptRpt1));
	}

	@Test
	public void testRptScheduleMgr3100() {
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		RptFormat format[] = { RptFormat.PDF };
		TransType type[] = null;
		eventTime = tasks.createEvent(desc, format, type, repeat, startDate,
				"SDP_NMP.rptdesign");
		tasks.openEvent(desc);
		tasks.disableRepeat();
		tasks.saveEvent();
		tasks.openEvent(desc);
		repeat = new Monthly(eventTime.get(Calendar.DAY_OF_MONTH));
		tasks.setRepeatRule(repeat);
		tasks.saveEvent();
		eventId = RptScheduleMgrTasks.getEventID(desc);
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Pattern exptRpt1 = tasks.getExceptedGenRptPattern(
					"SDP_NMP.rptdesign", RptFormat.PDF, eventId, eventTime);
			Assert.assertTrue(tasks.verifyRptGenerated(exptRpt1));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}

	@Test
	public void testRptScheduleMgr3200() {
		logger.info("Schedule Mgr Case 3200:User can edit event in week view");
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		eventTime = tasks.createEvent(desc, RptFormat.EXCEL, null,
				"SDP_NMP.rptdesign");
		tasks.gotoWeekView();
		tasks.scrollTo(eventTime);
		tasks.openEventOnWeekView(desc);
		desc = desc + "Updated";
		tasks.setEventDesc(desc);
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern expectedRpt = tasks.getExceptedGenRptPattern(
				"SDP_NMP.rptdesign", RptFormat.EXCEL, eventId, eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(expectedRpt));
	}

	@Test
	public void testRptScheduleMgr3201() {
		logger.info("Schedule Mgr Case 3201:User can edit an repeat event in week view");
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		TransType[] type = null;
		eventTime = tasks.createEvent(desc, new RptFormat[] { RptFormat.PDF }, type,
				repeat, startDate, "SDP_NMP.rptdesign");
		tasks.gotoWeekView();
		tasks.scrollTo(eventTime);
		tasks.openEventOnWeekView(desc);
		desc = desc + "Updated";
		tasks.setEventDesc(desc);
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern expectedRpt = tasks.getExceptedGenRptPattern(
				"SDP_NMP.rptdesign", RptFormat.PDF, eventId, eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(expectedRpt));
	}

	@Test
	// User can edit event in day view.
	public void testRptScheduleMgr3300() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		eventTime = tasks.createEvent(desc, RptFormat.EXCEL, null, "SDP_BUYS.rptdesign");
		tasks.gotoDayView();
		tasks.scrollTo(eventTime);
		tasks.openEventOnWeekView(desc);
		desc = desc + "Updated";
		tasks.setEventDesc(desc);
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern expectedRpt = tasks.getExceptedGenRptPattern(
				"SDP_BUYS.rptdesign", RptFormat.PDF, eventId, eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(expectedRpt));
	}

	@Test
	// User can edit an repeat event in Day view.
	public void testRptScheduleMgr3301() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		TransType[] type = null;
		eventTime = tasks.createEvent(desc, new RptFormat[] { RptFormat.PDF }, type,
				repeat, startDate, "SDP_BUYS.rptdesign");
		tasks.gotoDayView();
		tasks.scrollTo(eventTime);
		tasks.openEventOnWeekView(desc);
		desc = desc + "Updated";
		tasks.setEventDesc(desc);
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern expectedRpt = tasks.getExceptedGenRptPattern(
				"SDP_BUYS.rptdesign", RptFormat.PDF, eventId, eventTime);
		Assert.assertTrue(tasks.verifyRptGenerated(expectedRpt));
	}

	@Test
	// An event at least has one report.
	public void testRptScheduleMgr3400() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.setEventDesc(desc);
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals("Please select a report..", msg);
	}

	@Test
	// Event start time must be later than current time.
	public void testRptScheduleMgr3500() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.selectRpts("SDP_BUYS.rptdesign");
		tasks.selectRptFormat(RptFormat.PDF);
		tasks.setEventDesc(desc);
		startDate = Calendar.getInstance();
		startDate.add(Calendar.YEAR, -1);
		tasks.setEventStartTime(startDate);
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals(
				"The start date you entered occurs before current server date.",
				msg);
	}

	@Test
	// Event end date must be later than start date.
	public void testRptScheduleMgr3600() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.selectRpts("SDP_BUYS.rptdesign");
		tasks.selectRptFormat(RptFormat.PDF);
		tasks.setEventDesc(desc);
		startDate = Calendar.getInstance();
		startDate.add(Calendar.YEAR, -1);
		tasks.setEventStartTime(startDate);
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals(
				"The start date you entered occurs before current server date.",
				msg);
	}

	@Test
	// Event end date must be in format mm.dd.yyyy.
	public void testRptScheduleMgr3700() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		tasks.openNewEventWindow();
		tasks.selectRpts("SDP_BUYS.rptdesign");
		tasks.selectRptFormat(RptFormat.PDF);
		tasks.setEventDesc(desc);
		tasks.setRepeatRule(repeat);
		tasks.setRepeatEndDate("07032012");
		tasks.setEventStartTime();
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals(
				"The end date you entered is invalid,the correct date format should be mm.dd.yyyy.",
				msg);
	}

	@Ignore
	@Test
	// Paging bar on select report window works well.
	public void testRptScheduleMgr3800() {

	}

	@Test
	// User must select at least one export format for selected report.
	public void testRptScheduleMgr3900() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.selectRpts("SDP_BUYS.rptdesign");
		tasks.setEventDesc(desc);
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals(
				"Please select at least one export format for SDP_BUYS.rptdesign.",
				msg);
	}

	@Test
	// User must select at least one export format for each selected reports.
	public void testRptScheduleMgr3901() {
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.selectRpts("SDP_BUYS.rptdesign", "SDP_NMP.rptdesign");
		tasks.selectRptFormat("SDP_BUYS.rptdesign", RptFormat.PDF);
		tasks.setEventDesc(desc);
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals(
				"Please select at least one export format for SDP_NMP.rptdesign.",
				msg);
	}

	@Test
	// User can not left destinaiton email field empty if chooses sending via
	// email when create an event on report schedule manager
	public void testRptScheduleMgr4000() {
		RptTransSetting[] rptTrans = { new EmailSetting("") };
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.openNewEventWindow();		
		tasks.selectRpts("SDP_BUYS.rptdesign");
		tasks.selectRptFormat("SDP_BUYS.rptdesign", RptFormat.PDF);
		tasks.setRptTransSettings(rptTrans);
		tasks.setEventDesc(desc);
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals("Please input the Email.", msg);
	}

	@Test
	//User can not left ftp field empty if chooses sending via ftp when create an event on report schedule manager
	public void testRptScheduleMgr4001() {
		RptTransSetting[] rptTrans = { new FtpSetting("","","","") };
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.openNewEventWindow();		
		tasks.selectRpts("SDP_BUYS.rptdesign");
		tasks.selectRptFormat("SDP_BUYS.rptdesign", RptFormat.PDF);
		tasks.setRptTransSettings(rptTrans);
		tasks.setEventDesc(desc);
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals("Please input the FTP Url.", msg);
	}
	
	@Test
	//User can not left http field empty if chooses sending via http when create an event on report schedule manager
	public void testRptScheduleMgr4002() {
		RptTransSetting[] rptTrans = { new HttpSetting("","","") };
		RptScheduleMgrTasks tasks = new RptScheduleMgrTasks();
		tasks.gotoRptScheduleManager(rptCenterPg);
		tasks.openNewEventWindow();	
		tasks.selectRpts("SDP_BUYS.rptdesign");
		tasks.selectRptFormat("SDP_BUYS.rptdesign", RptFormat.PDF);
		tasks.setRptTransSettings(rptTrans);
		tasks.setEventDesc(desc);
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals("Please input the http Url.", msg);
	}
}
