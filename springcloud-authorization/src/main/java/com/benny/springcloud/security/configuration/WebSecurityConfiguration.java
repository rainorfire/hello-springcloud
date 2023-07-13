package com.benny.springcloud.security.configuration;

import com.benny.springcloud.security.authentication.CustomSecurityAuthenticationProvider;
import com.benny.springcloud.security.authhandler.LoginSuccessfulHandler;
import com.benny.springcloud.security.filter.LoginAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

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

    @Resource
    private LoginAuthFilter loginAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义登录认证
     *
     * @author ChenYuJia
     * @date 2023/6/30 15:57
     * @param 
     * @return 
     */
    @Bean
    public CustomSecurityAuthenticationProvider customSecurityAuthentication() {
        CustomSecurityAuthenticationProvider customSecurityAuthentication = new CustomSecurityAuthenticationProvider();
        customSecurityAuthentication.setPasswordEncoder(passwordEncoder());
        customSecurityAuthentication.setUserDetailsService(userDetailsService());
        return customSecurityAuthentication;
    }

    /**
     * 刷新token时自动调用，不能用CustomSecurityAuthenticationProvider替代
     * @return
     */
    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider(){
        PreAuthenticatedAuthenticationProvider paap = new PreAuthenticatedAuthenticationProvider();
        paap.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService()));
        return paap;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // 将自定义登录认证注册统一注册管理
//        return new ProviderManager(Arrays.asList(customSecurityAuthentication()));
        return super.authenticationManagerBean();
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        super.configure(web);
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider()
        auth.authenticationProvider(customSecurityAuthentication());
        // 刷新token需要
        auth.authenticationProvider(preAuthenticatedAuthenticationProvider());
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            // 禁用 BasicAuthenticationFilter
            .httpBasic().disable()
            .csrf().disable()
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and()
            .authorizeRequests()
            .antMatchers("/oauth/**").permitAll()
            .antMatchers("/login/**").permitAll()
            .antMatchers("/logout/**").permitAll()
            .antMatchers("/api/public/**").permitAll()
            .antMatchers("/actuator/**").permitAll()
            //                .antMatchers("/api/private/**").authenticated()
            .anyRequest().authenticated()
            .and()
//                //不要BasicAuthenticationFilter
//                .httpBasic().disable()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login/login-process-ss")
                .successHandler(new LoginSuccessfulHandler())
                .permitAll()
            .and()
                .logout()
                .logoutUrl("/logout")
                .permitAll();

        // 添加自定义过滤器，替换BasicAuthenticationFilter
        http.addFilterBefore(loginAuthFilter, UsernamePasswordAuthenticationFilter.class);
//        http.addFilterAt(loginAuthFilter, BasicAuthenticationFilter.class);

    }

//    @Bean
//    public LoginAuthFilter loginAuthFilter() {
//        final LoginAuthFilter loginAuthFilter = new LoginAuthFilter();
//        return loginAuthFilter;
//    }

//    @Bean
//    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
//        return new CustomAuthenticationFilter(authenticationManagerBean());
//    }

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return userDetailsService;
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsService;
    }
}
