
package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.PartnerContactLinkT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.PartnerProductDetailsDTO;
import com.tcs.destination.bean.PartnerSubSpMappingT;
import com.tcs.destination.bean.PartnerSubspProductMappingT;
import com.tcs.destination.bean.ProductContactLinkT;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.ConnectCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OpportunityPartnerLinkTRepository;
import com.tcs.destination.data.repository.PartnerContactLinkTRepository;
import com.tcs.destination.data.repository.PartnerDao;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.PartnerSubSpMappingTRepository;
import com.tcs.destination.data.repository.PartnerSubSpProductMappingTRepository;
import com.tcs.destination.data.repository.ProductContactLinkTRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.SmartSearchType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.CommonHelper;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;

/**
 * This service deals with partner requests and provide functionalities like
 * adding, deleting, updating, validating and saving a partner. It validates the
 * partner requests and it also performs find and search operations.
 */
@Service
public class PartnerService {

	private static final Logger logger = LoggerFactory
			.getLogger(PartnerService.class);

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;

	@Autowired
	OpportunityPartnerLinkTRepository opportunityPartnerLinkTRepository;

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	ConnectCustomerContactLinkTRepository connectCustomerContactLinkTRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	GeographyRepository geographyRepository;

	@Autowired
	private CommonHelper commonHelper;

	@Autowired @Lazy
	private PartnerDao partnerDao;

	@Autowired 
	private PartnerSubSpMappingTRepository partnerSubSpMappingTRepository;

	@Autowired 
	PartnerSubSpProductMappingTRepository partnerSubSpProductMappingTRepository;

	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	@Autowired
	private GeographyRepository geoRepository;
	
	@Autowired
	PartnerSubSpProductMappingTRepository partnerSubSpProductMappingRepository;

	@Autowired
	ProductContactLinkTRepository productContactLinkTRepository;

	@Autowired
	PartnerContactLinkTRepository partnerContactLinkTRepository;

	@Autowired
	SubSpRepository subSpRepository;
	
	@Autowired
	WorkflowService workflowService;
	
	@Autowired
	PartnerSubSpMappingTRepository partnerSubSpMappingRepository;

	private Map<String, GeographyMappingT> geographyMapping = null;

	private Map<String, GeographyCountryMappingT> geographyCountryMapping = null;



	/**
	 * This service saves partner details into partner_master_t
	 * 
	 * @param insertList
	 * @param keyword
	 * @throws Exception
	 */
	public void save(List<PartnerMasterT> partnerList) throws Exception {
		logger.debug("Begin:Inside save method of PartnerService");
		partnerRepository.save(partnerList);
		logger.debug("End:Inside save method of PartnerService");
	}

	/**
	 * 
	 * @param childList
	 * @param mapOfPartnerAndHqLink
	 */
	public void saveChild(List<PartnerMasterT>childList,Map<String, String> mapOfPartnerAndHqLink)
	{
		logger.debug("Begin:Inside save method of PartnerService");
		List<PartnerMasterT> childPartnerList=new ArrayList<PartnerMasterT>();
		for(PartnerMasterT partner:childList)
		{
			String hqPartnerLinkName=mapOfPartnerAndHqLink.get(partner.getPartnerName());
			String hqPartnerLinkId=partnerRepository.findPartnerIdByName(hqPartnerLinkName);
			partner.setHqPartnerLinkId(hqPartnerLinkId);
			childPartnerList.add(partner);
		}
		partnerRepository.save(childPartnerList);
		logger.debug("End:Inside save method of PartnerService");

	}

	/**
	 * Retrieve partner details based on partner id
	 * @param partnerId
	 * @param toCurrency
	 * @return
	 * @throws Exception
	 */
	public PartnerMasterT findById(String partnerId, List<String> toCurrency)
			throws Exception {
		logger.debug("Begin:Inside findById method of PartnerService");
		PartnerMasterT partner = partnerRepository.findOne(partnerId);
		if (partner == null) {
			logger.error("NOT_FOUND: No such partner found.");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No such partner found.");
		}
		for (OpportunityPartnerLinkT opportunityPartnerLinkT : partner
				.getOpportunityPartnerLinkTs()) {
			beaconConverterService.convertOpportunityCurrency(
					opportunityPartnerLinkT.getOpportunityT(), toCurrency);
		}
		preparePartner(partner);
		logger.debug("End:Inside findById method of PartnerService");
		return partner;
	}

