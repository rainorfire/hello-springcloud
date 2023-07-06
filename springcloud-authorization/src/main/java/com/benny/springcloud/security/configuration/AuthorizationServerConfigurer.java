package com.benny.springcloud.security.configuration;

import com.benny.springcloud.mapper.OauthClientDetailsMapper;
import com.benny.springcloud.model.OauthClientDetails;
import com.benny.springcloud.model.OauthClientDetailsExample;
import com.benny.springcloud.security.configuration.bean.ClientDetailsBean;
import com.benny.springcloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * description 认证服务器 <br/>
 *
 * http://localhost:8852/oauth/authorize?response_type=code&client_id=MY_APP_ID&scope=all&redirect_uri=http://localhost:8852/api/public/auth/callback
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
    private JwtTokenStoreConfig.JwtTokenEnhancer jwtTokenEnhancer;

    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Resource
    private TokenStore tokenStore;

    @Resource
    private ClientDetailsService clientDetailsService;

    /**
     * 方法是在认证服务器配置类中重写的一个方法，用于配置认证服务器的安全性。
     *
     * 在 OAuth 2.0 中，认证服务器除了管理客户端的认证和授权外，还需要保护自身的端点和资源，以确保安全性。
     *
     * AuthorizationServerSecurityConfigurer 对象是用于配置认证服务器安全性的配置器，通过调用该对象的方法，可以指定认证服务器的访问规则、安全策略、密码加密方式等信息。
     *
     * 以下是一些常用的方法：
     * <list>
     *     <li>allowFormAuthenticationForClients()：允许客户端使用表单身份验证进行认证。这使得客户端能够使用 client_id 和 client_secret 直接进行登录认证，而不需要通过请求头或其他方式。</li>
     *     <li>checkTokenAccess(String expression)：指定访问令牌验证端点的访问规则。通过传入表达式，可以限制对令牌验证端点的访问权限，只允许特定的用户或角色访问。</li>
     *     <li>tokenKeyAccess(String expression)：指定访问令牌密钥端点的访问规则。通过传入表达式，可以限制对令牌密钥端点的访问权限，只允许特定的用户或角色访问。</li>
     *     <li>authenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint)：指定认证入口点，用于处理未经身份验证的请求。可以自定义认证入口点，根据需要进行相关处理。</li>
     * </list>
     *
     * 通过调用上述方法，可以根据实际需求配置认证服务器的安全性。这些配置将决定认证服务器的访问规则、密码加密方式和安全策略。
     *
     * 通过调用 security.allowFormAuthenticationForClients() 方法，允许客户端使用表单身份验证进行认证，并调用 security.passwordEncoder(passwordEncoder) 方法，
     * 指定密码加密器为自定义的 PasswordEncoder。
     *
     * 总而言之，configure(AuthorizationServerSecurityConfigurer security) 方法用于配置认证服务器的安全性，包括访问规则、密码加密方式和其他安全策略。
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
        security.allowFormAuthenticationForClients()
            .passwordEncoder(passwordEncoder)
            /**
             * permitAll()：允许所有请求无需认证即可访问端点。
             * isAuthenticated()：要求请求已经通过认证才能访问端点。
             * hasRole("ROLE_NAME")：要求请求具有指定角色才能访问端点。
             * hasAnyRole("ROLE1", "ROLE2")：要求请求具有指定角色中的任意一个才能访问端点。
             * hasAuthority("AUTHORITY_NAME")：要求请求具有指定权限才能访问端点。
             * hasAnyAuthority("AUTHORITY1", "AUTHORITY2")：要求请求具有指定权限中的任意一个才能访问端点。
             * 自定义表达式：可以使用SpEL表达式编写更复杂的条件来控制访问权限。
             */
            .tokenKeyAccess("isAuthenticated()")
            .checkTokenAccess("isAuthenticated()");
