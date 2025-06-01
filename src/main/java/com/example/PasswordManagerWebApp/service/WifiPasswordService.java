package com.example.PasswordManagerWebApp.service;

import com.example.PasswordManagerWebApp.model.DeletedPassword;
import com.example.PasswordManagerWebApp.model.User;
import com.example.PasswordManagerWebApp.model.WifiPassword;
import com.example.PasswordManagerWebApp.repository.DeletedPasswordRepository;
import com.example.PasswordManagerWebApp.repository.WifiPasswordRepository;
import com.example.PasswordManagerWebApp.util.GenericQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WifiPasswordService {

    @Autowired
    private WifiPasswordRepository wifiPasswordRepository;

    @Autowired
    private DeletedPasswordRepository deletedPasswordRepository;

    private final GenericQueue<DeletedPassword> deletedQueue;

    // Constructor injection to share the same deletedQueue instance
    @Autowired
    public WifiPasswordService(DeletedPasswordRepository deletedPasswordRepository, PasswordService passwordService) {
        this.deletedPasswordRepository = deletedPasswordRepository;
        this.deletedQueue = passwordService.getDeletedQueue();
    }

    public List<WifiPassword> getWifiPasswordsByUser(User user) {
        return wifiPasswordRepository.findByUser(user);
    }

    public WifiPassword saveWifiPassword(User user, String website, String password, String notes) {
        WifiPassword wifiPassword = new WifiPassword();
        wifiPassword.setUser(user);
        wifiPassword.setWebsite(website);
        wifiPassword.setPassword(password);
        wifiPassword.setNotes(notes);
        return wifiPasswordRepository.save(wifiPassword);
    }

    public void deleteWifiPassword(Long id, User user) {
        WifiPassword wifiPassword = wifiPasswordRepository.findById(id).orElse(null);
        if (wifiPassword != null && wifiPassword.getUser().getId().equals(user.getId())) {
            DeletedPassword deleted = new DeletedPassword();
            deleted.setId(wifiPassword.getId());
            deleted.setSiteName(wifiPassword.getWebsite());
            deleted.setUsername("");
            deleted.setPassword(wifiPassword.getPassword());
            deleted.setNotes(wifiPassword.getNotes());
            deleted.setUser(user);
            deleted.setDeletedAt(LocalDateTime.now());
            deletedPasswordRepository.save(deleted);
            deletedQueue.enqueue(deleted);
            wifiPasswordRepository.delete(wifiPassword);
        }
    }

    public WifiPassword undoDelete(Long id) {
        System.out.println("Attempting to undo delete for WiFi password with id: " + id);
        DeletedPassword deleted = deletedPasswordRepository.findById(id).orElse(null);
        if (deleted == null || !deleted.getUsername().isEmpty()) { // WiFi passwords have empty username
            System.out.println("No deleted WiFi password found for id: " + id + " or not a WiFi password");
            return null;
        }

        // Verify the WifiPassword with this id doesn't already exist
        WifiPassword existingPassword = wifiPasswordRepository.findById(id).orElse(null);
        if (existingPassword != null) {
            System.out.println("A WiFi password with id " + id + " already exists in the database.");
            return null; // Cannot restore if a password with this id already exists
        }

        // Create a new WifiPassword
        WifiPassword restoredPassword = new WifiPassword();
        // Do not set ID; let the database generate a new one
        restoredPassword.setUser(deleted.getUser()); // Preserve user association
        restoredPassword.setWebsite(deleted.getSiteName());
        restoredPassword.setPassword(deleted.getPassword());
        restoredPassword.setNotes(deleted.getNotes());

        try {
            // Save the restored password
            WifiPassword savedPassword = wifiPasswordRepository.saveAndFlush(restoredPassword);
            System.out.println("Successfully restored WiFi password with id: " + savedPassword.getId());
            deletedPasswordRepository.delete(deleted); // Remove from database
            deletedQueue.getQueue().removeIf(d -> d.getId().equals(id)); // Remove from queue
            return savedPassword;
        } catch (Exception e) {
            System.err.println("Error restoring WiFi password: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}