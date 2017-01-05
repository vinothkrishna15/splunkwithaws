package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.dto.GeographyMappingDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.GeographyService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This controller deals with the geography related services
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/geography")
public class GeographyController {

	private static final Logger logger = LoggerFactory
			.getLogger(GeographyController.class);

	@Autowired
	GeographyService geographyService;

	/**
	 * This method is used to retrieve all the geography mappings (geography &
	 * display geography)
	 * 
	 * @param fields
	 * @param view
	 * @return geogaraphyMappingTs
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAllActive(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside Geography controller: Start of find");
		try {
			List<GeographyMappingT> geogaraphyMappingTs = geographyService.findAllActive();
			logger.info("Inside Geography controller: End of find");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, geogaraphyMappingTs);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving geography details");
		}
	}

	/**
	 * This method is used to retrieve all the geography mappings (geography &
	 * display geography)
	 * 
	 * @param fields
	 * @param view
	 * @return geogaraphyMappingTs
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody ContentDTO<GeographyMappingDTO> findAll()
					throws DestinationException {
		logger.info("Inside Geography controller: Start of findAll");
		try {
			ContentDTO<GeographyMappingDTO> geogaraphyMappingTs = geographyService.findAll();
			logger.info("Inside Geography controller: End of findAll");
			return geogaraphyMappingTs;
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving geography details");
		}
	}

	/**
	 * This method is used to retrieve all the geography mappings (geography &
	 * display geography)
	 * 
	 * @param fields
	 * @param view
	 * @return geogaraphyMappingTs
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/displayGeo", method = RequestMethod.GET)
	public @ResponseBody ContentDTO<String> findAllDisplayGeo()
			throws DestinationException {
		logger.info("Inside Geography controller: Start of findAll");
		try {
			ContentDTO<String> geogaraphyMappingTs = geographyService.findAllDisplayGeo();
			logger.info("Inside Geography controller: End of findAll");
			return geogaraphyMappingTs;
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving geography details");
		}
	}
}
