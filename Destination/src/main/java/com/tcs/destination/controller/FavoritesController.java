package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.service.FavoritesService;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

	@Autowired
	FavoritesService myFavService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findFavorite(
			@RequestParam("entityType") String entityType,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		List<UserFavoritesT> userFavourites = myFavService.findFavoritesFor(
				Constants.getCurrentUserDetails(), entityType);
		return Constants.filterJsonForFieldAndViews(fields, view,
				userFavourites);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addFavorite(
			@RequestBody UserFavoritesT favorites,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		if(myFavService.addFavorites(favorites)){
			status.setStatus(Status.SUCCESS, favorites.getUserFavoritesId());
		}


		return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews(
				"all", "", status), HttpStatus.OK);
	}
}
