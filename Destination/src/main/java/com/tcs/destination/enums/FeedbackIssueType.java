package com.tcs.destination.enums;

public enum FeedbackIssueType {

	SUGGESTION("SUGGESTION"), ENHANCEMENT("ENHANCEMENT"), BUG("BUG");

	private final String name;

	private FeedbackIssueType(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String test) {
		for (FeedbackIssueType c : FeedbackIssueType.values()) {
			if (c.name().equals(test)) {
				return true;
			}
		}

		return false;
	}
}
