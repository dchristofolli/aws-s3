package com.dchristofolli.projects.awss3.service;

import com.dchristofolli.projects.awss3.Stub;
import com.dchristofolli.projects.awss3.model.ResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.io.File;

@ExtendWith(SpringExtension.class)
class FileServiceTest {
    Logger logger = LoggerFactory.getLogger(FileServiceTest.class);

    @InjectMocks
    private FileService fileService;

    @Mock
    private S3AsyncClient s3AsyncClient;


    @BeforeEach
    void setUp() {
//        String bucket = "bucket";
//        fileService.createBucket(bucket);
//        Flux<FilePart> fileFlux = Flux.just(new File("test-folder", "test.txt"));
//        fileService.uploadFiles("test-folder", fileFlux)
//        s3AsyncClient.putObject(PutObjectRequest.builder()
//                .bucket(bucket)
//                .key("test.txt")
//                .build(), AsyncRequestBody.fromFile(Path.of("temp_files", "text.txt")));
//        logger.info("Keycount: " + s3AsyncClient.listObjectsV2(ListObjectsV2Request.builder()
//                .bucket(bucket)
//                .build()).join().keyCount());
//        BDDMockito.when(s3AsyncClient.listObjectsV2(ListObjectsV2Request.builder()
//                .bucket(bucket)
//                .build())
//                .join().contents()
//                .parallelStream()
//                .filter(s3Object -> s3Object.key().startsWith("1"))
//                .map(S3Object::key)
//                .map(s -> {
//                    int init = s.indexOf("/") + 1;
//                    return s.substring(init);
//                })
//                .collect(Collectors.toList())).thenReturn(Stub.folder().getFiles());
    }

    @Test
    void createBucket() {
    }

    @Test
    void listAllBuckets() {
    }

    @Test
    void uploadFiles() {
    }

    @Test
    void listAll() {
        ResponseModel response = Stub.responseModelStub();
        StepVerifier.create(fileService.listAll())
                .expectSubscription()
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void listAllByApplicant() {
    }

    @Test
    void downloadFile() {
    }

    @Test
    void deleteFile() {
    }
}