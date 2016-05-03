package com.tcs.destination.service;

import com.google.common.collect.Lists;
//i1297mport com.tcs.destination.bean.WorkflowCompetitorDetailsDTO;
import com.tcs.destination.bean.WorkflowCompetitorT;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.BeaconCustomerMappingTPK;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.RevenueCustomerMappingTPK;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.WorkflowCustomerT;
import com.tcs.destination.bean.WorkflowPartnerDetailsDTO;
import com.tcs.destination.bean.WorkflowPartnerT;
import com.tcs.destination.bean.WorkflowProcessTemplate;
import com.tcs.destination.bean.WorkflowRequestT;
import com.tcs.destination.bean.WorkflowStepT;
import com.tcs.destination.data.repository.BeaconCustomerMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WorkflowCompetitorTRepository;
import com.tcs.destination.data.repository.WorkflowCustomerTRepository;
import com.tcs.destination.data.repository.WorkflowPartnerRepository;
import com.tcs.destination.data.repository.WorkflowProcessTemplateRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.data.repository.WorkflowStepTRepository;
import com.tcs.destination.enums.EntityTypeId;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.enums.WorkflowStatus;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationMailUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.StringUtils;

import java.sql.Timestamp;
import java.util.Collections;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.tcs.destination.bean.MyWorklistDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.WorkflowCustomerDetailsDTO;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.utils.PaginationUtils;
import com.tcs.destination.utils.QueryConstants;

import static com.tcs.destination.enums.EntityTypeId.COMPETITOR;
import static com.tcs.destination.enums.EntityTypeId.CUSTOMER;
import static com.tcs.destination.enums.EntityTypeId.PARTNER;

/**
 * This service contains workflow related functionalities
 * 
 * @author
 *
 */
@Service
public class WorkflowService {

	private static final Logger logger = LoggerFactory
			.getLogger(WorkflowService.class);

	@Value("${workflowCustomerApproved}")
	private String workflowCustomerApprovedSubject;

	@Value("${workflowPartnerApproved}")
	private String workflowPartnerApprovedSubject;

	@Value("${workflowCustomerRejected}")
	private String workflowCustomerRejectedSubject;

	@Value("${workflowPartnerRejected}")
	private String workflowPartnerRejectedSubject;

	@Value("${workflowOpportunityReopenApproved}")
	private String workflowOpportunityReopenApprovedSubject;

	@Value("${workflowOpportunityReopenRejected}")
	private String workflowOpportunityReopenRejectedSubject;

	@Autowired
	DestinationMailUtils mailUtils;

	@Autowired
	ThreadPoolTaskExecutor mailTaskExecutor;

	@Value("${workflowCustomerRejected}")
	private String workflowCustomerRejected;

	@Autowired
	WorkflowStepTRepository workflowStepTRepository;

	@Autowired
	WorkflowCustomerTRepository workflowCustomerRepository;

	@Autowired
	WorkflowProcessTemplateRepository workflowProcessTemplateRepository;

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
	WorkflowRequestTRepository workflowRequestRepository;

	@Autowired
	WorkflowStepTRepository workflowStepRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	WorkflowPartnerRepository workflowPartnerRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	WorkflowCompetitorTRepository workflowCompetitorRepository;

	@Autowired
	OpportunityRepository workflowOpportunityRepository;

	@PersistenceContext
	private EntityManager entityManager;

	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouCustomerMappingT = null;
	Map<String, IouBeaconMappingT> mapOfIouBeaconMappingT = null;

	/**
	 * Requested entity approval
	 * 
	 * @param workflowStepT
	 * @return
	 */
	public boolean approveWorkflowEntity(WorkflowCustomerT workflowCustomerT) {

		int stepId = -1;
		int requestId = 0;
		int rowIteration = 0;
		int step = 0;
		String oldCustomerName = null;
		List<WorkflowStepT> requestSteps = new ArrayList<WorkflowStepT>();
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		WorkflowRequestT masterRequest = new WorkflowRequestT();
		UserT user = userRepository.findByUserId(userId);

		try {

			if (validateWorkflowRequest(workflowCustomerT)) {
				requestSteps = workflowStepTRepository
						.findStepForEditAndApprove(Constants.CONSTANT_ZERO,
								workflowCustomerT.getWorkflowCustomerId());
				masterRequest = workflowRequestTRepository.findRequestedRecord(
						Constants.CONSTANT_ZERO,
						workflowCustomerT.getWorkflowCustomerId());
				for (WorkflowStepT stepRecord : requestSteps) {
					if (stepRecord.getStepStatus().equals(
							WorkflowStatus.PENDING.getStatus())) {
						stepId = stepRecord.getStepId();
						requestId = stepRecord.getRequestId();
						WorkflowCustomerT oldObject = new WorkflowCustomerT();
						if (stepId != -1 && requestId != 0 && rowIteration == 0) {
							oldObject = workflowCustomerRepository
									.findOne(workflowCustomerT
											.getWorkflowCustomerId());
							oldCustomerName = oldObject.getCustomerName();
							if (isCustomerRequestModified(oldObject,
									workflowCustomerT)) {
								workflowCustomerRepository.save(oldObject);
							}
							//
							if (user.getUserRole().equals(
									UserRole.STRATEGIC_GROUP_ADMIN.getValue())) {
								CustomerMasterT oldCustomerMaster = customerRepository
										.findByCustomerName(oldCustomerName);
								if (oldCustomerMaster != null) {
									saveToMasterTables(oldCustomerMaster,
											workflowCustomerT);
									sendEmailNotificationforApprovedOrRejectMail(
											workflowCustomerApprovedSubject,
											masterRequest.getRequestId(),
											masterRequest.getCreatedDatetime(),
											masterRequest.getEntityTypeId());
								} else {
									CustomerMasterT newCustomerMaster = new CustomerMasterT();
									saveToMasterTables(newCustomerMaster,
											workflowCustomerT);
									sendEmailNotificationforApprovedOrRejectMail(
											workflowCustomerApprovedSubject,
											masterRequest.getRequestId(),
											masterRequest.getCreatedDatetime(),
											masterRequest.getEntityTypeId());
								}
							}
							//
							stepRecord.setUserId(userId);
							stepRecord.setStepStatus(WorkflowStatus.APPROVED
									.getStatus());
							stepRecord.setModifiedBy(userId);
							if (!StringUtils.isEmpty(workflowCustomerT
									.getComments())) {
								stepRecord.setComments(workflowCustomerT
										.getComments());
							}
							// for updating the status in workflow_request_t
							masterRequest.setModifiedBy(userId);
							masterRequest.setStatus(WorkflowStatus.APPROVED
									.getStatus());
							step = stepRecord.getStep() + 1;
							rowIteration++;
						}
					}

					if (stepRecord.getStep().equals(step)
							&& (rowIteration == 1)) {
						stepRecord.setStepStatus(WorkflowStatus.PENDING
								.getStatus());
						// for updating the status in workflow_request_t
						masterRequest.setModifiedBy(userId);
						masterRequest.setStatus(WorkflowStatus.PENDING
								.getStatus());
						stepRecord.setModifiedBy(userId);
						sendEmailNotificationforPending(
								masterRequest.getRequestId(),
								masterRequest.getCreatedDatetime(),
								masterRequest.getEntityTypeId());
						rowIteration++;
					}
				}
				workflowStepTRepository.save(requestSteps);
				workflowRequestTRepository.save(masterRequest);
			}
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while approving the request");
		}
		return true;
	}

	/**
	 * to check whether a customer object is modified
	 * 
	 * @param oldObject
	 * @param workflowCustomerT
	 * @return
	 */
	private boolean isCustomerRequestModified(WorkflowCustomerT oldObject,
			WorkflowCustomerT workflowCustomerT) {

		boolean isCustomerModifiedFlag = false;
		String corporateHqAdress = "";
		String website = "";
		String facebook = "";
		String notes = "";
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		// customer name
		if (!workflowCustomerT.getCustomerName().equals(
				oldObject.getCustomerName())) {
			oldObject.setCustomerName(workflowCustomerT.getCustomerName());
			isCustomerModifiedFlag = true;
		}
		// corpoarate address
		if (!StringUtils.isEmpty(oldObject.getCorporateHqAddress())) {
			corporateHqAdress = oldObject.getCorporateHqAddress();
		}
		if (!workflowCustomerT.getCorporateHqAddress()
				.equals(corporateHqAdress)) {
			oldObject.setCorporateHqAddress(workflowCustomerT
					.getCorporateHqAddress());
			isCustomerModifiedFlag = true;
		}
		// facebook
		if (!StringUtils.isEmpty(oldObject.getFacebook())) {
			facebook = oldObject.getFacebook();
		}
		if (!workflowCustomerT.getFacebook().equals(facebook)) {
			oldObject.setFacebook(workflowCustomerT.getFacebook());
			isCustomerModifiedFlag = true;
		}
		// website
		if (!StringUtils.isEmpty(oldObject.getWebsite())) {
			website = oldObject.getWebsite();
		}
		if (!workflowCustomerT.getWebsite().equals(website)) {
			oldObject.setWebsite(workflowCustomerT.getWebsite());
			isCustomerModifiedFlag = true;
		}
		// geography
		if (!workflowCustomerT.getGeography().equals(oldObject.getGeography())) {
			oldObject.setGeography(workflowCustomerT.getGeography());
			isCustomerModifiedFlag = true;
		}
		// notes for edit
		if (!StringUtils.isEmpty(oldObject.getNotes())) {
			notes = oldObject.getNotes();
		}
		if (!workflowCustomerT.getNotes().equals(notes)
				&& (!StringUtils.isEmpty(workflowCustomerT.getNotes()))) {
			oldObject.setNotes(workflowCustomerT.getNotes());
			isCustomerModifiedFlag = true;
		}
		// group customer name
		if (!workflowCustomerT.getGroupCustomerName().equals(
				oldObject.getGroupCustomerName())) {
			oldObject.setGroupCustomerName(workflowCustomerT
					.getGroupCustomerName());
			isCustomerModifiedFlag = true;
		}
		// iou
		if (!workflowCustomerT.getIou().equals(oldObject.getIou())) {
			oldObject.setIou(workflowCustomerT.getIou());
			isCustomerModifiedFlag = true;
		}
		oldObject.setModifiedBy(userId);
		return isCustomerModifiedFlag;
	}

