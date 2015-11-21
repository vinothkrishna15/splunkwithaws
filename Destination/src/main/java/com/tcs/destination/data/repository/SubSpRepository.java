package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.SubSpMappingT;

public interface SubSpRepository extends CrudRepository<SubSpMappingT, String> {

    @Query(value="select sub_sp from sub_sp_mapping_t", nativeQuery=true)
    List<String> getSubSp();
    
    SubSpMappingT findBySubSp(String subSp);
    
    List<SubSpMappingT> findByDisplaySubSp(String displaySubSp);
    
    @Query(value="select distinct display_sub_sp from sub_sp_mapping_t",nativeQuery=true)
    List<String> findDistinctDisplaySubsp();

}
