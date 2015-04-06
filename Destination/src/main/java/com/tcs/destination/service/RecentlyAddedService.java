package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;

@Component
public class RecentlyAddedService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	public Object recentlyAdded(String entityType, int count) throws Exception {
		if (EntityType.contains(entityType)) {
			switch (EntityType.valueOf(entityType)) {
			case CUSTOMER:

				List<CustomerMasterT> recentCustomers = customerRepository
						.findRecent(count);
				if (recentCustomers.isEmpty()) {
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Relevent Data Found in the database");
				}
				return recentCustomers;
			case PARTNER:

				List<PartnerMasterT> recentpartners = partnerRepository
						.findRecent(count);
				if (recentpartners.isEmpty()) {
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Relevent Data Found in the database");
				}
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