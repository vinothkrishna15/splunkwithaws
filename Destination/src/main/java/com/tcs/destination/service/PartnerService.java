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
		logger.debug("Inside save method");
		partnerRepository.save(insertList);
	}

	public PartnerMasterT findById(String partnerId, List<String> toCurrency)
			throws Exception {
		logger.debug("Inside findById Service");
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
			opportunityPartnerLinkT.getOpportunityT()
					.setOpportunityPartnerLinkTs(null);
		}
		preparePartner(partner);
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

		partnerRepository.save(partnerList);

	}

	/**
	 * This service deletes partner details from partner_master_t
	 * 
	 * @param partnerList
	 * @param keyword
	 * @throws Exception
	 */
	public void deletePartner(List<PartnerMasterT> partnerList) {
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
		if (partner.getPartnerName().isEmpty()
				|| partner.getPartnerName() == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"PartnerName is required");
		}
		if (partner.getGeographyMappingT() == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Geography is required");
		}

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
			logger.debug("Partner Saved .... " + partnerMasterT.getPartnerId());

		}

		return partnerMasterT;
	}

	public PaginatedResponse findByNameContaining(String nameWith, int page,
			int count) throws Exception {
		logger.debug("Inside findByNameContaining Service");
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
		return paginatedResponse;
	}

	public PaginatedResponse findByNameStarting(String startsWith, int page,
			int count) throws Exception {
		logger.debug("Inside findByNameContaining Service");
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
			paginatedResponse.setPartnerMasterTs(partnerMasterTs);
			logger.debug("Partners after pagination size is "
					+ partnerMasterTs.size());
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Partner available for the specified page");
		}
		return paginatedResponse;
	}
}