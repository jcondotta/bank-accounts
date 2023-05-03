package com.blitzar.bank.accounts.service;

import com.blitzar.bank.accounts.domain.AccountHolder;
import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.request.AddBankAccountRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddBankAccountService {

    private final BankAccountRepository repository;
    private final Clock currentInstant;
    private final Validator validator;

    @Autowired
    public AddBankAccountService(BankAccountRepository repository, Clock currentInstant, Validator validator) {
        this.repository = repository;
        this.currentInstant = currentInstant;
        this.validator = validator;
    }

    public BankAccount addBankAccount(AddBankAccountRequest request){
        var constraintViolations = validator.validate(request);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }

        var bankAccount = new BankAccount();
        bankAccount.setIban(UUID.randomUUID().toString());
        bankAccount.setDateOfOpening(LocalDateTime.now(currentInstant));
        bankAccount.setAccountHolders(request.accountHolders().stream()
                .map(accountHolderRequest -> new AccountHolder(bankAccount, accountHolderRequest.accountHolderName(), accountHolderRequest.dateOfBirth()))
                .collect(Collectors.toList()));

        return repository.save(bankAccount);
    }
}