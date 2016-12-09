package com.tcs.destination.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.CompetitorMappingT;
import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.dto.CompetitorMappingDTO;
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;

/**
 * This service retrieves data from competitor repository
 */
@Service
public class CompetitorService {
	
	private static final Logger logger = LoggerFactory.getLogger(CompetitorService.class);

	@Autowired
	CompetitorRepository compRepository;

	@Autowired
	BeaconConverterService converterService;

	@Autowired
	DozerBeanMapper beanMapper;
	
	public List<CompetitorMappingT> findByNameContaining(String chars) throws Exception {
		logger.debug("Begin:Inside findByNameContaining() of CompetitorService");
		List<CompetitorMappingT> compList = compRepository
				.findByActiveTrueAndCompetitorNameIgnoreCaseLike("%" + chars + "%");
		if (compList.isEmpty())
		{
			logger.error("NOT_FOUND: No such competitor");
			throw new DestinationException(HttpStatus.NOT_FOUND,"No such competitor");
		}
		logger.info("End:Inside findByNameContaining() of CompetitorService");
		return compList;
	}

	public ContentDTO<CompetitorMappingDTO> findByNameContainingAndDealDate(String chars, Date fromDate, Date toDate) throws Exception {
		logger.debug("Begin:Inside findByNameContainingAndDealDate() of CompetitorService");
		
		Date startDate = fromDate != null ? fromDate : DateUtils.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();

		List<CompetitorMappingDTO> dtos = Lists.newArrayList();
		List<CompetitorMappingT> competitorList = compRepository.findByNameContainingAndDealDate(startDate, endDate);
		for (CompetitorMappingT competitorMappingT : competitorList) {
			for (OpportunityCompetitorLinkT oppLink : competitorMappingT.getOpportunityCompetitorLinkTs()) {
				OpportunityT opportunityT = oppLink.getOpportunityT();
				if(opportunityT != null && opportunityT.getDigitalDealValue() != null) {
					BigDecimal convertedValue = converterService.convertCurrencyRate(opportunityT.getDealCurrency(), Constants.USD, opportunityT.getDigitalDealValue().doubleValue());
					opportunityT.setDigitalDealValue(convertedValue.intValue());
				}
			}
			dtos.add(beanMapper.map(competitorMappingT, CompetitorMappingDTO.class, Constants.COMPETITOR_OPPORTUNITY));
		}
		logger.info("End:Inside findByNameContainingAndDealDate() of CompetitorService");
		return new ContentDTO<CompetitorMappingDTO>(dtos);
	}

}