package com.tcs.destination.enums;

public enum DocumentActionType {
	ADD("ADD"), UPDATE("UPDATE"), DELETE("DELETE");
	
	private final String name;

	private DocumentActionType(String name) {
		this.name = name;
	}
	
	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String value) {
		for (DocumentActionType type : DocumentActionType.values()) {
			if (type.name().equals(value)) {
				return true;
			}
		}
		return false;
	}
}
