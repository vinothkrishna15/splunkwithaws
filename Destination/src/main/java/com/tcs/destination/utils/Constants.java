package com.tcs.destination.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.EnumMap;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.FrequentlySearchedResponse;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.controller.UserRepositoryUserDetailsService.UserRepositoryUserDetails;

public class Constants {

	public static final String FILTER = "DestinationFilter";

	public static enum EntityType {
		CUSTOMER("CUSTOMER"), PARTNER("PARTNER");

		private final String name;

		private EntityType(String name) {
			this.name = name;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}

		public static boolean contains(String test) {

			for (EntityType c : EntityType.values()) {
				if (c.name().equals(test)) {
					return true;
				}
			}

			return false;
		}

	}

	public static enum OWNER_TYPE {
		PRIMARY("PRIMARY"), SECONDARY("SECONDARY"), ALL("ALL");

		private final String name;

		private OWNER_TYPE(String name) {
			this.name = name;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}

		public static boolean contains(String test) {
			for (OWNER_TYPE c : OWNER_TYPE.values()) {
				if (c.name().equals(test)) {
					return true;
				}
			}

			return false;
		}

	}

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

	public static UserT getCurrentUserDetails() {
		Authentication a = SecurityContextHolder.getContext()
				.getAuthentication();
		return ((UserRepositoryUserDetails) a.getPrincipal());
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
	
	public static String getCurrentFinancialYear() {
		String financialYear = "FY'";
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.MONTH) > 3) {
			financialYear += cal.get(Calendar.YEAR)
					+ "-"
					+ String.valueOf(cal.get(Calendar.YEAR) + 1)
							.substring(2, 4);
		} else {
			financialYear += (cal.get(Calendar.YEAR) - 1) + "-"
					+ String.valueOf(cal.get(Calendar.YEAR)).subSequence(2, 4);
		}
		return financialYear;
	}
	
	public static Timestamp getCurrentTimeStamp() {
    	Date d = new Date();
		return new Timestamp(d.getTime());
	}

	//Task Entity Reference
	public static enum TaskEntityReference {
		Connect("Connect"), Opportunity("Opportunity");

		private final String name;

		private TaskEntityReference(String name) {
			this.name = name;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}
		
		public static boolean contains(String type) {
			for (TaskEntityReference entityRef : TaskEntityReference.values()) {
				if (entityRef.name().equals(type)) {
					return true;
				}
			}
			return false;
		}
	}

	//Task Collaboration Preference
	public static enum TaskCollaborationPreference {
		Private("Private"), Public("Public"), Restricted("Restricted");

		private final String name;

		private TaskCollaborationPreference(String name) {
			this.name = name;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}

		public static boolean contains(String type) {
			for (TaskCollaborationPreference pref : TaskCollaborationPreference.values()) {
				if (pref.name().equals(type)) {
					return true;
				}
			}
			return false;
		}
	}

	//Task Status
	public static enum TaskStatus {
		Open("Open"), Hold("Hold");

		private final String name;

		private TaskStatus(String name) {
			this.name = name;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}

		public static boolean contains(String type) {
			for (TaskStatus status : TaskStatus.values()) {
				if (status.name().equals(type)) {
					return true;
				}
			}
			return false;
		}
	}
}