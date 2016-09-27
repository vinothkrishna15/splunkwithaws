package com.tcs.destination.helper;

import static com.tcs.destination.utils.DateUtils.ACTUAL_FORMAT;
import static com.tcs.destination.utils.DateUtils.DATE_FORMAT_MONTH;
import static com.tcs.destination.utils.DateUtils.DATE_FORMAT_MONTH_NAME_WITH_SPACE;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.ConnectCustomer;
import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityLoss;
import com.tcs.destination.bean.OpportunityRFPSubmitted;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.OpportunityWins;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.service.NumericUtil;
import com.tcs.destination.service.OpportunityDownloadService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;




import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * helper for creating the weekly report
 * 
 * @author TCS
 *
 */
@Component("weeklyReportHelper")
public class WeeklyReportHelper {

	private static final Logger logger = LoggerFactory
			.getLogger(WeeklyReportHelper.class);

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

	@Autowired
	OpportunityDownloadService opportunityDownloadService;

	/**
	 * constructs the weekly report based on the geographies given
	 * 
	 * @param geos
	 * @param currentDate
	 * @param previousWeekDate
	 * @param geography
	 * @return
	 * @throws Exception
	 */
	public JasperPrint constructWeeklyReport(List<String> geos,
			Date currentDate, Date previousWeekDate, String geography, int weekNumber, String financialYear)
			throws Exception {

		logger.info("constructing weekly report for geography : " + geography);
		Date previousDate = DateUtils.getPreviousDate();
		String currentDateString = DATE_FORMAT_MONTH_NAME_WITH_SPACE.format(currentDate);
		String previousDateString = DATE_FORMAT_MONTH.format(previousDate);
		String previousWeekDateString = DATE_FORMAT_MONTH.format(previousWeekDate);
		
		// Opp Wins
		List<OpportunityWins> opportunityWins = getOpportunityWins(geos,
				currentDate, previousWeekDate);

		String totalWinValue = getTotalWinValue(opportunityWins);
		// Opp Loss
		List<OpportunityLoss> opportunityLoss = getOpportunityLoss(geos,
				currentDate, previousWeekDate);
		
		String totalLossValue = getTotalLossValue(opportunityLoss);

		// Opp RFP Submitted
		List<OpportunityRFPSubmitted> oppRFPSubmitted = getOpportunityRFPSubmitted(
				geos, currentDate, previousWeekDate);
		
		String totalBidsValue = getTotalBidsValue(oppRFPSubmitted);

		// Customer connects
		List<ConnectCustomer> connects = getCustomerConnects(geos,
				currentDate, previousWeekDate);
		Integer customerConnnectsSize = connects.size();
		// Partner connects
		List<ConnectCustomer> partnerConnects = getPartnerConnects(geos,
				currentDate, previousWeekDate);
		connects.addAll(partnerConnects);
		
		JasperCompileManager.compileReportToFile("/Users/bnpp/Movies/nPlus1/Destination/src/main/resources/weeklyReport.jrxml", 
				"/Users/bnpp/Desktop/Mani_PDF/weeklyReport.jasper");
        
		JasperCompileManager.compileReportToFile("/Users/bnpp/Movies/nPlus1/Destination/src/main/resources/weeklyReportTitle.jrxml", 
				"/Users/bnpp/Desktop/Mani_PDF/weeklyReportTitle.jasper");
		
		JasperCompileManager.compileReportToFile("/Users/bnpp/Movies/nPlus1/Destination/src/main/resources/performanceSnapshotReport.jrxml", 
				"/Users/bnpp/Desktop/Mani_PDF/performanceSnapshotReport.jasper");
		
		JasperCompileManager.compileReportToFile("/Users/bnpp/Movies/nPlus1/Destination/src/main/resources/opportunityWinsReport.jrxml", 
				"/Users/bnpp/Desktop/Mani_PDF/opportunityWinsReport.jasper");
		
		JasperCompileManager.compileReportToFile("/Users/bnpp/Movies/nPlus1/Destination/src/main/resources/opportunityLossReport.jrxml", 
				"/Users/bnpp/Desktop/Mani_PDF/opportunityLossReport.jasper");
		
		JasperCompileManager.compileReportToFile("/Users/bnpp/Movies/nPlus1/Destination/src/main/resources/opportunityBidsReport.jrxml", 
				"/Users/bnpp/Desktop/Mani_PDF/opportunityBidsReport.jasper");
		
		JasperCompileManager.compileReportToFile("/Users/bnpp/Movies/nPlus1/Destination/src/main/resources/connectsReport.jrxml", 
				"/Users/bnpp/Desktop/Mani_PDF/connectsReport.jasper");
		logger.info("######################## compiled ######################");
		
		JRBeanCollectionDataSource winColDataSource = new 
		         JRBeanCollectionDataSource(opportunityWins, false);
		
		JRBeanCollectionDataSource lossColDataSource = new 
		         JRBeanCollectionDataSource(opportunityLoss, false);
		
		JRBeanCollectionDataSource bidsColDataSource = new 
		         JRBeanCollectionDataSource(oppRFPSubmitted, false);
		
	     JRBeanCollectionDataSource connectsColDataSource = new 
		         JRBeanCollectionDataSource(connects, false);
		
	     Map<String,Object> parameters = Maps.newHashMap();
			parameters.put("dealsWonMainP", opportunityWins.size());
			parameters.put("dealsLossMainP", opportunityLoss.size());
			parameters.put("totalBidsMainP", oppRFPSubmitted.size());
			parameters.put("totalConnectsMainP", connects.size());
			parameters.put("custConnectsMainP", customerConnnectsSize+1);
			parameters.put("winsValueMainP", StringUtils.isNotEmpty(totalWinValue)?totalWinValue:"0");
			parameters.put("lossValueMainP", StringUtils.isNotEmpty(totalLossValue)?totalLossValue:"0");
			parameters.put("bidsValueMainP", StringUtils.isNotEmpty(totalBidsValue)?totalBidsValue:"0");
			parameters.put("winDataSource", winColDataSource);
			parameters.put("lossSize", opportunityLoss.size());
			parameters.put("lossDataSource", lossColDataSource);
			parameters.put("bidsDataSource", bidsColDataSource);
			parameters.put("custConnectsDataSource", connectsColDataSource);
			
			parameters.put("titleReportParameter", "/Users/bnpp/Desktop/Mani_PDF/weeklyReportTitle.jasper");
			parameters.put("performanceReportParameter", "/Users/bnpp/Desktop/Mani_PDF/performanceSnapshotReport.jasper");
			parameters.put("winReportParameter", "/Users/bnpp/Desktop/Mani_PDF/opportunityWinsReport.jasper");
			parameters.put("lossReportParameter", "/Users/bnpp/Desktop/Mani_PDF/opportunityLossReport.jasper");
			parameters.put("bidsReportParameter", "/Users/bnpp/Desktop/Mani_PDF/opportunityBidsReport.jasper");
			parameters.put("custConnectReportParameter", "/Users/bnpp/Desktop/Mani_PDF/connectsReport.jasper");
			
			parameters.put("geographyMainP", geography);
			parameters.put("weekNumberMainP", weekNumber);
			parameters.put("previousWeekDateMainP", previousWeekDateString);
			parameters.put("previousDateMainP", previousDateString);
			parameters.put("reportPublishedDateMainP", currentDateString);
			parameters.put("financialYearMainP", financialYear);
			
			
			parameters.put("titleDataSource", new JREmptyDataSource());
			
			JasperPrint jasperPrint = null;
			
			try {
				jasperPrint = JasperFillManager.fillReport("/Users/bnpp/Desktop/Mani_PDF/weeklyReport.jasper", parameters, new JREmptyDataSource());
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("######################## fillReport end ######################");

			
		return jasperPrint;
	}

	private String getTotalBidsValue(
			List<OpportunityRFPSubmitted> oppRFPSubmitted) {
		String totalBidsValue = null;
		int sum = 0;
		if(CollectionUtils.isNotEmpty(oppRFPSubmitted)) {
			for(OpportunityRFPSubmitted oppWin : oppRFPSubmitted) {
				sum = sum+oppWin.getDealValueInt();
			}
			totalBidsValue = NumericUtil.toUSDinNumberScale(new BigDecimal(sum));
		}
		return totalBidsValue;
	}

	private String getTotalLossValue(List<OpportunityLoss> opportunityLoss) {

		String totalLossValue = null;
		int sum = 0;
		if(CollectionUtils.isNotEmpty(opportunityLoss)) {
			for(OpportunityLoss oppWin : opportunityLoss) {
				sum = sum+oppWin.getDealValueInt();
			}
			totalLossValue = NumericUtil.toUSDinNumberScale(new BigDecimal(sum));
		}
		return totalLossValue;
	
	}

	private String getTotalWinValue(List<OpportunityWins> opportunityWins) {
		String totalWinValue = null;
		int sum = 0;
		if(CollectionUtils.isNotEmpty(opportunityWins)) {
			for(OpportunityWins oppWin : opportunityWins) {
				sum = sum+oppWin.getDealValueInt();
			}
			totalWinValue = NumericUtil.toUSDinNumberScale(new BigDecimal(sum));
		}
		return totalWinValue;
	}

	/**
	 * Retrieves the partner connects during a week
	 * 
	 * @param geos
	 * @param currentDate
	 * @param previousWeekDate
	 * @return
	 */
	private List<ConnectCustomer> getPartnerConnects(List<String> geos,
			Date currentDate, Date previousWeekDate) {
		logger.debug("Inside getPartnerConnects method");
		List<ConnectCustomer> partnerConnects = Lists.newArrayList();
		List<ConnectT> connects = connectRepository.getPartnerConnectsForAWeek(
				geos, new Timestamp(previousWeekDate.getTime()), new Timestamp(
						currentDate.getTime()), Constants.PARTNER);
		if (CollectionUtils.isNotEmpty(connects)) {
			for (ConnectT connectT : connects) {
				String displaySubSp = "";
				String contactNames = "";
				String contactRoles = "";
				List<String> connectContactNames = Lists.newArrayList();
				List<String> connectContactRoles = Lists.newArrayList();
				Set<String> connectSubSp = Sets.newHashSet();
				ConnectCustomer partnerConnect = new ConnectCustomer();
				// Primary Owner
				partnerConnect.setBdContact(connectT.getPrimaryOwnerUser()
						.getUserName());
				// Connect Date
				partnerConnect.setConnectDate(ACTUAL_FORMAT.format(connectT
						.getStartDatetimeOfConnect()));
				// Customer Name
				partnerConnect.setPartnerName(connectT.getPartnerMasterT()
						.getPartnerName());
				// Connect Name
				partnerConnect.setConnectName(connectT.getConnectName());
				// Sub Sp
				connectSubSp = getDisplaySubSpsOfConnect(connectT
						.getConnectSubSpLinkTs());
				displaySubSp = joinString(connectSubSp);
				partnerConnect.setSubSp(getValue(displaySubSp));
				// Contact and contact Role
				if (CollectionUtils.isNotEmpty(connectT
						.getConnectCustomerContactLinkTs())) {
					for (ConnectCustomerContactLinkT connectCustomerContactLinkT : connectT
							.getConnectCustomerContactLinkTs()) {
						ContactT contactT = connectCustomerContactLinkT
								.getContactT();
						connectContactNames.add(contactT.getContactName());
						if (StringUtils.isNotEmpty(contactT.getContactRole())) {
							if (StringUtils.isNotEmpty(contactT.getOtherRole())
									&& contactT.getContactRole().equals(
											Constants.OTHER)) {
								connectContactRoles
										.add(contactT.getOtherRole());
							} else {
								connectContactRoles.add(contactT
										.getContactRole());
							}
						}
					}
				}
				if (CollectionUtils.isNotEmpty(connectContactNames)) {
					contactNames = joinString(connectContactNames);
				}
				if (CollectionUtils.isNotEmpty(connectContactRoles)) {
					contactRoles = joinString(connectContactRoles);
				}
				partnerConnect.setPartnerContact(getValue(contactNames));
				partnerConnect.setPartnerContactRole(getValue(contactRoles));
				// Connect Type
				partnerConnect.setConnectCategory(StringUtils
						.isNotEmpty(connectT.getType()) ? connectT.getType()
						: Constants.NOT_AVAILABLE);
				partnerConnect.setType(1);
				partnerConnects.add(partnerConnect);
			}
		}
		return partnerConnects;

	}

	/**
	 * Retrieves the customer connects for a week
	 * 
	 * @param geos
	 * @param currentDate
	 * @param previousWeekDate
	 * @return
	 */
	private List<ConnectCustomer> getCustomerConnects(List<String> geos,
			Date currentDate, Date previousWeekDate) {
		logger.debug("Inside getCustomerConnects method");
		List<ConnectCustomer> customerConnects = Lists.newArrayList();
		List<ConnectT> connects = connectRepository
				.getCustomerConnectsForAWeek(geos, new Timestamp(
						previousWeekDate.getTime()),
						new Timestamp(currentDate.getTime()),
						Constants.CUSTOMER);
		if (CollectionUtils.isNotEmpty(connects)) {
			for (ConnectT connectT : connects) {
				String displaySubSp = "";
				String contactNames = "";
				String contactRoles = "";
				List<String> connectContactNames = Lists.newArrayList();
				List<String> connectContactRoles = Lists.newArrayList();
				Set<String> connectSubSp = Sets.newHashSet();
				ConnectCustomer customerConnect = new ConnectCustomer();
				// Primary Owner
				customerConnect.setBdContact(connectT.getPrimaryOwnerUser()
						.getUserName());
				// Connect Date
				customerConnect.setConnectDate(ACTUAL_FORMAT.format(connectT
						.getStartDatetimeOfConnect()));
				// Customer Name
				customerConnect.setCustomerName(connectT.getCustomerMasterT()
						.getCustomerName());
				// Connect Name
				customerConnect.setConnectName(connectT.getConnectName());
				// Sub Sp
				connectSubSp = getDisplaySubSpsOfConnect(connectT
						.getConnectSubSpLinkTs());
				displaySubSp = joinString(connectSubSp);
				customerConnect.setSubSp(getValue(displaySubSp));
				// IOU
				customerConnect.setIou(connectT.getCustomerMasterT()
						.getIouCustomerMappingT().getDisplayIou());
				// Contact and contact Role
				if (CollectionUtils.isNotEmpty(connectT
						.getConnectCustomerContactLinkTs())) {
					for (ConnectCustomerContactLinkT connectCustomerContactLinkT : connectT
							.getConnectCustomerContactLinkTs()) {
						ContactT contactT = connectCustomerContactLinkT
								.getContactT();
						connectContactNames.add(contactT.getContactName());
						if (StringUtils.isNotEmpty(contactT.getContactRole())) {
							if (StringUtils.isNotEmpty(contactT.getOtherRole())
									&& contactT.getContactRole().equals(
											Constants.OTHER)) {
								connectContactRoles
										.add(contactT.getOtherRole());
							} else {
								connectContactRoles.add(contactT
										.getContactRole());
							}
						}
					}
				}
				if (CollectionUtils.isNotEmpty(connectContactNames)) {
					contactNames = joinString(connectContactNames);
				}
				if (CollectionUtils.isNotEmpty(connectContactRoles)) {
					contactRoles = joinString(connectContactRoles);
				}

				customerConnect.setCustomerContact(getValue(contactNames));
				customerConnect.setCustomerContactRole(getValue(contactRoles));

				// Connect Type
				customerConnect
						.setConnectCategory(getValue(connectT.getType()));
				
				customerConnect.setType(0);

				customerConnects.add(customerConnect);
			}
		}
		return customerConnects;
	}

	private String getValue(String value) {
		return StringUtils.isNotEmpty(value) ? value : Constants.NOT_AVAILABLE;
	}

	/**
	 * gets display subsps of a connect
	 * 
	 * @param connectSubSpLinkTs
	 * @return
	 */
	private Set<String> getDisplaySubSpsOfConnect(
			List<ConnectSubSpLinkT> connectSubSpLinkTs) {
		Set<String> displaySubSps = Sets.newHashSet();
		if (CollectionUtils.isNotEmpty(connectSubSpLinkTs)) {
			for (ConnectSubSpLinkT connectSubSpLinkT : connectSubSpLinkTs) {
				displaySubSps.add(connectSubSpLinkT.getSubSpMappingT()
						.getDisplaySubSp());
			}
		}
		return displaySubSps;
	}

	/**
	 * Retrieves the RFP Submitted opportunities based on the geographies given
	 * 
	 * @param geos
	 * @param currentDate
	 * @param previousWeekDate
	 * @return
	 * @throws Exception
	 */
	private List<OpportunityRFPSubmitted> getOpportunityRFPSubmitted(
			List<String> geos, Date currentDate, Date previousWeekDate)
			throws Exception {
		logger.debug("Inside getOpportunityRFPSubmitted method");
		List<OpportunityRFPSubmitted> opportunityRFPSubmitted = Lists
				.newArrayList();
		List<OpportunityT> opportunity = opportunityRepository
				.getOpportunityForAWeek(geos,
						new Timestamp(previousWeekDate.getTime()),
						new Timestamp(currentDate.getTime()), 5);
		if (CollectionUtils.isNotEmpty(opportunity)) {
			for (OpportunityT opp : opportunity) {
				OpportunityRFPSubmitted oppRFPSubmitted = new OpportunityRFPSubmitted();
				String displaySubSp = "";
				String competitors = Constants.NOT_AVAILABLE;
				String expectedDateOfOutcome = Constants.NOT_AVAILABLE;
				List<String> oppCompetitor = Lists.newArrayList();
				// Opportunity Id
				oppRFPSubmitted.setOpportunityId(opp.getOpportunityId());
				// CRM Id
				oppRFPSubmitted
						.setCrmId(StringUtils.isNotEmpty(opp.getCrmId()) ? opp
								.getCrmId() : Constants.NOT_AVAILABLE);
				// Customer Name
				oppRFPSubmitted.setCustomerName(opp.getCustomerMasterT()
						.getCustomerName());
				// Opportunity Name
				oppRFPSubmitted.setOpportunityName(opp.getOpportunityName());
				//Opportunity Description
				oppRFPSubmitted.setOpportunityDescription(StringUtils.isNotEmpty(opp
						.getOpportunityDescription()) ? opp
						.getOpportunityDescription() : Constants.NOT_AVAILABLE);
				// get primary display subsp
				displaySubSp = getPrimaryDisplaySubSpOfOpportunity(opp
						.getOpportunitySubSpLinkTs());
				oppRFPSubmitted.setSubSp(getValue(displaySubSp));
				// Deal Value
				BigDecimal currencyToUSD = opportunityDownloadService
						.convertCurrencyToUSD(opp.getDealCurrency(),
								opp.getDigitalDealValue());
				String dealValue = NumericUtil
						.toUSDinNumberScale(currencyToUSD);
				oppRFPSubmitted.setDealValueInt(Integer.valueOf(currencyToUSD.intValue()));
				oppRFPSubmitted.setDealValue(getValue(dealValue));
				// Expected Date of outcome
				BidDetailsT bidDetailsT = bidDetailsTRepository
						.findFirstByOpportunityIdOrderByModifiedDatetimeDesc(opp
								.getOpportunityId());
				if (bidDetailsT != null) {
					expectedDateOfOutcome = (bidDetailsT
							.getExpectedDateOfOutcome() != null) ? ACTUAL_FORMAT
							.format(bidDetailsT.getExpectedDateOfOutcome())
							: expectedDateOfOutcome;
				}
				oppRFPSubmitted.setOutcomeExpectedDate(expectedDateOfOutcome);
				// Opportunity competitors
				oppCompetitor = getCompetitors(opp
						.getOpportunityCompetitorLinkTs());
				competitors = joinString(oppCompetitor);
				oppRFPSubmitted.setCompetitors(getValue(competitors));
				// Opportunity owner
				oppRFPSubmitted.setOwner(opp.getPrimaryOwnerUser()
						.getUserName());
				opportunityRFPSubmitted.add(oppRFPSubmitted);
			}
		}
		return opportunityRFPSubmitted;
	}

	/**
	 * retrieves opportunity losses for a week
	 * 
	 * @param geos
	 * @param currentDate
	 * @param previousWeekDate
	 * @return
	 * @throws Exception
	 */
	private List<OpportunityLoss> getOpportunityLoss(List<String> geos,
			Date currentDate, Date previousWeekDate) throws Exception {
		logger.debug("Inside getOpportunityLoss method");
		List<OpportunityLoss> opportunityLoss = Lists.newArrayList();
		List<OpportunityT> opportunity = opportunityRepository
				.getOpportunityForAWeek(geos,
						new Timestamp(previousWeekDate.getTime()),
						new Timestamp(currentDate.getTime()), 10);
		if (CollectionUtils.isNotEmpty(opportunity)) {
			for (OpportunityT opp : opportunity) {
				OpportunityLoss oppLoss = new OpportunityLoss();
				String displaySubSp = "";
				String winLossFactors = "";
				String competitors = Constants.NOT_AVAILABLE;
				List<String> oppWinFactors = Lists.newArrayList();
				List<String> oppCompetitor = Lists.newArrayList();
				// Opportunity Id
				oppLoss.setOpportunityId(opp.getOpportunityId());
				// CRM Id
				oppLoss.setCrmId(StringUtils.isNotEmpty(opp.getCrmId()) ? opp
						.getCrmId() : Constants.NOT_AVAILABLE);
				// Customer Name
				oppLoss.setCustomerName(opp.getCustomerMasterT()
						.getCustomerName());
				// Opportunity Name
				oppLoss.setOpportunityName(opp.getOpportunityName());
				//Opportunity Description
				oppLoss.setOpportunityDescription(StringUtils.isNotEmpty(opp
						.getOpportunityDescription()) ? opp
						.getOpportunityDescription() : Constants.NOT_AVAILABLE);
				// get primary display subsp
				displaySubSp = getPrimaryDisplaySubSpOfOpportunity(opp
						.getOpportunitySubSpLinkTs());
				oppLoss.setSubSp(getValue(displaySubSp));
				// Deal Value
				BigDecimal currencyToUSD = opportunityDownloadService
						.convertCurrencyToUSD(opp.getDealCurrency(),
								opp.getDigitalDealValue());
				String dealValue = NumericUtil
						.toUSDinNumberScale(currencyToUSD);
				oppLoss.setDealValueInt(Integer.valueOf(currencyToUSD.intValue()));
				oppLoss.setDealValue(getValue(dealValue));
				// Opportunity win factors
				oppWinFactors = getLossFactors(opp
						.getOpportunityWinLossFactorsTs());
				winLossFactors = joinString(oppWinFactors);
				oppLoss.setLossFactors(getValue(winLossFactors));
				// Opportunity competitors
				oppCompetitor = getCompetitors(opp
						.getOpportunityCompetitorLinkTs());
				competitors = joinString(oppCompetitor);
				oppLoss.setCompetitors(getValue(competitors));
				// Opportunity owner
				oppLoss.setOwner(opp.getPrimaryOwnerUser().getUserName());
				opportunityLoss.add(oppLoss);
			}
		}
		return opportunityLoss;
	}

	/**
	 * retrieves the opportunity wins for a week
	 * 
	 * @param geos
	 * @param currentDate
	 * @param previousWeekDate
	 * @return
	 * @throws Exception
	 */
	private List<OpportunityWins> getOpportunityWins(List<String> geos,
			Date currentDate, Date previousWeekDate) throws Exception {
		logger.debug("Inside getOpportunityWins method");
		List<OpportunityWins> opportunityWins = Lists.newArrayList();
		List<OpportunityT> opportunity = opportunityRepository
				.getOpportunityForAWeek(geos,
						new Timestamp(previousWeekDate.getTime()),
						new Timestamp(currentDate.getTime()), 9);
		if (CollectionUtils.isNotEmpty(opportunity)) {
			for (OpportunityT opp : opportunity) {
				OpportunityWins oppWins = new OpportunityWins();
				String displaySubSp = "";
				String winLossFactors = "";
				String competitors = Constants.NOT_AVAILABLE;
				List<String> oppWinFactors = Lists.newArrayList();
				List<String> oppCompetitor = Lists.newArrayList();
				// Opportunity Id
				oppWins.setOpportunityId(opp.getOpportunityId());
				// CRM Id
				oppWins.setCrmId(StringUtils.isNotEmpty(opp.getCrmId()) ? opp
						.getCrmId() : Constants.NOT_AVAILABLE);
				// Customer Name
				oppWins.setCustomerName(opp.getCustomerMasterT()
						.getCustomerName());
				// Opportunity Name
				oppWins.setOpportunityName(opp.getOpportunityName());
				//Opportunity Description
				oppWins.setOpportunityDescription(StringUtils.isNotEmpty(opp
						.getOpportunityDescription()) ? opp
						.getOpportunityDescription() : Constants.NOT_AVAILABLE);
				// get primary display subsp
				displaySubSp = getPrimaryDisplaySubSpOfOpportunity(opp
						.getOpportunitySubSpLinkTs());
				oppWins.setSubSp(getValue(displaySubSp));
				// Deal Value
				BigDecimal currencyToUSD = opportunityDownloadService
						.convertCurrencyToUSD(opp.getDealCurrency(),
								opp.getDigitalDealValue());
				String dealValue = NumericUtil
						.toUSDinNumberScale(currencyToUSD);
				oppWins.setDealValueInt(Integer.valueOf(currencyToUSD.intValue()));
				oppWins.setDealValue(getValue(dealValue));
				// Opportunity win factors
				oppWinFactors = getWinFactors(opp
						.getOpportunityWinLossFactorsTs());
				winLossFactors = joinString(oppWinFactors);
				oppWins.setWinFactors(getValue(winLossFactors));
				// Opportunity competitors
				oppCompetitor = getCompetitors(opp
						.getOpportunityCompetitorLinkTs());
				competitors = joinString(oppCompetitor);
				oppWins.setCompetitors(getValue(competitors));
				// Opportunity owner
				oppWins.setOwner(opp.getPrimaryOwnerUser().getUserName());
				opportunityWins.add(oppWins);
			}
		}
		return opportunityWins;
	}

	/**
	 * joins string by comma seperated
	 * 
	 * @param values
	 * @return
	 */
	private String joinString(List<String> values) {
		String value = "";
		if (CollectionUtils.isNotEmpty(values)) {
			value = StringUtils.join(values, ", ");
		}
		return value;
	}
	
	/**
	 * joins string by comma seperated
	 * 
	 * @param values
	 * @return
	 */
	private String joinString(Set<String> values) {
		String value = "";
		if (CollectionUtils.isNotEmpty(values)) {
			value = StringUtils.join(values, ", ");
		}
		return value;
	}

	/**
	 * gets primary display subsp of an opportunity
	 * 
	 * @param opportunitySubSpLinkTs
	 * @return
	 */
	private String getPrimaryDisplaySubSpOfOpportunity(
			List<OpportunitySubSpLinkT> opportunitySubSpLinkTs) {
		logger.debug("getPrimaryDisplaySubSpOfOpportunity");
		String displaySubSp = "";
		if (CollectionUtils.isNotEmpty(opportunitySubSpLinkTs)) {
			for (OpportunitySubSpLinkT opportunitySubSpLinkT : opportunitySubSpLinkTs) {
				if (opportunitySubSpLinkT.isSubspPrimary()) {
					displaySubSp = opportunitySubSpLinkT.getSubSpMappingT()
							.getDisplaySubSp();
					break;
				}
			}
		}
		return displaySubSp;
	}

	/**
	 * retrieves win factors of a won opportunity
	 * 
	 * @param oppWinLossFactors
	 * @return
	 */
	private List<String> getWinFactors(
			List<OpportunityWinLossFactorsT> oppWinLossFactors) {
		logger.debug("Inside getWinFactors method");
		List<String> oppWinFactors = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(oppWinLossFactors)) {
			for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : oppWinLossFactors) {
				if (opportunityWinLossFactorsT.getWinLossFactor().equals(
						Constants.WIN_OTHER)) {
					oppWinFactors.add(opportunityWinLossFactorsT
							.getWinLossOthersDescription());
				} else {
					oppWinFactors.add(opportunityWinLossFactorsT
							.getWinLossFactor());
				}
			}
		}
		return oppWinFactors;
	}

	/**
	 * retrieves Loss factors for a Lost opportunity
	 * 
	 * @param oppWinLossFactors
	 * @return
	 */
	private List<String> getLossFactors(
			List<OpportunityWinLossFactorsT> oppWinLossFactors) {
		logger.debug("Inside getLossFactors method");
		List<String> oppLossFactors = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(oppWinLossFactors)) {
			for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : oppWinLossFactors) {
				if (opportunityWinLossFactorsT.getWinLossFactor().equals(
						Constants.LOSS_OTHER)) {
					oppLossFactors.add(opportunityWinLossFactorsT
							.getWinLossOthersDescription());
				} else {
					oppLossFactors.add(opportunityWinLossFactorsT
							.getWinLossFactor());
				}
			}
		}
		return oppLossFactors;
	}

	/**
	 * retrieves competitors of an opportunity
	 * 
	 * @param oppCompetitors
	 * @return
	 */
	private List<String> getCompetitors(
			List<OpportunityCompetitorLinkT> oppCompetitors) {
		logger.debug("Inside getCompetitors");
		List<String> oppCompetitor = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(oppCompetitors)) {
			for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : oppCompetitors) {
				oppCompetitor.add(opportunityCompetitorLinkT
						.getCompetitorName());
			}
		}
		return oppCompetitor;
	}

}
