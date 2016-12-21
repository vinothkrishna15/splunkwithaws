package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CustomerAssociateAllocationDetailsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigInteger numberOfAllocatedAssociates;
	private Date lastModifiedDate;
	public List<CustomerAssociateDTO> customerAssociatesDTO;

	/**
	 * @return the numberOfAllocatedAssociates
	 */
	public BigInteger getNumberOfAllocatedAssociates() {
		return numberOfAllocatedAssociates;
	}

	/**
	 * @param numberOfAllocatedAssociates
	 *            the numberOfAllocatedAssociates to set
	 */
	public void setNumberOfAllocatedAssociates(
			BigInteger numberOfAllocatedAssociates) {
		this.numberOfAllocatedAssociates = numberOfAllocatedAssociates;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate
	 *            the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the customerAssociatesDTO
	 */
	public List<CustomerAssociateDTO> getCustomerAssociatesDTO() {
		return customerAssociatesDTO;
	}

	/**
	 * @param customerAssociatesDTO
	 *            the customerAssociatesDTO to set
	 */
	public void setCustomerAssociatesDTO(
			List<CustomerAssociateDTO> customerAssociatesDTO) {
		this.customerAssociatesDTO = customerAssociatesDTO;
	}

}
