package com.tcs.destination.enums;

public enum PlaceType {
	TCS("TCS"), CLIENT("CLIENT");

	private final String name;

	private PlaceType(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String type) {
		for (PlaceType status : PlaceType.values()) {
			if (status.name().equals(type)) {
				return true;
			}
		}
		return false;
	}
}
