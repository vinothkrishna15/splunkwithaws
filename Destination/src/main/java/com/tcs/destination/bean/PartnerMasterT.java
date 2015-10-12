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

import org.neo4j.cypher.internal.compiler.v2_1.ast.False;

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

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name = "documents_attached")
	private String documentsAttached;

	private String facebook;

	private byte[] logo;

	@Column(insertable = false, updatable = false)
	private String geography;

	@Column(name = "partner_name")
	private String partnerName;

	private String website;

	// bi-directional many-to-one association to CommentsT
	@OneToMany(mappedBy = "partnerMasterT")
	private List<CommentsT> commentsTs;

	// bi-directional many-to-one association to ConnectT
	@OneToMany(mappedBy = "partnerMasterT")
	@OrderBy("start_datetime_of_connect DESC")
	private List<ConnectT> connectTs;

	// bi-directional many-to-one association to ContactT
	@OneToMany(mappedBy = "partnerMasterT")
	@OrderBy("contact_name ASC")
	private List<ContactT> contactTs;

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
	@JoinColumn(name = "geography")
	private GeographyMappingT geographyMappingT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_modified_by", insertable = false, updatable = false)
	private UserT createdModifiedByUser;

	// bi-directional many-to-one association to UserFavoritesT
	@OneToMany(mappedBy = "partnerMasterT")
	private List<UserFavoritesT> userFavoritesTs;

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

	public List<ContactT> getContactTs() {
		return this.contactTs;
	}

	public void setContactTs(List<ContactT> contactTs) {
		this.contactTs = contactTs;
	}

	public ContactT addContactT(ContactT contactT) {
		getContactTs().add(contactT);
		contactT.setPartnerMasterT(this);

		return contactT;
	}

	public ContactT removeContactT(ContactT contactT) {
		getContactTs().remove(contactT);
		contactT.setPartnerMasterT(null);

		return contactT;
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

	public UserT getCreatedModifiedByUser() {
		return this.createdModifiedByUser;
	}

	public void setCreatedModifiedByUser(UserT createdModifiedByUser) {
		this.createdModifiedByUser = createdModifiedByUser;
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

}