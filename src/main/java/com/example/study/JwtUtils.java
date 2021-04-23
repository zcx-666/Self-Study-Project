package com.example.study;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;


import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtUtils {

    private static String secret = "7Zz3RdxQ8/En41s801JAaLxB/ll1BMxBlAbn*OUPpvrIn";
    private static int expiration = 7 * 24 * 60 * 60;
    private static String payloadName = "cookie";

    /**
     * 根据负责生成JWT的token
     */
    private static String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate()).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * 从token中获取JWT中的负载
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            log.info("JWT格式验证失败:{}", token);
        }
        return claims;
    }

    /**
     * 生成token的过期时间
     */
    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    public static String getCookie(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        } else {
            if (isTokenExpired(token)) {
                return null;
            }
            return (String) claims.get(payloadName);
        }
    }

    private static boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.getTime() < System.currentTimeMillis();
    }

    private static Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return new Date(claims.getExpiration().getTime());
    }

    public static String generateCookie(String cookieStr) {
        Map<String, Object> m = new HashMap<>();
//        String cookie = encryption(cookieStr + System.currentTimeMillis());
        m.put(payloadName, cookieStr);
        return generateToken(m);
    }
}