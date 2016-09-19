package com.tcs.destination.enums;

public enum EntityTypeId {
	
	CUSTOMER(0, "New Customer"), PARTNER(1, "New Partner"), COMPETITOR(2, "New Competitor"), OPPORTUNITY(3, "New Opportunity Reopen"), 
	BFM(4, "New Opportunity Deal Financial"), ESCALATION_A(5, "New Opportunity Deal Financial"), ESCALATION_B(6, "New Opportunity Deal Financial")
	,CONSULTED_ESCALATION_A(7, "New Opportunity Deal Financial"),CONSULTED_ESCALATION_B(8, "New Opportunity Deal Financial");


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
	
	public static EntityTypeId getFrom(Integer type){
		for (EntityTypeId c : EntityTypeId.values()) {
			if (c.getType().equals(type)) {
				return c;
			}
		}
		return null;
	}

}