//        security.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
    }

    /**
     * 该方法是在认证服务器配置类中重写的一个方法，用于配置客户端的详细信息，包括授权类型、授权范围、秘钥等内容。
     *
     * 在 OAuth 2.0 中，客户端是指需要访问受保护资源的应用程序或服务。每个客户端都需要在认证服务器上进行注册并获取相应的客户端凭证，以便与认证服务器进行交互。
     *
     * ClientDetailsServiceConfigurer 对象是用于配置客户端详细信息的配置器，通过调用该对象的方法，可以指定客户端的授权类型、授权范围、重定向 URI、自动授权等信息。
     *
     * 以下是一些常用的方法：
     * <list>
     *     <li>withClientDetails(ClientDetailsService clientDetailsService)：使用自定义的 ClientDetailsService 来获取客户端详细信息。</li>
     *     <li>inMemory()：使用内存方式配置客户端详细信息。</li>
     *     <li>withClient(String clientId)：指定客户端的唯一标识符。</li>
     *     <li>secret(String clientSecret)：指定客户端的秘钥，用于客户端的身份验证。</li>
     *     <li>authorizedGrantTypes(String... grantTypes)：指定客户端支持的授权类型，例如授权码模式、密码模式、客户端凭证模式等。</li>
     *     <li>scopes(String... scopes)：指定客户端的授权范围。</li>
     *     <li>redirectUris(String... redirectUris)：指定客户端的重定向 URI。</li>
     *     <li>autoApprove(boolean autoApprove)：指定是否自动授权。</li>
     * </list>
     * 通过调用上述方法，可以根据实际需求配置客户端的详细信息。这些配置将决定客户端在与认证服务器交互时的授权行为和权限范围。
     *
     * 通过调用 clients.withClientDetails(getClientDetailsService()) 方法，将自定义的 ClientDetailsService 注入，从数据库或其他持久化存储中获取客户端详细信息。
     *
     * 总而言之，configure(ClientDetailsServiceConfigurer clients) 方法用于配置客户端的详细信息，以便认证服务器能够识别和验证客户端，
     * 并根据配置的授权类型和授权范围进行相应的授权处理。
     *
     * @author ChenYuJia
     * @date 2022/5/7 18:16
     * @param
     * @return
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(getClientDetailsService());
//        clients.inMemory()
//            .withClient("MY_APP_ID")
//            .secret(new BCryptPasswordEncoder().encode("123456"))
//            .scopes("all")
//            // 授权码模式：authorization_code, 密码模式：password, 客户端模式：client_credentials, 简化模式：implicit, 令牌刷新：refresh_token
//            .authorizedGrantTypes("authorization_code", "password", "refresh_token")
//            .resourceIds("AUTHORIZATION_RESOURCES")
//            .redirectUris("http://localhost:8852/api/public/auth/callback")
////            .accessTokenValiditySeconds(3600)
////            .refreshTokenValiditySeconds(86400)
//            // 是否需要授权，设置为true则不需要用户点击确认授权直接返回授权码
//            .autoApprove(false);
    }

    /**
     * 认证服务器端点是指在 OAuth 2.0 认证服务器上暴露的特定 API 端点，用于处理认证和授权相关的请求。
     * 这些端点负责验证客户端、颁发访问令牌、刷新令牌以及验证令牌的有效性等操作。<br/>
     * 以下是一些常见的认证服务器端点及其作用：
     * <list>
     *     <li>/oauth/authorize：授权端点。用于处理用户授权请求，当用户尚未登录或未授权时，将用户重定向到登录页面或显示授权页面以获取用户的许可。</li>
     *     <li>/oauth/token：令牌端点。用于通过授权码、密码、客户端凭证等方式交换访问令牌和刷新令牌。</li>
     *     <li>/oauth/check_token：令牌验证端点。用于验证访问令牌的有效性，客户端可以通过发送令牌到该端点进行验证。</li>
     *     <li>/oauth/token_key：令牌密钥端点。用于公开访问用于签名和验证令牌的公钥。</li>
     *     <li>/oauth/confirm_access：用户授权确认端点。在用户登录并授权后，用于显示给用户以确认所请求的权限范围。</li>
     *     <li>/oauth/error：授权服务错误信息端点。</li>
     * </list>
     *
     * 认证服务器端点的作用是根据 OAuth 2.0 协议规范，实现认证和授权的不同步骤和流程。
     * 通过暴露这些端点，客户端可以与认证服务器进行交互，获取访问令牌、刷新令牌等信息，以实现对受保护资源的访问。
     *
     * 方法配置了认证服务器的端点，通过 endpoints.authenticationManager(authenticationManager) 将身份验证管理器注入认证服务器，以支持密码授权模式。
     * 这样，客户端可以通过 /oauth/token 端点使用用户名和密码来获取访问令牌。
     *
     * 认证服务器端点是实现 OAuth 2.0 认证和授权流程的核心部分，通过合理配置和保护这些端点，确保系统的安全性和可靠性。
     *
     * @author ChenYuJia
     * @date 2022/5/7 18:16
     * @param
     * @return
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
            // 授权模式需要
            .authorizationCodeServices(authorizationCodeServices())
            // 密码模式需要
            .authenticationManager(authenticationManager)
            // 令牌管理 无论哪种模式都需要
            .tokenServices(authorizationServerTokenServices())
//            //配置加载用户信息的服务
            .userDetailsService(userDetailsService)
            .accessTokenConverter(jwtAccessTokenConverter)
            .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);

        // 自定义token生成方式
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtTokenEnhancer, jwtAccessTokenConverter));
        endpoints.tokenEnhancer(tokenEnhancerChain);

//        endpoints.pathMapping("oauth/confirm_access", "/oauth/custom-confirm-access");
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
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    @Bean
    public AuthorizationServerTokenServices authorizationServerTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenEnhancer(jwtTokenEnhancer);
        // refresh token的过期时间
        defaultTokenServices.setRefreshTokenValiditySeconds(3600 * 24);
        // access token的过期时间
        defaultTokenServices.setAccessTokenValiditySeconds(3600 * 3);
        // 支持令牌刷新
        defaultTokenServices.setSupportRefreshToken(true);
//        defaultTokenServices.setReuseRefreshToken(false);
        // 令牌存储服务
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setAuthenticationManager(authenticationManager);
        // 客户端配置策略
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        return defaultTokenServices;
    }

}
