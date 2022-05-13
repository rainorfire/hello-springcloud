package com.benny.springcloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/7 18:09
 * @since 1.0
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan({"com.benny.springcloud.mapper"})
public class AuthorizationApplication implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationApplication.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AuthorizationApplication.applicationContext = applicationContext;
    }

    public static final ApplicationContext getApplicationContext() {
        return applicationContext;
    }


}
