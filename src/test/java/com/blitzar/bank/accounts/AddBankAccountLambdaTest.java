package com.blitzar.bank.accounts;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.blitzar.bank.accounts.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.account_holder.request.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.request.AddBankAccountRequest;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.ApplicationContext;
import io.micronaut.function.aws.proxy.MicronautLambdaHandler;
import io.micronaut.function.aws.test.annotation.MicronautLambdaTest;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautLambdaTest(transactional = false)
public class AddBankAccountLambdaTest implements LocalStackMySQLTestContainer {

    private static Context lambdaContext = new MockLambdaContext();

    private MicronautLambdaHandler handler;
    private AwsProxyRequest request;

    @Inject
    private Clock testFixedInstantUTC;

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ApplicationContext applicationContext;

    private String accountHolderName = "Jefferson Condotta";
    private LocalDate accountHolderDateOfBirth = LocalDate.of(1930, Month.SEPTEMBER, 20);
    private String accountHolderEmailAddress = "jefferson.condotta@dummy.com";

    @BeforeEach
    public void beforeEach() throws ContainerInitializationException {
        handler = new MicronautLambdaHandler(applicationContext);
        request = new AwsProxyRequestBuilder(BankAccountAPIConstants.BASE_PATH_API_V1_MAPPING, HttpMethod.POST.toString())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .build();
    }

    @Test
    public void givenValidRequest_whenAddBankAccount_thenReturnCreated() throws JsonProcessingException {
        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));

        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.getCode()),
                () -> assertThat(response.getBody()).isNotNull()
        );

        Long bankAccountId = Long.valueOf(response.getBody());
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow();

        assertAll(
                () -> assertThat(bankAccount.getBankAccountId()).isNotNull(),
                () -> assertThat(bankAccount.getIban()).isNotNull(),
                () -> assertThat(bankAccount.getDateOfOpening()).isEqualTo(LocalDateTime.now(testFixedInstantUTC)),
                () -> assertThat(bankAccount.getAccountHolders()).hasSize(1),
                () -> assertThat(bankAccount.getAccountHolders().get(0).getAccountHolderName()).isEqualTo(accountHolderName),
                () -> assertThat(bankAccount.getAccountHolders().get(0).getDateOfBirth()).isEqualTo(accountHolderDateOfBirth),
                () -> assertThat(bankAccount.getAccountHolders().get(0).getEmailAddress()).isEqualTo(accountHolderEmailAddress)
        );
    }

    @Test
    public void givenEmptyAccountHolders_whenAddBankAccount_thenReturnBadRequest() throws JsonProcessingException {
        var addBankAccountRequest = new AddBankAccountRequest(List.of());

        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));

        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
                () -> assertThat(response.getBody()).isNotEmpty()
        );
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderName_whenAddBankAccount_thenReturnBadRequest(String invalidAccountHolderName) throws JsonProcessingException {
        var accountHolder = new AccountHolderRequest(invalidAccountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));

        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
                () -> assertThat(response.getBody()).isNotEmpty()
        );
    }

    @Test
    public void givenNullAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest() throws JsonProcessingException {
        var accountHolder = new AccountHolderRequest(accountHolderName, null, accountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));

        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
                () -> assertThat(response.getBody()).isNotEmpty()
        );
    }

    @Test
    public void givenFutureAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest() throws JsonProcessingException {
        var accountHolderDateOfBirth = LocalDate.now().plusDays(1);

        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));

        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
                () -> assertThat(response.getBody()).isNotEmpty()
        );
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderEmailAddress_whenAddBankAccount_thenReturnBadRequest(String invalidAccountHolderEmailAddress) throws JsonProcessingException {
        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, invalidAccountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));

        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
                () -> assertThat(response.getBody()).isNotEmpty()
        );
    }
}

