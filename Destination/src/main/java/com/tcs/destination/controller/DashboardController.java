package com.tcs.destination.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.FileNameMap;
import java.net.URLConnection;

import javax.activation.FileDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.bean.PerformaceChartBean;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DashBoardService;
import com.tcs.destination.service.DocumentService;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	DashBoardService dashboardService;

	private static final Logger logger = LoggerFactory
			.getLogger(DashboardController.class);

	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public String chart(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "financialYear", defaultValue = "") String financialYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		PerformaceChartBean chartValues = dashboardService.getChartValues(
				userId, financialYear);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				chartValues);
	}

}
