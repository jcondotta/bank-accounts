package com.blitzar.bank.accounts.web.controller.account_holder;

import com.blitzar.bank.accounts.MySQLTestContainer;
import com.blitzar.bank.accounts.service.account_holder.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountService;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import com.blitzar.bank.accounts.web.dto.AccountHoldersDTO;
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

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class GetAccountHoldersControllerTest implements MySQLTestContainer {

    private RequestSpecification requestSpecification;

    @Inject
    private AddBankAccountService addBankAccountService;

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
    public void givenExistingBankAccountId_whenGetAccountHolders_thenReturnAccountHolders(){
        var accountHolderName1 = "Jefferson Condotta#1930";
        var accountHolderDateOfBirth1 = LocalDate.of(1930, Month.SEPTEMBER, 20);
        var accountHolderEmailAddress1 = "jefferson.condotta1930@dummy.com";
        var accountHolder1 = new AccountHolderRequest(accountHolderName1, accountHolderDateOfBirth1, accountHolderEmailAddress1);

        var accountHolderName2 = "Jefferson Condotta#1940";
        var accountHolderDateOfBirth2 = LocalDate.of(1940, Month.SEPTEMBER, 20);
        var accountHolderEmailAddress2 = "jefferson.condotta1940@dummy.com";
        var accountHolder2 = new AccountHolderRequest(accountHolderName2, accountHolderDateOfBirth2, accountHolderEmailAddress2);

        var addBankAccountRequest = new AddBankAccountRequest(accountHolder1, accountHolder2);
        var bankAccount = addBankAccountService.addBankAccount(addBankAccountRequest);

        AccountHoldersDTO accountHoldersDTO = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccount.getBankAccountId())
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .extract()
                .as(AccountHoldersDTO.class);

        assertAll(
                () -> assertThat(accountHoldersDTO.getAccountHolders()).hasSize(2),
                () -> assertThat(accountHoldersDTO.getAccountHolders().get(0).getAccountHolderName()).isEqualTo(accountHolder1.getAccountHolderName()),
                () -> assertThat(accountHoldersDTO.getAccountHolders().get(0).getDateOfBirth()).isEqualTo(accountHolder1.getDateOfBirth()),
                () -> assertThat(accountHoldersDTO.getAccountHolders().get(0).getEmailAddress()).isEqualTo(accountHolder1.getEmailAddress()),
                () -> assertThat(accountHoldersDTO.getAccountHolders().get(1).getAccountHolderName()).isEqualTo(accountHolder2.getAccountHolderName()),
                () -> assertThat(accountHoldersDTO.getAccountHolders().get(1).getDateOfBirth()).isEqualTo(accountHolder2.getDateOfBirth()),
                () -> assertThat(accountHoldersDTO.getAccountHolders().get(1).getEmailAddress()).isEqualTo(accountHolder2.getEmailAddress())
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