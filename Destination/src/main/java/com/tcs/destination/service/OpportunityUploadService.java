package com.tcs.destination.service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.utils.StringUtils;

import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.CompetitorMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.ContactTMapDTO;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityCustomerContactLinkT;
import com.tcs.destination.bean.OpportunityOfferingLinkT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.bean.WinLossFactorMappingT;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WinLossMappingRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.OpportunityUploadConstants;

/**
 * This service helps in uploading opportunities to database
 * 
 */
@Service
public class OpportunityUploadService {

    @Autowired
    OpportunityRepository opportunityRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    OpportunityService opportunityService;

    @Autowired
    CompetitorRepository competitorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubSpRepository subSpRepository;

    @Autowired
    OfferingRepository offeringRepository;

    @Autowired
    WinLossMappingRepository winLossMappingRepository;
    
    @Autowired
    BidDetailsTRepository bidDetailsTRepository;
    
    @Autowired
    GeographyRepository geographyRepository;
    
    @Autowired
    BeaconConverterService beaconConverterService;

    private Map<String, String> mapOfPartnerMasterT = null;
    private Map<String, String> mapOfCustomerMasterT = null;
    private Map<String, String> mapOfCustomerContactT = null;
    private Map<String, String> mapOfTCSContactT = null;
    private Map<String, String> mapOfUserT = null;
    private List<String> listOfCompetitors = null;
    private List<String> listOfSubSp = null;
    private List<String> listOfOfferings = null;
    private List<String> listOfWinLossFactors = null;
    private List<String> listOfBidRequestType = null;
    private List<String> listOfCountry = null;

    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
    private static final Logger logger = LoggerFactory
	    .getLogger(OpportunityUploadService.class);
    private String bidRequestType = null;
    private String actualSubmissionDate = null;

