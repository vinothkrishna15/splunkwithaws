package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectOpportunityLinkIdT;
import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectOfferingLinkRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectSecondaryOwnerRepository;
import com.tcs.destination.data.repository.ConnectSubSpLinkRepository;
import com.tcs.destination.data.repository.DocumentRepository;
import com.tcs.destination.exception.ConnectionNotFoundException;
import com.tcs.destination.exception.NoDataFoundException;
import com.tcs.destination.exception.NoSuchOwnerTypeException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.Constants.OWNER_TYPE;

@Component
public class ConnectService {

	@Autowired
	ConnectRepository connectRepository;
	
	@Autowired
	ConnectSecondaryOwnerRepository connectSecondaryOwnerRepository;
	
	@Autowired
	ConnectSubSpLinkRepository connSubSpRepo;

	@Autowired
	ConnectOfferingLinkRepository connOffLinkRepo;

	@Autowired
	DocumentRepository docRepo;

	public ConnectT searchforConnectsById(String connectId) {
		ConnectT connect= connectRepository.findByConnectId(connectId);
		
		if (connect == null)
			throw new ConnectionNotFoundException();
	
		return connect;
	 
	}

	public List<ConnectT> searchforConnectsByNameContaining(String name) {
		List<ConnectT> connectList = connectRepository
				.findByConnectNameIgnoreCaseLike("%" + name + "%");
		
		if (connectList.isEmpty())
			throw new ConnectionNotFoundException();
		return connectList;
	}

