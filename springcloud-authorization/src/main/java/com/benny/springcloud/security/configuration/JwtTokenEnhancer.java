//package com.benny.springcloud.security.configuration;
//
//import com.benny.springcloud.security.configuration.bean.UserDetailsBean;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.TokenEnhancer;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * description JWT内容增强器
// *
// * @author Chenyujia
// * @date 2022/5/8 0:38
// * @since 1.0
// */
//@Component
//public class JwtTokenEnhancer implements TokenEnhancer {
//
//    @Resource
//    private ClientDetailsService clientDetailsService;
//
//    @Override
//    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
//        UserDetails securityUser = (UserDetails) authentication.getPrincipal();
////        UserDetailsBean securityUser = (UserDetailsBean) authentication.getPrincipal();
//        Map<String, Object> info = new HashMap<>();
//        //把用户ID设置到JWT中
////        info.put("id", securityUser.getId());
//        info.put("userName", securityUser.getUsername());
//        info.put("roleList", securityUser.getAuthorities());
//        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
//
//        final ClientDetails clientDetails =
//            clientDetailsService.loadClientByClientId(authentication.getOAuth2Request().getClientId());
//
//        ((DefaultOAuth2AccessToken)accessToken)
//            .setExpiration(new Date(System.currentTimeMillis() + 1000 * clientDetails.getAccessTokenValiditySeconds()));
//        return accessToken;
//    }
//}
