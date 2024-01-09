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
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Singleton
public class UploadAccountHolderIdentityDocumentService {

//    private AwsS3Operations awsS3Operations;
//    private AccountHolderRepository accountHolderRepository;
//
//    @Inject
//    public UploadAccountHolderIdentityDocumentService(AwsS3Operations awsS3Operations, AccountHolderRepository accountHolderRepository) {
//        this.awsS3Operations = awsS3Operations;
//        this.accountHolderRepository = accountHolderRepository;
//    }

    public UploadResponse<PutObjectResponse> uploadIdentityDocument(Long bankAccountId, Long accountHolderId, CompletedFileUpload fileUpload){
        var uploadRequest = UploadRequest.fromCompletedFileUpload(fileUpload, fileUpload.getFilename());

//        AccountHolder accountHolder = accountHolderRepository.findById(accountHolderId)
//                .filter(accHolder -> accHolder.getBankAccount().getBankAccountId().equals(bankAccountId))
//                .orElseThrow(() -> new ResourceNotFoundException("No account holder with id: " + accountHolderId + " was found in the bank account id: " + bankAccountId));
//
//        UploadResponse<PutObjectResponse> uploadResponse = awsS3Operations.upload(uploadRequest);
//        accountHolder.setIdentityDocumentKey(uploadRequest.getKey());
//        accountHolderRepository.save(accountHolder);
//
//        return uploadResponse;
        return null;

    }
}
