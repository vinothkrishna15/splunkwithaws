package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the delivery_master_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "deliveryMasterId", scope = DeliveryMasterT.class)
@Entity
@Table(name="delivery_master_t")
@NamedQuery(name="DeliveryMasterT.findAll", query="SELECT d FROM DeliveryMasterT d")
public class DeliveryMasterT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="delivery_master_id")
	private String deliveryMasterId;

	@Temporal(TemporalType.DATE)
	@Column(name="actual_start_date")
	private Date actualStartDate;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Temporal(TemporalType.DATE)
	@Column(name="expected_end_date")
	private Date expectedEndDate;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	@Column(name="gl_id")
	private String glId;

	@Column(name="gl_name")
	private String glName;
	
	@Column(name="pl_id")
	private String plId;

	@Column(name="pl_name")
	private String plName;
	
	private String odc;

	@Temporal(TemporalType.DATE)
	@Column(name="scheduled_start_date")
	private Date scheduledStartDate;

	@Column(name="won_num")
	private String wonNum;
	
	@Column(name="delivery_centre_id")
	private Integer deliveryCentreId;
	
	@Column(name="delivery_stage")
	private Integer deliveryStage;
	
	@Column(name="opportunity_id")
	private String opportunityId;
	
	@Column(name="delivery_partner_id")
	private String deliveryPartnerId;

	@Column(name="delivery_partner_name")
	private String deliveryPartnerName;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Column(name="engagement_name")
	private String engagementName;
	
	@Column(name="comments")
	private String comments;
	
	@Column(name="reason")
	private String reason;
	

	//bi-directional many-to-one association to DeliveryCentreT
	@ManyToOne
	@JoinColumn(name="delivery_centre_id", insertable = false, updatable = false)
	private DeliveryCentreT deliveryCentreT;

	//bi-directional many-to-one association to DeliveryStageMappingT
	@ManyToOne
	@JoinColumn(name="delivery_stage", insertable = false, updatable = false)
	private DeliveryStageMappingT deliveryStageMappingT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;
	
	// bi-directional many-to-one association to DeliveryResourcesT
	@OneToMany(mappedBy = "deliveryMasterT")
	private List<DeliveryResourcesT> deliveryResourcesTs;
	
	// bi-directional many-to-one association to DeliveryMasterManagerLinkT
	@OneToMany(mappedBy = "deliveryMasterT")
	private List<DeliveryMasterManagerLinkT> deliveryMasterManagerLinkTs;

	public DeliveryMasterT() {
	}

	public String getDeliveryMasterId() {
		return this.deliveryMasterId;
	}

	public void setDeliveryMasterId(String deliveryMasterId) {
		this.deliveryMasterId = deliveryMasterId;
	}

	public Date getActualStartDate() {
		return this.actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Date getExpectedEndDate() {
		return this.expectedEndDate;
	}

	public void setExpectedEndDate(Date expectedEndDate) {
		this.expectedEndDate = expectedEndDate;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public Date getScheduledStartDate() {
		return this.scheduledStartDate;
	}

	public void setScheduledStartDate(Date scheduledStartDate) {
		this.scheduledStartDate = scheduledStartDate;
	}

	public String getWonNum() {
		return this.wonNum;
	}

	public void setWonNum(String wonNum) {
		this.wonNum = wonNum;
	}

	public DeliveryCentreT getDeliveryCentreT() {
		return this.deliveryCentreT;
	}

	public void setDeliveryCentreT(DeliveryCentreT deliveryCentreT) {
		this.deliveryCentreT = deliveryCentreT;
	}

	public DeliveryStageMappingT getDeliveryStageMappingT() {
		return this.deliveryStageMappingT;
	}

	public void setDeliveryStageMappingT(DeliveryStageMappingT deliveryStageMappingT) {
		this.deliveryStageMappingT = deliveryStageMappingT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public Integer getDeliveryCentreId() {
		return deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public Integer getDeliveryStage() {
		return deliveryStage;
	}

	public void setDeliveryStage(Integer deliveryStage) {
		this.deliveryStage = deliveryStage;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public List<DeliveryResourcesT> getDeliveryResourcesTs() {
		return deliveryResourcesTs;
	}

	public void setDeliveryResourcesTs(List<DeliveryResourcesT> deliveryResourcesTs) {
		this.deliveryResourcesTs = deliveryResourcesTs;
	}

	public String getGlId() {
		return glId;
	}

	public void setGlId(String glId) {
		this.glId = glId;
	}

	public String getGlName() {
		return glName;
	}

	public void setGlName(String glName) {
		this.glName = glName;
	}

	public String getPlId() {
		return plId;
	}

	public void setPlId(String plId) {
		this.plId = plId;
	}

	public String getPlName() {
		return plName;
	}

	public void setPlName(String plName) {
		this.plName = plName;
	}

	public String getOdc() {
		return odc;
	}

	public void setOdc(String odc) {
		this.odc = odc;
	}

	public String getDeliveryPartnerId() {
		return deliveryPartnerId;
	}

	public void setDeliveryPartnerId(String deliveryPartnerId) {
		this.deliveryPartnerId = deliveryPartnerId;
	}

	public String getDeliveryPartnerName() {
		return deliveryPartnerName;
	}

	public void setDeliveryPartnerName(String deliveryPartnerName) {
		this.deliveryPartnerName = deliveryPartnerName;
	}

	public String getEngagementName() {
		return engagementName;
	}

	public void setEngagementName(String engagementName) {
		this.engagementName = engagementName;
	}

	public List<DeliveryMasterManagerLinkT> getDeliveryMasterManagerLinkTs() {
		return deliveryMasterManagerLinkTs;
	}

	public void setDeliveryMasterManagerLinkTs(
			List<DeliveryMasterManagerLinkT> deliveryMasterManagerLinkTs) {
		this.deliveryMasterManagerLinkTs = deliveryMasterManagerLinkTs;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	
}