package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the user_preferences_t database table.
 * 
 */
@Entity
@Table(name="user_preferences_t")
@NamedQuery(name="UserPreferencesT.findAll", query="SELECT u FROM UserPreferencesT u")
public class UserPreferencesT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="preferences_id", unique=true, nullable=false, length=20)
	private String preferencesId;

	@Column(name="modified_timestamp")
	private Timestamp modifiedTimestamp;

	@Column(name="module_type", length=25)
	private String moduleType;
	
	@Column(name="competitor_name", length=25)
	private String competitorName;
	
	@Column(name="user_id", length=25)
	private String userId;
	
	@Column(name="group_customer_name", length=25)
	private String groupCustomerName;

	//bi-directional many-to-one association to CompetitorMappingT
	@ManyToOne
	@JoinColumn(name="competitor_name", updatable=false, insertable=false)
	private CompetitorMappingT competitorMappingT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id",updatable=false, insertable=false)
	private UserT userT;

	//bi-directional many-to-one association to GroupCustomerT
	@ManyToOne
	@JoinColumn(name="group_customer_name",updatable=false, insertable=false)
	private GroupCustomerT groupCustomerT;

	public UserPreferencesT() {
	}

	public String getPreferencesId() {
		return this.preferencesId;
	}

	public void setPreferencesId(String preferencesId) {
		this.preferencesId = preferencesId;
	}

	public Timestamp getModifiedTimestamp() {
		return this.modifiedTimestamp;
	}

	public void setModifiedTimestamp(Timestamp modifiedTimestamp) {
		this.modifiedTimestamp = modifiedTimestamp;
	}

	public String getModuleType() {
		return this.moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	/**
	 * @return the competitorName
	 */
	public String getCompetitorName() {
		return competitorName;
	}

	/**
	 * @param competitorName the competitorName to set
	 */
	public void setCompetitorName(String competitorName) {
		this.competitorName = competitorName;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the groupCustomerName
	 */
	public String getGroupCustomerName() {
		return groupCustomerName;
	}

	/**
	 * @param groupCustomerName the groupCustomerName to set
	 */
	public void setGroupCustomerName(String groupCustomerName) {
		this.groupCustomerName = groupCustomerName;
	}

	public CompetitorMappingT getCompetitorMappingT() {
		return this.competitorMappingT;
	}

	public void setCompetitorMappingT(CompetitorMappingT competitorMappingT) {
		this.competitorMappingT = competitorMappingT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public GroupCustomerT getGroupCustomerT() {
		return this.groupCustomerT;
	}

	public void setGroupCustomerT(GroupCustomerT groupCustomerT) {
		this.groupCustomerT = groupCustomerT;
	}

}