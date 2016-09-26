package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryRgsT;

@Repository
public interface DeliveryRgsTRepository extends JpaRepository<DeliveryRgsT, String>{
     
	
	@Query(value = "SELECT d.delivery_rgs_id FROM delivery_rgs_t d "
			+ "WHERE UPPER(d.delivery_rgs_id) LIKE UPPER(:idLike) "
			+ "ORDER BY d.delivery_rgs_id ASC "
			+ "LIMIT :limitNum", nativeQuery = true)
	List<String> findByRgsIdPattern(@Param("idLike") String idLike, @Param("limitNum") int limitNum);

}
