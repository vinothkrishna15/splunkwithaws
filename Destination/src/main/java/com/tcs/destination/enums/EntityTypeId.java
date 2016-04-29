package com.tcs.destination.enums;

public enum EntityTypeId {
	CUSTOMER(0, "New Customer"), PARTNER(1, "New Partner"), COMPETITOR(2, "New Competitor");

	private final Integer type;
	private final String displayName;

	private EntityTypeId(Integer type, String displayName) {
		this.type = type;
		this.displayName = displayName;
	}

	public final Integer getType() {
		return type;
	}
	
	public final String getDisplayName() {
		return displayName;
	}

	public static String getName(Integer type){
		for (EntityTypeId c : EntityTypeId.values()) {
			if (c.getType().equals(type)) {
				return c.name();
			}
		}
		return null;
	}

}
