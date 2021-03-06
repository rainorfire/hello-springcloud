package com.benny.springcloud.security.configuration;

import com.benny.springcloud.security.configuration.bean.ClientDetailsBean;
import com.benny.springcloud.mapper.OauthClientDetailsMapper;
import com.benny.springcloud.model.OauthClientDetails;
import com.benny.springcloud.model.OauthClientDetailsExample;
import com.benny.springcloud.security.entrypoint.CustomAuthenticationEntryPoint;
import com.benny.springcloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.StringUtils;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.annotation.Resource;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/7 18:14
 * @since 1.0
 */
@Order(7)
@Slf4j
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private OauthClientDetailsMapper oauthClientDetailsMapper;

    @Resource
    private JwtTokenEnhancer jwtTokenEnhancer;

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @author ChenYuJia
     * @date 2022/5/7 18:16
     * @param 
     * @return 
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//        super.configure(security);
        // allowFormAuthenticationForClients: ????????????/oauth/token??????client_id??????client_secret???????????????
        security.allowFormAuthenticationForClients().passwordEncoder(passwordEncoder).tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()");
        security.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @author ChenYuJia
     * @date 2022/5/7 18:16
     * @param 
     * @return 
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(getClientDetailsService());
    }

    /**
     * ??????????????????????????????token???????????????token??????????????????????????????????????????token?????????token?????????????????????
     *
     * @author ChenYuJia
     * @date 2022/5/7 18:16
     * @param 
     * @return 
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
            //?????????????????????????????????
            .userDetailsService(userDetailsService)
            .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);

        // ?????????token????????????
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtTokenEnhancer,accessTokenConverter()));
        endpoints.tokenEnhancer(tokenEnhancerChain);

        endpoints.pathMapping("oauth/confirm_access", "/oauth/custom-confirm-access");
    }

    @Bean
    public ClientDetailsService getClientDetailsService() {
        return clientId -> {
            OauthClientDetailsExample example = new OauthClientDetailsExample();
            example.createCriteria().andClientIdEqualTo(clientId);
            final List<OauthClientDetails> list = oauthClientDetailsMapper.selectByExample(example);
            if(CollectionUtils.isEmpty(list)) {
                throw new NoSuchClientException("No client with requested id: " + clientId);
            }
            final OauthClientDetails oauthClientDetails = list.get(0);
            ClientDetailsBean details =
                new ClientDetailsBean(oauthClientDetails.getClientId(), oauthClientDetails.getResourceIds(),
                    oauthClientDetails.getScope(), oauthClientDetails.getAuthorizedGrantTypes(),
                    oauthClientDetails.getAuthorities(), oauthClientDetails.getWebServerRedirectUri());
            details.setClientSecret(oauthClientDetails.getClientSecret());
            if (oauthClientDetails.getAccessTokenValidity() != null) {
                details.setAccessTokenValiditySeconds(oauthClientDetails.getAccessTokenValidity());
            }
            if (oauthClientDetails.getRefreshTokenValidity() != null) {
                details.setRefreshTokenValiditySeconds(oauthClientDetails.getRefreshTokenValidity());
            }
            String json = oauthClientDetails.getAdditionalInformation();
            if (json != null) {
                try {
                    Map<String, Object> additionalInformation = JsonUtil.json2Map(json, String.class, Object.class);
                    details.setAdditionalInformation(additionalInformation);
                }
                catch (Exception e) {
                    log.warn("Could not decode JSON for additional information: " + details, e);
                }
            }
            String scopes = oauthClientDetails.getScope();
            if (scopes != null && "true".equals(oauthClientDetails.getAutoapprove())) {
                details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(scopes));
            }
            return details;
        };
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        final KeyPair keyPair = keyPair();
        jwtAccessTokenConverter.setKeyPair(keyPair);
        try {
            jwtAccessTokenConverter.setVerifier(new RsaVerifier(new RSAPublicKeyImpl(keyPair.getPublic().getEncoded())));
        } catch (InvalidKeyException e) {
            log.error("", e);
        }
//        jwtAccessTokenConverter.setSigner(new RsaSigner(keyPair.getPrivate().toString()));
//        jwtAccessTokenConverter.setJwtClaimsSetVerifier();
        jwtAccessTokenConverter.setSigningKey(new String(keyPair.getPrivate().getEncoded()));

        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        // ???classpath??????????????????????????????
        // keytool -genkey -alias jwt -keyalg RSA -keystore jwt.jks
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}
