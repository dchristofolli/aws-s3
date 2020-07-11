package service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class UploadService {
    private final AmazonS3 amazonS3;

    @Async
    public String uploadFile(final MultipartFile multipartFile) {
        log.info("File upload in progress.");
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            uploadFileToS3Bucket(file);
            log.info("File upload is completed.");
            Files.delete(Path.of(String.valueOf(file)));
        } catch (final AmazonServiceException | IOException ex) {
            log.error("Error= {} while uploading file.", ex.getMessage());
        }
        return "[" + multipartFile.getOriginalFilename() + "] uploaded successfully.";
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            log.error("Error converting the multi-part file to file= " + ex.getMessage());
        }
        return file;
    }

    private void uploadFileToS3Bucket(final File file) {
        final String uniqueFileName = LocalDateTime.now() + "_" + file.getName();
        log.info("Uploading file with name= " + uniqueFileName);
        final PutObjectRequest putObjectRequest = new PutObjectRequest("dchristofolli", uniqueFileName, file);
        amazonS3.putObject(putObjectRequest);
    }
}