	/**
	 * This service updates partner details in partner_master_t
	 * 
	 * @param partnerList
	 * @param keyword
	 * @throws Exception
	 */
	public void updatePartner(List<PartnerMasterT> partnerList) {
		logger.debug("Begin:Inside updatePartner method of PartnerService");
		partnerRepository.save(partnerList);
		logger.debug("End:Inside updatePartner method of PartnerService");
	}

	/**
	 * This service deletes partner details from partner_master_t
	 * 
	 * @param partnerList
	 * @param keyword
	 * @throws Exception
	 */
	public void deletePartner(List<PartnerMasterT> partnerList) {
		logger.debug("Begin:Inside deletePartner method of PartnerService");
		partnerRepository.save(partnerList);
		logger.debug("End:Inside deletePartner method of PartnerService");
	}

	/**
	 * This service deletes partner subsp details from partner_subsp_mapping_t
	 * @param deleteList
	 */
	public void deletePartnerSubSp(List<PartnerSubSpMappingT> deleteList) {
		logger.debug("Begin:Inside deletePartnerSubSp method of PartnerService");
		partnerSubSpMappingTRepository.delete(deleteList);
		logger.debug("End:Inside deletePartnerSubSp method of PartnerService");
	}


	/**
	 * This service deletes partner subsp details from  partner_subsp_product_mapping_t
	 * @param deleteList
	 */
	public void deletePartnerSubSpProduct(List<PartnerSubspProductMappingT> deleteList) {
		logger.debug("Begin:Inside deletePartnerSubSpProduct method of PartnerService");
		partnerSubSpProductMappingTRepository.delete(deleteList);
		logger.debug("End:Inside deletePartnerSubSpProduct method of PartnerService");
	}

	/*
	 * @Transactional public boolean save(PartnerMasterT partner, boolean
	 * isUpdate) throws Exception { if (isUpdate) { if (partner.getPartnerId()
	 * == null) { throw new DestinationException(HttpStatus.BAD_REQUEST,
	 * "Cannot Update Partner without partnerId"); }
	 * 
	 * } else { if (partner.getPartnerId() != null) { throw new
	 * DestinationException
	 * (HttpStatus.BAD_REQUEST,"PartnerId should not be passed"); } }
	 * 
	 * // Validate input parameters validateRequest(partner);
	 * 
	 * PartnerMasterT managedPartner = saveBasePartner(partner); return true; }
	 * 
	 * private PartnerMasterT saveBasePartner(PartnerMasterT requestPartner)
	 * throws CloneNotSupportedException, Exception { PartnerMasterT partner
	 * =requestPartner.clone();
	 * partner.setPartnerId(partnerRepository.save(requestPartner
	 * ).getPartnerId()); return partner; }
	 */

	/**
	 * This method inserts partner to the database
	 * 
	 * @param partnerToInsert
	 * @return PartnerMasterT
	 * @throws Exception
	 */
	@Transactional
	public PartnerMasterT addPartner(PartnerMasterT partnerToInsert)
			throws Exception {

		PartnerMasterT partnerMasterT = null;
		List<PartnerMasterT> partners = null;
		logger.debug("Begin:Inside addPartner method of PartnerService");
		if (partnerToInsert != null) {
			partnerMasterT = new PartnerMasterT();
			partners = partnerRepository.findByPartnerName(partnerToInsert
					.getPartnerName());
			partnerMasterT.setCorporateHqAddress(partnerToInsert
					.getCorporateHqAddress());
			partnerMasterT.setCreatedBy(partnerToInsert.getCreatedBy());
			partnerMasterT.setModifiedBy(partnerToInsert.getModifiedBy());

			if (partners.isEmpty()) {
				partnerMasterT.setPartnerName(partnerToInsert.getPartnerName());
			} else {
				logger.error("EXISTS: Partner Already Exist!");
				throw new DestinationException(HttpStatus.CONFLICT,
						"Partner Already Exist!");
			}
			partnerMasterT.setWebsite(partnerToInsert.getWebsite());
			partnerMasterT.setFacebook(partnerToInsert.getFacebook());
			partnerMasterT.setGeography(partnerToInsert.getGeography());
			partnerMasterT.setDocumentsAttached("NO");
			partnerMasterT.setCountry(partnerToInsert.getCountry());
			partnerMasterT.setCity(partnerToInsert.getCity());
			partnerMasterT.setText1(partnerToInsert.getText1());
			partnerMasterT.setText2(partnerToInsert.getText2());
			partnerMasterT.setText3(partnerToInsert.getText3());
			partnerMasterT.setGroupPartnerName(partnerToInsert.getGroupPartnerName());
			partnerMasterT.setNotes(partnerToInsert.getNotes());
			partnerMasterT.setHqPartnerLinkId(partnerToInsert.getHqPartnerLinkId());

			validateInactiveIndicators(partnerMasterT);

			partnerMasterT = partnerRepository.save(partnerMasterT);
			logger.debug("End:Inside addPartner method of PartnerService");
		}

		return partnerMasterT;
	}

