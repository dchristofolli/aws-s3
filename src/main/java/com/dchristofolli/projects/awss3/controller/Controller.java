package com.dchristofolli.projects.awss3.controller;

import com.dchristofolli.projects.awss3.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class Controller {
    private final UploadService uploadService;

    @PostMapping(path = "/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> uploadFile(@RequestPart(value = "file") Mono<MultipartFile> multipartFile) {
        return uploadService.uploadFile(multipartFile);
    }

//    @GetMapping("/list")
//    public ResponseModel findAll() {
//        return downloadService.listAll();
//    }
//
//    @GetMapping("/download/{objectKey}")
//    public void downloadFile(@PathVariable String objectKey) {
//        downloadService.getObject(objectKey);
//    }
//
//    @DeleteMapping("delete/{fileName}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteFile(@PathVariable String fileName) {
//        adminService.deleteFile(fileName);
//    }
//
//    @DeleteMapping("/delete")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteAll() {
//        adminService.deleteAllFiles();
//    }
}
