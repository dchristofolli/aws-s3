package com.dchristofolli.projects.awss3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.dchristofolli.projects.awss3.exception.NotFoundException;
import com.dchristofolli.projects.awss3.model.FileModel;
import com.dchristofolli.projects.awss3.model.ResponseModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
@Slf4j
public class DownloadService {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private final String bucket;

    @Value("${download_path}")
    private final String downloadPath;

    public ResponseModel listAll() {
        ListObjectsV2Result result = amazonS3.listObjectsV2(bucket);
        List<FileModel> fileList = new ArrayList<>();
        AtomicInteger totalFileSize = new AtomicInteger();
        int keyCount = result.getKeyCount();
        getFileProperties(result, fileList, totalFileSize);
        return ResponseModel.builder()
                .bucketName(result.getBucketName())
                .fileModelList(fileList)
                .quantity(keyCount)
                .totalFileSize(totalFileSize.get() / 1024 + " kb")
                .build();
    }

    private void getFileProperties(ListObjectsV2Result result, List<FileModel> fileList, AtomicInteger totalFileSize) {
        result.getObjectSummaries()
                .forEach(file -> {
                    fileList.add(FileModel.builder()
                            .fileName(file.getKey())
                            .size(file.getSize()/1024)
                            .build());
                    totalFileSize.addAndGet(Math.toIntExact(file.getSize()));
                });
    }

    public void getObject(String objectKey) {
        if (!amazonS3.doesObjectExist(bucket, objectKey))
            throw new NotFoundException("File not exists", HttpStatus.NOT_FOUND);
        try {
            S3Object fullObject = amazonS3.getObject(bucket, objectKey);
            S3ObjectInputStream content = fullObject.getObjectContent();
            makeDirectory();
            makeFile(objectKey, content);
        } catch (AmazonServiceException | IOException e) {
            log.error(e.getMessage());
        }
    }

    private void makeDirectory() {
        try {
            Files.createDirectories(Path.of(downloadPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeFile(String objectKey, S3ObjectInputStream content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(
                new File(downloadPath + File.separator + objectKey))) {
            byte[] readBuffer = new byte[1024];
            int readLength;
            while ((readLength = content.read(readBuffer)) > 0) {
                fos.write(readBuffer, 0, readLength);
            }
            content.close();
        }
    }
}