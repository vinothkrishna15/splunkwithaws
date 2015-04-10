package com.tcs.destination.data.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BdmTargetT;

/**
 * @author bnpp
 *
 */
@Repository
public interface BdmTargetTRepository extends
		CrudRepository<BdmTargetT, String> {

	@Query(value = "select sum(target) from bdm_target_t where bdm_user_id=?1 and year = ?2", nativeQuery = true)
	List<BigDecimal> findSumOfTargetByBdmTargetIdAndYear(String bdmTargetId,
			String financialYear);

}
