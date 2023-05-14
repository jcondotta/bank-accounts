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
import java.time.Month;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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
        var accountHolder1 = new AccountHolderRequest("Jefferson Condotta#1954", LocalDate.of(1954, Month.JANUARY, 01));
        var accountHolder2 = new AccountHolderRequest("Jefferson Condotta#1978", LocalDate.of(1978, Month.MAY, 30));

        var addBankAccountRequest = new AddBankAccountRequest(accountHolder1, accountHolder2);
        var bankAccount = addBankAccountService.addBankAccount(addBankAccountRequest);

        AccountHoldersDTO accountHoldersDTO = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccount.getBankAccountId())
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .extract().as(AccountHoldersDTO.class);

        System.out.println(accountHoldersDTO);

        //TODO finish assert response fields
//                .extract()
//                    .as(BankAccountDTO.class);
//
//        assertAll(
//                () -> assertThat(bankAccountDTO.getIban()).isNotNull(),
//                () -> assertThat(bankAccountDTO.getDateOfOpening()).isEqualTo(LocalDateTime.now(testFixedInstantUTC)),
//                () -> assertThat(bankAccountDTO.getAccountHolders()).hasSize(1),
//                () -> assertThat(bankAccountDTO.getAccountHolders().get(0).getAccountHolderName()).isEqualTo(accountHolder.getAccountHolderName()),
//                () -> assertThat(bankAccountDTO.getAccountHolders().get(0).getDateOfBirth()).isEqualTo(accountHolder.getDateOfBirth())
//        );
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