package com.benny.springcloud.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/8 1:50
 * @since 1.0
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping("")
    public String loginPage(HttpServletRequest request) throws Exception {
        return "login";
    }

//    @PostMapping("/login-process")
//    public ModelAndView processLogin(HttpServletRequest request) throws Exception {
//        return new ModelAndView("login");
//    }

    @GetMapping("/login-successful")
    public ModelAndView success(HttpServletRequest request) throws Exception {
        // 登录成功后用户的认证信息 UserDetails会存在 安全上下文寄存器 SecurityContextHolder 中
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
//        SysUser sysUser = sysUserService.queryByUsername(username);
//        // 脱敏
//        sysUser.setEncodePassword("[PROTECT]");
        return new ModelAndView("successful");
    }

    @GetMapping("/login-failure")
    public ModelAndView failure(HttpServletRequest request) throws Exception {
        return new ModelAndView("failure");
    }
}
