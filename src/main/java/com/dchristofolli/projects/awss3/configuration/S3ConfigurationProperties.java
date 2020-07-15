package com.dchristofolli.projects.awss3.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

import static com.dchristofolli.projects.awss3.configuration.RootKeyReader.getRootKey;

@Data
@ConfigurationProperties("aws.s3")
public class S3ConfigurationProperties {
    private Region region = Region.SA_EAST_1;

    private String accessKeyId = getRootKey().getAWSAccessKeyId();

    private String secretAccessKey = getRootKey().getAWSSecretKey();

    @Value("${aws.s3.bucket}")
    private String bucket;

    private int multipartMinPartSize = 5 * 1024 * 1024;

}
