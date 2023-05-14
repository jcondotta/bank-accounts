package com.blitzar.bank.accounts.web.controller.account_holder;

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
public class AddAccountHolderControllerTest implements MySQLTestContainer {

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
                .basePath(BankAccountAPIConstants.ACCOUNT_HOLDER_API_V1_MAPPING);
    }

    @Test
    public void givenValidRequest_whenAddAccountHolder_thenReturnCreated(){
        var bankAccount = new BankAccount();
        bankAccount.setIban("asd");

        var noww = LocalDateTime.now();
        bankAccount.setDateOfOpening(noww);
        bankAccount = bankAccountRepository.save(bankAccount);

        var accountHolderRequest = new AccountHolderRequest("Jefferson Condotta", LocalDate.of(1988, Month.JUNE, 20));

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccount.getBankAccountId())
                .body(accountHolderRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.getCode());

        var bankAccount2 = bankAccountRepository.findById(bankAccount.getBankAccountId())
                        .orElseThrow();

        assertAll(
                () -> assertThat(bankAccount2.getAccountHolders()).hasSize(1),
                () -> assertThat(bankAccount2.getAccountHolders().get(0).getAccountHolderName()).isEqualTo("Jefferson Condotta"),
                () -> assertThat(bankAccount2.getAccountHolders().get(0).getDateOfBirth()).isEqualTo(LocalDate.of(1988, Month.JUNE, 20))
        );
    }
//
//    @Test
//    public void givenEmptyAccountHolders_whenAddBankAccount_thenReturnBadRequest(){
//        var addBankAccountRequest = new AddBankAccountRequest(new ArrayList<AccountHolderRequest>());
//
//        given()
//            .spec(requestSpecification)
//        .body(addBankAccountRequest)
//            .when()
//        .post()
//            .then()
//                .statusCode(HttpStatus.BAD_REQUEST.getCode());
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(InvalidStringArgumentProvider.class)
//    public void givenInvalidAccountHolderName_whenAddBankAccount_thenReturnBadRequest(String invalidAccountHolderName){
//        var accountHolder = new AccountHolderRequest(invalidAccountHolderName, LocalDate.of(1988, Month.JUNE, 20));
//        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);
//
//        given()
//            .spec(requestSpecification)
//            .body(addBankAccountRequest)
//        .when()
//            .post()
//        .then()
//            .statusCode(HttpStatus.BAD_REQUEST.getCode());
//    }
//
//    @Test
//    public void givenNullAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest(){
//        var accountHolder = new AccountHolderRequest("Jefferson Condotta", null);
//        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);
//
//        given()
//            .spec(requestSpecification)
//            .body(addBankAccountRequest)
//        .when()
//            .post()
//        .then()
//            .statusCode(HttpStatus.BAD_REQUEST.getCode());
//    }
//
//    @Test
//    public void givenFutureAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest(){
//        var accountHolder = new AccountHolderRequest("Jefferson Condotta", LocalDate.now().plusDays(1));
//        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);
//
//        given()
//            .spec(requestSpecification)
//            .body(addBankAccountRequest)
//        .when()
//            .post()
//        .then()
//            .statusCode(HttpStatus.BAD_REQUEST.getCode());
//    }
}