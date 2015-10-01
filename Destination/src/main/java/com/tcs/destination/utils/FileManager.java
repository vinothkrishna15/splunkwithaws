package com.tcs.destination.utils;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileManager {
	
	private static final Logger logger = LoggerFactory.getLogger(FileManager.class);
	
	public static void saveFile(MultipartFile file, String path) {
		
		logger.debug("Storing the file:");
		
		File fileSaveDir = new File(path);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdir();
        }
        String filePath = path + file.getOriginalFilename(); 
        File dest = new File(filePath);
        try {
			file.transferTo(dest);
		} catch (IllegalStateException | IOException e) {
			logger.error("Error while saving the file" + e.getMessage());
		}
	}

}
