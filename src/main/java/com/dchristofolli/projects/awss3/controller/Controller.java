package com.dchristofolli.projects.awss3.controller;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.DownloadService;
import service.UploadService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class Controller {
    private final UploadService uploadService;
    private final DownloadService downloadService;

    @PostMapping(path = "/upload")
    public String uploadFile(@RequestPart(value = "file") final MultipartFile multipartFile) {
        return uploadService.uploadFile(multipartFile);
    }

    @GetMapping("/list")
    public ListObjectsV2Result findAll(){
        return downloadService.listAll();
    }

    @GetMapping("/download/{objectKey}")
    public void getObject(@PathVariable String objectKey){
        downloadService.getObject(objectKey);
    }
}
