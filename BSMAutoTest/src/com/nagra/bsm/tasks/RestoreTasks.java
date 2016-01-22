/**
 * 
 */
package com.nagra.bsm.tasks;

import com.nagra.bsm.ui.RestorePage;

/**
 * @author tetang
 *
 */
public class RestoreTasks {
	private RestorePage restorePage;
	
	public RestoreTasks (RestorePage restorePage){
		this.restorePage = restorePage ;
	}
	
	public void restore(String bakfile){
		restorePage.restore(bakfile);
	}
}
