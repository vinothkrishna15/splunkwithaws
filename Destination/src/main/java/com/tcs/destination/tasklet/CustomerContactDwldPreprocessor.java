package com.tcs.destination.tasklet;

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

import static com.tcs.destination.enums.JobStep.CUSTOMER_CONTACT_DWLD_PROCESSING;
import static com.tcs.destination.enums.JobStep.END;
import static com.tcs.destination.enums.RequestType.CUSTOMER_CONTACT_DOWNLOAD;
import static com.tcs.destination.enums.RequestStatus.SUBMITTED;
import static com.tcs.destination.utils.Constants.NEXT_STEP;
import static com.tcs.destination.utils.Constants.REQUEST;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.PartnerContactLinkT;
import com.tcs.destination.data.repository.ContactCustomerLinkTRepository;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;

@Component("customerContactDwldPreprocessor")
public class CustomerContactDwldPreprocessor implements Tasklet {
	private static final Logger logger = LoggerFactory
			.getLogger(CustomerContactDwldPreprocessor.class);

	private List<DataProcessingRequestT> requestList = null;
	
	@Autowired
	private ContactCustomerLinkTRepository contactCustomerLinkTRepository;

	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	private Map<String, CustomerMasterT> contactCustomerMap;
	
	// data needed for customer contact sheet
		private Map<String,List<ContactCustomerLinkT>> customerMap;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		logger.debug("Inside customer contact download preprocessor");
		
		if (requestList == null) {
			
			requestList = dataProcessingRequestRepository
					.findByRequestTypeAndStatus(
							CUSTOMER_CONTACT_DOWNLOAD.getType(),
							SUBMITTED.getStatus());
		}
			ExecutionContext jobContext = chunkContext.getStepContext()
					.getStepExecution().getJobExecution().getExecutionContext();
			
			if (CollectionUtils.isNotEmpty(requestList)) {

				getContactCustomerLinkT();
				jobContext.put("mapOfContactCustomerLinkT",contactCustomerMap );
				
				DataProcessingRequestT request = requestList.remove(0);

				jobContext.put(REQUEST, request);
				jobContext.put(NEXT_STEP, CUSTOMER_CONTACT_DWLD_PROCESSING);
				
				populateCustomerMap();
				jobContext.put("customerMap", customerMap);

			} else {
				jobContext.put(NEXT_STEP, END);
				requestList = null;
			}			
		
		return RepeatStatus.FINISHED;
	}
	
	private Map<String, CustomerMasterT> getContactCustomerLinkT() {
		List<ContactCustomerLinkT> listOfContactCustomerLinkT = null;
		listOfContactCustomerLinkT = (List<ContactCustomerLinkT>) contactCustomerLinkTRepository.findAll();
		contactCustomerMap = new HashMap<String, CustomerMasterT>();
		for (ContactCustomerLinkT contactCustomerMappingT : listOfContactCustomerLinkT) {
			contactCustomerMap.put(contactCustomerMappingT.getContactId(), contactCustomerMappingT.getCustomerMasterT());
		}
		return contactCustomerMap;
	}
	
	private void populateCustomerMap() {
		List<ContactCustomerLinkT> contactCustomerLinkList = (List<ContactCustomerLinkT>) contactCustomerLinkTRepository.findAll();
		customerMap =new HashMap<String,List<ContactCustomerLinkT>>();
		if(contactCustomerLinkList!=null && !contactCustomerLinkList.isEmpty()){
			for(ContactCustomerLinkT contactCustomerLinkT : contactCustomerLinkList){
				String contactId = contactCustomerLinkT.getContactId();
				if(!customerMap.isEmpty()){
					List<ContactCustomerLinkT> contactCustomerList = customerMap.get(contactId);
					if(contactCustomerList==null){
						List<ContactCustomerLinkT> contactCusList = new ArrayList<ContactCustomerLinkT>();
						contactCusList.add(contactCustomerLinkT);
						customerMap.put(contactId, contactCusList);
					} else {
						contactCustomerList.add(contactCustomerLinkT);
					}
				} else {
					List<ContactCustomerLinkT> contactCusList = new ArrayList<ContactCustomerLinkT>();
					contactCusList.add(contactCustomerLinkT);
					customerMap.put(contactId, contactCusList);
				}
			}
		}
		logger.debug("Populated Customer Map : " + customerMap.size());
	}

}
