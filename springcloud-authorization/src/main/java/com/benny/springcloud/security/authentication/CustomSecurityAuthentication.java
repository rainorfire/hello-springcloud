package com.benny.springcloud.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * description
 *
 * @author Chenyujia
 * @date 2023/6/30 15:54
 * @since 1.0
 */
public class CustomSecurityAuthentication extends DaoAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
        UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // TODO 可以做验证码比对
        //做密码比对
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
