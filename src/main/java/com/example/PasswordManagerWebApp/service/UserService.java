package com.example.PasswordManagerWebApp.service;

import com.example.PasswordManagerWebApp.model.User;
import com.example.PasswordManagerWebApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Anurra
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(String fullname, String username, String password) {
        User user = new User();
        user.setFullname(fullname);
        user.setUsername(username);
        user.setPassword(password);
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}