	/**
	 * 
	 * @param approveOrRejectSubject
	 * @param requestId
	 * @param date
	 * @param entityTypeId
	 * @throws Exception
	 */
	private void sendEmailNotificationforApprovedOrRejectMail(
			final String approveOrRejectSubject, Integer requestId, Date date,
			Integer entityTypeId) throws Exception {
		// TODO Auto-generated method stub
		class WorkflowNotificationForApproveOrReject implements Runnable {
			Integer requestId;
			Date date;
			Integer entityTypeId;

			WorkflowNotificationForApproveOrReject(Integer requestId,
					Date date, Integer entityTypeId) {
				this.requestId = requestId;
				this.date = date;
				this.entityTypeId = entityTypeId;
			}

			@Override
			public void run() {
				try {
					mailUtils.sendWorkflowApprovedOrRejectMail(
							approveOrRejectSubject, requestId, date,
							entityTypeId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Error sending email " + e.getMessage());
				}
			}
		}
		WorkflowNotificationForApproveOrReject workflowNotificationForApproveOrReject = new WorkflowNotificationForApproveOrReject(
				requestId, date, entityTypeId);
		mailTaskExecutor.execute(workflowNotificationForApproveOrReject);
		logger.debug("End:Inside sendEmailNotification of workflow pending");
	}

	private void sendEmailNotificationforPending(Integer requestId, Date date,
			Integer entityTypeId) throws Exception {
		// TODO Auto-generated method stub
		class WorkflowNotificationForPending implements Runnable {
			Integer requestId;
			Date date;
			Integer entityTypeId;

			WorkflowNotificationForPending(Integer requestId, Date date,
					Integer entityTypeId) {
				this.requestId = requestId;
				this.date = date;
				this.entityTypeId = entityTypeId;
			}

			@Override
			public void run() {
				try {
					mailUtils.sendWorkflowPendingMail(requestId, date,
							entityTypeId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		WorkflowNotificationForPending workflowNotificationForPending = new WorkflowNotificationForPending(
				requestId, date, entityTypeId);
		mailTaskExecutor.execute(workflowNotificationForPending);
		logger.debug("End:Inside sendEmailNotification of workflow pending");

	}

	/*
	 * on admin approval new entity was created in the master table
	 */
	private void saveToMasterTables(CustomerMasterT oldCustomerMaster,
			WorkflowCustomerT workflowCustomerT) {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		String corporateHqAdress = "";
		String facebook = "";
		String website = "";
		CustomerMasterT savedCustomer = null;
		oldCustomerMaster.setCustomerName(workflowCustomerT.getCustomerName());
		oldCustomerMaster.setGroupCustomerName(workflowCustomerT
				.getGroupCustomerName());

		// corpoarate address
		if (!StringUtils.isEmpty(oldCustomerMaster.getCorporateHqAddress())) {
			corporateHqAdress = oldCustomerMaster.getCorporateHqAddress();
		}
		if (!workflowCustomerT.getCorporateHqAddress()
				.equals(corporateHqAdress)) {
			oldCustomerMaster.setCorporateHqAddress(workflowCustomerT
					.getCorporateHqAddress());
		}

		// facebook
		if (!StringUtils.isEmpty(oldCustomerMaster.getFacebook())) {
			facebook = oldCustomerMaster.getFacebook();
		}
		if (!workflowCustomerT.getFacebook().equals(facebook)) {
			oldCustomerMaster.setFacebook(workflowCustomerT.getFacebook());
		}
		// website
		if (!StringUtils.isEmpty(oldCustomerMaster.getWebsite())) {
			website = oldCustomerMaster.getWebsite();
		}
		if (!workflowCustomerT.getWebsite().equals(website)) {
			oldCustomerMaster.setWebsite(workflowCustomerT.getWebsite());
		}
		oldCustomerMaster.setIou(workflowCustomerT.getIou());
		oldCustomerMaster.setGeography(workflowCustomerT.getGeography());
		oldCustomerMaster.setLogo(workflowCustomerT.getLogo());
		oldCustomerMaster.setDocumentsAttached(workflowCustomerT
				.getDocumentsAttached());
		oldCustomerMaster.setCreatedModifiedBy(userId);
		savedCustomer = customerRepository.save(oldCustomerMaster);
		if (!workflowCustomerT.getRevenueCustomerMappingTs().isEmpty()) {
			for (RevenueCustomerMappingT rcmpt : workflowCustomerT
					.getRevenueCustomerMappingTs()) {
				RevenueCustomerMappingT revenueCustomer = new RevenueCustomerMappingT();
				// RevenueCustomerMappingTPK revenueTPK = new
				// RevenueCustomerMappingTPK();
				revenueCustomer.setFinanceCustomerName(rcmpt
						.getFinanceCustomerName());
				revenueCustomer.setFinanceIou(rcmpt.getFinanceIou());
				revenueCustomer.setCustomerGeography(rcmpt
						.getCustomerGeography());
				revenueCustomer.setCustomerId(savedCustomer.getCustomerId());
				revenueRepository.save(revenueCustomer);
			}
		}
		if (!workflowCustomerT.getBeaconCustomerMappingTs().isEmpty()) {
			for (BeaconCustomerMappingT bcmpt : workflowCustomerT
					.getBeaconCustomerMappingTs()) {
				BeaconCustomerMappingT beaconCustomer = new BeaconCustomerMappingT();
				// BeaconCustomerMappingTPK beaconTPK = new
				// BeaconCustomerMappingTPK();
				beaconCustomer.setBeaconCustomerName(bcmpt
						.getBeaconCustomerName());
				beaconCustomer.setBeaconIou(bcmpt.getBeaconIou());
				beaconCustomer.setCustomerGeography(bcmpt
						.getCustomerGeography());
				beaconCustomer.setCustomerId(savedCustomer.getCustomerId());
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

		if (user.getUserRole()
				.equals(UserRole.STRATEGIC_GROUP_ADMIN.getValue())) {

			// true incase of admin: to validate the iou field for not empty
			// check
			validateWorkflowCustomerMasterDetails(requestedCustomerT, true);

			if (StringUtils.isEmpty(requestedCustomerT.getGroupCustomerName())) {
				logger.error("Group Customer name is mandatory");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Group Customer name is mandatory");
			}
			List<RevenueCustomerMappingT> revenueCustomerMappingTs = new ArrayList<RevenueCustomerMappingT>();
			List<BeaconCustomerMappingT> beaconCustomerMappingTs = new ArrayList<BeaconCustomerMappingT>();
			revenueCustomerMappingTs = requestedCustomerT
					.getRevenueCustomerMappingTs();

			if (CollectionUtils.isNotEmpty(revenueCustomerMappingTs)) {
				validateRevenueCustomerDetails(revenueCustomerMappingTs);
			} else {
				logger.error("Revenue customer details are mandatory for admin");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Revenue customer details are mandatory");
			}
			beaconCustomerMappingTs = requestedCustomerT
					.getBeaconCustomerMappingTs();
			if (CollectionUtils.isNotEmpty(beaconCustomerMappingTs)) {
				validateBeaconCustomerDetails(beaconCustomerMappingTs);
			}
			isAdminValidated = true;
		} else {
			// true incase of admin: to validate the iou field for not empty
			// check
			validateWorkflowCustomerMasterDetails(requestedCustomerT, false);
			isAdminValidated = true;
		}
		return isAdminValidated;
	}

	/**
	 * customer master integrity validations for requested customer
	 * 
	 * @param requestedCustomerT
	 */
	private void validateWorkflowCustomerMasterDetails(
			WorkflowCustomerT requestedCustomerT, boolean isAdmin) {

		String customerName = requestedCustomerT.getCustomerName();
		// customer name should not be empty
		if (StringUtils.isEmpty(customerName)) {
			logger.error("Customer Name should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Customer Name should not be empty");
		}
		// to check duplicate of customer name
		CustomerMasterT customerMaster = customerRepository
				.findByCustomerName(customerName);
		// WorkflowCustomerT workflowCustomer =
		// workflowCustomerRepository.findByCustomerName(customerName);
		if ((customerMaster != null)) {
			logger.error("Customer name already exists");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Customer name already exists" + customerName);
		}
		// foreign key constraint for geography
		if (!StringUtils.isEmpty(requestedCustomerT.getGeography())) {
			if (!mapOfGeographyMappingT.containsKey(requestedCustomerT
					.getGeography())) {
				logger.error("Invalid Geography");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Geography" + requestedCustomerT.getGeography());
			}
		} else {
			logger.error("Geography Should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Geography Should not be empty");
		}
		if (isAdmin) {
			// foreign key constraint for iou
			if (!StringUtils.isEmpty(requestedCustomerT.getIou())) {
				if (!mapOfIouCustomerMappingT.containsKey(requestedCustomerT
						.getIou())) {
					logger.error("Invalid IOU");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Invalid IOU" + requestedCustomerT.getIou());
				}
			} else {
				logger.error("IOU Should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"IOU Should not be empty");
			}
		}

	}

	/**
	 * validate beacon details for the requested customer
	 * 
	 * @param beaconCustomerMappingTs
	 */
	private void validateBeaconCustomerDetails(
			List<BeaconCustomerMappingT> beaconCustomerMappingTs) {
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
			beaconCustomers = beaconRepository.checkBeaconMappingPK(
					bcmt.getBeaconCustomerName(), bcmt.getCustomerGeography(),
					bcmt.getBeaconIou());
			if (!beaconCustomers.isEmpty()) {
				logger.error("The combination of the Beacon Customer Name, geography and beacon IOU already exists");
				throw new DestinationException(
						HttpStatus.BAD_REQUEST,
						"The combination of the Beacon Customer Name, geography and beacon IOU already exists");
			}
		}

	}

	/**
	 * validate revenue details for the requested customer
	 * 
	 * @param revenueCustomerMappingTs
	 */
	private void validateRevenueCustomerDetails(
			List<RevenueCustomerMappingT> revenueCustomerMappingTs) {
		List<RevenueCustomerMappingT> financeCustomers = null;
		for (RevenueCustomerMappingT rcmt : revenueCustomerMappingTs) {
			if (StringUtils.isEmpty(rcmt.getFinanceCustomerName())) {
				logger.error("Finance Customer name should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Finance Customer name should not be empty");
			}
			if (!StringUtils.isEmpty(rcmt.getCustomerGeography())) {
				if (!mapOfGeographyMappingT.containsKey(rcmt
						.getCustomerGeography())) {
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
			financeCustomers = revenueRepository.checkRevenueMappingPK(
					rcmt.getFinanceCustomerName(), rcmt.getCustomerGeography(),
					rcmt.getFinanceIou());
			if (!financeCustomers.isEmpty()) {
				logger.error("This Revenue details already exists.."
						+ rcmt.getFinanceCustomerName() + " "
						+ rcmt.getCustomerGeography() + " "
						+ rcmt.getFinanceIou());
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"This Revenue details already exists.."
								+ rcmt.getFinanceCustomerName() + " "
								+ rcmt.getCustomerGeography() + " "
								+ rcmt.getFinanceIou());
			}
		}
	}

	/**
	 * Requested entity is rejected with comments for rejection
	 * 
	 * @param workflowStepT
	 * @return
	 */
	public boolean rejectWorkflowEntity(WorkflowStepT workflowStepT) {

		int stepId = -1;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		stepId = workflowStepT.getStepId();
		WorkflowStepT workflowStepToReject = new WorkflowStepT();
		WorkflowRequestT masterRequest = new WorkflowRequestT();
		try {
			if (stepId != -1) {
				workflowStepToReject = workflowStepTRepository.findStep(stepId);
				if (workflowStepToReject != null
						&& workflowStepToReject.getStepStatus()
								.equalsIgnoreCase(
										WorkflowStatus.PENDING.getStatus())) {
					masterRequest = workflowRequestTRepository
							.findOne(workflowStepToReject.getRequestId());
					workflowStepToReject.setUserId(userId);
					workflowStepToReject.setStepStatus(workflowStepT
							.getStepStatus());
					workflowStepToReject.setModifiedBy(userId);
					if (!(workflowStepT.getComments().isEmpty())
							&& (workflowStepT.getComments() != null)) {
						workflowStepToReject.setComments(workflowStepT
								.getComments());
					} else {
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Comments is mandatory: give reason for rejection");
					}
					masterRequest.setModifiedBy(userId);
					masterRequest.setStatus(workflowStepT.getStepStatus());
					workflowStepTRepository.save(workflowStepToReject);
					workflowRequestTRepository.save(masterRequest);
					if (masterRequest.getEntityTypeId().equals(
							EntityTypeId.CUSTOMER.getType())) {
						sendEmailNotificationforApprovedOrRejectMail(
								workflowCustomerRejected,
								masterRequest.getRequestId(),
								masterRequest.getCreatedDatetime(),
								EntityTypeId.CUSTOMER.getType());
					}
					if (masterRequest.getEntityTypeId().equals(
							EntityTypeId.PARTNER.getType())) {
						sendEmailNotificationforApprovedOrRejectMail(
								workflowPartnerRejectedSubject,
								masterRequest.getRequestId(),
								masterRequest.getCreatedDatetime(),
								EntityTypeId.PARTNER.getType());
					}
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Record doesnot exist for the given stepId: "
									+ stepId);
				}
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"StepId is not valid or empty");
			}
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while rejecting the Request");
		}
		return true;
	}

	/**
	 * This method inserts the workflow customer including respective workflow
	 * request and steps for normal users and inserts the customer and mapping
	 * details for strategic group admin
	 * 
	 * @param workflowCustomer
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean insertWorkflowCustomer(WorkflowCustomerT workflowCustomer,
			Status status) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Inside insertWorkflowCustomer method");
		boolean insertStatus = false;

		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		if (validateRequestCustomer(workflowCustomer)) {

			workflowCustomer.setCreatedBy(userId);
			workflowCustomer.setModifiedBy(userId);
			workflowCustomer.setDocumentsAttached(Constants.NO);
			WorkflowCustomerT requestedCustomer = workflowCustomerRepository
					.save(workflowCustomer);
			logger.info("workflow Customer saved, Id :"
					+ requestedCustomer.getWorkflowCustomerId());
			if (requestedCustomer != null) {
				String entityId = requestedCustomer.getWorkflowCustomerId();
				Integer entityTypeId = EntityTypeId.CUSTOMER.getType();
				WorkflowRequestT workflowRequest = populateWorkflowRequest(
						entityId, entityTypeId, userId, null);
				if (workflowRequest != null) {
					if (workflowRequest.getStatus().equals(
							WorkflowStatus.PENDING.getStatus())) {
						status.setStatus(
								Status.SUCCESS,
								"Request for new customer "
										+ workflowCustomer.getCustomerName()
										+ " is submitted for approval");
						// Sending email notification to whom with the request
						// is pending currently
						sendEmailNotificationforPending(
								workflowRequest.getRequestId(), new Date(),
								entityTypeId);
					} else {
						// Saving workflow customer details to CustomerMasterT
						// for Admin
						// CustomerMasterT customerMasterObj = new
						// CustomerMasterT();
						// saveToMasterTables(customerMasterObj ,
						// requestedCustomer);
						saveToCustomerMasterTables(requestedCustomer);
						status.setStatus(Status.SUCCESS, "Customer "
								+ workflowCustomer.getCustomerName()
								+ " added successfully");
					}
					insertStatus = true;
				}
			}
		}

		return insertStatus;
	}

	/**
	 * This method generates the workflow request for the requested customer
	 * 
	 * @param entityId
	 * @param entityTypeId
	 * @param userId
	 * @return
	 */
	private WorkflowRequestT populateWorkflowRequest(String entityId,
			Integer entityTypeId, String userId, String comments)
			throws Exception {
		logger.info("Inside Start of populateWorkflowRequest method");
		List<WorkflowStepT> workflowSteps = null;
		UserT user = userRepository.findByUserId(userId);
		String userRole = user.getUserRole();
		String userGroup = user.getUserGroup();
		WorkflowRequestT workflowRequest = new WorkflowRequestT();
		workflowRequest.setEntityId(entityId);
		workflowRequest.setEntityTypeId(entityTypeId);
		workflowRequest.setCreatedBy(userId);
		workflowRequest.setModifiedBy(userId);

		List<WorkflowProcessTemplate> workflowTemplates = new ArrayList<WorkflowProcessTemplate>();
		// Getting workflow templates for a particular entity
		workflowTemplates = workflowProcessTemplateRepository
				.findByEntityTypeIdOrderByStepAsc(entityTypeId);
		int templateStep = 0;
		for (WorkflowProcessTemplate wfpt : workflowTemplates) {
			if (wfpt.getUserGroup() != null || wfpt.getUserRole() != null
					|| wfpt.getUserId() != null) {
				if (!StringUtils.isEmpty(wfpt.getUserGroup())) {
					if (wfpt.getUserGroup().contains(userGroup)
							|| (isUserPMO(userId) && wfpt.getUserGroup()
									.contains("PMO"))) {
						// if (wfpt.getUserGroup().contains(userGroup)) {
						templateStep = wfpt.getStep();
					}
				}
				if (!StringUtils.isEmpty(wfpt.getUserRole())) {
					if (wfpt.getUserRole().contains(userRole)) {
						templateStep = wfpt.getStep();
					}
				}
				if (!StringUtils.isEmpty(wfpt.getUserId())) {
					if (wfpt.getUserId().contains(userId)) {
						templateStep = wfpt.getStep();
					}
				}
			}

		}
		if (templateStep == 0) {
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User does not have access to this service");
		}
		WorkflowProcessTemplate workflowProcessTemplate = new WorkflowProcessTemplate();
		workflowProcessTemplate = workflowProcessTemplateRepository
				.findByEntityTypeIdAndStep(entityTypeId, templateStep);
		// Generating workflow steps from workflow process template for a
		// request based on user role or user group or user id
		workflowSteps = populateWorkFlowStepForUserRoleOrUserGroupOrUserId(
				workflowProcessTemplate, user, workflowRequest, comments);
		workflowRequest.setWorkflowStepTs(workflowSteps);
		workflowRequestTRepository.save(workflowRequest);
		logger.info("Workflow request saved, Request Id :"
				+ workflowRequest.getRequestId());
		// Saving the workflow steps and the setting the request id in each step
		for (WorkflowStepT wfs : workflowSteps) {
			wfs.setRequestId(workflowRequest.getRequestId());
			workflowStepTRepository.save(wfs);
		}
		logger.info("Inside End of populateWorkflowRequest method");
		return workflowRequest;
	}

	/**
	 * Generates workflow steps from workflow process template for a request
	 * based on user role or user group or user id
	 * 
	 * @param workflowProcessTemplate
	 * @param user
	 * @param workflowRequest
	 * @return
	 */
	private List<WorkflowStepT> populateWorkFlowStepForUserRoleOrUserGroupOrUserId(
			WorkflowProcessTemplate workflowProcessTemplate, UserT user,
			WorkflowRequestT workflowRequest, String comments) {
		logger.info("Inside populateWorkFlowStepForUserRoleOrUserGroupOrUserId method");
		String userId = user.getUserId();
		List<WorkflowStepT> workflowSteps = new ArrayList<WorkflowStepT>();
		List<WorkflowProcessTemplate> workflowTemplatesForNotapplicable = new ArrayList<WorkflowProcessTemplate>();
		Integer stepPending = workflowProcessTemplate.getStep() + 1;
		// Getting workflow template for pending
		WorkflowProcessTemplate workflowTemplateForPending = workflowProcessTemplateRepository
				.findByEntityTypeIdAndStep(
						workflowProcessTemplate.getEntityTypeId(), stepPending);
		if (workflowTemplateForPending != null) {

			workflowSteps.add(constructWorkflowStep(workflowProcessTemplate,
					userId, WorkflowStatus.SUBMITTED.getStatus(), comments));
			workflowSteps.add(constructWorkflowStep(workflowTemplateForPending,
					userId, WorkflowStatus.PENDING.getStatus(), comments));
			workflowRequest.setStatus(WorkflowStatus.PENDING.getStatus());
			// Getting workflow template for rest of the user categories as not
			// applicable
			workflowTemplatesForNotapplicable = workflowProcessTemplateRepository
					.findByEntityTypeIdAndStepGreaterThan(
							workflowProcessTemplate.getEntityTypeId(),
							workflowTemplateForPending.getStep());
			if (CollectionUtils.isNotEmpty(workflowTemplatesForNotapplicable)) {
				for (WorkflowProcessTemplate workflowProcessTemplateForNotApplicable : workflowTemplatesForNotapplicable) {
					workflowSteps
							.add(constructWorkflowStep(
									workflowProcessTemplateForNotApplicable,
									userId,
									WorkflowStatus.NOT_APPLICABLE.getStatus(),
									comments));
				}
			}

		} else {
			workflowSteps.add(constructWorkflowStep(workflowProcessTemplate,
					userId, WorkflowStatus.APPROVED.getStatus(), comments));
			workflowRequest.setStatus(WorkflowStatus.APPROVED.getStatus());
		}
		return workflowSteps;
	}

	/**
	 * Gives the workflow step based on the template and status
	 * 
	 * @param workflowProcessTemplate
	 * @param userId
	 * @param status
	 * @return
	 */
	private WorkflowStepT constructWorkflowStep(
			WorkflowProcessTemplate workflowProcessTemplate, String userId,
			String status, String comments) {
		WorkflowStepT workflowStep = new WorkflowStepT();
		workflowStep.setStep(workflowProcessTemplate.getStep());
		workflowStep.setStepStatus(status);
		workflowStep.setUserRole(workflowProcessTemplate.getUserRole());
		workflowStep.setUserGroup(workflowProcessTemplate.getUserGroup());
		if (status.equals(WorkflowStatus.SUBMITTED.getStatus())
				|| status.equals(WorkflowStatus.APPROVED.getStatus())) {
			workflowStep.setUserId(userId);
			if (comments != null) {
				workflowStep.setComments(comments);
			}
		} else {
			workflowStep.setUserId(workflowProcessTemplate.getUserId());
		}
		workflowStep.setCreatedBy(userId);
		workflowStep.setModifiedBy(userId);
		return workflowStep;
	}

	private boolean isUserPMO(String userId) {
		boolean flag = false;
		if (userId.contains("pmo")) {
			flag = true;
		}
		return flag;
	}

	/**
	 * validates the workflow customer details
	 * 
	 * @param requestedCustomerT
	 * @return
	 */
	private boolean validateRequestCustomer(WorkflowCustomerT requestedCustomerT) {
		logger.info("Inside validateRequestCustomer Method");
		boolean isValid = false;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		UserT user = userRepository.findByUserId(userId);

		mapOfGeographyMappingT = customerUploadService.getGeographyMappingT();
		mapOfIouCustomerMappingT = customerUploadService.getIouMappingT();
		mapOfIouBeaconMappingT = customerUploadService.getBeaconIouMappingT();

		validateWorkflowCustomerMasterDetails(requestedCustomerT, false);

		if (user.getUserRole()
				.equals(UserRole.STRATEGIC_GROUP_ADMIN.getValue())) {
			String groupCustomerName = requestedCustomerT
					.getGroupCustomerName();
			if (StringUtils.isEmpty(groupCustomerName)) {
				logger.error("Group Customer name is mandatory");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Group Customer name is mandatory");
			}
			String iou = requestedCustomerT.getIou();
			if (!StringUtils.isEmpty(iou)) {
				if (!mapOfIouCustomerMappingT.containsKey(iou)) {
					logger.error("Invalid IOU");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Invalid IOU" + iou);
				}
			} else {
				logger.error("IOU Should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"IOU Should not be empty");
			}
			List<RevenueCustomerMappingT> revenueCustomerMappingTs = new ArrayList<RevenueCustomerMappingT>();
			List<BeaconCustomerMappingT> beaconCustomerMappingTs = new ArrayList<BeaconCustomerMappingT>();
			revenueCustomerMappingTs = requestedCustomerT
					.getRevenueCustomerMappingTs();
			if (CollectionUtils.isNotEmpty(revenueCustomerMappingTs)) {
				validateRevenueCustomerDetails(revenueCustomerMappingTs);
			} else {
				logger.error("Revenue customer details are mandatory");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Revenue customer details are mandatory");
			}
			beaconCustomerMappingTs = requestedCustomerT
					.getBeaconCustomerMappingTs();
			if (CollectionUtils.isNotEmpty(beaconCustomerMappingTs)) {
				validateBeaconCustomerDetails(beaconCustomerMappingTs);
			}
		}
		isValid = true;
		return isValid;
	}

	/**
	 * This method is used to retrieve workflow customer details based on Id.
	 * 
	 * @param requestedCustomerId
	 * @return
	 * @throws Exception
	 */
	public WorkflowCustomerDetailsDTO findRequestedCustomerDetailsById(
			Integer requestedCustomerId) throws DestinationException {
		logger.debug("Inside findRequestedCustomerDetailsById() service: Start");
		try {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			UserT user = userRepository.findByUserId(userId);
			WorkflowCustomerDetailsDTO workflowCustomerDetailsDTO = new WorkflowCustomerDetailsDTO();
			if (requestedCustomerId != null) {
				// Request details are retrieved based on Id
				WorkflowRequestT workflowRequest = workflowRequestRepository
						.findByRequestId(requestedCustomerId);
				if (workflowRequest != null) {

					// Check if the particular request is a new customer request
					if (workflowRequest.getEntityTypeId() == EntityTypeId.CUSTOMER
							.getType()) {

						// Get the status of the new customer request
						workflowCustomerDetailsDTO.setStatus(workflowRequest
								.getStatus());
						// Get the workflow customer Id from request table
						String workflowCustomerId = workflowRequest
								.getEntityId();
						// Get the new customer details for the request
						WorkflowCustomerT workflowCustomer = workflowCustomerRepository
								.findOne(workflowCustomerId);

						if (workflowCustomer != null) {
							workflowCustomer
									.setRevenueCustomerMappingTs(revenueRepository
											.getRevenueCustomerMappingForWorkflowCustomer(requestedCustomerId));
							workflowCustomer
									.setBeaconCustomerMappingTs(beaconRepository
											.getBeaconMappingForWorkflowCustomer(requestedCustomerId));
							workflowCustomerDetailsDTO
									.setRequestedCustomer(workflowCustomer);

							// Get the workflow steps associated with the new
							// customer request
							List<WorkflowStepT> workflowSteps = workflowRequest
									.getWorkflowStepTs();
							if (workflowSteps != null) {
								workflowCustomerDetailsDTO
										.setWorkflowSteps(workflowSteps);
								// Check if user is authorized to access the
								// request details
								boolean authorizedUserFlag = getAuthorizedUserFlag(
										workflowSteps, userId);
							} else {
								logger.info("No step details found for workflow customer id: "
										+ workflowCustomerId);
								throw new DestinationException(
										HttpStatus.INTERNAL_SERVER_ERROR,
										"Backend error in retrieving customer details");
							}
						} else {
							logger.info("workflow customer id: "
									+ workflowCustomerId
									+ " is not a valid workflow customer id");
							throw new DestinationException(
									HttpStatus.INTERNAL_SERVER_ERROR,
									"Backend error in retrieving customer details");
						}
					} else {
						logger.info("Request id: " + requestedCustomerId
								+ " is not a valid CUSTOMER request id");
						throw new DestinationException(HttpStatus.NOT_FOUND,
								"Request id is not a customer request");
					}
				} else {
					logger.info("No request found for the given request id");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No request found for the given request id");
				}
			} else {
				logger.info("Request Id cannot be null");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Request id is not valid or empty");
			}
			logger.debug("Inside findRequestedCustomerDetailsById() service: End");
			return workflowCustomerDetailsDTO;
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving request customer details");
		}
	}

	/**
	 * This method is used to retrieve workflow partner details based on Id.
	 * 
	 * @param requestedPartnerId
	 * @return
	 */
	public WorkflowPartnerDetailsDTO findRequestedPartnerDetailsById(
			Integer requestedPartnerId) {
		logger.debug("Inside findRequestedPartnerDetailsById() service: Start");
		try {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			WorkflowPartnerDetailsDTO workflowPartnerDetailsDTO = new WorkflowPartnerDetailsDTO();
			if (requestedPartnerId != null) {
				// Request details are retrieved based on Id
				WorkflowRequestT workflowRequest = workflowRequestRepository
						.findByRequestId(requestedPartnerId);
				if (workflowRequest != null) {

					// Check if the particular request is a new partner request
					if (workflowRequest.getEntityTypeId() == EntityTypeId.PARTNER
							.getType()) {

						// Get the status of the new partner request
						workflowPartnerDetailsDTO.setStatus(workflowRequest
								.getStatus());

						// Get the workflow partner Id from request table
						String workflowPartnerId = workflowRequest
								.getEntityId();
						// Get the new partner details for the request
						WorkflowPartnerT workflowPartner = workflowPartnerRepository
								.findOne(workflowPartnerId);

						if (workflowPartner != null) {
							workflowPartnerDetailsDTO
									.setRequestedPartner(workflowPartner);

							// Get the workflow steps associated with the new
							// partner request
							List<WorkflowStepT> workflowSteps = workflowRequest
									.getWorkflowStepTs();
							if (workflowSteps != null) {
								workflowPartnerDetailsDTO
										.setWorkflowSteps(workflowSteps);
								// Check if user is authorized to access the
								// request details
								boolean authorizedUserFlag = getAuthorizedUserFlag(
										workflowSteps, userId);
							} else {
								logger.info("No step details found for workflow partner id: "
										+ workflowPartnerId);
								throw new DestinationException(
										HttpStatus.INTERNAL_SERVER_ERROR,
										"Backend error in retrieving partner details");
							}
						} else {
							logger.info("workflow partner id: "
									+ workflowPartnerId
									+ " is not a valid workflow partner id");
							throw new DestinationException(
									HttpStatus.INTERNAL_SERVER_ERROR,
									"Backend error in retrieving partner details");
						}
					} else {
						logger.info("Request id: " + requestedPartnerId
								+ " is not a valid PARTNER request id");
						throw new DestinationException(HttpStatus.NOT_FOUND,
								"Request id is not a partner request");
					}
				} else {
					logger.info("No request found for the given request id");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No request found for the given request id");
				}
			} else {
				logger.info("Request Id cannot be null");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Request id is not valid or empty");
			}
			logger.debug("Inside findRequestedPartnerDetailsById() service: End");
			return workflowPartnerDetailsDTO;
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving request customer details");
		}
	}

	/**
	 * This service is used to retrieve the worklist of the logged in user
	 * 
	 * @param status
	 * @param page
	 * @param count
	 * @return
	 */
	public PaginatedResponse getMyWorklist(String status, int page, int count)
			throws DestinationException {
		try {
			logger.debug("Start of getMyWorklist service");
			// userId of the logged in user is retrieved
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			PaginatedResponse worklistResponse = new PaginatedResponse();
			// Contains list of all requests including customer, partner etc
			List<MyWorklistDTO> myWorklist = new ArrayList<MyWorklistDTO>();
			// Contains all the lists of customer requests
			List<List<Object[]>> listOfCustomerRequests = new ArrayList<>();
			// Contains all the lists of partner requests
			List<List<Object[]>> listOfPartnerRequests = new ArrayList<>();
			// Contains all the lists of partner requests
			List<List<Object[]>> listOfCompetitorRequests = new ArrayList<>();
			// Contains all the lists of partner requests
			List<List<Object[]>> listOfOpportunityReopenRequests = new ArrayList<>();

			// Get all requests
			Set<MyWorklistDTO> submittedAndApprovedRequests = getSubmittedAndApprovedRequests(
					status, userId);

			if (status.equalsIgnoreCase("ALL")) {

				// Get all requests pending for approval/rejection by user
				List<Object[]> pendingCustomerRequests = getPendingCustomerRequests(userId);
				List<Object[]> pendingPartnerRequests = getPendingPartnerRequests(userId);
				// List<Object[]> pendingCompetitorRequests =
				// getPendingCompetitorRequests(userId);
				List<Object[]> pendingOpportunityReopenRequests = getPendingOpportunityReopenRequests(userId);

				// Add all the lists of customer requests
				listOfCustomerRequests.add(pendingCustomerRequests);

				// Add all the lists of partner requests
				listOfPartnerRequests.add(pendingPartnerRequests);

				// Add all the lists of competitor requests
				// listOfCompetitorRequests.add(pendingCompetitorRequests);

				// Add all the lists of opportunity re-open requests
				listOfOpportunityReopenRequests
						.add(pendingOpportunityReopenRequests);
			}
			if (status.equalsIgnoreCase(WorkflowStatus.PENDING.getStatus())) {
				myWorklist = new ArrayList<MyWorklistDTO>();

				// Get all requests pending for user's approval/rejection
				List<Object[]> pendingCustomerRequests = getPendingCustomerRequests(userId);
				List<Object[]> pendingPartnerRequests = getPendingPartnerRequests(userId);
				// List<Object[]> pendingCompetitorRequests =
				// getPendingCompetitorRequests(userId);
				List<Object[]> pendingOpportunityReopenRequests = getPendingOpportunityReopenRequests(userId);

				// Add all the lists of customer requests
				listOfCustomerRequests.add(pendingCustomerRequests);

				// Add all the lists of partner requests
				listOfPartnerRequests.add(pendingPartnerRequests);

				// Add all the lists of competitor requests
				// listOfCompetitorRequests.add(pendingCompetitorRequests);

				// Add all the lists of opportunity re-open requests
				listOfOpportunityReopenRequests
						.add(pendingOpportunityReopenRequests);
			}

			// Populate the response object
			populateResponseList(listOfCustomerRequests,
					EntityType.CUSTOMER.toString(), myWorklist);
			populateResponseList(listOfPartnerRequests,
					EntityType.PARTNER.toString(), myWorklist);
			// populateResponseList(listOfCompetitorRequests,
			// EntityType.COMPETITOR.toString(), myWorklist);
			populateResponseList(listOfOpportunityReopenRequests,
					EntityType.OPPORTUNITY.toString(), myWorklist);

			// Add competitor list
			myWorklist.addAll(Lists.newArrayList(submittedAndApprovedRequests));

			// Sort the list based on modified date time
			Collections.sort(myWorklist);
			if (myWorklist.isEmpty()) {
				logger.debug("No items in worklist for the user" + userId);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No requests found with stage - " + status);
			}
			if (myWorklist != null && myWorklist.isEmpty()) {
				logger.debug("No items in worklist for the user" + userId);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No requests found with stage - " + status);
			}
			worklistResponse.setTotalCount(myWorklist.size());
			myWorklist = paginateMyWorklist(page, count, myWorklist);
			worklistResponse.setMyWorklists(myWorklist);
			logger.debug("End of getMyWorklist service");
			return worklistResponse;
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving worklist details");
		}
	}

	private List<Object[]> getPendingOpportunityReopenRequests(String userId) {
		// TODO Auto-generated method stub
		List<Object[]> resultList = null;
		UserT user = userRepository.findByUserId(userId);
		String userRole = user.getUserRole();
		String userGroup = user.getUserGroup();
		userRole = "%" + userRole + "%";
		userGroup = "%" + userGroup + "%";
		// Query to get pending partner requests for specific user's
		// approval/rejection
		if (userId.contains("pmo")) {
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.OPPORTUNTIY_REOPEN_PENDING_WITH_GEO_GROUP_QUERY);
			Query query = entityManager.createNativeQuery(queryBuffer
					.toString());
			query.setParameter("userId", userId);
		}
		// Query to get pending with group of users, based on user's role and
		// user group
		StringBuffer queryBuffer = new StringBuffer(
				QueryConstants.OPPORTUNTIY_REOPEN_PENDING_WITH_GROUP_QUERY);
		Query query = entityManager.createNativeQuery(queryBuffer.toString());
		query.setParameter("userRole", userRole);
		query.setParameter("userGroup", userGroup);
		if (resultList == null) {
			resultList = query.getResultList();
		} else {
			List<Object[]> resultForGroupPending = query.getResultList();
			resultList.addAll(resultForGroupPending);
		}

		return resultList;
	}

	private List<Object[]> getPendingCompetitorRequests(String userId) {
		// TODO Auto-generated method stub
		List<Object[]> resultList = null;
		UserT user = userRepository.findByUserId(userId);
		String userRole = user.getUserRole();
		String userGroup = user.getUserGroup();
		userRole = "%" + userRole + "%";
		userGroup = "%" + userGroup + "%";

		// Query to get pending with group of users, based on user's role and
		// user group
		StringBuffer queryBuffer = new StringBuffer(
				QueryConstants.PARTNER_PENDING_WITH_GROUP_QUERY);
		Query query = entityManager.createNativeQuery(queryBuffer.toString());
		query.setParameter("userRole", userRole);
		query.setParameter("userGroup", userGroup);
		if (resultList == null) {
			resultList = query.getResultList();
		} else {
			List<Object[]> resultForGroupPending = query.getResultList();
			resultList.addAll(resultForGroupPending);
		}

		return resultList;
	}

	/**
	 * @param status
	 * @param userId
	 * @return
	 */
	private Set<MyWorklistDTO> getSubmittedAndApprovedRequests(String status,
			String userId) {
		logger.info("Starting getSubmittedAndApprovedRequests");

		Set<WorkflowRequestT> workFlowRequest = new HashSet<WorkflowRequestT>();
		List<WorkflowRequestT> workFlowSubmittedRequest = null;
		List<WorkflowRequestT> workFlowActionedRequest = null;

		if (status.equalsIgnoreCase("ALL")) {
			workFlowActionedRequest = workflowRequestTRepository
					.getModifiedBy(userId);
			workFlowSubmittedRequest = workflowRequestTRepository
					.findByCreatedBy(userId);

		} else {
			workFlowActionedRequest = workflowRequestTRepository
					.getModifiedByAndStatus(userId, status);
			workFlowSubmittedRequest = workflowRequestTRepository
					.findByCreatedByAndStatus(userId, status);
		}

		if (CollectionUtils.isNotEmpty(workFlowSubmittedRequest)) {
			workFlowRequest.addAll(workFlowSubmittedRequest);
		}
		if (CollectionUtils.isNotEmpty(workFlowActionedRequest)) {
			workFlowRequest.addAll(workFlowActionedRequest);
		}
		logger.info("Ending getSubmittedAndApprovedRequests");
		return populateSubmittedAndApprovedRequests(workFlowRequest);
	}

	/**
	 * @param workFlowRequestCompetitor
	 * @return List<MyWorklistDTO>
	 */
	private Set<MyWorklistDTO> populateSubmittedAndApprovedRequests(
			Set<WorkflowRequestT> workFlowRequestCompetitor) {

		logger.debug("Starting populateCompetitorList");

		Set<MyWorklistDTO> myWorklistDTOs = new HashSet<MyWorklistDTO>();
		if (CollectionUtils.isNotEmpty(workFlowRequestCompetitor)) {
			for (WorkflowRequestT requestT : workFlowRequestCompetitor) {
				MyWorklistDTO myWorklistDTO = new MyWorklistDTO();

				switch (EntityTypeId.valueOf(EntityTypeId.getName(requestT
						.getEntityTypeId()))) {
				case CUSTOMER:
					myWorklistDTO.setEntityType(CUSTOMER.getDisplayName());
					myWorklistDTO.setEntityName(workflowCustomerRepository
							.findOne(requestT.getEntityId()).getCustomerName());
					break;
				case PARTNER:
					myWorklistDTO.setEntityType(PARTNER.getDisplayName());
					myWorklistDTO.setEntityName(workflowPartnerRepository
							.findOne(requestT.getEntityId()).getPartnerName());
					break;
				case COMPETITOR:
					myWorklistDTO.setEntityType(COMPETITOR.getDisplayName());
					myWorklistDTO.setEntityName(workflowCompetitorRepository
							.findOne(requestT.getEntityId())
							.getWorkflowCompetitorName());
					break;
				case OPPORTUNITY:
					myWorklistDTO.setEntityType(EntityTypeId.OPPORTUNITY
							.getDisplayName());
					myWorklistDTO.setEntityName(workflowOpportunityRepository
							.findOne(requestT.getEntityId())
							.getOpportunityName());
					break;
				}
				myWorklistDTO.setRequestId(requestT.getRequestId());

				WorkflowStepT stepT = workflowStepRepository
						.findFirstByRequestIdAndStepStatusNotOrderByStepIdDesc(
								requestT.getRequestId(),
								WorkflowStatus.NOT_APPLICABLE.getStatus());
				myWorklistDTO.setWorkflowStep(stepT);
				myWorklistDTO.setModifiedDatetime(stepT.getModifiedDatetime());
				myWorklistDTOs.add(myWorklistDTO);
			}
		}

		logger.debug("Ending populateCompetitorList");

		return myWorklistDTOs;
	}

	/**
	 * This method performs pagination for the getMyWorklist service
	 * 
	 * @param page
	 * @param count
	 * @param myWorklist
	 * @return
	 */
	private List<MyWorklistDTO> paginateMyWorklist(int page, int count,
			List<MyWorklistDTO> myWorklist) {
		if (PaginationUtils.isValidPagination(page, count, myWorklist.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					myWorklist.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					myWorklist.size()) + 1;
			myWorklist = myWorklist.subList(fromIndex, toIndex);
			logger.debug("MyWorklist after pagination size is "
					+ myWorklist.size());
		} else {
			myWorklist = null;
		}
		return myWorklist;
	}

	/**
	 * This method is used to populate the response object
	 * 
	 * @param listOfEntityRequests
	 * @param EntityType
	 * @param myWorklist
	 */
	private void populateResponseList(
			List<List<Object[]>> listOfEntityRequests, String entityType,
			List<MyWorklistDTO> myWorklist) {
		logger.debug("Start of populating response for worklist");
		for (int i = 0; i < listOfEntityRequests.size(); i++) {
			List<Object[]> tempRequestObject = listOfEntityRequests.get(i);
			if (tempRequestObject != null) {

				// Iterate the result and set the response object
				for (Object[] MyWorklistDTOArray : tempRequestObject) {

					MyWorklistDTO worklist = new MyWorklistDTO();
					if (entityType.equalsIgnoreCase(EntityType.CUSTOMER
							.toString())) {
						// All customer requests
						worklist.setEntityType("New Customer");
					} else if (entityType.equalsIgnoreCase(EntityType.PARTNER
							.toString())) {
						// All Partner requests
						worklist.setEntityType("New Partner");
					} else if (entityType
							.equalsIgnoreCase(EntityType.COMPETITOR.toString())) {
						// All Partner requests
						worklist.setEntityType("New Competitor");
					} else if (entityType
							.equalsIgnoreCase(EntityType.OPPORTUNITY.toString())) {
						// All Partner requests
						worklist.setEntityType("New Opportunity Reopen");
					}

					WorkflowStepT workflowStep = new WorkflowStepT();

					if (MyWorklistDTOArray[0] != null) {
						worklist.setEntityName(MyWorklistDTOArray[0].toString());
					} else {
						worklist.setEntityName("Unnamed");
					}
					if (MyWorklistDTOArray[2] != null) {
						String s = MyWorklistDTOArray[2].toString();
						workflowStep.setStepId(Integer.parseInt(s));
					}
					if (MyWorklistDTOArray[3] != null) {
						String s = MyWorklistDTOArray[3].toString();
						workflowStep.setRequestId(Integer.parseInt(s));
					}
					if (MyWorklistDTOArray[4] != null) {
						String s = MyWorklistDTOArray[4].toString();
						workflowStep.setStep(Integer.parseInt(s));
					}
					if (MyWorklistDTOArray[5] != null) {
						workflowStep
								.setUserId(MyWorklistDTOArray[5].toString());
						workflowStep
								.setUser(userRepository
										.findByUserId(MyWorklistDTOArray[5]
												.toString()));
					}
					if (MyWorklistDTOArray[6] != null) {
						workflowStep.setStepStatus(MyWorklistDTOArray[6]
								.toString());
					}
					if (MyWorklistDTOArray[7] != null) {
						workflowStep.setComments(MyWorklistDTOArray[7]
								.toString());
					}
					if (MyWorklistDTOArray[8] != null) {
						workflowStep.setCreatedBy(MyWorklistDTOArray[8]
								.toString());
						workflowStep
								.setCreatedByUser(userRepository
										.findByUserId(MyWorklistDTOArray[8]
												.toString()));
					}
					if (MyWorklistDTOArray[9] != null) {
						String s = MyWorklistDTOArray[9].toString();
						workflowStep.setCreatedDatetime(Timestamp.valueOf(s));
					}
					if (MyWorklistDTOArray[10] != null) {
						workflowStep.setModifiedBy(MyWorklistDTOArray[10]
								.toString());
					}
					if (MyWorklistDTOArray[11] != null) {
						String s = MyWorklistDTOArray[11].toString();
						workflowStep.setModifiedDatetime(Timestamp.valueOf(s));
						worklist.setModifiedDatetime(Timestamp.valueOf(s));
					}
					if (MyWorklistDTOArray[12] != null) {
						workflowStep.setUserGroup(MyWorklistDTOArray[12]
								.toString());
					}
					if (MyWorklistDTOArray[13] != null) {
						workflowStep.setUserRole(MyWorklistDTOArray[13]
								.toString());
					}
					worklist.setWorkflowStep(workflowStep);
					myWorklist.add(worklist);

				}
			}
		}
		logger.debug("End of populating response for worklist");
	}

	/**
	 * This method retrieves new customer requests created by user, based on the
	 * status of request
	 * 
	 * @param status
	 * @param userId
	 * @return
	 */
	private List<Object[]> getMyRequestsForCustomer(String status, String userId) {
		logger.debug("Inside getMyRequestsForCustomer method : Start");
		List<Object[]> resultList = null;
		Query query = null;
		if (status.equals("ALL")) {
			// Query to get new customer requests created by user
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.QUERY_FOR_CUSTOMER_REQUESTS_PREFIX);
			queryBuffer.append(QueryConstants.MY_CUSTOMER_REQUESTS_SUFFIX1);
			queryBuffer.append(QueryConstants.MY_REQUESTS_SUFFIX2);
			queryBuffer.append(QueryConstants.MY_REQUESTS_APPROVED_SUFFIX);
			queryBuffer.append(QueryConstants.MY_REQUESTS_SUFFIX3);

			query = entityManager.createNativeQuery(queryBuffer.toString());
		} else if ((status.equals(WorkflowStatus.PENDING.getStatus()))
				|| (status.equals(WorkflowStatus.REJECTED.getStatus()))) {
			// Query to get new customer requests created by user
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.QUERY_FOR_CUSTOMER_REQUESTS_PREFIX);
			queryBuffer.append(QueryConstants.MY_CUSTOMER_REQUESTS_SUFFIX1);
			queryBuffer
					.append(QueryConstants.MY_REQUESTS_PENDING_REJECTED_SUFFIX);

			query = entityManager.createNativeQuery(queryBuffer.toString());
			query.setParameter("stepStatus", status);
		} else if (status.equals(WorkflowStatus.APPROVED.getStatus())) {
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.QUERY_FOR_CUSTOMER_REQUESTS_PREFIX);
			queryBuffer.append(QueryConstants.MY_CUSTOMER_REQUESTS_SUFFIX1);
			queryBuffer.append(QueryConstants.MY_REQUESTS_WHERE);
			queryBuffer.append(QueryConstants.MY_REQUESTS_APPROVED_SUFFIX);

			query = entityManager.createNativeQuery(queryBuffer.toString());
		}
		if (query != null) {
			query.setParameter("userId", userId);
			resultList = query.getResultList();
		}
		logger.debug("Inside getMyRequestsForCustomer method : End");
		return resultList;
	}

	/**
	 * This method retrieves new partner requests created by user, based on the
	 * status of request
	 * 
	 * @param status
	 * @param userId
	 * @return
	 */
	private List<Object[]> getMyRequestsForPartner(String status, String userId) {
		logger.debug("Inside getMyRequestsForPartner method : Start");
		// Query to get new customer requests created by user
		List<Object[]> resultList = null;
		Query query = null;
		if (status.equals("ALL")) {
			// Query to get new customer requests created by user
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.QUERY_FOR_PARTNER_REQUESTS_PREFIX);
			queryBuffer.append(QueryConstants.MY_PARTNER_REQUESTS_SUFFIX);
			queryBuffer.append(QueryConstants.MY_REQUESTS_SUFFIX2);
			queryBuffer.append(QueryConstants.MY_REQUESTS_APPROVED_SUFFIX);
			queryBuffer.append(QueryConstants.MY_REQUESTS_SUFFIX3);

			query = entityManager.createNativeQuery(queryBuffer.toString());
		} else if ((status.equals(WorkflowStatus.PENDING.getStatus()))
				|| (status.equals(WorkflowStatus.REJECTED.getStatus()))) {
			// Query to get new customer requests created by user
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.QUERY_FOR_PARTNER_REQUESTS_PREFIX);
			queryBuffer.append(QueryConstants.MY_PARTNER_REQUESTS_SUFFIX);
			queryBuffer
					.append(QueryConstants.MY_REQUESTS_PENDING_REJECTED_SUFFIX);

			query = entityManager.createNativeQuery(queryBuffer.toString());
			query.setParameter("stepStatus", status);
		} else if (status.equals(WorkflowStatus.APPROVED.getStatus())) {
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.QUERY_FOR_PARTNER_REQUESTS_PREFIX);
			queryBuffer.append(QueryConstants.MY_PARTNER_REQUESTS_SUFFIX);
			queryBuffer.append(QueryConstants.MY_REQUESTS_WHERE);
			queryBuffer.append(QueryConstants.MY_REQUESTS_APPROVED_SUFFIX);

			query = entityManager.createNativeQuery(queryBuffer.toString());
		}
		if (query != null) {
			query.setParameter("userId", userId);
			resultList = query.getResultList();
		}
		logger.debug("Inside getMyRequestsForPartner method : End");
		return resultList;

	}

