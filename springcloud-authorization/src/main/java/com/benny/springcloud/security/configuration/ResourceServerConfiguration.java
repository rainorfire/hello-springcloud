package com.benny.springcloud.security.configuration;

import com.benny.springcloud.security.authhandler.LoginSuccessfulHandler;
import com.benny.springcloud.security.endpoint.CustomOauthTokenEndpoint;
import com.benny.springcloud.security.entrypoint.ExceptionEntryPoint;
import com.benny.springcloud.security.filter.LoginAuthFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

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

    @Resource
    private LoginAuthFilter loginAuthFilter;

    /**
     * 该方法是在Spring Security中配置资源服务器的安全性的方法之一。它的具体作用如下：
     *
     * <list>
     *     <li>
     *         配置资源服务器的资源ID：通过resources.resourceId("resource-server-rest-api")方法指定资源服务器的唯一标识（资源ID）。
     *         这个资源ID在OAuth2授权服务器中用于标识受保护的资源，以确保只有具有正确访问权限的客户端可以访问资源服务器。
     *         它在授权服务器中用于定义访问令牌的范围（scope），以及在资源服务器中用于验证访问令牌的有效性。
     *     </li>
     *     <li>
     *         配置访问令牌转换器：通过resources.tokenServices(tokenServices())方法设置访问令牌转换器。
     *         访问令牌转换器用于验证和解析访问令牌，并将令牌转换为认证对象（Authentication）。
     *     </li>
     *     <li>
     *         配置异常处理器：通过resources.authenticationEntryPoint(new CustomAuthenticationEntryPoint())方法设置异常处理器。
     *         异常处理器用于处理访问资源时出现的认证异常，例如访问令牌无效或过期等情况。
     *     </li>
     * </list>
     *  该方法的主要作用是配置资源服务器的安全性，包括验证访问令牌、处理异常等。
     *  通过这个方法，可以自定义资源服务器的安全行为，确保只有合法的、经过授权的客户端可以访问受保护的资源。
     *
     * @author ChenYuJia
     * @date 2023/6/30 11:35
     * @param
     * @return
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("AUTHORIZATION_RESOURCES")
        /*.tokenServices()*/;
    }

    /**
     * 该方法是 Spring Security 提供的一种配置机制，用于定义资源服务器的安全规则和访问控制。
     *
     * 在资源服务器中，configure(HttpSecurity http) 方法用于配置 HTTP 请求的安全性。通过该方法，您可以定义哪些请求路径需要进行身份验证、需要具备哪些权限才能访问，以及其他的安全规则。
     *
     * 以下是该方法的主要作用：
     * <list>
     *     <li>定义访问规则：使用 http.authorizeRequests() 方法配置针对不同请求路径的访问规则。</li>
     *     <li>设置路径权限：使用 .antMatchers() 方法指定需要进行安全保护的路径，并为这些路径设置相应的访问权限。</li>
     *     <li>配置登录页和注销：通过 .formLogin() 方法配置登录页面的路径和表单字段，以及注销的路径。</li>
     *     <li>配置跨站请求伪造（CSRF）保护：使用 .csrf() 方法启用或禁用 CSRF 保护。</li>
     *     <li>配置其他安全规则：您还可以配置其他的安全规则，如设置会话管理、禁用某些安全功能等。</li>
     * </list>
     *
     * @author ChenYuJia
     * @date 2023/6/30 11:23
     * @param
     * @return
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
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
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login/login-process-ss")
                .successHandler(new LoginSuccessfulHandler())
//                .defaultSuccessUrl("/public/login/successful")
            .permitAll()
            .and()
                .logout()
                .logoutUrl("/logout")
                .permitAll()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint((request, response, authException) -> {
                // 未登录时跳转到登录页面
                response.sendRedirect("/login");
            });;

        // 添加自定义过滤器，替换BasicAuthenticationFilter
        http.addFilterBefore(loginAuthFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
