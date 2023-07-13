package com.benny.springcloud.security.configuration;

import com.benny.springcloud.security.jwt.RSAUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * description
 *
 * @author Chenyujia
 * @date 2023/6/30 17:51
 * @since 1.0
 */
@Configuration
public class JwtTokenStoreConfig {

    private Logger log = LoggerFactory.getLogger(JwtTokenStoreConfig.class);

    @Resource
    private ClientDetailsService clientDetailsService;

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//        final KeyPair keyPair = keyPair();
//        jwtAccessTokenConverter.setKeyPair(keyPair);


        RSAUtil rsaUtil = new RSAUtil();
        final PrivateKey privateKey = rsaUtil.loadPemPrivateKey("/rsa/private-key.pem");
        final PublicKey publicKey = rsaUtil.loadPemPublicKey("/rsa/public-key.pem");
        // 创建RSA签名器
        RsaSigner signer = new RsaSigner((RSAPrivateKey)privateKey);

        // 创建RSA验证器
        SignatureVerifier verifier = new RsaVerifier((RSAPublicKey)publicKey);

        // 设置验证器到jwtAccessTokenConverter
        //不设置这个会出现 Cannot convert access token to JSON
        jwtAccessTokenConverter.setVerifier(verifier);

        jwtAccessTokenConverter.setSigner(signer);

        return jwtAccessTokenConverter;
    }

    @Bean
    public JwtTokenEnhancer jwtTokenEnhancer() {
        final JwtTokenEnhancer jwtTokenEnhancer = new JwtTokenEnhancer();
        jwtTokenEnhancer.setClientDetailsService(clientDetailsService);
        return jwtTokenEnhancer;
    }

//    @Bean
//    public KeyPair keyPair() {
//        // 从classpath下的证书中获取秘钥对
//        // keytool -genkey -alias jwt -keyalg RSA -keystore jwt.jks
//        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
//        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
//    }

    @Bean
    public TokenStore tokenStore() {

//        return new InMemoryTokenStore();
        return new JwtTokenStore(accessTokenConverter());
    }

    @Data
    public static class JwtTokenEnhancer implements TokenEnhancer {

        private ClientDetailsService clientDetailsService;

        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            UserDetails securityUser = (UserDetails) authentication.getPrincipal();
            //        UserDetailsBean securityUser = (UserDetailsBean) authentication.getPrincipal();
            Map<String, Object> info = new HashMap<>();
            //把用户ID设置到JWT中
            //        info.put("id", securityUser.getId());
            info.put("userName", securityUser.getUsername());
            info.put("roleList", securityUser.getAuthorities());
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);

            final ClientDetails clientDetails =
                clientDetailsService.loadClientByClientId(authentication.getOAuth2Request().getClientId());

            ((DefaultOAuth2AccessToken)accessToken)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * clientDetails.getAccessTokenValiditySeconds()));
            return accessToken;
        }
    }

}
