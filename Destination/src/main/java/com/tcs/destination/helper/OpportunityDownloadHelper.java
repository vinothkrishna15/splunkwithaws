package com.tcs.destination.helper;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.BidRequestTypeMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DealTypeMappingT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityOfferingLinkT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.WinLossFactorMappingT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.BidRequestTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.DealTypeRepository;
import com.tcs.destination.data.repository.NotesTRepository;
import com.tcs.destination.data.repository.OpportunityCompetitorLinkTRepository;
import com.tcs.destination.data.repository.OpportunityCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.OpportunityOfferingLinkTRepository;
import com.tcs.destination.data.repository.OpportunityPartnerLinkTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.OpportunitySalesSupportLinkTRepository;
import com.tcs.destination.data.repository.OpportunitySubSpLinkTRepository;
import com.tcs.destination.data.repository.OpportunityTcsAccountContactLinkTRepository;
import com.tcs.destination.data.repository.OpportunityWinLossFactorsTRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.WinLossMappingRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.BeaconConverterService;

@Component("opportunityDownloadHelper")
public class OpportunityDownloadHelper {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	SubSpRepository subSpRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	BeaconConvertorRepository beaconConvertorRepository;

	@Autowired
	WinLossMappingRepository winLossMappingRepository;

	@Autowired
	BidRequestTypeRepository bidRequestTypeRepository;

	@Autowired
	DealTypeRepository dealTypeRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	@Autowired
	OpportunityCompetitorLinkTRepository opportunityCompetitorLinkTRepository;

	@Autowired
	OpportunityCustomerContactLinkTRepository opportunityCustomerContactLinkTRepository;

	@Autowired
	OpportunityOfferingLinkTRepository opportunityOfferingLinkTRepository;

	@Autowired
	OpportunitySubSpLinkTRepository opportunitySubSpLinkTRepository;

	@Autowired
	OpportunityTcsAccountContactLinkTRepository opportunityTcsAccountContactLinkTRepository;

	@Autowired
	OpportunityWinLossFactorsTRepository opportunityWinLossFactorsTRepository;

	@Autowired
	OpportunitySalesSupportLinkTRepository opportunitySalesSupportLinkTRepository;

	@Autowired
	OpportunityPartnerLinkTRepository opportunityPartnerLinkTRepository;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

	@Autowired
	NotesTRepository notesTRepository;

	private static final DateFormat actualFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static final DateFormat desiredFormat = new SimpleDateFormat(
			"MM/dd/yy");

