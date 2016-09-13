package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.ProductContactLinkT;
import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.data.repository.ProductContactLinkTRepository;
import com.tcs.destination.data.repository.ProductRepository;

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
	 * This method validates the product contact data from the sheet and returns
	 * the corresponding error
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
		StringBuffer errorMsg = new StringBuffer();

		// Contact Category
		contact.setContactCategory("PRODUCT");

		// Contact Type
		contact.setContactType("EXTERNAL");

		// CreatedBy
		contact.setCreatedBy(userId);

		// ModifiedBy
		contact.setModifiedBy(userId);

		// Product Names
		String productName = data[3];
		if (StringUtils.isNotEmpty(productName.trim())) {
			ProductMasterT productMasterT = productRepository
					.findByProductName(productName.trim());
			if (productMasterT != null) {
				List<ProductContactLinkT> productContactLinkTs = constructProductContactLinkT(
						productMasterT, userId, null);
				contact.setProductContactLinkTs(productContactLinkTs);

			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Invalid product name; ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Product name is mandatory; ");
		}

		// Contact Name
		if (data[4] != null) {
			String contactName = data[4].trim();
			if (StringUtils.isNotEmpty(contactName)) {
				contact.setContactName(contactName);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Contact name is mandatory; ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact name is mandatory; ");
		}

		// Contact Role
		if (data[5] != null) {
			String contactRole = data[5].trim();
			if (StringUtils.isNotEmpty(contactRole)) {
				String role = validateContactRole(contactRole);
				if (StringUtils.isNotEmpty(role)) {
					contact.setContactRole(role);
				} else {
					contact.setContactRole("Other");
					contact.setOtherRole(contactRole);
				}

			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Contact role is mandatory; ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact role is mandatory; ");
		}
		// Contact Email id
		if (data[6] != null) {
			String contactEmailId = data[6].trim();
			if (StringUtils.isNotEmpty(contactEmailId)) {
				contact.setContactEmailId(contactEmailId);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Contact email id is mandatory; ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact email id is mandatory; ");
		}

		// Contact Telephone
		if (data[7] != null) {
			String contactTelephone = data[7].trim();
			if (StringUtils.isNotEmpty(contactTelephone)) {
				contact.setContactTelephone(contactTelephone);
			}
		}

		// Contact LinkedIn profile
		if (data[8] != null) {
			String contactLinkedInProfile = data[8].trim();
			if (StringUtils.isNotEmpty(contactLinkedInProfile)) {
				contact.setContactLinkedinProfile(contactLinkedInProfile);
			}
		}

		// ACTIVE
		if (data[9] != null) {
			String active = data[9].trim();
			if (StringUtils.isNotEmpty(active)) {
				if (active.equalsIgnoreCase("false")) {
					contact.setActive(false);
				}
			}
		}
		String errorMessage = errorMsg.toString();
		if(StringUtils.isNotEmpty(errorMessage)) {
			error.setMessage(errorMessage);
		}

		return error;
	}

	public UploadServiceErrorDetailsDTO validateProductContactDataUpdate(
			String[] data, String userId, ContactT contactMaster) throws Exception {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		ContactT contact = new ContactT();
		StringBuffer errorMsg = new StringBuffer();

		listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository
				.findAll();

		mapOfProductMasterT = getNameAndIdFromProductMasterT();

		// CONTACT_ID
		
		if (StringUtils.isNotEmpty(data[2])) {
			String contactId = data[2].trim();
			if (StringUtils.isNotEmpty(contactId)) {
				ContactT contactT = contactRepository
						.findByContactId(contactId);
				if (contactT == null) {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append("Invalid Contact Id; ");
				}
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Contact id is mandatory; ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact id is mandatory; ");
		}

		// Contact Category
		contact.setContactCategory("PRODUCT");

		// Contact Type
		contact.setContactType("EXTERNAL");

		// CreatedBy
		contact.setCreatedBy(userId);

		// ModifiedBy
		contact.setModifiedBy(userId);

		// PRODUCT NAME
		String productName = data[3];
		if (StringUtils.isNotEmpty(productName)) {

			if (mapOfProductT == null) {
				mapOfProductT = getProductMasterT();
			}
			productName = productName.trim();
			// List<String> productNamesFromExcel = new ArrayList<String>();
			// productNamesFromExcel.addAll(Arrays.asList(productName.split(",")));
			ProductMasterT productMasterT = mapOfProductT.get(productName);
			if (productMasterT != null) {
				List<ProductContactLinkT> productContactLinkTs = contactMaster
						.getProductContactLinkTs();
				productContactLinkTs = CollectionUtils
						.isNotEmpty(productContactLinkTs) ? productContactLinkTs
						: new ArrayList<ProductContactLinkT>();
				List<ProductContactLinkT> existingProductContactLinkTs = productContactLinkTRepository
						.findByProductIdAndContactId(
								productMasterT.getProductId(),
								contactMaster.getContactId());
				if (CollectionUtils.isEmpty(existingProductContactLinkTs)) {
					ProductContactLinkT productContactLinkT = constructProductContactLinkUpdate(
							userId, contactMaster, productMasterT);
					productContactLinkTs.add(productContactLinkT);
					contact.setProductContactLinkTs(productContactLinkTs);
				}
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Invalid Product Name; ");
			}

		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Product Name is mandatory; ");
		}

		// Contact Name
		if (data[4] != null) {
			String contactName = data[4].trim();
			if (StringUtils.isNotEmpty(contactName)) {
				contact.setContactName(contactName);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Contact Name is mandatory; ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact Name is mandatory; ");
		}

			// Contact Role
				if (data[5] != null) {
					String contactRole = data[5].trim();
					if (StringUtils.isNotEmpty(contactRole)) {
						String role = validateContactRole(contactRole);
						if (StringUtils.isNotEmpty(role)) {
							contact.setContactRole(role);
						} else {
							contact.setContactRole("Other");
							contact.setOtherRole(contactRole);
						}

					} else {
						error.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorMsg.append("Contact role is mandatory; ");
					}
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append("Contact role is mandatory; ");
				}

		// Contact Email id
		if (data[6] != null) {
			String contactEmailId = data[6].trim();
			if (StringUtils.isNotEmpty(contactEmailId)) {
				contact.setContactEmailId(contactEmailId);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Contact email id is mandatory; ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact email id is mandatory; ");
		}
		// Contact Telephone
		if (data[7] != null) {
			String contactTelephone = data[7].trim();
			if (StringUtils.isNotEmpty(contactTelephone)) {
				contact.setContactTelephone(contactTelephone);
			}
		}

		// Contact LinkedIn profile
		if (data[8] != null) {
			String contactLinkedInProfile = data[8].trim();
			if (StringUtils.isNotEmpty(contactLinkedInProfile)) {
				contact.setContactLinkedinProfile(contactLinkedInProfile);
			}
		}

		// ACTIVE
		if (data[9] != null) {
			String active = data[9].trim();
			if (StringUtils.isNotEmpty(active)) {
				if (active.equalsIgnoreCase("false")) {
					contact.setActive(false);
				}
			}
		}
		
		
		String errorMessage = errorMsg.toString();
		if(StringUtils.isNotEmpty(errorMessage)) {
			error.setMessage(errorMessage);
		} else {
			setContactFields(contactMaster, contact);
		}
   
		return error;

	}

	private void setContactFields(ContactT contactMaster, ContactT contact) {

		contactMaster.setContactCategory(contact.getContactCategory());

		contactMaster.setContactType(contact.getContactType());

		contactMaster.setCreatedBy(contact.getCreatedBy());

		contactMaster.setModifiedBy(contact.getModifiedBy());

		contactMaster.setProductContactLinkTs(contact.getProductContactLinkTs());

		contactMaster.setContactName(contact.getContactName());

		String contactRole = contact.getContactRole();
		if(StringUtils.isNotEmpty(contactRole)) {
			contactMaster.setContactRole(contactRole);
			
			String otherRole = contact.getOtherRole();
			if(StringUtils.isNotEmpty(otherRole)) {
				contactMaster.setOtherRole(otherRole);
			}
		}

		

		String contactTelephone = contact.getContactTelephone();
		
		if(StringUtils.isNotEmpty(contactTelephone)) {
			contactMaster.setContactTelephone(contactTelephone);
		}
		

		String contactLinkedinProfile = contact.getContactLinkedinProfile();
		
		if(StringUtils.isNotEmpty(contactLinkedinProfile)) {
			contactMaster.setContactLinkedinProfile(contactLinkedinProfile);
		}
		
     contactMaster.setActive(contact.isActive());
		
		
	}

	public UploadServiceErrorDetailsDTO validateContactId(String[] data,
			String userId, ContactT contact) {
		// TODO Auto-generated method stub
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String contactId = data[2];

		if (StringUtils.isEmpty(contactId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Contact Id is mandatory; ");
		} else {
			ContactT contactT = contactRepository.findByContactId(contactId);
			if (contactT.getContactId() == null) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Contact Id; ");
			} else {
				// ACTIVE
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
	private String validateContactRole(String contactRole) {
		for (ContactRoleMappingT role : listOfContactRole) {
			if (contactRole.equalsIgnoreCase(role.getContactRole())) {
				return role.getContactRole();
			}
		}
		return null;
	}

	/**
	 * This method is used to retrieve product details and assign it to map
	 * 
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> getNameAndIdFromProductMasterT()
			throws Exception {
		Map<String, String> mapOfProducts = new HashMap<String, String>();
		List<ProductMasterT> listOfProductMasterT = (List<ProductMasterT>) productRepository
				.findAll();
		for (ProductMasterT product : listOfProductMasterT) {
			mapOfProducts.put(product.getProductName(), product.getProductId());
		}
		return mapOfProducts;
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
			ProductMasterT productMaster, String userId, String contactId) {
		List<ProductContactLinkT> listOfProductContactLinkT = productContactLinkTRepository
				.findByProductId(productMaster.getProductId());
		listOfProductContactLinkT = listOfProductContactLinkT != null ? listOfProductContactLinkT
				: new ArrayList<ProductContactLinkT>();

		ProductContactLinkT pclt = new ProductContactLinkT();
		pclt.setCreatedBy(userId);
		pclt.setModifiedBy(userId);
		pclt.setProductId(productMaster.getProductId());
		if (contactId != null) {
			pclt.setContactId(contactId);
		}
		if (listOfProductContactLinkT != null) {
			listOfProductContactLinkT.add(pclt);
		}

		return listOfProductContactLinkT;
	}

	/**
	 * Method to return product details as map
	 * 
	 * @return Map<String, ProductMasterT>
	 */
	public Map<String, ProductMasterT> getProductMasterT() {
		List<ProductMasterT> listOfProductMasterT = null;
		listOfProductMasterT = productRepository.findByActiveTrue();
		Map<String, ProductMasterT> productMap = new HashMap<String, ProductMasterT>();
		for (ProductMasterT productT : listOfProductMasterT) {
			productMap.put(productT.getProductName(), productT);
		}
		return productMap;
	}

	private ProductContactLinkT constructProductContactLinkUpdate(
			String userId, ContactT contact, ProductMasterT productMaster) {

		ProductContactLinkT pclt = new ProductContactLinkT();
		pclt.setProductMasterT(productMaster);
		pclt.setProductId(productMaster.getProductId());
		pclt.setContactId(contact.getContactId());
		pclt.setCreatedBy(userId);
		pclt.setModifiedBy(userId);

		return pclt;

	}

}
