package com.tcs.destination.enums;

public enum TaskStatus {
	Open("Open"), Hold("Hold"), Closed("Closed");

	private final String name;

	private TaskStatus(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String type) {
		for (TaskStatus status : TaskStatus.values()) {
			if (status.name().equals(type)) {
				return true;
			}
		}
		return false;
	}
}
