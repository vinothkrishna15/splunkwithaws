package com.tcs.destination.enums;

import com.tcs.destination.utils.Constants.EntityType;

public enum OpportunityRole {
	PRIMARY_OWNER("PRIMARY_OWNER"), SALES_SUPPORT("SALES_SUPPORT"), BID_OFFICE(
			"BID_OFFICE"), ALL("ALL");

	private final String name;

	private OpportunityRole(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String test) {

		for (OpportunityRole c : OpportunityRole.values()) {
			if (c.name().equals(test)) {
				return true;
			}
		}

		return false;
	}

}
