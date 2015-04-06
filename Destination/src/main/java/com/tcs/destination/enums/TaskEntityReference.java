package com.tcs.destination.enums;




public enum TaskEntityReference {
	Connect("Connect"), Opportunity("Opportunity");

	private final String name;

	private TaskEntityReference(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}
	
	public static boolean contains(String type) {
		for (TaskEntityReference entityRef : TaskEntityReference.values()) {
			if (entityRef.name().equals(type)) {
				return true;
			}
		}
		return false;
	}
}
