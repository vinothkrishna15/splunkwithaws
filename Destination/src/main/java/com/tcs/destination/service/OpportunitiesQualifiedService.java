/**
 * 
 */
package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.dto.QualifiedPipelineDTO;
import com.tcs.destination.bean.dto.QualifiedPipelineDetails;
import com.tcs.destination.data.repository.OpportunitiesQualifiedRepository;

/**
 * @author tcs2
 *
 */
@Service
public class OpportunitiesQualifiedService {

	@Autowired
	OpportunitiesQualifiedRepository OpportunitiesQualifiedRepository;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public QualifiedPipelineDetails<QualifiedPipelineDetails> findAllQualifiedPipelineOpportunityDetails() {

		QualifiedPipelineDetails allQualifiedResult = new QualifiedPipelineDetails();
		List<Object[]> qualifiedList = OpportunitiesQualifiedRepository
				.findAllQualifiedPipelineOpportunities();
		List<Object[]> proactiveList = OpportunitiesQualifiedRepository
				.findOpportunitiesCountByProactiveType();
		List<Object[]> oneMillionList = OpportunitiesQualifiedRepository
				.findOneMillionQualifiedPipelineOpportunities();
		List<Object> resultSet = new ArrayList();
		listOfQualifiedPipeline(qualifiedList, proactiveList, oneMillionList,
				resultSet);

		allQualifiedResult.setQualifiedPipelineDTO(resultSet);
		return allQualifiedResult;
	}

	/**
	 * @param qualifiedList
	 * @param proactiveList
	 * @param oneMillionList
	 * @param master
	 */
	private void listOfQualifiedPipeline(List<Object[]> qualifiedList,
			List<Object[]> proactiveList, List<Object[]> oneMillionList,
			List<Object> resultant) {
		List<Integer> salesStage = new ArrayList<Integer>();
		if (qualifiedList != null && !qualifiedList.isEmpty()) {
			for (Object[] qualified : qualifiedList) {
				QualifiedPipelineDTO qualifiedPipelineDTO = new QualifiedPipelineDTO();
				if (null != qualified) {

					qualifiedPipelineDTO = setQualifiedPipelineDetails(
							qualified, qualifiedPipelineDTO, proactiveList,
							oneMillionList, salesStage);
				} else {

					qualifiedPipelineDTO = null;
				}
				resultant.add(qualifiedPipelineDTO);
			}
			unavailableSalesStages(salesStage, resultant);
		} else {
			unavailableSalesStages(salesStage, resultant);
		}

	}

	/**
	 * @param salesStage
	 * @param resultant
	 * @param qualifiedPipelineDTO
	 */
	private void unavailableSalesStages(List<Integer> salesStage,
			List<Object> resultant) {
		List<Integer> definedlist = new ArrayList<Integer>();
		definedlist.add(4);
		definedlist.add(5);
		definedlist.add(6);
		definedlist.add(7);
		definedlist.add(8);
		List<Integer> unavil = new ArrayList<Integer>(definedlist);
		unavil.removeAll(salesStage);
		for (Integer sales : unavil) {
			QualifiedPipelineDTO qualifiedPipelineDTOUnail = new QualifiedPipelineDTO();
			qualifiedPipelineDTOUnail.setSalesStageCode(sales);
			qualifiedPipelineDTOUnail.setOpportunitiesCount(BigInteger.ZERO);
			qualifiedPipelineDTOUnail.setDigitalDealValue(BigDecimal.ZERO);
			qualifiedPipelineDTOUnail
					.setOneMillionOpportunityCount(BigInteger.ZERO);
			qualifiedPipelineDTOUnail.setProactiveCount(BigInteger.ZERO);
			resultant.add(qualifiedPipelineDTOUnail);
		}
	}

	/**
	 * @param QfdList
	 * @return
	 */
	private QualifiedPipelineDTO setQualifiedPipelineDetails(Object[] QfdList,
			QualifiedPipelineDTO qualifiedPipelineDTO,
			List<Object[]> proactList, List<Object[]> million,
			List<Integer> salesStageDB) {

		qualifiedPipelineDTO.setSalesStageCode((int) QfdList[0]);
		qualifiedPipelineDTO.setOpportunitiesCount((BigInteger) QfdList[1]);
		qualifiedPipelineDTO.setDigitalDealValue(((BigDecimal) QfdList[2])
				.setScale(2, RoundingMode.HALF_UP));
		for (Object[] oneMillion : million) {
			if (oneMillion[0].equals(QfdList[0])) {
				qualifiedPipelineDTO
						.setOneMillionOpportunityCount((BigInteger) oneMillion[1]);
			}
		}
		for (Object[] proactive : proactList) {

			if (proactive[0].equals(QfdList[0])) {
				qualifiedPipelineDTO
						.setProactiveCount((BigInteger) (proactive[1]));
			}

		}

		salesStageDB.add((int) QfdList[0]);
		return qualifiedPipelineDTO;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public QualifiedPipelineDetails<QualifiedPipelineDetails> findQualifiedPipelineOpportunityDetailsForSalesAndConsulting(
			List<String> userGroup) {

		QualifiedPipelineDetails salesAndConsultingResult = new QualifiedPipelineDetails();
		List<Object[]> qualifiedList = OpportunitiesQualifiedRepository
				.findSalesQualifiedPipelineOpportunities(userGroup);
		List<Object[]> proactiveList = OpportunitiesQualifiedRepository
				.findSalesOpportunitiesCountByProactiveType(userGroup);
		List<Object[]> oneMillionList = OpportunitiesQualifiedRepository
				.findSalesOneMillionQualifiedPipelineOpportunities(userGroup);
		List<Object> resultSet = new ArrayList();
		listOfQualifiedPipeline(qualifiedList, proactiveList, oneMillionList,
				resultSet);

		salesAndConsultingResult.setQualifiedPipelineDTO(resultSet);
		return salesAndConsultingResult;
	}

}
