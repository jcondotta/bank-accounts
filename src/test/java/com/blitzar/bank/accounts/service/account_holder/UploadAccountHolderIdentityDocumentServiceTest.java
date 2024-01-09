package com.blitzar.bank.accounts.service.account_holder;

import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.repository.AccountHolderRepository;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.account_holder.request.AccountHolderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UploadAccountHolderIdentityDocumentServiceTest {

    private UploadAccountHolderIdentityDocumentService uploadAccountHolderIdentityDocumentService;

    @Mock
    private AccountHolderRepository accountHolderRepository;

    @BeforeEach
    public void beforeEach(){
//        uploadAccountHolderIdentityDocumentService = new UploadAccountHolderIdentityDocumentService(null, accountHolderRepository);
    }

    @Test
    public void givenValidRequest_whenAddAccountHolder_thenSaveAccountHolder(){
//        BankAccount bankAccount = mock(BankAccount.class);
//        when(bankAccountRepositoryMock.findById(any())).thenReturn(Optional.of(bankAccount));
//
//        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
//
//        addAccountHolderService.addAccountHolderToBankAccount(20L, accountHolder);
//
//        verify(bankAccountRepositoryMock).findById(any());
//        verify(accountHolderRepositoryMock).save(any());
    }

}