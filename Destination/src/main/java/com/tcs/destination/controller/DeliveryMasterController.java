package com.tcs.destination.controller;


import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.AsyncJobRequest;
import com.tcs.destination.bean.DeliveryMasterT;
import com.tcs.destination.bean.DeliveryRgsT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.JobName;
import com.tcs.destination.enums.OperationType;
import com.tcs.destination.enums.Switch;
import com.tcs.destination.enums.SmartSearchType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DeliveryMasterService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This Controller handles the opportunity module
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/delivery")
public class DeliveryMasterController {

	private static final Logger logger = LoggerFactory
			.getLogger(DeliveryMasterController.class);

	@Autowired
	DeliveryMasterService deliveryMasterService;
	
	@Autowired
	private JobLauncherController jobLauncherController;

	/**
	 * This method retrieves the delivery master list
	 * @param page
	 * @param count
	 * @param order
	 * @param sortBy
	 * @param stage
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "order", defaultValue = "DESC") String order,
			@RequestParam(value = "sortBy", defaultValue = "deliveryMasterId") String sortBy,
			@RequestParam(value = "stage", defaultValue = "-1") Integer stage,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {
		logger.info("Inside DeliveryMasterController: Start of /delivery/all GET");
		String response = null;
		PageDTO deliveryMasterDTO = null;
		try {

			deliveryMasterDTO = deliveryMasterService.findEngagements(stage,sortBy, order,
					page, count);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, deliveryMasterDTO);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the deliveryMaster list");
		}
		logger.info("Inside DeliveryMasterController: End of /delivery/all GET");
		return response;
	}

	/**
	 * This method is used to get the delivery master details for the given
	 * delivery master id
	 * 
	 * @param deliveryMasterId
	 * @param fields
	 * @param view
	 * @return deliveryMasterT
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findByDeliveryMasterId(
			@PathVariable("id") String deliveryMasterId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {

		logger.info("Inside DeliveryMasterController: Start of search by id");
		String response = null;
		DeliveryMasterT deliveryMasterT;
		try {
			deliveryMasterT = deliveryMasterService.findByDeliveryMasterId(deliveryMasterId);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, deliveryMasterT);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the delivery master details for the id:"
							+ deliveryMasterId);
		}
		logger.info("Inside DeliveryMasterController: End of search by id");
		return response;
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> editEngagement(
			@RequestBody DeliveryMasterT deliveryMaster,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {

		logger.info("Inside DeliveryMasterController: Start of Edit DeliveryMaster");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			List<AsyncJobRequest> asyncJobRequests = deliveryMasterService.updateDelivery(deliveryMaster);
			for(AsyncJobRequest asyncJobRequest : asyncJobRequests) {
			if (asyncJobRequest.getOn().equals(Switch.ON)) {
			jobLauncherController.asyncJobLaunch(asyncJobRequest.getJobName(), asyncJobRequest.getEntityType().name(), asyncJobRequest.getEntityId(), asyncJobRequest.getDealValue(), asyncJobRequest.getDeliveryCentreId());
				}
			}

				status.setStatus(Status.SUCCESS, deliveryMaster.getDeliveryMasterId());
				//jobLauncherController.asyncJobLaunchForNotification(JobName.notification, EntityType.CONNECT, connect.getConnectId(),OperationType.CONNECT_EDIT,connect.getModifiedBy());
			logger.info("Inside DeliveryMasterController: End of Edit delivery master");

			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating delivery master");
		}

	}
	
	/**
	 * This method retrieves the delivery RGS Id list for an Input RGS Id pattern
	 * @param idLike
	 * @param limitNum
	 * @return List<String>
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/search/rgs", method = RequestMethod.GET)
	public @ResponseBody List<String> searchByDeliveryRgsId(
			@RequestParam(value = "idLike", defaultValue = "") String idLike,
			@RequestParam(value = "limitNum", defaultValue = "20") int limitNum)
			throws DestinationException {

		logger.info("Inside DeliveryMasterController: Start of search by Delivery Rgs Id pattern: "+idLike+" with limit num:"+limitNum);
		List<String> response = null;
		try {
			response = deliveryMasterService.searchByDeliveryRgsId(idLike,limitNum);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the delivery Rgs Id List for the Rgs Id pattern:"
							+ idLike);
		}
		logger.info("Inside DeliveryMasterController: End of search by Delivery Rgs Id pattern");
		return response;
	}
	

	/**
	 * Service to fetch the delivery master t related information based on search type and the search keyword 
	 * @param searchType - category type
	 * @param term - keyword
	 * @param getAll - true, to retrieve entire result, false to filter the result to only 3 records.(<b>default:false</b>)
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/search/smart", method = RequestMethod.GET)
	public @ResponseBody String smartSearch(
			@RequestParam("searchType") String searchType,
			@RequestParam("term") String term,
			@RequestParam(value = "getAll", defaultValue = "false") boolean getAll,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {
		logger.info("Inside DeliveryMasterController: smart search by search term");
		try {
			UserT user = DestinationUtils.getCurrentUserDetails();
			PageDTO<SearchResultDTO<DeliveryMasterT>> res = deliveryMasterService.deliveryMasterSmartSearch(SmartSearchType.get(searchType), term, getAll, page, count,user);
			logger.info("Inside DeliveryMasterController: End - smart search by search term");
			return ResponseConstructors.filterJsonForFieldAndViews(fields, view, res, !getAll);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Error on smartSearch", e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving delivery master detail smart search");
		}
	}

	/**
	 * service to retrieve all the delivery managers under a particular delivery center
	 * @param deliveryCentreId
	 * @param nameWith
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/deliveryManagersForCentre", method = RequestMethod.GET)
	public @ResponseBody String findAllUsersForDeliveryCentreHeads(
			@RequestParam(value = "deliveryCentreIds", defaultValue = "-1") List<Integer> deliveryCentreIds,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {
		logger.info("Inside DeliveryMasterController: Start of /deliverCentreUserlist GET");
		String response = null;
		Set<UserT> deliveryCentreUserList = null;
		try {
			deliveryCentreUserList = deliveryMasterService.findDeliveryCentreUserList(deliveryCentreIds, nameWith);
			if (deliveryCentreUserList.size() == 0) {
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Delivery Managers not available for this delivery center !");
			}
			logger.info("Ending DeliveryMasterController findone method");
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, deliveryCentreUserList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the deliveryMaster list");
		}
		logger.info("Inside DeliveryMasterController: End of /deliverCentreUserlist GET");
		return response;
	}

}
