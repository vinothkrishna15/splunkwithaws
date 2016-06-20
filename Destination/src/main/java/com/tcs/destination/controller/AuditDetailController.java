package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.AuditHistoryDTO;
import com.tcs.destination.bean.AuditHistoryResponseDTO;
import com.tcs.destination.bean.AuditOpportunityHistoryDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.AuditDetailService;

/**
 * 
 * Controller to handle BDM related details
 *
 */
@RestController
@RequestMapping("/history")
public class AuditDetailController {

	private static final Logger logger = LoggerFactory
			.getLogger(AuditDetailController.class);

	@Autowired
	AuditDetailService auditDetailService;

	@RequestMapping(value = "/workflow", method = RequestMethod.GET)
	public @ResponseBody AuditHistoryResponseDTO<AuditHistoryDTO> getWorkFlowHistory(
			@RequestParam(value = "id") String workflowId)
			throws DestinationException {
		logger.info("Inside AuditDetailController : Start of getWorkFlowHistory");

		try {
			return auditDetailService.getWorkFlowHistory(workflowId);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Erron on getWorkFlowHistory", e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Work Flow History");
		}
	}

	@RequestMapping(value = "/opportunity", method = RequestMethod.GET)
	public @ResponseBody AuditHistoryResponseDTO<AuditOpportunityHistoryDTO> getOpportunityHistory(
			@RequestParam(value = "id") String oppId)
					throws DestinationException {
		logger.info("Inside AuditDetailController : Start of getOpportunityHistory");
		
		try {
			return auditDetailService.getOpportunityHistory(oppId);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Erron on getOpportunityHistory", e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Opportunity History");
		}
	}



}
