package com.tcs.destination.service;

import java.util.List;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	public Object recentlyAdded(String entityType, int count) throws Exception{
		if (Constants.EntityType.contains(entityType)) {
			if (entityType.equalsIgnoreCase(Constants.EntityType.CUSTOMER
					.toString())){
				List<CustomerMasterT> recentCustomers = customerRepository
						.findRecent(count);
				if (recentCustomers.isEmpty()) {
					throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
				}
				return recentCustomers;
			} else if (entityType.equalsIgnoreCase(Constants.EntityType.PARTNER
					.toString())) {
				List<PartnerMasterT> recentpartners = partnerRepository
						.findRecent(count);
				if (recentpartners.isEmpty()) {
					throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
				}
				return recentpartners;
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
			}
		}
		throw new DestinationException(HttpStatus.BAD_REQUEST,"No such Entity type exists. Please ensure your entity type.");
	}

}