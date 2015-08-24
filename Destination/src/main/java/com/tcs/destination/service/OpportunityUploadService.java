package com.tcs.destination.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.CompetitorMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.ContactTMapDTO;
import com.tcs.destination.bean.CustomerMasterT;
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
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OpportunityCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.OpportunityPartnerLinkTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;

@Service
public class OpportunityUploadService {

    @PersistenceContext
    EntityManager entityManager;
    
    @Autowired
    OpportunityRepository opportunityRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PartnerRepository partnerRepository;
    
    @Autowired
    OpportunityPartnerLinkTRepository opportunityPartnerLinkTRepository;
    
    @Autowired
    ContactRepository contactRepository;
    
    @Autowired
    OpportunityCustomerContactLinkTRepository opportunityCustomerContactLinkTRepository;
    
    @Autowired
    OpportunityService opportunityService;
    
    @Autowired
    CompetitorRepository competitorRepository;
    
    Map<String, String> mapOfPartnerMasterT = null;
    Map<String, String> mapOfCustomerMasterT = null;
    Map<String, String> mapOfCustomerContactT = null;
    Map<String, String> mapOfTCSContactT = null;
    List<String> listOfCompetitorLink = null;

    private static final Logger logger = LoggerFactory
	    .getLogger(ReportsUploadService.class);

