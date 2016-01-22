/**
 * 
 */
package com.nagra.bsm.ui;

/**
 * @author tetang
 * 
 */
public class Weekly extends RepeatType {
	public int interval;
	public String[] weekdays;
	
	public Weekly(int interval, String[] weekdays) {
		this.interval = interval;
		this.weekdays = weekdays;		
	}
}
