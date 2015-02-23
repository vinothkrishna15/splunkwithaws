package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the beacon_convertor_mapping_t database table.
 * 
 */
@Entity
@Table(name="beacon_convertor_mapping_t")
@NamedQuery(name="BeaconConvertorMappingT.findAll", query="SELECT b FROM BeaconConvertorMappingT b")
public class BeaconConvertorMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="currency_name")
	private String currencyName;

	@Column(name="conversion_rate")
	private BigDecimal conversionRate;

	public BeaconConvertorMappingT() {
	}

	public String getCurrencyName() {
		return this.currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public BigDecimal getConversionRate() {
		return this.conversionRate;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}

}