package com.dchristofolli.projects.awss3.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class UploadService {
    private final S3AsyncClient asyncClient;

    @Value("${aws.s3.bucket}")
    private final String bucket;
    private final S3Configuration s3config;

    public Mono<Void> uploadFile(Mono<MultipartFile> multipartFile) {
        CompletableFuture future = asyncClient
                .putObject(PutObjectRequest.builder()
                                .bucket(bucket)
                                .contentLength(length)
                                .key(fileKey.toString())
                                .contentType(mediaType.toString())
                                .metadata(metadata)
                                .build(),
                        AsyncRequestBody.fromPublisher(body));
    }
}
