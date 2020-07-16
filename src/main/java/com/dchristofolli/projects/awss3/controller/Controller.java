package com.dchristofolli.projects.awss3.controller;

import com.dchristofolli.projects.awss3.model.ResponseModel;
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

    //todo criar rotina de limpeza da pasta temp
    //todo fazer a pasta downloads apontar para a pasta downloads do usuário

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> uploadFiles(@RequestPart(value = "file") Flux<FilePart> filePartFlux) {
        return fileService.uploadFiles(filePartFlux);
    }

    @GetMapping("/list")
    public Mono<ResponseModel> findAll() {
        return fileService.listAll();
    }

    @GetMapping("/download/{folder}/{file}")
    public Mono<Void> downloadFile(@PathVariable("folder") String applicantFolder,
                                   @PathVariable("file") String fileKey) {
        return fileService.downloadFile(applicantFolder, fileKey);
    }

    @DeleteMapping("/delete/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteFile(@PathVariable String fileName) {
        return fileService.deleteFile(fileName);
    }
}
