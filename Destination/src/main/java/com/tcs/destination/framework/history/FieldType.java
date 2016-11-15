package com.tcs.destination.framework.history;


public enum FieldType {
	NORMAL, USER_ID, DELIVERY_CENTRE, DELIVERY_STAGE, OWNERSHIP_ID, CONTACT_ID, PARTNER_ID;
	
	public static FieldType getByName(String val) {
		for (FieldType c : FieldType.values()) {
			if (c.name().equals(val)) {
				return c;
			}
		}
		return null;
	}
}