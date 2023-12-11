package com.blitzar.bank.accounts.web.controller.account_holder;

import com.blitzar.bank.accounts.service.account_holder.DownloadAccountHolderIdentityDocumentService;
import com.blitzar.bank.accounts.service.account_holder.UploadAccountHolderIdentityDocumentService;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import io.micronaut.http.*;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.objectstorage.response.UploadResponse;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Validated
@Controller(BankAccountAPIConstants.GET_ACCOUNT_HOLDER_V1_MAPPING)
public class DownloadAccountHolderIdentityDocumentController {

    public static final String IDENTITY_DOCUMENT_PATH = "identity-document";

    private final DownloadAccountHolderIdentityDocumentService downloadAccountHolderIdentityDocumentService;

    @Inject
    public DownloadAccountHolderIdentityDocumentController(DownloadAccountHolderIdentityDocumentService downloadAccountHolderIdentityDocumentService) {
        this.downloadAccountHolderIdentityDocumentService = downloadAccountHolderIdentityDocumentService;
    }

    @Status(HttpStatus.CREATED)
    @Get(value = IDENTITY_DOCUMENT_PATH)
    public HttpResponse<?> uploadIdentification(@PathVariable("bank-account-id") Long bankAccountId, @PathVariable("account-holder-id") Long accountHolderId, HttpRequest<?> request){
        downloadAccountHolderIdentityDocumentService.downloadIdentityDocument(bankAccountId, accountHolderId);
        return HttpResponse.ok();
    }
}
