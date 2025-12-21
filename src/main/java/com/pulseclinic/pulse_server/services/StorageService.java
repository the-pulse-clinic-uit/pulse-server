package com.pulseclinic.pulse_server.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {
    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket.avatars}")
    private String avatarBucketName;

    private final WebClient webClient;

    public StorageService(WebClient.Builder webClientBuilder) {
        this.webClient = WebClient.builder().build();
    }

    public String uploadAvatar(MultipartFile file, UUID userId) throws IOException {
        validateFile(file);
        String fileName = generateFileName(userId, file.getOriginalFilename());

        String uploadUri = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, avatarBucketName, fileName);

        webClient.post()
                .uri(uploadUri)
                .header("Authorization", "Bearer " + supabaseKey)
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .bodyValue(file.getBytes())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return String.format("%s/storage/v1/object/public/%s/%s",
                supabaseUrl, avatarBucketName, fileName);
    }

    public void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();
        if(!contentType.startsWith("image/")){
            throw new RuntimeException("File is not an image");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new RuntimeException("File is too large");
        }
    }

    private String generateFileName(UUID userId, String fileName) {
        String extension = getFileExtension(fileName);
        return String.format("user-%s-%d.%s", userId.toString(), System.currentTimeMillis(), extension);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
