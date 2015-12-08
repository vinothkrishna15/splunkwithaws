package com.tcs.destination.tasklet;

import static com.tcs.destination.enums.JobStep.CONNECT_DWLD_PROCESSING;
import static com.tcs.destination.enums.JobStep.END;
import static com.tcs.destination.enums.RequestStatus.SUBMITTED;
import static com.tcs.destination.enums.RequestType.CONNECT_DOWNLOAD;
import static com.tcs.destination.utils.Constants.NEXT_STEP;
import static com.tcs.destination.utils.Constants.REQUEST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.ConnectOfferingLinkRepository;
import com.tcs.destination.data.repository.ConnectSecondaryOwnerRepository;
import com.tcs.destination.data.repository.ConnectSubSpLinkRepository;
import com.tcs.destination.data.repository.ConnectTcsAccountContactLinkTRepository;
import com.tcs.destination.data.repository.ContactCustomerLinkTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.NotesTRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;



@Component("connectDwldPreprocessor")
public class ConnectDwldPreprocessor implements Tasklet{
	
	private static final Logger logger = LoggerFactory
			.getLogger(ConnectDwldPreprocessor.class);
	
	private List<DataProcessingRequestT> requestList = null;
	
	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	@Autowired
	private CustomerRepository custRepo;
	
	@Autowired
	private PartnerRepository partnerRepo;
	
	@Autowired
	private ConnectSubSpLinkRepository connsubspLinkRepo;
	
	@Autowired
	private ConnectOfferingLinkRepository connOffRepo;
	
	@Autowired
	private ConnectSecondaryOwnerRepository connSecOwnerRepo;
	
	@Autowired
	private ConnectTcsAccountContactLinkTRepository connectTcsAccContactRepo;
	
	@Autowired
	private ConnectCustomerContactLinkTRepository connectCustomerContactRepo;
	
	@Autowired
	private NotesTRepository notesRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactRepository contactRepo;
	
	@Autowired
	private ContactCustomerLinkTRepository contactCustomerLinkTRepo;
	
	// data needed for connect master sheet
	private Map<String,String> customerIdCustomerNameMap;
	private Map<String,String> partnerIdPartnerMap;
	private Map<String,List<ConnectSubSpLinkT>> connectSubSpMap;
	private Map<String,List<ConnectOfferingLinkT>> connectOfferingMap;
	private Map<String,List<ConnectSecondaryOwnerLinkT>> connectSecondaryOwnerMap;
	private Map<String,List<ConnectTcsAccountContactLinkT>> connectTcsAccountContactMap;
	private Map<String,List<ConnectCustomerContactLinkT>> connectCustomerContactMap;
	private Map<String,List<NotesT>> connectNotesMap;
	private Map<String,UserT> userIdUserMap;
	private Map<String,ContactT> contactIdContactMap;
	private Map<String,String> contactIdCustomerIdMap;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		logger.debug("Inside execute method:");
		
		if (requestList == null) {
			requestList = dataProcessingRequestRepository.findByRequestTypeAndStatus(CONNECT_DOWNLOAD.getType(), SUBMITTED.getStatus());
		}
		

		ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		
		if (CollectionUtils.isNotEmpty(requestList)) {
			
			DataProcessingRequestT request = requestList.remove(0);
		   
		    jobContext.put(REQUEST,request);
		    jobContext.put(NEXT_STEP, CONNECT_DWLD_PROCESSING);
		    
		    populateCustomerNameMap();
			jobContext.put("customerIdCustomerMap", customerIdCustomerNameMap);
			
			populatePartnerNameMap();
			jobContext.put("partnerIdPartnerMap", partnerIdPartnerMap);
			
			populateConnectSubSpMap();
			jobContext.put("connectSubSpMap",connectSubSpMap);
			
			populateConnectOfferingMap();
			jobContext.put("connectOfferingMap",connectOfferingMap);
			
			populateConnectSecondaryOwnerMap();
			jobContext.put("connectSecondaryOwnerMap",connectSecondaryOwnerMap);
			
			populateConnectTcsAccountContactMap();
			jobContext.put("connectTcsAccountContactMap",connectTcsAccountContactMap);
			
			populateConnectCustomerContactMap();
			jobContext.put("connectCustomerContactMap",connectCustomerContactMap);
			
			populateConnectNotesMap();
			jobContext.put("connectNotesMap",connectNotesMap);
			
			populateUserIdUserMap();
			jobContext.put("userIdUserMap",userIdUserMap);
			
			populateContactIdContactMap();
			jobContext.put("contactIdContactMap",contactIdContactMap);
			
			populateContactIdCustomerIdMap();
			jobContext.put("contactIdCustomerIdMap",contactIdCustomerIdMap);
			
		} else {
			 jobContext.put(NEXT_STEP, END);
			 requestList = null;
		}
		
