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
import com.tcs.destination.bean.UserT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.FavoritesService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This controller handles the favourites module
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/favorites")
public class FavoritesController {

	private static final Logger logger = LoggerFactory
			.getLogger(FavoritesController.class);

	@Autowired
	FavoritesService myFavService;

	/**
	 * This method is used to get the favourites for respective entity type
	 * given
	 * 
	 * @param entityType
	 * @param page
	 * @param count
	 * @param fields
	 * @param view
	 * @return favourites
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findFavorite(
			@RequestParam("entityType") String entityType,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "100") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		UserT userT = DestinationUtils.getCurrentUserDetails();
		logger.info("Inside favorites controller : Start of retrieving the favourites");
		try {
			if (page < 0 && count < 0) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid pagination request");
			}
			PaginatedResponse favourites = myFavService.findFavoritesFor(
					userT, entityType, page, count);
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

	/**
	 * This method is used to create a new favourite
	 * 
	 * @param favorites
	 * @param fields
	 * @param view
	 * @return status
	 * @throws DestinationException
	 */
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

	/**
	 * This method removes the favourite
	 * 
	 * @param favoritesId
	 * @param fields
	 * @param view
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody String removeFromFavorites(
			@RequestParam(value = "userFavoritesId") String favoritesId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside favorites controller : Start of delete Favourites");
		Status status = new Status();
		try {
			myFavService.removeFromFavorites(favoritesId);
			status.setStatus(Status.SUCCESS, favoritesId);
			logger.info("Inside favorites controller : End of delete Favourites");
			return ResponseConstructors.filterJsonForFieldAndViews("all", "",
					status);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while removing the favourite :"
							+ favoritesId);
		}

	}
	
	/**
	 * This method is used to get the favorites for user
	 * 
	 * @param entityType
	 * @param page
	 * @param count
	 * @param fields
	 * @param view
	 * @return favorites
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public @ResponseBody String findFavoritesForUser(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "10") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		UserT userT = DestinationUtils.getCurrentUserDetails();
		logger.info("Inside favorites controller : Start of retrieving the favourites for user");
		try {
			// if page and count are negative
			if (page < 0 && count < 0) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid pagination request");
			}
			// calling service
			PaginatedResponse favourites = myFavService.findFavoritesForUser(userT, page, count);
			logger.info("Inside favorites controller : End of retrieving the favourites for user");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, favourites);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the favourites for " + userT.getUserId());
		}
	}

}
