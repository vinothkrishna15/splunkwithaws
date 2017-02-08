package com.tcs.destination.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CompetitorMappingT;
import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.dto.CompetitorMappingDTO;
import com.tcs.destination.bean.dto.CompetitorOpportunityWrapperDTO;
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
			@RequestParam(value= "nameWith", defaultValue = "") String chars,
			@RequestParam(value = "fields", defaultValue = "competitorName,logo,active") String fields,
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
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody String getListAndNameWith(
			@RequestParam(value= "nameWith", defaultValue = "") String chars,
			@RequestParam(value = "fields", defaultValue = "competitorName,logo") String fields,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "15") int count,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CompetitorController : Start of retrieving the getListAndNameWith");
		PageDTO<CompetitorMappingT> compList;
		try {
			compList = compService.findListByNameContaining(chars, page, count);
			logger.info("Inside CompetitorController : End of retrieving the getListAndNameWith");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, compList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the getListAndNameWith : "
							+ chars);
		}

	}
	
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
	@RequestMapping(value = "/opportunity", method = RequestMethod.GET)
	public @ResponseBody PageDTO<CompetitorMappingDTO> findByNameContainingAndDealDate(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "10") int count,
			@RequestParam(value = "competitors", defaultValue = "") List<String> competitors,
			@RequestParam(value = "fromDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate)
			throws DestinationException {
		logger.info("Inside CompetitorController : Start of retrieving the competitor list");
		PageDTO<CompetitorMappingDTO> compList;
		try {
			 compList = compService.findByNameContainingAndDealDate(competitors, fromDate, toDate, page, count);
			logger.info("Inside CompetitorController : End of retrieving the competitor list");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the competitor ");
		}
		return compList;

	}

	/**
	 * This method is used to retrieve metrics of competitors whose name starts
	 * with value mentioned in nameWith parameter
	 * 
	 * @param nameWith
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/vsOpportunity", method = RequestMethod.GET)
	public @ResponseBody ContentDTO<CompetitorOpportunityWrapperDTO> findMetricsByNameContainingAndDealDate(
			@RequestParam(value = "competitors", defaultValue = "") List<String> competitors,
			@RequestParam(value = "fromDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate)
					throws DestinationException {
		logger.info("Inside CompetitorController : Start of retrieving the competitor list");
		ContentDTO<CompetitorOpportunityWrapperDTO> compList;
		try {
			compList = compService.findMetricsByNameContainingAndDealDate(competitors, fromDate, toDate);
			logger.info("Inside CompetitorController : End of retrieving the competitor list");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the competitor list");
		}
		return compList;
		
	}

}
