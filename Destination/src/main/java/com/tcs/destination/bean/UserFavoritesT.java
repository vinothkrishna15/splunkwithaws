package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the user_favorites_t database table.
 * 
 */
@Entity
@Table(name="user_favorites_t")
@NamedQuery(name="UserFavoritesT.findAll", query="SELECT u FROM UserFavoritesT u")
public class UserFavoritesT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_favorites_id")
	private String userFavoritesId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="entity_type")
	private String entityType;
	
	@Column(name="user_id")
	private String userId;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id")
	private ConnectT connectT;

	//bi-directional many-to-one association to ContactT
	@ManyToOne
	@JoinColumn(name="contact_id")
	private ContactT contactT;

	//bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name="customer_id")
	private CustomerMasterT customerMasterT;

	//bi-directional many-to-one association to DocumentRepositoryT
	@ManyToOne
	@JoinColumn(name="document_id")
	private DocumentRepositoryT documentRepositoryT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to PartnerMasterT
	@ManyToOne
	@JoinColumn(name="partner_id")
	private PartnerMasterT partnerMasterT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id", insertable=false , updatable=false)
	private UserT userT;

	public UserFavoritesT() {
	}

	public String getUserFavoritesId() {
		return this.userFavoritesId;
	}

	public void setUserFavoritesId(String userFavoritesId) {
		this.userFavoritesId = userFavoritesId;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public ConnectT getConnectT() {
		return this.connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

	public ContactT getContactT() {
		return this.contactT;
	}

	public void setContactT(ContactT contactT) {
		this.contactT = contactT;
	}

	public CustomerMasterT getCustomerMasterT() {
		return this.customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public DocumentRepositoryT getDocumentRepositoryT() {
		return this.documentRepositoryT;
	}

	public void setDocumentRepositoryT(DocumentRepositoryT documentRepositoryT) {
		this.documentRepositoryT = documentRepositoryT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public PartnerMasterT getPartnerMasterT() {
		return this.partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterT partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}