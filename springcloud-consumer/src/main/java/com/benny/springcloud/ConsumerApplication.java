/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.benny.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * description ProviderApplication
 *
 * @author ChenYuJia
 * @date 2022/4/26 14:08
 * @since 1.0
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class ConsumerApplication {

	/**
	 * 校验：http://106.12.174.147:8848/nacos/v1/ns/catalog/instances?serviceName=springcloud-provider&clusterName=DEFAULT&pageSize=10&pageNo=1&namespaceId=
	 *
	 * @author ChenYuJia
	 * @date 2022/4/26 14:08
	 * @param 
	 * @return 
	 */
	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

}
