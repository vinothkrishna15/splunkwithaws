package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.FrequentlySearchedCustomerPartnerT;
import com.tcs.destination.bean.FrequentlySearchedResponse;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.FrequentlySearchedRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.OpportunityRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoDataFoundException;
import com.tcs.destination.exception.NoManditoryFieldsFoundExceptions;
import com.tcs.destination.exception.NoSuchEntityException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.Constants.EntityType;

@Component
public class FrequentlySearchedService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	FrequentlySearchedRepository frequentRepository;

	public List<FrequentlySearchedResponse> findFrequent(String entity,
			int count) throws Exception {
		entity = entity.toUpperCase();

		if (EntityType.contains(entity)) {

			List<Object[]> frequentMapping = frequentRepository
					.findFrequentEntities(entity, count);

			List<FrequentlySearchedResponse> sortedList = new ArrayList<FrequentlySearchedResponse>();
			switch (EntityType.valueOf(entity)) {
			case CUSTOMER:
				for (Object[] frequent : frequentMapping) {
					CustomerMasterT customer = customerRepository
							.findOne(frequent[1].toString());
					FrequentlySearchedResponse frequentResponse = Constants
							.convertToFrequentlySearchedResponse(
									Integer.parseInt(frequent[0].toString()),
									customer);
					sortedList.add(frequentResponse);
				}
				return sortedList;
			case PARTNER:
				for (Object[] frequent : frequentMapping) {
					PartnerMasterT partner = partnerRepository
							.findOne(frequent[1].toString());
					FrequentlySearchedResponse frequentResponse = Constants
							.convertToFrequentlySearchedResponse(
									Integer.parseInt(frequent[0].toString()),
									partner);
					sortedList.add(frequentResponse);

				}
				return sortedList;
			default:
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Please ensure your entity type.");

			}
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"No such Entity type exists. Please ensure your entity type.");
		}
	}

	public boolean insertFrequent(FrequentlySearchedCustomerPartnerT frequent) throws Exception {
		
		if (Constants.EntityType.contains(frequent.getEntityType())) {
			if(frequent.getEntityId()==null)
				throw new DestinationException(HttpStatus.BAD_REQUEST,"Entity ID can not be empty");
			if(frequent.getUserId() == null){
				throw new DestinationException(HttpStatus.BAD_REQUEST,"User ID can not be empty");
			}
				frequent.setSearchDatetime(Constants.getCurrentTimeStamp());
					return frequentRepository.save(frequent)!=null;
	}
		throw new DestinationException(HttpStatus.BAD_REQUEST,"Invalid Entity Type");
	}

}