package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.FavoritesSearchedRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoSuchEntityException;
import com.tcs.destination.utils.DateUtils;

@Component
public class FavoritesService {

	private static final Logger logger = LoggerFactory
			.getLogger(FavoritesService.class);

	@Autowired
	FavoritesSearchedRepository userFavRepository;

	public List<UserFavoritesT> findFavoritesFor(UserT user, String entityType, int start, int count)
			throws Exception {
		logger.debug("Inside findFavoritesFor Service");
		if (EntityType.contains(entityType)) {
			logger.debug("EntityType is present");
			Pageable pageable=new PageRequest(start, count);
			List<UserFavoritesT> userFavorites = userFavRepository
					.findByUserTAndEntityTypeIgnoreCase(user, entityType,pageable);

			if (userFavorites.isEmpty()) {
				logger.error("NOT_FOUND: No Relevent Data Found in the database");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Relevent Data Found in the database");
			}
			return userFavorites;
		} else {
			logger.error("BAD_REQUEST: No such Entity type exists. Please ensure your entity type.");
			throw new NoSuchEntityException();
		}
	}

	public boolean addFavorites(UserFavoritesT favorites) throws Exception {
		logger.debug("Inside addFavorites Service");
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
					userFavoritesT = userFavRepository.findByCustomerIdAndUserId(favorites.getCustomerId(), favorites.getUserId());
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
					userFavoritesT = userFavRepository.findByPartnerIdAndUserId(favorites.getPartnerId(), favorites.getUserId());
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
					userFavoritesT = userFavRepository.findByConnectIdAndUserId(favorites.getConnectId(), favorites.getUserId());
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
					userFavoritesT = userFavRepository.findByOpportunityIdAndUserId(favorites.getOpportunityId(), favorites.getUserId());
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
					userFavoritesT = userFavRepository.findByDocumentIdAndUserId(favorites.getDocumentId(), favorites.getUserId());
					favorites.setPartnerId(null);
					favorites.setContactId(null);
					favorites.setOpportunityId(null);
					favorites.setConnectId(null);
					favorites.setCustomerId(null);
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
			
			favorites.setCreatedDatetime(DateUtils.getCurrentTimeStamp());
			try {
				logger.debug("Saving the UserFavorite");
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

	public void removeFromFavorites(String favoritesId) throws Exception {
		try{
			userFavRepository.delete(favoritesId);
		} catch(Exception e){
			logger.error("INTERNAL_SERVER_ERROR: "+e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

}