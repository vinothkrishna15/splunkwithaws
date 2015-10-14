package com.tcs.destination.enums;

public enum UserRole {
	USER("User"), SYSTEM_ADMIN("System Admin"), STRATEGIC_GROUP_ADMIN(
			"Strategic Group Admin"), SYSTEM("System");

	private final String value;

	private UserRole(String value) {
		this.value = value;
	}

	public boolean equalsName(String otherValue) {
		return (otherValue == null) ? false : value.equals(otherValue);
	}

	public String toString() {
		return value;
	}

	public static boolean contains(String otherValue) {

		for (UserRole c : UserRole.values()) {
			if (c.getValue().equals(otherValue)) {
				return true;
			}
		}

		return false;
	}
	
	public String getValue() {
		return value;
	}

}
