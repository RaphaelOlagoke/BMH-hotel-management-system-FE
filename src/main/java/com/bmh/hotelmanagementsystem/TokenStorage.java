package com.bmh.hotelmanagementsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class TokenStorage {
    private static final Path TOKEN_FILE = Path.of(System.getProperty("user.home"), ".bmh_credentials");

    public static void saveCredentials(String username, String token, String userRole) {
        try {
            Files.write(TOKEN_FILE, List.of(username, token, userRole), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] loadCredentials() {
        try {
            if (Files.exists(TOKEN_FILE)) {
                List<String> lines = Files.readAllLines(TOKEN_FILE);
                if (lines.size() == 3) {
                    return new String[]{lines.get(0), lines.get(1), lines.get(2) };
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearCredentials() {
        try {
            Files.deleteIfExists(TOKEN_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
