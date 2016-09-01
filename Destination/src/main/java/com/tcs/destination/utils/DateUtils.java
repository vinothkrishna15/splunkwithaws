package com.tcs.destination.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Weeks;
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

	/**
	 * yyyy-MM-dd
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	
	/**
	 * MMM-yy
	 */
	private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat(
			"MMM-yy");

	/**
	 * yyyy-MM-dd HH:mm
	 */
	private static final SimpleDateFormat COMMENT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * dd/MM/yyyy HH:mm
	 */
	private static final Format DT_BATCH_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	/**
	 * dd-MMM-yyyy
	 */
	public static final DateFormat ACTUAL_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
	
	/**
	 * dd-MMM-yyyy
	 */
	public static final DateFormat ACTUAL_FORMAT_MONTH = new SimpleDateFormat("dd MMMMM yyyy");
	
	/**
	 * MM/dd/yyyy
	 */
	public static final DateFormat DESIRED_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	/**
	 * Format dd/MM/yyyy HH:mm:ss
	 */
	public static final DateFormat AUDIT_HISTORY_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	/**
	 * Fromat HH:mm 
	 */
	public static final DateFormat FORMAT_HH_COLON_MM = new SimpleDateFormat("HH:mm");
	
	/**
	 * Fromat dd/MM/yyyy 
	 */
	public static final DateFormat FORMAT_DATE_WITH_SLASH = new SimpleDateFormat("dd/MM/yyyy");
	
	public static final DateFormat FORMAT_DATE_WITH_SECONDS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
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
	
	public static String getCurrentDateForBatch () {
		return DT_BATCH_FORMAT.format(new Date());
	}
	
	public static String convertDtToStringForUser(Date date) {
		return DATE_FORMAT.format(date);
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
		return DATE_FORMAT.parse(strDate);
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
		return DB_DATE_FORMAT.format(date).toUpperCase();
	}
	
	public static String getFormattedTime(Object fromValue) throws ParseException {
		String timeStr = fromValue.toString();  
		Date time = COMMENT_FORMAT.parse(timeStr);
		return COMMENT_FORMAT.format(time);
	}
	
	public static String getFormattedDate(Object fromValue) throws ParseException {
		String timeStr = fromValue.toString();  
		Date time = DATE_FORMAT.parse(timeStr);
		return DATE_FORMAT.format(time);
	}

	/**
	 * Gets current month in the format as per database
	 * 
	 * @return
	 */
	public static String getCurrentMonth() {
		return getFormattedMonth(new Date());
	}
	
	public static boolean isDBFormattedMonth(String dbFormattedMonth){
		try{
			DB_DATE_FORMAT.parse(dbFormattedMonth);
			return true;
		} catch (ParseException e){
			return false;
		}
	}

	public static Date getDateFromDBFormattedString(String dbFormattedString)
			throws ParseException {
		return DB_DATE_FORMAT.parse(dbFormattedString);
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
			monthsList.add(DB_DATE_FORMAT.format(fromDate.getTime())
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
		return ACTUAL_FORMAT.format(new Date());
	}
	
	/**
     * Add days to date in java
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
                 
        return cal.getTime();
    }
	/**
	 * This method used to get the date from the string dd MMM yyyy format
	 * @return current date
	 * @throws ParseException 
	 */
	public static Date getDateFrString(String dateStr) throws ParseException {
		return ACTUAL_FORMAT.parse(dateStr);
	}
	
	/**
	 * 
	 * @param financialYear
	 *            in the format FY'20xx-xx
	 * @return
	 * @throws DestinationException
	 */
	public static ArrayList<String> getQuarters(String financialYear)
			throws DestinationException {
		ArrayList<String> quarterList = new ArrayList<String>();
		String years = financialYear.split("'")[1];
		if (years == null || years.isEmpty()) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Financial Year format. Must be in the format FY'20xx-xx.");
		} else {
			Integer currentYear = 0;
			try {
				currentYear = Integer.parseInt(years.split("-")[0]);
			} catch (Exception e) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Financial Year format. Must be in the format FY'20xx-xx.");
			}

			for (int i = 1; i < 5; i++) {
					quarterList.add("Q" + i + " - " + currentYear +"-" + years.split("-")[1]);
			}
		}

		return quarterList;
	}

	/**
	 * 
	 * @param quarter
	 *            The Quarter in the format Qx - 20XX-YY
	 * @return
	 * @throws DestinationException
	 */
	public static ArrayList<String> getMonths(String quarter)
			throws DestinationException {
		ArrayList<String> monthList = new ArrayList<String>();
		String years = quarter.split(" ")[0];
		if (years == null || years.isEmpty()) {
			logger.error("Exception in Quarter Format " + quarter);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Quarter format. Must be in the format Qx - 20xx-xx.");
		} else {

			String[] quarterArray = null;
			Integer quarterNumber = 0;
			Integer currentYear = 0;
			try {
				quarterArray = quarter.split("-");
				quarterNumber = Integer
						.parseInt(quarterArray[0].charAt(1) + "");
				currentYear = Integer.parseInt(quarterArray[1].trim());
			} catch (Exception e) {
				logger.error("Exception in Quarter Format " + e);
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Quarter format. Must be in the format Qx - 20xx-xx.");
			}

				switch (quarterNumber) {
				case 1:
					monthList.add("APR-" + currentYear);
					monthList.add("MAY-" + currentYear);
					monthList.add("JUN-" + currentYear);
					break;
				case 2:
					monthList.add("JUL-" + currentYear);
					monthList.add("AUG-" + currentYear);
					monthList.add("SEP-" + currentYear);
					break;
				case 3:
					monthList.add("OCT-" + currentYear);
					monthList.add("NOV-" + currentYear);
					monthList.add("DEC-" + currentYear);
					break;
				case 4:
					monthList.add("JAN-" + (currentYear + 1));
					monthList.add("FEB-" + (currentYear + 1));
					monthList.add("MAR-" + (currentYear + 1));
					break;
				default:
					break;
				}
			}

		return monthList;
	}
	
	// Return sub category list of year,quarter and month
		public static Map<String, Date> getSubDatesList(String month, String year,
				String quarter, boolean isFromDate) throws Exception,
				DestinationException {
			Map<String, Date> dateMap = new LinkedHashMap<String, Date>();
			List<String> subCategoryList = new ArrayList<String>();
			if (!month.isEmpty()) {
				subCategoryList.add(month);
				dateMap.put(month,
						DateUtils.getDateFromMonth(month, isFromDate));
			} else if (!year.isEmpty()) {
				subCategoryList = DateUtils.getQuarters(year);
				for (String subCategory : subCategoryList) {
					dateMap.put(subCategory,
							DateUtils.getDateFromQuarter(subCategory, isFromDate));
				}
			} else if (!quarter.isEmpty()) {
				subCategoryList = DateUtils.getMonths(quarter);
				for (String subCategory : subCategoryList) {
					dateMap.put(subCategory,
							DateUtils.getDateFromMonth(subCategory, isFromDate));
				}
			} else {
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Unable to pick date for the given request");
			}
			return dateMap;
		}

		public static List<String> getMonthsFromYear(String financialYear) throws Exception {
			List<String> months = new ArrayList<String>();
			List<String> quarters = getQuarters(financialYear);
			for(String quarter : quarters){
				List<String> quarterMonths = getMonths(quarter);
				for(String mon : quarterMonths){
					String temp = getFormattedMonth(getDateFromDBFormattedString(mon));
					quarterMonths.set(quarterMonths.indexOf(mon),temp);
				}
				months.addAll(quarterMonths);
			}
			return months;
		}	

	public static Date getNewTimestampFormat(String values)
			throws Exception {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		Date date = (Date) formatter.parse(values);
		SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String finalString = newFormat.format(date);
		Date newDateFormat = (Date) newFormat.parse(finalString);
		return newDateFormat;
	}

	public static Date getDateFromGivenAndCurrentFinancialYear(String givenDate, String year, boolean isTrue) throws Exception{
		Date date = null;
		if (isTrue) {
			if (givenDate !=null && givenDate.length()>0) {
				date = getDateFromMonth(givenDate, true);
			} else if (!year.equals("")) {
				logger.debug("year is not Empty");
				date = getDateFromFinancialYear(year, true);
			} else if (year.equals("")) {
				date = getDateFromFinancialYear(getCurrentFinancialYear(), true);
			}
		} else {
			if (givenDate !=null && givenDate.length()>0) {
				date = getDateFromMonth(givenDate, false);
			} else if (!year.equals("")) {
				logger.debug("year is not Empty");
				date = getDateFromFinancialYear(year, false);
			} else if (year.equals("")) {
				date = getDateFromFinancialYear(getCurrentFinancialYear(), false);
			}
		}
		return date;

	}

	public static String getCurrentQuarter() throws Exception {
		return getQuarterForMonth(getCurrentMonth());
	}
	
	
	public static List<String> getMonthsFromQuarter(String quarter) throws Exception {
		List<String> months = new ArrayList<String>();
			List<String> quarterMonths = getMonths(quarter);
			for(String mon : quarterMonths){
				String temp = getFormattedMonth(getDateFromDBFormattedString(mon));
				quarterMonths.set(quarterMonths.indexOf(mon),temp);
			}
			months.addAll(quarterMonths);
		return months;
	}

	/**
	 * @param value
	 * @return String 
	 * @throws ParseException 
	 */
	public static String[] formatUploadDateData(String value, String incomingFormat, String outgoingFormat) throws ParseException {
		
		SimpleDateFormat dbFormat = new SimpleDateFormat(incomingFormat);
		SimpleDateFormat uploadFormat = new SimpleDateFormat(outgoingFormat);
		
		Date date = uploadFormat.parse(value);
		
		String[] strArr = new String [3];
		
		strArr[0] = dbFormat.format(date);
		strArr[1] = getQuarterForMonth(strArr[0]);
		strArr[2] = getFinancialYearForQuarter(strArr [1]);
		return strArr;
	}

	/**
	 * @return
	 */
	public static String getCurrentDateForFile() {
		Date date=new Date();
		SimpleDateFormat formatDate =  new SimpleDateFormat ("MMddyy");
		return formatDate.format(date);
	}
	
	/*
	 * get java.util.Date from java.sql.Timestamp
	 */
	public static Date toDate(Timestamp timestamp) {
	    long milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);
	    return new Date(milliseconds);
	}
	
	/*
	 * get Date in dd/MM/YYYY format
	 */
	public static String convertDateToString(Date indate)
	{
		String dateString = null;
		SimpleDateFormat sdfr = new SimpleDateFormat("dd/MM/yyyy");
		try{
			dateString = sdfr.format( indate );
		}catch (Exception ex ){
			logger.debug(ex.getMessage());
		}
		return dateString;
	}
	
	/*
	 * get hour minute from date
	 */
	public static String convertDateToHourMinute(Date indate){
		SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm");
		return dateFormatter.format(indate);
	}

	/**
	 * @return
	 */
	public static String getCurrentDateInDesiredFormat() {
		return DESIRED_FORMAT.format(new Date());
	}
	
	/**
	 * format the date in given format
	 * @param date
	 * @param auditHistoryFormat
	 * @return
	 */
	public static String format(Date date, DateFormat sdfr) {
		return sdfr.format( date );
	}
	
	/**
	 * parse the date from the dateStr with the given format
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date parse(String dateStr, DateFormat sdfr) {
		try {
			return sdfr.parse(dateStr);
		} catch (ParseException e) {
			logger.warn(e.getMessage());
		}
		return null;
	}
	

	public static Date truncateSeconds(Date date) {
		Date truncatedDate = org.apache.commons.lang.time.DateUtils.truncate(date, Calendar.SECOND);
		return truncatedDate;
	}
	
	public static Date mergeDateWithTime(Date date, Date time) {
		LocalTime localTime = new LocalTime(time);
		return new DateTime(date).withHourOfDay(localTime.getHourOfDay()).withMinuteOfHour(localTime.getMinuteOfHour()).toDate();
	}
	
	/**
	 * calculates number of week between the current financial year start(1st Apr) and given date
	 * @param date
	 * @return
	 */
	public static int weekOfFinancialYr(Date date) {
		LocalDate dateTime1 = new LocalDate().withMonthOfYear(4).withDayOfMonth(1);

		LocalDate dateTime2 = new LocalDate(date);
		if(dateTime2.year().get() == dateTime1.year().get() && dateTime2.monthOfYear().get() < 4) {
			dateTime1 = dateTime1.minusYears(1);
		}

		return Weeks.weeksBetween(dateTime1, dateTime2).getWeeks();
	}

	/**
	 * calculates number of week between the current financial year start(1st Apr) and given date
	 * @param date
	 * @return
	 */
	public static String getFinancialYr() {
		//FY 2016-17
		int startYr;
		int endYr;
		LocalDate dateTime = new LocalDate();
		
		int currentYr = dateTime.getYear();
		if(dateTime.monthOfYear().get() < 4) {
			startYr = currentYr-1;
			endYr = currentYr;
		} else {
			startYr = currentYr;
			endYr = currentYr + 1;
		}

		return String.format("FY [%d-%d]", startYr, endYr%100) ;
	}
	
	/**
	 * gives the current date
	 * @return
	 */
	public static Date getCurrentMidnightDate(){
		return new LocalDate().toDate();
		}
	
	/**
	 * Gives the previous week date
	 * @return
	 */
	public static Date getPreviousWeekDate() {
		return new LocalDate().minusDays(7).toDate();
		}
	
	/**
	 * Gives the previous date
	 * @return
	 */
	public static Date getPreviousDate() {
		return new LocalDate().minusDays(1).toDate();
		}
	
}