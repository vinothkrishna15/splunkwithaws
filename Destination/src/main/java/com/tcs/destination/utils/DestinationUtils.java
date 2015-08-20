package com.tcs.destination.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.controller.UserRepositoryUserDetailsService.UserRepositoryUserDetails;
import com.tcs.destination.exception.DestinationException;

public class DestinationUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(DestinationUtils.class);
	
	// This method returns the current session authenticated user details
	public static UserT getCurrentUserDetails() {
		Authentication a = SecurityContextHolder.getContext()
				.getAuthentication();
		if(a instanceof AnonymousAuthenticationToken){
			return null;
		}
		return ((UserRepositoryUserDetails) a.getPrincipal());
	}

	// This method verifies if the user id passed in the URI parameter 
	// is same as current session authenticated user
	public static boolean isCurrentAuthenticatedUser(String userId) {
		if (getCurrentUserDetails().getUserId().equals(userId))
			return true;
		else
			return false;
	}

	/**
	 * Returns a copy of the object, or null if the object cannot
	 * be serialized.
	 */
	public static Object copy(Object originalObject) throws Exception {
		Object copyObject = null;
		try {
			// Write the object to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(originalObject);
			oos.flush();
			oos.close();

			// Make an input stream from the byte array and read a copy of the object
			ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(bos.toByteArray()));
			copyObject = ois.readObject();
		} catch(IOException ioe) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, 
					"Error occurred while cloning the object:" + ioe.getMessage());
		} catch(ClassNotFoundException cnfe) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, 
					"Error occurred while cloning the object:" + cnfe.getMessage());
		}
		return copyObject;
	}
	
	/*
	 * Returns the workbook object for given multipart file(xls / xlsm / xlsx)
	 */
	public static Workbook getWorkBook(MultipartFile file) throws IOException{
		String fileName = file.getOriginalFilename();
		logger.info("Received File : " + fileName);
		String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		logger.info("Received File's Extension : " + fileName);
		InputStream fileInputStream = file.getInputStream();
        if(fileExtension.equalsIgnoreCase("xls")){
        	return new HSSFWorkbook(fileInputStream);
        } else if(fileExtension.equalsIgnoreCase("xlsx")){
        	return new XSSFWorkbook(fileInputStream);
        } else if(fileExtension.equalsIgnoreCase("xlsm")){
        	return new XSSFWorkbook(fileInputStream);
        } else {
        	return null;
        }
	}
}
