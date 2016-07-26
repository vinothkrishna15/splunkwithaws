package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.ProductContactLinkT;
import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.ProductContactLinkTRepository;
import com.tcs.destination.data.repository.ProductRepository;
import com.tcs.destination.utils.StringUtils;

/**
 * This helper class deals with validating the data from the sheet
 * 
 * @author bnpp
 *
 */
@Component("productContactUploadHelper")
public class ProductContactUploadHelper {

	@Autowired
	ContactRoleMappingTRepository contactRoleMappingTRepository;

	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	ProductRepository productRepository;

	private List<ContactRoleMappingT> listOfContactRole = null;

	private Map<String, String> mapOfProductMasterT = null;
	private Map<String, ProductMasterT> mapOfProductT = null;
	
	@Autowired
	ProductContactLinkTRepository productContactLinkTRepository;


	/**
	 * This method validates the product contact data from the sheet and
	 * returns the corresponding error
	 * 
	 * @param data
	 * @param userId
	 * @param contact
	 * @return
	 * @throws Exception
	 */
	public UploadServiceErrorDetailsDTO validateProductContactData(
			String[] data, String userId, ContactT contact) throws Exception {
		// TODO Auto-generated method stub
		listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository
				.findAll();

		mapOfProductMasterT = getNameAndIdFromProductMasterT();

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		// Contact Category
		contact.setContactCategory("PRODUCT");
		
		//Contact Type
		contact.setContactType("EXTERNAL");
		
		// CreatedBy
		contact.setCreatedBy(userId);
		
		//ModifiedBy
		contact.setModifiedBy(userId);

		// Product Names
		String productName = data[3];
		String contactId=null;
		if (!StringUtils.isEmpty(productName)) {
			String[] productNameList = productName.split(",");
			List<String> productIds = retrieveProductIdFromName(productNameList);
			if ((productIds != null) && (!productIds.isEmpty())) {
				List<ProductContactLinkT> productContactLinkTs = constructProductContactLinkT(
						productIds, userId,contactId);
				contact.setProductContactLinkTs(productContactLinkTs);
				
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Product Name");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Product Name is mandatory ");
		}
		
		// Contact Name
		String contactName = data[4];
				if (!StringUtils.isEmpty(contactName)) {
					contact.setContactName(contactName);
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Contact Name is mandatory ");
				}


		// Contact Role
        String contactRole = data[5];
		if (!StringUtils.isEmpty(contactRole)) {
			if (validateContactRole(contactRole)) {
			contact.setContactRole(contactRole);
			}
			else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Contact role ");
			}
			
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Contact Role is mandatory ");
		}

		// Contact Email id
		String contactEmailId = data[6];
		if (!StringUtils.isEmpty(contactEmailId)) {
			contact.setContactEmailId(contactEmailId);
		}

		// Contact Telephone
		String contactTelephone = data[7];
		if((contactTelephone!=null)&&(!StringUtils.isEmpty(contactTelephone)))
		{
		Long telephoneNumber=Double.valueOf(contactTelephone).longValue();
		if (telephoneNumber!=null) {
			contact.setContactTelephone(telephoneNumber.toString());
		}
		}
		// Contact LinkedIn profile
		String contactLinkedInProfile = data[8];
		if (!StringUtils.isEmpty(contactLinkedInProfile)) {
			contact.setContactLinkedinProfile(contactLinkedInProfile);
		}
		
