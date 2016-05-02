package com.tcs.destination.enums;

public enum UserGroup {
	BDM("BDM"), BDM_SUPERVISOR("BDM Supervisor"), BID_OFFICE(
			"Bid Office"), GEO_HEADS("GEO Heads"), STRATEGIC_INITIATIVES("Strategic Initiatives"),
			SYSTEM("System"), IOU_HEADS("IOU Heads"), PRACTICE_HEAD("Practice Head"), PRACTICE_OWNER("Practice Owner"),
			REPORTING_TEAM("Reporting Team"), PMO("PMO");

	private final String value;

	private UserGroup(String value) {
		this.value = value;
	}

	public boolean equalsName(String otherValue) {
		return (otherValue == null) ? false : value.equals(otherValue);
	}

	public String toString() {
		return value;
	}

	public static boolean contains(String otherValue) {
		for (UserGroup c : UserGroup.values()) {
			if (c.getValue().equals(otherValue)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getName(String value){
		for (UserGroup c : UserGroup.values()) {
			if (c.getValue().equals(value)) {
				return c.name();
			}
		}
		return null;
	}
	
	public String getValue() {
		return value;
	}
}