/**
 * 
 */
package com.nagra.bsm.tasks;

import java.util.Calendar;

import com.nagra.bsm.ui.BackupPage;
import com.nagra.bsm.util.CommonUtil;

/**
 * @author tetang
 * //not finish
 */
public class BackupTasks extends AdministrationTasks{
	private BackupPage backupPage;
	
	public BackupTasks(BackupPage backupPage){
		this.backupPage = backupPage;		
	}

	public void backupManually(String bakpath){
		backupPage.selectTypeManual();
		backupPage.executeBackup(bakpath);
	}
	
	public void backupByScheduler(){					
		Calendar cal = CommonUtil.getRemoteTime();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		String h = new Integer(hour).toString();
		int min = cal.get(Calendar.MINUTE);
		String m = new Integer(min -2).toString();
		String cron = "0 "+ m +" "+ h + "  /* /* /?";
		backupPage.setCronExpression(cron);
		backupPage.clickSaveButton();
		CommonUtil.sleep(120);
	}

}

