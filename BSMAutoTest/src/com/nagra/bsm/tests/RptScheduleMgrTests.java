/**
 * 
 */
package com.nagra.bsm.tests;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;

import com.nagra.bsm.tasks.BrowserTasks;
import com.nagra.bsm.tasks.LoginTasks;
import com.nagra.bsm.tasks.RptMgrTasks;
import com.nagra.bsm.tasks.RptScheduleMgrTasks;
import com.nagra.bsm.tasks.TransportConfTasks;
import com.nagra.bsm.tests.categorymarker.SanityCheck;
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
import com.nagra.bsm.ui.Yearly;
import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.util.DateUtil;

/**
 * @author tetang
 *
 */
public class RptScheduleMgrTests extends BaseTests{
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
	public Timeout globalTimeout = new Timeout(900000);
	
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
		TransportConfTasks transConf = new TransportConfTasks();
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setSmtpSetting();
		transConf.setDesEmail("teresa.tang@nagra.com");
		transConf.setFtpSetting("/report");
		transConf.setHttpSetting();
		br.closeBrowser();		
	}
	
	
	
	@Before
	public void testSetup(){
		logger.info("\n============================="+testName.getMethodName()+"==============================");		
		CommonUtil.syncServerTimeWithClient();		
		description.append(testName.getMethodName().substring("testRptScheduleMgr".length()));
		description.append("_");
		description.append(CommonUtil.getRandomStr());
		desc = description.toString();
		br.launchBrowser();
		//open login page
		lg.loginPage = br.getURL();
		//login
		rptCenterPg = lg.login();		
	}

	@After
	public void testTeardown(){
		br.closeBrowser();
		logger.info("Close browaer");
		DateUtil.syncLocalTime();
//		CommonUtil.sleep(30); //wait client time sync process complete.
		RptScheduleMgrTasks.rmRptEventFromBackend(desc);
		logger.info("remove event");
	}
	
	@AfterClass
	public static void suiteTeardown(){
		CommonUtil.syncServerTimeWithClient();
	}
	
	
	/*
	 * ScheduleMgrCase0100(P1):User can create an non-repeat event with one report, generate report in Excel format,and transport report via Email.
	 */
	@Test
	@Category(SanityCheck.class)
	public void testRptScheduleMgr0100(){
		String rpt = "SDP_NMP.rptdesign";
		RptFormat format = RptFormat.EXCEL;
		TransType type = TransType.EMAIL;
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, type, rpt);		
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt, format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(sch.verifyRptEmailReceived(desc));
	}


	/*
	 * ScheduleMgrCase0101(P3):User can create an non-repeat event with one report, generate report in Excel and PDF format,and transport report via Email.
	 */
	@Test
	public void testRptScheduleMgr0101(){
		String rpt = "SDP_NMP.rptdesign";
		RptFormat[] formats = new RptFormat[]{RptFormat.EXCEL,RptFormat.PDF};
		TransType[] types = new TransType[]{TransType.EMAIL};		
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(desc, formats, types, rpt);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt, formats[0], eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
		Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt, formats[1], eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
		Assert.assertTrue(sch.verifyRptEmailReceived(desc));
	}
	
	/*
	 * Schedule Mgr Case 0102 (P3): User can create an non-repeat event with one report, generate report in Excel format(Check off ,recheck on),and transport report via Email.
	 * to be improve
	 */
	@Test
	@Category(SanityCheck.class)
	public void testRptScheduleMgr0102(){
		String rpt = "SDP_NMP.rptdesign";
		RptFormat format = RptFormat.EXCEL;
		TransType type = TransType.EMAIL;
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, type, rpt);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt, format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(sch.verifyRptEmailReceived(desc));
	}
	
	
	/*
	 * Schedule Mgr Case 0200 (P1): User can create an non-repeat event with one report, generate report in PDF format,and transport report via FTP.
	 */
	@Test
	public void testRptScheduleMgr0200(){
		String rpt = "SDP_NMP.rptdesign";
		RptFormat format = RptFormat.PDF;
		TransType type = TransType.FTP;
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, type, rpt);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt, format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));		
		Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt1));		
	}
	
  /*
   * Schedule Mgr Case 0201 (P3): User can create an non-repeat event with one report, generate report in PDF and HTML format,and transport report via FTP.	
   */
	@Test
	public void testRptScheduleMgr0201(){		
		String rpt = "SDP_NMP.rptdesign";
		RptFormat[] format = new RptFormat[]{RptFormat.PDF, RptFormat.HTML};
		TransType[] type = new TransType[]{TransType.FTP};
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, type, rpt);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt, format[0], eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt1));
		Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt, format[1], eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
		Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt2));	
	}
	
	/*
	 * Schedule Mgr Case 0202 (P3): User can create an non-repeat event with one report, generate report in PDF format,and transport report via FTP.
	 */
	@Test
	public void testRptScheduleMgr0202(){
		String rpt = "SDP_NMP.rptdesign";
		RptFormat format = RptFormat.PDF;
		TransType type = TransType.FTP;
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, type, rpt);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt, format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));		
		Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt1));
	}
	
	/*
	 * Schedule Mgr Case 0300 (P1): User can create an non-repeat event with one report, generate report in HTML format,and transport report via HTTP.
	 */
	@Test
	public void testRptScheduleMgr0300(){
		String rpt = "SDP_NMP.rptdesign";
		RptFormat format = RptFormat.HTML;
		TransType type = TransType.HTTP;
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, type, rpt);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt, format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt1));
	}
	
	/*
	 * Schedule Mgr Case 0301 (P3): User can create an non-repeat event with one report, generate report in HTML and Excel format,and transport report via HTTP.
	 */
	@Test
	public void testRptScheduleMgr0301(){		
		String rpt = "SDP_NMP.rptdesign";
		RptFormat[] format = new RptFormat[]{RptFormat.EXCEL, RptFormat.HTML};
		TransType[] type = new TransType[]{TransType.HTTP};
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(desc, format, type, rpt);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt, format[0], eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt1));
		Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt, format[1], eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
		Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt2));
	}
	
	/*
	 * Schedule Mgr Case 0400 (P2): User can create an non-repeat event with three report, generate three report in Excel format,and transports report via FTP.
	 */
	@Test
	public void testRptScheduleMgr0400(){		
		String rpt[] = {"SDP_NMP.rptdesign","SDP_BUYS.rptdesign","SDP_USERS.rptdesign"};
		RptFormat format = RptFormat.EXCEL;
		TransType type = TransType.FTP;
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(desc, format, type, rpt);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt1));
		Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt[1], format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
		Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt2));
		Pattern exptRpt3 = sch.getExceptedGenRptPattern(rpt[2], format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt3));
		Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt3));
	}
	
	/*
	 * Schedule Mgr Case 0401 (P3): User can create an non-repeat event with three report, generate three report in PDF format,and transports report via HTTP.
	 */
	@Test
	public void testRptScheduleMgr0401(){
		String rpt[] = {"SDP_NMP.rptdesign","SDP_BUYS.rptdesign","SDP_USERS.rptdesign"};
		RptFormat format = RptFormat.PDF;
		TransType type = TransType.HTTP;
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(desc, format, type, rpt);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt1));
		Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt[1], format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
		Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt2));
		Pattern exptRpt3 = sch.getExceptedGenRptPattern(rpt[2], format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt3));
		Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt3));
	}
	
	/*
	 * User can create an non-repeat event with three report, generate three report in HTML format,and transports report via Email.
	 */
	@Test
	public void testRptScheduleMgr0402(){
		String rpt[] = {"SDP_NMP.rptdesign","SDP_BUYS.rptdesign","SDP_USERS.rptdesign"};
		RptFormat format = RptFormat.HTML;
		TransType type = TransType.EMAIL;
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(desc, format, type, rpt);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));		
		Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt[1], format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
		Pattern exptRpt3 = sch.getExceptedGenRptPattern(rpt[2], format, eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt3));		
		Assert.assertTrue(sch.verifyRptEmailReceived(desc));
	}
	
	/*
	 * Schedule Mgr Case 0500 (P2): User can create an non-repeat event with three reports, generate each report in different format,and transports report via HTTP.
	 */
	@Test
	public void testRptScheduleMgr0500(){
		String rpt[] = {"SDP_NMP.rptdesign","SDP_BUYS.rptdesign","SDP_USERS.rptdesign"};
		Map<String, RptFormat> map = new HashMap<String, RptFormat>();
		map.put(rpt[0], RptFormat.EXCEL);
		map.put(rpt[1],RptFormat.PDF);
		map.put(rpt[2],RptFormat.HTML);
		TransType type[] = {TransType.HTTP};
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(desc, map, type);
		sch.triggerEvent(eventTime);
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], map.get(rpt[0]), eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));		
		Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt[1], map.get(rpt[1]), eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
		Pattern exptRpt3 = sch.getExceptedGenRptPattern(rpt[2], map.get(rpt[2]), eventId, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt3));		
		Assert.assertTrue(sch.verifyRptEmailReceived(desc));
	}
	
	@Test
	/*
	 * Schedule Mgr Case 0600 (P1): User can create repeat every 1 day event with one report, generate report in PDF format,and transport report via Email.
	 */
	public void testRptScheduleMgr0600(){
		String rpt[] = {"SDP_NMP.rptdesign","SDP_BUYS.rptdesign","SDP_USERS.rptdesign"};
		RptFormat[] format = {RptFormat.PDF};
		TransType[] type = {TransType.EMAIL};
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(desc, format, type, repeat, startDate, rpt);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1, exptRpt2, exptRpt3;
		for(int i=0; i<3;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));		
			exptRpt2 = sch.getExceptedGenRptPattern(rpt[1], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
			exptRpt3 = sch.getExceptedGenRptPattern(rpt[2], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt3));		
			Assert.assertTrue(sch.verifyRptEmailReceived(desc));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}		
	}
	
	@Test
	/*
	 * Schedule Mgr Case 0700 (P2):User can create repeat every 15 day event with three reports, generate each report in different format,and transport report via FTP.
	 */
	public void testRptScheduleMgr0700(){
		String rpt[] = {"SDP_NMP.rptdesign","SDP_BUYS.rptdesign","SDP_USERS.rptdesign"};
		Map<String, RptFormat> map = new HashMap<String, RptFormat>();
		map.put(rpt[0], RptFormat.PDF);
		map.put(rpt[1],RptFormat.HTML);
		map.put(rpt[2],RptFormat.EXCEL);
		TransType[] type = {TransType.FTP};
		RepeatType repeat = new Daily(15);
		int repeatCount = 3;
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(desc, map, type, repeat, startDate, repeatCount);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1, exptRpt2, exptRpt3;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], map.get(rpt[0]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
			Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt1));
			exptRpt2 = sch.getExceptedGenRptPattern(rpt[1], map.get(rpt[1]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
			Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt2));
			exptRpt3 = sch.getExceptedGenRptPattern(rpt[2], map.get(rpt[2]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt3));		
			Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt3));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}		
	}
	
	@Test
	/*
	 * Schedule Mgr Case 0701 (P3):Repeat every 15 day event run correctly when cross year and transport report via Email.
	 */
	public void testRptScheduleMgr0701(){
		String rpt[] = {"SDP_NMP.rptdesign"};
		Map<String, RptFormat> map = new HashMap<String, RptFormat>();
		map.put(rpt[0], RptFormat.PDF);		
		TransType[] type = {TransType.EMAIL};
		RepeatType repeat = new Daily(15);
		int repeatCount = 3;
		startDate = Calendar.getInstance();
		startDate.set(Calendar.DAY_OF_MONTH, 24);
		startDate.set(Calendar.MONTH, Calendar.DECEMBER);
		startDate.add(Calendar.YEAR, 1);		
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(desc, map, type, repeat, startDate, repeatCount);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], map.get(rpt[0]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
			Assert.assertTrue(sch.verifyRptEmailReceived(desc));			
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}		
	}
	
	@Test
	/*
	 * Schedule Mgr Case 0800 (P3): User can create repeat everywork day event with three reports, generate each report in different format,and transport report via HTTP.
	 */
	public void testRptScheduleMgr0800(){
		String rpt[] = {"SDP_NMP.rptdesign","SDP_BUYS.rptdesign","SDP_USERS.rptdesign"};
		Map<String, RptFormat> map = new HashMap<String, RptFormat>();
		map.put(rpt[0], RptFormat.HTML);
		map.put(rpt[1],RptFormat.EXCEL);
		map.put(rpt[2],RptFormat.PDF);
		TransType[] type = {TransType.HTTP};
		RepeatType repeat = new Daily();
		int repeatCount = 5;
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(desc, map, type, repeat, startDate, repeatCount);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1, exptRpt2, exptRpt3;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], map.get(rpt[0]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
			Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt1));
			exptRpt2 = sch.getExceptedGenRptPattern(rpt[1], map.get(rpt[1]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
			Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt2));
			exptRpt3 = sch.getExceptedGenRptPattern(rpt[2], map.get(rpt[2]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt3));		
			Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt3));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 0900 (P1):User can create repeat every Friday event with three reports, generate each report in different format,and transport report via Email and FTP.
	 */
	public void testRptScheduleMgr0900(){
		String rpt[] = {"SDP_NMP.rptdesign","SDP_BUYS.rptdesign","SDP_USERS.rptdesign"};
		Map<String, RptFormat> map = new HashMap<String, RptFormat>();
		map.put(rpt[0], RptFormat.HTML);
		map.put(rpt[1],RptFormat.PDF);
		map.put(rpt[2],RptFormat.PDF);
		TransType[] type = {TransType.EMAIL,TransType.FTP};
		RepeatType repeat = new Weekly(1,new String[]{"Friday"});
		int repeatCount = 3;
		startDate = Calendar.getInstance();		
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, map, type, repeat, startDate, repeatCount);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1, exptRpt2, exptRpt3;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], map.get(rpt[0]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
			Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt1));
			exptRpt2 = sch.getExceptedGenRptPattern(rpt[1], map.get(rpt[1]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
			Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt2));
			exptRpt3 = sch.getExceptedGenRptPattern(rpt[2], map.get(rpt[2]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt3));		
			Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt3));
			Assert.assertTrue(sch.verifyRptEmailReceived(desc));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1000 (P3): User can create repeat Monday and Saturday every 1 week event with three reports, generate each report in different format,and transport report via Email and HTTP.
	 */
	public void testRptScheduleMgr1000(){
		String rpt[] = {"SDP_NMP.rptdesign","SDP_BUYS.rptdesign","SDP_USERS.rptdesign"};
		Map<String, RptFormat> map = new HashMap<String, RptFormat>();
		map.put(rpt[0], RptFormat.HTML);
		map.put(rpt[1],RptFormat.HTML);
		map.put(rpt[2],RptFormat.EXCEL);
		TransType[] type = {TransType.EMAIL,TransType.HTTP};
		RepeatType repeat = new Weekly(1, new String[]{"Monday","Saturday"});
		int repeatCount = 6;
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, map, type, repeat, startDate, repeatCount);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1, exptRpt2, exptRpt3;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], map.get(rpt[0]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
			Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt1));
			exptRpt2 = sch.getExceptedGenRptPattern(rpt[1], map.get(rpt[1]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
			Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt2));
			exptRpt3 = sch.getExceptedGenRptPattern(rpt[2], map.get(rpt[2]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt3));		
			Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt3));
			Assert.assertTrue(sch.verifyRptEmailReceived(desc));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1100 (P2): User can create repeat on Sunday every 2 weeks event with three reports, generate each report in PDF format,and transport report via FTP and HTTP.
	 */
	public void testRptScheduleMgr1100(){
		String rpt[] = {"SDP_BUYS.rptdesign"};
		Map<String, RptFormat> map = new HashMap<String, RptFormat>();
		map.put(rpt[0], RptFormat.PDF);		
		TransType[] type = {TransType.FTP,TransType.HTTP};
		RepeatType repeat = new Weekly(2, new String[]{"Sunday"});
		int repeatCount = 3;
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, map, type, repeat, startDate, repeatCount);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], map.get(rpt[0]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
			Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt1));
			Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt1));			
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1101 (P3): Repeat on Wednesday every 2 weeks event with one report run correctly when cross year, generate each report in PDF format,and transport report via Email,FTP and HTTP.
	 */
	public void testRptScheduleMgr1101(){
		String rpt[] = {"SDP_BUYS.rptdesign"};
		Map<String, RptFormat> map = new HashMap<String, RptFormat>();
		map.put(rpt[0], RptFormat.PDF);		
		TransType[] type = {TransType.EMAIL,TransType.FTP,TransType.HTTP};
		RepeatType repeat = new Weekly(2, new String[]{"Wednesday"});
		int repeatCount = 3;
		startDate = Calendar.getInstance();		
		startDate.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
		startDate.set(Calendar.WEEK_OF_YEAR,51);
		startDate.add(Calendar.YEAR, 1);
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, map, type, repeat, startDate, repeatCount);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], map.get(rpt[0]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
			Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt1));
			Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt1));
			Assert.assertTrue(sch.verifyRptEmailReceived(desc));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1200 (P3): User can create repeat on Tuesday and Thursday every 2 weeks event with three reports, generate each report in Excel format,don't transport report.
	 */
	public void testRptScheduleMgr1200(){
		String rpt[] = {"SDP_NMP.rptdesign","SDP_BUYS.rptdesign","SDP_USERS.rptdesign"};
		Map<String, RptFormat> map = new HashMap<String, RptFormat>();
		map.put(rpt[0], RptFormat.EXCEL);
		map.put(rpt[1], RptFormat.EXCEL);
		map.put(rpt[2], RptFormat.EXCEL);
		TransType[] type = null;
		RepeatType repeat = new Weekly(2, new String[]{"Tuesday","Thursday"});
		int repeatCount = 6;
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, map, type, repeat, startDate, repeatCount);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1, exptRpt2, exptRpt3;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], map.get(rpt[0]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));			
			exptRpt2 = sch.getExceptedGenRptPattern(rpt[1], map.get(rpt[1]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));			
			exptRpt3 = sch.getExceptedGenRptPattern(rpt[2], map.get(rpt[2]), eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt3));				
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	
	@Test
	/*
	 * ScheduleMgrCase1300(P1):User can create repeat 1st day every month event, generate the report in three formats,transport report via Email but override default destination email address.
	 */
	public void testRptScheduleMgr1300(){
		TransportConfTasks transConf = new TransportConfTasks();
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setDesEmail("abc@nagra.com");
		String rpt[] = {"SDP_NMP.rptdesign"};
		RptFormat format[] = {RptFormat.PDF};
		RptTransSetting[] rptTrans = {new EmailSetting()};
		RepeatType repeat = new Monthly(1);
		int repeatCount = 3;
		startDate = Calendar.getInstance();		
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, rptTrans, repeat, startDate, rpt);
		logger.info("Restore report transport configure default settings");
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setDesEmail();
		logger.info("Verification:");
		eventId = RptScheduleMgrTasks.getEventID(desc);	
		Pattern exptRpt1;		
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));						
			Assert.assertTrue(sch.verifyRptEmailReceived(desc));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1301 (P3): User can create repeat 28th day every month event, transport report via Email but default destination email address is blank.
	 */
	public void testRptScheduleMgr1301(){
		TransportConfTasks transConf = new TransportConfTasks();
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.resetDesEmail();
		String rpt[] = {"SDP_NMP.rptdesign"};
		RptFormat format[] = {RptFormat.PDF};
		RptTransSetting[] rptTrans = {new EmailSetting()};
		RepeatType repeat = new Monthly(28);
		int repeatCount = 3;
		startDate = Calendar.getInstance();	
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, rptTrans, repeat, startDate, rpt);
		logger.info("Restore report transport configure default settings");
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setDesEmail();
		logger.info("Verification:");
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));						
			Assert.assertTrue(sch.verifyRptEmailReceived(desc));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}	
	
	@Test
	/*
	 * Schedule Mgr Case 1302 (P3): Repeat 1st day every month event run correctly when cross year, transport report via FTP but default FTP settings is blank.
	 */
	public void testRptScheduleMgr1302(){
		TransportConfTasks transConf = new TransportConfTasks();
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.resetFtpSetting();
		String rpt[] = {"SDP_NMP.rptdesign"};
		RptFormat format[] = {RptFormat.PDF};
		RptTransSetting[] rptTrans = {new FtpSetting()};
		RepeatType repeat = new Monthly(1);
		int repeatCount = 3;
		startDate = Calendar.getInstance();
		startDate.set(Calendar.MONTH, Calendar.NOVEMBER);
		startDate.add(Calendar.YEAR, 1);
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		Calendar eventTime = sch.createEvent(desc, format, rptTrans, repeat, startDate, rpt);
		logger.info("Restore report transport configure default settings");
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setFtpSetting();
		logger.info("Verification:");
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));						
			Assert.assertTrue(sch.verifyRptFtpUpload(rptTrans[0],exptRpt1));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1400 (P2): User can create repeat on 1st Monday every month event, transport report via FTP but override default FTP Settings.
	 */
	public void testRptScheduleMgr1400(){
		TransportConfTasks transConf = new TransportConfTasks();
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setFtpSetting();
		String rpt[] = {"SDP_NMP.rptdesign"};
		RptFormat format[] = {RptFormat.PDF};
		RptTransSetting[] rptTrans = {new FtpSetting("172.22.2.93", "/","bsm", "bsm")};
		RepeatType repeat = new Monthly(1,"Monday");
		int repeatCount = 3;
		startDate = Calendar.getInstance();	
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, rptTrans, repeat, startDate, rpt);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));						
			Assert.assertTrue(sch.verifyRptFtpUpload(rptTrans[0],exptRpt1));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1500 (P3): User can create repeat on fourth Thursday every month event, transport report via HTTP but override default HTTP Settings.
	 */
	public void testRptScheduleMgr1500(){
		TransportConfTasks transConf = new TransportConfTasks();
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setHttpSetting("http://testwebdav");
		String rpt[] = {"SDP_NMP.rptdesign"};
		RptFormat format[] = {RptFormat.PDF};
		RptTransSetting[] rptTrans = {new HttpSetting()};
		RepeatType repeat = new Monthly(4,"Thursday");
		int repeatCount = 3;
		startDate = Calendar.getInstance();	
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, rptTrans, repeat, startDate, rpt);
		logger.info("Restore report transport configure default settings");
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setHttpSetting();
		logger.info("Verification:");
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));						
			Assert.assertTrue(sch.verifyRptHttpUpload(rptTrans[0],exptRpt1));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1501 (P3): Repeat fourth Thursday every month event run correctly when cross year, transport report via HTTP but default HTTP settings is blank.
	 */
	public void testRptScheduleMgr1501(){
		TransportConfTasks transConf = new TransportConfTasks();
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.resetHttpSetting();
		String rpt[] = {"SDP_NMP.rptdesign"};
		RptFormat format[] = {RptFormat.PDF};
		RptTransSetting[] rptTrans = {new HttpSetting()};
		RepeatType repeat = new Monthly(4,"Thursday");
		int repeatCount = 3;
		startDate = Calendar.getInstance();
		startDate.set(Calendar.MONTH,Calendar.NOVEMBER);
		startDate.add(Calendar.YEAR, 1);
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, rptTrans, repeat, startDate, rpt);
		logger.info("Restore report transport configure default settings");
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setHttpSetting();
		logger.info("Verification:");
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));						
			Assert.assertTrue(sch.verifyRptHttpUpload(rptTrans[0],exptRpt1));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1600 (P1): User can create repeat on 1st of January every year event, transport report via FTP anonymously.
	 */
	public void testRptScheduleMgr1600(){	
		String rpt[] = {"SDP_NMP.rptdesign"};
		RptFormat format[] = {RptFormat.PDF};
		RptTransSetting[] rptTrans = {new FtpSetting("172.22.2.93","/report")};
		RepeatType repeat = new Yearly(1,"January");
		int repeatCount = 3;
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, rptTrans, repeat, startDate, rpt);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));						
			Assert.assertTrue(sch.verifyRptFtpUpload(rptTrans[0],exptRpt1));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1601 (P3): User can create repeat on 25th of December every year event, transport report via FTP anonymously.
	 */
	public void testRptScheduleMgr1601(){		
		String rpt[] = {"SDP_NMP.rptdesign"};
		RptFormat format[] = {RptFormat.PDF};
		RptTransSetting[] rptTrans = {new FtpSetting("172.22.2.93","/report")};
		RepeatType repeat = new Yearly(25,"December");
		int repeatCount = 3;
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, rptTrans, repeat, startDate, rpt);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));						
			Assert.assertTrue(sch.verifyRptFtpUpload(rptTrans[0],exptRpt1));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1700 (P2): User can create repeat on 1st Monday of January every year event, transport report via HTTP anonymously.
	 */
	
	public void testRptScheduleMgr1700(){		
		String rpt[] = {"SDP_NMP.rptdesign"};
		RptFormat format[] = {RptFormat.PDF};
		RptTransSetting[] rptTrans = {new HttpSetting("http://vm81653/webdav/report/")};
		RepeatType repeat = new Yearly(1,"Monday","January");
		int repeatCount = 3;
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, rptTrans, repeat, startDate, rpt);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));						
			Assert.assertTrue(sch.verifyRptHttpUpload(rptTrans[0], exptRpt1));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1701 (P3): User can create repeat on 4th Thursday of November every year event, transport report via HTTP anonymously.
	 */
	public void testRptScheduleMgr1701(){		
		String rpt[] = {"SDP_NMP.rptdesign"};
		RptFormat format[] = {RptFormat.PDF};
		RptTransSetting[] rptTrans = {new HttpSetting("http://vm81653/webdav/report/")};
		RepeatType repeat = new Yearly(4,"Thursday","November");
		int repeatCount = 3;
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);				
		eventTime = sch.createEvent(desc, format, rptTrans, repeat, startDate, rpt);		
		eventId = RptScheduleMgrTasks.getEventID(desc);		
		Pattern exptRpt1;
		for(int i=0; i<repeatCount;i++){
			sch.triggerEvent(eventTime);
			exptRpt1 = sch.getExceptedGenRptPattern(rpt[0], format[0], eventId, eventTime);
			Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));						
			Assert.assertTrue(sch.verifyRptHttpUpload(rptTrans[0], exptRpt1));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		}
	} 
	
	@Test
	/*
	 * Schedule Mgr Case 1800 (P1): Two event with same reports can simultaneously generate report in different format,and transports report via HTTP.
	 */
	public void testRptScheduleMgr1800(){
		TransportConfTasks transConf = new TransportConfTasks();
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setHttpSetting();
		String rpt = "SDP_NMP.rptdesign";
		RptFormat format = RptFormat.PDF;
		TransType type = TransType.HTTP;
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);
		String evtdesc1 = desc + "_a";
		eventTime = sch.createEvent(evtdesc1, format, type, rpt);
		String evtdesc2 = desc + "_b";
		sch.createEvent(evtdesc2, format, type, rpt);
		sch.triggerEvent(eventTime);
		String eventId1 = RptScheduleMgrTasks.getEventID(evtdesc1);
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt, format, eventId1, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt1));
		String eventId2 = RptScheduleMgrTasks.getEventID(evtdesc2);
		Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt, format, eventId2, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
		Assert.assertTrue(sch.verifyRptHttpUpload(exptRpt2));
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1801 (P3): Two event with different reports can simultaneously generate report in same format,and transports report via FTP.
	 */
	public void testRptScheduleMgr1801(){
		TransportConfTasks transConf = new TransportConfTasks();
		transConf.gotoRptTransConf(rptCenterPg);
		transConf.setFtpSetting();
		String rpt1 = "SDP_NMP.rptdesign";
		RptFormat format1 = RptFormat.PDF;
		String evtdesc1 = desc + "_a";
		String rpt2 = "SDP_BUYS.rptdesign";
		RptFormat format2 = RptFormat.EXCEL;
		TransType type = TransType.FTP;
		String evtdesc2 = desc + "_b";
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		eventTime = sch.createEvent(evtdesc1, format1, type, rpt1);		
		sch.createEvent(evtdesc2, format2, type, rpt2);
		sch.triggerEvent(eventTime);
		String eventId1 = RptScheduleMgrTasks.getEventID(evtdesc1);
		Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt1, format1, eventId1, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
		Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt1));
		String eventId2 = RptScheduleMgrTasks.getEventID(evtdesc2);
		Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt2, format2, eventId2, eventTime);
		Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
		Assert.assertTrue(sch.verifyRptFtpUpload(exptRpt2));
	}
	
	@Test
	/*
	 * Schedule Mgr Case 1900 (P2): Two repeat event that time duration has overlap can run isolate.
	 */
	public void testRptScheduleMgr1900(){
		String[] rpt1 = {"SDP_NMP.rptdesign"};
		RptFormat[] format1 = {RptFormat.PDF};
		String evtdesc1 = desc + "_a";
		RepeatType repeat1 = new Daily(10);
		String[] rpt2 = {"SDP_BUYS.rptdesign"};
		RptFormat[] format2 = {RptFormat.EXCEL};
		TransType[] type = null;
		String evtdesc2 = desc + "_b";
		RepeatType repeat2 = new Weekly(1,new String[]{"Friday"});
		startDate = Calendar.getInstance();
		RptScheduleMgrTasks sch = new RptScheduleMgrTasks();		
		sch.gotoRptScheduleManager(rptCenterPg);		
		Calendar eventTime1 = sch.createEvent(evtdesc1, format1, type, repeat1, startDate,rpt1);		
		Calendar eventTime2 = sch.createEvent(evtdesc2, format2, type, repeat2, startDate,rpt2);
		String eventId1 = RptScheduleMgrTasks.getEventID(evtdesc1);
		String eventId2 = RptScheduleMgrTasks.getEventID(evtdesc2);
		int eventOcc1 = 0;
		int eventOcc2 = 0;
		int i = 0;
		while(i<6){
			i++;
			if (eventTime1.after(eventTime2)||eventOcc2>=3){
				sch.triggerEvent(eventTime1);
				eventOcc1++;
				Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt1[0], format1[0], eventId1, eventTime1);
				Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
				eventTime1 = DateUtil.getNextOccurDate(eventTime1, repeat1);
			}else if (eventTime2.after(eventTime1)||eventOcc1>=3){
				sch.triggerEvent(eventTime2);
				eventOcc2++;
				Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt2[0], format2[0], eventId2, eventTime2);
				Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
				eventTime2 = DateUtil.getNextOccurDate(eventTime2, repeat2);
			}else{
				sch.triggerEvent(eventTime1);
				eventOcc1++;
				eventOcc2++;
				Pattern exptRpt1 = sch.getExceptedGenRptPattern(rpt1[0], format1[0], eventId1, eventTime1);
				Assert.assertTrue(sch.verifyRptGenerated(exptRpt1));
				eventTime1 = DateUtil.getNextOccurDate(eventTime1, repeat1);
				Pattern exptRpt2 = sch.getExceptedGenRptPattern(rpt2[0], format2[0], eventId2, eventTime2);
				Assert.assertTrue(sch.verifyRptGenerated(exptRpt2));
				eventTime2 = DateUtil.getNextOccurDate(eventTime2, repeat2);				
			}			
		}		
	}
}
