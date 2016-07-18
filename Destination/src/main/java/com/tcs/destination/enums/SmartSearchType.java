package com.tcs.destination.enums;


public enum SmartSearchType {
	ALL, CONNECT, CUSTOMER, PARTNER, SUBSP,
	EMPNO, EMPNAME,SUPERVISOR,LOCATION,GEOGRAPHY,GROUP_PARTNER_NAME,
	ID, NAME, PRIMARY_OWNER;

	/**
	 * returns {@link SmartSearchType} of the corresponding val
	 * @param val
	 * @return
	 */
	public static SmartSearchType get(String val) {

		for (SmartSearchType c : SmartSearchType.values()) {
			if (c.name().equals(val)) {
				return c;
			}
		}
		return null;
	}

}
