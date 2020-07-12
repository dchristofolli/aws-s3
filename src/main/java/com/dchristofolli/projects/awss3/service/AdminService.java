package com.dchristofolli.projects.awss3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.VersionListing;
import com.dchristofolli.projects.awss3.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class AdminService {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private final String bucket;

    public void deleteFile(String objectKey) {
        if (!amazonS3.doesObjectExist(bucket, objectKey))
            throw new BadRequestException("File not exists", HttpStatus.NOT_FOUND);
        amazonS3.deleteObject(bucket, objectKey);
    }

    public void deleteAllFiles() {
        var listObjectsV2Result = amazonS3.listObjectsV2(bucket);
        if (listObjectsV2Result.getObjectSummaries().isEmpty())
            throw new BadRequestException("There's nothing around here", HttpStatus.NOT_FOUND);
        listObjectsV2Result.getObjectSummaries()
                .forEach(file -> deleteFile(file.getKey()));
    }
}

