package com.blitzar.bank.accounts.event;

import com.agorapulse.micronaut.amazon.awssdk.sns.annotation.NotificationClient;
import com.agorapulse.micronaut.amazon.awssdk.sns.annotation.Topic;
import com.blitzar.bank.accounts.web.dto.BankAccountDTO;

@NotificationClient
public interface BankAccountCreatedTopicHandler {

    @Topic(value = "${app.aws.sns.bank-account-created-topic-name}")
    String publishMessage(BankAccountDTO bankAccount);

}