	/**
	 * this method populates the opportunity values and setting it in the
	 * corresponding cells of opportunity sheet
	 * 
	 * @param opportunitySheet
	 * @param items
	 * @param rowCount
	 * @return int
	 * @throws Exception
	 */
	public int populateOpportunitySheet(Sheet opportunitySheet,
			List<? extends OpportunityT> items, int rowCount) throws Exception {

		for (OpportunityT opportunity : items) {
			Row row = opportunitySheet.createRow(rowCount);

			// Opportunity Id
			Cell cellOppId = row.createCell(1);
			cellOppId.setCellValue(opportunity.getOpportunityId().trim());

			// IOU
			Cell cellIOU = row.createCell(3);
			cellIOU.setCellValue(opportunity.getCustomerMasterT().getIou());

			// Client Geo
			Cell cellGeo = row.createCell(4);
			cellGeo.setCellValue(opportunity.getCustomerMasterT()
					.getGeography());

			// Country
			Cell cellCountry = row.createCell(5);
			cellCountry.setCellValue(opportunity.getCountry().trim());

			// Crm Id
			if (opportunity.getCrmId() != null) {
				Cell cellCrmId = row.createCell(6);
				cellCrmId.setCellValue(opportunity.getCrmId().trim());
			}

			// Opp Name
			Cell cellOppName = row.createCell(7);
			cellOppName.setCellValue(opportunity.getOpportunityName().trim());

			// Opp Description
			if (opportunity.getOpportunityDescription() != null) {
				Cell cellOppDesc = row.createCell(8);
				cellOppDesc.setCellValue(opportunity
						.getOpportunityDescription().trim());
			}

			// Request Receive Date
			Cell cellOppReqReceiveDate = row.createCell(11);
			cellOppReqReceiveDate.setCellValue(getFormattedDate(opportunity
					.getOpportunityRequestReceiveDate().toString(),
					actualFormat, desiredFormat));

			// New Logo
			if (opportunity.getNewLogo() != null) {
				Cell cellNewLogo = row.createCell(12);
				cellNewLogo.setCellValue(opportunity.getNewLogo().trim());
			}

			// strategic initiative
			if (opportunity.getStrategicInitiative() != null) {
				Cell cellSI = row.createCell(13);
				cellSI.setCellValue(opportunity.getStrategicInitiative().trim());
			}

			// Digital Flag
			if (opportunity.getDigitalFlag() != null) {
				Cell cellDigitalFlag = row.createCell(14);
				cellDigitalFlag.setCellValue(opportunity.getDigitalFlag()
						.trim());
			}

			// SALES STAGE CODE
			Cell cellSSCode = row.createCell(15);
			cellSSCode.setCellValue(opportunity.getSalesStageMappingT()
					.getSalesStageDescription());

			// DEAL CURRENCY
			if (opportunity.getDealCurrency() != null) {
				Cell cellDealCurrency = row.createCell(16);
				cellDealCurrency.setCellValue(opportunity.getDealCurrency());
			}

			// OVERALL DEAL SIZE
			if (opportunity.getOverallDealSize() != null) {
				Cell cellOverallDealValue = row.createCell(17);
				cellOverallDealValue.setCellValue(opportunity
						.getOverallDealSize());
			}

			// OVERALL DEAL SIZE in USD
			// if(dealValueFlag){
			if (opportunity.getOverallDealSize() != null) {
				Cell cellOverallDealValue = row.createCell(18);
				cellOverallDealValue.setCellValue(convertCurrencyToUSD(
						opportunity.getDealCurrency(),
						opportunity.getOverallDealSize()).doubleValue());
			}
			// }

			// DIGITAL DEAL VALUE
			if (opportunity.getDigitalDealValue() != null) {
				Cell cellDigitalDealValue = row.createCell(19);
				cellDigitalDealValue.setCellValue(opportunity
						.getDigitalDealValue());
			}

			// DIGITAL DEAL SIZE in USD
			// if(dealValueFlag){
			if (opportunity.getDigitalDealValue() != null) {
				Cell cellDigitalDealValue = row.createCell(20);
				cellDigitalDealValue.setCellValue(convertCurrencyToUSD(
						opportunity.getDealCurrency(),
						opportunity.getDigitalDealValue()).doubleValue());
			}
			// }

			// OPPORTUNITY OWNER
			Cell cellOppOwner = row.createCell(21);
			cellOppOwner.setCellValue(opportunity.getPrimaryOwnerUser()
					.getUserName());

			// DEAL TYPE
			if (opportunity.getDealType() != null) {
				Cell cellDealType = row.createCell(35);
				cellDealType.setCellValue(opportunity.getDealType());
			}

			// DEAL CLOSURE DATE
			if (opportunity.getDealClosureDate() != null) {
				Cell cellClosureDate = row.createCell(36);
				cellClosureDate.setCellValue(getFormattedDate(opportunity
						.getDealClosureDate().toString(), actualFormat,
						desiredFormat));
			}

			// ENGAGEMENT DURATION
			if (opportunity.getEngagementDuration() != null) {
				Cell cellEngDuration = row.createCell(37);
				cellEngDuration.setCellValue(opportunity
						.getEngagementDuration());
			}

			// ENGAGEMENT START DATE
			if (opportunity.getEngagementStartDate() != null) {
				Cell cellEngStartDate = row.createCell(38);
				cellEngStartDate.setCellValue(getFormattedDate(opportunity
						.getEngagementStartDate().toString(), actualFormat,
						desiredFormat));
			}

			// COMMENTS FOR WIN LOSS
			if (opportunity.getDescriptionForWinLoss() != null) {
				Cell cellCommentsWinLoss = row.createCell(40);
				cellCommentsWinLoss.setCellValue(opportunity
						.getDescriptionForWinLoss());
			}

			// CUSTOMER NAME
			Cell cellCustomerName = row.createCell(2);
			cellCustomerName.setCellValue(opportunity.getCustomerMasterT()
					.getCustomerName());

			// SubSp
			List<OpportunitySubSpLinkT> opportunitySubSpLinkTs = opportunitySubSpLinkTRepository
					.findByOpportunityId(opportunity.getOpportunityId());
			if ((opportunitySubSpLinkTs != null)
					&& (!opportunitySubSpLinkTs.isEmpty())) {
				Cell cellSubSp = row.createCell(9);
				if (opportunitySubSpLinkTs.size() == 1) {
					cellSubSp.setCellValue(opportunitySubSpLinkTs.get(0)
							.getSubSp().trim());
				} else if (opportunitySubSpLinkTs.size() > 1) {
					cellSubSp
							.setCellValue(constructSubSpCell(opportunitySubSpLinkTs));
				}
			}

			// Offering
			List<OpportunityOfferingLinkT> opportunityOfferingLinkTs = opportunityOfferingLinkTRepository
					.findByOpportunityId(opportunity.getOpportunityId());
			if ((opportunityOfferingLinkTs != null)
					&& (!opportunityOfferingLinkTs.isEmpty())) {
				Cell cellOffering = row.createCell(10);
				if (opportunityOfferingLinkTs.size() == 1) {
					cellOffering.setCellValue(opportunityOfferingLinkTs.get(0)
							.getOffering().trim());
				} else if (opportunityOfferingLinkTs.size() > 1) {
					cellOffering
							.setCellValue(constructOfferingCell(opportunityOfferingLinkTs));
				}
			}

			// SALES SUPPORT OWNER - Commented on demand
			// if((opp.getOpportunitySalesSupportLinkTs()!=null)&&(!opp.getOpportunitySalesSupportLinkTs().isEmpty())){
			// Cell cellSSOwner = row.createCell(22);
			// if(opp.getOpportunitySalesSupportLinkTs().size()==1){
			// cellSSOwner.setCellValue(opp.getOpportunitySalesSupportLinkTs().get(0).getSalesSupportOwnerUser().getUserName().trim());
			// } else if(opp.getOpportunitySalesSupportLinkTs().size()>1) {
			// cellSSOwner.setCellValue(constructSalesSupportOwner(opp.getOpportunitySalesSupportLinkTs()));
			// }
			// }

			// PARTNER NAME
			List<OpportunityPartnerLinkT> opportunityPartnerLinkTs = opportunityPartnerLinkTRepository
					.findByOpportunityId(opportunity.getOpportunityId());
			if ((opportunityPartnerLinkTs != null)
					&& (!opportunityPartnerLinkTs.isEmpty())) {
				Cell cellPartner = row.createCell(25);
				if (opportunityPartnerLinkTs.size() == 1) {
					cellPartner.setCellValue(opportunityPartnerLinkTs.get(0)
							.getPartnerMasterT().getPartnerName().trim());
				} else if (opportunityPartnerLinkTs.size() > 1) {
					cellPartner
							.setCellValue(constructPartners(opportunityPartnerLinkTs));
				}
			}

			// COMPETITOR NAME
			List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs = opportunityCompetitorLinkTRepository
					.findByOpportunityId(opportunity.getOpportunityId());
			if ((opportunityCompetitorLinkTs != null)
					&& (!opportunityCompetitorLinkTs.isEmpty())) {
				Cell cellComp = row.createCell(26);
				if (opportunityCompetitorLinkTs.size() == 1) {
					cellComp.setCellValue(opportunityCompetitorLinkTs.get(0)
							.getCompetitorName().trim());
				} else if (opportunityCompetitorLinkTs.size() > 1) {
					cellComp.setCellValue(constructCompetitors(opportunityCompetitorLinkTs));
				}
			}

			// FACTORS FOR WIN LOSS
			List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs = opportunityWinLossFactorsTRepository
					.findByOpportunityId(opportunity.getOpportunityId());
			if ((opportunityWinLossFactorsTs != null)
					&& (!opportunityWinLossFactorsTs.isEmpty())) {
				Cell cellFactors = row.createCell(39);
				if (opportunityWinLossFactorsTs.size() == 1) {
					cellFactors.setCellValue(opportunityWinLossFactorsTs.get(0)
							.getWinLossFactorMappingT().getWinLossFactor()
							.trim());
				} else if (opportunityWinLossFactorsTs.size() > 1) {
					cellFactors
							.setCellValue(constructWinLossFactors(opportunityWinLossFactorsTs));
				}
			}

			// BID Details - Bid Req Type
			List<BidDetailsT> bidDetails = bidDetailsTRepository
					.findByOpportunityId(opportunity.getOpportunityId());
			if ((bidDetails != null) && (!bidDetails.isEmpty())) {

				// Req Type
				Cell cellBidReqType = row.createCell(27);
				cellBidReqType.setCellValue(bidDetails.get(0)
						.getBidRequestType());

				// Req Receive Date
				Cell cellBidReqReceiveDate = row.createCell(29);
				cellBidReqReceiveDate.setCellValue(getFormattedDate(bidDetails
						.get(0).getBidRequestReceiveDate().toString(),
						actualFormat, desiredFormat));

				// Target Date
				Cell cellTargetDate = row.createCell(30);
				cellTargetDate.setCellValue(getFormattedDate(bidDetails.get(0)
						.getTargetBidSubmissionDate().toString(), actualFormat,
						desiredFormat));

				// Actual Date
				if (bidDetails.get(0).getActualBidSubmissionDate() != null) {
					Cell cellActualDate = row.createCell(31);
					cellActualDate.setCellValue(getFormattedDate(bidDetails
							.get(0).getActualBidSubmissionDate().toString(),
							actualFormat, desiredFormat));
				}

				// Expected Date
				if (bidDetails.get(0).getExpectedDateOfOutcome() != null) {
					Cell cellExpDate = row.createCell(32);
					cellExpDate.setCellValue(getFormattedDate(bidDetails.get(0)
							.getExpectedDateOfOutcome().toString(),
							actualFormat, desiredFormat));
				}

				// Win probability
				if (bidDetails.get(0).getWinProbability() != null) {
					Cell cellWinProb = row.createCell(33);
					cellWinProb.setCellValue(bidDetails.get(0)
							.getWinProbability().trim());
				}

				// Core Attributes
				if (bidDetails.get(0).getCoreAttributesUsedForWinning() != null) {
					Cell cellWinProb = row.createCell(34);
					cellWinProb.setCellValue(bidDetails.get(0)
							.getCoreAttributesUsedForWinning().trim());
				}

			}

			// Deal Status Remarks
			List<NotesT> oppNotes = notesTRepository
					.findByOpportunityId(opportunity.getOpportunityId());
			if ((oppNotes != null) && (!oppNotes.isEmpty())) {
				Cell cellNotes = row.createCell(41);
				cellNotes.setCellValue(oppNotes.get(0).getNotesUpdated());
			}
			
			//Created Date
			Cell cellCreatedDate = row.createCell(42);
			cellCreatedDate.setCellValue(getFormattedDate(opportunity.getCreatedDatetime().toString(),actualFormat, desiredFormat));
			
			//Created By
			Cell cellCreatedBy = row.createCell(43);
			cellCreatedBy.setCellValue(opportunity.getCreatedBy());
			
			//Modified Date
			Cell cellModifiedDate = row.createCell(44);
			cellModifiedDate.setCellValue(getFormattedDate(opportunity.getModifiedDatetime().toString(),actualFormat, desiredFormat));
			
			//Modified By
			Cell cellModifiedBy = row.createCell(45);
			cellModifiedBy.setCellValue(opportunity.getModifiedBy());
			
			rowCount++;
		}
		return rowCount;

	}

