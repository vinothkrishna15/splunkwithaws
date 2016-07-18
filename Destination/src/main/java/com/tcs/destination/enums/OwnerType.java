package com.tcs.destination.enums;



public enum OwnerType {
	PRIMARY("PRIMARY"), SECONDARY("SECONDARY"), ALL("ALL"), OTHER("OTHER"),
	PRIMARY_OWNER("Primary Owner"),
	SECONDARY_OWNER("Secondary Owner"),
	SALES_SUPPORT_OWNER("Sales Support Owner"),
	BID_OFFICE_GROUP_OWNER("Bid Office Group Owner");

	private final String name;

	private OwnerType(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String test) {
		for (OwnerType c : OwnerType.values()) {
			if (c.name().equals(test)) {
				return true;
			}
		}

		return false;
	}

	public String getName() {
		return name;
	}
	
	

}
