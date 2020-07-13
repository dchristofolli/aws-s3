package com.dchristofolli.projects.awss3.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class UploadService {
    private final S3AsyncClient asyncClient;
    @Value("${aws.s3.bucket}")
    private final String bucket;


    public Mono<Void> uploadFile(final Mono<MultipartFile> multipartFile) {
        return multipartFile
                .map(this::convertMultiPartFileToFile)
                .map(this::uploadFileToS3Bucket)
                .then();
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            log.error(ex.getMessage());
        }
        return file;
    }

    private CompletableFuture<PutObjectResponse> uploadFileToS3Bucket(final File file) {
        return asyncClient.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(file.getName())
                .build(),
                AsyncRequestBody.fromFile(file));
    }
}
