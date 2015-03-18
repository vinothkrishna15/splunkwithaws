package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.exception.NoDataFoundException;
import com.tcs.destination.exception.NoSuchEntityException;
import com.tcs.destination.utils.Constants;

@Component
public class RecentlyAddedService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	public Object recentlyAdded(String entityType, int count) {
		if (Constants.EntityType.contains(entityType)) {
			if (entityType.equalsIgnoreCase(Constants.EntityType.CUSTOMER
					.toString())) {
				List<CustomerMasterT> recentCustomers = customerRepository
						.findRecent(5);
				if (recentCustomers.isEmpty()) {
					throw new NoDataFoundException();
				}
				return recentCustomers;
			} else if (entityType.equalsIgnoreCase(Constants.EntityType.PARTNER
					.toString())) {
				List<PartnerMasterT> recentpartners = partnerRepository
						.findRecent(5);
				if (recentpartners.isEmpty()) {
					throw new NoDataFoundException();
				}
				return recentpartners;
			} else {
				throw new NoDataFoundException();
			}
		}
		throw new NoSuchEntityException();
	}

}