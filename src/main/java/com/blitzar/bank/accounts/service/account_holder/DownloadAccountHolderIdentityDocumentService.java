package com.blitzar.bank.accounts.service.account_holder;

import com.blitzar.bank.accounts.domain.AccountHolder;
import com.blitzar.bank.accounts.exception.ResourceNotFoundException;
import com.blitzar.bank.accounts.repository.AccountHolderRepository;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.objectstorage.aws.AwsS3Operations;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.objectstorage.response.UploadResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Singleton
public class DownloadAccountHolderIdentityDocumentService {

//    private S3Client s3Client;
//    private AccountHolderRepository accountHolderRepository;
//
//    @Inject
//    public DownloadAccountHolderIdentityDocumentService(S3Client s3Client, AccountHolderRepository accountHolderRepository) {
//        this.s3Client = s3Client;
//        this.accountHolderRepository = accountHolderRepository;
//    }

    public ResponseInputStream<GetObjectResponse> downloadIdentityDocument(Long bankAccountId, Long accountHolderId){
//        AccountHolder accountHolder = accountHolderRepository.findById(accountHolderId)
//                .filter(accHolder -> accHolder.getBankAccount().getBankAccountId().equals(bankAccountId))
//                .orElseThrow(() -> new ResourceNotFoundException("No account holder with id: " + accountHolderId + " was found in the bank account id: " + bankAccountId));
//
//        ResponseInputStream<GetObjectResponse> objectResponse = s3Client.getObject(GetObjectRequest
//                .builder()
//                .bucket("blitzar-bank-account-identity-document")
//                .key(accountHolder.getIdentityDocumentKey())
//                .build());
//
//        System.out.println(objectResponse.response());
//
//        return objectResponse;
        return null;

    }
}
