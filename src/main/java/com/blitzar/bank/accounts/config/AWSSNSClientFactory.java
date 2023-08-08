package com.blitzar.bank.accounts.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

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