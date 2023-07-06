package com.benny.springcloud.controllers;

import com.benny.springcloud.security.configuration.bean.UserDetailsBean;
import com.benny.springcloud.vo.LoginParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/8 1:50
 * @since 1.0
 */
@Controller
@RequestMapping("")
public class LoginController {

    @Resource
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) throws Exception {
        return "login";
    }

    @PostMapping("/login/login-process")
    public String processLogin(HttpServletRequest request, HttpServletResponse response/*, @RequestBody LoginParam loginParam*/) throws Exception {
        final String username = request.getParameter("username");
        final String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        final Authentication authenticate = authenticationManager.authenticate(authRequest);

        if(!authenticate.isAuthenticated()) {
            return "redirect:/login/login-failure";
        }

        SecurityContextHolder.getContext().setAuthentication(authenticate);

        final String callback = request.getParameter("localUrl");
        if(StringUtils.isNotBlank(callback)) {
            return "redirect:" + callback;
        }

        final Cookie uuidCookie = new Cookie("uuid", ((UserDetailsBean) authenticate.getPrincipal()).getUsername().toString());
        uuidCookie.setPath("/");
        uuidCookie.setDomain("localhost");
        response.addCookie(uuidCookie);
        return "redirect:/login/login-successful";
    }

    @GetMapping("/login/login-successful")
    public ModelAndView success(HttpServletRequest request) throws Exception {
        // 登录成功后用户的认证信息 UserDetails会存在 安全上下文寄存器 SecurityContextHolder 中
//        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = principal.getUsername();
//        SysUser sysUser = sysUserService.queryByUsername(username);
//        // 脱敏
//        sysUser.setEncodePassword("[PROTECT]");
        return new ModelAndView("successful");
    }

    @GetMapping("/login/login-failure")
    public ModelAndView failure(HttpServletRequest request) throws Exception {
        return new ModelAndView("failure");
    }
}
