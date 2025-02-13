package com.example.demo.controller;

import com.example.demo.request.UserRequest;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody UserRequest request) {
        userService.registerUser(request.getName());
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> generateToken(@Valid @RequestBody UserRequest request) {
        if (userService.userExists(request.getName())) {
            String token = jwtService.generateToken(request.getName());
            return ResponseEntity.ok(Map.of("token", token));
        }
        // TODO: use not found
        return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
    }
}