    public void saveDocument(MultipartFile multipartFile, String userId)
	    throws Exception {
	logger.debug("Inside saveDocument Service");
	File file = convert(multipartFile);
	boolean isBulkDataLoad = true;
	// File file = new
	// File("/Users/bnpp/Desktop/csv/beaconcustomermapping.xls");
	// document.setFileReference(fileBasePath);
	// System.out.println(multipartFile.getName());
	// System.out.println(multipartFile.getOriginalFilename());
	// System.out.println(multipartFile.getSize());
	// System.out.println(multipartFile.getInputStream());

	try {
	    FileInputStream fileInputStream = new FileInputStream(file);

	    Workbook workbook = WorkbookFactory.create(fileInputStream);

	    Sheet sheet = workbook.getSheetAt(0);

	    System.out.println("count "
		    + workbook.getSheetAt(0).getLastRowNum());

	    mapOfCustomerMasterT = getNameAndIdFromCustomerMasterT();
	    
	    // String key = "BG International UK";
	    // System.out.println("Value for "+key+" is "+getMapValuesForKey(mapOfCustomerMasterT,key));

	    mapOfPartnerMasterT = getNameAndIdFromPartnerMasterT();

	    ContactTMapDTO cmDTO = getNameAndIdFromContactT();
	    mapOfTCSContactT = cmDTO.getMapOfTcsContactT();
	    mapOfCustomerContactT = cmDTO.getMapOfCustomerContactT();
	    
//	    System.out.println("**********");    
//	    for(String x :ls){
//		System.out.println(x);
//	    }
//	    System.out.println("**********");

	    int rowCount = 0;
	    List<String> listOfCellValues = null;
	    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	    // Iterate through each rows one by one
	    Iterator<Row> rowIterator = sheet.iterator();
//int firstRow=sheet.getFirstRowNum();
//int lastRow=sheet.getLastRowNum();
//System.out.println("Last Row"+lastRow);
	    while (rowIterator.hasNext() && rowCount < 3) {
		System.out.println("row "+rowCount);
		Row row = rowIterator.next();
//		if (rowCount == 0) {
		    // noOfColumns = row.getPhysicalNumberOfCells();
//		}

		// For each row, iterate through all the columns
		Iterator<Cell> cellIterator = row.cellIterator();

		if (rowCount > 0) {
		    int i = 0;
		    
		    listOfCellValues = new ArrayList<String>();
//		    for(int i=0;i<44;i++){
		    while (cellIterator.hasNext() && i < 36) {
			System.out.print("cell " + i);
			Cell cell = cellIterator.next();
//			Cell cell = row.getCell(i);
//			Cell cell = row.getCell(i);
			
//			String value = getIndividualCellValue(cell);
			String value = getCellValue(cell);
			if (value != null) {
			    listOfCellValues.add(value);
			}
//			i++;
		    }
		    
		    for(String x : listOfCellValues){
			System.out.println(x);
		    }
		    
		    System.out.print("size of list "+listOfCellValues.size());
		    if((listOfCellValues.size()>0)&&(listOfCellValues.size()<=37)){
		    OpportunityT opp = new OpportunityT();

		    // Params for opportunity_t Table - from Excel Sheet
		    opp.setCustomerId(getMapValuesForKey(mapOfCustomerMasterT,
			    listOfCellValues.get(0))); // CUSTOMER ID
		    opp.setCountry(listOfCellValues.get(1)); // COUNTRY
		    opp.setCrmId(listOfCellValues.get(2).substring(0,
			    listOfCellValues.get(2).length() - 2));// CRM ID
		    opp.setOpportunityName(listOfCellValues.get(3));// OPPORTUNITY
		    // NAME
		    opp.setOpportunityDescription(listOfCellValues.get(4));// OPPORTUNITY
		    // DESCRIPTION

		    opp.setOpportunityRequestReceiveDate(df
			    .parse(listOfCellValues.get(7)));// REQUEST RECEIVE
		    // DATE
		    opp.setNewLogo(listOfCellValues.get(8));// new logo
		    opp.setStrategicInitiative(listOfCellValues.get(9));// strategic
		    // initiative
		    opp.setDigitalFlag(listOfCellValues.get(10));// DIGITAL FLAG
		    opp.setSalesStageCode((Integer.parseInt(listOfCellValues
			    .get(11).substring(0,
				    listOfCellValues.get(11).length() - 2))));// SALES
		    // STAGE
		    // CODE
		    opp.setDealCurrency(listOfCellValues.get(12));// DEAL
		    // CURRENCY
		    opp.setOverallDealSize(Double.valueOf(
			    listOfCellValues.get(13)).intValue());// OVERALL
		    // DEAL SIZE
		    opp.setDigitalDealValue(Double.valueOf(
			    listOfCellValues.get(14)).intValue());// DIGITAL
		    // DEAL VALUE
		    opp.setOpportunityOwner(listOfCellValues.get(15).substring(
			    0, listOfCellValues.get(15).length() - 2));// OPPORTUNITY
		    // OWNER

		    opp.setDealType(listOfCellValues.get(21));// DEAL TYPE
		    opp.setDealClosureDate(df.parse(listOfCellValues.get(22)));// DEAL
		    // CLOSURE
		    // DATE
		    opp.setEngagementDuration((listOfCellValues.get(23)));// ENGAGEMENT
		    // DURATION
		    opp.setEngagementStartDate(df.parse(listOfCellValues
			    .get(24)));// ENGAGEMENT START DATE

		    opp.setDescriptionForWinLoss(listOfCellValues.get(26)); // COMMENTS FOR WIN LOSS
		    
		    // Params for opportunity_t Table - manually set
		    opp.setDocumentsAttached("No");
		    opp.setCreatedBy(userId);
		    opp.setModifiedBy(userId);
		    
		    // Partner Params
		    //opp.setOpportunityPartnerLinkTs(constructOppPartnerLink(listOfCellValues.get(19), userId, ""));
		    opp.setOpportunityPartnerLinkTs(constructOppPartnerLink(listOfCellValues.get(19), userId, mapOfPartnerMasterT));
		    //opportunityPartnerLinkTRepository.save(opp.getOpportunityPartnerLinkTs());
		    
		    // Customer Contact Params
		    opp.setOpportunityCustomerContactLinkTs(constructOppCustomerContactLink(listOfCellValues.get(18), userId, mapOfCustomerContactT));
		    //opportunityCustomerContactLinkTRepository.save(opp.getOpportunityCustomerContactLinkTs());
		    
		    // TCS Contact Params
		    opp.setOpportunityTcsAccountContactLinkTs(constructOppTCSContactLink(listOfCellValues.get(17), userId, mapOfTCSContactT));
		    
		    // Competitor Params
		    opp.setOpportunityCompetitorLinkTs(constructOppCompetitorLink(listOfCellValues.get(20), userId));

		    // Sub Sp Params
		    opp.setOpportunitySubSpLinkTs(constructOppSubSpLink(listOfCellValues.get(5), userId));
		    
		    //OpportunityOfferingLinkT Params
		    opp.setOpportunityOfferingLinkTs(constructOppOfferingLink(listOfCellValues.get(6), userId));
		    
		    //OpportunityOfferingLinkT Params
		    opp.setOpportunitySalesSupportLinkTs(constructOppSalesSupportLink(listOfCellValues.get(16), userId));
		    
		    //Bid Details
		    opp.setBidDetailsTs(constructbidDetailsT(listOfCellValues.get(27), listOfCellValues.get(29), listOfCellValues.get(30), listOfCellValues.get(31), listOfCellValues.get(32), listOfCellValues.get(33), listOfCellValues.get(34), userId));

		    // FACTORS FOR WIN LOSS - opportunity_win_loss_factors_t
		    opp.setOpportunityWinLossFactorsTs(constructOppWinLoss(listOfCellValues.get(25), userId));
		    
		    
		    System.out.println("Inserting...");
		    opportunityService.createOpportunity(opp, isBulkDataLoad);
		    System.out.println("Done");
		    
		    }
		}
		rowCount++;
	    }

	    fileInputStream.close();

	} catch (Exception e) {
	    e.printStackTrace();
	}
	// return fileBasePath;
    }
    
