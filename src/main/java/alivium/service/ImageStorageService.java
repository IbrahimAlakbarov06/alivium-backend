package alivium.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ImageStorageService {
    String uploadFile(MultipartFile file, String bucketName);
    void deleteFile(String bucketName, String fileName);
    InputStream downloadFile(String bucketName, String fileName);
    String getPreSignedUrl(String bucketName, String fileName, int expirySeconds);
}
