package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.utils.AuthFileCache;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PDFDownloader {

    private static final String BASE_URL = "https://bmh-backend-api.onrender.com/api/v1";

    private static String getToken(){
        String[] credentials = TokenStorage.loadCredentials();
        String token = "";
        if (credentials != null) {
            token = credentials[1];
        }
        return token;
    }
    public static File downloadPDF(String apiUrl) throws IOException {
        URL url = new URL(BASE_URL + apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + getToken());

        File tempFile = File.createTempFile("invoice", ".pdf");
        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(tempFile)) {
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }
}

