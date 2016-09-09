package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the delivery_master_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "deliveryMasterId")
@Entity
@Table(name="delivery_master_t")
@NamedQuery(name="DeliveryMasterT.findAll", query="SELECT d FROM DeliveryMasterT d")
public class DeliveryMasterT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="delivery_master_id")
	private Integer deliveryMasterId;

	@Temporal(TemporalType.DATE)
	@Column(name="actual_start_date")
	private Date actualStartDate;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Temporal(TemporalType.DATE)
	@Column(name="expected_end_date")
	private Date expectedEndDate;

	private String fulfilment;

	private String gl;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="num_onsite_offshore")
	private Integer numOnsiteOffshore;

	@Column(name="num_senior_junior")
	private Integer numSeniorJunior;

	@Column(name="num_skilled")
	private Integer numSkilled;

	private String pl;

	@Column(name="rgs_id")
	private String rgsId;

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
	
	@Column(name="delivery_partner")
	private String deliveryPartner;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="modified_by")
	private String modifiedBy;

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

	//bi-directional many-to-one association to PartnerMasterT
	@ManyToOne
	@JoinColumn(name="delivery_partner", insertable = false, updatable = false)
	private PartnerMasterT deliveryPartnerMaster;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	public DeliveryMasterT() {
	}

	public Integer getDeliveryMasterId() {
		return this.deliveryMasterId;
	}

	public void setDeliveryMasterId(Integer deliveryMasterId) {
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

	public String getFulfilment() {
		return this.fulfilment;
	}

	public void setFulfilment(String fulfilment) {
		this.fulfilment = fulfilment;
	}

	public String getGl() {
		return this.gl;
	}

	public void setGl(String gl) {
		this.gl = gl;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public Integer getNumOnsiteOffshore() {
		return this.numOnsiteOffshore;
	}

	public void setNumOnsiteOffshore(Integer numOnsiteOffshore) {
		this.numOnsiteOffshore = numOnsiteOffshore;
	}

	public Integer getNumSeniorJunior() {
		return this.numSeniorJunior;
	}

	public void setNumSeniorJunior(Integer numSeniorJunior) {
		this.numSeniorJunior = numSeniorJunior;
	}

	public Integer getNumSkilled() {
		return this.numSkilled;
	}

	public void setNumSkilled(Integer numSkilled) {
		this.numSkilled = numSkilled;
	}

	public String getPl() {
		return this.pl;
	}

	public void setPl(String pl) {
		this.pl = pl;
	}

	public String getRgsId() {
		return this.rgsId;
	}

	public void setRgsId(String rgsId) {
		this.rgsId = rgsId;
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

	public String getDeliveryPartner() {
		return deliveryPartner;
	}

	public void setDeliveryPartner(String deliveryPartner) {
		this.deliveryPartner = deliveryPartner;
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

	public PartnerMasterT getDeliveryPartnerMaster() {
		return deliveryPartnerMaster;
	}

	public void setDeliveryPartnerMaster(PartnerMasterT deliveryPartnerMaster) {
		this.deliveryPartnerMaster = deliveryPartnerMaster;
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
	
	

}