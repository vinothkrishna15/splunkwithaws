package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class OpportunitiesSplitDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	List<OpportunityT> wonOpportunitiesDTO;
	
	List<OpportunityT> lostOpportunitiesDTO;
	
	List<OpportunityT> pipelineOpportunitiesDTO;
	
	List<OpportunityT> anticipatingOpportunitiesDTO;
	
	int wonOpportunitiesCount;
	
	int lostOpportunitiesCount;
	
	int pipelineOpportunitiesCount;
	
	int anticipatingOpportunitiesCount;
	
	public int getWonOpportunitiesCount() {
		return this.wonOpportunitiesDTO!=null ? this.wonOpportunitiesDTO.size() : 0;
	}

	public void setWonOpportunitiesCount(int wonOpportunitiesCount) {
		this.wonOpportunitiesCount = wonOpportunitiesCount;
	}

	public int getLostOpportunitiesCount() {
		return this.lostOpportunitiesDTO!=null ? this.lostOpportunitiesDTO.size() : 0;
	}

	public void setLostOpportunitiesCount(int lostOpportunitiesCount) {
		this.lostOpportunitiesCount = lostOpportunitiesCount;
	}

	public int getPipelineOpportunitiesCount() {
		return this.pipelineOpportunitiesDTO!=null ? this.pipelineOpportunitiesDTO.size() : 0;
	}

	public void setPipelineOpportunitiesCount(int pipelineOpportunitiesCount) {
		this.pipelineOpportunitiesCount = pipelineOpportunitiesCount;
	}

	public List<OpportunityT> getWonOpportunitiesDTO() {
		return wonOpportunitiesDTO;
	}

	public void setWonOpportunitiesDTO(List<OpportunityT> wonOpportunitiesDTO) {
		this.wonOpportunitiesDTO = wonOpportunitiesDTO;
	}

	public List<OpportunityT> getLostOpportunitiesDTO() {
		return lostOpportunitiesDTO;
	}

	public void setLostOpportunitiesDTO(List<OpportunityT> lostOpportunitiesDTO) {
		this.lostOpportunitiesDTO = lostOpportunitiesDTO;
	}

	public List<OpportunityT> getPipelineOpportunitiesDTO() {
		return pipelineOpportunitiesDTO;
	}

	public void setPipelineOpportunitiesDTO(
			List<OpportunityT> pipelineOpportunitiesDTO) {
		this.pipelineOpportunitiesDTO = pipelineOpportunitiesDTO;
	}

	public List<OpportunityT> getAnticipatingOpportunitiesDTO() {
		return anticipatingOpportunitiesDTO;
	}

	public void setAnticipatingOpportunitiesDTO(
			List<OpportunityT> anticipatingOpportunitiesDTO) {
		this.anticipatingOpportunitiesDTO = anticipatingOpportunitiesDTO;
	}

	public int getAnticipatingOpportunitiesCount() {
		return this.anticipatingOpportunitiesDTO!=null ? this.anticipatingOpportunitiesDTO.size() : 0;
	}

	public void setAnticipatingOpportunitiesCount(int anticipatingOpportunitiesCount) {
		this.anticipatingOpportunitiesCount = anticipatingOpportunitiesCount;
	}
	
		
}
