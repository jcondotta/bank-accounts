package com.blitzar.bank.accounts.event;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.blitzar.bank.accounts.LocalStackMySQLTestContainer;
import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.repository.AccountHolderRepository;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.account_holder.request.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.request.AddBankAccountRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class BankAccountApplicationConsumerTest implements LocalStackMySQLTestContainer {

    @Inject
    private AmazonSQS sqsClient;

    @Value("${app.aws.sqs.bank-account-application-queue-name}")
    private String bankAccountApplicationQueueName;

    private String bankAccountApplicationQueueURL;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private AccountHolderRepository accountHolderRepository;

    private String accountHolderName = "Jefferson Condotta";
    private LocalDate accountHolderDateOfBirth = LocalDate.of(1988, Month.JUNE, 20);
    private String accountHolderEmailAddress = "jefferson.condotta@dummy.com";

    @BeforeEach
    public void beforeEach() {
        this.bankAccountApplicationQueueURL = sqsClient.createQueue(bankAccountApplicationQueueName).getQueueUrl();
        this.accountHolderRepository.deleteAll();
        this.bankAccountRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        sqsClient.purgeQueue(new PurgeQueueRequest(bankAccountApplicationQueueURL));
    }

    @Test
    public void givenValidBankAccountApplicationSQSMessage_whenConsumeMessage_thenCreateBankAccount() throws JsonProcessingException {
        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        sqsClient.sendMessage(bankAccountApplicationQueueURL, objectMapper.writeValueAsString(addBankAccountRequest));

        await().pollDelay(5, TimeUnit.SECONDS).untilAsserted(() -> {

            List<BankAccount> bankAccounts = bankAccountRepository.findAll();
            assertThat(bankAccounts).hasSize(1);

            var bankAccount = bankAccounts.get(0);
            assertAll(
                    () -> assertThat(bankAccount.getAccountHolders()).hasSize(1),
                    () -> assertThat(bankAccount.getAccountHolders().get(0).getAccountHolderName()).isEqualTo(accountHolder.getAccountHolderName()),
                    () -> assertThat(bankAccount.getAccountHolders().get(0).getDateOfBirth()).isEqualTo(accountHolder.getDateOfBirth()),
                    () -> assertThat(bankAccount.getAccountHolders().get(0).getEmailAddress()).isEqualTo(accountHolder.getEmailAddress())
            );}
        );
    }
}