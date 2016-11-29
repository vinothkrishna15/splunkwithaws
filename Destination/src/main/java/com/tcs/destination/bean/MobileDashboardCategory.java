package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the mobile_dashboard_category database table.
 * 
 */
@Entity
@Table(name="mobile_dashboard_category")
@NamedQuery(name="MobileDashboardCategory.findAll", query="SELECT m FROM MobileDashboardCategory m")
public class MobileDashboardCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="category_id")
	private Integer categoryId;

	@Column(name="category_name")
	private String categoryName;

	//bi-directional many-to-one association to MobileDashboardT
	@OneToMany(mappedBy="mobileDashboardCategory")
	private List<MobileDashboardT> mobileDashboardTs;

	public MobileDashboardCategory() {
	}

	public Integer getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<MobileDashboardT> getMobileDashboardTs() {
		return this.mobileDashboardTs;
	}

	public void setMobileDashboardTs(List<MobileDashboardT> mobileDashboardTs) {
		this.mobileDashboardTs = mobileDashboardTs;
	}

	public MobileDashboardT addMobileDashboardT(MobileDashboardT mobileDashboardT) {
		getMobileDashboardTs().add(mobileDashboardT);
		mobileDashboardT.setMobileDashboardCategory(this);

		return mobileDashboardT;
	}

	public MobileDashboardT removeMobileDashboardT(MobileDashboardT mobileDashboardT) {
		getMobileDashboardTs().remove(mobileDashboardT);
		mobileDashboardT.setMobileDashboardCategory(null);

		return mobileDashboardT;
	}

}