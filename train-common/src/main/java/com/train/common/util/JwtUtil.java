package com.train.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {
    static final String SECRET = "prain-parents";
    static final String SUBJECT = "prain-parents";
    private static final long TOKEN_EXPIRE_TIME= 30*60*1000;//超时时间30分钟
    private static final long REFRESH_TOKEN_EXPIRE_TIME= 15*24*60*1000;//超时时间15天


    public static String generateToken(String id,Map<String, Object> map) {
        Date expiresAt = new Date(System.currentTimeMillis() + TOKEN_EXPIRE_TIME);
        String jwt = Jwts.builder()
                .setId(id)
                .setSubject(SUBJECT)
                .setClaims(map)
                .setExpiration(expiresAt)// 24 hour
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return jwt;//jwt前面一般都会加Bearer
    }

    public static Claims parseJWT(String token) throws Exception{
            // parse the token.
        return Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}