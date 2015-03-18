package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.FavoritesSearchedRepository;
import com.tcs.destination.exception.NoDataFoundException;
import com.tcs.destination.exception.NoSuchEntityException;
import com.tcs.destination.utils.Constants;

@Component
public class FavoritesService {

	@Autowired
	FavoritesSearchedRepository userFavRepository;

	public List<UserFavoritesT> findFavoritesFor(
			UserT user,
			String entityType) {
		if (Constants.EntityType.contains(entityType)) {
			List<UserFavoritesT> userFavorites = userFavRepository
					.findByUserTAndEntityTypeIgnoreCase(user,
							entityType);

			if (userFavorites.isEmpty())
				throw new NoDataFoundException();
			return userFavorites;
		} else
			throw new NoSuchEntityException();
	}

}