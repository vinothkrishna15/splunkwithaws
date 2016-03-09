package com.tcs.destination.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.BeaconCustomerMappingTPK;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.RevenueCustomerMappingTPK;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.WorkflowCustomerT;
import com.tcs.destination.bean.WorkflowRequestT;
import com.tcs.destination.bean.WorkflowStepT;
import com.tcs.destination.data.repository.BeaconCustomerMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WorkflowCustomerTRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.data.repository.WorkflowStepTRepository;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.enums.WorkflowStatus;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationMailUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.StringUtils;

@Service
public class WorkflowService {

	private static final Logger logger = LoggerFactory.getLogger(WorkflowService.class);

	@Value("${workflowCustomerPending}")
	private String workflowCustomerPendingSubject;

	@Value("${workflowCustomerApproved}")
	private String workflowCustomerApprovedSubject;

	@Value("${workflowCustomerRejected}")
	private String workflowCustomerRejected;

	@Autowired 
	WorkflowStepTRepository workflowStepTRepository;

	@Autowired 
	WorkflowCustomerTRepository workflowCustomerRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	WorkflowRequestTRepository workflowRequestTRepository;

	@Autowired
	CustomerUploadService customerUploadService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RevenueCustomerMappingTRepository revenueRepository;

	@Autowired
	BeaconCustomerMappingRepository beaconRepository;

	@Autowired
	DestinationMailUtils mailUtils;

	@Autowired
	ThreadPoolTaskExecutor mailTaskExecutor;

	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouCustomerMappingT = null;
	Map<String, IouBeaconMappingT> mapOfIouBeaconMappingT = null;

