package com.benny.springcloud.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/8 1:50
 * @since 1.0
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/echo/{param}")
    public String loginPage(HttpServletRequest request, @PathVariable("param") String param) throws Exception {
        return param;
    }
}
