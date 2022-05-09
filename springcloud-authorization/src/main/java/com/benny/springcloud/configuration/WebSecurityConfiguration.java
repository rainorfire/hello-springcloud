package com.benny.springcloud.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/8 1:38
 * @since 1.0
 *
 */
@Slf4j
@Order(6)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Resource
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        super.configure(web);
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() //禁用跨站csrf攻击防御，后面的章节会专门讲解
            .formLogin(form -> {
                form.loginPage("/login")//用户未登录时，访问任何资源都转跳到该路径，即登录页面
                    .loginProcessingUrl("/login-process")//登录表单form中action的地址，也就是处理认证请求的路径
                    .usernameParameter("username")///登录表单form中用户名输入框input的name名，不修改的话默认是username
                    .passwordParameter("password")//form中密码输入框input的name名，不修改的话默认是password
                    .defaultSuccessUrl("/login/login-successful")//登录认证成功后默认转跳的路径
                    .failureUrl("/login/login-failure")
                    .permitAll();
            })
            .authorizeRequests()
            .antMatchers("/oauth/**","/login/**","/actuator/**", "/process").permitAll()//不需要通过登录验证就可以被访问的资源路径
//            .antMatchers("/biz1").hasAnyAuthority("biz1")  //前面是资源的访问路径、后面是资源的名称或者叫资源ID
//            .antMatchers("/biz2").hasAnyAuthority("biz2")
//            .antMatchers("/syslog").hasAnyAuthority("syslog")
//            .antMatchers("/sysuser").hasAnyAuthority("sysuser")
            .anyRequest().authenticated();

    }

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return userDetailsService;
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsService;
    }
}
