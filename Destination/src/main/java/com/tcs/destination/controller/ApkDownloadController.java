package com.tcs.destination.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.tcs.destination.exception.DestinationException;


@RestController
@RequestMapping("/destinationapk")
public class ApkDownloadController {

	private static final Logger logger = LoggerFactory
			.getLogger(ApkDownloadController.class);

	
@RequestMapping(method = RequestMethod.GET, produces="application/apk")
public @ResponseBody ResponseEntity<InputStreamResource> download()  {
   
	
	File file = null;
    logger.info("inside the ApkDownloadController");
   try{
	  
	
	file= new File("/home/168975/sandbox/apk/destination_sit.apk");        

    InputStreamResource isResource = new InputStreamResource(new FileInputStream(file));
    FileSystemResource fileSystemResource = new FileSystemResource(file);
    String fileName = FilenameUtils.getName(file.getAbsolutePath());
    fileName=new String(fileName.getBytes("UTF-8"));
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("application/vnd.android.package-archive"));
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Pragma", "no-cache");
    headers.add("Expires", "0");
    headers.setContentLength(fileSystemResource.contentLength());  
    headers.setContentDispositionFormData("attachment", "destination_sit.apk");
    logger.info("send the apk response to UI");
    
//    return ResponseEntity
//            .ok()
//            .headers(headers)
//            .contentLength(fileSystemResource.contentLength())
//            
//            .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
//            .body(new InputStreamResource(fileSystemResource.getInputStream()));
    return new ResponseEntity<InputStreamResource>(isResource, headers, HttpStatus.OK);

}
   catch(FileNotFoundException e){
	   logger.error(e.getMessage());
           throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"APK file is not present");       
   }
   catch(Exception e)
   {
	   logger.error(e.getMessage());
           throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Oops! Error in downloading the apk");
   }

}
}