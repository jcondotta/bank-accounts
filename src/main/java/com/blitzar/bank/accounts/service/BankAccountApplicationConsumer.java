package com.blitzar.bank.accounts.service;

import com.blitzar.bank.accounts.service.bank_account.AddBankAccountRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountService;
import io.micronaut.jms.annotations.JMSListener;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.jms.sqs.configuration.SqsConfiguration;
import io.micronaut.messaging.annotation.MessageBody;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@JMSListener(SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME)
public class BankAccountApplicationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountApplicationConsumer.class);

    @Inject
    private final AddBankAccountService addBankAccountService;

    public BankAccountApplicationConsumer(AddBankAccountService addBankAccountService) {
        this.addBankAccountService = addBankAccountService;
    }

    @Queue(value = "${app.aws.sqs.bank-account-application-queue-name}", concurrency = "1-3")
    public void consumeMessage(@MessageBody AddBankAccountRequest addBankAccountRequest) {
        logger.info("BankAccountApplicationConsumer received a new message.");

        addBankAccountService.addBankAccount(addBankAccountRequest);
    }
}
