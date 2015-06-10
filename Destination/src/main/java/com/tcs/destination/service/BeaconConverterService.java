package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.OpportunityDealValue;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.exception.DestinationException;

@Component
public class BeaconConverterService {

	public static final String ACTUALS_REVENUE_CURRENCY = "INR";

	public static final String TARGET_REVENUE_CURRENCY = "INR";

	@Autowired
	BeaconConvertorRepository converterRepository;

	public BigDecimal convert(String base, String target, double value)
			throws DestinationException {

		BigDecimal sourceVal = BigDecimal.valueOf(value);
		BigDecimal convertedVal = null;
		Status status = new Status();
		status.setStatus("FAILED", "Currency Conversion Failed");
		BeaconConvertorMappingT converterBase = converterRepository
				.findByCurrencyName(base);
		if (converterBase != null)
			sourceVal = converterBase.getConversionRate().multiply(sourceVal);
		else
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Currency Type " + base + " Not Found");

		BeaconConvertorMappingT converterTarget = converterRepository
				.findByCurrencyName(target);
		if (converterTarget != null) {
			convertedVal = sourceVal.divide(
					converterTarget.getConversionRate(), 2,
					RoundingMode.HALF_UP);
			status.setStatus("SUCCESS", convertedVal.toString());
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Currency Type " + target + " Not Found");
		}
		return convertedVal;
	}

	public BigDecimal convert(String base, String target, BigDecimal value)
			throws DestinationException {

		BigDecimal sourceVal = value;
		BigDecimal convertedVal = null;
		Status status = new Status();
		status.setStatus("FAILED", "Currency Conversion Failed");
		BeaconConvertorMappingT converterBase = converterRepository
				.findByCurrencyName(base);
		if (converterBase != null)
			sourceVal = converterBase.getConversionRate().multiply(sourceVal);
		else
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Currency Type " + base + " Not Found");

		BeaconConvertorMappingT converterTarget = converterRepository
				.findByCurrencyName(target);
		if (converterTarget != null) {
			convertedVal = sourceVal.divide(
					converterTarget.getConversionRate(), 2,
					RoundingMode.HALF_UP);
			status.setStatus("SUCCESS", convertedVal.toString());
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Currency Type " + target + " Not Found");
		}
		return convertedVal;
	}

	public List<OpportunityT> convertOpportunityCurrency(
			List<OpportunityT> opportunityTs, List<String> toCurrency)
			throws DestinationException {
		if (!opportunityTs.isEmpty() && !toCurrency.isEmpty()) {
			for (OpportunityT opportunityT : opportunityTs) {
				convertOpportunityCurrency(opportunityT, toCurrency);
			}
		}
		return opportunityTs;
	}

	public OpportunityT convertOpportunityCurrency(OpportunityT opportunityT,
			List<String> toCurrency) throws DestinationException {
		if (opportunityT != null && !toCurrency.isEmpty()) {
			List<OpportunityDealValue> opportunityDealValues = new ArrayList<OpportunityDealValue>();
			for (String currency : toCurrency) {
				OpportunityDealValue opportunityDealValue = new OpportunityDealValue();
				String fromCurrency = opportunityT.getDealCurrency();
				if (fromCurrency != null && !fromCurrency.isEmpty()) {
					if (opportunityT.getDigitalDealValue() != null) {
						opportunityDealValue.setDigitalDealValue(convert(
								fromCurrency, currency,
								opportunityT.getDigitalDealValue()));
					}
					if (opportunityT.getOverallDealSize() != null) {
						opportunityDealValue.setOverallDealSize(convert(
								fromCurrency, currency,
								opportunityT.getOverallDealSize()));
					}
					opportunityDealValue.setCurrency(currency);
				}
				opportunityDealValues.add(opportunityDealValue);
			}
			opportunityT.setOpportunityDealValues(opportunityDealValues);
		}
		return opportunityT;
	}

}
