/**
 * 
 */
package com.nagra.bsm.ui;

/**
 * @author tetang
 * 
 */
public class Yearly extends RepeatType {
	public int repeatday;
	public String month;
	public int no;
	public String weekday;

	public Yearly(int day, String month) {
		this.no = -1;
		this.repeatday = day;
		this.month = month;
	}
	

	public Yearly(int no, String weekday, String month) {
		this.no = no;
		this.weekday = weekday;
		this.month = month;
	}
}
