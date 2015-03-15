package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.RevenuesResponse;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.exception.CustomerNotFoundException;
import com.tcs.destination.exception.NoDataFoundException;

@Component
public class CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;

	public CustomerMasterT findById(String customerid) {
		CustomerMasterT customer = customerRepository.findOne(customerid);
		if (customer == null)
			throw new CustomerNotFoundException();
		return customer;
	}

	public List<RevenuesResponse> findTop10Customers() {
		List<CustomerMasterT> top10Customers = customerRepository
				.findTop10RevenueCustomers();
		List<RevenuesResponse> revenueList = new ArrayList<RevenuesResponse>();
		for (CustomerMasterT customer : top10Customers) {
			RevenuesResponse revenue = new RevenuesResponse();
			revenue.setCustomerName(customer.getCustomerName());
			revenue.setGroupCustomerName(customer.getGroupCustomerName());
			revenue.setLogo(customer.getLogo());
			revenueList.add(revenue);
		}
		if (revenueList.isEmpty())
			throw new NoDataFoundException();
		return revenueList;
	}

	public List<TargetVsActualResponse> findTargetVsActual(String name) {
		BeaconConvertorMappingT beacon = beaconRepository
				.findByCurrencyName("USD");
		List<TargetVsActualResponse> tarActResponseList = new ArrayList<TargetVsActualResponse>();
		String financialYear = "FY'";
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.MONTH) > 3) {
			financialYear += cal.get(Calendar.YEAR)
					+ "-"
					+ String.valueOf(cal.get(Calendar.YEAR) + 1)
							.substring(2, 4);
		} else {
			financialYear += (cal.get(Calendar.YEAR) - 1) + "-"
					+ String.valueOf(cal.get(Calendar.YEAR)).subSequence(2, 4);
		}
		System.out.println("Financial Year: " + financialYear);
		List<Object[]> actualList = customerRepository.findActual(name,
				financialYear);
		List<Object[]> targetList = customerRepository.findTarget(name,
				financialYear);
		for (Object[] actual : actualList) {
			TargetVsActualResponse response = new TargetVsActualResponse();
			response.setQuarter(actual[0].toString());
			response.setActual(new BigDecimal(actual[1].toString())
					.divide(beacon.getConversionRate(), 2, RoundingMode.HALF_UP));
			for (Object[] target : targetList) {
				if (target[0].toString().equals(response.getQuarter())) {
					response.setTarget(new BigDecimal(target[1].toString())
							.divide(beacon.getConversionRate(), 2, RoundingMode.HALF_UP));
				}
			}
			tarActResponseList.add(response);
		}
		if (tarActResponseList.isEmpty())
			throw new NoDataFoundException();
		return tarActResponseList;
	}

}