    private String getIndividualCellValue(Cell cell){
	
	String val="";
        if(cell!=null){
            switch(cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    val = String.valueOf(cell.getNumericCellValue());
//                    System.out.print(cell.getNumericCellValue() + "\t\t");
                    break;
                case Cell.CELL_TYPE_STRING:
                    val = String.valueOf(cell.getStringCellValue());
//                    System.out.print(cell.getStringCellValue() + "\t\t");
                    break;
                case Cell.CELL_TYPE_BLANK:
                    val="  ---  ";
//                    System.out.print("blank inside");
                    break;
            }
        }
        else {
            val="  ---  ";
        }
        return val;
	
    }

    private List<OpportunityWinLossFactorsT> constructOppWinLoss(
	    String factors, String userId) {
	
	List<OpportunityWinLossFactorsT> listOfWinLossFactors = null;
	
	if(factors!=null){
	    
	    listOfWinLossFactors = new ArrayList<OpportunityWinLossFactorsT>();
	    
	    String[] factorsArray = factors.split(",");
	    
	    int rank = 1;
	    for(String factor : factorsArray){
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

    private List<String> getCompetitorFromCompetitorLink(){
	
	List<String> compLink = new ArrayList<String>();
	
	List<CompetitorMappingT> listOfCompMapping =  (List<CompetitorMappingT>) competitorRepository.findAll();
	
	if((listOfCompMapping!=null)&&(!listOfCompMapping.isEmpty())){
    	for (CompetitorMappingT cmt : listOfCompMapping) {
    	    compLink.add(cmt.getCompetitorName());
    	}
	}
	return compLink;
	
    }
    
    private List<OpportunityCompetitorLinkT> constructOppCompetitorLink(String values,String userId) {

    	List<OpportunityCompetitorLinkT> listCompetitorLink = null;
    	if (values != null) {
    	    
    	    listCompetitorLink = new ArrayList<OpportunityCompetitorLinkT>();
    	    
    	    String[] valuesArray = values.split(",");
	    
    	    for (String value : valuesArray) {
    		OpportunityCompetitorLinkT oclt = new OpportunityCompetitorLinkT();
    		
    		oclt.setCompetitorName(value.trim());
    		oclt.setCreatedBy(userId);
    		oclt.setModifiedBy(userId);
//    		oclt.setOpportunityId(opportunityId);
    		oclt.setIncumbentFlag("N"); // To be Checked

    		listCompetitorLink.add(oclt);
    	    }

    	}
    	
    	return listCompetitorLink;
    }

    private List<OpportunitySalesSupportLinkT> constructOppSalesSupportLink(String values,String userId) {

    	List<OpportunitySalesSupportLinkT> listOfOppSubSpLink = null;
    	if (values != null) {
    	    
    	listOfOppSubSpLink = new ArrayList<OpportunitySalesSupportLinkT>();
    	    System.out.println(values+" ------------------>>>");
    	    String[] valuesArray = values.split(",");
	    
    	    for (String value : valuesArray) {
    		OpportunitySalesSupportLinkT oclt = new OpportunitySalesSupportLinkT();
    		
    		oclt.setSalesSupportOwner(validateAndRectifyValue(value.trim()));
    		oclt.setCreatedBy(userId);
    		oclt.setModifiedBy(userId);

    		listOfOppSubSpLink.add(oclt);
    	    }

    	}
    	
    	return listOfOppSubSpLink;
    
    }
    
    private List<BidDetailsT> constructbidDetailsT(String bidReqType, String bidReqDate, String targetSubmissionDate, String actualSubmissionDate, String expectedOutcomeDate, String winProbability, String coreAttributes, String userId) throws ParseException {

    	List<BidDetailsT> listOfBidDetailsT = new ArrayList<BidDetailsT>();
    	
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    	
    	BidDetailsT bdt = new BidDetailsT();
    	
    	bdt.setBidRequestType(bidReqType);
    	bdt.setBidRequestReceiveDate(df.parse(bidReqDate));
    	bdt.setTargetBidSubmissionDate(df.parse(targetSubmissionDate));
    	bdt.setActualBidSubmissionDate(df.parse(actualSubmissionDate));
    	bdt.setExpectedDateOfOutcome(df.parse(expectedOutcomeDate));
    	bdt.setWinProbability(winProbability);
    	bdt.setCoreAttributesUsedForWinning(coreAttributes);
    	bdt.setCreatedBy(userId);
    	bdt.setModifiedBy(userId);
    	
    	listOfBidDetailsT.add(bdt);
      	
    	return listOfBidDetailsT;
    
    }
    
    private String validateAndRectifyValue(String value) {
	System.out.println("******** ////// ");
	String val=value;
	System.out.println(value.substring(value.length()-2, value.length()));
	if(value!=null){
	    if(value.substring(value.length()-2, value.length()).equals(".0")){
		val = value.substring(0, value.length()-2);
	    }
	}
	System.out.println(val);
	System.out.println("******** ////// ");
	return val;
    }

    private List<OpportunitySubSpLinkT> constructOppSubSpLink(String values,String userId) {

    	List<OpportunitySubSpLinkT> listOfOppSubSpLink = null;
    	if (values != null) {
    	    
    	listOfOppSubSpLink = new ArrayList<OpportunitySubSpLinkT>();
    	    
    	    String[] valuesArray = values.split(",");
	    
    	    for (String value : valuesArray) {
    		OpportunitySubSpLinkT oclt = new OpportunitySubSpLinkT();
    		
    		oclt.setSubSp(value.trim());
    		oclt.setCreatedBy(userId);
    		oclt.setModifiedBy(userId);
//    		oclt.setOpportunityId(opportunityId);


    		listOfOppSubSpLink.add(oclt);
    	    }

    	}
    	
    	return listOfOppSubSpLink;
    
    }
    
    private List<OpportunityOfferingLinkT> constructOppOfferingLink(String values,String userId) {

    	List<OpportunityOfferingLinkT> listOfOppOfferingLink = null;
    	if (values != null) {
    	    
    	listOfOppOfferingLink = new ArrayList<OpportunityOfferingLinkT>();
    	    
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
    
//    private List<String> splitValuesBasedOnSeparator(String keysWithSeparator, List<String> list) {
//        
//	List<String> listOfValues = null;
//
//	if(keysWithSeparator!=null){
//	    
//	    listOfValues = new ArrayList<String>();
//	    
//	    String[] keysArray = keysWithSeparator.split(",");
//	    
//	    for (String key : keysArray) {
//		listOfValues.add(getMapValuesForKey(map, key.trim()));
////		listOfKeys.add(key.trim());
//	    }
//
////	    for (String key : listOfValues) {
////		System.out.println(key);
////	    }
//	    
//	}
//
//	return listOfValues;
//
//    }

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
		// value = value.substring(0,value.length()-2);
	    }
	    break;
	case Cell.CELL_TYPE_STRING:
	    value = cell.getStringCellValue();
	    break;
	case Cell.CELL_TYPE_BLANK: {
	    value = "";
//	    value = cell.getStringCellValue()+"1000";
	    System.out.println("Blank ----------->>>>");
	    break;
	}
	}
	// System.out.println(value);
	return value;
    }

    public File convert(MultipartFile file) throws Exception {
	File convFile = new File(file.getOriginalFilename());
	convFile.createNewFile();
	FileOutputStream fos = new FileOutputStream(convFile);
	fos.write(file.getBytes());
	fos.close();
	return convFile;
    }

    private Map<String, String> getNameAndIdFromCustomerMasterT() {

	List<CustomerMasterT> listOfCustomerMasterT = null;
	listOfCustomerMasterT = (List<CustomerMasterT>) customerRepository
		.findAll();

	Map<String, String> mapOfCMT = new HashMap<String, String>();

	for (CustomerMasterT cmt : listOfCustomerMasterT) {
	    mapOfCMT.put(cmt.getCustomerName().trim(), cmt.getCustomerId().trim());
	}

	// for (Map.Entry<String,String> entry : mapOfCMT.entrySet()) {
	// System.out.println(entry.getKey()+" "+entry.getValue());
	// }

	return mapOfCMT;

    }

    private Map<String, String> getNameAndIdFromPartnerMasterT() {

	List<PartnerMasterT> listOfPartnerMasterT = null;
	listOfPartnerMasterT = (List<PartnerMasterT>) partnerRepository
		.findAll();

	Map<String, String> mapOfPMT = new HashMap<String, String>();

	for (PartnerMasterT pmt : listOfPartnerMasterT) {
	    mapOfPMT.put(pmt.getPartnerName().trim(), pmt.getPartnerId().trim());
	}

//	for (Map.Entry<String, String> entry : mapOfPMT.entrySet()) {
//	    System.out.println(entry.getKey() + " " + entry.getValue());
//	}

	return mapOfPMT;

    }
    
    private ContactTMapDTO getNameAndIdFromContactT() {
        
	List<ContactT> listOfContactT = null;
	listOfContactT = (List<ContactT>) contactRepository.findAll();

	for(ContactT cc : listOfContactT){
//	    System.out.println(cc.getContactId());
	}
	
	ContactTMapDTO cmDTO = new ContactTMapDTO();
	
	Map<String, String> mapOfCustomerContactT = null;
	Map<String, String> mapOfTcsContactT = null;

	if((listOfContactT!=null)&&(!listOfContactT.isEmpty())){
	mapOfCustomerContactT = new HashMap<String, String>();
    	mapOfTcsContactT = new HashMap<String, String>();
	for (ContactT ct : listOfContactT) {
	    if((ct.getContactCategory().equals(EntityType.PARTNER.toString())&&(ct.getContactType().equals(ContactType.EXTERNAL.toString())))){
		mapOfCustomerContactT.put(ct.getContactName().trim(), ct.getContactId().trim());
	    } else if((ct.getContactCategory().equals(EntityType.CUSTOMER.toString())&&(ct.getContactType().equals(ContactType.INTERNAL.toString())))){
		mapOfTcsContactT.put(ct.getContactName().trim(), ct.getContactId().trim());
		
	    }
	}
	
	if(mapOfCustomerContactT!=null){
	    cmDTO.setMapOfCustomerContactT(mapOfCustomerContactT);
	}
	if(mapOfTcsContactT!=null){
	    cmDTO.setMapOfTcsContactT(mapOfTcsContactT);
	}

//	for (Map.Entry<String, String> entry : mapOfTcsContactT.entrySet()) {
//	    System.out.println(entry.getKey() + " " + entry.getValue());
//	}
	
	}

	return cmDTO;

    }
    
    private List<OpportunityPartnerLinkT> constructOppPartnerLink(
    	    String partnerValues,String userId, Map map) {
	
    	List<OpportunityPartnerLinkT> listOppPartnerLinkT = null;
    	if (partnerValues != null) {
    	    
    	    listOppPartnerLinkT = new ArrayList<OpportunityPartnerLinkT>();
    	    List<String> listOfpId = splitValuesBasedOnSeparator(partnerValues, map);
    	    
    	    if ((listOfpId != null) && (!listOfpId.isEmpty())) {
    		for (String pId : listOfpId) {
    		    OpportunityPartnerLinkT oplt = new OpportunityPartnerLinkT();

    		    oplt.setPartnerId(pId);
    		    oplt.setCreatedBy(userId);
    		    oplt.setModifiedBy(userId);
//    		    oplt.setOpportunityId(oppId);
    		    
    		    listOppPartnerLinkT.add(oplt);
    		}
    	    }
    	}
    	
    	return listOppPartnerLinkT;
    }
    
    private List<OpportunityCustomerContactLinkT> constructOppCustomerContactLink(
    	    String custNames,String userId, Map map) {
	
    	List<OpportunityCustomerContactLinkT> listOppCustomerLinkT = null;
    	if (custNames != null) {

    	    listOppCustomerLinkT = new ArrayList<OpportunityCustomerContactLinkT>();
    	    List<String> listOfcId = splitValuesBasedOnSeparator(custNames, map);
    	    
    	    if ((listOfcId != null) && (!listOfcId.isEmpty())) {
    		for (String cId : listOfcId) {
    		OpportunityCustomerContactLinkT occlt = new OpportunityCustomerContactLinkT();

    		occlt.setContactId(cId);
    		occlt.setCreatedBy(userId);
    		occlt.setModifiedBy(userId);
//    		occlt.setOpportunityId(oppId);
    		    
    		listOppCustomerLinkT.add(occlt);
    		}
    	    }
    	
    	}
    	
    	return listOppCustomerLinkT;
    }
    
    private List<OpportunityTcsAccountContactLinkT> constructOppTCSContactLink(
    	    String tcsNames,String userId, Map map) {
	
    	List<OpportunityTcsAccountContactLinkT> listTcsContactLinkT = null;
    	if (tcsNames != null) {
    	    
    	listTcsContactLinkT = new ArrayList<OpportunityTcsAccountContactLinkT>();
    	    List<String> listOfcId = splitValuesBasedOnSeparator(tcsNames, map);
    	    
    	    if ((listOfcId != null) && (!listOfcId.isEmpty())) {
    		for (String id : listOfcId) {
    		OpportunityTcsAccountContactLinkT occlt = new OpportunityTcsAccountContactLinkT();
    		    
    		occlt.setContactId(id);
    		occlt.setCreatedBy(userId);
    		occlt.setModifiedBy(userId);
//    		occlt.setOpportunityId(oppId);
    		    
    		listTcsContactLinkT.add(occlt);
    		}
    	    }
    	
    	}
    	
    	return listTcsContactLinkT;
    }

    private List<String> splitValuesBasedOnSeparator(String keysWithSeparator, Map map) {

	List<String> listOfValues = null;

	if(keysWithSeparator!=null){
	    
	    listOfValues = new ArrayList<String>();
	    
	    String[] keysArray = keysWithSeparator.split(",");
	    
	    for (String key : keysArray) {
		listOfValues.add(getMapValuesForKey(map, key.trim()));
//		listOfKeys.add(key.trim());
	    }

//	    for (String key : listOfValues) {
//		System.out.println(key);
//	    }
	    
	}

	return listOfValues;

    }

    private String getMapValuesForKey(Map<String, String> map, String key) {
	String value = null;
	if (map.containsKey(key)) {
	    value = map.get(key);
	}
	return value;
    }


    
}
