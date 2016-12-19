package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the group_customer_t database table.
 * 
 */
@Entity
@Table(name="group_customer_t")
@NamedQuery(name="GroupCustomerT.findAll", query="SELECT g FROM GroupCustomerT g")
public class GroupCustomerT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="group_customer_name")
	private String groupCustomerName;

	private byte[] logo;

	//bi-directional many-to-one association to CustomerMasterT
	@OneToMany(mappedBy="groupCustomerT")
	private List<CustomerMasterT> customerMasterTs;

	public GroupCustomerT() {
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

	public List<CustomerMasterT> getCustomerMasterTs() {
		return this.customerMasterTs;
	}

	public void setCustomerMasterTs(List<CustomerMasterT> customerMasterTs) {
		this.customerMasterTs = customerMasterTs;
	}

	public CustomerMasterT addCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().add(customerMasterT);
		customerMasterT.setGroupCustomerT(this);

		return customerMasterT;
	}

	public CustomerMasterT removeCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().remove(customerMasterT);
		customerMasterT.setGroupCustomerT(null);

		return customerMasterT;
	}

}