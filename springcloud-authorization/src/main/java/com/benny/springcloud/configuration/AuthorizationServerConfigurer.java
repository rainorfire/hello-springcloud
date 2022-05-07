package com.benny.springcloud.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/7 18:14
 * @since 1.0
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

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
        super.configure(security);
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
//        clients.jdbc().
        super.configure(clients);
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
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        super.configure(endpoints);
    }
}
