package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the delivery_pmo_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "deliveryPmoId")
@Entity
@Table(name="delivery_pmo_t")
@NamedQuery(name="DeliveryPmoT.findAll", query="SELECT d FROM DeliveryPmoT d")
public class DeliveryPmoT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="delivery_pmo_id")
	private Integer deliveryPmoId;
	
	@Column(name="delivery_centre_id")
	private Integer deliveryCentreId;
	
	@Column(name="pmo_id")
	private String pmoId;

	//bi-directional many-to-one association to DeliveryCentreT
	@ManyToOne
	@JoinColumn(name="delivery_centre_id", insertable = false, updatable = false)
	private DeliveryCentreT deliveryCentreT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="pmo_id", insertable = false, updatable = false)
	private UserT userT;

	public DeliveryPmoT() {
	}

	public Integer getDeliveryPmoId() {
		return this.deliveryPmoId;
	}

	public void setDeliveryPmoId(Integer deliveryPmoId) {
		this.deliveryPmoId = deliveryPmoId;
	}

	public DeliveryCentreT getDeliveryCentreT() {
		return this.deliveryCentreT;
	}

	public void setDeliveryCentreT(DeliveryCentreT deliveryCentreT) {
		this.deliveryCentreT = deliveryCentreT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public Integer getDeliveryCentreId() {
		return deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public String getPmoId() {
		return pmoId;
	}

	public void setPmoId(String pmoId) {
		this.pmoId = pmoId;
	}
}