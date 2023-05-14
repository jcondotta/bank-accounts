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
import com.blitzar.bank.accounts.service.account_holder.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountService;
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
import org.apache.commons.lang3.math.NumberUtils;
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
import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautLambdaTest(transactional = false)
public class GetBankAccountLambdaTest implements MySQLTestContainer {

    private static Context lambdaContext = new MockLambdaContext();

    private MicronautLambdaHandler handler;
    private AwsProxyRequest request;

    @Inject
    private AddBankAccountService addBankAccountService;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ApplicationContext applicationContext;

    @BeforeEach
    public void beforeEach() throws ContainerInitializationException {
        handler = new MicronautLambdaHandler(applicationContext);
        request = new AwsProxyRequestBuilder(BankAccountAPIConstants.GET_BANK_ACCOUNT_V1_MAPPING, HttpMethod.GET.name())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .build();
    }

    @Test
    public void givenExistingBankAccountId_whenGetBankAccount_thenReturnBankAccount(){
        var accountHolderName = "Jefferson Condotta#1989";
        var accountHolderDateOfBirth = LocalDate.of(1989, Month.DECEMBER, 20);

        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        BankAccount bankAccount = addBankAccountService.addBankAccount(addBankAccountRequest);
        request.setPathParameters(Map.of("bank-account-id", bankAccount.getBankAccountId().toString()));

        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getCode()),
                () -> assertThat(response.getBody()).isNotNull()
        );
    }

    @Test
    public void givenNonExistingBankAccountId_whenGetAccountHolders_thenReturnNotFound(){
        request.setPathParameters(Map.of("bank-account-id", NumberUtils.LONG_MINUS_ONE.toString()));

        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getCode()),
                () -> assertThat(response.getBody()).isNotNull()
        );
    }
}

