package com.benny.springcloud.security.authhandler;

import com.benny.springcloud.security.configuration.bean.UserDetailsBean;
import com.benny.springcloud.security.jwt.JwtUtil;
import com.benny.springcloud.util.CookieUtil;
import com.benny.springcloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * description
 *
 * @author Chenyujia
 * @date 2023/6/30 16:43
 * @since 1.0
 */
//@Component
public class LoginSuccessfulHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(LoginSuccessfulHandler.class);

//    @Resource
//    private JwtAccessTokenConverter tokenConverter;

    public LoginSuccessfulHandler() {
        super.setUseReferer(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

//        final Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();

        final UsernamePasswordAuthenticationToken authenticationToken =
            (UsernamePasswordAuthenticationToken)authentication;

        String header = request.getHeader("Authorization");
        final UserDetailsBean userDetailsBean = (UserDetailsBean) authenticationToken.getPrincipal();

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String loginToken = "";
        try {
            loginToken = JwtUtil.generateToken(userDetailsBean);
            response.setHeader("Authorization", loginToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        logger.info("认证成功！Authorization Token={}, authentication={}", loginToken, JsonUtil.object2Json(authentication));

        CookieUtil.addCookie(request, response, "al-token", loginToken, 7200, "/", "localhost");
        response.sendRedirect("/api/public/login/successful?al-token=" + loginToken);
//        request.getRequestDispatcher("/public/login/successful").forward(request,response);

//
//        super.onAuthenticationSuccess(request, response, authentication);


    }
}
