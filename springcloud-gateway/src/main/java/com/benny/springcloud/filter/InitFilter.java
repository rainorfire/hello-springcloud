package com.benny.springcloud.filter;

import com.alibaba.cloud.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/4/26 17:45
 * @since 1.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InitFilter implements GlobalFilter {

    private static final String LOGIN_PAGE_URL = "http://localhost:8852/login";

    private static final String LOGIN_URI_PRE = "/oauth-login";

    private static final String LOGIN_AUTHENTICATION_HEADER = "Authentication";

    private static final String OAUTH2_AUTHORIZE_URL = "http://localhost:8852/oauth/authorize?response_type=code&client_id=%s&scope=2&state=%s";

    private static final String OAUTH2_ACCESS_TOKEN_HEADER = "oauth2AccessToken";

    private static final String OAUTH_CLIENT_CODE_RESULT_URL = "/login/login-successful";

    private static final String OAUTH_ACCESS_TOKEN_URL = "http://localhost:8852/oauth/token?grant_type=authorization_code&code=%s&state=123&client_secret=123456&client_id=%s";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final RequestPath path = request.getPath();
        final List<String> authenticationUserIds = request.getHeaders().get(LOGIN_AUTHENTICATION_HEADER);
        final List<String> accessTokenList = request.getHeaders().get(OAUTH2_ACCESS_TOKEN_HEADER);

        Route route =(Route) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route.getId();

        // 认证结果
        if("springcloud-authorization".equals(routeId) && OAUTH_CLIENT_CODE_RESULT_URL.equals(request.getPath().toString())) {
            final List<String> oathStateList = request.getQueryParams().get("state");
            final List<String> codeList = request.getQueryParams().get("code");
            final String oldRouteId = oathStateList.get(0);
            final String url = String.format(OAUTH_ACCESS_TOKEN_URL, codeList.get(0), oldRouteId);
            RestTemplate restTemplate = new RestTemplate();
            final Map<String, Object> forObject = restTemplate.getForObject(url, Map.class);
            if(forObject != null) {
                final String accessToken = forObject.get("access_token").toString();
//                final String accessToken = forObject.get("refresh_token").toString();
                final String userName = forObject.get("userName").toString();
                final String roleList = forObject.get("roleList").toString();
                request.mutate().header(OAUTH2_ACCESS_TOKEN_HEADER, accessToken);
            }
        }

        // 没登录 跳转登录页面
        else if(CollectionUtils.isEmpty(authenticationUserIds) && path.toString().startsWith(LOGIN_URI_PRE)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set("Location", LOGIN_PAGE_URL);
            return exchange.getResponse().setComplete();
        }
        // 没认证，去认证
        else if(CollectionUtils.isEmpty(accessTokenList)) {
            final String authorizeUrl = String.format(OAUTH2_AUTHORIZE_URL, routeId, routeId);
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set("Location", authorizeUrl);
            return exchange.getResponse().setComplete();
        }
        exchange.getAttributes().put("consumingStartTime", System.currentTimeMillis());
        return chain.filter(exchange);
    }
}
