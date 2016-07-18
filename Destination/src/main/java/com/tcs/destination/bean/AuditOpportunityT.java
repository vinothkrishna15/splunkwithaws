package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the audit_opportunity_t database table.
 * 
 */
@Entity
@Table(name="audit_opportunity_t")
@NamedQuery(name="AuditOpportunityT.findAll", query="SELECT a FROM AuditOpportunityT a")
public class AuditOpportunityT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_opportunity_id")
	private Long auditOpportunityId;

	@Column(name="new_country")
	private String newCountry;

	@Column(name="new_crm_id")
	private String newCrmId;

	@Temporal(TemporalType.DATE)
	@Column(name="new_deal_closure_date")
	private Date newDealClosureDate;

	@Column(name="new_deal_currency")
	private String newDealCurrency;

	@Column(name="new_deal_type")
	private String newDealType;

	@Column(name="new_description_for_win_loss")
	private String newDescriptionForWinLoss;

	@Column(name="new_digital_deal_value")
	private Integer newDigitalDealValue;

	@Column(name="new_digital_flag")
	private String newDigitalFlag;

	@Column(name="new_documents_attached")
	private String newDocumentsAttached;

	@Column(name="new_engagement_duration")
	private String newEngagementDuration;

	@Temporal(TemporalType.DATE)
	@Column(name="new_engagement_start_date")
	private Date newEngagementStartDate;

	@Column(name="new_modified_by")
	private String newModifiedBy;

	@Column(name="new_modified_datetime")
	private Timestamp newModifiedDatetime;

	@Column(name="new_new_logo")
	private String newNewLogo;

	@Column(name="new_opportunity_description")
	private String newOpportunityDescription;

	@Column(name="new_opportunity_name")
	private String newOpportunityName;

	@Column(name="new_opportunity_owner")
	private String newOpportunityOwner;

	@Column(name="new_overall_deal_size")
	private Integer newOverallDealSize;

	@Column(name="new_sales_stage_code")
	private Integer newSalesStageCode;

	@Column(name="new_strategic_deal")
	private String newStrategicDeal;

	private Boolean notified;

	@Column(name="old_country")
	private String oldCountry;

	@Column(name="old_created_by")
	private String oldCreatedBy;

	@Column(name="old_created_datetime")
	private Timestamp oldCreatedDatetime;

	@Column(name="old_crm_id")
	private String oldCrmId;

	@Column(name="old_customer_id")
	private String oldCustomerId;

	@Temporal(TemporalType.DATE)
	@Column(name="old_deal_closure_date")
	private Date oldDealClosureDate;

	@Column(name="old_deal_currency")
	private String oldDealCurrency;

	@Column(name="old_deal_type")
	private String oldDealType;

	@Column(name="old_description_for_win_loss")
	private String oldDescriptionForWinLoss;

	@Column(name="old_digital_deal_value")
	private Integer oldDigitalDealValue;

	@Column(name="old_digital_flag")
	private String oldDigitalFlag;

	@Column(name="old_documents_attached")
	private String oldDocumentsAttached;

	@Column(name="old_engagement_duration")
	private String oldEngagementDuration;

	@Temporal(TemporalType.DATE)
	@Column(name="old_engagement_start_date")
	private Date oldEngagementStartDate;

	@Column(name="old_new_logo")
	private String oldNewLogo;

	@Column(name="old_opportunity_description")
	private String oldOpportunityDescription;

	@Column(name="old_opportunity_name")
	private String oldOpportunityName;

	@Column(name="old_opportunity_owner")
	private String oldOpportunityOwner;

	@Temporal(TemporalType.DATE)
	@Column(name="old_opportunity_request_receive_date")
	private Date oldOpportunityRequestReceiveDate;

	@Column(name="old_overall_deal_size")
	private Integer oldOverallDealSize;

	@Column(name="old_sales_stage_code")
	private Integer oldSalesStageCode;

	@Column(name="old_strategic_deal")
	private String oldStrategicDeal;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="opportunity_id")
	private String opportunityId;

	public AuditOpportunityT() {
	}

	public Long getAuditOpportunityId() {
		return this.auditOpportunityId;
	}

	public void setAuditOpportunityId(Long auditOpportunityId) {
		this.auditOpportunityId = auditOpportunityId;
	}

	public String getNewCountry() {
		return this.newCountry;
	}

	public void setNewCountry(String newCountry) {
		this.newCountry = newCountry;
	}

	public String getNewCrmId() {
		return this.newCrmId;
	}

	public void setNewCrmId(String newCrmId) {
		this.newCrmId = newCrmId;
	}

	public Date getNewDealClosureDate() {
		return this.newDealClosureDate;
	}

	public void setNewDealClosureDate(Date newDealClosureDate) {
		this.newDealClosureDate = newDealClosureDate;
	}

	public String getNewDealCurrency() {
		return this.newDealCurrency;
	}

	public void setNewDealCurrency(String newDealCurrency) {
		this.newDealCurrency = newDealCurrency;
	}

	public String getNewDealType() {
		return this.newDealType;
	}

	public void setNewDealType(String newDealType) {
		this.newDealType = newDealType;
	}

	public String getNewDescriptionForWinLoss() {
		return this.newDescriptionForWinLoss;
	}

	public void setNewDescriptionForWinLoss(String newDescriptionForWinLoss) {
		this.newDescriptionForWinLoss = newDescriptionForWinLoss;
	}

	public Integer getNewDigitalDealValue() {
		return this.newDigitalDealValue;
	}

	public void setNewDigitalDealValue(Integer newDigitalDealValue) {
		this.newDigitalDealValue = newDigitalDealValue;
	}

	public String getNewDigitalFlag() {
		return this.newDigitalFlag;
	}

	public void setNewDigitalFlag(String newDigitalFlag) {
		this.newDigitalFlag = newDigitalFlag;
	}

	public String getNewDocumentsAttached() {
		return this.newDocumentsAttached;
	}

	public void setNewDocumentsAttached(String newDocumentsAttached) {
		this.newDocumentsAttached = newDocumentsAttached;
	}

	public String getNewEngagementDuration() {
		return this.newEngagementDuration;
	}

	public void setNewEngagementDuration(String newEngagementDuration) {
		this.newEngagementDuration = newEngagementDuration;
	}

	public Date getNewEngagementStartDate() {
		return this.newEngagementStartDate;
	}

	public void setNewEngagementStartDate(Date newEngagementStartDate) {
		this.newEngagementStartDate = newEngagementStartDate;
	}

	public String getNewModifiedBy() {
		return this.newModifiedBy;
	}

	public void setNewModifiedBy(String newModifiedBy) {
		this.newModifiedBy = newModifiedBy;
	}

	public Timestamp getNewModifiedDatetime() {
		return this.newModifiedDatetime;
	}

	public void setNewModifiedDatetime(Timestamp newModifiedDatetime) {
		this.newModifiedDatetime = newModifiedDatetime;
	}

	public String getNewNewLogo() {
		return this.newNewLogo;
	}

	public void setNewNewLogo(String newNewLogo) {
		this.newNewLogo = newNewLogo;
	}

	public String getNewOpportunityDescription() {
		return this.newOpportunityDescription;
	}

	public void setNewOpportunityDescription(String newOpportunityDescription) {
		this.newOpportunityDescription = newOpportunityDescription;
	}

	public String getNewOpportunityName() {
		return this.newOpportunityName;
	}

	public void setNewOpportunityName(String newOpportunityName) {
		this.newOpportunityName = newOpportunityName;
	}

	public String getNewOpportunityOwner() {
		return this.newOpportunityOwner;
	}

	public void setNewOpportunityOwner(String newOpportunityOwner) {
		this.newOpportunityOwner = newOpportunityOwner;
	}

	public Integer getNewOverallDealSize() {
		return this.newOverallDealSize;
	}

	public void setNewOverallDealSize(Integer newOverallDealSize) {
		this.newOverallDealSize = newOverallDealSize;
	}

	public Integer getNewSalesStageCode() {
		return this.newSalesStageCode;
	}

	public void setNewSalesStageCode(Integer newSalesStageCode) {
		this.newSalesStageCode = newSalesStageCode;
	}



	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public String getOldCountry() {
		return this.oldCountry;
	}

	public void setOldCountry(String oldCountry) {
		this.oldCountry = oldCountry;
	}

	public String getOldCreatedBy() {
		return this.oldCreatedBy;
	}

	public void setOldCreatedBy(String oldCreatedBy) {
		this.oldCreatedBy = oldCreatedBy;
	}

	public Timestamp getOldCreatedDatetime() {
		return this.oldCreatedDatetime;
	}

	public void setOldCreatedDatetime(Timestamp oldCreatedDatetime) {
		this.oldCreatedDatetime = oldCreatedDatetime;
	}

	public String getOldCrmId() {
		return this.oldCrmId;
	}

	public void setOldCrmId(String oldCrmId) {
		this.oldCrmId = oldCrmId;
	}

	public String getOldCustomerId() {
		return this.oldCustomerId;
	}

	public void setOldCustomerId(String oldCustomerId) {
		this.oldCustomerId = oldCustomerId;
	}

	public Date getOldDealClosureDate() {
		return this.oldDealClosureDate;
	}

	public void setOldDealClosureDate(Date oldDealClosureDate) {
		this.oldDealClosureDate = oldDealClosureDate;
	}

	public String getOldDealCurrency() {
		return this.oldDealCurrency;
	}

	public void setOldDealCurrency(String oldDealCurrency) {
		this.oldDealCurrency = oldDealCurrency;
	}

	public String getOldDealType() {
		return this.oldDealType;
	}

	public void setOldDealType(String oldDealType) {
		this.oldDealType = oldDealType;
	}

	public String getOldDescriptionForWinLoss() {
		return this.oldDescriptionForWinLoss;
	}

	public void setOldDescriptionForWinLoss(String oldDescriptionForWinLoss) {
		this.oldDescriptionForWinLoss = oldDescriptionForWinLoss;
	}

	public Integer getOldDigitalDealValue() {
		return this.oldDigitalDealValue;
	}

	public void setOldDigitalDealValue(Integer oldDigitalDealValue) {
		this.oldDigitalDealValue = oldDigitalDealValue;
	}

	public String getOldDigitalFlag() {
		return this.oldDigitalFlag;
	}

	public void setOldDigitalFlag(String oldDigitalFlag) {
		this.oldDigitalFlag = oldDigitalFlag;
	}

	public String getOldDocumentsAttached() {
		return this.oldDocumentsAttached;
	}

	public void setOldDocumentsAttached(String oldDocumentsAttached) {
		this.oldDocumentsAttached = oldDocumentsAttached;
	}

	public String getOldEngagementDuration() {
		return this.oldEngagementDuration;
	}

	public void setOldEngagementDuration(String oldEngagementDuration) {
		this.oldEngagementDuration = oldEngagementDuration;
	}

	public Date getOldEngagementStartDate() {
		return this.oldEngagementStartDate;
	}

	public void setOldEngagementStartDate(Date oldEngagementStartDate) {
		this.oldEngagementStartDate = oldEngagementStartDate;
	}

	public String getOldNewLogo() {
		return this.oldNewLogo;
	}

	public void setOldNewLogo(String oldNewLogo) {
		this.oldNewLogo = oldNewLogo;
	}

	public String getOldOpportunityDescription() {
		return this.oldOpportunityDescription;
	}

	public void setOldOpportunityDescription(String oldOpportunityDescription) {
		this.oldOpportunityDescription = oldOpportunityDescription;
	}

	public String getOldOpportunityName() {
		return this.oldOpportunityName;
	}

	public void setOldOpportunityName(String oldOpportunityName) {
		this.oldOpportunityName = oldOpportunityName;
	}

	public String getOldOpportunityOwner() {
		return this.oldOpportunityOwner;
	}

	public void setOldOpportunityOwner(String oldOpportunityOwner) {
		this.oldOpportunityOwner = oldOpportunityOwner;
	}

	public Date getOldOpportunityRequestReceiveDate() {
		return this.oldOpportunityRequestReceiveDate;
	}

	public void setOldOpportunityRequestReceiveDate(Date oldOpportunityRequestReceiveDate) {
		this.oldOpportunityRequestReceiveDate = oldOpportunityRequestReceiveDate;
	}

	public Integer getOldOverallDealSize() {
		return this.oldOverallDealSize;
	}

	public void setOldOverallDealSize(Integer oldOverallDealSize) {
		this.oldOverallDealSize = oldOverallDealSize;
	}

	public Integer getOldSalesStageCode() {
		return this.oldSalesStageCode;
	}

	public void setOldSalesStageCode(Integer oldSalesStageCode) {
		this.oldSalesStageCode = oldSalesStageCode;
	}

	public String getNewStrategicDeal() {
		return newStrategicDeal;
	}

	public void setNewStrategicDeal(String newStrategicDeal) {
		this.newStrategicDeal = newStrategicDeal;
	}

	public String getOldStrategicDeal() {
		return oldStrategicDeal;
	}

	public void setOldStrategicDeal(String oldStrategicDeal) {
		this.oldStrategicDeal = oldStrategicDeal;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getOpportunityId() {
		return this.opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

}