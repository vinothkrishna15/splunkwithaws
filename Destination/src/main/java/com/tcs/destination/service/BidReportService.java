package com.tcs.destination.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DateUtils;

@Component
public class BidReportService {

	private static final Logger logger = LoggerFactory
			.getLogger(BidReportService.class);

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	public List<BidDetailsT> getBidDetailedReport(String year, Date fromDate,
			Date toDate,List<String>bidOwner, List<String> currency, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws DestinationException {
		logger.info("Inside getBidDetailedReport Service");
		Date startDate = null;
		Date endDate = null;
		if (!year.equals("")) {
			logger.debug("year is not Empty");
			startDate = DateUtils.getDateFromFinancialYear(year, true);
			endDate = DateUtils.getDateFromFinancialYear(year, false);
		} else {
			startDate = fromDate;
			endDate = toDate;
		}
		if (bidOwner.size() == 0) {
			logger.debug("bidOwner is Empty");
			bidOwner.add("");
		}
		addEmptyValues(iou, geography, country, serviceLines);
		List<BidDetailsT> bidDetailsList = bidDetailsTRepository
				.findByBidDetailsReport(startDate, endDate,bidOwner ,iou, geography,
						country, serviceLines);
		logger.info("Bid details has " + bidDetailsList.size() + " values");
		if (bidDetailsList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		} else {
			for (BidDetailsT bidDetail : bidDetailsList) {
				List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs = bidDetail
						.getOpportunityT()
						.getOpportunityTcsAccountContactLinkTs();
				for (OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT : opportunityTcsAccountContactLinkTs) {
					opportunityTcsAccountContactLinkT.getContactT()
							.setOpportunityTcsAccountContactLinkTs(null);
				}
				OpportunityT opportunityT = beaconConverterService
						.convertOpportunityCurrency(
								bidDetail.getOpportunityT(), currency);
				bidDetail.setOpportunityT(opportunityT);
			}
			return bidDetailsList;
		}
	}

	public void addEmptyValues(List<String> iou, List<String> geography,
			List<String> country, List<String> serviceLines) {
		if (iou.size() == 0) {
			logger.debug("iou is Empty");
			iou.add("");
		}
		if (geography.size() == 0) {
			logger.debug("geography is Empty");
			geography.add("");
		}
		if (country.size() == 0) {
			logger.debug("country is Empty");
			country.add("");
		}
		if (serviceLines.size() == 0) {
			logger.debug("serviceLines is Empty");
			serviceLines.add("");
		}
	}
}
