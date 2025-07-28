package com.example.runway.infra.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Util s3Util;

    public String uploadImage(MultipartFile file, String folderName) {
        String extension = s3Util.extractExtension(file);
        String fileName = folderName + "/" + UUID.randomUUID() + extension;
        return s3Util.uploadFile(fileName, file);
    }

    public void removeImage(String fileUrl) {
        s3Util.deleteFile(fileUrl);
    }


}
