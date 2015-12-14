package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;

/**
 * This controller deals with the images related functionalities
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/images")
public class ImagesDBController {

	private static final Logger logger = LoggerFactory
			.getLogger(GeographyController.class);

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	ContactRepository contactRepository;

	/**
	 * This method inserts the image for particular entity
	 * 
	 * @param entityId
	 * @param entityType
	 * @param file
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody String sampleInsert(
			@RequestParam(value = "entityId") String entityId,
			@RequestParam(value = "entityType") String entityType,
			@RequestParam("file") MultipartFile file)
			throws DestinationException {
		logger.info("Inside ImagesDB Controller: start of insert");

		try {
			byte[] imageBytes = file.getBytes();

			switch (EntityType.valueOf(entityType)) {
			case CUSTOMER:
				customerRepository.addImage(imageBytes, entityId);
				break;
			case PARTNER:
				partnerRepository.addImage(imageBytes, entityId);
				break;
			case CONTACT:
				contactRepository.addImage(imageBytes, entityId);
				break;
			case DOCUMENT:
				userRepository.addImage(imageBytes, entityId);
				break;

			default:

			}
			logger.info("Inside ImagesDB Controller: End of insert");
			return "";
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while uploading image");
		}
	}

	/**
	 * This method is used to get the photo for the particular entity
	 * 
	 * @param entityId
	 * @param entityType
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET, produces = "image/jpg")
	public ResponseEntity<byte[]> testphoto(
			@RequestParam(value = "id") String entityId,
			@RequestParam(value = "type") String entityType)
			throws DestinationException {
		logger.info("Inside ImagesDB Controller");
		try {
			byte[] img1 = new byte[0];
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);

			switch (EntityType.valueOf(entityType)) {
			case CUSTOMER:
				CustomerMasterT customer = customerRepository.findOne(entityId);
				img1 = customer.getLogo();
				break;
			case PARTNER:
				PartnerMasterT partner = partnerRepository.findOne(entityId);
				img1 = partner.getLogo();
				break;
			case CONTACT:
				ContactT contact = contactRepository.findOne(entityId);
				img1 = contact.getContactPhoto();
				break;
			case DOCUMENT:
				UserT user = userRepository.findOne(entityId);
				img1 = user.getUserPhoto();
				break;

			default:

			}

			logger.info("Exiting ImagesDB Controller");
			return new ResponseEntity<byte[]>(img1, headers, HttpStatus.CREATED);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving image");
		}
	}
}
