package com.benny.springcloud.security.jwt;


import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.benny.springcloud.security.configuration.bean.UserDetailsBean;
import com.benny.springcloud.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Json web token 工具
 * 验证、生成token
 *
 * @author Zoctan
 * @date 2018/06/09
 */
public class JwtUtil {

    private final static Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public static String generateToken(UserDetailsBean userDetailsBean) throws Exception {
        RSAUtil rsaUtil = new RSAUtil();
        final PrivateKey privateKey = rsaUtil.loadPemPrivateKey("/rsa/private-key.pem");
        final PublicKey publicKey = rsaUtil.loadPemPublicKey("/rsa/public-key.pem");

        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey)publicKey,(RSAPrivateKey) privateKey);

        Date expirDate =
            Date.from(LocalDateTime.now().plus(2L, ChronoUnit.HOURS).atZone(ZoneId.systemDefault()).toInstant());

        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("typ", "JWT");
        headerClaims.put("alg", "RSA");
        String token = JWT.create()
            .withHeader(headerClaims)
            .withClaim("userId", userDetailsBean.getId())
            .withClaim("userName", userDetailsBean.getUsername())
            .withClaim("roleList", userDetailsBean.getRoleList())
//            .withClaim("authorities", userDetailsBean.getAuthorities())
            .withClaim("datetime-claim", Instant.now())
            .withExpiresAt(expirDate)
            .sign(algorithm);
        System.out.println(token);
        return token;
    }

    public static UserDetailsBean verifyToken(String token) {
        try {
            RSAUtil rsaUtil = new RSAUtil();
            final PrivateKey privateKey = rsaUtil.loadPemPrivateKey("/rsa/private-key.pem");
            final PublicKey publicKey = rsaUtil.loadPemPublicKey("/rsa/public-key.pem");

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey)publicKey,(RSAPrivateKey) privateKey);

            JWTVerifier verifier = JWT.require(algorithm)
                // specify an specific claim validations
//                .withIssuer("auth0")
                // reusable verifier instance
                .build();
            if(token.startsWith("Bearer ")) {
                token = token.replace("Bearer ", "");
            }
            final DecodedJWT decodedJWT = verifier.verify(token);
            final Map<String, Claim> claims = decodedJWT.getClaims();
            UserDetailsBean userDetailsBean = new UserDetailsBean();
            userDetailsBean.setId(claims.get("userId") != null ? claims.get("userId").asLong() : 0L);
            userDetailsBean.setPassword(null);
            userDetailsBean.setUsername(claims.get("userName") != null ? claims.get("userName").asString() : "");
            userDetailsBean.setRoleList(claims.get("roleList") != null ? claims.get("roleList").asList(String.class) : null);

            return userDetailsBean;
        } catch (TokenExpiredException expiredException){
            throw expiredException;
        } catch (AlgorithmMismatchException algorithmMismatchException){
            throw algorithmMismatchException;
        } catch (InvalidClaimException invalidClaimException){
            throw invalidClaimException;
        } catch (SignatureVerificationException signatureVerificationException){
            throw signatureVerificationException;
        } catch (JWTDecodeException jwtDecodeException){
            throw jwtDecodeException;
        }
    }

//    public static void main(String[] args) throws Exception {
////        JwtUtil.generateToken();
//        final Date expirDate = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").parse("2023-07-03 18:40:00");
//        final DecodedJWT decodedJWT = JwtUtil.verifyToken(JwtUtil.generateToken(expirDate));
//        final Map<String, Claim> claims = decodedJWT.getClaims();
//        System.out.println(JsonUtil.object2Json(claims));
//    }
}