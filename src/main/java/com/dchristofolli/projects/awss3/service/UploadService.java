package com.dchristofolli.projects.awss3.service;

import com.dchristofolli.projects.awss3.configuration.S3ClientConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class UploadService {
    private final S3AsyncClient asyncClient;
    @Value("${aws.s3.bucket}")
    private final String bucket;

//    public Mono<ResponseEntity<UploadResult>> uploadFile(HttpHeaders headers, Flux<ByteBuffer> body) {
//    }
    @PostConstruct
    public void send() {
        CompletableFuture<PutObjectResponse> responseFuture =
                asyncClient.putObject(PutObjectRequest.builder()
                                .bucket(bucket)
                                .key("object-key")
                                .build(),
                        AsyncRequestBody.fromFile(Paths.get("test.txt")));

        CompletableFuture<PutObjectResponse> operationCompleteFuture =
                responseFuture.whenComplete((putObjectResponse, exception) -> {
                    if (putObjectResponse != null) {
                        // Print the object version
                        System.out.println(putObjectResponse.versionId());
                    } else {
                        // Handle the error
                        exception.printStackTrace();
                    }
                });
        operationCompleteFuture.join();
    }
}
