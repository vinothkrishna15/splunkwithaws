package com.tcs.destination.helper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.BeaconDataT;
import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.utils.StringUtils;

@Component("beaconUploadHelper")
public class BeaconUploadHelper {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private BeaconRepository beaconRepository;
	
	@Autowired
	private SubSpRepository subSpRepository;
	
	@Autowired
	private OfferingRepository offeringRepository;
	
	@Autowired
	private TimezoneMappingRepository timeZoneMappingRepository;
	
	@Autowired
	private  UserRepository userRepository;
	
	@Autowired
	private  ContactRepository contactRepository;
	
	@Autowired
	private  ConnectTypeRepository connectTypeRepository;
	
	@Autowired
	private BeaconDataTRepository beaconDataTRepository;
	
	@Autowired
	CommonHelper commonHelper;
	
	Map<String, IouBeaconMappingT> mapOfIouBeaconMappingT = null;
	
	
	public UploadServiceErrorDetailsDTO validateBeaconData(String[] data, String userId, BeaconDataT beaconDataT) throws Exception 
	{
		
			UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
			StringBuffer errorMsg = new StringBuffer();
			
			mapOfIouBeaconMappingT = mapOfIouBeaconMappingT != null ? mapOfIouBeaconMappingT
					: commonHelper.getIouBeaconMappingT();
			
			  List<BeaconCustomerMappingT> beaconCustomerMappingData=null;
			 
			
			  // to find whether beacon_customer_name, beacon_geography, beacon_iou
			  // (composite key) has foreign key existence in
			  // beacon_customer_mapping_t
			 
			  // BEACON CUSTOMER NAME
		        String beaconCustomerName = data[2];
		      // BEACON GEOGRAPHY
				String beaconGeography = data[1];
			  // BEACON_CUSTOMER_IOU - to find whether beacon_iou has foreign key existence in iou_beacon_mapping_t
				String beaconIou = data[4];
				
			 
				if(StringUtils.isEmpty(beaconGeography))
				{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" Beacon Geography Is Mandatory ");
				}
				
				
				if(StringUtils.isEmpty(beaconCustomerName))
				{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" Beacon Customer Name Is Mandatory ");
				}
				
				// BEACON_GROUP_CLIENT - does not have NOT_NULL constraint
				String beaconGroupClient = data[3];
				if(!StringUtils.isEmpty(beaconGroupClient)){
					beaconDataT.setBeaconGroupClient(beaconGroupClient);
				}
				
				
				// BEACON_CUSTOMER_IOU - to find whether beacon_iou has foreign key existence in iou_beacon_mapping_t
				
				if(StringUtils.isEmpty(beaconIou)){
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" Beacon IOU Is Mandatory ");
					}
				
				
				//QUARTER
				String quarter = data[8];
				if(!StringUtils.isEmpty(quarter)){
					beaconDataT.setQuarter(quarter);
				}
				else 
				{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" Quarter Is Mandatory ");
				}
				
				//REVENUE 
				BigDecimal beaconRevenue=null;
				if(!StringUtils.isEmpty(data[10])){
				try{
				  beaconRevenue = new BigDecimal(data[10]);
				  beaconDataT.setTarget(beaconRevenue);
				} catch (Exception e){
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" Invalid beacon revenue ");
				}
				
					
				}
				else 
				{   
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" Beacon Revenue is mandatory ");
				}
				
				//FINANCIAL YEAR
				String financialYear= data[7];
				if(!StringUtils.isEmpty(financialYear)){
					beaconDataT.setFinancialYear(financialYear);
				}
				else 
				{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" Beacon Revenue is mandatory ");
				}
				
				 if(StringUtils.isEmpty(errorMsg.toString())){
				 beaconCustomerMappingData=beaconRepository.findBeaconActive(beaconCustomerName,beaconGeography, beaconIou, true);
				  if ((!beaconCustomerMappingData.isEmpty())
							&& (beaconCustomerMappingData.size() == 1)) 
				  {
				     BeaconCustomerMappingT beaconCustomerMappingT=beaconCustomerMappingData.get(0);
				     Long beaconCustomerMapId=beaconCustomerMappingT.getBeaconCustomerMapId();
				     beaconDataT.setBeaconCustomerMapId(beaconCustomerMapId);
				 
				  } else {
					  error.setRowNumber(Integer.parseInt(data[0]) + 1);
					  errorMsg.append(" The combination of Beacon Customer Name,Beacon Geography And Beacon Iou is not valid/inactive ");

				  }
				 }
				 if(!StringUtils.isEmpty(errorMsg.toString())){
					 error.setMessage(errorMsg.toString());
				 }
				return error;
	}	
}