	/**
	 * Requested entity approval
	 * @param workflowStepT
	 * @return
	 */
	public boolean approveWorkflowEntity(WorkflowCustomerT workflowCustomerT) {

		int stepId = -1;
		int requestId = 0;
		int rowIteration = 0;
		int step = 0;
		List<WorkflowStepT> requestSteps = new ArrayList<WorkflowStepT>();
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		WorkflowRequestT masterRequest = new WorkflowRequestT();

		if(validateWorkflowRequest(workflowCustomerT)){
			saveToMasterTables(workflowCustomerT);
		}
		try{
			requestSteps = workflowStepTRepository.findStepForEditAndApprove(workflowCustomerT.getWorkflowCustomerId());
			masterRequest = workflowRequestTRepository.findRequestedRecord(0,workflowCustomerT.getWorkflowCustomerId());
			for (WorkflowStepT stepRecord : requestSteps){
				if(stepRecord.getStepStatus().equals(WorkflowStatus.PENDING.getStatus())){
					stepId = stepRecord.getStepId();
					requestId = stepRecord.getWorkflowRequestT().getRequestId();
					WorkflowCustomerT oldObject = new WorkflowCustomerT();
					if(stepId != -1 && requestId != 0 && rowIteration == 0){
						oldObject = workflowCustomerRepository.findOne(workflowCustomerT.getWorkflowCustomerId());
						if (!workflowCustomerT.equals(oldObject)){
							workflowCustomerT.setModifiedBy(userId);
							workflowCustomerRepository.save(workflowCustomerT);
						}
						stepRecord.setUserId(userId);
						stepRecord.setStepStatus(WorkflowStatus.APPROVED.getStatus());
						stepRecord.setModifiedBy(userId);
						if(!StringUtils.isEmpty(workflowCustomerT.getNotes())){
							stepRecord.setComments(workflowCustomerT.getNotes());
						}
						// for updating the status in workflow_request_t 
						masterRequest.setModifiedBy(userId);
						masterRequest.setStatus(WorkflowStatus.APPROVED.getStatus());
						sendEmailNotificationforApprovedOrRejectMail(workflowCustomerApprovedSubject,masterRequest.getRequestId(),new Date());
						step = stepRecord.getStep()+1;
						rowIteration++;
					}
				}
				if(stepRecord.getStep().equals(step) && (rowIteration == 1)){
					stepRecord.setStepStatus(WorkflowStatus.PENDING.getStatus());
					// for updating the status in workflow_request_t 
					masterRequest.setModifiedBy(userId);
					masterRequest.setStatus(WorkflowStatus.PENDING.getStatus());
					stepRecord.setModifiedBy(userId);
					sendEmailNotificationforPending(masterRequest.getRequestId(),new Date());
					rowIteration++;
				}
			}
			workflowStepTRepository.save(requestSteps);
			workflowRequestTRepository.save(masterRequest);
		}
		catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"Backend error while approving the request");
		}
		return true;
	}

	private void sendEmailNotificationforApprovedOrRejectMail(final String approveOrRejectSubject,Integer requestId, Date date) throws Exception {
		// TODO Auto-generated method stub
		class WorkflowNotificationForApproveOrReject implements Runnable {
			Integer requestId;
			Date date;

			WorkflowNotificationForApproveOrReject(Integer requestId, Date date) {
				this.requestId = requestId;
				this.date = date;
			}
			@Override
			public void run() {
				try {
					mailUtils.sendWorkflowApprovedOrRejectMail(approveOrRejectSubject,requestId,date);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		WorkflowNotificationForApproveOrReject workflowNotificationForApproveOrReject = new WorkflowNotificationForApproveOrReject(requestId,date);
		mailTaskExecutor.execute(workflowNotificationForApproveOrReject);
		logger.debug("End:Inside sendEmailNotification of workflow pending");
	}

	private void sendEmailNotificationforPending(Integer requestId, Date date) throws Exception {
		// TODO Auto-generated method stub
		class WorkflowNotificationForPending implements Runnable {
			Integer requestId;
			Date date;

			WorkflowNotificationForPending(Integer requestId, Date date) {
				this.requestId = requestId;
				this.date = date;
			}
			@Override
			public void run() {
				try {
					mailUtils.sendWorkflowPendingMail(workflowCustomerPendingSubject,requestId,date);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} 
		WorkflowNotificationForPending workflowNotificationForPending = new WorkflowNotificationForPending(requestId,date);
		mailTaskExecutor.execute(workflowNotificationForPending);
		logger.debug("End:Inside sendEmailNotification of workflow pending");

	}

	/*
	 * on admin approval new entity was created in the master table
	 */
	private void saveToMasterTables(WorkflowCustomerT workflowCustomerT) {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		CustomerMasterT customerMaster =new CustomerMasterT();
		customerMaster.setCustomerName(workflowCustomerT.getCustomerName());
		customerMaster.setGroupCustomerName(workflowCustomerT.getGroupCustomerName());
		customerMaster.setCorporateHqAddress(workflowCustomerT.getCorporateHqAddress());
		customerMaster.setWebsite(workflowCustomerT.getWebsite());
		customerMaster.setFacebook(workflowCustomerT.getFacebook());
		customerMaster.setIou(workflowCustomerT.getIou());
		customerMaster.setGeography(workflowCustomerT.getGeography());
		customerMaster.setLogo(workflowCustomerT.getLogo());
		customerMaster.setDocumentsAttached(workflowCustomerT.getDocumentsAttached());
		customerMaster.setCreatedModifiedBy(userId);
		customerRepository.save(customerMaster);
		if(!workflowCustomerT.getRevenueCustomerMappingTs().isEmpty()){
			for(RevenueCustomerMappingT rcmpt : workflowCustomerT.getRevenueCustomerMappingTs()){
				RevenueCustomerMappingT revenueCustomer =new RevenueCustomerMappingT();
				RevenueCustomerMappingTPK revenueTPK = new RevenueCustomerMappingTPK();
				revenueTPK.setFinanceCustomerName(rcmpt.getFinanceCustomerName());
				revenueCustomer.setCustomerName(customerMaster.getCustomerName());
				revenueTPK.setFinanceIou(rcmpt.getFinanceIou());
				revenueTPK.setCustomerGeography(rcmpt.getCustomerGeography());
				revenueCustomer.setId(revenueTPK);
				revenueRepository.save(revenueCustomer);
			}
		}
		if(!workflowCustomerT.getBeaconCustomerMappingTs().isEmpty()){
			for(BeaconCustomerMappingT bcmpt : workflowCustomerT.getBeaconCustomerMappingTs()){
				BeaconCustomerMappingT beaconCustomer =new BeaconCustomerMappingT();
				BeaconCustomerMappingTPK beaconTPK = new BeaconCustomerMappingTPK();
				beaconTPK.setBeaconCustomerName(bcmpt.getBeaconCustomerName());
				beaconCustomer.setCustomerName(customerMaster.getCustomerName());
				beaconTPK.setBeaconIou(bcmpt.getBeaconIou());
				beaconTPK.setCustomerGeography(bcmpt.getCustomerGeography());
				beaconCustomer.setId(beaconTPK);
				beaconRepository.save(beaconCustomer);
			}
		}
	}

	/*
	 * Entry level validation for the workflow customerT input json
	 */
	private boolean validateWorkflowRequest(WorkflowCustomerT requestedCustomerT) {

		boolean isAdminValidated = false;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		UserT user = userRepository.findByUserId(userId);
		mapOfGeographyMappingT = customerUploadService.getGeographyMappingT();
		mapOfIouCustomerMappingT = customerUploadService.getIouMappingT();
		mapOfIouBeaconMappingT = customerUploadService.getBeaconIouMappingT();

		validateWorkflowCustomerMasterDetails(requestedCustomerT);

		if (user.getUserRole().equals(UserRole.STRATEGIC_GROUP_ADMIN.getValue())) {
			if (StringUtils.isEmpty(requestedCustomerT.getGroupCustomerName())) {
				logger.error("Group Customer name is mandatory");
				throw new DestinationException(HttpStatus.BAD_REQUEST,"Group Customer name is mandatory");
			}
			List<RevenueCustomerMappingT> revenueCustomerMappingTs = new ArrayList<RevenueCustomerMappingT>();
			List<BeaconCustomerMappingT> beaconCustomerMappingTs = new ArrayList<BeaconCustomerMappingT>();
			revenueCustomerMappingTs = requestedCustomerT.getRevenueCustomerMappingTs();

			if (CollectionUtils.isNotEmpty(revenueCustomerMappingTs)) {
				validateRevenueCustomerDetails(revenueCustomerMappingTs);
			} else {
				logger.error("revenue customer details are mandatory for admin");
				throw new DestinationException(HttpStatus.BAD_REQUEST,"revenue customer details are mandatory");
			}
			beaconCustomerMappingTs = requestedCustomerT.getBeaconCustomerMappingTs();
			if (CollectionUtils.isNotEmpty(beaconCustomerMappingTs)) {
				validateBeaconCustomerDetails(beaconCustomerMappingTs);
			}
			isAdminValidated = true;
		}
		return isAdminValidated;
	}

	/**
	 * customer master integrity validations for requested customer
	 * @param requestedCustomerT
	 */
	private void validateWorkflowCustomerMasterDetails(WorkflowCustomerT requestedCustomerT) {

		String customerName = requestedCustomerT.getCustomerName();
		//customer name should not be empty
		if (StringUtils.isEmpty(customerName)) {
			logger.error("Customer Name should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,"Customer Name should not be empty");
		}
		// to check duplicate of customer name
		CustomerMasterT customerMaster = customerRepository.findByCustomerName(customerName);
		//	WorkflowCustomerT workflowCustomer = workflowCustomerRepository.findByCustomerName(customerName);
		if ((customerMaster!=null)){
			logger.error("Customer name already exists");
			throw new DestinationException(HttpStatus.BAD_REQUEST,"Customer name already exists" + customerName);
		}
		//foreign key constraint for geography
		if (!StringUtils.isEmpty(requestedCustomerT.getGeography())) {
			if (!mapOfGeographyMappingT.containsKey(requestedCustomerT.getGeography())) {
				logger.error("Invalid Geography");
				throw new DestinationException(HttpStatus.NOT_FOUND,"Invalid Geography" + requestedCustomerT.getGeography());
			}
		} else {
			logger.error("Geography Should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,"Geography Should not be empty");
		}
		//foreign key constraint for iou
		if (!StringUtils.isEmpty(requestedCustomerT.getIou())) {
			if (!mapOfIouCustomerMappingT.containsKey(requestedCustomerT.getIou())) {
				logger.error("Invalid IOU");
				throw new DestinationException(HttpStatus.NOT_FOUND,"Invalid IOU" + requestedCustomerT.getIou());
			}
		} else {
			logger.error("IOU Should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,"IOU Should not be empty");
		}
	}

	/**
	 * validate beacon details for the requested customer
	 * @param beaconCustomerMappingTs
	 */
	private void validateBeaconCustomerDetails(List<BeaconCustomerMappingT> beaconCustomerMappingTs) {
		List<BeaconCustomerMappingT> beaconCustomers = null;
		for (BeaconCustomerMappingT bcmt : beaconCustomerMappingTs) {
			if (StringUtils.isEmpty(bcmt.getBeaconCustomerName())) {
				logger.error("Beacon Customer name should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Beacon Customer name should not be empty");
			}
			if (!StringUtils.isEmpty(bcmt.getCustomerGeography())) {
				if (!mapOfGeographyMappingT.containsKey(bcmt
						.getCustomerGeography())) {
					logger.error("Invalid Geography");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Invalid Geography" + bcmt.getCustomerGeography());
				}
			} else {
				logger.error("Geography Should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Geography Should not be empty");
			}
			if (!StringUtils.isEmpty(bcmt.getBeaconIou())) {
				if (!mapOfIouBeaconMappingT.containsKey(bcmt.getBeaconIou())) {
					logger.error("Invalid IOU");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Invalid IOU" + bcmt.getBeaconIou());
				}
			} else {
				logger.error("IOU Should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"IOU Should not be empty");
			}
			beaconCustomers = beaconRepository.checkBeaconMappingPK(bcmt.getBeaconCustomerName(),bcmt.getCustomerGeography(),bcmt.getBeaconIou());
			if(!beaconCustomers.isEmpty()){
				logger.error("The combination of the beaconCustomerName, geography and beaconIOU already exists");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Finance Customer name should not be empty");
			}
		}

	}

	/**
	 * validate revenue details for the requested customer
	 * @param revenueCustomerMappingTs
	 */
	private void validateRevenueCustomerDetails(List<RevenueCustomerMappingT> revenueCustomerMappingTs) {
		List<RevenueCustomerMappingT> financeCustomers = null;
		for (RevenueCustomerMappingT rcmt : revenueCustomerMappingTs) {
			if (StringUtils.isEmpty(rcmt.getFinanceCustomerName())) {
				logger.error("Finance Customer name should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Finance Customer name should not be empty");
			}
			if (!StringUtils.isEmpty(rcmt.getCustomerGeography())) {
				if (!mapOfGeographyMappingT.containsKey(rcmt.getCustomerGeography())) {
					logger.error("Invalid Geography");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Invalid Geography" + rcmt.getCustomerGeography());
				}
			} else {
				logger.error("Geography Should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Geography Should not be empty");
			}
			if (!StringUtils.isEmpty(rcmt.getFinanceIou())) {
				if (!mapOfIouCustomerMappingT.containsKey(rcmt.getFinanceIou())) {
					logger.error("Invalid IOU");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Invalid IOU" + rcmt.getFinanceIou());
				}
			} else {
				logger.error("IOU Should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"IOU Should not be empty");
			}

			financeCustomers = revenueRepository.checkRevenueMappingPK(rcmt.getFinanceCustomerName(),rcmt.getCustomerGeography(),rcmt.getFinanceIou());
			if(!financeCustomers.isEmpty()){
				logger.error("The combination of the finanaceCustomerName, geography and finanaceIOU already exists");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Finance Customer name should not be empty");
			}
		}

	}

	/**
	 * Requested entity is rejected with comments for rejection
	 * @param workflowStepT
	 * @return
	 */
	public boolean rejectWorkflowEntity(WorkflowStepT workflowStepT) {

		int stepId = -1;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		stepId = workflowStepT.getStepId();
		WorkflowStepT workflowStepToReject = new WorkflowStepT();
		WorkflowRequestT masterRequest = new WorkflowRequestT();
		try{
			if(stepId != -1){
				workflowStepToReject = workflowStepTRepository.findStep(stepId);
				masterRequest = workflowRequestTRepository.findRequest(workflowStepT.getWorkflowRequestT().getRequestId());
				if(workflowStepToReject != null && workflowStepToReject.getStepStatus()
						.equalsIgnoreCase(WorkflowStatus.PENDING.getStatus())){
					workflowStepToReject.setUserId(userId);
					workflowStepToReject.setStepStatus(workflowStepT.getStepStatus());
					workflowStepToReject.setModifiedBy(userId);
					if(!(workflowStepT.getComments().isEmpty()) && (workflowStepT.getComments()!=null)){
						workflowStepToReject.setComments(workflowStepT.getComments());
					}
					else{
						throw new DestinationException(HttpStatus.NOT_FOUND,
								"comments is mandatory: give reason for rejection");
					}
					masterRequest.setModifiedBy(userId);
					masterRequest.setStatus(workflowStepT.getStepStatus());
					workflowStepTRepository.save(workflowStepToReject);
					workflowRequestTRepository.save(masterRequest);
					sendEmailNotificationforApprovedOrRejectMail(workflowCustomerRejected,masterRequest.getRequestId(),new Date());
				}
				else{
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Record doesnot exist for the given stepId: " + stepId);
				}
			}
			else{
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"StepId is not valid or empty");
			}
		}
		catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while rejecting the Request");
		}
		return true;
	}
}