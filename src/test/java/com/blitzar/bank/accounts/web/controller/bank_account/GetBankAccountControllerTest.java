package com.blitzar.bank.accounts.web.controller.bank_account;

import com.blitzar.bank.accounts.LocalStackMySQLTestContainer;
import com.blitzar.bank.accounts.service.account_holder.request.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.request.AddBankAccountRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountService;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import com.blitzar.bank.accounts.web.dto.BankAccountDTO;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class GetBankAccountControllerTest implements LocalStackMySQLTestContainer {

    private RequestSpecification requestSpecification;

    @Inject
    private AddBankAccountService addBankAccountService;

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
                .basePath(BankAccountAPIConstants.GET_BANK_ACCOUNT_V1_MAPPING);
    }

    @Test
    public void givenExistingBankAccountId_whenGetBankAccount_thenReturnBankAccount(){
        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        var bankAccount = addBankAccountService.addBankAccount(addBankAccountRequest);

        BankAccountDTO bankAccountDTO = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccount.getBankAccountId())
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .extract()
                .as(BankAccountDTO.class);

        assertAll(
                () -> assertThat(bankAccountDTO.getBankAccountId()).isNotNull(),
                () -> assertThat(bankAccountDTO.getIban()).isNotNull(),
                () -> assertThat(bankAccountDTO.getDateOfOpening()).isEqualTo(LocalDateTime.now(testFixedInstantUTC)),
                () -> assertThat(bankAccountDTO.getAccountHolders()).hasSize(1),
                () -> assertThat(bankAccountDTO.getAccountHolders().get(0).getAccountHolderName()).isEqualTo(accountHolder.getAccountHolderName()),
                () -> assertThat(bankAccountDTO.getAccountHolders().get(0).getDateOfBirth()).isEqualTo(accountHolder.getDateOfBirth()),
                () -> assertThat(bankAccountDTO.getAccountHolders().get(0).getEmailAddress()).isEqualTo(accountHolder.getEmailAddress())
        );
    }

    @Test
    public void givenNonExistingBankAccountId_whenGetAccountHolders_thenReturnNotFound(){
        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", NumberUtils.LONG_MINUS_ONE)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode())
                .body("message", equalTo(HttpStatus.NOT_FOUND.getReason()))
                .rootPath("_embedded")
                    .body("errors", hasSize(1))
                    .body("errors[0].message", containsString("No bank account has been found with id: " + NumberUtils.LONG_MINUS_ONE));
    }
}