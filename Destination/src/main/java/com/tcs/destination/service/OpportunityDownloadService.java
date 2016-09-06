package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.BidRequestTypeMappingT;
import com.tcs.destination.bean.ContactCustomerLinkT;
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
import com.tcs.destination.bean.SalesStageMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.WinLossFactorMappingT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.BidRequestTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.DealTypeRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SalesStageMappingRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.WinLossMappingRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.PropertyUtil;

/**
 * this service handles requests related to 
 * opportunity downloads and populates all the opportunity sheet templates
 * on download request
 */
@Service
public class OpportunityDownloadService {

	@Autowired
	CommonWorkbookSheetsForDownloadServices commonWorkbookSheets;

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
	SalesStageMappingRepository salesStageMappingRepository;

	@Autowired
	DealTypeRepository dealTypeRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityService.class);

	private static final DateFormat actualFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat desiredFormat = new SimpleDateFormat("MM/dd/yy");

	/**
	 * method to download opportunity based on its deal value
	 * @param oppFlag
	 * @param userId
	 * @param dealValueFlag
	 * @return
	 * @throws Exception
	 */
	public InputStreamResource downloadDocument(boolean oppFlag, String userId, boolean dealValueFlag)
			throws Exception {

		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;

		logger.debug("Begin: Inside downloadDocument of OpportunityDownloadService");

		try {

			workbook =(XSSFWorkbook) ExcelUtils.getWorkBook(new File
					(PropertyUtil.getProperty
							(Constants.OPPORTUNITY_TEMPLATE_LOCATION_PROPERTY_NAME)));

			if(oppFlag){

				// Populate Opportunity Sheet
				populateOpportunitySheet(workbook
						.getSheet(Constants.OPPORTUNITY_TEMPLATE_OPPORTUNITY_SHEET_NAME), dealValueFlag);
			}

			// Populate Competitor Sheet
			commonWorkbookSheets.populateCompetitorRefSheet(workbook
					.getSheet(Constants.OPPORTUNITY_TEMPLATE_COMPETITOR_SHEET_NAME));

			// Populate Geography Country Ref
			commonWorkbookSheets
			.populateGeographyCountryRef(workbook
					.getSheet(Constants.OPPORTUNITY_TEMPLATE_GEOGRAPHY_COUNTRY_SHEET_NAME));

			// Populate Offering Ref
			commonWorkbookSheets.populateOfferingRefSheet(workbook
					.getSheet(Constants.OPPORTUNITY_TEMPLATE_OFFERING_SHEET_NAME));

			// Populate User Ref
			commonWorkbookSheets.populateUserRefSheet(workbook
					.getSheet(Constants.OPPORTUNITY_TEMPLATE_USER_SHEET_NAME));

			// Populate Customer Master Ref
			populateCustomerMasterSheet(workbook
					.getSheet(Constants.OPPORTUNITY_TEMPLATE_CUSTOMER_MASTER_SHEET_NAME));

			//populate tcs acount and customer contact
			populateTcsAndCustomerContactSheet(workbook
					.getSheet(Constants.OPPORTUNITY_TEMPLATE_TCS_AND_CUSTOMER_SHEET_NAME));

			// Populate SubSp Ref
			populateSubSpSheet(workbook
					.getSheet(Constants.OPPORTUNITY_TEMPLATE_SUBSP_SHEET_NAME));

			populatePartnerSheet(workbook
					.getSheet(Constants.OPPORTUNITY_TEMPLATE_PARTNER_SHEET_NAME));

			//  populateContactSheets(workbook.getSheet(Constants.OPPORTUNITY_TEMPLATE_PARTNER_CONTACT_SHEET_NAME));

			populateCurrencySheet(workbook.getSheet(Constants.OPPORTUNITY_TEMPLATE_CURRENCY_SHEET_NAME));

			populateWinLossSheet(workbook.getSheet(Constants.OPPORTUNITY_TEMPLATE_WIN_LOSS_SHEET_NAME));

			populateBidRequestDealTypeSheet(workbook.getSheet(Constants.OPPORTUNITY_TEMPLATE_BID_REQUEST_DEAL_TYPE_SHEET_NAME));

			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));
			//	    FileOutputStream fos = new FileOutputStream("/Users/bnpp/Documents/abcde.xls");
			//	    fos.write(bytes);
			//	    fos.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An Internal Exception has occured");
		}
		logger.debug("End: Inside downloadDocument of OpportunityDownloadService");
		return inputStreamResource;
	}

	/**
	 * method to populate tcs and customer contact sheet
	 * @param tcsAccountCustomerContactSheet
	 */
	private void populateTcsAndCustomerContactSheet(Sheet tcsAccountCustomerContactSheet) {
		logger.debug("Begin:Inside populateTcsAndCustomerContactSheet of OpportunityDownloadService");
		List<ContactT> listOfContactT = (List<ContactT>) contactRepository
				.findAll();

		if(listOfContactT != null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (ContactT contact : listOfContactT) {
				// Create row with rowCount
				Row row = tcsAccountCustomerContactSheet.createRow(rowCount);

				String customerName = "";

				// Create new Cell and set cell value
				if (contact.getContactCategory().equalsIgnoreCase(EntityType.CUSTOMER.toString())) {

					Cell cellCustomerName = row.createCell(0);
					List<ContactCustomerLinkT> contactCustomerLinkTs =  contact.getContactCustomerLinkTs();
					if (contactCustomerLinkTs.size() > 0) {
						customerName = contactCustomerLinkTs.get(0).getCustomerMasterT().getCustomerName();
					}
					cellCustomerName.setCellValue(customerName.trim());

					Cell cellContactType = row.createCell(1);
					cellContactType.setCellValue(contact.getContactType().trim());

					Cell employeeNumber = row.createCell(2);
					employeeNumber.setCellValue(contact.getEmployeeNumber().trim());

					Cell cellContactName = row.createCell(3);
					cellContactName.setCellValue(contact.getContactName());

					Cell cellContactRole = row.createCell(4);
					cellContactRole.setCellValue(contact.getContactRole());

					Cell cellContactEmail = row.createCell(5);
					cellContactEmail.setCellValue(contact.getContactEmailId());

					Cell cellContactTelephone = row.createCell(6);
					cellContactTelephone.setCellValue(contact.getContactTelephone());
					
					// Increment row counter
					rowCount++;
				}
			}
		}
		logger.debug("End:Inside populateTcsAndCustomerContactSheet of OpportunityDownloadService");
	}

	/**
	 * method to populate the Opportunity sheet from OpportunityT table
	 * @param opportunitySheet
	 * @param dealValueFlag
	 * @throws Exception
	 */
	private void populateOpportunitySheet(Sheet opportunitySheet, boolean dealValueFlag) throws Exception{

		logger.debug("Begin: Inside populateOpportunitySheet of OpportunityDownloadService");

		List<OpportunityT> listOfOpportunity = opportunityRepository.findAll();

		int rowCount = 2;
		for(OpportunityT opp : listOfOpportunity){
			Row row = opportunitySheet.createRow(rowCount);

			//Opportunity Id
			Cell cellOppId = row.createCell(1);
			cellOppId.setCellValue(opp.getOpportunityId().trim());

			// Country
			Cell cellCountry = row.createCell(5);
			cellCountry.setCellValue(opp.getCountry().trim());

			//Crm Id
			if(opp.getCrmId()!=null) {
				Cell cellCrmId = row.createCell(6);
				cellCrmId.setCellValue(opp.getCrmId().trim());
			}

			//Opp Name
			Cell cellOppName = row.createCell(7);
			cellOppName.setCellValue(opp.getOpportunityName().trim());

			//Opp Description
			if(opp.getOpportunityDescription()!=null) {
				Cell cellOppDesc = row.createCell(8);
				cellOppDesc.setCellValue(opp.getOpportunityDescription().trim());
			}

			//Request Receive Date
			Cell cellOppReqReceiveDate = row.createCell(11);
			cellOppReqReceiveDate.setCellValue(getFormattedDate(opp.getOpportunityRequestReceiveDate().toString(), actualFormat, desiredFormat));

			//New Logo
			if(opp.getNewLogo()!=null){
				Cell cellNewLogo = row.createCell(12);
				cellNewLogo.setCellValue(opp.getNewLogo().trim());
			}

			//strategic initiative
			if(opp.getStrategicDeal()!=null){
				Cell cellSI = row.createCell(13);
				cellSI.setCellValue(opp.getStrategicDeal().trim());
			}

			//Digital Flag
			if(opp.getDigitalFlag()!=null){
				Cell cellDigitalFlag = row.createCell(14);
				cellDigitalFlag.setCellValue(opp.getDigitalFlag().trim());
			}

			//SALES STAGE CODE
			Cell cellSSCode = row.createCell(15);
			cellSSCode.setCellValue(opp.getSalesStageMappingT().getSalesStageDescription());

			//DEAL CURRENCY
			if(opp.getDealCurrency()!=null){
				Cell cellDealCurrency = row.createCell(16);
				cellDealCurrency.setCellValue(opp.getDealCurrency());
			}

			//OVERALL DEAL SIZE
			if(opp.getOverallDealSize()!=null) {
				Cell cellOverallDealValue = row.createCell(17);
				cellOverallDealValue.setCellValue(opp.getOverallDealSize());
			}

			//	OVERALL DEAL SIZE in USD
			if(dealValueFlag){
				if(opp.getOverallDealSize()!=null) {
					Cell cellOverallDealValue = row.createCell(18);
					cellOverallDealValue.setCellValue(convertCurrencyToUSD(opp.getDealCurrency(), opp.getOverallDealSize()).doubleValue());
				}
			}

			//DIGITAL DEAL VALUE
			if(opp.getDigitalDealValue()!=null){
				Cell cellDigitalDealValue = row.createCell(19);
				cellDigitalDealValue.setCellValue(opp.getDigitalDealValue());
			}

			// DIGITAL DEAL SIZE in USD
			if(dealValueFlag){
				if(opp.getDigitalDealValue()!=null) {
					Cell cellDigitalDealValue = row.createCell(20);
					cellDigitalDealValue.setCellValue(convertCurrencyToUSD(opp.getDealCurrency(), opp.getDigitalDealValue()).doubleValue());
				}
			}

			//OPPORTUNITY OWNER
			Cell cellOppOwner = row.createCell(21);
			cellOppOwner.setCellValue(opp.getPrimaryOwnerUser().getUserName());

			//DEAL TYPE
			if(opp.getDealType()!=null) {
				Cell cellDealType = row.createCell(35);
				cellDealType.setCellValue(opp.getDealType());
			}

			//DEAL CLOSURE DATE
			if(opp.getDealClosureDate()!=null) {
				Cell cellClosureDate = row.createCell(36);
				cellClosureDate.setCellValue(getFormattedDate(opp.getDealClosureDate().toString(), actualFormat, desiredFormat));
			}

			//ENGAGEMENT DURATION
			if(opp.getEngagementDuration()!=null) {
				Cell cellEngDuration = row.createCell(37);
				cellEngDuration.setCellValue(opp.getEngagementDuration());
			}

			//ENGAGEMENT START DATE
			if(opp.getEngagementStartDate()!=null) {
				Cell cellEngStartDate = row.createCell(38);
				cellEngStartDate.setCellValue(getFormattedDate(opp.getEngagementStartDate().toString(), actualFormat, desiredFormat));
			}

			//COMMENTS FOR WIN LOSS
			if(opp.getDealClosureComments()!=null) {
				Cell cellCommentsWinLoss = row.createCell(40);
				cellCommentsWinLoss.setCellValue(opp.getDealClosureComments());
			}

			//CUSTOMER NAME
			Cell cellCustomerName = row.createCell(2);
			cellCustomerName.setCellValue(opp.getCustomerMasterT().getCustomerName());

			//SubSp
			if((opp.getOpportunitySubSpLinkTs()!=null)&&(!opp.getOpportunitySubSpLinkTs().isEmpty())){
				Cell cellSubSp = row.createCell(9);
				if(opp.getOpportunitySubSpLinkTs().size()==1){
					cellSubSp.setCellValue(opp.getOpportunitySubSpLinkTs().get(0).getSubSp().trim());
				} else if(opp.getOpportunitySubSpLinkTs().size()>1){
					cellSubSp.setCellValue(constructSubSpCell(opp.getOpportunitySubSpLinkTs()));
				}
			}

			//IOU
			Cell cellIOU = row.createCell(3);
			cellIOU.setCellValue(opp.getCustomerMasterT().getIou());

			//Client Geo
			Cell cellGeo = row.createCell(4);
			cellGeo.setCellValue(opp.getCustomerMasterT().getGeography());

			//Offering
			if((opp.getOpportunityOfferingLinkTs()!=null)&&(!opp.getOpportunityOfferingLinkTs().isEmpty())){
				Cell cellOffering = row.createCell(10);
				if(opp.getOpportunityOfferingLinkTs().size()==1){
					cellOffering.setCellValue(opp.getOpportunityOfferingLinkTs().get(0).getOffering().trim());
				} else if(opp.getOpportunityOfferingLinkTs().size()>1){
					cellOffering.setCellValue(constructOfferingCell(opp.getOpportunityOfferingLinkTs()));
				}
			}

			//SALES SUPPORT OWNER - Commented on demand
			//	    if((opp.getOpportunitySalesSupportLinkTs()!=null)&&(!opp.getOpportunitySalesSupportLinkTs().isEmpty())){
			//	    Cell cellSSOwner = row.createCell(22);
			//		if(opp.getOpportunitySalesSupportLinkTs().size()==1){
			//		    cellSSOwner.setCellValue(opp.getOpportunitySalesSupportLinkTs().get(0).getSalesSupportOwnerUser().getUserName().trim());
			//		} else if(opp.getOpportunitySalesSupportLinkTs().size()>1) {		
			//		    cellSSOwner.setCellValue(constructSalesSupportOwner(opp.getOpportunitySalesSupportLinkTs()));
			//		}
			//	    }

			//PARTNER NAME
			if((opp.getOpportunityPartnerLinkTs()!=null)&&(!opp.getOpportunityPartnerLinkTs().isEmpty())){
				Cell cellPartner = row.createCell(25);
				if(opp.getOpportunityPartnerLinkTs().size()==1){
					cellPartner.setCellValue(opp.getOpportunityPartnerLinkTs().get(0).getPartnerMasterT().getPartnerName().trim());
				} else if(opp.getOpportunityPartnerLinkTs().size()>1){
					cellPartner.setCellValue(constructPartners(opp.getOpportunityPartnerLinkTs()));
				}  
			}

			//COMPETITOR NAME
			if((opp.getOpportunityCompetitorLinkTs()!=null)&&(!opp.getOpportunityCompetitorLinkTs().isEmpty())){
				Cell cellComp = row.createCell(26);
				if(opp.getOpportunityCompetitorLinkTs().size()==1){
					cellComp.setCellValue(opp.getOpportunityCompetitorLinkTs().get(0).getCompetitorName().trim());
				} else if(opp.getOpportunityCompetitorLinkTs().size()>1){
					cellComp.setCellValue(constructCompetitors(opp.getOpportunityCompetitorLinkTs()));
				}  
			}

			//FACTORS FOR WIN LOSS
			if((opp.getOpportunityWinLossFactorsTs()!=null)&&(!opp.getOpportunityWinLossFactorsTs().isEmpty())){
				Cell cellFactors = row.createCell(39);
				if(opp.getOpportunityWinLossFactorsTs().size()==1){
					cellFactors.setCellValue(opp.getOpportunityWinLossFactorsTs().get(0).getWinLossFactorMappingT().getWinLossFactor().trim());
				} else if(opp.getOpportunityWinLossFactorsTs().size()>1){
					cellFactors.setCellValue(constructWinLossFactors(opp.getOpportunityWinLossFactorsTs()));
				}  
			}

			//BID Details - Bid Req Type
			if((opp.getBidDetailsTs()!=null)&&(!opp.getBidDetailsTs().isEmpty())){

				// Req Type
				Cell cellBidReqType = row.createCell(27);
				cellBidReqType.setCellValue(opp.getBidDetailsTs().get(0).getBidRequestType());

				//Req Receive Date
				Cell cellBidReqReceiveDate = row.createCell(29);
				cellBidReqReceiveDate.setCellValue(getFormattedDate(opp.getBidDetailsTs().get(0).getBidRequestReceiveDate().toString(), actualFormat, desiredFormat));

				//Target Date
				Cell cellTargetDate = row.createCell(30);
				cellTargetDate.setCellValue(getFormattedDate(opp.getBidDetailsTs().get(0).getTargetBidSubmissionDate().toString(), actualFormat, desiredFormat));

				//Actual Date
				if(opp.getBidDetailsTs().get(0).getActualBidSubmissionDate()!=null){
					Cell cellActualDate = row.createCell(31);
					cellActualDate.setCellValue(getFormattedDate(opp.getBidDetailsTs().get(0).getActualBidSubmissionDate().toString(), actualFormat, desiredFormat));
				}

				//Expected Date
				if(opp.getBidDetailsTs().get(0).getExpectedDateOfOutcome()!=null){
					Cell cellExpDate = row.createCell(32);
					cellExpDate.setCellValue(getFormattedDate(opp.getBidDetailsTs().get(0).getExpectedDateOfOutcome().toString(), actualFormat, desiredFormat));
				}

				//Win probability
				if(opp.getBidDetailsTs().get(0).getWinProbability()!=null){
					Cell cellWinProb = row.createCell(33);
					cellWinProb.setCellValue(opp.getBidDetailsTs().get(0).getWinProbability().trim());
				}

				//Core Attributes
				if(opp.getBidDetailsTs().get(0).getCoreAttributesUsedForWinning()!=null){
					Cell cellWinProb = row.createCell(34);
					cellWinProb.setCellValue(opp.getBidDetailsTs().get(0).getCoreAttributesUsedForWinning().trim());
				}

			}

			// Deal Status Remarks
			if((opp.getNotesTs()!=null)&&(!opp.getNotesTs().isEmpty())){
				Cell cellNotes = row.createCell(41);

				String notes="";
				for(NotesT notestT:opp.getNotesTs())
				{
					notes+=notestT.getUserUpdated()+"|"+getFormattedDate(notestT.getCreatedDatetime().toString(),actualFormat,desiredFormat)+":"+notestT.getNotesUpdated()+"\n";
				}

				cellNotes.setCellValue(notes);
			}

			rowCount++;
		}
		logger.debug("End: Inside populateOpportunitySheet of OpportunityDownloadService");
	}

	/**
	 * This method converts the given currency to USD
	 * 
	 * @param dealCurrency
	 * @param overallDealSize
	 * @return
	 * @throws Exception
	 */

	public BigDecimal convertCurrencyToUSD(String dealCurrency,Integer overallDealSize) throws Exception{

		logger.debug("Inside convertCurrencyToUSD of OpportunityDownloadService");
		if(overallDealSize != null) {
			return beaconConverterService.convertCurrencyRate(dealCurrency, "USD", overallDealSize.doubleValue());
		}
		return null;
	}

	/**
	 * 
	 * @param opportunityWinLossFactorsTs
	 * @return
	 */
	private String constructWinLossFactors(List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs) {

		StringBuilder buffer = new StringBuilder();

		for(OpportunityWinLossFactorsT link : opportunityWinLossFactorsTs){
			buffer.append(link.getWinLossFactorMappingT().getWinLossFactor().trim()+",");
		}

		if(buffer.length()>0){
			buffer.deleteCharAt(buffer.length()-1);
		}
		logger.debug("Inside constructWinLossFactors of OpportunityDownloadService");
		return buffer.toString();
	}

	/**
	 * to constructCompetitors
	 * @param opportunityCompetitorLinkTs
	 * @return
	 */
	private String constructCompetitors(List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs) {

		StringBuilder buffer = new StringBuilder();

		for(OpportunityCompetitorLinkT link : opportunityCompetitorLinkTs){
			buffer.append(link.getCompetitorName().trim()+",");
		}

		if(buffer.length()>0){
			buffer.deleteCharAt(buffer.length()-1);
		}
		logger.debug("Inside constructCompetitors of OpportunityDownloadService");
		return buffer.toString();
	}

	/**
	 * to constructPartners
	 * @param opportunityPartnerLinkTs
	 * @return
	 */
	private String constructPartners(List<OpportunityPartnerLinkT> opportunityPartnerLinkTs) {

		StringBuilder buffer = new StringBuilder();

		for(OpportunityPartnerLinkT link : opportunityPartnerLinkTs){
			buffer.append(link.getPartnerMasterT().getPartnerName().trim()+",");
		}

		if(buffer.length()>0){
			buffer.deleteCharAt(buffer.length()-1);
		}
		logger.debug("Inside constructPartners of OpportunityDownloadService");
		return buffer.toString();
	}

	private String constructSalesSupportOwner(List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs) {

		StringBuilder buffer = new StringBuilder();

		for(OpportunitySalesSupportLinkT link : opportunitySalesSupportLinkTs){
			buffer.append(link.getSalesSupportOwnerUser().getUserName().trim()+",");
		}

		if(buffer.length()>0){
			buffer.deleteCharAt(buffer.length()-1);
		}
		logger.debug("Inside constructSalesSupportOwner of OpportunityDownloadService");
		return buffer.toString();
	}

	/**
	 * This method converts the list to offering names to a string separated by commas
	 * 
	 * @param opportunityOfferingLinkTs
	 * @return String
	 */
	private String constructOfferingCell(List<OpportunityOfferingLinkT> opportunityOfferingLinkTs) {

		StringBuilder offeringBuffer = new StringBuilder();

		for(OpportunityOfferingLinkT oolt : opportunityOfferingLinkTs) {
			offeringBuffer.append(oolt.getOffering().trim()+",");
		}

		if(offeringBuffer.length()>0){
			offeringBuffer.deleteCharAt(offeringBuffer.length()-1);
		}
		logger.debug("Inside constructOfferingCell of OpportunityDownloadService");
		return offeringBuffer.toString();    }

	/**
	 * This method converts the list to subsp names to a string separated by commas
	 * 
	 * @param opportunitySubSpLinkTs
	 * @return String
	 */
	private String constructSubSpCell(List<OpportunitySubSpLinkT> opportunitySubSpLinkTs) {

		StringBuilder subSpBuffer = new StringBuilder();

		for(OpportunitySubSpLinkT osst : opportunitySubSpLinkTs) {
			subSpBuffer.append(osst.getSubSp().trim()+",");
		}

		if(subSpBuffer.length()>0){
			subSpBuffer.deleteCharAt(subSpBuffer.length()-1);
		}
		logger.debug("Inside constructSubSpCell of OpportunityDownloadService");
		return subSpBuffer.toString();
	}

	/**
	 * This method populates Deal Types and Bid Request Types
	 * 
	 * @param bidRequestDealType
	 */
	private void populateBidRequestDealTypeSheet(Sheet bidRequestDealType) {

		logger.debug("Begin:Inside populateBidRequestDealTypeSheet of OpportunityDownloadService");
		List<BidRequestTypeMappingT> listOfBids = (List<BidRequestTypeMappingT>) bidRequestTypeRepository.findAll();

		List<DealTypeMappingT> listOfDeals = (List<DealTypeMappingT>) dealTypeRepository.findAll();

		List<SalesStageMappingT> listOfSalesStageCodes = salesStageMappingRepository.getSalesStageCodes();

		int sizeOfBids = 0;
		int sizeOfDeals = 0;
		int sizeOfSSC = 0;
		if(listOfBids!=null) {
			sizeOfBids = listOfBids.size();
		}
		if(listOfDeals!=null) {
			sizeOfDeals = listOfDeals.size();
		}
		if(listOfSalesStageCodes!=null) {
			sizeOfSSC = listOfSalesStageCodes.size();
		}


		int rowCount = 1;

		while((rowCount<=sizeOfBids)||(rowCount<=sizeOfDeals)||(rowCount<=sizeOfSSC)){

			Row row = bidRequestDealType.createRow(rowCount);

			if(rowCount<=sizeOfBids){
				Cell cellBid = row.createCell(0);
				cellBid.setCellValue(listOfBids.get(rowCount-1).getBidRequestType().trim());
			}
			if(rowCount<=sizeOfDeals){
				Cell cellDeal = row.createCell(2);
				cellDeal.setCellValue(listOfDeals.get(rowCount-1).getDealType().trim());
			}
			if(rowCount<=sizeOfSSC){
				Cell cellCode = row.createCell(4);
				Cell cellDesc = row.createCell(5);
				cellCode.setCellValue(listOfSalesStageCodes.get(rowCount-1).getSalesStageCode());
				cellDesc.setCellValue(listOfSalesStageCodes.get(rowCount-1).getSalesStageDescription().trim());
			}

			rowCount++;

		}
		logger.debug("End:Inside populateBidRequestDealTypeSheet of OpportunityDownloadService");
	}

	/**
	 * This method populates the Customer Master Sheet
	 * 
	 * @param customerMasterSheet
	 */
	public void populateCustomerMasterSheet(Sheet customerMasterSheet) throws Exception{
		logger.debug("Begin:Inside populateCustomerMasterSheet of OpportunityDownloadService");
		List<CustomerMasterT> listOfCMT = (List<CustomerMasterT>) customerRepository
				.findAll();

		if(listOfCMT!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (CustomerMasterT cmt : listOfCMT) {
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
		}
		logger.debug("End:Inside populateCustomerMasterSheet of OpportunityDownloadService");

	}

	/**
	 * This method populates the SubSp sheet
	 * 
	 * @param subSpSheet
	 */
	public void populateSubSpSheet(Sheet subSpSheet) throws Exception{
		logger.debug("Begin:Inside populateSubSpSheet of OpportunityDownloadService");
		List<SubSpMappingT> listOfSubSp = (List<SubSpMappingT>) subSpRepository.findAll();

		if(listOfSubSp!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (SubSpMappingT ssmt : listOfSubSp) {
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
				if(ssmt.getSpCode()!=null){
					cellSpCode.setCellValue(ssmt.getSpCode());
				} 

				Cell cellActive = row.createCell(4);
				cellActive.setCellValue(ssmt.isActive());

				// Increment row counter
				rowCount++;
			}
		}
		logger.debug("End:Inside populateSubSpSheet of OpportunityDownloadService");
	}

	/**
	 * This method populates the partner sheet
	 * 
	 * @param partnerSheet
	 */
	public void populatePartnerSheet(Sheet partnerSheet) throws Exception{
		logger.debug("Begin:Inside populatePartnerSheet of OpportunityDownloadService");
		List<Object[]> listOfPartner = partnerRepository.getPartnerNameAndGeography();

		if(listOfPartner!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (Object[] ob : listOfPartner) {
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
		}
		logger.debug("End:Inside populatePartnerSheet of OpportunityDownloadService");
	}

	/**
	 * This method populates the partner contacts sheet 
	 * 
	 * @param partnerContactSheet
	 */
	public void populateContactSheets(Sheet partnerContactSheet) throws Exception{
		logger.debug("Begin:Inside populateContactSheets of OpportunityDownloadService");
		List<ContactT> listOfContact = (List<ContactT>) contactRepository.findAll();

		if(listOfContact!=null) {
			int rowCountPartnerSheet = 1; // Excluding the header, header starts with index 0
			for (ContactT ct : listOfContact) {

				if ((ct.getContactCategory().equals(EntityType.PARTNER.toString()) && 
						(ct.getContactType().equals(ContactType.EXTERNAL.toString())))) { // For Partner Contact

					// Create row with rowCount
					Row row = partnerContactSheet.createRow(rowCountPartnerSheet);

					// Create new Cell and set cell value
					Cell cellPartnerName = row.createCell(0);
					try {
						cellPartnerName.setCellValue(ct.getPartnerContactLinkTs().get(0).getPartnerMasterT().getPartnerName().trim());
					} catch(NullPointerException npe){
						throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, "Partner Contact cannot exist without Partner");
					}

					Cell cellPartnerContactName = row.createCell(1);
					cellPartnerContactName.setCellValue(ct.getContactName());

					Cell cellPartnerContactRole = row.createCell(2);
					cellPartnerContactRole.setCellValue(ct.getContactRole());

					Cell cellPartnerContactEmailId = row.createCell(3);
					if(ct.getContactEmailId()!=null) {
						cellPartnerContactEmailId.setCellValue(ct.getContactEmailId());
					}

					// Increment row counter for partner contact sheet
					rowCountPartnerSheet++;

				}
			}
		}
		logger.debug("End:Inside populateContactSheets of OpportunityDownloadService");
	}

	/**
	 * This method populates the currency sheet
	 * 
	 * @param currencySheet
	 */
	public void populateWinLossSheet(Sheet currencySheet) throws Exception{
		logger.debug("Begin:Inside populateWinLossSheet of OpportunityDownloadService");
		List<WinLossFactorMappingT> listOfWinLoss = (List<WinLossFactorMappingT>) winLossMappingRepository.findAll();

		if(listOfWinLoss!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (WinLossFactorMappingT wlm : listOfWinLoss) {

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
		}
		logger.debug("End:Inside populateWinLossSheet of OpportunityDownloadService");
	}

	/**
	 * This method populates the win loss factor sheet
	 * 
	 * @param currencySheet
	 */
	public void populateCurrencySheet(Sheet winLossSheet) throws Exception{

		List<BeaconConvertorMappingT> listOfCurrencyMapping = (List<BeaconConvertorMappingT>) beaconConvertorRepository.findAll();
		logger.debug("Begin:Inside populateCurrencySheet of OpportunityDownloadService");

		if(listOfCurrencyMapping!=null) {

			int rowCount = 1; // Excluding the header, header starts with index 0
			for (BeaconConvertorMappingT bcmt : listOfCurrencyMapping) {
				// Create row with rowCount
				Row row = winLossSheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellCurrencyName = row.createCell(0);
				cellCurrencyName.setCellValue(bcmt.getCurrencyName().trim());

				Cell cellCurrencyValue = row.createCell(1);
				cellCurrencyValue.setCellValue(bcmt.getConversionRate().doubleValue());

				// Increment row counter
				rowCount++;
			}
		}
		logger.debug("End:Inside populateCurrencySheet of OpportunityDownloadService");
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
	public static String getFormattedDate(String Stringdate, DateFormat actualFormat, DateFormat destFormat) throws Exception{

		return destFormat.format(actualFormat.parse(Stringdate));
	}





}
