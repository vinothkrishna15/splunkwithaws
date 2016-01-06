package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.FrequentlySearchedCustomerPartnerT;
import com.tcs.destination.bean.FrequentlySearchedResponse;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.FrequentlySearchedRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 *This service handles the frequently searched customer and partner 
 *and its insertion and find operations 
 *
 */
@Service
public class FrequentlySearchedService {
	
	private static final Logger logger = LoggerFactory.getLogger(FrequentlySearchedService.class);

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	FrequentlySearchedRepository frequentRepository;

	/**
	 * This method searches  and finds the frequently searched 
	 * customers and partners
	 * @param entityType
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public List<FrequentlySearchedResponse> findFrequent(String entityType,
			int count) throws Exception {
		logger.debug("Begin: findFrequent() of FrequentlySearchedService");
		if (EntityType.contains(entityType)) {
			List<Object[]> frequentMapping = frequentRepository
					.findFrequentEntities(entityType, count);

			List<FrequentlySearchedResponse> sortedList = new ArrayList<FrequentlySearchedResponse>();
			switch (EntityType.valueOf(entityType)) {
			case CUSTOMER:
				for (Object[] frequent : frequentMapping) {
					CustomerMasterT customer = customerRepository
							.findOne(frequent[1].toString());
					FrequentlySearchedResponse frequentResponse = ResponseConstructors
							.convertToFrequentlySearchedResponse(
									Integer.parseInt(frequent[0].toString()),
									customer);
					sortedList.add(frequentResponse);
				}
					if (sortedList.isEmpty()) {
						logger.error("NOT_FOUND: No Relevent Data Found in the database");
						throw new DestinationException(HttpStatus.NOT_FOUND,
								"No Relevent Data Found in the database");
					}else {
						logger.debug("End: findFrequent() of FrequentlySearchedService: CUSTOMER entity");
								return sortedList; 
	}
			case PARTNER:
				for (Object[] frequent : frequentMapping) {
					PartnerMasterT partner = partnerRepository
							.findOne(frequent[1].toString());
					FrequentlySearchedResponse frequentResponse = ResponseConstructors
							.convertToFrequentlySearchedResponse(
									Integer.parseInt(frequent[0].toString()),
									partner);
					sortedList.add(frequentResponse);

				}
				if (sortedList.isEmpty()) {
				logger.error("NOT_FOUND: No Relevent Data Found in the database");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Relevent Data Found in the database");
			}else {
				logger.debug("End: findFrequent() of FrequentlySearchedService: PARTNER entity");
						return sortedList; 
}
			default:
			{
				logger.error("BAD_REQUEST: Please ensure your entity type.");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"This Feature is unavailable for " + entityType);

			}
			}} else {
			logger.error("BAD_REQUEST: No such Entity type exists. Please ensure your entity type.");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"No such Entity type exists. Please ensure your entity type.");
		}
	}

	/**
	 * This method allows to insert the frequently searched Customer / Partner
	 * @param frequent
	 * @return
	 * @throws Exception
	 */
	public boolean insertFrequent(FrequentlySearchedCustomerPartnerT frequent)	throws Exception {
		frequent.setUserId(DestinationUtils.getCurrentUserDetails().getUserId());
		logger.debug("Begin: insertFrequent() of FrequentlySearchedService");
		if (EntityType.contains(frequent.getEntityType())) {
			if (frequent.getEntityId() == null)
			{
				logger.error("BAD_REQUEST: Entity ID can not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,"Entity ID can not be empty");
			}
			if (frequent.getUserId() == null) {
			logger.error("BAD_REQUEST: User ID can not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"User ID can not be empty");
			}
			frequent.setSearchDatetime(DateUtils.getCurrentTimeStamp());
			logger.debug("End: insertFrequent() of FrequentlySearchedService");
			return frequentRepository.save(frequent) != null;
		}
		logger.error("BAD_REQUEST: Invalid Entity Type");
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"Invalid Entity Type");
	}

}