package com.example.PasswordManagerWebApp.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 *
 * @author Anurra
 */
@Service
public class PasswordGenerator {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()";
    private final SecureRandom random = new SecureRandom();

    public String generatePassword(int length, boolean includeUppercase, boolean includeLowercase,
                                   boolean includeNumbers, boolean includeSpecial) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than 0");
        }
        if (!includeUppercase && !includeLowercase && !includeNumbers && !includeSpecial) {
            throw new IllegalArgumentException("At least one character category must be selected");
        }

        StringBuilder charPool = new StringBuilder();
        if (includeUppercase) charPool.append(UPPERCASE);
        if (includeLowercase) charPool.append(LOWERCASE);
        if (includeNumbers) charPool.append(NUMBERS);
        if (includeSpecial) charPool.append(SPECIAL_CHARS);

        if (charPool.length() == 0) {
            throw new IllegalArgumentException("No characters available to generate password");
        }

        StringBuilder password = new StringBuilder();
        if (includeUppercase && UPPERCASE.length() > 0) password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        if (includeLowercase && LOWERCASE.length() > 0) password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        if (includeNumbers && NUMBERS.length() > 0) password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        if (includeSpecial && SPECIAL_CHARS.length() > 0) password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        int remainingLength = length - password.length();
        for (int i = 0; i < remainingLength; i++) {
            password.append(charPool.charAt(random.nextInt(charPool.length())));
        }

        return shuffleString(password.toString());
    }

    private String shuffleString(String str) {
        char[] characters = str.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}