package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the contact_role_mapping_t database table.
 * 
 */
@Entity
@Table(name="contact_role_mapping_t")
@NamedQuery(name="ContactRoleMappingT.findAll", query="SELECT c FROM ContactRoleMappingT c")
public class ContactRoleMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="contact_role")
	private String contactRole;

	private String active;

	//bi-directional many-to-one association to ContactT
	@OneToMany(mappedBy="contactRoleMappingT")
	private List<ContactT> contactTs;

	public ContactRoleMappingT() {
	}

	public String getContactRole() {
		return this.contactRole;
	}

	public void setContactRole(String contactRole) {
		this.contactRole = contactRole;
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public List<ContactT> getContactTs() {
		return this.contactTs;
	}

	public void setContactTs(List<ContactT> contactTs) {
		this.contactTs = contactTs;
	}

	public ContactT addContactT(ContactT contactT) {
		getContactTs().add(contactT);
		contactT.setContactRoleMappingT(this);

		return contactT;
	}

	public ContactT removeContactT(ContactT contactT) {
		getContactTs().remove(contactT);
		contactT.setContactRoleMappingT(null);

		return contactT;
	}

}