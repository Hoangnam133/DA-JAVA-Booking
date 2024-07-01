package com.example.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class HandleImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file) throws IOException {
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

            // Generate a unique file name to avoid cache issues
            String uniqueFileName = System.currentTimeMillis() + "_" + originalFilename;

            // Copy file to the target location (replace existing file with the same name)
            Path targetLocation = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return the relative path with the unique file name
            return "/hinhanh/" + uniqueFileName; // Return the stored file path with unique name
        } catch (IOException ex) {
            throw new IOException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }
}
