package com.tcs.destination.enums;


public enum EntityType {
	CUSTOMER("CUSTOMER"), PARTNER("PARTNER"), CONNECT("CONNECT"), COMPETITOR("COMPETITOR"),OPPORTUNITY("OPPORTUNITY"),
	TASK("TASK"), DOCUMENT("DOCUMENT"),	COMMENT("COMMENT"), CONTACT("CONTACT"), USER("USER"),  
	ACTUAL_REVENUE("ACTUAL REVENUE"), CUSTOMER_CONTACT("CUSTOMER CONTACT"), PARTNER_CONTACT("PARTNER CONTACT"), BEACON("BEACON")
	//added for parter changes
	,PRODUCT("PRODUCT"),PRODUCT_CONTACT("PRODUCT_CONTACT"),PARTNER_MASTER("PARTNER_MASTER"),BFM("BFM"),WEEKLY_REPORT("Weekly Report"),
	RGS("RGS"), DELIVERY("DELIVERY"), DELIVERY_INTIMATED("DELIVERY INTIMATED"),
	DELIVERY_CENTRE_UTILIZATION("DELIVERY_CENTRE_UTILIZATION"),
	CUSTOMER_ASSOCIATE("CUSTOMER ASSOCIATE"), DELIVERY_CENTRE_UNALLOCATION("DELIVERY_CENTRE_UNALLOCATION");

	private final String name;

	private EntityType(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public static boolean contains(String test) {

		for (EntityType c : EntityType.values()) {
			if (c.name().equals(test)) {
				return true;
			}
		}

		return false;
	}
	
	public static EntityType getByValue(String value) {
		for (EntityType c : EntityType.values()) {
			if (c.name().equals(value)) {
				return c;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	
}
