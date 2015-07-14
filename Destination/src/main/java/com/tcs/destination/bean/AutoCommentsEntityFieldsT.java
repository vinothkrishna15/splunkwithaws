package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the auto_comments_entity_fields_t database table.
 * 
 */
@Entity
@Table(name="auto_comments_entity_fields_t")
@NamedQuery(name="AutoCommentsEntityFieldsT.findAll", query="SELECT a FROM AutoCommentsEntityFieldsT a")
public class AutoCommentsEntityFieldsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="field_id")
	private Integer fieldId;

	@Column(name="add_message_template")
	private String addMessageTemplate;

	private String isactive;

	private String name;

	private String type;

	@Column(name="update_message_template")
	private String updateMessageTemplate;

	@Column(name="entity_id")
	private Integer entityId;
	
	@Column(name="linked_entity_id")
	private Integer linkedEntityId;

	//bi-directional many-to-one association to AutoCommentsEntityT
	@ManyToOne
	@JoinColumn(name="entity_id", insertable=false, updatable=false)
	private AutoCommentsEntityT entity;

	//bi-directional many-to-one association to AutoCommentsEntityT
	@ManyToOne
	@JoinColumn(name="linked_entity_id", insertable=false, updatable=false)
	private AutoCommentsEntityT linkedEntity;

	public AutoCommentsEntityFieldsT() {
	}

	public Integer getFieldId() {
		return this.fieldId;
	}

	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	public String getAddMessageTemplate() {
		return this.addMessageTemplate;
	}

	public void setAddMessageTemplate(String addMessageTemplate) {
		this.addMessageTemplate = addMessageTemplate;
	}

	public String getIsactive() {
		return this.isactive;
	}

	public void setIsactive(String isactive) {
		this.isactive = isactive;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUpdateMessageTemplate() {
		return this.updateMessageTemplate;
	}

	public void setUpdateMessageTemplate(String updateMessageTemplate) {
		this.updateMessageTemplate = updateMessageTemplate;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public Integer getLinkedEntityId() {
		return linkedEntityId;
	}

	public void setLinkedEntityId(Integer linkedEntityId) {
		this.linkedEntityId = linkedEntityId;
	}

	public AutoCommentsEntityT getEntity() {
		return this.entity;
	}

	public void setEntity(AutoCommentsEntityT entity) {
		this.entity = entity;
	}

	public AutoCommentsEntityT getLinkedEntity() {
		return this.linkedEntity;
	}

	public void setLinkedEntity(AutoCommentsEntityT linkedEntity) {
		this.linkedEntity = linkedEntity;
	}
}