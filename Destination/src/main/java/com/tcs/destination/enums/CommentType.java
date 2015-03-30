package com.tcs.destination.enums;

import com.tcs.destination.utils.Constants.EntityType;

public enum CommentType {
	USER("USER"), AUTO("AUTO");

	private final String name;

	private CommentType(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String test) {

		for (CommentType c : CommentType.values()) {
			if (c.name().equals(test)) {
				return true;
			}
		}

		return false;
	}

}
