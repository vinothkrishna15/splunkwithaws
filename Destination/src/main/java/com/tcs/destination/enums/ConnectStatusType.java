package com.tcs.destination.enums;

public enum ConnectStatusType {
	ALL("ALL"), OPEN("OPEN"), CLOSED("CLOSED");

	private final String type;

	private ConnectStatusType(String type){
		this.type=type;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : type.equals(otherName);
	}

	public String toString() {
		return type;
	}

	public static boolean contains(String test) {

		for (ConnectStatusType c : ConnectStatusType.values()) {
			if (c.name().equalsIgnoreCase(test)) {
				return true;
			}
		}

		return false;
	}

	public String getType() {
		return type;
	}

	public static ConnectStatusType getByName(String name) {
		for (ConnectStatusType el : ConnectStatusType.values()) {
			if(el.getType().equals(name)) {
				return el;
			}
		}

		return null;
	}


}
