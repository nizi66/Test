package com.test.controller;

import com.test.feignclient.FeignCilentTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@Validated
@RestController
public class FeignAndRibbonController {

    @Autowired
    private FeignCilentTest feignCilentTest;

    @GetMapping("client")
    public void client(){
        feignCilentTest.server();
    }

    @GetMapping("server")
    public void server() throws InterruptedException {
        log.debug("server====");
        TimeUnit.SECONDS.sleep(10);
    }
}
