package com.benny.springcloud.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/11 9:07
 * @since 1.0
 */
@Data
public class LoginParam {

    /**
     * 登录方式：1 用户名密码，2 短信验证码 3 微信授权登录
     */
    private Integer loginType;

    @JsonProperty("username")
    private String userName;

    /**
     * 用户密码，适用于登录方式1
     */
    @JsonProperty("password")
    private String passWord;

    /**
     * 验证码：适用于2
     */
    private String verificationCode;
}
