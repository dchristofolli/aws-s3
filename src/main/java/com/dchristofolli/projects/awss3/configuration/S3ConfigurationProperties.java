package com.dchristofolli.projects.awss3.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

import static com.dchristofolli.projects.awss3.configuration.RootKeyReader.getRootKey;

@Configuration
@Data
public class S3ConfigurationProperties {
    private Region region = Region.SA_EAST_1;

//    @Value("${aws.s3.uri}")
//    private String uri;
//
//    private URI endpoint = URI.create(uri);

    private String accessKeyId = getRootKey().getAWSAccessKeyId();

    private String secretAccessKey = getRootKey().getAWSSecretKey();

    @Value("${aws.s3.bucket}")
    private String bucket;

    private int multipartMinPartSize = 5 * 1024 * 1024;

}
