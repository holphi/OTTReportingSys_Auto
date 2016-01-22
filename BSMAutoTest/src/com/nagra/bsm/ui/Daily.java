/**
 * 
 */
package com.nagra.bsm.ui;

/**
 * @author tetang
 * 
 */
public class Daily extends RepeatType {
	public int interval;

	public Daily() {
		interval = -1;
	}		

	public Daily(int interval) {
		this.interval = interval;
	}
		
}
