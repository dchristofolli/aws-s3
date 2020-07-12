package com.dchristofolli.projects.awss3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class UploadService {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private final String bucket;

    @Async
    public void uploadFile(final MultipartFile multipartFile) {
        log.info("File upload in progress.");
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            uploadFileToS3Bucket(file);
            log.info("File upload is completed.");
            Files.delete(Path.of(String.valueOf(file)));
        } catch (final AmazonServiceException | IOException ex) {
            log.error("Error= {} while uploading file.", ex.getMessage());
        }
    }

    public void multipleFileUpload(List<File> fileList) {
        if (fileList.size() > 1048576)
            log.info("Size: " + fileList.size());
        fileList.forEach(file -> fileList.add(new File(String.valueOf(file))));
        TransferManager transferManager = TransferManagerBuilder.standard()
                .build();
        MultipleFileUpload multiFileUpload = transferManager
                .uploadFileList(bucket,
                        "_",
                        new File("."), fileList);
        multiFileUpload.getProgress();
        try {
            multiFileUpload.waitForCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        transferManager.shutdownNow();
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

    private void uploadFileToS3Bucket(final File file) {
        amazonS3.putObject(new PutObjectRequest(
                bucket,
                file.getName(),
                file));
    }
}
