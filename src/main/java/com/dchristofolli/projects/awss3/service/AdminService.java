package com.dchristofolli.projects.awss3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.dchristofolli.projects.awss3.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AdminService {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private final String bucket;

    public void deleteFile(String objectKey) {
        if (!amazonS3.doesObjectExist(bucket, objectKey))
            throw new ApiException("File not exists", HttpStatus.NOT_FOUND);
        amazonS3.deleteObject(bucket, objectKey);
    }

    public void deleteAllFiles() {
        amazonS3.listObjects(bucket)
                .getObjectSummaries()
                .forEach(o -> deleteFile(o.getKey()));
    }
}

