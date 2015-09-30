package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.OpportunityDealValue;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.exception.DestinationException;

@Service
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

	public List<BidDetailsT> convertBidDetailsCurrency(List<BidDetailsT> bidDetailsList, List<String> toCurrency)
			throws DestinationException {
		if (!bidDetailsList.isEmpty() && !toCurrency.isEmpty()) {
			for (BidDetailsT bidDetail : bidDetailsList) {
				convertBidDetailsCurrency(bidDetail, toCurrency);
			}
		}
		return bidDetailsList;
	}
	
	public BidDetailsT convertBidDetailsCurrency(BidDetailsT bidDetailsT,
			List<String> toCurrency) throws DestinationException {
		if (bidDetailsT != null && !toCurrency.isEmpty()) {
			List<OpportunityDealValue> opportunityDealValues = new ArrayList<OpportunityDealValue>();
			for (String currency : toCurrency) {
				OpportunityDealValue opportunityDealValue = new OpportunityDealValue();
				String fromCurrency = bidDetailsT.getOpportunityT().getDealCurrency();
				if (fromCurrency != null && !fromCurrency.isEmpty()) {
					if (bidDetailsT.getOpportunityT().getDigitalDealValue() != null) {
						opportunityDealValue.setDigitalDealValue(convert(fromCurrency, currency,bidDetailsT.getOpportunityT().getDigitalDealValue()));
					}
					if (bidDetailsT.getOpportunityT().getOverallDealSize() != null) {
						opportunityDealValue.setOverallDealSize(convert(fromCurrency, currency, bidDetailsT.getOpportunityT().getOverallDealSize()));
					}
					opportunityDealValue.setCurrency(currency);
				}
				opportunityDealValues.add(opportunityDealValue);
			}
			bidDetailsT.getOpportunityT().setOpportunityDealValues(opportunityDealValues);
		}
		return bidDetailsT;
	}
	
	/**
	 * This method returns a map of Currency Name and Rate with Name as key and Rate as value
	 * 
	 * @return
	 */
	public Map<String, BigDecimal> getCurrencyNameAndRate(){
		
		Map<String, BigDecimal> map =null;
		
		List<Object[]> listOfObjects = converterRepository.getCurrencyNameAndRate();
		
		if((listOfObjects!=null)&&(!listOfObjects.isEmpty())){
			
			map = new HashMap<String, BigDecimal>();

				for (Object[] ob : listOfObjects) {
				    map.put(ob[0].toString().trim(), new BigDecimal(ob[1].toString().trim()));
				}

		}
		return map;
	}
	
	/**
	 * Convert to required currency
	 * 
	 * @param base
	 * @param target
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public BigDecimal convertCurrencyRate(String base, String target, double value) throws Exception {

		Map<String, BigDecimal> mapOfBeaconConverter = getCurrencyNameAndRate();
		
		BigDecimal sourceVal = BigDecimal.valueOf(value);
		BigDecimal convertedVal = null;
		
		BigDecimal conversionBaseRate = getMapValuesForKey(mapOfBeaconConverter, base);
		if (conversionBaseRate != null) {
			sourceVal = conversionBaseRate.multiply(sourceVal);
		}

		BigDecimal conversionTargetRate = getMapValuesForKey(mapOfBeaconConverter, target);
		if (conversionTargetRate != null) {
			convertedVal = sourceVal.divide(conversionTargetRate, 2, RoundingMode.HALF_UP);
		} 
		return convertedVal;
	}
	
	/**
     * This method retrieves the value for the key
     * 
     * @param map
     * @param key
     * @return BigDecimal
     * @throws Exception
     */
    private BigDecimal getMapValuesForKey(Map<String, BigDecimal> map, String key)
			throws Exception {
    	BigDecimal value = null;
		if (map.containsKey(key)) {
			value = map.get(key);
		}
		return value;
	}
	
}
