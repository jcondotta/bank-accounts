package com.blitzar.bank.accounts.web.controller.bank_account;

import com.blitzar.bank.accounts.MySQLTestContainer;
import com.blitzar.bank.accounts.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.account_holder.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountRequest;
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
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class AddBankAccountControllerTest implements MySQLTestContainer {

    private RequestSpecification requestSpecification;

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private Clock testFixedInstantUTC;

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
        var accountHolderName = "Jefferson Condotta";
        var accountHolderDateOfBirth = LocalDate.of(1988, Month.JUNE, 20);

        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth);
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
        var addBankAccountRequest = new AddBankAccountRequest(new ArrayList<AccountHolderRequest>());

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
    public void givenInvalidAccountHolderName_whenAddBankAccount_thenReturnBadRequest(String invalidAccountHolderName){
        var accountHolder = new AccountHolderRequest(invalidAccountHolderName, LocalDate.of(1988, Month.JUNE, 20));
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
        var accountHolder = new AccountHolderRequest("Jefferson Condotta", null);
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
        var accountHolder = new AccountHolderRequest("Jefferson Condotta", LocalDate.now().plusDays(1));
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