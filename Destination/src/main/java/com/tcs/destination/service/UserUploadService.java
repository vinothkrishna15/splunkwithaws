package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.CellModel;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserGeneralSettingsT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.IOURepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserGeneralSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.DestinationUserDefaultObjectsHelper;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FieldValidator;
import com.tcs.destination.utils.StringUtils;

/**
 * This service helps in uploading opportunities to database
 * 
 * @author bnpp
 *
 */
@Service
public class UserUploadService {

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    UserGeneralSettingsRepository userGenSettingsRepository;
    
    @Autowired
    UserNotificationSettingsRepository userNotificationSettingsRepository;

    @Autowired
    UserAccessPrivilegesRepository userAccessPrivilegesRepository;
    
    @Autowired
    TimezoneMappingRepository timeZoneRepository;

    @Autowired
	GeographyRepository geographyRepository;

    @Autowired
	SubSpRepository subspRepository;

    @Autowired
	IOURepository iouRepository;

    @Autowired
	CustomerRepository custRepository;

    private static final Logger logger = LoggerFactory
	    .getLogger(UserUploadService.class);
    
    public static final String VALIDATOR_SHEET_NAME="Validate";

	private static final int USER_MASTER_COLUMN_COUNT = 12;
	

	private String validateAndRectifyValue(String value) {
		String val = value;
		//System.out.println(value.substring(value.length() - 2, value.length()));
		if (!StringUtils.isEmpty(value)) {
			if (value.substring(value.length() - 2, value.length()).equals(".0")) {
				val = value.substring(0, value.length() - 2);
			}
		}
		return val;
	}
	
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
    	Workbook workbook = ExcelUtils.getWorkBook(multipartFile);
    	// Validates the spreadsheet for errors after validating the excel sheet
    		uploadStatus = null;
    		if (validateSheet(workbook)) {
    			uploadStatus = new UploadStatusDTO();
        		uploadStatus.setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());
    			
        		List<UploadServiceErrorDetailsDTO> sheetErrors = uploadStatus.getListOfErrors();
        		
        		//reading user master table
    			Sheet userMasterSheet = workbook.getSheetAt(2);	
    			Iterator<Row> userMasterIterator = userMasterSheet.iterator();
    			int lastValidRow = userMasterSheet.getPhysicalNumberOfRows();
    			int processedCount = 1;
    			
    			if(userMasterIterator.hasNext()){
    				userMasterIterator.next();
    			}
    			while(processedCount < lastValidRow && userMasterIterator.hasNext()){
    				saveUser(workbook, sheetErrors, userMasterIterator, processedCount);
    				processedCount++;
    			}
    			
