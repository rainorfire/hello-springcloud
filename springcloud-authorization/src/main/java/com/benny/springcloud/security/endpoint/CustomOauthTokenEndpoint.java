package com.benny.springcloud.security.endpoint;

import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;

import java.security.Principal;

/**
 * description
 *
 * @author Chenyujia
 * @date 2023/7/5 18:34
 * @since 1.0
 */
public class CustomOauthTokenEndpoint extends TokenEndpoint {

    @Override
    protected String getClientId(Principal principal) {
        return super.getClientId(principal);
    }
}
