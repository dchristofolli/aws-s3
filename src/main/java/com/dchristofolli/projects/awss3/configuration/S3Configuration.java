package com.dchristofolli.projects.awss3.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.dchristofolli.projects.awss3.configuration.RootKeyReader.getRootKey;

@Configuration
@Getter
public class S3Configuration {
    private final String accessKeyId = getRootKey().getAWSAccessKeyId();
    private final String secretAccessKey = getRootKey().getAWSSecretKey();

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${download_path}")
    private String downloadPath;

    @Bean
    public AmazonS3 getAmazonS3Cient() {
        final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }

    @Bean
    public String string(){
        return "";
    }
}
