/**
 * 
 */
package com.nagra.bsm.tests;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import com.nagra.bsm.tasks.BackupTasks;
import com.nagra.bsm.tasks.BrowserTasks;
import com.nagra.bsm.tasks.FrontConfTasks;
import com.nagra.bsm.tasks.LoginTasks;
import com.nagra.bsm.tasks.RestoreTasks;
import com.nagra.bsm.tasks.RoleMgrTasks;
import com.nagra.bsm.tasks.UserMgrTasks;
import com.nagra.bsm.ui.AdminPage;
import com.nagra.bsm.ui.LoginPage;
import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.util.CommonUtil;

/**
 * @author tetang
 *
 */
public class BackupAndRestoreTests extends BaseTests{
	private AdminPage adminPage;
	private static String testDataDir = CommonUtil.getCurrDir()+"\\testdata\\";
	private String testTempDir = CommonUtil.getCurrDir()+"\\testtemp\\";
	//Utility method for quick navigation to test page.
			private AdminPage navigateToTestPage(String username, String password)
			{
				logger.info("Login in with User:" + username + ", Password:" + password);

				LoginPage loginPage = new LoginPage(CommonUtil.launchTestPortal());

				RptCenterPage rptCenterPage= loginPage.loginAs(username, password);
				
				logger.info("Click Administration to go to Administration tab.");
				return rptCenterPage.goToAdmin();
			}
	
			@Before
			public void testSetup(){
				logger.info("============================="+testName.getMethodName()+"==============================");
				this.adminPage = navigateToTestPage(CommonUtil.getPropertyValue("username"),
						   CommonUtil.getPropertyValue("password"));
			}
			
			@Test			
			//BackupandRestoreCase0100 :BSM configuration can be backup manually.
			public void testBackupAndRestore0100(){
				String bakname = "BsmConf0100.zip";
				try{
				FrontConfTasks frontConf = new FrontConfTasks(adminPage.goToFrontPageConfPage());
				frontConf.setHeaderImage(testDataDir + "1024x99.jpg" );
				RoleMgrTasks roleMgr = new RoleMgrTasks(adminPage.goToRoleMgrPage());
				roleMgr.createRole("BAK1", "BAK1", "");
				UserMgrTasks usrMgr = new UserMgrTasks(adminPage.goToUserMgrPage());
				usrMgr.createUser("bak1", "1", "1", true, "USER");
				BackupTasks backup = new BackupTasks(adminPage.gotoBsmConfBakPage());
				//backup.backupManually(testTempDir+bakname);
				adminPage.goToFrontPageConfPage();
				frontConf.setHeaderImage(testDataDir + "1400x120.jpg" );
				adminPage.goToRoleMgrPage();
				roleMgr.removeRole("BAK1");
				adminPage.goToUserMgrPage();
				usrMgr.removeUser("bak1");
				RestoreTasks restore = new RestoreTasks(adminPage.gotoBsmCfgRestorePage());
				restore.restore(testTempDir+bakname);				
				BrowserTasks bwTask = new BrowserTasks(adminPage);				
				//reload login Page and login
				LoginTasks loginT = new LoginTasks(bwTask.reloadLoginPage());
				loginT.login();
				adminPage.goToAdmin();
				//Verify Restore
				adminPage.goToFrontPageConfPage();
				Assert.assertTrue("Verify the header image is restored.",frontConf.getHeaderImg().contains("1024x99.jpg"));
				adminPage.goToRoleMgrPage();
				Assert.assertTrue("Verify Role Manager is rstored", roleMgr.chkRoleExists("BAK1"));
				adminPage.goToUserMgrPage();
				Assert.assertTrue("Verify User Manager is rstored", usrMgr.chkUserExists("bak1"));				
				}catch(Exception e){
					CommonUtil.logError(e, testName);
				}finally{
					CommonUtil.closeBrowser();
				}
			}
			
			@Test			
			//BackupandRestoreCase0200 :BSM configuration can be backup by scheduler and transport backup file by email,FTP and HTTP.
			public void testBackupAndRestore0200(){
				String bakname = "BsmConf0200.zip";
				try{
					
					FrontConfTasks frontConf = new FrontConfTasks(adminPage.goToFrontPageConfPage());
					frontConf.setHeaderImage(testDataDir + "1024x99.gif" );
					RoleMgrTasks roleMgr = new RoleMgrTasks(adminPage.goToRoleMgrPage());
					roleMgr.createRole("BAK2", "BAK2", "");
					UserMgrTasks usrMgr = new UserMgrTasks(adminPage.goToUserMgrPage());
					usrMgr.createUser("bak2", "1", "1", true, "USER");
					BackupTasks backup = new BackupTasks(adminPage.gotoBsmConfBakPage());
					backup.backupByScheduler();
					adminPage.goToFrontPageConfPage();
					frontConf.setHeaderImage(testDataDir + "1400x120.jpg" );
					adminPage.goToRoleMgrPage();
					roleMgr.removeRole("BAK2");
					adminPage.goToUserMgrPage();
					usrMgr.removeUser("bak2");
					RestoreTasks restore = new RestoreTasks(adminPage.gotoBsmCfgRestorePage());
					restore.restore(testTempDir+bakname);				
					BrowserTasks bwTask = new BrowserTasks(adminPage);				
					//reload login Page and login
					LoginTasks loginT = new LoginTasks(bwTask.reloadLoginPage());
					loginT.login();
					adminPage.goToAdmin();
					//Verify Restore
					adminPage.goToFrontPageConfPage();
					Assert.assertTrue("Verify the header image is restored.",frontConf.getHeaderImg().contains("1024x99.gif"));
					adminPage.goToRoleMgrPage();
					Assert.assertTrue("Verify Role Manager is rstored", roleMgr.chkRoleExists("BAK2"));
					adminPage.goToUserMgrPage();
					Assert.assertTrue("Verify User Manager is rstored", usrMgr.chkUserExists("bak2"));				
					}catch(Exception e){
						CommonUtil.logError(e, testName);
					}finally{
						CommonUtil.closeBrowser();
					}
			}
}