	/**
	 * Method to convert date in a format to another format
	 * 
	 * @param Stringdate
	 * @param actualFormat
	 * @param destFormat
	 * @return String
	 * @throws Exception
	 */
	public static String getFormattedDate(String Stringdate,
			DateFormat actualFormat, DateFormat destFormat) throws Exception {

		return destFormat.format(actualFormat.parse(Stringdate));
	}

	/**
	 * This method converts the given
	 * 
	 * @param dealCurrency
	 * @param overallDealSize
	 * @return
	 * @throws Exception
	 */
	private BigDecimal convertCurrencyToUSD(String dealCurrency,
			Integer overallDealSize) throws Exception {

		return beaconConverterService.convertCurrencyRate(dealCurrency, "USD",
				overallDealSize.doubleValue());
	}

	/**
	 * This method converts the list to subsp names to a string separated by
	 * commas
	 * 
	 * @param opportunitySubSpLinkTs
	 * @return String
	 */
	private String constructSubSpCell(
			List<OpportunitySubSpLinkT> opportunitySubSpLinkTs) {

		StringBuilder subSpBuffer = new StringBuilder();

		for (OpportunitySubSpLinkT osst : opportunitySubSpLinkTs) {
			subSpBuffer.append(osst.getSubSp().trim() + ",");
		}

		if (subSpBuffer.length() > 0) {
			subSpBuffer.deleteCharAt(subSpBuffer.length() - 1);
		}

		return subSpBuffer.toString();
	}

