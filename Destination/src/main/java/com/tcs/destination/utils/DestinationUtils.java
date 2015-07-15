package com.tcs.destination.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.controller.UserRepositoryUserDetailsService.UserRepositoryUserDetails;
import com.tcs.destination.exception.DestinationException;

public class DestinationUtils {


	public static UserT getCurrentUserDetails() {
		Authentication a = SecurityContextHolder.getContext()
				.getAuthentication();
		return ((UserRepositoryUserDetails) a.getPrincipal());
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
}
