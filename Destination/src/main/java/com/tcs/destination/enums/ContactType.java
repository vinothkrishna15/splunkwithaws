package com.tcs.destination.enums;

public enum ContactType {
	INTERNAL("INTERNAL"), EXTERNAL("EXTERNAL");

	private final String name;

	private ContactType(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String test) {

		for (ContactType c : ContactType.values()) {
			if (c.name().equals(test)) {
				return true;
			}
		}

		return false;
	}

}
