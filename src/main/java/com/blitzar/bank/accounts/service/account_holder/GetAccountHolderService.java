package com.blitzar.bank.accounts.service.account_holder;

import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.exception.ResourceNotFoundException;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.web.dto.AccountHoldersDTO;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class GetAccountHolderService {

    private BankAccountRepository bankAccountRepository;

    @Inject
    public GetAccountHolderService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public AccountHoldersDTO findAllByBankAccountId(Long bankAccountId){
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("No bank account has been found with id: " + bankAccountId));

        return new AccountHoldersDTO(bankAccount.getAccountHolders());
    }
}
