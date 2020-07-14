package com.dchristofolli.projects.awss3.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Slf4j
@Service
@AllArgsConstructor
public class UploadService {

    private final S3AsyncClient asyncClient;
    @Value("${aws.s3.bucket}")
    private final String bucket;


    public Mono<Void> uploadFile(final Mono<FilePart> filePartMono) {
        String fileName = UUID.randomUUID().toString();
        return filePartMono
                .map(filePart -> filePart.transferTo(new File(fileName)))
                .map(v -> Mono.fromFuture(uploadFileToS3Bucket(new File(fileName))))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private CompletableFuture<PutObjectResponse> uploadFileToS3Bucket(final File file) {
        return asyncClient.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(file.getName())
                .build(), AsyncRequestBody.fromFile(file));
    }
}