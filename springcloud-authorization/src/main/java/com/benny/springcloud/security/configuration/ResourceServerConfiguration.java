package com.benny.springcloud.security.configuration;

import com.benny.springcloud.security.entrypoint.ExceptionEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/8 4:28
 * @since 1.0
 */
@Order(8)
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("1");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers("/oauth/**", "/login/**","/actuator/**", "/login-process")
            .permitAll()
            .anyRequest()
            .authenticated();
        http.exceptionHandling().authenticationEntryPoint(new ExceptionEntryPoint());

    }
}
