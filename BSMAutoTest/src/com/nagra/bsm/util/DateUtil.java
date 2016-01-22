/**
 * 
 */
package com.nagra.bsm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.nagra.bsm.ui.Daily;
import com.nagra.bsm.ui.Monthly;
import com.nagra.bsm.ui.RepeatType;
import com.nagra.bsm.ui.Weekly;
import com.nagra.bsm.ui.Yearly;

/**
 * @author tetang
 * 
 */
public class DateUtil {
	protected static final Logger logger = Logger.getLogger(DateUtil.class);

	// get a calendar from a string
	public static Calendar setDate(String day) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = simpleDateFormat.parse(day);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	// get a calendar from a string and set time from a calendar
	public static Calendar setDateTime(String day, Calendar cal) {
		Calendar dt = setDate(day);
		dt.set(Calendar.HOUR, cal.get(Calendar.HOUR));
		dt.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		return dt;
	}

	public static Calendar getDateTime(Calendar date, Calendar time) {
		date.set(Calendar.HOUR, time.get(Calendar.HOUR));
		date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		return date;
	}

	public static Calendar getNextOccurDate(Calendar lastOccurDate,
			RepeatType repeat) {
		if (repeat instanceof Daily) {
			Daily da = (Daily) repeat;
			getNextDailyOccurDate(lastOccurDate, da);
		} else if (repeat instanceof Weekly) {
			Weekly we = (Weekly) repeat;
			getNextWeeklyOccurDate(lastOccurDate, we);
		} else if (repeat instanceof Monthly) {
			Monthly mon = (Monthly) repeat;
			getNextMonthlyOccurDate(lastOccurDate, mon);
		} else if (repeat instanceof Yearly) {
			Yearly ye = (Yearly) repeat;
			getNextYearlyOccurDate(lastOccurDate, ye);
		} else {
			logger.error("Cannot get repeat type");
		}
		return lastOccurDate;
	}

	public static Calendar getFirstOccurDate(Calendar calendar,
			RepeatType repeat) {
		if (repeat instanceof Daily) {
			Daily da = (Daily) repeat;
			if (da.interval < 0) {
				while (!isWorkDay(calendar)) {
					calendar.add(Calendar.DAY_OF_WEEK, 1);
				}
			}
		} else if (repeat instanceof Weekly) {
			Weekly we = (Weekly) repeat;
			getFirstWeeklyOccurDate(calendar, we);
		} else if (repeat instanceof Monthly) {
			Monthly mon = (Monthly) repeat;
			getFirstMonthlyOccurDate(calendar, mon);
		} else if (repeat instanceof Yearly) {
			Yearly year = (Yearly) repeat;
			getFirstYearlyOccurDate(calendar, year);
		} else {
			logger.error("Cannot get repeat type");
		}
		return calendar;
	}

	public static int[] getSortedWeeekDays(Weekly weekly) {
		int len = weekly.weekdays.length;
		int wd[] = new int[len];
		int index = 0;
		for (; index < len; index++) {
			wd[index] = valueOfWeekday(weekly.weekdays[index]);
		}
		Arrays.sort(wd);
		return wd;
	}

	public static Calendar getFirstWeeklyOccurDate(Calendar calendar,
			Weekly weekly) {
		int[] wd = getSortedWeeekDays(weekly);
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		calendar.set(Calendar.DAY_OF_WEEK, wd[0]);
		return calendar;
	}

	public static Calendar getFirstMonthlyOccurDate(Calendar calendar,
			Monthly monthly) {
		return getNextMonthlyOccurDate(calendar, monthly);
	}

	public static Calendar getFirstYearlyOccurDate(Calendar calendar,
			Yearly yearly) {
		return getNextYearlyOccurDate(calendar, yearly);
	}

	public static Calendar getNoWeekdayOfMonth(Calendar calendar,
			int weekdayno, String weekday) {
		int wkday = valueOfWeekday(weekday);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		if (calendar.get(Calendar.DAY_OF_WEEK) < wkday) {
			calendar.set(Calendar.WEEK_OF_MONTH, weekdayno);
		} else {
			calendar.set(Calendar.WEEK_OF_MONTH, weekdayno + 1);
		}
		calendar.set(Calendar.DAY_OF_WEEK, wkday);
		return calendar;
	}

