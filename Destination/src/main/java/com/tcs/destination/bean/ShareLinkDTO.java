package com.tcs.destination.bean;

import java.util.List;

public class ShareLinkDTO {

	private String entityType;
	
	private String entityId;
	
	private String url;
	
	private List<String> recipientIds;
	
	private boolean portalNotify;
	
	private boolean emailNotify;

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getRecipientIds() {
		return recipientIds;
	}

	public void setRecipientIds(List<String> recipientIds) {
		this.recipientIds = recipientIds;
	}

	public boolean getPortalNotify() {
		return portalNotify;
	}

	public void setPortalNotify(boolean isPortalNotify) {
		this.portalNotify = isPortalNotify;
	}

	public boolean getEmailNotify() {
		return emailNotify;
	}

	public void setEmailNotify(boolean isEmailNotify) {
		this.emailNotify = isEmailNotify;
	}
	
}
