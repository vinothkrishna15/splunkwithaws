package com.tcs.destination.controller;

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

import com.tcs.destination.bean.PaginatedResponse;
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
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "100") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.info("Inside favorites controller : Start of retrieving the favourites");
		try {
			if (page < 0 && count < 0) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid pagination request");
			}
			PaginatedResponse favourites = myFavService.findFavoritesFor(
					userId, entityType, page, count);
			logger.info("Inside favorites controller : End of retrieving the favourites");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, favourites);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the favourites for "
							+ entityType);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addFavorite(
			@RequestBody UserFavoritesT favorites,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside favorites controller : Start of adding user favourites");
		try {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			favorites.setUserId(userId);
			Status status = new Status();
			status.setStatus(Status.FAILED, "");
			if (myFavService.addFavorites(favorites)) {
				logger.debug("User FavoritesId"
						+ favorites.getUserFavoritesId()
						+ "Inserted Successfully");
				status.setStatus(Status.SUCCESS, favorites.getUserFavoritesId());
			}
			logger.info("Inside favorites controller : End of adding user favourites");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in adding the favourites");
		}
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody String removeFromFavorites(
			@RequestParam(value = "userFavoritesId") String favoritesId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside FavoritesController /favorites?userFavoritesId="
				+ favoritesId + " DELETE");
		logger.info("Start of delete Favourites");
		Status status = new Status();
		try {
			myFavService.removeFromFavorites(favoritesId);
			status.setStatus(Status.SUCCESS, favoritesId);
			logger.info("End of delete Favourites");
			return ResponseConstructors.filterJsonForFieldAndViews("all", "",
					status);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the chart values");
		}

	}

}
