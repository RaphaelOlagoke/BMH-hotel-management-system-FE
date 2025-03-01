package com.bmh.hotelmanagementsystem.BackendService;

import com.bmh.hotelmanagementsystem.BackendService.utils.AuthFileCache;
import com.bmh.hotelmanagementsystem.SceneManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestClient {
    private static final String BASE_URL = "http://localhost:8080/api/v1";

    private static String getAuthToken() {
        return AuthFileCache.getToken();
    }

    private static void handleExpiredToken() {
        System.out.println("Token expired. Redirecting to login...");
        AuthFileCache.clear();  // Delete the token
        SceneManager.switchToLogin();
    }

    public static String get(String endpoint) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .header("Authorization", "Bearer " + getAuthToken())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401 || response.statusCode() == 403) {
            handleExpiredToken();
            return null; // Stop processing
        }

        return response.body();
    }

    public static String post(String endpoint, String jsonPayload) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getAuthToken())
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401 || response.statusCode() == 403) {
            handleExpiredToken();
            return null; // Stop processing
        }

        return response.body();
    }

    public static String postWthoutToken(String endpoint, String jsonPayload) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401 || response.statusCode() == 403) {
            handleExpiredToken();
            return null; // Stop processing
        }

        return response.body();
    }


}

