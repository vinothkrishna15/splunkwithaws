package com.tcs.destination.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.service.DocumentService;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/document")
public class DocumentController {

	@Autowired
	DocumentService documentService;

	// @RequestMapping(value = "/insert", method = RequestMethod.GET)
	// public @ResponseBody int ajaxConnectSearchById() throws IOException
	// {
	// FileInputStream fileInputStream=null;
	// File file = new
	// File("/Users/BNPP/Documents/my new pgm/Destination/src/test/java/com/tcs/textedit/MY_FUTURE_REF");
	// byte[] bFile = new byte[(int) file.length()];
	// fileInputStream = new FileInputStream(file);
	// fileInputStream.read(bFile);
	// fileInputStream.close();
	// // for (int i = 0; i < bFile.length; i++)
	// // {
	// // System.out.print((char)bFile[i]);
	// // }
	// DocumentRepositoryT docrep=new DocumentRepositoryT();
	// docrep.setDocumentType("retail");
	// docrep.setDocumentName("ABC RET");
	// docrep.setDocumentSearchKeywords("ABC");
	// docrep.setFileReference(bFile);
	// docrep.setEntityType("RETAIL DOC");
	// docrep.setParentEntity("AMERICA");
	// docrep.setParentEntityId("34343");
	// // docrep.setUploadedBy("886301");
	// docrep.setUploadedDatetime(new Timestamp(date.getTime()));
	// return documentService.save(docrep);
	// }

	@RequestMapping(value = "/download/{documentId}", method = RequestMethod.GET)
	public @ResponseBody byte[] download(
			@PathVariable("documentId") String documentId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		return documentService.download(documentId);
	}

	@RequestMapping(value = "/{documentId}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("documentId") String documentId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		DocumentRepositoryT docrep = documentService
				.findByDocumentId(documentId);
		return Constants.filterJsonForFieldAndViews(fields, view, docrep);
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String upload(
			@RequestParam("documentName") String documentName,
			@RequestParam("documentSearchKeywords") String documentSearchKeywords,
			@RequestParam("documentType") String documentType,
			@RequestParam("entityType") String entityType,
			@RequestParam("parentEntity") String parentEntity,
			@RequestParam("parentEntityId") String parentEntityId,
			@RequestParam(value = "commentId", defaultValue = "") String commentId,
			@RequestParam(value = "connectId", defaultValue = "") String connectId,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "opportunityId", defaultValue = "") String opportunityId,
			@RequestParam(value = "partnerId", defaultValue = "") String partnerId,
			@RequestParam(value = "taskId", defaultValue = "") String taskId,
			@RequestParam("uploadedBy") String uploadedBy,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			String docId = documentService.saveDocument(documentName,
					documentSearchKeywords, documentType, entityType,
					parentEntity, parentEntityId, commentId, connectId,
					customerId, opportunityId, partnerId, taskId, uploadedBy,
					file);
			status.setStatus(Status.SUCCESS, "Id : " + docId);
		} catch (Exception e) {
			status.setStatus(Status.FAILED, e.getMessage());
		}

		return Constants.filterJsonForFieldAndViews(fields, view, status);
	}

}