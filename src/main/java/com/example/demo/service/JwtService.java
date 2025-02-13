package com.example.demo.service;

import com.example.demo.constant.Constants;
import com.example.demo.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
            return claims.get("sub", String.class);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired token");
        }
    }

    public Map<String, String> generateTokens(String username) {
        String accessToken = createToken(username, Constants.ACCESS_TOKEN_EXPIRATION);
        String refreshToken = createToken(username, Constants.REFRESH_TOKEN_EXPIRATION);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public String validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String generateNewAccessToken(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String username = claims.getSubject();
            long expiration = claims.getExpiration().getTime();

            // Ensure the refresh token is not expired
            if (expiration < System.currentTimeMillis()) {
                throw new RuntimeException("Refresh token expired");
            }

            // Generate new access token
            return createToken(username, Constants.ACCESS_TOKEN_EXPIRATION);

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Refresh token expired", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token", e);
        }
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
