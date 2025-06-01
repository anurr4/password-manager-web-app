/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.PasswordManagerWebApp.repository;
import com.example.PasswordManagerWebApp.model.User;
import com.example.PasswordManagerWebApp.model.WifiPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
/**
 *
 * @author Anurra
 */
public interface WifiPasswordRepository extends JpaRepository<WifiPassword, Long> {
    List<WifiPassword> findByUser(User user);
}
