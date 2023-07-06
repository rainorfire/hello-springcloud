package com.benny.springcloud.security.filter;

import com.benny.springcloud.AuthorizationApplication;
import com.benny.springcloud.security.configuration.bean.UserDetailsBean;
import com.benny.springcloud.security.jwt.JwtUtil;
import com.benny.springcloud.util.CookieUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/11 18:03
 * @since 1.0
 */
@Data
@Component
public class LoginAuthFilter extends OncePerRequestFilter {

    private static final String LOGIN_PAGE_URL = "http://localhost:8852/login";

//    @Resource
//    private TokenEndpoint tokenEndpoint;
//
//    @Resource
//    private AuthorizationEndpoint authorizationEndpoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

//        String uuid = "";
//        final Cookie[] cookies = request.getCookies();
//        if(cookies != null && cookies.length > 0) {
//            for(Cookie c : cookies) {
//                if("uuid".equals(c.getName())) {
//                    uuid = c.getValue();
//                    break;
//                }
//            }
//        }
//
        String authorization = request.getHeader("al-token");
        if(StringUtils.isBlank(authorization)) {
            final Cookie authorizationCookie = CookieUtil.getCookieByName(request, "al-token");
            if(authorizationCookie != null && StringUtils.isNotBlank(authorizationCookie.getValue()) ) {
                authorization = authorizationCookie.getValue();
            }
        }


        if (StringUtils.isNotBlank(authorization)) {
            final UserDetailsBean userDetailsBean = JwtUtil.verifyToken(authorization);
            final ApplicationContext applicationContext = AuthorizationApplication.getApplicationContext();
            final UserDetailsService userDetailsService = applicationContext.getBean(UserDetailsService.class);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(userDetailsBean.getUsername());
            if(userDetails != null) {
                final Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(new User(userDetails.getUsername(), "", authorities), null,
                        authorities);
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }

        } else {
//            response.sendRedirect(LOGIN_PAGE_URL);
//            return;
        }
//
        filterChain.doFilter(request, response);
    }
}
