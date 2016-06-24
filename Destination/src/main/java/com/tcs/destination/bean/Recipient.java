package com.tcs.destination.bean;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.tcs.destination.enums.OwnerType;
import com.tcs.destination.enums.RecipientType;

/**
 * @author TCS
 * POJO for the recipient
 *
 */
public class Recipient {

	private String id;
	//private RecipientType type;
	private List<Recipient> subodinates;
	//private List<NotificationSettingEvent> subscribedEvents;
	private Map<RecipientType,List<Integer>> events;
	private OwnerType ownerType;
	private boolean isRemoved;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public List<Recipient> getSubodinates() {
		if(subodinates == null) {
			subodinates = Lists.newArrayList();
		}
		return subodinates;
	}
	/*public void setSubodinates(List<Recipient> subodinates) {
		this.subodinates = subodinates;
	}*/
	public Map<RecipientType, List<Integer>> getEvents() {
		return events;
	}
	public void setEvents(Map<RecipientType, List<Integer>> events) {
		this.events = events;
	}
	public OwnerType getOwnerType() {
		return ownerType;
	}
	public void setOwnerType(OwnerType ownerType) {
		this.ownerType = ownerType;
	}
	public boolean isRemoved() {
		return isRemoved;
	}
	public void setRemoved(boolean isRemoved) {
		this.isRemoved = isRemoved;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Recipient other = (Recipient) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
