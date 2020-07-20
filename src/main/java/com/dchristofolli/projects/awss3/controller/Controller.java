package com.dchristofolli.projects.awss3.controller;

import com.dchristofolli.projects.awss3.model.FolderModel;
import com.dchristofolli.projects.awss3.model.ResponseModel;
import com.dchristofolli.projects.awss3.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class Controller {
    private final FileService fileService;

    //todo criar rotina de limpeza da pasta temp
    //todo fazer a pasta downloads apontar para a pasta downloads do usuário

    @PostMapping("/upload/{folder}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> uploadFiles(@PathVariable("folder") String applicantId,
                                  @RequestPart(value = "file") Mono<FilePart> filePartMono) {
        return fileService.uploadFiles(applicantId, filePartMono);
    }

    @PostMapping("/create/{bucket}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> uploadFiles(@PathVariable("bucket") String bucketName) {
        return fileService.createBucket(bucketName);
    }

    @GetMapping("/list-buckets")
    public Flux<List<String>> findAllBuckets() {
        return fileService.listAllBuckets();
    }

    @GetMapping("/list/{folder}")
    public Mono<FolderModel> findAllByApplicant(@PathVariable("folder") String applicantId) {
        return fileService.listAllByApplicant(applicantId);
    }

    @GetMapping("/list")
    public Mono<ResponseModel> findAll() {
        return fileService.listAll();
    }

    @GetMapping("/download/{folder}/{file}")
    public Mono<Void> downloadFile(@PathVariable("folder") String applicantId,
                                   @PathVariable("file") String fileKey) {
        return fileService.downloadFile(applicantId, fileKey);
    }

    @DeleteMapping("/delete/{folder}/{file}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteFile(@PathVariable("folder") String applicantId,
                                 @PathVariable("file") String fileName) {
        return fileService.deleteFile(applicantId, fileName);
    }
}
