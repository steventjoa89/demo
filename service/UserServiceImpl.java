package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void registerUser(String name) {
        User user = new User();
        user.setName(name);
        userRepository.save(user);
    }

    @Override
    public boolean userExists(String name) {
        return userRepository.existsByName(name);
    }
}
