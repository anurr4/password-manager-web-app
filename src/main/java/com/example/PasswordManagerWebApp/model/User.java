package com.example.PasswordManagerWebApp.model;

import jakarta.persistence.*;
import java.util.List;

/**
 *
 * @author Anurra
 */
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullname;
    private String username;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PasswordEntry> passwordEntries;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<DeletedPassword> deletedPasswords;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WifiPassword> wifiPasswords;

    public Long getId() { return id; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<PasswordEntry> getPasswordEntries() { return passwordEntries; }
    public void setPasswordEntries(List<PasswordEntry> passwordEntries) { this.passwordEntries = passwordEntries; }
    public List<DeletedPassword> getDeletedPasswords() { return deletedPasswords; }
    public void setDeletedPasswords(List<DeletedPassword> deletedPasswords) { this.deletedPasswords = deletedPasswords; }
    public List<WifiPassword> getWifiPasswords() { return wifiPasswords; }
    public void setWifiPasswords(List<WifiPassword> wifiPasswords) { this.wifiPasswords = wifiPasswords; }
}