/**
 * 
 */
package com.nagra.bsm.ui;

/**
 * @author tetang
 * 
 */
public class Monthly extends RepeatType {
	public int repeatday;
	public int weekNo;
	public String weekday;

	public Monthly(int day) {
		this.weekNo = -1;
		this.repeatday = day;
	}

	public Monthly(int no, String weekday) {
		this.weekNo = no;
		this.weekday = weekday;
	}
}
