package com.tcs.destination.service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectSummaryResponse;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DateUtils;

@Component
public class ConnectReportService {

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectReportService.class);

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	BidReportService bidReportService;

	public List<ConnectT> getConnectDetailedReports(String month,
			String quarter, String year, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws Exception {
		logger.info("Inside getConnectDetailedReports Service");
		bidReportService.addEmptyValues(iou, geography, country, serviceLines);
		Date fromDate = DateUtils.getDate(month, quarter, year, true);
		Date toDate = DateUtils.getDate(month, quarter, year, false);
		List<ConnectT> connectList = connectRepository.findByConnectReport(
				new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), iou, geography, country,
				serviceLines);
		if (connectList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		} else {
			return connectList;
		}
	}

	public List<ConnectSummaryResponse> getSummaryReports(String required,
			String month, String quarter, String year, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws Exception {
		logger.info("Inside searchForReports Service");
		bidReportService.addEmptyValues(iou, geography, country, serviceLines);
		Date fromDate = DateUtils.getDate(month, quarter, year, true);
		Date toDate = DateUtils.getDate(month, quarter, year, false);
		List<ConnectSummaryResponse> connectSummaryResponses = new ArrayList<ConnectSummaryResponse>();
		switch (required) {
		case "subSp":
			connectSummaryResponses = getConnectSummaryReportsBySubSp(fromDate,
					toDate, iou, geography, country, serviceLines);
			break;
		case "geography":
			connectSummaryResponses = getConnectSummaryReportsByGeography(
					fromDate, toDate, iou, geography, country, serviceLines);
			break;
		case "iou":
			connectSummaryResponses = getConnectSummaryReportsByIou(fromDate,
					toDate, iou, geography, country, serviceLines);
			break;
		default:
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid URL");
		}
		return connectSummaryResponses;
	}

	public List<ConnectSummaryResponse> getConnectSummaryReportsBySubSp(
			Date fromDate, Date toDate, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws Exception {
		logger.info("Inside getConnectSummaryReportsBySubSp Service");
		List<ConnectSummaryResponse> connectSummaryResponses = new ArrayList<ConnectSummaryResponse>();
		List<Object[]> subSpConnectCountList = connectRepository
				.findBySubSpConnectSummaryReport(
						new Timestamp(fromDate.getTime()),
						new Timestamp(toDate.getTime()), iou, geography,
						country, serviceLines);
		if (subSpConnectCountList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		} else {
			for (Object[] subSpConnectCount : subSpConnectCountList) {
				ConnectSummaryResponse connectSummaryResponse = new ConnectSummaryResponse();
				connectSummaryResponse
						.setConnectCount((BigInteger) subSpConnectCount[0]);
				connectSummaryResponse
						.setRowLabel((String) subSpConnectCount[1]);
				connectSummaryResponses.add(connectSummaryResponse);
			}
			return connectSummaryResponses;
		}
	}

	public List<ConnectSummaryResponse> getConnectSummaryReportsByGeography(
			Date fromDate, Date toDate, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws Exception {
		logger.info("Inside getConnectSummaryReportsByGeography Service");
		List<ConnectSummaryResponse> connectSummaryResponses = new ArrayList<ConnectSummaryResponse>();
		List<Object[]> geographyConnectCountList = connectRepository
				.findByGeographyConnectSummaryReport(
						new Timestamp(fromDate.getTime()),
						new Timestamp(toDate.getTime()), iou, geography,
						country, serviceLines);
		if (geographyConnectCountList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		} else {
			for (Object[] geographyConnectCount : geographyConnectCountList) {
				ConnectSummaryResponse connectSummaryResponse = new ConnectSummaryResponse();
				connectSummaryResponse
						.setConnectCount((BigInteger) geographyConnectCount[0]);
				connectSummaryResponse
						.setRowLabel((String) geographyConnectCount[1]);
				connectSummaryResponses.add(connectSummaryResponse);
			}
			return connectSummaryResponses;
		}
	}

	public List<ConnectSummaryResponse> getConnectSummaryReportsByIou(
			Date fromDate, Date toDate, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws Exception {
		logger.info("Inside getConnectSummaryReportsByIou Service");
		List<ConnectSummaryResponse> connectSummaryResponses = new ArrayList<ConnectSummaryResponse>();
		List<Object[]> iouConnectCountList = connectRepository
				.findByIouConnectSummaryReport(
						new Timestamp(fromDate.getTime()),
						new Timestamp(toDate.getTime()), iou, geography,
						country, serviceLines);
		if (iouConnectCountList.isEmpty()) {
			logger.error("NOT_FOUND:No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		} else {
			for (Object[] iouConnectCount : iouConnectCountList) {
				ConnectSummaryResponse connectSummaryResponse = new ConnectSummaryResponse();
				connectSummaryResponse
						.setConnectCount((BigInteger) iouConnectCount[0]);
				connectSummaryResponse.setRowLabel((String) iouConnectCount[1]);
				connectSummaryResponses.add(connectSummaryResponse);
			}
			return connectSummaryResponses;
		}
	}
}
