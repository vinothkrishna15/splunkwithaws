package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.exception.CustomerNotFoundException;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoDataFoundException;
import com.tcs.destination.exception.NoSuchCurrencyException;
import com.tcs.destination.utils.Constants;

@Component
public class CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;

	public CustomerMasterT findById(String customerid) throws Exception{
		CustomerMasterT customer = customerRepository.findOne(customerid);
		if (customer == null)
			throw new DestinationException(HttpStatus.NOT_FOUND,"No such Customer");
		return customer;
	}

	public List<CustomerMasterT> findTopRevenue(int count, String financialYear) throws Exception{
		if (financialYear.equals("")){
			financialYear = Constants.getCurrentFinancialYear();
		}
		List<CustomerMasterT> topRevenueList = customerRepository
				.findTopRevenue(count, financialYear);
		if (topRevenueList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
		return topRevenueList;
	}

	public List<TargetVsActualResponse> findTargetVsActual(String name,
			String currency) throws Exception{
		BeaconConvertorMappingT beacon = beaconRepository
				.findByCurrencyName(currency);
		if (beacon == null)
			throw new NoSuchCurrencyException();
		List<TargetVsActualResponse> tarActResponseList = new ArrayList<TargetVsActualResponse>();
		String financialYear = Constants.getCurrentFinancialYear();
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
					response.setTarget(new BigDecimal(target[1].toString())
							.divide(beacon.getConversionRate(), 2,
									RoundingMode.HALF_UP));
				}
			}
			tarActResponseList.add(response);
		}
		if (tarActResponseList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
		return tarActResponseList;
	}

	public List<CustomerMasterT> findByNameContaining(String chars) throws Exception {
		List<CustomerMasterT> custList = customerRepository
				.findByCustomerNameIgnoreCaseLike("%" + chars + "%");
		if (custList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,"No such Customer");
		return custList;

	}

	public List<CustomerMasterT> findByGroupCustomerName(String groupCustName) throws Exception {
		List<CustomerMasterT> custList = (List<CustomerMasterT>) customerRepository
				.findByGroupCustomerNameIgnoreCaseLike("%" + groupCustName
						+ "%");
		if (custList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,"No such Customer");
		return custList;
	}

}