package com.example.booking.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class handleImage {
    @Value("${file.upload-dir}")
    private static String uploadDir;
    public static String saveImage(MultipartFile file) throws IOException {
        // Ensure the upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Normalize the file name
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (originalFilename.contains("..")) {
                throw new IOException("Invalid file name: " + originalFilename);
            }

            // Copy file to the target location (replace existing file with the same name)
            Path targetLocation = uploadPath.resolve(originalFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return originalFilename; // Return the stored file name
        } catch (IOException ex) {
            throw new IOException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }
}
