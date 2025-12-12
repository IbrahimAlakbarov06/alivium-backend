package alivium.service;

import alivium.config.MinioProperties;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioProperties minioProperties;
    private final MinioClient minioClient;

    public boolean doesBucketExists(String bucketName){
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Bucket check failed: " + e.getMessage());
        }
    }

    public void createBucketIfNotExists(String bucketName){
        try{
            boolean bucketExists = doesBucketExists(bucketName);

            if(!bucketExists){
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Bucket create failed: " + e.getMessage());
        }
    }

    public String uploadFile(MultipartFile file, String bucketName){
        try{
            createBucketIfNotExists(bucketName);

            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(),file.getSize(),-1)
                            .contentType(file.getContentType())
                            .build()
            );

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Minio upload failed: " + e.getMessage());
        }
    }

    public void deleteFile(String bucketName, String fileName){
        try{
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Minio delete failed: " + e.getMessage());
        }
    }

    public InputStream downloadFile(String bucketName, String fileName){
        try{
            return  minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Minio download failed: " + e.getMessage());
        }
    }

    public String getFileUrl(String bucketName, String fileName){
        return "%s/%s/%s".formatted(minioProperties.getEndpoint(), bucketName, fileName);
    }

    public String getPreSignedUrl(String bucketName, String fileName, int expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(expirySeconds)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate preSigned URL: " + e.getMessage());
        }
    }

}