	/**
	 * This method converts the list to offering names to a string separated by
	 * commas
	 * 
	 * @param opportunityOfferingLinkTs
	 * @return String
	 */
	private String constructOfferingCell(
			List<OpportunityOfferingLinkT> opportunityOfferingLinkTs) {

		StringBuilder offeringBuffer = new StringBuilder();

		for (OpportunityOfferingLinkT oolt : opportunityOfferingLinkTs) {
			offeringBuffer.append(oolt.getOffering().trim() + ",");
		}

		if (offeringBuffer.length() > 0) {
			offeringBuffer.deleteCharAt(offeringBuffer.length() - 1);
		}

		return offeringBuffer.toString();
	}

	private String constructWinLossFactors(
			List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs) {

		StringBuilder buffer = new StringBuilder();

		for (OpportunityWinLossFactorsT link : opportunityWinLossFactorsTs) {
			buffer.append(link.getWinLossFactorMappingT().getWinLossFactor()
					.trim()
					+ ",");
		}

		if (buffer.length() > 0) {
			buffer.deleteCharAt(buffer.length() - 1);
		}

		return buffer.toString();

	}

	private String constructCompetitors(
			List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs) {

		StringBuilder buffer = new StringBuilder();

		for (OpportunityCompetitorLinkT link : opportunityCompetitorLinkTs) {
			buffer.append(link.getCompetitorName().trim() + ",");
		}

		if (buffer.length() > 0) {
			buffer.deleteCharAt(buffer.length() - 1);
		}

		return buffer.toString();
	}

