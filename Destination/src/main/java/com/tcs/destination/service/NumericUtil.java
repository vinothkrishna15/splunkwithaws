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

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static com.tcs.destination.utils.Constants.USD_PATTERN;

/**
 * This NumericUtil class will provide features for handling numbers
 * 
 */
public class NumericUtil {
	
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

}
