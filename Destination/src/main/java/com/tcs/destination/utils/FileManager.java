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
	 * @param errorPath
	 * @param errorFileName
	 * @return
	 */
	public static File createFile(String errorPath,
			String errorFileName) {
		
		logger.debug("Storing the file:");
		
		File fileSaveDir = new File(errorPath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        String filePath = errorPath + errorFileName; 
        File dest = new File(filePath);
        
		return dest;
	}
	
	/**
	 * Method to delete the file older than the number of days
	 * @param days
	 * @param rootPath
	 */
	public static void purgeOldFile(int days, String rootPath) {
		
		logger.debug("Inside the purge method:");
		
		File root = new File(rootPath);
		
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			for (File file: files) {
				if (file.isDirectory()) {
					File[] subFiles = file.listFiles();
					for (File f: subFiles) {
						if (f.isDirectory()) {
							String temp = null;
							try {
								temp = f.getName();
								Date dirDate = DateUtils.getDateFrString(temp);
								if (dirDate.before(DateUtils.addDays(new Date(), -days))) {
									FileUtils.cleanDirectory(f);
									f.delete();
								}
							} catch (ParseException e) {
								logger.error("Invalid date: {}, exception: {}", temp, e);
							} catch (IOException e) {
								logger.error("Error while deleting files: {}", e);
							}
						}
					}
				}
			}
		}
		
	}

}