	private String constructPartners(
			List<OpportunityPartnerLinkT> opportunityPartnerLinkTs) {

		StringBuilder buffer = new StringBuilder();

		for (OpportunityPartnerLinkT link : opportunityPartnerLinkTs) {
			buffer.append(link.getPartnerMasterT().getPartnerName().trim()
					+ ",");
		}

		if (buffer.length() > 0) {
			buffer.deleteCharAt(buffer.length() - 1);
		}

		return buffer.toString();
	}

	private String constructSalesSupportOwner(
			List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs) {

		StringBuilder buffer = new StringBuilder();

		for (OpportunitySalesSupportLinkT link : opportunitySalesSupportLinkTs) {
			buffer.append(link.getSalesSupportOwnerUser().getUserName().trim()
					+ ",");
		}

		if (buffer.length() > 0) {
			buffer.deleteCharAt(buffer.length() - 1);
		}

		return buffer.toString();
	}

	/**
	 * This method populates the Customer Master Sheet
	 * 
	 * @param customerMasterSheet
	 */
	public int populateCustomerMasterSheet(Sheet customerMasterSheet,
			List<? extends CustomerMasterT> items, int rowCount)
			throws Exception {
		// Excluding the header, header starts with index 0
		for (CustomerMasterT cmt : items) {
			// Create row with rowCount
			Row row = customerMasterSheet.createRow(rowCount);

			// Create new Cell and set cell value
			Cell cellGrpClient = row.createCell(0);
			cellGrpClient.setCellValue(cmt.getGroupCustomerName().trim());

			Cell cellCustName = row.createCell(1);
			cellCustName.setCellValue(cmt.getCustomerName().trim());

			Cell cellIou = row.createCell(2);
			cellIou.setCellValue(cmt.getIouCustomerMappingT().getIou());

			Cell cellGeo = row.createCell(3);
			cellGeo.setCellValue(cmt.getGeographyMappingT().getGeography()
					.trim());

			// Increment row counter
			rowCount++;
		}
		return rowCount;

	}

