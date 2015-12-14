package com.tcs.destination.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ConnectTypeMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ConnectTypeService;
import com.tcs.destination.utils.ResponseConstructors;
/**
 * 
 * This Controller is used to handle ConnectType searches.
 *
 */
@RestController
@RequestMapping("/connecttype")
public class ConnectTypeController {

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectTypeController.class);

	@Autowired
	ConnectTypeService conTypeService;

	/**
	 * This method is used to get the connect type mapping
	 * 
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside connectTypeController: Start of retrieving the Connect Type Mapping");
		try {
			ArrayList<ConnectTypeMappingT> connectTypeMapping = (ArrayList<ConnectTypeMappingT>) conTypeService
					.findAll();
			logger.info("Inside connectTypeController: End of retrieving the Connect Type Mapping");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, connectTypeMapping);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the connect type mapping");
		}
	}

}
