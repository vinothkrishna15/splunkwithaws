package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;


/**
 * The persistent class for the associate_t database table.
 * 
 */
@Entity
@Table(name="associate_t")
@NamedQuery(name="AssociateT.findAll", query="SELECT a FROM AssociateT a")
public class AssociateT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="associate_id")
	private String associateId;

	@Column(name="associate_name")
	private String associateName;

	@Column(name="base_branch")
	private String baseBranch;

	@Column(name="base_country")
	private String baseCountry;

	private String gender;

	//bi-directional many-to-one association to CustomerAssociateT
	@OneToMany(mappedBy="associateT")
	private List<CustomerAssociateT> customerAssociateTs;

	public AssociateT() {
	}

	public String getAssociateId() {
		return this.associateId;
	}

	public void setAssociateId(String associateId) {
		this.associateId = associateId;
	}

	public String getAssociateName() {
		return this.associateName;
	}

	public void setAssociateName(String associateName) {
		this.associateName = associateName;
	}

	public String getBaseBranch() {
		return this.baseBranch;
	}

	public void setBaseBranch(String baseBranch) {
		this.baseBranch = baseBranch;
	}

	public String getBaseCountry() {
		return this.baseCountry;
	}

	public void setBaseCountry(String baseCountry) {
		this.baseCountry = baseCountry;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<CustomerAssociateT> getCustomerAssociateTs() {
		return this.customerAssociateTs;
	}

	public void setCustomerAssociateTs(List<CustomerAssociateT> customerAssociateTs) {
		this.customerAssociateTs = customerAssociateTs;
	}

	public CustomerAssociateT addCustomerAssociateT(CustomerAssociateT customerAssociateT) {
		getCustomerAssociateTs().add(customerAssociateT);
		customerAssociateT.setAssociateT(this);

		return customerAssociateT;
	}

	public CustomerAssociateT removeCustomerAssociateT(CustomerAssociateT customerAssociateT) {
		getCustomerAssociateTs().remove(customerAssociateT);
		customerAssociateT.setAssociateT(null);

		return customerAssociateT;
	}

}