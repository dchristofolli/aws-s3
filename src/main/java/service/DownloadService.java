package service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public ListObjectsV2Result listAll() {
        return amazonS3.listObjectsV2("dchristofolli");
    }

    public void getObject(String objectKey) {
        try {
            S3Object fullObject = amazonS3.getObject("dchristofolli", objectKey);
            S3ObjectInputStream s3is = fullObject.getObjectContent();
            makeDirectory();
            try (FileOutputStream fos = new FileOutputStream(
                    new File("Downloads" + File.separator + objectKey))) {
                byte[] readBuffer = new byte[1024];
                int readLength;
                while ((readLength = s3is.read(readBuffer)) > 0) {
                    fos.write(readBuffer, 0, readLength);
                }
                s3is.close();
            }
        } catch (AmazonServiceException | IOException e) {
            log.error(e.getMessage());
        }
    }

    private void makeDirectory() {
        try {
            Files.createDirectories(Path.of("Downloads"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
