package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the opportunity_tcs_account_contact_link_t database table.
 * 
 */
@Entity
@Table(name="opportunity_tcs_account_contact_link_t")
@NamedQuery(name="OpportunityTcsAccountContactLinkT.findAll", query="SELECT o FROM OpportunityTcsAccountContactLinkT o")
public class OpportunityTcsAccountContactLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="opportunity_tcs_account_contact_link_id")
	private String opportunityTcsAccountContactLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	//bi-directional many-to-one association to ContactT
	@ManyToOne
	@JoinColumn(name="contact_id")
	private ContactT contactT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	public OpportunityTcsAccountContactLinkT() {
	}

	public String getOpportunityTcsAccountContactLinkId() {
		return this.opportunityTcsAccountContactLinkId;
	}

	public void setOpportunityTcsAccountContactLinkId(String opportunityTcsAccountContactLinkId) {
		this.opportunityTcsAccountContactLinkId = opportunityTcsAccountContactLinkId;
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

	public ContactT getContactT() {
		return this.contactT;
	}

	public void setContactT(ContactT contactT) {
		this.contactT = contactT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

}