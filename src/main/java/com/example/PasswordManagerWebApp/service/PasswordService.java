package com.example.PasswordManagerWebApp.service;

import com.example.PasswordManagerWebApp.model.DeletedPassword;
import com.example.PasswordManagerWebApp.model.PasswordEntry;
import com.example.PasswordManagerWebApp.model.User;
import com.example.PasswordManagerWebApp.model.WifiPassword;
import com.example.PasswordManagerWebApp.repository.DeletedPasswordRepository;
import com.example.PasswordManagerWebApp.repository.PasswordEntryRepository;
import com.example.PasswordManagerWebApp.util.GenericQueue;
import com.example.PasswordManagerWebApp.util.GenericStack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordService {
    @Autowired
    private PasswordEntryRepository passwordRepository;

    @Autowired
    private DeletedPasswordRepository deletedPasswordRepository;

    @Autowired
    private WifiPasswordService wifiPasswordService;

    private GenericStack<PasswordEntry> undoStack = new GenericStack<>();
    private GenericQueue<DeletedPassword> deletedQueue = new GenericQueue<>();

    public PasswordEntry savePassword(User user, String website, String username, String password, String notes) {
        PasswordEntry pwd = new PasswordEntry();
        pwd.setUser(user);
        pwd.setSiteName(website);
        pwd.setUsername(username);
        pwd.setPassword(password);
        pwd.setNotes(notes);
        return passwordRepository.save(pwd);
    }

    public PasswordEntry updatePassword(Long id, User user, String website, String username, String password, String notes) {
        PasswordEntry pwd = passwordRepository.findById(id).orElse(null);
        if (pwd != null && pwd.getUser().getId().equals(user.getId())) {
            pwd.setSiteName(website);
            pwd.setUsername(username);
            pwd.setPassword(password);
            pwd.setNotes(notes);
            return passwordRepository.save(pwd);
        }
        return null;
    }

    public List<PasswordEntry> getPasswords(User user) {
        return new ArrayList<>(passwordRepository.findByUser(user));
    }

    public List<WifiPassword> getWifiPasswords(User user) {
        return new ArrayList<>(wifiPasswordService.getWifiPasswordsByUser(user));
    }

    public void deletePassword(Long id, User user) {
        PasswordEntry password = passwordRepository.findById(id).orElse(null);
        if (password != null && password.getUser().getId().equals(user.getId())) {
            undoStack.push(password); // Store for undo
            DeletedPassword deleted = new DeletedPassword();
            deleted.setId(password.getId()); // Ensure the DeletedPassword has the same id
            deleted.setSiteName(password.getSiteName());
            deleted.setUsername(password.getUsername());
            deleted.setPassword(password.getPassword());
            deleted.setNotes(password.getNotes());
            deleted.setUser(user);
            deleted.setDeletedAt(LocalDateTime.now());
            deletedPasswordRepository.save(deleted);
            deletedQueue.enqueue(deleted);
            passwordRepository.delete(password);
        }
    }

    public PasswordEntry undoDelete(Long id) {
        System.out.println("Attempting to undo delete for id: " + id);
        DeletedPassword deleted = deletedPasswordRepository.findById(id).orElse(null);
        if (deleted != null) {
            System.out.println("Found deleted password: " + deleted.getSiteName());
            // Verify the PasswordEntry with this id doesn't already exist
            PasswordEntry existingPassword = passwordRepository.findById(id).orElse(null);
            if (existingPassword != null) {
                System.out.println("A password with id " + id + " already exists in the database.");
                return null; // Cannot restore if a password with this id already exists
            }

            // Create a new PasswordEntry
            PasswordEntry restoredPassword = new PasswordEntry();
            // Do not set ID; let the database generate a new one
            restoredPassword.setUser(deleted.getUser()); // Preserve user association
            restoredPassword.setSiteName(deleted.getSiteName());
            restoredPassword.setUsername(deleted.getUsername());
            restoredPassword.setPassword(deleted.getPassword());
            restoredPassword.setNotes(deleted.getNotes());

            try {
                // Save the restored password
                PasswordEntry savedPassword = passwordRepository.saveAndFlush(restoredPassword);
                System.out.println("Successfully restored password with id: " + savedPassword.getId());
                deletedPasswordRepository.delete(deleted); // Remove from database
                deletedQueue.getQueue().removeIf(d -> d.getId().equals(id)); // Remove from queue
                return savedPassword;
            } catch (Exception e) {
                System.err.println("Error restoring password: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } else {
            System.out.println("No deleted password found for id: " + id);
        }
        return null;
    }

    public List<DeletedPassword> getDeletedPasswords(User user) {
        return deletedPasswordRepository.findByUser(user);
    }

    public GenericQueue<DeletedPassword> getDeletedQueue() {
        return deletedQueue;
    }
}