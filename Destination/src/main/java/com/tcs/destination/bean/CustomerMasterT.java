package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the customer_master_t database table.
 * 
 */
@Entity
@Table(name = "customer_master_t")
@NamedQuery(name = "CustomerMasterT.findAll", query = "SELECT c FROM CustomerMasterT c")
public class CustomerMasterT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "corporate_hq_address")
	private String corporateHqAddress;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name = "customer_name")
	private String customerName;

	@Column(name = "documents_attached")
	private String documentsAttached;

	private String facebook;

	@Column(name = "group_customer_name")
	private String groupCustomerName;

	private byte[] logo;

	private String website;

	// bi-directional many-to-one association to ConnectT
	@OneToMany(mappedBy = "customerMasterT")
	private List<ConnectT> connectTs;

	// bi-directional many-to-one association to ContactT
	@OneToMany(mappedBy = "customerMasterT")
	private List<ContactT> contactTs;

	// bi-directional many-to-one association to GeographyMappingT
	@ManyToOne
	@JoinColumn(name = "geography")
	private GeographyMappingT geographyMappingT;

	// bi-directional many-to-one association to IouCustomerMappingT
	@ManyToOne
	@JoinColumn(name = "iou", insertable = false, updatable = false)
	private IouCustomerMappingT iouCustomerMappingT;

	// bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy = "customerMasterT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	// bi-directional many-to-one association to NotesT
	@OneToMany(mappedBy = "customerMasterT")
	private List<NotesT> notesTs;

	// bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy = "customerMasterT")
	private List<OpportunityT> opportunityTs;

	// bi-directional many-to-one association to UserFavoritesT
	@JsonIgnore
	@OneToMany(mappedBy = "customerMasterT")
	private List<UserFavoritesT> userFavoritesTs;

	// bi-directional many-to-one association to RevenueCustomerMappingT
	@JsonIgnore
	@OneToMany(mappedBy = "customerMasterT")
	private List<RevenueCustomerMappingT> revenueCustomerMappingTs;

	// bi-directional many-to-one association to BeaconCustomerMappingT
	@JsonIgnore
	@OneToMany(mappedBy = "customerMasterT")
	private List<BeaconCustomerMappingT> beaconCustomerMappingTs;

	public CustomerMasterT() {
	}

	public String getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCorporateHqAddress() {
		return this.corporateHqAddress;
	}

	public void setCorporateHqAddress(String corporateHqAddress) {
		this.corporateHqAddress = corporateHqAddress;
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

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getDocumentsAttached() {
		return this.documentsAttached;
	}

	public void setDocumentsAttached(String documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	public String getFacebook() {
		return this.facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getGroupCustomerName() {
		return this.groupCustomerName;
	}

	public void setGroupCustomerName(String groupCustomerName) {
		this.groupCustomerName = groupCustomerName;
	}

	public byte[] getLogo() {
		return this.logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public List<ConnectT> getConnectTs() {
		return this.connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public ConnectT addConnectT(ConnectT connectT) {
		getConnectTs().add(connectT);
		connectT.setCustomerMasterT(this);

		return connectT;
	}

	public ConnectT removeConnectT(ConnectT connectT) {
		getConnectTs().remove(connectT);
		connectT.setCustomerMasterT(null);

		return connectT;
	}

	public List<ContactT> getContactTs() {
		return this.contactTs;
	}

	public void setContactTs(List<ContactT> contactTs) {
		this.contactTs = contactTs;
	}

	public ContactT addContactT(ContactT contactT) {
		getContactTs().add(contactT);
		contactT.setCustomerMasterT(this);

		return contactT;
	}

	public ContactT removeContactT(ContactT contactT) {
		getContactTs().remove(contactT);
		contactT.setCustomerMasterT(null);

		return contactT;
	}

	public GeographyMappingT getGeographyMappingT() {
		return this.geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingT geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
	}

	public IouCustomerMappingT getIouCustomerMappingT() {
		return this.iouCustomerMappingT;
	}

	public void setIouCustomerMappingT(IouCustomerMappingT iouCustomerMappingT) {
		this.iouCustomerMappingT = iouCustomerMappingT;
	}

	public List<DocumentRepositoryT> getDocumentRepositoryTs() {
		return this.documentRepositoryTs;
	}

	public void setDocumentRepositoryTs(
			List<DocumentRepositoryT> documentRepositoryTs) {
		this.documentRepositoryTs = documentRepositoryTs;
	}

	public DocumentRepositoryT addDocumentRepositoryT(
			DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().add(documentRepositoryT);
		documentRepositoryT.setCustomerMasterT(this);

		return documentRepositoryT;
	}

	public DocumentRepositoryT removeDocumentRepositoryT(
			DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().remove(documentRepositoryT);
		documentRepositoryT.setCustomerMasterT(null);

		return documentRepositoryT;
	}

	public List<NotesT> getNotesTs() {
		return this.notesTs;
	}

	public void setNotesTs(List<NotesT> notesTs) {
		this.notesTs = notesTs;
	}

	public NotesT addNotesT(NotesT notesT) {
		getNotesTs().add(notesT);
		notesT.setCustomerMasterT(this);

		return notesT;
	}

	public NotesT removeNotesT(NotesT notesT) {
		getNotesTs().remove(notesT);
		notesT.setCustomerMasterT(null);

		return notesT;
	}

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setCustomerMasterT(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setCustomerMasterT(null);

		return opportunityT;
	}

	public List<UserFavoritesT> getUserFavoritesTs() {
		return this.userFavoritesTs;
	}

	public void setUserFavoritesTs(List<UserFavoritesT> userFavoritesTs) {
		this.userFavoritesTs = userFavoritesTs;
	}

	public UserFavoritesT addUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().add(userFavoritesT);
		userFavoritesT.setCustomerMasterT(this);

		return userFavoritesT;
	}

	public UserFavoritesT removeUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().remove(userFavoritesT);
		userFavoritesT.setCustomerMasterT(null);

		return userFavoritesT;
	}

	public List<BeaconCustomerMappingT> getBeaconCustomerMappingTs() {
		return this.beaconCustomerMappingTs;
	}

	public void setBeaconCustomerMappingTs(
			List<BeaconCustomerMappingT> beaconCustomerMappingTs) {
		this.beaconCustomerMappingTs = beaconCustomerMappingTs;
	}

	public BeaconCustomerMappingT addBeaconCustomerMappingT(
			BeaconCustomerMappingT beaconCustomerMappingT) {
		getBeaconCustomerMappingTs().add(beaconCustomerMappingT);
		beaconCustomerMappingT.setCustomerMasterT(this);

		return beaconCustomerMappingT;
	}

	public BeaconCustomerMappingT removeBeaconCustomerMappingT(
			BeaconCustomerMappingT beaconCustomerMappingT) {
		getBeaconCustomerMappingTs().remove(beaconCustomerMappingT);
		beaconCustomerMappingT.setCustomerMasterT(null);

		return beaconCustomerMappingT;
	}

	public List<RevenueCustomerMappingT> getRevenueCustomerMappingTs() {
		return this.revenueCustomerMappingTs;
	}

	public void setRevenueCustomerMappingTs(
			List<RevenueCustomerMappingT> revenueCustomerMappingTs) {
		this.revenueCustomerMappingTs = revenueCustomerMappingTs;
	}

	public RevenueCustomerMappingT addRevenueCustomerMappingT(
			RevenueCustomerMappingT revenueCustomerMappingT) {
		getRevenueCustomerMappingTs().add(revenueCustomerMappingT);
		revenueCustomerMappingT.setCustomerMasterT(this);

		return revenueCustomerMappingT;
	}

	public RevenueCustomerMappingT removeRevenueCustomerMappingT(
			RevenueCustomerMappingT revenueCustomerMappingT) {
		getRevenueCustomerMappingTs().remove(revenueCustomerMappingT);
		revenueCustomerMappingT.setCustomerMasterT(null);

		return revenueCustomerMappingT;
	}

}