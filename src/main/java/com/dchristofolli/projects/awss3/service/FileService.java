package com.dchristofolli.projects.awss3.service;

import com.dchristofolli.projects.awss3.model.ResponseModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class FileService {

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

    public Mono<ResponseModel> listAll() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();
//        return asyncClient.listObjectsV2(request).complete();
        return null;
    }
}