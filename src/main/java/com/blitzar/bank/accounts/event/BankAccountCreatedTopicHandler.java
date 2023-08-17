package com.blitzar.bank.accounts.event;

import com.agorapulse.micronaut.amazon.awssdk.sns.annotation.NotificationClient;
import com.agorapulse.micronaut.amazon.awssdk.sns.annotation.Topic;
import com.blitzar.bank.accounts.web.dto.BankAccountDTO;

@NotificationClient
public interface BankAccountCreatedTopicHandler {

    @Topic(value = "${aws.sns.topic}")
    String publishMessage(BankAccountDTO bankAccount);

}
