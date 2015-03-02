package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the connect_secondary_owner_link_t database table.
 * 
 */
@Entity
@Table(name="connect_secondary_owner_link_t")
@NamedQuery(name="ConnectSecondaryOwnerLinkT.findAll", query="SELECT c FROM ConnectSecondaryOwnerLinkT c")
public class ConnectSecondaryOwnerLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="connect_secondary_owner_link_id")
	private String connectSecondaryOwnerLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id")
	private ConnectT connectT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="secondary_owner")
	private UserT userT;

	public ConnectSecondaryOwnerLinkT() {
	}

	public String getConnectSecondaryOwnerLinkId() {
		return this.connectSecondaryOwnerLinkId;
	}

	public void setConnectSecondaryOwnerLinkId(String connectSecondaryOwnerLinkId) {
		this.connectSecondaryOwnerLinkId = connectSecondaryOwnerLinkId;
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

	public ConnectT getConnectT() {
		return this.connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}