package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void registerUser(String name);
    boolean userExists(String name);
}
