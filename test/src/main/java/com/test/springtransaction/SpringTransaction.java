package com.test.springtransaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.*;

@RestController
@Slf4j
public class SpringTransaction {

    @Autowired
    private SpringTransactionMap springTransactionMap;

    @GetMapping("get")
    @Transactional(propagation = NEVER)
    public List get(){
       return springTransactionMap.get();
    }

    @GetMapping("get1")
    @Transactional(propagation = NEVER)
    public List get1(){

        log.info("test1");
        return springTransactionMap.get();
    }

}