    /**
     * This method uploads the spreadsheet to Opportunity_t and its depending
     * tables
     * 
     * @param multipartFile
     * @param userId
     * @return UploadStatusDTO
     * @throws Exception
     */
    public UploadStatusDTO saveDocument(MultipartFile multipartFile,
	    String userId) throws Exception {

	logger.debug("Begin:Inside saveDocument of OpportunityUploadService");

	UploadStatusDTO uploadStatus = null;

	try {

	    Workbook workbook = ExcelUtils.getWorkBook(multipartFile);
	    uploadStatus = new UploadStatusDTO();
	    uploadStatus
		    .setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());

	    // Validates the spreadsheet for errors after validating the excel
	    // sheet
	    if (validateSheet(workbook)) {

		Sheet sheet = workbook.getSheet("Opportunity");

		boolean isBulkDataLoad = true;

		uploadStatus.setStatusFlag(true);

		// Get Customer Name and Id from corresponding tables
		mapOfCustomerMasterT = getNameAndIdFromCustomerMasterT();

		mapOfPartnerMasterT = getNameAndIdFromPartnerMasterT();

		ContactTMapDTO cmDTO = getNameAndIdFromContactT();
		mapOfTCSContactT = cmDTO.getMapOfTcsContactT();
		mapOfCustomerContactT = cmDTO.getMapOfCustomerContactT();

		mapOfUserT = getNameAndIdFromUserT();

		listOfCompetitors = competitorRepository.getCompetitorName();

		listOfSubSp = subSpRepository.getSubSp();

		listOfOfferings = offeringRepository.getOffering();

		listOfWinLossFactors = winLossMappingRepository.getWinLossFactor();
		
		listOfBidRequestType = bidDetailsTRepository.getBidRequestType();
		
		listOfCountry = geographyRepository.getCountry();

		int rowCount = 0;
		List<String> listOfCellValues = null;
		List<String> remarks=new ArrayList<String>();
		//listOfCellValues.clear();
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();

		while (rowIterator.hasNext()
			&& rowCount <= sheet.getLastRowNum()) {

		    Row row = rowIterator.next();

		    // For each row, iterate through all the columns
		    if (rowCount > 1) {

			int emptyCount = 0;

			listOfCellValues = new ArrayList<String>();
		
			for (int cellCount = 0; cellCount < OpportunityUploadConstants.OPPORTUNITY_UPLOAD_COLUMN_SIZE; cellCount++) {

			    Cell cell = row.getCell(cellCount);

			    String value = getIndividualCellValue(cell);

			    if (value != null) {
				listOfCellValues.add(value.trim());
				if (value.equals("")) {
				    emptyCount++;
				}
			    }
			}

			// set the cell values to the corresponding fields in
			// the OpportunityT entity object
			if ((listOfCellValues.size() > 0) && (emptyCount < OpportunityUploadConstants.OPPORTUNITY_UPLOAD_COLUMN_SIZE)) {

			    try {
				OpportunityT opp = new OpportunityT();

				// CUSTOMER ID
				if (!StringUtils.isEmpty(listOfCellValues.get(2))) {
				    String custId = getMapValuesForKey(mapOfCustomerMasterT, listOfCellValues.get(2));
				    if (custId != null) {
					opp.setCustomerId(custId);
				    } else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid "+OpportunityUploadConstants.CUSTOMER_NAME);
				    }
				} else {
				    throw new DestinationException(HttpStatus.BAD_REQUEST, OpportunityUploadConstants.CUSTOMER_NAME+" is empty");
				}

				// COUNTRY
				if (!StringUtils.isEmpty(listOfCellValues.get(5))) {
				    validateCellByStringLength(listOfCellValues.get(5),OpportunityUploadConstants.COUNTRY, 5, OpportunityUploadConstants.COUNTRY_MAX_SIZE);
				    if(searchGeographyCountryMappingT(listOfCellValues.get(5))){
					opp.setCountry(listOfCellValues.get(5));
				    } else {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid "+OpportunityUploadConstants.COUNTRY);
				    }
				} else {
				    throw new DestinationException(HttpStatus.BAD_REQUEST, OpportunityUploadConstants.COUNTRY+" is empty");
				}

				// CRM ID
				if (!StringUtils.isEmpty(listOfCellValues.get(6))) {
					validateCellByStringLength(listOfCellValues.get(6),OpportunityUploadConstants.CRMID, 6, OpportunityUploadConstants.CRMID_MAX_SIZE);
					opp.setCrmId(listOfCellValues.get(6));
				}

				// OPPORTUNITY NAME
				if (!StringUtils.isEmpty(listOfCellValues.get(7))) {
				    validateCellByStringLength(listOfCellValues.get(7),OpportunityUploadConstants.OPP_NAME, 7, OpportunityUploadConstants.OPP_NAME_MAX_SIZE);
				    opp.setOpportunityName(listOfCellValues.get(7));
				} else {
				    throw new DestinationException(HttpStatus.BAD_REQUEST,OpportunityUploadConstants.OPP_NAME+" is empty");
				}

				// OPPORTUNITY DESCRIPTION
				if (!StringUtils.isEmpty(listOfCellValues.get(8))) {
					validateCellByStringLength(listOfCellValues.get(8),OpportunityUploadConstants.OPP_DESC, 8, OpportunityUploadConstants.OPP_DESC_MAX_SIZE);
					opp.setOpportunityDescription(listOfCellValues.get(8));
				}

				// REQUEST RECEIVE DATE
				if (!StringUtils.isEmpty(listOfCellValues.get(11))) {
				    opp.setOpportunityRequestReceiveDate(dateFormat.parse(listOfCellValues.get(11)));
                   //opp.setOpportunityRequestReceiveDate(DateUtils.getDateTimeFormat(listOfCellValues.get(11), dateFormat, simpleDateFormat));
				} else {
				    throw new DestinationException(HttpStatus.BAD_REQUEST, "Request Receive Date is empty");
				}

				// NEW LOGO
				if (!StringUtils.isEmpty(listOfCellValues.get(12))) {
				    validateCellByStringLength(listOfCellValues.get(12),OpportunityUploadConstants.NEW_LOGO, 12, OpportunityUploadConstants.NEW_LOGO_MAX_SIZE);
				    opp.setNewLogo(listOfCellValues.get(12).toUpperCase());
				}
				else
				{
					opp.setNewLogo("NO");
				}

				// STRATEGIC INITIATIVE
				if (!StringUtils.isEmpty(listOfCellValues.get(13))) {
				    validateCellByStringLength(listOfCellValues.get(13),OpportunityUploadConstants.STRATEGIC_INIT, 13, OpportunityUploadConstants.STRATEGIC_INIT_MAX_SIZE);
				    opp.setStrategicInitiative(listOfCellValues.get(13).toUpperCase());
				}
				else
				{
					opp.setStrategicInitiative("NO");
				}
              
				// DIGITAL FLAG
				if (!StringUtils.isEmpty(listOfCellValues.get(14))) {
				    validateCellByStringLength(listOfCellValues.get(14),OpportunityUploadConstants.DIGITAL_FLAG, 14, OpportunityUploadConstants.DIGITAL_FLAG_MAX_SIZE);
				    opp.setDigitalFlag(listOfCellValues.get(14).toUpperCase());
				}
				else
				{
					opp.setDigitalFlag("N");
				}
				
				// SALES STAGE CODE
				if (!StringUtils.isEmpty(listOfCellValues.get(15))) {
				    opp.setSalesStageCode((Integer.parseInt(listOfCellValues.get(15).substring(0, 2))));
				} else {
				    throw new DestinationException(HttpStatus.BAD_REQUEST, "Sales Stage Code is empty");
				}

				// DEAL CURRENCY
				if (!StringUtils.isEmpty(listOfCellValues.get(16))) {
				    validateCellByStringLength(listOfCellValues.get(16),OpportunityUploadConstants.DEAL_CURRENCY, 16, OpportunityUploadConstants.DEAL_CURRENCY_MAX_SIZE);
				    opp.setDealCurrency(listOfCellValues.get(16));
				}

				// OVERALL DEAL SIZE
				if (!StringUtils.isEmpty(listOfCellValues.get(18))&&!StringUtils.isEmpty(listOfCellValues.get(16))) {
					BigDecimal actualDealValue=beaconConverterService.convert("USD",opp.getDealCurrency(), Double.valueOf(listOfCellValues.get(20)));
					actualDealValue.setScale(2, RoundingMode.HALF_DOWN);
				    opp.setOverallDealSize(actualDealValue);
				}

				// DIGITAL DEAL VALUE
				if (!StringUtils.isEmpty(listOfCellValues.get(20))&&!StringUtils.isEmpty(listOfCellValues.get(16))) {
					BigDecimal digitalDealValue=beaconConverterService.convert("USD",opp.getDealCurrency(), Double.valueOf(listOfCellValues.get(20)));
					digitalDealValue.setScale(2, RoundingMode.HALF_DOWN);
				    opp.setDigitalDealValue(digitalDealValue);
				}

				// OPPORTUNITY OWNER
				if (!StringUtils.isEmpty(listOfCellValues.get(21))) {
				    String oppOwner = getMapValuesForKey(mapOfUserT, listOfCellValues.get(21).trim());
				    if (oppOwner != null) {
					opp.setOpportunityOwner(oppOwner);
				    } else {
					throw new DestinationException(HttpStatus.NOT_FOUND,"Invalid Opportunity Owner");
				    }
				} else {
				    throw new DestinationException(HttpStatus.BAD_REQUEST,"Opportunity Owner is empty");
				}

				// DEAL TYPE
				if (!StringUtils.isEmpty(listOfCellValues.get(35))) {
				    validateCellByStringLength(listOfCellValues.get(35),OpportunityUploadConstants.DEAL_TYPE, 35, OpportunityUploadConstants.DEAL_TYPE_MAX_SIZE);
				    opp.setDealType(listOfCellValues.get(35));
				}

				// DEAL CLOSURE DATE
				if (!StringUtils.isEmpty(listOfCellValues.get(36))) {
				    opp.setDealClosureDate(dateFormat.parse(listOfCellValues.get(36)));
				}

				// ENGAGEMENT DURATION
				if (!StringUtils.isEmpty(listOfCellValues.get(37))) {
				    validateCellByStringLength(listOfCellValues.get(37),OpportunityUploadConstants.ENGAGEMENT_DURATION, 37, OpportunityUploadConstants.ENGAGEMENT_DURATION_MAX_SIZE);
				    opp.setEngagementDuration((listOfCellValues.get(37)));
				}

				// ENGAGEMENT START DATE
				if (!StringUtils.isEmpty(listOfCellValues
					.get(38))) {
				    opp.setEngagementStartDate(dateFormat
					    .parse(listOfCellValues.get(38)));
				}

				// COMMENTS FOR WIN LOSS
				if (!StringUtils.isEmpty(listOfCellValues.get(40))) {
				    validateCellByStringLength(listOfCellValues.get(40),OpportunityUploadConstants.COMMENTS_FOR_WIN_LOSS, 40, OpportunityUploadConstants.COMMENTS_FOR_WIN_LOSS_MAX_SIZE);
				    opp.setDealClosureComments(listOfCellValues
						.get(40));
				}

				// Params for opportunity_t Table - manually set
				opp.setDocumentsAttached(Constants.NO);
				opp.setCreatedBy(userId);
				opp.setModifiedBy(userId);

				// Partner Params
				if (!StringUtils.isEmpty(listOfCellValues
					.get(25))) {
				    opp.setOpportunityPartnerLinkTs(constructOppPartnerLink(
					    listOfCellValues.get(25).trim(),
					    userId, mapOfPartnerMasterT));
				}

				/*
				 * Commented on-demand
				 */
				//Customer Contact Params
				 if(!StringUtils.isEmpty(listOfCellValues.get(24))){
				 opp.setOpportunityCustomerContactLinkTs(constructOppCustomerContactLink(listOfCellValues.get(24),
				 userId, mapOfCustomerContactT));
				 }
				
				 // TCS Contact Params
				 if(!StringUtils.isEmpty(listOfCellValues.get(23))){
				 opp.setOpportunityTcsAccountContactLinkTs(constructOppTCSContactLink(listOfCellValues.get(23),
				 userId, mapOfTCSContactT));
				 }

				// Competitor Params
				if (!StringUtils.isEmpty(listOfCellValues
					.get(26))) {
				    opp.setOpportunityCompetitorLinkTs(constructOppCompetitorLink(
					    listOfCellValues.get(26), userId));
				}

				// Sub Sp Params
				if (!StringUtils.isEmpty(listOfCellValues
					.get(9))) {
				    opp.setOpportunitySubSpLinkTs(constructOppSubSpLink(
					    listOfCellValues.get(9).trim(), userId));
				}

				// OpportunityOfferingLinkT Params
				if (!StringUtils.isEmpty(listOfCellValues
					.get(10))) {
				    opp.setOpportunityOfferingLinkTs(constructOppOfferingLink(
					    listOfCellValues.get(10), userId));
				}

				/*
				 * Commented on-demand
				 */
				// //OpportunitySalesSupportLinkT Params
				 if(!StringUtils.isEmpty(listOfCellValues.get(22))){
				opp.setOpportunityOwner(getMapValuesForKey(mapOfUserT,
				listOfCellValues.get(21).trim()));
				
				 opp.setOpportunitySalesSupportLinkTs(constructOppSalesSupportLink(listOfCellValues.get(22),
				 userId));
				 }
				 else
				 {
					 throw new DestinationException(HttpStatus.BAD_REQUEST,"Sales Support Owner is empty");
				 }

				// Bid Details
				if (!StringUtils.isEmpty(listOfCellValues
					.get(27))
					&& (!StringUtils
						.isEmpty(listOfCellValues
							.get(29)))
					&& (!StringUtils
						.isEmpty(listOfCellValues
							.get(30)))) {
				    opp.setBidDetailsTs(constructbidDetailsT(
					    listOfCellValues.get(27),
					    listOfCellValues.get(29),
					    listOfCellValues.get(30),
					    listOfCellValues.get(31),
					    listOfCellValues.get(32),
					    listOfCellValues.get(33),
					    listOfCellValues.get(34), userId));
				}
				/*
				 * Commented on-demand 
				 */
				// else {
				// String message="";
				// if(StringUtils.isEmpty(listOfCellValues.get(27))){
				// message+="Bid Request Type";
				// }
				// if(StringUtils.isEmpty(listOfCellValues.get(29))){
				// if(!StringUtils.isEmpty(message))
				// message+=",";
				// message+="Bid Request Received Date";
				// }
				// if(StringUtils.isEmpty(listOfCellValues.get(30))){
				// if(!StringUtils.isEmpty(message))
				// message+=",";
				// message+="Target Bid Submission Date";
				// }
				// message+=" cannot be empty";
				// throw new
				// DestinationException(HttpStatus.BAD_REQUEST,
				// message);
				// }

				// FACTORS FOR WIN LOSS -
				if (!StringUtils.isEmpty(listOfCellValues.get(39))) {
				    opp.setOpportunityWinLossFactorsTs(constructOppWinLoss(listOfCellValues.get(39), userId));
				}

				// Deal Status Remarks 
				if (!StringUtils.isEmpty(listOfCellValues.get(41))) {
				   
				    validateCellByStringLength(listOfCellValues.get(41), OpportunityUploadConstants.DEAL_STATUS_REMARKS, 41, OpportunityUploadConstants.DEAL_STATUS_REMARKS_MAX_SIZE);
				    remarks.add(listOfCellValues.get(41));
				  }
				
				 //Remarks1
			    if (!StringUtils.isEmpty(listOfCellValues.get(43))) {
			    	validateCellByStringLength(listOfCellValues.get(43), OpportunityUploadConstants.REMARKS_1, 43, OpportunityUploadConstants.REMARKS_1_MAX_SIZE);
			    	remarks.add(listOfCellValues.get(43));
			    }
			    //Remarks2
			    if (!StringUtils.isEmpty(listOfCellValues.get(44))) {
			    	validateCellByStringLength(listOfCellValues.get(44), OpportunityUploadConstants.REMARKS_2, 44, OpportunityUploadConstants.REMARKS_2_MAX_SIZE);
					remarks.add(listOfCellValues.get(44));  
				}
			    
			    if(!remarks.isEmpty())
			    {
			    	 opp.setNotesTs(constructNotesT(remarks,opp.getCustomerId(), userId));	
			    }
				
			    if (!StringUtils.isEmpty(listOfCellValues.get(27))){
			    	bidRequestType = listOfCellValues.get(27);
			    } else {
			    	bidRequestType = null;
			    }
			    if (!StringUtils.isEmpty(listOfCellValues.get(31))){
			    	actualSubmissionDate = listOfCellValues.get(31);
			    } else {
			    	actualSubmissionDate = null;
			    }
			    
				opportunityService.createOpportunity(opp,
					isBulkDataLoad, bidRequestType, actualSubmissionDate);
				remarks.clear();
				
				listOfCellValues.clear();

			    } catch (Exception e) {
				// Catch the exception pertaining to a
				// particular row and continue iteration
				if (uploadStatus.isStatusFlag()) {
				    uploadStatus.setStatusFlag(false);
				}

				UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

				error.setRowNumber(rowCount + 1);
				error.setMessage(e.getMessage());

				uploadStatus.getListOfErrors().add(error);
			    }
			} else if (emptyCount == OpportunityUploadConstants.OPPORTUNITY_UPLOAD_COLUMN_SIZE) {
			    // If a row is empty, assume that there are no
			    // values to process and exit the iteration
			    break;
			}

		    }
		    rowCount++;
		}

	    } else {
		logger.error(
			"BAD_REQUEST: The Excel uploaded by user : {} contains validation errors, please rectify them before you upload the sheet again",
			userId);
		throw new DestinationException(
			HttpStatus.BAD_REQUEST,
			"The Excel uploaded by user : "
				+ userId
				+ " contains validation errors, please rectify them before you upload the sheet again");
	    }
	} catch (DestinationException de) {
	    logger.error("BAD_REQUEST:" + de.getMessage());
	    throw new DestinationException(HttpStatus.BAD_REQUEST,
		    de.getMessage());
	} catch (Exception e) {
	    logger.error(
		    "INTERNAL_SERVER_ERROR: An Exception has occured while processing the request for : {}",
		    userId);
	    throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
		    "An Exception has occured while processing the request for "
			    + userId);
	}
	logger.debug("End:Inside saveDocument of OpportunityUploadService");
	return uploadStatus;
    }
    
    /**
     * This method validates the date provided
     * @param date
     * @param columnName
     * @return Date
     * @throws Exception
     */
    private Date validateDate(String date, String columnName) throws Exception{
    	logger.debug("Begin:Inside validateDate of OpportunityUploadService");
	Date formattedDate = null;
	
	try {
	    formattedDate = dateFormat.parse(date);
	}catch(Exception e){
	    throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid "+columnName);
	}
	logger.debug("End:Inside validateDate of OpportunityUploadService");
	return formattedDate;
    }

    /**
     * This method validates the fields against their sizes provided in the database
     * 
     * @param value
     * @param columnName
     * @param columnNumber
     * @param length
     * @throws Exception
     */
    private void validateCellByStringLength(String value, String columnName, int columnNumber, int length) throws Exception{
	if(value.length()>length){
	    throw new DestinationException(HttpStatus.BAD_REQUEST, columnName+" should be a maximum of "+length+" characters");
	}
    }

    /**
     * This method constructs NotesT object
     * 
     * @param dealRemarks
     * @param customerId
     * @param userUpdated
     * @return
     */
    private List<NotesT> constructNotesT(List<String> dealRemarks,String customerId,String userUpdated) {
    	logger.debug("Begin:Inside constructNotesT of OpportunityUploadService");
    List<NotesT> listOfNotes = new ArrayList<NotesT>();
	for(int i=0;i<dealRemarks.size();i++)
	{
		NotesT notes = new NotesT();
		notes.setEntityType(EntityType.OPPORTUNITY.toString());
	    notes.setNotesUpdated(dealRemarks.get(i));
	    notes.setCustomerId(customerId);
		notes.setUserUpdated(userUpdated);
		listOfNotes.add(notes);
	}
	logger.debug("End:Inside constructNotesT of OpportunityUploadService");
	return listOfNotes;
    }

    /**
     * This method accepts a cell, checks the value and returns the response.
     * The default value sent is an empty string
     * 
     * @param cell
     * @return String
     */
    private String getIndividualCellValue(Cell cell) {
    	logger.debug("Begin:Inside getIndividualCellValue of OpportunityUploadService");
	String val = "";
	if (cell != null) {
	    switch (cell.getCellType()) {
	    case Cell.CELL_TYPE_NUMERIC:
		if (DateUtil.isCellDateFormatted(cell)) {
		    Date date = DateUtil
			    .getJavaDate(cell.getNumericCellValue());
		    String dateFmt = cell.getCellStyle().getDataFormatString();
		    val = new CellDateFormatter(dateFmt).format(date);
		} else {
		    val = String.valueOf(cell.getNumericCellValue()).trim();
		}
		break;
	    case Cell.CELL_TYPE_STRING:
		val = String.valueOf(cell.getStringCellValue());
		break;
	    case Cell.CELL_TYPE_BLANK:
		val = "";
		break;
	    }
	} else {
	    val = "";
	}
	logger.debug("End:Inside getIndividualCellValue of OpportunityUploadService");
	return val;
    }

    /**
     * This method accepts the factors and returns list of
     * OpportunityWinLossFactorsT objects
     * 
     * @param factors
     * @param userId
     * @return List<OpportunityWinLossFactorsT>
     * @throws Exception
     */
    private List<OpportunityWinLossFactorsT> constructOppWinLoss(
	    String factors, String userId) throws Exception {
    	logger.debug("Begin:Inside constructOppWinLoss of OpportunityUploadService");
	List<OpportunityWinLossFactorsT> listOfWinLossFactors = null;

	if (factors != null) {

	    listOfWinLossFactors = new ArrayList<OpportunityWinLossFactorsT>();

	    // factors can be comma separated, hence split() is used
	    String[] factorsArray = factors.split(",");

	    // rank is set based on the order of factors which are given as
	    // input
	    int rank = 1;
	    for (String factor : factorsArray) {
		if (searchWinLossFactor(factor.trim())) {
		    OpportunityWinLossFactorsT owlf = new OpportunityWinLossFactorsT();

		    owlf.setCreatedBy(userId);
		    owlf.setModifiedBy(userId);
		    owlf.setRank(rank);
		    owlf.setWinLossFactor(factor.trim());

		    listOfWinLossFactors.add(owlf);
		    rank++;
		} else {
		    throw new DestinationException(HttpStatus.NOT_FOUND,
			    "Invalid Win Loss Factor");
		}
	    }

	}
	logger.debug("End:Inside constructOppWinLoss of OpportunityUploadService");
	return listOfWinLossFactors;
    }

    /**
     * Searches the WinLossFactorMappingT for the given value
     * 
     * @param value
     * @return boolean
     */
    private boolean searchWinLossFactor(String value) {
    	logger.debug("Begin:Inside searchWinLossFactor of OpportunityUploadService");
	boolean flag = false;
	if (listOfWinLossFactors.contains(value)) {
	    flag = true;
	}
	logger.debug("End:Inside searchWinLossFactor of OpportunityUploadService");
	return flag;
    }

    /**
     * This method provides the values of competitor name from
     * CompetitorMappingT in list of String
     * 
     * @return List<String>
     * @throws Exception
     */
    private List<String> getCompetitorNameFromCompetitorMappingT()
	    throws Exception {
    	logger.debug("Begin:Inside getCompetitorNameFromCompetitorMappingT of OpportunityUploadService");
	List<String> listOfCompName = new ArrayList<String>();

	List<CompetitorMappingT> listOfCompMapping = (List<CompetitorMappingT>) competitorRepository
		.findAll();

	if ((listOfCompMapping != null) && (!listOfCompMapping.isEmpty())) {
	    for (CompetitorMappingT cmt : listOfCompMapping) {
		listOfCompName.add(cmt.getCompetitorName());
	    }
	}
	logger.debug("End:Inside getCompetitorNameFromCompetitorMappingT of OpportunityUploadService");
	return listOfCompName;
    }

    /**
     * This method provides the values of sub sp name from SubSpMappingT in list
     * of String
     * 
     * @return List<String>
     * @throws Exception
     */
    private List<String> getSubSpFromSubSpMappingT() throws Exception {
    	logger.debug("Begin:Inside getSubSpFromSubSpMappingT of OpportunityUploadService");
	List<String> subSpnames = new ArrayList<String>();

	List<SubSpMappingT> listOfSubSpMapping = (List<SubSpMappingT>) subSpRepository
		.findAll();

	if ((listOfSubSpMapping != null) && (!listOfSubSpMapping.isEmpty())) {
	    for (SubSpMappingT ssmt : listOfSubSpMapping) {
		subSpnames.add(ssmt.getSubSp());
	    }
	}
	logger.debug("End:Inside getSubSpFromSubSpMappingT of OpportunityUploadService");
	return subSpnames;
    }

    /**
     * This method provides the values of offering name from OfferingMappingT in
     * list of String
     * 
     * @return List<String>
     * @throws Exception
     */
    private List<String> getWinLossFactorsFromWinLossFactorsMappingT()
	    throws Exception {
    	logger.debug("Begin:Inside getWinLossFactorsFromWinLossFactorsMappingT of OpportunityUploadService");
	List<String> winLossFactors = new ArrayList<String>();

	List<WinLossFactorMappingT> listOfWinLossFactorMapping = (List<WinLossFactorMappingT>) winLossMappingRepository
		.findAll();

	if ((listOfWinLossFactorMapping != null)
		&& (!listOfWinLossFactorMapping.isEmpty())) {
	    for (WinLossFactorMappingT wlfmt : listOfWinLossFactorMapping) {
		winLossFactors.add(wlfmt.getWinLossFactor());
	    }
	}
	logger.debug("End:Inside getWinLossFactorsFromWinLossFactorsMappingT of OpportunityUploadService");
	return winLossFactors;
    }

    /**
     * This method provides the values of win loss factors from
     * WinLossFactorsMappingT in list of String
     * 
     * @return List<String>
     * @throws Exception
     */
    private List<String> getOfferingsFromOfferingMappingT() throws Exception {

	List<String> offerings = new ArrayList<String>();
	logger.debug("Begin:Inside getOfferingsFromOfferingMappingT of OpportunityUploadService");
	List<OfferingMappingT> listOfOfferingMapping = (List<OfferingMappingT>) offeringRepository
		.findAll();

	if ((listOfOfferingMapping != null)
		&& (!listOfOfferingMapping.isEmpty())) {
	    for (OfferingMappingT omt : listOfOfferingMapping) {
		offerings.add(omt.getOffering());
	    }
	}
	logger.debug("End:Inside getOfferingsFromOfferingMappingT of OpportunityUploadService");
	return offerings;
    }

    /**
     * This method constructs list of OpportunityCompetitorLinkT based on the
     * CompetitorName values provided
     * 
     * @param values
     * @param userId
     * @return List<OpportunityCompetitorLinkT>
     * @throws Exception
     */
    private List<OpportunityCompetitorLinkT> constructOppCompetitorLink(
	    String values, String userId) throws Exception {

	List<OpportunityCompetitorLinkT> listCompetitorLink = null;
	logger.debug("Begin:Inside constructOppCompetitorLink of OpportunityUploadService");
	if (values != null) {

	    listCompetitorLink = new ArrayList<OpportunityCompetitorLinkT>();

	    // More than one competitor could be provided separated by comma,
	    // hence split() is used
	    String[] valuesArray = values.split(",");

	    for (String value : valuesArray) {

		if (searchCompetitorMappingT(value.trim())) {
		    OpportunityCompetitorLinkT oclt = new OpportunityCompetitorLinkT();

		    oclt.setCompetitorName(value.trim());
		    oclt.setCreatedBy(userId);
		    oclt.setModifiedBy(userId);
		    oclt.setIncumbentFlag(Constants.N);

		    listCompetitorLink.add(oclt);
		} else {
		    throw new DestinationException(HttpStatus.NOT_FOUND,
			    "Invalid Competitor Name");
		}

	    }
	}
	logger.debug("End:Inside constructOppCompetitorLink of OpportunityUploadService");
	return listCompetitorLink;
    }

    /**
     * Searches the CompetitorMappingT for the given value
     * 
     * @param value
     * @return boolean
     */
    private boolean searchCompetitorMappingT(String cName) throws Exception {
    	logger.debug("Begin:Inside searchCompetitorMappingT of OpportunityUploadService");
	boolean flag = false;
	if (listOfCompetitors.contains(cName)) {
	    flag = true;
	}
	logger.debug("End:Inside searchCompetitorMappingT of OpportunityUploadService");
	return flag;
    }

    /**
     * This method constructs list of OpportunitySalesSupportLinkT based on the
     * SalesSupportOwner values provided
     * 
     * @param values
     * @param userId
     * @return List<OpportunitySalesSupportLinkT>
     * @throws Exception
     */
    private List<OpportunitySalesSupportLinkT> constructOppSalesSupportLink(
	    String values, String userId) throws Exception {

	List<OpportunitySalesSupportLinkT> listOfOppSubSpLink = null;
	logger.debug("Begin:Inside constructOppSalesSupportLink of OpportunityUploadService");
	if (!StringUtils.isEmpty(values)) {

	    listOfOppSubSpLink = new ArrayList<OpportunitySalesSupportLinkT>();

	    // More than one Sales Support Owner could be provided separated by
	    // comma, hence split() is used
	    String[] valuesArray = values.split(",");

	    for (String value : valuesArray) {
		OpportunitySalesSupportLinkT oclt = new OpportunitySalesSupportLinkT();

		// SalesSupportOwner comes with location separated by '-', hence
		// split is used
		//String[] ssValue = value.split("-");
		oclt.setSalesSupportOwner(getMapValuesForKey(mapOfUserT,
			value.trim()));
		oclt.setCreatedBy(userId);
		oclt.setModifiedBy(userId);

		listOfOppSubSpLink.add(oclt);
	    }

	}
	logger.debug("End:Inside constructOppSalesSupportLink of OpportunityUploadService");
	return listOfOppSubSpLink;
    }

    /**
     * Searches the GeographyCountryMappingT for the given value
     * 
     * @param value
     * @return boolean
     */
    private boolean searchGeographyCountryMappingT(String cName) throws Exception {
    	logger.debug("Begin:Inside searchGeographyCountryMappingT of OpportunityUploadService");
	boolean flag = false;
	if (listOfCountry.contains(cName)) {
	    flag = true;
	}
	logger.debug("End:Inside searchGeographyCountryMappingT of OpportunityUploadService");
	return flag;
    }
    
    /**
     * Searches the BidDetailsMappingT for the given value
     * 
     * @param value
     * @return boolean
     */
    private boolean searchBidDetailsMappingT(String bidType) throws Exception {
    	logger.debug("Begin:Inside searchBidDetailsMappingT of OpportunityUploadService");
	boolean flag = false;
	if (listOfBidRequestType.contains(bidType)) {
	    flag = true;
	}
	logger.debug("End:Inside searchBidDetailsMappingT of OpportunityUploadService");
	return flag;
    }

    /**
     * This method constructs the BidDetailsT entity using the input provided
     * 
     * @param bidReqType
     * @param bidReqDate
     * @param targetSubmissionDate
     * @param actualSubmissionDate
     * @param expectedOutcomeDate
     * @param winProbability
     * @param coreAttributes
     * @param userId
     * @return List<BidDetailsT>
     * @throws ParseException
     */
    private List<BidDetailsT> constructbidDetailsT(String bidReqType,
	    String bidReqDate, String targetSubmissionDate,
	    String actualSubmissionDate, String expectedOutcomeDate,
	    String winProbability, String coreAttributes, String userId)
	    throws Exception {

    	logger.debug("Begin:Inside constructbidDetailsT of OpportunityUploadService");
	List<BidDetailsT> listOfBidDetailsT = new ArrayList<BidDetailsT>();

	if(searchBidDetailsMappingT(bidReqType)){
	
        	BidDetailsT bdt = new BidDetailsT();
        
        	if (!StringUtils.isEmpty(bidReqType)) {
        	    bdt.setBidRequestType(bidReqType);
        	}
        	if (!StringUtils.isEmpty(bidReqDate)) {
        	    bdt.setBidRequestReceiveDate(dateFormat.parse(bidReqDate));
        	}
        	if (!StringUtils.isEmpty(targetSubmissionDate)) {
        	    bdt.setTargetBidSubmissionDate(dateFormat
        		    .parse(targetSubmissionDate));
        	}
        	if (!StringUtils.isEmpty(actualSubmissionDate)) {
        	    bdt.setActualBidSubmissionDate(dateFormat
        		    .parse(actualSubmissionDate));
        	}
        	if (!StringUtils.isEmpty(expectedOutcomeDate)) {
        	    bdt.setExpectedDateOfOutcome(dateFormat.parse(expectedOutcomeDate));
        	}
        	if (!StringUtils.isEmpty(winProbability)) {
        	    validateCellByStringLength(winProbability, OpportunityUploadConstants.WIN_PROBABILITY, 33, OpportunityUploadConstants.WIN_PROBABILITY_MAX_SIZE);
        	    bdt.setWinProbability(winProbability);
        	}
        	if (!StringUtils.isEmpty(coreAttributes)) {
        	    validateCellByStringLength(coreAttributes, OpportunityUploadConstants.CORE_ATTRIBUTES, 34, OpportunityUploadConstants.CORE_ATTRIBUTES_MAX_VALUE);
        	    bdt.setCoreAttributesUsedForWinning(coreAttributes);
        	}
        	bdt.setCreatedBy(userId);
        	bdt.setModifiedBy(userId);
        
        	listOfBidDetailsT.add(bdt);
	} else {
	    throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid Bid Request Type");
	} 
	logger.debug("End:Inside constructbidDetailsT of OpportunityUploadService");
	return listOfBidDetailsT;
    }

    /**
     * This utility method truncates '.0' which is returned as the cell value if
     * the cell contains numbers
     * 
     * @param value
     * @return String
     */
    private String validateAndRectifyValue(String value) {
    	logger.debug("Begin:Inside validateAndRectifyValue of OpportunityUploadService");
	String val = value;
	if (value != null) {
	    if (value.substring(value.length() - 2, value.length())
		    .equals(".0")) {
		val = value.substring(0, value.length() - 2);
	    }
	}
	logger.debug("End:Inside validateAndRectifyValue of OpportunityUploadService");
	return val;
    }

    /**
     * This method constructs list of OpportunitySubSpLinkT based on the SubSp
     * values provided
     * 
     * @param values
     * @param userId
     * @return List<OpportunitySubSpLinkT>
     * @throws Exception
     */
    private List<OpportunitySubSpLinkT> constructOppSubSpLink(String values,
	    String userId) throws Exception {
    	logger.debug("Begin:Inside constructOppSubSpLink of OpportunityUploadService");
	List<OpportunitySubSpLinkT> listOfOppSubSpLink = null;
	if (values != null) {

	    listOfOppSubSpLink = new ArrayList<OpportunitySubSpLinkT>();

	    // More than one SubSp could be provided separated by comma, hence
	    // split() is used
	    String[] valuesArray = values.split(",");

	    for (String value : valuesArray) {
		if (searchSubSpMappingT(value.trim())) {
		    OpportunitySubSpLinkT oclt = new OpportunitySubSpLinkT();

		    oclt.setSubSp(value.trim());
		    oclt.setCreatedBy(userId);
		    oclt.setModifiedBy(userId);

		    listOfOppSubSpLink.add(oclt);
		} else {
		    throw new DestinationException(HttpStatus.NOT_FOUND,
			    "Invalid SubSp");
		}
	    }
	}
	logger.debug("End:Inside constructOppSubSpLink of OpportunityUploadService");
	return listOfOppSubSpLink;
    }

    /**
     * Searches the SubSbMappingT for the given value
     * 
     * @param value
     * @return boolean
     */
    private boolean searchSubSpMappingT(String value) throws Exception {
    	logger.debug("Begin:Inside searchSubSpMappingT of OpportunityUploadService");
	boolean flag = false;
	if (listOfSubSp.contains(value)) {
	    flag = true;
	}
	logger.debug("End:Inside searchSubSpMappingT of OpportunityUploadService");
	return flag;
    }

    /**
     * This method constructs list of OpportunityOfferingLinkT based on the
     * Offering values provided
     * 
     * @param values
     * @param userId
     * @return List<OpportunityOfferingLinkT>
     * @throws Exception
     */
    private List<OpportunityOfferingLinkT> constructOppOfferingLink(
	    String values, String userId) throws Exception {
    	logger.debug("Begin:Inside constructOppOfferingLink of OpportunityUploadService");
	List<OpportunityOfferingLinkT> listOfOppOfferingLink = null;
	if (values != null) {

	    listOfOppOfferingLink = new ArrayList<OpportunityOfferingLinkT>();

	    // More than one Offering could be provided separated by comma,
	    // hence split() is used
	    String[] valuesArray = values.split(",");

	    for (String value : valuesArray) {
		if (searchOffering(value.trim())) {
		    OpportunityOfferingLinkT oolt = new OpportunityOfferingLinkT();

		    oolt.setOffering(value.trim());
		    oolt.setCreatedBy(userId);
		    oolt.setModifiedBy(userId);

		    listOfOppOfferingLink.add(oolt);
		} else {
		    throw new DestinationException(HttpStatus.NOT_FOUND,
			    "Invalid Offering");
		}
	    }

	}
	logger.debug("End:Inside constructOppOfferingLink of OpportunityUploadService");
	return listOfOppOfferingLink;
    }

    /**
     * Searches the OfferingMappingT for the given value
     * 
     * @param value
     * @return boolean
     */
    private boolean searchOffering(String value) throws Exception {
	boolean flag = false;
	if (listOfOfferings.contains(value)) {
	    flag = true;
	}
	return flag;
    }

    /**
     * This method accepts a cell, checks the value and returns the response.
     * The default value sent is an empty string
     * 
     * @param cell
     * @return String
     */
    private String getCellValue(Cell cell) {
	String value = null;
	// Check the cell type and format accordingly
	switch (cell.getCellType()) {
	case Cell.CELL_TYPE_NUMERIC:
	    if (DateUtil.isCellDateFormatted(cell)) {
		Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
		String dateFmt = cell.getCellStyle().getDataFormatString();
		value = new CellDateFormatter(dateFmt).format(date);
	    } else {
		value = String.valueOf(cell.getNumericCellValue()).trim();
	    }
	    break;
	case Cell.CELL_TYPE_STRING:
	    value = cell.getStringCellValue();
	    break;
	case Cell.CELL_TYPE_BLANK: {
	    value = "";
	    logger.debug("Blank ----------->>>>");
	    break;
	}
	}
	return value;
    }

    /**
     * Converts multipart file to File object
     * 
     * @param file
     * @return File
     * @throws Exception
     */
    public File convert(MultipartFile file) throws Exception {
    	logger.debug("Begin:Inside convert of OpportunityUploadService");
	File convFile = new File(file.getOriginalFilename());
	convFile.createNewFile();
	FileOutputStream fos = new FileOutputStream(convFile);
	fos.write(file.getBytes());
	fos.close();
	logger.debug("End:Inside convert of OpportunityUploadService");
	return convFile;
    }

    /**
     * This method retrieves Customer Name and Id from CustomerMasterT
     * 
     * @return Map
     * @throws Exception
     */
    private Map<String, String> getNameAndIdFromCustomerMasterT()
	    throws Exception {
    	logger.debug("Begin:Inside getNameAndIdFromCustomerMasterT of OpportunityUploadService");
	Map<String, String> mapOfCMT = new HashMap<String, String>();
	try {
		List<CustomerMasterT> listOfCustomerMasterT = customerRepository
				.getNameAndId();

		for (CustomerMasterT customerMasterT : listOfCustomerMasterT) {
			mapOfCMT.put(customerMasterT.getCustomerName(), customerMasterT.getCustomerId());
		}
	} catch (Exception e) {
	    e.printStackTrace();
	}
	logger.debug("End:Inside getNameAndIdFromCustomerMasterT of OpportunityUploadService");
	return mapOfCMT;
    }

    /**
     * This method retrieves Customer Name and Id from PartnerMasterT
     * 
     * @return Map<String, String>
     * @throws Exception
     */
    private Map<String, String> getNameAndIdFromPartnerMasterT()
	    throws Exception {
    
    	logger.debug("Begin:Inside getNameAndIdFromPartnerMasterT of OpportunityUploadService");
	List<Object[]> listOfPartnerMasterT = partnerRepository
		.getPartnerNameAndId();

	Map<String, String> mapOfPMT = new HashMap<String, String>();

	for (Object[] ob : listOfPartnerMasterT) {
	    mapOfPMT.put(ob[0].toString().trim(), ob[1].toString().trim());
	}
	logger.debug("End:Inside getNameAndIdFromPartnerMasterT of OpportunityUploadService");
	return mapOfPMT;
    }

    /**
     * This method retrieves Customer Name and Id from UserT
     * 
     * @return Map<String, String>
     * @throws Exception
     */
    private Map<String, String> getNameAndIdFromUserT() throws Exception {
    	logger.debug("Begin:Inside getNameAndIdFromUserT of OpportunityUploadService");
	List<Object[]> listOfUsers = userRepository.getNameAndId();

	Map<String, String> mapOfUserT = new HashMap<String, String>();

	for (Object[] ut : listOfUsers) {
	    mapOfUserT.put(ut[0].toString().trim(), ut[1].toString().trim());
	}
	logger.debug("End:Inside getNameAndIdFromUserT of OpportunityUploadService");
	return mapOfUserT;
    }

    /**
     * This method retrieves Customer Name and Id from ContactT
     * 
     * @return
     * @throws Exception
     */
    private ContactTMapDTO getNameAndIdFromContactT() throws Exception {
    	logger.debug("Begin:Inside getNameAndIdFromContactT of OpportunityUploadService");
	List<ContactT> listOfContactT = (List<ContactT>) contactRepository
		.findAll();

	ContactTMapDTO cmDTO = new ContactTMapDTO();

	Map<String, String> mapOfCustomerContactT = null;
	Map<String, String> mapOfTcsContactT = null;

	if ((listOfContactT != null) && (!listOfContactT.isEmpty())) {
	    mapOfCustomerContactT = new HashMap<String, String>();
	    mapOfTcsContactT = new HashMap<String, String>();
	    for (ContactT ct : listOfContactT) {
		if ((ct.getContactCategory().equals(
			EntityType.PARTNER.toString()) && (ct.getContactType()
			.equals(ContactType.EXTERNAL.toString())))) {
		    mapOfCustomerContactT.put(ct.getContactName().trim(), ct
			    .getContactId().trim());
		} else if ((ct.getContactCategory().equals(
			EntityType.CUSTOMER.toString()) && (ct.getContactType()
			.equals(ContactType.INTERNAL.toString())))) {
		    mapOfTcsContactT.put(ct.getContactName().trim(), ct
			    .getContactId().trim());

		}
	    }

	    if (mapOfCustomerContactT != null) {
		cmDTO.setMapOfCustomerContactT(mapOfCustomerContactT);
	    }
	    if (mapOfTcsContactT != null) {
		cmDTO.setMapOfTcsContactT(mapOfTcsContactT);
	    }

	}
	logger.debug("End:Inside getNameAndIdFromContactT of OpportunityUploadService");
	return cmDTO;
    }

    /**
     * This method constructs list of OpportunityPartnerLinkT based on the
     * partnerValues values provided
     * 
     * @param partnerValues
     * @param userId
     * @param map
     * @return List<OpportunityPartnerLinkT>
     * @throws Exception
     */
    private List<OpportunityPartnerLinkT> constructOppPartnerLink(
	    String partnerValues, String userId, Map<String, String> map)
	    throws Exception {

	List<OpportunityPartnerLinkT> listOppPartnerLinkT = null;
	logger.debug("Begin:Inside constructOppPartnerLink of OpportunityUploadService");
	if (partnerValues != null) {

	    listOppPartnerLinkT = new ArrayList<OpportunityPartnerLinkT>();
	    // get the Id values based on the values in the map
	    List<String> listOfpId = getValuesFromKeysSeparatedByComma(
		    partnerValues, map, "Partner Name");

	    if ((listOfpId != null) && (!listOfpId.isEmpty())) {
		for (String pId : listOfpId) {
		    OpportunityPartnerLinkT oplt = new OpportunityPartnerLinkT();

		    oplt.setPartnerId(pId);
		    oplt.setCreatedBy(userId);
		    oplt.setModifiedBy(userId);

		    listOppPartnerLinkT.add(oplt);
		}
	    }
	}
	logger.debug("End:Inside constructOppPartnerLink of OpportunityUploadService");
	return listOppPartnerLinkT;
    }

    /**
     * This method constructs list of OpportunityCustomerContactLinkT based on
     * the customer names provided
     * 
     * @param custNames
     * @param userId
     * @param map
     * @return List<OpportunityCustomerContactLinkT>
     * @throws Exception
     */
    private List<OpportunityCustomerContactLinkT> constructOppCustomerContactLink(
	    String custNames, String userId, Map<String, String> map)
	    throws Exception {

	List<OpportunityCustomerContactLinkT> listOppCustomerLinkT = null;
	logger.debug("Begin:Inside constructOppCustomerContactLink of OpportunityUploadService");
	if (custNames != null) {

	    listOppCustomerLinkT = new ArrayList<OpportunityCustomerContactLinkT>();
	    // get the Id values based on the values in the map
	    List<String> listOfcId = getValuesFromKeysSeparatedByComma(
		    custNames, map, "Customer Name");

	    if ((listOfcId != null) && (!listOfcId.isEmpty())) {
		for (String cId : listOfcId) {
		    OpportunityCustomerContactLinkT occlt = new OpportunityCustomerContactLinkT();

		    occlt.setContactId(cId);
		    occlt.setCreatedBy(userId);
		    occlt.setModifiedBy(userId);

		    listOppCustomerLinkT.add(occlt);
		}
	    }

	}
	logger.debug("End:Inside constructOppCustomerContactLink of OpportunityUploadService");
	return listOppCustomerLinkT;
    }

    /**
     * This method constructs list of OpportunityTcsAccountContactLinkT based on
     * the tcs associate names provided
     * 
     * @param tcsNames
     * @param userId
     * @param map
     * @return List<OpportunityTcsAccountContactLinkT>
     * @throws Exception
     */
    private List<OpportunityTcsAccountContactLinkT> constructOppTCSContactLink(
	    String tcsNames, String userId, Map<String, String> map)
	    throws Exception {

	List<OpportunityTcsAccountContactLinkT> listTcsContactLinkT = null;
	logger.debug("Begin:Inside constructOppTCSContactLink of OpportunityUploadService");

	if (tcsNames != null) {

	    listTcsContactLinkT = new ArrayList<OpportunityTcsAccountContactLinkT>();
	    // get the Id values based on the values in the map
	    List<String> listOfcId = getValuesFromKeysSeparatedByComma(
		    tcsNames, map, "TCS Contact Name");

	    if ((listOfcId != null) && (!listOfcId.isEmpty())) {
		for (String id : listOfcId) {
		    OpportunityTcsAccountContactLinkT occlt = new OpportunityTcsAccountContactLinkT();

		    occlt.setContactId(id);
		    occlt.setCreatedBy(userId);
		    occlt.setModifiedBy(userId);

		    listTcsContactLinkT.add(occlt);
		}
	    }

	}
	logger.debug("End:Inside constructOppTCSContactLink of OpportunityUploadService");
	return listTcsContactLinkT;
    }

    /**
     * This utility method get the keys which are separated by commas and
     * returns the values for the keys
     * 
     * @param keysWithSeparator
     * @param map
     * @return
     * @throws Exception
     */
    private List<String> getValuesFromKeysSeparatedByComma(
	    String keysWithSeparator, Map<String, String> map, String columnName)
	    throws Exception {

	List<String> listOfValues = null;

	if (keysWithSeparator != null) {

	    listOfValues = new ArrayList<String>();

	    String[] keysArray = keysWithSeparator.split(",");

	    for (String key : keysArray) {
		String value = getMapValuesForKey(map, key.trim());
		if (value != null) {
		    listOfValues.add(value);
		} else {
		    throw new DestinationException(HttpStatus.BAD_REQUEST,
			    "Invalid " + columnName);
		}
	    }

	}

	return listOfValues;

    }

    /**
     * This method retrieves the value for the key
     * 
     * @param map
     * @param key
     * @return String
     * @throws Exception
     */
    private String getMapValuesForKey(Map<String, String> map, String key)
	    throws Exception {
	String value = null;
	if (map.containsKey(key)) {
	    value = map.get(key);
	}
	return value;
    }

    /**
     * This method checks the spreadsheet's validate tab for any validation
     * related errors
     * 
     * @param workbook
     * @return boolean
     * @throws Exception
     */
    private boolean validateSheet(Workbook workbook) throws Exception {
	return ExcelUtils.isValidWorkbook(workbook,
		OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 4, 1)
		|| ExcelUtils.isValidWorkbook(workbook,
			OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 4, 2);
    }

}
