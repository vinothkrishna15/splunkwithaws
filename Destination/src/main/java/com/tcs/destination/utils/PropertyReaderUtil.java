package com.tcs.destination.utils;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * This utility class is to read properties files
 * 
 * @author bnpp
 *
 */
public class PropertyReaderUtil {
    
    public static String readPropertyFile(String propertyFileName, String propertyName) throws Exception{
	
	String value = null;
	
	ResourceBundle rb = ResourceBundle.getBundle(propertyFileName);
	
	Enumeration <String> keys = rb.getKeys();
	while (keys.hasMoreElements()) {
		String key = keys.nextElement();
		if(key.equals(propertyName)){
		    value = rb.getString(key);
		}
	}
	
	return value;
    }

}
