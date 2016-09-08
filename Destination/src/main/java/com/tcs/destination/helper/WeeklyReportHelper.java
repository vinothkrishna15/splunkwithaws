package com.tcs.destination.helper;

import static com.tcs.destination.utils.DateUtils.DATE_FORMAT_MONTH_NAME;
import static com.tcs.destination.utils.DateUtils.ACTUAL_FORMAT;
import static com.tcs.destination.utils.DateUtils.DATE_FORMAT_MONTH;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.ConnectCustomer;
import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectPartner;
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
import com.tcs.destination.utils.ReportUtil;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;

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
	public JasperReportBuilder constructWeeklyReport(List<String> geos,
			Date currentDate, Date previousWeekDate, String geography, int weekNumber, String financialYear)
			throws Exception {

		logger.info("constructing weekly report for geography : " + geography);
		Date previousDate = DateUtils.getPreviousDate();
		String currentDateString = DATE_FORMAT_MONTH_NAME.format(currentDate);
		String previousDateString = DATE_FORMAT_MONTH.format(previousDate);
		String previousWeekDateString = DATE_FORMAT_MONTH.format(previousWeekDate);
		FontBuilder defaultFont = DynamicReports.stl.font().setFontName(ReportUtil.FONT_MYRIAD_PRO);

		StyleBuilder boldStyle = DynamicReports.stl.style().setFontSize(ReportUtil.FONT_SIZE_TITLE)
				.bold();
		
		StyleBuilder subTitleStyle = DynamicReports.stl.style().setFontSize(ReportUtil.FONT_SIZE_SUB_TITLE)
				.bold();

		StyleBuilder reportDateStyle = DynamicReports.stl.style().setFontSize(
				11).setMarkup(Markup.STYLED);
		
		// Opp Wins
		List<OpportunityWins> opportunityWins = getOpportunityWins(geos,
				currentDate, previousWeekDate);

		TextFieldBuilder<String> winTitle = getTitle(opportunityWins, "Win(s) Reported-");
		// Opp Loss
		List<OpportunityLoss> opportunityLoss = getOpportunityLoss(geos,
				currentDate, previousWeekDate);

		TextFieldBuilder<String> lossTitle = getTitle(opportunityLoss, "Loss(s) Reported-");
		// Opp RFP Submitted
		List<OpportunityRFPSubmitted> oppRFPSubmitted = getOpportunityRFPSubmitted(
				geos, currentDate, previousWeekDate);

		TextFieldBuilder<String> rfpSubmittedTitle = getTitle(oppRFPSubmitted,
				"Bids submitted-");
		// Customer connects
		List<ConnectCustomer> customerConnects = getCustomerConnects(geos,
				currentDate, previousWeekDate);

		TextFieldBuilder<String> customerConnectsTitle = getTitle(
				customerConnects, "Customer connects this week-");
		// Partner connects
		List<ConnectPartner> partnerConnects = getPartnerConnects(geos,
				currentDate, previousWeekDate);

		TextFieldBuilder<String> partnerConnectsTitle = getTitle(
				partnerConnects, "Partner connects this week-");
		// Report for wins
		JasperReportBuilder reportForWins = null;
		reportForWins = ReportUtil.buildReport(winTitle, opportunityWins);
		logger.info("Report constructed for wins , Geography :" + geography);

		// Report for opportunity loss
		JasperReportBuilder reportForLoss = null;
		reportForLoss = ReportUtil.buildReport(lossTitle, opportunityLoss);
		logger.info("Report constructed for loss, Geography :" + geography);

		// Report for Opportunity RFP Submitted
		JasperReportBuilder reportForRFPSubmitted = null;
		reportForRFPSubmitted = ReportUtil.buildReport(rfpSubmittedTitle,
				oppRFPSubmitted);
		logger.info("Report constructed for RFP Submitted, Geography :"
				+ geography);

		// Report for customer connects
		JasperReportBuilder reportForCustomerConnects = null;
		reportForCustomerConnects = ReportUtil.buildReport(
				customerConnectsTitle, customerConnects);
		logger.info("Report constructed for customer connects, Geography :"
				+ geography);

		// Report for Partner connects
		JasperReportBuilder reportForPartnerConnects = null;
		reportForPartnerConnects = ReportUtil.buildReport(partnerConnectsTitle,
				partnerConnects);
		logger.info("Report constructed for partner connects, Geography :"
				+ geography);

		// Merging all the sub reports into a single report
		JasperReportBuilder report = report()
				.setDefaultFont(defaultFont)
				.title(cmp
						.text("Weekly Report for " + geography)
						.setStyle(boldStyle)
						.setHorizontalTextAlignment(
								HorizontalTextAlignment.CENTER),
						DynamicReports.cmp.verticalGap(5),
						cmp.text("(W" + weekNumber + "'" + financialYear + ": "
								+ previousWeekDateString + "-"
								+ previousDateString+")").setStyle(subTitleStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER),
								DynamicReports.cmp.verticalGap(10),
								cmp.text("<i>Report Date :</i> " + currentDateString)
										.setStyle(reportDateStyle)
										.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),			
						DynamicReports.cmp.verticalGap(20),
						cmp.verticalList(cmp.subreport(reportForWins),
								DynamicReports.cmp.verticalGap(30),
								cmp.subreport(reportForLoss),
								DynamicReports.cmp.verticalGap(35),
								cmp.subreport(reportForRFPSubmitted),
								DynamicReports.cmp.verticalGap(40),
								cmp.subreport(reportForCustomerConnects),
								DynamicReports.cmp.verticalGap(40),
								cmp.subreport(reportForPartnerConnects)))
				.setPageMargin(
						DynamicReports.margin(20).setLeft(10).setRight(10))
				.setPageFormat(PageType.A4, PageOrientation.LANDSCAPE)
				.pageFooter(
						Components.text("TCS Confidential")
								.setHorizontalTextAlignment(
										HorizontalTextAlignment.RIGHT));

		return report;
	}

	/**
	 * builds title for weekly report
	 * 
	 * @param values
	 * @param appendValue
	 * @return
	 */
	private TextFieldBuilder<String> getTitle(List<?> values, String appendValue) {
//		return Components.text(new StringBuffer(new Integer(values.size())
//				.toString()).append(appendValue).toString());
		return Components.text(new StringBuffer(appendValue).append(" ")
				.append(values.size()).toString());
	}

	/**
	 * Retrieves the partner connects during a week
	 * 
	 * @param geos
	 * @param currentDate
	 * @param previousWeekDate
	 * @return
	 */
	private List<ConnectPartner> getPartnerConnects(List<String> geos,
			Date currentDate, Date previousWeekDate) {
		logger.debug("Inside getPartnerConnects method");
		List<ConnectPartner> partnerConnects = Lists.newArrayList();
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
				List<String> connectSubSp = Lists.newArrayList();
				ConnectPartner partnerConnect = new ConnectPartner();
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
					contactNames = StringUtils.join(connectContactNames, "\n");
				}
				if (CollectionUtils.isNotEmpty(connectContactRoles)) {
					contactRoles = StringUtils.join(connectContactRoles, "\n");
				}
				partnerConnect.setPartnerContact(getValue(contactNames));
				partnerConnect.setPartnerContactRole(getValue(contactRoles));
				// Connect Type
				partnerConnect.setConnectCategory(StringUtils
						.isNotEmpty(connectT.getType()) ? connectT.getType()
						: Constants.NOT_AVAILABLE);

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
				List<String> connectSubSp = Lists.newArrayList();
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
					contactNames = StringUtils.join(connectContactNames, "\n");
				}
				if (CollectionUtils.isNotEmpty(connectContactRoles)) {
					contactRoles = StringUtils.join(connectContactRoles, "\n");
				}

				customerConnect.setCustomerContact(getValue(contactNames));
				customerConnect.setCustomerContactRole(getValue(contactRoles));

				// Connect Type
				customerConnect
						.setConnectCategory(getValue(connectT.getType()));

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
	private List<String> getDisplaySubSpsOfConnect(
			List<ConnectSubSpLinkT> connectSubSpLinkTs) {
		List<String> displaySubSps = Lists.newArrayList();
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
				String dealValue = NumericUtil
						.toUSDinNumberScale(opportunityDownloadService
								.convertCurrencyToUSD(opp.getDealCurrency(),
										opp.getDigitalDealValue()));
				dealValue = StringUtils.remove(dealValue, "USD");
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
				String dealValue = NumericUtil
						.toUSDinNumberScale(opportunityDownloadService
								.convertCurrencyToUSD(opp.getDealCurrency(),
										opp.getDigitalDealValue()));
				dealValue = StringUtils.remove(dealValue, "USD");
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
				String dealValue = NumericUtil
						.toUSDinNumberScale(opportunityDownloadService
								.convertCurrencyToUSD(opp.getDealCurrency(),
										opp.getDigitalDealValue()));
				dealValue = StringUtils.remove(dealValue, "USD");
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
