package com.bmh.hotelmanagementsystem.BackendService.utils;

import java.io.FileWriter;
import java.io.FileReader;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.File;

public class AuthFileCache {
    private static final String FILE_PATH = "auth.json";

    public static void saveCredentials(String token, String username) {
        try (FileWriter file = new FileWriter(FILE_PATH)) {
            JSONObject obj = new JSONObject();
            obj.put("authToken", token);
            obj.put("username", username);
            file.write(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getToken() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            JSONObject obj = new JSONObject(new JSONTokener(reader));
            return obj.getString("authToken");
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUsername() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            JSONObject obj = new JSONObject(new JSONTokener(reader));
            return obj.getString("username");
        } catch (Exception e) {
            return "User";
        }
    }

    public static void clear() {
        new File(FILE_PATH).delete();
    }
}
