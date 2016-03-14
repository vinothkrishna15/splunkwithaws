package com.tcs.destination.enums;

public enum EntityTypeId {
	CUSTOMER(0), PARTNER(1), COMPETITOR(2);

	private final Integer type;

	private EntityTypeId(Integer type) {
		this.type = type;
	}

	public Integer getType() {
		return type;
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
