package com.blitzar.bank.accounts.web.controller.account_holder;

import com.blitzar.bank.accounts.LocalStackMySQLTestContainer;
import com.blitzar.bank.accounts.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank.accounts.repository.AccountHolderRepository;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.account_holder.request.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.request.AddBankAccountRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountService;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;
import java.time.Month;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class AddAccountHolderControllerTest implements LocalStackMySQLTestContainer {

    private RequestSpecification requestSpecification;

    @Inject
    private AddBankAccountService addBankAccountService;

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private AccountHolderRepository accountHolderRepository;

    private String accountHolderName = "Jefferson Condotta";
    private LocalDate accountHolderDateOfBirth = LocalDate.of(1929, Month.SEPTEMBER, 20);
    private String accountHolderEmailAddress = "jefferson.condotta@dummy.com";

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
        var addBankAccountRequest = new AddBankAccountRequest(new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress));
        var bankAccountId = addBankAccountService.addBankAccount(addBankAccountRequest).getBankAccountId();

        var newAccountHolderName = "Jefferson William";
        var newAccountHolderDateOfBirth = LocalDate.of(1929, Month.SEPTEMBER, 20);
        var newAccountHolderEmailAddress = "jefferson.william@dummy.com";

        var accountHolderRequest = new AccountHolderRequest(newAccountHolderName, newAccountHolderDateOfBirth, newAccountHolderEmailAddress);

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
                () -> assertThat(bankAccount.getAccountHolders().get(1).getDateOfBirth()).isEqualTo(accountHolderRequest.getDateOfBirth()),
                () -> assertThat(bankAccount.getAccountHolders().get(1).getEmailAddress()).isEqualTo(accountHolderRequest.getEmailAddress())
        );
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderName_whenAddAccountHolder_thenReturnBadRequest(String invalidAccountHolderName){
        var addBankAccountRequest = new AddBankAccountRequest(new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress));
        var bankAccountId = addBankAccountService.addBankAccount(addBankAccountRequest).getBankAccountId();

        var newAccountHolderDateOfBirth = LocalDate.of(1929, Month.SEPTEMBER, 20);
        var newAccountHolderEmailAddress = "jefferson.william@dummy.com";

        var accountHolderRequest = new AccountHolderRequest(invalidAccountHolderName, newAccountHolderDateOfBirth, newAccountHolderEmailAddress);

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
        var addBankAccountRequest = new AddBankAccountRequest(new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress));
        var bankAccountId = addBankAccountService.addBankAccount(addBankAccountRequest).getBankAccountId();

        var newAccountHolderName = "Jefferson William";
        var newAccountHolderEmailAddress = "jefferson.william@dummy.com";

        var accountHolderRequest = new AccountHolderRequest(newAccountHolderName, null, newAccountHolderEmailAddress);

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
        var addBankAccountRequest = new AddBankAccountRequest(new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress));
        var bankAccountId = addBankAccountService.addBankAccount(addBankAccountRequest).getBankAccountId();

        var newAccountHolderName = "Jefferson William";
        var newAccountHolderDateOfBirth = LocalDate.now().plusDays(1);
        var newAccountHolderEmailAddress = "jefferson.william@dummy.com";

        var accountHolderRequest = new AccountHolderRequest(newAccountHolderName, newAccountHolderDateOfBirth, newAccountHolderEmailAddress);

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

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderEmailAddress_whenAddAccountHolder_thenReturnBadRequest(String invalidAccountHolderEmailAddress){
        var addBankAccountRequest = new AddBankAccountRequest(new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress));
        var bankAccountId = addBankAccountService.addBankAccount(addBankAccountRequest).getBankAccountId();

        var newAccountHolderName = "Jefferson William";
        var newAccountHolderDateOfBirth = LocalDate.of(1929, Month.SEPTEMBER, 20);

        var accountHolderRequest = new AccountHolderRequest(newAccountHolderName, newAccountHolderDateOfBirth, invalidAccountHolderEmailAddress);

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
                    .body("errors[0].message", equalTo("emailAddress: must not be blank"));
    }
}