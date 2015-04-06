package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
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
import com.tcs.destination.utils.ResponseConstructors;

@Component
public class FrequentlySearchedService {
	
	private static final Logger logger = LoggerFactory.getLogger(FrequentlySearchedService.class);

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	FrequentlySearchedRepository frequentRepository;

	public List<FrequentlySearchedResponse> findFrequent(String entityType,
			int count) throws Exception {
//<<<<<<< HEAD
//		logger.debug("Inside findFrequent Service");
//		entity = entity.toUpperCase();
//
//		if (EntityType.contains(entity)) {
//			logger.debug("Entity is present");
//=======
		if (EntityType.contains(entityType)) {
			logger.debug("Entity is present");
//>>>>>>> 2e608ca3023dc288d7e7291656f680f7d028fbdc
			List<Object[]> frequentMapping = frequentRepository
					.findFrequentEntities(entityType, count);

			List<FrequentlySearchedResponse> sortedList = new ArrayList<FrequentlySearchedResponse>();
			switch (EntityType.valueOf(entityType)) {
			case CUSTOMER:
				logger.debug("CUSTOMER ENTITY FOUND");
				for (Object[] frequent : frequentMapping) {
					CustomerMasterT customer = customerRepository
							.findOne(frequent[1].toString());
					FrequentlySearchedResponse frequentResponse = ResponseConstructors
							.convertToFrequentlySearchedResponse(
									Integer.parseInt(frequent[0].toString()),
									customer);
					sortedList.add(frequentResponse);
				}
				return sortedList;
			case PARTNER:
				logger.debug("PARTNER ENTITY FOUND");
				for (Object[] frequent : frequentMapping) {
					PartnerMasterT partner = partnerRepository
							.findOne(frequent[1].toString());
					FrequentlySearchedResponse frequentResponse = ResponseConstructors
							.convertToFrequentlySearchedResponse(
									Integer.parseInt(frequent[0].toString()),
									partner);
					sortedList.add(frequentResponse);

				}
				return sortedList;
			default:
			{
				logger.error("BAD_REQUEST: Please ensure your entity type.");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
//<<<<<<< HEAD
//						"Please ensure your entity type.");
//			}
//=======
						"This Feature is unavailable for " + entityType);

//>>>>>>> 2e608ca3023dc288d7e7291656f680f7d028fbdc
			}
			}} else {
			logger.error("BAD_REQUEST: No such Entity type exists. Please ensure your entity type.");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"No such Entity type exists. Please ensure your entity type.");
		}
	}

//<<<<<<< HEAD
//	public boolean insertFrequent(FrequentlySearchedCustomerPartnerT frequent) throws Exception {
//		logger.debug("Inside insertFrequent Service");
//		if (Constants.EntityType.contains(frequent.getEntityType())) {
//			logger.debug("EntityType is present");
//			if(frequent.getEntityId()==null)
//			{
//				logger.error("BAD_REQUEST: Entity ID can not be empty");
//				throw new DestinationException(HttpStatus.BAD_REQUEST,"Entity ID can not be empty");
//			}
//			if(frequent.getUserId() == null){
//				logger.error("BAD_REQUEST: User ID can not be empty");
//				throw new DestinationException(HttpStatus.BAD_REQUEST,"User ID can not be empty");
//			}
//				frequent.setSearchDatetime(Constants.getCurrentTimeStamp());
//				logger.debug("Frequent detail is saving");
//					return frequentRepository.save(frequent)!=null;
//	}
//		logger.error("BAD_REQUEST: Invalid Entity Type");
//		throw new DestinationException(HttpStatus.BAD_REQUEST,"Invalid Entity Type");
//=======
		
	
	public boolean insertFrequent(FrequentlySearchedCustomerPartnerT frequent)	throws Exception {
	    logger.debug("Inside insertFrequent Service");
		if (EntityType.contains(frequent.getEntityType())) {
			logger.debug("EntityType is present");
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
			logger.debug("Frequent detail is saving");
			return frequentRepository.save(frequent) != null;
		}
		logger.error("BAD_REQUEST: Invalid Entity Type");
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"Invalid Entity Type");
//>>>>>>> 2e608ca3023dc288d7e7291656f680f7d028fbdc
	}

}