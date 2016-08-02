package com.tcs.destination.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.data.repository.FavoritesSearchedRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoSuchEntityException;

/*
 * This service is used to find favorites, to add favorites
 *  and to remove favorites for an user
 */
@Service
public class FavoritesService {

	private static final Logger logger = LoggerFactory
			.getLogger(FavoritesService.class);

	@Autowired
	FavoritesSearchedRepository userFavRepository;

	@Autowired
	ContactService contactService;
	
/**
 * This method finds the favorites for an user
 * @param userId
 * @param entityType
 * @param start
 * @param count
 * @return
 * @throws Exception
 */
	public PaginatedResponse findFavoritesFor(String userId, String entityType,
			int start, int count) throws Exception {
		PaginatedResponse favorites = null;

		logger.debug("Starting findFavoritesFor FavoritesService");
		if (EntityType.contains(entityType)) {
			Pageable pageable = new PageRequest(start, count);
			
			Page<UserFavoritesT> userFavorites = userFavRepository
					.findByUserIdAndEntityTypeIgnoreCaseOrderByCreatedDatetimeDesc(
							userId, entityType, pageable);

			if (userFavorites.getContent().isEmpty()) {
				logger.error("NOT_FOUND: No Relevent Data Found in the database");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Favorites found");
			} else {
				prepareFavorites(userFavorites);
				favorites = new PaginatedResponse();
				favorites.setUserFavoritesTs(userFavorites.getContent());
				favorites.setTotalCount(userFavorites.getTotalElements());
				logger.debug("Ending findFavoritesFor FavoritesService");
				return favorites;
			}
		} else {
			logger.error("BAD_REQUEST: No such Entity type exists. Please ensure your entity type.");
			throw new NoSuchEntityException();
		}
	}

	/**
	 * This method is used to add the favorites for an user
	 * @param favorites
	 * @return
	 * @throws Exception
	 */
	public boolean addFavorites(UserFavoritesT favorites) throws Exception {
		logger.debug("Starting addFavorites FavoritesService");
		if (EntityType.contains(favorites.getEntityType())) {
			UserFavoritesT userFavoritesT = null;
			switch (EntityType.valueOf(favorites.getEntityType())) {
			case CUSTOMER:
				logger.debug("Adding Favorites Customer");
				if (favorites.getCustomerId() == null) {
					logger.error("BAD_REQUEST: Customer ID can not be empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Customer ID can not be empty");
				} else {
					userFavoritesT = userFavRepository
							.findByCustomerIdAndUserId(
									favorites.getCustomerId(),
									favorites.getUserId());
					favorites.setConnectId(null);
					favorites.setContactId(null);
					favorites.setDocumentId(null);
					favorites.setOpportunityId(null);
					favorites.setPartnerId(null);
				}
				break;
			case PARTNER:
				logger.debug("Adding Favorites Partner");
				if (favorites.getPartnerId() == null) {
					logger.error("BAD_REQUEST: Partner ID can not be empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Partner ID can not be empty");
				} else {
					userFavoritesT = userFavRepository
							.findByPartnerIdAndUserId(favorites.getPartnerId(),
									favorites.getUserId());
					favorites.setConnectId(null);
					favorites.setContactId(null);
					favorites.setDocumentId(null);
					favorites.setOpportunityId(null);
					favorites.setCustomerId(null);
				}
				break;
			case CONNECT:
				logger.debug("Adding Favorites Connect");
				if (favorites.getConnectId() == null) {
					logger.error("BAD_REQUEST: Connect ID can not be empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Connect ID can not be empty");
				} else {
					userFavoritesT = userFavRepository
							.findByConnectIdAndUserId(favorites.getConnectId(),
									favorites.getUserId());
					favorites.setPartnerId(null);
					favorites.setContactId(null);
					favorites.setDocumentId(null);
					favorites.setOpportunityId(null);
					favorites.setCustomerId(null);
				}
				break;
			case OPPORTUNITY:
				logger.debug("Adding Favorites Opportunity");
				if (favorites.getOpportunityId() == null) {
					logger.error("BAD_REQUEST: Opportunity ID can not be empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Opportunity ID can not be empty");
				} else {
					userFavoritesT = userFavRepository
							.findByOpportunityIdAndUserId(
									favorites.getOpportunityId(),
									favorites.getUserId());
					favorites.setPartnerId(null);
					favorites.setContactId(null);
					favorites.setDocumentId(null);
					favorites.setConnectId(null);
					favorites.setCustomerId(null);
				}
				break;
			case DOCUMENT:
				logger.debug("Adding Favorites Document");
				if (favorites.getDocumentId() == null) {
					logger.error("BAD_REQUEST: Document ID can not be empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Document ID can not be empty");
				} else {
					userFavoritesT = userFavRepository
							.findByDocumentIdAndUserId(
									favorites.getDocumentId(),
									favorites.getUserId());
					favorites.setPartnerId(null);
					favorites.setContactId(null);
					favorites.setOpportunityId(null);
					favorites.setConnectId(null);
					favorites.setCustomerId(null);
				}
				break;

			case CONTACT:
				logger.debug("Adding Favorites Contact");
				if (favorites.getContactId() == null) {
					logger.error("BAD_REQUEST: Contact ID can not be empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Contact ID can not be empty");
				} else {
					userFavoritesT = userFavRepository
							.findByContactIdAndUserId(favorites.getContactId(),
									favorites.getUserId());
					favorites.setPartnerId(null);
					favorites.setOpportunityId(null);
					favorites.setConnectId(null);
					favorites.setCustomerId(null);
					favorites.setDocumentId(null);
				}
				break;

			case TASK:
				logger.debug("Adding Favorites Document");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Saving Task as favorite is not supported!");

			}

			if (userFavoritesT != null) {
				logger.error("BAD_REQUEST: The Entity has already been added to User Favorites");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"The Entity has already been added to User Favorites");
			}

			try {
				logger.debug("Saving the UserFavorite");
				logger.debug("Ending addFavorites FavoritesService");
				return userFavRepository.save(favorites) != null;
			} catch (Exception e) {
				logger.error("BAD_REQUEST" + e.getMessage());
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						e.getMessage());
			}
		}
		logger.error("BAD_REQUEST: Invalid Entity Type");
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"Invalid Entity Type");
	}
	
