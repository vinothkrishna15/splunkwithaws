/**
 * 
 */
package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author tcs2
 *
 */
public class QualifiedPipelineDetails<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param qualifiedPipelineDTO
	 */
	public QualifiedPipelineDetails(
			List<QualifiedPipelineDTO> qualifiedPipelineDTOValue) {
		super();
		this.qualifiedPipelineDTOValue = qualifiedPipelineDTOValue;
	}

	/**
	 * 
	 */
	public QualifiedPipelineDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	List<QualifiedPipelineDTO> qualifiedPipelineDTOValue;

	/**
	 * @return the qualifiedPipelineDTO
	 */
	public List<QualifiedPipelineDTO> getQualifiedPipelineDTO() {
		return qualifiedPipelineDTOValue;
	}

	/**
	 * @param qualifiedPipelineDTO
	 *            the qualifiedPipelineDTO to set
	 */
	public void setQualifiedPipelineDTO(
			List<QualifiedPipelineDTO> qualifiedPipelineDTOValue) {
		this.qualifiedPipelineDTOValue = qualifiedPipelineDTOValue;
	}

}
