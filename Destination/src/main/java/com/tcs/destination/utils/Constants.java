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
import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;

public class Constants {

	public static final String FILTER = "DestinationFilter";

	public static enum EntityType {
		CUSTOMER("Customer"), PARTNER("Partner");

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

			for (Constants.EntityType c : Constants.EntityType.values()) {
				if (c.name().equalsIgnoreCase(test)) {
					return true;
				}
			}

			return false;
		}

	}

	public static CustPartResultCard convertToCard(PartnerMasterT partner) {
		CustPartResultCard card = new CustPartResultCard();

		// recent.setGroupCustomerName(partners.getCorporateHqAddress());
		card.setGeographyMappingT(partner.getGeographyMappingT());
		card.setId(partner.getPartnerId());
		card.setLogo(partner.getLogo());
		card.setName(partner.getPartnerName());
		card.setCreatedModifiedDatetime(partner.getCreatedModifiedDatetime());
		card.setOpportunities(partner.getOpportunityPartnerLinkTs().size());
		card.setConnects(partner.getConnectTs().size());
		card.setEntityType(Constants.EntityType.PARTNER.toString());
		return card;
	}

	public static CustPartResultCard convertToCard(CustomerMasterT customer) {
		CustPartResultCard card = new CustPartResultCard();
		card.setGroupCustomerName(customer.getGroupCustomerName());
		card.setGeographyMappingT(customer.getGeographyMappingT());
		card.setId(customer.getCustomerId());
		card.setLogo(customer.getLogo());
		card.setName(customer.getCustomerName());
		card.setCreatedModifiedDatetime(customer.getCreatedModifiedDatetime());
		card.setOpportunities(customer.getOpportunityTs().size());
		card.setConnects(customer.getConnectTs().size());
		card.setEntityType(Constants.EntityType.CUSTOMER.toString());
		return card;
	}

	public static String filterJsonForFieldAndViews(String fields,String view,Object object){
		if(!view.equals("")){
			StringTokenizer st = new StringTokenizer(view, ",");
			while (st.hasMoreTokens()) {
				//TODO:check and add the Fields based on View
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
}