	/**
	 * validates all the fields of partner which has any inactive fields 
	 * @param partner
	 * @throws {@link DestinationException} if any inactive records founds
	 */
	public void validateInactiveIndicators(PartnerMasterT partner) {

		//createdModifiedBy, 
		String createdBy = partner.getCreatedBy();
		if(StringUtils.isNotBlank(createdBy) && userRepository.findByActiveTrueAndUserId(createdBy) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The user createdBy is inactive");
		}

		// geography, 
		String geography = partner.getGeography();
		if(StringUtils.isNotBlank(geography) && geoRepository.findByActiveTrueAndGeography(geography) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The geography is inactive");
		}
	}

	/**
	 * retrieves partner names containing a particular string
	 * @param nameWith
	 * @param page
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public PaginatedResponse findByNameContaining(String nameWith, int page,
			int count) throws Exception {
		logger.debug("Begin:Inside findByNameContaining method of PartnerService");
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count);
		Page<PartnerMasterT> partnersPage = partnerRepository
				.findByPartnerNameIgnoreCaseContainingAndActiveOrderByPartnerNameAsc(
						nameWith, pageable,true);

		paginatedResponse.setTotalCount(partnersPage.getTotalElements());
		List<PartnerMasterT> partners = partnersPage.getContent();
		if (partners.isEmpty()) {
			logger.error("NOT_FOUND: No Partners found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Partners found");
		}
		preparePartner(partners);
		paginatedResponse.setPartnerMasterTs(partners);
		logger.debug("End:Inside findByNameContaining method of PartnerService");
		return paginatedResponse;
	}

	/**
	 * retrieves partner names starting with a particular string
	 * @param startsWith
	 * @param page
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public PaginatedResponse findByNameStarting(String startsWith, int page,
			int count) throws Exception {
		logger.debug("Begin:Inside findByNameStarting method of PartnerService");
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count);
		List<PartnerMasterT> partnerList = new ArrayList<PartnerMasterT>();
		if (!startsWith.equals("@")) {
			Page<PartnerMasterT> partnersPage = partnerRepository
					.findByPartnerNameIgnoreCaseStartingWithAndActiveOrderByPartnerNameAsc(
							startsWith, pageable,true);
			paginatedResponse.setTotalCount(partnersPage.getTotalElements());
			partnerList.addAll(partnersPage.getContent());
		} else {
			for (int i = 0; i <= 9; i++) {
				Page<PartnerMasterT> partnersPage = partnerRepository
						.findByPartnerNameIgnoreCaseStartingWithAndActiveOrderByPartnerNameAsc(i + "",  pageable,true);
				paginatedResponse.setTotalCount(partnersPage.getTotalElements());
				partnerList.addAll(partnersPage.getContent());
			}		
		}

		if (partnerList.isEmpty()) {
			logger.error("NOT_FOUND: No Partners found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Partners found");
		}
		preparePartner(partnerList);
		paginatedResponse.setPartnerMasterTs(partnerList);
		logger.debug("End:Inside findByNameStarting method of PartnerService");
		return paginatedResponse;
	}
	private void preparePartner(List<PartnerMasterT> partners) {
		for (PartnerMasterT partner : partners) {
			preparePartner(partner);
		}
	}

	private void preparePartner(PartnerMasterT partner) {
		if (partner != null) {
			List<OpportunityPartnerLinkT> opportunityPartnerLinkTs = partner
					.getOpportunityPartnerLinkTs();
			for (OpportunityPartnerLinkT opportunityPartnerLinkT : opportunityPartnerLinkTs) {
				opportunityPartnerLinkT.getOpportunityT()
				.setOpportunityPartnerLinkTs(null);
				opportunityPartnerLinkT.getOpportunityT().getCustomerMasterT()
				.setOpportunityTs(null);
			}

			//remove cyclic partnerContactLinkTs
			List<PartnerContactLinkT> partnerContactLinkTs = partner.getPartnerContactLinkTs();
			for (PartnerContactLinkT partnerContactLinkT : partnerContactLinkTs) {
				partnerContactLinkT.getContactT().setPartnerContactLinkTs(null);
				if (partnerContactLinkT.getContactT() != null) {
					for (ProductContactLinkT productContactLinkT : partnerContactLinkT.getContactT().getProductContactLinkTs() ) {
						if (productContactLinkT.getProductMasterT() != null) {
							productContactLinkT.getProductMasterT().setProductContactLinkTs(null);
						}
					}
				}
			}
			//remove cyclic PartnerSubspProductMappingTs
			for (PartnerSubSpMappingT partnerSubSpMappingT : partner.getPartnerSubSpMappingTs()){
				for (PartnerSubspProductMappingT partnerSubspProductMappingT : partnerSubSpMappingT.getPartnerSubspProductMappingTs()) {
					if (partnerSubspProductMappingT.getProductMasterT() != null) {
						partnerSubspProductMappingT.getProductMasterT().setPartnerSubspProductMappingTs(null);
					}
				}
			}
		}
	}

	/**
	 * service implementation for partner advanced search
	 * with paginated response
	 * @param name
	 * @param geography
	 * @param inactive
	 * @param page
	 * @param count
	 * @return
	 * @throws DestinationException
	 */
	public PaginatedResponse search(String name, List<String> geography, boolean inactive,
			int page, int count) throws DestinationException {
		logger.debug("Begin:Inside search method of PartnerService");
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		if (geography.isEmpty())
			geography.add("");
		List<PartnerMasterT> partnerMasterTs = partnerRepository
				.findByPartnerNameAndGeographyNonMandatory(
						"%" + name.toUpperCase() + "%", geography, !inactive);
		if (partnerMasterTs.isEmpty()) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Partner available");
		}
		paginatedResponse.setTotalCount(partnerMasterTs.size());
		// Code for pagination
		if (PaginationUtils.isValidPagination(page, count,
				partnerMasterTs.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					partnerMasterTs.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					partnerMasterTs.size()) + 1;
			partnerMasterTs = partnerMasterTs.subList(fromIndex, toIndex);
			preparePartner(partnerMasterTs);
			paginatedResponse.setPartnerMasterTs(partnerMasterTs);
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Partner available for the specified page");
		}
		logger.debug("End:Inside search method of PartnerService");
		return paginatedResponse;
	}

	/**
	 * This method is used to edit the partner details and save the same.
	 * 
	 * @param partnerMaster
	 * @return
	 * @throws Exception 
	 */
	public boolean updatePartner(PartnerMasterT partnerMaster)
			throws Exception {
		logger.info("Inside updatePartner method");
		boolean updateStatus = false;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		UserT user = userRepository.findByUserId(userId);
		String userRole = user.getUserRole();
		String userGroup=user.getUserGroup();
		boolean isBdmWithAccess=false;
		if (UserRole.contains(userRole)) {
			switch (UserRole.valueOf(UserRole.getName(userRole))) {
			case SYSTEM_ADMIN:
			case STRATEGIC_GROUP_ADMIN:
				updateStatus=validateAndUpdatePartner(partnerMaster,isBdmWithAccess);
				break;

			case USER:	
				if (UserGroup.contains(userGroup))
				{
					switch(UserGroup.valueOf(UserGroup.getName(userGroup)))
					{
					case BDM:

						isBdmWithAccess=true;
						updateStatus=validateAndUpdatePartner(partnerMaster,isBdmWithAccess);
						break;
					default:
						break;


					}
				}
				break;
			default:
				logger.error("User is not authorized to access this service");
				throw new DestinationException(HttpStatus.FORBIDDEN,
						"User is not authorised to access this service");

			}
		}
		return updateStatus;
	}

	/**
	 * validation for partner details before it got saved to partner master T
	 * @param partnerMaster
	 * @param isBdmWithAccess
	 * @return
	 * @throws Exception 
	 */
	boolean validateAndUpdatePartner(PartnerMasterT partnerMaster,boolean isBdmWithAccess) throws Exception
	{
		boolean isUpdate=false;
		String partnerId = partnerMaster.getPartnerId();
		if (partnerId == null) {
			logger.error("BAD_REQUEST: partner Id is required for update");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"partner Id is required for update");
		}
		if (!partnerRepository.exists(partnerId)) {
			logger.error(
					"NOT_FOUND: Partner Details not found for update: {}",
					partnerId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Partner Details not found for update: "
							+ partnerId);
		}
		PartnerMasterT partnerWithMappingDetails = (PartnerMasterT) DestinationUtils.copy(partnerMaster);
		PartnerMasterT partner = partnerRepository.findOne(partnerId);

		// Partner Name
		String partnerName = partnerMaster.getPartnerName();
		if (!StringUtils.isEmpty(partnerName)) {

			List<PartnerMasterT> findPartnerName = partnerRepository.findByPartnerName(partnerName);
			if(findPartnerName!=null && !findPartnerName.isEmpty()){
				PartnerMasterT partnerExistingByName = findPartnerName.get(0);
				if(!partnerExistingByName.getPartnerId().equals(partner.getPartnerId())){
					if(partnerExistingByName.getPartnerName().equals(partner.getPartnerName())){
						logger.error("Partner Name already exists");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Partner Name already exists");
					}
				}
			} else {
				if(!isBdmWithAccess)
				{
					partner.setPartnerName(partnerName);
					isUpdate=true;
				}
				else
				{
					logger.error("NOT_AUTHORISED: user is not authorised to update the partner name");
					throw new DestinationException(HttpStatus.UNAUTHORIZED, "user is not authorised to update the partner name" );
				}
			}
		} else {
			logger.error("Partner Name should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Partner Name should not be empty");
		}
		// Geography
		String geography = partnerMaster.getGeography();
		if (!StringUtils.isEmpty(geography)) {
			geographyMapping = commonHelper.getGeographyMappingT();
			if (geographyMapping.containsKey(partnerMaster
					.getGeography())) {
				if(!(partner.getGeography().equals(partnerMaster.getGeography())))
				{
					if(!isBdmWithAccess)
					{
						partner.setGeography(geography);
						isUpdate=true;
					}
					else
					{
						logger.error("NOT_AUTHORISED: user is not authorised to update the geography");
						throw new DestinationException(HttpStatus.UNAUTHORIZED, "user is not authorised to update the geography" );
					}
				}
			} else {
				logger.error("Invalid geography");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Geography :" + partnerMaster.getGeography()
						+ "is not found");
			}
		} else {
			logger.error("geography should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Geography should not be empty");
		}

		// mandatory validations for group partner name

		// group Partner Name
		/*String groupPartnerName = partnerMaster.getGroupPartnerName();
		if (!StringUtils.isEmpty(groupPartnerName)) {
			if(!(partner.getGroupPartnerName().equals(partnerMaster.getGroupPartnerName()))){

				if(!isBdmWithAccess)
				{
					partner.setGroupPartnerName(groupPartnerName);
					isUpdate=true;
				}
				else
				{
					logger.error("NOT_AUTHORISED: user is not authorised to update the group Partner name");
					throw new DestinationException(HttpStatus.UNAUTHORIZED, "user is not authorised to update the partner name" );
				}
			}
		}
		else {
			logger.error("group Partner name should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"group Partner name should not be empty");
		}*/
		// country
		/*String countryStr = partnerMaster.getCountry();
		if (!StringUtils.isEmpty(countryStr)) {
			geographyCountryMapping = commonHelper.getGeographyCountryMappingT();
			if (geographyCountryMapping.containsKey(partnerMaster.getCountry())) {
				if(!(partner.getCountry().equals(partnerMaster.getCountry())))
				{
					if(!isBdmWithAccess)
					{
						partner.setCountry(countryStr);
						isUpdate=true;
					}
					else
					{
						logger.error("NOT_AUTHORISED: user is not authorised to update the country");
						throw new DestinationException(HttpStatus.UNAUTHORIZED, "user is not authorised to update the country" );
					}
				}
			} else {
				logger.error("Invalid country");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Country :" + partnerMaster.getCountry()
						+ "is not found");
			}
		} else {
			logger.error("Country should not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Country should not be empty");
		}*/

		////notes edited
		//		if(!StringUtils.isEmpty(oldCustomerObj.getNotes())){
		//			notes = oldCustomerObj.getNotes();
		//		}
		//		if (!customerMaster.getNotes().equals(notes)) {
		//			oldCustomerObj.setNotes(customerMaster.getNotes());
		//			isCustomerModifiedFlag = true;
		//		}

		// Website
		String website = "";
		if (!StringUtils.isEmpty(partner.getWebsite())) {
			website = partner.getWebsite();
		}
		if (!partnerMaster.getWebsite().equals(website)) {
			partner.setWebsite(partnerMaster.getWebsite());
			isUpdate=true;
		}

		// notes
		String notes = partnerMaster.getNotes();
		if (!StringUtils.isEmpty(notes)) {
			partner.setNotes(notes);
			isUpdate=true;
		}
		else
		{
			partner.setNotes("");
		}


		// Corporate HQ Address
		String address = partnerMaster.getCorporateHqAddress();
		if (!StringUtils.isEmpty(address)) {
			partner.setCorporateHqAddress(address);
			isUpdate=true;
		}
		// Facebook
		String facebook = partnerMaster.getFacebook();
		if (!StringUtils.isEmpty(facebook)) {
			partner.setFacebook(facebook);
			isUpdate=true;
		}
		// Logo 
		byte[] logo = partnerMaster.getLogo();
		if (logo!=null) {
			partner.setLogo(logo);
			isUpdate=true;
		}

		// added for partne rdata model chnages
		//text1,text2 and text3
		String text1 = partnerMaster.getText1();
		if (!StringUtils.isEmpty(text1)) {
			partner.setText1(text1);
			isUpdate=true;
		}
		String text2 = partnerMaster.getText2();
		if (!StringUtils.isEmpty(text2)) {
			partner.setText2(text2);
			isUpdate=true;
		}
		String text3 = partnerMaster.getText3();
		if (!StringUtils.isEmpty(text3)) {
			partner.setText3(text3);
			isUpdate=true;
		}
		// hq partner link Id
		String hqPartnerLinkId = partnerMaster.getHqPartnerLinkId();
		if (!StringUtils.isEmpty(hqPartnerLinkId)) {
			partner.setHqPartnerLinkId(hqPartnerLinkId);
			isUpdate=true;
		}

		// city
		String city = partnerMaster.getCity();
		if (!StringUtils.isEmpty(city)) {
			partner.setCity(city);
			isUpdate=true;
		}

		//country
		String country = partnerMaster.getCountry();
		if (!StringUtils.isEmpty(country)) {
			partner.setCountry(country);
			isUpdate=true;
		}
		//groupPartnerName
		String groupPartnerNamestr = partnerMaster.getGroupPartnerName();
		if (!StringUtils.isEmpty(groupPartnerNamestr)) {
			partner.setGroupPartnerName(groupPartnerNamestr);
			isUpdate=true;
		}

		partner.setModifiedBy(DestinationUtils.getCurrentUserId());

		if(isUpdate)
		{
			PartnerMasterT partnerCreated = partnerRepository.save(partner);
			deletePartnerSub(partnerId);
			deletePartnerContact(partnerId);
			
			
			// for updating the subsp and product mapping tables
			if(!partnerWithMappingDetails.getPartnerProductDetailsDTOs().isEmpty()){
				for(PartnerProductDetailsDTO partnerProductDetailsDTO : partnerWithMappingDetails.getPartnerProductDetailsDTOs()){
					// processing subsps for the new partner
					if(partnerProductDetailsDTO.getSubspList().size() > 0 ){
						for(Integer subSpId : partnerProductDetailsDTO.getSubspList()){
							PartnerSubSpMappingT partnerSubsp = new PartnerSubSpMappingT();
							partnerSubsp.setPartnerId(partnerCreated.getPartnerId());
							partnerSubsp.setSubSpId(subSpId);
							partnerSubsp.setSubSp(subSpRepository.findBySubSpId(subSpId).getSubSp());
							partnerSubsp.setCreatedBy(DestinationUtils.getCurrentUserDetails().getUserId());
							partnerSubsp.setModifiedBy(DestinationUtils.getCurrentUserDetails().getUserId());
							PartnerSubSpMappingT partnerSubspSaved = partnerSubSpMappingRepository.save(partnerSubsp);

							//If product available for this partner then this partner subsp has to be persisted along with its product in partner_subsp_product_mapping_t
							if(partnerProductDetailsDTO.getProductId() != null){
								workflowService.savePartnerSubspAndProduct(partnerSubspSaved, partnerProductDetailsDTO.getProductId());
							}
						}
					}

					//Processing contacts for partner and Products
					if(partnerProductDetailsDTO.getPartnerProductContact() != null ){
						ContactT productcontactSaved = new ContactT();
						String contactId = null;
						if (partnerProductDetailsDTO.getPartnerProductContact().getContactId() == null ) {
							productcontactSaved = workflowService.saveNewContact(partnerProductDetailsDTO);
							if(productcontactSaved != null && productcontactSaved.getContactId() != null){
								contactId = productcontactSaved.getContactId();
								workflowService.populateAsProductOrPartnerContact(partnerProductDetailsDTO, contactId, partnerCreated.getPartnerId());
							}
						}
						else if(partnerProductDetailsDTO.getPartnerProductContact().getContactId() != null){
							contactId = partnerProductDetailsDTO.getPartnerProductContact().getContactId();
							workflowService.populateAsProductOrPartnerContact(partnerProductDetailsDTO, contactId, partnerCreated.getPartnerId());
						}			 
					}
				}
			}
			logger.info(partner.getPartnerId() + " Partner details updated");
		}
		return isUpdate;
	}

	/**
	 * delete all partner contacts from partnerContactLinkt for a given partner
	 * @param partnerId
	 */
	private void deletePartnerContact(String partnerId) {
		List<PartnerContactLinkT> partnerContatcLinkTs = partnerContactLinkTRepository.findByPartnerId(partnerId);
		if (CollectionUtils.isNotEmpty(partnerContatcLinkTs)) {
			for (PartnerContactLinkT partnerContact: partnerContatcLinkTs) {
			partnerContactLinkTRepository.delete(partnerContact);
			}
		}
	}

	/**
	 * delete all subsp and supsp-product entries for the given parner
	 * @param partnerId
	 */
	private void deletePartnerSub(String partnerId) {
		List<PartnerSubSpMappingT> subSPLinks = partnerSubSpMappingRepository.findByPartnerId(partnerId);
		for (PartnerSubSpMappingT partnerSubSpMappingT : subSPLinks) {
			String partnerSubspMappingId = partnerSubSpMappingT.getPartnerSubspMappingId();
			List<PartnerSubspProductMappingT> subSpProducts = partnerSubSpProductMappingRepository.findByPartnerSubspMappingId(partnerSubspMappingId);
			if(CollectionUtils.isNotEmpty(subSpProducts)) {
				partnerSubSpProductMappingRepository.delete(subSpProducts);
			}
			partnerSubSpMappingRepository.delete(partnerSubSpMappingT);
		}
		
	}

	public PageDTO<SearchResultDTO<PartnerMasterT>> smartSearch(
			SmartSearchType smartSearchType, String term, boolean getAll,
			int page, int count) {
		logger.info("PartnerService::smartSearch type {}",smartSearchType);
		PageDTO<SearchResultDTO<PartnerMasterT>> res = new PageDTO<SearchResultDTO<PartnerMasterT>>();
		List<SearchResultDTO<PartnerMasterT>> resList = Lists.newArrayList();
		SearchResultDTO<PartnerMasterT> searchResultDTO = new SearchResultDTO<PartnerMasterT>();
		if(smartSearchType != null) {

			switch(smartSearchType) {
			case ALL:
				resList.add(getPartnersByPartnerName(term, getAll));
				resList.add(getPartnersByCountry(term, getAll));
				resList.add(getPartnersBySubSp(term, getAll));
				break;
			case PARTNER:
				searchResultDTO = getPartnersByPartnerName(term, getAll);
				break;
			case COUNTRY:
				searchResultDTO = getPartnersByCountry(term, getAll);
				break;
			case SUBSP:
				searchResultDTO = getPartnersBySubSp(term, getAll);
				break;
			default:
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid search type");
			}

			if(smartSearchType != SmartSearchType.ALL) {//paginate the result if it is fetching entire record(ie. getAll=true)
				if(getAll) {
					List<PartnerMasterT> values = searchResultDTO.getValues();
					List<PartnerMasterT> records = PaginationUtils.paginateList(page, count, values);
					if(CollectionUtils.isNotEmpty(records)) {
						preparePartner(records);
					}
					searchResultDTO.setValues(records);
					res.setTotalCount(values.size());
				}
				resList.add(searchResultDTO);
			}
		}
		res.setContent(resList);
		return res;
	}

	private SearchResultDTO<PartnerMasterT> getPartnersByPartnerName(String term,
			boolean getAll) {
		List<PartnerMasterT> records = partnerRepository.searchByPartnerName("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.PARTNER);
	}

	private SearchResultDTO<PartnerMasterT> getPartnersBySubSp(
			String term, boolean getAll) {
		List<PartnerMasterT> records = partnerRepository.searchBySubSp("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.SUBSP);
	}

	private SearchResultDTO<PartnerMasterT> getPartnersByCountry(String term, boolean getAll) {
		List<PartnerMasterT> records = partnerRepository.searchByCountry("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.COUNTRY);
	}


	private SearchResultDTO<PartnerMasterT> createSearchResultFrom(
			List<PartnerMasterT> records, SmartSearchType type) {
		SearchResultDTO<PartnerMasterT> conRes = new SearchResultDTO<PartnerMasterT>();
		conRes.setSearchType(type);
		conRes.setValues(records);
		return conRes;
	}

	/**
	 * This service saves partner supsp details into partner_sub_sp_mapping_t
	 * 
	 * @param insertList
	 * @param keyword
	 * @throws Exception
	 */
	public void savePartnerSubsp(List<PartnerSubSpMappingT> partnerList) throws Exception {
		logger.debug("Begin:Inside save method of PartnerService");
		partnerSubSpMappingTRepository.save(partnerList);
		logger.debug("End:Inside save method of PartnerService");
	}

	/**
	 * 
	 * @param partnerList
	 * @throws Exception
	 */
	public void savePartnerSubSpProduct(List<PartnerSubspProductMappingT> partnerList) throws Exception {
		logger.debug("Begin:Inside save method of PartnerService");
		partnerSubSpProductMappingTRepository.save(partnerList);
		logger.debug("End:Inside save method of PartnerService");
	}

	/**
	 * To retrieve the list of partners based on group partner name
	 * @param nameWith
	 * @return
	 */
	public Set<String> findByGroupPartnerName(String groupPartnerName) {
		logger.debug("Inside findByGroupPartnerName() service");
		Set<String> groupPartnerNameSet = new HashSet<String>();
		List<PartnerMasterT> partnerList = partnerRepository
				.findDistinctByGroupPartnerNameIgnoreCaseContainingAndGroupPartnerNameIgnoreCaseNotLikeAndActiveOrderByGroupPartnerNameAsc(
						groupPartnerName, Constants.UNKNOWN_PARTNER,true);
		//retrieving distinct groupPartnerNames from the queried result
		for (PartnerMasterT partner : partnerList) {
			groupPartnerNameSet.add(partner.getGroupPartnerName());
		}
		if (groupPartnerNameSet.isEmpty()) {
			logger.error(
					"NOT_FOUND: Partner not found with given group Partner name: {}",
					groupPartnerName);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Partner not found with given group Partner name: "
							+ groupPartnerName);
		}
		return groupPartnerNameSet;
	}

	private void preparePartnerDetails(List<PartnerMasterT> partnerList) {
		logger.debug("Inside preparePartnerDetails() method");

		if (partnerList != null && !partnerList.isEmpty()) {
			ArrayList<String> partnerNameList = new ArrayList<String>();
			for (PartnerMasterT partnerMasterT : partnerList) {
				partnerNameList.add(partnerMasterT.getPartnerName());
			}
			partnerNameList =  partnerDao.getPreviledgedPartnerName(DestinationUtils
					.getCurrentUserDetails().getUserId(), partnerNameList,
					true);

			for (PartnerMasterT partnerMasterT : partnerList) {
				preparePartnerDetails(partnerMasterT, partnerNameList);
			}
		}
	}

	private void preparePartnerDetails(PartnerMasterT partnerMasterT,
			ArrayList<String> partnerNameList) throws DestinationException {
		logger.debug("Inside preparePartnerDetails() method");

		removeCyclicForLinkedContactTs(partnerMasterT);
		try {
			if (partnerNameList == null) {
				partnerNameList = new ArrayList<String>();
				partnerNameList.add(partnerMasterT.getPartnerName());
				partnerNameList =  partnerDao.getPreviledgedPartnerName(DestinationUtils
						.getCurrentUserDetails().getUserId(), partnerNameList,
						true);
			}
			if (partnerNameList == null
					|| partnerNameList.isEmpty()
					|| (!partnerNameList.contains(partnerMasterT
							.getPartnerName()))) {
			}
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	private void removeCyclicForLinkedContactTs(PartnerMasterT partnerMasterT) {
		if (partnerMasterT != null) {
			if (partnerMasterT.getPartnerContactLinkTs() != null) {
				for (PartnerContactLinkT partnerContactLinkT : partnerMasterT
						.getPartnerContactLinkTs()) {
					partnerContactLinkT.getContactT()
					.setPartnerContactLinkTs(null);
				}
			}
		}
	}
}