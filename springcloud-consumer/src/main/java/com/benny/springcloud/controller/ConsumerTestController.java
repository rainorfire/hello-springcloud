package com.benny.springcloud.controller;

import com.benny.springcloud.api.EchoApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/4/26 13:57
 * @since 1.0
 */
@RequestMapping("/consumer")
@RestController
class ConsumerTestController {

    @Resource
    private EchoApi echoApi;

    @GetMapping("/test/{param}")
    public ResponseEntity index(@PathVariable String param) {
        final String echo = echoApi.echo(param);
        return new ResponseEntity(echo, HttpStatus.OK);
    }

}
