package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

/**
 * POJO model for smart search result response
 * @author TCS
 *
 */
public class SearchResultResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<SearchResultDTO> results;

	public List<SearchResultDTO> getResults() {
		return results;
	}

	public void setResults(List<SearchResultDTO> results) {
		this.results = results;
	}
	
}
