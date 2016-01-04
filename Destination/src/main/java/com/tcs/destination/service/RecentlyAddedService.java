package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;

/**
 * This service retrieves the recently added customers,
 * and partners
 */

@Service
public class RecentlyAddedService {
	
	private static final Logger logger = LoggerFactory.getLogger(RecentlyAddedService.class);

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	/**
	 * this method finds the recent customers and partners
	 * @param entityType
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public Object recentlyAdded(String entityType, int count) throws Exception {
		logger.info("Begin: inside recentlyAdded() of RecentlyAddedService");
		if (EntityType.contains(entityType)) {
			switch (EntityType.valueOf(entityType)) {
			case CUSTOMER:

				List<CustomerMasterT> recentCustomers = customerRepository
						.findRecent(count);
				if (recentCustomers.isEmpty()) {
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Relevent Data Found in the database");
				}
				logger.info("End: inside recentlyAdded() CUSTOMER of RecentlyAddedService");
				return recentCustomers;
			case PARTNER:

				List<PartnerMasterT> recentpartners = partnerRepository
						.findRecent(count);
				if (recentpartners.isEmpty()) {
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Relevent Data Found in the database");
				}
				logger.info("End: inside recentlyAdded() PARTNER of RecentlyAddedService");
				return recentpartners;

			default:
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"This Feature is unavailable for " + entityType);

			}
		}
		throw new DestinationException(HttpStatus.NOT_FOUND,
				"No Relevent Data Found in the database");
	}

}