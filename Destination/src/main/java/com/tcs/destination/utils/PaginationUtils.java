package com.tcs.destination.utils;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class performs Pagination
 * 
 * @author bnpp
 *
 */
public class PaginationUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(PaginationUtils.class);
	
	public static int getEndIndex(int page, int count, int listSize) {
		int endIndex = listSize;
		if (page == 0) {
			endIndex = count - 1;
		} else {
			endIndex = (page + 1) * count - 1;
		}
		if (listSize <= endIndex) {
			endIndex = listSize - 1;
		}
		return endIndex;
	}

	public static int getStartIndex(int page, int count, int listSize) {
		int startIndex = 0;
		if (page == 0) {
			startIndex = 0;
		} else {
			startIndex = page * count;
		}
		return startIndex;
	}

	public static boolean isValidPagination(int page, int count, int listSize) {
		boolean isValid = false;
		int numAllowedPages = 0;
		int numPages = listSize / count;
		int numLastPageRecords = listSize % count;
		if (numLastPageRecords == 0) {
			numAllowedPages = numPages;
		} else {
			numAllowedPages = numPages + 1;
		}
		if (page < numAllowedPages) {
			isValid = true;
		}
		return isValid;
	}
	
	
	/**
	 * trim the list to accommodate to pagination
	 * @param page - page number
	 * @param count - number records/page
	 * @param list 
	 * @return
	 */
	public static <T> List<T> paginateList(int page, int count,
			List<T> list) {
		List<T> trimlist = null;
		if (CollectionUtils.isNotEmpty(list) && PaginationUtils.isValidPagination(page, count, list.size())) {
			int size = list.size();
			int fromIndex = PaginationUtils.getStartIndex(page, count, size);
			int toIndex = PaginationUtils.getEndIndex(page, count, size) + 1;
			 trimlist = list.subList(fromIndex, toIndex);
			logger.debug("PaginationUtils after pagination size is {}", size);
		}
		return trimlist;
	}

}
