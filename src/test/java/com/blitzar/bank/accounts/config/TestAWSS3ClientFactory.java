package com.blitzar.bank.accounts.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Factory
public class TestAWSS3ClientFactory {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.endpoint}")
    private String endPoint;

    @Singleton
    @Primary
    S3Client sqsClient(AWSCredentials awsCredentials) {
        return S3Client
                .builder()
                .endpointOverride(URI.create(endPoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsCredentials.getAWSAccessKeyId(), awsCredentials.getAWSSecretKey()))
                    )
                .region(Region.of(region))
                .build();
    }
}