package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.exception.DestinationException;

import org.springframework.transaction.annotation.Transactional;

@Service
public class PartnerService {

	private static final Logger logger = LoggerFactory
			.getLogger(PartnerService.class);

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;

	public PartnerMasterT findById(String partnerId) throws Exception {
		logger.debug("Inside findById Service");
		PartnerMasterT partner = partnerRepository.findOne(partnerId);
		if (partner == null) {
			logger.error("NOT_FOUND: No such partner found.");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No such partner found.");
		}
		preparePartner(partner);
		return partner;
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
			partners = partnerRepository
					.findByPartnerNameIgnoreCaseContainingOrderByPartnerNameAsc(partnerToInsert
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

	public List<PartnerMasterT> findByNameContaining(String nameWith)
			throws Exception {
		logger.debug("Inside findByNameContaining Service");
		List<PartnerMasterT> partners = partnerRepository
				.findByPartnerNameIgnoreCaseContainingOrderByPartnerNameAsc(nameWith);

		if (partners.isEmpty()) {
			logger.error("NOT_FOUND: No Partners found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Partners found");
		}
		preparePartner(partners);
		return partners;
	}

	public List<PartnerMasterT> findByNameStarting(String startsWith)
			throws Exception {
		logger.debug("Inside findByNameContaining Service");
		List<PartnerMasterT> partners = partnerRepository
				.findByPartnerNameIgnoreCaseStartingWithOrderByPartnerNameAsc(startsWith);

		if (partners.isEmpty()) {
			logger.error("NOT_FOUND: No Partners found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Partners found");
		}
		preparePartner(partners);
		return partners;
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
			}

		}

	}
}