package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.MyFavorites;
import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.controller.UserRepositoryUserDetailsService.UserRepositoryUserDetails;
import com.tcs.destination.exception.NoSuchEntityException;
import com.tcs.destination.service.FavoritesService;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

	@Autowired
	FavoritesService myFavService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findFavorite(
			@RequestParam("entityType") String entityType,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {

		Authentication a = SecurityContextHolder.getContext()
				.getAuthentication();
		List<UserFavoritesT> userFavourites = myFavService.findFavoritesFor(
				((UserRepositoryUserDetails) a.getPrincipal()), entityType);
		return Constants.filterJsonForFieldAndViews(fields, view,
				userFavourites);
	}
}
