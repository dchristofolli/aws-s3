package service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DownloadService {
    private final AmazonS3 amazonS3;

    public ListObjectsV2Result listAll() {
        return amazonS3.listObjectsV2("dchristofolli");
    }
}
