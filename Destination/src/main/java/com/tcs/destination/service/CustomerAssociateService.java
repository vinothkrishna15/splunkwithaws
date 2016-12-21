/**
 * 
 */
package com.tcs.destination.service;

import java.math.BigInteger;
import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.dto.CustomerAssociateAllocationDetailsDTO;
import com.tcs.destination.bean.dto.CustomerAssociateDTO;
import com.tcs.destination.data.repository.CustomerAssociateRepository;

/**
 * This service is used to fetch the customer-associate allocation details.
 *
 */
@Service
public class CustomerAssociateService {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerAssociateService.class);

	@Autowired
	CustomerAssociateRepository customerAssociateRepository;

	/**
	 * Method to fetch the list of associates based on group customer and also
	 * the total number of WON associates as per last uploaded date.
	 * 
	 * @return custAllocationDTO - required details for
	 *         /customerAssociateDetails
	 * @throws Exception
	 */
	public CustomerAssociateAllocationDetailsDTO findAssociatesAllocationDetailsByGroupCustomer()
			throws Exception {
		logger.debug("Inside findAssociatesAllocationDetailsByGroupCustomer() service");

		CustomerAssociateAllocationDetailsDTO custAllocationDTO = new CustomerAssociateAllocationDetailsDTO();
		List<CustomerAssociateDTO> customerAssociateDetails = customerAssociateRepository
				.findAssociatesByGroupCustomer();

		custAllocationDTO.setCustomerAssociatesDTO(customerAssociateDetails);

		List<Object[]> totalWonAssociatesAndModifiedDate = customerAssociateRepository
				.getTotalAssociatesCount();

		if (!totalWonAssociatesAndModifiedDate.isEmpty()
				&& null != totalWonAssociatesAndModifiedDate) {
			Object[] associatesAndDate = totalWonAssociatesAndModifiedDate
					.get(0);
			custAllocationDTO
					.setNumberOfAllocatedAssociates((BigInteger) associatesAndDate[0]);
			custAllocationDTO.setLastModifiedDate(new LocalDate(
					associatesAndDate[1]).toDate());
		}
		return custAllocationDTO;

	}
}
