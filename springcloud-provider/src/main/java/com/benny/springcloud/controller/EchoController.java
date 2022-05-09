package com.benny.springcloud.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/4/26 13:57
 * @since 1.0
 */
@RequestMapping("")
@RestController
class EchoController {

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @GetMapping("/")
    public ResponseEntity index(HttpServletRequest request) {
        final String code = request.getParameter("code");
        final String state = request.getParameter("state");
        final String result = "code = " + code + ", state = " + state;
        System.out.println(result);
        return new ResponseEntity("index result = " + result, HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity test() {
        return new ResponseEntity("error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/sleep")
    public String sleep() {
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @GetMapping("/echo/{string}")
    public String echo(@PathVariable String string) {
        return "hello Nacos Discovery " + string;
    }

    @GetMapping("/divide")
    public String divide(@RequestParam Integer a, @RequestParam Integer b) {
        if (b == 0) {
            return String.valueOf(0);
        }
        else {
            return String.valueOf(a / b);
        }
    }

    @GetMapping("/zone")
    public String zone() {
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        return "provider zone " + metadata.get("zone");
    }

}
