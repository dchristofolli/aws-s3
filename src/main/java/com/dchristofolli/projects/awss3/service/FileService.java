package com.dchristofolli.projects.awss3.service;

import com.dchristofolli.projects.awss3.exception.BadRequestException;
import com.dchristofolli.projects.awss3.exception.NotFoundException;
import com.dchristofolli.projects.awss3.model.FileModel;
import com.dchristofolli.projects.awss3.model.FolderModel;
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
import java.util.stream.Collectors;

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

    private static final String KEY_SEPARATOR = "/";

    public Mono<Void> uploadFiles(String folder, final Flux<FilePart> filePartFlux) {
        makeLocalDirectory(temp);
        AtomicReference<String> fileName = new AtomicReference<>();
        return filePartFlux
                .map(filePart -> {
                    fileName.set(temp + File.separator + (filePart.filename()));
                    return filePart.transferTo(new File(temp + File.separator + filePart.filename()));
                })
                .map(v -> Mono.fromFuture(uploadFileToS3Bucket(folder, new File(fileName.get()))))
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
                .bucket(bucket)
                .files(fileList)
                .totalFileSize(fileList.stream()
                        .mapToInt(FileModel::getSize).sum() + " kb")
                .quantity(keyCount).build());
    }

    public Mono<FolderModel> listAllByApplicant(String applicantId) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();
        List<String> files = s3AsyncClient.listObjectsV2(request)
                .join().contents()
                .parallelStream()
                .filter(s3Object -> s3Object.key().startsWith(applicantId))
                .map(S3Object::key)
                .map(s -> {
                    int init = s.indexOf("/") + 1;
                    return s.substring(init);
                })
                .collect(Collectors.toList());
        return Mono.just(FolderModel.builder()
                .folderName(applicantId)
                .files(files)
                .quantity(files.size())
                .build());
    }

    public Mono<Void> downloadFile(String applicantFolder, String fileKey) {
        checkFileConsistency(applicantFolder, fileKey);
        makeLocalDirectory(downloadPath);
        makeLocalDirectory(downloadPath + File.separator + applicantFolder);
        return Mono.just(
                s3AsyncClient.getObject(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(applicantFolder + KEY_SEPARATOR + fileKey)
                        .build(), Paths.get(downloadPath, applicantFolder, fileKey)))
                .then();
    }

    private void checkFileConsistency(String applicantFolder, String fileKey) {
        if (fileNotExistsInBucket(applicantFolder, fileKey))
            throw new NotFoundException("File not exists", HttpStatus.NOT_FOUND);
        if (Files.exists(Paths.get(downloadPath, applicantFolder, fileKey)))
            throw new BadRequestException("File already exists.", HttpStatus.BAD_REQUEST);
    }

    public Mono<Void> deleteFile(String applicantId, String fileName) {
        return Mono.just(s3AsyncClient
                .deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(applicantId + KEY_SEPARATOR + fileName)
                        .build()))
                .then();
    }

    private void makeLocalDirectory(String path) {
        try {
            if (!Files.exists(Paths.get(path)))
                Files.createDirectory(Path.of(path)).toAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CompletableFuture<PutObjectResponse> uploadFileToS3Bucket(String folder, File file) {
        String keyName = s3PathMaker(folder, file);
        return s3AsyncClient.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(keyName)
                .build(), AsyncRequestBody.fromFile(file));
    }

    private String s3PathMaker(String folder, File file) {
        String fileName = file.getName()
                .replace(" ", "_")
                .replace("/", "_");
        return folder + KEY_SEPARATOR + fileName;
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

    private boolean fileNotExistsInBucket(String folderName, String fileName) {
        return s3AsyncClient
                .listObjectsV2(getObjectsRequest())
                .join()
                .contents()
                .parallelStream()
                .noneMatch(s3Object -> s3Object.key().equals(folderName + KEY_SEPARATOR + fileName));
    }
}