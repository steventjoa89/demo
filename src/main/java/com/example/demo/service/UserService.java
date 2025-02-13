package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserUnauthorizedException;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class UserService {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    public void registerUser(UserRequest data) {
        if (userRepository.existsByName(data.getName())) {
            throw new UserAlreadyExistsException("User with name '" + data.getName() + "' already exists");
        }
        User user = new User();
        user.setName(data.getName());
        user.setPassword(data.getPassword());
        userRepository.save(user);
    }

    public Map<String, String> loginUser(UserRequest data){
        User user = checkIfNameExists(data.getName());

        if (!user.getPassword().equals(data.getPassword())) {
            throw new UserUnauthorizedException("Invalid password");
        }
        return jwtService.generateTokens(data.getName());
    }

    public String generateNewAccessToken(String refreshToken){
        return jwtService.generateNewAccessToken(refreshToken);
    }

    public User checkIfNameExists(String name){
        return userRepository.findByName(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
