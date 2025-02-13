package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public interface JwtService {
    String generateToken(String name);
    String validateToken(String token);
}
