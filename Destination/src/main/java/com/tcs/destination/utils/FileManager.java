/**
 * 
 * FileManager.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * This FileManager class provide the functionality for handling files
 * 
 */
public class FileManager {
	
	private static final Logger logger = LoggerFactory.getLogger(FileManager.class);
	
	/**
	 * Method to save file to the file server
	 * @param file
	 * @param path
	 * @throws Exception
	 */
	public static void saveFile(MultipartFile file, String path) throws Exception {
		
		logger.debug("Storing the file:");
		
		File fileSaveDir = new File(path);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        String filePath = path + file.getOriginalFilename(); 
        File dest = new File(filePath);
        try {
			file.transferTo(dest);
		} catch (IllegalStateException | IOException e) {
			logger.error("Error while saving the file" + e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Method to create a new file under the specified path
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static File createFile(String path,
			String fileName) {
		
		logger.debug("Storing the file:");
		
		File fileSaveDir = new File(path);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        String filePath = path + fileName; 
        File dest = new File(filePath);
        
		return dest;
	}
	
	/**
	 * Method to delete the file older than the number of days
	 * @param days
	 * @param rootPath
	 * @throws IOException 
	 */
	public static void purgeOldFile(int days, String filePath) throws IOException {
		
		logger.debug("Inside the purgeOldFile method:");
		
		File file = new File(filePath);
		if (file.isDirectory()) {
			purgeFile(days, file);
		}
	}

	/**
	 * @param days
	 * @param file
	 * @throws IOException 
	 */
	private static void purgeFile(int days, File file) throws IOException {
		
		logger.debug("Inside the purgeFile method:");
		
		for (File f: file.listFiles()) {
			if (f.isDirectory()) { 
				try {
					Date dirDate = DateUtils.getDateFrString(f.getName());
					if (dirDate.before(DateUtils.addDays(new Date(), -days))) {
						FileUtils.cleanDirectory(f);
						f.delete();
					}
				} catch (ParseException e) {
					purgeFile(days, f);
				} 
			}
		}
			
		
	}

	/**
	 * @param path
	 * @param template
	 * @return 
	 */
	public static void copyFile(String destiantionDir, String filePath, String fileName) {
		
		logger.debug("Inside the copyFile method:");
		
		File file = new File (filePath);
		File dir = new File(destiantionDir);
		File destinationFile = null;
		dir.mkdirs();
		
		if (file.exists()) {
			
			destinationFile = new File(destiantionDir + fileName);
			try {
				CopyOption[] options = new CopyOption[]{
					      StandardCopyOption.REPLACE_EXISTING,
					      StandardCopyOption.COPY_ATTRIBUTES
					    }; 
				Files.copy(file.toPath(),  destinationFile.toPath(), options);
			} catch (IOException e) {
				logger.error("Error while copying file: {}", e);
			}
			
		} else {
			logger.error("Source file doesn't exist.");
		}

	}

}