/**
 * This method is used to remove the favorites for an user
 * @param favoritesId
 * @throws Exception
 */
	public void removeFromFavorites(String favoritesId) throws Exception {
		try {
			logger.debug("Starting removeFromFavorites in FavoritesService");
			userFavRepository.delete(favoritesId);
			logger.debug("Ending removeFromFavorites in FavoritesService");
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR: " + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	/**
	 * This method is used to remove cyclic dependency in UserFavoritesT
	 * 
	 * @param userFavorites
	 * @throws DestinationException
	 */
	private void prepareFavorites(Iterable<UserFavoritesT> userFavorites)
			throws DestinationException {
		logger.debug("Starting prepareFavorites service");
		for (UserFavoritesT userFavoritesT : userFavorites) {
			if (userFavoritesT.getContactT() != null) {
				contactService.removeCyclicForLinkedContactTs(userFavoritesT
						.getContactT());
				contactService.prepareContactDetails(
						userFavoritesT.getContactT(), null);
				userFavoritesT.getContactT().setUserFavoritesTs(null);
			}
			if (userFavoritesT.getCustomerMasterT() != null && userFavoritesT.getCustomerMasterT().isActive()==true) {
				userFavoritesT.getCustomerMasterT().setUserFavoritesTs(null);
				if (userFavoritesT.getCustomerMasterT().getConnectTs() != null) {
					for (ConnectT connectT : userFavoritesT
							.getCustomerMasterT().getConnectTs()) {
						connectT.setUserFavoritesTs(null);
					}
				}
				if (userFavoritesT.getCustomerMasterT().getOpportunityTs() != null) {
					for (OpportunityT opportunityT : userFavoritesT
							.getCustomerMasterT().getOpportunityTs()) {
						opportunityT.setUserFavoritesTs(null);
					}
				}
			}
			if (userFavoritesT.getPartnerMasterT() != null   && userFavoritesT.getPartnerMasterT().isActive()==true) {
				userFavoritesT.getPartnerMasterT().setUserFavoritesTs(null);
				if (userFavoritesT.getPartnerMasterT().getConnectTs() != null) {
					for (ConnectT connectT : userFavoritesT.getPartnerMasterT()
							.getConnectTs()) {
						connectT.setUserFavoritesTs(null);
					}
				}
				if (userFavoritesT.getPartnerMasterT()
						.getOpportunityPartnerLinkTs() != null) {
					for (OpportunityPartnerLinkT opportunityPartnerLinkT : userFavoritesT
							.getPartnerMasterT().getOpportunityPartnerLinkTs()) {
						if (opportunityPartnerLinkT.getOpportunityT() != null) {
							opportunityPartnerLinkT.getOpportunityT()
									.setUserFavoritesTs(null);
						}
					}
				}
			}
		}
		logger.debug("Ending prepareFavorites service");
	}

	/**
	 * This service retrieves for the favorites of a users
	 * 
	 * @param userId
	 * @param page
	 * @param count
	 * @return
	 */
	public PaginatedResponse findFavoritesForUser(String userId, int page, int count) {

		PaginatedResponse favorites = new PaginatedResponse();;
		Page<UserFavoritesT> userFavorites = null;
		
		logger.debug("Starting findFavoritesForUser Favorites Service");
			Pageable pageable = new PageRequest(page, count);
			
			userFavorites = userFavRepository
					.findByUserIdOrderByCreatedDatetimeDesc(userId, pageable);

			if (userFavorites.getContent().isEmpty()) { // If NO favorites are found
				logger.error("NOT_FOUND: No Relevent Data Found in the database");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Favorites found");
			} else { // If favorites exists for the user
				prepareFavorites(userFavorites); // remove cyclic dependency
				favorites.setUserFavoritesTs(userFavorites.getContent()); 
				favorites.setTotalCount(userFavorites.getTotalElements());
				logger.debug("Ending findFavoritesForUser Favorites Service");
			}
			
			return favorites;
	
	}
 
}