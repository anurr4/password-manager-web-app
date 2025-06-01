package com.example.PasswordManagerWebApp.controller;

import com.example.PasswordManagerWebApp.model.DeletedPassword;
import com.example.PasswordManagerWebApp.model.PasswordEntry;
import com.example.PasswordManagerWebApp.model.User;
import com.example.PasswordManagerWebApp.model.WifiPassword;
import com.example.PasswordManagerWebApp.service.PasswordService;
import com.example.PasswordManagerWebApp.service.WifiPasswordService;
import com.example.PasswordManagerWebApp.service.PasswordGenerator;
import com.example.PasswordManagerWebApp.util.GenericQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/passwords")
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private WifiPasswordService wifiPasswordService;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @GetMapping
    public String showPasswordManagement(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("passwords", passwordService.getPasswords(user));
        model.addAttribute("wifiPasswords", wifiPasswordService.getWifiPasswordsByUser(user));
        List<DeletedPassword> deletedPasswords = passwordService.getDeletedPasswords(user);
        GenericQueue<DeletedPassword> deletedQueue = passwordService.getDeletedQueue();
        deletedQueue.getQueue().clear();
        for (DeletedPassword deleted : deletedPasswords) {
            deletedQueue.enqueue(deleted);
        }
        model.addAttribute("deletedPasswords", new ArrayList<>(deletedQueue.getQueue()));
        return "password_management";
    }

    @PostMapping
    public String savePassword(
            @RequestParam String website,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        System.out.println("Saving password - Website: " + website + ", Username: " + username);
        if (user == null) {
            return "redirect:/login";
        }
        passwordService.savePassword(user, website, username, password, notes);
        redirectAttributes.addFlashAttribute("success", "Password saved successfully.");
        return "redirect:/passwords";
    }

    @PostMapping("/wifi")
    public String saveWifiPassword(
            @RequestParam String website,
            @RequestParam String password,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        System.out.println("Saving WiFi password - Website: " + website);
        if (user == null) {
            return "redirect:/login";
        }
        wifiPasswordService.saveWifiPassword(user, website, password, notes);
        redirectAttributes.addFlashAttribute("success", "WiFi password saved successfully.");
        return "redirect:/passwords";
    }

    @PostMapping("/edit")
    public String editPassword(
            @RequestParam Long id,
            @RequestParam String website,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        System.out.println("Editing password with id: " + id);
        if (user == null) {
            return "redirect:/login";
        }
        PasswordEntry updatedPassword = passwordService.updatePassword(id, user, website, username, password, notes);
        if (updatedPassword != null) {
            redirectAttributes.addFlashAttribute("success", "Password updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update password.");
        }
        return "redirect:/passwords";
    }

    @PostMapping("/delete")
    public String deletePassword(
            @RequestParam Long id,
            @RequestParam(required = false, defaultValue = "App") String category,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        System.out.println("Deleting password with id: " + id + ", Category: " + category);
        if (user == null) {
            return "redirect:/login";
        }
        if ("WiFi".equals(category)) {
            wifiPasswordService.deleteWifiPassword(id, user);
            redirectAttributes.addFlashAttribute("success", "WiFi password deleted successfully.");
        } else {
            passwordService.deletePassword(id, user);
            redirectAttributes.addFlashAttribute("success", "Password deleted successfully.");
        }
        return "redirect:/passwords";
    }

    @PostMapping("/undo")
    public String undoDelete(
            @RequestParam Long id,
            @RequestParam(required = false, defaultValue = "App") String category,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        System.out.println("Undoing delete for id: " + id + ", category: " + category);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to restore passwords.");
            return "redirect:/login";
        }
        try {
            if ("WiFi".equals(category)) {
                WifiPassword restored = wifiPasswordService.undoDelete(id);
                if (restored != null) {
                    redirectAttributes.addFlashAttribute("success", "WiFi password restored successfully.");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Failed to restore WiFi password. It may no longer exist.");
                }
            } else {
                PasswordEntry restored = passwordService.undoDelete(id);
                if (restored != null) {
                    redirectAttributes.addFlashAttribute("success", "Password restored successfully.");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Failed to restore password. It may no longer exist.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error in undoDelete controller: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "An error occurred while restoring the password.");
        }
        return "redirect:/passwords";
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generatePassword(
            @RequestParam(name = "length", defaultValue = "12") int length,
            @RequestParam(name = "uppercase", defaultValue = "true") boolean uppercase,
            @RequestParam(name = "lowercase", defaultValue = "true") boolean lowercase,
            @RequestParam(name = "numbers", defaultValue = "true") boolean numbers,
            @RequestParam(name = "special", defaultValue = "true") boolean special) {
        System.out.println("Generating password with length: " + length + ", uppercase: " + uppercase +
                           ", lowercase: " + lowercase + ", numbers: " + numbers + ", special: " + special);
        try {
            if (length < 4 || length > 64) {
                return ResponseEntity.badRequest().body("Length must be between 4 and 64");
            }
            String password = passwordGenerator.generatePassword(length, uppercase, lowercase, numbers, special);
            return ResponseEntity.ok(password);
        } catch (IllegalArgumentException e) {
            System.err.println("Error generating password: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error occurred");
        }
    }
}