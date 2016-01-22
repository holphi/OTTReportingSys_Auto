/**
 * 
 */
package com.nagra.bsm.tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import com.nagra.bsm.ui.GeneratedFiles;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.RptFormat;
import com.nagra.bsm.ui.RptMgrPage;
import com.nagra.bsm.ui.RptScheduleMgrPage;
import com.nagra.bsm.ui.RptUpload;
import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.util.DBUtil;

/**
 * @author tetang
 *
 */
public class RptMgrTasks {
	private RptMgrPage rptMgrPage;
	protected static final Logger logger = Logger.getLogger(RptMgrTasks.class);
	
	public RptMgrTasks(RptMgrPage rptMgrPage){
		this.rptMgrPage = rptMgrPage ;
	}

	public RptMgrTasks(){
		
	}
	
	public void gotoRptMgr(RptCenterPage rptCenterPg){
		this.rptMgrPage = rptCenterPg.goToReportMgr();
		logger.info("Go to Report Mgr");
	}
	
	//path:source file path,end with "\\"
	public void addRpt(String path, String ...rptNames ){
		rptMgrPage.clickAddRpt();
		logger.info("Click on Add Report Template");
		RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());
		for (String rpt : rptNames){
			rptUpload.setFilePath(path+rpt);
			logger.info("Add file");
		}
		rptUpload.clickUpload();
		logger.info("Click Upload");
		
		CommonUtil.sleep(6);			
		rptUpload.closeWindow();
		logger.info("Close upload window");
	}
	
	public String rmRpt(String ...rptName) throws Exception{
		for (String rpt : rptName){
			rptMgrPage.checkRpt(rpt);
			logger.info("Check on report");
		}
		return remove();
	}
	
	public void rmRptAll(){
		if (rptMgrPage.getFileCount()>0){
			rptMgrPage.chkOnSelectAll();
			remove();
		}
		
	}
	
	public String remove(){
		rptMgrPage.clickRemoveRpt();
		logger.info("Click Remove Report Template");
		String msg;
		CommonUtil.sleep(3);
		msg = rptMgrPage.getTextFromAlertWindow();
		logger.info(msg);
		rptMgrPage.acceptAlertWindow();
		msg = rptMgrPage.getTextFromAlertWindow();
		logger.info(msg);
		rptMgrPage.acceptAlertWindow();
		return msg;
	}
	
	public boolean isRptPresent(String rpt){
		return rptMgrPage.isRptItemPresent(rpt);		
	}
	
	public boolean isRptExisted(String rpt, String user){
		String sql = "select Report_Name from BSM_Report as rpt join BSM_USER as user on rpt.CREATE_USER_ID = user.USER_ID where rpt.report_Name = ? and user.USER_NAME = ?";
		try {
			PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql);
			ps.setString(1, rpt);
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
	
	public boolean isRptExisted(String rpt){
		return isRptExisted(rpt,"admin");
	}
	
	public List<WebElement> getVisiableBtns(){
		return rptMgrPage.getVisiableBtns();
	}
	
	public void batchUpload(String path, String rptZip){
		rptMgrPage.clickBatchUpload();
		logger.info("Click on Batch Upload Template");
		RptUpload rptUpload = new RptUpload(rptMgrPage.getWebDriver());		
		rptUpload.setFilePath(path+rptZip);
		logger.info("Add report template package");
		
		rptUpload.clickUpload();
		logger.info("Click Upload");
		
		CommonUtil.sleep(6);			
		rptUpload.closeWindow();
		logger.info("Close upload window");
	}
	
	public Calendar setSchedule(String rptName, RptFormat format,String desc){
		Calendar eventTime = null;
		try {
			rptMgrPage.clickSetSchedule(rptName);
			RptScheduleMgrPage rptSchPg = new RptScheduleMgrPage(rptMgrPage.getWebDriver());
			rptSchPg.selectRptFormat(format);
			rptSchPg.inputEventDesc(desc);
			eventTime = rptSchPg.setStartTime();
			rptSchPg.saveEvent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return eventTime;
		
	}
	
	public void setRptPublic(String ...rpt){
		try {
			rptMgrPage.checkRpt(rpt);
			rptMgrPage.clickSetToPublic();
			CommonUtil.sleep(2);
			rptMgrPage.acceptAlertWindow();
			CommonUtil.sleep(2);
			rptMgrPage.acceptAlertWindow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> viewGeneratedFiles(String rpt){
		try {
			rptMgrPage.clickViewRpt(rpt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CommonUtil.sleep(1);
		GeneratedFiles generated = new GeneratedFiles(rptMgrPage.getWebDriver());
		return generated.getGeneratedFiles();
	}
	
	public boolean isRptUpdated(String rpt,String user){
		String sql = "select rpt.update_time from BSM_REPORT as rpt join BSM_USER as usr on rpt.create_USER_ID = usr.USER_ID Where rpt.Report_Name = ? AND usr.USER_NAME = ?";
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
