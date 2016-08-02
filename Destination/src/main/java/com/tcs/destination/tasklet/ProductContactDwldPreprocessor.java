package com.tcs.destination.tasklet;

import static com.tcs.destination.enums.JobStep.PRODUCT_CONTACT_DWLD_PROCESSING;
import static com.tcs.destination.enums.JobStep.END;
import static com.tcs.destination.enums.RequestStatus.SUBMITTED;
import static com.tcs.destination.enums.RequestType.PRODUCT_CONTACT_DOWNLOAD;
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

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.PartnerContactLinkT;
import com.tcs.destination.bean.ProductContactLinkT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.PartnerContactLinkTRepository;
import com.tcs.destination.data.repository.ProductContactLinkTRepository;



@Component("productContactDwldPreprocessor")
public class ProductContactDwldPreprocessor implements Tasklet{
	
	private static final Logger logger = LoggerFactory
			.getLogger(ProductContactDwldPreprocessor.class);
	
	private List<DataProcessingRequestT> requestList = null;
	
	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	// data needed for partner contact sheet
		private Map<String,List<ProductContactLinkT>> productMap;
		
		@Autowired
		ProductContactLinkTRepository productContactLinkTRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		logger.debug("Inside execute method:");
		
		if (requestList == null) {
			requestList = dataProcessingRequestRepository.findByRequestTypeAndStatus(PRODUCT_CONTACT_DOWNLOAD.getType(), SUBMITTED.getStatus());
		}
		
		ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		
		if (CollectionUtils.isNotEmpty(requestList)) {
			
			DataProcessingRequestT request = requestList.remove(0);
			   
		    jobContext.put(REQUEST,request);
		    jobContext.put(NEXT_STEP, PRODUCT_CONTACT_DWLD_PROCESSING);
		   
		    populateProductMap();
			jobContext.put("productMap", productMap);
			
		} else {
			 jobContext.put(NEXT_STEP, END);
			 requestList = null;
		}
		
		return RepeatStatus.FINISHED;
	}
	
	private void populateProductMap() {
		logger.debug("Inside populateProductMap method");
		List<ProductContactLinkT> productContactLinkList = (List<ProductContactLinkT>) productContactLinkTRepository.findAll();
		productMap =new HashMap<String,List<ProductContactLinkT>>();
		if(productContactLinkList!=null && !productContactLinkList.isEmpty()){
			for(ProductContactLinkT productContactLinkT : productContactLinkList){
				String contactId = productContactLinkT.getContactId();
				if(!productMap.isEmpty()){
					List<ProductContactLinkT> productContactList = productMap.get(contactId);
					if(productContactList==null){
						List<ProductContactLinkT> productList = new ArrayList<ProductContactLinkT>();
						productList.add(productContactLinkT);
						productMap.put(contactId, productList);
					} else {
						productContactList.add(productContactLinkT);
					}
				} else {
					List<ProductContactLinkT> productList = new ArrayList<ProductContactLinkT>();
					productList.add(productContactLinkT);
					productMap.put(contactId, productList);
				}
			}
		}
		logger.debug("Populated Partner Map : " + productMap.size());
	}

}
