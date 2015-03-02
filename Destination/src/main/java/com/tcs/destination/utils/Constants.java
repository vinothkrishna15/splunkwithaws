package com.tcs.destination.utils;

import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;

public class Constants {

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
		return card;
	}
}
