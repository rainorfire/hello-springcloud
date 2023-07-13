//package com.benny.springcloud.controllers;
//
//import com.nimbusds.jose.jwk.JWKSet;
//import com.nimbusds.jose.jwk.RSAKey;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.security.KeyPair;
//import java.security.interfaces.RSAPublicKey;
//import java.util.Map;
//
///**
// * description
// *
// * @author Chenyujia
// * @date 2022/5/8 1:50
// * @since 1.0
// */
//@RestController
//public class KeyPairController {
//
//    @Resource
//    private KeyPair keyPair;
//
//    @GetMapping("/rsa/publicKey")
//    public Map<String, Object> getKey() {
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAKey key = new RSAKey.Builder(publicKey).build();
//        return new JWKSet(key).toJSONObject();
//    }
//
//}
