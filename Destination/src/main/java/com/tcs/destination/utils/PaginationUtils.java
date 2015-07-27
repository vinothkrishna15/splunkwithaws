package com.tcs.destination.utils;

/**
 * This class performs Pagination
 * 
 * @author bnpp
 *
 */
public class PaginationUtils {
	
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

}
