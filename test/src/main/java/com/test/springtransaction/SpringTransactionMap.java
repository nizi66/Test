package com.test.springtransaction;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringTransactionMap
{
    List get();
}
