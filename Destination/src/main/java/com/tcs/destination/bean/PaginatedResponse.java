package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class PaginatedResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<OpportunityT> opportunityTs;

	private List<PartnerMasterT> partnerMasterTs;

	private List<CustomerMasterT> customerMasterTs;

	private List<UserFavoritesT> userFavoritesTs;

	private List<ConnectT> connectTs;
	
	private List<MyWorklistDTO> myWorklists;
	
	private List<WorklistDTO<Object>> worklists;
	
	private List<UserT> userTs;
	
	private List<ContactT> contactTs;
	
	private List<ContactRoleMappingT> contactRoleMappingTs;
	
	private List<DeliveryMasterT> deliveryMasterTs;
	
	private long totalCount;
	
	public List<WorklistDTO<Object>> getWorklists() {
		return worklists;
	}

	public void setWorklists(List<WorklistDTO<Object>> worklists) {
		this.worklists = worklists;
	}
	
	public List<OpportunityT> getOpportunityTs() {
		return opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public List<UserFavoritesT> getUserFavoritesTs() {
		return userFavoritesTs;
	}

	public void setUserFavoritesTs(List<UserFavoritesT> userFavoritesTs) {
		this.userFavoritesTs = userFavoritesTs;
	}

	public List<PartnerMasterT> getPartnerMasterTs() {
		return partnerMasterTs;
	}

	public void setPartnerMasterTs(List<PartnerMasterT> partnerMasterTs) {
		this.partnerMasterTs = partnerMasterTs;
	}

	public List<CustomerMasterT> getCustomerMasterTs() {
		return customerMasterTs;
	}

	public void setCustomerMasterTs(List<CustomerMasterT> customerMasterTs) {
		this.customerMasterTs = customerMasterTs;
	}

	public List<ConnectT> getConnectTs() {
		return connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public List<MyWorklistDTO> getMyWorklists() {
		return myWorklists;
	}

	public void setMyWorklists(List<MyWorklistDTO> myWorklists) {
		this.myWorklists = myWorklists;
	}


	public List<UserT> getUserTs() {
		return userTs;
	}

	public void setUserTs(List<UserT> userTs) {
		this.userTs = userTs;
	}

	public List<ContactT> getContactTs() {
		return contactTs;
	}

	public void setContactTs(List<ContactT> contactTs) {
		this.contactTs = contactTs;
	}
	
	public List<ContactRoleMappingT> getContactRoleMappingTs() {
		return contactRoleMappingTs;
	}

	public void setContactRoleMappingTs(
			List<ContactRoleMappingT> contactRoleMappingTs) {
		this.contactRoleMappingTs = contactRoleMappingTs;

	}
	
	public List<DeliveryMasterT> getDeliveryMasterTs() {
		return deliveryMasterTs;
	}

	public void setDeliveryMasterTs(List<DeliveryMasterT> deliveryMasterTs) {
		this.deliveryMasterTs = deliveryMasterTs;
	}
	
}