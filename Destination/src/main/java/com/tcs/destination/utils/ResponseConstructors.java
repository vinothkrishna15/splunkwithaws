package com.tcs.destination.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.FrequentlySearchedResponse;
import com.tcs.destination.bean.PartnerMasterT;

public class ResponseConstructors {

	public static String filterJsonForFieldAndViews(String fields, String view,
			Object object) {
		if (!view.equals("")) {
			StringTokenizer st = new StringTokenizer(view, ",");
			while (st.hasMoreTokens()) {
				// TODO:check and add the Fields based on View
			}
		}
		return filterJsonForFields(fields, object);

	}

	public static String filterJsonForFields(String fields, Object object) {
		if (fields.equals("all")) {
			try {
				ObjectMapper mapper = new ObjectMapper();
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
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				filterProperties.add(token);
			}

			ObjectMapper mapper = new ObjectMapper();
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
