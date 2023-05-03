package com.blitzar.bank.accounts.web.controller;

import com.blitzar.bank.accounts.TestMySQLContainer;
import com.blitzar.bank.accounts.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank.accounts.config.TestTimeConfiguration;
import com.blitzar.bank.accounts.service.request.AddBankAccountRequest;
import com.blitzar.bank.accounts.service.request.AccountHolderRequest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

import static io.restassured.RestAssured.given;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TestTimeConfiguration.class)
public class AddBankAccountControllerTest extends TestMySQLContainer {

    private RequestSpecification requestSpecification;

    @Autowired
    @Qualifier("testFixedInstantUTC")
    private Clock testFixedInstantUTC;

    @BeforeAll
    public static void beforeAll(@LocalServerPort int serverHttpPort){
        RestAssured.port = serverHttpPort;
        RestAssured.basePath = "/api/v1/bank-accounts";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        this.requestSpecification = new RequestSpecBuilder()
                .build()
                .contentType(ContentType.JSON);
    }

    @Test
    public void givenValidRequest_whenAddBankAccount_thenSaveBankAccount(){
        var accountHolder = new AccountHolderRequest("Jefferson Condotta", LocalDate.of(1988, Month.JUNE, 20));
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        given()
            .spec(requestSpecification)
            .body(addBankAccountRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void givenEmptyAccountHolders_whenAddBankAccount_thenThrowException(){
        var addBankAccountRequest = new AddBankAccountRequest(Collections.EMPTY_LIST);

        given()
            .spec(requestSpecification)
        .body(addBankAccountRequest)
            .when()
        .post()
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderName_whenAddBankAccount_thenThrowException(String invalidAccountHolderName){
        var accountHolder = new AccountHolderRequest(invalidAccountHolderName, LocalDate.of(1988, Month.JUNE, 20));
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        given()
            .spec(requestSpecification)
            .body(addBankAccountRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void givenNullAccountHolderDateOfBirth_whenAddBankAccount_thenThrowException(){
        var accountHolder = new AccountHolderRequest("Jefferson Condotta", null);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        given()
            .spec(requestSpecification)
            .body(addBankAccountRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void givenFutureAccountHolderDateOfBirth_whenAddBankAccount_thenThrowException(){
        var accountHolder = new AccountHolderRequest("Jefferson Condotta", LocalDate.now().plusDays(1));
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        given()
            .spec(requestSpecification)
            .body(addBankAccountRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}