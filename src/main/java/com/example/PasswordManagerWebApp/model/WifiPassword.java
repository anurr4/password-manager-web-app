/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.PasswordManagerWebApp.model;
import jakarta.persistence.*;

/**
 *
 * @author Anurra
 */
@Entity
public class WifiPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String website; // Used as SSID for WiFi
    private String password;
    private String notes;
    @ManyToOne
    private User user;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
