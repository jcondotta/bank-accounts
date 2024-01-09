package com.blitzar.bank.accounts.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Factory
public class AWSS3ClientFactory {

    @Value("${aws.region}")
    private String region;

    @Singleton
    S3Client sqsClient(AWSCredentials awsCredentials) {
        return S3Client
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsCredentials.getAWSAccessKeyId(), awsCredentials.getAWSSecretKey()))
                    )
                .region(Region.of(region))
                .build();
    }
}