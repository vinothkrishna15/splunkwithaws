package com.tcs.destination.enums;

public enum TaskCollaborationPreference {
	Private("Private"), Public("Public"), Restricted("Restricted");

	private final String name;

	private TaskCollaborationPreference(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String type) {
		for (TaskCollaborationPreference pref : TaskCollaborationPreference.values()) {
			if (pref.name().equals(type)) {
				return true;
			}
		}
		return false;
	}
}
