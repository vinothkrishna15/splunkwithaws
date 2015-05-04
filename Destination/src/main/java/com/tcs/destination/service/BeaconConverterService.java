package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.exception.DestinationException;

@Component
public class BeaconConverterService {

	@Autowired
	BeaconConvertorRepository converterRepository;
	
	public BigDecimal convert(String base,String target,double value) throws DestinationException{
		
		BigDecimal sourceVal = BigDecimal.valueOf(value);
		BigDecimal convertedVal = null;
		Status status=new Status();
		status.setStatus("FAILED", "Currency Conversion Failed");
		if(!base.equalsIgnoreCase("INR")){
			BeaconConvertorMappingT converterBase = converterRepository.findByCurrencyName(base);
			if(converterBase != null)
			sourceVal = converterBase.getConversionRate().multiply(sourceVal);
			else
				throw new DestinationException(HttpStatus.NOT_FOUND,"Currency Type "+base+" Not Found");
		}
		
		BeaconConvertorMappingT converterTarget = converterRepository.findByCurrencyName(target);
		if(converterTarget != null)
		{
		convertedVal = sourceVal.divide(converterTarget.getConversionRate(),2, RoundingMode.HALF_UP); 
		 status.setStatus("SUCCESS", convertedVal.toString());
		}
		else
		{
			throw new DestinationException(HttpStatus.NOT_FOUND,"Currency Type "+target+" Not Found");
		}
		return convertedVal;
	}
	
	
}
