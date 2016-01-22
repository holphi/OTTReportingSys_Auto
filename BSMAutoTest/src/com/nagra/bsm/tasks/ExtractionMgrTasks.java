package com.nagra.bsm.tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import junit.framework.Assert;

import com.nagra.bsm.ui.ExtractionScheduleMgrPage;
import com.nagra.bsm.ui.LoginPage;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.RptUpload;
import com.nagra.bsm.ui.TransformMgrPage;
import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.util.DBUtil;

public class ExtractionMgrTasks {
	protected static final Logger logger = Logger
			.getLogger(ExtractionMgrTasks.class);
	public TransformMgrPage transMgrPage;

	public ExtractionMgrTasks(TransformMgrPage transMgrPage){
		this.transMgrPage = transMgrPage;
	}
	
	public ExtractionMgrTasks() {
		// TODO Auto-generated constructor stub
	}

	public void gotoExtractionManager(RptCenterPage rptCenterPg) {
		this.transMgrPage = rptCenterPg.goToTransformMgr();
		logger.info("go to Extraction Manager");
	}
	
	//path:source file path,end with "\\"
	public void addTransform(String path , String ...transformName){		
			transMgrPage.ClickAddTransform();			
			
			RptUpload rptUpload = new RptUpload(transMgrPage.getWebDriver());
			for (String trans : transformName){
				rptUpload.setFilePath(path+trans);
				Assert.assertTrue(rptUpload.isFileListedInContainer(trans));
			}
			rptUpload.clickUpload();
			CommonUtil.sleep(6);			
			rptUpload.closeWindow();
	}
						
	public void batchUpload(String path, String rptZip){
		transMgrPage.clickBatchUpload();
		logger.info("Click on Batch Upload Template");
		RptUpload rptUpload = new RptUpload(transMgrPage.getWebDriver());		
		rptUpload.setFilePath(path+rptZip);
		logger.info("Add report template package");
		
		rptUpload.clickUpload();
		logger.info("Click Upload");
		
		CommonUtil.sleep(6);			
		rptUpload.closeWindow();
		logger.info("Close upload window");
	}
	
	public void updateTransform(String path , String transformName) throws Exception{		
			transMgrPage.clickUpdateTransform(transformName);			
			
			RptUpload rptUpload = new RptUpload(transMgrPage.getWebDriver());
			rptUpload.setFilePath(path+transformName);
			Assert.assertTrue(rptUpload.isFileListedInContainer(transformName));
			rptUpload.clickUpload();
			CommonUtil.sleep(6);			
			rptUpload.closeWindow();			
	}
	
	public String removeTransform(String ...transformName) throws Exception{
		for (String trans : transformName){
			transMgrPage.checkOnTransform(trans);
		}
		transMgrPage.ClickRemoveTransform();
		String msg;
		CommonUtil.sleep(3);
		msg = transMgrPage.getTextFromAlertWindow();
		return msg;
//		if (transMgrPage.isAlertPresent()){
//			msg = transMgrPage.getTextFromAlertWindow();
//			transMgrPage.acceptAlertWindow();
//			return msg;
//		}else{
//			msg="";
//			return msg;
//		}				
			
	}
	
	public String setTransformPublic(String ...transformName) {
		for (String trans : transformName){
			try {
				transMgrPage.checkOnTransform(trans);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		transMgrPage.clickSetPublic(); 
		CommonUtil.sleep(3);
		String msg;
		msg = transMgrPage.getTextFromAlertWindow();					
		return msg;	
	}
	
	public String setTransformPrivate(String ...transformName) {
		for (String trans : transformName){
			try {
				transMgrPage.checkOnTransform(trans);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		transMgrPage.clickSetPrivate();
		CommonUtil.sleep(3);
		String msg=transMgrPage.getTextFromAlertWindow();;		
		return msg;	
	}
	
	public boolean isTransformGeneric(String transformName){
		String generic = transMgrPage.getValueFromTable(transformName, 6);
		if (generic.equals("Yes")){
			return true;
		}
		return false;
	}
	
	public boolean isTransformGeneric(String transformName,String user){
		String sql = "select IS_GENERIC from BSM_ETL where ETL_NAME = ? and CREATE_USER = ?";
		int is_generic = -1;
		try {
			PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql);
			ps.setString(1, transformName);
			ps.setString(2, user);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				is_generic = rs.getInt(1);
			}else{
				logger.error("Fail to get transform is_generic value");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (is_generic == 1){
			return true;
		}else {
			return false;
		}		
	}
	
	public List<WebElement> getVisiableBtns(){
		return transMgrPage.getVisiableBtns();
	}
	
	public void setSchedule(String desc, String trans) {
		transMgrPage.clickSetScheduleTransform(trans);
		ExtractionScheduleMgrPage extraSchPg = new ExtractionScheduleMgrPage(transMgrPage.getWebDriver());
		extraSchPg.inputEventDesc(desc);
		extraSchPg.setStartDate(Calendar.getInstance());
		extraSchPg.clickSave();
	}
	
	public boolean isTfmExisted(String transform,String user){
		String sql = "select ETL_Name from BSM_ETL where ETL_NAME = ? AND CREATE_USER = ? ";
		try {
			PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql);
			ps.setString(1, transform);
			ps.setString(2, user);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isTfmExisted(String transform){
		return isTfmExisted(transform,"admin");
	}
	
	public void setTfmToPublic(String ...transform){
		try{
			for(String tfm : transform ){
				transMgrPage.checkOnTransform(tfm);
				transMgrPage.clickSetPublic();
				CommonUtil.sleep(1);
				transMgrPage.acceptAlertWindow();
				CommonUtil.sleep(1);
				transMgrPage.acceptAlertWindow();
			}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String executeTransform(String transform){
		String msg = null;
		try {
			transMgrPage.clickExecuteTransform(transform);
			int i = 0;
			while(i<12){
				CommonUtil.sleep(10);
				msg = transMgrPage.getTextFromAlertWindow();
				if(msg.contains("wait")){
					continue;
				}else if(msg.contains("Execute")){
					break;
				}
			}
			transMgrPage.acceptAlertWindow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}
	
	public boolean isTfmUpdated(String rpt,String user){
		String sql = "select tfm.update_time from BSM_ETL as tfm join BSM_USER as usr on tfm.create_USER_ID = usr.USER_ID Where tfm.ETL_Name = ? AND usr.USER_NAME = ?";
		try {
			PreparedStatement ps =DBUtil.getConnection().prepareStatement(sql);
			ps.setString(1, rpt);
			ps.setString(2, user);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				Timestamp ts = rs.getTimestamp(1);
				if(ts!=null){
					return true;
				}else{
					return false;
				}
			}else{
				logger.error("Fail to find report update info");
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
