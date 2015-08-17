package com.tcs.destination.utils;

public class StringUtils {

	public static boolean isEmpty(String value) {
		if (value == null || value.trim().length() == 0)
			 return true;
		else 
			return false;
	}
}
