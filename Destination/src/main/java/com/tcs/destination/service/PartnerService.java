package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.ConnectCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.OpportunityPartnerLinkTRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.PaginationUtils;

/**
 * This service deals with partner requests  and 
 * provide functionalities like adding, deleting, updating, validating and saving a partner.
 * It validates the partner requests and it also performs find and search operations.
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

	/**
	 * This service saves partner details into partner_master_t
	 * 
	 * @param insertList
	 * @param keyword
	 * @throws Exception
	 */
	public void save(List<PartnerMasterT> insertList) throws Exception {
		logger.debug("Begin:Inside save method of PartnerService");
		partnerRepository.save(insertList);
		logger.debug("End:Inside save method of PartnerService");
	}

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
		List<ContactT> contactListT = new ArrayList<ContactT>();
		List<ConnectT> connectListT = new ArrayList<ConnectT>();
		List<OpportunityPartnerLinkT> opportunityPartnerListT = new ArrayList<OpportunityPartnerLinkT>();
		List<ConnectCustomerContactLinkT> connectCustomerContactListT = new ArrayList<ConnectCustomerContactLinkT>();

		if (!partnerList.isEmpty()) {
			for (PartnerMasterT partnerT : partnerList) {

				contactListT = contactRepository.findByPartnerId(partnerT
						.getPartnerId());

				connectListT = connectRepository.findByPartnerId(partnerT
						.getPartnerId());

				opportunityPartnerListT = opportunityPartnerLinkTRepository
						.findByPartnerId(partnerT.getPartnerId());

				for (ContactT contactT : contactListT) {
					connectCustomerContactListT = connectCustomerContactLinkTRepository
							.findByContactId(contactT.getContactId());
				}

			}
		}
		connectCustomerContactLinkTRepository
				.delete(connectCustomerContactListT);
		contactRepository.delete(contactListT);
		connectRepository.delete(connectListT);
		opportunityPartnerLinkTRepository.delete(opportunityPartnerListT);
		partnerRepository.delete(partnerList);
		logger.debug("End:Inside deletePartner method of PartnerService");
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
	 * This method is used to validate contact input parameters.
	 * 
	 * @param contact
	 * @return
	 */
	private void validateRequest(PartnerMasterT partner)
			throws DestinationException {
		logger.debug("Begin:Inside validateRequest method of PartnerService");
		if (partner.getPartnerName().isEmpty()
				|| partner.getPartnerName() == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"PartnerName is required");
		}
		if (partner.getGeographyMappingT() == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Geography is required");
		}
		logger.debug("End:Inside validateRequest method of PartnerService");
	}

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
			partnerMasterT.setCreatedModifiedBy(partnerToInsert
					.getCreatedModifiedBy());
			if (partners.isEmpty()) {
				partnerMasterT.setPartnerName(partnerToInsert.getPartnerName());
			} else {
				logger.error("EXISTS: Partner Already Exist!");
				throw new DestinationException(HttpStatus.CONFLICT,
						"Partner Already Exist!");
			}
			partnerMasterT.setWebsite(partnerToInsert.getWebsite());
			partnerMasterT.setFacebook(partnerToInsert.getFacebook());
			partnerMasterT.setGeographyMappingT(partnerToInsert
					.getGeographyMappingT());
			partnerMasterT.setDocumentsAttached("NO");
			partnerMasterT = partnerRepository.save(partnerMasterT);
			logger.debug("End:Inside addPartner method of PartnerService");
		}

		return partnerMasterT;
	}

	public PaginatedResponse findByNameContaining(String nameWith, int page,
			int count) throws Exception {
		logger.debug("Begin:Inside findByNameContaining method of PartnerService");
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count);
		Page<PartnerMasterT> partnersPage = partnerRepository
				.findByPartnerNameIgnoreCaseContainingOrderByPartnerNameAsc(
						nameWith, pageable);

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

	public PaginatedResponse findByNameStarting(String startsWith, int page,
			int count) throws Exception {
		logger.debug("Begin:Inside findByNameStarting method of PartnerService");
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count);
		Page<PartnerMasterT> partnersPage = partnerRepository
				.findByPartnerNameIgnoreCaseStartingWithOrderByPartnerNameAsc(
						startsWith, pageable);

		paginatedResponse.setTotalCount(partnersPage.getTotalElements());
		List<PartnerMasterT> partners = partnersPage.getContent();
		if (partners.isEmpty()) {
			logger.error("NOT_FOUND: No Partners found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Partners found");
		}
		preparePartner(partners);
		paginatedResponse.setPartnerMasterTs(partners);
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

		}

	}

	public PaginatedResponse search(String name, List<String> geography,
			int page, int count) throws DestinationException {
		logger.debug("Begin:Inside search method of PartnerService");
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		if (geography.isEmpty())
			geography.add("");
		List<PartnerMasterT> partnerMasterTs = partnerRepository
				.findByPartnerNameAndGeographyNonMandatory(
						"%" + name.toUpperCase() + "%", geography);
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
}