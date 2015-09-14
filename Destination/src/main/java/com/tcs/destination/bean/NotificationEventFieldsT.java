package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the notification_event_fields_t database table.
 * 
 */
@Entity
@Table(name="notification_event_fields_t")
@NamedQuery(name="NotificationEventFieldsT.findAll", query="SELECT n FROM NotificationEventFieldsT n")
public class NotificationEventFieldsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="field_id")
	private Integer fieldId;

	@Column(name="entity_type")
	private String entityType;

	@Column(name="field_name")
	private String fieldName;

	@Column(name="field_type")
	private String fieldType;

	private String isactive;

	@Column(name="message_template")
	private String messageTemplate;

	@Column(name="userid_field")
	private String useridField;

	@Column(name="primary_key_field")
	private String primaryKeyField;

	//bi-directional many-to-one association to NotificationEventFieldsT
	@ManyToOne
	@JoinColumn(name="parent_field_id",insertable=false,updatable=false)
	private NotificationEventFieldsT notificationEventFieldsT;
	
	@Column(name="parent_field_id")
	private Integer parentFieldId;

	//bi-directional many-to-one association to NotificationEventFieldsT
	@OneToMany(mappedBy="notificationEventFieldsT")
	private List<NotificationEventFieldsT> notificationEventFieldsTs;

	//bi-directional many-to-one association to NotificationSettingsEventMappingT
	@ManyToOne
	@JoinColumn(name="notification_event_id",insertable=false,updatable=false)
	private NotificationSettingsEventMappingT notificationSettingsEventMappingT;
	
	@Column(name="notification_event_id")
	private int notificationEventId;

	public NotificationEventFieldsT() {
	}

	public Integer getFieldId() {
		return this.fieldId;
	}

	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return this.fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getIsactive() {
		return this.isactive;
	}

	public void setIsactive(String isactive) {
		this.isactive = isactive;
	}

	public String getMessageTemplate() {
		return this.messageTemplate;
	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public String getUseridField() {
		return this.useridField;
	}

	public void setUseridField(String useridField) {
		this.useridField = useridField;
	}

	public NotificationEventFieldsT getNotificationEventFieldsT() {
		return this.notificationEventFieldsT;
	}

	public void setNotificationEventFieldsT(NotificationEventFieldsT notificationEventFieldsT) {
		this.notificationEventFieldsT = notificationEventFieldsT;
	}

	public Integer getParentFieldId() {
		return parentFieldId;
	}

	public void setParentFieldId(Integer parentFieldId) {
		this.parentFieldId = parentFieldId;
	}

	public List<NotificationEventFieldsT> getNotificationEventFieldsTs() {
		return this.notificationEventFieldsTs;
	}

	public void setNotificationEventFieldsTs(List<NotificationEventFieldsT> notificationEventFieldsTs) {
		this.notificationEventFieldsTs = notificationEventFieldsTs;
	}

	public NotificationEventFieldsT addNotificationEventFieldsT(NotificationEventFieldsT notificationEventFieldsT) {
		getNotificationEventFieldsTs().add(notificationEventFieldsT);
		notificationEventFieldsT.setNotificationEventFieldsT(this);

		return notificationEventFieldsT;
	}

	public NotificationEventFieldsT removeNotificationEventFieldsT(NotificationEventFieldsT notificationEventFieldsT) {
		getNotificationEventFieldsTs().remove(notificationEventFieldsT);
		notificationEventFieldsT.setNotificationEventFieldsT(null);

		return notificationEventFieldsT;
	}

	public NotificationSettingsEventMappingT getNotificationSettingsEventMappingT() {
		return this.notificationSettingsEventMappingT;
	}

	public void setNotificationSettingsEventMappingT(NotificationSettingsEventMappingT notificationSettingsEventMappingT) {
		this.notificationSettingsEventMappingT = notificationSettingsEventMappingT;
	}

	public int getNotificationEventId() {
		return notificationEventId;
	}

	public void setNotificationEventId(int notificationEventId) {
		this.notificationEventId = notificationEventId;
	}

	public String getPrimaryKeyField() {
		return primaryKeyField;
	}

	public void setPrimaryKeyField(String primaryKeyField) {
		this.primaryKeyField = primaryKeyField;
	}
}