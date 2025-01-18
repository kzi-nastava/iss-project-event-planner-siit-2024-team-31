package com.example.eventplanner.service;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Setter
@Getter
@RequiredArgsConstructor
public class PhotoService {

    private final S3Client s3Client;

    @Value("${aws.s3.region}")
    private String region;

    public List<String> uploadPhotos(List<MultipartFile> photos, String bucketName, String photosPrefix) {
        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile photo : photos) {
            try {
                String keyName = photosPrefix + "/" + UUID.randomUUID() + "_" + photo.getOriginalFilename();

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(photo.getInputStream(), photo.getSize()));

                String fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + keyName;
                photoUrls.add(fileUrl);
            } catch (IOException e) {
                System.out.printf("Error uploading photo: %s", e.getMessage());
            } catch (Exception e) {
                System.out.printf("Exception: %s", e.getMessage());
            }
        }
        return photoUrls;
    }
}
