package com.benny.springcloud.api;

import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/4/29 10:05
 * @since 1.0
 */
@Component
public class EchoApiFallBack implements EchoApi {

    @Override
    public String echo(String string) {
        return "Fall Back!";
    }
}
