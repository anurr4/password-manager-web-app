/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.PasswordManagerWebApp.repository;

import com.example.PasswordManagerWebApp.model.PasswordEntry;
import com.example.PasswordManagerWebApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/**
 *
 * @author Anurra
 */
public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {
    List<PasswordEntry> findByUser(User user);
}
