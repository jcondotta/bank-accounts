package com.blitzar.bank.accounts.web.controller.account_holder;

import com.blitzar.bank.accounts.LocalStackMySQLTestContainer;
import com.blitzar.bank.accounts.repository.AccountHolderRepository;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.account_holder.request.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountService;
import com.blitzar.bank.accounts.service.bank_account.request.AddBankAccountRequest;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest.Builder;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.function.Consumer;

import static io.restassured.RestAssured.given;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class UploadAccountHolderIdentityDocumentControllerTest implements LocalStackMySQLTestContainer {

    private RequestSpecification requestSpecification;

    @Inject
    private AddBankAccountService addBankAccountService;

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private AccountHolderRepository accountHolderRepository;

    @Inject
    private S3Client s3Client;

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
                .contentType(ContentType.MULTIPART)
                .basePath(BankAccountAPIConstants.GET_ACCOUNT_HOLDER_V1_MAPPING);
    }

    @AfterEach
    public void afterEach(){
        accountHolderRepository.deleteAll();
        bankAccountRepository.deleteAll();
    }

    @Test
    public void givenValidRequest_whenUploadIdentityDocument_thenReturnCreated(){
//        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
//                .bucket(bucketName)
//                .build();


        var addBankAccountRequest = new AddBankAccountRequest(new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress));
        var bankAccount = addBankAccountService.addBankAccount(addBankAccountRequest);

        var bankAccountId = bankAccount.getBankAccountId();
        var accountHolderId = bankAccount.getAccountHolders().get(0).getAccountHolderId();

        Path path = Path.of("src/test/resources/identity_document_sample_1.png");

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .pathParam("account-holder-id", accountHolderId)
                .multiPart("fileUpload", path.toFile())
        .when()
            .post(UploadAccountHolderIdentityDocumentController.UPLOAD_IDENTIFICATION_PATH)
        .then()
            .statusCode(HttpStatus.CREATED.getCode());
    }

}