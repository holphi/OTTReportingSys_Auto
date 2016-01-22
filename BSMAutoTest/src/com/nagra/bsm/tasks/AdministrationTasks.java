package com.nagra.bsm.tasks;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import com.nagra.bsm.ui.AdminPage;
import com.nagra.bsm.ui.BackupPage;
import com.nagra.bsm.ui.FrontConfPage;
import com.nagra.bsm.ui.RestorePage;
import com.nagra.bsm.ui.RoleMgrPage;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.ui.RptTransportPage;
import com.nagra.bsm.ui.TransformVariablePage;
import com.nagra.bsm.ui.UserMgrPage;

public class AdministrationTasks {
//	public static WebDriver driver;
	protected static final Logger logger = Logger.getLogger(AdministrationTasks.class);
	private AdminPage adminPage;
	
	public void gotoAdministration(RptCenterPage rptCenterPg){
		this.adminPage = rptCenterPg.goToAdmin();
		logger.info("go to Administration");
	}
	
	public List<WebElement> getDisplayedTreeNodes(){
		return adminPage.getAdminTreeRow();
	}
	
	public FrontConfPage gotoFrontConfPage(){
		FrontConfPage frontPage = adminPage.goToFrontPageConfPage();
		logger.info("Go to Frontpage Config Page");
		return frontPage;
	}
	
	public RptTransportPage gotoRptTransportPage(){
		RptTransportPage rptTransPage = adminPage.goToRptTransportConfPage();
		logger.info("Go to Report Transport Configure Page");
		return rptTransPage;
	}

/* Beacuse Backup Transport Configure Page UI is totally same as Report Transport Configure Page,so reuse it.
 * That means in this method RptTransportPage is Backup Transport configuration page indeed. */
	public RptTransportPage gotoBakTransportPage(){
		RptTransportPage rptTransPage = adminPage.goToBakTransportConfPage();
		logger.info("Go to Backup Transport Configure Page");
		return rptTransPage;
	}
	
	public BackupPage gotoBsmConfBackupPage(){
		BackupPage backupPage = adminPage.gotoBsmConfBakPage();
		logger.info("Go to BSM Configuration Backup Page");		
		return backupPage;
	}
	
	public BackupPage gotoRptBackupPage(){
		BackupPage backupPage = adminPage.gotoRptBakPage();
		logger.info("Go to Report Template Backup Page");		
		return backupPage;
	}
	
	public BackupPage gotoPdiTfmBackupPage(){
		BackupPage backupPage = adminPage.gotoPdiTfmBakPage();
		logger.info("Go to PDI Transform Backup Page");		
		return backupPage;
	}
	
	public RestorePage gotoBsmConfRestorePage(){
		RestorePage restorePage = adminPage.gotoBsmCfgRestorePage();
		logger.info("Go to BSM Configuration Backup Page");				
		return restorePage;
	}
	
	public RestorePage gotoRptRestorePage(){
		RestorePage restorePage = adminPage.gotoRptRestorePage();
		logger.info("Go to Report Template Backup Page");		
		return restorePage;
	}
	
	public BackupPage gotoPdiTfmRestorePage(){
		RestorePage RestorePage = adminPage.gotoPdiTfmRestorePage();
		logger.info("Go to PDI Transform Backup Page");		
		return gotoPdiTfmRestorePage();
	}
	
	public RoleMgrPage gotoRoleMgrPage(){
		RoleMgrPage roleMgrPage = adminPage.goToRoleMgrPage();
		logger.info("Go to Role Manager Page");		
		return roleMgrPage;
	}
	
	public UserMgrPage gotoUserMgrPage(){
		UserMgrPage userMgrPage = adminPage.goToUserMgrPage();
		logger.info("Go to Role Manager Page");		
		return userMgrPage;
	}
	
	public TransformVariablePage gotoTransformVariablePage(){
		TransformVariablePage trfmVarPg = adminPage.goToTransformVariablePage();
		logger.info("Go to Transform Variable page");
		return trfmVarPg;
	}
	
	
}	
