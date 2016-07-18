package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the operation_event_recipient_mapping_t database table.
 * 
 */
@Entity
@Table(name="operation_event_recipient_mapping_t")
@NamedQuery(name="OperationEventRecipientMappingT.findAll", query="SELECT o FROM OperationEventRecipientMappingT o")
public class OperationEventRecipientMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="operation_event_recipient_mapping_id")
	private Integer operationEventRecipientMappingId;

	@Column(name="condition_subscriber")
	private Boolean conditionSubscriber;

	@Column(name="follower_subscription")
	private Boolean followerSubscription;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="owner_subscription")
	private Boolean ownerSubscription;

	@Column(name="strategic_initiatives")
	private Boolean strategicInitiatives;

	@Column(name="supervisor_subscription")
	private Boolean supervisorSubscription;
	
	@Column(name="bdm_tagged")
	private Boolean bdmTagged;
	
	@Column(name="event_id")
	private Integer eventId;
	
	

	//bi-directional many-to-one association to NotificationSettingsEventMappingT
	@ManyToOne
	@JoinColumn(name="event_id", updatable = false, insertable = false)
	private NotificationSettingsEventMappingT notificationSettingsEventMappingT;

	public OperationEventRecipientMappingT() {
	}

	public Integer getOperationEventRecipientMappingId() {
		return this.operationEventRecipientMappingId;
	}

	public void setOperationEventRecipientMappingId(Integer operationEventRecipientMappingId) {
		this.operationEventRecipientMappingId = operationEventRecipientMappingId;
	}

	public Boolean getConditionSubscriber() {
		return this.conditionSubscriber;
	}

	public void setConditionSubscriber(Boolean conditionSubscriber) {
		this.conditionSubscriber = conditionSubscriber;
	}

	public Boolean getFollowerSubscription() {
		return this.followerSubscription;
	}

	public void setFollowerSubscription(Boolean followerSubscription) {
		this.followerSubscription = followerSubscription;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public Boolean getOwnerSubscription() {
		return this.ownerSubscription;
	}
	
	public Boolean isOwnerSubscription() {
		return this.ownerSubscription;
	}

	public void setOwnerSubscription(Boolean ownerSubscription) {
		this.ownerSubscription = ownerSubscription;
	}

	public Boolean getStrategicInitiatives() {
		return this.strategicInitiatives;
	}

	public void setStrategicInitiatives(Boolean strategicInitiatives) {
		this.strategicInitiatives = strategicInitiatives;
	}

	public Boolean getSupervisorSubscription() {
		return this.supervisorSubscription;
	}

	public void setSupervisorSubscription(Boolean supervisorSubscription) {
		this.supervisorSubscription = supervisorSubscription;
	}

	public NotificationSettingsEventMappingT getNotificationSettingsEventMappingT() {
		return this.notificationSettingsEventMappingT;
	}

	public void setNotificationSettingsEventMappingT(NotificationSettingsEventMappingT notificationSettingsEventMappingT) {
		this.notificationSettingsEventMappingT = notificationSettingsEventMappingT;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public Boolean getBdmTagged() {
		return bdmTagged;
	}

	public void setBdmTagged(Boolean bdmTagged) {
		this.bdmTagged = bdmTagged;
	}
	
	
	
	

}