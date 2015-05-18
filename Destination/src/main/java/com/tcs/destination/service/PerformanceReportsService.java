package com.tcs.destination.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.GeographyReport;
import com.tcs.destination.bean.IOUReport;
import com.tcs.destination.bean.SubSpReport;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.PerformanceReportsRepository;
import com.tcs.destination.exception.NoSuchCurrencyException;

@Component
public class PerformanceReportsService {

	private static final Logger logger = LoggerFactory
			.getLogger(PerformanceReportsService.class);

	@Autowired
	private PerformanceReportsRepository perfRepo;
	
	@Autowired
	BeaconConvertorRepository beaconRepository;

	public List<IOUReport> getRevenuesByIOU(String financialYear,
			String quarter, String geography, String serviceLine, String currency)
			throws Exception {
		
		BeaconConvertorMappingT beacon = beaconRepository
				.findByCurrencyName(currency);
		if (beacon == null) {
			logger.error("No Such Currency Exception");
			throw new NoSuchCurrencyException();
		}
		
		List<Object[]> iouObjList = new ArrayList<Object[]>();
		iouObjList = perfRepo.getRevenuesByIOU(financialYear, quarter,
				geography, serviceLine);
		List<IOUReport> iouRevenuesList = new ArrayList<IOUReport>();
		
		for(Object[] obj : iouObjList){
			IOUReport item = new IOUReport();
			item.setDisplayIOU((String)obj[0]);
			BigDecimal rev = new BigDecimal(obj[1].toString());
			//BigDecimal revenue = (BigDecimal) obj[1];
			//revenue = revenue.setScale(2, BigDecimal.ROUND_DOWN);
			item.setActualRevenue(rev.divide(beacon.getConversionRate() ,2).setScale(2, BigDecimal.ROUND_DOWN));
			//item.setActualRevenue(rev.setScale(2, BigDecimal.ROUND_DOWN));
			iouRevenuesList.add(item);
		}
		return iouRevenuesList;
	}
		
		public List<SubSpReport> getRevenuesBySubSp(String financialYear,String quarter,String geography,String customerName,String iou,String currency)
				throws Exception {
			
			BeaconConvertorMappingT beacon = beaconRepository
					.findByCurrencyName(currency);
			if (beacon == null) {
				logger.error("No Such Currency Exception");
				throw new NoSuchCurrencyException();
			}
			
			List<Object[]> subObjList = new ArrayList<Object[]>();
			subObjList = perfRepo.getRevenuesBySubSp(financialYear, quarter,
					geography, customerName, iou);
			List<SubSpReport> subSpRevenuesList = new ArrayList<SubSpReport>();
			
			for(Object[] obj : subObjList){
				SubSpReport item = new SubSpReport();
				item.setDisplaySubSp((String)obj[0]);
				BigDecimal rev = new BigDecimal(obj[1].toString());
				//BigDecimal revenue = (BigDecimal) obj[1];
				//revenue = revenue.setScale(2, BigDecimal.ROUND_DOWN);
				item.setActualRevenue(rev.divide(beacon.getConversionRate() ,2).setScale(2, BigDecimal.ROUND_DOWN));
				//item.setActualRevenue(rev.setScale(2, BigDecimal.ROUND_DOWN));
				subSpRevenuesList.add(item);
			}
			return subSpRevenuesList;
	}
		
		public List<GeographyReport> getRevenuesByDispGeography(String financialYear,String quarter,String customer,String subSp,String iou,String currency)
				throws Exception {
			
			BeaconConvertorMappingT beacon = beaconRepository
					.findByCurrencyName(currency);
			if (beacon == null) {
				logger.error("No Such Currency Exception");
				throw new NoSuchCurrencyException();
			}
			
			List<Object[]> geoObjList = new ArrayList<Object[]>();
			geoObjList = perfRepo.getRevenuesByDispGeo(financialYear, quarter,
					customer, subSp, iou);
			List<GeographyReport> geoRevenuesList = new ArrayList<GeographyReport>();
			
			for(Object[] obj : geoObjList){
				GeographyReport item = new GeographyReport();
				item.setGeography((String)obj[0]);
				BigDecimal rev = new BigDecimal(obj[1].toString());
				//BigDecimal revenue = (BigDecimal) obj[1];
				//revenue = revenue.setScale(2, BigDecimal.ROUND_DOWN);
				item.setActualRevenue(rev.divide(beacon.getConversionRate() ,2).setScale(2, BigDecimal.ROUND_DOWN));
				//item.setActualRevenue(rev.setScale(2, BigDecimal.ROUND_DOWN));
				geoRevenuesList.add(item);
			}
			return geoRevenuesList;
	}	
		
		
		public List<GeographyReport> getRevenuesBySubGeography(String financialYear,String quarter,String customer,String subSp,String iou,String geography,String currency)
				throws Exception {
			
			BeaconConvertorMappingT beacon = beaconRepository
					.findByCurrencyName(currency);
			if (beacon == null) {
				logger.error("No Such Currency Exception");
				throw new NoSuchCurrencyException();
			}
			
			List<Object[]> geoObjList = new ArrayList<Object[]>();
			geoObjList = perfRepo.getRevenuesBySubGeo(financialYear, quarter,
					customer, subSp, iou, geography);
			List<GeographyReport> geoRevenuesList = new ArrayList<GeographyReport>();
			
			for(Object[] obj : geoObjList){
				GeographyReport item = new GeographyReport();
				item.setGeography((String)obj[0]);
				BigDecimal rev = new BigDecimal(obj[1].toString());
				//BigDecimal revenue = (BigDecimal) obj[1];
				//revenue = revenue.setScale(2, BigDecimal.ROUND_DOWN);
				item.setActualRevenue(rev.divide(beacon.getConversionRate() ,2).setScale(2, BigDecimal.ROUND_DOWN));
				//item.setActualRevenue(rev.setScale(2, BigDecimal.ROUND_DOWN));
				geoRevenuesList.add(item);
			}
			return geoRevenuesList;
	}	
		
		
}
