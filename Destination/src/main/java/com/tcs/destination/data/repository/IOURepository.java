package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.SubSpMappingT;

public interface IOURepository extends CrudRepository<IouCustomerMappingT, String> {

    List<IouCustomerMappingT> findByDisplayIou(String displayIOU);

}
