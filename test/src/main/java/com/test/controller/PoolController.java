package com.test.controller;

import com.test.vo.Refuse;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Validated
@RestController
public class PoolController
{
    @GetMapping("test")
    public void wrong(){
        JedisPool jedisPool = new JedisPool("127.0.0.1", 6379);
        try(Jedis jedis = jedisPool.getResource()){
            new Thread(() -> {
                for (int i = 0; i < 1000; i++) {
                    String result = jedis.get("a");
                    log.info(result);
                    if (!result.equals("1")) {
                        log.info("Expect a to be 1 but found {}", result);
                        return;
                    }
                }
            }).start();
        }

        try(Jedis jedis = jedisPool.getResource()){
            new Thread(() -> {
                for (int i = 0; i < 1000; i++) {
                    String result = jedis.get("a");
                    log.info(result);
                    if (!result.equals("1")) {
                        log.info("Expect a to be 1 but found {}", result);
                        return;
                    }
                }
            }).start();
        }

//        new Thread(() -> {
//            for (int i = 0; i < 1000; i++) {
//                String result = jedis.get("b");
//                if (!result.equals("2")) {
//                    log.warn("Expect b to be 2 but found {}", result);
//                    return;
//                }
//            }
//        }).start();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


//    @PostConstruct
//    public void init() {
//        JedisPool jedisPool = new JedisPool("127.0.0.1", 6379);
//        try(Jedis jedis = jedisPool.getResource()){
//            Assert.isTrue("OK".equals(jedis.set("a", "1")), "set a = 1 return OK");
//            Assert.isTrue("OK".equals(jedis.set("b", "2")), "set b = 2 return OK");
//        }
//    }


    public static final void main(String[] args) {
        try {
            Map<String,String> refuseDate=new HashMap<String, String>();
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();
            KieSession kSession = kContainer.newKieSession("session-base");
            Refuse refuse=new Refuse();
            refuse.setAge(34);
            kSession.setGlobal("refuseDate",refuseDate);
            kSession.insert(refuse);
            int count=kSession.fireAllRules();
            System.out.println("规则执行条数：--------"+count);
            System.out.println("规则执行完成--------"+refuse.toString());
            System.out.println(kSession.getGlobals().toString());
        } catch (Throwable t) {
            t.printStackTrace();
        }
}}
