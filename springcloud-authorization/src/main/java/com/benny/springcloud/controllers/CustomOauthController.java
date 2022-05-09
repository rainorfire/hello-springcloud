package com.benny.springcloud.controllers;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/8 1:50
 * @since 1.0
 */
@Controller
@RequestMapping("/oauth")
//@SessionAttributes("authorizationRequest")
public class CustomOauthController {

    @GetMapping("/custom-confirm-access")
    public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest)model.get("authorizationRequest");

        ModelAndView view = new ModelAndView();
        view.setViewName("oauth-grant");

        view.addObject("clientId", authorizationRequest.getClientId());

        view.addObject("scopes", authorizationRequest.getScope());

        return view;
    }

}
