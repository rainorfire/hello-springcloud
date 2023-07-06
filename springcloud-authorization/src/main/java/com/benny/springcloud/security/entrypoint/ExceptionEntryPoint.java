package com.benny.springcloud.security.entrypoint;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/11 9:05
 * @since 1.0
 */
@Slf4j
public class ExceptionEntryPoint implements AuthenticationEntryPoint {

    private static final String LOGIN_PAGE_URL = "http://localhost:8852/login";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        log.info("ExceptionEntryPoint ", authException);

        if (authException instanceof InsufficientAuthenticationException) {
            StringBuffer currentUrl = request.getRequestURL();
            String queryString = request.getQueryString();
            String currentUrlTmp = currentUrl.toString();
            if (StringUtils.isNotBlank(queryString)) {
                currentUrlTmp = currentUrlTmp + "?" + queryString;
            }
            response.sendRedirect(LOGIN_PAGE_URL + "?callback=" + URLEncoder.encode(currentUrlTmp, "UTF-8"));
        }
    }
}
