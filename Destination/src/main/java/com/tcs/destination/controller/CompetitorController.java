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

import com.tcs.destination.bean.CompetitorMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CompetitorService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This Controller is used to handle Competitor related details
 * 
 */
@RestController
@RequestMapping("/competitor")
public class CompetitorController {

	private static final Logger logger = LoggerFactory
			.getLogger(CompetitorController.class);

	@Autowired
	CompetitorService compService;

	/**
	 * This method is used to retrieve list of competitors whose name starts
	 * with value mentioned in nameWith parameter
	 * 
	 * @param nameWith
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findNameWith(
			@RequestParam("nameWith") String chars,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CompetitorController : Start of retrieving the competitor list");
		List<CompetitorMappingT> compList;
		try {
			compList = compService.findByNameContaining(chars);
			logger.info("Inside CompetitorController : End of retrieving the competitor list");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, compList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the competitor name for : "
							+ chars);
		}

	}

}
