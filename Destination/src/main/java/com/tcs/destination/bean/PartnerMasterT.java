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
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the partner_master_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "partnerId")
@Entity
@Table(name = "partner_master_t")
@NamedQuery(name = "PartnerMasterT.findAll", query = "SELECT p FROM PartnerMasterT p")
public class PartnerMasterT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "corporate_hq_address")
	private String corporateHqAddress;

	@Column(name = "documents_attached")
	private String documentsAttached;

	private String facebook;
	
	@Column(name = "active")
	private boolean active = true;

	private byte[] logo;

	private String geography;

	@Column(name = "partner_name")
	private String partnerName;

	private String website;
	
	private String notes;

	// bi-directional many-to-one association to CommentsT
	@OneToMany(mappedBy = "partnerMasterT")
	private List<CommentsT> commentsTs;

	// bi-directional many-to-one association to ConnectT
	@OneToMany(mappedBy = "partnerMasterT")
	@OrderBy("start_datetime_of_connect DESC")
	private List<ConnectT> connectTs;

	// bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy = "partnerMasterT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	// bi-directional many-to-one association to NotesT
	@OneToMany(mappedBy = "partnerMasterT")
	private List<NotesT> notesTs;

	// bi-directional many-to-one association to OpportunityPartnerLinkT
	@OneToMany(mappedBy = "partnerMasterT")
	private List<OpportunityPartnerLinkT> opportunityPartnerLinkTs;

	// bi-directional many-to-one association to GeographyMappingT
	@ManyToOne
	@JoinColumn(name = "geography", insertable = false, updatable = false)
	private GeographyMappingT geographyMappingT;

	

	// bi-directional many-to-one association to UserFavoritesT
	@OneToMany(mappedBy = "partnerMasterT")
	private List<UserFavoritesT> userFavoritesTs;
	
	//added for partner changes - city, country, text1,text2,text3,group partner name,hqpqrtner link id
	private String city;
	
	private String text1;

	private String text2;

	private String text3;
	
	@Column(name="group_partner_name")
	private String groupPartnerName;
	
	private String country;
	
	//bi-directional many-to-one association to GeographyCountryMappingT
	@ManyToOne
	@JoinColumn(name="country")
	private GeographyCountryMappingT geographyCountryMappingT;

	@Column(name="hq_partner_link_id")
	private String hqPartnerLinkId;
	
	//bi-directional many-to-one association to PartnerMasterT
	@ManyToOne
	@JoinColumn(name="hq_partner_link_id", insertable = false, updatable = false)
	private PartnerMasterT partnerMasterT;

	//bi-directional many-to-one association to PartnerMasterT
	@OneToMany(mappedBy="partnerMasterT")
	private List<PartnerMasterT> partnerMasterTs;

	//bi-directional many-to-one association to PartnerSubSpMappingT
	@OneToMany(mappedBy="partnerMasterT")
	private List<PartnerSubSpMappingT> partnerSubSpMappingTs;
	
	//bi-directional many-to-one association to CollaborationCommentT
	@OneToMany(mappedBy="partnerMasterT")
	private List<CollaborationCommentT> collaborationCommentTs;

	
	//added for partner changes - split createdmodifiedby and createdmodifieddatetime
	@Column(name="created_by")
	private String createdBy;
	
	@ManyToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	public PartnerMasterT() {
	}

	public String getPartnerId() {
		return this.partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getCorporateHqAddress() {
		return this.corporateHqAddress;
	}

	public void setCorporateHqAddress(String corporateHqAddress) {
		this.corporateHqAddress = corporateHqAddress;
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

	public byte[] getLogo() {
		return this.logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getPartnerName() {
		return this.partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public List<CommentsT> getCommentsTs() {
		return this.commentsTs;
	}

	public void setCommentsTs(List<CommentsT> commentsTs) {
		this.commentsTs = commentsTs;
	}

	public CommentsT addCommentsT(CommentsT commentsT) {
		getCommentsTs().add(commentsT);
		commentsT.setPartnerMasterT(this);

		return commentsT;
	}

	public CommentsT removeCommentsT(CommentsT commentsT) {
		getCommentsTs().remove(commentsT);
		commentsT.setPartnerMasterT(null);

		return commentsT;
	}

	public List<ConnectT> getConnectTs() {
		return this.connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public ConnectT addConnectT(ConnectT connectT) {
		getConnectTs().add(connectT);
		connectT.setPartnerMasterT(this);

		return connectT;
	}

	public ConnectT removeConnectT(ConnectT connectT) {
		getConnectTs().remove(connectT);
		connectT.setPartnerMasterT(null);

		return connectT;
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
		documentRepositoryT.setPartnerMasterT(this);

		return documentRepositoryT;
	}

	public DocumentRepositoryT removeDocumentRepositoryT(
			DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().remove(documentRepositoryT);
		documentRepositoryT.setPartnerMasterT(null);

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
		notesT.setPartnerMasterT(this);

		return notesT;
	}

	public NotesT removeNotesT(NotesT notesT) {
		getNotesTs().remove(notesT);
		notesT.setPartnerMasterT(null);

		return notesT;
	}

	public List<OpportunityPartnerLinkT> getOpportunityPartnerLinkTs() {
		return this.opportunityPartnerLinkTs;
	}

	public void setOpportunityPartnerLinkTs(
			List<OpportunityPartnerLinkT> opportunityPartnerLinkTs) {
		this.opportunityPartnerLinkTs = opportunityPartnerLinkTs;
	}

	public OpportunityPartnerLinkT addOpportunityPartnerLinkT(
			OpportunityPartnerLinkT opportunityPartnerLinkT) {
		getOpportunityPartnerLinkTs().add(opportunityPartnerLinkT);
		opportunityPartnerLinkT.setPartnerMasterT(this);

		return opportunityPartnerLinkT;
	}

	public OpportunityPartnerLinkT removeOpportunityPartnerLinkT(
			OpportunityPartnerLinkT opportunityPartnerLinkT) {
		getOpportunityPartnerLinkTs().remove(opportunityPartnerLinkT);
		opportunityPartnerLinkT.setPartnerMasterT(null);

		return opportunityPartnerLinkT;
	}

	public GeographyMappingT getGeographyMappingT() {
		return this.geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingT geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
	}

	

	public List<UserFavoritesT> getUserFavoritesTs() {
		return this.userFavoritesTs;
	}

	public void setUserFavoritesTs(List<UserFavoritesT> userFavoritesTs) {
		this.userFavoritesTs = userFavoritesTs;
	}

	public UserFavoritesT addUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().add(userFavoritesT);
		userFavoritesT.setPartnerMasterT(this);

		return userFavoritesT;
	}

	public UserFavoritesT removeUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().remove(userFavoritesT);
		userFavoritesT.setPartnerMasterT(null);

		return userFavoritesT;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	//partner changes - upto modifiedbyuser
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}

	public String getText3() {
		return text3;
	}

	public void setText3(String text3) {
		this.text3 = text3;
	}

	public GeographyCountryMappingT getGeographyCountryMappingT() {
		return geographyCountryMappingT;
	}

	public void setGeographyCountryMappingT(
			GeographyCountryMappingT geographyCountryMappingT) {
		this.geographyCountryMappingT = geographyCountryMappingT;
	}

	public PartnerMasterT getPartnerMasterT() {
		return partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterT partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}

	public List<PartnerMasterT> getPartnerMasterTs() {
		return partnerMasterTs;
	}

	public void setPartnerMasterTs(List<PartnerMasterT> partnerMasterTs) {
		this.partnerMasterTs = partnerMasterTs;
	}
	
	public String getCountry(){
		return country;
	}
	
	public void setCountry(String country){
		this.country = country;
	}
	
	public String getHqPartnerLinkId(){
		return hqPartnerLinkId;
	}
	
	public void setHqPartnerLinkId(String hqPartnerLinkId){
		this.hqPartnerLinkId = hqPartnerLinkId;
	}
	
	
	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public UserT getCreatedByUser() {
		return this.createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getModifiedByUser() {
		return this.modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}
	
}