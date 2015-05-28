package com.tcs.destination.enums;

public enum FeedbackPriority {
	CRITICAL("CRITICAL"), HIGH("HIGH"), MEDIUM("MEDIUM"), LOW("LOW");

	private final String name;

	private FeedbackPriority(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String type) {
		for (FeedbackPriority status : FeedbackPriority.values()) {
			if (status.name().equals(type)) {
				return true;
			}
		}
		return false;
	}
}
