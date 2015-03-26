package com.tcs.destination.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * Utility class to handle Date related functions
 */
public class DateUtil {
	
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
	
}
