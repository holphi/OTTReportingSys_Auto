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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import com.nagra.bsm.tasks.BrowserTasks;
import com.nagra.bsm.tasks.ExtraScheduleMgrTasks;
import com.nagra.bsm.tasks.ExtractionMgrTasks;
import com.nagra.bsm.tasks.LoginTasks;
import com.nagra.bsm.tasks.RptScheduleMgrTasks;
import com.nagra.bsm.ui.Daily;
import com.nagra.bsm.ui.Monthly;
import com.nagra.bsm.ui.RepeatType;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.RptFormat;
import com.nagra.bsm.ui.TransType;
import com.nagra.bsm.ui.Weekly;
import com.nagra.bsm.ui.Yearly;
import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.util.DBUtil;
import com.nagra.bsm.util.DateUtil;
import com.nagra.bsm.util.SshUtil;

/**
 * @author tetang
 *
 */
public class ExtraScheduleMgrTest extends BaseTests{
	private static String testDataDir = CommonUtil.getCurrDir()+"\\testdata\\Transform\\";
	BrowserTasks br = new BrowserTasks();
	LoginTasks lg = new LoginTasks();
	RptCenterPage rptCenterPg;
	String desc = null;
	StringBuffer description = new StringBuffer("ExE");
	String eventId = null;
	Calendar startDate = null;
	Calendar eventTime = null; 
	
	@Rule
	public Timeout globalTimeout = new Timeout(600000);
	
	@BeforeClass
	public static void suiteSetup(){
		BrowserTasks br = new BrowserTasks();
		br.launchBrowser();
		LoginTasks lg = new LoginTasks();
		lg.loginPage = br.getURL();
		RptCenterPage rptCenterPg;
		rptCenterPg = lg.login();
		ExtractionMgrTasks extTasks = new ExtractionMgrTasks();
		extTasks.gotoExtractionManager(rptCenterPg);
		String[] tfms = {"TransformOraToOra.ktr","TransformOraToOra2.ktr","TransformOraToOra3.ktr","TransformOraToMongo.ktr"};
		for(String t:tfms){
			if(!extTasks.isTfmExisted(t)){
				extTasks.addTransform(testDataDir, t);
			}
		}				
		br.closeBrowser();
		ExtraScheduleMgrTasks.restoreOraSrcTable();
		ExtraScheduleMgrTasks.startMongoService();
	}
	
	@Before
	public void testSetup(){
		logger.info("\n============================="+testName.getMethodName()+"==============================");		
		CommonUtil.syncServerTimeWithClient();		
		description.append(testName.getMethodName().substring("testTransformScheduleMgr".length()));
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
		ExtraScheduleMgrTasks.cleanEventDB();
		logger.info("remove event");
	}
	
	@AfterClass
	public static void suiteTeardown(){
		String sql = "delete from cor_device_test_src where DEV_SPID = 2";
		DBUtil.executeSqlInOra(sql);
	}
	