	/**
	 * This method is used to retrieve requests which are
	 * approved/rejected(status) by the user
	 * 
	 * @param status
	 * @param userId
	 * @param entity
	 * @return
	 */
	private List<Object[]> getRequestsApprovedOrRejectedByUser(String status,
			String userId, String entity) {
		logger.debug("Inside getRequestsApprovedOrRejectedByUser method : Start");
		Query query = null;
		List<Object[]> resultList = null;
		if (entity.equals(EntityType.CUSTOMER.toString())) {
			if (status.equalsIgnoreCase(WorkflowStatus.APPROVED.getStatus())) {
				// Query to get customer requests APPROVED by user
				query = entityManager
						.createNativeQuery(QueryConstants.QUERY_CUSTOMER_FINAL_APPROVED);

			} else {
				// Query to get customer requests REJECTED by user
				StringBuffer queryBuffer = new StringBuffer(
						QueryConstants.QUERY_FOR_CUSTOMER_REQUESTS_PREFIX);
				queryBuffer
						.append(QueryConstants.APPROVED_REJECTED_REQUESTS_SUFFIX1);
				queryBuffer
						.append(QueryConstants.APPROVED_REJECTED_REQUESTS_SUFFIX2);
				query = entityManager.createNativeQuery(queryBuffer.toString());

			}
			query.setParameter("stepStatus", status);
			query.setParameter("userId", userId);
			resultList = query.getResultList();

		} else if (entity.equals(EntityType.PARTNER.toString())) {
			if (status.equalsIgnoreCase(WorkflowStatus.APPROVED.getStatus())) {
				// Query to get partner requests APPROVED by user
				query = entityManager
						.createNativeQuery(QueryConstants.QUERY_PARTNER_FINAL_APPROVED);
			} else {
				// Query to get partner requests REJECTED by user
				StringBuffer queryBuffer = new StringBuffer(
						QueryConstants.QUERY_FOR_PARTNER_REQUESTS_PREFIX);
				queryBuffer
						.append(QueryConstants.APPROVED_REJECTED_REQUESTS_SUFFIX1);
				queryBuffer
						.append(QueryConstants.APPROVED_REJECTED_REQUESTS_SUFFIX3);
				query = entityManager.createNativeQuery(queryBuffer.toString());
			}
			query.setParameter("stepStatus", status);
			query.setParameter("userId", userId);
			resultList = query.getResultList();
		}
		logger.debug("Inside getRequestsApprovedOrRejectedByUser method : End");
		return resultList;

	}

