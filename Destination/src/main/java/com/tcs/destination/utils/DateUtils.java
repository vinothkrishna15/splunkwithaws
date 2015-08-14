package com.tcs.destination.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.tcs.destination.exception.DestinationException;

/**
 * 
 * Utility class to handle Date related functions
 */
public class DateUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(DateUtils.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static final SimpleDateFormat dbDateFormat = new SimpleDateFormat(
			"MMM-yy");

	private static final Map<String, Integer> monthMap = new HashMap<String, Integer>();
	static {
		monthMap.put("JAN", Calendar.JANUARY);
		monthMap.put("FEB", Calendar.FEBRUARY);
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

	/**
	 * 
	 * @param strDate
	 *            in yyyy-MM-dd format
	 * @return Date object of the String
	 * @throws ParseException
	 */
	public static Date convertStringToDate(String strDate)
			throws ParseException {
		if (strDate == null)
			return null;
		return dateFormat.parse(strDate);
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
				setEndHourMinuteSec(cal);
			}

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

	public static Date getDateFromMonth(String month, boolean isStartDate)
			throws Exception {
		try {
			String[] monthStr = month.split("-");
			String yearStr = monthStr[1].trim();
			String monStr = monthStr[0].trim();
			int startYear = Integer.parseInt(yearStr);
			int endYear = Integer.parseInt(yearStr);
			int startMon, endMon;
			int startDay = 1;
			if (monthMap.containsKey(monStr)) {
				startMon = monthMap.get(monStr);
				endMon = monthMap.get(monStr);
				Calendar cal = getDefaultTime();
				if (isStartDate) {
					cal.set(Calendar.YEAR, startYear);
					cal.set(Calendar.MONTH, startMon);
					cal.set(Calendar.DATE, startDay);
				} else {
					cal.set(Calendar.YEAR, endYear);
					cal.set(Calendar.MONTH, endMon);
					cal.set(Calendar.DATE,
							cal.getActualMaximum(Calendar.DAY_OF_MONTH));
					setEndHourMinuteSec(cal);
				}
				return cal.getTime();
			} else {
				// invalid month
				logger.error("Exception in Month Format ");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Month format. Must be in the format MMM-YYYY.");
			}
		} catch (Exception e) {
			logger.error("Exception in Month Format " + e);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Month format. Must be in the format MMM-YYYY.");
		}
	}

	private static Calendar getDefaultTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
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

	public static Date getDate(String month, String quarter, String year,
			boolean isStart) throws Exception {
		if (year.isEmpty() && quarter.isEmpty() && month.isEmpty())
			year = DateUtils.getCurrentFinancialYear();

		Date fromDate;
		if (!quarter.isEmpty() && year.isEmpty() && month.isEmpty()) {
			fromDate = DateUtils.getDateFromQuarter(quarter, isStart);
		} else if (!year.isEmpty() && quarter.isEmpty() && month.isEmpty()) {
			fromDate = DateUtils.getDateFromFinancialYear(year, isStart);
		} else if (!month.isEmpty() && quarter.isEmpty() && year.isEmpty()) {
			fromDate = DateUtils.getDateFromMonth(month, isStart);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Request");
		}
		return fromDate;
	}

	public static String getFormattedMonth(Date date) {
		return dbDateFormat.format(date).toUpperCase();
	}

	/**
	 * Gets current month in the format as per database
	 * 
	 * @return
	 */
	public static String getCurrentMonth() {
		return getFormattedMonth(new Date());
	}

	private static Date getDateFromDBFormattedString(String dbFormattedString)
			throws ParseException {
		return dbDateFormat.parse(dbFormattedString);
	}

	public static List<String> getAllMonthsBetween(String fromMonth,
			String toMonth) throws DestinationException {
		List<String> monthsList = new ArrayList<String>();
		// Just a initialisation to prevent NULL values
		Calendar fromDate = Calendar.getInstance();
		Calendar toDate = Calendar.getInstance();

		try {
			fromDate.setTimeInMillis(getDateFromDBFormattedString(fromMonth)
					.getTime());
			// Added a millisecond so that End Month is also included
			toDate.setTimeInMillis(getDateFromDBFormattedString(toMonth)
					.getTime() + 1);

		} catch (ParseException e) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Month Format for FROM or TO month");
		}
		while (fromDate.before(toDate)) {
			monthsList.add(dbDateFormat.format(fromDate.getTime())
					.toUpperCase());
			int month = fromDate.get(Calendar.MONTH) + 1;
			int year = fromDate.get(Calendar.YEAR) + month / 12;
			month = month % 12;
			fromDate.set(Calendar.MONTH, month);
			fromDate.set(Calendar.YEAR, year);
		}
		return monthsList;
	}

	public static String getQuarterForMonth(String formattedMonth)
			throws ParseException {
		String quarter = "";
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(getDateFromDBFormattedString(formattedMonth)
				.getTime());
		int year = cal.get(Calendar.YEAR);
		switch (cal.get(Calendar.MONTH)) {
		case Calendar.JANUARY:
		case Calendar.FEBRUARY:
		case Calendar.MARCH:
			quarter = "Q4 - " + (year - 1) + "-"
					+ Integer.toString(year).substring(2);
			break;
		case Calendar.APRIL:
		case Calendar.MAY:
		case Calendar.JUNE:
			quarter = "Q1 - " + (year) + "-"
					+ Integer.toString(year + 1).substring(2);
			break;
		case Calendar.JULY:
		case Calendar.AUGUST:
		case Calendar.SEPTEMBER:
			quarter = "Q2 - " + (year) + "-"
					+ Integer.toString(year + 1).substring(2);
			break;
		case Calendar.OCTOBER:
		case Calendar.NOVEMBER:
		case Calendar.DECEMBER:
			quarter = "Q3 - " + (year) + "-"
					+ Integer.toString(year + 1).substring(2);
			break;
		default:
			break;
		}
		return quarter;
	}

	public static String getFinancialYearForQuarter(String quarter) {
		return "FY'" + quarter.split(" ")[2];
	}

	/**
	 * Method to identify the index of the month of the specific quarter
	 * 
	 * @param formattedMonth
	 *            the month in MMM-yy format (as in DB)
	 * @return the number n, specifying that it is the nth month of the quarter
	 * @throws ParseException
	 *             when the month is not in specified format
	 */
	public static int getMonthIndexOnQuarter(String formattedMonth)
			throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(getDateFromDBFormattedString(formattedMonth)
				.getTime());
		switch (cal.get(Calendar.MONTH)) {
		case Calendar.JANUARY:
		case Calendar.APRIL:
		case Calendar.JULY:
		case Calendar.OCTOBER:
			return 1;
		case Calendar.FEBRUARY:
		case Calendar.MAY:
		case Calendar.AUGUST:
		case Calendar.NOVEMBER:
			return 2;
		case Calendar.MARCH:
		case Calendar.JUNE:
		case Calendar.SEPTEMBER:
		case Calendar.DECEMBER:
			return 3;
		}
		return 0;
	}

	public static List<String> getAllQuartersBetween(String fromQuarter,
			String toQuarter) {
		// fromQuarter = Qx - 20xx-xx
		int fromQuarterNumber = Integer.parseInt(fromQuarter.charAt(1) + "");
		// Removing the offset of 1-4 to 0-3 for calculation
		fromQuarterNumber--;
		String yearStr = fromQuarter.split("-")[1].trim();
		int startYear = Integer.parseInt(yearStr);
		List<String> quarters = new ArrayList<String>();
		quarters.add(fromQuarter);
		for (; !quarters.get(quarters.size() - 1).equals(toQuarter);) {
			// Incrementing the Quarters
			fromQuarterNumber++;
			// Make the Quarter to range from 0-3 and also change the financial
			// year when required.
			if ((fromQuarterNumber %= 4) == 0)
				startYear++;
			quarters.add("Q" + (fromQuarterNumber + 1) + " - " + startYear
					+ "-" + ((startYear % 100) + 1));
		}
		return quarters;
	}
	
	/**
	 * This method used to get the current date in dd MMM yyyy format
	 * @return current date
	 */
	public static String getCurrentDate() {
		Date date=new Date();
		SimpleDateFormat formatDate =  new SimpleDateFormat ("dd MMM yyyy");
		return formatDate.format(date);
	}
}