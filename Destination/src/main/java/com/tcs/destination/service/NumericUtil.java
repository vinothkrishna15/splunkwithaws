/**
 * 
 * NumericUtil.java 
 *
 * @author TCS
 * @Version 1.0 - 2016
 * 
 * @Copyright 2016 Tata Consultancy 
 */
package com.tcs.destination.service;

import static com.tcs.destination.utils.Constants.USD;
import static com.tcs.destination.utils.Constants.USD_PATTERN;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * This NumericUtil class will provide features for handling numbers
 * 
 */
public class NumericUtil {
	
	private static BigDecimal THOUSAND = new BigDecimal(1000);
	private static BigDecimal MILLION = new BigDecimal(1000000);
	private static BigDecimal BILLION = new BigDecimal(1000000000);
	private static BigDecimal TRILLION = new BigDecimal(1000000000000L);
	public static MathContext mc = new MathContext(3, RoundingMode.HALF_EVEN);
	
	private static DecimalFormat usdFormatter = new DecimalFormat(USD_PATTERN);
	
	
	
	/**
	 * Method to format it to the USD String
	 * 
	 * @param value
	 * @return String
	 */
	public static String toUSD(BigDecimal value) {
		
		return usdFormatter.format(value);
	}
	
	public static String toUSDinNumberScale(BigDecimal value) {
		
		String returnVal = null;
		
		if (value.compareTo(THOUSAND) != 1) {
			returnVal = USD + " " + value.round(mc).stripTrailingZeros().toEngineeringString();
		} else if (value.compareTo(THOUSAND) != -1 && value.compareTo(MILLION) == -1) {
			returnVal = USD + " " + value.divide(THOUSAND, mc).round(mc).stripTrailingZeros().toEngineeringString() + "K ";
		} else if (value.compareTo(MILLION) != -1 && value.compareTo(BILLION) == -1) {
			returnVal = USD + " " + value.divide(MILLION, mc).round(mc).stripTrailingZeros().toEngineeringString() + "M ";
		} else if (value.compareTo(MILLION) != -1 && value.compareTo(TRILLION) == -1) {
			returnVal = USD + " " + value.divide(BILLION, mc).round(mc).stripTrailingZeros().toEngineeringString() + "B ";
		} else if (value.compareTo(TRILLION) != -1) {
			returnVal = USD + " " + value.divide(TRILLION, mc).round(mc).stripTrailingZeros().toEngineeringString() + "T ";
		}
		
		return returnVal;
	}
	
}