	@Test
	public void testTransformScheduleMgr0100(){
		String tfm = "TransformOraToOra.ktr";
		String srcTable = "cor_device_test_src";
		String desTable = "cor_device_test_des";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		//clean up
		tasks.cleanOraTable(desTable);
		//test
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, tfm);
		tasks.triggerEvent(eventTime);		
		Assert.assertTrue(tasks.verifyTransformToOracleSucceed(srcTable, desTable));
	}
	
	@Test
	public void testTransformScheduleMgr0200(){
		String[] tfm = {"TransformOraToOra.ktr","TransformOraToOra2.ktr","TransformOraToOra3.ktr"};
		String srcTable = "cor_device_test_src";
		String desTable1 = "cor_device_test_des";
		String desTable2 = "cor_device_test_des2";
		String desTable3 = "cor_device_test_des3";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		//clean up
		tasks.cleanOraTable(desTable1);
		tasks.cleanOraTable(desTable2);
		tasks.cleanOraTable(desTable3);
		//test
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, tfm);
		tasks.triggerEvent(eventTime);		
		Assert.assertTrue(tasks.verifyTransformToOracleSucceed(srcTable, desTable1));
		Assert.assertTrue(tasks.verifyTransformToOracleSucceed(srcTable, desTable2));
		Assert.assertTrue(tasks.verifyTransformToOracleSucceed(srcTable, desTable3));
	}
	
	@Test
	public void testTransformScheduleMgr0300(){
		String[] tfm = {"TransformOraToOra.ktr","TransformOraToOra2.ktr","TransformOraToOra3.ktr"};
		String srcTable = "cor_device_test_src";
		String desTable1 = "cor_device_test_des";
		String desTable2 = "cor_device_test_des2";
		String desTable3 = "cor_device_test_des3";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		//clean up
		tasks.cleanOraTable(desTable1);
		tasks.cleanOraTable(desTable2);
		tasks.cleanOraTable(desTable3);
		//test
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.selectTransforms(tfm);
		tasks.unselectedTransform(tfm[0],tfm[1]);
		Assert.assertTrue(tasks.verifyTransformIsSelected(tfm[2]));
		Assert.assertFalse(tasks.verifyTransformIsSelected(tfm[0]));
		Assert.assertFalse(tasks.verifyTransformIsSelected(tfm[1]));
		tasks.setEventDesc(desc);
		eventTime = tasks.setEventStartTime();
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);		
		Assert.assertTrue(tasks.verifyTransformToOracleSucceed(srcTable, desTable3));
	}
	
	@Test
	public void testTransformScheduleMgr0400(){
		String tfm = "TransformOraToOra.ktr";
		String srcTable = "cor_device_test_src";
		String desTable = "cor_device_test_des";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		//clean up
		tasks.cleanOraTable(desTable);
		//test
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.selectTransforms(tfm);
		tasks.setEventDesc(desc);
		eventTime = tasks.setEventStartTime();
		tasks.cancelEdit();
		Assert.assertFalse(tasks.verifyExtEventExistsInDB(desc));
	}
	
	@Test //User can create repeat every 1 day event with one transform
	public void testTransformScheduleMgr0500(){
		String tfm = "TransformOraToOra.ktr";
		String srcTable = "cor_device_test_src";
		String desTable = "cor_device_test_des";
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();	
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);
		for(int i = 0; i<3; i++){
			tasks.cleanOraTable(desTable);
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyTransformToOracleSucceed(srcTable, desTable));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}		
	}
	
	@Test //User can create repeat every 15 days event with three transform
	public void testTransformScheduleMgr0600() {
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Daily(15);
		startDate = Calendar.getInstance();
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //Repeat every 15 day event with one transform run correctly when cross year .
	public void testTransformScheduleMgr0700(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Daily(15);
		startDate = Calendar.getInstance();
		startDate.set(Calendar.DAY_OF_MONTH, 24);
		startDate.set(Calendar.MONTH, Calendar.DECEMBER);
		startDate.add(Calendar.YEAR, 1);
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create repeat every workday event with three transform.
	public void testTransformScheduleMgr0800(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Daily();
		startDate = Calendar.getInstance();		
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 5; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create event repeated on Friday every 1 week with three transform.
	public void testTransformScheduleMgr0900(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Weekly(1,new String[]{"Friday"});
		startDate = Calendar.getInstance();		
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create event repeated Monday and Saturday every 1 week with three transform.
	public void testTransformScheduleMgr1000(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Weekly(1, new String[]{"Monday","Saturday"});
		startDate = Calendar.getInstance();		
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 6; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create event repeated on Sunday every 2 weeks three transform
	public void testTransformScheduleMgr1100(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Weekly(2, new String[]{"Sunday"});
		startDate = Calendar.getInstance();		
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //Event repeated on Wednesday every 2 weeks with three transform run correctly when cross year.
	public void testTransformScheduleMgr1200(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Weekly(2, new String[]{"Wednesday"});
		startDate = Calendar.getInstance();		
		startDate.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
		startDate.set(Calendar.WEEK_OF_YEAR,51);
		startDate.add(Calendar.YEAR, 1);	
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create event repeated 1st day every month with two transform.
	public void testTransformScheduleMgr1300(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Monthly(1);
		startDate = Calendar.getInstance();					
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create event repeated on 1st Monday every month with four transform.
	public void testTransformScheduleMgr1400(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Monthly(1,"Monday");
		startDate = Calendar.getInstance();					
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //Event repeated fourth Thursday every month run correctly when cross year.
	public void testTransformScheduleMgr1500(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Monthly(4,"Thursday");
		startDate = Calendar.getInstance();	
		startDate.set(Calendar.MONTH,Calendar.NOVEMBER);
		startDate.add(Calendar.YEAR, 1);
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create event repeated on 1st of January every year.
	public void testTransformScheduleMgr1600(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Yearly(1,"January");
		startDate = Calendar.getInstance();	
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create event repeated on 16th of July every year.
	public void testTransformScheduleMgr1700(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Yearly(16,"July");
		startDate = Calendar.getInstance();	
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create event repeated on 31th of December every year
	public void testTransformScheduleMgr1800(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Yearly(31,"December");
		startDate = Calendar.getInstance();	
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create event repeated on 1st Monday of January every year.
	public void testTransformScheduleMgr1900(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Yearly(1,"Monday","January");
		startDate = Calendar.getInstance();	
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create event repeated on on third Tuesday of May every year.
	public void testTransformScheduleMgr2000(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Yearly(3,"Tuesday","May");
		startDate = Calendar.getInstance();	
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can create an non-repeat event on Week View
	public void testTransformScheduleMgr2100(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.goToWeekView();
		tasks.openNewEventWindowWeekView();
		tasks.selectTransforms(tfm);
		tasks.setEventDesc(desc);
		eventTime = tasks.setEventStartTime();
		tasks.saveEvent();				
		tasks.triggerEvent(eventTime);
		Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));		
	}
	
	@Test //User can create an repeat event on Week View
	public void testTransformScheduleMgr2101(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.goToWeekView();
		tasks.openNewEventWindowWeekView();
		tasks.selectTransforms(tfm);
		tasks.setEventDesc(desc);
		RepeatType repeat = new Daily(1);
		tasks.setRepeatRule(repeat);
		eventTime = tasks.setEventStartTime();
		tasks.saveEvent();
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}	
	}
	
	@Test //User can create an non-repeat event on Day View
	public void testTransformScheduleMgr2200(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.goToDayView();
		tasks.openNewEventWindowDayView();
		tasks.selectTransforms(tfm);
		tasks.setEventDesc(desc);
		eventTime = tasks.setEventStartTime();
		tasks.saveEvent();				
		tasks.triggerEvent(eventTime);
		Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));		
	}
	
	@Test //User can delete a non-repeat event in month view.
	public void testTransformScheduleMgr2300(){
		String tfm = "TransformOraToMongo.ktr";					
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.createEvent(desc, tfm);
		tasks.openEvent(desc);
		tasks.deleteEvent();
		String msg = tasks.confirmAlert();
		Assert.assertFalse(tasks.verifyExtEventExistsInDB(desc));
		Assert.assertEquals("Event will be deleted permanently, are you sure?", msg);
	}
	
	@Test //Event will not be delete if cancel to delete
	public void testTransformScheduleMgr2400(){
		String tfm = "TransformOraToMongo.ktr";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.createEvent(desc, tfm);
		tasks.openEvent(desc);
		tasks.deleteEvent();
		String msg = tasks.cancelAlert();
		Assert.assertTrue(tasks.verifyExtEventExistsInDB(desc));
		Assert.assertEquals("Event will be deleted permanently, are you sure?", msg);
	}
	
	@Test//User can delete a non-repeat event in week view.
	public void testTransformScheduleMgr2500(){
		String tfm = "TransformOraToMongo.ktr";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, tfm);
		tasks.goToWeekView();
		tasks.scrollTo(eventTime);
		tasks.openEventWeekView(desc);		
		tasks.deleteEvent();
		String msg = tasks.confirmAlert();
		Assert.assertFalse(tasks.verifyExtEventExistsInDB(desc));
		Assert.assertEquals("Event will be deleted permanently, are you sure?", msg);
	}
	
	@Test //User can delete a repeat event in week view.
	public void testTransformScheduleMgr2501(){
		String tfm = "TransformOraToMongo.ktr";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc,new Daily(1), Calendar.getInstance(), tfm);
		tasks.goToWeekView();
		tasks.scrollTo(eventTime);
		tasks.openEventWeekView(desc);		
		tasks.deleteEvent();
		String msg = tasks.confirmAlert();
		Assert.assertFalse(tasks.verifyExtEventExistsInDB(desc));
		Assert.assertEquals("Event will be deleted permanently, are you sure?", msg);
	}
	
	@Test //User can delete a non-repeat event in day view.
	public void testTransformScheduleMgr2600(){
		String tfm = "TransformOraToMongo.ktr";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, tfm);
		tasks.goToDayView();
		tasks.scrollTo(eventTime);
		tasks.openEventWeekView(desc);		
		tasks.deleteEvent();
		String msg = tasks.confirmAlert();
		Assert.assertFalse(tasks.verifyExtEventExistsInDB(desc));
		Assert.assertEquals("Event will be deleted permanently, are you sure?", msg);
	}
	
	@Test //User can delete a repeat event in Day view.
	public void testTransformScheduleMgr2601(){
		String tfm = "TransformOraToMongo.ktr";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc,new Daily(1), Calendar.getInstance(), tfm);
		tasks.goToDayView();
		tasks.scrollTo(eventTime);
		tasks.openEventWeekView(desc);		
		tasks.deleteEvent();
		String msg = tasks.confirmAlert();
		Assert.assertFalse(tasks.verifyExtEventExistsInDB(desc));
		Assert.assertEquals("Event will be deleted permanently, are you sure?", msg);
	}
	
	@Test //User can edit transform selected in event.
	public void testTransformScheduleMgr2700(){
		String tfm = "TransformOraToMongo.ktr";
		String tfm2 = "TransformOraToOra.ktr";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, tfm);
		tasks.openEvent(desc);
		tasks.unselectedTransform(tfm);		
		tasks.selectTransforms(tfm2);				
		tasks.saveEvent();
		tasks.openEvent(desc);
		Assert.assertTrue(tasks.verifyTransformIsSelected(tfm2));
		Assert.assertFalse(tasks.verifyTransformIsSelected(tfm));		
	}
	
	@Test // User can change event time period when edit event
	public void testTransformScheduleMgr2800(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, tfm);
		tasks.openEvent(desc);				
		eventTime.add(Calendar.MINUTE, 5);
		eventTime.set(Calendar.SECOND, 10);
		tasks.setEventStartDateTime(eventTime);
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));		
	}
	
	@Test //User can change non-repeat event to repeat event when edit event.
	public void testTransformScheduleMgr2900(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, tfm);
		tasks.openEvent(desc);
		RepeatType repeat = new Daily(1);
		tasks.setRepeatRule(repeat);
		tasks.saveEvent();
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}	
	}
	
	@Test //User can change repeat event to non-repeat event when edit event.
	public void testTransformScheduleMgr3000(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		tasks.goToExtraScheduleMgr(rptCenterPg);
		RepeatType repeat = new Daily(1);
		eventTime = tasks.createEvent(desc,repeat,Calendar.getInstance(), tfm);
		tasks.openEvent(desc);
		tasks.disableRepeat();
		tasks.saveEvent();
		tasks.triggerEvent(eventTime);
		Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
		eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		index = index +1;								
		tasks.inserRowInOracle(sql,index);
		tasks.triggerEvent(eventTime);
		Assert.assertFalse(tasks.verifyRecordCountInMongo(srcTable, desCollection));		
	}
	
	@Test //User can change repeat rule when edit event.
	public void testTransformScheduleMgr3100(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		tasks.goToExtraScheduleMgr(rptCenterPg);
		RepeatType repeat = new Daily(1);
		eventTime = tasks.createEvent(desc,repeat,Calendar.getInstance(), tfm);
		repeat = new Weekly(1,new String[]{"Friday"});
		tasks.openEvent(desc);
		tasks.setRepeatRule(repeat);
		tasks.saveEvent();
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
	
	@Test //User can edit a non-repeat event in week view.
	public void testTransformScheduleMgr3200(){
		String tfm = "TransformOraToMongo.ktr";	
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, tfm);
		tasks.goToWeekView();
		tasks.scrollTo(eventTime);
		tasks.openEventWeekView(desc);
		desc=desc + "Update";
		tasks.setEventDesc(desc);
		tasks.saveEvent();		
		Assert.assertTrue(tasks.verifyExtEventExistsInDB(desc));
		tasks.triggerEvent(eventTime);
		Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
	}
	
	@Test //User can edit a repeat event in week view.
	public void testTransformScheduleMgr3201(){
		String tfm = "TransformOraToMongo.ktr";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc,new Daily(1), Calendar.getInstance(), tfm);
		tasks.goToWeekView();
		tasks.scrollTo(eventTime);
		tasks.openEventWeekView(desc);
		desc=desc + "Update";
		tasks.setEventDesc(desc);
		tasks.saveEvent();		
		Assert.assertTrue(tasks.verifyExtEventExistsInDB(desc));		
	}
	
	@Test // User can edit a non-repeat event in day view
	public void testTransformScheduleMgr3300(){
		String tfm = "TransformOraToMongo.ktr";	
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, tfm);
		tasks.goToDayView();
		tasks.scrollTo(eventTime);
		tasks.openEventWeekView(desc);
		desc=desc + "Update";
		tasks.setEventDesc(desc);
		tasks.saveEvent();		
		Assert.assertTrue(tasks.verifyExtEventExistsInDB(desc));
		tasks.triggerEvent(eventTime);
		Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
	}
	
	@Test //User can edit a repeat event in week view.
	public void testTransformScheduleMgr3301(){
		String tfm = "TransformOraToMongo.ktr";			
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc,new Daily(1), Calendar.getInstance(), tfm);
		tasks.goToDayView();
		tasks.scrollTo(eventTime);
		tasks.openEventWeekView(desc);
		desc=desc + "Update";
		tasks.setEventDesc(desc);
		tasks.saveEvent();		
		Assert.assertTrue(tasks.verifyExtEventExistsInDB(desc));		
	}
	
	@Test //When user edit event, if cancel, change will not be save.
	public void testTransformScheduleMgr3400(){
		String tfm = "TransformOraToMongo.ktr";		
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, tfm);
		tasks.openEvent(desc);		
		tasks.setEventDesc(desc + "Update");
		tasks.cancelEdit();		
		Assert.assertTrue(tasks.verifyExtEventExistsInDB(desc));	
	}
	
	@Test //An event at least has one transform.
	public void testTransformScheduleMgr3500(){
		String tfm = "TransformOraToMongo.ktr";		
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.setEventDesc(desc);
		tasks.setEventStartTime();
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals("Please select a transform.", msg);
	}
	
	@Test //Event start time must be later than current time.
	public void testTransformScheduleMgr3600(){
		String tfm = "TransformOraToMongo.ktr";		
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.selectTransforms(tfm);
		tasks.setEventDesc(desc);
		startDate = Calendar.getInstance();
		startDate.add(Calendar.MONTH, -1);
		tasks.setEventStartDateTime(startDate);
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals("The start date you entered occurs before current date", msg);
	}
	
	@Test //Event end date must be later than start date.
	public void testTransformScheduleMgr3700(){
		String tfm = "TransformOraToMongo.ktr";		
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.selectTransforms(tfm);
		tasks.setEventDesc(desc);
		tasks.setRepeatRule(new Daily(1));
		tasks.setEndByDate("06.08.2012");		
		tasks.setEventStartTime();
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals("The end date you entered occurs before start date.", msg);
	}
	
	@Test //Event end date must be in format mm.dd.yyyy.
	public void testTransformScheduleMgr3800(){
		String tfm = "TransformOraToMongo.ktr";		
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		tasks.openNewEventWindow();
		tasks.selectTransforms(tfm);
		tasks.setEventDesc(desc);
		tasks.setRepeatRule(new Daily(1));
		tasks.setEndByDate("03082012");
		tasks.setEventStartTime();
		tasks.saveEvent();
		String msg = tasks.confirmDialog();
		Assert.assertEquals("The end date you entered is invalid,the correct date format should be mm.dd.yyyy.", msg);
	}
	
	@Test //Two event with same Transform can simultaneously generate.
	public void testTransformScheduleMgr4000(){
		String tfm = "TransformOraToMongo.ktr";	
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();	
		tasks.removeMongoCollection(desCollection);
		tasks.goToExtraScheduleMgr(rptCenterPg);
		startDate = Calendar.getInstance();
		startDate.add(Calendar.MINUTE, 5);
		eventTime = tasks.createEvent(desc+"_a", null, startDate, tfm);
		tasks.createEvent(desc+"_b", null, startDate, tfm);
		tasks.triggerEvent(eventTime);
		Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
	}
	
	@Test //Two event with different Transforms can simultaneously generate.
	public void testTransformScheduleMgr4100(){
		String tfm1 = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desTable = "cor_device_test_des";
		String tfm2 = "TransformOraToOra.ktr";		
		String desCollection = "cor_device_test_des";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();	
		tasks.removeMongoCollection(desCollection);
		tasks.cleanOraTable(desTable);
		tasks.goToExtraScheduleMgr(rptCenterPg);
		startDate = Calendar.getInstance();
		startDate.add(Calendar.MINUTE, 5);
		eventTime = tasks.createEvent(desc+"a", null, startDate, tfm1);
		tasks.createEvent(desc+"_b", null, startDate, tfm2);
		tasks.triggerEvent(eventTime);
		Assert.assertTrue(tasks.verifyTransformToOracleSucceed(srcTable, desTable));
		Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
	}
	
	@Test //Two repeat event that time duration has overlap can run isolate
	public void testTransformScheduleMgr4200(){
		String tfm1 = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desTable = "cor_device_test_des";
		String tfm2 = "TransformOraToOra.ktr";		
		String desCollection = "cor_device_test_des";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		startDate = Calendar.getInstance();
		startDate.add(Calendar.MINUTE, 5);
		RepeatType repeat1 = new Weekly(1,new String[]{"Friday"});
		Calendar eventTime1 = tasks.createEvent(desc+"a", repeat1, startDate, tfm1);
		RepeatType repeat2 = new Daily(10);
		Calendar eventTime2 = tasks.createEvent(desc+"a", repeat2, startDate, tfm2);		
		int i = 0;
		while (i<6){
			i++;
			if(eventTime1.before(eventTime2)){
				System.out.println("eventtime1:"+ eventTime1.toString());
				System.out.println("eventtime2:"+ eventTime1.toString());
				tasks.removeMongoCollection(desCollection);				
				tasks.triggerEvent(eventTime1);
				Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));	
				eventTime1 = DateUtil.getNextOccurDate(eventTime1, repeat1);
			}else if(eventTime2.before(eventTime1)){
				System.out.println("eventtime1:"+ eventTime1.toString());
				System.out.println("eventtime2:"+ eventTime1.toString());
				tasks.cleanOraTable(desTable);
				tasks.triggerEvent(eventTime1);
				Assert.assertTrue(tasks.verifyTransformToOracleSucceed(srcTable, desTable));	
				eventTime2 = DateUtil.getNextOccurDate(eventTime2, repeat2);
			}else{
				System.out.println("eventtime1:"+ eventTime1.toString());
				System.out.println("eventtime2:"+ eventTime1.toString());
				tasks.cleanOraTable(desTable);
				tasks.removeMongoCollection(desCollection);
				tasks.triggerEvent(eventTime1);
				Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
				Assert.assertTrue(tasks.verifyTransformToOracleSucceed(srcTable, desTable));
				eventTime1 = DateUtil.getNextOccurDate(eventTime1, repeat1);
				eventTime2 = DateUtil.getNextOccurDate(eventTime2, repeat2);
			}
		}
	}
	
	@Test //Two event with can simultaneously generate.(One is created on Report Schedule Manager page,the other is created on Transform Schedule Manager page)
	public void testTransformScheduleMgr4300(){
		String tfm1 = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";		
		String desCollection = "cor_device_test_des";
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();	
		tasks.removeMongoCollection(desCollection);		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		startDate = Calendar.getInstance();
		startDate.add(Calendar.MINUTE, 5);
		eventTime = tasks.createEvent(desc+"_extra", null, startDate, tfm1);
		RptScheduleMgrTasks rptTasks = new RptScheduleMgrTasks();
		String rpt = "SDP_NMP.rptdesign";
		TransType[] type = null;
		RptFormat[] format = {RptFormat.EXCEL};
		rptTasks.gotoRptScheduleManager(rptCenterPg);
		String rptDesc = desc+"_rpt";
		rptTasks.createEvent(rptDesc, format, type, null, startDate, rpt);
		tasks.triggerEvent(eventTime);		
		Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
		String eventID = RptScheduleMgrTasks.getEventID(rptDesc);
		Pattern expectedRpt = rptTasks.getExceptedGenRptPattern(rpt, format[0], eventID, eventTime);
		Assert.assertTrue(rptTasks.verifyRptGenerated(expectedRpt));
	}
	
	@Test //Transform can extract data to mongodb incremently by scheduler event.
	public void testTransformScheduleMgr4400(){
		String tfm = "TransformOraToMongo.ktr";
		String srcTable = "cor_device_test_src";
		String desCollection = "cor_device_test_des";		
		RepeatType repeat = new Daily(1);
		startDate = Calendar.getInstance();
		ExtraScheduleMgrTasks tasks = new ExtraScheduleMgrTasks();
		tasks.removeMongoCollection(desCollection);
		int index = tasks.getIndexMaxNum(srcTable, "DEV_UID");
		String sql = "INSERT INTO cor_device_test_src(DEV_UID,DEV_SPID,DEV_TYPE,CREATION_DATE,DEV_STATUS) VALUES(?,2,'MP',TO_DATE('2013-06-05','yyyy-MM-dd'),2)";		
		tasks.goToExtraScheduleMgr(rptCenterPg);
		eventTime = tasks.createEvent(desc, repeat, startDate, tfm);		
		for (int i = 0; i < 3; i++) {
			tasks.triggerEvent(eventTime);
			Assert.assertTrue(tasks.verifyRecordCountInMongo(srcTable, desCollection));
			eventTime = DateUtil.getNextOccurDate(eventTime, repeat);
			index = index +1;								
			tasks.inserRowInOracle(sql,index);
		}
	}
}
