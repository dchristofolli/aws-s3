package com.dchristofolli.projects.awss3.controller;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.DownloadService;
import service.UploadService;

import java.io.File;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class Controller {
    private final UploadService uploadService;
    private final DownloadService downloadService;

    @PostMapping(path = "/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(@RequestPart(value = "file") MultipartFile multipartFile) {
        uploadService.uploadFile(multipartFile);
    }

//    @PostMapping(path = "/multi-upload")
//    public void multiFileUpload( List<File> fileList) {
//        uploadService.multipleFileUpload(fileList);
//    }

    @GetMapping("/list")
    public ListObjectsV2Result findAll() {
        return downloadService.listAll();
    }

    @GetMapping("/download/{objectKey}")
    public void getObject(@PathVariable String objectKey) {
        downloadService.getObject(objectKey);
    }
}
