package com.tcs.destination.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.FrequentlySearchedResponse;
import com.tcs.destination.bean.PartnerMasterT;

public class ResponseConstructors {

	private static final Logger logger = LoggerFactory.getLogger(ResponseConstructors.class);

	public static String filterJsonForFieldAndViews(String fields, String view,
			Object object) throws Exception {
		if (!view.equals("")) {
			StringTokenizer st = new StringTokenizer(view, ",");
			while (st.hasMoreTokens()) {
				// TODO:check and add the Fields based on View
			}
		}
		return filterJsonForFields(fields, object);

	}

	public static String filterJsonForFields(String fields, Object object) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		//For Jackson to understand Hibernate datatypes (e.g .Lazy loading)
		//mapper.registerModule(new Hibernate4Module());
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		if (fields.equals("all")) {
			try {
				FilterProvider filters = new SimpleFilterProvider().addFilter(
						Constants.FILTER,
						SimpleBeanPropertyFilter.serializeAllExcept(""));
				return mapper.writer(filters).writeValueAsString(object);
			} catch (JsonProcessingException e) {
				return e.getMessage();
			}
		} else {
			StringTokenizer st = new StringTokenizer(fields, ",");
			Set<String> filterProperties = new HashSet<String>();
			String objectId = null;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				filterProperties.add(token);
				//Add object id to support JsonIdentityInfo object reference
				objectId = BeanObjectIdMapper.getObjectId(token); 
				if (objectId != null) {
					logger.debug("Adding ObjectId: {}", objectId);
					filterProperties.add(objectId);
				}
			}

			FilterProvider filters = new SimpleFilterProvider().addFilter(
					Constants.FILTER, SimpleBeanPropertyFilter
							.filterOutAllExcept(filterProperties));
			try {
				return mapper.writer(filters).writeValueAsString(object);
			} catch (IOException e) {
				e.printStackTrace();
				return e.getMessage();
			}

		}
	}
	
	/**
	 * This method is used to handle child object fields.
	 * Calls all child object's getter methods using reflection
	 * @param object
	 * @return 
	 */
/*	private static void handleChildObjects(Object parentObj) throws Exception {
		//logger.info("Inside handleChildObjects() method");
		Method[] methods = null;
		
		if (parentObj != null) { 
			methods = parentObj.getClass().getDeclaredMethods();
		}
		
		logger.info("Parent object = " + parentObj.getClass().getName());
		if (methods != null) {
			try {
				for (Method method: methods) {
					if (method.getName().startsWith("get")) {
						Object childObj = method.invoke(parentObj, new Object[] {});
						if (null != childObj) { 
							//For lazy loading
							childObj.toString(); 
						}
					}
				}
			} catch (Exception e) {
				logger.error("Error occured while handling child objects." + e.getMessage());
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
			}
		}
	}
*/	
	public static FrequentlySearchedResponse convertToFrequentlySearchedResponse(
			Integer count, PartnerMasterT partner) {
		FrequentlySearchedResponse response = new FrequentlySearchedResponse();
		response.setCount(count);
		response.setEntity(partner);
		return response;
	}

	public static FrequentlySearchedResponse convertToFrequentlySearchedResponse(
			Integer count, CustomerMasterT customer) {
		FrequentlySearchedResponse response = new FrequentlySearchedResponse();
		response.setCount(count);
		response.setEntity(customer);
		return response;
	}
}
