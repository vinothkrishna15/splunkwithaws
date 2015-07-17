package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the auto_comments_entity_t database table.
 * 
 */
@Entity
@Table(name="auto_comments_entity_t")
@NamedQuery(name="AutoCommentsEntityT.findAll", query="SELECT a FROM AutoCommentsEntityT a")
public class AutoCommentsEntityT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="entity_id")
	private Integer entityId;

	@Column(name="add_message_template")
	private String addMessageTemplate;

	@Column(name="class_name")
	private String className;

	private String isactive;

	private String name;

	//bi-directional many-to-one association to AutoCommentsEntityFieldsT
	// Do not remove FetchType.EAGER as it is required for Auto comments
	@OneToMany(mappedBy="entity", fetch=FetchType.EAGER)
	private List<AutoCommentsEntityFieldsT> entityFields;

	//bi-directional many-to-one association to AutoCommentsEntityFieldsT
	@OneToMany(mappedBy="linkedEntity")
	private List<AutoCommentsEntityFieldsT> linkedEntityFields;

	public AutoCommentsEntityT() {
	}

	public Integer getEntityId() {
		return this.entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public String getAddMessageTemplate() {
		return this.addMessageTemplate;
	}

	public void setAddMessageTemplate(String addMessageTemplate) {
		this.addMessageTemplate = addMessageTemplate;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
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

	public List<AutoCommentsEntityFieldsT> getEntityFields() {
		return this.entityFields;
	}

	public void setEntityFields(List<AutoCommentsEntityFieldsT> entityFields) {
		this.entityFields = entityFields;
	}

	public AutoCommentsEntityFieldsT addEntityFields(AutoCommentsEntityFieldsT autoCommentsEntityFieldsT1) {
		getEntityFields().add(autoCommentsEntityFieldsT1);
		autoCommentsEntityFieldsT1.setEntity(this);

		return autoCommentsEntityFieldsT1;
	}

	public AutoCommentsEntityFieldsT removeEntityFields(AutoCommentsEntityFieldsT autoCommentsEntityFieldsT1) {
		getEntityFields().remove(autoCommentsEntityFieldsT1);
		autoCommentsEntityFieldsT1.setEntity(null);

		return autoCommentsEntityFieldsT1;
	}

	public List<AutoCommentsEntityFieldsT> getLinkedEntityFields() {
		return this.linkedEntityFields;
	}

	public void setLinkedEntityFields(List<AutoCommentsEntityFieldsT> linkedEntityFields) {
		this.linkedEntityFields = linkedEntityFields;
	}

	public AutoCommentsEntityFieldsT addLinkedEntityFields(AutoCommentsEntityFieldsT autoCommentsEntityFieldsT2) {
		getLinkedEntityFields().add(autoCommentsEntityFieldsT2);
		autoCommentsEntityFieldsT2.setLinkedEntity(this);

		return autoCommentsEntityFieldsT2;
	}

	public AutoCommentsEntityFieldsT removeLinkedEntityFields(AutoCommentsEntityFieldsT autoCommentsEntityFieldsT2) {
		getLinkedEntityFields().remove(autoCommentsEntityFieldsT2);
		autoCommentsEntityFieldsT2.setLinkedEntity(null);

		return autoCommentsEntityFieldsT2;
	}

}