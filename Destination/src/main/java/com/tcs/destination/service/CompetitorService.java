package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.CompetitorMappingT;
import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.dto.CompetitorMappingDTO;
import com.tcs.destination.bean.dto.CompetitorOpportunityWrapperDTO;
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.data.repository.UserPreferencesRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;

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
	
	@Autowired
	private UserPreferencesRepository userPrefRepo;
	
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

	public PageDTO<CompetitorMappingT> findListByNameContaining(String chars, int page, int count) throws Exception {
		logger.debug("Begin:Inside findListByNameContaining() of CompetitorService");
		
		Pageable pageable = new PageRequest(page, count); 
		List<String> userPrefList = Lists.newArrayList();
			//filter already added competitors
			userPrefList = userPrefRepo.getCompetitorList(DestinationUtils.getCurrentUserId());
			
		Page<CompetitorMappingT> compList = compRepository
				.findByCompetitorNameIgnoreCaseLikeAndCompetitorNameNotIn("%" + chars + "%",userPrefList, pageable);
		if (compList.getContent().isEmpty())
		{
			logger.error("NOT_FOUND: No such competitor");
			throw new DestinationException(HttpStatus.NOT_FOUND,"No such competitor");
		}
		logger.info("End:Inside findListByNameContaining() of CompetitorService");
		return new PageDTO<CompetitorMappingT>(compList.getContent(), compList.getTotalElements());
	}

	public PageDTO<CompetitorMappingDTO> findByNameContainingAndDealDate(List<String> competitors, Date fromDate, Date toDate, int page, int count) throws Exception {
		logger.debug("Begin:Inside findByNameContainingAndDealDate() of CompetitorService");
		
		Date startDate = fromDate != null ? fromDate : DateUtils.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();

		Pageable pageable = new PageRequest(page, count);
		
		List<CompetitorMappingDTO> dtos = Lists.newArrayList();
		Page<CompetitorMappingT> competitorList;
		if(CollectionUtils.isEmpty(competitors)) {
			competitorList = compRepository.findByNameContainingAndDealDate(startDate, endDate, pageable);
		} else {
			competitorList = compRepository.findByNameContainingAndDealDate(startDate, endDate, competitors, pageable);
		}
		
		
		if(CollectionUtils.isNotEmpty(competitorList.getContent())) {
			for (CompetitorMappingT competitorMappingT : competitorList.getContent()) {
				for (OpportunityCompetitorLinkT oppLink : competitorMappingT.getOpportunityCompetitorLinkTs()) {
					OpportunityT opportunityT = oppLink.getOpportunityT();
					if(opportunityT != null && opportunityT.getDigitalDealValue() != null) {
						BigDecimal convertedValue = converterService.convertCurrencyRate(opportunityT.getDealCurrency(), Constants.USD, opportunityT.getDigitalDealValue().doubleValue());
						opportunityT.setDigitalDealValue(convertedValue.intValue());
					}
				}
				dtos.add(beanMapper.map(competitorMappingT, CompetitorMappingDTO.class, Constants.COMPETITOR_OPPORTUNITY));
			}
		}
		logger.info("End:Inside findByNameContainingAndDealDate() of CompetitorService");
		return new PageDTO<CompetitorMappingDTO>(dtos, competitorList.getTotalElements());
	}

	public ContentDTO<CompetitorOpportunityWrapperDTO> findMetricsByNameContainingAndDealDate(List<String> competitors, Date fromDate,
			Date toDate) {
		logger.debug("Begin:Inside findByNameContainingAndDealDate() of CompetitorService");
		
		Date startDate = fromDate != null ? fromDate : DateUtils.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();

		if(CollectionUtils.isEmpty(competitors)) {
			competitors = userPrefRepo.getCompetitorList(DestinationUtils.getCurrentUserId());
			if(CollectionUtils.isEmpty(competitors)) {
				throw new DestinationException(HttpStatus.NOT_FOUND, "User prefered competitors are not found.");
			}
		}
		
		List<CompetitorOpportunityWrapperDTO> dtoList = Lists.newArrayList();
		List<Object[]> values = compRepository.findOpportunityMetrics(startDate, endDate, competitors);
		if(CollectionUtils.isNotEmpty(values)) {
			for (Object[] fieldArr : values) {
				CompetitorOpportunityWrapperDTO dto = new CompetitorOpportunityWrapperDTO();
				dto.setCompetitorName((String) fieldArr[0]);
				dto.setLogo((byte[]) fieldArr[1]);
				dto.setWinCount((BigInteger) fieldArr[2]);
				dto.setWinValue(DestinationUtils.scaleToTwoDecimal((BigDecimal) fieldArr[3], true));
				dto.setLossCount((BigInteger) fieldArr[4]);
				dto.setLossValue(DestinationUtils.scaleToTwoDecimal((BigDecimal)fieldArr[5], true));
				dto.setPipelineCount((BigInteger) fieldArr[6]);
				dto.setPiplineValue(DestinationUtils.scaleToTwoDecimal((BigDecimal)fieldArr[7], true));
				
				dtoList.add(dto);
			}
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Competitor data is not available");
		}
		
		logger.info("End:Inside findByNameContainingAndDealDate() of CompetitorService");
		return new ContentDTO<CompetitorOpportunityWrapperDTO>(dtoList);//new ContentDTO<CompetitorMappingDTO>(dtos);
	}

}