package com.dchristofolli.projects.awss3.service;

import com.dchristofolli.projects.awss3.exception.NotFoundException;
import com.dchristofolli.projects.awss3.model.FileModel;
import com.dchristofolli.projects.awss3.model.ResponseModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@AllArgsConstructor
public class FileService {

    private final S3AsyncClient s3AsyncClient;
    @Value("${aws.s3.bucket}")
    private final String bucket;

    @Value("${path.temp}")
    private final String temp;

    @Value("${path.downloads}")
    private final String downloadPath;

    public Mono<Void> uploadFiles(final Flux<FilePart> filePartFlux) {
        makeDirectory(temp);
        AtomicReference<String> fileName = new AtomicReference<>();
        return filePartFlux
                .map(filePart -> {
                    fileName.set(temp + File.separator + (filePart.filename()));
                    return filePart.transferTo(new File(temp + File.separator + filePart.filename()));
                })
                .map(v -> Mono.fromFuture(uploadFileToS3Bucket(new File(fileName.get()))))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<ResponseModel> listAll() {
        ListObjectsV2Request listing = getObjectsRequest();
        ListObjectsV2Response result = s3AsyncClient.listObjectsV2(listing).join();
        List<FileModel> fileList = new ArrayList<>();
        AtomicInteger totalFileSize = new AtomicInteger();
        int keyCount = result.keyCount();
        getFileProperties(result, fileList, totalFileSize);
        return Mono.just(ResponseModel
                .builder()
                .bucketName(bucket)
                .keys(fileList)
                .totalFileSize(fileList.stream()
                        .mapToInt(FileModel::getSize).sum() + " kb")
                .quantity(keyCount).build());
    }

    public Mono<Void> downloadFile(String objectKey) {
        if (fileNotExists(objectKey))
            throw new NotFoundException("File not exists", HttpStatus.NOT_FOUND);
        makeDirectory(downloadPath);
        return Mono.just(
                s3AsyncClient.getObject(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .build(), Paths.get(downloadPath + File.separator + objectKey)))
                .then();
    }

    public Mono<Void> deleteFile(String fileName) {
        if (fileNotExists(fileName))
            throw new NotFoundException("File not exists", HttpStatus.NOT_FOUND);
        return Mono.just(s3AsyncClient
                .deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build()))
                .then();
    }

    private void makeDirectory(String path) {
        try {
            if (!Files.exists(Paths.get(path)))
                Files.createDirectory(Path.of(path)).toAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CompletableFuture<PutObjectResponse> uploadFileToS3Bucket(final File file) {
        return s3AsyncClient.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(file.getName().replace(" ", "_"))
                .build(), AsyncRequestBody.fromFile(file));
    }

    private void getFileProperties(ListObjectsV2Response response,
                                   List<FileModel> fileList,
                                   AtomicInteger totalFileSize) {
        response.contents()
                .forEach(file -> {
                    fileList.add(FileModel.builder()
                            .fileName(file.key())
                            .size((int) (file.size() / 1024))
                            .build());
                    totalFileSize.addAndGet(Math.toIntExact(file.size()));
                });
    }

    private ListObjectsV2Request getObjectsRequest() {
        return ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();
    }

    private boolean fileNotExists(String fileName) {
        return s3AsyncClient
                .listObjectsV2(getObjectsRequest())
                .join()
                .contents()
                .parallelStream()
                .noneMatch(s3Object -> s3Object.key().equals(fileName));
    }
}