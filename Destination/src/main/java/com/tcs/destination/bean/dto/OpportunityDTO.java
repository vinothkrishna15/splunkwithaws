package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The persistent class for the opportunity_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class OpportunityDTO implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private String opportunityId;

	private UserDTO createdByUser;
	private Timestamp createdDatetime;

	private UserDTO modifiedByUser;
	private Timestamp modifiedDatetime;

	private String dealClosureComments;

	private String crmId;

	private Date dealClosureDate;

	private Integer digitalDealValue;

	private String documentsAttached;

	private BigDecimal engagementDuration;

	private Date engagementStartDate;

	private String newLogo;

	private String opportunityDescription;

	private String opportunityName;

	private String dealType;

	private String country;

	private String digitalFlag;

	private Date opportunityRequestReceiveDate;

	private Integer overallDealSize;

	private String strategicDeal;
	
	private Boolean deliveryTeamFlag;

	private String dealCurrency;

	private String isuOwnReason; 
	
	private Integer salesStageCode; 
	private SalesStageMappingDTO salesStageMappingT;
	
	private List<BidDetailsDTO> bidDetailsTs;

	private List<ConnectOpportunityLinkDTO> connectOpportunityLinkIdTs;

	private List<OpportunityCompetitorLinkDTO> opportunityCompetitorLinkTs;

	private List<OpportunityCustomerContactLinkDTO> opportunityCustomerContactLinkTs;

	private List<OpportunityOfferingLinkDTO> opportunityOfferingLinkTs;

	private List<OpportunityPartnerLinkDTO> opportunityPartnerLinkTs;

	private List<OpportunitySalesSupportLinkDTO> opportunitySalesSupportLinkTs;

	private List<OpportunitySubSpLinkDTO> opportunitySubSpLinkTs;

	private CustomerMasterDTO customerMasterT;

	private UserDTO primaryOwnerUser;

	private List<OpportunityTcsAccountContactLinkDTO> opportunityTcsAccountContactLinkTs;

	private DeliveryOwnershipDTO deliveryOwnershipT;

	private List<OpportunityDeliveryCentreMappingDTO> opportunityDeliveryCentreMappingTs;

	private List<OpportunityWinLossFactorsDTO> opportunityWinLossFactorsTs;

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public UserDTO getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserDTO createdByUser) {
		this.createdByUser = createdByUser;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public UserDTO getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserDTO modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public String getDealClosureComments() {
		return dealClosureComments;
	}

	public void setDealClosureComments(String dealClosureComments) {
		this.dealClosureComments = dealClosureComments;
	}

	public String getCrmId() {
		return crmId;
	}

	public void setCrmId(String crmId) {
		this.crmId = crmId;
	}

	public Date getDealClosureDate() {
		return dealClosureDate;
	}

	public void setDealClosureDate(Date dealClosureDate) {
		this.dealClosureDate = dealClosureDate;
	}

	public Integer getDigitalDealValue() {
		return digitalDealValue;
	}

	public void setDigitalDealValue(Integer digitalDealValue) {
		this.digitalDealValue = digitalDealValue;
	}

	public String getDocumentsAttached() {
		return documentsAttached;
	}

	public void setDocumentsAttached(String documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	public BigDecimal getEngagementDuration() {
		return engagementDuration;
	}

	public void setEngagementDuration(BigDecimal engagementDuration) {
		this.engagementDuration = engagementDuration;
	}

	public Date getEngagementStartDate() {
		return engagementStartDate;
	}

	public void setEngagementStartDate(Date engagementStartDate) {
		this.engagementStartDate = engagementStartDate;
	}

	public String getNewLogo() {
		return newLogo;
	}

	public void setNewLogo(String newLogo) {
		this.newLogo = newLogo;
	}

	public String getOpportunityDescription() {
		return opportunityDescription;
	}

	public void setOpportunityDescription(String opportunityDescription) {
		this.opportunityDescription = opportunityDescription;
	}

	public String getOpportunityName() {
		return opportunityName;
	}

	public void setOpportunityName(String opportunityName) {
		this.opportunityName = opportunityName;
	}

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDigitalFlag() {
		return digitalFlag;
	}

	public void setDigitalFlag(String digitalFlag) {
		this.digitalFlag = digitalFlag;
	}

	public Date getOpportunityRequestReceiveDate() {
		return opportunityRequestReceiveDate;
	}

	public void setOpportunityRequestReceiveDate(Date opportunityRequestReceiveDate) {
		this.opportunityRequestReceiveDate = opportunityRequestReceiveDate;
	}

	public Integer getOverallDealSize() {
		return overallDealSize;
	}

	public void setOverallDealSize(Integer overallDealSize) {
		this.overallDealSize = overallDealSize;
	}

	public String getStrategicDeal() {
		return strategicDeal;
	}

	public void setStrategicDeal(String strategicDeal) {
		this.strategicDeal = strategicDeal;
	}

	public Boolean getDeliveryTeamFlag() {
		return deliveryTeamFlag;
	}

	public void setDeliveryTeamFlag(Boolean deliveryTeamFlag) {
		this.deliveryTeamFlag = deliveryTeamFlag;
	}

	public String getDealCurrency() {
		return dealCurrency;
	}

	public void setDealCurrency(String dealCurrency) {
		this.dealCurrency = dealCurrency;
	}

	public String getIsuOwnReason() {
		return isuOwnReason;
	}

	public void setIsuOwnReason(String isuOwnReason) {
		this.isuOwnReason = isuOwnReason;
	}

	public SalesStageMappingDTO getSalesStageMappingT() {
		return salesStageMappingT;
	}

	public void setSalesStageMappingT(SalesStageMappingDTO salesStageMappingT) {
		this.salesStageMappingT = salesStageMappingT;
	}

	public List<BidDetailsDTO> getBidDetailsTs() {
		return bidDetailsTs;
	}

	public void setBidDetailsTs(List<BidDetailsDTO> bidDetailsTs) {
		this.bidDetailsTs = bidDetailsTs;
	}

	public List<ConnectOpportunityLinkDTO> getConnectOpportunityLinkIdTs() {
		return connectOpportunityLinkIdTs;
	}

	public void setConnectOpportunityLinkIdTs(
			List<ConnectOpportunityLinkDTO> connectOpportunityLinkIdTs) {
		this.connectOpportunityLinkIdTs = connectOpportunityLinkIdTs;
	}

	public List<OpportunityCompetitorLinkDTO> getOpportunityCompetitorLinkTs() {
		return opportunityCompetitorLinkTs;
	}

	public void setOpportunityCompetitorLinkTs(
			List<OpportunityCompetitorLinkDTO> opportunityCompetitorLinkTs) {
		this.opportunityCompetitorLinkTs = opportunityCompetitorLinkTs;
	}

	public List<OpportunityCustomerContactLinkDTO> getOpportunityCustomerContactLinkTs() {
		return opportunityCustomerContactLinkTs;
	}

	public void setOpportunityCustomerContactLinkTs(
			List<OpportunityCustomerContactLinkDTO> opportunityCustomerContactLinkTs) {
		this.opportunityCustomerContactLinkTs = opportunityCustomerContactLinkTs;
	}

	public List<OpportunityOfferingLinkDTO> getOpportunityOfferingLinkTs() {
		return opportunityOfferingLinkTs;
	}

	public void setOpportunityOfferingLinkTs(
			List<OpportunityOfferingLinkDTO> opportunityOfferingLinkTs) {
		this.opportunityOfferingLinkTs = opportunityOfferingLinkTs;
	}

	public List<OpportunityPartnerLinkDTO> getOpportunityPartnerLinkTs() {
		return opportunityPartnerLinkTs;
	}

	public void setOpportunityPartnerLinkTs(
			List<OpportunityPartnerLinkDTO> opportunityPartnerLinkTs) {
		this.opportunityPartnerLinkTs = opportunityPartnerLinkTs;
	}

	public List<OpportunitySalesSupportLinkDTO> getOpportunitySalesSupportLinkTs() {
		return opportunitySalesSupportLinkTs;
	}

	public void setOpportunitySalesSupportLinkTs(
			List<OpportunitySalesSupportLinkDTO> opportunitySalesSupportLinkTs) {
		this.opportunitySalesSupportLinkTs = opportunitySalesSupportLinkTs;
	}

	public List<OpportunitySubSpLinkDTO> getOpportunitySubSpLinkTs() {
		return opportunitySubSpLinkTs;
	}

	public void setOpportunitySubSpLinkTs(
			List<OpportunitySubSpLinkDTO> opportunitySubSpLinkTs) {
		this.opportunitySubSpLinkTs = opportunitySubSpLinkTs;
	}

	public CustomerMasterDTO getCustomerMasterT() {
		return customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterDTO customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public UserDTO getPrimaryOwnerUser() {
		return primaryOwnerUser;
	}

	public void setPrimaryOwnerUser(UserDTO primaryOwnerUser) {
		this.primaryOwnerUser = primaryOwnerUser;
	}

	public List<OpportunityTcsAccountContactLinkDTO> getOpportunityTcsAccountContactLinkTs() {
		return opportunityTcsAccountContactLinkTs;
	}

	public void setOpportunityTcsAccountContactLinkTs(
			List<OpportunityTcsAccountContactLinkDTO> opportunityTcsAccountContactLinkTs) {
		this.opportunityTcsAccountContactLinkTs = opportunityTcsAccountContactLinkTs;
	}

	public DeliveryOwnershipDTO getDeliveryOwnershipT() {
		return deliveryOwnershipT;
	}

	public void setDeliveryOwnershipT(DeliveryOwnershipDTO deliveryOwnershipT) {
		this.deliveryOwnershipT = deliveryOwnershipT;
	}

	public List<OpportunityDeliveryCentreMappingDTO> getOpportunityDeliveryCentreMappingTs() {
		return opportunityDeliveryCentreMappingTs;
	}

	public void setOpportunityDeliveryCentreMappingTs(
			List<OpportunityDeliveryCentreMappingDTO> opportunityDeliveryCentreMappingTs) {
		this.opportunityDeliveryCentreMappingTs = opportunityDeliveryCentreMappingTs;
	}

	public List<OpportunityWinLossFactorsDTO> getOpportunityWinLossFactorsTs() {
		return opportunityWinLossFactorsTs;
	}

	public void setOpportunityWinLossFactorsTs(
			List<OpportunityWinLossFactorsDTO> opportunityWinLossFactorsTs) {
		this.opportunityWinLossFactorsTs = opportunityWinLossFactorsTs;
	}

	public Integer getSalesStageCode() {
		return salesStageCode;
	}

	public void setSalesStageCode(Integer salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

}