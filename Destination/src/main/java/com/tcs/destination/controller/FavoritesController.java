package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.FavoritesService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

	private static final Logger logger = LoggerFactory
			.getLogger(FavoritesController.class);

	@Autowired
	FavoritesService myFavService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findFavorite(
			@RequestParam("entityType") String entityType,
			@RequestParam("page") int page,
			@RequestParam("count") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside FavoritesController /favorites?entityType="
				+ entityType + " GET");
		if(page<0 && count <0){
			throw new DestinationException(HttpStatus.BAD_REQUEST,"Invalid pagination request");
		}
		List<UserFavoritesT> userFavourites = myFavService.findFavoritesFor(
				DestinationUtils.getCurrentUserDetails(), entityType, page,count);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				userFavourites);
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addFavorite(
			@RequestBody UserFavoritesT favorites,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside FavoritesController /favorites POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		if (myFavService.addFavorites(favorites)) {
			logger.debug("User FavoritesId" + favorites.getUserFavoritesId()
					+ "Inserted Successfully");
			status.setStatus(Status.SUCCESS, favorites.getUserFavoritesId());
		}
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(
				"all", "", status), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody String removeFromFavorites(
			@RequestParam(value = "userFavoritesId") String favoritesId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside FavoritesController /favorites?userFavoritesId="+favoritesId+" DELETE");
		Status status = new Status();
		myFavService.removeFromFavorites(favoritesId);
		status.setStatus(Status.SUCCESS, favoritesId);
		return ResponseConstructors.filterJsonForFieldAndViews("all", "", status);

	}
}
