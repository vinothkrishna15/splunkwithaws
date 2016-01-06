package com.tcs.destination.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.exception.DestinationException;

/**
 * this service handles requests for reports upload
 */
@Service
public class ReportsUploadService {
	
	@Autowired
	ConnectRepository connectRepository;

	private static final Logger logger = LoggerFactory
			.getLogger(ReportsUploadService.class);

		public void saveDocument(MultipartFile multipartFile) throws Exception {
			logger.debug("Begin:Inside saveDocument() ReportsUploadService");
			File file = convert(multipartFile);

			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				// Create Workbook instance holding reference to .xlsx file
				Workbook workbook = WorkbookFactory.create(fileInputStream);
				// Get first/desired sheet from the workbook
				Sheet sheet = workbook.getSheetAt(0);
				 int rowStart = sheet.getFirstRowNum();
				 int rowEnd = sheet.getLastRowNum();
				rowStart++;
				 for (int rowNum = rowStart; rowNum < rowEnd; rowNum++){
					 Row row1=sheet.getRow(rowNum);
				 if(row1 != null){
					// For each row, iterate through all the columns
					Iterator<Cell> cellIterator = row1.cellIterator();
					ConnectT connectT=new ConnectT();
					connectT.setConnectId(cellIterator.next().getStringCellValue());
					connectT.setConnectCategory(cellIterator.next().getStringCellValue());
					connectT.setConnectName(cellIterator.next().getStringCellValue());
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				    Date parsedDate = dateFormat.parse(cellIterator.next().getStringCellValue().toString());
				    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
					connectT.setStartDatetimeOfConnect(timestamp);
					Date parseDate = dateFormat.parse(cellIterator.next().getStringCellValue());
					Timestamp endDate = new java.sql.Timestamp(parseDate.getTime());
					connectT.setEndDatetimeOfConnect(endDate);
					String primary=cellIterator.next().getNumericCellValue()+"";
					connectT.setPrimaryOwner(primary.substring(0, 6));
					connectT.setDocumentsAttached(cellIterator.next().getStringCellValue());
					String created=cellIterator.next().getNumericCellValue()+"";
					connectT.setCreatedBy(created.substring(0, 6));
//					connectT.setCreatedDatetime(timestamp);
					connectT.setCountry(cellIterator.next().getStringCellValue());
//					connectT.setPartnerId(cellIterator.next().getStringCellValue());
//					connectT.setCustomerId(cellIterator.next().getStringCellValue().toString());
					connectT.setTimeZone(cellIterator.next().getStringCellValue());
					connectT.setType(cellIterator.next().getStringCellValue());
					connectT.setLocation(cellIterator.next().getStringCellValue());
					String modified=cellIterator.next().getNumericCellValue()+"";
					connectT.setModifiedBy(modified.substring(0, 6));
					Date parsDate = dateFormat.parse(cellIterator.next().getStringCellValue());
					Timestamp modDate = new java.sql.Timestamp(parsDate.getTime());
					connectT.setModifiedDatetime(modDate);
					ConnectT connect=connectRepository.save(connectT);
					if(connect==null){
						throw new DestinationException(HttpStatus.NOT_ACCEPTABLE, "Not Saved");
					}
					}
					fileInputStream.close();
				 	}
				 } catch (Exception e1) {
				e1.printStackTrace();
				}
			logger.debug("End:Inside saveDocument() ReportsUploadService");
		}
		
		public File convert(MultipartFile file) throws Exception {
			File convFile = new File(file.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
			return convFile;
		}	
	}