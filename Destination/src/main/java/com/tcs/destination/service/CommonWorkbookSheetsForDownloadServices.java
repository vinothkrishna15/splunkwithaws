package com.tcs.destination.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.data.repository.GeographyCountryRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.UserRepository;

/**
 * This service holds the common sheets for download services of opportunity,
 * connects
 * 
 * @author bnpp
 *
 */
@Service
public class CommonWorkbookSheetsForDownloadServices {

    @Autowired
    CompetitorRepository competitorRepository;
    
    @Autowired
    GeographyCountryRepository geoCountryRepository;
    
    @Autowired
    OfferingRepository offeringRepository;
    
    @Autowired
    UserRepository userRepository;
    
    /**
     * This method populates Competitor Ref sheet in workbook
     * 
     * @param competitorRefSheet
     * @param listOfCompetitors
     * @return Sheet
     */
    public void populateCompetitorRefSheet(Sheet competitorRefSheet) throws Exception{

	// Get competitor names from repository
	List<String> listOfCompetitors = competitorRepository.getCompetitorName();
	
	if(listOfCompetitors!=null){
	
	int rowCount = 1; // Excluding the header, header starts with index 0
	for (String competitor : listOfCompetitors) {

	    // Create row with rowCount
	    Row row = competitorRefSheet.createRow(rowCount);

	    // Create new Cell and set cell value
	    Cell cell = row.createCell(0);
	    cell.setCellValue(competitor.trim());

	    // Increment row counter
	    rowCount++;
	}
	}
    }
    
    /**
     * This method populates Geography Country Ref sheet
     * 
     * @param geoCountrySheet
     * @throws Exception
     */
    public void populateGeographyCountryRef(Sheet geoCountrySheet) throws Exception{
	
	List<Object[]> listOfGeoCountry = geoCountryRepository.getGeographyCountry();

	if(listOfGeoCountry!=null){
	int rowCount = 1; // Excluding the header, header starts with index 0
	for (Object[] gc : listOfGeoCountry) {

	    // Create row with rowCount
	    Row row = geoCountrySheet.createRow(rowCount);

	    // Create new Cell and set cell value
	    Cell cellGeo = row.createCell(0);
	    cellGeo.setCellValue(gc[0].toString().trim());
	    
	    Cell cellCountry = row.createCell(1);
	    cellCountry.setCellValue(gc[1].toString().trim());

	    // Increment row counter
	    rowCount++;
	}
	}
	
    }
    
    /**
     * This method poplates Offering Ref sheet
     * 
     * @param offeringSheet
     * @throws Exception
     */
    public void populateOfferingRefSheet(Sheet offeringSheet) throws Exception{
	
	List<Object[]> listOfOffering = offeringRepository.getSubSpOffering();
	
	if(listOfOffering!=null) {
	int rowCount = 1; // Excluding the header, header starts with index 0
	for(Object[] ob : listOfOffering){
	    // Create row with rowCount
	    Row row = offeringSheet.createRow(rowCount);

	    // Create new Cell and set cell value
	    Cell cellSubSp = row.createCell(0);
	    cellSubSp.setCellValue(ob[0].toString().trim());
	    
	    Cell cellOffering = row.createCell(1);
	    cellOffering.setCellValue(ob[1].toString().trim());
//	    System.out.println(cellSubSp.getStringCellValue()+" "+cellOffering.getStringCellValue());

	    // Increment row counter
	    rowCount++;
	}
	}
	
    }
    
    public void populateUserRefSheet(Sheet userSheet) throws Exception {

	 List<Object[]> listOfUsers = userRepository.getNameAndId();
	
	 if(listOfUsers!=null) { 
	 int rowCount = 1; // Excluding the header, header starts with index 0
	 for(Object[] ob : listOfUsers){
	    // Create row with rowCount
	    Row row = userSheet.createRow(rowCount);

	    // Create new Cell and set cell value
	    Cell cellId = row.createCell(0);
	    cellId.setCellValue(ob[1].toString().trim());
	    
	    Cell cellName = row.createCell(1);
	    cellName.setCellValue(ob[0].toString().trim());

	    // Increment row counter
	    rowCount++;
	 }
	 }
    }

}
