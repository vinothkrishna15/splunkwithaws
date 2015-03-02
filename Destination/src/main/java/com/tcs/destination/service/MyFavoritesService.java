package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.MyFavorites;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.FavoritesSearchedRepository;

@Component
public class MyFavoritesService {

	@Autowired
	FavoritesSearchedRepository userFavRepository;

	public List<MyFavorites> findFavoritesFor(String userId) {
		UserT usert = new UserT();
		usert.setUserId(userId);
		List<UserFavoritesT> userFavorites = userFavRepository
				.findByUserT(usert);
		List<MyFavorites> favorites = new ArrayList<MyFavorites>();

		for (UserFavoritesT userFav : userFavorites) {
			MyFavorites favorite = new MyFavorites();
			if (userFav.getCustomerMasterT() != null) {
				CustomerMasterT customer = userFav.getCustomerMasterT();
				favorite.setId(customer.getCustomerId());
				favorite.setName(customer.getCustomerName());
				favorite.setLogo(customer.getLogo());
				favorite.setGeography(customer.getGeographyMappingT());
				favorites.add(favorite);
			} else if (userFav.getPartnerMasterT() != null) {
				PartnerMasterT partner = new PartnerMasterT();
				favorite.setId(partner.getPartnerId());
				favorite.setName(partner.getPartnerName());
				favorite.setLogo(partner.getLogo());
				favorite.setGeography(partner.getGeographyMappingT());
				favorites.add(favorite);
			}
		}
		return favorites;
	}

}