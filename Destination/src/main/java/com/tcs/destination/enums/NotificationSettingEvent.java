package com.tcs.destination.enums;

import java.util.List;

import com.google.common.collect.Lists;

public enum NotificationSettingEvent {
	OWNER_CHANGE(1,NotificationEventGroup.OWNER_CHANGE),
	TAG_UPDATES_TASK(2, NotificationEventGroup.BDM_TAG),
	FOLLOW_CONNECT_OPPORTUNITY(3, NotificationEventGroup.FOLLOW),
	TASK_CHANGE(4, NotificationEventGroup.KEY_CHANGES),
	TASK_COMPLETION_NEAR_R(5),
	CONNECT_UPDATES_R(6),
	TASK_COMPLETION_PASSED_R(7),
	COMMENT(8, NotificationEventGroup.COMMENT),
	KEY_CHANGES(9, NotificationEventGroup.KEY_CHANGES),
	COLLAB_CONDITION(10, NotificationEventGroup.COLLAB_CONDITION),
	SUBORDINATES_AS_OWNERS(11, NotificationEventGroup.OWNER_CHANGE),
	SUBORDINATES_TASK_COMPLETION_PASSED_R(12),
	COMMENT_ON_SUBORDINATES_ENTITY(13, NotificationEventGroup.COMMENT),
	SUBORDINATES_OPPORTUNITIES_WL(14, NotificationEventGroup.WIN_LOSS_OPPORTUNITIES),
	DIGITAL_OPPORTUNITIES(15, NotificationEventGroup.DIGITAL_OPPORTUNITIES),
	STRATEGIC_OPPORTUNITIES(16, NotificationEventGroup.STRATEGIC_OPPORTUNITIES);
	
	private int eventId;
	private NotificationEventGroup group;
	
	private NotificationSettingEvent(int eventId) {
		this.eventId = eventId;
	}

	private NotificationSettingEvent(int eventId, NotificationEventGroup group) {
		this.eventId = eventId;
		this.group = group;
	}
	
	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	
	public NotificationEventGroup getGroup() {
		return group;
	}

	public void setGroup(NotificationEventGroup group) {
		this.group = group;
	}

	/**
	 * get the {@link NotificationSettingEvent} by string value
	 * @param value
	 * @return
	 */
	public static NotificationSettingEvent getByValue(String value) {
		for (NotificationSettingEvent c : NotificationSettingEvent.values()) {
			if (c.name().equals(value)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * get the {@link NotificationSettingEvent} by string value
	 * @param eventId
	 * @return
	 */
	public static NotificationSettingEvent getByValue(Integer eventId) {
		if(eventId == null) {
			return null;
		}
		for (NotificationSettingEvent c : NotificationSettingEvent.values()) {
			if (c.getEventId() == eventId.intValue()) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * return the list of event for the corresponding group
	 * @param group
	 * @return
	 */
	public static List<NotificationSettingEvent> getByGroup(NotificationEventGroup group) {
		List<NotificationSettingEvent> eventList = Lists.newArrayList();
		for (NotificationSettingEvent c : NotificationSettingEvent.values()) {
			if (c.getGroup() != null && c.getGroup().equals(group)) {
				eventList.add(c);
			}
		}
		return eventList;
	}
	
}
