package com.tcs.destination.controller;

import java.util.Date;

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

import com.tcs.destination.bean.dto.CarouselMetricsDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CarouselService;
/**
 * This controller handles the carousel related services
 * @author TCS
 *
 */
@RestController
@RequestMapping("/carousel")
public class CarouselController {
	
	private static final Logger logger = LoggerFactory
			.getLogger(CarouselController.class);
	
	@Autowired
	CarouselService carouselService;
	/**
	 * gets the carousel metrics values based on the type 
	 * @param type
	 * @param fromDate
	 * @param toDate
	 * @param associateCount
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody CarouselMetricsDTO getCarouselMetrics(
			@RequestParam(value = "type", defaultValue = "ALL") String type,
			@RequestParam(value = "fromDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "associateCount", defaultValue = "100") Long associateCount)
			throws DestinationException {
		logger.info("Inside CarouselController: Start of getCarouselMetrics");
		CarouselMetricsDTO carouselMetrics;
		try {
			carouselMetrics = carouselService.getCarouselMetrics(
					type, fromDate,toDate, associateCount);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Carousel Metrics");
		}
		logger.info("Inside CarouselController: end of getCarouselMetrics");
		return carouselMetrics;
	}
	
}
