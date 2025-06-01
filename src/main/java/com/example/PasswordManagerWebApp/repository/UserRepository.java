/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.PasswordManagerWebApp.repository;

import com.example.PasswordManagerWebApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Anurra
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
