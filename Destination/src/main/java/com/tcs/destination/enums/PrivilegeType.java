package com.tcs.destination.enums;

public enum PrivilegeType {
	GEOGRAPHY("GEOGRAPHY"), SUBSP("SUBSP"), IOU("IOU"), CUSTOMER("CUSTOMER"), GROUP_CUSTOMER("GROUP_CUSTOMER");

	private final String name;

	private PrivilegeType(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String type) {
		for (PrivilegeType status : PrivilegeType.values()) {
			if (status.name().equals(type)) {
				return true;
			}
		}
		return false;
	}
}
