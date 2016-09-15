package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.DeliveryMasterT;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.data.repository.DeliveryMasterRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.PaginationUtils;

/**
 * handle service functionalities for delivery
 * 
 * @author TCS
 *
 */
@Service
public class DeliveryMasterService {

	private static final Logger logger = LoggerFactory.getLogger(DeliveryMasterService.class);
	
	@Autowired
	DeliveryMasterRepository deliveryMasterRepository;
	
	/**
	 * @param sortBy
	 * @param order
	 * @param isCurrentFinancialYear
	 * @param page
	 * @param count
	 * @return
	 * @throws DestinationException
	 */
	public PaginatedResponse findAll(String sortBy, String order,
			Boolean isCurrentFinancialYear, int page, int count)
					throws DestinationException {
		logger.info("Inside DeliveryMasterService: findAll start");
		PaginatedResponse deliveryMasterResponse = new PaginatedResponse();

		List<DeliveryMasterT> deliveryMasterTs = null;
		deliveryMasterTs = (List<DeliveryMasterT>) deliveryMasterRepository.findAll();
				deliveryMasterResponse.setTotalCount(deliveryMasterTs.size());

				// Code for pagination
				if (PaginationUtils.isValidPagination(page, count,
						deliveryMasterTs.size())) {
					int fromIndex = PaginationUtils.getStartIndex(page, count,
							deliveryMasterTs.size());
					int toIndex = PaginationUtils.getEndIndex(page, count,
							deliveryMasterTs.size()) + 1;
					deliveryMasterTs = deliveryMasterTs.subList(fromIndex, toIndex);
					deliveryMasterResponse.setDeliveryMasterTs(deliveryMasterTs);
					logger.debug("deliveryMasterTs  after pagination size is "
							+ deliveryMasterTs.size());
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No delivery available for the specified page");
				}		
				
				
		if (deliveryMasterTs == null || deliveryMasterTs.size() == 0)
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No delivery found");
		logger.info("Inside DeliveryMasterService: findAll end");
		return deliveryMasterResponse;
	}
	
	
	/**
	 * To fetch delivery master details by delivery master id
	 * 
	 * @param deliveryMasterId
	 * @return
	 * @throws Exception
	 */
	public DeliveryMasterT findByDeliveryMasterId(Integer deliveryMasterId) throws Exception {
		logger.debug("Inside findByDeliveryMasterId() service");
		DeliveryMasterT deliveryMaster = deliveryMasterRepository.findOne(deliveryMasterId);
		if (deliveryMaster != null) {
			return deliveryMaster;
		} else {
			logger.error("NOT_FOUND: Delivery Master Details not found: {}", deliveryMasterId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Delivery Master not found: " + deliveryMasterId);
		}
	}
	
}

