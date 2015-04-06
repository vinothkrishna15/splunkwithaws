package com.tcs.destination.service;

import java.util.List;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoDataFoundException;
import com.tcs.destination.exception.NoSuchEntityException;
import com.tcs.destination.utils.Constants;

@Component
public class RecentlyAddedService {
	
	private static final Logger logger = LoggerFactory.getLogger(RecentlyAddedService.class);

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	public Object recentlyAdded(String entityType, int count) throws Exception{
		logger.debug("Inside recentlyAdded Service");
		if (Constants.EntityType.contains(entityType)) {
			logger.debug("EntityType is Present");
			if (entityType.equalsIgnoreCase(Constants.EntityType.CUSTOMER
					.toString())){
				logger.debug("EntityType of Customer is Found");
				List<CustomerMasterT> recentCustomers = customerRepository
						.findRecent(count);
				if (recentCustomers.isEmpty()) {
					logger.error("NOT_FOUND: No Relevent Data Found in the database");
					throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
				}
				return recentCustomers;
			} else if (entityType.equalsIgnoreCase(Constants.EntityType.PARTNER
					.toString())) {
				logger.debug("EntityType of Partner is Found");
				List<PartnerMasterT> recentpartners = partnerRepository
						.findRecent(count);
				if (recentpartners.isEmpty()) {
					logger.error("NOT_FOUND: No Relevent Data Found in the database");
					throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
				}
				return recentpartners;
			} else {
				logger.error("NOT_FOUND: No Relevent Data Found in the database");
				throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
			}
		}
		logger.error("BAD_REQUEST: No such Entity type exists. Please ensure your entity type.");
		throw new DestinationException(HttpStatus.BAD_REQUEST,"No such Entity type exists. Please ensure your entity type.");
	}

}