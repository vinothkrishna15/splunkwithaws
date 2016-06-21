package com.tcs.destination.enums;

/**
 * @author TCS
 * Enumeration for Recipient type
 */
public enum RecipientType {
	OWNER, 
	SUPERVISOR,
	FOLLOWER,
	BDM_TAGGED,
	SUBSCRIBER,
	STRATEGIC_INITIATIVE;

	public static boolean contains(String value) {
		return (getByValue(value) != null);
	}
	
	public static RecipientType getByValue(String value) {
		
		for (RecipientType c : RecipientType.values()) {
			if (c.name().equals(value)) {
				return c;
			}
		}
		return null;
	}

}
