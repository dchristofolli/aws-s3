package com.dchristofolli.projects.awss3.controller;

import com.dchristofolli.projects.awss3.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class Controller {
    private final FileService fileService;

    @PostMapping(path = "/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> uploadFile(@RequestPart(value = "file") Mono<FilePart> filePartMono) {
        return fileService.uploadFile(filePartMono);
    }

    @GetMapping("/list")
    public Flux<String> findAll() {
        return fileService.listAll();
    }
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
