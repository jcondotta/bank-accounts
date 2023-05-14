package com.blitzar.bank.accounts.service.bank_account;

import com.blitzar.bank.accounts.exception.ResourceNotFoundException;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.web.dto.BankAccountDTO;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class GetBankAccountService {

    private BankAccountRepository bankAccountRepository;

    @Inject
    public GetBankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public BankAccountDTO byId(Long bankAccountId){
        return bankAccountRepository.findById(bankAccountId)
                .map(bankAccount -> new BankAccountDTO(bankAccount))
                .orElseThrow(() -> new ResourceNotFoundException("No bank account has been found with id: " + bankAccountId));
    }
}
