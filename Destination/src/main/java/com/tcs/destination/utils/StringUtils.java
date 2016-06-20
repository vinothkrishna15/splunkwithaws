package com.tcs.destination.utils;

import java.util.Random;

public class StringUtils {

	public static boolean isEmpty(String value) {
		if (value == null || value.trim().length() == 0)
			 return true;
		else 
			return false;
	}
	
	public static String generateRandomString(int length){
		Random r = new Random();
		
		String numbers = new String("0123456789");
		int num_n = numbers.length();
		
		String uppercase  = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		int num_u = uppercase.length();
		
		String lowercase = new String("abcdefghijklmnopqrstuvwxyz"); 
		int num_l = lowercase.length();
		
		String specialchars = new String("#$*@%_-");
		int num_s = specialchars.length();
		
		int order[][] = {
				        {0,1,2,3}, {0,1,3,2}, {0,2,1,3}, {0,2,3,1}, {0,3,1,2}, {0,3,2,1},
				        {1,0,2,3}, {1,0,3,2}, {2,0,1,3}, {2,0,3,1}, {3,0,1,2}, {3,0,2,1}
				        };
		int choice = r.nextInt(12);
		
		String result = new String(); 
		
		for (int i=0; i<length; i++){
			if (i % 4 == order[choice][0]){
				result = result + uppercase.charAt(r.nextInt(num_u));
			} else if(i % 4 == order[choice][1]){
				result = result + lowercase.charAt(r.nextInt(num_l));
			} else if (i % 4 == order[choice][2]){
				result = result + numbers.charAt(r.nextInt(num_n));
			} else {
				result = result + specialchars.charAt(r.nextInt(num_s));
			}
		}
		return result;
		}
}
