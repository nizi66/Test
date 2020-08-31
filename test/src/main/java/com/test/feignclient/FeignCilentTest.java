package com.test.feignclient;

        import org.springframework.cloud.openfeign.FeignClient;
        import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "clientsdk")
public interface FeignCilentTest {
    @GetMapping("/server")
     void server();
}
