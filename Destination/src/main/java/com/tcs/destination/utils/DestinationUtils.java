package com.tcs.destination.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.SearchResult;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.controller.UserRepositoryUserDetailsService.UserRepositoryUserDetails;
import com.tcs.destination.exception.DestinationException;

public class DestinationUtils {

	/**
	 * This method returns the current session authenticated user details
	 * @return
	 */
	public static UserT getCurrentUserDetails() {
		Authentication a = SecurityContextHolder.getContext()
				.getAuthentication();
		if(a instanceof AnonymousAuthenticationToken){
			return null;
		}
		return ((UserRepositoryUserDetails) a.getPrincipal());
	}
	
	/**
	 * returns the current session authenticated user's id
	 * @return
	 */
	public static String getCurrentUserId() {
		return getCurrentUserDetails().getUserId();
	}

	/**
	 * This method verifies if the user id passed in the URI parameter is same
	 * as current session authenticated user
	 * 
	 * @param userId
	 * @return
	 */
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

	/**
	 * creates list of smart search result dto from the array of id and name
	 * @param records
	 * @return
	 */
	public static List<SearchResult> getSearchResults(List<Object[]> records) {
		List<SearchResult> res = Lists.newArrayList();
		if(records != null) {
			for (Object[] rec : records) {
				SearchResult searchResult = new SearchResult(String.valueOf(rec[0]), String.valueOf(rec[1]));
				res.add(searchResult);
			}
		}
		return res;
	}
	
}
