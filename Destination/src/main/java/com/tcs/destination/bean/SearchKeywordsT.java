package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the search_keywords_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "searchKeywordsId")
@Entity
@Table(name = "search_keywords_t")
@NamedQuery(name = "SearchKeywordsT.findAll", query = "SELECT s FROM SearchKeywordsT s")
public class SearchKeywordsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "search_keywords_id")
	private String searchKeywordsId;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name = "entity_id")
	private String entityId;

	@Column(name = "entity_type")
	private String entityType;

	@Column(name = "search_keywords")
	private String searchKeywords;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_modified_by", insertable = false, updatable = false)
	private UserT userT;

	public SearchKeywordsT() {
	}

	public String getSearchKeywordsId() {
		return this.searchKeywordsId;
	}

	public void setSearchKeywordsId(String searchKeywordsId) {
		this.searchKeywordsId = searchKeywordsId;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getSearchKeywords() {
		return this.searchKeywords;
	}

	public void setSearchKeywords(String searchKeywords) {
		this.searchKeywords = searchKeywords;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public String getCreatedModifiedBy() {
		return createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

}