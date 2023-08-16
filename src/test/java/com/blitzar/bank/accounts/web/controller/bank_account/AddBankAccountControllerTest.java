package com.blitzar.bank.accounts.web.controller.bank_account;

import com.blitzar.bank.accounts.LocalStackMySQLTestContainer;
import com.blitzar.bank.accounts.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.account_holder.request.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.request.AddBankAccountRequest;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
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

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class AddBankAccountControllerTest implements LocalStackMySQLTestContainer {

    private RequestSpecification requestSpecification;

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private Clock testFixedInstantUTC;

    private String accountHolderName = "Jefferson Condotta";
    private LocalDate accountHolderDateOfBirth = LocalDate.of(1930, Month.SEPTEMBER, 20);
    private String accountHolderEmailAddress = "jefferson.condotta@dummy.com";

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(BankAccountAPIConstants.BASE_PATH_API_V1_MAPPING);
    }

    @Test
    public void givenValidRequest_whenAddBankAccount_thenReturnCreated(){
        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        Long bankAccountId = given()
            .spec(requestSpecification)
            .body(addBankAccountRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.getCode())
                .extract()
                .body()
                .as(Long.class);

        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                        .orElseThrow();

        assertAll(
                () -> assertThat(bankAccount.getBankAccountId()).isNotNull(),
                () -> assertThat(bankAccount.getIban()).isNotNull(),
                () -> assertThat(bankAccount.getDateOfOpening()).isEqualTo(LocalDateTime.now(testFixedInstantUTC)),
                () -> assertThat(bankAccount.getAccountHolders()).hasSize(1),
                () -> assertThat(bankAccount.getAccountHolders().get(0).getAccountHolderName()).isEqualTo(accountHolderName),
                () -> assertThat(bankAccount.getAccountHolders().get(0).getDateOfBirth()).isEqualTo(accountHolderDateOfBirth)
        );
    }

    @Test
    public void givenEmptyAccountHolders_whenAddBankAccount_thenReturnBadRequest(){
        var addBankAccountRequest = new AddBankAccountRequest(List.of());

        given()
            .spec(requestSpecification)
        .body(addBankAccountRequest)
            .when()
        .post()
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.getCode())
                .body("message", equalTo(HttpStatus.BAD_REQUEST.getReason()))
                    .rootPath("_embedded")
                    .body("errors", hasSize(1))
                    .body("errors[0].message", equalTo("accountHolders: must not be empty"));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderName_whenAddBankAccount_thenReturnBadRequest(String invalidAccountHolderName){
        var accountHolder = new AccountHolderRequest(invalidAccountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        given()
            .spec(requestSpecification)
            .body(addBankAccountRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode());
    }

    @Test
    public void givenNullAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest(){
        var accountHolder = new AccountHolderRequest(accountHolderName, null, accountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        given()
            .spec(requestSpecification)
            .body(addBankAccountRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode());
    }

    @Test
    public void givenFutureAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest(){
        var accountHolderDateOfBirth = LocalDate.now().plusDays(1);

        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        given()
            .spec(requestSpecification)
            .body(addBankAccountRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderEmailAddress_whenAddBankAccount_thenReturnBadRequest(String invalidAccountHolderEmailAddress){
        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, invalidAccountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        given()
            .spec(requestSpecification)
            .body(addBankAccountRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode());
    }
}