    			uploadStatus.setListOfErrors(sheetErrors);
    			if(sheetErrors!=null && !sheetErrors.isEmpty()){
    				uploadStatus.setStatusFlag(false);
    			}
    			
    		}
    		return uploadStatus;
    }

    @Transactional
	private void saveUser(Workbook workbook,
			List<UploadServiceErrorDetailsDTO> sheetErrors,
			Iterator<Row> userMasterIterator, int processedCount) {
		Row userRow = userMasterIterator.next();
		logger.info("Row : " + (userRow.getRowNum()+1));
		List<CellModel> CellModelList = new ArrayList<CellModel>();
		String action = userRow.getCell(0).getStringCellValue();
		if(action.equalsIgnoreCase(Constants.ACTION_ADD)){		
			CellModel userIdCellModel = validateEmptyStrAndLength(userRow,FieldValidator.UserT_userId,true,"userId");
			CellModelList.add(userIdCellModel);
			CellModel userNameCellModel = validateEmptyStrAndLength(userRow,FieldValidator.UserT_userName,true,"");
			CellModelList.add(userNameCellModel);
			CellModel tempPasswordCellModel = validateEmptyStrAndLength(userRow,FieldValidator.UserT_tempPassword,true,"");
			CellModelList.add(tempPasswordCellModel);
			CellModel baseLocationCellModel = validateEmptyStrAndLength(userRow,FieldValidator.UserT_baseLocation,true,"");
			CellModelList.add(baseLocationCellModel);
			CellModel supervisorIdCellModel = validateEmptyStrAndLength(userRow,FieldValidator.UserT_supervisorUserId,false,"");
			CellModelList.add(supervisorIdCellModel);
			CellModel supervisorNameCellModel = validateEmptyStrAndLength(userRow,FieldValidator.UserT_supervisorUserName,false,"");
			CellModelList.add(supervisorNameCellModel);
			CellModel userTelephoneCellModel = validateEmptyStrAndLength(userRow, FieldValidator.UserT_userTelephone, false, "");
			CellModelList.add(userTelephoneCellModel);
			CellModel userEmailIdCellModel = validateEmptyStrAndLength(userRow, FieldValidator.UserT_userEmailId, false, "");
			CellModelList.add(userEmailIdCellModel);
			CellModel userRoleCellModel = validateEmptyStrAndLength(userRow,FieldValidator.UserT_userRole,true,"userRole");
			CellModelList.add(userRoleCellModel);
			CellModel userGroupCellModel = validateEmptyStrAndLength(userRow,FieldValidator.UserT_userGroup,true,"userGroup");
			CellModelList.add(userGroupCellModel);
			CellModel userGenTimeZoneCellModel = validateEmptyStrAndLength(userRow,FieldValidator.User_General_SettingsT_timeZoneDesc,false,"timeZoneDesc");
			CellModelList.add(userGenTimeZoneCellModel);

			if(isRowHasErrors(CellModelList)){
				populateErrorListForRow(userRow,sheetErrors,CellModelList);
			} else {
				try {
					UserT user = new UserT();
					user.setUserId(validateAndRectifyValue(userIdCellModel.getCellValue()));
					user.setUserName(userNameCellModel.getCellValue());
					user.setTempPassword(tempPasswordCellModel.getCellValue());
					user.setBaseLocation(baseLocationCellModel.getCellValue());
					user.setUserEmailId(userEmailIdCellModel.getCellValue());
					user.setUserTelephone(userTelephoneCellModel.getCellValue());
					user.setUserRole(userRoleCellModel.getCellValue());
					user.setUserGroup(userGroupCellModel.getCellValue());
					user.setSupervisorUserId(validateAndRectifyValue(supervisorIdCellModel.getCellValue()));
					user.setSupervisorUserName(supervisorNameCellModel.getCellValue());
					userRepository.save(user);
					logger.info(userNameCellModel.getCellValue());
					logger.info(userGroupCellModel.getCellValue());
					logger.info("User : saved");

					//saving user general settings for the user
					UserGeneralSettingsT userGenSettings = DestinationUserDefaultObjectsHelper.getDefaultSettings
							(validateAndRectifyValue(userIdCellModel.getCellValue()),
									userGenTimeZoneCellModel.getCellValue());
					userGenSettingsRepository.save(userGenSettings);
					logger.info("User General Settings : saved");

					// saving user notification settings for the user
					List<UserNotificationSettingsT> userNotificationSettingsList = DestinationUserDefaultObjectsHelper.getUserNotificationSettingsList(user);
					userNotificationSettingsRepository.save(userNotificationSettingsList);
					logger.info("User Notification Settings : saved");

					// saving privileges for the user
					populateAndSavePrivileges(workbook, user, sheetErrors);


				} catch(Exception e) {
					logger.error("Error Processing the user upload " + e.getMessage());
					UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
					errorDTO.setSheetName(userRow.getSheet().getSheetName());
					errorDTO.setRowNumber(userRow.getRowNum()+1);
					errorDTO.setMessage(e.getMessage());
					sheetErrors.add(errorDTO);
					//throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
				}
			}
		}
		
	}
	
	

	private void populateAndSavePrivileges(Workbook workbook, UserT user, List<UploadServiceErrorDetailsDTO> sheetErrors) {
		Sheet userPrivilegesSheet = workbook.getSheetAt(3);	
		
		Row userPrivilegeRow = getPrivilegeRow(user,userPrivilegesSheet);
		List<CellModel> privilegeModelList = new ArrayList<CellModel>();
		if(userPrivilegeRow == null) {
			logger.info("No Access privileges defined");
		} else {
			CellModel parentTypeCellModel = validateEmptyStrAndLength(userPrivilegeRow,FieldValidator.User_PrivilegesT_ParentType,true,"parentPrivilegeType");
			privilegeModelList.add(parentTypeCellModel);

			CellModel parentValueCellModel = validateEmptyStrAndLengthForPrivilegeValue(userPrivilegeRow,FieldValidator.User_PrivilegesT_ParentValues,true);
			populateInvalidPrivileges(privilegeModelList,parentValueCellModel,parentTypeCellModel);

			CellModel childTypeCell = validateEmptyStrAndLength(userPrivilegeRow,FieldValidator.User_PrivilegesT_ChildType,false,"childPrivilegeType");
			privilegeModelList.add(childTypeCell);

			CellModel childValueCellModel = validateEmptyStrAndLengthForPrivilegeValue(userPrivilegeRow,FieldValidator.User_PrivilegesT_ChildValues,false);
			populateInvalidPrivileges(privilegeModelList,childValueCellModel,childTypeCell);

			if(isRowHasErrors(privilegeModelList)){
				populateErrorListForRow(userPrivilegeRow,sheetErrors,privilegeModelList);
			} else {
				List<String> parentValuesList = parentValueCellModel.getCellValues();
				for(String parentPrivilege : parentValuesList){
					UserAccessPrivilegesT parentAccessPrivilege = new UserAccessPrivilegesT();
					parentAccessPrivilege.setPrivilegeType(parentTypeCellModel.getCellValue());
                    parentAccessPrivilege.setPrivilegeValue(parentPrivilege);
                    parentAccessPrivilege.setUserId(user.getUserId());
                    parentAccessPrivilege.setIsactive(Constants.Y);
                    parentAccessPrivilege = userAccessPrivilegesRepository.save(parentAccessPrivilege);
                    Integer parentAccessPrivilegeId = parentAccessPrivilege.getPrivilegeId();
                    logger.info("Parent Privilege saved : Id - " + parentAccessPrivilegeId + ", " + parentTypeCellModel.getCellValue() + " - "+ parentPrivilege);
                    List<String> childValuesList = childValueCellModel.getCellValues();
                    for(String childPrivilege : childValuesList){
                    	UserAccessPrivilegesT childAccessPrivilege = new UserAccessPrivilegesT();
                    	childAccessPrivilege.setPrivilegeType(childTypeCell.getCellValue());
                    	childAccessPrivilege.setPrivilegeValue(childPrivilege);
                    	childAccessPrivilege.setParentPrivilegeId(parentAccessPrivilegeId);
                    	childAccessPrivilege.setUserId(user.getUserId());
                    	childAccessPrivilege.setIsactive(Constants.Y);
                    	childAccessPrivilege = userAccessPrivilegesRepository.save(childAccessPrivilege);
                    	Integer childAccessPrivilegeId = childAccessPrivilege.getPrivilegeId();
                    	logger.info("Child Privilege saved : Id - " + childAccessPrivilegeId + ", parent Id - " + parentAccessPrivilegeId + ", "+ childTypeCell.getCellValue() + " - "+ childPrivilege);
                    }
				}
				
				
			}


		}
		
	}

	private void populateInvalidPrivileges(List<CellModel> privilegeModelList,
			CellModel cellModel, CellModel parentTypeCellModel) {
		List<String> privilegeValues = null;
		if(cellModel.getErrors()!=null && !cellModel.getErrors().isEmpty()){
			privilegeModelList.add(cellModel);
		} else {
			privilegeValues = cellModel.getCellValues();
			List<String> invalidPrivilegeValueErrors = getErrorList(privilegeValues,parentTypeCellModel.getCellValue());
			if(cellModel.getErrors()!=null){
				cellModel.getErrors().addAll(invalidPrivilegeValueErrors);
			} else {
				cellModel.setErrors(invalidPrivilegeValueErrors);
			}
			privilegeModelList.add(cellModel);
		}
	}

	private List<String> getErrorList(List<String> privilegeValues, String privilegeType) {
		
		List<String> errors = new ArrayList<String>();
		
		for( String privilegeValue : privilegeValues){
		
		switch (PrivilegeType.valueOf(privilegeType)){
		case GEOGRAPHY : 
			GeographyMappingT geography = geographyRepository.findOne(privilegeValue);
			if( geography==null ) {
				logger.error("Privilege Value :Geography is invalid");
				errors.add(" Invalid Entry : " + privilegeValue );
				//throw new DestinationException(HttpStatus.BAD_REQUEST,"Privilege Value : Geography is invalid");
			}
			break;

		case SUBSP :
			List<SubSpMappingT> subSp = subspRepository.findByDisplaySubSp(privilegeValue);
			if(subSp == null || subSp.isEmpty()) {
				logger.error("Privilege Value :displaysubsp is invalid");
				errors.add(" Invalid Entry : " + privilegeValue );
				//throw new DestinationException(HttpStatus.BAD_REQUEST,"Privilege Value : DisplaySubsp is invalid");
			}
			break;

		case IOU :     
			List<IouCustomerMappingT> iou = iouRepository.findByDisplayIou(privilegeValue);
			if(iou == null || iou.isEmpty()) {
				logger.error("Privilege Value :displayiou is invalid");
				errors.add(" Invalid Entry : " + privilegeValue );
				//throw new DestinationException(HttpStatus.BAD_REQUEST,"Privilege Value : DisplayIOU is invalid");
			}
			break;

		case CUSTOMER : 
			if(!privilegeValue.equals(Constants.GLOBAL)){
				CustomerMasterT customer = custRepository.findByCustomerName(privilegeValue);
				if(customer == null){
					logger.error("Customers not found for the customer name: {}",privilegeValue);
					errors.add(" Invalid Entry : " + privilegeValue );
					//throw new DestinationException(HttpStatus.BAD_REQUEST,
						//	"Customers not found for the customer name: " + privilegeValue);
				}
			}
//			} else {
//				logger.error("Wrong Privilege Value {}",privilegeValue);
//				errors.add(" Invalid Entry : " + privilegeValue );
//				//throw new DestinationException(HttpStatus.BAD_REQUEST,
//				//		"Wrong Privilege Value: " + privilegeValue 
//				//		+ ", replace with " + Constants.GLOBAL);
//			}

			break;

		case GROUP_CUSTOMER :
			List<CustomerMasterT> customerList = custRepository
			.findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeOrderByGroupCustomerNameAsc(privilegeValue, Constants.UNKNOWN_CUSTOMER);

			if (customerList == null && customerList.isEmpty()) {
				logger.error("Customers not found for the group customer name: {}",
						privilegeValue);
				errors.add(" Invalid Entry : " + privilegeValue );
				//throw new DestinationException(
				//		HttpStatus.BAD_REQUEST,
					//	"Customers not found for the group customer name: "
					//			+ privilegeValue);

			}
			break;
		}
		
		}
		
		return errors;
	}

	private List<String> getValuesList(String value) {
		List<String> values = new ArrayList<String>();
		if(!StringUtils.isEmpty(value)){
			values = Arrays.asList(value.split(", "));
			for( String pValue : values){
				pValue.trim();
			}
		}
		return values;
	}

	private Row getPrivilegeRow(UserT user, Sheet userPrivilegesSheet) {
		Iterator<Row> userPrivilegesIterator = userPrivilegesSheet.iterator();
		int lastPrivilegeValidRow = userPrivilegesSheet.getPhysicalNumberOfRows();
		int privilegeProcessedCount = 1;
		
		if(userPrivilegesIterator.hasNext()){
			userPrivilegesIterator.next();
		}
		while(privilegeProcessedCount < lastPrivilegeValidRow && userPrivilegesIterator.hasNext()){
			Row privilegeRow = userPrivilegesIterator.next();
			Cell cell = privilegeRow.getCell(FieldValidator.FIELD_INDEX_MAP.get(FieldValidator.User_PrivilegesT_UserId));
			String excelStr = validateAndRectifyValue(getIndividualCellValue(cell));
			if(excelStr.equalsIgnoreCase(user.getUserId())){
				logger.info("privilege found");
				return privilegeRow;
			}
		}
		
		return null;
	}

	private void populateErrorListForRow(Row userRow,
			List<UploadServiceErrorDetailsDTO> sheetErrors,
			List<CellModel> cellModelList) {
		
		for(CellModel cellModel: cellModelList){
			List<String> errors = cellModel.getErrors();
			if(errors!=null && !errors.isEmpty()){
				for(String error : errors){
					UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
					errorDTO.setSheetName(userRow.getSheet().getSheetName());
					errorDTO.setRowNumber(userRow.getRowNum()+1);
					errorDTO.setMessage(error);
					printErrorToConsole(errorDTO);
					sheetErrors.add(errorDTO);
				}
			}
		}
		
	}

	private void printErrorToConsole(UploadServiceErrorDetailsDTO errorDTO) {
		String msg = errorDTO.getMessage();
		//System.out.println(msg);
		logger.error(msg);
	}

	private boolean isRowHasErrors(List<CellModel> cellModelList) {
		for(CellModel cellModel : cellModelList){
		 if(cellModel.getErrors()!=null && !cellModel.getErrors().isEmpty()){
			 return true;
		 }
		}
		return false;
	}

	private CellModel validateEmptyStrAndLength(Row userRow,String fieldName,boolean isMandatory,String checkDBField) {
		Cell cell = userRow.getCell(FieldValidator.FIELD_INDEX_MAP.get(fieldName));
		String excelStr = getIndividualCellValue(cell);
		List<String> errorList = FieldValidator.validate(fieldName, excelStr, isMandatory);
		if(isMandatory){
			if(checkDBField.equalsIgnoreCase("userId")){
				if(isUserExists(excelStr)){
					if(errorList==null){
						errorList = new ArrayList<String>();
					}
					errorList.add("UserId already exists");
				}
			} else if(checkDBField.equalsIgnoreCase("userRole")){
				if(!UserRole.contains(excelStr)){
					errorList.add("Invalid UserRole");
				}
			} else if(checkDBField.equalsIgnoreCase("userGroup")){
				if(!UserGroup.contains(excelStr)){
					errorList.add("Invalid UserGroup");
				}
			} else if(checkDBField.equalsIgnoreCase("timeZoneDesc")){
				if(!isValidTimeZone(excelStr)){
					errorList.add("Invalid time zone");
				}
			} else if(checkDBField.equalsIgnoreCase("parentPrivilegeType")){
				if(!PrivilegeType.contains(excelStr)){
					errorList.add("Invalid Parent Privilege Type");
				}
			} else if(checkDBField.equalsIgnoreCase("childPrivilegeType")){
				if(!PrivilegeType.contains(excelStr)){
					errorList.add("Invalid Child Privilege Type");
				}
			}
		}
		
		CellModel cellModel = new CellModel();
		cellModel.setErrors(errorList);
		if(errorList.isEmpty()){
			cellModel.setCellValue(excelStr);
		}
		
		return cellModel;
	}
	
	
	private CellModel validateEmptyStrAndLengthForPrivilegeValue(Row userRow,String fieldName,boolean isMandatory) {
		Cell cell = userRow.getCell(FieldValidator.FIELD_INDEX_MAP.get(fieldName));
		String excelStr = getIndividualCellValue(cell);
		List<String> errors = new ArrayList<String>();
		if(StringUtils.isEmpty(excelStr) && isMandatory){
			errors.add("Value is empty");
		}
		CellModel cellModel = new CellModel();
		List<String> values = getValuesList(excelStr);
		
		for(String value : values){
			if(FieldValidator.isExceedsLength(fieldName, value)){
				errors.add("Value : " + value + " exceeds length(100)");
			}
		}
		
		cellModel.setErrors(errors);
		if(errors.isEmpty()){
			cellModel.setCellValue(excelStr);
			cellModel.setCellValues(values);
		}
		
		return cellModel;
	}

	private boolean isValidTimeZone(String excelStr) {
		return timeZoneRepository.findOne(excelStr) != null;
	}

	private boolean isUserExists(String userIdExcelStr) {
		return userRepository.findOne(userIdExcelStr) != null;
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
		val = String.valueOf(cell.getStringCellValue().trim());
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
     * This method checks the spreadsheet's validate tab for any validation
     * related errors
     * 
     * @param workbook
     * @return boolean
     * @throws Exception
     */
    private boolean validateSheet(Workbook workbook) throws Exception {
    	return ExcelUtils.isValidWorkbook(workbook, VALIDATOR_SHEET_NAME, 4, 1)
    			|| ExcelUtils.isValidWorkbook(workbook,VALIDATOR_SHEET_NAME, 4, 2);
    }

}
