package com.tcs.destination.enums;

public enum FeedbackStatus {
	NEW("NEW"), INPROGRESS("INPROGRESS"), HOLD("HOLD"), CLOSED("CLOSED"), FIXED(
			"FIXED"), CLARIFICATION_NEEDED("CLARIFICATION_NEEDED"), NOT_AN_ISSUE(
			"NOT_AN_ISSUE"), DUPLICATE("DUPLICATE"), REOPEN("REOPEN");

	private final String name;

	private FeedbackStatus(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String type) {
		for (FeedbackStatus status : FeedbackStatus.values()) {
			if (status.name().equals(type)) {
				return true;
			}
		}
		return false;
	}
}