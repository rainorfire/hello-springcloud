package com.benny.springcloud.controllers;

import com.benny.springcloud.util.HttpClientUtil;
import com.benny.springcloud.util.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/8 1:50
 * @since 1.0
 */
@RestController
@RequestMapping("/api")
public class TestController {

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/public/getAuthCode")
    public String getAuthCode(HttpServletRequest request) throws Exception {

        // 构建授权请求的URL
        String authorizationUrl = "http://localhost:8852/oauth/authorize" +
            "?response_type=code" +
            "&client_id=MY_APP_ID" +
            "&redirect_uri=http://localhost:8852/api/public/getAccessCode/callback" +
            "&scope=read";

        final ResponseEntity<String> entity = restTemplate.getForEntity(authorizationUrl, String.class);

        return JsonUtil.object2Json(entity);
    }

    @GetMapping("/public/login/successful")
    public void getAccessCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String alToken = request.getParameter("al-token");
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String redirectUrl = "http://localhost:8852/oauth/authorize?response_type=code&client_id=MY_APP_ID&scope=all&redirect_uri=http://localhost:8852/api/public/auth/callback";
        response.setHeader("al-token", alToken);
        response.sendRedirect(redirectUrl);
//        return "";
    }

    @GetMapping("/public/auth/callback")
    public String testPublic(HttpServletRequest request) throws Exception {

        String authorizationCodeTmp = request.getParameter("code");

        // 使用授权码构建令牌请求的URL
        String tokenUrl = "http://localhost:8852/oauth/token";

        // 构建请求体参数
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("code", authorizationCodeTmp);
        parameters.add("client_id", "MY_APP_ID");
        parameters.add("client_secret","123456"/* new BCryptPasswordEncoder().encode("123456")*/);
        parameters.add("redirect_uri", "http://localhost:8852/api/public/auth/callback");
        parameters.add("scope", "all");

//        final Cookie cookie = CookieUtil.getCookieByName(request, "al-token");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + (cookie != null ? cookie.getValue() : ""));
//        HttpEntity<MultiValueMap<String, Object>>
//            httpEntity = new HttpEntity<MultiValueMap<String, Object>>(null,headers);

        // 发起令牌请求
//        ResponseEntity<OAuth2AccessToken> response = restTemplate.exchange(tokenUrl, HttpMethod.GET, httpEntity, OAuth2AccessToken.class, parameters);
//        ResponseEntity<OAuth2AccessToken> response = restTemplate.getForEntity(tokenUrl,  OAuth2AccessToken.class, parameters);
//        OAuth2AccessToken accessToken = response.getBody();

        String param =
            "grant_type=authorization_code&client_id=MY_APP_ID&client_secret=123456&redirect_uri=http://localhost:8852/api/public/auth/callback&scope=all&code="
                + authorizationCodeTmp;
        tokenUrl = tokenUrl + "?"+ param;

        HttpClientUtil.getSender(tokenUrl, null, response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                String responseEntityStr = entity != null ? EntityUtils.toString(entity) : null;
                System.out.println("*********************** response entity start *******************");
                System.out.println(responseEntityStr);
                System.out.println("*********************** response entity end *******************");
                return (CloseableHttpResponse)response;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        });


        return "successful";
    }

    @GetMapping("/public/echo/{param}")
    public String testPublic(HttpServletRequest request, @PathVariable("param") String param) throws Exception {
        return param;
    }



    @GetMapping("/private/echo/{param}")
    public String testPrivate(HttpServletRequest request, @PathVariable("param") String param) throws Exception {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String redirectUrl = "/oauth/authorize?response_type=code&client_id=MY_APP_ID&scope=all&redirect_uri=http://localhost:8852/api/public/auth/callback";
//        return "redirect:" + redirectUrl;
        return param;
    }
}
