package com.tcs.destination.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityCustomerContactLinkT;
import com.tcs.destination.bean.OpportunityOfferingLinkT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ExcelUtils;

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

	private Map<String, String> mapOfPartnerMasterT = null;
	private Map<String, String> mapOfCustomerMasterT = null;
	private Map<String, String> mapOfCustomerContactT = null;
	private Map<String, String> mapOfTCSContactT = null;
	private Map<String, String> mapOfUserT = null;

	private static final DateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy");
	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityUploadService.class);

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

		logger.debug("Inside saveDocument Service");

		UploadStatusDTO uploadStatus = null;

		try {

			File file = convert(multipartFile);

			FileInputStream fileInputStream = new FileInputStream(file);

			Workbook workbook = WorkbookFactory.create(fileInputStream);

			uploadStatus = new UploadStatusDTO();
			uploadStatus
					.setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());

			// Validates the spreadsheet for errors after validating the excel
			// sheet
			if (validateSheet(workbook)) {

				Sheet sheet = workbook.getSheetAt(2);

				logger.debug("count " + workbook.getSheetAt(2).getLastRowNum());

				System.out.println("count "
						+ workbook.getSheetAt(2).getLastRowNum());

				boolean isBulkDataLoad = true;

				uploadStatus.setStatusFlag(true);

				// Get Customer Name and Id from corresponding tables
				mapOfCustomerMasterT = getNameAndIdFromCustomerMasterT();

				mapOfPartnerMasterT = getNameAndIdFromPartnerMasterT();

				ContactTMapDTO cmDTO = getNameAndIdFromContactT();
				mapOfTCSContactT = cmDTO.getMapOfTcsContactT();
				mapOfCustomerContactT = cmDTO.getMapOfCustomerContactT();

				mapOfUserT = getNameAndIdFromUserT();

				int rowCount = 0;
				List<String> listOfCellValues = null;

				// Iterate through each rows one by one
				Iterator<Row> rowIterator = sheet.iterator();

				while (rowIterator.hasNext()
						&& rowCount <= sheet.getLastRowNum()) {
					// logger.debug("row "+rowCount);
					Row row = rowIterator.next();
					// if (rowCount == 0) {
					// noOfColumns = row.getPhysicalNumberOfCells();
					// }

					// For each row, iterate through all the columns
					// Iterator<Cell> cellIterator = row.cellIterator();

					if (rowCount > 1) {
						// int cellCount = 0;
						int emptyCount = 0;
						logger.debug("Total Cells"
								+ row.getPhysicalNumberOfCells());

						listOfCellValues = new ArrayList<String>();
						for (int cellCount = 0; cellCount < 43; cellCount++) {
							// while (cellIterator.hasNext() && i < 36) {
							// Cell cell = cellIterator.next();
							Cell cell = row.getCell(cellCount);

							String value = getIndividualCellValue(cell);
							// String value = getCellValue(cell);
							if (value != null) {
								listOfCellValues.add(value.trim());
								if (value.equals("")) {
									emptyCount++;
								}
							}
							logger.debug(cellCount + "      " + value);
							// cellCount++;
						}

						logger.debug("size of list " + listOfCellValues.size());
						logger.debug("empty count " + emptyCount);

						// set the cell values to the corresponding fields in
						// the OpportunityT entity object
						if ((listOfCellValues.size() > 0) && (emptyCount < 43)) {

							try {
								OpportunityT opp = new OpportunityT();

								// CUSTOMER ID
								if (!StringUtils.isEmpty(listOfCellValues
										.get(2))) {
									String custId = getMapValuesForKey(
											mapOfCustomerMasterT,
											listOfCellValues.get(2).trim());
									if (custId != null) {
										opp.setCustomerId(custId);
									} else {
										throw new DestinationException(
												HttpStatus.NOT_FOUND,
												"Invalid Customer Name");
									}
								} else {
									throw new DestinationException(
											HttpStatus.BAD_REQUEST,
											"Customer Name is empty");
								}

								// COUNTRY
								if (!StringUtils.isEmpty(listOfCellValues
										.get(5))) {
									opp.setCountry(listOfCellValues.get(5));
								} else {
									throw new DestinationException(
											HttpStatus.BAD_REQUEST,
											"Country is empty");
								}

								// CRM ID
								if (!StringUtils.isEmpty(listOfCellValues
										.get(6))) {
									opp.setCrmId(listOfCellValues.get(6)
											.substring(
													0,
													listOfCellValues.get(6)
															.length() - 2));
								}

								// OPPORTUNITY NAME
								if (!StringUtils.isEmpty(listOfCellValues
										.get(7))) {
									opp.setOpportunityName(listOfCellValues
											.get(7));
								} else {
									throw new DestinationException(
											HttpStatus.BAD_REQUEST,
											"Opportunity Name is empty");
								}

								// OPPORTUNITY DESCRIPTION
								if (!StringUtils.isEmpty(listOfCellValues
										.get(8))) {
									if (listOfCellValues.get(8).length() <= Constants.OPPORTUNITY_DESC_MAX_SIZE) {
										opp.setOpportunityDescription(listOfCellValues
												.get(8));
									} else {
										throw new DestinationException(
												HttpStatus.BAD_REQUEST,
												"Opportunity Description should be a maximum of "
														+ Constants.OPPORTUNITY_DESC_MAX_SIZE
														+ " characters");
									}
								}

								// REQUEST RECEIVE DATE
								if (!StringUtils.isEmpty(listOfCellValues
										.get(11))) {
									opp.setOpportunityRequestReceiveDate(dateFormat
											.parse(listOfCellValues.get(11)));
								} else {
									throw new DestinationException(
											HttpStatus.BAD_REQUEST,
											"Request Receive Date ("
													+ listOfCellValues.get(11)
													+ ") is empty");
								}

								// new logo
								if (!StringUtils.isEmpty(listOfCellValues
										.get(12))) {
									opp.setNewLogo(listOfCellValues.get(12));
								}

								// strategic initiative
								if (!StringUtils.isEmpty(listOfCellValues
										.get(13))) {
									opp.setStrategicInitiative(listOfCellValues
											.get(13));
								}

								// DIGITAL FLAG
								if (!StringUtils.isEmpty(listOfCellValues
										.get(14))) {
									opp.setDigitalFlag(listOfCellValues.get(14));
								}

								// SALES STAGE CODE
								if (!StringUtils.isEmpty(listOfCellValues
										.get(15))) {
									opp.setSalesStageCode((Integer
											.parseInt(listOfCellValues.get(15)
													.substring(0, 2))));
								} else {
									throw new DestinationException(
											HttpStatus.BAD_REQUEST,
											"Sales Stage Code is empty");
								}

								// DEAL CURRENCY
								if (!StringUtils.isEmpty(listOfCellValues
										.get(16))) {
									opp.setDealCurrency(listOfCellValues
											.get(16));
								}

								// OverallDealSize
								if (!StringUtils.isEmpty(listOfCellValues
										.get(17))) {
									opp.setOverallDealSize(Double.valueOf(
											listOfCellValues.get(17))
											.intValue());
								}

								// DIGITAL DEAL VALUE
								if (!StringUtils.isEmpty(listOfCellValues
										.get(19))) {
									opp.setDigitalDealValue(Double.valueOf(
											listOfCellValues.get(19))
											.intValue());
								}

								// OPPORTUNITY OWNER
								if (!StringUtils.isEmpty(listOfCellValues
										.get(21))) {
									// opp.setOpportunityOwner(listOfCellValues.get(21).substring(0,
									// listOfCellValues.get(21).length() - 2));
									String oppOwner = getMapValuesForKey(
											mapOfUserT, listOfCellValues
													.get(21).trim());
									if (oppOwner != null) {
										opp.setOpportunityOwner(oppOwner);
									} else {
										throw new DestinationException(
												HttpStatus.NOT_FOUND,
												"Invalid Opportunity Owner");
									}

								} else {
									throw new DestinationException(
											HttpStatus.BAD_REQUEST,
											"Opportunity Owner is empty");
								}

								// DEAL TYPE
								if (!StringUtils.isEmpty(listOfCellValues
										.get(35))) {
									opp.setDealType(listOfCellValues.get(35));
								}

								// DEAL CLOSURE DATE
								if (!StringUtils.isEmpty(listOfCellValues
										.get(36))) {
									opp.setDealClosureDate(dateFormat
											.parse(listOfCellValues.get(36)));
								}

								// ENGAGEMENT DURATION
								if (!StringUtils.isEmpty(listOfCellValues
										.get(37))) {
									if (listOfCellValues.get(37).length() <= Constants.ENGAGEMENT_DURATION_MAX_SIZE) {
										opp.setEngagementDuration((listOfCellValues
												.get(37)));
									} else {
										throw new DestinationException(
												HttpStatus.BAD_REQUEST,
												"Engagement duration should be a maximum of "
														+ Constants.ENGAGEMENT_DURATION_MAX_SIZE
														+ " characters");
									}
								}

								// ENGAGEMENT START DATE
								if (!StringUtils.isEmpty(listOfCellValues
										.get(38))) {
									opp.setEngagementStartDate(dateFormat
											.parse(listOfCellValues.get(38)));
								}

								// COMMENTS FOR WIN LOSS
								if (!StringUtils.isEmpty(listOfCellValues
										.get(40))) {
									opp.setDescriptionForWinLoss(listOfCellValues
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

								// // Customer Contact Params
								// if(!StringUtils.isEmpty(listOfCellValues.get(24))){
								// opp.setOpportunityCustomerContactLinkTs(constructOppCustomerContactLink(listOfCellValues.get(24),
								// userId, mapOfCustomerContactT));
								// }
								//
								// // TCS Contact Params
								// if(!StringUtils.isEmpty(listOfCellValues.get(23))){
								// opp.setOpportunityTcsAccountContactLinkTs(constructOppTCSContactLink(listOfCellValues.get(23),
								// userId, mapOfTCSContactT));
								// }

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
											listOfCellValues.get(9), userId));
								}

								// OpportunityOfferingLinkT Params
								if (!StringUtils.isEmpty(listOfCellValues
										.get(10))) {
									opp.setOpportunityOfferingLinkTs(constructOppOfferingLink(
											listOfCellValues.get(10), userId));
								}

								// //OpportunitySalesSupportLinkT Params
								// if(!StringUtils.isEmpty(listOfCellValues.get(22))){
								// //opp.setOpportunityOwner(getMapValuesForKey(mapOfUserT,
								// listOfCellValues.get(21).trim()));
								// opp.setOpportunitySalesSupportLinkTs(constructOppSalesSupportLink(listOfCellValues.get(22),
								// userId));
								// }

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
								// opportunity_win_loss_factors_t
								if (!StringUtils.isEmpty(listOfCellValues
										.get(39))) {
									opp.setOpportunityWinLossFactorsTs(constructOppWinLoss(
											listOfCellValues.get(39), userId));
								}

								// Deal Status Remarks
								if (!StringUtils.isEmpty(listOfCellValues
										.get(41))) {
									if (listOfCellValues.get(41).length() <= Constants.NOTEST_MAX_SIZE) {
										opp.setNotesTs(constructNotesT(
												listOfCellValues.get(41),
												opp.getCustomerId(), userId));
									} else {
										throw new DestinationException(
												HttpStatus.BAD_REQUEST,
												"Deal Status should be a maximum of "
														+ Constants.NOTEST_MAX_SIZE
														+ " characters");
									}
								}

								logger.debug("Inserting...");

								opportunityService.createOpportunity(opp,
										isBulkDataLoad);
								logger.debug("Done");

								// } catch(DestinationException e){
								// Catch the exception pertaining to a
								// particular row and continue iteration
								/*
								 * if(uploadStatus.isStatusFlag()){
								 * uploadStatus.setStatusFlag(false); }
								 * 
								 * UploadServiceErrorDetailsDTO error = new
								 * UploadServiceErrorDetailsDTO();
								 * 
								 * error.setRowNumber(rowCount+1);
								 * error.setMessage(e.getMessage());
								 * 
								 * uploadStatus.getListOfErrors().add(error);
								 */
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
								// logger.error("INTERNAL_SERVER_ERROR: An Exception has occured while processing the request for : {}",
								// userId);
								// throw new
								// DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
								// "An Exception has occured while processing the request for "+userId);
							}
						} else if (emptyCount == 43) {
							// If a row is empty, assume that there are no
							// values to process and exit the iteration
							logger.debug("BREAK...");
							break;
						}

						logger.debug("*************************************");
					}
					rowCount++;
				}

				fileInputStream.close();
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
		return uploadStatus;
	}

	/**
	 * This method constructs NotesT object
	 * 
	 * @param dealRemarks
	 * @param customerId
	 * @param userUpdated
	 * @return
	 */
	private List<NotesT> constructNotesT(String dealRemarks, String customerId,
			String userUpdated) {
		NotesT notes = new NotesT();
		notes.setEntityType(EntityType.OPPORTUNITY.toString());
		notes.setNotesUpdated(dealRemarks);
		notes.setCustomerId(customerId);
		notes.setUserUpdated(userUpdated);
		List<NotesT> listOfNotes = new ArrayList<NotesT>();
		listOfNotes.add(notes);
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

		List<OpportunityWinLossFactorsT> listOfWinLossFactors = null;

		if (factors != null) {

			listOfWinLossFactors = new ArrayList<OpportunityWinLossFactorsT>();

			// factors can be comma separated, hence split() is used
			String[] factorsArray = factors.split(",");

			// rank is set based on the order of factors which are given as
			// input
			int rank = 1;
			for (String factor : factorsArray) {
				OpportunityWinLossFactorsT owlf = new OpportunityWinLossFactorsT();

				owlf.setCreatedBy(userId);
				owlf.setModifiedBy(userId);
				owlf.setRank(rank);
				owlf.setWinLossFactor(factor.trim());

				listOfWinLossFactors.add(owlf);
				rank++;
			}

		}

		return listOfWinLossFactors;
	}

	/**
	 * This method provides the values of competitor name from
	 * CompetitorMappingT in list of String
	 * 
	 * @return List<String>
	 * @throws Exception
	 */
	private List<String> getCompetitorFromCompetitorLink() throws Exception {

		List<String> compLink = new ArrayList<String>();

		List<CompetitorMappingT> listOfCompMapping = (List<CompetitorMappingT>) competitorRepository
				.findAll();

		if ((listOfCompMapping != null) && (!listOfCompMapping.isEmpty())) {
			for (CompetitorMappingT cmt : listOfCompMapping) {
				compLink.add(cmt.getCompetitorName());
			}
		}
		return compLink;

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
		if (values != null) {

			listCompetitorLink = new ArrayList<OpportunityCompetitorLinkT>();

			// More than one competitor could be provided separated by comma,
			// hence split() is used
			String[] valuesArray = values.split(",");

			for (String value : valuesArray) {
				OpportunityCompetitorLinkT oclt = new OpportunityCompetitorLinkT();

				oclt.setCompetitorName(value.trim());
				oclt.setCreatedBy(userId);
				oclt.setModifiedBy(userId);
				oclt.setIncumbentFlag(Constants.N);

				listCompetitorLink.add(oclt);
			}

		}

		return listCompetitorLink;
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
		if (values != null) {

			listOfOppSubSpLink = new ArrayList<OpportunitySalesSupportLinkT>();

			// More than one Sales Support Owner could be provided separated by
			// comma, hence split() is used
			String[] valuesArray = values.split(",");

			for (String value : valuesArray) {
				OpportunitySalesSupportLinkT oclt = new OpportunitySalesSupportLinkT();

				// SalesSupportOwner comes with location separated by '-', hence
				// split is used
				String[] ssValue = value.split("-");
				oclt.setSalesSupportOwner(getMapValuesForKey(mapOfUserT,
						ssValue[0].trim()));
				oclt.setCreatedBy(userId);
				oclt.setModifiedBy(userId);

				listOfOppSubSpLink.add(oclt);
			}

		}

		return listOfOppSubSpLink;

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
			throws ParseException {

		List<BidDetailsT> listOfBidDetailsT = new ArrayList<BidDetailsT>();

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
			bdt.setWinProbability(winProbability);
		}
		if (!StringUtils.isEmpty(coreAttributes)) {
			bdt.setCoreAttributesUsedForWinning(coreAttributes);
		}
		bdt.setCreatedBy(userId);
		bdt.setModifiedBy(userId);

		listOfBidDetailsT.add(bdt);

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
		String val = value;
		if (value != null) {
			if (value.substring(value.length() - 2, value.length())
					.equals(".0")) {
				val = value.substring(0, value.length() - 2);
			}
		}
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

		List<OpportunitySubSpLinkT> listOfOppSubSpLink = null;
		if (values != null) {

			listOfOppSubSpLink = new ArrayList<OpportunitySubSpLinkT>();

			// More than one SubSp could be provided separated by comma, hence
			// split() is used
			String[] valuesArray = values.split(",");

			for (String value : valuesArray) {
				OpportunitySubSpLinkT oclt = new OpportunitySubSpLinkT();

				oclt.setSubSp(value.trim());
				oclt.setCreatedBy(userId);
				oclt.setModifiedBy(userId);

				listOfOppSubSpLink.add(oclt);
			}

		}

		return listOfOppSubSpLink;

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

		List<OpportunityOfferingLinkT> listOfOppOfferingLink = null;
		if (values != null) {

			listOfOppOfferingLink = new ArrayList<OpportunityOfferingLinkT>();

			// More than one Offering could be provided separated by comma,
			// hence split() is used
			String[] valuesArray = values.split(",");

			for (String value : valuesArray) {
				OpportunityOfferingLinkT oolt = new OpportunityOfferingLinkT();

				oolt.setOffering(value.trim());
				oolt.setCreatedBy(userId);
				oolt.setModifiedBy(userId);

				listOfOppOfferingLink.add(oolt);
			}

		}

		return listOfOppOfferingLink;

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
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
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

		List<CustomerMasterT> listOfCustomerMasterT = null;
		listOfCustomerMasterT = (List<CustomerMasterT>) customerRepository
				.findAll();

		Map<String, String> mapOfCMT = new HashMap<String, String>();

		for (CustomerMasterT cmt : listOfCustomerMasterT) {
			mapOfCMT.put(cmt.getCustomerName().trim(), cmt.getCustomerId()
					.trim());
		}

		// for (Map.Entry<String,String> entry : mapOfCMT.entrySet()) {
		// logger.debug(entry.getKey()+" "+entry.getValue());
		// }

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

		List<PartnerMasterT> listOfPartnerMasterT = null;
		listOfPartnerMasterT = (List<PartnerMasterT>) partnerRepository
				.findAll();

		Map<String, String> mapOfPMT = new HashMap<String, String>();

		for (PartnerMasterT pmt : listOfPartnerMasterT) {
			mapOfPMT.put(pmt.getPartnerName().trim(), pmt.getPartnerId().trim());
		}

		// for (Map.Entry<String, String> entry : mapOfPMT.entrySet()) {
		// logger.debug(entry.getKey() + " " + entry.getValue());
		// }

		return mapOfPMT;

	}

	/**
	 * This method retrieves Customer Name and Id from UserT
	 * 
	 * @return Map<String, String>
	 * @throws Exception
	 */
	private Map<String, String> getNameAndIdFromUserT() throws Exception {

		List<UserT> listOfUsers = null;
		listOfUsers = (List<UserT>) userRepository.findAll();

		Map<String, String> mapOfUserT = new HashMap<String, String>();

		for (UserT ut : listOfUsers) {
			mapOfUserT.put(ut.getUserName().trim(), ut.getUserId().trim());
		}

		for (Map.Entry<String, String> entry : mapOfUserT.entrySet()) {
			logger.debug(entry.getKey() + " " + entry.getValue());
		}

		return mapOfUserT;

	}

	/**
	 * This method retrieves Customer Name and Id from ContactT
	 * 
	 * @return
	 * @throws Exception
	 */
	private ContactTMapDTO getNameAndIdFromContactT() throws Exception {

		List<ContactT> listOfContactT = null;
		listOfContactT = (List<ContactT>) contactRepository.findAll();

		// for(ContactT cc : listOfContactT){
		// logger.debug(cc.getContactId());
		// }

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

			// for (Map.Entry<String, String> entry :
			// mapOfTcsContactT.entrySet()) {
			// logger.debug(entry.getKey() + " " + entry.getValue());
			// }

		}

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
		if (partnerValues != null) {

			listOppPartnerLinkT = new ArrayList<OpportunityPartnerLinkT>();
			// get the Id values based on the values in the map
			List<String> listOfpId = getValuesFromKeysSeparatedByComma(
					partnerValues, map);

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
		if (custNames != null) {

			listOppCustomerLinkT = new ArrayList<OpportunityCustomerContactLinkT>();
			// get the Id values based on the values in the map
			List<String> listOfcId = getValuesFromKeysSeparatedByComma(
					custNames, map);

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
		if (tcsNames != null) {

			listTcsContactLinkT = new ArrayList<OpportunityTcsAccountContactLinkT>();
			// get the Id values based on the values in the map
			List<String> listOfcId = getValuesFromKeysSeparatedByComma(
					tcsNames, map);

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
			String keysWithSeparator, Map<String, String> map) throws Exception {

		List<String> listOfValues = null;

		if (keysWithSeparator != null) {

			listOfValues = new ArrayList<String>();

			String[] keysArray = keysWithSeparator.split(",");

			for (String key : keysArray) {
				listOfValues.add(getMapValuesForKey(map, key.trim()));
			}

			// for (String key : listOfValues) {
			// logger.debug(key);
			// }

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
				Constants.VALIDATOR_SHEET_NAME, 4, 1)
				&& ExcelUtils.isValidWorkbook(workbook,
						Constants.VALIDATOR_SHEET_NAME, 4, 2);
	}

}