		//ACTIVE
		String active=data[9];
		boolean activeFlag=false;
		if (!StringUtils.isEmpty(active)) {
		 if(active.equalsIgnoreCase("true"))
		 {
			activeFlag=true;
			contact.setActive(activeFlag);
			
		 }
		 else
		 {
			 contact.setActive(activeFlag);
		 }
		}
		return error;
	}
	
	public UploadServiceErrorDetailsDTO validateProductContactDataUpdate(
			String[] data, String userId, ContactT contact) throws Exception {

		        // TODO Auto-generated method stub
				UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
				
				listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository.findAll();
				
				mapOfProductMasterT = getNameAndIdFromProductMasterT();
				
			    //CONTACT_ID
		        String contactId=data[2];
			
				if (!StringUtils.isEmpty(contactId)) 
				{
                 ContactT contactT = contactRepository.findByContactId(contactId);
				    if(contactT==null)
				   {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Invalid Contact Id");
				   }
				}
				else
				{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Contact id is mandatory");
				}
				
				if(contact.isActive())
				{
	
				// Contact Category
				contact.setContactCategory("PRODUCT");
				
				//Contact Type
				contact.setContactType("EXTERNAL");

				// CreatedBy
				contact.setCreatedBy(userId);
				
				//ModifiedBy
				contact.setModifiedBy(userId);

				// PRODUCT NAMES
				String productName = data[3];
				if (!StringUtils.isEmpty(productName)) {
					if (mapOfProductT == null) {
						mapOfProductT = getProductMasterT();
					}
					List<ProductContactLinkT> deleteList = new ArrayList<ProductContactLinkT>();
					List<ProductContactLinkT> updateList = new ArrayList<ProductContactLinkT>();
					List<String> productNamesFromExcel = new ArrayList<String>();
					productNamesFromExcel.addAll(Arrays.asList(productName.split(",")));
					List<ProductContactLinkT> productContactLinkTs = contact.getProductContactLinkTs();
					for (ProductContactLinkT productContactLinkT : productContactLinkTs) {
						if(productContactLinkT.getProductMasterT().isActive())
						{
						 if (!productNamesFromExcel.contains(productContactLinkT.getProductMasterT().getProductName())) {
							deleteList.add(productContactLinkT);
						 } else {
							updateList.add(productContactLinkT);
							productNamesFromExcel.remove(productContactLinkT.getProductMasterT().getProductName());
						 }
						}
					}
					if (!productNamesFromExcel.isEmpty()) {
						for (String prodName : productNamesFromExcel) {

							if (mapOfProductT.containsKey(prodName)) {
								ProductContactLinkT productContactLinkT = constructProductContactLinkUpdate(prodName, userId, mapOfProductT,contact);
								updateList.add(productContactLinkT);
							} else {
								error.setRowNumber(Integer.parseInt(data[0]) + 1);
								error.setMessage("Invalid Product Name ");
							}
						}
					}

					productContactLinkTRepository.delete(deleteList);
					contact.setProductContactLinkTs(updateList);
				}
				
				// Contact Name
				String contactName = data[4];
						if (!StringUtils.isEmpty(contactName)) {
							contact.setContactName(contactName);
						} else {
							error.setRowNumber(Integer.parseInt(data[0]) + 1);
							error.setMessage("Contact Name is mandatory ");
						}


				// Contact Role
		        String contactRole = data[5];
				if (!StringUtils.isEmpty(contactRole)) {
					if (validateContactRole(contactRole)) {
					contact.setContactRole(contactRole);
					}
					else {
						error.setRowNumber(Integer.parseInt(data[0]) + 1);
						error.setMessage("Invalid Contact role ");
					}
					
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Contact Role is mandatory ");
				}

				// Contact Email id
				String contactEmailId = data[6];
				if (!StringUtils.isEmpty(contactEmailId)) {
					contact.setContactEmailId(contactEmailId);
				}

				// Contact Telephone
				String contactTelephone = data[7];
				if((contactTelephone!=null)&&(!StringUtils.isEmpty(contactTelephone)))
				{
				Long telephoneNumber=Double.valueOf(contactTelephone).longValue();
				if (telephoneNumber!=null) {
					contact.setContactTelephone(telephoneNumber.toString());
				}
				}
				// Contact LinkedIn profile
				String contactLinkedInProfile = data[8];
				if (!StringUtils.isEmpty(contactLinkedInProfile)) {
					contact.setContactLinkedinProfile(contactLinkedInProfile);
				}
				
				//ACTIVE
				String active=data[9];
				boolean activeFlag=false;
				if (!StringUtils.isEmpty(active)) {
				 if(active.equalsIgnoreCase("true"))
				 {
					activeFlag=true;
					contact.setActive(activeFlag);
					
				 }
				 else
				 {
					 contact.setActive(activeFlag);
				 }
				}
			}
				else
				{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Contact is not active to be updated.");
				}
				return error;
	
	}
	
	public UploadServiceErrorDetailsDTO validateContactId(String[] data,String userId,
			ContactT contact) {
		// TODO Auto-generated method stub
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String contactId = data[2];

		if (StringUtils.isEmpty(contactId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Contact Id is mandatory ");
		} else {
			ContactT contactT = contactRepository.findByContactId(contactId);
			if (contactT.getContactId() == null) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Contact Id ");
			}
			else
			{
				//ACTIVE
				contact.setActive(false);
				
			}
		}

		return error;
	}


	/**
	 * This method is used to validate the contact role by checking whether the
	 * role is available in the list of contact roles
	 * 
	 * @param contactRole
	 * @return
	 */
	private boolean validateContactRole(String contactRole) {
		boolean flag = false;
		for (ContactRoleMappingT role : listOfContactRole) {
			if (contactRole.equalsIgnoreCase(role.getContactRole())) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * This method is used to retrieve product details and assign it to map
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> getNameAndIdFromProductMasterT()
			throws Exception {
		Map<String, String> mapOfProducts = new HashMap<String, String>();
		List<ProductMasterT> listOfProductMasterT = (List<ProductMasterT>) productRepository.findAll();
		for (ProductMasterT product : listOfProductMasterT) {
			mapOfProducts.put(product.getProductName(), product.getProductId());
		}
		return mapOfProducts;
	}

	/**
	 * This method is used to retrieve the list of customer ids for
	 * corresponding list of customer names given
	 * 
	 * @param listOfCustomerName
	 * @return
	 * @throws Exception
	 */
	private List<String> retrieveProductIdFromName(String[] listOfProductName) 
	{
		List<String> listOfProductId = null;
		try
        {
		  if ((listOfProductName != null)) {
			listOfProductId = new ArrayList<String>();
			for (String productName : listOfProductName) {
				String productId = getMapValuesForKey(mapOfProductMasterT,
						productName);
				listOfProductId.add(productId);
			}
		 }
        }
        catch(Exception e)
        {
        	System.out.println("Exception"+e);
        }
		return listOfProductId;
	}

	/**
	 * This method returns the value to which the specified key is mapped
	 * 
	 * @param map
	 * @param key
	 * @return
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
	 * This method is used to get the list of customer contact link for the list
	 * of customer ids
	 * 
	 * @param listOfCustomerId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private List<ProductContactLinkT> constructProductContactLinkT(
			List<String> listOfProductId, String userId,String contactId)
	{
		List<ProductContactLinkT> listOfProductContactLinkT = null;
		if ((listOfProductId != null) && (!listOfProductId.isEmpty())) {
			listOfProductContactLinkT = new ArrayList<ProductContactLinkT>();
			for (String productId : listOfProductId) {
				ProductContactLinkT pclt = new ProductContactLinkT();
				pclt.setCreatedBy(userId);
				pclt.setModifiedBy(userId);
				pclt.setProductId(productId);
				if(contactId!=null)
				{
					pclt.setContactId(contactId);
				}
				if(listOfProductContactLinkT!=null)
				{
				  listOfProductContactLinkT.add(pclt);
				}
			}
		}
		return listOfProductContactLinkT;
	}
	
	/**
	 * Method to return product details as map
	 * @return  Map<String, ProductMasterT>
	 */
	public Map<String, ProductMasterT> getProductMasterT() {
		List<ProductMasterT> listOfProductMasterT = null;
		listOfProductMasterT = (List<ProductMasterT>) productRepository.findAll();
		Map<String, ProductMasterT> productMap = new HashMap<String, ProductMasterT>();
		for (ProductMasterT productT : listOfProductMasterT) {
			productMap.put(productT.getProductName(), productT);
		}
		return productMap;
	}
	

	private ProductContactLinkT constructProductContactLinkUpdate(String productName,
			String userId,  Map<String, ProductMasterT> mapOfProduct,
			ContactT contact) {
		// TODO Auto-generated method stub
		
		ProductContactLinkT pclt = new ProductContactLinkT();

		ProductMasterT productMasterT = getProductMasterTMapValuesForKey(mapOfProduct, productName);
		pclt.setProductMasterT(productMasterT);
		pclt.setProductId(productMasterT.getProductId());
		pclt.setContactId(contact.getContactId());
		pclt.setCreatedBy(userId);
		pclt.setModifiedBy(userId);
		return pclt;
		
	}
	
	private ProductMasterT getProductMasterTMapValuesForKey(
			Map<String, ProductMasterT> mapOfProduct, String key) {
		ProductMasterT product = null;
		if (mapOfProduct.containsKey(key)) {
			product = mapOfProduct.get(key);
		}
		return product;
	}

}
