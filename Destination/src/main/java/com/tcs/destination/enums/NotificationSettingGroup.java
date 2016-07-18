package com.tcs.destination.enums;

public enum NotificationSettingGroup {
	
	NOTIFICATIONS(1),
	REMINDERS(2),
	COLLABORATION(3),
	SUPERVISOR(4),
	LEADERSHIP(5);
	
    private int groupId;

    
	private NotificationSettingGroup(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
    
	/**
	 * get the {@link NotificationSettingGroup} by Integer value
	 * @param eventId
	 * @return
	 */
	public static NotificationSettingGroup getByValue(Integer groupId) {
		if(groupId == null) {
			return null;
		}
		for (NotificationSettingGroup c : NotificationSettingGroup.values()) {
			if (c.getGroupId() == groupId.intValue()) {
				return c;
			}
		}
		return null;
	}
}
