package com.tcs.destination.enums;

import com.tcs.destination.utils.Constants;

public enum RequestType {
	
	USER_UPLOAD(1, EntityType.USER, Constants.USER_UPLOAD_SUBJECT, Constants.USER_UPLOAD_NOTIFY_SUBJECT),
	CUSTOMER_UPLOAD(2, EntityType.CUSTOMER, Constants.CUSTOMER_UPLOAD_SUBJECT, Constants.CUSTOMER_UPLOAD_NOTIFY_SUBJECT),
	CONNECT_UPLOAD(3, EntityType.CONNECT, Constants.CONNECT_UPLOAD_SUBJECT, Constants.CONNECT_UPLOAD_NOTIFY_SUBJECT),
	OPPORTUNITY_UPLOAD(4, EntityType.OPPORTUNITY, Constants.OPPORTUNITY_UPLOAD_SUBJECT, Constants.OPPORTUNITY_UPLOAD_NOTIFY_SUBJECT),
	ACTUAL_REVENUE_UPLOAD(5, EntityType.ACTUAL_REVENUE, Constants.ACTUAL_REVENUE_UPLOAD_SUBJECT, Constants.ACTUAL_REVENUE_UPLOAD_NOTIFY_SUBJECT),
	CUSTOMER_CONTACT_UPLOAD(6, EntityType.CUSTOMER_CONTACT, Constants.CUSTOMER_CONTACT_UPLOAD_SUBJECT, Constants.CUSTOMER_CONTACT_UPLOAD_NOTIFY_SUBJECT),
	PARTNER_UPLOAD(7, EntityType.PARTNER, Constants.PARTNER_UPLOAD_SUBJECT, Constants.PARTNER_UPLOAD_NOTIFY_SUBJECT),
	PARTNER_CONTACT_UPLOAD(8, EntityType.PARTNER_CONTACT, Constants.PARTNER_CONTACT_UPLOAD_SUBJECT, Constants.PARTNER_CONTACT_UPLOAD_NOTIFY_SUBJECT),
	BEACON_UPLOAD(9, EntityType.BEACON, Constants.BEACON_UPLOAD_SUBJECT, Constants.BEACON_UPLOAD_NOTIFY_SUBJECT),
	USER_DOWNLOAD(10, EntityType.USER, Constants.USER_DOWNLOAD_SUBJECT),
	CUSTOMER_DOWNLOAD(11, EntityType.CUSTOMER, Constants.CUSTOMER_DOWNLOAD_SUBJECT),
	CONNECT_DOWNLOAD(12, EntityType.CONNECT, Constants.CONNECT_DOWNLOAD_SUBJECT),
	OPPORTUNITY_DOWNLOAD(13, EntityType.OPPORTUNITY, Constants.OPPORTUNITY_DOWNLOAD_SUBJECT),
	ACTUAL_REVENUE_DOWNLOAD(14, EntityType.ACTUAL_REVENUE, Constants.ACTUAL_REVENUE_DOWNLOAD_SUBJECT),
	CUSTOMER_CONTACT_DOWNLOAD(15, EntityType.CUSTOMER_CONTACT, Constants.CUSTOMER_CONTACT_DOWNLOAD_SUBJECT),
	PARTNER_DOWNLOAD(16, EntityType.PARTNER, Constants.PARTNER_DOWNLOAD_SUBJECT),
	PARTNER_CONTACT_DOWNLOAD(17, EntityType.PARTNER_CONTACT, Constants.PARTNER_CONTACT_DOWNLOAD_SUBJECT),
	BEACON_DOWNLOAD(18, EntityType.BEACON, Constants.BEACON_DOWNLOAD_SUBJECT),
	OPPORTUNITY_DAILY_DOWNLOAD(19, EntityType.OPPORTUNITY, Constants.OPPORTUNITY_DAILY_DOWNLOAD_SUBJECT),
	
	//added for partner changes
	PRODUCT_UPLOAD(20,EntityType.PRODUCT,Constants.PRODUCT_UPLOAD_SUBJECT),
	PRODUCT_DOWNLOAD(21,EntityType.PRODUCT,Constants.PRODUCT_DOWNLOAD_SUBJECT),
	PRODUCT_CONTACT_UPLOAD(22,EntityType.PRODUCT_CONTACT,Constants.PRODUCT_CONTACT_UPLOAD_SUBJECT),
	PRODUCT_CONTACT_DOWNLOAD(23,EntityType.PRODUCT_CONTACT,Constants.PRODUCT_CONTACT_DOWNLOAD_SUBJECT),
	PARTNER_MASTER_UPLOAD(24,EntityType.PARTNER_MASTER,Constants.PARTNER_MASTER_UPLOAD_SUBJECT),
	PARTNER_MASTER_DOWNLOAD(25,EntityType.PARTNER_MASTER,Constants.PARTNER_MASTER_DOWNLOAD_SUBJECT);
	
	
	private final int type;
	private String mailSubject;
	private EntityType entityType;
	private String notifySubject;

	private RequestType(int type) {
		this.type = type;
	}
	
	private RequestType(int type, EntityType entityType, String subject) {
		this.type = type;
		this.mailSubject = subject;
		this.entityType = entityType;
	}
	
	private RequestType(int type, EntityType entityType, String subject, String notifySubject) {
		this.type = type;
		this.mailSubject = subject;
		this.entityType = entityType;
		this.notifySubject = notifySubject;
	}
	
	public int getType() {
		return this.type;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public EntityType getEntityType() {
		return entityType;
	}
	
	public String getNotifySubject() {
		return notifySubject;
	}

	/**
	 * Get {@link RequestType} enum from given type int value
	 * @param type
	 * @return
	 */
	public static RequestType getByType(final int type) {
		for (RequestType reqType : values()) {
			if(reqType.getType() == type) {
				return reqType;
			}
		}
		return null;
	}

}
