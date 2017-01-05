package com.tcs.destination.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcs.destination.bean.SearchResult;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.controller.UserRepositoryUserDetailsService.UserRepositoryUserDetails;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;

public class DestinationUtils {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DestinationUtils.class);

	private static final Map<String, String> MIME_TYPE;

	static {
		Map<String, String> map = Maps.newHashMap();
		map.put("pdf", "application/pdf");
		map.put("xls", "application/vnd.ms-excel");
		map.put("xlsm", "application/vnd.ms-excel.sheet.macroenabled.12");
		map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		map.put("png", "image/png");
		map.put("bmp", "image/bmp");
		map.put("gif", "image/gif");
		map.put("jpg", "image/jpeg");
		map.put("jpeg", "image/jpeg");
		map.put("doc", "application/msword");
		map.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		map.put("txt", "text/plain");
		
		MIME_TYPE = Collections.unmodifiableMap(map);
	}
	
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
	
	/**
	 * retunrs file extension from filename
	 * @param fileName
	 * @return
	 */
	public static String getExtension(String fileName) {
		String extention = "";
		if(fileName.indexOf('.') > 0) {
			extention = fileName.substring(fileName.lastIndexOf('.') + 1);
		}
		return extention;
	}
	

	public static String getMimeType(String fileName) {
		String extention = DestinationUtils.getExtension(fileName);
		if(StringUtils.isNotEmpty(extention) && MIME_TYPE.containsKey(extention)) {
			return MIME_TYPE.get(extention);
		}
		return "";
	}
	
	/**
	 * calls the method using the reflection and returns the method's return value
	 * @param obj
	 * @param methodName
	 * @param parameters
	 * @return
	 */
	public static Object invokeMethod(Object obj, String methodName,
			Object... parameters) {
		if(obj != null) {
			try {
				Class<?>[] paramTypes = new Class<?>[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					paramTypes[i] = parameters[i].getClass();
				}
				Method method = obj.getClass().getMethod(methodName, paramTypes);
				return method.invoke(obj, parameters);

			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				logger.info("Exception on method reflecction for the method {}.{}, message :{}", obj.getClass().getName(), methodName, e.getMessage());
			}
		}
		return null;
	}
	
	public static byte[] decodeBase64(String base64String) {
		return Base64.decodeBase64(base64String);
	}
	
	public static BigDecimal scaleToTwoDecimal(BigDecimal val, boolean zeroIfNull) {
		BigDecimal returnValue = null; 
		if(val != null) {
			returnValue = val.setScale(2, BigDecimal.ROUND_HALF_UP);
		} else if(zeroIfNull) {
			 return new BigDecimal(0);
		}
		
		return returnValue;
	}

	public static List<String> getSalesUserGroups() {
		return Lists.newArrayList(UserGroup.BDM.getValue(), UserGroup.BDM_SUPERVISOR.getValue(), UserGroup.GEO_HEADS.getValue());
	}

	public static List<String> getConsultingUserGroups() {
		return Lists.newArrayList(UserGroup.PRACTICE_HEAD.getValue(), UserGroup.PRACTICE_OWNER.getValue());
	}
	
	
	
	
}
