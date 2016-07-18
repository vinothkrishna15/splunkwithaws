package com.tcs.destination.enums;

public enum NotificationEventGroup {
	OWNER_CHANGE,
	COLLAB_CONDITION,
	DIGITAL_OPPORTUNITIES,
	STRATEGIC_OPPORTUNITIES,
	KEY_CHANGES,
	WIN_LOSS_OPPORTUNITIES,
	FOLLOW,
	COMMENT,
	BDM_TAG;
	
	/**
	 * get the {@link NotificationEventGroup} by string value
	 * @param value
	 * @return
	 */
	public static NotificationEventGroup getByValue(String value) {
		for (NotificationEventGroup c : NotificationEventGroup.values()) {
			if (c.name().equals(value)) {
				return c;
			}
		}
		return null;
	}
}
