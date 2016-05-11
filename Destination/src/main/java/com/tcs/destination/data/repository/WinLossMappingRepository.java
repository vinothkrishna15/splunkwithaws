package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WinLossFactorMappingT;

@Repository
public interface WinLossMappingRepository extends CrudRepository<WinLossFactorMappingT, String> {

    @Query(value="select win_loss_factor from win_loss_factor_mapping_t", nativeQuery=true)
    List<String> getWinLossFactor();

	List<WinLossFactorMappingT> findByActiveTrue();
	
	WinLossFactorMappingT findByActiveTrueAndWinLossFactor(String winLossFactor);

}
