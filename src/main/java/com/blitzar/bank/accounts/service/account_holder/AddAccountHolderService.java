package com.blitzar.bank.accounts.service.account_holder;

import com.blitzar.bank.accounts.domain.AccountHolder;
import com.blitzar.bank.accounts.exception.ResourceNotFoundException;
import com.blitzar.bank.accounts.repository.AccountHolderRepository;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

@Singleton
@Transactional
public class AddAccountHolderService {

    private static final Logger logger = LoggerFactory.getLogger(AddAccountHolderService.class);

    private final AccountHolderRepository accountHolderRepository;
    private final BankAccountRepository bankAccountRepository;
    private final Validator validator;

    public AddAccountHolderService(AccountHolderRepository accountHolderRepository, BankAccountRepository bankAccountRepository, Validator validator) {
        this.accountHolderRepository = accountHolderRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.validator = validator;
    }

    public AccountHolder addAccountHolderToBankAccount(Long bankAccountId, AccountHolderRequest request){
        logger.info("Attempting to add an account holder to bank account id: {}", bankAccountId);

        var constraintViolations = validator.validate(request);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }

        var bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("No bank account has been found with id: " + bankAccountId));

        var accountHolder = new AccountHolder(bankAccount,
                request.getAccountHolderName(),
                request.getDateOfBirth(),
                request.getEmailAddress());

        return accountHolderRepository.save(accountHolder);
    }
}