package com.example.demo.service;

import com.example.demo.constant.Constants;
import com.example.demo.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JstServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    public Map<String, String> generateTokens(String username) {
        String accessToken = createToken(username, Constants.ACCESS_TOKEN_EXPIRATION);
        String refreshToken = createToken(username, Constants.REFRESH_TOKEN_EXPIRATION);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // Helper methods to create token
    private String createToken(String username, long validity) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();
    }
}
