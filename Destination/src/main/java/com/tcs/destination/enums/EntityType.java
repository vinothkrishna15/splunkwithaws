package com.tcs.destination.enums;


public enum EntityType {
	CUSTOMER("CUSTOMER"), PARTNER("PARTNER"),CONNECT("CONNECT"), OPPORTUNITY("OPPORTUNITY"),TASK("TASK"),DOCUMENT("DOCUMENT"),COMMENT("COMMENT");

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
