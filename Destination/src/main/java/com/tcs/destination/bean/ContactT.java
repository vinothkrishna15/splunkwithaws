package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the contact_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "contactId")
@Entity
@Table(name = "contact_t")
@NamedQuery(name = "ContactT.findAll", query = "SELECT c FROM ContactT c")
public class ContactT implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "contact_id")
	private String contactId;

	@Column(name = "contact_category")
	private String contactCategory;

	@Column(name = "contact_email_id")
	private String contactEmailId;

	@Column(name = "contact_linkedin_profile")
	private String contactLinkedinProfile;

	@Column(name = "contact_name")
	private String contactName;

	@Column(name = "contact_photo")
	private byte[] contactPhoto;

	@Column(name = "contact_role")
	private String contactRole;

	@Column(name = "contact_telephone")
	private String contactTelephone;

	@Column(name = "contact_type")
	private String contactType;

	@Column(name = "employee_number")
	private String employeeNumber;

	@Column(name = "other_role")
	private String otherRole;
	
	@Column(name = "active")
	private boolean active = true;

	// bi-directional many-to-one association to ConnectCustomerContactLinkT
	@OneToMany(mappedBy = "contactT")
	private List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs;

	// bi-directional many-to-one association to ConnectTcsAccountContactLinkT
	@OneToMany(mappedBy = "contactT")
	private List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs;

	@OneToMany(mappedBy = "contactT", cascade = CascadeType.ALL)
	private List<ContactCustomerLinkT> contactCustomerLinkTs;

	@Transient
	private List<ContactCustomerLinkT> deleteContactCustomerLinkTs;

	// bi-directional many-to-one association to ContactRoleMappingT
	@ManyToOne
	@JoinColumn(name = "contact_role", insertable = false, updatable = false)
	private ContactRoleMappingT contactRoleMappingT;

	// bi-directional many-to-one association to OpportunityCustomerContactLinkT
	@OneToMany(mappedBy = "contactT")
	private List<OpportunityCustomerContactLinkT> opportunityCustomerContactLinkTs;

	// bi-directional many-to-one association to
	// OpportunityTcsAccountContactLinkT
	@OneToMany(mappedBy = "contactT")
	private List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs;

	// bi-directional many-to-one association to UserFavoritesT
	@OneToMany(mappedBy = "contactT")
	private List<UserFavoritesT> userFavoritesTs;
	
	@Transient
	private ConnectsSplitDTO tcsAccountContactConnectsDTO; // added for 360 changes
	
	@Transient
	private ConnectsSplitDTO customerContactConnectsDTO; // added for 360 changes
	
	@Transient
	private OpportunitiesSplitDTO tcsAccountContactOpportunitiesDTO; // added for 360 changes
	
	@Transient
	private OpportunitiesSplitDTO customerContactOpportunitiesDTO; // added for 360 changes
	
	//added for partner changes - split of creation and modification
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="created_datetime")
	private Timestamp createdDatetime;
	
	@ManyToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private UserT createdByUser;
	
	@Column(name="modified_by")
	private String modifiedBy;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;
	
	//bi-directional many-to-one association to ProductContactLinkT
	@OneToMany(mappedBy="contactT")
	private List<ProductContactLinkT> productContactLinkTs;
	
	public ConnectsSplitDTO getTcsAccountContactConnectsDTO() {
		return tcsAccountContactConnectsDTO;
	}

	public void setTcsAccountContactConnectsDTO(
			ConnectsSplitDTO tcsAccountContactConnectsDTO) {
		this.tcsAccountContactConnectsDTO = tcsAccountContactConnectsDTO;
	}

	public ConnectsSplitDTO getCustomerContactConnectsDTO() {
		return customerContactConnectsDTO;
	}

	public void setCustomerContactConnectsDTO(
			ConnectsSplitDTO customerContactConnectsDTO) {
		this.customerContactConnectsDTO = customerContactConnectsDTO;
	}

	public OpportunitiesSplitDTO getTcsAccountContactOpportunitiesDTO() {
		return tcsAccountContactOpportunitiesDTO;
	}

	public void setTcsAccountContactOpportunitiesDTO(
			OpportunitiesSplitDTO tcsAccountContactOpportunitiesDTO) {
		this.tcsAccountContactOpportunitiesDTO = tcsAccountContactOpportunitiesDTO;
	}

	public OpportunitiesSplitDTO getCustomerContactOpportunitiesDTO() {
		return customerContactOpportunitiesDTO;
	}

	public void setCustomerContactOpportunitiesDTO(
			OpportunitiesSplitDTO customerContactOpportunitiesDTO) {
		this.customerContactOpportunitiesDTO = customerContactOpportunitiesDTO;
	}

	public ContactT() {
	}

	public String getContactId() {
		return this.contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactCategory() {
		return this.contactCategory;
	}

	public void setContactCategory(String contactCategory) {
		this.contactCategory = contactCategory;
	}

	public String getContactEmailId() {
		return this.contactEmailId;
	}

	public void setContactEmailId(String contactEmailId) {
		this.contactEmailId = contactEmailId;
	}

	public String getContactLinkedinProfile() {
		return this.contactLinkedinProfile;
	}

	public void setContactLinkedinProfile(String contactLinkedinProfile) {
		this.contactLinkedinProfile = contactLinkedinProfile;
	}

	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public byte[] getContactPhoto() {
		return this.contactPhoto;
	}

	public void setContactPhoto(byte[] contactPhoto) {
		this.contactPhoto = contactPhoto;
	}

	public String getContactTelephone() {
		return this.contactTelephone;
	}

	public void setContactTelephone(String contactTelephone) {
		this.contactTelephone = contactTelephone;
	}

	public String getContactType() {
		return this.contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	
	public String getEmployeeNumber() {
		return this.employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getOtherRole() {
		return this.otherRole;
	}

	public void setOtherRole(String otherRole) {
		this.otherRole = otherRole;
	}

	public List<ConnectCustomerContactLinkT> getConnectCustomerContactLinkTs() {
		return this.connectCustomerContactLinkTs;
	}

	public void setConnectCustomerContactLinkTs(
			List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs) {
		this.connectCustomerContactLinkTs = connectCustomerContactLinkTs;
	}

	public ConnectCustomerContactLinkT addConnectCustomerContactLinkT(
			ConnectCustomerContactLinkT connectCustomerContactLinkT) {
		getConnectCustomerContactLinkTs().add(connectCustomerContactLinkT);
		connectCustomerContactLinkT.setContactT(this);

		return connectCustomerContactLinkT;
	}

	public List<ContactCustomerLinkT> getContactCustomerLinkTs() {
		return this.contactCustomerLinkTs;
	}

	public void setContactCustomerLinkTs(
			List<ContactCustomerLinkT> contactCustomerLinkTs) {
		this.contactCustomerLinkTs = contactCustomerLinkTs;
	}

	public ConnectCustomerContactLinkT removeConnectCustomerContactLinkT(
			ConnectCustomerContactLinkT connectCustomerContactLinkT) {
		getConnectCustomerContactLinkTs().remove(connectCustomerContactLinkT);
		connectCustomerContactLinkT.setContactT(null);

		return connectCustomerContactLinkT;
	}

	public List<ConnectTcsAccountContactLinkT> getConnectTcsAccountContactLinkTs() {
		return this.connectTcsAccountContactLinkTs;
	}

	public ContactCustomerLinkT addContactCustomerLinkT(
			ContactCustomerLinkT contactCustomerLinkT) {
		getContactCustomerLinkTs().add(contactCustomerLinkT);
		contactCustomerLinkT.setContactT(this);

		return contactCustomerLinkT;
	}

	public ContactCustomerLinkT removeContactCustomerLinkT(
			ContactCustomerLinkT contactCustomerLinkT) {
		getContactCustomerLinkTs().remove(contactCustomerLinkT);
		contactCustomerLinkT.setContactT(null);

		return contactCustomerLinkT;
	}

	public void setConnectTcsAccountContactLinkTs(
			List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs) {
		this.connectTcsAccountContactLinkTs = connectTcsAccountContactLinkTs;
	}

	public ConnectTcsAccountContactLinkT addConnectTcsAccountContactLinkT(
			ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT) {
		getConnectTcsAccountContactLinkTs().add(connectTcsAccountContactLinkT);
		connectTcsAccountContactLinkT.setContactT(this);

		return connectTcsAccountContactLinkT;
	}

	public ConnectTcsAccountContactLinkT removeConnectTcsAccountContactLinkT(
			ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT) {
		getConnectTcsAccountContactLinkTs().remove(
				connectTcsAccountContactLinkT);
		connectTcsAccountContactLinkT.setContactT(null);

		return connectTcsAccountContactLinkT;
	}

	public ContactRoleMappingT getContactRoleMappingT() {
		return this.contactRoleMappingT;
	}

	public void setContactRoleMappingT(ContactRoleMappingT contactRoleMappingT) {
		this.contactRoleMappingT = contactRoleMappingT;
	}

	public List<OpportunityCustomerContactLinkT> getOpportunityCustomerContactLinkTs() {
		return this.opportunityCustomerContactLinkTs;
	}

	public void setOpportunityCustomerContactLinkTs(
			List<OpportunityCustomerContactLinkT> opportunityCustomerContactLinkTs) {
		this.opportunityCustomerContactLinkTs = opportunityCustomerContactLinkTs;
	}

	public OpportunityCustomerContactLinkT addOpportunityCustomerContactLinkT(
			OpportunityCustomerContactLinkT opportunityCustomerContactLinkT) {
		getOpportunityCustomerContactLinkTs().add(
				opportunityCustomerContactLinkT);
		opportunityCustomerContactLinkT.setContactT(this);

		return opportunityCustomerContactLinkT;
	}

	public OpportunityCustomerContactLinkT removeOpportunityCustomerContactLinkT(
			OpportunityCustomerContactLinkT opportunityCustomerContactLinkT) {
		getOpportunityCustomerContactLinkTs().remove(
				opportunityCustomerContactLinkT);
		opportunityCustomerContactLinkT.setContactT(null);

		return opportunityCustomerContactLinkT;
	}

	public List<OpportunityTcsAccountContactLinkT> getOpportunityTcsAccountContactLinkTs() {
		return this.opportunityTcsAccountContactLinkTs;
	}

	public void setOpportunityTcsAccountContactLinkTs(
			List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs) {
		this.opportunityTcsAccountContactLinkTs = opportunityTcsAccountContactLinkTs;
	}

	public OpportunityTcsAccountContactLinkT addOpportunityTcsAccountContactLinkT(
			OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT) {
		getOpportunityTcsAccountContactLinkTs().add(
				opportunityTcsAccountContactLinkT);
		opportunityTcsAccountContactLinkT.setContactT(this);

		return opportunityTcsAccountContactLinkT;
	}

	public OpportunityTcsAccountContactLinkT removeOpportunityTcsAccountContactLinkT(
			OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT) {
		getOpportunityTcsAccountContactLinkTs().remove(
				opportunityTcsAccountContactLinkT);
		opportunityTcsAccountContactLinkT.setContactT(null);

		return opportunityTcsAccountContactLinkT;
	}

	public List<UserFavoritesT> getUserFavoritesTs() {
		return this.userFavoritesTs;
	}

	public void setUserFavoritesTs(List<UserFavoritesT> userFavoritesTs) {
		this.userFavoritesTs = userFavoritesTs;
	}

	public UserFavoritesT addUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().add(userFavoritesT);
		userFavoritesT.setContactT(this);

		return userFavoritesT;
	}

	public UserFavoritesT removeUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().remove(userFavoritesT);
		userFavoritesT.setContactT(null);

		return userFavoritesT;
	}

	public String getContactRole() {
		return contactRole;
	}

	public void setContactRole(String contactRole) {
		this.contactRole = contactRole;
	}

	public List<ContactCustomerLinkT> getDeleteContactCustomerLinkTs() {
		return deleteContactCustomerLinkTs;
	}

	public void setDeleteContactCustomerLinkTs(
			List<ContactCustomerLinkT> deleteContactCustomerLinkTs) {
		this.deleteContactCustomerLinkTs = deleteContactCustomerLinkTs;
	}

	public ContactT clone() throws CloneNotSupportedException {
		return (ContactT) super.clone();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public List<ProductContactLinkT> getProductContactLinkTs() {
		return productContactLinkTs;
	}

	public void setProductContactLinkTs(
			List<ProductContactLinkT> productContactLinkTs) {
		this.productContactLinkTs = productContactLinkTs;
	}

}