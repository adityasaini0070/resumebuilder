package com.aditya.resumebuilder.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {
    private final Cloudinary cloudinary;
    public Map<String, String> uploadSingleImage(MultipartFile file) throws IOException {
        Map<String, Object> imageUploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type","image"));
        log.info("Inside FileUploadService - uploadSingleImage(): {}", imageUploadResult.get("secure_url").toString());
        return Map.of("image_url", imageUploadResult.get("secure_url").toString());
    }
}
