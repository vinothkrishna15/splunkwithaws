package com.tcs.destination.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.PerformanceReportService;

/**
 * 
 * Utility class to handle Date related functions
 */
public class DateUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(DateUtils.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	
	private static final Map<String, Integer> monthMap = new HashMap<String, Integer>();
    static {
        monthMap.put("JAN",Calendar.JANUARY);
        monthMap.put("FEB",Calendar.FEBRUARY);
        monthMap.put("MAR", Calendar.MARCH);
        monthMap.put("APR", Calendar.APRIL);
        monthMap.put("MAY", Calendar.MAY);
        monthMap.put("JUN", Calendar.JUNE);
        monthMap.put("JUL", Calendar.JULY);
        monthMap.put("AUG", Calendar.AUGUST);
        monthMap.put("SEP", Calendar.SEPTEMBER);
        monthMap.put("OCT", Calendar.OCTOBER);
        monthMap.put("NOV", Calendar.NOVEMBER);
        monthMap.put("DEC", Calendar.DECEMBER);
    }

	public static Date convertStringToDate(String strDate)
			throws ParseException {
		if (strDate == null)
			return null;
		Date date = null;
		synchronized (dateFormat) {
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

	/**
	 * Returns the Start or End Date of the given financial year
	 * 
	 * @param financialYear
	 *            The financial year for which the start or end date to be
	 *            retrieved.
	 * @param isStartDate
	 *            <code>true</code> if Start Date is required.
	 *            <code>false</code> if end date is needed.
	 * @return start or end {@link Date} of the financial year
	 * @throws DestinationException
	 *             when financial year is not in the format <b>FY'20xx-xx</b> <br>
	 *             (eg): FY'2015-16.
	 */
	public static Date getDateFromFinancialYear(String financialYear,
			boolean isStartDate) throws DestinationException {
		try {
			String yearStr = financialYear.split("'")[1].split("-")[0];
			int startYear = Integer.parseInt(yearStr);
			Calendar cal = getDefaultTime();
			if (isStartDate) {
				cal.set(Calendar.YEAR, startYear);
				cal.set(Calendar.MONTH, Calendar.APRIL);
				cal.set(Calendar.DATE, 1);
			} else {
				cal.set(Calendar.YEAR, startYear + 1);
				cal.set(Calendar.MONTH, Calendar.MARCH);
				cal.set(Calendar.DATE,
						cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
			setEndHourMinuteSec(cal);
			return cal.getTime();
		} catch (Exception e) {
			logger.error("Exception in Financial Year Format " + e);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Financial Year format. Must be in the format FY'20xx-xx.");
		}
	}

	/**
	 * Returns the Start or End Date of the given Quarter
	 * 
	 * @param quarter
	 *            The Quarter for which the start or end date to be retrieved.
	 * @param isStartDate
	 *            <code>true</code> if Start Date is required.
	 *            <code>false</code> if end date is needed.
	 * @return start or end {@link Date} of the Quarter
	 * @throws DestinationException
	 *             when quarter is not in the format <b>Qx - 20xx-xx</b> (eg):
	 *             Q1 - 2015-16.<br>
	 *             Or when in appropriate Quarter (say Q5) is requested
	 */
	public static Date getDateFromQuarter(String quarter, boolean isStartDate)
			throws DestinationException {
		try {
			int quarterNumber = Integer.parseInt(quarter.charAt(1) + "");
			String yearStr = quarter.split("-")[1].trim();
			int startYear = Integer.parseInt(yearStr);
			Calendar cal = getDefaultTime();
			if (isStartDate) {
				switch (quarterNumber) {
				case 1:
					cal.set(Calendar.YEAR, startYear);
					cal.set(Calendar.MONTH, Calendar.APRIL);
					break;
				case 2:
					cal.set(Calendar.YEAR, startYear);
					cal.set(Calendar.MONTH, Calendar.JULY);
					break;
				case 3:
					cal.set(Calendar.YEAR, startYear);
					cal.set(Calendar.MONTH, Calendar.OCTOBER);
					break;
				case 4:
					cal.set(Calendar.YEAR, startYear + 1);
					cal.set(Calendar.MONTH, Calendar.JANUARY);
					break;
				default:
					throw new Exception();
				}
				cal.set(Calendar.DATE, 1);
			} else {
				switch (quarterNumber) {
				case 1:
					cal.set(Calendar.YEAR, startYear);
					cal.set(Calendar.MONTH, Calendar.JUNE);
					break;
				case 2:
					cal.set(Calendar.YEAR, startYear);
					cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
					break;
				case 3:
					cal.set(Calendar.YEAR, startYear);
					cal.set(Calendar.MONTH, Calendar.DECEMBER);
					break;
				case 4:
					cal.set(Calendar.YEAR, startYear + 1);
					cal.set(Calendar.MONTH, Calendar.MARCH);
					break;
				default:
					throw new Exception();
				}
				cal.set(Calendar.DATE,
						cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				setEndHourMinuteSec(cal);
			}
			return cal.getTime();
		} catch (Exception e) {
			logger.error("Exception in Quarter Format " + e);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Quarter format. Must be in the format Qx - 20xx-xx.");
		}
	}
	
	public static Date getDateFromMonth(String month, boolean isStartDate) throws Exception{
		try{
			String[] monthStr = month.split("-");
			String yearStr = monthStr[1].trim();
			String monStr = monthStr[0].trim();
			int startYear = Integer.parseInt(yearStr);
			int endYear = Integer.parseInt(yearStr);
			int startMon, endMon;
			int startDay = 1;
			if(monthMap.containsKey(monStr)){
				startMon = monthMap.get(monStr);
				endMon = monthMap.get(monStr);
				Calendar cal = getDefaultTime();
				if(isStartDate){
					cal.set(Calendar.YEAR, startYear);
					cal.set(Calendar.MONTH, startMon);
					cal.set(Calendar.DATE, startDay);
				} else {
					cal.set(Calendar.YEAR, endYear);
					cal.set(Calendar.MONTH, endMon);
					cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
					setEndHourMinuteSec(cal);
				}
				return cal.getTime();
			} else {
				//invalid month
				logger.error("Exception in Month Format ");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Month format. Must be in the format MMM-YYYY.");
			}
		} catch (Exception e){
			logger.error("Exception in Month Format " + e);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Month format. Must be in the format MMM-YYYY.");
		}
	}

	private static Calendar getDefaultTime() {
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	private static void setEndHourMinuteSec(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
	}

	
	
}
