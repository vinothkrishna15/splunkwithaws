package com.tcs.destination.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * Utility class to handle Date related functions
 */
public class DateUtils {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static Date convertStringToDate(String strDate) throws ParseException {
		if (strDate == null)
			return null;
		Date date = null;
		synchronized(dateFormat) {
			date = dateFormat.parse(strDate);
		}
		return date;
	}
	
	

	public static String getCurrentFinancialYear() {
		String financialYear = "FY'";
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.MONTH) > 2) {
			financialYear += cal.get(Calendar.YEAR)
					+ "-"
					+ String.valueOf(cal.get(Calendar.YEAR) + 1)
							.substring(2, 4);
		} else {
			financialYear += (cal.get(Calendar.YEAR) - 1) + "-"
					+ String.valueOf(cal.get(Calendar.YEAR)).subSequence(2, 4);
		}
		return financialYear;
	}
	
	public static Timestamp getCurrentTimeStamp() {
    	Date d = new Date();
		return new Timestamp(d.getTime());
	}
}