	/**
	 * This method populates the SubSp sheet
	 * 
	 * @param subSpSheet
	 */
	public int populateSubSpSheet(Sheet subSpSheet,
			List<? extends SubSpMappingT> items, int rowCount) throws Exception {

		for (SubSpMappingT ssmt : items) {
			// Create row with rowCount
			Row row = subSpSheet.createRow(rowCount);

			// Create new Cell and set cell value
			Cell cellActualSp = row.createCell(0);
			cellActualSp.setCellValue(ssmt.getActualSubSp().trim());

			Cell cellSp = row.createCell(1);
			cellSp.setCellValue(ssmt.getSubSp().trim());

			Cell cellDisplaySp = row.createCell(2);
			cellDisplaySp.setCellValue(ssmt.getDisplaySubSp());

			Cell cellSpCode = row.createCell(3);
			if (ssmt.getSpCode() != null) {
				cellSpCode.setCellValue(ssmt.getSpCode());
			}

			Cell cellActive = row.createCell(4);
			if (ssmt.getActive() != null) {
				cellActive.setCellValue(ssmt.getActive().trim());
			}

			// Increment row counter
			rowCount++;
		}
		return rowCount;
	}

	/**
	 * This method populates the partner sheet
	 * 
	 * @param partnerSheet
	 */
	public int populatePartnerSheet(Sheet partnerSheet,
			List<? extends Object[]> items, int rowCount) throws Exception {
		// Excluding the header, header starts with index 0
		for (Object[] ob : items) {
			// Create row with rowCount
			Row row = partnerSheet.createRow(rowCount);

			// Create new Cell and set cell value
			Cell cellPartnerName = row.createCell(0);
			cellPartnerName.setCellValue(ob[0].toString().trim());

			Cell cellGeo = row.createCell(1);
			cellGeo.setCellValue(ob[1].toString().trim());

			// Increment row counter
			rowCount++;
		}
		return rowCount;

	}

