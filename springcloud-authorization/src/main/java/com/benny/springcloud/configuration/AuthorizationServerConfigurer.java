package com.benny.springcloud.configuration;

import com.benny.springcloud.configuration.bean.ClientDetailsBean;
import com.benny.springcloud.mapper.OauthClientDetailsMapper;
import com.benny.springcloud.model.OauthClientDetails;
import com.benny.springcloud.model.OauthClientDetailsExample;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.KeyPair;
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
    //    @Resource
    //    private DataSource dataSource;
    @Resource
    private OauthClientDetailsMapper oauthClientDetailsMapper;

    @Resource
    private JwtTokenEnhancer jwtTokenEnhancer;

    /**
     * 主要针对授权服务器端点的访问策略、认证策略、加密方式等进行配置
     *
     * @author ChenYuJia
     * @date 2022/5/7 18:16
     * @param 
     * @return 
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//        super.configure(security);
        // allowFormAuthenticationForClients: 主要是让/oauth/token支持client_id以及client_secret作登录认证
        security.allowFormAuthenticationForClients().passwordEncoder(passwordEncoder()).tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()");
    }

    /**
     * 主要配置接入的客户端相关信息，如授权类型、授权范围、秘钥等内容
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
     * 主要配置授权服务器的token存储方式、token转换、端点增强、端点自定义、token授权、token生成等进行配置
     *
     * @author ChenYuJia
     * @date 2022/5/7 18:16
     * @param 
     * @return 
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        super.configure(endpoints);
        endpoints.authenticationManager(authenticationManager)
            //配置加载用户信息的服务
            .userDetailsService(userDetailsService)
            .accessTokenConverter(accessTokenConverter())
            .tokenEnhancer(jwtTokenEnhancer)
            .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);

        endpoints.pathMapping("oauth/confirm_access", "/oauth/custom-confirm-access");
    }

    private ClientDetailsService getClientDetailsService() {
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
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        // 从classpath下的证书中获取秘钥对
        // keytool -genkey -alias jwt -keyalg RSA -keystore jwt.jks
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
