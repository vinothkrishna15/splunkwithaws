package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.FavoritesSearchedRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoSuchEntityException;
import com.tcs.destination.utils.Constants;

@Component
public class FavoritesService {

	@Autowired
	FavoritesSearchedRepository userFavRepository;

	public List<UserFavoritesT> findFavoritesFor(UserT user, String entityType)
			throws Exception {
		if (Constants.EntityType.contains(entityType)) {
			List<UserFavoritesT> userFavorites = userFavRepository
					.findByUserTAndEntityTypeIgnoreCase(user, entityType);

			if (userFavorites.isEmpty())
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Relevent Data Found in the database");
			return userFavorites;
		} else
			throw new NoSuchEntityException();
	}

	public boolean addFavorites(UserFavoritesT favorites) throws Exception {
		if (Constants.EntityType.contains(favorites.getEntityType())) {
			switch (Constants.EntityType.valueOf(favorites.getEntityType())) {
			case CUSTOMER:
				if (favorites.getCustomerId() == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Customer ID can not be empty");
				}
				break;
			case PARTNER:
				if (favorites.getPartnerId() == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Partner ID can not be empty");
				}
				break;
			case CONNECT:
				if (favorites.getConnectId() == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Connect ID can not be empty");
				}
				break;
			case OPPORTUNITY:
				if (favorites.getOpportunityId() == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Opportunity ID can not be empty");
				}
				break;
			case DOCUMENT:
				if (favorites.getDocumentId() == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Document ID can not be empty");
				}
				break;
			}
			favorites.setCreatedDatetime(Constants.getCurrentTimeStamp());
			try {
				return userFavRepository.save(favorites) != null;
			} catch (Exception e) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						e.getMessage());
			}
		}
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"Invalid Entity Type");
	}

}