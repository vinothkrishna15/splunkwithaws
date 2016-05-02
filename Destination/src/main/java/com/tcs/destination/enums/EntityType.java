package com.tcs.destination.enums;


public enum EntityType {
	CUSTOMER("CUSTOMER"), PARTNER("PARTNER"), CONNECT("CONNECT"), COMPETITOR("COMPETITOR"),OPPORTUNITY("OPPORTUNITY"),
	TASK("TASK"), DOCUMENT("DOCUMENT"),	COMMENT("COMMENT"), CONTACT("CONTACT"), USER("USER"),  
	ACTUAL_REVENUE("ACTUAL REVENUE"), CUSTOMER_CONTACT("CUSTOMER CONTACT"), PARTNER_CONTACT("PARTNER CONTACT"), BEACON("BEACON");

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
