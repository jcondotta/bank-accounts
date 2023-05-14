package com.blitzar.bank.accounts.web.controller.account_holder;

import com.blitzar.bank.accounts.MySQLTestContainer;
import com.blitzar.bank.accounts.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.repository.AccountHolderRepository;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.account_holder.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountService;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.validation.ConstraintViolationException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class AddAccountHolderControllerTest implements MySQLTestContainer {

    private RequestSpecification requestSpecification;

    @Inject
    private AddBankAccountService addBankAccountService;

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private AccountHolderRepository accountHolderRepository;

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(BankAccountAPIConstants.ACCOUNT_HOLDER_API_V1_MAPPING);
    }

    @AfterEach
    public void afterEach(){
        accountHolderRepository.deleteAll();
        bankAccountRepository.deleteAll();
    }

    @Test
    public void givenValidRequest_whenAddAccountHolder_thenReturnCreated(){
        var accountHolderName = "Jefferson Condotta#1929";
        var accountHolderDateOfBirth = LocalDate.of(1929, Month.SEPTEMBER, 20);

        var addBankAccountRequest = new AddBankAccountRequest(new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth));
        var bankAccountId = addBankAccountService.addBankAccount(addBankAccountRequest).getBankAccountId();

        var accountHolderRequest = new AccountHolderRequest("NBA Jam 2020", LocalDate.of(2020, Month.SEPTEMBER, 01));

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .body(accountHolderRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.getCode());

        var bankAccount = bankAccountRepository.findById(bankAccountId).orElseThrow();
        assertAll(
                () -> assertThat(bankAccount.getAccountHolders()).hasSize(2),
                () -> assertThat(bankAccount.getAccountHolders().get(1).getAccountHolderName()).isEqualTo(accountHolderRequest.getAccountHolderName()),
                () -> assertThat(bankAccount.getAccountHolders().get(1).getDateOfBirth()).isEqualTo(accountHolderRequest.getDateOfBirth())
        );
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderName_whenAddAccountHolder_thenReturnBadRequest(String invalidAccountHolderName){
        var accountHolderName = "Jefferson Condotta#1930";
        var accountHolderDateOfBirth = LocalDate.of(1930, Month.SEPTEMBER, 20);

        var addBankAccountRequest = new AddBankAccountRequest(new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth));
        var bankAccountId = addBankAccountService.addBankAccount(addBankAccountRequest).getBankAccountId();

        var accountHolderRequest = new AccountHolderRequest(invalidAccountHolderName, LocalDate.of(2020, Month.SEPTEMBER, 01));

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .body(accountHolderRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
                .body("message", equalTo(HttpStatus.BAD_REQUEST.getReason()))
                .rootPath("_embedded")
                    .body("errors", hasSize(1))
                    .body("errors[0].message", equalTo("accountHolderName: must not be blank"));
    }

    @Test
    public void givenNullDateOfBirth_whenAddAccountHolder_thenReturnBadRequest(){
        var accountHolderName = "Jefferson Condotta#1930";
        var accountHolderDateOfBirth = LocalDate.of(1930, Month.SEPTEMBER, 20);

        var addBankAccountRequest = new AddBankAccountRequest(new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth));
        var bankAccountId = addBankAccountService.addBankAccount(addBankAccountRequest).getBankAccountId();

        var accountHolderRequest = new AccountHolderRequest("NBA Jam 2020", null);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .body(accountHolderRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
                .body("message", equalTo(HttpStatus.BAD_REQUEST.getReason()))
                .rootPath("_embedded")
                    .body("errors", hasSize(1))
                    .body("errors[0].message", equalTo("dateOfBirth: must not be null"));
    }

    @Test
    public void givenFutureDateOfBirth_whenAddBankAccount_thenReturnBadRequest(){
        var accountHolderName = "Jefferson Condotta#1930";
        var accountHolderDateOfBirth = LocalDate.of(1930, Month.SEPTEMBER, 20);

        var addBankAccountRequest = new AddBankAccountRequest(new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth));
        var bankAccountId = addBankAccountService.addBankAccount(addBankAccountRequest).getBankAccountId();

        var accountHolderRequest = new AccountHolderRequest("NBA Jam 2020", LocalDate.now().plusDays(1));

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .body(accountHolderRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
                .body("message", equalTo(HttpStatus.BAD_REQUEST.getReason()))
                .rootPath("_embedded")
                    .body("errors", hasSize(1))
                    .body("errors[0].message", equalTo("dateOfBirth: must be a past date"));
    }
}