		return RepeatStatus.FINISHED;
	}

	private void populateContactIdCustomerIdMap() {
		logger.debug("Inside populateContactIdCustomerIdMap method:");
		contactIdCustomerIdMap = new HashMap<String,String>();
		List<ContactCustomerLinkT> customerContactList = (List<ContactCustomerLinkT>) contactCustomerLinkTRepo.findAll();
		if(customerContactList!=null){
			for(ContactCustomerLinkT contactCustomerLinkT:customerContactList){
				contactIdCustomerIdMap.put(contactCustomerLinkT.getContactId(), contactCustomerLinkT.getCustomerId());
			}
		}
		logger.debug("populated contactIdCustomerIdMap : " + contactIdCustomerIdMap.size());
	}

	private void populateContactIdContactMap() {
		logger.debug("Inside populateContactIdContactMap method:");
		List<ContactT> contactList = (List<ContactT>) contactRepo.findAll();
		contactIdContactMap = new HashMap<String,ContactT>();
		if(contactList!=null && !contactList.isEmpty()){
			for(ContactT contact: contactList){
				contactIdContactMap.put(contact.getContactId(),contact);
			}
		}
		logger.debug("populated contactIdContactMap : " + contactIdContactMap.size());
	}

	private void populateUserIdUserMap() {
		logger.debug("Inside populateUserIdUserMap method:");
		List<UserT> userList = (List<UserT>) userRepo.findAll();
		userIdUserMap = new HashMap<String,UserT>();
		if(userList!=null && !userList.isEmpty()){
			for(UserT user: userList){
				userIdUserMap.put(user.getUserId(),user);
			}
		}
		logger.debug("populated userIdUserMap : " + userIdUserMap.size());
	}

	private void populateConnectNotesMap() {
		logger.debug("Inside populateConnectNotesMap method:");
		List<NotesT> notesForConnectsList = (List<NotesT>) notesRepo.findByEntityTypeAndConnectIdIsNotNull(EntityType.CONNECT.toString());
		connectNotesMap = new HashMap<String,List<NotesT>>();
		
		if(notesForConnectsList!=null && !notesForConnectsList.isEmpty()){
			for(NotesT notesT : notesForConnectsList){
				String connectId = notesT.getConnectId();
				if(!connectNotesMap.isEmpty()){
					List<NotesT> connectIdNotesList = connectNotesMap.get(connectId);
					if(connectIdNotesList==null){
						List<NotesT> connectNotesList = new ArrayList<NotesT>();
						connectNotesList.add(notesT);
						connectNotesMap.put(connectId, connectNotesList);
					} else {
						connectIdNotesList.add(notesT);
					}
				} else {
					List<NotesT> connectNotesList = new ArrayList<NotesT>();
					connectNotesList.add(notesT);
					connectNotesMap.put(connectId, connectNotesList);
				}
			}
		}
		logger.debug("populated connectNotesMap : " + connectNotesMap.size());
	}

	private void populateConnectCustomerContactMap() {
		logger.debug("Inside populateConnectCustomerContactMap method:");
		List<ConnectCustomerContactLinkT> connectCustomerContactLinkTList = (List<ConnectCustomerContactLinkT>) connectCustomerContactRepo.findAll();
		connectCustomerContactMap = new HashMap<String,List<ConnectCustomerContactLinkT>>();
		
		if(connectCustomerContactLinkTList!=null && !connectCustomerContactLinkTList.isEmpty()){
			for(ConnectCustomerContactLinkT connectCustomerContactLinkT : connectCustomerContactLinkTList){
				String connectId = connectCustomerContactLinkT.getConnectId();
				if(!connectCustomerContactMap.isEmpty()){
					List<ConnectCustomerContactLinkT> ConnectCustomerContactLinkList = connectCustomerContactMap.get(connectId);
					if(ConnectCustomerContactLinkList==null){
						List<ConnectCustomerContactLinkT> connectCustomerContactList = new ArrayList<ConnectCustomerContactLinkT>();
						connectCustomerContactList.add(connectCustomerContactLinkT);
						connectCustomerContactMap.put(connectId, connectCustomerContactList);
					} else {
						ConnectCustomerContactLinkList.add(connectCustomerContactLinkT);
					}
				} else {
					List<ConnectCustomerContactLinkT> connectCustomerContactList = new ArrayList<ConnectCustomerContactLinkT>();
					connectCustomerContactList.add(connectCustomerContactLinkT);
					connectCustomerContactMap.put(connectId, connectCustomerContactList);
				}
			}
		}
		logger.debug("populated connectCustomerContactMap : " + connectCustomerContactMap.size());
	}

	private void populateConnectTcsAccountContactMap() {
		logger.debug("Inside populateConnectTcsAccountContactMap method:");
		List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTList = (List<ConnectTcsAccountContactLinkT>) connectTcsAccContactRepo.findAll();
		connectTcsAccountContactMap = new HashMap<String,List<ConnectTcsAccountContactLinkT>>();
		
		if(connectTcsAccountContactLinkTList!=null && !connectTcsAccountContactLinkTList.isEmpty()){
			for(ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT : connectTcsAccountContactLinkTList){
				String connectId = connectTcsAccountContactLinkT.getConnectId();
				if(!connectTcsAccountContactMap.isEmpty()){
					List<ConnectTcsAccountContactLinkT> ConnectTcsAccountContactLinkList = connectTcsAccountContactMap.get(connectId);
					if(ConnectTcsAccountContactLinkList==null){
						List<ConnectTcsAccountContactLinkT> connectTcsAccountContactList = new ArrayList<ConnectTcsAccountContactLinkT>();
						connectTcsAccountContactList.add(connectTcsAccountContactLinkT);
						connectTcsAccountContactMap.put(connectId, connectTcsAccountContactList);
					} else {
						ConnectTcsAccountContactLinkList.add(connectTcsAccountContactLinkT);
					}
				} else {
					List<ConnectTcsAccountContactLinkT> connectTcsAccountContactList = new ArrayList<ConnectTcsAccountContactLinkT>();
					connectTcsAccountContactList.add(connectTcsAccountContactLinkT);
					connectTcsAccountContactMap.put(connectId, connectTcsAccountContactList);
				}
			}
		}
		logger.debug("populated connectTcsAccountContactMap : " + connectTcsAccountContactMap.size());
	}

	private void populateConnectSecondaryOwnerMap() {
		logger.debug("Inside populateConnectSecondaryOwnerMap method:");
		List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTList = (List<ConnectSecondaryOwnerLinkT>) connSecOwnerRepo.findAll();
		connectSecondaryOwnerMap = new HashMap<String,List<ConnectSecondaryOwnerLinkT>>();
		
		if(connectSecondaryOwnerLinkTList!=null && !connectSecondaryOwnerLinkTList.isEmpty()){
			for(ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT : connectSecondaryOwnerLinkTList){
				String connectId = connectSecondaryOwnerLinkT.getConnectId();
				if(!connectSecondaryOwnerMap.isEmpty()){
					List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkList = connectSecondaryOwnerMap.get(connectId);
					if(connectSecondaryOwnerLinkList==null){
						List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerList = new ArrayList<ConnectSecondaryOwnerLinkT>();
						connectSecondaryOwnerList.add(connectSecondaryOwnerLinkT);
						connectSecondaryOwnerMap.put(connectId, connectSecondaryOwnerList);
					} else {
						connectSecondaryOwnerLinkList.add(connectSecondaryOwnerLinkT);
					}
				} else {
					List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerList = new ArrayList<ConnectSecondaryOwnerLinkT>();
					connectSecondaryOwnerList.add(connectSecondaryOwnerLinkT);
					connectSecondaryOwnerMap.put(connectId, connectSecondaryOwnerList);
				}
			}
		}
		logger.debug("populated connectSecondaryOwnerMap : " + connectSecondaryOwnerMap.size());
	}

	private void populateConnectOfferingMap() {
		logger.debug("Inside populateConnectOfferingMap method:");
		List<ConnectOfferingLinkT> connectOfferingLinkList = (List<ConnectOfferingLinkT>) connOffRepo.findAll();
		connectOfferingMap = new HashMap<String,List<ConnectOfferingLinkT>>();
		if(connectOfferingLinkList!=null && !connectOfferingLinkList.isEmpty()){
			for(ConnectOfferingLinkT connectOfferingLinkT : connectOfferingLinkList){
				String connectId = connectOfferingLinkT.getConnectId();
				if(!connectOfferingMap.isEmpty()){
					List<ConnectOfferingLinkT> connectIdOfferingList = connectOfferingMap.get(connectId);
					if(connectIdOfferingList==null){
						List<ConnectOfferingLinkT> connectIdOfferList = new ArrayList<ConnectOfferingLinkT>();
						connectIdOfferList.add(connectOfferingLinkT);
						connectOfferingMap.put(connectId, connectIdOfferList);
					} else {
						connectIdOfferingList.add(connectOfferingLinkT);
					}
				} else {
					List<ConnectOfferingLinkT> connectIdOfferList = new ArrayList<ConnectOfferingLinkT>();
					connectIdOfferList.add(connectOfferingLinkT);
					connectOfferingMap.put(connectId, connectIdOfferList);
				}
			}
		}
		logger.debug("populated connectOfferingMap : " + connectOfferingMap.size());
	}

	private void populateConnectSubSpMap() {
		logger.debug("Inside populateConnectSubSpMap method:");
		List<ConnectSubSpLinkT> connectSubspLinkList = (List<ConnectSubSpLinkT>) connsubspLinkRepo.findAll();
		connectSubSpMap = new HashMap<String,List<ConnectSubSpLinkT>>();
		if(connectSubspLinkList!=null && !connectSubspLinkList.isEmpty()){
			for(ConnectSubSpLinkT connectSubSpLinkT : connectSubspLinkList){
				String connectId = connectSubSpLinkT.getConnectId();
				if(!connectSubSpMap.isEmpty()){
					List<ConnectSubSpLinkT> connectIdSubspList = connectSubSpMap.get(connectId);
					if(connectIdSubspList==null){
						List<ConnectSubSpLinkT> connectIdSubList = new ArrayList<ConnectSubSpLinkT>();
						connectIdSubList.add(connectSubSpLinkT);
						connectSubSpMap.put(connectId, connectIdSubList);
					} else {
						connectIdSubspList.add(connectSubSpLinkT);
					}
				} else {
					List<ConnectSubSpLinkT> connectIdSubList = new ArrayList<ConnectSubSpLinkT>();
					connectIdSubList.add(connectSubSpLinkT);
					connectSubSpMap.put(connectId, connectIdSubList);
				}
			}
		}
		logger.debug("populated connectSubSpMap : " + connectSubSpMap.size());
	}

	private void populatePartnerNameMap() {
		logger.debug("Inside populatePartnerNameMap method:");
		List<Object[]> partnerList= (List<Object[]>) partnerRepo.findPartnerIdName();
		partnerIdPartnerMap = new HashMap<String,String>();
		if(partnerList!=null && !partnerList.isEmpty()){
			for(Object[] partnerArr : partnerList){
				partnerIdPartnerMap.put((String)partnerArr[0],(String)partnerArr[1]);
			}
		}
		logger.debug("populated partnerIdPartnerMap : " + partnerIdPartnerMap.size());
	}

	private void populateCustomerNameMap() {
		logger.debug("Inside populateCustomerNameMap method:");
		List<Object[]> customerList= (List<Object[]>) custRepo.findAllCustomerIdName();
		customerIdCustomerNameMap = new HashMap<String,String>();
		if(customerList!=null && !customerList.isEmpty()){
			for(Object[] customerArr : customerList){
				customerIdCustomerNameMap.put((String)customerArr[0],(String)customerArr[1]);
			}
		}
		logger.debug("populated customerIdCustomerNameMap : " + customerIdCustomerNameMap.size());
	}
	
}
