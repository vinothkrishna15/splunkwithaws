package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import scala.Array;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoSuchCurrencyException;
import com.tcs.destination.utils.DateUtils;

@Component
public class CustomerService {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerService.class);

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;

	public CustomerMasterT findById(String customerid) throws Exception {
		logger.debug("Inside findById Service");
		CustomerMasterT customerMasterT = customerRepository
				.findOne(customerid);
		if (customerMasterT == null) {
			logger.error("NOT_FOUND: No such Customer");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No such Customer");
		}
		removeCyclicForLinkedContactTs(customerMasterT);
		return customerMasterT;
	}

	public List<CustomerMasterT> findTopRevenue(int count, String financialYear)
			throws Exception {
		logger.debug("Inside findTopRevenue Service");
		if (financialYear.equals("")) {
			logger.debug("Financial Year is Empty");
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<CustomerMasterT> topRevenueList = customerRepository
				.findTopRevenue(count, financialYear);
		if (topRevenueList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		}
		removeCyclicForLinkedContactTs(topRevenueList);
		return topRevenueList;
	}

	public List<TargetVsActualResponse> findTargetVsActual(String name,
			String currency, String financialYear) throws Exception {
		logger.debug("Inside findTargetVsActual Service");
		BeaconConvertorMappingT beacon = beaconRepository
				.findByCurrencyName(currency);
		if (beacon == null) {
			logger.error("No Such Currency Exception");
			throw new NoSuchCurrencyException();
		}
		List<TargetVsActualResponse> tarActResponseList = new ArrayList<TargetVsActualResponse>();
		if (financialYear.equals("")) {
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<Object[]> actualList = customerRepository.findActual(name,
				financialYear);
		List<Object[]> targetList = customerRepository.findTarget(name,
				financialYear);
		for (Object[] actual : actualList) {
			TargetVsActualResponse response = new TargetVsActualResponse();
			response.setQuarter(actual[0].toString());
			response.setActual(new BigDecimal(actual[1].toString()).divide(
					beacon.getConversionRate(), 2, RoundingMode.HALF_UP));
			for (Object[] target : targetList) {
				if (target[0].toString().equals(response.getQuarter())) {
					logger.debug("Target Equals Quarter");
					response.setTarget(new BigDecimal(target[1].toString())
							.divide(beacon.getConversionRate(), 2,
									RoundingMode.HALF_UP));
				}
			}
			tarActResponseList.add(response);
		}
		if (actualList.isEmpty()) {
			for (Object[] target : targetList) {
				TargetVsActualResponse response = new TargetVsActualResponse();
				response.setQuarter(target[0].toString());
				response.setTarget(new BigDecimal(target[1].toString()).divide(
						beacon.getConversionRate(), 2, RoundingMode.HALF_UP));
				tarActResponseList.add(response);
			}
		}
		if (tarActResponseList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		}
		return tarActResponseList;
	}

	public List<CustomerMasterT> findByNameContaining(String nameWith)
			throws Exception {
		List<CustomerMasterT> custList = customerRepository
				.findByCustomerNameIgnoreCaseContainingOrderByCustomerNameAsc(nameWith);
		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: No such Customer");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Customer found");
		}
		removeCyclicForLinkedContactTs(custList);
		return custList;
	}

	public List<CustomerMasterT> findByGroupCustomerName(String groupCustName)
			throws Exception {
		logger.debug("Inside findByGroupCustomerName Service");
		List<CustomerMasterT> custList = customerRepository
				.findByGroupCustomerNameIgnoreCaseContainingOrderByGroupCustomerNameAsc(groupCustName);
		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: No such Customer");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Customer found");
		}
		removeCyclicForLinkedContactTs(custList);
		return custList;
	}

	public List<CustomerMasterT> findByNameStarting(String startsWith)
			throws Exception {
		logger.error("Starts With" + startsWith);
		List<CustomerMasterT> custList = new ArrayList<CustomerMasterT>();
		if (!startsWith.equals("@"))
			custList.addAll(customerRepository
					.findByCustomerNameIgnoreCaseStartingWithOrderByCustomerNameAsc(startsWith));
		else
			for (int i = 0; i <= 9; i++) {
				List<CustomerMasterT> customerMasterTs = customerRepository
						.findByCustomerNameIgnoreCaseStartingWithOrderByCustomerNameAsc(i
								+ "");
				custList.addAll(customerMasterTs);
			}

		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: No such Customer");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Customer found");
		}
		removeCyclicForLinkedContactTs(custList);
		return custList;
	}

	private void removeCyclicForLinkedContactTs(
			List<CustomerMasterT> customerMasterTs) {
		if (customerMasterTs != null) {
			for (CustomerMasterT customerMasterT : customerMasterTs) {
				removeCyclicForLinkedContactTs(customerMasterT);
			}
		}
	}

	private void removeCyclicForLinkedContactTs(CustomerMasterT customerMasterT) {
		if (customerMasterT != null) {
			if (customerMasterT.getContactCustomerLinkTs() != null) {
				for (ContactCustomerLinkT contactCustomerLinkT : customerMasterT
						.getContactCustomerLinkTs()) {
					contactCustomerLinkT.getContactT()
							.setContactCustomerLinkTs(null);
				}
			}
		}
	}

}