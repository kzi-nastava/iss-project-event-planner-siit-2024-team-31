package com.example.eventplanner.service;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Setter
@Getter
@RequiredArgsConstructor
public class PhotoService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.region}")
    private String region;

    public List<String> uploadPhotos(List<MultipartFile> photos, String bucketName, String photosPrefix) {
        List<String> keys = new ArrayList<>();
        for (MultipartFile photo : photos) {
            try {
                String keyName = photosPrefix + "/" + UUID.randomUUID() + "_" + photo.getOriginalFilename();

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build();

                s3Client.putObject(putObjectRequest,
                        RequestBody.fromInputStream(photo.getInputStream(), photo.getSize()));

                keys.add(keyName);
            } catch (IOException e) {
                System.out.printf("Error uploading photo: %s%n", e.getMessage());
            } catch (Exception e) {
                System.out.printf("Exception: %s%n", e.getMessage());
            }
        }
        return keys;
    }

    public String generatePresignedUrl(String fileName, String bucketName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        //make for 60 minutes
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedGetObjectRequest.url().toString();
    }

}