	public static Calendar getNextDailyOccurDate(Calendar lastOccurDate,
			Daily daily) {
		if (daily.interval < 0) {
			do {
				lastOccurDate.add(Calendar.DAY_OF_MONTH, 1);
			} while (!isWorkDay(lastOccurDate));
		} else {
			lastOccurDate.add(Calendar.DAY_OF_MONTH, daily.interval);
		}
		return lastOccurDate;
	}

	public static boolean isWorkDay(Calendar cal) {
		int day = cal.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
			return false;
		else
			return true;
	}

	public static Calendar getNextWeeklyOccurDate(Calendar lastOccurDate,
			Weekly weekly) {
		int[] wd = getSortedWeeekDays(weekly);
		int len = wd.length;
		int curwd = lastOccurDate.get(Calendar.DAY_OF_WEEK);
		if (curwd == wd[len - 1]) {
			lastOccurDate.add(Calendar.WEEK_OF_YEAR, weekly.interval);
			lastOccurDate.set(Calendar.DAY_OF_WEEK, wd[0]);
		} else {
			int index = Arrays.binarySearch(wd, curwd);
			lastOccurDate.set(Calendar.DAY_OF_WEEK, wd[index + 1]);
		}
		return lastOccurDate;
	}

	public static int valueOfWeekday(String weekday) {
		Map<String, Integer> mp = new HashMap<String, Integer>();
		mp.put("Sunday", Calendar.SUNDAY);
		mp.put("Monday", Calendar.MONDAY);
		mp.put("Tuesday", Calendar.TUESDAY);
		mp.put("Wednesday", Calendar.WEDNESDAY);
		mp.put("Thursday", Calendar.THURSDAY);
		mp.put("Friday", Calendar.FRIDAY);
		mp.put("Saturday", Calendar.SATURDAY);
		return mp.get(weekday).intValue();
	}

	public static int valueOfMonth(String month) {
		Map<String, Integer> mp = new HashMap<String, Integer>();
		mp.put("January", Calendar.JANUARY);
		mp.put("February", Calendar.FEBRUARY);
		mp.put("March", Calendar.MARCH);
		mp.put("April", Calendar.APRIL);
		mp.put("May", Calendar.MAY);
		mp.put("June", Calendar.JUNE);
		mp.put("July", Calendar.JULY);
		mp.put("August", Calendar.AUGUST);
		mp.put("September", Calendar.SEPTEMBER);
		mp.put("October", Calendar.OCTOBER);
		mp.put("November", Calendar.NOVEMBER);
		mp.put("December", Calendar.DECEMBER);
		return mp.get(month).intValue();
	}

	public static Calendar getNextMonthlyOccurDate(Calendar lastOccurDate,
			Monthly monthly) {
		lastOccurDate.set(Calendar.DAY_OF_MONTH, 1);
		lastOccurDate.add(Calendar.MONTH, 1);
		if (monthly.weekNo < 0) {
			lastOccurDate.set(Calendar.DAY_OF_MONTH, monthly.repeatday);
		} else {
			getNoWeekdayOfMonth(lastOccurDate, monthly.weekNo, monthly.weekday);
		}
		return lastOccurDate;
	}

	public static Calendar getNextYearlyOccurDate(Calendar lastOccurDate,
			Yearly yearly) {
		int mon = valueOfMonth(yearly.month);
		lastOccurDate.add(Calendar.YEAR, 1);
		if (yearly.no < 0) {
			lastOccurDate.set(Calendar.MONTH, mon);
			lastOccurDate.set(Calendar.DAY_OF_MONTH, yearly.repeatday);
		} else {
			lastOccurDate.set(Calendar.MONTH, mon);
			getNoWeekdayOfMonth(lastOccurDate, yearly.no, yearly.weekday);
		}
		return lastOccurDate;
	}

	public static void setLocalTime(Calendar cal) {
		String cmd = null;		
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");	
		cmd = String.format(" cmd /c date %s", df.format(cal.getTime()));
		try {
			Runtime.getRuntime().exec(cmd);
			logger.info(String.format("Set local date: %s", cmd));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void syncLocalTime() {
		String cmd = "w32tm -resync";
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			InputStream in = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine())!= null) {
				logger.info(line);
			}
			in.close();
			process.waitFor();
			process.destroy();
			logger.info(String.format("Sync local time with time server."));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
/*	public static void main(String[] args) {
		syncLocalTime();
	}
*/
}
