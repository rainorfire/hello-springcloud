package com.benny.springcloud.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/4/28 18:54
 * @since 1.0
 */
@FeignClient(value = "springcloud-gateway" , fallback = FallBackEchoApi.class, path = "/echo-api")
public interface EchoApi {

    @GetMapping("/echo/{string}")
    String echo(@PathVariable String string);


}

class FallBackEchoApi implements EchoApi {

    @Override
    public String echo(String string) {
        return "Fall Back!";
    }
}


