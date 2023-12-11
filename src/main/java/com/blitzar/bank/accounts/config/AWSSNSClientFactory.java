package com.blitzar.bank.accounts.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import io.micronaut.aws.sdk.v2.service.s3.S3ClientFactory;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.objectstorage.aws.AwsS3Operations;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Factory
public class AWSSNSClientFactory {

    @Value("${aws.region}")
    private String region;

    @Singleton
    AmazonSNS snsClient(AWSStaticCredentialsProvider awsStaticCredentialsProvider) {
        return AmazonSNSClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(awsStaticCredentialsProvider)
                .build();
    }
}