package com.blitzar.bank.accounts.web.controller.account_holder;

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
public class UploadAccountHolderIdentityDocumentController {

    public static final String UPLOAD_IDENTIFICATION_PATH = "upload-identification";

    private final UploadAccountHolderIdentityDocumentService uploadAccountHolderIdentityDocumentService;
    private final HttpHostResolver httpHostResolver;

    @Inject
    public UploadAccountHolderIdentityDocumentController(UploadAccountHolderIdentityDocumentService uploadAccountHolderIdentityDocumentService, HttpHostResolver httpHostResolver) {
        this.uploadAccountHolderIdentityDocumentService = uploadAccountHolderIdentityDocumentService;
        this.httpHostResolver = httpHostResolver;
    }

    @Status(HttpStatus.CREATED)
    @Post(value = UPLOAD_IDENTIFICATION_PATH, consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<?> uploadIdentification(CompletedFileUpload fileUpload, @PathVariable("bank-account-id") Long bankAccountId, @PathVariable("account-holder-id") Long accountHolderId, HttpRequest<?> request){
        UploadResponse<PutObjectResponse> response = uploadAccountHolderIdentityDocumentService.uploadIdentityDocument(bankAccountId, accountHolderId, fileUpload);

        return HttpResponse.created(UriBuilder.of(httpHostResolver.resolve(request))
                        .path(UPLOAD_IDENTIFICATION_PATH)
                        .path(response.getKey())
                        .build())
                .header(HttpHeaders.ETAG, response.getETag());
    }
}