	public List<ConnectT> searchforConnectsBetweenForUser(Date fromDate,
			Date toDate, UserT userT, String owner) {
		if (OWNER_TYPE.contains(owner)) {
			List<ConnectT> connects = new ArrayList<ConnectT>();
			if (owner.equalsIgnoreCase(OWNER_TYPE.PRIMARY.toString())) {
				connects = connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetween(
								userT.getUserId(),
								new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime()));
				System.out.println("Primary :" + connects.size());
			} else if (owner.equalsIgnoreCase(OWNER_TYPE.SECONDARY.toString())) {
				connects = connectSecondaryOwnerRepository
						.findConnectTWithDateWithRangeForSecondaryOwner(
								userT.getUserId(),
								new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime()));
			} else if (owner.equalsIgnoreCase(OWNER_TYPE.ALL.toString())) {
				connects.addAll(connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetween(
								userT.getUserId(),
								new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime())));
				connects.addAll(connectSecondaryOwnerRepository
						.findConnectTWithDateWithRangeForSecondaryOwner(
								userT.getUserId(),
								new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime())));
			}
			if (connects.isEmpty())
				throw new NoDataFoundException();
			return connects;
		}
		throw new NoSuchOwnerTypeException();
	}
	
	public boolean insertConnect(ConnectT connect) throws Exception{
		
		Timestamp currentTimeStamp = Constants.getCurrentTimeStamp();
		UserT currentUser = Constants.getCurrentUserDetails();
		String currentUserId = currentUser.getUserId();
		
		connect.setCreatedModifiedBy(currentUserId);
		connect.setCreatedModifiedDatetime(currentTimeStamp);
		
		ConnectT backupConnect = backup(connect);
		setNullForReferencedObjects(connect);
		
        if (connectRepository.save(connect)!= null) {
			
			backupConnect.setConnectId(connect.getConnectId());
			connect = restore(backupConnect);
//			connect.setCreatedModifiedBy(currentUserId);
//			connect.setCreatedModifiedDatetime(currentTimeStamp);
			String categoryUpperCase = connect.getConnectCategory().toUpperCase();
			connect.setConnectCategory(categoryUpperCase);
            String connectId = connect.getConnectId();
            String customerId = connect.getCustomerId();
            String partnerId = connect.getPartnerId();
			
			List<NotesT> noteList = connect.getNotesTs();
			populateNotes(currentTimeStamp,currentUserId,customerId,partnerId,categoryUpperCase,connectId, noteList);
			
			List<ConnectCustomerContactLinkT> conCustConLinkTList = connect.getConnectCustomerContactLinkTs();
	        populateConnectCustomerContactLinks(currentUserId,currentTimeStamp,connectId,conCustConLinkTList);
			
	        List<ConnectOfferingLinkT> conOffLinkTList = connect.getConnectOfferingLinkTs();
	        populateConnectOfferingLinks(currentUserId,currentTimeStamp,connectId,conOffLinkTList);
	        
	        List<ConnectSubSpLinkT> conSubSpLinkTList = connect.getConnectSubSpLinkTs();
	        populateConnectSubSpLinks(currentUserId,currentTimeStamp,connectId,conSubSpLinkTList);
	        
	        List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList = connect.getConnectSecondaryOwnerLinkTs();
            populateConnectSecondaryOwnerLinks(currentUserId,currentTimeStamp,connectId,conSecOwnLinkTList);
	        
            List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList = connect.getConnectTcsAccountContactLinkTs();
	        populateConnectTcsAccountContactLinks(currentUserId,currentTimeStamp,connectId,conTcsAccConLinkTList);
	        
	        if (connectRepository.save(connect)!= null) {
	        	return true;
	        } else {
	        	return false;
	        }
	        
        } 
        return false;
	}
        
        

		private void populateConnectTcsAccountContactLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList) {
			
        	 for(ConnectTcsAccountContactLinkT conTcsAccConLink : conTcsAccConLinkTList){
 	        	conTcsAccConLink.setCreatedModifiedBy(currentUserId);
 	        	conTcsAccConLink.setCreatedModifiedDatetime(currentTimeStamp);
 	        	conTcsAccConLink.setConnectId(connectId);
 	        }
		
	}

		private void populateConnectSecondaryOwnerLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList) {
			
        	for(ConnectSecondaryOwnerLinkT conSecOwnLink : conSecOwnLinkTList){
	        	conSecOwnLink.setCreatedModifiedBy(currentUserId);
	        	conSecOwnLink.setCreatedModifiedDatetime(currentTimeStamp);
	        	conSecOwnLink.setConnectId(connectId);
	        }
		
	}

		private void populateConnectSubSpLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectSubSpLinkT> conSubSpLinkTList) {
			
        	 for(ConnectSubSpLinkT conSubSpLink : conSubSpLinkTList){
 	        	conSubSpLink.setConnectId(connectId);
 	        	conSubSpLink.setCreatedModifiedBy(currentUserId);
 	        	conSubSpLink.setCreatedModifiedDatetime(currentTimeStamp);
 	        }
		
	}

		private void populateConnectOfferingLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectOfferingLinkT> conOffLinkTList) {
			
        	for(ConnectOfferingLinkT conOffLink : conOffLinkTList){
	        	conOffLink.setCreatedModifiedBy(currentUserId);
	        	conOffLink.setCreatedModifiedDatetime(currentTimeStamp);
	        	conOffLink.setConnectId(connectId);
	        }
		
	}

		private void populateConnectCustomerContactLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectCustomerContactLinkT> conCustConLinkTList) {
		
        	for(ConnectCustomerContactLinkT conCustConLink : conCustConLinkTList){
 			   conCustConLink.setCreatedModifiedBy(currentUserId);
 			   conCustConLink.setCreatedModifiedDatetime(currentTimeStamp);
 			   conCustConLink.setConnectId(connectId);
 	        }
	}

		private void populateNotes(Timestamp currentTimeStamp,
			String currentUserId, String customerId,String partnerId,String categoryUpperCase,
			String connectId, List<NotesT> noteList) {
			
        	for(NotesT note : noteList){
    			note.setEntityType(categoryUpperCase);
    			UserT user = new UserT();
    			user.setUserId(currentUserId);
    	        note.setUserT(user);
    	        note.setCreatedDatetime(currentTimeStamp);
    	        note.setConnectId(connectId);
    	        
    	        if(categoryUpperCase.equalsIgnoreCase("CUSTOMER")){
    	        CustomerMasterT customer = new CustomerMasterT();
    	        customer.setCustomerId(customerId);
    	        note.setCustomerMasterT(customer);
    	        }else{
    	        	PartnerMasterT partner = new PartnerMasterT();
    	        	partner.setPartnerId(partnerId);
    	        	note.setPartnerMasterT(partner);
    	        }
    			}
		
	}

		private ConnectT restore(ConnectT backupConnect) {
    		return backupConnect;
    	}

    	private ConnectT backup(ConnectT connect) {
    		return new ConnectT(connect);
    	}

    	private void setNullForReferencedObjects(ConnectT connect) {
    		connect.setCollaborationCommentTs(null);
    		connect.setConnectCustomerContactLinkTs(null);
    		connect.setConnectOfferingLinkTs(null);
    		connect.setConnectOpportunityLinkIdTs(null);
    		connect.setConnectSecondaryOwnerLinkTs(null);
    		connect.setConnectSubSpLinkTs(null);
    		connect.setConnectTcsAccountContactLinkTs(null);
    		connect.setCustomerMasterT(null);
    		connect.setDocumentRepositoryTs(null);
    		connect.setGeographyCountryMappingT(null);
    		connect.setNotesTs(null);
    		connect.setPartnerMasterT(null);
    		connect.setUserFavoritesTs(null);
    		connect.setUserNotificationsTs(null);
    		connect.setUserT(null);
    	}

    	@Transactional
		public boolean editConnect(ConnectT connect) throws Exception{
			//ConnectT backupConnect = backup(connect);
			//setNullForReferencedObjects(connect);
			
			Timestamp currentTimeStamp = Constants.getCurrentTimeStamp();
			UserT currentUser = Constants.getCurrentUserDetails();
			String currentUserId = currentUser.getUserId();
			
			connect.setCreatedModifiedBy(currentUserId);
			connect.setCreatedModifiedDatetime(currentTimeStamp);
				
				//backupConnect.setConnectId(connect.getConnectId());
				//connect = restore(backupConnect);
				String categoryUpperCase = connect.getConnectCategory().toUpperCase();
				connect.setConnectCategory(categoryUpperCase);
	            String connectId = connect.getConnectId();
				
				List<NotesT> noteList = connect.getNotesTs();
				String customerId = connect.getCustomerId();
	            String partnerId = connect.getPartnerId();
				//populateNotes(currentTimeStamp,currentUserId,categoryUpperCase,connectId, noteList,connect);
	            if(noteList!=null)
				populateNotes(currentTimeStamp,currentUserId,customerId,partnerId,categoryUpperCase,connectId, noteList);
				
				List<ConnectCustomerContactLinkT> conCustConLinkTList = connect.getConnectCustomerContactLinkTs();
				if(conCustConLinkTList!=null)
		        populateConnectCustomerContactLinks(currentUserId,currentTimeStamp,connectId,conCustConLinkTList);
				
		        List<ConnectOfferingLinkT> conOffLinkTList = connect.getConnectOfferingLinkTs();
		        if(conOffLinkTList!=null)
		        populateConnectOfferingLinks(currentUserId,currentTimeStamp,connectId,conOffLinkTList);
		        
		        List<ConnectSubSpLinkT> conSubSpLinkTList = connect.getConnectSubSpLinkTs();
		        if(conSubSpLinkTList!=null)
		        populateConnectSubSpLinks(currentUserId,currentTimeStamp,connectId,conSubSpLinkTList);
		        
		        List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList = connect.getConnectSecondaryOwnerLinkTs();
		        if(conSecOwnLinkTList!=null)
	            populateConnectSecondaryOwnerLinks(currentUserId,currentTimeStamp,connectId,conSecOwnLinkTList);
		        
	            List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList = connect.getConnectTcsAccountContactLinkTs();
	            if(conTcsAccConLinkTList!=null)
		        populateConnectTcsAccountContactLinks(currentUserId,currentTimeStamp,connectId,conTcsAccConLinkTList);
		        
		        
		        List<TaskT> taskList = connect.getTaskTs();
		        if(taskList!=null)
		        populateTasks(currentUserId,currentTimeStamp,connectId,taskList);
		        
		        List<ConnectOpportunityLinkIdT> conOppLinkIdTList = connect.getConnectOpportunityLinkIdTs();
		        populateOppLinks(currentUserId,currentTimeStamp,connectId,conOppLinkIdTList);
		        
		        if(connect.getConnectSubLinkDeletionList()!=null){
		        deleteSubSps(connect.getConnectSubLinkDeletionList());
		        }
		        if(connect.getConnectOfferingLinkDeletionList()!=null){
		        deleteOfferings(connect.getConnectOfferingLinkDeletionList());
		        }
		        if(connect.getDocumentsDeletionList()!=null){
		        deleteDocuments(connect.getDocumentsDeletionList());
		        }
		        
		        if (connectRepository.save(connect)!= null) {
		        	return true;
		        } else {
		        	return false;
		        }
	        
		}

		private void deleteDocuments(
				List<DocumentRepositoryT> documentsDeletionList) {
			for(DocumentRepositoryT connectDoc : documentsDeletionList){
				docRepo.delete(connectDoc);
			}
		}

		private void deleteOfferings(
				List<ConnectOfferingLinkT> connectOfferingLinkDeletionList) {
			for(ConnectOfferingLinkT connectOffLink : connectOfferingLinkDeletionList){
				connOffLinkRepo.delete(connectOffLink.getConnectOfferingLinkId());
			}
		}

		private void deleteSubSps(
				List<ConnectSubSpLinkT> connectSubLinkDeletionList) {
			for(ConnectSubSpLinkT connectSubSp : connectSubLinkDeletionList){
				connSubSpRepo.delete(connectSubSp.getConnectSubSpLinkId());
			}
		}

		private void populateOppLinks(String currentUserId,
				Timestamp currentTimeStamp, String connectId,
				List<ConnectOpportunityLinkIdT> conOppLinkIdTList) {
			for(ConnectOpportunityLinkIdT conOppLinkId : conOppLinkIdTList){
				conOppLinkId.setCreatedModifiedBy(currentUserId);
				conOppLinkId.setCreatedModifiedDatetime(currentTimeStamp);
				conOppLinkId.setConnectId(connectId);
	 	        }
			
		}

		private void populateTasks(String currentUserId,
				Timestamp currentTimeStamp, String connectId,
				List<TaskT> taskList) {
			for(TaskT task : taskList){
				task.setCreatedModifiedBy(currentUserId);
				task.setCreatedModifiedDatetime(currentTimeStamp);
				task.setConnectId(connectId);
	 	        }
			
		}

}
