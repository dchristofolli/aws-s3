package service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@AllArgsConstructor
@Slf4j
public class DownloadService {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private final String bucket;

    @Value("${download_path}")
    private final String downloadPath;

    public ListObjectsV2Result listAll() {
        return amazonS3.listObjectsV2(bucket);
    }

    public void getObject(String objectKey) {
        try {
            S3Object fullObject = amazonS3.getObject(bucket, objectKey);
            S3ObjectInputStream content = fullObject.getObjectContent();
            makeDirectory();
            try (FileOutputStream fos = new FileOutputStream(
                    new File(downloadPath + File.separator + objectKey))) {
                byte[] readBuffer = new byte[1024];
                int readLength;
                while ((readLength = content.read(readBuffer)) > 0) {
                    fos.write(readBuffer, 0, readLength);
                }
                content.close();
            }
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
}