	/**
	 * This method populates the partner contacts sheet
	 * 
	 * @param partnerContactSheet
	 */
	public int populateContactSheets(Sheet partnerContactSheet,
			List<? extends ContactT> items, int rowCount) throws Exception {

		// Excluding the header, header starts with index 0
		for (ContactT ct : items) {

			if ((ct.getContactCategory().equals(EntityType.PARTNER.toString()) && (ct
					.getContactType().equals(ContactType.EXTERNAL.toString())))) { // For
																					// Partner
																					// Contact

				// Create row with rowCount
				Row row = partnerContactSheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellPartnerName = row.createCell(0);
				try {
					cellPartnerName.setCellValue(ct.getPartnerMasterT()
							.getPartnerName().trim());
				} catch (NullPointerException npe) {
					throw new DestinationException(
							HttpStatus.INTERNAL_SERVER_ERROR,
							"Partner Contact cannot exist without Partner");
				}

				Cell cellPartnerContactName = row.createCell(1);
				cellPartnerContactName.setCellValue(ct.getContactName());

				Cell cellPartnerContactRole = row.createCell(2);
				cellPartnerContactRole.setCellValue(ct.getContactRole());

				Cell cellPartnerContactEmailId = row.createCell(3);
				if (ct.getContactEmailId() != null) {
					cellPartnerContactEmailId.setCellValue(ct
							.getContactEmailId());
				}

				// Increment row counter for partner contact sheet
				rowCount++;

			}
		}
		return rowCount;
	}

	/**
	 * This method populates the currency sheet
	 * 
	 * @param currencySheet
	 */
	public int populateCurrencySheet(Sheet winLossSheet,
			List<? extends BeaconConvertorMappingT> items, int rowCount)
			throws Exception {

		// Excluding the header, header starts with index 0
		for (BeaconConvertorMappingT bcmt : items) {
			// Create row with rowCount
			Row row = winLossSheet.createRow(rowCount);

			// Create new Cell and set cell value
			Cell cellCurrencyName = row.createCell(0);
			cellCurrencyName.setCellValue(bcmt.getCurrencyName().trim());

			Cell cellCurrencyValue = row.createCell(1);
			cellCurrencyValue.setCellValue(bcmt.getConversionRate()
					.doubleValue());

			// Increment row counter
			rowCount++;
		}
		return rowCount;

	}

	/**
	 * This method populates the win Loss sheet
	 * 
	 * @param currencySheet
	 */
	public int populateWinLossSheet(Sheet currencySheet,
			List<? extends WinLossFactorMappingT> items, int rowCount)
			throws Exception {

		// Excluding the header, header starts with index 0
		for (WinLossFactorMappingT wlm : items) {

			// Create row with rowCount
			Row row = currencySheet.createRow(rowCount);

			// Create new Cell and set cell value
			Cell cellType = row.createCell(0);
			cellType.setCellValue(wlm.getType().trim());

			Cell cellFactor = row.createCell(1);
			cellFactor.setCellValue(wlm.getWinLossFactor().trim());

			// Increment row counter
			rowCount++;
		}
		return rowCount;

	}

	/**
	 * This method populates Deal Types and Bid Request Types
	 * 
	 * @param bidRequestDealType
	 */
	public void populateBidRequestDealTypeSheet(Sheet bidRequestDealType) {

		List<BidRequestTypeMappingT> listOfBids = (List<BidRequestTypeMappingT>) bidRequestTypeRepository
				.findAll();

		List<DealTypeMappingT> listOfDeals = (List<DealTypeMappingT>) dealTypeRepository
				.findAll();

		int sizeOfBids = 0;
		int sizeOfDeals = 0;
		if (listOfBids != null) {
			sizeOfBids = listOfBids.size();
		}
		if (listOfDeals != null) {
			sizeOfDeals = listOfDeals.size();
		}

		int rowCount = 1;

		while ((rowCount <= sizeOfBids) || (rowCount <= sizeOfDeals)) {

			Row row = bidRequestDealType.createRow(rowCount);

			if (rowCount <= sizeOfBids) {
				Cell cellBid = row.createCell(0);
				cellBid.setCellValue(listOfBids.get(rowCount - 1)
						.getBidRequestType().trim());
			}
			if (rowCount <= sizeOfDeals) {
				Cell cellDeal = row.createCell(2);
				cellDeal.setCellValue(listOfDeals.get(rowCount - 1)
						.getDealType().trim());
			}

			rowCount++;

		}
	}

}