	/**
	 * This method is used to retrieve customer requests pending with user
	 * 
	 * @param userId
	 * @return
	 */
	private List<Object[]> getPendingCustomerRequests(String userId) {
		logger.debug("Inside getPendingCustomerRequests method : Start");
		List<Object[]> resultList = null;
		// Get user role and user group
		UserT user = userRepository.findByUserId(userId);
		String userRole = user.getUserRole();
		String userGroup = user.getUserGroup();
		String userRoleLike = "%" + userRole + "%";
		String userGroupLike = "%" + userGroup + "%";
		List<Object[]> resultForGroupPending = null;
		Query query = null;
		switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
		case IOU_HEADS: {
			// Query to get customer requests pending based on IOU
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.CUSTOMER_PENDING_WITH_IOU_GROUP_QUERY);
			query = entityManager.createNativeQuery(queryBuffer.toString());
			query.setParameter("userId", userId);
			break;
		}
		case GEO_HEADS: {
			// Query to get customer requests pending based on Geography
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.CUSTOMER_PENDING_WITH_GEO_GROUP_QUERY);
			query = entityManager.createNativeQuery(queryBuffer.toString());
			query.setParameter("userId", userId);
			break;
		}
		case STRATEGIC_INITIATIVES: {
			// Query to get customer requests pending for a SI as no access
			// privilege applies to SI
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.CUSTOMER_PENDING_WITH_SI_QUERY);
			query = entityManager.createNativeQuery(queryBuffer.toString());
			break;
		}
		}
		if (userId.contains("pmo")) {
			StringBuffer queryBuffer = new StringBuffer(
					QueryConstants.CUSTOMER_PENDING_WITH_GEO_GROUP_QUERY);
			query = entityManager.createNativeQuery(queryBuffer.toString());
			query.setParameter("userId", userId);
		}
		if (query != null) {
			query.setParameter("userRole", userRoleLike);
			query.setParameter("userGroup", userGroupLike);
			resultForGroupPending = query.getResultList();
		}

		resultList = resultForGroupPending;
		// Query to get pending customer requests for specific user's
		// approval/rejection
		StringBuffer queryBuffer = new StringBuffer(
				QueryConstants.CUSTOMER_PENDING_WITH_USER_QUERY);
		query = entityManager.createNativeQuery(queryBuffer.toString());
		query.setParameter("userId", userId);
		if (resultList != null) {
			if (resultList.isEmpty()) {
				resultList = query.getResultList();
			} else {
				List<Object[]> resultForUserPending = query.getResultList();
				resultList.addAll(resultForUserPending);
			}
		} else {
			resultList = query.getResultList();
		}
		logger.debug("Inside getPendingCustomerRequests method : End");
		return resultList;
	}

	/**
	 * This method inserts the workflow partner including respective workflow
	 * request and steps for normal users and inserts the partner master details
	 * for system admin
	 * 
	 * @param workflowPartner
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean addPartner(WorkflowPartnerT workflowPartner, Status status)
			throws Exception {
		logger.info("Inside PartnerWorkflowService ::  addPartner() ");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		mapOfGeographyMappingT = customerUploadService.getGeographyMappingT();
		validateRequestedPartner(workflowPartner);
		workflowPartner.setCreatedBy(userId);
		workflowPartner.setModifiedBy(userId);
		workflowPartner.setDocumentsAttached(Constants.NO);
		WorkflowPartnerT requestedPartner = workflowPartnerRepository
				.save(workflowPartner);
		logger.info("Workflow Partner saved , Id : "
				+ requestedPartner.getWorkflowPartnerId());
		if (requestedPartner != null) {
			String entityId = requestedPartner.getWorkflowPartnerId();
			Integer entityTypeId = EntityTypeId.PARTNER.getType();
			WorkflowRequestT workflowRequest = populateWorkflowRequest(
					entityId, entityTypeId, userId, null);
			if (workflowRequest != null) {
				if (workflowRequest.getStatus().equals(
						WorkflowStatus.PENDING.getStatus())) {
					status.setStatus(Status.SUCCESS, "Request for new Partner "
							+ requestedPartner.getPartnerName()
							+ " is submitted for approval");
					// Sending email notification to whom with the request
					// is pending currently
					sendEmailNotificationforPending(
							workflowRequest.getRequestId(), new Date(),
							entityTypeId);
				} else {
					// Saving workflow Partner details to PartnerMasterT
					// for Admin
					savePartnerMaster(requestedPartner);
					status.setStatus(Status.SUCCESS, "Partner "
							+ requestedPartner.getPartnerName()
							+ " added successfully");
				}
			}
		}

		return true;
	}

	/**
	 * This method is used to save the workflow partner details to
	 * PartnerMasterT
	 * 
	 * @param requestedPartner
	 */
	private void savePartnerMaster(WorkflowPartnerT requestedPartner) {
		// TODO Auto-generated method stub
		PartnerMasterT partnerMaster = new PartnerMasterT();
		partnerMaster.setCreatedModifiedBy(requestedPartner.getCreatedBy());
		partnerMaster.setCorporateHqAddress(requestedPartner
				.getCorporateHqAddress());
		partnerMaster.setDocumentsAttached(requestedPartner
				.getDocumentsAttached());
		partnerMaster.setFacebook(requestedPartner.getFacebook());
		partnerMaster.setGeography(requestedPartner.getGeography());
		partnerMaster.setLogo(requestedPartner.getLogo());
		partnerMaster.setPartnerName(requestedPartner.getPartnerName());
		partnerMaster.setWebsite(requestedPartner.getWebsite());
		partnerRepository.save(partnerMaster);

	}

	private void validateRequestedPartner(WorkflowPartnerT reqPartner)
			throws Exception {

		// Validate Partner Name

		String partnerName = reqPartner.getPartnerName();
		if (StringUtils.isEmpty(partnerName)) {
			logger.error("Partner Name should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Partner name Should not be empty");

		} else {
			if (!StringUtils.isEmpty(partnerRepository
					.findPartnerName(partnerName))) {
				logger.error("Partner Name already exists");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Partner name " + partnerName + " already exists");
			}
		}

		// foreign key constraint for geography
		if (!StringUtils.isEmpty(reqPartner.getGeography())) {
			if (!mapOfGeographyMappingT.containsKey(reqPartner.getGeography())) {
				logger.error("Invalid Geography");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Geography" + reqPartner.getGeography());
			}
		} else {
			logger.error("Geography Should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Geography Should not be empty");
		}
	}

	/**
	 * To find if the user is authorized to access the request
	 * 
	 * @param workflowSteps
	 * @param authorizedUserFlag
	 * @param userId
	 * @return
	 * @throws DestinationException
	 */
	private boolean getAuthorizedUserFlag(List<WorkflowStepT> workflowSteps,
			String userId) throws DestinationException {
		boolean authorizedUserFlag = false;
		UserT user = userRepository.findByUserId(userId);
		String userRole = user.getUserRole();
		String userGroup = user.getUserGroup();
		for (WorkflowStepT workflowStep : workflowSteps) {
			if ((workflowStep.getUserId() != null)
					&& (workflowStep.getUserId().equals(userId))) {
				authorizedUserFlag = true;
			} else if (workflowStep.getStepStatus().equals(
					WorkflowStatus.PENDING.getStatus())) {
				if ((workflowStep.getUserGroup() != null)
						&& (workflowStep.getUserGroup().contains(userGroup))) {
					authorizedUserFlag = true;
				} else if ((workflowStep.getUserRole() != null)
						&& (workflowStep.getUserRole().contains(userRole))) {
					authorizedUserFlag = true;
				}
			}
		}
		if (authorizedUserFlag == false) {
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User not authorized to access this request");
		}
		return authorizedUserFlag;

	}

	/**
	 * This method is used to retrieve partner requests pending with user
	 * 
	 * @param userId
	 * @return
	 */
	private List<Object[]> getPendingPartnerRequests(String userId) {

		List<Object[]> resultList = null;
		UserT user = userRepository.findByUserId(userId);
		String userRole = user.getUserRole();
		String userGroup = user.getUserGroup();
		userRole = "%" + userRole + "%";
		userGroup = "%" + userGroup + "%";
		// Query to get pending partner requests for specific user's
		// approval/rejection
		StringBuffer queryBufferForPending = new StringBuffer(
				QueryConstants.PARTNER_PENDING_WITH_USER_QUERY);
		Query queryForPending = entityManager
				.createNativeQuery(queryBufferForPending.toString());
		queryForPending.setParameter("userId", userId);
		resultList = queryForPending.getResultList();
		// Query to get pending with group of users, based on user's role and
		// user group
		StringBuffer queryBuffer = new StringBuffer(
				QueryConstants.PARTNER_PENDING_WITH_GROUP_QUERY);
		Query query = entityManager.createNativeQuery(queryBuffer.toString());
		query.setParameter("userRole", userRole);
		query.setParameter("userGroup", userGroup);
		if (resultList == null) {
			resultList = query.getResultList();
		} else {
			List<Object[]> resultForGroupPending = query.getResultList();
			resultList.addAll(resultForGroupPending);
		}

		return resultList;
	}

	public boolean approvePartnerWorkflowEntity(
			WorkflowPartnerT workflowPartnerT) {

		int stepId = -1;
		int requestId = 0;
		int rowIteration = 0;
		int step = 0;
		String oldPartnerName = null;
		List<WorkflowStepT> requestSteps = new ArrayList<WorkflowStepT>();
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		WorkflowRequestT masterRequest = new WorkflowRequestT();
		mapOfGeographyMappingT = customerUploadService.getGeographyMappingT();
		UserT user = userRepository.findByUserId(userId);

		try {
			if (validateWorkflowPartnerMasterDetails(workflowPartnerT)) {

				requestSteps = workflowStepTRepository
						.findStepForEditAndApprove(Constants.CONSTANT_ONE,
								workflowPartnerT.getWorkflowPartnerId());
				masterRequest = workflowRequestTRepository.findRequestedRecord(
						Constants.CONSTANT_ONE,
						workflowPartnerT.getWorkflowPartnerId());
				for (WorkflowStepT stepRecord : requestSteps) {
					if (stepRecord.getStepStatus().equals(
							WorkflowStatus.PENDING.getStatus())) {
						stepId = stepRecord.getStepId();
						requestId = stepRecord.getRequestId();
						WorkflowPartnerT oldObject = new WorkflowPartnerT();
						if (stepId != -1 && requestId != 0 && rowIteration == 0) {
							oldObject = workflowPartnerRepository
									.findOne(workflowPartnerT
											.getWorkflowPartnerId());
							oldPartnerName = oldObject.getPartnerName();
							if (isPartnerModified(oldObject, workflowPartnerT)) {
								workflowPartnerRepository.save(oldObject);
							}
							if (user.getUserRole().equals(
									UserRole.SYSTEM_ADMIN.getValue())) {
								List<PartnerMasterT> oldPartnerMasterList = partnerRepository
										.findByPartnerName(oldPartnerName);
								if (oldPartnerMasterList.size() > 0) {
									for (PartnerMasterT oldPartnerMaster : oldPartnerMasterList) {
										saveToPartnerMasterTables(
												oldPartnerMaster,
												workflowPartnerT);
									}
								} else {
									PartnerMasterT newPartnerMaster = new PartnerMasterT();
									saveToPartnerMasterTables(newPartnerMaster,
											workflowPartnerT);
								}
							}
							stepRecord.setUserId(userId);
							stepRecord.setStepStatus(WorkflowStatus.APPROVED
									.getStatus());
							stepRecord.setModifiedBy(userId);
							if (!StringUtils.isEmpty(workflowPartnerT
									.getComments())) {
								stepRecord.setComments(workflowPartnerT
										.getComments());
							}
							// for updating the status in workflow_request_t
							masterRequest.setModifiedBy(userId);
							masterRequest.setStatus(WorkflowStatus.APPROVED
									.getStatus());
							sendEmailNotificationforApprovedOrRejectMail(
									workflowPartnerApprovedSubject,
									masterRequest.getRequestId(),
									masterRequest.getCreatedDatetime(),
									masterRequest.getEntityTypeId());
							step = stepRecord.getStep() + 1;
							rowIteration++;
						}
					}

					if (stepRecord.getStep().equals(step)
							&& (rowIteration == 1)) {
						stepRecord.setStepStatus(WorkflowStatus.PENDING
								.getStatus());
						// for updating the status in workflow_request_t
						masterRequest.setModifiedBy(userId);
						masterRequest.setStatus(WorkflowStatus.PENDING
								.getStatus());
						stepRecord.setModifiedBy(userId);
						sendEmailNotificationforPending(
								masterRequest.getRequestId(), new Date(),
								masterRequest.getEntityTypeId());
						rowIteration++;
					}
				}
				workflowStepTRepository.save(requestSteps);
				workflowRequestTRepository.save(masterRequest);
			}
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while approving the request");
		}
		return true;
	}

	private boolean isPartnerModified(WorkflowPartnerT oldObject,
			WorkflowPartnerT workflowPartnerT) {

		boolean isPartnerModifiedFlag = false;
		String corporateHqAdress = "";
		String website = "";
		String facebook = "";
		String notes = "";
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		if (!workflowPartnerT.getPartnerName().equals(
				oldObject.getPartnerName())) {
			oldObject.setPartnerName(workflowPartnerT.getPartnerName());
			isPartnerModifiedFlag = true;
		}
		// corpoarate address
		if (!StringUtils.isEmpty(oldObject.getCorporateHqAddress())) {
			corporateHqAdress = oldObject.getCorporateHqAddress();
		}
		if (!workflowPartnerT.getCorporateHqAddress().equals(corporateHqAdress)) {
			oldObject.setCorporateHqAddress(workflowPartnerT
					.getCorporateHqAddress());
			isPartnerModifiedFlag = true;
		}

		// facebook
		if (!StringUtils.isEmpty(oldObject.getFacebook())) {
			facebook = oldObject.getFacebook();
		}
		if (!workflowPartnerT.getFacebook().equals(facebook)) {
			oldObject.setFacebook(workflowPartnerT.getFacebook());
			isPartnerModifiedFlag = true;
		}
		// website
		if (!StringUtils.isEmpty(oldObject.getWebsite())) {
			website = oldObject.getWebsite();
		}
		if (!workflowPartnerT.getWebsite().equals(website)) {
			oldObject.setWebsite(workflowPartnerT.getWebsite());
			isPartnerModifiedFlag = true;
		}
		// geography
		if (!workflowPartnerT.getGeography().equals(oldObject.getGeography())) {
			oldObject.setGeography(workflowPartnerT.getGeography());
			isPartnerModifiedFlag = true;
		}
		// notes for edit
		if (!StringUtils.isEmpty(oldObject.getNotes())) {
			notes = oldObject.getNotes();
		}
		if (!workflowPartnerT.getNotes().equals(notes)
				&& (!StringUtils.isEmpty(workflowPartnerT.getNotes()))) {
			oldObject.setNotes(workflowPartnerT.getNotes());
			isPartnerModifiedFlag = true;
		}
		oldObject.setModifiedBy(userId);
		return isPartnerModifiedFlag;
	}

	/**
	 * after admin approval the entity was saved into the partner masterT table
	 * 
	 * @param workflowPartnerT
	 */
	private void saveToPartnerMasterTables(PartnerMasterT oldPartnerMaster,
			WorkflowPartnerT workflowPartnerT) {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		oldPartnerMaster.setPartnerName(workflowPartnerT.getPartnerName());
		if (!StringUtils.isEmpty(workflowPartnerT.getCorporateHqAddress())) {
			oldPartnerMaster.setCorporateHqAddress(workflowPartnerT
					.getCorporateHqAddress());
		}

		// check for "" in db
		if (!StringUtils.isEmpty(workflowPartnerT.getWebsite())) {
			oldPartnerMaster.setWebsite(workflowPartnerT.getWebsite());
		}
		if (!StringUtils.isEmpty(workflowPartnerT.getFacebook())) {
			oldPartnerMaster.setFacebook(workflowPartnerT.getFacebook());
		}
		oldPartnerMaster.setGeography(workflowPartnerT.getGeography());
		oldPartnerMaster.setLogo(workflowPartnerT.getLogo());
		oldPartnerMaster.setDocumentsAttached(workflowPartnerT
				.getDocumentsAttached());
		oldPartnerMaster.setCreatedModifiedBy(userId);
		partnerRepository.save(oldPartnerMaster);
	}

	private boolean validateWorkflowPartnerMasterDetails(
			WorkflowPartnerT requestedPartner) {
		boolean validated = true;
		String partnerName = requestedPartner.getPartnerName();
		// Partner name should not be empty
		if (StringUtils.isEmpty(partnerName)) {
			logger.error("Partner Name should not be empty");
			validated = false;
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Partner Name should not be empty");
		}
		// to check duplicate of Partner name
		List<PartnerMasterT> partnerMaster = partnerRepository
				.findByPartnerName(requestedPartner.getPartnerName());
		if ((partnerMaster.size() != 0)) {
			logger.error("Partner name already exists");
			validated = false;
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Partner name already exists" + partnerName);
		}
		// foreign key constraint for geography
		if (!StringUtils.isEmpty(requestedPartner.getGeography())) {
			if (!mapOfGeographyMappingT.containsKey(requestedPartner
					.getGeography())) {
				logger.error("Invalid Geography");
				validated = false;
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Geography" + requestedPartner.getGeography());
			}
		} else {
			logger.error("Geography Should not be empty");
			validated = false;
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Geography Should not be empty");
		}
		// not null check for documents_attached
		if (StringUtils.isEmpty(requestedPartner.getDocumentsAttached())) {
			logger.error("documents should not be empty");
			validated = false;
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Documents should not be empty");
		}
		return validated;
	}

	/*
	 * on admin approval new entity was created in the master table
	 */
	private void saveToCustomerMasterTables(WorkflowCustomerT workflowCustomerT) {
		logger.info("Inside saveToCustomerMasterTables");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		CustomerMasterT customerMaster = new CustomerMasterT();
		CustomerMasterT customerMastersaved = new CustomerMasterT();

		customerMaster.setCustomerName(workflowCustomerT.getCustomerName());
		customerMaster.setGroupCustomerName(workflowCustomerT
				.getGroupCustomerName());
		customerMaster.setCorporateHqAddress(workflowCustomerT
				.getCorporateHqAddress());
		customerMaster.setWebsite(workflowCustomerT.getWebsite());
		customerMaster.setFacebook(workflowCustomerT.getFacebook());
		customerMaster.setIou(workflowCustomerT.getIou());
		customerMaster.setGeography(workflowCustomerT.getGeography());
		customerMaster.setLogo(workflowCustomerT.getLogo());
		customerMaster.setDocumentsAttached(workflowCustomerT
				.getDocumentsAttached());
		customerMaster.setCreatedModifiedBy(userId);
		customerMastersaved = customerRepository.save(customerMaster);
		logger.info("Customer saved" + customerMaster.getCustomerId());
		if (!workflowCustomerT.getRevenueCustomerMappingTs().isEmpty()) {
			for (RevenueCustomerMappingT rcmpt : workflowCustomerT
					.getRevenueCustomerMappingTs()) {
				RevenueCustomerMappingT revenueCustomer = new RevenueCustomerMappingT();
				// RevenueCustomerMappingTPK revenueTPK = new
				// RevenueCustomerMappingTPK();
				revenueCustomer.setFinanceCustomerName(rcmpt
						.getFinanceCustomerName());
				revenueCustomer.setFinanceIou(rcmpt.getFinanceIou());
				revenueCustomer.setCustomerGeography(rcmpt
						.getCustomerGeography());
				revenueCustomer.setCustomerId(customerMastersaved
						.getCustomerId());
				revenueRepository.save(revenueCustomer);
			}
		}
		if (!workflowCustomerT.getBeaconCustomerMappingTs().isEmpty()) {
			for (BeaconCustomerMappingT bcmpt : workflowCustomerT
					.getBeaconCustomerMappingTs()) {
				BeaconCustomerMappingT beaconCustomer = new BeaconCustomerMappingT();
				beaconCustomer.setBeaconCustomerName(bcmpt
						.getBeaconCustomerName());
				beaconCustomer.setBeaconIou(bcmpt.getBeaconIou());
				beaconCustomer.setCustomerGeography(bcmpt
						.getCustomerGeography());
				beaconCustomer.setCustomerId(customerMastersaved
						.getCustomerId());
				beaconRepository.save(beaconCustomer);
			}
		}
	}

	/**
	 * This method is used to request to reopen a opportunity which got shelved
	 * 
	 * @param opportunityReopenRequestT
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean requestOpportunityReopen(
			OpportunityReopenRequestT opportunityReopenRequestT, Status status)
			throws Exception {
		// TODO Auto-generated method stub
		logger.info("Inside requestOpportunityReopen method");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		String opportunityId = opportunityReopenRequestT.getOpportunityId();
		Integer entityTypeId = EntityTypeId.OPPORTUNITY.getType();
		OpportunityT opportunity = opportunityRepository.findOne(opportunityId);
		logger.info("Opportunity found");
		if (opportunity != null) {
			if (opportunityReopenRequestT.getReasonForReopen() != null) {
				if (validateOpportunityRequest(opportunity)) {
					if (workflowRequestRepository
							.findByEntityTypeIdAndEntityIdAndStatus(
									entityTypeId, opportunityId,
									WorkflowStatus.PENDING.getStatus()) != null) {
						logger.error("Reopen request already exists for this opportunity.");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Reopen request already exists for this opportunity.");
					} else {
						WorkflowRequestT workflowRequest = populateWorkflowRequest(
								opportunityId, entityTypeId, userId,
								opportunityReopenRequestT.getReasonForReopen());
						if (workflowRequest != null) {
							if (workflowRequest.getStatus().equals(
									WorkflowStatus.PENDING.getStatus())) {
								sendEmailNotificationforPending(
										workflowRequest.getRequestId(),
										new Date(), entityTypeId);
								status.setStatus(
										Status.SUCCESS,
										"Your request to reopen the Opportunity "
												+ opportunity
														.getOpportunityName()
												+ " is submitted");
							} else {
								int i = opportunityRepository
										.reopenOpportunity(opportunityId);
								if (i > 0) {
									status.setStatus(
											Status.SUCCESS,
											"Opportunity "
													+ opportunity
															.getOpportunityName()
													+ "has been reopened");
									logger.info("Opportunity reopened :"
											+ opportunityId);
								}
							}
						}
					}
				} else {
					throw new DestinationException(
							HttpStatus.FORBIDDEN,
							"You are not authorised to Request for reopen. Only Opportunity Owner or Sales Support Owner are allowed to request for update");
				}
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Reason for reopen the opportunity is mandatory");
			}

		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Opportunity not found");
		}
		return true;
	}

	/**
	 * This method validates the given reopen request
	 * 
	 * @param opportunity
	 * @return
	 */
	private boolean validateOpportunityRequest(OpportunityT opportunity) {
		logger.info("Inside validateOpportunityRequest method");
		boolean isValid = false;
		if (opportunity.getSalesStageCode() != 12) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Cannot reopen a request which is on "
							+ opportunity.getSalesStageMappingT()
									.getSalesStageDescription());
		}

		if (opportunity.getOpportunityOwner().equals(
				DestinationUtils.getCurrentUserDetails().getUserId()))
			isValid = true;
		if (opportunity.getOpportunitySalesSupportLinkTs() != null) {
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getOpportunitySalesSupportLinkTs()) {
				if (opportunitySalesSupportLinkT.getSalesSupportOwner().equals(
						DestinationUtils.getCurrentUserDetails().getUserId()))
					isValid = true;
			}
		}
		return isValid;
	}

	/**
	 * This method is used to approve or reject the opportunity reopen request
	 * 
	 * @param opportunityReopenRequestT
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean approveOrRejectOpportunityReopen(
			OpportunityReopenRequestT opportunityReopenRequestT, Status status)
			throws Exception {
		// TODO Auto-generated method stub
		logger.info("Inside approveOpportunityReopen method");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		UserT user = userRepository.findByUserId(userId);
		String userRole = user.getUserRole();
		String userGroup = user.getUserGroup();
		String opportunityId = opportunityReopenRequestT.getOpportunityId();
		Integer entityTypeId = EntityTypeId.OPPORTUNITY.getType();
		List<WorkflowStepT> workflowStep = new ArrayList<WorkflowStepT>();
		OpportunityT opportunity = opportunityRepository.findOne(opportunityId);
		if (opportunity != null) {
			if (opportunityReopenRequestT.getApprovedRejectedComments() != null) {
				WorkflowRequestT workflowRequest = workflowRequestRepository
						.findByEntityTypeIdAndEntityId(entityTypeId,
								opportunityId);
				if (workflowRequest != null) {
					if (workflowRequest.getStatus().equals(
							WorkflowStatus.PENDING.getStatus())) {
						WorkflowStepT workflowStepPending = workflowStepRepository
								.findByRequestIdAndStepStatus(
										workflowRequest.getRequestId(),
										WorkflowStatus.PENDING.getStatus());

						if (checkUserAccess(workflowStepPending, userGroup,
								userRole, userId)) {
							if (!opportunityReopenRequestT.isRejectFlag()) {
								WorkflowStepT workflowNextStep = workflowStepRepository
										.findByRequestIdAndStep(
												workflowRequest.getRequestId(),
												workflowStepPending.getStep() + 1);
								if (workflowNextStep != null) {
									// If the user is a intermediate approver
									workflowStepPending
											.setStepStatus(WorkflowStatus.APPROVED
													.getStatus());

									workflowStepPending.setUserId(userId);
									workflowStepPending
											.setComments(opportunityReopenRequestT
													.getApprovedRejectedComments());
									workflowStepPending.setModifiedBy(userId);
									workflowStep.add(workflowStepPending);
									// Changing the next step status to pending
									workflowNextStep
											.setStepStatus(WorkflowStatus.PENDING
													.getStatus());
									workflowNextStep.setModifiedBy(userId);
									workflowStep.add(workflowNextStep);
									workflowStepRepository.save(workflowStep);
									workflowRequest.setModifiedBy(userId);
									workflowRequestRepository
											.save(workflowRequest);
									logger.info("Request approved "
											+ workflowRequest.getRequestId());
									sendEmailNotificationforPending(
											workflowRequest.getRequestId(),
											workflowRequest
													.getCreatedDatetime(),
											entityTypeId);

								} else {
									// if the user is a final approver
									workflowStepPending
											.setStepStatus(WorkflowStatus.APPROVED
													.getStatus());
									workflowStepPending.setUserId(userId);
									workflowStepPending.setModifiedBy(userId);
									workflowStepPending
											.setComments(opportunityReopenRequestT
													.getApprovedRejectedComments());
									// reopen the opportunity and setting the
									// status
									// for
									// request and step as approved
									opportunityRepository
											.reopenOpportunity(opportunityId);

									workflowStepRepository
											.save(workflowStepPending);
									workflowRequest
											.setStatus(WorkflowStatus.APPROVED
													.getStatus());
									workflowRequest.setModifiedBy(userId);
									workflowRequestRepository
											.save(workflowRequest);
									logger.info("Request approved and Opportunity Reopened "
											+ workflowRequest.getRequestId());
									sendEmailNotificationforApprovedOrRejectMail(
											workflowOpportunityReopenApprovedSubject,
											workflowRequest.getRequestId(),
											new Date(), entityTypeId);

								}
							} else {
								workflowStepPending
										.setStepStatus(WorkflowStatus.REJECTED
												.getStatus());
								workflowStepPending.setUserId(userId);
								workflowStepPending.setModifiedBy(userId);
								workflowStepPending
										.setComments(opportunityReopenRequestT
												.getApprovedRejectedComments());
								workflowStepRepository
										.save(workflowStepPending);
								workflowRequest
										.setStatus(WorkflowStatus.REJECTED
												.getStatus());
								workflowRequest.setModifiedBy(userId);
								workflowRequestRepository.save(workflowRequest);
								logger.info("Opportunity reopen rejected : request Id"
										+ workflowRequest.getRequestId());
								sendEmailNotificationforApprovedOrRejectMail(
										workflowOpportunityReopenRejectedSubject,
										workflowRequest.getRequestId(),
										new Date(), entityTypeId);
							}

						} else {
							throw new DestinationException(
									HttpStatus.FORBIDDEN,
									"You are not authorised to access this service");
						}

					} else {
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"The request is being already approved or rejected");
					}

				} else {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"No request is exists to reopen the opportunity");
				}
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Comments should not be empty");
			}
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Opportunity not found");
		}
		status.setStatus(Status.SUCCESS,
				"The opportunity reopen request has been approved");
		return true;
	}

	/**
	 * This method is used to check whether the user is having the access to
	 * aspprove or reject a request
	 * 
	 * @param workflowStep
	 * @param userGroup
	 * @param userRole
	 * @param userId
	 * @return
	 */
	private boolean checkUserAccess(WorkflowStepT workflowStep,
			String userGroup, String userRole, String userId) {
		// TODO Auto-generated method stub
		logger.info("Inside checkUserAccess method");
		boolean flag = false;
		if (workflowStep.getUserGroup() != null) {
			if (workflowStep.getUserGroup().contains(userGroup)
					|| (isUserPMO(userId) && workflowStep.getUserGroup()
							.contains("PMO"))) {
				flag = true;
			}
		}
		if (workflowStep.getUserRole() != null) {
			if (workflowStep.getUserRole().contains(userRole)) {
				flag = true;
			}
		}
		if (workflowStep.getUserId() != null) {
			if (workflowStep.getUserId().contains(userId)) {
				flag = true;
			}
		}
		return flag;
	}

}