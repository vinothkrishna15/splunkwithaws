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
 * The persistent class for the audit_delivery_master_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "auditDeliveryMasterId")
@Entity
@Table(name="audit_delivery_master_t")
@NamedQuery(name="AuditDeliveryMasterT.findAll", query="SELECT a FROM AuditDeliveryMasterT a")
public class AuditDeliveryMasterT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_delivery_master_id")
	private Integer auditDeliveryMasterId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Temporal(TemporalType.DATE)
	@Column(name="new_actual_start_date")
	private Date newActualStartDate;

	@Column(name="new_delivery_centre_id")
	private Integer newDeliveryCentreId;

	@Column(name="new_delivery_partner_id")
	private String newDeliveryPartnerId;

	@Column(name="new_delivery_partner_name")
	private String newDeliveryPartnerName;

	@Column(name="new_delivery_stage")
	private Integer newDeliveryStage;

	@Temporal(TemporalType.DATE)
	@Column(name="new_expected_end_date")
	private Date newExpectedEndDate;

	@Column(name="new_gl_id")
	private String newGlId;

	@Column(name="new_gl_name")
	private String newGlName;

	@Column(name="new_odc")
	private String newOdc;

	@Column(name="new_opportunity_id")
	private String newOpportunityId;

	@Column(name="new_pl_id")
	private String newPlId;

	@Column(name="new_pl_name")
	private String newPlName;

	@Temporal(TemporalType.DATE)
	@Column(name="new_scheduled_start_date")
	private Date newScheduledStartDate;

	@Column(name="new_won_num")
	private String newWonNum;

	@Temporal(TemporalType.DATE)
	@Column(name="old_actual_start_date")
	private Date oldActualStartDate;

	@Column(name="old_delivery_centre_id")
	private Integer oldDeliveryCentreId;

	@Column(name="old_delivery_master_id")
	private Integer oldDeliveryMasterId;

	@Column(name="old_delivery_partner_id")
	private String oldDeliveryPartnerId;

	@Column(name="old_delivery_partner_name")
	private String oldDeliveryPartnerName;

	@Column(name="old_delivery_stage")
	private Integer oldDeliveryStage;

	@Temporal(TemporalType.DATE)
	@Column(name="old_expected_end_date")
	private Date oldExpectedEndDate;

	@Column(name="old_gl_id")
	private String oldGlId;

	@Column(name="old_gl_name")
	private String oldGlName;

	@Column(name="old_odc")
	private String oldOdc;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="old_pl_id")
	private String oldPlId;

	@Column(name="old_pl_name")
	private String oldPlName;

	@Temporal(TemporalType.DATE)
	@Column(name="old_scheduled_start_date")
	private Date oldScheduledStartDate;

	@Column(name="old_won_num")
	private String oldWonNum;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditDeliveryMasterT() {
	}

	public Integer getAuditDeliveryMasterId() {
		return this.auditDeliveryMasterId;
	}

	public void setAuditDeliveryMasterId(Integer auditDeliveryMasterId) {
		this.auditDeliveryMasterId = auditDeliveryMasterId;
	}

	public String getCreatedModifiedBy() {
		return this.createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public Date getNewActualStartDate() {
		return this.newActualStartDate;
	}

	public void setNewActualStartDate(Date newActualStartDate) {
		this.newActualStartDate = newActualStartDate;
	}

	public Integer getNewDeliveryCentreId() {
		return this.newDeliveryCentreId;
	}

	public void setNewDeliveryCentreId(Integer newDeliveryCentreId) {
		this.newDeliveryCentreId = newDeliveryCentreId;
	}

	public String getNewDeliveryPartnerId() {
		return this.newDeliveryPartnerId;
	}

	public void setNewDeliveryPartnerId(String newDeliveryPartnerId) {
		this.newDeliveryPartnerId = newDeliveryPartnerId;
	}

	public String getNewDeliveryPartnerName() {
		return this.newDeliveryPartnerName;
	}

	public void setNewDeliveryPartnerName(String newDeliveryPartnerName) {
		this.newDeliveryPartnerName = newDeliveryPartnerName;
	}

	public Integer getNewDeliveryStage() {
		return this.newDeliveryStage;
	}

	public void setNewDeliveryStage(Integer newDeliveryStage) {
		this.newDeliveryStage = newDeliveryStage;
	}

	public Date getNewExpectedEndDate() {
		return this.newExpectedEndDate;
	}

	public void setNewExpectedEndDate(Date newExpectedEndDate) {
		this.newExpectedEndDate = newExpectedEndDate;
	}

	public String getNewGlId() {
		return this.newGlId;
	}

	public void setNewGlId(String newGlId) {
		this.newGlId = newGlId;
	}

	public String getNewGlName() {
		return this.newGlName;
	}

	public void setNewGlName(String newGlName) {
		this.newGlName = newGlName;
	}

	public String getNewOdc() {
		return this.newOdc;
	}

	public void setNewOdc(String newOdc) {
		this.newOdc = newOdc;
	}

	public String getNewOpportunityId() {
		return this.newOpportunityId;
	}

	public void setNewOpportunityId(String newOpportunityId) {
		this.newOpportunityId = newOpportunityId;
	}

	public String getNewPlId() {
		return this.newPlId;
	}

	public void setNewPlId(String newPlId) {
		this.newPlId = newPlId;
	}

	public String getNewPlName() {
		return this.newPlName;
	}

	public void setNewPlName(String newPlName) {
		this.newPlName = newPlName;
	}

	public Date getNewScheduledStartDate() {
		return this.newScheduledStartDate;
	}

	public void setNewScheduledStartDate(Date newScheduledStartDate) {
		this.newScheduledStartDate = newScheduledStartDate;
	}

	public String getNewWonNum() {
		return this.newWonNum;
	}

	public void setNewWonNum(String newWonNum) {
		this.newWonNum = newWonNum;
	}

	public Date getOldActualStartDate() {
		return this.oldActualStartDate;
	}

	public void setOldActualStartDate(Date oldActualStartDate) {
		this.oldActualStartDate = oldActualStartDate;
	}

	public Integer getOldDeliveryCentreId() {
		return this.oldDeliveryCentreId;
	}

	public void setOldDeliveryCentreId(Integer oldDeliveryCentreId) {
		this.oldDeliveryCentreId = oldDeliveryCentreId;
	}

	public Integer getOldDeliveryMasterId() {
		return this.oldDeliveryMasterId;
	}

	public void setOldDeliveryMasterId(Integer oldDeliveryMasterId) {
		this.oldDeliveryMasterId = oldDeliveryMasterId;
	}

	public String getOldDeliveryPartnerId() {
		return this.oldDeliveryPartnerId;
	}

	public void setOldDeliveryPartnerId(String oldDeliveryPartnerId) {
		this.oldDeliveryPartnerId = oldDeliveryPartnerId;
	}

	public String getOldDeliveryPartnerName() {
		return this.oldDeliveryPartnerName;
	}

	public void setOldDeliveryPartnerName(String oldDeliveryPartnerName) {
		this.oldDeliveryPartnerName = oldDeliveryPartnerName;
	}

	public Integer getOldDeliveryStage() {
		return this.oldDeliveryStage;
	}

	public void setOldDeliveryStage(Integer oldDeliveryStage) {
		this.oldDeliveryStage = oldDeliveryStage;
	}

	public Date getOldExpectedEndDate() {
		return this.oldExpectedEndDate;
	}

	public void setOldExpectedEndDate(Date oldExpectedEndDate) {
		this.oldExpectedEndDate = oldExpectedEndDate;
	}

	public String getOldGlId() {
		return this.oldGlId;
	}

	public void setOldGlId(String oldGlId) {
		this.oldGlId = oldGlId;
	}

	public String getOldGlName() {
		return this.oldGlName;
	}

	public void setOldGlName(String oldGlName) {
		this.oldGlName = oldGlName;
	}

	public String getOldOdc() {
		return this.oldOdc;
	}

	public void setOldOdc(String oldOdc) {
		this.oldOdc = oldOdc;
	}

	public String getOldOpportunityId() {
		return this.oldOpportunityId;
	}

	public void setOldOpportunityId(String oldOpportunityId) {
		this.oldOpportunityId = oldOpportunityId;
	}

	public String getOldPlId() {
		return this.oldPlId;
	}

	public void setOldPlId(String oldPlId) {
		this.oldPlId = oldPlId;
	}

	public String getOldPlName() {
		return this.oldPlName;
	}

	public void setOldPlName(String oldPlName) {
		this.oldPlName = oldPlName;
	}

	public Date getOldScheduledStartDate() {
		return this.oldScheduledStartDate;
	}

	public void setOldScheduledStartDate(Date oldScheduledStartDate) {
		this.oldScheduledStartDate = oldScheduledStartDate;
	}

	public String getOldWonNum() {
		return this.oldWonNum;
	}

	public void setOldWonNum(String oldWonNum) {
		this.oldWonNum = oldWonNum;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}
}