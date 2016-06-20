package com.tcs.destination.utils;

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
	
	/**
	 * returns the json string for the given object
	 * 
	 * @param fields - fields to filter in json
	 * @param view
	 * @param object - object to serialize
	 * @param allowDuplicateEntity - true to ignore JsonIdentityInfo annotation
	 * @return
	 * @throws Exception
	 */
	public static String filterJsonForFieldAndViews(String fields, String view,
			Object object,  boolean allowDuplicateEntity) throws Exception {
		StringBuffer viewFields = null;
		if (!view.equals("")) {
			viewFields = new StringBuffer();
			StringTokenizer st = new StringTokenizer(view, ",");
			while (st.hasMoreTokens()) {
				String v_fields = ViewFieldsMapper.getFields(st.nextToken());
				if (v_fields != null)
					viewFields.append(v_fields);
			}
		}
		if ((viewFields != null) && (viewFields.length() > 0)) {
			if (fields.equalsIgnoreCase("all")) {
				fields = viewFields.toString();
			} else { 
				fields = fields.concat("," + viewFields.toString());
			}
		}
		return filterJsonForFields(fields, object, allowDuplicateEntity);
	}
	
	/**
	 * returns the json string for the given object
	 * @param fields
	 * @param view
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static String filterJsonForFieldAndViews(String fields, String view,
			Object object) throws Exception {
		return filterJsonForFieldAndViews(fields, view, object, false);
	}

	private static String filterJsonForFields(String fields, Object object, boolean allowDuplicateEntity) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		//For Jackson to understand Hibernate datatypes (e.g .Lazy loading)
		//mapper.registerModule(new Hibernate4Module());
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		if(allowDuplicateEntity) {
			mapper.setAnnotationIntrospector(new DestinationAnnotationIntrospector());
		}
		
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
				
				String response = mapper.writer(filters).writeValueAsString(object);
				logger.info("Response::::::::::::::::::: " + response);
				return response;
			} catch (Exception e) {
				return e.getMessage();
			}

		}
	}
	
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
